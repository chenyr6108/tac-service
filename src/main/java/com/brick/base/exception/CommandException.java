package com.brick.base.exception;

public class CommandException extends RuntimeException {
	public CommandException(Exception e){
		super(e);
	}
}
