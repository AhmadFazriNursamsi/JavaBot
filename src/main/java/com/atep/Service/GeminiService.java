package com.atep.Service;
import com.google.gson.*;
import okhttp3.*;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import io.github.cdimascio.dotenv.Dotenv;


@Service
public class GeminiService {
    private static final Dotenv dotenv = Dotenv.load();
    private static final String API_KEY = dotenv.get("GEMINI_API_KEY");
    

    private static final String URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + API_KEY;

    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();

    private final List<JsonObject> chatHistory = new ArrayList<>();

    public String chat(String userInput) throws IOException {
        // Tambahkan input user ke history
        JsonObject userMessage = new JsonObject();
        userMessage.addProperty("role", "user");

        JsonArray userParts = new JsonArray();
        JsonObject userText = new JsonObject();
        String styledPrompt = "Kamu adalah AI sarkastik. Gunakan percakapan sebelumnya untuk membalas dengan nada menyindir. " + userInput;

        userText.addProperty("text", styledPrompt);
        userParts.add(userText);
        userMessage.add("parts", userParts);

        chatHistory.add(userMessage);

        // Buat request JSON
        JsonObject requestBodyJson = new JsonObject();
        JsonArray contents = new JsonArray();
        for (JsonObject message : chatHistory) {
            contents.add(message);
        }
        requestBodyJson.add("contents", contents);

        String jsonBody = gson.toJson(requestBodyJson);

        RequestBody body = RequestBody.create(jsonBody, MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder().url(URL).post(body).build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
            
                JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();
            
                JsonArray candidates = jsonResponse.getAsJsonArray("candidates");
            
                if (candidates != null && !candidates.isEmpty()) {
                    JsonObject content = candidates.get(0).getAsJsonObject().getAsJsonObject("content");
                    JsonArray parts = content.getAsJsonArray("parts");
            
                    if (parts != null && !parts.isEmpty()) {
                        String botReply = parts.get(0).getAsJsonObject().get("text").getAsString();
            
                        // Tambahkan balasan bot ke history
                        JsonObject botMessage = new JsonObject();
                        botMessage.addProperty("role", "model");
            
                        JsonArray botParts = new JsonArray();
                        JsonObject botText = new JsonObject();
                        botText.addProperty("text", botReply);
                        botParts.add(botText);
                        botMessage.add("parts", botParts);
            
                        chatHistory.add(botMessage);
            
                        return botReply;
                    } else {
                        return "⚠️ Tidak ada teks dalam response Gemini.";
                    }
                } else {
                    return "⚠️ Tidak ada kandidat jawaban dari Gemini.";
                }
            
            } else {
                return "❌ Request gagal: HTTP " + response.code();
            }            
        }
    }

    public void resetChat() {
        chatHistory.clear();
    }

}
