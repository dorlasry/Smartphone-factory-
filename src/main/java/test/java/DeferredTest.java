

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import bgu.spl.a2.Deferred;
import junit.framework.Assert;

public class DeferredTest {

	Deferred <int[]> defTest;
	@Before
	public void setUp() throws Exception {
		defTest=new Deferred<int []>();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGet() {
		int[] i;
		try{
			i=defTest.get();
			fail();
		}catch(IllegalStateException e){
			assertTrue(e instanceof IllegalStateException);
		}		
	}

	@Test
	public void testGet2() {  	//positive test
		
		
		int[] j={}, i;
		defTest.resolve(j);
		try{
			i=defTest.get();
		}catch(Exception e){
			fail();
		}
		assertEquals(defTest.get(),j);
		
	}
	
	@Test
	public void testIsResolved() {
		if(defTest.isResolved())
		{
			try{
				int[] i=defTest.get();
			}catch(Exception e){
				fail();
			}
		}
		else
			try{
				int[] i=defTest.get();
				fail();
			}catch(IllegalStateException e){
				assertTrue(e instanceof IllegalStateException);
			}
	}

	@Test
	public void testResolve() {
		int [] i={};
		if(defTest.isResolved())
			try{
				defTest.resolve(i);
				fail();
			}catch (IllegalStateException e){
				assertTrue(e instanceof IllegalStateException);
			}
		else 
			defTest.resolve(i);
		assertEquals(defTest.get(),i);
		
	}

	@Test
	public void testWhenResolved() {
		class Temp{
			public  int check=0;
		}
		Temp t=new Temp();
		int [] i={};
		
		defTest.whenResolved(()->{t.check=1;});
		if(t.check!=0)
			fail();
		
		defTest.resolve(i);
		if(t.check!=1)
			fail();	}
	

}
