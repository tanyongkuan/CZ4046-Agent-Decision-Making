package classes;

/* Contains single state of the maze*/
public class State {
	private double reward = 0.0;
	private boolean isWall = false;
	
	public State(double reward) {
		this.reward = reward;
	}
	
	public double getReward() {
		return reward;
	}
	
	public void setReward(double reward) {
		this.reward = reward;
	}
	
	public boolean isWall() {
		return isWall;
	}
	
	public void setWall(boolean isWall) {
		this.isWall = isWall;
	}
}
