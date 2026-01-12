package bm.b0b0b0.service;

import bm.b0b0b0.config.CommentConfig;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

public class CommentService {
    private final CommentConfig commentConfig;
    
    public CommentService(CommentConfig commentConfig) {
        this.commentConfig = commentConfig;
    }
    
    public void sendComment(AbsSender sender, Long chatId, Integer replyToMessageId) throws TelegramApiException {
        InlineKeyboardMarkup keyboard = buildKeyboard();
        
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId.toString());
        sendMessage.setText(commentConfig.getText());
        sendMessage.setReplyToMessageId(replyToMessageId);
        sendMessage.setReplyMarkup(keyboard);
        
        sender.execute(sendMessage);
    }
    
    private InlineKeyboardMarkup buildKeyboard() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        
        if (commentConfig.getButtons() != null) {
            List<InlineKeyboardButton> currentRow = new ArrayList<>();
            
            for (int i = 0; i < commentConfig.getButtons().length; i++) {
                CommentConfig.ButtonConfig buttonConfig = commentConfig.getButtons()[i];
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setText(buttonConfig.getText());
                button.setUrl(buttonConfig.getUrl());
                currentRow.add(button);
                
                if (currentRow.size() == 2 || i == commentConfig.getButtons().length - 1) {
                    keyboard.add(new ArrayList<>(currentRow));
                    currentRow.clear();
                }
            }
        }
        
        markup.setKeyboard(keyboard);
        return markup;
    }
}
