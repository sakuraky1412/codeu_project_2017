
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

package codeu.chat.client.simplegui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.border.Border;

import codeu.chat.client.ClientContext;
import codeu.chat.common.ConversationSummary;
import codeu.chat.common.Message;
import codeu.chat.common.User;
import codeu.chat.util.encryption.EncryptionStrategy;
import codeu.chat.util.encryption.Copy;
import codeu.chat.util.encryption.CaesarCipher;
import codeu.chat.util.encryption.Greek;
import codeu.chat.util.encryption.MonoalphabeticCipher;
import codeu.chat.util.encryption.RailFence;
import codeu.chat.util.encryption.VigenereCipher;


// NOTE: JPanel is serializable, but there is no need to serialize MessagePanel
// without the @SuppressWarnings, the compiler will complain of no override for serialVersionUID
@SuppressWarnings("serial")
public final class MessagePanel extends JPanel implements ActionListener {

    // These objects are modified by the Conversation Panel.
    private final JLabel messageOwnerLabel = new JLabel("Owner:", JLabel.RIGHT);
    private final JLabel messageConversationLabel = new JLabel("Conversation:", JLabel.LEFT);
    // final DefaultListModel<String> messageListModel = new DefaultListModel<>();
    private final JTextArea userListArea = new JTextArea();

    private final ClientContext clientContext;

    /**
     * TextAreas for getting input and displaying output
     **/
    private JTextArea plain;
    private JTextArea cipher;
    /**
     * Buttons for returning output
     **/
    private JButton encrypt;
    private JButton decrypt;
    private JButton send;
    private final JButton updateButton = new JButton("Update");
    private final JButton addButton = new JButton("Add");

    /**
     * JComboBox that allows the user to choose encryption algorithms
     **/
    private JComboBox<String> encryptionType;
    /**
     * String of input and output
     **/
    private String plainText;
    private String cipherText;
    /**
     * Strings of shortened names of the encryption algorithms
     **/
    private String copy = "Copy";
    private String caesar = "Caesar cipher";
    private String rail = "Rail fence";
    private String mono = "Monoalphabetic cipher";
    private String vigen = "Vigenere cipher";
    private String greek = "Greek";
    /**
     * Logic behind the encryption and decryption
     **/
    private EncryptionStrategy strategy;

    /**
     * Add the buttons and combo box to the GUI
     */
    private void addButtons(JPanel p) {
        // set up JComboBox
        String[] encryptionNames = {copy, caesar, rail, mono, vigen, greek};
        encryptionType = new JComboBox<String>(encryptionNames);

        // set up buttons
        encrypt = new JButton("Encrypt");
        encrypt.addActionListener(this);
        decrypt = new JButton("Decrypt");
        decrypt.addActionListener(this);
        send = new JButton("Send");
        send.addActionListener(this);

        JPanel buttons = new JPanel(new FlowLayout());

        buttons.add(encrypt);
        buttons.add(decrypt);
        buttons.add(send);
        buttons.add(encryptionType);

        p.add(buttons, BorderLayout.SOUTH);
    }

    /**
     * Add the text areas to the GUI
     */
    private void addTextAreas(JPanel p) {
        // prepare the labels
        JLabel plainLabel = new JLabel("Plain text");
        JLabel cipherLabel = new JLabel("Cipher text");
        plainLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        cipherLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        setTextAreas();

        JPanel textAreas = new JPanel();
        textAreas.setLayout(new BoxLayout(textAreas, BoxLayout.Y_AXIS));

        textAreas.add(plainLabel);
        textAreas.add(plain);
        textAreas.add(cipherLabel);
        textAreas.add(cipher);

        p.add(textAreas, BorderLayout.CENTER);
    }

    /**
     * Set up the text areas
     */
    private void setTextAreas() {
        // Set up default display messages
        plainText = new String("Please enter plain text here.");
        cipherText = new String("Please enter cipher text here.");

        plain = new JTextArea(plainText, getWidth() / 2, getHeight() / 2);
        cipher = new JTextArea(cipherText, getWidth() / 2, getHeight() / 2);

        plain.setLineWrap(true);
        cipher.setLineWrap(true);

        plain.setWrapStyleWord(true);
        cipher.setWrapStyleWord(true);

        Border border = BorderFactory.createLineBorder(Color.BLACK);
        plain.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        cipher.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(10, 10, 10, 10)));
    }

    @Override
    /**
     * Implement the button functions
     *
     * @param e
     *            Clicking on a button
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        // Set the encryption type that the user chose
        setEncryption();
        // do the encryption and display the result in the cipher text area
        if (e.getSource() == encrypt) {
            plainText = plain.getText();
            cipherText = strategy.encrypt(plainText);
            cipher.setText(cipherText);
        }
        // produce the plain text
        else if (e.getSource() == decrypt) {
            cipherText = cipher.getText();
            plainText = strategy.decrypt(cipherText);
            plain.setText(plainText);
        }
        else if (e.getSource() == addButton) {
            if (!clientContext.user.hasCurrent()) {
                JOptionPane.showMessageDialog(MessagePanel.this, "You are not signed in.");
            } else if (!clientContext.conversation.hasCurrent()) {
                JOptionPane.showMessageDialog(MessagePanel.this, "You must select a conversation.");
            } else {
                final String messageText = (String) JOptionPane.showInputDialog(
                        MessagePanel.this, "Enter message:", "Add Message", JOptionPane.PLAIN_MESSAGE,
                        null, null, "");
                if (messageText != null && messageText.length() > 0) {
                    try {
                        clientContext.message.addMessage(
                                clientContext.user.getCurrent().id,
                                clientContext.conversation.getCurrentId(),
                                messageText);
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }
                    try {
                        MessagePanel.this.getAllMessages(clientContext.conversation.getCurrent());
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }
                }
            }

        }
        else if (e.getSource() == send) {
            if (!clientContext.user.hasCurrent()) {
                JOptionPane.showMessageDialog(MessagePanel.this, "You are not signed in.");
            } else if (!clientContext.conversation.hasCurrent()) {
                JOptionPane.showMessageDialog(MessagePanel.this, "You must select a conversation.");
            } else {
                final String messageText = cipher.getText();
                if (messageText != null && messageText.length() > 0) {
                    try {
                        clientContext.message.addMessage(
                                clientContext.user.getCurrent().id,
                                clientContext.conversation.getCurrentId(),
                                messageText);
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }
                    try {
                        MessagePanel.this.getAllMessages(clientContext.conversation.getCurrent());
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }
        else if (e.getSource() == updateButton) {
            clientContext.message.updateMessages(true);
            MessagePanel.this.getAllMessages(clientContext.conversation.getCurrent());
        }
    }

    /**
     * Get the encryption type that the user chose and assign the corresponding
     * codeStrategies subtypes so that we can use different encryption
     * algorithms
     */
    private void setEncryption() {
        String encryptionName = (String) encryptionType.getSelectedItem();
        if (encryptionName.equals(copy)) {
            strategy = new Copy();
        } else if (encryptionName.equals(caesar)) {
            strategy = new CaesarCipher();
        } else if (encryptionName.equals(rail)) {
            strategy = new RailFence();
        } else if (encryptionName.equals(mono)) {
            strategy = new MonoalphabeticCipher();
        } else if (encryptionName.equals(vigen)) {
            strategy = new VigenereCipher();
        } else if (encryptionName.equals(greek)) {
            strategy = new Greek();
        }
    }

    public MessagePanel(ClientContext clientContext) throws SQLException {
        super(new GridBagLayout());
        this.clientContext = clientContext;
        initialize();
        JPanel messagePanel = new JPanel(new BorderLayout());
        final GridBagConstraints messagePanelC = new GridBagConstraints();
        addTextAreas(messagePanel);
        addButtons(messagePanel);
        messagePanelC.gridx = 15;
        messagePanelC.gridy = 0;
        messagePanelC.gridwidth = 1;
        messagePanelC.gridheight = 5;
        messagePanelC.fill = GridBagConstraints.BOTH;
        messagePanelC.weightx = 0.5;
        messagePanelC.weighty = 0.5;
        this.add(messagePanel, messagePanelC);
    }

    // External agent calls this to trigger an update of this panel's contents.
    public void update(ConversationSummary owningConversation) throws SQLException {

        final User u = (owningConversation == null) ?
                null :
                clientContext.user.lookup(owningConversation.owner);

        messageOwnerLabel.setText("Owner: " +
                ((u == null) ?
                        ((owningConversation == null) ? "" : owningConversation.owner) :
                        u.name));

        messageConversationLabel.setText("Conversation: " + owningConversation.title);

        getAllMessages(owningConversation);
    }

    private void initialize() throws SQLException {

        // This panel contains the messages in the current conversation.
        // It has a title bar with the current conversation and owner,
        // then a list panel with the messages, then a button bar.

        // Title bar - current conversation and owner
        final JPanel titlePanel = new JPanel(new GridBagLayout());
        final GridBagConstraints titlePanelC = new GridBagConstraints();

        final JPanel titleConvPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        final GridBagConstraints titleConvPanelC = new GridBagConstraints();
        titleConvPanelC.gridx = 0;
        titleConvPanelC.gridy = 0;
        titleConvPanelC.anchor = GridBagConstraints.PAGE_START;

        final JPanel titleOwnerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        final GridBagConstraints titleOwnerPanelC = new GridBagConstraints();
        titleOwnerPanelC.gridx = 0;
        titleOwnerPanelC.gridy = 1;
        titleOwnerPanelC.anchor = GridBagConstraints.PAGE_START;

        // messageConversationLabel is an instance variable of Conversation panel
        // can update it.
        messageConversationLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleConvPanel.add(messageConversationLabel);

        // messageOwnerLabel is an instance variable of Conversation panel
        // can update it.
        messageOwnerLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleOwnerPanel.add(messageOwnerLabel);

        titlePanel.add(titleConvPanel, titleConvPanelC);
        titlePanel.add(titleOwnerPanel, titleOwnerPanelC);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // User List panel.
        final JPanel listShowPanel = new JPanel();
        final GridBagConstraints listPanelC = new GridBagConstraints();

        // messageListModel is an instance variable so Conversation panel
        // can update it.
        // final JList<String> userList = new JList<>(messageListModel);
        // userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // userList.setVisibleRowCount(15);
        // userList.setSelectedIndex(-1);

        final JScrollPane userListScrollPane = new JScrollPane(userListArea);
        listShowPanel.add(userListScrollPane);
        userListScrollPane.setMinimumSize(new Dimension(500, 180));
        userListScrollPane.setPreferredSize(new Dimension(500, 180));

        // Button panel
        final JPanel buttonPanel = new JPanel();
        final GridBagConstraints buttonPanelC = new GridBagConstraints();

        buttonPanel.add(updateButton);
        buttonPanel.add(addButton);

        // Placement of title, list panel, buttons, and current user panel.
        titlePanelC.gridx = 0;
        titlePanelC.gridy = 0;
        titlePanelC.gridwidth = 10;
        titlePanelC.gridheight = 1;
        titlePanelC.fill = GridBagConstraints.HORIZONTAL;
        titlePanelC.anchor = GridBagConstraints.FIRST_LINE_START;

        listPanelC.gridx = 0;
        listPanelC.gridy = 1;
        listPanelC.gridwidth = 10;
        listPanelC.gridheight = 8;
        listPanelC.fill = GridBagConstraints.BOTH;
        listPanelC.anchor = GridBagConstraints.FIRST_LINE_START;
        listPanelC.weighty = 0.8;

        buttonPanelC.gridx = 0;
        buttonPanelC.gridy = 11;
        buttonPanelC.gridwidth = 10;
        buttonPanelC.gridheight = 1;
        buttonPanelC.fill = GridBagConstraints.HORIZONTAL;
        buttonPanelC.anchor = GridBagConstraints.FIRST_LINE_START;

        this.add(titlePanel, titlePanelC);
        this.add(listShowPanel, listPanelC);
        this.add(buttonPanel, buttonPanelC);

        // Update the current message panel
        updateButton.addActionListener(this);
        // User click Messages Add button - prompt for message body and add new Message to Conversation
        addButton.addActionListener(this);

        // Panel is set up. If there is a current conversation, Populate the conversation list.
        getAllMessages(clientContext.conversation.getCurrent());
    }

    // Populate ListModel
    // TODO: don't refetch messages if current conversation not changed
    private void getAllMessages(ConversationSummary conversation) throws SQLException {
        userListArea.setText("");

        for (final Message m : clientContext.message.getConversationContents(conversation)) {
            // Display author name if available.  Otherwise display the author UUID.
            final String authorName = clientContext.user.getName(m.author);

            final String displayString = String.format("%s: [%s]: %s",
                    ((authorName == null) ? m.author : authorName), m.creation, m.content);

            userListArea.append(displayString + '\n');
        }
    }
}
