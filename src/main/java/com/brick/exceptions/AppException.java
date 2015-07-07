package com.brick.exceptions;
/**
 * 系统中用到的自定义异常，此类仅仅给定一个模板(自定义类的模板)
 * exception by myself
 * this is a template please must be define exception like this if your excepion
 * by yourself
 * key :error key
 * values: key -> values
 * @author yangxuan
 *
 */
	
public class AppException extends RuntimeException {
	/**
	 *exception by myself 
	 */
	private static final long serialVersionUID = 1L;
	private String key;
	private Object values[];
	public AppException(String key,String msg) {
		this(msg);
		this.key = key;
	}
	public AppException(String key,Object value,String msg) {
		this(msg);
		this.key = key;
		this.values = new Object[]{value};
	}
	public AppException(String key,Object[] values,String msg) {
		this(msg);
		this.key = key;
		this.values = values;
	}
	public String getKey() {
		return key;
	}
	public Object getValues() {
		return values;
	}
	public AppException() {
		super();
	}
	public AppException(String msg) {
		super(msg);
	}
	public AppException(String msg,Throwable cause) {
		super(msg, cause);
	}
	public AppException(Throwable cause) {
		super(cause);
	}
	
	@Override public void printStackTrace() {
		super.getCause().getMessage().toString();
	}
}
