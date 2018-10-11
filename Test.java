import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 * @author Marcus Henke
 * This is the driver class for Project1. This class
 * is responsible for parsing through text files and 
 * sending tokens to Cache objects. A console message will display
 * either the results from the Cache process or an error message.
 */
public class Test {
	
	//Declare instances variables
	private static final String INPUT_ERROR = 
	"There was an error processing your input. The format should be: \n" + 
	"java Test 1 <cache size> <input textfile name>\n" + 
	"or\n" + 
	"java Test 2 <1st-level cache size> <2nd-level cache size> <input textfile name>" +
	"\n\nplease ensure your cache sizes are greater than zero and your first cache is smaller than" + 
	" your second cache." ;
	private static File textFile;
	private static int numCaches;
	private static int cache1Size;
	private static int cache2Size;
	private static Cache<String> cache1;
	private static Cache<String> cache2;

	/**
	 * Main method for the driver class. Ensures the first command line argument is 
	 * valid, then initializes the Cache objects if checkArgs methods return true. 
	 * @param args, command line arguments
	 * @throws FileNotFoundException, because a check for file.exists() is called in
	 * checkArgsSingle and checkArgsDouble methods
	 */
	public static void main(String[] args) throws FileNotFoundException {
		// TODO Auto-generated method stub
		boolean isValid = false;
		if (args[0].equals("1")) {
			numCaches = 1;
			isValid = checkArgsSingle(args);
		}
		else if (args[0].equals("2")) {
			numCaches = 2;
			isValid = checkArgsDouble(args);
		}
		else showError();
		
		if (isValid) {
			if (cache1Size > cache2Size) {
			   showError();
			}
			cache1 = new Cache<String>(cache1Size);
			System.out.println("First Level Cache with " + cache1Size + " entries has been created.");
			if (numCaches == 2) {
				cache2 = new Cache<String>(cache2Size);
				System.out.println("Second Level Cache with " + cache2Size + " entries has been created.");
			}
			parse();
		}
		else showError();
	}
	
	/**
	 * Checks to ensure the user arguments for a single cache
	 * are valid
	 * @param args, command line arguments
	 * @return true if user input is valid
	 * @return false if user input is invalid
	 */
	private static boolean checkArgsSingle(String[] args) {
		File file = new File(args[2]);
		if (file.exists() && (Integer.parseInt(args[1]) > 0))  {
			textFile = file;
			cache1Size = Integer.parseInt(args[1]);
			return true;
		}
		else return false;
	}
	
	/**
	 * Checks to ensure the user arguments for a double cache
	 * are valid
	 * @param args, command line arguments
	 * @return true if user input is valid
	 * @return false if user input is invalid
	 */
	private static boolean checkArgsDouble(String[] args) {
		File file = new File(args[3]);
		if (file.exists() && (Integer.parseInt(args[1]) > 0) && (Integer.parseInt(args[2]) > 0))  {
			textFile = file;
			cache1Size = Integer.parseInt(args[1]);
			cache2Size = Integer.parseInt(args[2]);
			return true;
		}
		else return false;
	}

	/**
	 * Parses through text files and calls processDouble or processSingle cache methods
	 * Prints loading bar for large file parsing.
	 * Once completed, calls printDoubleCacheResult/printSingleCacheResult methods.
	 * @throws FileNotFoundException because a check for file.exists() is called in prior
	 * checkArgsSingle and checkArgsDouble methods
	 */
	private static void parse() throws FileNotFoundException {
		Scanner scan = new Scanner(textFile);
		int x = 0;
		System.out.print("\nProcessing...");
		if (numCaches == 2) {
			while (scan.hasNextLine()) {
				String line = scan.nextLine();
				StringTokenizer st = new StringTokenizer(line);
				while (st.hasMoreTokens()) {
					x++;
					String s = st.nextToken();
					processDoubleCache(s);
					if (x % 30000 == 0) {
						System.out.print(".");
					}
				}
			}
			printDoubleCacheResults();
		}
		else {
			while (scan.hasNextLine()) {
				String line = scan.nextLine();
				StringTokenizer st = new StringTokenizer(line);
				while (st.hasMoreTokens()) {
					x++;
					String s = st.nextToken();
					processSingleCache(s);
					if (x % 30000 == 0) {
						System.out.print(".");
					}
				}
			}
			printSingleCacheResults();
		}
		scan.close();
	}
	
	
	/**
	 * -----For One Cache------
	 * Scans, adds and moves String objects in the Cache object 
	 * depending on if they already exist in the Cache or not
	 * @param s, the String to send to the Cache
	 */
	private static void processSingleCache(String s) {
		if (cache1.getObject(s)) {
			cache1.moveToTop(s);
		}
		else {
			cache1.addObject(s);
		}
		
	}
	
	/**
	 * -----For Two Caches------
	 * Scans, adds and moves String objects in the Cache objects 
	 * depending on if they already exist in the Caches or not
	 * @param s, the String to send to the Caches
	 */
	private static void processDoubleCache(String s) {
		if (cache1.getObject(s)) {
			cache1.moveToTop(s);
			cache2.moveToTop(s);
		}
		else {
			cache1.addObject(s);
			if (cache2.getObject(s)) {
				cache2.moveToTop(s);
			}
			else {
				cache2.addObject(s);
			}
		}
	}
	
	/**
	 * Prints the results from creating a single cache
	 */
	private static void printSingleCacheResults() {
		int refs = cache1.getReferences();
		int hits = cache1.getHits();
		double hitRatio = (double) hits/refs;
		
		System.out.println("\n\nNumber of cache references: " + refs);
		System.out.println("Number of cache hits: " + hits);
		System.out.println("Cache hit ratio: " + hitRatio);
	}
	
	/*
	 * Prints the results from creating two caches
	 */
	private static void printDoubleCacheResults() {
		int refs1 = cache1.getReferences();
		int refs2 = cache2.getReferences();
		int hits1 = cache1.getHits();
		int hits2 = cache2.getHits();
		
		double cache1Ratio = (double) hits1/refs1;
		double cache2Ratio = (double) hits2/refs2;
		double globalRatio = (double) (hits1 + hits2)/refs1;
		
		System.out.println("\n\nNumber of global references: " + refs1);
		System.out.println("Number of global cache hits: " + (hits1 + hits2));
		System.out.println("Global cache hit ratio: " + globalRatio);
		
		System.out.println("\nNumber of 1st-level references: " + refs1);
		System.out.println("Number of 1st-level cache hits: " + (hits1));
		System.out.println("1st-level cache hit ratio: " + cache1Ratio);
		
		System.out.println("\nNumber of 2nd-level references: " + refs2);
		System.out.println("Number of 2nd-level global cache hits: " + (hits2));
		System.out.println("2nd-level cache hit ratio: " + cache2Ratio);
	}
	
	/*
	 * Displays an error message
	 */
	private static void showError() {
		System.out.println(INPUT_ERROR);
		System.exit(0);
	}
	
	

}
