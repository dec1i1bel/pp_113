package jm.task.core.jdbc.dao;

import jm.task.core.jdbc.model.User;
import jm.task.core.jdbc.util.Util;
import io.github.cdimascio.dotenv.Dotenv;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserDaoJDBCImpl implements UserDao {
    private static final String USER_TABLE_NAME = "user";

    private final String hostName;
    private final String dbName;
    private final String userName;
    private final String password;

    public UserDaoJDBCImpl() {
        Dotenv dotenv = Dotenv.load();
        this.hostName = dotenv.get("DB_HOSTNAME");
        this.dbName = dotenv.get("DB_NAME");
        this.userName = dotenv.get("DB_USERNAME");
        this.password = dotenv.get("DB_PASSWORD");
    }

    public void createUsersTable() {
        try (Connection con = Util.connectMySQL(this.hostName, this.dbName, this.userName, this.password)) {
            boolean isTableExist = checkTableExistence(con);

            if (isTableExist) {
                return;
            }

            String sql = String.format("""
                    CREATE TABLE %s (
                        id INT PRIMARY KEY AUTO_INCREMENT,
                        name VARCHAR(20) NOT NULL,
                        last_name VARCHAR(20) NOT NULL,
                        age TINYINT
                    );""", USER_TABLE_NAME);

            execSqlUpdate(sql, con);
        } catch (SQLException e) {
            System.out.printf("SQL exception when creating table 'users': %s. Stack trace: %s%n",
                    e.getMessage(),
                    Arrays.toString(e.getStackTrace())
            );
        }
    }

    public void dropUsersTable() {
        try (Connection con = Util.connectMySQL(this.hostName, this.dbName, this.userName, this.password)) {
            boolean isTableExist = checkTableExistence(con);

            if (!isTableExist) {
                return;
            }

            String sql = String.format("DROP TABLE %s;", USER_TABLE_NAME);

            execSqlUpdate(sql, con);
        } catch (SQLException e) {
            System.out.printf("SQL exception when creating table 'users': %s. Stack trace: %s%n",
                    e.getMessage(),
                    Arrays.toString(e.getStackTrace())
            );
        }
    }

    public void saveUser(String name, String lastName, byte age) {
        if (name == null || name.isBlank() || lastName == null || lastName.isBlank() || age <= 0) {
            throw new IllegalArgumentException("DAO save user: all fields are required");
        }

        try (Connection con = Util.connectMySQL(this.hostName, this.dbName, this.userName, this.password)) {
            String sql = String.format("""
                    INSERT INTO user(name, last_name, age)
                    VALUES('%s', '%s', '%d');
                """, name, lastName, age);

            execSqlUpdate(sql, con);
        } catch (SQLException e) {
            System.out.printf("SQL exception when saving user: %s. Stack trace: %s%n",
                    e.getMessage(),
                    Arrays.toString(e.getStackTrace())
            );
        }
    }

    public void removeUserById(long id) {
        if (id <= 0) {
            throw new IllegalArgumentException("DAO removing user by id: id must be more than 0");
        }

        try (Connection con = Util.connectMySQL(this.hostName, this.dbName, this.userName, this.password)) {
            String sql = String.format("DELETE FROM user WHERE id=%d", id);
            execSqlUpdate(sql, con);
        } catch (SQLException e) {
            System.out.printf("SQL exception when removing user: %s. Stack trace: %s%n",
                    e.getMessage(),
                    Arrays.toString(e.getStackTrace())
            );
        }
    }

    public List<User> getAllUsers() {
        try (Connection con = Util.connectMySQL(this.hostName, this.dbName, this.userName, this.password)) {
            String sql = "SELECT * FROM user";
            ResultSet rs = execSqlSelect(sql, con);
            List<User> users = new ArrayList<>();

            while (rs.next()) {
                long id = rs.getLong("id");
                String name = rs.getString("name");
                String lastName = rs.getString("last_name");
                byte age = rs.getByte("age");

                User user = new User(id, name, lastName, age);
                users.add(user);
            }

            return users;
        } catch (SQLException e) {
            System.out.printf("SQL exception when getting all users: %s. Stack trace: %s%n",
                    e.getMessage(),
                    Arrays.toString(e.getStackTrace())
            );

            return null;
        }
    }

    public void cleanUsersTable() {
        try (Connection con = Util.connectMySQL(this.hostName, this.dbName, this.userName, this.password)) {
            String sql = String.format("TRUNCATE TABLE %s;", USER_TABLE_NAME);
            execSqlUpdate(sql, con);
        } catch (SQLException e) {
            System.out.printf("SQL exception when creating table 'users': %s. Stack trace: %s%n",
                    e.getMessage(),
                    Arrays.toString(e.getStackTrace())
            );
        }
    }

    private boolean checkTableExistence(Connection con) throws SQLException {
        DatabaseMetaData metaData = con.getMetaData();
        ResultSet rs = metaData.getTables(null, null, USER_TABLE_NAME, new String[]{"TABLE"});

        return rs.next();
    }

    private void execSqlUpdate(String sql, Connection con) throws SQLException {
        Statement statement = con.createStatement();
        statement.executeUpdate(sql);
    }

    private ResultSet execSqlSelect(String sql, Connection con) throws SQLException {
        Statement statement = con.createStatement();
        return statement.executeQuery(sql);
    }
}
