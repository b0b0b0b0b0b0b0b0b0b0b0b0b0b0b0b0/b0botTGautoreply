package bm.b0b0b0.config;

import com.google.gson.Gson;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class BotConfig {
    private String channelId;
    private Long targetUserId;
    private String commentConfigFile;
    
    private BotConfig() {}
    
    public static BotConfig load() throws IOException {
        Gson gson = new Gson();
        try (InputStreamReader reader = new InputStreamReader(
                new FileInputStream("bot-config.json"), StandardCharsets.UTF_8)) {
            return gson.fromJson(reader, BotConfig.class);
        }
    }
    
    public String getChannelId() {
        return channelId;
    }
    
    public Long getTargetUserId() {
        return targetUserId;
    }
    
    public String getCommentConfigFile() {
        return commentConfigFile;
    }
}
