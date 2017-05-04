package codeu.chat.util.mysql;

import java.sql.*;
import java.util.Properties;


public class MySQLConnection{
    private static final String DATABASE_DRIVER = "com.mysql.jdbc.Driver";
    // Is my hosting information correct here?
    // Upon further testing I believe it is. Will likely need to be changed depending
    // 	on the computer that is running it.
    private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/CodeUChat?autoReconnect=true&&useSSL=false";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "RootedPass";

    // Create connection object
    private Connection connection;
    // Creat properties object
    private Properties properties;

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
}