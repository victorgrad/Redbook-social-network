package ro.ubb.map.gtsn.guitoysocialnetwork.repository.database;

import ro.ubb.map.gtsn.guitoysocialnetwork.domain.User;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.exceptions.RepoException;
import ro.ubb.map.gtsn.guitoysocialnetwork.repository.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserRepo implements Repository<String, User> {
    private final Connection connection;
    private int pageSize;

    public UserRepo(int pageSize, String url, String username, String password) throws SQLException {
        if(pageSize <= 0){
            throw new RepoException("Invalid page size");
        }

        connection = DriverManager.getConnection(url, username, password);
        this.pageSize = pageSize;
    }

    /**
     * Returns the page size
     * @return int, the page size
     */
    public int getPageSize() {
        return pageSize;
    }

    public long size(){
        String sql = "SELECT count(*) from users";
        long size = 0L;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                size = resultSet.getInt("count");
            }
        }
        catch (SQLException err){
            throw new RepoException("SQL query error");
        }
        return size;
    }

    /**
     * Sets the page size
     * @param pageSize int, the new page size
     * @throws RepoException if pageSize is smaller than 1
     */
    public void setPageSize(int pageSize) {
        if(pageSize <= 0){
            throw new RepoException("Invalid page size");
        }

        this.pageSize = pageSize;
    }

    @Override
    public boolean exists(String username){
        String sql = "SELECT count(*) from users where username = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                if(resultSet.getInt("count") > 0){
                    return true;
                }
            }
        }
        catch (SQLException err){
            throw new RepoException("SQL query error");
        }
        return false;
    }

    @Override
    public User findOne(String username) {
        User user = null;
        String sql = "SELECT * from users where username = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()) {
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                user = new User(username, firstName, lastName);
            }
        }
        catch (SQLException err){
            throw new RepoException("SQL query error");
        }

        return user;
    }

    /**
     * returns the password hash of a user
     * @param username String the username of the user
     * @return String, if the user exists, null otherwise
     */
    public String getPasswordHash(String username) {
        String pass = null;
        String sql = "SELECT pass from users where username = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()) {
                pass = resultSet.getString("pass");
            }
        }
        catch (SQLException err){
            throw new RepoException("SQL query error");
        }

        return pass;
    }

    @Override
    public Iterable<User> findAll() {
        Set<User> users = new HashSet<>();

        try(PreparedStatement statement = connection.prepareStatement("SELECT * from users");
            ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String username = resultSet.getString("username");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");

                User user = new User(username, firstName, lastName);
                users.add(user);
            }
        }
        catch (SQLException err){
            throw new RepoException("SQL query error");
        }
        return users;
    }

    /**
     * Returns an iterable with pageSize users
     * @param pageNumber int, the set of users to be returned
     * @return iterable of users
     * @throws RepoException if the pageNumber < 0
     */
    public Iterable<User> findPage(int pageNumber){
        if(pageNumber < 0){
            throw new RepoException("Invalid page number");
        }

        Set<User> users = new HashSet<>();

        try(PreparedStatement statement = connection.prepareStatement("SELECT * from users offset ? limit ?")) {

            statement.setInt(1, pageNumber * pageSize);
            statement.setInt(2, pageSize);
            statement.setFetchSize(pageSize);

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String username = resultSet.getString("username");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");

                User user = new User(username, firstName, lastName);
                users.add(user);
            }
        }
        catch (SQLException err){
            throw new RepoException("SQL query error");
        }
        return users;
    }

    /**
     * Returns the page that contains a filtered list of users that contain the starting string in their username, firstname or lastname
     * @param starting, string
     * @param pageNumber, int
     * @return list of users
     */
    public List<User> findUserPageByName(String starting, int pageNumber){
        if(pageNumber < 0){
            throw new RepoException("Invalid page number");
        }

        List<User> users = new ArrayList<>();

        try(PreparedStatement statement = connection.prepareStatement("SELECT * from users where (username like ? or first_name like ? or last_name like ?) offset ? limit ?")) {

            statement.setString(1, starting + "%");
            statement.setString(2, starting + "%");
            statement.setString(3, starting + "%");
            statement.setInt(4, pageNumber * pageSize);
            statement.setInt(5, pageSize);
            statement.setFetchSize(pageSize);

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String username = resultSet.getString("username");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");

                User user = new User(username, firstName, lastName);
                users.add(user);
            }
        }
        catch (SQLException err){
            throw new RepoException("SQL query error");
        }
        return users;
    }

    @Override
    public String save(User entity) {
        String sql = "insert into users (username, first_name, last_name, pass) values (?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, entity.getId());
            ps.setString(2, entity.getFirstName());
            ps.setString(3, entity.getLastName());
            ps.setString(4, entity.getPasswordHash());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RepoException("Couldn't insert in repo");
        }

        return entity.getId();
    }

    @Override
    public void delete(String username) {
        String sql = "delete from users where username = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setString(1, username);
            statement.executeUpdate();
        }
        catch (SQLException err){
            throw new RepoException("Couldn't delete from repo");
        }
    }

    @Override
    public void update(User entity) {
        String sql = "update users set first_name = ?, last_name = ? where username = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setString(1, entity.getFirstName());
            statement.setString(2, entity.getLastName());
            statement.setString(3, entity.getId());

            statement.executeUpdate();
        }
        catch (SQLException err){
            throw new RepoException("Couldn't update repo");
        }
    }
}
