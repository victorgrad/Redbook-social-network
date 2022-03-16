package ro.ubb.map.gtsn.guitoysocialnetwork.repository.database;

import ro.ubb.map.gtsn.guitoysocialnetwork.domain.Message;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.exceptions.RepoException;
import ro.ubb.map.gtsn.guitoysocialnetwork.repository.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public class MessageRepo implements Repository<Long, Message> {
    Connection connection;
    private int pageSize;

    public MessageRepo(int pageSize, String url, String username, String password) throws SQLException {
        if(pageSize < 1){
            throw new RepoException("Invalid page size");
        }
        this.pageSize = pageSize;
        connection = DriverManager.getConnection(url, username, password);
    }

    /**
     * Returns the page size
     * @return int, the page size
     */
    public int getPageSize() {
        return pageSize;
    }

    public long size(){
        String sql = "SELECT count(*) from messages";
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
        String sql1 = "select count(*) from (select distinct messages.id from (messages inner join message_recipients on messages.id = message_recipients.message) where ((messages.from = ? OR message_recipients.recipient = ?) and id not in (select distinct original_message from messages where original_message is not null))) nr";
        long size = 0L;
        try (PreparedStatement statement = connection.prepareStatement(sql1)) {
            statement.setString(1, username);
            statement.setString(2, username);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                size += resultSet.getInt(1);
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
    public Message findOne(Long aLong) {
        Message message = null;
        String sql = "SELECT * from messages where id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)){

            statement.setLong(1, aLong);
            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()) {
                Long id = resultSet.getLong("id");
                LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();
                String from = resultSet.getString("from");
                String text = resultSet.getString("text");
                Long originalId = resultSet.getLong("original_message");
                message = new Message(id,from,text,date,originalId);

                findTo(id,message);
            }
        }
        catch (SQLException err){
            throw new RepoException("Sql query error");
        }

        return message;
    }

    @Override
    public Set<Message> findAll() {
        Set<Message> messages;
        String sql = "SELECT * from messages";

        try (PreparedStatement statement = connection.prepareStatement(sql)){
            ResultSet resultSet = statement.executeQuery();

            messages = findFiltered(resultSet);
        }
        catch (SQLException err){
            throw new RepoException("Sql query error");
        }

        return messages;
    }

    public Set<Message> findPage(int pageNumber){
        if(pageNumber < 0){
            throw new RepoException("Invalid page number");
        }

        Set<Message> messages;
        String sql = "SELECT * from messages order by messages.id desc offset ? limit ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setInt(1, pageNumber * pageSize);
            statement.setInt(2, pageSize);
            statement.setFetchSize(pageSize);
            ResultSet resultSet = statement.executeQuery();

            messages = findFiltered(resultSet);
        }
        catch (SQLException err){
            throw new RepoException("Sql query error");
        }

        return messages;
    }

    @Override
    public Long save(Message entity) {
        long last_inserted_id = -1L;

        String sql1 = "insert into messages (date, \"from\", text) values (?, ?, ?)";
        String sql2 = "insert into messages (date, \"from\", text, original_message) values (?, ?, ?, ?)";
        String sql;

        if(entity.getReplyToMessageId().equals(0L)){
            sql = sql1;
        }
        else{
            sql = sql2;
        }

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setTimestamp(1,Timestamp.valueOf(entity.getMessageDateTime()));
            ps.setString(2,entity.getFrom());
            ps.setString(3,entity.getMessage());

            if(!entity.getReplyToMessageId().equals(0L)){
                ps.setLong(4, entity.getReplyToMessageId());
            }

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if(rs.next())
            {
                last_inserted_id = rs.getInt(1);
            }

        } catch (SQLException e) {
            throw new RepoException("Couldn't save in repo");
        }


        String sql3 = "insert into message_recipients (message,recipient) values (?, ?)";
        try(PreparedStatement ps2 = connection.prepareStatement(sql3)){
            ps2.setLong(1, (long) last_inserted_id);
            for(var username:entity.getTo()){
                ps2.setString(2, username);
                ps2.executeUpdate();
            }
        }catch (SQLException e){
            throw new RepoException("Couldn't save in repo");
        }

        return last_inserted_id;
    }

    @Override
    public void delete(Long aLong) {
        String sql = "delete from messages where id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setLong(1, aLong);
            statement.executeUpdate();
        }
        catch (SQLException err){
            throw new RepoException("Couldn't delete from repo");
        }
    }

    @Override
    public void update(Message entity) {
        //todo: -add feature to edit a message
    }

    @Override
    public boolean exists(Long aLong) {
        String sql = "SELECT count(*) from messages where id = ?";

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
            throw new RepoException("Sql query error");
        }
        return false;
    }


    /**
     * Returns the messages between 2 people(either user1 sent a message to user2 or user2 sent a message to user 1)
     * @param username1 String, the username of the first person
     * @param username2 String, the username of the second person
     * @return Set<Message>
     */
    public Set<Message> findConversation(String username1,String username2){
        Set<Message> messages;
        String sql = "SELECT * from (messages inner join message_recipients on messages.id = message_recipients.message) where (messages.from = ? AND message_recipients.recipient = ?) OR (messages.from = ? AND message_recipients.recipient = ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setString(1,username1);
            statement.setString(2,username2);
            statement.setString(3,username2);
            statement.setString(4,username1);
            ResultSet resultSet = statement.executeQuery();

            messages = findFiltered(resultSet);
        }
        catch (SQLException err){
            throw new RepoException("Sql query error");
        }

        return messages;
    }

    public Set<Message> findConversationPage(String username1,String username2, int pageNumber){
        if(pageNumber < 0){
            throw new RepoException("Invalid page number");
        }

        Set<Message> messages;
        String sql = "SELECT * from (messages inner join message_recipients on messages.id = message_recipients.message) where (messages.from = ? AND message_recipients.recipient = ?) OR (messages.from = ? AND message_recipients.recipient = ?)  order by messages.id desc offset ? limit ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setString(1,username1);
            statement.setString(2,username2);
            statement.setString(3,username2);
            statement.setString(4,username1);
            statement.setInt(5, pageNumber * pageSize);
            statement.setInt(6, pageSize);
            statement.setFetchSize(pageSize);
            ResultSet resultSet = statement.executeQuery();

            messages = findFiltered(resultSet);
        }
        catch (SQLException err){
            throw new RepoException("Sql query error");
        }

        return messages;
    }

    /**
     * Cleanup function used in findAll and findConversation
     * @param resultSet ResultSet, the messages that were found
     * @return Set<Message>
     */
    private Set<Message> findFiltered(ResultSet resultSet) throws SQLException{
        Set<Message> messages = new TreeSet<>(new Comparator<Message>() {
            @Override
            public int compare(Message o1, Message o2) {
                return (int)(o2.getId() - o1.getId());
            }

            @Override
            public boolean equals(Object obj) {
                return false;
            }
        });
        while(resultSet.next()) {
            Long id = resultSet.getLong("id");
            LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();
            String from = resultSet.getString("from");
            String text = resultSet.getString("text");
            Long originalId = resultSet.getLong("original_message");
            Message message = new Message(id,from,text,date,originalId);

            findTo(id,message);

            messages.add(message);
        }
        return messages;
    }

    /**
     * Cleanup function used in findOne and findFilter that adds user id's's to the set of to:
     * @param id Long, id of the message
     * @param message Message, the Message where we want the id's added
     */
    private void findTo(Long id, Message message){
        String sql2 = "SELECT * from message_recipients where message = ?";
        try(PreparedStatement statement2 = connection.prepareStatement(sql2)){
            statement2.setLong(1, id);
            ResultSet resultSet2 =statement2.executeQuery();
            while(resultSet2.next()){
                String idRecipient = resultSet2.getString("recipient");
                message.addRecipient(idRecipient);
            }
        }
        catch(SQLException ignored){}
    }

    public Set<Message> findUserMessages(String username){
        Set<Message> messages;
        String sql = "SELECT * from (messages inner join message_recipients on messages.id = message_recipients.message) where messages.from = ? OR message_recipients.recipient = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setString(1,username);
            statement.setString(2,username);
            ResultSet resultSet = statement.executeQuery();

            messages = findFiltered(resultSet);
        }
        catch (SQLException err){
            throw new RepoException("Sql query error");
        }

        return messages;
    }

    public Set<Message> findUserMessagesPage(String username, int pageNumber){
        if(pageNumber < 0){
            throw new RepoException("Invalid page number");
        }

        Set<Message> messages;
        String sql = "SELECT * from (messages inner join message_recipients on messages.id = message_recipients.message) where messages.from = ? OR message_recipients.recipient = ? order by messages.id desc offset ? limit ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setString(1,username);
            statement.setString(2,username);
            statement.setInt(3, pageNumber * pageSize);
            statement.setInt(4, pageSize);
            statement.setFetchSize(pageSize);
            ResultSet resultSet = statement.executeQuery();

            messages = findFiltered(resultSet);
        }
        catch (SQLException err){
            throw new RepoException("Sql query error");
        }

        return messages;
    }

    /**
     * Returns a list with all the messages that are at the end of a reply chain
     * @param username, String the username of the user that sent or receiver the messages
     * @param pageNumber, int the page number
     * @return Set of messages
     * @throws RepoException if pageNumber < 0
     */
    public Set<Message> getLastUserMessagesFromChainPage(String username, int pageNumber){
        if(pageNumber < 0){
            throw new RepoException("Invalid page number");
        }

        Set<Message> messages;
        String sql = "select distinct messages.id, messages.date, messages.from, messages.text, messages.original_message from (messages inner join message_recipients on messages.id = message_recipients.message) where ((messages.from = ? OR message_recipients.recipient = ?) and id not in (select distinct original_message from messages where original_message is not null)) order by messages.id desc offset ? limit ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setString(1,username);
            statement.setString(2,username);
            statement.setInt(3, pageNumber * pageSize);
            statement.setInt(4, pageSize);
            statement.setFetchSize(pageSize);
            ResultSet resultSet = statement.executeQuery();

            messages = findFiltered(resultSet);
        }
        catch (SQLException err){
            throw new RepoException("Sql query error");
        }

        return messages;
    }

    public Set<Message> findAllNotifyable(String username){
        Set<Message> messages = new HashSet<>();
        String sql = "SELECT * from message_recipients where (recipient = ? and notification_seen = ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1,username);
            statement.setBoolean(2,false);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Long messageId = resultSet.getLong("message");

                Message message = findOne(messageId);
                messages.add(message);

            }
        } catch (SQLException err) {
            throw new RepoException("Sql query error");
        }
        return messages;
    }

    public void setSeen(String username){
        String sql = "update message_recipients set notification_seen = ? where recipient = ?";

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


