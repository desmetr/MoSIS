package train.traincontroller;
import java.util.LinkedList;
import java.util.List;
import train.ITimer;

public class TrainControllerStatemachine implements ITrainControllerStatemachine {

	protected class SCInterfaceImpl implements SCInterface {
	
		private List<SCInterfaceListener> listeners = new LinkedList<SCInterfaceListener>();
		
		public List<SCInterfaceListener> getListeners() {
			return listeners;
		}
		private SCInterfaceOperationCallback operationCallback;
		
		public void setSCInterfaceOperationCallback(
				SCInterfaceOperationCallback operationCallback) {
			this.operationCallback = operationCallback;
		}
		private boolean close;
		
		public void raiseClose() {
			close = true;
		}
		
		private boolean open;
		
		public void raiseOpen() {
			open = true;
		}
		
		private boolean leave;
		
		public void raiseLeave() {
			leave = true;
		}
		
		private boolean enter;
		
		public void raiseEnter() {
			enter = true;
		}
		
		private boolean pause;
		
		public void raisePause() {
			pause = true;
		}
		
		private boolean continueEvent;
		
		public void raiseContinue() {
			continueEvent = true;
		}
		
		private boolean awake;
		
		public void raiseAwake() {
			awake = true;
		}
		
		private boolean yellow_light;
		
		public void raiseYellow_light() {
			yellow_light = true;
		}
		
		private boolean red_light;
		
		public void raiseRed_light() {
			red_light = true;
		}
		
		private boolean green_light;
		
		public void raiseGreen_light() {
			green_light = true;
		}
		
		private boolean update_acceleration;
		
		private double update_accelerationValue;
		
		public void raiseUpdate_acceleration(double value) {
			update_acceleration = true;
			update_accelerationValue = value;
		}
		
		protected double getUpdate_accelerationValue() {
			if (! update_acceleration ) 
				throw new IllegalStateException("Illegal event value access. Event Update_acceleration is not raised!");
			return update_accelerationValue;
		}
		
		private boolean closeDoors;
		
		public boolean isRaisedCloseDoors() {
			return closeDoors;
		}
		
		protected void raiseCloseDoors() {
			closeDoors = true;
			for (SCInterfaceListener listener : listeners) {
				listener.onCloseDoorsRaised();
			}
		}
		
		private boolean openDoors;
		
		public boolean isRaisedOpenDoors() {
			return openDoors;
		}
		
		protected void raiseOpenDoors() {
			openDoors = true;
			for (SCInterfaceListener listener : listeners) {
				listener.onOpenDoorsRaised();
			}
		}
		
		private boolean error;
		
		private String errorValue;
		
		public boolean isRaisedError() {
			return error;
		}
		
		protected void raiseError(String value) {
			error = true;
			errorValue = value;
			for (SCInterfaceListener listener : listeners) {
				listener.onErrorRaised(value);
			}
		}
		
		public String getErrorValue() {
			if (! error ) 
				throw new IllegalStateException("Illegal event value access. Event Error is not raised!");
			return errorValue;
		}
		
		private boolean warning;
		
		private String warningValue;
		
		public boolean isRaisedWarning() {
			return warning;
		}
		
		protected void raiseWarning(String value) {
			warning = true;
			warningValue = value;
			for (SCInterfaceListener listener : listeners) {
				listener.onWarningRaised(value);
			}
		}
		
		public String getWarningValue() {
			if (! warning ) 
				throw new IllegalStateException("Illegal event value access. Event Warning is not raised!");
			return warningValue;
		}
		
		private boolean clearWarning;
		
		public boolean isRaisedClearWarning() {
			return clearWarning;
		}
		
		protected void raiseClearWarning() {
			clearWarning = true;
			for (SCInterfaceListener listener : listeners) {
				listener.onClearWarningRaised();
			}
		}
		
		private double velocity;
		
		public double getVelocity() {
			return velocity;
		}
		
		public void setVelocity(double value) {
			this.velocity = value;
		}
		
		protected void clearEvents() {
			close = false;
			open = false;
			leave = false;
			enter = false;
			pause = false;
			continueEvent = false;
			awake = false;
			yellow_light = false;
			red_light = false;
			green_light = false;
			update_acceleration = false;
		}
		protected void clearOutEvents() {
		
		closeDoors = false;
		openDoors = false;
		error = false;
		warning = false;
		clearWarning = false;
		}
		
	}
	
	protected SCInterfaceImpl sCInterface;
	
	private boolean initialized = false;
	
	public enum State {
		main_default,
		main_main,
		main_main_train_trainState,
		main_main_train_trainState_movement_train_Still,
		main_main_train_trainState_movement_train_Driving,
		main_main_train_trainState_movement_train_Cruising,
		main_main_train_trainState_movement_train_Breaking,
		main_main_train_trainState_movement_train_Cooldown,
		main_main_train_trainState_Place_train_InStation,
		main_main_train_trainState_Place_train_OutOfStation,
		main_main_train_trainState_Doors_station_openingDoors,
		main_main_train_trainState_Doors_station_ClosedDoors,
		main_main_train_trainState_Doors_station_openDoors,
		main_main_train_trainState_Doors_station_ClosingDoors,
		main_main_train_trainState_Last_light_NotYellow,
		main_main_train_trainState_Last_light_Yellow,
		main_main_train_trainState_Last_light_YellowAndBreaking,
		main_main_dead_man_s_button_Normal,
		main_main_dead_man_s_button_Button_Prompted,
		main_main_dead_man_s_button_Emergency_Brake_Mode,
		$NullState$
	};
	
	private State[] historyVector = new State[5];
	private final State[] stateVector = new State[5];
	
	private int nextStateIndex;
	
	private ITimer timer;
	
	private final boolean[] timeEvents = new boolean[6];
	
	private boolean emergency;
	
	private boolean toClosedDoors;
	private double acceleration;
	
	protected void setAcceleration(double value) {
		acceleration = value;
	}
	
	protected double getAcceleration() {
		return acceleration;
	}
	
	public TrainControllerStatemachine() {
		sCInterface = new SCInterfaceImpl();
	}
	
	public void init() {
		this.initialized = true;
		if (timer == null) {
			throw new IllegalStateException("timer not set.");
		}
		if (this.sCInterface.operationCallback == null) {
			throw new IllegalStateException("Operation callback for interface sCInterface must be set.");
		}
		
		for (int i = 0; i < 5; i++) {
			stateVector[i] = State.$NullState$;
		}
		for (int i = 0; i < 5; i++) {
			historyVector[i] = State.$NullState$;
		}
		clearEvents();
		clearOutEvents();
		sCInterface.setVelocity(0.0);
		
		setAcceleration(0.0);
	}
	
	public void enter() {
		if (!initialized) {
			throw new IllegalStateException(
					"The state machine needs to be initialized first by calling the init() function.");
		}
		if (timer == null) {
			throw new IllegalStateException("timer not set.");
		}
		entryAction();
		enterSequence_main_default();
	}
	
	public void exit() {
		exitSequence_main();
		exitAction();
	}
	
	/**
	 * @see IStatemachine#isActive()
	 */
	public boolean isActive() {
		return stateVector[0] != State.$NullState$||stateVector[1] != State.$NullState$||stateVector[2] != State.$NullState$||stateVector[3] != State.$NullState$||stateVector[4] != State.$NullState$;
	}
	
	/** 
	* Always returns 'false' since this state machine can never become final.
	*
	* @see IStatemachine#isFinal()
	*/
	public boolean isFinal() {
		return false;
	}
	/**
	* This method resets the incoming events (time events included).
	*/
	protected void clearEvents() {
		sCInterface.clearEvents();
		emergency = false;
		toClosedDoors = false;
		for (int i=0; i<timeEvents.length; i++) {
			timeEvents[i] = false;
		}
	}
	
	/**
	* This method resets the outgoing events.
	*/
	protected void clearOutEvents() {
		sCInterface.clearOutEvents();
	}
	
	/**
	* Returns true if the given state is currently active otherwise false.
	*/
	public boolean isStateActive(State state) {
	
		switch (state) {
		case main_default:
			return stateVector[0] == State.main_default;
		case main_main:
			return stateVector[0].ordinal() >= State.
					main_main.ordinal()&& stateVector[0].ordinal() <= State.main_main_dead_man_s_button_Emergency_Brake_Mode.ordinal();
		case main_main_train_trainState:
			return stateVector[0].ordinal() >= State.
					main_main_train_trainState.ordinal()&& stateVector[0].ordinal() <= State.main_main_train_trainState_Last_light_YellowAndBreaking.ordinal();
		case main_main_train_trainState_movement_train_Still:
			return stateVector[0] == State.main_main_train_trainState_movement_train_Still;
		case main_main_train_trainState_movement_train_Driving:
			return stateVector[0] == State.main_main_train_trainState_movement_train_Driving;
		case main_main_train_trainState_movement_train_Cruising:
			return stateVector[0] == State.main_main_train_trainState_movement_train_Cruising;
		case main_main_train_trainState_movement_train_Breaking:
			return stateVector[0] == State.main_main_train_trainState_movement_train_Breaking;
		case main_main_train_trainState_movement_train_Cooldown:
			return stateVector[0] == State.main_main_train_trainState_movement_train_Cooldown;
		case main_main_train_trainState_Place_train_InStation:
			return stateVector[1] == State.main_main_train_trainState_Place_train_InStation;
		case main_main_train_trainState_Place_train_OutOfStation:
			return stateVector[1] == State.main_main_train_trainState_Place_train_OutOfStation;
		case main_main_train_trainState_Doors_station_openingDoors:
			return stateVector[2] == State.main_main_train_trainState_Doors_station_openingDoors;
		case main_main_train_trainState_Doors_station_ClosedDoors:
			return stateVector[2] == State.main_main_train_trainState_Doors_station_ClosedDoors;
		case main_main_train_trainState_Doors_station_openDoors:
			return stateVector[2] == State.main_main_train_trainState_Doors_station_openDoors;
		case main_main_train_trainState_Doors_station_ClosingDoors:
			return stateVector[2] == State.main_main_train_trainState_Doors_station_ClosingDoors;
		case main_main_train_trainState_Last_light_NotYellow:
			return stateVector[3] == State.main_main_train_trainState_Last_light_NotYellow;
		case main_main_train_trainState_Last_light_Yellow:
			return stateVector[3] == State.main_main_train_trainState_Last_light_Yellow;
		case main_main_train_trainState_Last_light_YellowAndBreaking:
			return stateVector[3] == State.main_main_train_trainState_Last_light_YellowAndBreaking;
		case main_main_dead_man_s_button_Normal:
			return stateVector[4] == State.main_main_dead_man_s_button_Normal;
		case main_main_dead_man_s_button_Button_Prompted:
			return stateVector[4] == State.main_main_dead_man_s_button_Button_Prompted;
		case main_main_dead_man_s_button_Emergency_Brake_Mode:
			return stateVector[4] == State.main_main_dead_man_s_button_Emergency_Brake_Mode;
		default:
			return false;
		}
	}
	
	/**
	* Set the {@link ITimer} for the state machine. It must be set
	* externally on a timed state machine before a run cycle can be correct
	* executed.
	* 
	* @param timer
	*/
	public void setTimer(ITimer timer) {
		this.timer = timer;
	}
	
	/**
	* Returns the currently used timer.
	* 
	* @return {@link ITimer}
	*/
	public ITimer getTimer() {
		return timer;
	}
	
	public void timeElapsed(int eventID) {
		timeEvents[eventID] = true;
	}
	
	public SCInterface getSCInterface() {
		return sCInterface;
	}
	
	private void raiseEmergency() {
		emergency = true;
	}
	
	
	private void raiseToClosedDoors() {
		toClosedDoors = true;
	}
	
	
	public void raiseClose() {
		sCInterface.raiseClose();
	}
	
	public void raiseOpen() {
		sCInterface.raiseOpen();
	}
	
	public void raiseLeave() {
		sCInterface.raiseLeave();
	}
	
	public void raiseEnter() {
		sCInterface.raiseEnter();
	}
	
	public void raisePause() {
		sCInterface.raisePause();
	}
	
	public void raiseContinue() {
		sCInterface.raiseContinue();
	}
	
	public void raiseAwake() {
		sCInterface.raiseAwake();
	}
	
	public void raiseYellow_light() {
		sCInterface.raiseYellow_light();
	}
	
	public void raiseRed_light() {
		sCInterface.raiseRed_light();
	}
	
	public void raiseGreen_light() {
		sCInterface.raiseGreen_light();
	}
	
	public void raiseUpdate_acceleration(double value) {
		sCInterface.raiseUpdate_acceleration(value);
	}
	
	public boolean isRaisedCloseDoors() {
		return sCInterface.isRaisedCloseDoors();
	}
	
	public boolean isRaisedOpenDoors() {
		return sCInterface.isRaisedOpenDoors();
	}
	
	public boolean isRaisedError() {
		return sCInterface.isRaisedError();
	}
	
	public String getErrorValue() {
		return sCInterface.getErrorValue();
	}
	
	public boolean isRaisedWarning() {
		return sCInterface.isRaisedWarning();
	}
	
	public String getWarningValue() {
		return sCInterface.getWarningValue();
	}
	
	public boolean isRaisedClearWarning() {
		return sCInterface.isRaisedClearWarning();
	}
	
	public double getVelocity() {
		return sCInterface.getVelocity();
	}
	
	public void setVelocity(double value) {
		sCInterface.setVelocity(value);
	}
	
	/* Entry action for statechart 'TrainController'. */
	private void entryAction() {
		timer.setTimer(this, 5, 20, true);
	}
	
	/* Entry action for state 'default'. */
	private void entryAction_main_default() {
		sCInterface.operationCallback.updateGUI();
	}
	
	/* Entry action for state 'Still'. */
	private void entryAction_main_main_train_trainState_movement_train_Still() {
		sCInterface.operationCallback.print("still");
		
		sCInterface.setVelocity(0.0);
		
		setAcceleration(0.0);
	}
	
	/* Entry action for state 'Driving'. */
	private void entryAction_main_main_train_trainState_movement_train_Driving() {
		sCInterface.operationCallback.print("driving");
		
		setAcceleration(sCInterface.getUpdate_accelerationValue());
	}
	
	/* Entry action for state 'Cruising'. */
	private void entryAction_main_main_train_trainState_movement_train_Cruising() {
		sCInterface.operationCallback.print("cruising");
		
		sCInterface.setVelocity(100);
		
		setAcceleration(0);
	}
	
	/* Entry action for state 'Breaking'. */
	private void entryAction_main_main_train_trainState_movement_train_Breaking() {
		sCInterface.operationCallback.print("Breaking");
		
		setAcceleration(-1.0);
		
		sCInterface.operationCallback.updateGUI();
	}
	
	/* Entry action for state 'Cooldown'. */
	private void entryAction_main_main_train_trainState_movement_train_Cooldown() {
		timer.setTimer(this, 0, 5 * 1000, false);
		
		sCInterface.operationCallback.print("Cooldown");
	}
	
	/* Entry action for state 'InStation'. */
	private void entryAction_main_main_train_trainState_Place_train_InStation() {
		sCInterface.operationCallback.print("Entered station");
	}
	
	/* Entry action for state 'OutOfStation'. */
	private void entryAction_main_main_train_trainState_Place_train_OutOfStation() {
		sCInterface.operationCallback.print("Left station");
	}
	
	/* Entry action for state 'openingDoors'. */
	private void entryAction_main_main_train_trainState_Doors_station_openingDoors() {
		timer.setTimer(this, 1, 5 * 1000, false);
		
		sCInterface.operationCallback.print("openingDoors");
		
		sCInterface.raiseOpenDoors();
	}
	
	/* Entry action for state 'ClosedDoors'. */
	private void entryAction_main_main_train_trainState_Doors_station_ClosedDoors() {
		sCInterface.operationCallback.print("ClosedDoors");
	}
	
	/* Entry action for state 'openDoors'. */
	private void entryAction_main_main_train_trainState_Doors_station_openDoors() {
		sCInterface.operationCallback.print("Doors closable");
	}
	
	/* Entry action for state 'ClosingDoors'. */
	private void entryAction_main_main_train_trainState_Doors_station_ClosingDoors() {
		sCInterface.operationCallback.print("Closing doors");
		
		sCInterface.raiseCloseDoors();
	}
	
	/* Entry action for state 'NotYellow'. */
	private void entryAction_main_main_train_trainState_Last_light_NotYellow() {
		sCInterface.operationCallback.print("NotYellow");
	}
	
	/* Entry action for state 'Yellow'. */
	private void entryAction_main_main_train_trainState_Last_light_Yellow() {
		sCInterface.operationCallback.print("Yellow");
	}
	
	/* Entry action for state 'Normal'. */
	private void entryAction_main_main_dead_man_s_button_Normal() {
		timer.setTimer(this, 2, 30 * 1000, true);
		
		sCInterface.raiseClearWarning();
		
		sCInterface.operationCallback.updateGUI();
	}
	
	/* Entry action for state 'Button Prompted'. */
	private void entryAction_main_main_dead_man_s_button_Button_Prompted() {
		timer.setTimer(this, 3, 5 * 1000, false);
		
		sCInterface.raiseWarning("Press the POLL button!");
		
		sCInterface.operationCallback.updateGUI();
	}
	
	/* Entry action for state 'Emergency Brake Mode'. */
	private void entryAction_main_main_dead_man_s_button_Emergency_Brake_Mode() {
		timer.setTimer(this, 4, 5 * 1000, false);
		
		sCInterface.operationCallback.print("User not reactive");
	}
	
	/* Exit action for state 'TrainController'. */
	private void exitAction() {
		timer.unsetTimer(this, 5);
	}
	
	/* Exit action for state 'Cooldown'. */
	private void exitAction_main_main_train_trainState_movement_train_Cooldown() {
		timer.unsetTimer(this, 0);
	}
	
	/* Exit action for state 'openingDoors'. */
	private void exitAction_main_main_train_trainState_Doors_station_openingDoors() {
		timer.unsetTimer(this, 1);
	}
	
	/* Exit action for state 'Normal'. */
	private void exitAction_main_main_dead_man_s_button_Normal() {
		timer.unsetTimer(this, 2);
	}
	
	/* Exit action for state 'Button Prompted'. */
	private void exitAction_main_main_dead_man_s_button_Button_Prompted() {
		timer.unsetTimer(this, 3);
	}
	
	/* Exit action for state 'Emergency Brake Mode'. */
	private void exitAction_main_main_dead_man_s_button_Emergency_Brake_Mode() {
		timer.unsetTimer(this, 4);
	}
	
	/* 'default' enter sequence for state default */
	private void enterSequence_main_default_default() {
		entryAction_main_default();
		nextStateIndex = 0;
		stateVector[0] = State.main_default;
	}
	
	/* 'default' enter sequence for state trainState */
	private void enterSequence_main_main_train_trainState_default() {
		enterSequence_main_main_train_trainState_movement_train_default();
		enterSequence_main_main_train_trainState_Place_train_default();
		enterSequence_main_main_train_trainState_Doors_station_default();
		enterSequence_main_main_train_trainState_Last_light_default();
		historyVector[0] = stateVector[0];
	}
	
	/* 'default' enter sequence for state Still */
	private void enterSequence_main_main_train_trainState_movement_train_Still_default() {
		entryAction_main_main_train_trainState_movement_train_Still();
		nextStateIndex = 0;
		stateVector[0] = State.main_main_train_trainState_movement_train_Still;
		
		historyVector[1] = stateVector[0];
	}
	
	/* 'default' enter sequence for state Driving */
	private void enterSequence_main_main_train_trainState_movement_train_Driving_default() {
		entryAction_main_main_train_trainState_movement_train_Driving();
		nextStateIndex = 0;
		stateVector[0] = State.main_main_train_trainState_movement_train_Driving;
		
		historyVector[1] = stateVector[0];
	}
	
	/* 'default' enter sequence for state Cruising */
	private void enterSequence_main_main_train_trainState_movement_train_Cruising_default() {
		entryAction_main_main_train_trainState_movement_train_Cruising();
		nextStateIndex = 0;
		stateVector[0] = State.main_main_train_trainState_movement_train_Cruising;
		
		historyVector[1] = stateVector[0];
	}
	
	/* 'default' enter sequence for state Breaking */
	private void enterSequence_main_main_train_trainState_movement_train_Breaking_default() {
		entryAction_main_main_train_trainState_movement_train_Breaking();
		nextStateIndex = 0;
		stateVector[0] = State.main_main_train_trainState_movement_train_Breaking;
		
		historyVector[1] = stateVector[0];
	}
	
	/* 'default' enter sequence for state Cooldown */
	private void enterSequence_main_main_train_trainState_movement_train_Cooldown_default() {
		entryAction_main_main_train_trainState_movement_train_Cooldown();
		nextStateIndex = 0;
		stateVector[0] = State.main_main_train_trainState_movement_train_Cooldown;
		
		historyVector[1] = stateVector[0];
	}
	
	/* 'default' enter sequence for state InStation */
	private void enterSequence_main_main_train_trainState_Place_train_InStation_default() {
		entryAction_main_main_train_trainState_Place_train_InStation();
		nextStateIndex = 1;
		stateVector[1] = State.main_main_train_trainState_Place_train_InStation;
		
		historyVector[2] = stateVector[1];
	}
	
	/* 'default' enter sequence for state OutOfStation */
	private void enterSequence_main_main_train_trainState_Place_train_OutOfStation_default() {
		entryAction_main_main_train_trainState_Place_train_OutOfStation();
		nextStateIndex = 1;
		stateVector[1] = State.main_main_train_trainState_Place_train_OutOfStation;
		
		historyVector[2] = stateVector[1];
	}
	
	/* 'default' enter sequence for state openingDoors */
	private void enterSequence_main_main_train_trainState_Doors_station_openingDoors_default() {
		entryAction_main_main_train_trainState_Doors_station_openingDoors();
		nextStateIndex = 2;
		stateVector[2] = State.main_main_train_trainState_Doors_station_openingDoors;
		
		historyVector[3] = stateVector[2];
	}
	
	/* 'default' enter sequence for state ClosedDoors */
	private void enterSequence_main_main_train_trainState_Doors_station_ClosedDoors_default() {
		entryAction_main_main_train_trainState_Doors_station_ClosedDoors();
		nextStateIndex = 2;
		stateVector[2] = State.main_main_train_trainState_Doors_station_ClosedDoors;
		
		historyVector[3] = stateVector[2];
	}
	
	/* 'default' enter sequence for state openDoors */
	private void enterSequence_main_main_train_trainState_Doors_station_openDoors_default() {
		entryAction_main_main_train_trainState_Doors_station_openDoors();
		nextStateIndex = 2;
		stateVector[2] = State.main_main_train_trainState_Doors_station_openDoors;
		
		historyVector[3] = stateVector[2];
	}
	
	/* 'default' enter sequence for state ClosingDoors */
	private void enterSequence_main_main_train_trainState_Doors_station_ClosingDoors_default() {
		entryAction_main_main_train_trainState_Doors_station_ClosingDoors();
		nextStateIndex = 2;
		stateVector[2] = State.main_main_train_trainState_Doors_station_ClosingDoors;
		
		historyVector[3] = stateVector[2];
	}
	
	/* 'default' enter sequence for state NotYellow */
	private void enterSequence_main_main_train_trainState_Last_light_NotYellow_default() {
		entryAction_main_main_train_trainState_Last_light_NotYellow();
		nextStateIndex = 3;
		stateVector[3] = State.main_main_train_trainState_Last_light_NotYellow;
		
		historyVector[4] = stateVector[3];
	}
	
	/* 'default' enter sequence for state Yellow */
	private void enterSequence_main_main_train_trainState_Last_light_Yellow_default() {
		entryAction_main_main_train_trainState_Last_light_Yellow();
		nextStateIndex = 3;
		stateVector[3] = State.main_main_train_trainState_Last_light_Yellow;
		
		historyVector[4] = stateVector[3];
	}
	
	/* 'default' enter sequence for state YellowAndBreaking */
	private void enterSequence_main_main_train_trainState_Last_light_YellowAndBreaking_default() {
		nextStateIndex = 3;
		stateVector[3] = State.main_main_train_trainState_Last_light_YellowAndBreaking;
		
		historyVector[4] = stateVector[3];
	}
	
	/* 'default' enter sequence for state Normal */
	private void enterSequence_main_main_dead_man_s_button_Normal_default() {
		entryAction_main_main_dead_man_s_button_Normal();
		nextStateIndex = 4;
		stateVector[4] = State.main_main_dead_man_s_button_Normal;
	}
	
	/* 'default' enter sequence for state Button Prompted */
	private void enterSequence_main_main_dead_man_s_button_Button_Prompted_default() {
		entryAction_main_main_dead_man_s_button_Button_Prompted();
		nextStateIndex = 4;
		stateVector[4] = State.main_main_dead_man_s_button_Button_Prompted;
	}
	
	/* 'default' enter sequence for state Emergency Brake Mode */
	private void enterSequence_main_main_dead_man_s_button_Emergency_Brake_Mode_default() {
		entryAction_main_main_dead_man_s_button_Emergency_Brake_Mode();
		nextStateIndex = 4;
		stateVector[4] = State.main_main_dead_man_s_button_Emergency_Brake_Mode;
	}
	
	/* 'default' enter sequence for region main */
	private void enterSequence_main_default() {
		react_main__entry_Default();
	}
	
	/* deep enterSequence with history in child train */
	private void deepEnterSequence_main_main_train() {
		switch (historyVector[0]) {
		case main_main_train_trainState_movement_train_Still:
			deepEnterSequence_main_main_train_trainState_movement_train();
			deepEnterSequence_main_main_train_trainState_Place_train();
			deepEnterSequence_main_main_train_trainState_Doors_station();
			deepEnterSequence_main_main_train_trainState_Last_light();
			break;
		case main_main_train_trainState_movement_train_Driving:
			deepEnterSequence_main_main_train_trainState_movement_train();
			deepEnterSequence_main_main_train_trainState_Place_train();
			deepEnterSequence_main_main_train_trainState_Doors_station();
			deepEnterSequence_main_main_train_trainState_Last_light();
			break;
		case main_main_train_trainState_movement_train_Cruising:
			deepEnterSequence_main_main_train_trainState_movement_train();
			deepEnterSequence_main_main_train_trainState_Place_train();
			deepEnterSequence_main_main_train_trainState_Doors_station();
			deepEnterSequence_main_main_train_trainState_Last_light();
			break;
		case main_main_train_trainState_movement_train_Breaking:
			deepEnterSequence_main_main_train_trainState_movement_train();
			deepEnterSequence_main_main_train_trainState_Place_train();
			deepEnterSequence_main_main_train_trainState_Doors_station();
			deepEnterSequence_main_main_train_trainState_Last_light();
			break;
		case main_main_train_trainState_movement_train_Cooldown:
			deepEnterSequence_main_main_train_trainState_movement_train();
			deepEnterSequence_main_main_train_trainState_Place_train();
			deepEnterSequence_main_main_train_trainState_Doors_station();
			deepEnterSequence_main_main_train_trainState_Last_light();
			break;
		default:
			break;
		}
	}
	
	/* 'default' enter sequence for region movement_train */
	private void enterSequence_main_main_train_trainState_movement_train_default() {
		react_main_main_train_trainState_movement_train__entry_Default();
	}
	
	/* deep enterSequence with history in child movement_train */
	private void deepEnterSequence_main_main_train_trainState_movement_train() {
		switch (historyVector[1]) {
		case main_main_train_trainState_movement_train_Still:
			enterSequence_main_main_train_trainState_movement_train_Still_default();
			break;
		case main_main_train_trainState_movement_train_Driving:
			enterSequence_main_main_train_trainState_movement_train_Driving_default();
			break;
		case main_main_train_trainState_movement_train_Cruising:
			enterSequence_main_main_train_trainState_movement_train_Cruising_default();
			break;
		case main_main_train_trainState_movement_train_Breaking:
			enterSequence_main_main_train_trainState_movement_train_Breaking_default();
			break;
		case main_main_train_trainState_movement_train_Cooldown:
			enterSequence_main_main_train_trainState_movement_train_Cooldown_default();
			break;
		default:
			break;
		}
	}
	
	/* 'default' enter sequence for region Place_train */
	private void enterSequence_main_main_train_trainState_Place_train_default() {
		react_main_main_train_trainState_Place_train__entry_Default();
	}
	
	/* deep enterSequence with history in child Place_train */
	private void deepEnterSequence_main_main_train_trainState_Place_train() {
		switch (historyVector[2]) {
		case main_main_train_trainState_Place_train_InStation:
			enterSequence_main_main_train_trainState_Place_train_InStation_default();
			break;
		case main_main_train_trainState_Place_train_OutOfStation:
			enterSequence_main_main_train_trainState_Place_train_OutOfStation_default();
			break;
		default:
			break;
		}
	}
	
	/* 'default' enter sequence for region Doors_station */
	private void enterSequence_main_main_train_trainState_Doors_station_default() {
		react_main_main_train_trainState_Doors_station__entry_Default();
	}
	
	/* deep enterSequence with history in child Doors_station */
	private void deepEnterSequence_main_main_train_trainState_Doors_station() {
		switch (historyVector[3]) {
		case main_main_train_trainState_Doors_station_openingDoors:
			enterSequence_main_main_train_trainState_Doors_station_openingDoors_default();
			break;
		case main_main_train_trainState_Doors_station_ClosedDoors:
			enterSequence_main_main_train_trainState_Doors_station_ClosedDoors_default();
			break;
		case main_main_train_trainState_Doors_station_openDoors:
			enterSequence_main_main_train_trainState_Doors_station_openDoors_default();
			break;
		case main_main_train_trainState_Doors_station_ClosingDoors:
			enterSequence_main_main_train_trainState_Doors_station_ClosingDoors_default();
			break;
		default:
			break;
		}
	}
	
	/* 'default' enter sequence for region Last_light */
	private void enterSequence_main_main_train_trainState_Last_light_default() {
		react_main_main_train_trainState_Last_light__entry_Default();
	}
	
	/* deep enterSequence with history in child Last_light */
	private void deepEnterSequence_main_main_train_trainState_Last_light() {
		switch (historyVector[4]) {
		case main_main_train_trainState_Last_light_NotYellow:
			enterSequence_main_main_train_trainState_Last_light_NotYellow_default();
			break;
		case main_main_train_trainState_Last_light_Yellow:
			enterSequence_main_main_train_trainState_Last_light_Yellow_default();
			break;
		case main_main_train_trainState_Last_light_YellowAndBreaking:
			enterSequence_main_main_train_trainState_Last_light_YellowAndBreaking_default();
			break;
		default:
			break;
		}
	}
	
	/* 'default' enter sequence for region dead man's button */
	private void enterSequence_main_main_dead_man_s_button_default() {
		react_main_main_dead_man_s_button__entry_Default();
	}
	
	/* Default exit sequence for state default */
	private void exitSequence_main_default() {
		nextStateIndex = 0;
		stateVector[0] = State.$NullState$;
	}
	
	/* Default exit sequence for state main */
	private void exitSequence_main_main() {
		exitSequence_main_main_train();
		exitSequence_main_main_dead_man_s_button();
	}
	
	/* Default exit sequence for state trainState */
	private void exitSequence_main_main_train_trainState() {
		exitSequence_main_main_train_trainState_movement_train();
		exitSequence_main_main_train_trainState_Place_train();
		exitSequence_main_main_train_trainState_Doors_station();
		exitSequence_main_main_train_trainState_Last_light();
	}
	
	/* Default exit sequence for state Still */
	private void exitSequence_main_main_train_trainState_movement_train_Still() {
		nextStateIndex = 0;
		stateVector[0] = State.$NullState$;
	}
	
	/* Default exit sequence for state Driving */
	private void exitSequence_main_main_train_trainState_movement_train_Driving() {
		nextStateIndex = 0;
		stateVector[0] = State.$NullState$;
	}
	
	/* Default exit sequence for state Cruising */
	private void exitSequence_main_main_train_trainState_movement_train_Cruising() {
		nextStateIndex = 0;
		stateVector[0] = State.$NullState$;
	}
	
	/* Default exit sequence for state Breaking */
	private void exitSequence_main_main_train_trainState_movement_train_Breaking() {
		nextStateIndex = 0;
		stateVector[0] = State.$NullState$;
	}
	
	/* Default exit sequence for state Cooldown */
	private void exitSequence_main_main_train_trainState_movement_train_Cooldown() {
		nextStateIndex = 0;
		stateVector[0] = State.$NullState$;
		
		exitAction_main_main_train_trainState_movement_train_Cooldown();
	}
	
	/* Default exit sequence for state InStation */
	private void exitSequence_main_main_train_trainState_Place_train_InStation() {
		nextStateIndex = 1;
		stateVector[1] = State.$NullState$;
	}
	
	/* Default exit sequence for state OutOfStation */
	private void exitSequence_main_main_train_trainState_Place_train_OutOfStation() {
		nextStateIndex = 1;
		stateVector[1] = State.$NullState$;
	}
	
	/* Default exit sequence for state openingDoors */
	private void exitSequence_main_main_train_trainState_Doors_station_openingDoors() {
		nextStateIndex = 2;
		stateVector[2] = State.$NullState$;
		
		exitAction_main_main_train_trainState_Doors_station_openingDoors();
	}
	
	/* Default exit sequence for state ClosedDoors */
	private void exitSequence_main_main_train_trainState_Doors_station_ClosedDoors() {
		nextStateIndex = 2;
		stateVector[2] = State.$NullState$;
	}
	
	/* Default exit sequence for state openDoors */
	private void exitSequence_main_main_train_trainState_Doors_station_openDoors() {
		nextStateIndex = 2;
		stateVector[2] = State.$NullState$;
	}
	
	/* Default exit sequence for state ClosingDoors */
	private void exitSequence_main_main_train_trainState_Doors_station_ClosingDoors() {
		nextStateIndex = 2;
		stateVector[2] = State.$NullState$;
	}
	
	/* Default exit sequence for state NotYellow */
	private void exitSequence_main_main_train_trainState_Last_light_NotYellow() {
		nextStateIndex = 3;
		stateVector[3] = State.$NullState$;
	}
	
	/* Default exit sequence for state Yellow */
	private void exitSequence_main_main_train_trainState_Last_light_Yellow() {
		nextStateIndex = 3;
		stateVector[3] = State.$NullState$;
	}
	
	/* Default exit sequence for state YellowAndBreaking */
	private void exitSequence_main_main_train_trainState_Last_light_YellowAndBreaking() {
		nextStateIndex = 3;
		stateVector[3] = State.$NullState$;
	}
	
	/* Default exit sequence for state Normal */
	private void exitSequence_main_main_dead_man_s_button_Normal() {
		nextStateIndex = 4;
		stateVector[4] = State.$NullState$;
		
		exitAction_main_main_dead_man_s_button_Normal();
	}
	
	/* Default exit sequence for state Button Prompted */
	private void exitSequence_main_main_dead_man_s_button_Button_Prompted() {
		nextStateIndex = 4;
		stateVector[4] = State.$NullState$;
		
		exitAction_main_main_dead_man_s_button_Button_Prompted();
	}
	
	/* Default exit sequence for state Emergency Brake Mode */
	private void exitSequence_main_main_dead_man_s_button_Emergency_Brake_Mode() {
		nextStateIndex = 4;
		stateVector[4] = State.$NullState$;
		
		exitAction_main_main_dead_man_s_button_Emergency_Brake_Mode();
	}
	
	/* Default exit sequence for region main */
	private void exitSequence_main() {
		switch (stateVector[0]) {
		case main_default:
			exitSequence_main_default();
			break;
		case main_main_train_trainState_movement_train_Still:
			exitSequence_main_main_train_trainState_movement_train_Still();
			break;
		case main_main_train_trainState_movement_train_Driving:
			exitSequence_main_main_train_trainState_movement_train_Driving();
			break;
		case main_main_train_trainState_movement_train_Cruising:
			exitSequence_main_main_train_trainState_movement_train_Cruising();
			break;
		case main_main_train_trainState_movement_train_Breaking:
			exitSequence_main_main_train_trainState_movement_train_Breaking();
			break;
		case main_main_train_trainState_movement_train_Cooldown:
			exitSequence_main_main_train_trainState_movement_train_Cooldown();
			break;
		default:
			break;
		}
		
		switch (stateVector[1]) {
		case main_main_train_trainState_Place_train_InStation:
			exitSequence_main_main_train_trainState_Place_train_InStation();
			break;
		case main_main_train_trainState_Place_train_OutOfStation:
			exitSequence_main_main_train_trainState_Place_train_OutOfStation();
			break;
		default:
			break;
		}
		
		switch (stateVector[2]) {
		case main_main_train_trainState_Doors_station_openingDoors:
			exitSequence_main_main_train_trainState_Doors_station_openingDoors();
			break;
		case main_main_train_trainState_Doors_station_ClosedDoors:
			exitSequence_main_main_train_trainState_Doors_station_ClosedDoors();
			break;
		case main_main_train_trainState_Doors_station_openDoors:
			exitSequence_main_main_train_trainState_Doors_station_openDoors();
			break;
		case main_main_train_trainState_Doors_station_ClosingDoors:
			exitSequence_main_main_train_trainState_Doors_station_ClosingDoors();
			break;
		default:
			break;
		}
		
		switch (stateVector[3]) {
		case main_main_train_trainState_Last_light_NotYellow:
			exitSequence_main_main_train_trainState_Last_light_NotYellow();
			break;
		case main_main_train_trainState_Last_light_Yellow:
			exitSequence_main_main_train_trainState_Last_light_Yellow();
			break;
		case main_main_train_trainState_Last_light_YellowAndBreaking:
			exitSequence_main_main_train_trainState_Last_light_YellowAndBreaking();
			break;
		default:
			break;
		}
		
		switch (stateVector[4]) {
		case main_main_dead_man_s_button_Normal:
			exitSequence_main_main_dead_man_s_button_Normal();
			break;
		case main_main_dead_man_s_button_Button_Prompted:
			exitSequence_main_main_dead_man_s_button_Button_Prompted();
			break;
		case main_main_dead_man_s_button_Emergency_Brake_Mode:
			exitSequence_main_main_dead_man_s_button_Emergency_Brake_Mode();
			break;
		default:
			break;
		}
	}
	
	/* Default exit sequence for region train */
	private void exitSequence_main_main_train() {
		switch (stateVector[0]) {
		case main_main_train_trainState_movement_train_Still:
			exitSequence_main_main_train_trainState_movement_train_Still();
			break;
		case main_main_train_trainState_movement_train_Driving:
			exitSequence_main_main_train_trainState_movement_train_Driving();
			break;
		case main_main_train_trainState_movement_train_Cruising:
			exitSequence_main_main_train_trainState_movement_train_Cruising();
			break;
		case main_main_train_trainState_movement_train_Breaking:
			exitSequence_main_main_train_trainState_movement_train_Breaking();
			break;
		case main_main_train_trainState_movement_train_Cooldown:
			exitSequence_main_main_train_trainState_movement_train_Cooldown();
			break;
		default:
			break;
		}
		
		switch (stateVector[1]) {
		case main_main_train_trainState_Place_train_InStation:
			exitSequence_main_main_train_trainState_Place_train_InStation();
			break;
		case main_main_train_trainState_Place_train_OutOfStation:
			exitSequence_main_main_train_trainState_Place_train_OutOfStation();
			break;
		default:
			break;
		}
		
		switch (stateVector[2]) {
		case main_main_train_trainState_Doors_station_openingDoors:
			exitSequence_main_main_train_trainState_Doors_station_openingDoors();
			break;
		case main_main_train_trainState_Doors_station_ClosedDoors:
			exitSequence_main_main_train_trainState_Doors_station_ClosedDoors();
			break;
		case main_main_train_trainState_Doors_station_openDoors:
			exitSequence_main_main_train_trainState_Doors_station_openDoors();
			break;
		case main_main_train_trainState_Doors_station_ClosingDoors:
			exitSequence_main_main_train_trainState_Doors_station_ClosingDoors();
			break;
		default:
			break;
		}
		
		switch (stateVector[3]) {
		case main_main_train_trainState_Last_light_NotYellow:
			exitSequence_main_main_train_trainState_Last_light_NotYellow();
			break;
		case main_main_train_trainState_Last_light_Yellow:
			exitSequence_main_main_train_trainState_Last_light_Yellow();
			break;
		case main_main_train_trainState_Last_light_YellowAndBreaking:
			exitSequence_main_main_train_trainState_Last_light_YellowAndBreaking();
			break;
		default:
			break;
		}
	}
	
	/* Default exit sequence for region movement_train */
	private void exitSequence_main_main_train_trainState_movement_train() {
		switch (stateVector[0]) {
		case main_main_train_trainState_movement_train_Still:
			exitSequence_main_main_train_trainState_movement_train_Still();
			break;
		case main_main_train_trainState_movement_train_Driving:
			exitSequence_main_main_train_trainState_movement_train_Driving();
			break;
		case main_main_train_trainState_movement_train_Cruising:
			exitSequence_main_main_train_trainState_movement_train_Cruising();
			break;
		case main_main_train_trainState_movement_train_Breaking:
			exitSequence_main_main_train_trainState_movement_train_Breaking();
			break;
		case main_main_train_trainState_movement_train_Cooldown:
			exitSequence_main_main_train_trainState_movement_train_Cooldown();
			break;
		default:
			break;
		}
	}
	
	/* Default exit sequence for region Place_train */
	private void exitSequence_main_main_train_trainState_Place_train() {
		switch (stateVector[1]) {
		case main_main_train_trainState_Place_train_InStation:
			exitSequence_main_main_train_trainState_Place_train_InStation();
			break;
		case main_main_train_trainState_Place_train_OutOfStation:
			exitSequence_main_main_train_trainState_Place_train_OutOfStation();
			break;
		default:
			break;
		}
	}
	
	/* Default exit sequence for region Doors_station */
	private void exitSequence_main_main_train_trainState_Doors_station() {
		switch (stateVector[2]) {
		case main_main_train_trainState_Doors_station_openingDoors:
			exitSequence_main_main_train_trainState_Doors_station_openingDoors();
			break;
		case main_main_train_trainState_Doors_station_ClosedDoors:
			exitSequence_main_main_train_trainState_Doors_station_ClosedDoors();
			break;
		case main_main_train_trainState_Doors_station_openDoors:
			exitSequence_main_main_train_trainState_Doors_station_openDoors();
			break;
		case main_main_train_trainState_Doors_station_ClosingDoors:
			exitSequence_main_main_train_trainState_Doors_station_ClosingDoors();
			break;
		default:
			break;
		}
	}
	
	/* Default exit sequence for region Last_light */
	private void exitSequence_main_main_train_trainState_Last_light() {
		switch (stateVector[3]) {
		case main_main_train_trainState_Last_light_NotYellow:
			exitSequence_main_main_train_trainState_Last_light_NotYellow();
			break;
		case main_main_train_trainState_Last_light_Yellow:
			exitSequence_main_main_train_trainState_Last_light_Yellow();
			break;
		case main_main_train_trainState_Last_light_YellowAndBreaking:
			exitSequence_main_main_train_trainState_Last_light_YellowAndBreaking();
			break;
		default:
			break;
		}
	}
	
	/* Default exit sequence for region dead man's button */
	private void exitSequence_main_main_dead_man_s_button() {
		switch (stateVector[4]) {
		case main_main_dead_man_s_button_Normal:
			exitSequence_main_main_dead_man_s_button_Normal();
			break;
		case main_main_dead_man_s_button_Button_Prompted:
			exitSequence_main_main_dead_man_s_button_Button_Prompted();
			break;
		case main_main_dead_man_s_button_Emergency_Brake_Mode:
			exitSequence_main_main_dead_man_s_button_Emergency_Brake_Mode();
			break;
		default:
			break;
		}
	}
	
	/* Default react sequence for initial entry  */
	private void react_main__entry_Default() {
		enterSequence_main_default_default();
	}
	
	/* Default react sequence for initial entry  */
	private void react_main_main_train_trainState_movement_train__entry_Default() {
		enterSequence_main_main_train_trainState_movement_train_Still_default();
	}
	
	/* Default react sequence for initial entry  */
	private void react_main_main_train_trainState_Place_train__entry_Default() {
		enterSequence_main_main_train_trainState_Place_train_OutOfStation_default();
	}
	
	/* Default react sequence for initial entry  */
	private void react_main_main_train_trainState_Doors_station__entry_Default() {
		enterSequence_main_main_train_trainState_Doors_station_ClosedDoors_default();
	}
	
	/* Default react sequence for initial entry  */
	private void react_main_main_train_trainState_Last_light__entry_Default() {
		enterSequence_main_main_train_trainState_Last_light_NotYellow_default();
	}
	
	/* Default react sequence for initial entry  */
	private void react_main_main_train__entry_Default() {
		enterSequence_main_main_train_trainState_default();
	}
	
	/* Default react sequence for deep history entry hist */
	private void react_main_main_train_hist() {
		/* Enter the region with deep history */
		if (historyVector[0] != State.$NullState$) {
			deepEnterSequence_main_main_train();
		} else {
			enterSequence_main_main_train_trainState_default();
		}
	}
	
	/* Default react sequence for initial entry  */
	private void react_main_main_dead_man_s_button__entry_Default() {
		enterSequence_main_main_dead_man_s_button_Normal_default();
	}
	
	private boolean react(boolean try_transition) {
		if (timeEvents[5]) {
			sCInterface.setVelocity(sCInterface.getVelocity() + (acceleration / 2));
			
			sCInterface.setVelocity(sCInterface.velocity>0 ? sCInterface.velocity : 0);
			
			sCInterface.operationCallback.updateGUI();
		}
		return false;
	}
	
	private boolean main_default_react(boolean try_transition) {
		boolean did_transition = try_transition;
		
		if (try_transition) {
			if (react(try_transition)==false) {
				if (sCInterface.continueEvent) {
					exitSequence_main_default();
					react_main_main_train_hist();
					enterSequence_main_main_dead_man_s_button_default();
				} else {
					did_transition = false;
				}
			}
		}
		if (did_transition==false) {
		}
		return did_transition;
	}
	
	private boolean main_main_react(boolean try_transition) {
		boolean did_transition = try_transition;
		
		if (try_transition) {
			if (react(try_transition)==false) {
				if (sCInterface.pause) {
					exitSequence_main_main();
					enterSequence_main_default_default();
				} else {
					did_transition = false;
				}
			}
		}
		if (did_transition==false) {
		}
		return did_transition;
	}
	
	private boolean main_main_train_trainState_react(boolean try_transition) {
		boolean did_transition = try_transition;
		
		if (try_transition) {
			if (main_main_react(try_transition)==false) {
				if (sCInterface.red_light) {
					exitSequence_main_main_train_trainState();
					raiseEmergency();
					
					sCInterface.operationCallback.print("Red light");
					
					enterSequence_main_main_train_trainState_default();
				} else {
					did_transition = false;
				}
			}
		}
		if (did_transition==false) {
		}
		return did_transition;
	}
	
	private boolean main_main_train_trainState_movement_train_Still_react(boolean try_transition) {
		boolean did_transition = try_transition;
		
		if (try_transition) {
			if (main_main_train_trainState_react(try_transition)==false) {
				if ((sCInterface.update_acceleration) && (sCInterface.getUpdate_accelerationValue()>0.0 && isStateActive(State.main_main_train_trainState_Doors_station_ClosedDoors))) {
					exitSequence_main_main_train_trainState_movement_train_Still();
					enterSequence_main_main_train_trainState_movement_train_Driving_default();
				} else {
					did_transition = false;
				}
			}
		}
		if (did_transition==false) {
		}
		return did_transition;
	}
	
	private boolean main_main_train_trainState_movement_train_Driving_react(boolean try_transition) {
		boolean did_transition = try_transition;
		
		if (try_transition) {
			if (main_main_train_trainState_react(try_transition)==false) {
				if (sCInterface.getVelocity()>=100.0) {
					exitSequence_main_main_train_trainState_movement_train_Driving();
					enterSequence_main_main_train_trainState_movement_train_Cruising_default();
				} else {
					if (sCInterface.getVelocity()<=0) {
						exitSequence_main_main_train_trainState_movement_train_Driving();
						enterSequence_main_main_train_trainState_movement_train_Still_default();
					} else {
						if (sCInterface.update_acceleration) {
							exitSequence_main_main_train_trainState_movement_train_Driving();
							enterSequence_main_main_train_trainState_movement_train_Driving_default();
						} else {
							if (emergency) {
								exitSequence_main_main_train_trainState_movement_train_Driving();
								enterSequence_main_main_train_trainState_movement_train_Breaking_default();
							} else {
								did_transition = false;
							}
						}
					}
				}
			}
		}
		if (did_transition==false) {
		}
		return did_transition;
	}
	
	private boolean main_main_train_trainState_movement_train_Cruising_react(boolean try_transition) {
		boolean did_transition = try_transition;
		
		if (try_transition) {
			if (main_main_train_trainState_react(try_transition)==false) {
				if ((sCInterface.update_acceleration) && (sCInterface.getUpdate_accelerationValue()<0.0)) {
					exitSequence_main_main_train_trainState_movement_train_Cruising();
					enterSequence_main_main_train_trainState_movement_train_Driving_default();
				} else {
					if (emergency) {
						exitSequence_main_main_train_trainState_movement_train_Cruising();
						enterSequence_main_main_train_trainState_movement_train_Breaking_default();
					} else {
						did_transition = false;
					}
				}
			}
		}
		if (did_transition==false) {
		}
		return did_transition;
	}
	
	private boolean main_main_train_trainState_movement_train_Breaking_react(boolean try_transition) {
		boolean did_transition = try_transition;
		
		if (try_transition) {
			if (main_main_train_trainState_react(try_transition)==false) {
				if (sCInterface.getVelocity()<=0) {
					exitSequence_main_main_train_trainState_movement_train_Breaking();
					enterSequence_main_main_train_trainState_movement_train_Cooldown_default();
				} else {
					did_transition = false;
				}
			}
		}
		if (did_transition==false) {
		}
		return did_transition;
	}
	
	private boolean main_main_train_trainState_movement_train_Cooldown_react(boolean try_transition) {
		boolean did_transition = try_transition;
		
		if (try_transition) {
			if (main_main_train_trainState_react(try_transition)==false) {
				if (timeEvents[0]) {
					exitSequence_main_main_train_trainState_movement_train_Cooldown();
					enterSequence_main_main_train_trainState_movement_train_Still_default();
				} else {
					did_transition = false;
				}
			}
		}
		if (did_transition==false) {
		}
		return did_transition;
	}
	
	private boolean main_main_train_trainState_Place_train_InStation_react(boolean try_transition) {
		boolean did_transition = try_transition;
		
		if (try_transition) {
			if (sCInterface.leave) {
				exitSequence_main_main_train_trainState_Place_train_InStation();
				raiseToClosedDoors();
				
				enterSequence_main_main_train_trainState_Place_train_OutOfStation_default();
			} else {
				if (sCInterface.getVelocity()>20.0) {
					exitSequence_main_main_train_trainState_Place_train_InStation();
					raiseEmergency();
					
					sCInterface.operationCallback.print("Velocity > 20 in station");
					
					enterSequence_main_main_train_trainState_Place_train_InStation_default();
				} else {
					did_transition = false;
				}
			}
		}
		if (did_transition==false) {
		}
		return did_transition;
	}
	
	private boolean main_main_train_trainState_Place_train_OutOfStation_react(boolean try_transition) {
		boolean did_transition = try_transition;
		
		if (try_transition) {
			if (sCInterface.enter) {
				exitSequence_main_main_train_trainState_Place_train_OutOfStation();
				enterSequence_main_main_train_trainState_Place_train_InStation_default();
			} else {
				did_transition = false;
			}
		}
		if (did_transition==false) {
		}
		return did_transition;
	}
	
	private boolean main_main_train_trainState_Doors_station_openingDoors_react(boolean try_transition) {
		boolean did_transition = try_transition;
		
		if (try_transition) {
			if (timeEvents[1]) {
				exitSequence_main_main_train_trainState_Doors_station_openingDoors();
				enterSequence_main_main_train_trainState_Doors_station_openDoors_default();
			} else {
				did_transition = false;
			}
		}
		if (did_transition==false) {
		}
		return did_transition;
	}
	
	private boolean main_main_train_trainState_Doors_station_ClosedDoors_react(boolean try_transition) {
		boolean did_transition = try_transition;
		
		if (try_transition) {
			if ((sCInterface.open) && (isStateActive(State.main_main_train_trainState_movement_train_Still) && isStateActive(State.main_main_train_trainState_Place_train_InStation))) {
				exitSequence_main_main_train_trainState_Doors_station_ClosedDoors();
				enterSequence_main_main_train_trainState_Doors_station_openingDoors_default();
			} else {
				did_transition = false;
			}
		}
		if (did_transition==false) {
		}
		return did_transition;
	}
	
	private boolean main_main_train_trainState_Doors_station_openDoors_react(boolean try_transition) {
		boolean did_transition = try_transition;
		
		if (try_transition) {
			if (sCInterface.close) {
				exitSequence_main_main_train_trainState_Doors_station_openDoors();
				enterSequence_main_main_train_trainState_Doors_station_ClosingDoors_default();
			} else {
				did_transition = false;
			}
		}
		if (did_transition==false) {
		}
		return did_transition;
	}
	
	private boolean main_main_train_trainState_Doors_station_ClosingDoors_react(boolean try_transition) {
		boolean did_transition = try_transition;
		
		if (try_transition) {
			if (toClosedDoors) {
				exitSequence_main_main_train_trainState_Doors_station_ClosingDoors();
				enterSequence_main_main_train_trainState_Doors_station_ClosedDoors_default();
			} else {
				did_transition = false;
			}
		}
		if (did_transition==false) {
		}
		return did_transition;
	}
	
	private boolean main_main_train_trainState_Last_light_NotYellow_react(boolean try_transition) {
		boolean did_transition = try_transition;
		
		if (try_transition) {
			if (sCInterface.yellow_light) {
				exitSequence_main_main_train_trainState_Last_light_NotYellow();
				enterSequence_main_main_train_trainState_Last_light_Yellow_default();
			} else {
				did_transition = false;
			}
		}
		if (did_transition==false) {
		}
		return did_transition;
	}
	
	private boolean main_main_train_trainState_Last_light_Yellow_react(boolean try_transition) {
		boolean did_transition = try_transition;
		
		if (try_transition) {
			if (sCInterface.getVelocity()>50.0) {
				exitSequence_main_main_train_trainState_Last_light_Yellow();
				raiseEmergency();
				
				sCInterface.operationCallback.print("Yellow && speed>50");
				
				enterSequence_main_main_train_trainState_Last_light_YellowAndBreaking_default();
			} else {
				if (sCInterface.green_light) {
					exitSequence_main_main_train_trainState_Last_light_Yellow();
					enterSequence_main_main_train_trainState_Last_light_NotYellow_default();
				} else {
					did_transition = false;
				}
			}
		}
		if (did_transition==false) {
		}
		return did_transition;
	}
	
	private boolean main_main_train_trainState_Last_light_YellowAndBreaking_react(boolean try_transition) {
		boolean did_transition = try_transition;
		
		if (try_transition) {
			if (sCInterface.green_light) {
				exitSequence_main_main_train_trainState_Last_light_YellowAndBreaking();
				enterSequence_main_main_train_trainState_Last_light_NotYellow_default();
			} else {
				did_transition = false;
			}
		}
		if (did_transition==false) {
		}
		return did_transition;
	}
	
	private boolean main_main_dead_man_s_button_Normal_react(boolean try_transition) {
		boolean did_transition = try_transition;
		
		if (try_transition) {
			if (timeEvents[2]) {
				exitSequence_main_main_dead_man_s_button_Normal();
				enterSequence_main_main_dead_man_s_button_Button_Prompted_default();
			} else {
				did_transition = false;
			}
		}
		if (did_transition==false) {
		}
		return did_transition;
	}
	
	private boolean main_main_dead_man_s_button_Button_Prompted_react(boolean try_transition) {
		boolean did_transition = try_transition;
		
		if (try_transition) {
			if (sCInterface.awake) {
				exitSequence_main_main_dead_man_s_button_Button_Prompted();
				enterSequence_main_main_dead_man_s_button_Normal_default();
			} else {
				if (timeEvents[3]) {
					exitSequence_main_main_dead_man_s_button_Button_Prompted();
					raiseEmergency();
					
					enterSequence_main_main_dead_man_s_button_Emergency_Brake_Mode_default();
				} else {
					did_transition = false;
				}
			}
		}
		if (did_transition==false) {
		}
		return did_transition;
	}
	
	private boolean main_main_dead_man_s_button_Emergency_Brake_Mode_react(boolean try_transition) {
		boolean did_transition = try_transition;
		
		if (try_transition) {
			if (timeEvents[4]) {
				exitSequence_main_main_dead_man_s_button_Emergency_Brake_Mode();
				enterSequence_main_main_dead_man_s_button_Normal_default();
			} else {
				did_transition = false;
			}
		}
		if (did_transition==false) {
		}
		return did_transition;
	}
	
	public void runCycle() {
		if (!initialized)
			throw new IllegalStateException(
					"The state machine needs to be initialized first by calling the init() function.");
		clearOutEvents();
		for (nextStateIndex = 0; nextStateIndex < stateVector.length; nextStateIndex++) {
			switch (stateVector[nextStateIndex]) {
			case main_default:
				main_default_react(true);
				break;
			case main_main_train_trainState_movement_train_Still:
				main_main_train_trainState_movement_train_Still_react(true);
				break;
			case main_main_train_trainState_movement_train_Driving:
				main_main_train_trainState_movement_train_Driving_react(true);
				break;
			case main_main_train_trainState_movement_train_Cruising:
				main_main_train_trainState_movement_train_Cruising_react(true);
				break;
			case main_main_train_trainState_movement_train_Breaking:
				main_main_train_trainState_movement_train_Breaking_react(true);
				break;
			case main_main_train_trainState_movement_train_Cooldown:
				main_main_train_trainState_movement_train_Cooldown_react(true);
				break;
			case main_main_train_trainState_Place_train_InStation:
				main_main_train_trainState_Place_train_InStation_react(true);
				break;
			case main_main_train_trainState_Place_train_OutOfStation:
				main_main_train_trainState_Place_train_OutOfStation_react(true);
				break;
			case main_main_train_trainState_Doors_station_openingDoors:
				main_main_train_trainState_Doors_station_openingDoors_react(true);
				break;
			case main_main_train_trainState_Doors_station_ClosedDoors:
				main_main_train_trainState_Doors_station_ClosedDoors_react(true);
				break;
			case main_main_train_trainState_Doors_station_openDoors:
				main_main_train_trainState_Doors_station_openDoors_react(true);
				break;
			case main_main_train_trainState_Doors_station_ClosingDoors:
				main_main_train_trainState_Doors_station_ClosingDoors_react(true);
				break;
			case main_main_train_trainState_Last_light_NotYellow:
				main_main_train_trainState_Last_light_NotYellow_react(true);
				break;
			case main_main_train_trainState_Last_light_Yellow:
				main_main_train_trainState_Last_light_Yellow_react(true);
				break;
			case main_main_train_trainState_Last_light_YellowAndBreaking:
				main_main_train_trainState_Last_light_YellowAndBreaking_react(true);
				break;
			case main_main_dead_man_s_button_Normal:
				main_main_dead_man_s_button_Normal_react(true);
				break;
			case main_main_dead_man_s_button_Button_Prompted:
				main_main_dead_man_s_button_Button_Prompted_react(true);
				break;
			case main_main_dead_man_s_button_Emergency_Brake_Mode:
				main_main_dead_man_s_button_Emergency_Brake_Mode_react(true);
				break;
			default:
				// $NullState$
			}
		}
		clearEvents();
	}
}
