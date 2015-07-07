package com.brick.sms.email;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import com.brick.dataDictionary.service.DictionaryUtil;
import com.brick.service.core.AService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.brick.log.service.LogPrint;


public class TestMail extends AService{
	static Log logger = LogFactory.getLog(TestMail.class);
/**
 * Map map
 * 		map.put("toAddress", "");接收人地址
		map.put("subject", "");邮件主题
		map.put("content", "");邮件内容
 */	
	@SuppressWarnings("unchecked")
	public static String seneMail(Map map) {
		List mailInfor=null;
		
		try {
			 mailInfor = (List<Map>)DictionaryUtil.getDictionary("邮件");
			for (Iterator iterator = mailInfor.iterator(); iterator.hasNext();) {
				Map object = (Map) iterator.next();
				map.put(object.get("FLAG").toString(),object.get("CODE").toString());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 
		
		MailInfo mailinfo=new MailInfo();
		//--设置邮件服务器开始
		mailinfo.setMailServerHost(map.get("mailServerHost").toString());
		mailinfo.setMailServerPort(map.get("mailServerPort").toString());
		mailinfo.setValidate(true);
		mailinfo.setUserName(map.get("userName").toString());
		mailinfo.setPassword(map.get("password").toString());
		//--设置邮件服务器结束
		mailinfo.setFromAddress(map.get("fromAddress").toString());//邮件发送者的地址
		//设置接受用户
		String []ToAddress={map.get("toAddress").toString()};
		mailinfo.setToAddress(ToAddress);
		//设置附件
		String []attach={"C:\\2312.docx"};
		mailinfo.setAttachFileNames(attach);
		mailinfo.setSubject(map.get("subject").toString());
		mailinfo.setContent(map.get("content").toString());//网页内容
		SendMail sm=new SendMail();
		//sm.sendTextMail(mailInfo)
		//sm.sendHtmlMail(mailInfo)
		//sm.sendAttach(mailInfo)
		if(sm.sendTextMail(mailinfo)){
			//System.out.println("邮件发送成功");
		return "邮件发送成功";
		}
		else{
			//System.out.println("邮件发送失败");
		return "邮件发送失败";
		}
		
	}
	
	public static String seneMail(List<Map<String, Object>> mailData){
		String result = null;
		boolean flag = true;
		for (Map<String, Object> map : mailData) {
			try {
				result = seneMail(map);
				if ("邮件发送失败".equals(result)) {
					flag = false;
				}
			} catch (Exception e) {
				logger.warn("邮件【发送到：" + map.get("toAddress")  + "，内容为：" + map.get("content") + "】发送失败！");
				flag = false;
				break;
			}
		}
		return flag ? "邮件发送成功" : "邮件发送失败";
	}
	public static void main(String[] args) {
		Map map=new HashMap();
		map.put("toAddress", "yangyun@tacleasing.cn");
		map.put("subject", "-----邮件测试123456----");
		map.put("content", "123456");
		TestMail.seneMail(map);
	}
}