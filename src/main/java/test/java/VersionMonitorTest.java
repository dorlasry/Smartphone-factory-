

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import bgu.spl.a2.VersionMonitor;

public class VersionMonitorTest {

	VersionMonitor verMon;
	@Before
	public void setUp() throws Exception {
		 verMon=new VersionMonitor();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetVersion() {
		int ver=verMon.getVersion();
		assertTrue(ver>=0);
		verMon.inc();
		assertEquals(ver+1, verMon.getVersion());
	}

	@Test
	public void testInc() {
		int oldVer=verMon.getVersion();
		verMon.inc();
		assertEquals(oldVer+1, verMon.getVersion());
	}

	@Test
	public void testAwait() {
		
		Thread t1 = new Thread(()->{while(true){verMon.inc();}});
		t1.start();
		try{
			verMon.await(verMon.getVersion());
			fail();
		}catch (InterruptedException e){
			t1.interrupt();
			assertTrue(e instanceof InterruptedException);
		}
	}

}
