import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Server {
    private static final int PORT = 12345;
    public static final int MAX_MESSAGES = 200;
    public static Message[] sentMessages = new Message[MAX_MESSAGES];
    public static Message[] receivedMessages = new Message[MAX_MESSAGES];
    public static int sentCounter = 0;
    public static int receivedCounter = 0;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server Connected and Listening on Port: " + PORT);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New Client Connected: " + clientSocket.getInetAddress());
                new ClientHandler(clientSocket).start();
            }
        } catch (IOException e) {
            System.out.println("Error Starting server: " + e.getMessage());
        }
    }

    static class ClientHandler extends Thread {
        private Socket clientSocket;
        private BufferedReader in;
        private PrintWriter out;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        public void run() {
            try {
                out = new PrintWriter(clientSocket.getOutputStream(), true, StandardCharsets.UTF_8);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
                String clientRequest;
                while ((clientRequest = in.readLine()) != null) {
                    switch (clientRequest) {
                        case "SEND_MESSAGE":
                            handleSendMessage();
                            break;
                        case "READ_SENT_MESSAGES":
                            handleReadSentMessages();
                            break;
                        case "READ_RECEIVED_MESSAGES":
                            handleReadReceivedMessages();
                            break;
                        case "READ_ALL_MESSAGES":
                            handleReadAllMessages();
                            break;
                        case "SEARCH_MESSAGE":
                            handleSearchMessage();
                            break;
                        case "DELETE_MESSAGE":
                            handleDeleteMessage();
                            break;
                        case "CLOSE_CONNECTION":
                            handleCloseConnection();
                            return;
                        default:
                            out.println("Invalid request.");
                    }
                }
            } catch (IOException e) {
                System.out.println("Error in Client Handler: " + e.getMessage());
            }
        }

        private void handleSendMessage() throws IOException {
            if (sentCounter >= MAX_MESSAGES) {
                out.println("Messages Limit Reached");
                return;
            }

            String messageContent = in.readLine();
            System.out.println("Received and saved message: " + messageContent);
            out.println("Message sent successfully.");
            receivedMessages[receivedCounter++] = new Message(messageContent, "Received");

        }

        private void handleReadSentMessages() {
            if (sentCounter == 0) {
                out.println("No sent messages found.");
                out.println("END_OF_MESSAGES"); // Signal end of response
            } else {
                for (int i = 0; i < sentCounter; i++) {
                    out.println(sentMessages[i]);
                }
                out.println("END_OF_MESSAGES"); // Signal end of response
            }
        }

        private void handleReadReceivedMessages() {
            if (receivedCounter == 0) {
                out.println("No received messages found.");
                out.println("END_OF_MESSAGES"); // Signal end of response
            } else {
                for (int i = 0; i < receivedCounter; i++) {
                    out.println(receivedMessages[i]);
                }
                out.println("END_OF_MESSAGES"); // Signal end of response
            }
        }

        private void handleReadAllMessages() {
            if (sentCounter == 0 && receivedCounter == 0) {
                out.println("No messages found.");
                return;
            } else {
                Message[] allMessages = new Message[sentCounter + receivedCounter];
                System.arraycopy(sentMessages, 0, allMessages, 0, sentCounter);
                System.arraycopy(receivedMessages, 0, allMessages, sentCounter, receivedCounter);

                Arrays.sort(allMessages, (m1, m2) -> m2.compareTo(m1)); // Sorts all messages based on date and time

                for (Message message : allMessages) {
                    out.println(message);
                }
                out.println("END_OF_MESSAGES"); // Signal end of response
            }
        }

        private void handleSearchMessage() throws IOException {
            String keyword = in.readLine();
            boolean found = false;
            for (int i = 0; i < sentCounter; i++) {
                if (sentMessages[i].getContent().contains(keyword)) {
                    out.println(sentMessages[i].toString());
                    found = true;
                }
            }
            for (int i = 0; i < receivedCounter; i++) {
                if (receivedMessages[i].getContent().contains(keyword)) {
                    out.println(receivedMessages[i].toString());
                    found = true;
                }
            }
            if (found)
                out.println("END_OF_MESSAGES"); // Signal end of response
            if (!found) {
                out.println("No messages found for the keyword: " + keyword);
                out.println("END_OF_MESSAGES"); // Signal end of response
            }
        }

        private void handleDeleteMessage() throws IOException {
            String messageId = in.readLine();
            boolean messageDeleted = false;
            for (int i = 0; i < sentCounter; i++) {
                if (sentMessages[i].getMessageId().equals(messageId)) {
                    for (int j = i; j < sentCounter - 1; j++) {
                        sentMessages[j] = sentMessages[j + 1];
                    }
                    sentMessages[--sentCounter] = null;
                    messageDeleted = true;
                    break;
                }
            }
            for (int i = 0; i < receivedCounter && !messageDeleted; i++) {
                if (receivedMessages[i].getMessageId().equals(messageId)) {
                    for (int j = i; j < receivedCounter - 1; j++) {
                        receivedMessages[j] = receivedMessages[j + 1];
                    }
                    receivedMessages[--receivedCounter] = null;
                    messageDeleted = true;
                    break;
                }
            }
            out.println(messageDeleted ? "Message deleted successfully." : "Message not found.");
        }

        // Close the client connection
        private void handleCloseConnection() {
            try {
                System.out.println("Client disconnected: " + clientSocket.getInetAddress());
                clientSocket.close();
            } catch (IOException e) {
                System.out.println("Error closing connection: " + e.getMessage());
            }
        }
    }
}