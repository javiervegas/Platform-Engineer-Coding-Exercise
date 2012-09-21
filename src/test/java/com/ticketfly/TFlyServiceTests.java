package com.ticketfly;

import static org.junit.Assert.*;

import org.junit.Test;

public class TFlyServiceTests {

	@Test
	public void testExecute() {
		TFlyService service = new TFlyService();
		String resp = "fail";
		while(true){
			try {
				resp = service.execute("ticketfly");
				break;
			} catch (TFlyService.TFlyServiceException e){
				// ingore and loop
			}
		}
		assertEquals("ylftekcit", resp);
	}
	
	/**
	 * This maybe not the best way to test because this will periodically fail.
	 * Collect 5 exceptions which should happen in about 100 runs. I verify that
	 * The exception gets throws between 1 and 10 percent of the time which 
	 * accomplishes what I am after.
	 */
	@Test
	public void testException() {
		TFlyService service = new TFlyService();
		String tester = "tester";
		int i = 0;
		int j = 0;
		while(true){
			try{
				i++;
				service.execute(tester);
			} catch (TFlyService.TFlyServiceException e){
				j++;
				if( j == 5 )
					break;
			}
		}
		double d = (double) j / (double) i;
		assertTrue( .01d < d );
		assertTrue( .1d > d );
	}

}
