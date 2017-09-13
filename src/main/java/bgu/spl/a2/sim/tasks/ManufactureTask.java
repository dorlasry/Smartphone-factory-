package bgu.spl.a2.sim.tasks;

import java.util.ArrayList;
import java.util.List;

//import bgu.spl.a2.Deferred;
import bgu.spl.a2.*;
import bgu.spl.a2.sim.Product;
import bgu.spl.a2.sim.Warehouse;
import bgu.spl.a2.sim.conf.ManufactoringPlan;
import bgu.spl.a2.sim.tools.Tool;

public class ManufactureTask extends Task<Product> {				

	Warehouse warehouse;
	Product product;												
	
	public ManufactureTask(Warehouse warehouse, Product product){
		this.warehouse=warehouse;
		this.product=product;
	}
	
	@Override
	/**
	 * starts the task- producing the product
	 */
	protected void start() {
		ManufactoringPlan productPlan=warehouse.getPlan(product.getName());		
		List< ManufactureTask> taskList= new ArrayList<>();
		ManufactureTask partPlan;
		for(String part : productPlan.getParts()){							//producing the product's parts
			Product productPart=new Product(product.getStartId()+1, part);
			partPlan=new ManufactureTask(warehouse,productPart);
			spawn(partPlan);
			taskList.add(partPlan);			
		}
		
		whenResolved(taskList,()->{											
			for(ManufactureTask task: taskList)						//adding the parts to their list
				product.addPart(task.product);  						
			for(String toolName : productPlan.getTools()){					//use every tool on the product's parts when
					Deferred<Tool>  tool=warehouse.acquireTool(toolName);
					tool.whenResolved(()->{														//the tool is acquired
						synchronized(product){													//sync- to make sure only one tool is 'working' on the product at the same time
							product.setFinalId(product.getFinalId()+tool.get().useOn(product));
						}
					warehouse.releaseTool(tool.get());						
				});
			}
			complete(product);
		}); 		
	}
}
