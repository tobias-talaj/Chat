package Chat;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

public class Archive {
	public static final String DRIVER = "org.sqlite.JDBC";
	public static final String DB_URL = "jdbc:sqlite:archive.db";

	private Connection conn;
	private Statement stat;
	
	Calendar calendar = GregorianCalendar.getInstance();
	public int hour = calendar.get(Calendar.HOUR_OF_DAY);
	public int minute = calendar.get(Calendar.MINUTE);
	String time = String.format("%02d:%02d", hour, minute);

	public Archive() {
		try {
			Class.forName(Archive.DRIVER);
		} catch (ClassNotFoundException e) {
			System.err.println("Brak sterownika JDBC");
			e.printStackTrace();
		}

		try {
			conn = DriverManager.getConnection(DB_URL);
			stat = conn.createStatement();
		} catch (SQLException e) {
			System.err.println("Problem z otwarciem polaczenia");
			e.printStackTrace();
		}

		createTable();
	}

	public boolean createTable() {
		String createArchive = "CREATE TABLE IF NOT EXISTS archive (message_id INTEGER PRIMARY KEY AUTOINCREMENT, time TEXT, user TEXT, message TEXT)";
		try {
			stat.execute(createArchive);
		} catch (SQLException e) {
			System.err.println("Blad przy tworzeniu tabeli");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean insertMessage(String userName, String archMessage) {
		try {
			PreparedStatement prepStmt = conn.prepareStatement("insert into archive values (NULL, ?, ?, ?);");
			prepStmt.setString(1, time);
			prepStmt.setString(2, userName);
			prepStmt.setString(3, archMessage);
			prepStmt.execute();
		} catch (SQLException e) {
			System.err.println("Blad przy wstawianiu wiadomosci");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public List<Messages> showHistory() {
		List<Messages> archivedMessages = new LinkedList<Messages>();
		try {
			ResultSet result = stat.executeQuery("SELECT * FROM archive");
			int id;
			String time, user, message;
			while (result.next()) {
				id = result.getInt("message_id");
				time = result.getString("time");
				user = result.getString("user");
				message = result.getString("message") + "\n";
				archivedMessages.add(new Messages(id, time, user, message));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return archivedMessages;
	}

	public void closeConnection() {
		try {
			conn.close();
		} catch (SQLException e) {
			System.err.println("Problem z zamknieciem polaczenia");
			e.printStackTrace();
		}
	}
}