package com.brick.base.exception;

public class ProcessException extends RuntimeException {
	public ProcessException(Exception e){
		super(e);
	}
	public ProcessException(String msg){
		super(msg);
	}
}
