package me.kartonixon.bettergiveaways;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.*;

public class DiscordWebhookMessage {

    private static final Gson gson = new GsonBuilder().create();

    public static void sendWebhook(String webhookUrl, String embedTitle, String embedContent, String embedImage) {

        try {

            URL url = new URL(webhookUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
    
            // Crear el objeto JSON para el mensaje embed
            JsonObject embedJson = new JsonObject();
            embedJson.addProperty("title", embedTitle);
            embedJson.addProperty("description", embedContent);
    
            // Crear el objeto JSON para la imagen
            JsonObject webhookJson = new JsonObject();
            webhookJson.addProperty("content", "");
            webhookJson.add("embeds", gson.toJsonTree(new JsonObject[]{embedJson}));
    
            String json = gson.toJson(webhookJson);
    
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(json.getBytes("UTF-8"));
            outputStream.close();
    
            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("[BetterGiveaways] Discord embed sended.");
            } else {
                System.out.println("[BetterGiveaways] Error to send webhook. Error: " + responseCode);
            }

            connection.disconnect();
        } catch (Exception e) {

            e.printStackTrace();
            
        }


    }

}