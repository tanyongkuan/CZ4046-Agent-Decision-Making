package main;

import java.util.ArrayList;
import java.util.List;

import classes.StateInfo;
import classes.Maze;
import classes.State;

import util.FileIO;
import util.Utility;

public class ValueIterationApp {
	// Size of the Maze
	public static final int NUM_COLS = 6;
	public static final int NUM_ROWS = 6;
	
	// Discount factor
	public static final double DISCOUNT =  0.990;
	
	public static final double R_MAX = 1.000;
	
	public static final double C = 8;
	
	// Epsilon e = c * Rmax
	public static final double EPSILON = C * R_MAX;
	
	public static void main(String[] args) {
		Maze maze = new Maze();
		
		State[][] grid = maze.getGrid();
		
		// Displays the settings currently used
		System.out.println("Discount: " + DISCOUNT);
		System.out.println("Epsilon (c * Rmax): " + EPSILON);
		
		// Perform value iteration
		List<StateInfo[][]> stateInfos = valueIteration(grid);
		
		// Output to csv file to plot utility estimates as a function of iteration
		FileIO.writeToFile(stateInfos, "value_iteration_utilities");
		
		// Final item in the list is the optimal policy derived by value iteration
		final StateInfo[][] optimalPolicy = stateInfos.get(stateInfos.size()-1);
		
		// Display the utilities of all the (non-wall) states
		Utility.displayUtilities(grid, optimalPolicy);
		
		// Display the optimal policy
		System.out.println("\nOptimal Policy:");
		Utility.displayPolicy(optimalPolicy);
		
		// Display the utilities of all states
		System.out.println("\nUtilities of all states:");
		Utility.displayUtilitiesGrid(optimalPolicy);
	}
	
	public static List<StateInfo[][]> valueIteration(final State[][] grid) {
		StateInfo[][] currUtilArr = new StateInfo[NUM_COLS][NUM_ROWS];
		StateInfo[][] newUtilArr = new StateInfo[NUM_COLS][NUM_ROWS];
		
		for (int col = 0; col < NUM_COLS; col++) {
			for (int row = 0; row < NUM_ROWS; row++) {
				newUtilArr[col][row] = new StateInfo();
			}
		}
		
		List<StateInfo[][]> stateInfos = new ArrayList<>();
		
		double deltaMax = Double.MIN_VALUE;
		double deltaMin = Double.MAX_VALUE;
		
		double convergenceCriteria = EPSILON * ((1.000 - DISCOUNT) / DISCOUNT);
		System.out.printf("Convergence criteria: %.5f "
				+ "(the span semi-norm must be < this value)%n", convergenceCriteria);
		int numIterations = 0;
		
		do {
			Utility.array2DCopy(newUtilArr, currUtilArr);
			deltaMax = Double.MIN_VALUE;
			deltaMin = Double.MAX_VALUE;
			
			// Append to list of StateInfo a copy of the existing actions & utilities
			StateInfo[][] currUtilArrCopy = new StateInfo[NUM_COLS][NUM_ROWS];
			Utility.array2DCopy(currUtilArr, currUtilArrCopy);
			stateInfos.add(currUtilArrCopy);
			
			// For each state
			for(int row = 0 ; row < NUM_ROWS ; row++) {
		        for(int col = 0 ; col < NUM_COLS ; col++) {
		        	
		        	// Not necessary to calculate for walls
		        	if(grid[col][row].isWall())
		        		continue;
		        	
		        	newUtilArr[col][row] = Utility.calcBestUtility(col, row, currUtilArr, grid);
		        	
		        	double newUtil = newUtilArr[col][row].getUtility();
		        	double currUtil = currUtilArr[col][row].getUtility();
		        	double sDelta = Math.abs(newUtil - currUtil);
		        	
		        	// Update maximum delta & minimum delta, if necessary
		        	deltaMax = Math.max(deltaMax, sDelta);
		        	deltaMin = Math.min(deltaMin, sDelta);
		        }
			}
			
			++numIterations;
		} while ((deltaMax - deltaMin) >= convergenceCriteria);
		
		System.out.printf("%nNumber of iterations: %d%n", numIterations);
		return stateInfos;
	}
}
