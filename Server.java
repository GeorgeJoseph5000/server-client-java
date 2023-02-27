import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.awt.event.*;

public class Server extends JFrame{
	
	private JTextField userField;
	private JTextArea chatWindow;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private Socket connection;
	private ServerSocket server;
	
	public Server() {
		super("Eailio Instant Messanger");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		setSize(350, 150);
		userField = new JTextField();
		userField.setEditable(false);
		userField.addActionListener(
			new ActionListener(){
				public void actionPerformed(ActionEvent event){
					sendMessage(event.getActionCommand());
					userField.setText("");
				}
			}
		);
		add(userField, BorderLayout.NORTH);
		chatWindow = new JTextArea();
		chatWindow.setEditable(false);
		add(new JScrollPane(chatWindow), BorderLayout.CENTER);
	}
	public void startRunning(){
		try {
			server = new ServerSocket(15478, 100);
			while (true) {
				try{
					waitingForConnection();
					setupStreams();
					whileChatting();
				} catch (EOFException e) {
					showMessage("\nServer ended connection!!!\n");
				}finally{
					closeCrap();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private void waitingForConnection() throws IOException {
		showMessage("Waiting for connection....\n");
		connection = server.accept();
		showMessage(String.format("Now you are connected to %s\n", connection.getInetAddress().getHostName()));
	}
	private void setupStreams() throws IOException {
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		showMessage("Streams are now setup!!!\n\n");
	}
	private void whileChatting() throws IOException {
		String message = "You are now connected";
		sendMessage(message);
		ableToType(true);
		do{
			try{
				message = (String) input.readObject();
				showMessage(message);
			}catch (ClassNotFoundException e) {
				showMessage("idk wtf what you wrote");
			}	
		}while(!message.equals("CLIENT - END\n"));
	}
	private void closeCrap() throws IOException {
		ableToType(false);
		output.close();
		input.close();
		connection.close();
		showMessage("Closing connections!!\n\n");
	}
	private void sendMessage(String message) {
		try {
			String eMessage = String.format("SERVER - %s\n",message);
			output.writeObject(eMessage);
			showMessage(eMessage);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private void showMessage(String message) {
		chatWindow.append(message);
	}
	private void ableToType(boolean tof) {
		userField.setEditable(tof);
	}
}
