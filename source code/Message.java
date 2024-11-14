import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Message implements Comparable<Message>{
    final String messageId;
    private String content;
    private String dateTime;
    private String status;
    private static final String  senderName="Aleesha";
    private static final String receiverName="Sara";

    public static int id = 0;

    public Message(String content, String status){
        this.messageId=String.format("MSG%07d",++id);
        this.content = content;
        LocalDateTime current = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE dd-MM-yyyy HH:mm:ss");
        this.dateTime= current.format(formatter);
        this.status=status;
    }

    public String getContent() {
        return content;
    }

    public String getMessageId() {
        return messageId;
    }
    @Override
    public int compareTo(Message other) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE dd-MM-yyyy HH:mm:ss");
        LocalDateTime thisDateTime = LocalDateTime.parse(this.dateTime, formatter);
        LocalDateTime otherDateTime = LocalDateTime.parse(other.dateTime, formatter);
        return thisDateTime.compareTo(otherDateTime);
    }

    @Override
    public String toString(){
        return String.format("To: %s\nMessage ID: %s\nContent: %s\nStatus: %s\nFrom: %s\n%s\n",receiverName,messageId,content,status,senderName,dateTime);
    }
}