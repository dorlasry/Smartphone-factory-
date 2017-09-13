package bgu.spl.a2;

import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * represents a work stealing thread pool - to understand what this class does
 * please refer to your assignment.
 *
 * Note for implementors: you may add methods and synchronize any of the
 * existing methods in this class *BUT* you must be able to explain why the
 * synchronization is needed. In addition, the methods you add can only be
 * private, protected or package protected - in other words, no new public
 * methods
 */
public class WorkStealingThreadPool {

    private Thread[] threads;
    private Processor[] processors;
    private ConcurrentLinkedDeque<Task<?>>[] queues;
    private final int nthreads;
    private boolean start=false, shouldStop=false;
    private AtomicInteger taskCount=new AtomicInteger(0);
    private VersionMonitor ver=new VersionMonitor();
	/**
     * creates a {@link WorkStealingThreadPool} which has nthreads
     * {@link Processor}s. Note, threads should not get started until calling to
     * the {@link #start()} method.
     *
     * Implementors note: you may not add other constructors to this class nor
     * you allowed to add any other parameter to this constructor - changing
     * this may cause automatic tests to fail..
     *
     * @param nthreads the number of threads that should be started by this
     * thread pool
     */
    public WorkStealingThreadPool(int nthreads) {
        this.nthreads=nthreads;
    	threads=new Thread[nthreads];
        processors=new Processor[nthreads];
        queues=new ConcurrentLinkedDeque[nthreads];
        for(int i=0; i<nthreads; i++){
        	processors[i]=new Processor(i, this);
        	threads[i]=new Thread(processors[i]);
        	queues[i]=new ConcurrentLinkedDeque<Task<?>>();
        }
    }

    /**
     * submits a task to be executed by a processor belongs to this thread pool
     * @param task- the task to execute
     */
    public void submit(Task<?> task) {
    	int num=(int)(Math.random()*nthreads);
    	queues[num].addFirst(task);
    	taskCount.incrementAndGet();
    	ver.inc();
    }

    /**
     * submits a task to be executed by the processor which spwaned it.
     * @param task- the task to execute
     * @param id- the processor's id
     */
    void submitSpawnedTask(int id, Task<?> task){
    	queues[id].add(task);
    	taskCount.incrementAndGet();
    	ver.inc();
    }
    
    /**
     * decrements the counter of the tasks
     */
    void decrementTaskCount(){
    	taskCount.decrementAndGet();
    }
    
    /**
     * getter for specific processor's queue
     * @param id-the requested processor
     * @return - the requested queue
     */
    ConcurrentLinkedDeque<Task<?>> getSpecificQueue(int id){
    	return queues[id];
    }
    
    /**
     * closes the thread pool - this method interrupts all the threads and wait
     * for them to stop - it is returns *only* when there are no live threads in
     * the queue.
     *
     * after calling this method - one should not use the queue anymore.
     *
     * @throws InterruptedException if the thread that shut down the threads is
     * interrupted
     * @throws UnsupportedOperationException if the thread that attempts to
     * shutdown the queue is itself a processor of this queue
     */
    public void shutdown() throws InterruptedException {
    	if(!start){
    		Thread.currentThread().interrupt();
    	}
    	
    	while (taskCount.get()!=0)							//effectively waits for all processors to finish their tasks
    		Thread.sleep(1000);
    	for(int i=0; i<nthreads; i++)						
    		if(Thread.currentThread().equals(threads[i]))
    			throw new UnsupportedOperationException("A processor can't shutdown the pool.");
    	shouldStop=true;
    	ver.inc();
    	for(int i=0; i<nthreads; i++){						//interrupts all threads to let them 'know' they should stop working
    			threads[i].interrupt();
    	}
    }

    /**
     * start the threads belongs to this thread pool
     */
    public void start() {
    	
    	start=true;
    	for(int i=0; i<nthreads; i++)
    		threads[i].start();
    }

	
	/**
	 * getter for the number of threads
	 * @return nthreads- the number of threads
	 */
	int getNumOfThreads(){
		return nthreads;
	}
	
	/**
	 * transfers tasks between processors, in order to balance the work overload
	 * @param id- the processor which is requesting tasks
	 */
	void steal(int id){
		boolean stole=false;
		int toStealFrom=(id+1)%nthreads;
		int currentVersion=ver.getVersion();
		while(!stole && !shouldStop){						//checking the stealing processor doesn't have a task, and that there are tasks to steal 		
			if(toStealFrom==id){							//if all other processors were checked to steal from them, awaits until another task will be added				
				try {
					ver.await(currentVersion);				
				} catch (InterruptedException ignored) {}
				currentVersion=ver.getVersion();
				toStealFrom=(id+1)%nthreads;
				if(!queues[id].isEmpty()){					//to verify 'this' wasn't the processor which got the task				
					stole=true;
				}
			}
			else{
				synchronized(queues[toStealFrom]){								//because .size() isn't atomic-two processors might try to steal, and one might try to access cells which don't exist(were stolen)
					if(queues[toStealFrom].size()>0){							
						int toSteal=(queues[toStealFrom].size()+1)/2;			
						for(int i=0; i<toSteal; i++){
							queues[id].add(queues[toStealFrom].removeLast());
						}
						stole=true;
					}
				}
			}
			toStealFrom=(toStealFrom+1)%nthreads;
		}
	}
}
