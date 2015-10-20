package TrivaGameClient;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

/**
 * 
 * @author Jordanelle (Nikki) Espinosa
 * Oct 19, 2015
 * This is the initial GUI display the player receives when attempting to enter the game
 * 
 * 
 * 
 *  Needs to get
        User name
        Server IP address
        Is the user ready or not
        Deregistering the user before that game starts

    Needs to Display
        The users name
        The IP address of the server they are currently connected to
        Registration success or failure
        Deregistering success or failure
        User ready status

 *
 */
public class InitialDisplay {

	private String nameStr = "";
	private String serverIPStr = "";
	private boolean isRegistered = false;
	private boolean isReady = false;

	private JLabel yourName;
	private JTextField enterYourName;
	private JLabel serverIP;
	private JTextField enterServerIP;
	private JLabel registered;
	private JButton register;
	private JButton ready;
	private JLabel readyNWaiting;
	
	private RegistrationSystem sys;
	
	public InitialDisplay(){}	// WILL BE REMOVED AFTER TESTING!
	
	public InitialDisplay(RegistrationSystem sys)
	{
		this.sys = sys;
	}

	//go() method for the class; creates the GUI with components, registers the components with their listeners
	public void go()
	{

		//Components for frame
		JFrame frame = new JFrame("Registration Window");
		yourName = new JLabel("Your name: ");
		JLabel enterN = new JLabel("Enter: ");
		JLabel enterS = new JLabel("Enter: ");
		enterYourName = new JTextField(20);
		serverIP = new JLabel("Server's IP: ");
		enterServerIP = new JTextField(20);
		registered = new JLabel("You are not registered.");
		register = new JButton("Register");
		ready = new JButton("Ready to Start Game");
		readyNWaiting = new JLabel("");
		JLabel emptySpace = new JLabel("-------------------------------------");



		//Panels for frame
		JPanel biggestPanel = new JPanel();
		JPanel yourNamePanel = new JPanel();
		JPanel serverIPPanel = new JPanel();
		JPanel regReadPanel = new JPanel();
		JPanel smallPanel1 = new JPanel();
		JPanel smallPanel2 = new JPanel();

		//Setting layouts of panels
		biggestPanel.setLayout(new BoxLayout(biggestPanel, BoxLayout.Y_AXIS));
		yourNamePanel.setLayout(new BoxLayout(yourNamePanel, BoxLayout.Y_AXIS));
		serverIPPanel.setLayout(new BoxLayout(serverIPPanel, BoxLayout.Y_AXIS));
		regReadPanel.setLayout(new BoxLayout(regReadPanel, BoxLayout.Y_AXIS));
		smallPanel1.setLayout(new BoxLayout(smallPanel1, BoxLayout.X_AXIS));
		smallPanel2.setLayout(new BoxLayout(smallPanel2, BoxLayout.X_AXIS));

		//Adding components to frame and panels
		frame.getContentPane().add(BorderLayout.NORTH, biggestPanel);
		biggestPanel.add(yourNamePanel);
		biggestPanel.add(serverIPPanel);
		biggestPanel.add(regReadPanel);

		yourNamePanel.add(yourName);
		yourNamePanel.add(smallPanel1);
		smallPanel1.add(enterN);
		smallPanel1.add(enterYourName);

		serverIPPanel.add(serverIP);
		serverIPPanel.add(smallPanel2);
		smallPanel2.add(enterS);
		smallPanel2.add(enterServerIP);

		regReadPanel.add(registered);
		regReadPanel.add(register);
		regReadPanel.add(emptySpace);
		regReadPanel.add(ready);
		regReadPanel.add(readyNWaiting);


		//Registering the components with their listeners
		enterYourName.addActionListener(new nameListener());
		enterServerIP.addActionListener(new serverIPListener());
		register.addActionListener(new registerListener());
		ready.addActionListener(new readyListener());

		// Close everything if they close the window
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Make the frame starts at a non-silly size
		frame.setSize(500, 300);

		// Make it visible (otherwise you won't see it!)
		frame.setVisible(true);

	}//go()
	
	private void updateName()
	{
		nameStr = enterYourName.getText();
		yourName.setText("Your name: "+nameStr);
	}
	
	private void updateIp()
	{
		serverIPStr = enterServerIP.getText();
		serverIP.setText("Server's IP: "+serverIPStr);
	}
	

	//Get the player's name, as they entered it
	public String getPlayerName(){
		return nameStr;
	}//getPlayerName
	
	//Get the server's IP, as the user entered it
	public String getServerIP(){
		return serverIPStr;
	}//getServerIP
	
	//Gets whether the user has CLICKED register button
	public boolean getIsRegistered(){
		return isRegistered;
	}//getIsRegistered
	
	//Get whether the user has clicked Ready button
	public boolean getIsReady(){
		return isReady;
	}//getIsReady
	

	/**
	 * 
	 * Listeners
	 */


	// Respond to buzzer
	class nameListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			updateName();

		}//actionPerformed
	}//FieldListener

	// Respond to serverIP input
	class serverIPListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			updateIp();

		}//actionPerformed
	}//FieldListener

	// Respond to register/deregister button
	class registerListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			if(isRegistered){
				isRegistered = false;
				register.setText("Register");
				registered.setText("You are deregistered.");
				
				/*** add deregister code here ***/
				
			}//if isRegistered already
			else{
				isRegistered = true;
				register.setText("Deregister");
				registered.setText("You are registered.");
				updateName();
				updateIp();
				
				/*** add register code here ***/
				
			}//else
		}//actionPerformed
	}//FieldListener

	// Respond to ready button
	class readyListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			
			if(!isReady)
			{
				isReady = true;
				readyNWaiting.setText("Your game will begin shortly.");
				ready.setText("Not Ready");
				
				/*** add ready code here ***/
			}
			else
			{
				isReady = false;
				readyNWaiting.setText("");
				ready.setText("Ready");
				
				/*** add not ready code here ***/
			}

		}//actionPerformed
	}//FieldListener



	//main method for creating an InitialDisplay object and calling its go() method
	public static void main(String [] args)
	{
		InitialDisplay initDisp = new InitialDisplay();
		initDisp.go();
	}//main()

}//InitialDisplay
