package ro.ubb.map.gtsn.guitoysocialnetwork.repository.database;

import ro.ubb.map.gtsn.guitoysocialnetwork.domain.Event;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.exceptions.RepoException;
import ro.ubb.map.gtsn.guitoysocialnetwork.repository.Repository;

import java.sql.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class EventRepo implements Repository<Long,Event> {
    Connection connection;
    int pageSize;

    public EventRepo(int pageSize, String url, String username, String password) throws SQLException {
        if(pageSize < 1){
            throw new RepoException("Invalid page size");
        }
        connection = DriverManager.getConnection(url, username, password);
        this.pageSize = pageSize;
    }

    public long size(){
        String sql = "SELECT count(*) from events";
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

    public long getExistingNumber(){
        String sql = "SELECT count(*) from events where date > now()";
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

    @Override
    public Event findOne(Long aLong) {
        Event event = null;
        String sql = "SELECT * from events where id = ?";
        String users = "SELECT * from event_participants where event_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, aLong);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String name = resultSet.getString("name");
                LocalDateTime dateTime = resultSet.getTimestamp("date").toLocalDateTime();
                String description = resultSet.getString("description");
                String location = resultSet.getString("location");
                event = new Event(name,dateTime,description,location);
                event.setId(aLong);
            }
        } catch (SQLException err) {
            throw new RepoException("Sql query error");
        }

        try (PreparedStatement statement = connection.prepareStatement(users)) {

            statement.setLong(1, aLong);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String username = resultSet.getString("username");
                event.addParticipant(username);
            }
        } catch (SQLException err) {
            throw new RepoException("Sql query error");
        }


        return event;
    }

    private void findParticipants(Event event){
        String users = "SELECT * from event_participants where event_id = ?";
        try (PreparedStatement statement2 = connection.prepareStatement(users)) {

            statement2.setLong(1, event.getId());
            ResultSet resultSet2 = statement2.executeQuery();

            while (resultSet2.next()) {
                String username = resultSet2.getString("username");
                event.addParticipant(username);
            }
        } catch (SQLException err) {
            throw new RepoException("Sql query error");
        }
    }

    @Override
    public Set<Event> findAll() {
        Set<Event> events= new HashSet<>();
        String sql = "SELECT * from events";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            extractEvents(events, statement);
        } catch (SQLException err) {
            throw new RepoException("Sql query error");
        }
        return events;
    }

    public Set<Event> findPage(int pageNumber) {
        if(pageNumber < 0){
            throw new RepoException("Invalid page number");
        }

        Set<Event> events= new HashSet<>();
        String sql = "SELECT * from events offset ? limit?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, pageNumber * pageSize);
            statement.setInt(2, pageSize);

            extractEvents(events, statement);
        } catch (SQLException err) {
            throw new RepoException("Sql query error");
        }
        return events;
    }

    private void extractEvents(Set<Event> events, PreparedStatement statement) throws SQLException {
        ResultSet resultSet = statement.executeQuery();

        while (resultSet.next()) {
            Long id = resultSet.getLong("id");
            String name = resultSet.getString("name");
            LocalDateTime dateTime = resultSet.getTimestamp("date").toLocalDateTime();
            String description = resultSet.getString("description");
            String location = resultSet.getString("location");

            Event event = new Event(name,dateTime,description,location);
            event.setId(id);
            findParticipants(event);

            events.add(event);
        }
    }

    @Override
    public Long save(Event entity) {
        long id = 0L;
        String sql = "insert into events (name, date, description, location) values (?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, entity.getName());
            ps.setTimestamp(2, Timestamp.valueOf(entity.getDateTime()));
            ps.setString(3, entity.getDescription());
            ps.setString(4, entity.getLocation());

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if(rs.next()){
                id = rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new RepoException("Couldn't save in repo");
        }
        return id;
    }

    @Override
    public void delete(Long aLong) {
        String sql = "delete from events where id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setLong(1, aLong);
            statement.executeUpdate();
        }
        catch (SQLException err){
            throw new RepoException("Couldn't delete from repo");
        }
    }

    @Override
    public void update(Event entity) {
        String sql = "update events set name = ?, date = ?, description = ?, location = ? where id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setString(1,entity.getName());
            statement.setTimestamp(2,Timestamp.valueOf(entity.getDateTime()));
            statement.setString(3,entity.getDescription());
            statement.setString(4, entity.getLocation());
            statement.setLong(5,entity.getId());

            statement.executeUpdate();
        }
        catch (SQLException err){
            throw new RepoException("Couldn't update repo");
        }
    }

    @Override
    public boolean exists(Long aLong) {
        String sql = "SELECT count(*) from events where id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, aLong);
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

    /**
     * returns a list of events that the user participates in
     * @param username String, the username of the user
     * @return Set<Event>
     */
    public Set<Event> findAllForUser(String username) {
        Set<Event> events= new HashSet<>();
        String sql = "SELECT * from event_participants where username = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Long event_id = resultSet.getLong("event_id");
                Event event = findOne(event_id);
                events.add(event);
            }
        } catch (SQLException err) {
            throw new RepoException("Sql query error");
        }
        return events;
    }

    public Set<Event> findAllExistingEvents() {
        Set<Event> events= new HashSet<>();
        String sql = "select * from events where date > now()";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            extractEvents(events, statement);
        } catch (SQLException err) {
            throw new RepoException("Sql query error");
        }
        return events;
    }

    public Set<Event> findExistingEventsPage(int pageNumber){
        if(pageNumber < 0){
            throw new RepoException("Invalid page number");
        }

        Set<Event> events= new HashSet<>();
        String sql = "select * from events where date > now() offset ? limit ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, pageNumber * pageSize);
            statement.setInt(2, pageSize);

            extractEvents(events, statement);
        } catch (SQLException err) {
            throw new RepoException("Sql query error");
        }
        return events;
    }

    public boolean isUserSubscribedTo(String username, Long eventID){
        String sql = "SELECT count(*) from event_participants where event_id = ? and username = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, eventID);
            statement.setString(2,username);
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

    public void saveParticipant(Long eventId, String username,Boolean toNotify){
        String sql = "insert into event_participants (event_id, username,notification_enabled) values (?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, eventId);
            ps.setString(2, username);
            ps.setBoolean(3,toNotify);

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RepoException("Couldn't save in repo");
        }
    }

    public Set<Event> findAllNotifiable(String username){
        Set<Event> events= new HashSet<>();
        String sql = "SELECT * from (events inner join event_participants on events.id = event_participants.event_id) where (username = ? and notification_enabled = ? and notification_seen = ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1,username);
            statement.setBoolean(2,true);
            statement.setBoolean(3,false);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String name = resultSet.getString("name");
                LocalDateTime dateTime = resultSet.getTimestamp("date").toLocalDateTime();
                String description = resultSet.getString("description");
                String location = resultSet.getString("location");

                LocalDateTime now = LocalDateTime.now();

                if(dateTime.isAfter(now) && Duration.between(dateTime,now).compareTo(Duration.ofDays(1))<0){
                    Event event = new Event(name,dateTime,description,location);
                    event.setId(id);
                    events.add(event);
                }

            }
        } catch (SQLException err) {
            throw new RepoException("Sql query error");
        }
        return events;
    }

    public void setSeen(String username){
        String sql = "update event_participants set notification_seen = ? where username = ?";

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
