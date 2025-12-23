package service;

import model.User;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AuthService {
    private static final String USERS_FILE = "users.csv";
    private User currentUser;

    public AuthService() {
        initializeUsersFile();
    }

    private void initializeUsersFile() {
        File file = new File(USERS_FILE);
        if (!file.exists()) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                writer.println("username,password");
                writer.println("admin,admin123");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean login(String username, String password) {
        List<User> users = loadUsers();
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                currentUser = user;
                return true;
            }
        }
        return false;
    }

    public void logout() {
        currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    private List<User> loadUsers() {
        List<User> users = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE))) {
            String line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    users.add(new User(parts[0], parts[1]));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return users;
    }

    public boolean registerUser(String username, String password) {
        List<User> users = loadUsers();
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return false;
            }
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(USERS_FILE, true))) {
            writer.println(username + "," + password);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updatePassword(String username, String newPassword) {
        List<User> users = loadUsers();
        boolean found = false;

        for (User user : users) {
            if (user.getUsername().equals(username)) {
                user.setPassword(newPassword);
                found = true;
                break;
            }
        }

        if (!found)
            return false;

        try (PrintWriter writer = new PrintWriter(new FileWriter(USERS_FILE))) {
            writer.println("username,password");
            for (User user : users) {
                writer.println(user.getUsername() + "," + user.getPassword());
            }
            // Update current user session if applicable
            if (currentUser != null && currentUser.getUsername().equals(username)) {
                currentUser.setPassword(newPassword);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}