/**
 * Author : zxb 
 */
package com.brick.util;

import java.security.MessageDigest;

/**
 * @author zxb
 * @version 1 at Oct 23, 2005
 * <p/>
 */
public class MD5 {

	public static String digest(String str){
		StringBuffer sb = new StringBuffer();
		
		try{
			MessageDigest md5 = MessageDigest.getInstance( "md5" );
		    md5.update( str.getBytes( "ISO8859-1") );
		    byte[] array = md5.digest();		    
		    for( int x=0; x<16; x++ )
			{
			  if( ((int)array[x] & 0xff) < 0x10 )
			    sb.append( "0" );
			  
			  	sb.append( Long.toString( (int)array[x] & 0xff, 16 ));
			}
		}
	    catch(Exception e){
	    	System.out.println(e.getCause().getMessage());
	    }
	   
	    return sb.toString();
	}
	
	public static void main(String args[]) {
		System.out.println(MD5.digest("111"));
		
	}

}
