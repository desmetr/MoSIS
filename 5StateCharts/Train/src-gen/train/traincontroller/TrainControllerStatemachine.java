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
		main_main_train_normal,
		main_main_train_normal_movement_train_Still,
		main_main_train_normal_movement_train_Driving,
		main_main_train_normal_movement_train_Cruising,
		main_main_train_Red_Light,
		main_main_train_Yellow_Light_Emergency,
		main_main_train_Yellow_Light_Normal,
		main_main_parameters_parametersState,
		$NullState$
	};
	
	private State[] historyVector = new State[1];
	private final State[] stateVector = new State[2];
	
	private int nextStateIndex;
	
	private ITimer timer;
	
	private final boolean[] timeEvents = new boolean[2];
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
					main_main.ordinal()&& stateVector[0].ordinal() <= State.main_main_parameters_parametersState.ordinal();
		case main_main_train_normal:
			return stateVector[0].ordinal() >= State.
					main_main_train_normal.ordinal()&& stateVector[0].ordinal() <= State.main_main_train_normal_movement_train_Cruising.ordinal();
		case main_main_train_normal_movement_train_Still:
			return stateVector[0] == State.main_main_train_normal_movement_train_Still;
		case main_main_train_normal_movement_train_Driving:
			return stateVector[0] == State.main_main_train_normal_movement_train_Driving;
		case main_main_train_normal_movement_train_Cruising:
			return stateVector[0] == State.main_main_train_normal_movement_train_Cruising;
		case main_main_train_Red_Light:
			return stateVector[0] == State.main_main_train_Red_Light;
		case main_main_train_Yellow_Light_Emergency:
			return stateVector[0] == State.main_main_train_Yellow_Light_Emergency;
		case main_main_train_Yellow_Light_Normal:
			return stateVector[0] == State.main_main_train_Yellow_Light_Normal;
		case main_main_parameters_parametersState:
			return stateVector[1] == State.main_main_parameters_parametersState;
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
		timer.setTimer(this, 1, 20, true);
	}
	
	/* Entry action for state 'Still'. */
	private void entryAction_main_main_train_normal_movement_train_Still() {
		sCInterface.operationCallback.print("still");
		
		setAcceleration(0.0);
		
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
	
	/* Entry action for state 'Red Light'. */
	private void entryAction_main_main_train_Red_Light() {
		setAcceleration(-1.0);
		
		sCInterface.operationCallback.print("Passed red light, acceleration=-1");
	}
	
	/* Entry action for state 'Yellow Light Emergency'. */
	private void entryAction_main_main_train_Yellow_Light_Emergency() {
		setAcceleration(-1.0);
		
		sCInterface.operationCallback.print("Passed yellow light at too high speed, acceleration=-1");
	}
	
	/* Entry action for state 'Yellow Light Normal'. */
	private void entryAction_main_main_train_Yellow_Light_Normal() {
		sCInterface.setVelocity(50.0);
		
		sCInterface.operationCallback.print("Passed yellow light at good speed. Limiting speed to 50km/h");
	}
	
	/* Entry action for state 'parametersState'. */
	private void entryAction_main_main_parameters_parametersState() {
		timer.setTimer(this, 0, 20, true);
		
		sCInterface.setVelocity(sCInterface.getVelocity() + (acceleration / 2));
		
		sCInterface.setVelocity(sCInterface.velocity>0 ? sCInterface.velocity : 0);
		
		sCInterface.operationCallback.updateGUI();
	}
	
	/* Exit action for state 'TrainController'. */
	private void exitAction() {
		timer.unsetTimer(this, 1);
	}
	
	/* Exit action for state 'parametersState'. */
	private void exitAction_main_main_parameters_parametersState() {
		timer.unsetTimer(this, 0);
	}
	
	/* 'default' enter sequence for state default */
	private void enterSequence_main_default_default() {
		nextStateIndex = 0;
		stateVector[0] = State.main_default;
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
	
	/* 'default' enter sequence for state Red Light */
	private void enterSequence_main_main_train_Red_Light_default() {
		entryAction_main_main_train_Red_Light();
		nextStateIndex = 0;
		stateVector[0] = State.main_main_train_Red_Light;
	}
	
	/* 'default' enter sequence for state Yellow Light Emergency */
	private void enterSequence_main_main_train_Yellow_Light_Emergency_default() {
		entryAction_main_main_train_Yellow_Light_Emergency();
		nextStateIndex = 0;
		stateVector[0] = State.main_main_train_Yellow_Light_Emergency;
	}
	
	/* 'default' enter sequence for state Yellow Light Normal */
	private void enterSequence_main_main_train_Yellow_Light_Normal_default() {
		entryAction_main_main_train_Yellow_Light_Normal();
		nextStateIndex = 0;
		stateVector[0] = State.main_main_train_Yellow_Light_Normal;
	}
	
	/* 'default' enter sequence for state parametersState */
	private void enterSequence_main_main_parameters_parametersState_default() {
		entryAction_main_main_parameters_parametersState();
		nextStateIndex = 1;
		stateVector[1] = State.main_main_parameters_parametersState;
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
	
	/* 'default' enter sequence for region parameters */
	private void enterSequence_main_main_parameters_default() {
		react_main_main_parameters__entry_Default();
	}
	
	/* Default exit sequence for state default */
	private void exitSequence_main_default() {
		nextStateIndex = 0;
		stateVector[0] = State.$NullState$;
	}
	
	/* Default exit sequence for state main */
	private void exitSequence_main_main() {
		exitSequence_main_main_train();
		exitSequence_main_main_parameters();
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
	
	/* Default exit sequence for state Red Light */
	private void exitSequence_main_main_train_Red_Light() {
		nextStateIndex = 0;
		stateVector[0] = State.$NullState$;
	}
	
	/* Default exit sequence for state Yellow Light Emergency */
	private void exitSequence_main_main_train_Yellow_Light_Emergency() {
		nextStateIndex = 0;
		stateVector[0] = State.$NullState$;
	}
	
	/* Default exit sequence for state Yellow Light Normal */
	private void exitSequence_main_main_train_Yellow_Light_Normal() {
		nextStateIndex = 0;
		stateVector[0] = State.$NullState$;
	}
	
	/* Default exit sequence for state parametersState */
	private void exitSequence_main_main_parameters_parametersState() {
		nextStateIndex = 1;
		stateVector[1] = State.$NullState$;
		
		exitAction_main_main_parameters_parametersState();
	}
	
	/* Default exit sequence for region main */
	private void exitSequence_main() {
		switch (stateVector[0]) {
		case main_default:
			exitSequence_main_default();
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
		case main_main_train_Red_Light:
			exitSequence_main_main_train_Red_Light();
			break;
		case main_main_train_Yellow_Light_Emergency:
			exitSequence_main_main_train_Yellow_Light_Emergency();
			break;
		case main_main_train_Yellow_Light_Normal:
			exitSequence_main_main_train_Yellow_Light_Normal();
			break;
		default:
			break;
		}
		
		switch (stateVector[1]) {
		case main_main_parameters_parametersState:
			exitSequence_main_main_parameters_parametersState();
			break;
		default:
			break;
		}
	}
	
	/* Default exit sequence for region train */
	private void exitSequence_main_main_train() {
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
		case main_main_train_Red_Light:
			exitSequence_main_main_train_Red_Light();
			break;
		case main_main_train_Yellow_Light_Emergency:
			exitSequence_main_main_train_Yellow_Light_Emergency();
			break;
		case main_main_train_Yellow_Light_Normal:
			exitSequence_main_main_train_Yellow_Light_Normal();
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
	
	/* Default exit sequence for region parameters */
	private void exitSequence_main_main_parameters() {
		switch (stateVector[1]) {
		case main_main_parameters_parametersState:
			exitSequence_main_main_parameters_parametersState();
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
	private void react_main_main_parameters__entry_Default() {
		enterSequence_main_main_parameters_parametersState_default();
	}
	
	private boolean react(boolean try_transition) {
		if (timeEvents[1]) {
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
					enterSequence_main_main_parameters_default();
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
				did_transition = false;
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
					enterSequence_main_main_train_Red_Light_default();
				} else {
					if ((sCInterface.yellow_light) && (sCInterface.getVelocity()>50.0)) {
						exitSequence_main_main_train_normal();
						enterSequence_main_main_train_Yellow_Light_Emergency_default();
					} else {
						if ((sCInterface.yellow_light) && (sCInterface.getVelocity()<=50.0)) {
							exitSequence_main_main_train_normal();
							enterSequence_main_main_train_Yellow_Light_Normal_default();
						} else {
							if (sCInterface.green_light) {
								exitSequence_main_main_train_normal();
								enterSequence_main_main_train_normal_default();
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
	
	private boolean main_main_train_normal_movement_train_Still_react(boolean try_transition) {
		boolean did_transition = try_transition;
		
		if (try_transition) {
			if (main_main_train_normal_react(try_transition)==false) {
				if (sCInterface.update_acceleration) {
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
				if ((sCInterface.update_acceleration) && (getAcceleration()==-1.0)) {
					exitSequence_main_main_train_normal_movement_train_Cruising();
					enterSequence_main_main_train_normal_movement_train_Still_default();
				} else {
					did_transition = false;
				}
			}
		}
		if (did_transition==false) {
		}
		return did_transition;
	}
	
	private boolean main_main_train_Red_Light_react(boolean try_transition) {
		boolean did_transition = try_transition;
		
		if (try_transition) {
			if (main_main_react(try_transition)==false) {
				if ((sCInterface.pause) && (sCInterface.getVelocity()==0)) {
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
	
	private boolean main_main_train_Yellow_Light_Emergency_react(boolean try_transition) {
		boolean did_transition = try_transition;
		
		if (try_transition) {
			if (main_main_react(try_transition)==false) {
				if ((sCInterface.pause) && (sCInterface.getVelocity()==0)) {
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
	
	private boolean main_main_parameters_parametersState_react(boolean try_transition) {
		boolean did_transition = try_transition;
		
		if (try_transition) {
			if (timeEvents[0]) {
				exitSequence_main_main_parameters_parametersState();
				enterSequence_main_main_parameters_parametersState_default();
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
			case main_main_train_normal_movement_train_Still:
				main_main_train_normal_movement_train_Still_react(true);
				break;
			case main_main_train_normal_movement_train_Driving:
				main_main_train_normal_movement_train_Driving_react(true);
				break;
			case main_main_train_normal_movement_train_Cruising:
				main_main_train_normal_movement_train_Cruising_react(true);
				break;
			case main_main_train_Red_Light:
				main_main_train_Red_Light_react(true);
				break;
			case main_main_train_Yellow_Light_Emergency:
				main_main_train_Yellow_Light_Emergency_react(true);
				break;
			case main_main_train_Yellow_Light_Normal:
				main_main_train_Yellow_Light_Normal_react(true);
				break;
			case main_main_parameters_parametersState:
				main_main_parameters_parametersState_react(true);
				break;
			default:
				// $NullState$
			}
		}
		clearEvents();
	}
}
