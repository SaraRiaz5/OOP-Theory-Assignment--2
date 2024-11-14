import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Client {
    private BufferedReader in;
    private PrintWriter out;
    private Socket socket;
    private Scanner scanner;
    public Client(){
        try{
            this.scanner=new Scanner(System.in);
            System.out.println("Enter IP Address: ");
            String ip = scanner.nextLine();
            System.out.println("Enter Port Number: ");
            int port = scanner.nextInt();
            socket = new Socket(ip,port);
            out = new PrintWriter(socket.getOutputStream(), true, StandardCharsets.UTF_8);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream(),StandardCharsets.UTF_8));
            System.out.println("Connected to the server.");
        } catch (IOException e) {
            System.out.println("Error connecting to the server: " + e.getMessage());
        }
    }


    public static void main(String[] args) {
        Client client = new Client();
        Scanner sc = new Scanner(System.in);
        while(true){
            System.out.println("\nMessaging App Menu:");
            System.out.println("1. Send a Message");
            System.out.println("2. Read Sent Messages");
            System.out.println("3. Read Received Messages");
            System.out.println("4. Read All Messages");
            System.out.println("5. Search Messages by Keyword");
            System.out.println("6. Delete a Message by ID");
            System.out.println("7. Exit");
            System.out.print("Choose an option: ");
            try{
                String option = sc.nextLine();
                switch (option) {
                    case "1":
                        client.sendMessage();
                        break;
                    case "2":
                        client.readSentMessages();
                        break;
                    case "3":
                        client.readReceivedMessages();
                        break;
                    case "4":
                        client.readAllMessages();
                        break;
                    case "5":
                        client.searchMessages();
                        break;
                    case "6":
                        client.deleteMessage();
                        break;
                    case "7":
                        System.out.println("Exiting...");
                        client.closeConnection();
                        return;
                    default:
                        System.out.println("Invalid option. Please try again.");
                }
            } catch (IOException e) {
                System.out.println("Error processing option: " + e.getMessage());
            }
        }

    }

    // Send a message to the server
    private void sendMessage() throws IOException {
        out.println("SEND_MESSAGE");
        System.out.print("Enter your message: ");
        String messageContent = scanner.nextLine();
        Server.sentMessages[Server.sentCounter]=new Message(messageContent,"Sent");
        // Send data to server
        out.println(messageContent);

        // Receive confirmation
        System.out.println("Server response: " + in.readLine());
    }

    // Read sent messages from the server
    private void readSentMessages() throws IOException {
        out.println("READ_SENT_MESSAGES");
        displayServerResponse();
    }

    // Read received messages from the server
    private void readReceivedMessages() throws IOException {
        out.println("READ_RECEIVED_MESSAGES");
        displayServerResponse();
    }

    // Read all messages from the server
    private void readAllMessages() throws IOException {
        out.println("READ_ALL_MESSAGES");
        displayServerResponse();
    }

    // Search messages by keyword
    private void searchMessages() throws IOException {
        out.println("SEARCH_MESSAGE");
        System.out.print("Enter a keyword to search: ");
        String keyword = scanner.nextLine();
        out.println(keyword);
        displayServerResponse();
    }

    // Delete a message by ID
    private void deleteMessage() throws IOException {
        out.println("DELETE_MESSAGE");
        System.out.print("Enter the message ID to delete: ");
        String messageId = scanner.nextLine();
        out.println(messageId);
        System.out.println("Server response: " + in.readLine());
    }

    // Display the server's response line-by-line
    private void displayServerResponse() throws IOException {
        String serverResponse;
        while ((serverResponse = in.readLine()) != null) {
            if (serverResponse.equals("END_OF_MESSAGES")) break; // Indicates end of message list
            System.out.println(serverResponse);
        }
    }

    // Close the client connection
    private void closeConnection() {
        try {
            out.println("CLOSE_CONNECTION");
            socket.close();
            scanner.close();
            System.out.println("Connection closed.");
        } catch (IOException e) {
            System.out.println("Error closing connection: " + e.getMessage());
        }
    }
}