package user.service;

import base.BaseDatabase;
import org.telegram.telegrambots.meta.api.objects.Update;
import user.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserService extends BaseDatabase {

    String CHECK_USER = "select * from users where chat_id = ?";
    String ADD_USER = "insert into users (name, username, chat_id) values (?, ?, ?)";
    String USER_LIST = "select * from users";

    public List<User> getUsers() {
        List<User> users = new ArrayList<>();
        try (Connection connection = getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement(USER_LIST);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                User user = new User();
                user.setId(resultSet.getLong("id"));
                user.setName(resultSet.getString("name"));
                user.setUsername(resultSet.getString("username"));
                user.setChatId(resultSet.getString("chat_id"));
                users.add(user);
            }
            return users;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public boolean checkUser(Update update) {
        String chatId = update.getMessage().getChatId().toString();
        try (Connection connection = getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement(CHECK_USER);
            preparedStatement.setString(1, chatId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return true;
            } else {
                addUser(update);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void addUser(Update update) {
        String chatId = update.getMessage().getChatId().toString();
        String name = update.getMessage().getChat().getFirstName();
        String username = update.getMessage().getChat().getUserName();
        try (Connection connection = getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement(ADD_USER);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, username);
            preparedStatement.setString(3, chatId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
