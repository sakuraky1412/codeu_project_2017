// Copyright 2017 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package codeu.chat.client;

import codeu.chat.common.BasicController;
import codeu.chat.common.Conversation;
import codeu.chat.common.Message;
import codeu.chat.common.NetworkCode;
import codeu.chat.common.User;
import codeu.chat.common.Uuid;
import codeu.chat.common.Uuids;
import codeu.chat.util.Logger;
import codeu.chat.util.Serializers;
import codeu.chat.util.connections.Connection;
import codeu.chat.util.connections.ConnectionSource;

import codeu.chat.util.mysql.MySQLConnection;

public class Controller implements BasicController {

    private final static Logger.Log LOG = Logger.newLog(Controller.class);

    private final ConnectionSource source;

    public Controller(ConnectionSource source) {
        this.source = source;
    }

    @Override
    public Message newMessage(Uuid author, Uuid conversation, String body) {

        Message response = null;

        try (final Connection connection = source.connect()) {

            Serializers.INTEGER.write(connection.out(), NetworkCode.NEW_MESSAGE_REQUEST);
            Uuids.SERIALIZER.write(connection.out(), author);
            Uuids.SERIALIZER.write(connection.out(), conversation);
            Serializers.STRING.write(connection.out(), body);

            MySQLConnection conn = new MySQLConnection();


            if (Serializers.INTEGER.read(connection.in()) == NetworkCode.NEW_MESSAGE_RESPONSE) {
                response = Serializers.nullable(Message.SERIALIZER).read(connection.in());
                conn.updateMessages(response.previous, response.next, conversation);
//                conn.updateConversations(response.id, conversation);
            } else {
                LOG.error("Response from server failed.");
            }
        } catch (Exception ex) {
            System.out.println("ERROR: Exception during call on server. Check log for details.");
            LOG.error(ex, "Exception during call on server.");
        }

        return response;
    }

    /**
     * Creates a new user, making sure to clear it with the server first, before adding the name to the MySQL database.
     *
     * @param name name of the user
     * @return the newly added user
     */
    @Override
    public User newUser(String name) {

        User response = null;

        try (final Connection connection = source.connect()) {

            Serializers.INTEGER.write(connection.out(), NetworkCode.NEW_USER_REQUEST);
            Serializers.STRING.write(connection.out(), name);
            LOG.info("newUser: Request completed.");

            if (Serializers.INTEGER.read(connection.in()) == NetworkCode.NEW_USER_RESPONSE) {
                response = Serializers.nullable(User.SERIALIZER).read(connection.in());
                LOG.info("newUser: Response completed.");
            } else {
                LOG.error("Response from server failed.");
            }
        } catch (Exception ex) {
            System.out.println("ERROR: Exception during call on server. Check log for details.");
            LOG.error(ex, "Exception during call on server.");
        }

        return response;
    }

    /**
     * Add new user with password
     *
     * @param name name of the user
     * @param pass user password
     * @return the newly added user
     */
    public User newUser(String name, String pass) {

        User response = null;

        try (final Connection connection = source.connect()) {

            Serializers.INTEGER.write(connection.out(), NetworkCode.NEW_USER_REQUEST);
            Serializers.STRING.write(connection.out(), name);
            Serializers.STRING.write(connection.out(), pass);

            LOG.info("newUser: Request completed.");

            // Quick test to see if user with password request is successfully created
            // System.out.println("User with password request created.");

            // If server response works correctly
            if (Serializers.INTEGER.read(connection.in()) == NetworkCode.NEW_USER_RESPONSE) {
                response = Serializers.nullable(User.SERIALIZER).read(connection.in());
                LOG.info("newUser: Response completed.");
                // Send name to database
                // mysqlConnection.addUser(name);
            } else {
                // If server response fails
                LOG.error("Response from server failed.");
            }
        } catch (Exception ex) {
            System.out.println("ERROR: Exception during call on server. Check log for details.");
            LOG.error(ex, "Exception during call on server.");
        }

        return response;
    }

    @Override
    public Conversation newConversation(String title, Uuid owner) {

        Conversation response = null;

        try (final Connection connection = source.connect()) {

            Serializers.INTEGER.write(connection.out(), NetworkCode.NEW_CONVERSATION_REQUEST);
            Serializers.STRING.write(connection.out(), title);
            Uuids.SERIALIZER.write(connection.out(), owner);

            if (Serializers.INTEGER.read(connection.in()) == NetworkCode.NEW_CONVERSATION_RESPONSE) {
                response = Serializers.nullable(Conversation.SERIALIZER).read(connection.in());
            } else {
                LOG.error("Response from server failed.");
            }
        } catch (Exception ex) {
            System.out.println("ERROR: Exception during call on server. Check log for details.");
            LOG.error(ex, "Exception during call on server.");
        }

        return response;
    }
}
