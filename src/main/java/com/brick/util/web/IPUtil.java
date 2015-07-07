package com.brick.util.web;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class IPUtil {
	private static ArrayList<Long> start;
	private static ArrayList end;
	private static ArrayList adress;

	private static int ipSize;
	
	static{
		init("");
	}
	
	//将文本文件读入数组中
	private static int init(String filename){
		start=new ArrayList();
		end=new ArrayList();
		adress=new ArrayList();

		
		int count=0;
		File file=new File(filename);
		FileReader reader=null;
		try {
			reader=new FileReader(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		BufferedReader buffer=new BufferedReader(reader);
		String line=null;
		try {
			while((line=buffer.readLine())!=null){
				StringTokenizer token=new StringTokenizer(line);
				if(token.hasMoreTokens()){
					start.add(new Long(token.nextToken()));
				}else{
					System.out.println("start error");
					break;
				}
				if(token.hasMoreTokens()){
					end.add(new Long(token.nextToken()));
				}else{
					System.out.println("end error");
					break;
				}
				if(token.hasMoreTokens()){
					adress.add(token.nextToken());
				}else{
					adress.add("");
				}

				count++;
			}
			
			ipSize = start.size();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return count;
	}
	//查找IP所在数组序号
	private static int find(long ip){
		
		if(ip>4278190079L)return -1;
		
		int a,b,c;
		a=0;
		b=ipSize;
		c=b/2;
		long value1=-1,value2=-1;
		while(a<=b){
			value1=((Long)start.get(c)).longValue();
			if(value1==ip){
				break;
			}
			if(ip<value1){
				b=c-1;
			}else{
				a=c+1;
			}
			c=(a+b)/2;
		}
		
		if(a<=b){
			return c;
		}else{
			value2=((Long)end.get(b)).longValue();
			if(ip<=value2){
				return b;
			}else{
				return -1;
			}
		}
	}
	//将字符串IP变成数值
	private static long change(String ip){
		StringTokenizer token=new StringTokenizer(ip,".");
		if(token.countTokens()!=4)return -1;
		long value=0;
		while(token.hasMoreTokens()){
			long temp=Long.parseLong(token.nextToken());
			value=value*256+temp;
		}
		return value-1;
	}
	
	public static String getadress(String ip){
		int i = find(IPUtil.change(ip));
		if(i!=-1){
			return (String)adress.get(i);
		}else{
			return "unknowIP";
		}
	}
	

	
	public static void main(String[] args){
		long l1=System.currentTimeMillis();

			System.out.println(IPUtil.getadress("61.153.135.125"));
			System.out.println(IPUtil.getadress("127.0.0.1"));
			System.out.println(IPUtil.getadress("19442.168.145.1153"));
	
		long l2=System.currentTimeMillis();
		System.out.println("耗时 "+(l2 - l1)+"ms");
	}
}
