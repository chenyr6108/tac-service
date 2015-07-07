package com.brick.common.sms.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.brick.base.service.BaseService;

public class SmsUtilService extends BaseService {
	
	public void sendSms(String phone, String msg, String sendBy){
		Map<String, Object> sendSmsEntity = new HashMap<String, Object>();
		sendSmsEntity.put("MESSAGE", msg);
		sendSmsEntity.put("SENDTYPE", 0);
		sendSmsEntity.put("MTEL", phone);
		sendSmsEntity.put("CREATE_BY", sendBy);
		sendSmsEntity.put("SEND_MODE","0");//0 means 手动, 1 means 自动
		sendSmsEntity.put("SEND_TYPE","1");//0 means 邮件, 1 means 发送短信
		update("lockManagement.createSendMsg", sendSmsEntity);
	}
	
	public void sendSmsBySystem(String phone, String msg){
		Map<String, Object> sendSmsEntity = new HashMap<String, Object>();
		sendSmsEntity.put("MESSAGE", msg);
		sendSmsEntity.put("SENDTYPE", 0);
		sendSmsEntity.put("MTEL", phone);
		sendSmsEntity.put("CREATE_BY", "System");
		sendSmsEntity.put("SEND_MODE","0");//0 means 手动, 1 means 自动
		sendSmsEntity.put("SEND_TYPE","1");//0 means 邮件, 1 means 发送短信
		update("lockManagement.createSendMsg", sendSmsEntity);
	}
	
	public void sendSms(List<String> phones, String msg, String sendBy){
		for (String phone : phones) {
			sendSms(phone, msg, sendBy);
		}
	}
	
}
