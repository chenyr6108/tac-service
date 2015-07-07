package com.brick.base.command;

import java.util.List;

import com.brick.base.exception.CommandException;
import com.brick.base.exception.ServiceException;
import com.brick.base.service.BaseService;
import com.brick.base.to.BaseTo;
import com.brick.service.core.AService;

public class BaseCommand extends AService{
	public BaseService baseService;

	public BaseService getBaseService() {
		return baseService;
	}

	public void setBaseService(BaseService baseService) {
		this.baseService = baseService;
	}
	
}
