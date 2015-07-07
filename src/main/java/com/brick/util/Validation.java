package com.brick.util;

import java.util.List;
import java.util.Map;

import com.brick.service.entity.Context;

public class Validation {
	
	public static void validateString(						
								 final String title, 
								 final String varName, 
								 Context context,
								 final boolean allowNull,
								 final int minLength,
								 final int maxLength){
		
		String var = (String)context.getContextMap().get(varName);		
		List errList = context.errList;
		
		if(!allowNull && var == null){
			errList.add(title + "不能为空");
			return;
		} 
//		if (!(var.length()>5&var.length()<9)) {
//			errList.add(title + "必须在6~8位之间");
//			return;
//		}
		
		
		if(minLength>0 && var.length()<minLength){
			errList.add(title + " 最少需要 " + minLength + " 个字符 ");
			return;
		}
		
		if(maxLength>0 && var.length()>maxLength){
			errList.add(title + " 最长只能 " + maxLength + " 个字符");
			return;
		}
		
		
		
	}
	
	
	public static void validateStringEqual(						
			 final String titleA, 
			 final String varNameA, 
			 final String titleB, 
			 final String varNameB,
			 Context context){

		String varA = (String)context.getContextMap().get(varNameA);	
		String varB = (String)context.getContextMap().get(varNameB);
		
		if(!varA.equals(varB)){
			context.errList.add(titleA + " 与 " + titleB + " 不符");
			return;
		}
	}
	
}
