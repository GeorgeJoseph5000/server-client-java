import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.awt.event.*;

public class Client extends JFrame{
	
	private JTextField userField;
	private JTextArea chatWindow;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private Socket connection;
	private String ip;
	
	public Client(String ip) {
		super("Client mofo!");
		this.ip = ip;
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
			try{
				connectToServer();
				setupStreams();
				whileChatting();
			} catch (EOFException e) {
				showMessage("\nClient ended connection!!!\n");
			}catch (IOException e) {
				e.printStackTrace();
			}finally{
				closeCrap();
			}
		}
	private void connectToServer() throws IOException {
		showMessage("Attempting connection....\n");
		connection = new Socket(InetAddress.getByName(ip), 15478);
	}
	private void setupStreams() throws IOException {
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		showMessage("Streams are now setup!!!\n\n");
	}
	private void whileChatting() throws IOException {
		String message = null;
		ableToType(true);
		do{
			try{
				message = (String) input.readObject();
				showMessage(message);
			}catch (ClassNotFoundException e) {
				showMessage("idk wtf what you wrote");
			}	
		}while(!message.equals("SERVER - END\n"));
	}
	private void closeCrap() {
		ableToType(false);
		try{
			output.close();
			input.close();
			connection.close();
			showMessage("Closing connections!!\n\n");
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	private void sendMessage(String message) {
		try {
			String eMessage = String.format("CLIENT - %s\n",message);
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
