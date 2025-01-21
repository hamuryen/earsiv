package tr.bm.earsiv.utilities;

import java.time.Duration;
import java.util.Base64;
import java.util.Collections;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Pdf;
import org.openqa.selenium.PrintsPage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.print.PrintOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tr.bm.earsiv.exceptions.EArsivException;

public class HtmlToPdfConverter {
  private static final Logger logger = LoggerFactory.getLogger(HtmlToPdfConverter.class);
  private static final int POOL_SIZE = 2;
  private static final int WAIT_TIME_SECONDS = 5;
  private static final int MAX_RETRIES = 3;
  private static volatile ChromeOptions options = null;
  private static volatile BlockingQueue<WebDriver> driverPool = null;
  private static final AtomicBoolean isShutdown = new AtomicBoolean(false);
  private static final ReentrantLock shutdownLock = new ReentrantLock();
  private static final Object initLock = new Object();

  private static ChromeOptions getOptions() {
    if (options == null) {
      synchronized (initLock) {
        if (options == null) {
          options = initChromeOptions();
        }
      }
    }
    return options;
  }

  private static BlockingQueue<WebDriver> getDriverPool() {
    if (driverPool == null) {
      synchronized (initLock) {
        if (driverPool == null) {
          driverPool = initDriverPool();
        }
      }
    }
    return driverPool;
  }

  private static ChromeOptions initChromeOptions() {
    ChromeOptions opts = new ChromeOptions();
    opts.addArguments("--headless=new");
    opts.addArguments("--disable-gpu");
    opts.addArguments("--no-sandbox");
    opts.addArguments("--disable-dev-shm-usage");
    opts.addArguments("--disable-extensions");
    opts.addArguments("--disable-logging");
    opts.addArguments("--disable-notifications");
    opts.addArguments("--disable-default-apps");
    opts.addArguments("--disable-popup-blocking");
    opts.addArguments("--memory-pressure-off");
    opts.addArguments("--js-flags=--max-old-space-size=512");
    opts.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-logging"));
    return opts;
  }

  private static BlockingQueue<WebDriver> initDriverPool() {
    BlockingQueue<WebDriver> pool = new ArrayBlockingQueue<>(POOL_SIZE);
    for (int i = 0; i < POOL_SIZE; i++) {
      try {
        WebDriver driver = createNewDriver();
        if (driver != null) {
          pool.offer(driver);
        }
      } catch (Exception e) {
        logger.error("Failed to initialize driver", e);
      }
    }
    if (pool.isEmpty()) {
      throw new IllegalStateException("Failed to initialize any WebDriver instances");
    }
    return pool;
  }

  private static WebDriver createNewDriver() {
    try {
      return new ChromeDriver(getOptions());
    } catch (Exception e) {
      logger.error("Failed to create new WebDriver", e);
      return null;
    }
  }

  public static byte[] convertHtmlToPdf(String htmlContent) throws EArsivException {
    if (isShutdown.get()) {
      throw new EArsivException("Converter is shutdown");
    }

    WebDriver driver = null;
    int retryCount = 0;

    while (retryCount < MAX_RETRIES) {
      try {
        driver = getDriverPool().poll(WAIT_TIME_SECONDS, TimeUnit.SECONDS);
        if (driver == null) {
          throw new EArsivException("Failed to acquire WebDriver from pool");
        }

        synchronized (driver) {
          String encodedHtml = Base64.getEncoder().encodeToString(htmlContent.getBytes("UTF-8"));
          String dataUrl = "data:text/html;base64," + encodedHtml;

          driver.get(dataUrl);

          new WebDriverWait(driver, Duration.ofSeconds(WAIT_TIME_SECONDS))
              .until(
                  webDriver ->
                      ((JavascriptExecutor) webDriver)
                          .executeScript("return document.readyState")
                          .equals("complete"));

          PrintOptions printOptions = new PrintOptions();
          printOptions.setPageRanges("1-");

          Pdf pdf = ((PrintsPage) driver).print(printOptions);
          byte[] result = Base64.getDecoder().decode(pdf.getContent());

          returnDriverToPool(driver);
          driver = null;

          return result;
        }

      } catch (Exception e) {
        logger.error("PDF conversion attempt {} failed", retryCount + 1, e);
        retryCount++;
        if (driver != null) {
          refreshDriver(driver);
        }

        if (retryCount >= MAX_RETRIES) {
          throw new EArsivException(
              "PDF conversion failed after " + MAX_RETRIES + " attempts: " + e.getMessage());
        }
      }
    }

    throw new EArsivException("Unexpected error in PDF conversion");
  }

  private static void returnDriverToPool(WebDriver driver) {
    if (driver != null && !isShutdown.get()) {
      try {
        synchronized (driver) {
          ((JavascriptExecutor) driver).executeScript("window.localStorage.clear();");
          ((JavascriptExecutor) driver).executeScript("window.sessionStorage.clear();");
          getDriverPool().offer(driver);
        }
      } catch (Exception e) {
        logger.error("Failed to return driver to pool", e);
        refreshDriver(driver);
      }
    }
  }

  private static void refreshDriver(WebDriver driver) {
    try {
      synchronized (driver) {
        driver.quit();
      }
      if (!isShutdown.get()) {
        WebDriver newDriver = createNewDriver();
        if (newDriver != null) {
          getDriverPool().offer(newDriver);
        }
      }
    } catch (Exception ex) {
      logger.error("Failed to refresh WebDriver", ex);
    }
  }

  public static void shutdown() {
    shutdownLock.lock();
    try {
      if (isShutdown.compareAndSet(false, true)) {
        logger.info("Shutting down HtmlToPdfConverter");
        getDriverPool()
            .forEach(
                driver -> {
                  try {
                    driver.quit();
                  } catch (Exception e) {
                    logger.error("Error while shutting down driver", e);
                  }
                });
        getDriverPool().clear();
      }
    } finally {
      shutdownLock.unlock();
    }
  }

  public static boolean isShutdown() {
    return isShutdown.get();
  }
}
