package TrivaGameClient;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JTextPane;
import javax.swing.JButton;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.UUID;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

public class TriviaGameDisplay extends JFrame implements Observer {
	private int selectedAnsIndex = -1;
	private String catagory, questionText;
	private String[] answers;
	private UUID[] answerIds;
	private TriviaGame sys;
	
	private static final long serialVersionUID = 1L;
	
	private JPanel contentPane;
	private final ButtonGroup answerGroup = new ButtonGroup();
	private JLabel lblScore, lblTimeLeft, lblCatagory;
	private JTextPane txtpnQuestionInfo;
	private JButton btnSubmitAns;
	private List<JRadioButton> answerSelection = new ArrayList<JRadioButton>();

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TriviaGameDisplay frame = new TriviaGameDisplay();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	private TriviaGameDisplay() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				quitGame();
			}
		});
		
		setAlwaysOnTop(true);
		setResizable(false);
		setTitle("Trivia Game");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 569, 380);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBounds(10, 11, 543, 23);
		contentPane.add(panel);
		panel.setLayout(new BorderLayout(0, 0));
		
		JLabel lblPlayerName = new JLabel("Player: ");
		panel.add(lblPlayerName, BorderLayout.WEST);
		
		lblScore = new JLabel("Score: ");
		panel.add(lblScore, BorderLayout.EAST);
		
		lblTimeLeft = new JLabel("Time Left: ");
		panel.add(lblTimeLeft, BorderLayout.CENTER);
		
		lblCatagory = new JLabel("Category: ");
		lblCatagory.setBounds(10, 45, 543, 14);
		contentPane.add(lblCatagory);
		
		txtpnQuestionInfo = new JTextPane();
		txtpnQuestionInfo.setEditable(false);
		txtpnQuestionInfo.setBounds(10, 70, 543, 125);
		contentPane.add(txtpnQuestionInfo);
		
		JRadioButton rdbtnAnswer = new JRadioButton("Answer 1");
		rdbtnAnswer.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				if(arg0.getStateChange() == ItemEvent.SELECTED)
					selectedAnsIndex = 0;
			}
		});
		answerSelection.add(rdbtnAnswer);
		answerGroup.add(rdbtnAnswer);
		rdbtnAnswer.setBounds(6, 202, 272, 23);
		contentPane.add(rdbtnAnswer);
		
		JRadioButton rdbtnAnswer_1 = new JRadioButton("Answer 2");
		rdbtnAnswer_1.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				if(arg0.getStateChange() == ItemEvent.SELECTED)
					selectedAnsIndex = 1;
			}
		});
		answerSelection.add(rdbtnAnswer_1);
		answerGroup.add(rdbtnAnswer_1);
		rdbtnAnswer_1.setBounds(6, 228, 272, 23);
		contentPane.add(rdbtnAnswer_1);
		
		JRadioButton rdbtnAnswer_2 = new JRadioButton("Answer 3");
		rdbtnAnswer_2.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				if(arg0.getStateChange() == ItemEvent.SELECTED)
					selectedAnsIndex = 2;
			}
		});
		answerSelection.add(rdbtnAnswer_2);
		answerGroup.add(rdbtnAnswer_2);
		rdbtnAnswer_2.setBounds(6, 254, 272, 23);
		contentPane.add(rdbtnAnswer_2);
		
		JRadioButton rdbtnAnswer_3 = new JRadioButton("Answer 4");
		rdbtnAnswer_3.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				if(arg0.getStateChange() == ItemEvent.SELECTED)
					selectedAnsIndex = 3;
			}
		});
		answerSelection.add(rdbtnAnswer_3);
		answerGroup.add(rdbtnAnswer_3);
		rdbtnAnswer_3.setBounds(6, 280, 272, 23);
		contentPane.add(rdbtnAnswer_3);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBounds(10, 310, 543, 30);
		contentPane.add(panel_1);
		panel_1.setLayout(null);
		
		btnSubmitAns = new JButton("Submit Answer");
		btnSubmitAns.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				submitAnswer();
			}
		});
		btnSubmitAns.setBounds(0, 0, 391, 30);
		panel_1.add(btnSubmitAns);
		
		JLabel label = new JLabel("");
		label.setBounds(181, 0, 181, 30);
		panel_1.add(label);
		
		JLabel label_1 = new JLabel("");
		label_1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				closeForm();
			}
		});
		label_1.setBounds(401, 0, 142, 30);
		panel_1.add(label_1);
		
		JButton btnQuit = new JButton("Quit");
		btnQuit.setBounds(401, 0, 142, 30);
		panel_1.add(btnQuit);
	}
	
	public TriviaGameDisplay(TriviaGame sys)
	{
		this();
		this.sys = sys;
		this.sys.addObserver(this);
	}
	
	private void displayQuestion()
	{
		setAnswerText();
		btnSubmitAns.setEnabled(true);
	}
	
	private void quitGame()
	{
		sys.leaveGame();
	}
	
	private void closeForm()
	{
		quitGame();
		this.dispose();
	}
	
	private void submitAnswer()
	{		
		if(selectedAnsIndex > -1)
		{
			disableInterface();
			sys.submitAnswer(answerIds[selectedAnsIndex]);
		}
	}
	
	private void disableInterface()
	{
		btnSubmitAns.setEnabled(false);
	}
	
	private void setAnswerText()
	{
		lblCatagory.setText(catagory);
		txtpnQuestionInfo.setText(questionText);
		
		for(int index = 0; index < answers.length; index++)
			answerSelection.get(index).setText(answers[index]);
	}
	

	@Override
	public void update(Observable arg0, Object arg1) 
	{
		if(arg0 instanceof TriviaGame)
		{
			TriviaGame gameSystem = ((TriviaGame) arg0);
		    QuestionMessage msg = gameSystem.getQuestionMsg();
		    
		    if(msg != null)
		    {
			    catagory = msg.getCategory();
			    answerIds = msg.getAnswersId();
			    questionText = (msg.getQuestion() + "\n\nDifficulty:" + msg.getDifficulty());
			    answers = msg.getAnswers();
		    }
		    
		    if(!gameSystem.getFreezeFlag())
		    	displayQuestion();
		    else
		    	disableInterface();

		    lblScore.setText("Score: " + gameSystem.getScore());
		}
		
	}
}
