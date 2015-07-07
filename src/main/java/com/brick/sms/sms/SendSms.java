/**
 * @(#)MyClient.java 2009-11-10 Copyright 2009 LINKAGE, Inc. All rights
 *                   reserved. LINKAGE PROPRIETARY/CONFIDtheENTIAL. Use is
 *                   subject to license terms.
 */
package com.brick.sms.sms;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.brick.dataDictionary.service.DictionaryUtil;
import com.brick.sms.email.TestMail;
import com.linkage.netmsg.NetMsgclient;
import com.linkage.netmsg.server.AnswerBean;
import com.linkage.netmsg.server.ReceiveMsg;

/**
 * @description
 * @author li
 * @date 2009-11-10
 * @version 1.0.0
 * @since 1.0
 */
public class SendSms {

    
    static OutputStream out;

    static InputStream in = null;
    
    public static boolean result;
    
    @SuppressWarnings("unchecked")
    public static String sendSms(Map map) {
    	
    	List messageInfor=null;
    	String end = "短信发送失败";
    	 try {
    		 messageInfor = (List<Map>)DictionaryUtil.getDictionary("手机短信");
			for (Iterator iterator = messageInfor.iterator(); iterator.hasNext();) {
				Map object = (Map) iterator.next();
				map.put(object.get("FLAG").toString(),object.get("CODE").toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
        
		NetMsgclient client   = new NetMsgclient();
		if(client==null)
			System.out.println(123);
        /*ReceiveMsgImpl为ReceiveMsg类的子类，构造时，构造自己继承的子类就行*/
        ReceiveMsg receiveMsg = new ReceiveSms();
        
        /*初始化参数*/
        client = client.initParameters(map.get("ip").toString(), Integer.parseInt(map.get("port").toString()), map.get("userName").toString(), map.get("passWord").toString(),receiveMsg);
        try {
            
            /*登录认证*/
            boolean isLogin = client.anthenMsg(client);
            if(isLogin)System.out.println("login sucess");
            

            /*发送短信*/
          	client.sendMsg(client, 0, map.get("toPhone").toString(), map.get("content").toString(), 1);
            
           if(client.getSeqId()%1000==0)
        	   end = "短信发送成功";
           else
        	   end = "短信发送失败";
            
           //关闭发送短信与接收短信连接
           client.closeConn();
            
        } catch (Exception e1) {
            e1.printStackTrace();
            client.closeConn();
        }
    	
    	
    	return end;
    }
    

    /**
     * @description
     * @author li
     * @date 2009-11-10
     * @version 1.0.0
     * @param args
     */

    public static void main(String[] args) {
        
    	Map map=new HashMap();
		map.put("toPhone", "13603981343");
		map.put("content", "ceshi!~");
		System.out.println(SendSms.sendSms(map));
    }
}