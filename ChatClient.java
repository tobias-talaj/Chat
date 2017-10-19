import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class ChatClient extends Application {

	Socket socket;
	BufferedReader reader;
	PrintWriter writer;
	TextArea conversationTextArea;
	Calendar calendar;
	MenuBar menuBar;
	String nick;

	public void configureCommunication() {
		try {
			socket = new Socket("127.0.0.1", 5000);
			InputStreamReader readerStrm = new InputStreamReader(socket.getInputStream());
			reader = new BufferedReader(readerStrm);
			writer = new PrintWriter(socket.getOutputStream());
			System.out.println("Communication service prepared");
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void start(Stage primaryStage) {
		configureCommunication();
		Runnable receiver = new Receiver();
		Thread thread = new Thread(receiver);
		thread.start();

		primaryStage.setTitle("JavaFX Chat");
		GridPane grid = new GridPane();
		grid.setAlignment(Pos.TOP_CENTER);
		grid.setHgap(0);
		grid.setVgap(10);
		grid.setPadding(new Insets(0, 0, 0, 0));
		menuBar = new MenuBar();
		grid.add(menuBar, 1, 0);
		menuBar.prefWidthProperty().bind(primaryStage.widthProperty());

		Menu propMenu = new Menu("Properties");
		MenuItem nickItem = new MenuItem("Nickname");
		menuBar.getMenus().addAll(propMenu);
		propMenu.getItems().addAll(nickItem);

		conversationTextArea = new TextArea();
		grid.add(conversationTextArea, 1, 1);
		conversationTextArea.setPrefRowCount(5);
		conversationTextArea.setPrefColumnCount(5);
		conversationTextArea.setWrapText(true);
		conversationTextArea.setEditable(false);
		conversationTextArea.setPromptText("Conversation");
		conversationTextArea.setFocusTraversable(false);

		TextField messageTextField = new TextField();
		grid.add(messageTextField, 1, 2);
		messageTextField.setPromptText("Type your message: ");

		Button button = new Button("Send");
		HBox hbButton = new HBox(10);
		hbButton.setAlignment(Pos.BOTTOM_CENTER);
		hbButton.getChildren().add(button);
		grid.add(hbButton, 1, 4);

		button.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent actionEvent) {
				String message = messageTextField.getText();
				writer.println(message);
				writer.flush();
				messageTextField.clear();
			}
		});

		messageTextField.setOnKeyPressed(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent keyEvent) {
				if (keyEvent.getCode().equals(KeyCode.ENTER)) {
					String message = messageTextField.getText();
					writer.println(message);
					writer.flush();
					messageTextField.clear();
				}
			}
		});

		nickItem.setOnAction(new EventHandler<ActionEvent>() {

			public void handle(ActionEvent nickEvent) {
				Stage stage = new Stage();
				stage.setTitle("Change your nickname");
				GridPane grid = new GridPane();
				grid.setAlignment(Pos.CENTER);
				grid.setHgap(10);
				grid.setVgap(10);
				grid.setPadding(new Insets(25, 25, 25, 25));
				TextField userTextField = new TextField();
				grid.add(userTextField, 1, 1);

				userTextField.setOnKeyPressed(new EventHandler<KeyEvent>() {

					@Override
					public void handle(KeyEvent keyEvent) {
						if (keyEvent.getCode().equals(KeyCode.ENTER)) {
							nick = userTextField.getText();
							System.out.println("Nickname was changed to: " + nick);
							stage.close();
						}
					}
				});

				Scene scene = new Scene(grid, 300, 275);
				stage.setScene(scene);
				stage.show();
			}
		});

		Scene scene = new Scene(grid, 350, 300);
		primaryStage.setScene(scene);
		primaryStage.show();

	}

	public static void main(String[] args) {
		launch(args);
	}

	public class Receiver implements Runnable {
		public void run() {
			String receivedMessage;
			try {
				while ((receivedMessage = reader.readLine()) != null) {
					calendar = GregorianCalendar.getInstance();
					int hour = calendar.get(Calendar.HOUR_OF_DAY);
					int minute = calendar.get(Calendar.MINUTE);
					System.out.println("Received message: " + receivedMessage);
					conversationTextArea
							.appendText("[" + hour + ":" + minute + "]" + nick + " wrote: " + receivedMessage + "\n");
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}
