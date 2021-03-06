package osdi.primesolver;

import osdi.collections.BoundBuffer;
import osdi.locks.*;
import osdi.collections.SimpleQueue;

import java.util.ArrayList;
import java.util.Collection;

/*

Abby Powers- Homework #2- COMP 374 Fall 2017

 * you may not use anything in java.util.concurrent.* you may only use locks from osdi.locks.*
 */

public class PrimeNumberCounter {

    private long currentCount = 0L;
    private Semaphore semaphore;

    /*
     * you may not modify this method
     */

    private static int getThreadCount() {
        return Runtime.getRuntime().availableProcessors() * 4;
    }

 /* you may not modify the method, but you can modify the signature of the method if needed */

 private void startThreads(SimpleQueue<Long> valuesToCheck, SimpleQueue<Long> valuesThatArePrime) {
        
	Collection<Thread> threads = new ArrayList<>();
        int threadCount = getThreadCount();
        
        for(int i = 0; i < threadCount; i++) {
            Thread t = new Thread(()->findPrimeValues(valuesToCheck, valuesThatArePrime));
            t.setDaemon(true);
            threads.add(t);
        }
        Thread counter = new Thread(()->countPrimeValues(valuesThatArePrime));
        threads.add(counter);

        for(Thread t : threads) {
            t.setDaemon(true);
            t.start();
        }
    }

    /*
     * you may modify this method
     */
    public long countPrimeNumbers(NumberRange range) {
        SimpleQueue<Long> valuesToCheck = BoundBuffer.createBoundBufferWithSemaphores(100);
        SimpleQueue<Long> valuesThatArePrime = BoundBuffer.createBoundBufferWithSemaphores(50);
        
        startThreads(valuesToCheck, valuesThatArePrime);


        for(Long value : range) {
            valuesToCheck.enqueue(value);
        }
 
        return currentCount;
    }
	


    /*
     * you may modify this method
     */

private void findPrimeValues(SimpleQueue<Long> valuesToCheck, SimpleQueue<Long> valuesThatArePrime) {
         while(true) {
            Long current = valuesToCheck.dequeue();
           
         if (current!=null){
               
	    if(current%1000000==0) {
                    System.out.println(current); 
                }

	 if(Number.IsPrime(current)) {
                valuesThatArePrime.enqueue(current);
          } 
       }   
    }
}

  

    /*
     * you may modify this method
     */
 private void countPrimeValues(SimpleQueue<Long> valuesThatArePrime) {
        while(true) {

            Long current = valuesThatArePrime.dequeue();
            currentCount++;
            
            if(currentCount % 1000000 == 0) {
                System.out.println("The above range have " + currentCount + " prime values.");
                System.out.flush(); 
            }
       }
   }
}

