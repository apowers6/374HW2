
package osdi.collections;

import osdi.locks.Monitor;
import osdi.locks.SpinLock;

import java.util.ArrayDeque;


/* 

Modify this as you see fit. you may not use anything in java.util.concurrent.* you may only use locks from osdi.locks.

resources used: 11/16 class example of SpinLock & Monitor use. The tutor helped me form the while/if loops. I integrated his help and class SpinLock example to get it up and running.* 

*/
 
class BoundBufferImpl<T> implements SimpleQueue<T> {
    private final int bufferSize;
    private final java.util.Queue<T> queue;
    Monitor monitor;
    SpinLock lock;

    public BoundBufferImpl(int bufferSize) {
        this.bufferSize = bufferSize;
        queue = new ArrayDeque<>(bufferSize);
        monitor = new Monitor();
        lock = new SpinLock();
    }

  @Override
    public void enqueue(T item) {
        while (this.queue.size() == bufferSize) {
                monitor.sync((Monitor.MonitorOperations::Wait));
        }
        if (this.queue.size() >= 0) {
            monitor.sync((Monitor.MonitorOperations::pulse));
        }
        lock.lock();
        if (item != null) {
            queue.add(item);
        }
        lock.unlock();

    }

 @Override
    public T dequeue() {
        T item = null;
        while(queue.isEmpty()){
            monitor.sync((Monitor.MonitorOperations::Wait));
        }
        if(this.queue.size() <= bufferSize){
            monitor.sync((Monitor.MonitorOperations::pulse));
        }
        lock.lock();
        if (!queue.isEmpty()) {
            item = queue.remove();
        }
        lock.unlock();

        return item;
    }


   public int size() {
    	int size = queue.size();
    	return size;
    }
}


