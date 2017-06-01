package codeu.chat.util.mysql;


import codeu.chat.util.Uuid;

import java.sql.*;
import java.util.Properties;
import codeu.chat.common.Conversation;
import codeu.chat.common.Message;
import codeu.chat.common.User;
import codeu.chat.util.Uuid;

import java.sql.*;
import java.util.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.Vector;
import java.util.Date;
import java.text.SimpleDateFormat;


public class MySQLConnection{
    private static final String DATABASE_DRIVER = "com.mysql.jdbc.Driver";
    private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/CodeUChat?autoReconnect=true&&useSSL=false";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "king";

    // Create connection object
    private Connection connection;
    // Creat properties object
    private Properties properties;


    public MySQLConnection()
    {
        this.connection = getConnection();
    }

    // Create the actual properties
    private Properties getProperties(){
        if (properties == null){
            properties = new Properties();
            properties.setProperty("user", USERNAME);
            properties.setProperty("password", PASSWORD);
        }
        return properties;
    }

    // Connect to the database
    private Connection connect(){
        if(connection == null){
            try{
                Class.forName(DATABASE_DRIVER);
                connection = DriverManager.getConnection(DATABASE_URL, getProperties());
            } catch (ClassNotFoundException | SQLException e){
                System.out.println("There was an error connecting to the database.");
                e.printStackTrace();
            }
        }
        return connection;
    }

    public Connection getConnection(){
        return connect();
    }

    // Closing the database connection
    private void disconnect(){
        if(connection != null){
            try{
                connection.close();
                connection = null;
            } catch (SQLException e){
                e.printStackTrace();
            }
        }
    }

    public void disconnectConnection(){
        disconnect();
    }



    public void writeConversations(codeu.chat.common.Uuid id, codeu.chat.common.Uuid owner, String title) throws SQLException {

        java.util.Date date = new java.util.Date();
        java.sql.Timestamp timestamp = new java.sql.Timestamp(date.getTime());

        // PreparedStatements can use variables and are more efficient
        PreparedStatement preparedStatement = connection
                .prepareStatement("insert into  CodeUChat.Conversations (id, Owner, Title, Time) values (?, ?, ?, ?)");
        // Parameters start with 1
        preparedStatement.setString(1, codeu.chat.common.Uuids.toString(id));
        preparedStatement.setString(2, codeu.chat.common.Uuids.toString(owner));
        preparedStatement.setString(3, title);
        preparedStatement.setTimestamp(4, timestamp);
        preparedStatement.executeUpdate();
    }

    public void writeUsers(codeu.chat.common.Uuid id, String name, String password) throws SQLException {

        java.util.Date date = new java.util.Date();
        java.sql.Timestamp timestamp = new java.sql.Timestamp(date.getTime());

        // PreparedStatements can use variables and are more efficient
        PreparedStatement preparedStatement = connection
                .prepareStatement("insert into  CodeUChat.Users values (?, ?, ?, ?)");
        // Parameters start with 1
        preparedStatement.setString(1, codeu.chat.common.Uuids.toString(id));
        preparedStatement.setString(2, name);
        preparedStatement.setString(3, password);
        preparedStatement.setTimestamp(4, timestamp);
        preparedStatement.executeUpdate();

    }


    public void writeMessages(codeu.chat.common.Uuid id, codeu.chat.common.Uuid owner, String body) throws SQLException
    {

        // PreparedStatements can use variables and are more efficient
        PreparedStatement preparedStatement = connection
                .prepareStatement("insert into  CodeUChat.Messages (id, Time, Owner, Body) values (?, ?, ?, ?)");

        java.util.Date date = new java.util.Date();
        java.sql.Timestamp timestamp = new java.sql.Timestamp(date.getTime());

        // Parameters start with 1
        preparedStatement.setString(1, codeu.chat.common.Uuids.toString(id));
//        preparedStatement.setString(2, codeu.chat.common.Uuids.toString(next));
//        preparedStatement.setString(3, codeu.chat.common.Uuids.toString(prev));
        preparedStatement.setTimestamp(2, timestamp);
        preparedStatement.setString(3, codeu.chat.common.Uuids.toString(owner));
        preparedStatement.setString(4, body);
        preparedStatement.executeUpdate();
    }


    public void updateMessages(codeu.chat.common.Uuid next, codeu.chat.common.Uuid prev, codeu.chat.common.Uuid id) throws SQLException
    {
        PreparedStatement preparedStatement = connection
                .prepareStatement("update CodeUChat.Messages set Next = ? , Prev = ? where id = ?");
        // Parameters start with 1
        preparedStatement.setString(1, codeu.chat.common.Uuids.toString(next));
        preparedStatement.setString(2, codeu.chat.common.Uuids.toString(prev));
        preparedStatement.setString(3, codeu.chat.common.Uuids.toString(id));
        preparedStatement.executeUpdate();
    }


    public void updateConversations(codeu.chat.common.Uuid last, codeu.chat.common.Uuid id) throws SQLException
    {
        PreparedStatement preparedStatement = connection
                .prepareStatement("update CodeUChat.Conversations set Last = ? where id = ?");
        // Parameters start with 1
        preparedStatement.setString(1, codeu.chat.common.Uuids.toString(last));
        preparedStatement.setString(2, codeu.chat.common.Uuids.toString(id));
        preparedStatement.executeUpdate();
    }

    public void setFirstConversations(codeu.chat.common.Uuid first, codeu.chat.common.Uuid id) throws SQLException
    {
        PreparedStatement preparedStatement = connection
                .prepareStatement("update CodeUChat.Conversations set First = ? where id = ?");
        // Parameters start with 1
        preparedStatement.setString(1, codeu.chat.common.Uuids.toString(first));
        preparedStatement.setString(2, codeu.chat.common.Uuids.toString(id));
        preparedStatement.executeUpdate();
    }

    public Collection<User> readUsers() throws SQLException {

        Collection<User> myUsers = new ArrayList<>();

        // Statements allow to issue SQL queries to the database
        Statement statement = connection.createStatement();
        // Result set get the result of the SQL query
        ResultSet ownerResultSet = statement
                .executeQuery("select * from CodeUChat.Users");

        while (ownerResultSet.next())
        {
            codeu.chat.common.Uuid myID = codeu.chat.common.Uuids.fromString(ownerResultSet.getString("id"));

            String myName = ownerResultSet.getString("Name");
            String myPass = ownerResultSet.getString("Password");
            java.sql.Timestamp timestamp = ownerResultSet.getTimestamp("Time");
            codeu.chat.common.Time myTime = new codeu.chat.common.Time(timestamp.getTime());

            User myUser = new User(myID, myName, myPass, myTime);
            myUsers.add(myUser);
        }

        return myUsers;

    }


    public Collection<Conversation> readConversations() throws SQLException
    {
        Collection<Conversation> myConvos = new ArrayList<>();

        // Statements allow to issue SQL queries to the database
        Statement statement = connection.createStatement();
        // Result set get the result of the SQL query
        ResultSet myResultSet = statement
                .executeQuery("select * from CodeUChat.Conversations");

        while (myResultSet.next())
        {
            codeu.chat.common.Uuid myID = codeu.chat.common.Uuids.fromString(myResultSet.getString("id"));
            codeu.chat.common.Uuid myOwner = codeu.chat.common.Uuids.fromString(myResultSet.getString("Owner"));
            codeu.chat.common.Uuid myFirst = codeu.chat.common.Uuids.fromString(myResultSet.getString("First"));
            codeu.chat.common.Uuid myLast = codeu.chat.common.Uuids.fromString(myResultSet.getString("Last"));

            String myTitle = myResultSet.getString("Title");
            java.sql.Timestamp timestamp = myResultSet.getTimestamp("Time");
            codeu.chat.common.Time myTime = new codeu.chat.common.Time(timestamp.getTime());


            codeu.chat.common.Conversation myConvo = new Conversation(myID, myOwner, myFirst, myLast, myTime, myTitle);
            myConvos.add(myConvo);
        }

        return myConvos;
    }


    public Collection<Message> readMessages() throws SQLException
    {
        Collection<Message> myMessages = new ArrayList<>();

        // Statements allow to issue SQL queries to the database
        Statement statement = connection.createStatement();
        // Result set get the result of the SQL query
        ResultSet myResultSet = statement
                .executeQuery("select * from CodeUChat.Messages");

        while (myResultSet.next())
        {
            codeu.chat.common.Uuid myID = codeu.chat.common.Uuids.fromString(myResultSet.getString("id"));
            codeu.chat.common.Uuid myOwner = codeu.chat.common.Uuids.fromString(myResultSet.getString("Owner"));
            codeu.chat.common.Uuid myNext = codeu.chat.common.Uuids.fromString(myResultSet.getString("Next"));
            codeu.chat.common.Uuid myPrev = codeu.chat.common.Uuids.fromString(myResultSet.getString("Prev"));

            String myBody = myResultSet.getString("Body");
            java.sql.Timestamp timestamp = myResultSet.getTimestamp("Time");
            codeu.chat.common.Time myTime = new codeu.chat.common.Time(timestamp.getTime());


            codeu.chat.common.Message myMessage = new Message(myID, myNext, myPrev, myTime, myOwner, myBody);
            myMessages.add(myMessage);
        }

        return myMessages;

    }
}