package util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.text.DecimalFormat;

import classes.StateInfo;
import classes.State;
import classes.StateInfo.Action;

public class Utility {
	// Size of Maze
	public static final int NUM_COLS = 6;
	public static final int NUM_ROWS = 6;
	// Transition model
	public static final double PROB_INTENT = 0.800;
	public static final double PROB_CW = 0.100;
	public static final double PROB_CCW = 0.100;
	// Discount factor
	public static final double DISCOUNT =  0.990;
	
	/**
	 * Copy the contents from the source array to the destination array
	 * 
	 * @param aSrc	Source array
	 * @param aDest	Destination array
	 */
	public static void array2DCopy(StateInfo[][] aSrc, StateInfo[][] aDest) {
		for (int i = 0; i < aSrc.length; i++) {
			System.arraycopy(aSrc[i], 0, aDest[i], 0, aSrc[i].length);
		}
	}
	
	public static void displayPolicy(final StateInfo[][] utilArr) {
		
		StringBuilder sb = new StringBuilder();
		sb.append("|");
        for(int col = 0 ; col < NUM_COLS ; col++) {
        	sb.append("---|");
        }
        sb.append("\n");
        
		for (int row = 0; row < NUM_ROWS; row++) {
			sb.append("|");
			for (int col = 0; col < NUM_COLS; col++) {
				sb.append(String.format(" %s |", utilArr[col][row].getActionString()));
			}
	        
	        sb.append("\n|");
	        for(int col = 0 ; col < NUM_COLS ; col++) {
	        	sb.append("---|");
	        }
	        sb.append("\n");
	    }
		
		System.out.println(sb.toString());
	}
	
	//Display the utilities of all the (non-wall) states
	public static void displayUtilities(final State[][] grid, final StateInfo[][] utilArr) {
		for (int col = 0; col < NUM_COLS; col++) {
			for (int row = 0; row < NUM_ROWS; row++) {
				if (!grid[col][row].isWall()) {
					System.out.printf("(%1d, %1d): %-2.6f%n", col, row, utilArr[col][row].getUtility());
				}
			}
		}
	}
	
	// Display the utilities of all the states, in a grid format
	public static void displayUtilitiesGrid(final StateInfo[][] utilArr) {

		StringBuilder sb = new StringBuilder();

		sb.append("|");
		for (int col = 0; col < NUM_COLS; col++) {
			sb.append("--------|");
		}
		sb.append("\n");

		String pattern = "00.000";
		DecimalFormat decimalFormat = new DecimalFormat(pattern);

		for (int row = 0; row < NUM_ROWS; row++) {
			sb.append("|");
			for (int col = 0; col < NUM_COLS; col++) {
				sb.append("--------|".replace('-', ' '));
			}
			sb.append("\n");

			sb.append("|");
			for (int col = 0; col < NUM_COLS; col++) {

				sb.append(String.format(" %s |", decimalFormat.format(utilArr[col][row].getUtility()).substring(0, 6)));
			}

			sb.append("\n|");
			for (int col = 0; col < NUM_COLS; col++) {
				sb.append("--------|".replace('-', ' '));
			}
			sb.append("\n");

			sb.append("|");
			for (int col = 0; col < NUM_COLS; col++) {
				sb.append("--------|");
			}
			sb.append("\n");
		}

		System.out.println(sb.toString());
	}
	
	//Calculates the utility for each possible action and returns the action with maximal utility
	public static StateInfo calcBestUtility(final int col, final int row,
			final StateInfo[][] currUtilArr, final State[][] grid) {
		
		List<StateInfo> stateInfos = new ArrayList<>();

		stateInfos.add(new StateInfo(StateInfo.Action.UP,
				calcActionUtility(Action.UP, col, row, currUtilArr, grid)));
		stateInfos.add(new StateInfo(StateInfo.Action.DOWN,
				calcActionUtility(Action.DOWN, col, row, currUtilArr, grid)));
		stateInfos.add(new StateInfo(StateInfo.Action.LEFT,
				calcActionUtility(Action.LEFT, col, row, currUtilArr, grid)));
		stateInfos.add(new StateInfo(StateInfo.Action.RIGHT,
				calcActionUtility(Action.RIGHT, col, row, currUtilArr, grid)));

		Collections.sort(stateInfos); //based on descending order for utility
		
		return stateInfos.get(0); //Index 0 is the maximal utility based on the sort
	}
	
	//Calculates the utility for the given action
	public static StateInfo calcFixedUtility(final Action action, final int col,
			final int row, final StateInfo[][] actionUtilArr, final State[][] grid) {
		
		StateInfo state = new StateInfo(action,calcActionUtility(action,col, row, actionUtilArr, grid));
		
		return state;
	}
	
	//Calculates the utility of the actions available to attempt based on the action
	public static double calcActionUtility(final Action action,final int col, final int row,
			final StateInfo[][] currUtilArr, final State[][] grid) {
		
		double actionUtility = 0;
		
		actionUtility = PROB_INTENT * go(action, col, row, currUtilArr, grid);
		
		switch(action) {
			case UP:
				actionUtility +=  PROB_CW * go(Action.RIGHT, col, row, currUtilArr, grid);
				actionUtility +=  PROB_CCW * go(Action.LEFT, col, row, currUtilArr, grid);
				break;
			case DOWN:
				actionUtility +=  PROB_CW * go(Action.LEFT, col, row, currUtilArr, grid);
				actionUtility +=  PROB_CCW * go(Action.RIGHT, col, row, currUtilArr, grid);
				break;
			case LEFT:
				actionUtility +=  PROB_CW * go(Action.UP, col, row, currUtilArr, grid);
				actionUtility +=  PROB_CCW * go(Action.DOWN, col, row, currUtilArr, grid);
				break;
			case RIGHT:
				actionUtility +=  PROB_CW * go(Action.DOWN, col, row, currUtilArr, grid);
				actionUtility +=  PROB_CCW * go(Action.UP, col, row, currUtilArr, grid);
				break;
		}
		
		actionUtility = grid[col][row].getReward() + DISCOUNT * actionUtility;
		
		return actionUtility;
	}
	
	/*
	 * Attempts to go and returns the utility value of the resulting state
	 * Succeeds if the target state is not out-of-bounds and not a wall
	 * Failure results in the agent staying in the same place as before
	 */
	public static double go (final Action action, final int col, final int row, final StateInfo[][] currUtilArr, final State[][] grid) {
		int newCol = col;
		int newRow = row;
		
		switch (action) {
			case UP:
				newRow -= 1; 
				break;
			case DOWN:
				newRow += 1;
				break;
			case LEFT:
				newCol -= 1;
				break;
			case RIGHT:
				newCol += 1;
				break;
		}
		
		if((newRow >= 0 && newRow < NUM_ROWS) && (newCol >= 0 && newCol < NUM_COLS) && !grid[newCol][newRow].isWall())
			return currUtilArr[newCol][newRow].getUtility();
		else
			return currUtilArr[col][row].getUtility();
	}
}
