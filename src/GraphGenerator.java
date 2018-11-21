import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;

public class GraphGenerator
{
	public static void main(String[] args)
	{
		int M_START = 100000;
		int M_STEP = 100000;
		int M_END = 1000000;
		ArrayList<Integer> mX = new ArrayList<>();
		ArrayList<Float> mY = new ArrayList<>();
		
		int N_START = 100000;
		int N_STEP = 100000;
		int N_END = 1000000;
		ArrayList<Integer> nX = new ArrayList<>();
		ArrayList<Float> nY = new ArrayList<>();
		
		int NUM_TESTS = 200;
		int MAX_TIME_STEP = 5;
		String TEMP_FILE = "tempTest.txt";
		
		// Modulating Num Comms
		for (int i = M_START; i <= M_END; i += M_STEP)
		{
			// Create Test Cases
			TestGenerator.main(new String[] {N_START + "", i + "", NUM_TESTS + "", MAX_TIME_STEP + "", TEMP_FILE});
			
			// Parse			
			ArrayList<TraceScan.Triple> triples = new ArrayList<>();
			ArrayList<TraceScan.TestCase> tests = new ArrayList<>();
			int numComputers = TraceScan.Parse(TEMP_FILE, triples, tests);
			
			// Solve each test case using a Graph method
			long start = System.nanoTime();
			for (int t = 0; t < tests.size(); ++t)
			{
				TraceScan.TestCase test = tests.get(t);

				TraceScan.SolveGraph(numComputers, triples, test.C1, test.Start, test.C2, test.End);
			}				
			long totalNanos = System.nanoTime() - start;
			
			mX.add(i);
			mY.add(((float)totalNanos) / tests.size());
		}
		
		// Modulating Num Computers
		for (int i = N_START; i <= N_END; i += N_STEP)
		{
			// Create Test Cases
			TestGenerator.main(new String[] {i + "", M_START + "", NUM_TESTS + "", MAX_TIME_STEP + "", TEMP_FILE});
			
			// Parse			
			ArrayList<TraceScan.Triple> triples = new ArrayList<>();
			ArrayList<TraceScan.TestCase> tests = new ArrayList<>();
			int numComputers = TraceScan.Parse(TEMP_FILE, triples, tests);
			
			// Solve each test case using a Graph method
			long start = System.nanoTime();
			for (int t = 0; t < tests.size(); ++t)
			{
				TraceScan.TestCase test = tests.get(t);

				TraceScan.SolveGraph(numComputers, triples, test.C1, test.Start, test.C2, test.End);
			}
			long totalNanos = System.nanoTime() - start;
			
			nX.add(i);
			nY.add(((float)totalNanos) / tests.size());
		}
		
		// Print Results
		try
		{
			BufferedWriter writerComms = new BufferedWriter(new FileWriter("ModulateComms.csv"));
			BufferedWriter writerComps = new BufferedWriter(new FileWriter("ModulateComps.csv"));
			
			for (int i = 0; i < mX.size(); ++i)
			{
				writerComms.write(mX.get(i) + ", " + mY.get(i));
				writerComms.newLine();
			}
			
			for (int i = 0; i < nX.size(); ++i)
			{
				writerComps.write(nX.get(i) + ", " + nY.get(i));
				writerComps.newLine();
			}
			
			writerComms.flush();
			writerComps.flush();
			writerComms.close();
			writerComps.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
