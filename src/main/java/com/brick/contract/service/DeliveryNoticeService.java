package com.brick.contract.service;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.brick.baseManage.service.BusinessLog;
import com.brick.coderule.service.CodeRule;
import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.core.DataAccessor.RS_TYPE;
import com.brick.service.entity.Context;
import com.brick.util.DataUtil;
import com.brick.util.web.HTMLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.brick.log.service.LogPrint;

/**
 * 发货通知单
 * 
 * @author cheng
 * @date Jun 22, 2010
 * @version 1.0
 */
public class DeliveryNoticeService extends AService {
	Log logger = LogFactory.getLog(DeliveryNoticeService.class);

    public static final Logger log = Logger.getLogger(DeliveryNoticeService.class);

    // href="../servlet/defaultDispatcher?__action=deliveryNoticeService.selectEquipment&RECT_ID=62"></a>
    /**  RECT_ID 参数大写
     * 准备数据 新建发货通知单前选择设备 从表中（ t_pucs_contractdetail）得到还没有选择地设备
     */
    @SuppressWarnings("unchecked")
    public void selectEquipment(Context context) {
	Map outputMap = new HashMap();
	List errList = context.errList;
	List equipList = null;
	Map rentContract = null;
	if (errList.isEmpty()) {
	    try {
		equipList = (List) DataAccessor.query(
			"deliveryNotice.getEquipByRectId", context.contextMap,DataAccessor.RS_TYPE.LIST);
		
		rentContract = (Map) DataAccessor.query(
			"deliveryNotice.getRentContractByRectId",context.contextMap, DataAccessor.RS_TYPE.MAP);
		
	    } catch (Exception e) {
		log.error("com.brick.contract.service.DeliveryNoticeService.selectEquipment"+ e.getMessage());
		e.printStackTrace();
		errList.add("com.brick.contract.service.DeliveryNoticeService.selectEquipment"+ e.getMessage());
		LogPrint.getLogStackTrace(e, logger);
		errList.add(e);
	    }
	}

	if (errList.isEmpty()) {
	    outputMap.put("equipList", equipList);
	    outputMap.put("rentContract", rentContract);
	    Output.jspOutput(outputMap, context,"/deliveryNotice/deliveryNoticeEquipment.jsp");
	}

    }

    /**
     * 进入创建发货通知单页面 Pre  参数大写
     * 准备承租人信息等资料，填写  
     * @param context    RECEIVING_UNIT  getPucsContractbyId
     */
    @SuppressWarnings("unchecked")
    public void getDeliveryNoticeCreatePre(Context context) {
	Map outputMap = new HashMap();
	List errList = context.errList;
	 Map  preMap = new HashMap();
	Map deliveryMap =null;
	String code = HTMLUtil.getStrParam(context.request,"rentContract.LEASE_CODE", "");
	String[] eqmt_ids = HTMLUtil.getParameterValues(context.request,"eqmt_id", ""); //eqmt_ids[0]="41,YU-13663,6" 以“,” 分割字符串，得到 [0]empt_id， [2]puct_id  
	List equipList = new ArrayList();
	String PUCT_ID  = (eqmt_ids[0].split(","))[2]; 
	preMap.put("PUCT_ID", PUCT_ID);
	//发货单号
	String deliveryCode = null;
	Long rectId = 0l;
	
	if (errList.isEmpty()) {
	    try {
		 context.contextMap.put("eqmt_id", (eqmt_ids[0].split(","))[0]);         //如上
		 deliveryMap = (Map) DataAccessor.query("deliveryNotice.getPucsContractbyId", preMap, RS_TYPE.MAP);
		 for(int i = 0; i<eqmt_ids.length; i++) {
			context.contextMap.put("eqmt_id", (eqmt_ids[i].split(","))[0]); //如上
			
			Map equipMap = (Map)DataAccessor.query(
				"deliveryNotice.getEquipInfoByEqmtId", context.contextMap, DataAccessor.RS_TYPE.MAP);
			
			float unit_price = DataUtil.floatUtil(equipMap.get("UNIT_PRICE"));
			int amount = DataUtil.intUtil(equipMap.get("AMOUNT"));
			
			equipMap.put("PUCT_CODE", (eqmt_ids[i]).split(",")[1] );
			
			equipMap.put("TOTAL", unit_price * amount);
			equipList.add(equipMap);
			
			rectId =DataUtil.longUtil(context.contextMap.get("RECT_ID"));
			deliveryCode = CodeRule.generateDeliveryNoticeCode(rectId);
		}
		
	    } catch (Exception e) {
		log.error(
			"com.brick.contract.service.DeliveryNoticeService.getDeliveryNoticeCreate"+ e.getMessage());
		e.printStackTrace();
		
		errList.add(
			"com.brick.contract.service.DeliveryNoticeService.getDeliveryNoticeCreate"+ e.getMessage());
		LogPrint.getLogStackTrace(e, logger);
		errList.add(e);
	    }
	}

	if (errList.isEmpty()) {
		//发货单号
		outputMap.put("deliveryCode", deliveryCode); 
	    outputMap.put("CODE", code); // 页面上的合同号
	    outputMap.put("equipList", equipList);
	    outputMap.put("deliveryMap", deliveryMap);
	    outputMap.put("RECT_ID", context.contextMap.get("RECT_ID")); // 页面上的合同号的ID
									    
	    Output.jspOutput(outputMap, context,"/deliveryNotice/deliveryNoticeCreate.jsp");
	}
    }

    /**
     * 创建发货通知单 提交数据保存到 T-DELV-DELIBERYLOG 和T-DELV-DELIVERYLOG2EQIUP 表
     * 需要修改6.24
     */
    @SuppressWarnings("unchecked")
    public void getDeliveryNoticeCreate(Context context) {

	Map contextMap = context.contextMap;
	List errList = context.errList;

	String[] eqmt_ids = HTMLUtil.getParameterValues(context.request,"EQMT_ID", "0"); // 控制插入中间表循环次数 设备id id数即为循环数
	String[] amounts = HTMLUtil.getParameterValues(context.request,"AMOUNT", "1");

	Long delv_id = null;
	if (errList.isEmpty()) {

	    try {
		
		DataAccessor.getSession().startTransaction();
		
		delv_id = (Long) DataAccessor.getSession().insert(
			"deliveryNotice.createDeliveryNotice", contextMap);

		for (int i = 0; i < eqmt_ids.length; i++) { // 循环开始，，同时，将DELV_ID,对应
							    // EQMT_ID插入到中间表LOG2EQUIP中
		    contextMap.put("EQMT_ID", eqmt_ids[i]); // EQMT_ID，设备，页面中显示，选中的
							    // AMOUNT 发货数量
		    contextMap.put("DELV_ID", delv_id);
		    contextMap.put("AMOUNT", amounts[i]);

		    DataAccessor.getSession().insert("deliveryNotice.creatLog2Equip", contextMap);
		}
		
		
		DataAccessor.getSession().executeBatch();
		DataAccessor.getSession().commitTransaction();
		
	    }catch (Exception e) {
		log.error(
			"com.brick.contract.service.DeliveryNoticeService.getDeliveryNoticeCreate"+ e.getMessage());
		e.printStackTrace();
		errList.add(
			"com.brick.contract.service.DeliveryNoticeService.getDeliveryNoticeCreate"+ e.getMessage());
		LogPrint.getLogStackTrace(e, logger);
		errList.add(e);
	    } finally {
		try {
		    DataAccessor.getSession().endTransaction();
		} catch (SQLException e) {
		    e.printStackTrace();
		    LogPrint.getLogStackTrace(e, logger);
			errList.add(e);
		}
	    }
	}
	
	if (errList.isEmpty()) {
	    Output.jspSendRedirect(context,"defaultDispatcher?__action=rentContract.queryRentContract");
	} else {
	    
	}
    }

    /**2010.6.25  DELV_ID   
     * 查看发货通知单 查看根据 发货通知单 ID  DELV_ID  
     * queryNoticById
     * equipList
     * getEqmtById(DELV_ID)
     *   /servlet/defaultDispatcher?__action=deliveryNoticeService.deliveryNoticeShow&DELV_ID=141
     */
    @SuppressWarnings("unchecked")
    public void deliveryNoticeShow(Context context) {
	Map contextMap = context.contextMap;
	List errList = context.errList;
	List equipList = new ArrayList<Map>();      
	List eqL       = null;    // 传递数据用
	Map  equipMap = null;
	Map outputMap = new HashMap();
	Map noticeMap = null;
	if (errList.isEmpty()) {
	    try {
		noticeMap = (Map) DataAccessor.query("deliveryNotice.queryNoticById", contextMap, RS_TYPE.MAP);
		
		eqL = (List) DataAccessor.query("deliveryNotice.getEqmtById", contextMap, RS_TYPE.LIST);
		 
	for(int i=0;i<eqL.size();i++ ){
	    equipMap =  (Map) eqL.get(i);

		float unit_price = DataUtil.floatUtil(equipMap.get("UNIT_PRICE"));
		int amount = DataUtil.intUtil(equipMap.get("AMOUNT"));
		equipMap.put("TOTAL", unit_price * amount);
		equipList.add(equipMap);
	}
		
	     } catch (Exception e) {
		log.error(
			"com.brick.contract.service.DeliveryNoticeService.getDeliveryNoticeShow"+ e.getMessage());
		e.printStackTrace();
		errList.add(
			"com.brick.contract.service.DeliveryNoticeService.getDeliveryNoticeShow"+ e.getMessage());
		LogPrint.getLogStackTrace(e, logger);
		errList.add(e);
	     } finally {
		try {
		    DataAccessor.getSession().endTransaction();
		} catch (SQLException e) {
		    e.printStackTrace();
		    LogPrint.getLogStackTrace(e, logger);
			errList.add(e);
		}
	    }
	}
	if (errList.isEmpty()) {
	    outputMap.put("equipList", equipList);
	    outputMap.put("noticeMap", noticeMap);
	    Output.jspOutput(outputMap, context, "/deliveryNotice/deliveryNoticeDetail.jsp");
	}
	
    }
    
    /**
     * 进入发货单修改页面 kk
     */
    @SuppressWarnings("unchecked")
    public void deliveryNoticeUpdateJsp(Context context) {
    	Map outputMap = new HashMap();
    	List errList = context.errList;
    	Map noticeMap = null;
    	List eqL = null;
    	Map equipMap = null;
    	List equipList = new ArrayList<Map>(); 
		if(errList.isEmpty()) {
			try {	
				noticeMap = (Map) DataAccessor.query("deliveryNotice.queryNoticById", context.contextMap, RS_TYPE.MAP);
				eqL = (List) DataAccessor.query("deliveryNotice.getEqmtById", context.contextMap, RS_TYPE.LIST);
				for(int i=0;i<eqL.size();i++ ){
				    equipMap =  (Map) eqL.get(i);
					float unit_price = DataUtil.floatUtil(equipMap.get("UNIT_PRICE"));
					int amount = DataUtil.intUtil(equipMap.get("AMOUNT"));
					equipMap.put("TOTAL", unit_price * amount);
					equipList.add(equipMap);
				}
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}		
		if(errList.isEmpty()) {
		    outputMap.put("equipList", equipList);
		    outputMap.put("noticeMap", noticeMap);
			Output.jspOutput(outputMap, context,"/deliveryNotice/deliveryNoticeUpdate.jsp");
		}
	
    }
    /**
     * 修改发货单 kk
     */
    @SuppressWarnings("unchecked")
    public void updateDeliveryNotice(Context context) {
    	List errList = context.errList;

    	String[] eqmt_ids = HTMLUtil.getParameterValues(context.request,"EQMT_ID", "0"); 
    	String[] amounts = HTMLUtil.getParameterValues(context.request,"AMOUNT", "1");

    	Long delv_id = 0l;
    	if (errList.isEmpty()) {
    	    try {    		
    	    	DataAccessor.getSession().startTransaction();
    	    	DataAccessor.getSession().update("deliveryNotice.updateDeliveryLog", context.contextMap);
    	    	delv_id = DataUtil.longUtil(context.contextMap.get("DELV_ID"));
    		for (int i = 0; i < eqmt_ids.length; i++) { 
    			context.contextMap.put("EQMT_ID", eqmt_ids[i]);
    			context.contextMap.put("DELV_ID", delv_id);
    			context.contextMap.put("AMOUNT", amounts[i]);
    		    DataAccessor.getSession().update("deliveryNotice.updateLog2Equip", context.contextMap);
    		}
    		DataAccessor.getSession().commitTransaction();
    		
    	    }catch (Exception e) {
    	    	e.printStackTrace();
    	    	LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
    	    } finally {
    	    	try {
    	    		DataAccessor.getSession().endTransaction();
    	    	} catch (SQLException e) {
    	    		e.printStackTrace();
    	    		LogPrint.getLogStackTrace(e, logger);
    				errList.add(e);
    			}
    	    }
    	}
		if(errList.isEmpty()) {	
			//Output.jspOutput(outputMap, context,"/payment/paymentList.jsp");
			Output.jspSendRedirect(context,"../servlet/defaultDispatcher?__action=deliveryNoticeService.deliveryNoticeShow&DELV_ID="+delv_id);
		}
    	
    }

    /*
     * @see com.brick.service.core.AService#afterExecute(java.lang.String,
     *      com.brick.service.entity.Context)
     */
    @Override
    protected void afterExecute(String action, Context context) {
		if ("deliveryNoticeService.getDeliveryNoticeCreate".equals(action)) {
			
			Long creditId = null;
			Long contractId = DataUtil.longUtil(context.contextMap.get("RECT_ID"));
			String logType = "融资租赁合同发货单";
			String logTitle = "生成";
			String logCode = String.valueOf(context.contextMap.get("DELV_CODE"));
			String memo = "融资租赁合同生成发货单";
			int state = 1;
			Long userId = DataUtil.longUtil(context.contextMap.get("s_employeeId"));
			Long otherId = null;
			
			BusinessLog.addBusinessLog(creditId, contractId, logType, logTitle, logCode, memo, state, userId, otherId, (String)context.contextMap.get("IP"));
		}
    }

    /*
     * @see com.brick.service.core.AService#preExecute(java.lang.String,
     *      com.brick.service.entity.Context)
     */
    @Override
    protected boolean preExecute(String action, Context context) {
	// TODO Auto-generated method stub
	return super.preExecute(action, context);
    }

}
