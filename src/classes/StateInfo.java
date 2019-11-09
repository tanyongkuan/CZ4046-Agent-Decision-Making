package classes;

import java.util.Random;

public class StateInfo implements Comparable<StateInfo>{
	private Action action;
	private double utility;
	
	public StateInfo() {
		action = null;
		utility = 0.0;
	}
	
	public StateInfo(Action action, double utility) {
		this.action = action;
		this.utility = utility;
	}
	
	public Action getAction() {
		return action;
	}
	
	public String getActionString() {
		// No action at wall, otherwise return one of the 4 possible actions
		if (action != null)
			return action.toString();
		
		return "W";
	}
	
	public void setAction(Action action) {
		this.action = action;
	}
	
	public double getUtility() {
		return utility;
	}
	
	public void setUtility(double utility) {
		this.utility = utility;
	}
	
	public enum Action {
		UP("^"),
		DOWN("v"),
		LEFT("<"),
		RIGHT(">");
		
		// String representation
		private String strRepresent;
		
		Action(String strRepresent) {
			this.strRepresent = strRepresent;
		}
		
		@Override
		public String toString() {
			return strRepresent;
		}
	}

	@Override
	public int compareTo(StateInfo state) {
		return ((Double) state.getUtility()).compareTo(getUtility());
	}
}
