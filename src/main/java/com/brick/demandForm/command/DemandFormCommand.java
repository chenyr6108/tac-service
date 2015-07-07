package com.brick.demandForm.command;

import org.apache.log4j.Logger;

import com.brick.base.command.BaseCommand;
import com.brick.demandForm.service.DemandFormService;
import com.brick.service.entity.Context;

public class DemandFormCommand extends BaseCommand {
	
	Logger log = Logger.getLogger(DemandFormCommand.class);
	
	private DemandFormService demandFormService;

	public DemandFormService getDemandFormService() {
		return demandFormService;
	}

	public void setDemandFormService(DemandFormService demandFormService) {
		this.demandFormService = demandFormService;
	}
	
	
	public void showDemandFormPage(Context context){
		
	}
}
