import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Random;

/**
 * Class to generate Test Case files for problem 3.11 from "Algorithm Design" by Jon Kleinberg, Eva Tardos
 * 
 * Test Cases are of the form:
 * n
 * m
 * x1, y1, k1
 * x2, y2, k2
 * ...
 * xm, ym, km
 * t
 * u1, i1, v1, j1
 * u2, i2, v2, j2
 * ...
 * ut, it, vt, jt
 * 
 * Where:
 * int n = The number of computers
 * int m = The number of communications (x, y, k) sorted in ascending order of k
 * int x = The first computer index in the communication
 * int y = The second computer in the communication (guaranteed unique from x)
 * int k = The time at which the communication took place
 * int t = The number of test cases
 * int u = The originally infected computer in the test case
 * int i = The time of original infection in the test case
 * int v = The observed computer in the test case
 * int j = The observed time in the test case
 * 
 * @author Cody Beebe
 *
 */
public class TestGenerator
{
	// Maximum time duration between two communications.
	static int MAX_TIME_STEP = 10;
	
	// Randomizer
	static Random Randomizer;
	
	// Entry
	public static void main(String[] args)
	{		
		// Usage
		if (args.length != 5)
		{
			System.out.println("Usage: java -j TestGenerator.jar NumComputers(n) NumCommunications(m) NumTestCases(t) MaxTimeStep <Filename>");
		}
		
		Randomizer = new Random();
		
		// Initialize test input
		int numComputers = Integer.parseInt(args[0]);
		int numCommunications = Integer.parseInt(args[1]);
		int numTests = Integer.parseInt(args[2]);
		MAX_TIME_STEP = Integer.parseInt(args[3]);
		
		try
		{
			BufferedWriter writer = new BufferedWriter(new FileWriter(args[4]));
			
			// Write num computers
			writer.write(numComputers + "");
			writer.newLine();
			
			// Write num communications
			writer.write(numCommunications + "");
			writer.newLine();
			
			// Write all communications
			int time = 0;
			for (int i = 0; i < numCommunications; ++i)
			{
				int C1 = getRandComputer(numComputers);
				int C2 = getRandOtherComputer(numComputers, C1);
				
				writer.write(C1 + ", " + C2 + ", " + time);
				writer.newLine();
				
				time += getRandTimeStep();
			}
			
			// Write number of test cases
			writer.write(numTests + "");
			writer.newLine();
			
			// Write test cases
			for (int i = 0; i < numTests; ++i)
			{
				int C1 = getRandComputer(numComputers);
				int start = Randomizer.nextInt(time);
				int C2 = getRandOtherComputer(numComputers, C1);
				int end = start + Randomizer.nextInt(time - start);
				
				writer.write(C1 + ", " + start + ", " + C2 + ", " + end);
				writer.newLine();
			}
			
			writer.flush();
			writer.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	// Returns a random computer index
	public static int getRandComputer(int numComputers)
	{
		return Randomizer.nextInt(numComputers);
	}
	
	// Returns a random computer index not equal to the first computer index
	public static int getRandOtherComputer(int numComputers, int firstComputer)
	{
		int otherComputer = getRandComputer(numComputers);
		
		while (otherComputer == firstComputer)
		{
			otherComputer = getRandComputer(numComputers);
		}
		
		return otherComputer;
	}

	// Returns a random time step with equal distribution on [0, MAX_TIME_STEP)
	public static int getRandTimeStep()
	{
		return Randomizer.nextInt(MAX_TIME_STEP);
	}
}
