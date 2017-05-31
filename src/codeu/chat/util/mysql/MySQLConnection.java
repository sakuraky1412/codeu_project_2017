package codeu.chat.util.mysql;

import codeu.chat.util.Uuid;

import java.sql.*;
import java.util.Properties;
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



    //WILL NEED TO MAKE SURE STRING AND VARCHAR ARE COMPATIBLE!!

    public void writeConversations(Uuid id, Uuid owner, String title) throws SQLException {
//        Connection connect = getConnection();

        java.util.Date date = new java.util.Date();
        java.sql.Timestamp timestamp = new java.sql.Timestamp(date.getTime());

        // PreparedStatements can use variables and are more efficient
        PreparedStatement preparedStatement = connection
                .prepareStatement("insert into  CodeUChat.Conversations values (?, ?, ?, ?)");
        // Parameters start with 1
        preparedStatement.setString(1, Uuid.toString(id));
        preparedStatement.setString(2, Uuid.toString(owner));
        preparedStatement.setString(3, title);
        preparedStatement.setTimestamp(4, timestamp);
        preparedStatement.executeUpdate();
    }

    public void writeUsers(Uuid id, String name) throws SQLException {
//        Connection connect = getConnection();

        // PreparedStatements can use variables and are more efficient
        PreparedStatement preparedStatement = connection
                .prepareStatement("insert into  CodeUChat.Users values (?, ?)");
        // Parameters start with 1
        preparedStatement.setString(1, Uuid.toString(id));
        preparedStatement.setString(2, name);
        preparedStatement.executeUpdate();
    }


    public void writeMessages(Uuid id, Uuid owner, String body) throws SQLException
    {
//        Connection connect = getConnection();

        // PreparedStatements can use variables and are more efficient
        PreparedStatement preparedStatement = connection
                .prepareStatement("insert into  CodeUChat.Messages values (?, ?, ?, ?)");

        java.util.Date date = new java.util.Date();
        java.sql.Timestamp timestamp = new java.sql.Timestamp(date.getTime());

        // Parameters start with 1
        preparedStatement.setString(1, Uuid.toString(id));
        preparedStatement.setTimestamp(2, timestamp);
        preparedStatement.setString(3, Uuid.toString(owner));
        preparedStatement.setString(4, body);
        preparedStatement.executeUpdate();
    }


    public String[] readUsers() throws SQLException {

        // Statements allow to issue SQL queries to the database
        Statement statement = connection.createStatement();
        // Result set get the result of the SQL query
        ResultSet ownerResultSet = statement
                .executeQuery("select distinct Name from CodeUChat.Users");

        String[] arr = null;
        while (ownerResultSet.next()) {
            String em = ownerResultSet.getString("Name");
            arr = em.split("\n");
            for (int i = 0; i < arr.length; i++) {
                System.out.println(arr[i]);
            }
        }

        return arr;

    }


    public String[] readConversations(Uuid owner) throws SQLException {

//        Connection connect = getConnection();

//        // Statements allow to issue SQL queries to the database
//        Statement statement = connection.createStatement();
//        // Result set get the result of the SQL query
        PreparedStatement statement = connection.prepareStatement("select * Title from CodeUChat.Conversations where Owner = ?");

        statement.setString(1, Uuid.toString(owner));

        ResultSet ownerResultSet = statement
                .executeQuery();

        String[] arr = null;
        while (ownerResultSet.next()) {
            String em = ownerResultSet.getString("Title");
            arr = em.split("\n");
            for (int i = 0; i < arr.length; i++) {
                System.out.println(arr[i]);
            }
        }

        return arr;

//        ResultSet titleResultSet = statement
//                .executeQuery("select distinct Title from CodeUChat.Conversations");
        //writeResultSet(resultSet);
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