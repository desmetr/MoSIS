package train;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import train.traincontroller.ITrainControllerStatemachine.SCInterfaceListener;
import train.traincontroller.ITrainControllerStatemachine.SCInterfaceOperationCallback;
import train.traincontroller.TrainControllerStatemachine;

public class TrainDemo extends JFrame {

	private static final long serialVersionUID = -8909693541678814631L;

	protected TrainControllerStatemachine statemachine;

	private InfoPanel infoPanel;
	private RailwayPanel railwayPanel;
	private ButtonPanel buttonPanel;
	
	private double velocity;
	private double travelled;
	private int travelled_int = 0;

	public static void main(String[] args) {
		TrainDemo application = new TrainDemo();
		application.addWindowListener(new WindowAdapter() {			
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		application.createContents();
		application.setupStatemachine();
		application.run();
	}
	
	protected void createContents() {		
		setTitle("Railway");
		setSize(1000, 325);
		setResizable(false);
		
		setLayout(new BorderLayout());
		
		infoPanel = new InfoPanel();
		getContentPane().add(BorderLayout.NORTH, infoPanel);
		buttonPanel = new ButtonPanel();
		getContentPane().add(BorderLayout.SOUTH, buttonPanel);
		railwayPanel = new RailwayPanel();
		getContentPane().add(BorderLayout.CENTER, railwayPanel);
		
		setVisible(true);
	}

	public void setupStatemachine() {
		statemachine = new TrainControllerStatemachine();
		railwayPanel.setStatemachine(statemachine);
		statemachine.setTimer(new TimerService());
		statemachine.getSCInterface().getListeners().add(new SCInterfaceListener() {
			
			@Override
			public void onWarningRaised(String value) {
				buttonPanel.setInfoMessage(value, "WARNING");
			}
			
			@Override
			public void onErrorRaised(String value) {
				buttonPanel.setInfoMessage(value, "ERROR");
			}
			
			@Override
			public void onCloseDoorsRaised() {
				infoPanel.setDoorsMessage("Doors are CLOSED");
			}
			
			@Override
			public void onOpenDoorsRaised() {
				infoPanel.setDoorsMessage("Doors are OPEN");
			}

			@Override
			public void onClearWarningRaised() {
				buttonPanel.clearInfoMessage();
			}
		});
		
		statemachine.getSCInterface().setSCInterfaceOperationCallback(new SCInterfaceOperationCallback() {
			
			@Override
			public void updateGUI() {
				velocity = statemachine.getSCInterface().getVelocity();
				
				travelled += velocity / 20.0;
				int delta_x = -(int) (travelled - travelled_int);
				travelled_int = (int) travelled;
				railwayPanel.setDelta(delta_x);
				railwayPanel.setTravelledX(travelled);
				
				infoPanel.setVelocityMessage(velocity);
				
				repaint();
			}

			@Override
			public void print(String msg) {
				System.out.println(msg);
			}
		});
		
		buttonPanel.getContinuebtn()
			.addActionListener(e -> statemachine.getSCInterface().raiseContinue());
		buttonPanel.getPause()
			.addActionListener(e -> statemachine.getSCInterface().raisePause());
		buttonPanel.getOpen()
			.addActionListener(e -> statemachine.getSCInterface().raiseOpen());
		buttonPanel.getClose()
			.addActionListener(e -> statemachine.getSCInterface().raiseClose());
		buttonPanel.getPoll()
			.addActionListener(e -> statemachine.getSCInterface().raiseAwake());
		buttonPanel.getSpeed().addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				statemachine.getSCInterface().raiseUpdate_acceleration(buttonPanel.getSpeed().getValue() / 100.0);
			}
		});
		
		statemachine.init();
	}
	
	protected void run() {
		statemachine.enter();
		RuntimeService.getInstance().registerStatemachine(statemachine, 1);
	}
}
