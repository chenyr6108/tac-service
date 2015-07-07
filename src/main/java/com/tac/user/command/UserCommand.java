package com.tac.user.command;



import java.util.List;

import com.brick.base.command.BaseCommand;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.tac.user.service.UserService;



public class UserCommand extends BaseCommand{
	
	public UserService userService;
	
	public void getUsers(Context context){		
		List list = userService.getAllUsers();
		Output.jsonArrayOutputForObject(list, context);
	}
	

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	
	
	
}
