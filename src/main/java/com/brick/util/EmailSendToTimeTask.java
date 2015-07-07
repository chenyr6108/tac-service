package com.brick.util;
import java.util.TimerTask;
import javax.servlet.ServletContext;
import java.util.Calendar;
import com.brick.contract.service.LockManagementService;

public class EmailSendToTimeTask extends TimerTask
{
	
	private ServletContext context;  
    
    private static boolean isRunning = false; 
    
    private static final int C_SCHEDULE_HOUR = 12;
    
	public EmailSendToTimeTask(ServletContext context)
	{
		this.context = context; 
	}
	
	public void run() 
	{  
		Calendar cal = Calendar.getInstance();        
		if (!isRunning)  
		{           
			if (C_SCHEDULE_HOUR == cal.get(Calendar.HOUR_OF_DAY)) 
			{            
				isRunning = true;                
				context.log("开始执行指定任务");
				System.out.println("触发");
				//业务
//				LockManagementService lock=new LockManagementService();
//				lock.sendEmailToTime();
				
				isRunning = false;
				context.log("指定任务执行结束");  
			} 
			else 
			{
				context.log("上一次任务执行还未结束");
			} 
		}   
	}
	
}
