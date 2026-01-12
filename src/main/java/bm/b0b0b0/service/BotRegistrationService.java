package bm.b0b0b0.service;

import bm.b0b0b0.bot.AutoCommentBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class BotRegistrationService {
    
    public static void registerBot(AutoCommentBot bot) throws TelegramApiException {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        
        try {
            botsApi.registerBot(bot);
        } catch (TelegramApiException e) {
            Throwable cause = e.getCause();
            String errorMessage = e.getMessage();
            
            boolean isWebhook404Error = (errorMessage != null && 
                (errorMessage.contains("404") || 
                 errorMessage.contains("Error removing old webhook") ||
                 errorMessage.contains("Not Found"))) ||
                (cause != null && cause.getMessage() != null && 
                 cause.getMessage().contains("404"));
            
            if (isWebhook404Error) {
                try {
                    Thread.sleep(500);
                    botsApi.registerBot(bot);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new TelegramApiException("Прервано при регистрации бота", ie);
                } catch (TelegramApiException retryException) {
                    String retryMessage = retryException.getMessage();
                    if (retryMessage == null || 
                        (!retryMessage.contains("404") && 
                         !retryMessage.contains("webhook") &&
                         !retryMessage.contains("Not Found"))) {
                        throw retryException;
                    }
                }
            } else {
                throw e;
            }
        }
    }
}
