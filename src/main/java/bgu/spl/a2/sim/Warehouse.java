package bgu.spl.a2.sim;
import bgu.spl.a2.sim.tools.*;
import bgu.spl.a2.sim.conf.ManufactoringPlan;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;

import bgu.spl.a2.Deferred;


/**
 * A class representing the warehouse in your simulation
 * 
 * Note for implementors: you may add methods and synchronize any of the
 * existing methods in this class *BUT* you must be able to explain why the
 * synchronization is needed. In addition, the methods you add to this class can
 * only be private!!!
 *
 */
public class Warehouse {
	
	
	AtomicInteger[] amountOfTools;
	ConcurrentLinkedDeque<Deferred<Tool>> waitingListGSdriver; 
	ConcurrentLinkedDeque<Deferred<Tool>> waitingListNPhammer; 
	ConcurrentLinkedDeque<Deferred<Tool>> waitingListRSpliers; 	
	List<ManufactoringPlan> planList=new ArrayList<>();
	
	/**
	* Constructor
	*/
    public Warehouse(){
    	amountOfTools=new AtomicInteger[3];
    	for(int i=0; i<3; i++)
    		amountOfTools[i]=new AtomicInteger();
    	waitingListGSdriver=new ConcurrentLinkedDeque<Deferred<Tool>>();
    	waitingListNPhammer=new ConcurrentLinkedDeque<Deferred<Tool>>();
    	waitingListRSpliers=new ConcurrentLinkedDeque<Deferred<Tool>>();
    }
	
    /**
	* Tool acquisition procedure
	* Note that this procedure is non-blocking and should return immediatly
	* @param type - string describing the required tool
	* @return a deferred promise for the  requested tool
	*/
    public Deferred<Tool> acquireTool(String type)
    {
    	int index=typeToIndex(type); 
    	Deferred<Tool> d=new Deferred<>();
    	synchronized(amountOfTools[index]){			//sync- to make sure two threads won't take/return a tool to a specific list 
    		if(amountOfTools[index].get()!=0){
    			d.resolve(indexToTool(index));   			
    			amountOfTools[index].decrementAndGet();
    		}
    		else
    			addToWaitingList(index,d); 	
    	}
    	return d;    		
    }

	private void addToWaitingList(int index, Deferred<Tool> d) {		
		switch(index){
    	case 0: waitingListGSdriver.add(d); break;
    	case 1: waitingListNPhammer.add(d); break;
    	case 2: waitingListRSpliers.add(d); break;
    	}	
	}

	/**
	* Tool return procedure - releases a tool which becomes available in the warehouse upon completion.
	* @param tool - The tool to be returned
	*/
    public void releaseTool(Tool tool){
    	int index=tool.getIndex(); 
    	
    	synchronized(amountOfTools[index]){
    		if(isEmptyWaitingList(index)){
    			amountOfTools[index].incrementAndGet();
    		}
    		else{
    			releaseFromWaitingList(index);//    		
    		}
    	}
    }
    
    private void releaseFromWaitingList(int index) {
    	switch(index){
    	case 0: waitingListGSdriver.removeFirst().resolve(indexToTool(index)); break;
    	case 1: waitingListNPhammer.removeFirst().resolve(indexToTool(index)); break;
    	case 2: waitingListRSpliers.removeFirst().resolve(indexToTool(index)); break;
    	}		
	}

	private boolean isEmptyWaitingList(int index) {
    	switch(index){
    	case 0: return waitingListGSdriver.isEmpty(); 
    	case 1: return waitingListNPhammer.isEmpty(); 
    	case 2: return waitingListRSpliers.isEmpty(); 
    	}
    	return false;	//maybe needs to throw exception
    }

	/**
	 * transforms string tool's type to the right index in the array: 0=gcd 1=hammer 2= pliers. it's used in Simualtor class and therefore it's protedted
	 * @param type - the tools name
	 */
	protected int typeToIndex(String type) 
    {
    	switch(type){
    		case "gs-driver": return 0;
    		case "np-hammer":return 1;
    		case "rs-pliers":return 2;
    	}
    	return -1;
    }
    
	/**
	 * transforms an index to the right tool and returns it. it's used in Simualtor class and therefore it's protedted
	 * @param type - the right tool
	 */
    protected Tool indexToTool(int index){     	
    	switch(index){
    		case 0: return new GcdScrewDriver();
    		case 1: return new NextPrimeHammer();
    		case 2: return new RandomSumPliers();
    	}
    	return null;	
    }

	
	/**
	* Getter for ManufactoringPlans
	* @param product - a string with the product name for which a ManufactoringPlan is desired
	* @return A ManufactoringPlan for product
	*/
    public ManufactoringPlan getPlan(String product){
    	for(int i=0; i<planList.size(); i++){
    		if(planList.get(i).getProductName().equals(product)){
    			return planList.get(i);
    		}
    	}
    	return null;				
    }
	
	/**
	* Store a ManufactoringPlan in the warehouse for later retrieval
	* @param plan - a ManufactoringPlan to be stored
	*/
    public void addPlan(ManufactoringPlan plan){
    	planList.add(plan);
    }
    
	/**
	* Store a qty Amount of tools of type tool in the warehouse for later retrieval
	* @param tool - type of tool to be stored
	* @param qty - amount of tools of type tool to be stored
	*/
    public void addTool(Tool tool, int qty){	
    	int index=typeToIndex(tool.getType());
    	amountOfTools[index].addAndGet(qty); 
    }
    
  
    
    
    
    
    

}
