package main;

import java.util.ArrayList;
import java.util.List;

import classes.StateInfo;
import classes.StateInfo.Action;
import classes.Maze;
import classes.State;

import util.FileIO;
import util.Utility;

public class PolicyIterationApp {

	public static Maze maze = null;
	// Size of the Grid World
	public static final int NUM_COLS = 6;
	public static final int NUM_ROWS = 6;
	
	// Discount factor
	public static final double DISCOUNT =  0.990;
	
	// Constant k (i.e. no. of times simplified Bellman update is executed to produce the next utility estimate)
	public static final int K = 10;
	
	public static void main(String[] args) {
		maze = new Maze();
		
		State[][] grid = maze.getGrid();
		
		// Displays the settings currently used
		System.out.println("Discount: " + DISCOUNT);
		System.out.println("k: " + K + " (# of times simplified Bellman update is repeated to produce the next utility estimate)");
		
		// Perform policy iteration
		List<StateInfo[][]> stateInfos = policyIteration(grid);
		
		// Output to csv file to plot utility estimates as a function of iteration
		FileIO.writeToFile(stateInfos, "policy_iteration_utilities");
		
		// Final item in the list is the optimal policy derived by policy iteration
		final StateInfo[][] optimalPolicy = stateInfos.get(stateInfos.size() - 1);
		
		// Display the utilities of all the (non-wall) states
		Utility.displayUtilities(grid, optimalPolicy);
		
		// Display the optimal policy
		System.out.println("\nOptimal Policy:");
		Utility.displayPolicy(optimalPolicy);
		
		// Display the utilities of all states
		System.out.println("\nUtilities of all states:");
		Utility.displayUtilitiesGrid(optimalPolicy);
	}
	
	//Performs modified policy iteration
	public static List<StateInfo[][]> policyIteration(final State[][] grid) {
		StateInfo[][] currUtilArr = new StateInfo[NUM_COLS][NUM_ROWS];
		
		for (int col = 0; col < NUM_COLS; col++) {
			for (int row = 0; row < NUM_ROWS; row++) {
				currUtilArr[col][row] = new StateInfo();
				if (!grid[col][row].isWall()) {
					currUtilArr[col][row].setAction(Action.UP);
				}
			}
		}
		
		List<StateInfo[][]> stateInfos = new ArrayList<>();
		boolean unchanged = true;
		int numIterations = 0;
		
		do {
			// Append to list of StateInfo a copy of the existing actions & utilities
			StateInfo[][] currUtilArrCopy = new StateInfo[NUM_COLS][NUM_ROWS];
			Utility.array2DCopy(currUtilArr, currUtilArrCopy);
			stateInfos.add(currUtilArrCopy);
			
			// Policy Estimation
			StateInfo[][] policyActionUtil = produceUtilEst(currUtilArr, grid);
			
			unchanged = true;
			
			// For each state - Policy improvement
			for (int row = 0; row < NUM_ROWS; row++) {
				for (int col = 0; col < NUM_COLS; col++) {
					// Not necessary to calculate for walls
					if (grid[col][row].isWall())
						continue;

					// Best calculated action based on maximizing utility
					StateInfo bestActionUtil = Utility.calcBestUtility(col, row, policyActionUtil, grid);
					
					// Action and the corresponding utlity based on current policy
					Action policyAction = policyActionUtil[col][row].getAction();
					StateInfo pActionUtil = Utility.calcFixedUtility(
							policyAction, col, row, policyActionUtil, grid);
					
					if((bestActionUtil.getUtility() > pActionUtil.getUtility())) {
						policyActionUtil[col][row].setAction(bestActionUtil.getAction());
						unchanged = false;
					}
				}
			}
			
			Utility.array2DCopy(policyActionUtil, currUtilArr);
			
			numIterations++;
			
		} while (!unchanged);
		
		System.out.printf("%nNumber of iterations: %d%n", numIterations);
		return stateInfos;
	}
	
	//Simplified Bellman update to produce the next utility estimate
	public static StateInfo[][] produceUtilEst(final StateInfo[][] currUtilArr,
			final State[][] grid) {
		
		StateInfo[][] currUtilArrCpy = new StateInfo[NUM_COLS][NUM_ROWS];
		StateInfo[][] newUtilArr = new StateInfo[NUM_COLS][NUM_ROWS];
		
		for (int col = 0; col < NUM_COLS; col++) {
			for (int row = 0; row < NUM_ROWS; row++) {
				currUtilArrCpy[col][row] = new StateInfo(
						currUtilArr[col][row].getAction(), currUtilArr[col][row].getUtility());
				newUtilArr[col][row] = new StateInfo();
			}
		}
		
		for(int k = 0; k < K; k++) {
			for (int row = 0; row < NUM_ROWS; row++) {
				for (int col = 0; col < NUM_COLS; col++) {
					//Skip for walls
					if (grid[col][row].isWall())
						continue;
	
					//Updates the utility based on the action stated in the policy
					Action action = currUtilArrCpy[col][row].getAction();
					newUtilArr[col][row] = Utility.calcFixedUtility(action,
							col, row, currUtilArrCpy, grid);
				}
			}
			Utility.array2DCopy(newUtilArr, currUtilArrCpy);
		}
		
		return newUtilArr;
	}
}
