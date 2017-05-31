package codeu.chat.util.mysql;

import codeu.chat.common.Conversation;
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
import java.text.SimpleDateFormat;


public class MySQLConnection{
    private static final String DATABASE_DRIVER = "com.mysql.jdbc.Driver";
    // Is my hosting information correct here?
    // Upon further testing I believe it is. Will likely need to be changed depending
    // 	on the computer that is running it.
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
//        Connection connect = getConnection();

        java.util.Date date = new java.util.Date();
        java.sql.Timestamp timestamp = new java.sql.Timestamp(date.getTime());

        // PreparedStatements can use variables and are more efficient
        PreparedStatement preparedStatement = connection
                .prepareStatement("insert into  CodeUChat.Conversations values (?, ?, ?, ?)");
        // Parameters start with 1
        preparedStatement.setString(1, codeu.chat.common.Uuids.toString(id));
        preparedStatement.setString(2, codeu.chat.common.Uuids.toString(owner));
        preparedStatement.setString(3, title);
        preparedStatement.setTimestamp(4, timestamp);
        preparedStatement.executeUpdate();
    }

    public void writeUsers(codeu.chat.common.Uuid id, String name) throws SQLException {

        java.util.Date date = new java.util.Date();
        java.sql.Timestamp timestamp = new java.sql.Timestamp(date.getTime());

        // PreparedStatements can use variables and are more efficient
        PreparedStatement preparedStatement = connection
                .prepareStatement("insert into  CodeUChat.Users values (?, ?, ?)");
        // Parameters start with 1
        preparedStatement.setString(1, codeu.chat.common.Uuids.toString(id));
        preparedStatement.setString(2, name);
        preparedStatement.setTimestamp(3, timestamp);
        preparedStatement.executeUpdate();

    }


    public void writeMessages(codeu.chat.common.Uuid id, codeu.chat.common.Uuid owner, String body) throws SQLException
    {
//        Connection connect = getConnection();

        // PreparedStatements can use variables and are more efficient
        PreparedStatement preparedStatement = connection
                .prepareStatement("insert into  CodeUChat.Messages values (?, ?, ?, ?)");

        java.util.Date date = new java.util.Date();
        java.sql.Timestamp timestamp = new java.sql.Timestamp(date.getTime());

        // Parameters start with 1
        preparedStatement.setString(1, codeu.chat.common.Uuids.toString(id));
        preparedStatement.setTimestamp(2, timestamp);
        preparedStatement.setString(3, codeu.chat.common.Uuids.toString(owner));
        preparedStatement.setString(4, body);
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
            java.sql.Timestamp timestamp = ownerResultSet.getTimestamp("Time");
            codeu.chat.common.Time myTime = new codeu.chat.common.Time(timestamp.getTime());

            User myUser = new User(myID, myName, myTime);
            myUsers.add(myUser);
        }

        return myUsers;

    }


    public Collection<Conversation> readConversationsFromOwner(Uuid owner) throws SQLException {

        Collection<Conversation> myConvos = new ArrayList<>();

        PreparedStatement statement = connection.prepareStatement("select * from CodeUChat.Conversations where Owner = ?");

        statement.setString(1, Uuid.toString(owner));

        ResultSet myResultSet = statement
                .executeQuery();

        while (myResultSet.next())
        {
            codeu.chat.common.Uuid myID = codeu.chat.common.Uuids.fromString(myResultSet.getString("id"));
            codeu.chat.common.Uuid myOwner = codeu.chat.common.Uuids.fromString(myResultSet.getString("Owner"));

//            System.out.println(myID.toString());

            String myTitle = myResultSet.getString("Title");
            java.sql.Timestamp timestamp = myResultSet.getTimestamp("Time");
            codeu.chat.common.Time myTime = new codeu.chat.common.Time(timestamp.getTime());


            codeu.chat.common.Conversation myConvo = new Conversation(myID, myOwner, myTime, myTitle);
            myConvos.add(myConvo);
        }

        return myConvos;
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

            String myTitle = myResultSet.getString("Title");
            java.sql.Timestamp timestamp = myResultSet.getTimestamp("Time");
            codeu.chat.common.Time myTime = new codeu.chat.common.Time(timestamp.getTime());


            codeu.chat.common.Conversation myConvo = new Conversation(myID, myOwner, myTime, myTitle);
            myConvos.add(myConvo);
        }

        return myConvos;
    }

    //read all the messages from owner
    public String[] readMessages(Uuid owner) throws SQLException
    {
//        Connection connect = getConnection();

        // Statements allow to issue SQL queries to the database
        PreparedStatement statement = connection.prepareStatement("select Body from CodeUChat.Messages where Owner = ? ");
        // Result set get the result of the SQL query

        statement.setString(1, Uuid.toString(owner));

        ResultSet ownerResultSet = statement
                .executeQuery();
        //writeResultSet(resultSet);

        String[] arr = null;
        while (ownerResultSet.next()) {
            String em = ownerResultSet.getString("Body");
            arr = em.split("\n");
            for (int i = 0; i < arr.length; i++) {
                System.out.println(arr[i]);
            }
        }

        return arr;

    }

//    public void writeResultSet()
//    {
//        //writing owners into the current session so they can appear in the gui
//    }



}