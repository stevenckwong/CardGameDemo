package com.stevenckwong.cardgamedemo;

public class RandomErrorManager {
	
	static private boolean randomErrorOn;
	static private int percentError;
	
	
	public RandomErrorManager() {
		randomErrorOn = true;
		percentError = 5;
	}
	
	static public void generateRandomError() throws Exception {
		if (randomErrorOn) {
			int random = (int)(Math.random() * 100 + 1);   // random generates a random number between 0 to 0.99999
			if (random <= percentError) {
				throw new Exception("Random Generated Application Exception. Random: " + random + ", Percent: " + percentError);
			}
		}
	}

	
	static public void setPercentError(int newPercentError) {
		percentError = newPercentError;
	}
	
	static public int getPercentError() {
		return percentError;
	}
	
	static public void turnOnRandomError() {
		randomErrorOn = true;
	}
	
	static public void turnOffRandomError() {
		randomErrorOn = false;
	}
	
	static public boolean randomErrorIsOn() {
		return randomErrorOn;
	}
	
}
