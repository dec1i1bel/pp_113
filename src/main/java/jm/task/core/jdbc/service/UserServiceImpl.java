package jm.task.core.jdbc.service;

import jm.task.core.jdbc.dao.UserDaoJDBCImpl;
import jm.task.core.jdbc.model.User;

import java.util.List;

public class UserServiceImpl implements UserService {
    private final UserDaoJDBCImpl dao;

    public UserServiceImpl() {
        dao = new UserDaoJDBCImpl();
    }

    public void createUsersTable() {
        dao.createUsersTable();
    }

    public void dropUsersTable() {
        dao.dropUsersTable();
    }

    public void saveUser(String name, String lastName, byte age) {
        if (name == null || name.isBlank() || lastName == null || lastName.isBlank() || age <= 0) {
            throw new IllegalArgumentException("Service save user: all fields are required");
        }

        dao.saveUser(name, lastName, age);
    }

    public void removeUserById(long id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Service removing user by id: id must be more than 0");
        }

        dao.removeUserById(id);
    }

    public List<User> getAllUsers() {
        return dao.getAllUsers();
    }

    public void cleanUsersTable() {
        dao.cleanUsersTable();
    }
}
