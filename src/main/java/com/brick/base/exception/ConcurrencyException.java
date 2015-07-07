package com.brick.base.exception;

public class ConcurrencyException extends Exception {
	private static final long serialVersionUID = 1L;

	public ConcurrencyException(){
		super("您操作的数据已被修改，请刷新页面。");
	}
}
