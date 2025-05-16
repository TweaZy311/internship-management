package org.example.internship.utils;

import org.example.internship.config.properties.TelegramProperties;
import org.example.internship.exception.ErrorCode;
import org.example.internship.exception.ServiceException;
import org.example.internship.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Component
public class NotificationBot extends TelegramLongPollingBot {
    private final TelegramProperties telegramProperties;
    private final UserService userService;

    @Autowired
    public NotificationBot(TelegramProperties telegramProperties, UserService userService) {
        super(telegramProperties.getToken());
        this.telegramProperties = telegramProperties;
        this.userService = userService;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (telegramProperties.getEnabled() == Boolean.FALSE){
            return;
        }

        if (update.hasMessage()) {
            String text = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();

            if (text.startsWith("/connect ")) {
                String username = text.substring(9);
                userService.linkTelegramChatId(username, chatId);
                sendMessage(chatId, "Ваш Telegram успешно привязан!");
            }
        }
    }

    @Override
    public String getBotUsername() {
        return telegramProperties.getUsername();
    }

    @Override
    public void onUpdatesReceived(List<Update> updates) {
        super.onUpdatesReceived(updates);
    }

    public void sendMessage(Long chatId, String text) {
        if (telegramProperties.getEnabled() == Boolean.FALSE){
            return;
        }

        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new ServiceException(e, ErrorCode.TGM_400.getCode());
        }
    }
}
