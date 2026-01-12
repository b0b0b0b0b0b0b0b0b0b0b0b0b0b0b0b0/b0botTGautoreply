package bm.b0b0b0.config;

import com.google.gson.Gson;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class CommentConfig {
    private String text;
    private ButtonConfig[] buttons;
    
    private CommentConfig() {}
    
    public static CommentConfig load(String configFile) throws IOException {
        Gson gson = new Gson();
        try (InputStreamReader reader = new InputStreamReader(
                new FileInputStream(configFile), StandardCharsets.UTF_8)) {
            return gson.fromJson(reader, CommentConfig.class);
        }
    }
    
    public String getText() {
        return text;
    }
    
    public ButtonConfig[] getButtons() {
        return buttons;
    }
    
    public static class ButtonConfig {
        private String text;
        private String url;
        
        public String getText() {
            return text;
        }
        
        public String getUrl() {
            return url;
        }
    }
}
