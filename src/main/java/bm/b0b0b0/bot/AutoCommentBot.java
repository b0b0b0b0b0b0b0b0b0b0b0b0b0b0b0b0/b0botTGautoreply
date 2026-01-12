package bm.b0b0b0.bot;

import bm.b0b0b0.config.BotConfig;
import bm.b0b0b0.config.CommentConfig;
import bm.b0b0b0.config.TelegramKeyConfig;
import bm.b0b0b0.handler.ChannelMessageHandler;
import bm.b0b0b0.service.CommentService;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.IOException;

public class AutoCommentBot extends TelegramLongPollingBot {
    private final String botToken;
    private final String botUsername;
    private final ChannelMessageHandler messageHandler;
    
    public AutoCommentBot(TelegramKeyConfig keyConfig, BotConfig botConfig) throws IOException {
        super(keyConfig.getBotToken());
        this.botToken = keyConfig.getBotToken();
        this.botUsername = keyConfig.getBotUsername();
        CommentConfig commentConfig = CommentConfig.load(botConfig.getCommentConfigFile());
        CommentService commentService = new CommentService(commentConfig);
        this.messageHandler = new ChannelMessageHandler(botConfig, commentService);
    }
    
    @Override
    public void onUpdateReceived(Update update) {
        messageHandler.handleUpdate(update, this);
    }
    
    @Override
    public String getBotUsername() {
        return botUsername;
    }
    
    @Override
    public String getBotToken() {
        return botToken;
    }
}
