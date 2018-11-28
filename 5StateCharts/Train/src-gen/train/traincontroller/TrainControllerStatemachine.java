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
		main_main_train_Emergency_Brake_Mode,
		main_main_train_Yellow_Light_Normal,
		main_main_train_normal,
		main_main_train_normal_movement_train_Still,
		main_main_train_normal_movement_train_Driving,
		main_main_train_normal_movement_train_Cruising,
		main_main_train_In_Station,
		main_main_train_Doors_Opened,
		main_main_dead_man_s_button_Normal,
		main_main_dead_man_s_button_Button_Prompted,
		main_main_dead_man_s_button_Emergency_Brake_Mode,
		$NullState$
	};
	
	private State[] historyVector = new State[1];
	private final State[] stateVector = new State[2];
	
	private int nextStateIndex;
	
	private ITimer timer;
	
	private final boolean[] timeEvents = new boolean[4];
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
		
		for (int i = 0; i < 2; i++) {
			stateVector[i] = State.$NullState$;
		}
		for (int i = 0; i < 1; i++) {
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
		return stateVector[0] != State.$NullState$||stateVector[1] != State.$NullState$;
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
		case main_main_train_Emergency_Brake_Mode:
			return stateVector[0] == State.main_main_train_Emergency_Brake_Mode;
		case main_main_train_Yellow_Light_Normal:
			return stateVector[0] == State.main_main_train_Yellow_Light_Normal;
		case main_main_train_normal:
			return stateVector[0].ordinal() >= State.
					main_main_train_normal.ordinal()&& stateVector[0].ordinal() <= State.main_main_train_normal_movement_train_Cruising.ordinal();
		case main_main_train_normal_movement_train_Still:
			return stateVector[0] == State.main_main_train_normal_movement_train_Still;
		case main_main_train_normal_movement_train_Driving:
			return stateVector[0] == State.main_main_train_normal_movement_train_Driving;
		case main_main_train_normal_movement_train_Cruising:
			return stateVector[0] == State.main_main_train_normal_movement_train_Cruising;
		case main_main_train_In_Station:
			return stateVector[0] == State.main_main_train_In_Station;
		case main_main_train_Doors_Opened:
			return stateVector[0] == State.main_main_train_Doors_Opened;
		case main_main_dead_man_s_button_Normal:
			return stateVector[1] == State.main_main_dead_man_s_button_Normal;
		case main_main_dead_man_s_button_Button_Prompted:
			return stateVector[1] == State.main_main_dead_man_s_button_Button_Prompted;
		case main_main_dead_man_s_button_Emergency_Brake_Mode:
			return stateVector[1] == State.main_main_dead_man_s_button_Emergency_Brake_Mode;
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
		timer.setTimer(this, 3, 20, true);
	}
	
	/* Entry action for state 'Emergency Brake Mode'. */
	private void entryAction_main_main_train_Emergency_Brake_Mode() {
		setAcceleration(-1.0);
		
		sCInterface.operationCallback.print("In Emergency Brake Mode, acceleration=-1");
	}
	
	/* Entry action for state 'Yellow Light Normal'. */
	private void entryAction_main_main_train_Yellow_Light_Normal() {
		sCInterface.setVelocity(50.0);
		
		sCInterface.operationCallback.print("Passed yellow light at good speed. Limiting speed to 50km/h");
	}
	
	/* Entry action for state 'Still'. */
	private void entryAction_main_main_train_normal_movement_train_Still() {
		sCInterface.operationCallback.print("still");
		
		sCInterface.setVelocity(0.0);
	}
	
	/* Entry action for state 'Driving'. */
	private void entryAction_main_main_train_normal_movement_train_Driving() {
		sCInterface.operationCallback.print("driving");
	}
	
	/* Entry action for state 'Cruising'. */
	private void entryAction_main_main_train_normal_movement_train_Cruising() {
		sCInterface.operationCallback.print("cruising");
	}
	
	/* Entry action for state 'In Station'. */
	private void entryAction_main_main_train_In_Station() {
		sCInterface.operationCallback.print("in station");
	}
	
	/* Entry action for state 'Doors Opened'. */
	private void entryAction_main_main_train_Doors_Opened() {
		timer.setTimer(this, 0, 5 * 1000, false);
		
		sCInterface.raiseOpenDoors();
	}
	
	/* Entry action for state 'Normal'. */
	private void entryAction_main_main_dead_man_s_button_Normal() {
		timer.setTimer(this, 1, 30 * 1000, true);
	}
	
	/* Entry action for state 'Button Prompted'. */
	private void entryAction_main_main_dead_man_s_button_Button_Prompted() {
		timer.setTimer(this, 2, 5 * 1000, false);
		
		sCInterface.raiseWarning("Press the POLL button!");
	}
	
	/* Entry action for state 'Emergency Brake Mode'. */
	private void entryAction_main_main_dead_man_s_button_Emergency_Brake_Mode() {
		setAcceleration(-1.0);
		
		sCInterface.operationCallback.print("In Emergency Brake Mode, acceleration=-1");
	}
	
	/* Exit action for state 'TrainController'. */
	private void exitAction() {
		timer.unsetTimer(this, 3);
	}
	
	/* Exit action for state 'Doors Opened'. */
	private void exitAction_main_main_train_Doors_Opened() {
		timer.unsetTimer(this, 0);
	}
	
	/* Exit action for state 'Normal'. */
	private void exitAction_main_main_dead_man_s_button_Normal() {
		timer.unsetTimer(this, 1);
	}
	
	/* Exit action for state 'Button Prompted'. */
	private void exitAction_main_main_dead_man_s_button_Button_Prompted() {
		timer.unsetTimer(this, 2);
	}
	
	/* 'default' enter sequence for state default */
	private void enterSequence_main_default_default() {
		nextStateIndex = 0;
		stateVector[0] = State.main_default;
	}
	
	/* 'default' enter sequence for state Emergency Brake Mode */
	private void enterSequence_main_main_train_Emergency_Brake_Mode_default() {
		entryAction_main_main_train_Emergency_Brake_Mode();
		nextStateIndex = 0;
		stateVector[0] = State.main_main_train_Emergency_Brake_Mode;
	}
	
	/* 'default' enter sequence for state Yellow Light Normal */
	private void enterSequence_main_main_train_Yellow_Light_Normal_default() {
		entryAction_main_main_train_Yellow_Light_Normal();
		nextStateIndex = 0;
		stateVector[0] = State.main_main_train_Yellow_Light_Normal;
	}
	
	/* 'default' enter sequence for state normal */
	private void enterSequence_main_main_train_normal_default() {
		enterSequence_main_main_train_normal_movement_train_default();
	}
	
	/* 'default' enter sequence for state Still */
	private void enterSequence_main_main_train_normal_movement_train_Still_default() {
		entryAction_main_main_train_normal_movement_train_Still();
		nextStateIndex = 0;
		stateVector[0] = State.main_main_train_normal_movement_train_Still;
		
		historyVector[0] = stateVector[0];
	}
	
	/* 'default' enter sequence for state Driving */
	private void enterSequence_main_main_train_normal_movement_train_Driving_default() {
		entryAction_main_main_train_normal_movement_train_Driving();
		nextStateIndex = 0;
		stateVector[0] = State.main_main_train_normal_movement_train_Driving;
		
		historyVector[0] = stateVector[0];
	}
	
	/* 'default' enter sequence for state Cruising */
	private void enterSequence_main_main_train_normal_movement_train_Cruising_default() {
		entryAction_main_main_train_normal_movement_train_Cruising();
		nextStateIndex = 0;
		stateVector[0] = State.main_main_train_normal_movement_train_Cruising;
		
		historyVector[0] = stateVector[0];
	}
	
	/* 'default' enter sequence for state In Station */
	private void enterSequence_main_main_train_In_Station_default() {
		entryAction_main_main_train_In_Station();
		nextStateIndex = 0;
		stateVector[0] = State.main_main_train_In_Station;
	}
	
	/* 'default' enter sequence for state Doors Opened */
	private void enterSequence_main_main_train_Doors_Opened_default() {
		entryAction_main_main_train_Doors_Opened();
		nextStateIndex = 0;
		stateVector[0] = State.main_main_train_Doors_Opened;
	}
	
	/* 'default' enter sequence for state Normal */
	private void enterSequence_main_main_dead_man_s_button_Normal_default() {
		entryAction_main_main_dead_man_s_button_Normal();
		nextStateIndex = 1;
		stateVector[1] = State.main_main_dead_man_s_button_Normal;
	}
	
	/* 'default' enter sequence for state Button Prompted */
	private void enterSequence_main_main_dead_man_s_button_Button_Prompted_default() {
		entryAction_main_main_dead_man_s_button_Button_Prompted();
		nextStateIndex = 1;
		stateVector[1] = State.main_main_dead_man_s_button_Button_Prompted;
	}
	
	/* 'default' enter sequence for state Emergency Brake Mode */
	private void enterSequence_main_main_dead_man_s_button_Emergency_Brake_Mode_default() {
		entryAction_main_main_dead_man_s_button_Emergency_Brake_Mode();
		nextStateIndex = 1;
		stateVector[1] = State.main_main_dead_man_s_button_Emergency_Brake_Mode;
	}
	
	/* 'default' enter sequence for region main */
	private void enterSequence_main_default() {
		react_main__entry_Default();
	}
	
	/* 'default' enter sequence for region movement_train */
	private void enterSequence_main_main_train_normal_movement_train_default() {
		react_main_main_train_normal_movement_train__entry_Default();
	}
	
	/* deep enterSequence with history in child movement_train */
	private void deepEnterSequence_main_main_train_normal_movement_train() {
		switch (historyVector[0]) {
		case main_main_train_normal_movement_train_Still:
			enterSequence_main_main_train_normal_movement_train_Still_default();
			break;
		case main_main_train_normal_movement_train_Driving:
			enterSequence_main_main_train_normal_movement_train_Driving_default();
			break;
		case main_main_train_normal_movement_train_Cruising:
			enterSequence_main_main_train_normal_movement_train_Cruising_default();
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
	
	/* Default exit sequence for state Emergency Brake Mode */
	private void exitSequence_main_main_train_Emergency_Brake_Mode() {
		nextStateIndex = 0;
		stateVector[0] = State.$NullState$;
	}
	
	/* Default exit sequence for state Yellow Light Normal */
	private void exitSequence_main_main_train_Yellow_Light_Normal() {
		nextStateIndex = 0;
		stateVector[0] = State.$NullState$;
	}
	
	/* Default exit sequence for state normal */
	private void exitSequence_main_main_train_normal() {
		exitSequence_main_main_train_normal_movement_train();
	}
	
	/* Default exit sequence for state Still */
	private void exitSequence_main_main_train_normal_movement_train_Still() {
		nextStateIndex = 0;
		stateVector[0] = State.$NullState$;
	}
	
	/* Default exit sequence for state Driving */
	private void exitSequence_main_main_train_normal_movement_train_Driving() {
		nextStateIndex = 0;
		stateVector[0] = State.$NullState$;
	}
	
	/* Default exit sequence for state Cruising */
	private void exitSequence_main_main_train_normal_movement_train_Cruising() {
		nextStateIndex = 0;
		stateVector[0] = State.$NullState$;
	}
	
	/* Default exit sequence for state In Station */
	private void exitSequence_main_main_train_In_Station() {
		nextStateIndex = 0;
		stateVector[0] = State.$NullState$;
	}
	
	/* Default exit sequence for state Doors Opened */
	private void exitSequence_main_main_train_Doors_Opened() {
		nextStateIndex = 0;
		stateVector[0] = State.$NullState$;
		
		exitAction_main_main_train_Doors_Opened();
	}
	
	/* Default exit sequence for state Normal */
	private void exitSequence_main_main_dead_man_s_button_Normal() {
		nextStateIndex = 1;
		stateVector[1] = State.$NullState$;
		
		exitAction_main_main_dead_man_s_button_Normal();
	}
	
	/* Default exit sequence for state Button Prompted */
	private void exitSequence_main_main_dead_man_s_button_Button_Prompted() {
		nextStateIndex = 1;
		stateVector[1] = State.$NullState$;
		
		exitAction_main_main_dead_man_s_button_Button_Prompted();
	}
	
	/* Default exit sequence for state Emergency Brake Mode */
	private void exitSequence_main_main_dead_man_s_button_Emergency_Brake_Mode() {
		nextStateIndex = 1;
		stateVector[1] = State.$NullState$;
	}
	
	/* Default exit sequence for region main */
	private void exitSequence_main() {
		switch (stateVector[0]) {
		case main_default:
			exitSequence_main_default();
			break;
		case main_main_train_Emergency_Brake_Mode:
			exitSequence_main_main_train_Emergency_Brake_Mode();
			break;
		case main_main_train_Yellow_Light_Normal:
			exitSequence_main_main_train_Yellow_Light_Normal();
			break;
		case main_main_train_normal_movement_train_Still:
			exitSequence_main_main_train_normal_movement_train_Still();
			break;
		case main_main_train_normal_movement_train_Driving:
			exitSequence_main_main_train_normal_movement_train_Driving();
			break;
		case main_main_train_normal_movement_train_Cruising:
			exitSequence_main_main_train_normal_movement_train_Cruising();
			break;
		case main_main_train_In_Station:
			exitSequence_main_main_train_In_Station();
			break;
		case main_main_train_Doors_Opened:
			exitSequence_main_main_train_Doors_Opened();
			break;
		default:
			break;
		}
		
		switch (stateVector[1]) {
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
		case main_main_train_Emergency_Brake_Mode:
			exitSequence_main_main_train_Emergency_Brake_Mode();
			break;
		case main_main_train_Yellow_Light_Normal:
			exitSequence_main_main_train_Yellow_Light_Normal();
			break;
		case main_main_train_normal_movement_train_Still:
			exitSequence_main_main_train_normal_movement_train_Still();
			break;
		case main_main_train_normal_movement_train_Driving:
			exitSequence_main_main_train_normal_movement_train_Driving();
			break;
		case main_main_train_normal_movement_train_Cruising:
			exitSequence_main_main_train_normal_movement_train_Cruising();
			break;
		case main_main_train_In_Station:
			exitSequence_main_main_train_In_Station();
			break;
		case main_main_train_Doors_Opened:
			exitSequence_main_main_train_Doors_Opened();
			break;
		default:
			break;
		}
	}
	
	/* Default exit sequence for region movement_train */
	private void exitSequence_main_main_train_normal_movement_train() {
		switch (stateVector[0]) {
		case main_main_train_normal_movement_train_Still:
			exitSequence_main_main_train_normal_movement_train_Still();
			break;
		case main_main_train_normal_movement_train_Driving:
			exitSequence_main_main_train_normal_movement_train_Driving();
			break;
		case main_main_train_normal_movement_train_Cruising:
			exitSequence_main_main_train_normal_movement_train_Cruising();
			break;
		default:
			break;
		}
	}
	
	/* Default exit sequence for region dead man's button */
	private void exitSequence_main_main_dead_man_s_button() {
		switch (stateVector[1]) {
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
	private void react_main_main_train_normal_movement_train__entry_Default() {
		enterSequence_main_main_train_normal_movement_train_Still_default();
	}
	
	/* Default react sequence for deep history entry hist */
	private void react_main_main_train_normal_movement_train_hist() {
		/* Enter the region with deep history */
		if (historyVector[0] != State.$NullState$) {
			deepEnterSequence_main_main_train_normal_movement_train();
		} else {
			enterSequence_main_main_train_normal_movement_train_Still_default();
		}
	}
	
	/* Default react sequence for initial entry  */
	private void react_main_main_train__entry_Default() {
		enterSequence_main_main_train_normal_default();
	}
	
	/* Default react sequence for initial entry  */
	private void react_main_main_dead_man_s_button__entry_Default() {
		enterSequence_main_main_dead_man_s_button_Normal_default();
	}
	
	private boolean react(boolean try_transition) {
		if (timeEvents[3]) {
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
					react_main_main_train_normal_movement_train_hist();
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
	
	private boolean main_main_train_Emergency_Brake_Mode_react(boolean try_transition) {
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
	
	private boolean main_main_train_Yellow_Light_Normal_react(boolean try_transition) {
		boolean did_transition = try_transition;
		
		if (try_transition) {
			if (main_main_react(try_transition)==false) {
				if ((sCInterface.update_acceleration) && (sCInterface.getVelocity()<=50.0)) {
					exitSequence_main_main_train_Yellow_Light_Normal();
					react_main_main_train_normal_movement_train_hist();
				} else {
					did_transition = false;
				}
			}
		}
		if (did_transition==false) {
		}
		return did_transition;
	}
	
	private boolean main_main_train_normal_react(boolean try_transition) {
		boolean did_transition = try_transition;
		
		if (try_transition) {
			if (main_main_react(try_transition)==false) {
				if (sCInterface.red_light) {
					exitSequence_main_main_train_normal();
					enterSequence_main_main_train_Emergency_Brake_Mode_default();
				} else {
					if ((sCInterface.yellow_light) && (sCInterface.getVelocity()<=50.0)) {
						exitSequence_main_main_train_normal();
						enterSequence_main_main_train_Yellow_Light_Normal_default();
					} else {
						if (sCInterface.green_light) {
							exitSequence_main_main_train_normal();
							enterSequence_main_main_train_normal_default();
						} else {
							if (sCInterface.enter) {
								exitSequence_main_main_train_normal();
								enterSequence_main_main_train_In_Station_default();
							} else {
								if ((sCInterface.yellow_light) && (sCInterface.getVelocity()>50.0)) {
									exitSequence_main_main_train_normal();
									enterSequence_main_main_train_Emergency_Brake_Mode_default();
								} else {
									if ((sCInterface.enter) && (sCInterface.getVelocity()>20.0)) {
										exitSequence_main_main_train_normal();
										enterSequence_main_main_train_Emergency_Brake_Mode_default();
									} else {
										did_transition = false;
									}
								}
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
	
	private boolean main_main_train_normal_movement_train_Still_react(boolean try_transition) {
		boolean did_transition = try_transition;
		
		if (try_transition) {
			if (main_main_train_normal_react(try_transition)==false) {
				if ((sCInterface.update_acceleration) && (getAcceleration()>0.0)) {
					exitSequence_main_main_train_normal_movement_train_Still();
					enterSequence_main_main_train_normal_movement_train_Driving_default();
				} else {
					did_transition = false;
				}
			}
		}
		if (did_transition==false) {
		}
		return did_transition;
	}
	
	private boolean main_main_train_normal_movement_train_Driving_react(boolean try_transition) {
		boolean did_transition = try_transition;
		
		if (try_transition) {
			if (main_main_train_normal_react(try_transition)==false) {
				if ((sCInterface.update_acceleration) && (sCInterface.getVelocity()==100.0)) {
					exitSequence_main_main_train_normal_movement_train_Driving();
					enterSequence_main_main_train_normal_movement_train_Cruising_default();
				} else {
					did_transition = false;
				}
			}
		}
		if (did_transition==false) {
		}
		return did_transition;
	}
	
	private boolean main_main_train_normal_movement_train_Cruising_react(boolean try_transition) {
		boolean did_transition = try_transition;
		
		if (try_transition) {
			if (main_main_train_normal_react(try_transition)==false) {
				if ((sCInterface.update_acceleration) && (sCInterface.getVelocity()==-1.0)) {
					exitSequence_main_main_train_normal_movement_train_Cruising();
					enterSequence_main_main_train_normal_movement_train_Still_default();
				} else {
					if ((sCInterface.update_acceleration) && (getAcceleration()<0.0)) {
						exitSequence_main_main_train_normal_movement_train_Cruising();
						enterSequence_main_main_train_normal_movement_train_Driving_default();
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
	
	private boolean main_main_train_In_Station_react(boolean try_transition) {
		boolean did_transition = try_transition;
		
		if (try_transition) {
			if (main_main_react(try_transition)==false) {
				if ((sCInterface.open) && (sCInterface.getVelocity()==0.0)) {
					exitSequence_main_main_train_In_Station();
					enterSequence_main_main_train_Doors_Opened_default();
				} else {
					did_transition = false;
				}
			}
		}
		if (did_transition==false) {
		}
		return did_transition;
	}
	
	private boolean main_main_train_Doors_Opened_react(boolean try_transition) {
		boolean did_transition = try_transition;
		
		if (try_transition) {
			if (main_main_react(try_transition)==false) {
				if (timeEvents[0]) {
					exitSequence_main_main_train_Doors_Opened();
					sCInterface.raiseCloseDoors();
					
					react_main_main_train_normal_movement_train_hist();
				} else {
					did_transition = false;
				}
			}
		}
		if (did_transition==false) {
		}
		return did_transition;
	}
	
	private boolean main_main_dead_man_s_button_Normal_react(boolean try_transition) {
		boolean did_transition = try_transition;
		
		if (try_transition) {
			if (timeEvents[1]) {
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
				if (timeEvents[2]) {
					exitSequence_main_main_dead_man_s_button_Button_Prompted();
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
			did_transition = false;
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
			case main_main_train_Emergency_Brake_Mode:
				main_main_train_Emergency_Brake_Mode_react(true);
				break;
			case main_main_train_Yellow_Light_Normal:
				main_main_train_Yellow_Light_Normal_react(true);
				break;
			case main_main_train_normal_movement_train_Still:
				main_main_train_normal_movement_train_Still_react(true);
				break;
			case main_main_train_normal_movement_train_Driving:
				main_main_train_normal_movement_train_Driving_react(true);
				break;
			case main_main_train_normal_movement_train_Cruising:
				main_main_train_normal_movement_train_Cruising_react(true);
				break;
			case main_main_train_In_Station:
				main_main_train_In_Station_react(true);
				break;
			case main_main_train_Doors_Opened:
				main_main_train_Doors_Opened_react(true);
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
