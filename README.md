# Multithreaded-Primes
A multithreaded prime calculator for COP4520 as Programming Assignment 1. Coded in Java using Eclipse IDE.

Filename: PrimeCalculator.java

# Run Instructions:
1. Open terminal
2. Navigate to directory containing PrimeCalculator.java
3. Compile using:
```bash
  javac PrimeCalculator.java
```
4. Run using:
```bash
  java PrimeCalculator
```

# Problem Statement:
Your non-technical manager assigns you the task to find all primes between 1 and 108.  The assumption is that your company is going to use a parallel machine that supports eight concurrent threads. Thus, in your design you should plan to spawn 8 threads that will perform the necessary computation. Your boss does not have a strong technical background but she is a reasonable person. Therefore, she expects to see that the work is distributed such that the computational execution time is approximately equivalent among the threads. Finally, you need to provide a brief summary of your approach and an informal statement reasoning about the correctness and efficiency of your design. Provide a summary of the experimental evaluation of your approach. Remember, that your company cannot afford a supercomputer and rents a machine by the minute, so the longer your program takes, the more it costs. Feel free to use any programming language of your choice that supports multi-threading as long as you provide a ReadMe file with instructions for your manager explaining how to compile and run your program from the command prompt.

# Proof of Correctness:
My implementation uses a Reentrant Lock to make sure there is mutual exclusion between the threads. This works by allowing threads to reenter into a lock on a resource without causing a deadlock situation. It gives a lock to the current working their and blocks all other threads from accessing that shared resource at the time, and unlocks once the working thread is finished with its operation. The algorithm used for determine the primeness of a number was achieved using a Sieve of Atkins. This works by running a certain range of numbers through a set of conditions to determine if they are prime, which are explained in more detail in the code in comments. 

# Experimental Evaluation:
Processor used for testing: 11th Gen Intel(R) Core(TM) i7-1165G7 @ 2.80GHz (8 CPUs), ~2.8GHz

My experimental evaluation throughout this assignment was long and tedious. Although the basic implementation of a prime calculator was not complicated, the main area of concern was the efficiency and run-time.

The algorithm used to find the prime numbers was the Sieve of Atkins, which I aquired from GeeksForGeeks. https://www.geeksforgeeks.org/sieve-of-atkin/

I began by modifying the Sieve of Atkins code to paralellize it. Implenenting the threads to do so was not difficult. However, upon first testing, it was obvious that the threads were not satisfying mutual exclusion, as I was receiving various different results for the sum of primes and total amount of primes generated. My first idea towards fixing this solution was to atomize the sieve array that is used for storing the true/false values for all numbers within the range. I used an Atomic Array consisting of Atomic Booleans and had to use .get() and .set() functions to get and set those boolean values. That did not fix the problem of mutual exclusion. After further research, I determined that what I needed to implement was a locking system. 

My first implementation of a locking system was the semaphore lock. However, this lock took a huge hit on the runtime and performance of my program (~30000ms), however, it fixed the correct-ness of the program and provided me with the correct amount of primes. I then discovered the Reentrant Lock, and after testing, determined that it was my choice for a locking system as the performance was significantly improved to a runtime of (~15000ms - ~20000ms). I felt like my runtime was still a bit slow. I decided to discard the atomization of the sieve array, and just use a normal boolean array like I did in the beginning. I'm not sure if my implementation of the atomic booleans was correct or not, but discarding them provided the last boost of efficiency that I needed. After reverting, my runtime decreased to an average of ~3000ms. I was satisfied with my results, as the implementation of a lock provided accurate results, and was efficient enough to run in a few seconds.


