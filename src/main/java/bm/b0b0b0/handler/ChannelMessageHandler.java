package bm.b0b0b0.handler;

import bm.b0b0b0.config.BotConfig;
import bm.b0b0b0.service.CommentService;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class ChannelMessageHandler {
    private final BotConfig config;
    private final CommentService commentService;
    
    public ChannelMessageHandler(BotConfig config, CommentService commentService) {
        this.config = config;
        this.commentService = commentService;
    }
    
    public void handleUpdate(Update update, TelegramLongPollingBot bot) {
        
        Message messageToProcess = null;
        Long chatId = null;
        String channelUsername = null;
        Long channelId = null;
        
        if (update.hasChannelPost()) {
            messageToProcess = update.getChannelPost();
            chatId = messageToProcess.getChatId();
            channelUsername = messageToProcess.getChat().getUserName();
            channelId = chatId;
        } else if (update.hasMessage()) {
            Message message = update.getMessage();
            
            if (message.getChat().getType().equals("channel")) {
                messageToProcess = message;
                chatId = message.getChatId();
                channelUsername = message.getChat().getUserName();
                channelId = chatId;
            } else if (message.getSenderChat() != null && message.getSenderChat().getType().equals("channel")) {
                messageToProcess = message;
                chatId = message.getChatId();
                channelUsername = message.getSenderChat().getUserName();
                channelId = message.getSenderChat().getId();
            }
        }
        
        if (messageToProcess == null) {
            return;
        }
        
        boolean isTargetChannel = config.getChannelId() == null || 
            (channelUsername != null && (
                config.getChannelId().equals("@" + channelUsername) ||
                config.getChannelId().equals(channelUsername)
            )) ||
            (channelId != null && config.getChannelId().equals(String.valueOf(channelId)));
        
        if (!isTargetChannel) {
            return;
        }
        
        if (messageToProcess.getFrom() != null) {
            Long userId = messageToProcess.getFrom().getId();
            
            if (config.getTargetUserId() != null && config.getTargetUserId().equals(userId)) {
                return;
            }
        }
        
        try {
            Thread.sleep(500);
            Integer messageId = messageToProcess.getMessageId();
            commentService.sendComment(bot, chatId, messageId);
        } catch (TelegramApiException e) {
            System.err.println("Ошибка при отправке комментария: " + e.getMessage());
            e.printStackTrace();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Прервано: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
