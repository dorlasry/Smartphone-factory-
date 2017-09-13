package bgu.spl.a2;

import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * this class represents a single work stealing processor, it is
 * {@link Runnable} so it is suitable to be executed by threads.
 *
 * Note for implementors: you may add methods and synchronize any of the
 * existing methods in this class *BUT* you must be able to explain why the
 * synchronization is needed. In addition, the methods you add can only be
 * private, protected or package protected - in other words, no new public
 * methods
 *
 */
public class Processor implements Runnable {

    private final WorkStealingThreadPool pool;
    private final int id;

    /**
     * constructor for this class
     *
     * IMPORTANT:
     * 1) this method is package protected, i.e., only classes inside
     * the same package can access it - you should *not* change it to
     * public/private/protected
     *
     * 2) you may not add other constructors to this class
     * nor you allowed to add any other parameter to this constructor - changing
     * this may cause automatic tests to fail..
     *
     * @param id - the processor id (every processor need to have its own unique
     * id inside its thread pool)
     * @param pool - the thread pool which owns this processor
     */
    /*package*/ Processor(int id, WorkStealingThreadPool pool) {
        this.id = id;
        this.pool = pool;
    }

    @Override
    /**
     * The main function of the processor: it starts doing the tasks from it's queue.
     */
    public void run() {
    	ConcurrentLinkedDeque<Task<?>> taskQueue = pool.getSpecificQueue(id);
    
    	while (!Thread.currentThread().isInterrupted()){
    		if(taskQueue.isEmpty())
    			steal();
    		else 					
    			while(!taskQueue.isEmpty()){
    				Task<?> taskTodo;									//to ensure that no one steals until 'this' takes a task
    				synchronized(taskQueue){								
    	        		if(!taskQueue.isEmpty()){
    	        			taskTodo = taskQueue.removeFirst();
    	        			taskTodo.handle(this);
    	        			pool.decrementTaskCount();
    	        		}
    				}
    			}
   		}
    }
   
    /**
     * Adds a sub-task to it's queue	
     * @param task - the sub task
     */
    void addSpawnedTask(Task<?> task){
    	pool.submitSpawnedTask(id, task);
    }
    
   
    /**
     * Steals a task - asking the pool to steal a task
     */
    void steal(){
    	pool.steal(id);
    }
    
    /**
     * getter
     * @return id - the processor's id
     */
    int getId(){
    	return id;
    }
}
