package com.brick.job.service;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Transactional;

import com.brick.base.exception.ServiceException;
import com.brick.base.service.BaseService;
import com.brick.credit.to.CreditTo;

public class CreditStatusUpdateJob extends BaseService {
	
	Log logger = LogFactory.getLog(CreditStatusUpdateJob.class);
	
	public void doService() throws Exception{
		Integer statusVersion = null;
		
		try {
			statusVersion = (Integer) queryForObj("job.getBusinessStatusVersion");
			if (statusVersion == null) {
				statusVersion = 0;
			}
			statusVersion += 1;
			//System.out.println("本次案件状态的版本号是：【" + statusVersion + "】");
			
			/*
			 * 案件状态列表
			 * -600----正常结清
			 * -500----提前结清
			 * -400----合同作废
			 * -300----撤销
			 * -200----婉拒
			 * -100----已核准未拨款已过期
			 * 100-----调查中
			 * 200-----业务主管审批中
			 * 300-----业务副总审批中
			 * 400-----初级评审
			 * 500-----一级评审
			 * 600-----二级评审
			 * 700-----三级评审
			 * 800-----四级评审
			 * 900-----已核准文件准备中
			 * 1000----已核准文审中
			 * 1100----拨款审批中
			 * 1200----付款审批中
			 * 1300----已拨款
			 * */
			int[] allStatus = new int[]{-600, -500, -400, -300, -200, -100, 100, 
										200, 300, 400, 500, 600, 700, 
										800, 900, 1000, 1100, 1200, 1300};
			
			for (int i : allStatus) {
				try {
					updateBusinessStatus(i, statusVersion);
				} catch (Exception e) {
					logger.warn(e);
					System.out.println(e.getMessage());
					continue;
				}
			}
		} catch (ServiceException e) {
			throw e;
		}
	}
	
	private void updateBusinessStatus(int status, int version) throws Exception{
		List<CreditTo> resultList = null;
		String statusStr = String.valueOf(status);
		resultList = (List<CreditTo>) queryForList("job.getStatusFor" + statusStr.replace("-", "_"));
		for (CreditTo creditTo : resultList) {
			try {
				if (checkUpdate(creditTo, status, version)) {
					creditTo.setBusinessStatus(status);
					creditTo.setBusinessStatusVersion(version);
					update("job.updateBusinessStatus", creditTo);
				}
			} catch (Exception e) {
				System.out.println(e.getMessage());
				continue;
			}
		}
	}
	
	private boolean checkUpdate(CreditTo creditTo, int toStatus, int version) throws Exception{
		/*if (creditTo.getBusinessStatus() == toStatus) {
			System.out.println("案件号为【" + creditTo.getCreditRuncode() + "】,状态没有变化");
			return false;
		}*/
		if (creditTo.getBusinessStatusVersion() == version) {
			//System.out.println("案件号为【" + creditTo.getCreditRuncode() + "】," +
			//		"状态由【" + creditTo.getBusinessStatus() + "】改变成【 " + toStatus+ " 】时冲突。");
			return false;
		}
		return true;
	}
}
