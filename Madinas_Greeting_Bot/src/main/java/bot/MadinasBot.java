package bot;

import bot.service.KeyBoardService;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import user.model.User;
import user.service.UserService;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static security.Security.*;

public class MadinasBot extends TelegramLongPollingBot {

    private UserService userService = new UserService();
    private KeyBoardService keyBoardService = new KeyBoardService();

    @Override
    public String getBotUsername() {
        return USERNAME;
    }

    @Override
    public String getBotToken() {
        return TOKEN;
    }

    @Override
    public void onUpdateReceived(Update update) {
        userService.checkUser(update);
        String message = update.getMessage().getText();
        if(message.equals("/start")){
            sendMessage(new SendMessage(update.getMessage().getChatId().toString(), "Assalamu alaykum, Men Madinaning salom beruvchi botiman.\n"));
        } else if(message.equals("/admin")){
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(update.getMessage().getChatId().toString());
            sendMessage.setText("Assalamu alaykum Admin!\n" + "Xush kelibsiz");
            sendMessage.setReplyMarkup(keyBoardService.getMainKeyboard());
            sendMessage(sendMessage);
        } else if(message.equals("Say Assalamu alaykum")){
            sendMessageAllUsers("Assalamu alaykum");
        } else if (message.equals("Say GOODBYE")){
            sendMessageAllUsers("Hayr haxshi qolinglar");
        }
    }

    private void sendMessage(SendMessage sendMessage){
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendMessageAllUsers(String message){
        ExecutorService executorService = Executors.newCachedThreadPool();
        List<User> users = userService.getUsers();
        for (User user : users) {
            executorService.submit(() -> {
                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(user.getChatId());
                sendMessage.setText(message);
                sendMessage(sendMessage);
            });
        }
    }
}
