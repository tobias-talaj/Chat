import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ChatClient extends Application {

	Socket socket;
	BufferedReader reader;
	PrintWriter writer;
	TextArea conversationTextArea;

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
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25, 25, 25, 25));
		Text sceneTitle = new Text("Welcome");
		sceneTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
		grid.add(sceneTitle, 0, 0, 2, 1);

		Label conversationLabel = new Label("Conversation:");
		grid.add(conversationLabel, 0, 1);
		conversationTextArea = new TextArea();
		grid.add(conversationTextArea, 1, 1);
		conversationTextArea.setPrefRowCount(5);
		conversationTextArea.setPrefColumnCount(5);
		conversationTextArea.setWrapText(true);
		conversationTextArea.setEditable(false);

		Label messageLabel = new Label("Type your message:");
		grid.add(messageLabel, 0, 2);
		TextField messageTextField = new TextField();
		grid.add(messageTextField, 1, 2);

		Button button = new Button("Send");
		HBox hbButton = new HBox(10);
		hbButton.setAlignment(Pos.BOTTOM_RIGHT);
		hbButton.getChildren().add(button);
		grid.add(hbButton, 1, 4);

		button.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent e) {
				String message = messageTextField.getText();
				writer.println(message);
				writer.flush();
				messageTextField.clear();
			}
		});

		Scene scene = new Scene(grid, 330, 275);
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
					System.out.println("Received message: " + receivedMessage);
					conversationTextArea.appendText(receivedMessage + "\n" );
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}
