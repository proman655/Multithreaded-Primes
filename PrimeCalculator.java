// Pedro Roman
// COP 4520
// Programming Assignment 1

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;
import java.util.List;
import java.util.ArrayList;

public class PrimeCalculator {	
	
	// Variables
	public static final int limit = 100000000; // Determine the amount of primes up to this integer.
    public static final int numOfThreads = 8; // Specify the amount of threads to be created and used.
    public static boolean sieve[] = new boolean[limit]; // Sieve array to keep track of prime numbers.
    public static boolean finished[] = new boolean[numOfThreads]; // Array to keep track of threads that are finished working.
    public static long totalPrimes = 0; // Variable to keep track of total number of primes found.
    public static long totalSum = 0; // Variable to determine the sum of all primes found.

    // Create lists for handling the threads
    private List<Primes> workers;
    private List<Thread> threads;
    
    // Create Reentrant Lock object 
    public ReentrantLock lock = new ReentrantLock();
   
    @SuppressWarnings("static-access")
	public static void main(String args[]) throws IOException, InterruptedException {
    	
    	// Create the main thread
        PrimeCalculator mainThread = new PrimeCalculator();
        
        // Initialize the sieve array with a false value in each index
        for (int i = 0; i < limit; i++) {
        	mainThread.sieve[i] = false;
        }

        // Create the lists handling the threads
        mainThread.workers = new ArrayList<>();
        mainThread.threads = new ArrayList<>();
        
        for (int i = 0; i < numOfThreads; i++) {
            Primes Primes = new Primes(mainThread, i, limit);
            mainThread.workers.add(Primes);
            
            Thread thread = new Thread(Primes);
            mainThread.threads.add(thread);
        }
        
        // Add numbers 2 and 3 to the list of prime numbers
        mainThread.sieve[2] = true;
        mainThread.sieve[3] = true;

        // Start the timer for run-time
        long startTime = System.nanoTime();

        // Start the threads (call the run() method for each thread).
        for (Thread i : mainThread.threads) {
            i.start();
        }        

        // Wait for threads to finish their operations
        for (Thread i : mainThread.threads) {
        	i.join();
        }

        // Stop the timer
        long endTime = System.nanoTime();

        // Terminate all threads
        for (Thread i : mainThread.threads) {
        	i.interrupt();
        }

        // Calculate execution time in milliseconds
        long executionTime = java.util.concurrent.TimeUnit.NANOSECONDS.toMillis(endTime - startTime); 

        // Calculate total primes calculated and their sum by looping through the array, detecting which indices have a value of true
        for (int i = 0; i < limit; i++) {
        	
            if (sieve[i] == true) {
                totalPrimes++;
                totalSum += i;
            }
        }

        // Write program output to primes.txt using FileWriter
        int printedPrimes = 0;
       	int[] topTenPrimes = new int[10];
       	
        File myFile = new File("primesNew.txt");
        myFile.createNewFile();
            
        FileWriter myWriter = new FileWriter("primesNew.txt");
        myWriter.write("Execution Time: " + executionTime + "ms \n" + "Prime count: " +
        totalPrimes + "\n" + "Sum of Primes: " + totalSum + "\n");
            
        for (int i = limit - 1; i > 0 && printedPrimes < 10; i--) {
            if (sieve[i] == true) {
                topTenPrimes[printedPrimes] = i;
                printedPrimes++;
            }
        }
        for (int i = 9; i >= 0; i--) {
            // Print the 10 biggest primes in ascending order.
            myWriter.write(topTenPrimes[i] + " ");
        }

        myWriter.close();

    }
}

class Primes implements Runnable {

    private PrimeCalculator mainThread;
    private int threadNum;
    private int limit;

    // Constructor
    public Primes(PrimeCalculator inputThread, int inputthreadNum, int inputlimit) {
        this.mainThread = inputThread;
        this.threadNum = inputthreadNum;
        this.limit = inputlimit;
    }
    
    

    // Overriding the run function within the Runnable class to tell the threads what to do.
    @Override
    public void run() {
    	
            	// Execute the Sieve of Atkin function 
				try {
					SieveOfAtkin(mainThread, threadNum, limit);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
    }

//					Sieve of Atkin algorithm explanation, taken from GeeksforGeeks.
//    Step 0: 
//    	The status for all the numbers at the start is false. 2 and 3 were already set as prime.
//
//    	Step 1: 
//    	Generate values for the conditions.  
//
//    	Step 2: 
//    		Flipping the status according to condition.
//    		The above values of n in the table generated in the x, y loop will be tested for modulo conditions.
    
//    		Condition 1: if % 12 == 1 or 5 then flip the sieve status for that number. 
//    					We are taking % with 12 in place of 60. This is because if we take % 60 then we have to consider as many r as 1, 13, 17, 29, 37, 41, 49, or 53 and for all these r, % 12 is 1 or 5. (done only to reduce the expression size)
    
//    		Condition 2: if % 12 == 7 
//    	       			then flip the sieve status for that number.
    
//    		Condition 3: if % 12 == 11 
//    	         		then flip the sieve status for that number.
//
//    	Step 3 : 
//    		Checking for Square free Condition: If any number in our list is the square of any number, then remove it.
    
    @SuppressWarnings("static-access")
	public void SieveOfAtkin(PrimeCalculator mainThread, int threadNum, int limit) throws InterruptedException
    {
    	// Step 2
        for (int x = 1 + threadNum; x * x < limit; x += mainThread.numOfThreads) {
            for (int y = 1; y * y < limit; y++) {
            	
            	// Create variable n for computation
                int n = (4 * x * x) + (y * y);
                
                // Condition 1
                if (n <= limit && (n % 12 == 1 || n % 12 == 5)) {
                   
                	this.mainThread.lock.lock();
                    this.mainThread.sieve[n] ^= true;
                    this.mainThread.lock.unlock();
                    
                }

                n = (3 * x * x) + (y * y);
                
                // Condition 2
                if (n <= limit && n % 12 == 7) {
                    
                	this.mainThread.lock.lock();
                    this.mainThread.sieve[n] ^= true;
                    this.mainThread.lock.unlock();
                     
                }

                n = (3 * x * x) - (y * y);
                
                // Condition 3
                if (x > y && n <= limit && n % 12 == 11) {
                   
                	this.mainThread.lock.lock();
                    this.mainThread.sieve[n] ^= true;
                    this.mainThread.lock.unlock();
                }
            }
        }

        // Mark this thread as done with its corresponding section.
        mainThread.finished[threadNum] = true;
        
        // Wait threads to finish their corresponding section.
        boolean allDone = false;
        while(!allDone) {
            
            Thread.sleep(100);

            allDone = true;
            for (int i = 0; i < mainThread.numOfThreads; i++) {
                if (mainThread.finished[i] == false) {
                    allDone = false;
                    break;
                }
            }
        }

        // Step 3 - Remove any squares of any number since they can not be prime.
        for (int r = 5 + threadNum; r * r < limit; r += mainThread.numOfThreads) {
       
                if (this.mainThread.sieve[r]) {
                    for (int i = r * r; i < limit; i += r * r) {		
                    	if (this.mainThread.sieve[i] == true) {
                            this.mainThread.sieve[i] = false;
                        }
                    }
                }
                
           
        }
    }
}
