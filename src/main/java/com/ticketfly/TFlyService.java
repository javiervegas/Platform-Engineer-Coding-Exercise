package com.ticketfly;


/**
 * This is a mock service. It sleeps for 100 millis to simulate 
 * computation then reverses its input. It will throw a TFlyServiceException
 * for about 5% of the executions.
 * 
 * @author andy
 *
 */
public final class TFlyService {
	
	public final static class TFlyServiceException extends RuntimeException {
		private static final long serialVersionUID = 1L;
		
		TFlyServiceException(String message){
			super(message);
		}
		
	}
	
	public final String execute(final String string){
		try{
			// simulate computation
			Thread.sleep(100);
		} catch (InterruptedException e){
			// continue without error.
		}
		
		// reverse the string
		char[] reversed = new char[string.length()];
		int i = string.length() - 1;
		for(char c :string.toCharArray()){
			reversed[i--] = c;
		}
		
		// throw an exception for 5% of requests
		if(Math.random() > 0.95d)
			throw new TFlyServiceException("Service Error");
		
		return new String(reversed);
	}

}
