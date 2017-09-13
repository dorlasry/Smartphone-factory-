package bgu.spl.a2.sim.tools;

import bgu.spl.a2.sim.Product;
import java.lang.Math;
import java.math.BigInteger;

public class GcdScrewDriver implements Tool {
	
	private String type="gs-driver";
	private final int index=0;

	@Override
	public String getType() {
		return type;
	}
	
	@Override
	public int getIndex() {
		return index;
	}

	@Override
	public long useOn(Product p){
	    	long value=0;
	    	for(Product part : p.getParts()){
	    		value+=Math.abs(fGcd(part.getFinalId()));
	    		
	    	}
	    	if(p.getName().equals("touch-contorller")) System.out.println("touch was inc by"+value);
	      return value;
	    }

	private long fGcd(long id){
    	BigInteger b1 = BigInteger.valueOf(id);
        BigInteger b2 = BigInteger.valueOf(reverse(id));
        long value= (b2.gcd(b1)).longValue();
        return value;
    }
	
	private long reverse(long number)
	{
		
		long revNumber=0;
		while(number!=0){
			  revNumber = revNumber * 10 + number % 10;
			  number = number / 10;
				}
		return revNumber;
	}
	

	

}
