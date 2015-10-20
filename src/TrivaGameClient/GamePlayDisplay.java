import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
public class GamePlayDisplay {

	private boolean buzzed = false;
	private int answerChosen = 0;
	private boolean quit = false;

	private JFrame frame;
	private JLabel yourName;
	private JLabel score;
	private JLabel timer;
	private JTextArea opponentsText;
	private JButton quitButton;
	private JTextArea topicText;
	private JTextArea questionText;
	private JButton BUZZ;
	private JButton ans1;
	private JButton ans2;
	private JButton ans3;
	private JButton ans4;

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
		BUZZ = new JButton("BUZZ");
		ans1 = new JButton("1) ");
		ans2 = new JButton("2)");
		ans3 = new JButton("3) ");
		ans4 = new JButton("4) ");
		JLabel emptySpace = new JLabel("-------------------------------------");
		JLabel curlySpace = new JLabel("~~~~~~~~~~~~");
		JLabel curlySpace2 = new JLabel("~~~~~~~~~~~~");



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
		questionPanel.add(smallBUZZPanel);
		smallBUZZPanel.add(BorderLayout.CENTER, BUZZ);
		questionPanel.add(emptySpace);
		questionPanel.add(ans1);
		questionPanel.add(ans2);
		questionPanel.add(ans3);
		questionPanel.add(ans4);

		//Registering the components with their listeners
		BUZZ.addActionListener(new BUZZListener());
		ans1.addActionListener(new ans1Listener());
		ans2.addActionListener(new ans2Listener());
		ans3.addActionListener(new ans3Listener());
		ans4.addActionListener(new ans4Listener());
		quitButton.addActionListener(new quitListener());

		//TODO find how to disable this
		// Close everything if they close the window
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		// Make the frame starts at a non-silly size
		frame.setSize(800, 500);

		// Make it visible (otherwise you won't see it!)
		frame.setVisible(true);

	}//go()

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
	public void updateAnswers(String ans1Str, String ans2Str, String ans3Str, String ans4Str){
		ans1.setText(ans1Str);
		ans2.setText(ans2Str);
		ans3.setText(ans3Str);
		ans4.setText(ans4Str);
	}//updateAnswers

	//Enables or disables answer buttons, based on boolean passed in
	public void enableAnsButtons(boolean enable){
		if(enable){
			ans1.setEnabled(true);
			ans2.setEnabled(true);
			ans3.setEnabled(true);
			ans4.setEnabled(true);
		}//if enable
		else{
			ans1.setEnabled(false);
			ans2.setEnabled(false);
			ans3.setEnabled(false);
			ans4.setEnabled(false);
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

		}//actionPerformed
	}//FieldListener

	//Respond to ans1 button
	class ans1Listener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent arg0) {
			answerChosen = 1;
			enableAnsButtons(false);

		}//actionPerformed
	}//ans1Listener

	//Respond to ans2 button
	class ans2Listener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent arg0) {
			answerChosen = 2;
			enableAnsButtons(false);

		}//actionPerformed
	}//ans1Listener

	//Respond to ans3 button
	class ans3Listener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent arg0) {
			answerChosen = 3;
			enableAnsButtons(false);

		}//actionPerformed
	}//ans1Listener

	//Respond to ans4 button
	class ans4Listener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent arg0) {
			answerChosen = 4;
			enableAnsButtons(false);

		}//actionPerformed
	}//ans1Listener

	//Respond to quit button
	class quitListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent arg0) {
			quit = true;
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		}//actionPerformed
	}//quitListener


	/**
	 * 
	 * Main method
	 * @param args
	 */

	//main method for creating an InitialDisplay object and calling its go() method
	public static void main(String [] args)
	{
		int scoreInt = 0;
		String nameStr = "Anon";
		double timeRem = 0.0;
		String[] oppsScores = {"Adam 2", "Claire 3", "Erin 5", "Fred 2", "Gene 2", "Harper 4", "Roger 6", "Stacy 8", 
				"Susy 2", "Perry 5", "James 4", "Jess 5", "Rhonda 5"};
		String topicStr = "All the things\n";
		String questionStr = "What are all the things in the world, huh? What? What? What?....What?\n";
		String difficultyStr = "Expert (5 points)";
		String ans1Str = "For reals, like what?";
		String ans2Str = "All the things are everything.";
		String ans3Str = "Winning isn't everything. It's all the things.";
		String ans4Str = "Things da best. Da best!";


		GamePlayDisplay gameDisp = new GamePlayDisplay();
		gameDisp.go();
		gameDisp.enableAnsButtons(true);
		gameDisp.enableBuzzer(true);
		gameDisp.updateName(nameStr);
		gameDisp.updateScore(scoreInt);
		gameDisp.updateTimer(timeRem);
		gameDisp.updateOpponents(oppsScores);
		gameDisp.updateTopic(topicStr);
		gameDisp.updateQuestion(questionStr, difficultyStr);
		gameDisp.updateAnswers(ans1Str, ans2Str, ans3Str, ans4Str);

		if(gameDisp.getAnswerChosen() != 0){
			System.out.println(gameDisp.getAnswerChosen());
		}//if

	}//main()

}//GamePlayDisplay
