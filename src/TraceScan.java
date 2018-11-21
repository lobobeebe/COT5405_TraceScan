import java.io.BufferedReader;
import java.io.FileReader;
import java.time.Instant;
import java.util.ArrayList;

/**
 * Class to solve problem 3.11 from "Algorithm Design" by Jon Kleinberg, Eva Tardos
 * See the TestGenerator.java class for the format of each test case. 
 * 
 * @author Cody Beebe
 *
 */
public class TraceScan
{	
	public static int Parse(String filename, ArrayList<Triple> triples, ArrayList<TestCase> tests)
	{
		int numComputers = 0;
		
		try
		{
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			
			// Read Num Computers
			String line = reader.readLine();
			numComputers = Integer.parseInt(line);
			
			// Read Num Communications
			line = reader.readLine();
			int numCommunications = Integer.parseInt(line);
			
			// Read each Communication
			for (int i = 0; i < numCommunications; ++i)
			{
				line = reader.readLine();
				String[] split = line.split(",");
				int c1 = Integer.parseInt(split[0].trim());
				int c2 = Integer.parseInt(split[1].trim());
				int time = Integer.parseInt(split[2].trim());
				
				triples.add(new Triple(c1, c2, time));
			}
			
			// Read the number of test cases
			line = reader.readLine();
			int numTests = Integer.parseInt(line);
			
			// For each Test Case
			for (int i = 0; i < numTests; ++i)
			{
				line = reader.readLine();
				
				String[] split = line.split(",");
				int c1 = Integer.parseInt(split[0].trim());
				int start = Integer.parseInt(split[1].trim());
				int c2 = Integer.parseInt(split[2].trim());
				int end = Integer.parseInt(split[3].trim());
				
				tests.add(new TestCase(c1, c2, start, end));
			}
				
			reader.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return numComputers;
	}
	
	// Entry
	public static void main(String[] args)
	{		
		// Usage
		if (args.length != 1)
		{
			System.out.println("Usage: java -j TraceScan.jar <Filename>");
		}
		
		String filename = args[0];
				
		ArrayList<Triple> triples = new ArrayList<>();
		ArrayList<TestCase> tests = new ArrayList<>();
		
		// Parse the input file
		int numComputers = Parse(filename, triples, tests);
		
		// Solve each test case using a Graph, Dynamic, and Brute Force method. Ensure each agree.
		for (int i = 0; i < tests.size(); ++i)
		{
			TestCase test = tests.get(i);

			
			long start = System.nanoTime();
			boolean graphSolution = SolveGraph(numComputers, triples, test.C1, test.Start, test.C2, test.End);
			long end = System.nanoTime();
			
			boolean dynamicSolution = SolveDynamic(numComputers, triples, test.C1, test.Start, test.C2, test.End);
			boolean bruteSolution = SolveBruteForce(numComputers, triples, test.C1, test.Start, test.C2, test.End);
			
			System.out.println("Test Case: C1 = " + test.C1 + ", Start = " +
					test.Start + ", C2 = " + test.C2 + ", End = " + test.End + " Solution = " + graphSolution +
					", Time Elapsed = " + (end - start) + "ns");
			
			if (graphSolution != dynamicSolution || dynamicSolution != bruteSolution || graphSolution != bruteSolution)
			{
				System.out.println("Error in Test Case " + i + ": Graph = " + graphSolution + ", Dynamic = " + dynamicSolution +
						", Brute = " + bruteSolution);	
			}
		}
	}
	
	// Helper function to return the index of the triples at which the first occurrence of the given start time appears.
	static int getStartIndex(ArrayList<Triple> triples, int startTime)
	{
		int startIndex = 0;
		
		// Iterate through the triples until the first triple with the start time is found.
		while (startIndex < triples.size())
		{
			if (triples.get(startIndex).Time >= startTime)
			{
				break;
			}
			
			++startIndex;
		}
		
		return startIndex;
	}
	
	// Solves the problem using a Brute Force technique. Used for validation purposes of other solutions.
	// - Let 'q' be an empty queue of integers representing indices of infected computers
	// - Let 'times' be an array of integers of size n where each index, i, is the time at which the computer with index i was infected and
	//   n is the number of computers. All values are originally set to the max value of an integer. A computer with index i is said to be
	//   infected iff times[i] < endTime, where endTime is the end of the simulation.
	// 
	// 1) Add u to q, where u is the originally infected computer index
	// 2) Let times[u] = i, where i is the time at which u was infected
	// 3) While q is not empty
	// 4)	If q.front == v then return true, where v is the observed computer
	// 5) 	Iterate over all communications in which q.front participated between times[q.front] and j, where j is the observed time
	// 6)		If the other computer in the communication, o, is not already infected at this time or before, add o to q
	// 7) 	Pop the front off of q
	public static boolean SolveBruteForce(int n, ArrayList<Triple> triples, int startComputer, int startTime, int endComputer, int endTime)
	{
		int[] infectedTimes = new int[n];
		for (int i = 0; i < n; ++i)
		{
			infectedTimes[i] = Integer.MAX_VALUE;
		}
		
		infectedTimes[startComputer] = startTime;
		
		ArrayList<Integer> infectedNodesToCheck = new ArrayList<>();
		infectedNodesToCheck.add(startComputer);
		
		while (infectedNodesToCheck.size() > 0)
		{
			int infectedComputer = infectedNodesToCheck.get(0); 
			
			if (infectedComputer == endComputer)
				return true;
			
			// Add all of the communications with the infected node
			infectedNodesToCheck.addAll(getInfectedNodes(triples, infectedTimes, infectedComputer, endComputer, endTime));
			
			// Done checking the first in the list
			infectedNodesToCheck.remove(0);
		}
		
		return false;
	}
	
	public static ArrayList<Integer> getInfectedNodes(ArrayList<Triple> triples, int[] infectionTimes, int infectedComputer, int endComputer, int endTime)
	{
		int startIndex = getStartIndex(triples, infectionTimes[infectedComputer]);
		
		ArrayList<Integer> infectedNodes = new ArrayList<>();
		
		for (int i = startIndex; i < triples.size(); ++i)
		{
			Triple currentTriple = triples.get(i); 
			
			if (currentTriple.Time > endTime)
			{
				break;
			}
			
			if (currentTriple.C1 == infectedComputer && currentTriple.Time < infectionTimes[currentTriple.C2])
			{
				infectionTimes[currentTriple.C2] = currentTriple.Time;
				infectedNodes.add(currentTriple.C2);
			}			
			else if (currentTriple.C2 == infectedComputer && currentTriple.Time < infectionTimes[currentTriple.C1])
			{
				infectionTimes[currentTriple.C1] = currentTriple.Time;
				infectedNodes.add(currentTriple.C1);
			}
		}
		
		return infectedNodes;
	}
	
	// Solves the problem using Dynamic Programming. Used only for validation of the graph solution.
	public static boolean SolveDynamic(int n, ArrayList<Triple> triples, int startIndex, int startTime, int endIndex, int endTime)
	{
		// All computers start as non-infected
		boolean[] isComputerInfected = new boolean[n];
		
		// Traversal variables
		int current = 0;
		Triple currentTriple;
		
		// Iterate through the triples until the first triple with the start time is found.
		while (current < triples.size())
		{
			currentTriple = triples.get(current);
			if (currentTriple.Time >= startTime)
			{
				break;
			}
			
			++current;
		}
		
		// Now, at the start time, the insertion computer is infected
		isComputerInfected[startIndex] = true;

		// Iterate through triples
		for (; current < triples.size(); ++current)
		{
			int currentTime = triples.get(current).Time; 
			
			// Stop when the end time has been passed
			if (currentTime > endTime)
			{
				break;
			}
			
			boolean isInfectionAtThisTime;
			
			do
			{
				isInfectionAtThisTime = false;
				
				for (int i = current; i < triples.size() && triples.get(i).Time == currentTime; ++i)
				{
					currentTriple = triples.get(i);
					
					// If either of the two computers in the communication were infected, both are now.
					if (isComputerInfected[currentTriple.C1] != isComputerInfected[currentTriple.C2])
					{
						isComputerInfected[currentTriple.C1] = true;
						isComputerInfected[currentTriple.C2] = true;
						isInfectionAtThisTime = true;
					}
				}
			}
			while (isInfectionAtThisTime);
		}
		
		return isComputerInfected[endIndex];
	}
	
	// Solves the problem using Graph Theory.
	public static boolean SolveGraph(int n, ArrayList<Triple> triples, int startIndex, int startTime, int endIndex, int endTime)
	{
		// Generate a graph in which the nodes are computers and the edges are communications (with time of that communication)
		// O(n)
		ArrayList<Node> nodes = new ArrayList<>(n);
		for (int i = 0; i < n; ++i)
		{
			nodes.add(new Node(n));
		}
				
		// Iterate through the communications from start time to end time
		// O(m)
		for (int i = 0; i < triples.size(); ++i)
		{
			// The communication must fall within the observation period
			Triple current = triples.get(i);
			if (current.Time >= startTime && current.Time <= endTime)
			{
				Node c1 = nodes.get(current.C1);
				Node c2 = nodes.get(current.C2);
				
				// Add a directed edge from C1 to C2 and from C2 to C1 
				c1.Edges.add(new Edge(current.C2, current.Time));
				c2.Edges.add(new Edge(current.C1, current.Time));
			}
		}
		
		// Perform DFS to determine if the insertion computer is connected with the end computer
		// Each edge is visited, at most, twice. Each node is visited once. O(n + m)
		return isConnectedToNodeAfterTime(nodes, startIndex, endIndex, startTime);
	}
	
	// DFS to determine if the insertion computer 
	public static boolean isConnectedToNodeAfterTime(ArrayList<Node> nodes, int startIndex, int endIndex, int time)
	{
		Node node = nodes.get(startIndex);
		
		boolean isFound = false;
		
		for (int i = 0; i < node.Edges.size() && !isFound; ++i)
		{
			Edge edge = node.Edges.get(i);
			
			if (edge.Time >= time)
			{
				if (edge.CompIndex == endIndex)
				{
					isFound = true;
				}
				else if (edge.CompIndex != startIndex && !edge.IsVisited)
				{
					edge.IsVisited = true;
					isFound = isConnectedToNodeAfterTime(nodes, edge.CompIndex, endIndex, edge.Time);
				}
			}
		}
		
		return isFound;
	}
	
	// Helper class to hold communications
	public static class Triple
	{
		public int C1, C2, Time;
		
		public Triple(int c1, int c2, int time)
		{
			C1 = c1;
			C2 = c2;
			Time = time;
		}
	}
	
	// Helper class to read Test Cases
	public static class TestCase
	{
		public int C1, C2, Start, End;
		
		public TestCase(int c1, int c2, int start, int end)
		{
			C1 = c1;
			C2 = c2;
			Start = start;
			End = end;
		}
	}
	
	// Helper class to store a list of edges
	public static class Node
	{
		public ArrayList<Edge> Edges;
		
		public Node(int numNodes)
		{
			Edges = new ArrayList<>();
		}	
	}
	
	// Link between two nodes and the time of the link.
	public static class Edge
	{
		public int CompIndex;
		public int Time;
		public boolean IsVisited;
		
		public Edge(int c, int t)
		{
			CompIndex = c;
			Time = t;
			
			IsVisited = false;
		}
	}
}
