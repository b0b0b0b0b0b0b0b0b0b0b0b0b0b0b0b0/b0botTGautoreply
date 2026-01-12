package bm.b0b0b0.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class ConfigInitializationService {
    
    private static final String[] CONFIG_FILES = {
        "telegram-key.json",
        "bot-config.json",
        "comment-config.json"
    };
    
    public static boolean initializeConfigs() throws IOException {
        boolean anyCreated = false;
        for (String configFile : CONFIG_FILES) {
            File file = new File(configFile);
            if (!file.exists()) {
                copyFromResources(configFile);
                System.out.println("Создан файл конфигурации: " + configFile);
                anyCreated = true;
            }
        }
        return anyCreated;
    }
    
    private static void copyFromResources(String fileName) throws IOException {
        InputStream resourceStream = ConfigInitializationService.class
            .getClassLoader()
            .getResourceAsStream(fileName);
        
        if (resourceStream == null) {
            throw new IOException("Не найден ресурс: " + fileName);
        }
        
        Files.copy(resourceStream, new File(fileName).toPath(), StandardCopyOption.REPLACE_EXISTING);
        resourceStream.close();
    }
    
    public static boolean validateConfigs() {
        File telegramKeyFile = new File("telegram-key.json");
        if (!telegramKeyFile.exists()) {
            return false;
        }
        
        try {
            bm.b0b0b0.config.TelegramKeyConfig keyConfig = bm.b0b0b0.config.TelegramKeyConfig.load();
            
            if (keyConfig.getBotToken() == null || keyConfig.getBotToken().trim().isEmpty()) {
                return false;
            }
            
            File botConfigFile = new File("bot-config.json");
            if (!botConfigFile.exists()) {
                return false;
            }
            
            bm.b0b0b0.config.BotConfig botConfig = bm.b0b0b0.config.BotConfig.load();
            
            String commentConfigFile = botConfig.getCommentConfigFile();
            File commentFile = new File(commentConfigFile);
            if (!commentFile.exists()) {
                return false;
            }
            
            return true;
        } catch (IOException e) {
            return false;
        }
    }
    
    public static void printConfigurationInstructions() {
        System.out.println();
        System.out.println("========================================");
        System.out.println("НЕОБХОДИМО НАСТРОИТЬ КОНФИГУРАЦИЮ");
        System.out.println("========================================");
        System.out.println();
        System.out.println("1. Откройте файл telegram-key.json и укажите:");
        System.out.println("   - botToken: токен вашего бота от @BotFather");
        System.out.println("   - botUsername: username бота (опционально)");
        System.out.println();
        System.out.println("2. Откройте файл bot-config.json и укажите:");
        System.out.println("   - channelId: username вашего канала (например, @my_channel)");
        System.out.println("   - targetUserId: ваш Telegram User ID (опционально, если хотите исключить свои сообщения)");
        System.out.println();
        System.out.println("3. Откройте файл comment-config.json и настройте:");
        System.out.println("   - text: текст комментария");
        System.out.println("   - buttons: кнопки с ссылками");
        System.out.println();
        System.out.println("После настройки запустите бота снова.");
        System.out.println("========================================");
    }
}
