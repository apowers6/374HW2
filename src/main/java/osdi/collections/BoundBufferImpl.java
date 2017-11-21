package osdi.collections;

import osdi.locks.*;
import java.util.ArrayDeque;



/*
Modify this as you see fit. you may not use anything in java.util.concurrent.* you may only use locks from osdi.locks.
*/


class BoundBufferImpl<T> implements SimpleQueue<T> {
    private final int bufferSize;
    private final java.util.Queue<T> queue;
    private Mutex mutex;
    private Semaphore full;
    private Semaphore empty;


    public BoundBufferImpl(int bufferSize) {
        this.bufferSize = bufferSize;
        queue = new java.util.ArrayDeque<>();
        mutex = new Mutex();
        empty = new Semaphore(size());
        full = new Semaphore(0);
    }

  @Override
    public void enqueue(T item) {

        while (queue.size() == bufferSize) {
               empty.down();
	}
	mutex.lock();
	if (item != null) {
            queue.add(item);
        }
        mutex.unlock();
	full.up();
    }

 @Override
    public T dequeue() {

        T item = null;
        
	while(queue.isEmpty()){
               full.down();
        }
        mutex.lock();
	if(!queue.isEmpty()) {
            item = queue.remove();
        }
        mutex.unlock();
	empty.up();
        return item;
    }

   public int size() {
    	mutex.lock();
    	int size = queue.size();
    	mutex.unlock();
    	return size;
    }
}

