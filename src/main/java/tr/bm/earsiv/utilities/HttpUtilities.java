package tr.bm.earsiv.utilities;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Iterator;
import org.json.JSONArray;
import org.json.JSONObject;
import tr.bm.earsiv.exceptions.EArsivException;

public class HttpUtilities {

  private static final HttpClient client =
      HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(30)).build();

  public static JSONObject post(String url, JSONObject requestBody, String successKey)
      throws EArsivException {
    try {
      StringBuilder formData = new StringBuilder();
      Iterator<String> keys = requestBody.keys();
      boolean isFirst = true;
      while (keys.hasNext()) {
        String key = keys.next();
        if (!isFirst) {
          formData.append("&");
        }
        String encodedKey = URLEncoder.encode(key, StandardCharsets.UTF_8);
        String encodedValue =
            URLEncoder.encode(requestBody.get(key).toString(), StandardCharsets.UTF_8);
        formData.append(encodedKey).append("=").append(encodedValue);
        isFirst = false;
      }

      HttpRequest request =
          HttpRequest.newBuilder()
              .uri(URI.create(url))
              .header("Content-Type", "application/x-www-form-urlencoded; charset=utf-8")
              .POST(HttpRequest.BodyPublishers.ofString(formData.toString()))
              .build();

      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

      if (response.statusCode() < 200 || response.statusCode() >= 300) {
        throw new EArsivException("HTTP error: " + response.statusCode());
      }

      JSONObject responseJson = new JSONObject(response.body());
      checkError(responseJson, successKey);
      return responseJson;

    } catch (IOException | InterruptedException e) {
      throw new EArsivException("Connection error: " + e.getMessage());
    } catch (Exception e) {
      if (e instanceof EArsivException) {
        throw e;
      }
      throw new EArsivException("Unexpected error: " + e.getMessage());
    }
  }

  private static void checkError(JSONObject responseJson, String successKey)
      throws EArsivException {
    if (responseJson.has("error")) {
      String errorMessage = "System error";
      try {
        if (responseJson.has("messages")) {
          JSONArray messages = responseJson.getJSONArray("messages");
          StringBuilder messageBuilder = new StringBuilder();

          for (int i = 0; i < messages.length(); i++) {
            Object message = messages.get(i);
            if (message instanceof JSONObject) {
              JSONObject msgObj = (JSONObject) message;
              if (msgObj.has("text")) {
                if (messageBuilder.length() > 0) {
                  messageBuilder.append("; ");
                }
                messageBuilder.append(msgObj.getString("text"));
              }
            } else if (message instanceof String) {
              if (messageBuilder.length() > 0) {
                messageBuilder.append("; ");
              }
              messageBuilder.append(message);
            }
          }

          if (messageBuilder.length() > 0) {
            errorMessage = messageBuilder.toString();
          }
        }
      } catch (Exception e) {
        throw new EArsivException("Error parsing error messages: " + e.getMessage());
      }
      throw new EArsivException(errorMessage);
    } else if (successKey != null) {
      if (!responseJson.toString().contains(successKey)) {
        throw new EArsivException("Success key not found: " + successKey);
      }
    }
  }
}
