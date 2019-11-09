package classes;

public class Maze {

	private State[][] grid = null;
	
	// Size of the Maze
	public static final int NUM_COLS = 6;
	public static final int NUM_ROWS = 6;
	
	// Reward functions
	public static final double WHITE_REWARD = -0.040;
	public static final double GREEN_REWARD = +1.000;
	public static final double BROWN_REWARD = -1.000;
	public static final double WALL_REWARD = 0.000;

	//Maze information in (col, row) format delimited by semi-colon
	public static final String GREEN_SQUARES = "0,0;2,0;5,0;3,1;4,2;5,3";
	public static final String BROWN_SQUARES = "1,1;5,1;2,2;3,3;4,4";
	public static final String WALLS_SQUARES = "1,0;4,1;1,4;2,4;3,4";
	
	public Maze() {
		grid = new State[NUM_COLS][NUM_ROWS];
		buildGrid();
	}
	
	public State[][] getGrid() {
		return grid;
	}
	
	public void buildGrid() {
		// Initialize all grids with reward of -0.040
		for(int row = 0 ; row < NUM_ROWS ; row++) {
	        for(int col = 0 ; col < NUM_COLS ; col++) {	
	        	grid[col][row] = new State(WHITE_REWARD);
	        }
	    }
		
		// Set all the green squares (+1.000)
		String[] greenSquaresArr = GREEN_SQUARES.split(";");
		for(String greenSquare : greenSquaresArr) {
			String [] gridInfo = greenSquare.split(",");
			int gridCol = Integer.parseInt(gridInfo[0]);
			int gridRow = Integer.parseInt(gridInfo[1]);
			
			grid[gridCol][gridRow].setReward(GREEN_REWARD);
		}
		
		// Set all the brown squares (-1.000)
		String[] brownSquaresArr = BROWN_SQUARES.split(";");
		for (String brownSquare : brownSquaresArr) {
			String[] gridInfo = brownSquare.split(",");
			int gridCol = Integer.parseInt(gridInfo[0]);
			int gridRow = Integer.parseInt(gridInfo[1]);

			grid[gridCol][gridRow].setReward(BROWN_REWARD);
		}
		
		// Set all the walls (0.000 and unreachable)
		String[] wallSquaresArr = WALLS_SQUARES.split(";");
		for (String wallSquare : wallSquaresArr) {
			String[] gridInfo = wallSquare.split(",");
			int gridCol = Integer.parseInt(gridInfo[0]);
			int gridRow = Integer.parseInt(gridInfo[1]);

			grid[gridCol][gridRow].setReward(WALL_REWARD);
			grid[gridCol][gridRow].setWall(true);
		}
	}
}
