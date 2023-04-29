package dao;
import dbconnection.ConnectionPool;
import lombok.SneakyThrows;
import models.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public static void addUser(User user) throws SQLException, InterruptedException {
        ConnectionPool cd = ConnectionPool.getConnectionPool();
        Connection connection = cd.getConnection();
        String sql = "INSERT INTO public.user (email, password, role,fullname) VALUES (?, ?, ?, ?)";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, user.getEmail());
        statement.setString(2, user.getPassword());
        statement.setString(3, user.getRole());
        statement.setString(4,user.getFullname());
        statement.executeUpdate();
        cd.releaseConnection(connection);
    }

    public static User getUserById(int id) throws SQLException, InterruptedException {
        ConnectionPool cd = ConnectionPool.getConnectionPool();
        Connection connection = cd.getConnection();
        String sql = "SELECT * FROM public.user WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, id);
        ResultSet result = statement.executeQuery();
        if (result.next()) {
            User user = new User();
            user.setId(result.getInt("id"));
            user.setEmail(result.getString("email"));
            user.setPassword(result.getString("password"));
            user.setRole(result.getString("role"));
            user.setFullname(result.getString("fullname"));
            cd.releaseConnection(connection);
            return user;
        } else {
            cd.releaseConnection(connection);
            return null;
        }
    }

    public static List<User> getAllUsers() throws SQLException, InterruptedException {
        ConnectionPool cd = ConnectionPool.getConnectionPool();
        Connection connection = cd.getConnection();
        String sql = "SELECT * FROM public.user";
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet result = statement.executeQuery();
        List<User> users = new ArrayList<>();
        System.out.println(result);
        while (result.next()) {
            User user = new User();
            user.setId(result.getInt("id"));
            user.setEmail(result.getString("email"));
            user.setPassword(result.getString("password"));
            user.setRole(result.getString("role"));
            user.setFullname(result.getString(("fullname")));
            users.add(user);
        }
        cd.releaseConnection(connection);
        return users;
    }

    public static void update(User user) throws SQLException, InterruptedException {
        ConnectionPool cd = ConnectionPool.getConnectionPool();
        Connection connection = cd.getConnection();
        String sql = "UPDATE public.user SET email = ?, password = ?, role = ?, fullname = ? WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, user.getEmail());
        statement.setString(2, user.getPassword());
        statement.setString(3, user.getRole());
        statement.setString(4, user.getFullname());
        statement.setInt(5, user.getId());
        statement.executeUpdate();
        System.out.println(user.getFullname());
        System.out.println(statement.executeUpdate());
        statement.close();
        cd.releaseConnection(connection);
    }

    public static void delete(int id) throws SQLException, InterruptedException {
        ConnectionPool cd = ConnectionPool.getConnectionPool();
        Connection connection = cd.getConnection();
        String sql = "DELETE FROM public.user WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, id);
        statement.executeUpdate();
        cd.releaseConnection(connection);
    }

    @SneakyThrows
    public static User getUserByEmail(String email){
        ConnectionPool cd = ConnectionPool.getConnectionPool();
        Connection connection = cd.getConnection();
        String sql = "SELECT * FROM public.user WHERE email = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, email);
        ResultSet result = statement.executeQuery();
        if (result.next()) {
            User user = new User();
            user.setId(result.getInt("id"));
            user.setEmail(result.getString("email"));
            user.setPassword(result.getString("password"));
            user.setRole(result.getString("role"));
            user.setFullname(result.getString("fullname"));
            return user;
        } else {
            return null;
        }
    }
}
