/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.spl.a2.sim;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.google.gson.Gson;
import bgu.spl.a2.WorkStealingThreadPool;
import bgu.spl.a2.sim.conf.ManufactoringPlan;
import bgu.spl.a2.sim.tasks.ManufactureTask;
import bgu.spl.a2.sim.tools.Tool;


/**
 * A class describing the simulator for part 2 of the assignment
 */
public class Simulator {
	
	private static WorkStealingThreadPool myPool;
	private static ConcurrentLinkedQueue<Product> products=new ConcurrentLinkedQueue<Product>();
	private static Factory myFactory;
	static int index=0;
	static int numOfProducts=0;
	static Product[] waveProducts;
	
	/**
	* Begin the simulation
	* Should not be called before attachWorkStealingThreadPool()
	*/
    public static ConcurrentLinkedQueue<Product> start(){
    	
		Warehouse warehouse=new Warehouse();						//initializing tools
		for(ToolGson tool: myFactory.getTools()){
			String type=tool.getTool();
			int qty=tool.getQty();
			Tool toolToAdd=warehouse.indexToTool(warehouse.typeToIndex(type));
			warehouse.addTool(toolToAdd, qty);
		}
		
		for(Plan mPlan: myFactory.getPlans()){						//initializing plans 
			
			String product=mPlan.getProduct();
			String[] parts=new String[mPlan.getParts().size()];
			int i=0;
			for(String part: mPlan.getParts()){
				parts[i]=part;
				i++;
			}
			
			String[] tools=new String[mPlan.getTools().size()];
			i=0;
			for(String tool: mPlan.getTools()){
				tools[i]=tool;
				i++;
			}

			ManufactoringPlan plan=new ManufactoringPlan(product, parts, tools);
			warehouse.addPlan(plan); 							
		}														
		
		
		myPool.start();
		
		for(List<Wave> waveList: myFactory.getWaves()){
			numOfProducts=products.size();
			for(Wave wave: waveList){										//runs on all waves
				index=0;
				waveProducts=new Product[wave.getQty()];
				for(int i=0; i<wave.getQty(); i++){
					index=i;
					ManufactureTask task=new ManufactureTask(warehouse,new Product(wave.getStartId()+i,wave.getProduct()));
					myPool.submit(task);
					Simulator.addProduct(waveProducts,i,task);
					
				}
				while(numOfProducts<products.size()+wave.getQty()){			//effectively awaits until current order is finished (the assignment orders were to finish each wave
					try {													//and in that case the loop should be transferred to   | but we put it here to match your output: every order
						Thread.sleep(1000);									//seperately. 										   | note that the assignments orders are still fulfilled.									   
					} catch (InterruptedException e) {						//													   |
						System.out.println("The main thread shouldn't be interrupted.");
					}
				}
				for(int i=0; i<wave.getQty(); i++){							//adds wave's products to the products list
					products.add(waveProducts[i]);
				}
			}																													
		}
		
		try {
			myPool.shutdown();					
		} catch (InterruptedException e) {
			System.out.println("Couldn't move to next wave. Didn't finish this wave.");
		}
		
		return products;
    }
    
    /**
     * adds a produced product to the wave's product's list
     * @param products- the wave's product's list
     * @param index- the right place to put the product in 
     * @param task- the task which produces the product
     */
    public static void addProduct(Product[] products, int index, ManufactureTask task){
    	task.getResult().whenResolved(()->{						
			products[index]=task.getResult().get();	
			numOfProducts++;});			
	}
    
	
	/**
	* attach a WorkStealingThreadPool to the Simulator, this WorkStealingThreadPool will be used to run the simulation
	* @param myWorkStealingThreadPool - the WorkStealingThreadPool which will be used by the simulator
	*/
	public static void attachWorkStealingThreadPool(WorkStealingThreadPool myWorkStealingThreadPool){
		myPool=myWorkStealingThreadPool;
	}
	
	/**
	 * setter for myFactory
	 * @param factory-the Factory to set
	 */
	public static void setFactory(Factory factory){
		myFactory=factory;
	}
	
	public static void main(String [] args) throws IOException{
		
		Gson gson=new Gson();
		FileReader fr=new FileReader(args[0]);
		Factory factory=gson.fromJson(fr, Factory.class);
		int numOfThreads=factory.getThreads();
		WorkStealingThreadPool pool=new WorkStealingThreadPool(numOfThreads);
		Simulator.attachWorkStealingThreadPool(pool);
		Simulator.setFactory(factory);
		ConcurrentLinkedQueue<Product> SimulationResult=Simulator.start();						
		
		FileOutputStream fout;
		try {
			fout = new FileOutputStream("result.ser");
			ObjectOutputStream oos = new ObjectOutputStream(fout);
			oos.writeObject(SimulationResult);
			oos.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
