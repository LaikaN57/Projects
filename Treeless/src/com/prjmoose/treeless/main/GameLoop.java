package com.prjmoose.treeless.main;

import com.prjmoose.treeless.Game;
import com.prjmoose.treeless.views.PlayerInterfacePanel;

public class GameLoop implements Runnable {
	public static final int DEFAULT_UPDATE_RATE = 100;
	
	private int updateRate;

	private volatile Thread thisThread;

	// Local World
	private Game localWorld;

	// Remote World Link
	//private ServerInterface remoteWorldLink;

	public GameLoop(Game localWorld) {
		this.localWorld = localWorld;
		
		updateRate = DEFAULT_UPDATE_RATE;
	}
	
    public Thread start() {
    	if(thisThread != null) {
    		return thisThread;
    	}

    	thisThread = new Thread(this);
    	thisThread.start();
    	
    	return thisThread;
    }

	@Override
	public void run() {
		long lastTime;
		long startTime;
		long deltaTime;
		long endTime;
		long totalTime;
		long waitTime;
		long overTime = 0;
		
		startTime = System.nanoTime();

		while (thisThread == Thread.currentThread()) {
	        try {
				// Start loop timer
	        	lastTime = startTime;
				startTime = System.nanoTime();
				deltaTime = startTime - lastTime;
				
//				if(remoteWorldLink != null) {
//					// TODO Update localWorld based on remoteWorld
//					// force update on any new info from remoteWorld
//				}

				if(localWorld.getStartTime() < System.currentTimeMillis()) {
					
					// Update localWorld
					localWorld.update(deltaTime);
				}
	
				// End loop timer
				endTime = System.nanoTime();
	
				// Calculate waitTime
				totalTime = (endTime - startTime) / 1000000;
				waitTime = 1000 / updateRate - totalTime;
	
				// Protect from negative waitTimes
				if(waitTime < 0) {
					overTime += -waitTime;
					System.out.printf("GameLoop took %d ms (%d ms over target). Total over time: %d ms\n", totalTime, -waitTime, overTime);
					continue;
				} else if(waitTime > 0 && overTime > 0) {
					waitTime--;
					overTime--;
				}

				// Wait for the rest of the update period
				try {
					Thread.sleep(waitTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				// FIXME This is supposed to be InterruptedException
	        } catch (Exception e){
				e.printStackTrace();
	        }
		}
	}

	public void stop() {
		thisThread = null;
	}

	public Thread getThread() {
		return thisThread;
	}

	public int getUpdateRate() {
		return updateRate;
	}
}
