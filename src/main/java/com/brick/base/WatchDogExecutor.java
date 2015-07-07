package com.brick.base;

public class WatchDogExecutor {
	
	private WatchDogForQuartz watchDogForQuartz;

	public WatchDogForQuartz getWatchDogForQuartz() {
		return watchDogForQuartz;
	}

	public void setWatchDogForQuartz(WatchDogForQuartz watchDogForQuartz) {
		this.watchDogForQuartz = watchDogForQuartz;
	}
	
	public void execute(){
		if (this.watchDogForQuartz != null) {
			Thread watchDogForQuartzThread = new Thread(this.watchDogForQuartz);
			watchDogForQuartzThread.start();
		} else {
			System.out.println("初始化失败。");
		}
	}
}
