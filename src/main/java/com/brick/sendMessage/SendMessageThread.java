package com.brick.sendMessage;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.brick.log.service.LogPrint;
import com.brick.service.core.DataAccessor;
import mobset.smsSDK;
import mobset.str_SendMsg;

public class SendMessageThread  {
	smsSDK sdk = new smsSDK();
	private static int iRet = -1;
	Log logger = LogFactory.getLog(SendMessageThread.class);
	
	//String strHost, int iCorpID, String strLoginName, String strPasswd, int iTimeOut
	private String strHost;
	private Integer iCorpID;
	private String strLoginName;
	private String strPasswd;
	private Integer iTimeOut;
	
	public String getStrHost() {
		return strHost;
	}

	public void setStrHost(String strHost) {
		this.strHost = strHost;
	}

	public Integer getiCorpID() {
		return iCorpID;
	}

	public void setiCorpID(Integer iCorpID) {
		this.iCorpID = iCorpID;
	}

	public String getStrLoginName() {
		return strLoginName;
	}

	public void setStrLoginName(String strLoginName) {
		this.strLoginName = strLoginName;
	}

	public String getStrPasswd() {
		return strPasswd;
	}

	public void setStrPasswd(String strPasswd) {
		this.strPasswd = strPasswd;
	}

	public Integer getiTimeOut() {
		return iTimeOut;
	}

	public void setiTimeOut(Integer iTimeOut) {
		this.iTimeOut = iTimeOut;
	}

	public void run() {
		connectSms();
		Map map=new HashMap();
		Map message=null;
		List list = new ArrayList();
		System.out.println("--------  发送短信 开始------------------");
		try {
			DataAccessor.getSession().startTransaction();
			DataAccessor.getSession().update("sendMessage.updateUpdateFlag", map);
			list = (List) DataAccessor.query("sendMessage.selectAllSendMessage",map , DataAccessor.RS_TYPE.LIST);
			for (int i = 0; i < list.size(); i++) {
				message = (Map) list.get(i);
				sendSms(String.valueOf(message.get("MTEL")),String.valueOf(message.get("MESSAGE")));
				DataAccessor.getSession().update("sendMessage.updateMessageSendFlag", message);
			}
			DataAccessor.getSession().commitTransaction();
			System.out.println("--------  发送短信成功 ------------------");
		} catch (Exception e1) {
			disConnectSms();
			e1.printStackTrace();
			System.out.println("--------  发送短信 失败------------------");
		}finally {
			try {
				DataAccessor.getSession().endTransaction();
			} catch (SQLException e) {
				System.out.println("--------  发送短信 结束------------------");
				logger.info("--------  发送短信 关闭事物错误");
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
			}
		}
	}

	private void sendSms(String mobile, String msg) {
		str_SendMsg[] sendMsg = new str_SendMsg[1];
		sendMsg[0] = new str_SendMsg();
		sendMsg[0].strMobile = mobile.trim(); // 目标手机号码，测试时请更改号码。
		sendMsg[0].strMsg = msg; // 短信内容
		int iRet = sdk.Sms_Send_LongSms(sendMsg, 1);
		if (iRet <= 0) {
			System.out.println("短信发送失败:" + mobile.trim() + "错误代码:" + iRet
					+ " :" + msg);
		} else {
			System.out.println("短信发送成功:" + mobile.trim() + " :" + msg);
		}
	}

	private void connectSms() {
		// 测试时请更改企业ID,用户名,密码
		//int iRet = -1;
		while (iRet != 0) {
			iRet = sdk.Sms_Connect(strHost, iCorpID, strLoginName, strPasswd, iTimeOut);
			//iRet = sdk.Sms_Connect("www.mobset.com", 116626, "yrzl", "tac.1234", 30);
			System.out.println(iRet);
			if (iRet == 0)// 登录成功
			{
				System.out.println("连接服务器成功...");
				return;
			} else {
				System.out.println("连接服务器失败，错误代码是:" + iRet);
			}

		}
	}

	private void disConnectSms() {
		sdk.Sms_DisConnect(); // 断开与服务器的连接
		sdk = null;
	}

	public static void main(String[] args) {

		// sendMessage.disConnectSms();
		// for (int i=0;i<list.size();i++){
		// System.out.println(list.get(i));
		// }
//		SendMessageThread sendMessage = new SendMessageThread();
//		sendMessage.connectSms();
//		sendMessage.sendSms("13862401058","系统测试");
		//sendMessage.sendSms("13656229115","Test");
//		sendMessage.disConnectSms();
	}
}

