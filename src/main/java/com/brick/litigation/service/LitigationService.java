package com.brick.litigation.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Transactional;

import com.brick.base.exception.DaoException;
import com.brick.base.exception.ServiceException;
import com.brick.base.service.BaseService;
import com.brick.base.service.BaseService.ORDER_TYPE;
import com.brick.base.to.PagingInfo;
import com.brick.litigation.dao.LitigationDao;
import com.brick.service.entity.Context;
import com.brick.util.StringUtils;

public class LitigationService extends BaseService {
	Log logger = LogFactory.getLog(LitigationService.class);

	private LitigationDao litigationDao;

	public LitigationDao getLitigationDao() {
		return litigationDao;
	}

	public void setLitigationDao(LitigationDao litigationDao) {
		this.litigationDao = litigationDao;
	}
	//增加诉讼进程
	@Transactional(rollbackFor=Exception.class)
	public void addLitigation(Map<String, Object> map) throws DaoException, ServiceException{
		//更新之前的状态STATUS为0
		litigationDao.updateLitigation(map);
		//1、标识当前状态
		map.put("STATUS", "1");
		//
		Integer rectId =(Integer)queryForObj("litigation.getRectIdByLCode", map);
		map.put("RECT_ID", rectId);
		map.put("REMARK", StringUtils.autoInsertWrap((String)map.get("REMARK"),40));
		litigationDao.addLitigation(map);
		//插入催收记录
		//催收内容=诉讼详情
		//数据字典
		map.put("dataType", "诉讼进程");
		List<Map> classList =(List<Map>) queryForList("litigation.getPrClassList",map );
		String content=null;
		for(Map mapClass :classList){
			if(mapClass.get("CODE").equals(map.get("PROCESS"))){
				content="诉讼进程："+mapClass.get("FLAG")+"，"+"诉讼日期："+(String)map.get("LITIGATION_DATE")+"，"+"备注："+StringUtils.autoInsertWrap((String)map.get("REMARK"),50);
			}
		}
		//查询公司code
		Map<String, Object> mapDun = new HashMap<String,Object>();
		String custCode=(String)queryForObj("litigation.getCustCode", map);
		mapDun.put("s_employeeId", map.get("CREATE_USER"));
		mapDun.put("CUST_CODE", custCode);
		mapDun.put("CALL_CONTENT",content);
		mapDun.put("RESULT", "15");
		mapDun.put("PHONE_NUMBER", "00");
		mapDun.put("ANSWERPHONE_NAME", "00");
		litigationDao.addDunRecord(mapDun);
		
	}
	//查看诉讼列表
	public PagingInfo<Object>  queryLitigationList(Context context)throws ServiceException{
		PagingInfo<Object> dw = queryForListWithPaging("litigation.queryLitigationList", context.contextMap, "CREATE_DATE", ORDER_TYPE.DESC);
		return dw;
	}
	//诉讼进程
	public List<Map>  queryLProcessList(Context context) throws ServiceException{
		
		List<Map> processList =(List<Map>) queryForList("litigation.queryLProcessList", context.contextMap);
		
		return processList;
	}
	
}
