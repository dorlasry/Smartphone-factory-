package bgu.spl.a2.sim;

import java.util.ArrayList;
import java.util.List;

/**
 * A class that represents a product produced during the simulation.
 */
public class Product implements java.io.Serializable{
	
	
	final long startId;
	long finalId=0;
	private String name;
	private List<Product> partList=new ArrayList<>();
	
	/**
	* Constructor 
	* @param startId - Product start id
	* @param name - Product name
	*/
    public Product(long startId, String name){
    	this.startId=startId;
    	this.name=name;
    	finalId=startId;
    }

	/**
	* @return The product name as a string
	*/
    public String getName(){
    	return name;
    }

	/**
	* @return The product start ID as a long. start ID should never be changed.
	*/
    public long getStartId(){
    	return startId;
    }
    
	/**
	* @return The product final ID as a long. 
	* final ID is the ID the product received as the sum of all UseOn(); 
	*/
    public long getFinalId(){
        return finalId;
    }

	/**
	 * setter for finalId
	 * @param finalId
	 */
    public void setFinalId(long finalId) {
		this.finalId = finalId;
	}

	/**
	* @return Returns all parts of this product as a List of Products
	*/
    public List<Product> getParts(){
    	return partList;
    }

	/**
	* Add a new part to the product
	* @param p - part to be added as a Product object
	*/
    public void addPart(Product p){
    	partList.add(p);
    }


}
