package TrivaGameClient;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.util.Observable;
import java.util.Observer;
import java.util.UUID;

import javax.swing.*;

/**
 * 
 * @author Jordanelle (Nikki) Espinosa
 * Oct 19, 2015
 * The display GUI for game play with the BUZZER trivia game
 *
 *  Needs to get
        The answer the user selects
        If user buzzed in
        User wants to quit

    Needs to display
        Time to answer a question and the reaming time on a question
        The score for that player and all other players it receives information on
        The question, answers, category, difficulty, etc.

    Other functionality
        Must be able to prevent input when some one buzzes in and only allow input after the Triva Game Systems says so
 *
 */
public class GamePlayDisplay implements Observer  {

	private boolean buzzed = false;
	private int answerChosen = 0;
	private boolean quit = false;
	private UUID selectedAnswerId;

	private JFrame frame;
	private JLabel yourName;
	private JLabel score;
	private JLabel timer;
	private JTextArea opponentsText;
	private JButton quitButton;
	private JTextArea topicText;
	private JTextArea questionText;
	private ButtonGroup questionOptions;
	private JRadioButton[] answers = new JRadioButton[4];
	private JButton BUZZ;

	private TriviaGame sys;
	private Thread sysThread;
	private UUID playerID;
	private InetAddress groupIp;
	private UUID [] answerIDs;
	
	public GamePlayDisplay(TriviaGame sys)
	{
		this.sys = sys;
		sys.addObserver(this);
		
		sysThread = new Thread(sys);
		sysThread.start();
	}

	//go() method for the class; creates the GUI with components, registers the components with their listeners
	public void go()
	{

		//Components for the display
		frame = new JFrame("Game Play Window");
		JLabel opponents = new JLabel("OPPONENTS ");
		JLabel nameNScore = new JLabel("Name:   Score: ");
		yourName = new JLabel("Player: "+"     ");
		score = new JLabel("Score: " +"     ");
		timer = new JLabel("Timer: " +"     ");
		opponentsText = new JTextArea(20,5);
		opponentsText.setLineWrap(true);
		opponentsText.setEnabled(false);
		quitButton = new JButton("QUIT");
		quitButton.setPreferredSize(new Dimension(100, 50));
		quitButton.setBackground(Color.RED);
		JLabel curQuest = new JLabel("Current Question");
		JLabel topic = new JLabel("Topic:        ");
		topicText = new JTextArea();
		topicText.setLineWrap(true);
		topicText.setEnabled(false);
		JLabel question = new JLabel("Question: ");
		questionText = new JTextArea();
		questionText.setLineWrap(true);
		questionText.setEnabled(false);
		BUZZ = new JButton("Submit");
		JLabel emptySpace = new JLabel("-------------------------------------");
		JLabel curlySpace = new JLabel("~~~~~~~~~~~~");
		JLabel curlySpace2 = new JLabel("~~~~~~~~~~~~");
		
		answers[0] = new JRadioButton("1)");
		answers[1] = new JRadioButton("2)");
		answers[2] = new JRadioButton("3)");
		answers[3] = new JRadioButton("4)");
		answers[0].setActionCommand("0");
		answers[1].setActionCommand("1");
		answers[2].setActionCommand("2");
		answers[3].setActionCommand("3");
		questionOptions = new ButtonGroup();
		
		// add the radio buttions to the group
		for(JRadioButton btn : answers)
		{
			btn.addActionListener(new answerSlectedListener());
			questionOptions.add(btn);
		}


		//Panels for the display
		JPanel biggestPanel = new JPanel();
		JPanel headerPanel = new JPanel();
		JPanel opponentsPanel = new JPanel();
		JPanel questionPanel = new JPanel();
		JPanel smallTopicPanel = new JPanel();
		JPanel smallQuestionPanel = new JPanel();
		JPanel smallBUZZPanel = new JPanel();
		JScrollPane scroller = new JScrollPane(opponentsText);

		//Setting the layouts of the panels
		biggestPanel.setLayout(new BorderLayout());
		headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.X_AXIS));
		opponentsPanel.setLayout(new BoxLayout(opponentsPanel, BoxLayout.Y_AXIS));
		questionPanel.setLayout(new BoxLayout(questionPanel, BoxLayout.Y_AXIS));
		smallTopicPanel.setLayout(new BoxLayout(smallTopicPanel, BoxLayout.X_AXIS));
		smallQuestionPanel.setLayout(new BoxLayout(smallQuestionPanel, BoxLayout.X_AXIS));
		smallBUZZPanel.setLayout(new BorderLayout());

		//Adding the panels to the display
		frame.getContentPane().add(BorderLayout.NORTH, biggestPanel);
		biggestPanel.add(BorderLayout.NORTH, headerPanel);
		biggestPanel.add(BorderLayout.EAST, opponentsPanel);
		biggestPanel.add(BorderLayout.WEST, questionPanel);

		//Adding the components to the panels
		headerPanel.add(yourName);
		headerPanel.add(score);
		headerPanel.add(timer);

		opponentsPanel.add(opponents);
		opponentsPanel.add(nameNScore);
		opponentsPanel.add(scroller);
		opponentsPanel.add(quitButton);
		//opponentsPanel.add(opponentsText);

		questionPanel.add(curQuest);
		questionPanel.add(smallTopicPanel);
		smallTopicPanel.add(topic);
		smallTopicPanel.add(topicText);
		questionPanel.add(curlySpace);
		questionPanel.add(smallQuestionPanel);
		smallQuestionPanel.add(question);
		smallQuestionPanel.add(questionText);
		questionPanel.add(curlySpace2);
		
		for(JRadioButton btn : answers)
			questionPanel.add(btn);
		
		questionPanel.add(emptySpace);
		questionPanel.add(smallBUZZPanel);
		smallBUZZPanel.add(BorderLayout.CENTER, BUZZ);

		//Registering the components with their listeners
		BUZZ.addActionListener(new BUZZListener());
		quitButton.addActionListener(new quitListener());

		//TODO find how to disable this
		// Close everything if they close the window
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		// Make the frame starts at a non-silly size
		frame.setSize(800, 500);

		// Make it visible (otherwise you won't see it!)
		frame.setVisible(true);

	}//go()
	
	public UUID getSelectedAnswerId(){
		return selectedAnswerId;
	}

	//Get whether the user has buzzed in
	public boolean getBuzzed(){
		return buzzed;
	}//getBuzzed

	//Get which answer the user has chosen, starts at 0
	public int getAnswerChosen(){
		return answerChosen;
	}//getAnswerChosen

	//Get whether user wants to quit
	public boolean getQuit(){
		return quit;
	}//getQuit

	//Update the name for this player, starts as ""
	public void updateName(String newName){
		yourName.setText("Player: " + newName + "    ");
	}//updateName

	//Update the score displayed for this player
	public void updateScore(int newScore){
		score.setText("Score: "+newScore+"    ");
	}//updateScore

	//Update the time shown by the countdown timer
	public void updateTimer(double newTimeRem){
		timer.setText("Timer: "+newTimeRem+"    ");
	}//updateTimer

	//Update the opponents displayed by passing in an array of Playe, Score pairs
	public void updateOpponents(String[] opponents){
		opponentsText.setText("");
		for(int i = 0; i < opponents.length; i++){
			opponentsText.append(opponents[i]+"\n");
		}//for
	}//updateOpponents

	//Update the topic displayed
	public void updateTopic(String newTopic){
		topicText.setText(newTopic);
	}//updateTopic

	//Update the question displayed
	public void updateQuestion(String newQuestion, String newDifficulty){
		questionText.setText(newQuestion);
		questionText.append("\n\nDifficulty:" + newDifficulty);
	}//updateQuestion

	//Update the text of the answers on teh answer buttons
	public void updateAnswers(String[] answers){
		int x = 0;
		for(JRadioButton btn : this.answers)
			btn.setText(answers[x++]);
	}//updateAnswers
	
	//Enables or disables answer buttons, based on boolean passed in
	public void enableAnsButtons(boolean enable){
		if(enable){
			for(JRadioButton btn : answers)
				btn.setEnabled(true);
		}//if enable
		else{
			for(JRadioButton btn : answers)
				btn.setEnabled(false);
		}//else
	}//enableButtons

	//Enables or disables the buzzer button, based on boolean passed in
	public void enableBuzzer(boolean enable){
		if(enable){
			BUZZ.setEnabled(true);
		}//if
		else{
			BUZZ.setEnabled(false);
		}//else
	}//enableBuzzer
	
	public void resetVals(){
	    buzzed = false;
	    answerChosen = 0;
	    quit = false;
	    //TODO reset to initial UUID val: selectedAnswerId;
	}//resetVals

	/**
	 * 
	 * Listeners
	 *
	 */

	// Respond to buzzer
	class BUZZListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			buzzed = true;
			enableBuzzer(false);
			
			/*** Added buzzer/submit answer logic here ***/
			sys.submitAnswer(selectedAnswerId);

		}//actionPerformed
	}//FieldListener

	//Respond to quit button
	class quitListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent arg0) {
			quit = true;
			frame.setVisible(false);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			
			/*** Added leaving game code here ***/
			playerID = sys.getPlayerID();
			groupIp = sys.getGroupIp();
			sys.leaveGame(playerID, groupIp);

		}//actionPerformed
	}//quitListener
	
	class answerSlectedListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			/*** added setting the answer UUID to send here ***/
		    int index = Integer.parseInt(arg0.getActionCommand());
		    selectedAnswerId = answerIDs[index];
		    
		}
		
	}


	/**
	 * 
	 * Main method
	 * @param args
	 */

	//main method for creating an InitialDisplay object and calling its go() method
//	public static void main(String [] args)
//	{
//		int scoreInt = 0;
//		String nameStr = "Anon";
//		double timeRem = 0.0;
//		String[] oppsScores = {"Adam 2", "Claire 3", "Erin 5", "Fred 2", "Gene 2", "Harper 4", "Roger 6", "Stacy 8", 
//				"Susy 2", "Perry 5", "James 4", "Jess 5", "Rhonda 5"};
//		String topicStr = "All the things\n";
//		String questionStr = "What are all the things in the world, huh? What? What? What?....What?\n";
//		String difficultyStr = "Expert (5 points)";
//		String[] ansStr = { "For reals, like what?", "All the things are everything.", "Winning isn't everything. It's all the things.", "Things da best. Da best!"};


//		GamePlayDisplay gameDisp = new GamePlayDisplay();
//		gameDisp.go();
//		boolean enable = true;
//		gameDisp.enableAnsButtons(enable);
//		gameDisp.enableBuzzer(enable);
//		gameDisp.updateName(nameStr);
//		gameDisp.updateScore(scoreInt);
//		gameDisp.updateTimer(timeRem);
//		gameDisp.updateOpponents(oppsScores);
//		gameDisp.updateTopic(topicStr);
//		gameDisp.updateQuestion(questionStr, difficultyStr);
//		gameDisp.updateAnswers(ansStr);

//		if(gameDisp.getAnswerChosen() != 0){
//			System.out.println(gameDisp.getAnswerChosen());
//		}//if

//	}//main()

	@Override
	public void update(Observable arg0, Object arg1) {
		if(arg0 instanceof TriviaGame)
		{
		/*** Add code to manipulate the interface here ***/
		/*** Examples: load question, enable/disable interface, score update, etc ***/
		    QuestionMessage msg = ((TriviaGame) arg0).getQuestionMsg();
		    updateTopic(msg.getCategory());
		    answerIDs = msg.getAnswersId();
		    updateQuestion(msg.getQuestion(), msg.getDifficulty());
		    updateAnswers(msg.getAnswers());
		    boolean enable = !sys.getFreezeFlag();
		    enableAnsButtons(enable);
		    enableBuzzer(enable);
		    updateScore(((TriviaGame) arg0).getScore());
		}
	}

}//GamePlayDisplay
