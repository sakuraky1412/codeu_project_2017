package codeu.chat.util.mysql;

import java.sql.*;
import java.util.Calendar;
import java.util.Properties;

//import com.mysql.jdbc.Driver;

public class MySQLConnection {
    private static final String DATABASE_DRIVER = "com.mysql.jdbc.Driver";
    // Will likely need to be changed depending on the computer that is running it.
    private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/codeu?autoReconnect=true&&useSSL=false";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "1869";

    // Create connection object
    private Connection connection;
    // Create properties object
    private Properties properties;

    // Create the actual properties
    private Properties getProperties() {
        if (properties == null) {
            properties = new Properties();
            properties.setProperty("user", USERNAME);
            properties.setProperty("password", PASSWORD);
        }
        return properties;
    }

    // Connect to the database
    private Connection connect() {
        if (connection == null) {
            try {
                Class.forName(DATABASE_DRIVER);
                connection = DriverManager.getConnection(DATABASE_URL, getProperties());
            } catch (ClassNotFoundException | SQLException e) {
                System.out.println("There was an error connecting to the database.");
                e.printStackTrace();
            }
        }
        return connection;
    }

    public Connection getConnection() {
        return connect();
    }

    // Closing the database connection
    private void disconnect() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void disconnectConnection() {
        disconnect();
    }

    /*
        ADD METHODS
        Adds information to the database
    */
    public void addUser(String userName) {
        // Create connection and statement
        Connection conn = getConnection();
        Statement state = null;

        try {
            // Create and execute statement
            state = conn.createStatement();
            state.executeUpdate("INSERT INTO Users (NAME, PASSWORD) VALUES (\"" + userName +
                    "\", NULL");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        disconnectConnection();
    }

    public void addUser(String userName, String password) {
    }

    ;

    public void addConversation(String title, String owner) {
        // Create connection and statement
        Connection conn = getConnection();
        Statement state = null;

        try {
            // Create nad execute adding a conversation
            state = conn.createStatement();
            state.executeUpdate("INSERT INTO Conversations (Title, Owner) VALUES (\"" + title + "\",owner");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addConversation(String title, String owner, String password) {
    }

    ;

    public void addMessage(String owner, String body, String conversation) {
        // Fetch timestamp
        Calendar cal = Calendar.getInstance();
        java.util.Date date = cal.getTime();
        java.sql.Timestamp currentTimestamp = new Timestamp(date.getTime());

    }
}