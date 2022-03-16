package ro.ubb.map.gtsn.guitoysocialnetwork.repository.database;


import ro.ubb.map.gtsn.guitoysocialnetwork.constants.Constants;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.Request;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.Tuple;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.exceptions.RepoException;
import ro.ubb.map.gtsn.guitoysocialnetwork.repository.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RequestRepo implements Repository<Tuple<String, String>, Request> {
    Connection connection;
    private int pageSize;

    public RequestRepo(int pageSize, String url, String username, String password) throws SQLException {
        if(pageSize <= 0){
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
        String sql = "SELECT count(*) from friendship_requests";
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
        String sql = "SELECT count(*) from friendship_requests where second_user = ? and status = ?";
        long size = 0L;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            statement.setString(2, Constants.PENDINGREQUEST);
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
     * sets the new page size
     * @param pageSize int
     * @throws RepoException if pageSize < 1
     */
    public void setPageSize(int pageSize) {
        if(pageSize <= 0){
            throw new RepoException("Invalid page size");

        }
        this.pageSize = pageSize;
    }

    @Override
    public Request findOne(Tuple<String, String> fromTo) {
        Request request = null;
        String sql = "select * from friendship_requests where (first_user = ? and second_user = ?)";

        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setString(1, fromTo.getFirst());
            ps.setString(2, fromTo.getSecond());
            ResultSet resultSet = ps.executeQuery();

            if (resultSet.next()){
                String from = resultSet.getString("first_user");
                String to = resultSet.getString("second_user");
                String status = resultSet.getString("status");
                LocalDateTime dateTime = resultSet.getTimestamp("date").toLocalDateTime();
                request = new Request(from, to, status, dateTime);
            }
        }catch (SQLException err){
            throw new RepoException("SQL query error");
        }

        return request;
    }

    @Override
    public List<Request> findAll() {
        List<Request> requests = new ArrayList<Request>();
        String sql = "select * from friendship_requests";

        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ResultSet resultSet = ps.executeQuery();

            while (resultSet.next()){
                String from = resultSet.getString("first_user");
                String to = resultSet.getString("second_user");
                String status = resultSet.getString("status");
                LocalDateTime dateTime = resultSet.getTimestamp("date").toLocalDateTime();
                requests.add(new Request(from, to, status, dateTime));
            }
        }catch (SQLException err){
            throw new RepoException("SQL query error");
        }

        return requests;
    }

    public List<Request> findPage(int pageNumber) {
        if(pageNumber < 0){
            throw new RepoException("Invalid page number");
        }

        List<Request> requests = new ArrayList<Request>();
        String sql = "select * from friendship_requests offset ? limit ?";

        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setInt(1, pageNumber * pageSize);
            ps.setInt(2, pageSize);
            ps.setFetchSize(pageSize);

            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()){
                String from = resultSet.getString("first_user");
                String to = resultSet.getString("second_user");
                String status = resultSet.getString("status");
                LocalDateTime dateTime = resultSet.getTimestamp("date").toLocalDateTime();
                requests.add(new Request(from, to, status, dateTime));
            }
        }catch (SQLException err){
            throw new RepoException("SQL query error");
        }

        return requests;
    }

    /**
     * Adds a new request or updates an existent one if possible
     * @param request Request, friendship request to be added
     * @throws RepoException if the users are already friends or the request is already pending
     */
    @Override
    public Tuple<String, String> save(Request request) {
        String sql = "insert into friendship_requests (first_user, second_user, status, date) values (?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, request.getFrom());
            ps.setString(2, request.getTo());
            ps.setString(3, request.getStatus());
            ps.setTimestamp(4, Timestamp.valueOf(request.getLocalDateTime()));

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RepoException("Couldn't save request");
        }

        return request.getId();
    }

    /**
     * Removes the request sent by the first user to the second user
     * @param fromTo Tuple that contains the usernames of the users
     * @throws RepoException if an SQL error occurs when updating the database
     */
    @Override
    public void delete(Tuple<String, String> fromTo) {
        String sql = "delete from friendship_requests where (first_user = ? and second_user = ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, fromTo.getFirst());
            ps.setString(2, fromTo.getSecond());

            ps.executeUpdate();
        } catch (SQLException err){
            throw new RepoException("Couldn't delete request");
        }
    }

    /**
     * Updates the status and date of the request that has the same ID(tuple of usernames) with the new status
     * @param request the request to be updated
     * @throws RepoException if an SQL error occurs when updating the database
     */
    @Override
    public void update(Request request) {
        String sql = "update friendship_requests set status = ?, date = ? where first_user = ? and second_user = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, request.getStatus());
            ps.setTimestamp(2, Timestamp.valueOf(request.getLocalDateTime()));
            ps.setString(3, request.getFrom());
            ps.setString(4, request.getTo());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RepoException("Couldn't save request");
        }
    }

    @Override
    public boolean exists(Tuple<String, String> fromTo) {
        String sql = "select count(*) from friendship_requests where (first_user = ? and second_user = ?)";

        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setString(1, fromTo.getFirst());
            ps.setString(2, fromTo.getSecond());
            ResultSet resultSet = ps.executeQuery();

            if (resultSet.next()){
                if(resultSet.getInt("count") > 0){
                    return true;
                }
            }
        }catch (SQLException err){
            throw new RepoException("SQL query error");
        }

        return false;
    }

    /**
     * Returns a list with all the requests that can be accepted by the user
     * @param user String the username of the user
     * @return List that contains Tuples that contain in the first value the username of the user sending the request and in the second value the status of the request
     */
    public List<Request> getFriendRequests(String user) {
        List<Request> rez = new ArrayList<>();

        String sql = "select * from friendship_requests where second_user = ?";

        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setString(1, user);
            ResultSet resultSet = ps.executeQuery();

            while (resultSet.next()){
                String from = resultSet.getString("first_user");
                String to = resultSet.getString("second_user");
                String status = resultSet.getString("status");
                LocalDateTime dateTime = resultSet.getTimestamp("date").toLocalDateTime();
                rez.add(new Request(from, to, status, dateTime));
            }
        }catch (SQLException err){
            throw new RepoException("SQL query error");
        }

        return rez;
    }

    public List<Request> getFriendRequestsPage(String user, int pageNumber) {
        if(pageNumber < 0){
            throw new RepoException("Invalid page number");
        }

        List<Request> rez = new ArrayList<>();

        String sql = "select * from friendship_requests where second_user = ? offset ? limit ?";

        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setString(1, user);
            ps.setInt(2, pageNumber * pageSize);
            ps.setInt(3, pageSize);
            ps.setFetchSize(pageSize);
            ResultSet resultSet = ps.executeQuery();

            while (resultSet.next()){
                String from = resultSet.getString("first_user");
                String to = resultSet.getString("second_user");
                String status = resultSet.getString("status");
                LocalDateTime dateTime = resultSet.getTimestamp("date").toLocalDateTime();
                rez.add(new Request(from, to, status, dateTime));
            }
        }catch (SQLException err){
            throw new RepoException("SQL query error");
        }

        return rez;
    }

    public Set<Request> findAllNotifyable(String username){
        Set<Request> requests = new HashSet<>();
        String sql = "SELECT * from friendship_requests where (second_user = ? and notification_seen = ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1,username);
            statement.setBoolean(2,false);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String first_user = resultSet.getString("first_user");
                String second_user = resultSet.getString("second_user");
                LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();

                Request request = new Request(first_user,second_user,date);
                requests.add(request);

            }
        } catch (SQLException err) {
            throw new RepoException("Sql query error");
        }
        return requests;
    }

    public void setSeen(String username){
        String sql = "update friendship_requests set notification_seen = ? where second_user = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setBoolean(1,true);
            statement.setString(2, username);

            statement.executeUpdate();
        }
        catch (SQLException err){
            throw new RepoException("Couldn't update repo");
        }
    }
}
