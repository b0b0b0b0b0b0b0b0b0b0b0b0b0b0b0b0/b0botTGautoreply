package bm.b0b0b0.handler;

import bm.b0b0b0.config.BotConfig;
import bm.b0b0b0.config.ChannelConfig;
import bm.b0b0b0.config.CommentConfig;
import bm.b0b0b0.service.CommentService;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ChannelMessageHandler {
    private final BotConfig config;
    private final Map<String, CommentService> commentServices;
    
    public ChannelMessageHandler(BotConfig config) throws IOException {
        this.config = config;
        this.commentServices = new HashMap<>();
        
        if (config.getChannels() != null) {
            for (ChannelConfig channelConfig : config.getChannels()) {
                CommentConfig commentConfig = CommentConfig.load(channelConfig.getCommentConfigFile());
                CommentService commentService = new CommentService(commentConfig);
                commentServices.put(channelConfig.getChannelId(), commentService);
            }
        }
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
        
        ChannelConfig targetChannelConfig = null;
        
        if (config.getChannels() != null) {
            for (ChannelConfig channelConfig : config.getChannels()) {
                boolean matches = channelConfig.getChannelId() == null ||
                    (channelUsername != null && (
                        channelConfig.getChannelId().equals("@" + channelUsername) ||
                        channelConfig.getChannelId().equals(channelUsername)
                    )) ||
                    (channelId != null && channelConfig.getChannelId().equals(String.valueOf(channelId)));
                
                if (matches) {
                    targetChannelConfig = channelConfig;
                    break;
                }
            }
        }
        
        if (targetChannelConfig == null) {
            return;
        }
        
        if (messageToProcess.getFrom() != null) {
            Long userId = messageToProcess.getFrom().getId();
            
            if (targetChannelConfig.getTargetUserId() != null && 
                targetChannelConfig.getTargetUserId().equals(userId)) {
                return;
            }
        }
        
        CommentService service = commentServices.get(targetChannelConfig.getChannelId());
        if (service == null) {
            return;
        }
        
        try {
            Thread.sleep(500);
            Integer messageId = messageToProcess.getMessageId();
            service.sendComment(bot, chatId, messageId);
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
