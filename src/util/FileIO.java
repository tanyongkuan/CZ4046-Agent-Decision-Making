package util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.List;

import classes.StateInfo;

public class FileIO
{
	//Size of the maze
	public static final int NUM_COLS = 6;
	public static final int NUM_ROWS = 6;
	
	public static void writeToFile(List<StateInfo[][]> stateInfos, String fileName) {
		
		StringBuilder sb = new StringBuilder();
		String pattern = "00.000";
        DecimalFormat decimalFormat = new DecimalFormat(pattern);
        
		for (int col = 0; col < NUM_COLS; col++) {
			for (int row = 0; row < NUM_ROWS; row++) {
				Iterator<StateInfo[][]> iter = stateInfos.iterator();
				while(iter.hasNext()) {
					StateInfo[][] actionUtil = iter.next();
					sb.append(decimalFormat.format(
							actionUtil[col][row].getUtility()).substring(0, 6));
					
					if(iter.hasNext()) {
						sb.append(",");
					}
				}
				sb.append("\n");
			}
		}
		
		try
		{
			FileWriter fw = new FileWriter(new File(fileName + ".csv"), false);

			fw.write(sb.toString().trim());
			fw.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
}