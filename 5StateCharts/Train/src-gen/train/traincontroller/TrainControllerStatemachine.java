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
		main_main_train_trainState_Light_LastLight,
		main_main_train_trainState_Light_LastLight_Mode_Yellow,
		main_main_train_trainState_Light_LastLight_Mode_Yellow_Mode_NotSpeeding,
		main_main_train_trainState_Light_LastLight_Mode_Yellow_Mode_Speeding,
		main_main_train_trainState_Light_LastLight_Mode_NotYellow,
		main_main_train_trainState_place_NotInStation,
		main_main_train_trainState_place_InStation,
		main_main_train_trainState_place_InStation_Mode_NotSpeeding,
		main_main_train_trainState_place_InStation_Mode_Speeding,
		main_main_train_trainState_doors_OpenDoors,
		main_main_train_trainState_doors_OpenDoors_Status_NotClosable,
		main_main_train_trainState_doors_OpenDoors_Status_Closable,
		main_main_train_trainState_doors_ClosedDoors,
		main_main_train_trainState_doors_ClosedDoors_status_NotOpenable,
		main_main_train_trainState_doors_ClosedDoors_status_DoorsOpenable,
		main_main_train_trainState_movement_Still,
		main_main_train_trainState_movement_Moving,
		main_main_train_trainState_movement_Moving_NormalMovement_Cruising,
		main_main_train_trainState_movement_Moving_NormalMovement_Driving,
		main_main_train_trainState_movement_Breaking,
		main_main_train_trainState_movement_Cooldown,
		main_main_dead_man_s_button_Normal,
		main_main_dead_man_s_button_Button_Prompted,
		main_main_dead_man_s_button_Emergency_Brake_Mode,
		$NullState$
	};
	
	private State[] historyVector = new State[11];
	private final State[] stateVector = new State[5];
	
	private int nextStateIndex;
	
	private ITimer timer;
	
	private final boolean[] timeEvents = new boolean[6];
	
	private boolean emergency;
	
	private boolean toOpenableDoors;
	private double acceleration;
	
	protected void setAcceleration(double value) {
		acceleration = value;
	}
	
	protected double getAcceleration() {
		return acceleration;
	}
	
	private double storedAcc;
	
	protected void setStoredAcc(double value) {
		storedAcc = value;
	}
	
	protected double getStoredAcc() {
		return storedAcc;
	}
	
	private double storedVel;
	
	protected void setStoredVel(double value) {
		storedVel = value;
	}
	
	protected double getStoredVel() {
		return storedVel;
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
		for (int i = 0; i < 11; i++) {
			historyVector[i] = State.$NullState$;
		}
		clearEvents();
		clearOutEvents();
		sCInterface.setVelocity(0.0);
		
		setAcceleration(0.0);
		
		setStoredAcc(0.0);
		
		setStoredVel(0.0);
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
		toOpenableDoors = false;
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
					main_main_train_trainState.ordinal()&& stateVector[0].ordinal() <= State.main_main_train_trainState_movement_Cooldown.ordinal();
		case main_main_train_trainState_Light_LastLight:
			return stateVector[0].ordinal() >= State.
					main_main_train_trainState_Light_LastLight.ordinal()&& stateVector[0].ordinal() <= State.main_main_train_trainState_Light_LastLight_Mode_NotYellow.ordinal();
		case main_main_train_trainState_Light_LastLight_Mode_Yellow:
			return stateVector[0].ordinal() >= State.
					main_main_train_trainState_Light_LastLight_Mode_Yellow.ordinal()&& stateVector[0].ordinal() <= State.main_main_train_trainState_Light_LastLight_Mode_Yellow_Mode_Speeding.ordinal();
		case main_main_train_trainState_Light_LastLight_Mode_Yellow_Mode_NotSpeeding:
			return stateVector[0] == State.main_main_train_trainState_Light_LastLight_Mode_Yellow_Mode_NotSpeeding;
		case main_main_train_trainState_Light_LastLight_Mode_Yellow_Mode_Speeding:
			return stateVector[0] == State.main_main_train_trainState_Light_LastLight_Mode_Yellow_Mode_Speeding;
		case main_main_train_trainState_Light_LastLight_Mode_NotYellow:
			return stateVector[0] == State.main_main_train_trainState_Light_LastLight_Mode_NotYellow;
		case main_main_train_trainState_place_NotInStation:
			return stateVector[1] == State.main_main_train_trainState_place_NotInStation;
		case main_main_train_trainState_place_InStation:
			return stateVector[1].ordinal() >= State.
					main_main_train_trainState_place_InStation.ordinal()&& stateVector[1].ordinal() <= State.main_main_train_trainState_place_InStation_Mode_Speeding.ordinal();
		case main_main_train_trainState_place_InStation_Mode_NotSpeeding:
			return stateVector[1] == State.main_main_train_trainState_place_InStation_Mode_NotSpeeding;
		case main_main_train_trainState_place_InStation_Mode_Speeding:
			return stateVector[1] == State.main_main_train_trainState_place_InStation_Mode_Speeding;
		case main_main_train_trainState_doors_OpenDoors:
			return stateVector[2].ordinal() >= State.
					main_main_train_trainState_doors_OpenDoors.ordinal()&& stateVector[2].ordinal() <= State.main_main_train_trainState_doors_OpenDoors_Status_Closable.ordinal();
		case main_main_train_trainState_doors_OpenDoors_Status_NotClosable:
			return stateVector[2] == State.main_main_train_trainState_doors_OpenDoors_Status_NotClosable;
		case main_main_train_trainState_doors_OpenDoors_Status_Closable:
			return stateVector[2] == State.main_main_train_trainState_doors_OpenDoors_Status_Closable;
		case main_main_train_trainState_doors_ClosedDoors:
			return stateVector[2].ordinal() >= State.
					main_main_train_trainState_doors_ClosedDoors.ordinal()&& stateVector[2].ordinal() <= State.main_main_train_trainState_doors_ClosedDoors_status_DoorsOpenable.ordinal();
		case main_main_train_trainState_doors_ClosedDoors_status_NotOpenable:
			return stateVector[2] == State.main_main_train_trainState_doors_ClosedDoors_status_NotOpenable;
		case main_main_train_trainState_doors_ClosedDoors_status_DoorsOpenable:
			return stateVector[2] == State.main_main_train_trainState_doors_ClosedDoors_status_DoorsOpenable;
		case main_main_train_trainState_movement_Still:
			return stateVector[3] == State.main_main_train_trainState_movement_Still;
		case main_main_train_trainState_movement_Moving:
			return stateVector[3].ordinal() >= State.
					main_main_train_trainState_movement_Moving.ordinal()&& stateVector[3].ordinal() <= State.main_main_train_trainState_movement_Moving_NormalMovement_Driving.ordinal();
		case main_main_train_trainState_movement_Moving_NormalMovement_Cruising:
			return stateVector[3] == State.main_main_train_trainState_movement_Moving_NormalMovement_Cruising;
		case main_main_train_trainState_movement_Moving_NormalMovement_Driving:
			return stateVector[3] == State.main_main_train_trainState_movement_Moving_NormalMovement_Driving;
		case main_main_train_trainState_movement_Breaking:
			return stateVector[3] == State.main_main_train_trainState_movement_Breaking;
		case main_main_train_trainState_movement_Cooldown:
			return stateVector[3] == State.main_main_train_trainState_movement_Cooldown;
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
	
	
	private void raiseToOpenableDoors() {
		toOpenableDoors = true;
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
		
		sCInterface.setVelocity(0);
		
		setAcceleration(0);
	}
	
	/* Entry action for state 'NotInStation'. */
	private void entryAction_main_main_train_trainState_place_NotInStation() {
		sCInterface.operationCallback.print("NotInStaion");
	}
	
	/* Entry action for state 'InStation'. */
	private void entryAction_main_main_train_trainState_place_InStation() {
		sCInterface.operationCallback.print("InStation");
	}
	
	/* Entry action for state 'Speeding'. */
	private void entryAction_main_main_train_trainState_place_InStation_Mode_Speeding() {
		sCInterface.operationCallback.print("Speeding");
	}
	
	/* Entry action for state 'NotClosable'. */
	private void entryAction_main_main_train_trainState_doors_OpenDoors_Status_NotClosable() {
		timer.setTimer(this, 0, 5 * 1000, false);
		
		sCInterface.operationCallback.print("OpenDoors");
	}
	
	/* Entry action for state 'Closable'. */
	private void entryAction_main_main_train_trainState_doors_OpenDoors_Status_Closable() {
		sCInterface.operationCallback.print("DoorsClosable");
	}
	
	/* Entry action for state 'NotOpenable'. */
	private void entryAction_main_main_train_trainState_doors_ClosedDoors_status_NotOpenable() {
		sCInterface.operationCallback.print("ClosedDoors");
	}
	
	/* Entry action for state 'DoorsOpenable'. */
	private void entryAction_main_main_train_trainState_doors_ClosedDoors_status_DoorsOpenable() {
		sCInterface.operationCallback.print("DoorsOpenable");
	}
	
	/* Entry action for state 'Still'. */
	private void entryAction_main_main_train_trainState_movement_Still() {
		sCInterface.operationCallback.print("still");
		
		sCInterface.setVelocity(0.0);
		
		setAcceleration(0.0);
	}
	
	/* Entry action for state 'Cruising'. */
	private void entryAction_main_main_train_trainState_movement_Moving_NormalMovement_Cruising() {
		sCInterface.operationCallback.print("cruising");
		
		sCInterface.setVelocity(100);
		
		setAcceleration(0);
		
		sCInterface.operationCallback.print("cruising");
		
		sCInterface.setVelocity(100);
		
		setAcceleration(0);
	}
	
	/* Entry action for state 'Driving'. */
	private void entryAction_main_main_train_trainState_movement_Moving_NormalMovement_Driving() {
		sCInterface.operationCallback.print("driving");
	}
	
	/* Entry action for state 'Breaking'. */
	private void entryAction_main_main_train_trainState_movement_Breaking() {
		sCInterface.operationCallback.print("Breaking");
		
		setAcceleration(-1.0);
	}
	
	/* Entry action for state 'Cooldown'. */
	private void entryAction_main_main_train_trainState_movement_Cooldown() {
		timer.setTimer(this, 1, 5 * 1000, false);
		
		sCInterface.operationCallback.print("Cooldown");
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
	
	/* Exit action for state 'NotClosable'. */
	private void exitAction_main_main_train_trainState_doors_OpenDoors_Status_NotClosable() {
		timer.unsetTimer(this, 0);
	}
	
	/* Exit action for state 'Driving'. */
	private void exitAction_main_main_train_trainState_movement_Moving_NormalMovement_Driving() {
		setStoredAcc(acceleration);
		
		setStoredVel(sCInterface.velocity);
	}
	
	/* Exit action for state 'Cooldown'. */
	private void exitAction_main_main_train_trainState_movement_Cooldown() {
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
		enterSequence_main_main_train_trainState_Light_default();
		enterSequence_main_main_train_trainState_place_default();
		enterSequence_main_main_train_trainState_doors_default();
		enterSequence_main_main_train_trainState_movement_default();
		historyVector[0] = stateVector[0];
	}
	
	/* 'default' enter sequence for state LastLight */
	private void enterSequence_main_main_train_trainState_Light_LastLight_default() {
		enterSequence_main_main_train_trainState_Light_LastLight_Mode_default();
		historyVector[1] = stateVector[0];
	}
	
	/* 'default' enter sequence for state Yellow */
	private void enterSequence_main_main_train_trainState_Light_LastLight_Mode_Yellow_default() {
		enterSequence_main_main_train_trainState_Light_LastLight_Mode_Yellow_Mode_default();
		historyVector[2] = stateVector[0];
	}
	
	/* 'default' enter sequence for state NotSpeeding */
	private void enterSequence_main_main_train_trainState_Light_LastLight_Mode_Yellow_Mode_NotSpeeding_default() {
		nextStateIndex = 0;
		stateVector[0] = State.main_main_train_trainState_Light_LastLight_Mode_Yellow_Mode_NotSpeeding;
		
		historyVector[3] = stateVector[0];
	}
	
	/* 'default' enter sequence for state Speeding */
	private void enterSequence_main_main_train_trainState_Light_LastLight_Mode_Yellow_Mode_Speeding_default() {
		nextStateIndex = 0;
		stateVector[0] = State.main_main_train_trainState_Light_LastLight_Mode_Yellow_Mode_Speeding;
		
		historyVector[3] = stateVector[0];
	}
	
	/* 'default' enter sequence for state NotYellow */
	private void enterSequence_main_main_train_trainState_Light_LastLight_Mode_NotYellow_default() {
		nextStateIndex = 0;
		stateVector[0] = State.main_main_train_trainState_Light_LastLight_Mode_NotYellow;
		
		historyVector[2] = stateVector[0];
	}
	
	/* 'default' enter sequence for state NotInStation */
	private void enterSequence_main_main_train_trainState_place_NotInStation_default() {
		entryAction_main_main_train_trainState_place_NotInStation();
		nextStateIndex = 1;
		stateVector[1] = State.main_main_train_trainState_place_NotInStation;
		
		historyVector[4] = stateVector[1];
	}
	
	/* 'default' enter sequence for state NotSpeeding */
	private void enterSequence_main_main_train_trainState_place_InStation_Mode_NotSpeeding_default() {
		nextStateIndex = 1;
		stateVector[1] = State.main_main_train_trainState_place_InStation_Mode_NotSpeeding;
		
		historyVector[5] = stateVector[1];
	}
	
	/* 'default' enter sequence for state Speeding */
	private void enterSequence_main_main_train_trainState_place_InStation_Mode_Speeding_default() {
		entryAction_main_main_train_trainState_place_InStation_Mode_Speeding();
		nextStateIndex = 1;
		stateVector[1] = State.main_main_train_trainState_place_InStation_Mode_Speeding;
		
		historyVector[5] = stateVector[1];
	}
	
	/* 'default' enter sequence for state NotClosable */
	private void enterSequence_main_main_train_trainState_doors_OpenDoors_Status_NotClosable_default() {
		entryAction_main_main_train_trainState_doors_OpenDoors_Status_NotClosable();
		nextStateIndex = 2;
		stateVector[2] = State.main_main_train_trainState_doors_OpenDoors_Status_NotClosable;
		
		historyVector[7] = stateVector[2];
	}
	
	/* 'default' enter sequence for state Closable */
	private void enterSequence_main_main_train_trainState_doors_OpenDoors_Status_Closable_default() {
		entryAction_main_main_train_trainState_doors_OpenDoors_Status_Closable();
		nextStateIndex = 2;
		stateVector[2] = State.main_main_train_trainState_doors_OpenDoors_Status_Closable;
		
		historyVector[7] = stateVector[2];
	}
	
	/* 'default' enter sequence for state ClosedDoors */
	private void enterSequence_main_main_train_trainState_doors_ClosedDoors_default() {
		enterSequence_main_main_train_trainState_doors_ClosedDoors_status_default();
		historyVector[6] = stateVector[2];
	}
	
	/* 'default' enter sequence for state NotOpenable */
	private void enterSequence_main_main_train_trainState_doors_ClosedDoors_status_NotOpenable_default() {
		entryAction_main_main_train_trainState_doors_ClosedDoors_status_NotOpenable();
		nextStateIndex = 2;
		stateVector[2] = State.main_main_train_trainState_doors_ClosedDoors_status_NotOpenable;
		
		historyVector[8] = stateVector[2];
	}
	
	/* 'default' enter sequence for state DoorsOpenable */
	private void enterSequence_main_main_train_trainState_doors_ClosedDoors_status_DoorsOpenable_default() {
		entryAction_main_main_train_trainState_doors_ClosedDoors_status_DoorsOpenable();
		nextStateIndex = 2;
		stateVector[2] = State.main_main_train_trainState_doors_ClosedDoors_status_DoorsOpenable;
		
		historyVector[8] = stateVector[2];
	}
	
	/* 'default' enter sequence for state Still */
	private void enterSequence_main_main_train_trainState_movement_Still_default() {
		entryAction_main_main_train_trainState_movement_Still();
		nextStateIndex = 3;
		stateVector[3] = State.main_main_train_trainState_movement_Still;
		
		historyVector[9] = stateVector[3];
	}
	
	/* 'default' enter sequence for state Moving */
	private void enterSequence_main_main_train_trainState_movement_Moving_default() {
		enterSequence_main_main_train_trainState_movement_Moving_NormalMovement_default();
		historyVector[9] = stateVector[3];
	}
	
	/* 'default' enter sequence for state Cruising */
	private void enterSequence_main_main_train_trainState_movement_Moving_NormalMovement_Cruising_default() {
		entryAction_main_main_train_trainState_movement_Moving_NormalMovement_Cruising();
		nextStateIndex = 3;
		stateVector[3] = State.main_main_train_trainState_movement_Moving_NormalMovement_Cruising;
		
		historyVector[10] = stateVector[3];
	}
	
	/* 'default' enter sequence for state Driving */
	private void enterSequence_main_main_train_trainState_movement_Moving_NormalMovement_Driving_default() {
		entryAction_main_main_train_trainState_movement_Moving_NormalMovement_Driving();
		nextStateIndex = 3;
		stateVector[3] = State.main_main_train_trainState_movement_Moving_NormalMovement_Driving;
		
		historyVector[10] = stateVector[3];
	}
	
	/* 'default' enter sequence for state Breaking */
	private void enterSequence_main_main_train_trainState_movement_Breaking_default() {
		entryAction_main_main_train_trainState_movement_Breaking();
		nextStateIndex = 3;
		stateVector[3] = State.main_main_train_trainState_movement_Breaking;
		
		historyVector[9] = stateVector[3];
	}
	
	/* 'default' enter sequence for state Cooldown */
	private void enterSequence_main_main_train_trainState_movement_Cooldown_default() {
		entryAction_main_main_train_trainState_movement_Cooldown();
		nextStateIndex = 3;
		stateVector[3] = State.main_main_train_trainState_movement_Cooldown;
		
		historyVector[9] = stateVector[3];
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
		case main_main_train_trainState_Light_LastLight_Mode_Yellow_Mode_NotSpeeding:
			deepEnterSequence_main_main_train_trainState_Light();
			deepEnterSequence_main_main_train_trainState_place();
			deepEnterSequence_main_main_train_trainState_doors();
			deepEnterSequence_main_main_train_trainState_movement();
			break;
		case main_main_train_trainState_Light_LastLight_Mode_Yellow_Mode_Speeding:
			deepEnterSequence_main_main_train_trainState_Light();
			deepEnterSequence_main_main_train_trainState_place();
			deepEnterSequence_main_main_train_trainState_doors();
			deepEnterSequence_main_main_train_trainState_movement();
			break;
		case main_main_train_trainState_Light_LastLight_Mode_NotYellow:
			deepEnterSequence_main_main_train_trainState_Light();
			deepEnterSequence_main_main_train_trainState_place();
			deepEnterSequence_main_main_train_trainState_doors();
			deepEnterSequence_main_main_train_trainState_movement();
			break;
		default:
			break;
		}
	}
	
	/* 'default' enter sequence for region Light */
	private void enterSequence_main_main_train_trainState_Light_default() {
		react_main_main_train_trainState_Light__entry_Default();
	}
	
	/* deep enterSequence with history in child Light */
	private void deepEnterSequence_main_main_train_trainState_Light() {
		switch (historyVector[1]) {
		case main_main_train_trainState_Light_LastLight_Mode_Yellow_Mode_NotSpeeding:
			deepEnterSequence_main_main_train_trainState_Light_LastLight_Mode();
			break;
		case main_main_train_trainState_Light_LastLight_Mode_Yellow_Mode_Speeding:
			deepEnterSequence_main_main_train_trainState_Light_LastLight_Mode();
			break;
		case main_main_train_trainState_Light_LastLight_Mode_NotYellow:
			deepEnterSequence_main_main_train_trainState_Light_LastLight_Mode();
			break;
		default:
			break;
		}
	}
	
	/* 'default' enter sequence for region Mode */
	private void enterSequence_main_main_train_trainState_Light_LastLight_Mode_default() {
		react_main_main_train_trainState_Light_LastLight_Mode__entry_Default();
	}
	
	/* deep enterSequence with history in child Mode */
	private void deepEnterSequence_main_main_train_trainState_Light_LastLight_Mode() {
		switch (historyVector[2]) {
		case main_main_train_trainState_Light_LastLight_Mode_Yellow_Mode_NotSpeeding:
			deepEnterSequence_main_main_train_trainState_Light_LastLight_Mode_Yellow_Mode();
			break;
		case main_main_train_trainState_Light_LastLight_Mode_Yellow_Mode_Speeding:
			deepEnterSequence_main_main_train_trainState_Light_LastLight_Mode_Yellow_Mode();
			break;
		case main_main_train_trainState_Light_LastLight_Mode_NotYellow:
			enterSequence_main_main_train_trainState_Light_LastLight_Mode_NotYellow_default();
			break;
		default:
			break;
		}
	}
	
	/* 'default' enter sequence for region Mode */
	private void enterSequence_main_main_train_trainState_Light_LastLight_Mode_Yellow_Mode_default() {
		react_main_main_train_trainState_Light_LastLight_Mode_Yellow_Mode__entry_Default();
	}
	
	/* deep enterSequence with history in child Mode */
	private void deepEnterSequence_main_main_train_trainState_Light_LastLight_Mode_Yellow_Mode() {
		switch (historyVector[3]) {
		case main_main_train_trainState_Light_LastLight_Mode_Yellow_Mode_NotSpeeding:
			enterSequence_main_main_train_trainState_Light_LastLight_Mode_Yellow_Mode_NotSpeeding_default();
			break;
		case main_main_train_trainState_Light_LastLight_Mode_Yellow_Mode_Speeding:
			enterSequence_main_main_train_trainState_Light_LastLight_Mode_Yellow_Mode_Speeding_default();
			break;
		default:
			break;
		}
	}
	
	/* 'default' enter sequence for region place */
	private void enterSequence_main_main_train_trainState_place_default() {
		react_main_main_train_trainState_place__entry_Default();
	}
	
	/* deep enterSequence with history in child place */
	private void deepEnterSequence_main_main_train_trainState_place() {
		switch (historyVector[4]) {
		case main_main_train_trainState_place_NotInStation:
			enterSequence_main_main_train_trainState_place_NotInStation_default();
			break;
		case main_main_train_trainState_place_InStation_Mode_NotSpeeding:
			entryAction_main_main_train_trainState_place_InStation();
			deepEnterSequence_main_main_train_trainState_place_InStation_Mode();
			break;
		case main_main_train_trainState_place_InStation_Mode_Speeding:
			entryAction_main_main_train_trainState_place_InStation();
			deepEnterSequence_main_main_train_trainState_place_InStation_Mode();
			break;
		default:
			break;
		}
	}
	
	/* deep enterSequence with history in child Mode */
	private void deepEnterSequence_main_main_train_trainState_place_InStation_Mode() {
		switch (historyVector[5]) {
		case main_main_train_trainState_place_InStation_Mode_NotSpeeding:
			enterSequence_main_main_train_trainState_place_InStation_Mode_NotSpeeding_default();
			break;
		case main_main_train_trainState_place_InStation_Mode_Speeding:
			enterSequence_main_main_train_trainState_place_InStation_Mode_Speeding_default();
			break;
		default:
			break;
		}
	}
	
	/* 'default' enter sequence for region doors */
	private void enterSequence_main_main_train_trainState_doors_default() {
		react_main_main_train_trainState_doors__entry_Default();
	}
	
	/* deep enterSequence with history in child doors */
	private void deepEnterSequence_main_main_train_trainState_doors() {
		switch (historyVector[6]) {
		case main_main_train_trainState_doors_OpenDoors_Status_NotClosable:
			deepEnterSequence_main_main_train_trainState_doors_OpenDoors_Status();
			break;
		case main_main_train_trainState_doors_OpenDoors_Status_Closable:
			deepEnterSequence_main_main_train_trainState_doors_OpenDoors_Status();
			break;
		case main_main_train_trainState_doors_ClosedDoors_status_NotOpenable:
			deepEnterSequence_main_main_train_trainState_doors_ClosedDoors_status();
			break;
		case main_main_train_trainState_doors_ClosedDoors_status_DoorsOpenable:
			deepEnterSequence_main_main_train_trainState_doors_ClosedDoors_status();
			break;
		default:
			break;
		}
	}
	
	/* deep enterSequence with history in child Status */
	private void deepEnterSequence_main_main_train_trainState_doors_OpenDoors_Status() {
		switch (historyVector[7]) {
		case main_main_train_trainState_doors_OpenDoors_Status_NotClosable:
			enterSequence_main_main_train_trainState_doors_OpenDoors_Status_NotClosable_default();
			break;
		case main_main_train_trainState_doors_OpenDoors_Status_Closable:
			enterSequence_main_main_train_trainState_doors_OpenDoors_Status_Closable_default();
			break;
		default:
			break;
		}
	}
	
	/* 'default' enter sequence for region status */
	private void enterSequence_main_main_train_trainState_doors_ClosedDoors_status_default() {
		react_main_main_train_trainState_doors_ClosedDoors_status__entry_Default();
	}
	
	/* deep enterSequence with history in child status */
	private void deepEnterSequence_main_main_train_trainState_doors_ClosedDoors_status() {
		switch (historyVector[8]) {
		case main_main_train_trainState_doors_ClosedDoors_status_NotOpenable:
			enterSequence_main_main_train_trainState_doors_ClosedDoors_status_NotOpenable_default();
			break;
		case main_main_train_trainState_doors_ClosedDoors_status_DoorsOpenable:
			enterSequence_main_main_train_trainState_doors_ClosedDoors_status_DoorsOpenable_default();
			break;
		default:
			break;
		}
	}
	
	/* 'default' enter sequence for region movement */
	private void enterSequence_main_main_train_trainState_movement_default() {
		react_main_main_train_trainState_movement__entry_Default();
	}
	
	/* deep enterSequence with history in child movement */
	private void deepEnterSequence_main_main_train_trainState_movement() {
		switch (historyVector[9]) {
		case main_main_train_trainState_movement_Still:
			enterSequence_main_main_train_trainState_movement_Still_default();
			break;
		case main_main_train_trainState_movement_Moving_NormalMovement_Cruising:
			deepEnterSequence_main_main_train_trainState_movement_Moving_NormalMovement();
			break;
		case main_main_train_trainState_movement_Moving_NormalMovement_Driving:
			deepEnterSequence_main_main_train_trainState_movement_Moving_NormalMovement();
			break;
		case main_main_train_trainState_movement_Breaking:
			enterSequence_main_main_train_trainState_movement_Breaking_default();
			break;
		case main_main_train_trainState_movement_Cooldown:
			enterSequence_main_main_train_trainState_movement_Cooldown_default();
			break;
		default:
			break;
		}
	}
	
	/* 'default' enter sequence for region NormalMovement */
	private void enterSequence_main_main_train_trainState_movement_Moving_NormalMovement_default() {
		react_main_main_train_trainState_movement_Moving_NormalMovement__entry_Default();
	}
	
	/* deep enterSequence with history in child NormalMovement */
	private void deepEnterSequence_main_main_train_trainState_movement_Moving_NormalMovement() {
		switch (historyVector[10]) {
		case main_main_train_trainState_movement_Moving_NormalMovement_Cruising:
			enterSequence_main_main_train_trainState_movement_Moving_NormalMovement_Cruising_default();
			break;
		case main_main_train_trainState_movement_Moving_NormalMovement_Driving:
			enterSequence_main_main_train_trainState_movement_Moving_NormalMovement_Driving_default();
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
	
	/* Default exit sequence for state LastLight */
	private void exitSequence_main_main_train_trainState_Light_LastLight() {
		exitSequence_main_main_train_trainState_Light_LastLight_Mode();
	}
	
	/* Default exit sequence for state NotSpeeding */
	private void exitSequence_main_main_train_trainState_Light_LastLight_Mode_Yellow_Mode_NotSpeeding() {
		nextStateIndex = 0;
		stateVector[0] = State.$NullState$;
	}
	
	/* Default exit sequence for state Speeding */
	private void exitSequence_main_main_train_trainState_Light_LastLight_Mode_Yellow_Mode_Speeding() {
		nextStateIndex = 0;
		stateVector[0] = State.$NullState$;
	}
	
	/* Default exit sequence for state NotYellow */
	private void exitSequence_main_main_train_trainState_Light_LastLight_Mode_NotYellow() {
		nextStateIndex = 0;
		stateVector[0] = State.$NullState$;
	}
	
	/* Default exit sequence for state NotInStation */
	private void exitSequence_main_main_train_trainState_place_NotInStation() {
		nextStateIndex = 1;
		stateVector[1] = State.$NullState$;
	}
	
	/* Default exit sequence for state InStation */
	private void exitSequence_main_main_train_trainState_place_InStation() {
		exitSequence_main_main_train_trainState_place_InStation_Mode();
	}
	
	/* Default exit sequence for state NotSpeeding */
	private void exitSequence_main_main_train_trainState_place_InStation_Mode_NotSpeeding() {
		nextStateIndex = 1;
		stateVector[1] = State.$NullState$;
	}
	
	/* Default exit sequence for state Speeding */
	private void exitSequence_main_main_train_trainState_place_InStation_Mode_Speeding() {
		nextStateIndex = 1;
		stateVector[1] = State.$NullState$;
	}
	
	/* Default exit sequence for state OpenDoors */
	private void exitSequence_main_main_train_trainState_doors_OpenDoors() {
		exitSequence_main_main_train_trainState_doors_OpenDoors_Status();
	}
	
	/* Default exit sequence for state NotClosable */
	private void exitSequence_main_main_train_trainState_doors_OpenDoors_Status_NotClosable() {
		nextStateIndex = 2;
		stateVector[2] = State.$NullState$;
		
		exitAction_main_main_train_trainState_doors_OpenDoors_Status_NotClosable();
	}
	
	/* Default exit sequence for state Closable */
	private void exitSequence_main_main_train_trainState_doors_OpenDoors_Status_Closable() {
		nextStateIndex = 2;
		stateVector[2] = State.$NullState$;
	}
	
	/* Default exit sequence for state ClosedDoors */
	private void exitSequence_main_main_train_trainState_doors_ClosedDoors() {
		exitSequence_main_main_train_trainState_doors_ClosedDoors_status();
	}
	
	/* Default exit sequence for state NotOpenable */
	private void exitSequence_main_main_train_trainState_doors_ClosedDoors_status_NotOpenable() {
		nextStateIndex = 2;
		stateVector[2] = State.$NullState$;
	}
	
	/* Default exit sequence for state DoorsOpenable */
	private void exitSequence_main_main_train_trainState_doors_ClosedDoors_status_DoorsOpenable() {
		nextStateIndex = 2;
		stateVector[2] = State.$NullState$;
	}
	
	/* Default exit sequence for state Still */
	private void exitSequence_main_main_train_trainState_movement_Still() {
		nextStateIndex = 3;
		stateVector[3] = State.$NullState$;
	}
	
	/* Default exit sequence for state Moving */
	private void exitSequence_main_main_train_trainState_movement_Moving() {
		exitSequence_main_main_train_trainState_movement_Moving_NormalMovement();
	}
	
	/* Default exit sequence for state Cruising */
	private void exitSequence_main_main_train_trainState_movement_Moving_NormalMovement_Cruising() {
		nextStateIndex = 3;
		stateVector[3] = State.$NullState$;
	}
	
	/* Default exit sequence for state Driving */
	private void exitSequence_main_main_train_trainState_movement_Moving_NormalMovement_Driving() {
		nextStateIndex = 3;
		stateVector[3] = State.$NullState$;
		
		exitAction_main_main_train_trainState_movement_Moving_NormalMovement_Driving();
	}
	
	/* Default exit sequence for state Breaking */
	private void exitSequence_main_main_train_trainState_movement_Breaking() {
		nextStateIndex = 3;
		stateVector[3] = State.$NullState$;
	}
	
	/* Default exit sequence for state Cooldown */
	private void exitSequence_main_main_train_trainState_movement_Cooldown() {
		nextStateIndex = 3;
		stateVector[3] = State.$NullState$;
		
		exitAction_main_main_train_trainState_movement_Cooldown();
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
		case main_main_train_trainState_Light_LastLight_Mode_Yellow_Mode_NotSpeeding:
			exitSequence_main_main_train_trainState_Light_LastLight_Mode_Yellow_Mode_NotSpeeding();
			break;
		case main_main_train_trainState_Light_LastLight_Mode_Yellow_Mode_Speeding:
			exitSequence_main_main_train_trainState_Light_LastLight_Mode_Yellow_Mode_Speeding();
			break;
		case main_main_train_trainState_Light_LastLight_Mode_NotYellow:
			exitSequence_main_main_train_trainState_Light_LastLight_Mode_NotYellow();
			break;
		default:
			break;
		}
		
		switch (stateVector[1]) {
		case main_main_train_trainState_place_NotInStation:
			exitSequence_main_main_train_trainState_place_NotInStation();
			break;
		case main_main_train_trainState_place_InStation_Mode_NotSpeeding:
			exitSequence_main_main_train_trainState_place_InStation_Mode_NotSpeeding();
			break;
		case main_main_train_trainState_place_InStation_Mode_Speeding:
			exitSequence_main_main_train_trainState_place_InStation_Mode_Speeding();
			break;
		default:
			break;
		}
		
		switch (stateVector[2]) {
		case main_main_train_trainState_doors_OpenDoors_Status_NotClosable:
			exitSequence_main_main_train_trainState_doors_OpenDoors_Status_NotClosable();
			break;
		case main_main_train_trainState_doors_OpenDoors_Status_Closable:
			exitSequence_main_main_train_trainState_doors_OpenDoors_Status_Closable();
			break;
		case main_main_train_trainState_doors_ClosedDoors_status_NotOpenable:
			exitSequence_main_main_train_trainState_doors_ClosedDoors_status_NotOpenable();
			break;
		case main_main_train_trainState_doors_ClosedDoors_status_DoorsOpenable:
			exitSequence_main_main_train_trainState_doors_ClosedDoors_status_DoorsOpenable();
			break;
		default:
			break;
		}
		
		switch (stateVector[3]) {
		case main_main_train_trainState_movement_Still:
			exitSequence_main_main_train_trainState_movement_Still();
			break;
		case main_main_train_trainState_movement_Moving_NormalMovement_Cruising:
			exitSequence_main_main_train_trainState_movement_Moving_NormalMovement_Cruising();
			break;
		case main_main_train_trainState_movement_Moving_NormalMovement_Driving:
			exitSequence_main_main_train_trainState_movement_Moving_NormalMovement_Driving();
			break;
		case main_main_train_trainState_movement_Breaking:
			exitSequence_main_main_train_trainState_movement_Breaking();
			break;
		case main_main_train_trainState_movement_Cooldown:
			exitSequence_main_main_train_trainState_movement_Cooldown();
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
		case main_main_train_trainState_Light_LastLight_Mode_Yellow_Mode_NotSpeeding:
			exitSequence_main_main_train_trainState_Light_LastLight_Mode_Yellow_Mode_NotSpeeding();
			break;
		case main_main_train_trainState_Light_LastLight_Mode_Yellow_Mode_Speeding:
			exitSequence_main_main_train_trainState_Light_LastLight_Mode_Yellow_Mode_Speeding();
			break;
		case main_main_train_trainState_Light_LastLight_Mode_NotYellow:
			exitSequence_main_main_train_trainState_Light_LastLight_Mode_NotYellow();
			break;
		default:
			break;
		}
		
		switch (stateVector[1]) {
		case main_main_train_trainState_place_NotInStation:
			exitSequence_main_main_train_trainState_place_NotInStation();
			break;
		case main_main_train_trainState_place_InStation_Mode_NotSpeeding:
			exitSequence_main_main_train_trainState_place_InStation_Mode_NotSpeeding();
			break;
		case main_main_train_trainState_place_InStation_Mode_Speeding:
			exitSequence_main_main_train_trainState_place_InStation_Mode_Speeding();
			break;
		default:
			break;
		}
		
		switch (stateVector[2]) {
		case main_main_train_trainState_doors_OpenDoors_Status_NotClosable:
			exitSequence_main_main_train_trainState_doors_OpenDoors_Status_NotClosable();
			break;
		case main_main_train_trainState_doors_OpenDoors_Status_Closable:
			exitSequence_main_main_train_trainState_doors_OpenDoors_Status_Closable();
			break;
		case main_main_train_trainState_doors_ClosedDoors_status_NotOpenable:
			exitSequence_main_main_train_trainState_doors_ClosedDoors_status_NotOpenable();
			break;
		case main_main_train_trainState_doors_ClosedDoors_status_DoorsOpenable:
			exitSequence_main_main_train_trainState_doors_ClosedDoors_status_DoorsOpenable();
			break;
		default:
			break;
		}
		
		switch (stateVector[3]) {
		case main_main_train_trainState_movement_Still:
			exitSequence_main_main_train_trainState_movement_Still();
			break;
		case main_main_train_trainState_movement_Moving_NormalMovement_Cruising:
			exitSequence_main_main_train_trainState_movement_Moving_NormalMovement_Cruising();
			break;
		case main_main_train_trainState_movement_Moving_NormalMovement_Driving:
			exitSequence_main_main_train_trainState_movement_Moving_NormalMovement_Driving();
			break;
		case main_main_train_trainState_movement_Breaking:
			exitSequence_main_main_train_trainState_movement_Breaking();
			break;
		case main_main_train_trainState_movement_Cooldown:
			exitSequence_main_main_train_trainState_movement_Cooldown();
			break;
		default:
			break;
		}
	}
	
	/* Default exit sequence for region Light */
	private void exitSequence_main_main_train_trainState_Light() {
		switch (stateVector[0]) {
		case main_main_train_trainState_Light_LastLight_Mode_Yellow_Mode_NotSpeeding:
			exitSequence_main_main_train_trainState_Light_LastLight_Mode_Yellow_Mode_NotSpeeding();
			break;
		case main_main_train_trainState_Light_LastLight_Mode_Yellow_Mode_Speeding:
			exitSequence_main_main_train_trainState_Light_LastLight_Mode_Yellow_Mode_Speeding();
			break;
		case main_main_train_trainState_Light_LastLight_Mode_NotYellow:
			exitSequence_main_main_train_trainState_Light_LastLight_Mode_NotYellow();
			break;
		default:
			break;
		}
	}
	
	/* Default exit sequence for region Mode */
	private void exitSequence_main_main_train_trainState_Light_LastLight_Mode() {
		switch (stateVector[0]) {
		case main_main_train_trainState_Light_LastLight_Mode_Yellow_Mode_NotSpeeding:
			exitSequence_main_main_train_trainState_Light_LastLight_Mode_Yellow_Mode_NotSpeeding();
			break;
		case main_main_train_trainState_Light_LastLight_Mode_Yellow_Mode_Speeding:
			exitSequence_main_main_train_trainState_Light_LastLight_Mode_Yellow_Mode_Speeding();
			break;
		case main_main_train_trainState_Light_LastLight_Mode_NotYellow:
			exitSequence_main_main_train_trainState_Light_LastLight_Mode_NotYellow();
			break;
		default:
			break;
		}
	}
	
	/* Default exit sequence for region Mode */
	private void exitSequence_main_main_train_trainState_Light_LastLight_Mode_Yellow_Mode() {
		switch (stateVector[0]) {
		case main_main_train_trainState_Light_LastLight_Mode_Yellow_Mode_NotSpeeding:
			exitSequence_main_main_train_trainState_Light_LastLight_Mode_Yellow_Mode_NotSpeeding();
			break;
		case main_main_train_trainState_Light_LastLight_Mode_Yellow_Mode_Speeding:
			exitSequence_main_main_train_trainState_Light_LastLight_Mode_Yellow_Mode_Speeding();
			break;
		default:
			break;
		}
	}
	
	/* Default exit sequence for region place */
	private void exitSequence_main_main_train_trainState_place() {
		switch (stateVector[1]) {
		case main_main_train_trainState_place_NotInStation:
			exitSequence_main_main_train_trainState_place_NotInStation();
			break;
		case main_main_train_trainState_place_InStation_Mode_NotSpeeding:
			exitSequence_main_main_train_trainState_place_InStation_Mode_NotSpeeding();
			break;
		case main_main_train_trainState_place_InStation_Mode_Speeding:
			exitSequence_main_main_train_trainState_place_InStation_Mode_Speeding();
			break;
		default:
			break;
		}
	}
	
	/* Default exit sequence for region Mode */
	private void exitSequence_main_main_train_trainState_place_InStation_Mode() {
		switch (stateVector[1]) {
		case main_main_train_trainState_place_InStation_Mode_NotSpeeding:
			exitSequence_main_main_train_trainState_place_InStation_Mode_NotSpeeding();
			break;
		case main_main_train_trainState_place_InStation_Mode_Speeding:
			exitSequence_main_main_train_trainState_place_InStation_Mode_Speeding();
			break;
		default:
			break;
		}
	}
	
	/* Default exit sequence for region doors */
	private void exitSequence_main_main_train_trainState_doors() {
		switch (stateVector[2]) {
		case main_main_train_trainState_doors_OpenDoors_Status_NotClosable:
			exitSequence_main_main_train_trainState_doors_OpenDoors_Status_NotClosable();
			break;
		case main_main_train_trainState_doors_OpenDoors_Status_Closable:
			exitSequence_main_main_train_trainState_doors_OpenDoors_Status_Closable();
			break;
		case main_main_train_trainState_doors_ClosedDoors_status_NotOpenable:
			exitSequence_main_main_train_trainState_doors_ClosedDoors_status_NotOpenable();
			break;
		case main_main_train_trainState_doors_ClosedDoors_status_DoorsOpenable:
			exitSequence_main_main_train_trainState_doors_ClosedDoors_status_DoorsOpenable();
			break;
		default:
			break;
		}
	}
	
	/* Default exit sequence for region Status */
	private void exitSequence_main_main_train_trainState_doors_OpenDoors_Status() {
		switch (stateVector[2]) {
		case main_main_train_trainState_doors_OpenDoors_Status_NotClosable:
			exitSequence_main_main_train_trainState_doors_OpenDoors_Status_NotClosable();
			break;
		case main_main_train_trainState_doors_OpenDoors_Status_Closable:
			exitSequence_main_main_train_trainState_doors_OpenDoors_Status_Closable();
			break;
		default:
			break;
		}
	}
	
	/* Default exit sequence for region status */
	private void exitSequence_main_main_train_trainState_doors_ClosedDoors_status() {
		switch (stateVector[2]) {
		case main_main_train_trainState_doors_ClosedDoors_status_NotOpenable:
			exitSequence_main_main_train_trainState_doors_ClosedDoors_status_NotOpenable();
			break;
		case main_main_train_trainState_doors_ClosedDoors_status_DoorsOpenable:
			exitSequence_main_main_train_trainState_doors_ClosedDoors_status_DoorsOpenable();
			break;
		default:
			break;
		}
	}
	
	/* Default exit sequence for region movement */
	private void exitSequence_main_main_train_trainState_movement() {
		switch (stateVector[3]) {
		case main_main_train_trainState_movement_Still:
			exitSequence_main_main_train_trainState_movement_Still();
			break;
		case main_main_train_trainState_movement_Moving_NormalMovement_Cruising:
			exitSequence_main_main_train_trainState_movement_Moving_NormalMovement_Cruising();
			break;
		case main_main_train_trainState_movement_Moving_NormalMovement_Driving:
			exitSequence_main_main_train_trainState_movement_Moving_NormalMovement_Driving();
			break;
		case main_main_train_trainState_movement_Breaking:
			exitSequence_main_main_train_trainState_movement_Breaking();
			break;
		case main_main_train_trainState_movement_Cooldown:
			exitSequence_main_main_train_trainState_movement_Cooldown();
			break;
		default:
			break;
		}
	}
	
	/* Default exit sequence for region NormalMovement */
	private void exitSequence_main_main_train_trainState_movement_Moving_NormalMovement() {
		switch (stateVector[3]) {
		case main_main_train_trainState_movement_Moving_NormalMovement_Cruising:
			exitSequence_main_main_train_trainState_movement_Moving_NormalMovement_Cruising();
			break;
		case main_main_train_trainState_movement_Moving_NormalMovement_Driving:
			exitSequence_main_main_train_trainState_movement_Moving_NormalMovement_Driving();
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
		setStoredAcc(0);
		
		setStoredVel(0);
		
		enterSequence_main_default_default();
	}
	
	/* Default react sequence for initial entry  */
	private void react_main_main_train_trainState_Light__entry_Default() {
		enterSequence_main_main_train_trainState_Light_LastLight_default();
	}
	
	/* Default react sequence for initial entry  */
	private void react_main_main_train_trainState_Light_LastLight_Mode_Yellow_Mode__entry_Default() {
		enterSequence_main_main_train_trainState_Light_LastLight_Mode_Yellow_Mode_NotSpeeding_default();
	}
	
	/* Default react sequence for initial entry  */
	private void react_main_main_train_trainState_Light_LastLight_Mode__entry_Default() {
		enterSequence_main_main_train_trainState_Light_LastLight_Mode_NotYellow_default();
	}
	
	/* Default react sequence for initial entry  */
	private void react_main_main_train_trainState_place__entry_Default() {
		enterSequence_main_main_train_trainState_place_NotInStation_default();
	}
	
	/* Default react sequence for initial entry  */
	private void react_main_main_train_trainState_doors__entry_Default() {
		enterSequence_main_main_train_trainState_doors_ClosedDoors_default();
	}
	
	/* Default react sequence for initial entry  */
	private void react_main_main_train_trainState_doors_ClosedDoors_status__entry_Default() {
		enterSequence_main_main_train_trainState_doors_ClosedDoors_status_NotOpenable_default();
	}
	
	/* Default react sequence for initial entry  */
	private void react_main_main_train_trainState_movement_Moving_NormalMovement__entry_Default() {
		enterSequence_main_main_train_trainState_movement_Moving_NormalMovement_Driving_default();
	}
	
	/* Default react sequence for initial entry  */
	private void react_main_main_train_trainState_movement__entry_Default() {
		enterSequence_main_main_train_trainState_movement_Still_default();
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
					sCInterface.operationCallback.print("Continue");
					
					setAcceleration(storedAcc);
					
					sCInterface.setVelocity(storedVel);
					
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
					sCInterface.operationCallback.print("Pause");
					
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
				did_transition = false;
			}
		}
		if (did_transition==false) {
		}
		return did_transition;
	}
	
	private boolean main_main_train_trainState_Light_LastLight_react(boolean try_transition) {
		boolean did_transition = try_transition;
		
		if (try_transition) {
			if (main_main_train_trainState_react(try_transition)==false) {
				if (sCInterface.green_light) {
					exitSequence_main_main_train_trainState_Light_LastLight();
					enterSequence_main_main_train_trainState_Light_LastLight_default();
				} else {
					if (sCInterface.red_light) {
						exitSequence_main_main_train_trainState_Light_LastLight();
						raiseEmergency();
						
						sCInterface.operationCallback.print("Red_light");
						
						enterSequence_main_main_train_trainState_Light_LastLight_default();
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
	
	private boolean main_main_train_trainState_Light_LastLight_Mode_Yellow_react(boolean try_transition) {
		boolean did_transition = try_transition;
		
		if (try_transition) {
			if (main_main_train_trainState_Light_LastLight_react(try_transition)==false) {
				did_transition = false;
			}
		}
		if (did_transition==false) {
		}
		return did_transition;
	}
	
	private boolean main_main_train_trainState_Light_LastLight_Mode_Yellow_Mode_NotSpeeding_react(boolean try_transition) {
		boolean did_transition = try_transition;
		
		if (try_transition) {
			if (main_main_train_trainState_Light_LastLight_Mode_Yellow_react(try_transition)==false) {
				if (sCInterface.getVelocity()>50) {
					exitSequence_main_main_train_trainState_Light_LastLight_Mode_Yellow_Mode_NotSpeeding();
					raiseEmergency();
					
					sCInterface.operationCallback.print("speeding");
					
					enterSequence_main_main_train_trainState_Light_LastLight_Mode_Yellow_Mode_Speeding_default();
				} else {
					did_transition = false;
				}
			}
		}
		if (did_transition==false) {
		}
		return did_transition;
	}
	
	private boolean main_main_train_trainState_Light_LastLight_Mode_Yellow_Mode_Speeding_react(boolean try_transition) {
		boolean did_transition = try_transition;
		
		if (try_transition) {
			if (main_main_train_trainState_Light_LastLight_Mode_Yellow_react(try_transition)==false) {
				if (sCInterface.getVelocity()<50) {
					exitSequence_main_main_train_trainState_Light_LastLight_Mode_Yellow_Mode_Speeding();
					enterSequence_main_main_train_trainState_Light_LastLight_Mode_Yellow_Mode_NotSpeeding_default();
				} else {
					did_transition = false;
				}
			}
		}
		if (did_transition==false) {
		}
		return did_transition;
	}
	
	private boolean main_main_train_trainState_Light_LastLight_Mode_NotYellow_react(boolean try_transition) {
		boolean did_transition = try_transition;
		
		if (try_transition) {
			if (main_main_train_trainState_Light_LastLight_react(try_transition)==false) {
				if (sCInterface.yellow_light) {
					exitSequence_main_main_train_trainState_Light_LastLight_Mode_NotYellow();
					enterSequence_main_main_train_trainState_Light_LastLight_Mode_Yellow_default();
				} else {
					did_transition = false;
				}
			}
		}
		if (did_transition==false) {
		}
		return did_transition;
	}
	
	private boolean main_main_train_trainState_place_NotInStation_react(boolean try_transition) {
		boolean did_transition = try_transition;
		
		if (try_transition) {
			if (sCInterface.enter) {
				exitSequence_main_main_train_trainState_place_NotInStation();
				raiseToOpenableDoors();
				
				entryAction_main_main_train_trainState_place_InStation();
				enterSequence_main_main_train_trainState_place_InStation_Mode_NotSpeeding_default();
				historyVector[4] = stateVector[1];
			} else {
				did_transition = false;
			}
		}
		if (did_transition==false) {
		}
		return did_transition;
	}
	
	private boolean main_main_train_trainState_place_InStation_react(boolean try_transition) {
		boolean did_transition = try_transition;
		
		if (try_transition) {
			if (sCInterface.leave) {
				exitSequence_main_main_train_trainState_place_InStation();
				enterSequence_main_main_train_trainState_place_NotInStation_default();
			} else {
				did_transition = false;
			}
		}
		if (did_transition==false) {
		}
		return did_transition;
	}
	
	private boolean main_main_train_trainState_place_InStation_Mode_NotSpeeding_react(boolean try_transition) {
		boolean did_transition = try_transition;
		
		if (try_transition) {
			if (main_main_train_trainState_place_InStation_react(try_transition)==false) {
				if (sCInterface.getVelocity()>20) {
					exitSequence_main_main_train_trainState_place_InStation_Mode_NotSpeeding();
					raiseEmergency();
					
					enterSequence_main_main_train_trainState_place_InStation_Mode_Speeding_default();
				} else {
					did_transition = false;
				}
			}
		}
		if (did_transition==false) {
		}
		return did_transition;
	}
	
	private boolean main_main_train_trainState_place_InStation_Mode_Speeding_react(boolean try_transition) {
		boolean did_transition = try_transition;
		
		if (try_transition) {
			if (main_main_train_trainState_place_InStation_react(try_transition)==false) {
				did_transition = false;
			}
		}
		if (did_transition==false) {
		}
		return did_transition;
	}
	
	private boolean main_main_train_trainState_doors_OpenDoors_react(boolean try_transition) {
		boolean did_transition = try_transition;
		
		if (try_transition) {
			did_transition = false;
		}
		if (did_transition==false) {
		}
		return did_transition;
	}
	
	private boolean main_main_train_trainState_doors_OpenDoors_Status_NotClosable_react(boolean try_transition) {
		boolean did_transition = try_transition;
		
		if (try_transition) {
			if (main_main_train_trainState_doors_OpenDoors_react(try_transition)==false) {
				if (timeEvents[0]) {
					exitSequence_main_main_train_trainState_doors_OpenDoors_Status_NotClosable();
					enterSequence_main_main_train_trainState_doors_OpenDoors_Status_Closable_default();
				} else {
					did_transition = false;
				}
			}
		}
		if (did_transition==false) {
		}
		return did_transition;
	}
	
	private boolean main_main_train_trainState_doors_OpenDoors_Status_Closable_react(boolean try_transition) {
		boolean did_transition = try_transition;
		
		if (try_transition) {
			if (main_main_train_trainState_doors_OpenDoors_react(try_transition)==false) {
				if (sCInterface.close) {
					exitSequence_main_main_train_trainState_doors_OpenDoors();
					sCInterface.raiseCloseDoors();
					
					enterSequence_main_main_train_trainState_doors_ClosedDoors_status_NotOpenable_default();
					historyVector[6] = stateVector[2];
				} else {
					did_transition = false;
				}
			}
		}
		if (did_transition==false) {
		}
		return did_transition;
	}
	
	private boolean main_main_train_trainState_doors_ClosedDoors_react(boolean try_transition) {
		boolean did_transition = try_transition;
		
		if (try_transition) {
			did_transition = false;
		}
		if (did_transition==false) {
		}
		return did_transition;
	}
	
	private boolean main_main_train_trainState_doors_ClosedDoors_status_NotOpenable_react(boolean try_transition) {
		boolean did_transition = try_transition;
		
		if (try_transition) {
			if (main_main_train_trainState_doors_ClosedDoors_react(try_transition)==false) {
				if (sCInterface.enter) {
					exitSequence_main_main_train_trainState_doors_ClosedDoors_status_NotOpenable();
					enterSequence_main_main_train_trainState_doors_ClosedDoors_status_DoorsOpenable_default();
				} else {
					did_transition = false;
				}
			}
		}
		if (did_transition==false) {
		}
		return did_transition;
	}
	
	private boolean main_main_train_trainState_doors_ClosedDoors_status_DoorsOpenable_react(boolean try_transition) {
		boolean did_transition = try_transition;
		
		if (try_transition) {
			if (main_main_train_trainState_doors_ClosedDoors_react(try_transition)==false) {
				if ((sCInterface.open) && (isStateActive(State.main_main_train_trainState_place_InStation) && isStateActive(State.main_main_train_trainState_movement_Still))) {
					exitSequence_main_main_train_trainState_doors_ClosedDoors();
					sCInterface.raiseOpenDoors();
					
					enterSequence_main_main_train_trainState_doors_OpenDoors_Status_NotClosable_default();
					historyVector[6] = stateVector[2];
				} else {
					did_transition = false;
				}
			}
		}
		if (did_transition==false) {
		}
		return did_transition;
	}
	
	private boolean main_main_train_trainState_movement_Still_react(boolean try_transition) {
		boolean did_transition = try_transition;
		
		if (try_transition) {
			if ((sCInterface.update_acceleration) && (sCInterface.getUpdate_accelerationValue()>0.0 && isStateActive(State.main_main_train_trainState_doors_ClosedDoors))) {
				exitSequence_main_main_train_trainState_movement_Still();
				sCInterface.setVelocity(0.001);
				
				setAcceleration(sCInterface.getUpdate_accelerationValue());
				
				enterSequence_main_main_train_trainState_movement_Moving_default();
			} else {
				did_transition = false;
			}
		}
		if (did_transition==false) {
		}
		return did_transition;
	}
	
	private boolean main_main_train_trainState_movement_Moving_react(boolean try_transition) {
		boolean did_transition = try_transition;
		
		if (try_transition) {
			if (sCInterface.getVelocity()<=0) {
				exitSequence_main_main_train_trainState_movement_Moving();
				enterSequence_main_main_train_trainState_movement_Still_default();
			} else {
				if (emergency) {
					exitSequence_main_main_train_trainState_movement_Moving();
					sCInterface.operationCallback.print("Event:Emergency");
					
					enterSequence_main_main_train_trainState_movement_Breaking_default();
				} else {
					did_transition = false;
				}
			}
		}
		if (did_transition==false) {
		}
		return did_transition;
	}
	
	private boolean main_main_train_trainState_movement_Moving_NormalMovement_Cruising_react(boolean try_transition) {
		boolean did_transition = try_transition;
		
		if (try_transition) {
			if (main_main_train_trainState_movement_Moving_react(try_transition)==false) {
				if ((sCInterface.update_acceleration) && (sCInterface.getUpdate_accelerationValue()<0.0)) {
					exitSequence_main_main_train_trainState_movement_Moving_NormalMovement_Cruising();
					setAcceleration(sCInterface.getUpdate_accelerationValue());
					
					enterSequence_main_main_train_trainState_movement_Moving_NormalMovement_Driving_default();
				} else {
					did_transition = false;
				}
			}
		}
		if (did_transition==false) {
		}
		return did_transition;
	}
	
	private boolean main_main_train_trainState_movement_Moving_NormalMovement_Driving_react(boolean try_transition) {
		boolean did_transition = try_transition;
		
		if (try_transition) {
			if (main_main_train_trainState_movement_Moving_react(try_transition)==false) {
				if (sCInterface.update_acceleration) {
					exitSequence_main_main_train_trainState_movement_Moving_NormalMovement_Driving();
					setAcceleration(sCInterface.getUpdate_accelerationValue());
					
					enterSequence_main_main_train_trainState_movement_Moving_NormalMovement_Driving_default();
				} else {
					if (sCInterface.getVelocity()>=100.0) {
						exitSequence_main_main_train_trainState_movement_Moving_NormalMovement_Driving();
						enterSequence_main_main_train_trainState_movement_Moving_NormalMovement_Cruising_default();
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
	
	private boolean main_main_train_trainState_movement_Breaking_react(boolean try_transition) {
		boolean did_transition = try_transition;
		
		if (try_transition) {
			if (sCInterface.getVelocity()<=0) {
				exitSequence_main_main_train_trainState_movement_Breaking();
				enterSequence_main_main_train_trainState_movement_Cooldown_default();
			} else {
				did_transition = false;
			}
		}
		if (did_transition==false) {
		}
		return did_transition;
	}
	
	private boolean main_main_train_trainState_movement_Cooldown_react(boolean try_transition) {
		boolean did_transition = try_transition;
		
		if (try_transition) {
			if (timeEvents[1]) {
				exitSequence_main_main_train_trainState_movement_Cooldown();
				enterSequence_main_main_train_trainState_movement_Still_default();
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
			case main_main_train_trainState_Light_LastLight_Mode_Yellow_Mode_NotSpeeding:
				main_main_train_trainState_Light_LastLight_Mode_Yellow_Mode_NotSpeeding_react(true);
				break;
			case main_main_train_trainState_Light_LastLight_Mode_Yellow_Mode_Speeding:
				main_main_train_trainState_Light_LastLight_Mode_Yellow_Mode_Speeding_react(true);
				break;
			case main_main_train_trainState_Light_LastLight_Mode_NotYellow:
				main_main_train_trainState_Light_LastLight_Mode_NotYellow_react(true);
				break;
			case main_main_train_trainState_place_NotInStation:
				main_main_train_trainState_place_NotInStation_react(true);
				break;
			case main_main_train_trainState_place_InStation_Mode_NotSpeeding:
				main_main_train_trainState_place_InStation_Mode_NotSpeeding_react(true);
				break;
			case main_main_train_trainState_place_InStation_Mode_Speeding:
				main_main_train_trainState_place_InStation_Mode_Speeding_react(true);
				break;
			case main_main_train_trainState_doors_OpenDoors_Status_NotClosable:
				main_main_train_trainState_doors_OpenDoors_Status_NotClosable_react(true);
				break;
			case main_main_train_trainState_doors_OpenDoors_Status_Closable:
				main_main_train_trainState_doors_OpenDoors_Status_Closable_react(true);
				break;
			case main_main_train_trainState_doors_ClosedDoors_status_NotOpenable:
				main_main_train_trainState_doors_ClosedDoors_status_NotOpenable_react(true);
				break;
			case main_main_train_trainState_doors_ClosedDoors_status_DoorsOpenable:
				main_main_train_trainState_doors_ClosedDoors_status_DoorsOpenable_react(true);
				break;
			case main_main_train_trainState_movement_Still:
				main_main_train_trainState_movement_Still_react(true);
				break;
			case main_main_train_trainState_movement_Moving_NormalMovement_Cruising:
				main_main_train_trainState_movement_Moving_NormalMovement_Cruising_react(true);
				break;
			case main_main_train_trainState_movement_Moving_NormalMovement_Driving:
				main_main_train_trainState_movement_Moving_NormalMovement_Driving_react(true);
				break;
			case main_main_train_trainState_movement_Breaking:
				main_main_train_trainState_movement_Breaking_react(true);
				break;
			case main_main_train_trainState_movement_Cooldown:
				main_main_train_trainState_movement_Cooldown_react(true);
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
