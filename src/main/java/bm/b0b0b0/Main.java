package bm.b0b0b0;

import bm.b0b0b0.bot.AutoCommentBot;
import bm.b0b0b0.config.BotConfig;
import bm.b0b0b0.config.TelegramKeyConfig;
import bm.b0b0b0.service.BotRegistrationService;
import bm.b0b0b0.service.ConfigInitializationService;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        System.setProperty("file.encoding", "UTF-8");
        System.setProperty("console.encoding", "UTF-8");
        try {
            boolean configsCreated = ConfigInitializationService.initializeConfigs();
            
            if (configsCreated || !ConfigInitializationService.validateConfigs()) {
                ConfigInitializationService.printConfigurationInstructions();
                System.exit(0);
            }
            
            TelegramKeyConfig keyConfig = TelegramKeyConfig.load();
            BotConfig botConfig = BotConfig.load();
            
            AutoCommentBot bot = new AutoCommentBot(keyConfig, botConfig);
            BotRegistrationService.registerBot(bot);
            
            System.out.println("Бот запущен и готов к работе!");
        } catch (TelegramApiException | IOException e) {
            System.err.println("Ошибка при запуске бота: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}

