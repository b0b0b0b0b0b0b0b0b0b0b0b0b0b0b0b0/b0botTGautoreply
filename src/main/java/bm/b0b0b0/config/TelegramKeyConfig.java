package bm.b0b0b0.config;

import com.google.gson.Gson;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class TelegramKeyConfig {
    private String botToken;
    private String botUsername;
    
    private TelegramKeyConfig() {}
    
    public static TelegramKeyConfig load() throws IOException {
        Gson gson = new Gson();
        try (InputStreamReader reader = new InputStreamReader(
                new FileInputStream("telegram-key.json"), StandardCharsets.UTF_8)) {
            return gson.fromJson(reader, TelegramKeyConfig.class);
        }
    }
    
    public String getBotToken() {
        return botToken;
    }
    
    public String getBotUsername() {
        return botUsername != null ? botUsername : "AutoCommentBot";
    }
}
