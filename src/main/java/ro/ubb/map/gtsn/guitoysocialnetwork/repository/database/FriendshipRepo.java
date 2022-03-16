package ro.ubb.map.gtsn.guitoysocialnetwork.repository.database;

import ro.ubb.map.gtsn.guitoysocialnetwork.constants.Constants;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.Friendship;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.Pair;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.exceptions.RepoException;
import ro.ubb.map.gtsn.guitoysocialnetwork.repository.Repository;

import java.sql.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class FriendshipRepo implements Repository<Pair<String>, Friendship> {
    Connection connection;
    private int pageSize;

    public FriendshipRepo(int pageSize, String url, String username, String password) throws SQLException {
        if(pageSize < 1){
            throw new RepoException("Invalid page size");
        }
        this.pageSize = pageSize;
        connection = DriverManager.getConnection(url, username, password);
    }

    /**
     * Returns the page size
     * @return int
     */
    public int getPageSize() {
        return pageSize;
    }

    public long size(){
        String sql = "SELECT count(*) from friendships";
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

    public long userSize(String username){
        String sql = "SELECT count(*) from friendships where first_user = ? or second_user = ?";
        long size = 0L;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            statement.setString(2, username);
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
     * Sets a new page size
     * @param pageSize int
     * @throws RepoException if pageSize < 1
     */
    public void setPageSize(int pageSize) {
        if(pageSize < 1){
            throw new RepoException("Invalid page size");
        }

        this.pageSize = pageSize;
    }

    @Override
    public boolean exists(Pair<String> usernames){
        String sql = "SELECT count(*) from friendships where first_user = ? and second_user = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, usernames.getLeft());
            statement.setString(2, usernames.getRight());
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                if(resultSet.getInt("count") > 0){
                    return true;
                }
            }
        }
        catch (SQLException err){
            throw new RepoException("Sql query error");
        }
        return false;
    }

    @Override
    public Friendship findOne(Pair<String> usernames) {
        Friendship friendship = null;
        String sql = "SELECT * from friendships where first_user = ? and second_user = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)){

            statement.setString(1, usernames.getLeft());
            statement.setString(2, usernames.getRight());
            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()) {
                String first = resultSet.getString("first_user");
                String second = resultSet.getString("second_user");
                LocalDate date = LocalDate.parse(resultSet.getString("begin_date"), Constants.DATE_TIME_FORMATTER);
                friendship = new Friendship(first, second, date);
            }
        }
        catch (SQLException err){
            throw new RepoException("Sql query error");
        }

        return friendship;
    }

    @Override
    public Set<Friendship> findAll() {
        Set<Friendship> friendships = new HashSet<Friendship>();
        String sql = "SELECT * from friendships";

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()){

            while(resultSet.next()) {
                String first = resultSet.getString("first_user");
                String second = resultSet.getString("second_user");
                LocalDate date = LocalDate.parse(resultSet.getString("begin_date"), Constants.DATE_TIME_FORMATTER);
                Friendship friendship = new Friendship(first, second, date);
                friendships.add(friendship);
            }
        }
        catch (SQLException err){
            throw new RepoException("Sql query error");
        }

        return friendships;
    }

    /**
     * Returns an iterable with pageSize objects
     * @param pageNumber int, the set of friendships to be returned
     * @return iterable of friendships
     * @throws RepoException if the pageNumber < 0
     */
    public Set<Friendship> findPage(int pageNumber){
        if(pageNumber < 0){
            throw new RepoException("Invalid page number");
        }

        Set<Friendship> friendships = new HashSet<Friendship>();
        String sql = "SELECT * from friendships offset ? limit ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setInt(1, pageNumber * pageSize);
            statement.setInt(2, pageSize);
            statement.setFetchSize(pageSize);

            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()) {
                String first = resultSet.getString("first_user");
                String second = resultSet.getString("second_user");
                LocalDate date = LocalDate.parse(resultSet.getString("begin_date"), Constants.DATE_TIME_FORMATTER);
                Friendship friendship = new Friendship(first, second, date);
                friendships.add(friendship);
            }
        }
        catch (SQLException err){
            throw new RepoException("Sql query error");
        }

        return friendships;
    }

    @Override
    public Pair<String> save(Friendship entity) {
        String sql = "insert into friendships (first_user, second_user, begin_date) values (?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, entity.getId().getLeft());
            ps.setString(2, entity.getId().getRight());
            ps.setDate(3, Date.valueOf(entity.getFriendshipDate()));

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RepoException("Couldn't save in repo");
        }

        return entity.getId();
    }

    @Override
    public void delete(Pair<String> usernames) {
        String sql = "delete from friendships where first_user = ? and second_user = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setString(1, usernames.getLeft());
            statement.setString(2, usernames.getRight());
            statement.executeUpdate();
        }
        catch (SQLException err){
            throw new RepoException("Couldn't delete from repo");
        }
    }

    @Override
    public void update(Friendship entity) {
        String sql = "update friendships set begin_date = ? where first_user = ? and second_user = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setString(1, entity.getFriendshipDate().format(Constants.DATE_TIME_FORMATTER));
            statement.setString(2, entity.getId().getLeft());
            statement.setString(3, entity.getId().getRight());

            statement.executeUpdate();
        }
        catch (SQLException err){
            throw new RepoException("Couldn't update repo");
        }
    }

    public Iterable<String> getUserFriends(String username){
        String sql = "select * from friendships where first_user = ? or second_user = ?";
        Set<String> friends = new HashSet<>();

        try(PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setString(1, username);
            statement.setString(2, username);
            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()) {
                String first = resultSet.getString("first_user");
                String second = resultSet.getString("second_user");

                if(first.equals(username)){
                    friends.add(second);
                }
                else{
                    friends.add(first);
                }
            }
        }
        catch (SQLException err){
            throw new RepoException("Sql query error");
        }

        return friends;
    }

    public Iterable<String> getUserFriendsPage(String username, int pageNumber){
        if(pageNumber < 0){
            throw new RepoException("Invalid page number");
        }

        String sql = "select * from friendships where first_user = ? or second_user = ? offset ? limit ?";
        Set<String> friends = new HashSet<>();

        try(PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setString(1, username);
            statement.setString(2, username);
            statement.setInt(3, pageNumber * pageSize);
            statement.setInt(4, pageSize);
            statement.setFetchSize(pageSize);
            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()) {
                String first = resultSet.getString("first_user");
                String second = resultSet.getString("second_user");

                if(first.equals(username)){
                    friends.add(second);
                }
                else{
                    friends.add(first);
                }
            }
        }
        catch (SQLException err){
            throw new RepoException("Sql query error");
        }

        return friends;
    }


    public int getUserFriendCount(String username){
        String sql = "select count(*) from friendships where first_user = ? or second_user = ?";
        int result = 0;

        try(PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setString(1, username);
            statement.setString(2, username);
            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()) {
                result = resultSet.getInt("count");
            }
        }
        catch (SQLException err){
            throw new RepoException("Sql query error");
        }

        return result;
    }
}
