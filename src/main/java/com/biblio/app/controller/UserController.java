package com.biblio.app.controller;

import com.biblio.app.model.User;
import com.biblio.dao.UserDao;
import java.sql.SQLException;

public class UserController {

    private final UserDao userDao;

    public UserController() {
        this.userDao = new UserDao();
    }

    public boolean registerUser(String firstName, String lastName, String email, String password, String gender, String phone) {

        if (userExists(email) || userExists(phone)) {
            return false;
        }
        userDao.setUser(firstName, lastName, email, password, gender, phone);

        try {
            return userDao.create();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean authenticateUser(String emailOrPhone, String password) {

        User user = getUserByEmailOrPhone(emailOrPhone);

        if (user != null) {
            return this.userDao.checkPassword(password, user.getPassword());
        }

        return false;
    }

    public User getUserByEmailOrPhone(String emailOrPhone) {
        if (isEmail(emailOrPhone)) {
            this.userDao.setEmail(emailOrPhone);
            return this.userDao.getByEmailWithRoles();
        } else if (isPhoneNumber(emailOrPhone)) {
            this.userDao.setPhone(emailOrPhone);
            return this.userDao.getByPhoneWithRoles();
        }

        return null;
    }

    private boolean isEmail(String input) {
        return input.contains("@");
    }

    private boolean isPhoneNumber(String input) {
        return input.matches("\\d{10}");
    }

    private boolean userExists(String emailOrPhone) {
        User existingUser = getUserByEmailOrPhone(emailOrPhone);
        return existingUser != null;
    }
}
