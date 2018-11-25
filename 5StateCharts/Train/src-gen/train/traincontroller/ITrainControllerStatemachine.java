package train.traincontroller;

import java.util.List;
import train.IStatemachine;
import train.ITimerCallback;

public interface ITrainControllerStatemachine extends ITimerCallback,IStatemachine {

	public interface SCInterface {
	
		public void raiseClose();
		
		public void raiseOpen();
		
		public void raiseLeave();
		
		public void raiseEnter();
		
		public void raisePause();
		
		public void raiseContinue();
		
		public void raiseAwake();
		
		public void raiseYellow_light();
		
		public void raiseRed_light();
		
		public void raiseGreen_light();
		
		public void raiseUpdate_acceleration(double value);
		
		public boolean isRaisedCloseDoors();
		
		public boolean isRaisedOpenDoors();
		
		public boolean isRaisedError();
		
		public String getErrorValue();
		
		public boolean isRaisedWarning();
		
		public String getWarningValue();
		
		public boolean isRaisedClearWarning();
		
		public double getVelocity();
		
		public void setVelocity(double value);
		
	public List<SCInterfaceListener> getListeners();
		public void setSCInterfaceOperationCallback(SCInterfaceOperationCallback operationCallback);
	
	}
	
	public interface SCInterfaceListener {
	
		public void onCloseDoorsRaised();
		public void onOpenDoorsRaised();
		public void onErrorRaised(String value);
		public void onWarningRaised(String value);
		public void onClearWarningRaised();
		}
	
	public interface SCInterfaceOperationCallback {
	
		public void updateGUI();
		
		public void print(String msg);
		
	}
	
	public SCInterface getSCInterface();
	
}
