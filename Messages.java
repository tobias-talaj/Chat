package Chat;

public class Messages {
	private int message_id;
	private String time;
    private String name;
    private String message;
 
    public int getId() {
        return message_id;
    }
    public void setId(int id) {
        this.message_id = id;
    }
    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
 
    public Messages() { }
    public Messages(int id, String time, String name, String message) {
        this.message_id = id;
        this.time = time;
        this.name = name;
        this.message = message;
    }
 
    @Override
    public String toString() {
        return "[" + time + "] " + name + ": " + message;
    }
}
