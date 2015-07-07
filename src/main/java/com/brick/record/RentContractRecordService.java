package com.brick.record;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import com.brick.base.exception.DaoException;
import com.brick.base.service.BaseService;
import com.brick.base.to.PagingInfo;
import com.brick.base.util.LeaseUtil;
import com.brick.common.mail.service.MailUtilService;
import com.brick.common.mail.to.MailSettingTo;
import com.brick.dataDictionary.service.DictionaryUtil;
import com.tac.agent.service.AgentService;
import com.tac.agent.to.Agent;
import com.tac.user.service.UserService;
import com.tac.user.to.UserTo;

public class RentContractRecordService extends BaseService {
	
	private RentContractRecordDAO recordDAO;
	
	private MailUtilService mailUtilService;
	
	private UserService userService;
	
	private AgentService agentService;
	
	
	public RentContractRecordDAO getRecordDAO() {
		return recordDAO;
	}


	public void setRecordDAO(RentContractRecordDAO recordDAO) {
		this.recordDAO = recordDAO;
	}


	public PagingInfo getRentContractRecord(Map params){
		return this.queryForListWithPaging("rentContractRecord.getAllRentContractRecord", params, "PAY_DATE",ORDER_TYPE.DESC);
	}
	
	public PagingInfo getRentContractRecordApply(Map params){
		return this.queryForListWithPaging("rentContractRecord.queryRecordApply", params, "APPLY_DATE",ORDER_TYPE.DESC);
	}
	
	public PagingInfo getRecordPositions(Map params){
		return this.queryForListWithPaging("rentContractRecord.getRecordPositions", params, "AREA_NAME",ORDER_TYPE.DESC);
	}
	
	
	public void insertRentContractRecord(String rect_id,String userId,String area,String room,String chest,String floor,String comment){		
		recordDAO.insertRentContractRecord(rect_id, userId,area,room,chest,floor,comment);	
	}
	@Transactional(rollbackFor=Exception.class)
	public void borrowRentContractRecord(String userId,String record_id, 
			String borrower,String plan_return_date,String reason,String from_area,String from_room,
			String from_chest,String from_floor,String position,String comment){
		recordDAO.updateRecord(record_id,2,null,null,null,null);//借出状态
		recordDAO.insertRentContractRecordDetail(record_id, userId, borrower, plan_return_date, reason,from_area,from_room,
				from_chest,from_floor,position, comment);
	}
	
	@Transactional(rollbackFor=Exception.class)
	public void insertRentContractRecordApply(int apply_user,
			String plan_return_date,String reason,String comment,String rect_id,int apply_status,int dealUser) throws Exception{
		recordDAO.insertRentContractRecordApply(apply_user, plan_return_date, reason, comment, rect_id,apply_status,dealUser);
		MailSettingTo mailSettingTo = new MailSettingTo();
		mailSettingTo.setEmailSubject("合同档案借出审批提醒");
		String leaseCode = LeaseUtil.getLeaseCodeByRectId(Long.parseLong(rect_id));
		
		String cc = LeaseUtil.getEmailByUserId(String.valueOf(apply_user));		
		mailSettingTo.setEmailCc(cc);
		String email = null;
		if(apply_status==1){//单位主管审核邮件提醒
			UserTo deptLeader = userService.getDeptLeaderByUserId(apply_user);
			email = deptLeader.getEmail();
			Agent agent = agentService.getAgent(deptLeader.getId(), "合同档案借出");
			if(agent!=null){
				email += ";" + LeaseUtil.getEmailByUserId(agent.getAgentUserId().toString());
			}
			
			mailSettingTo.setEmailContent("您好：合同号【"+leaseCode+"】的档案申请借出，请在租赁系统中【合同管理】---【档案借出管理】中进行审批。");			
		}else if(apply_status==5){//业管处主管审核邮件提醒
			String bussManager = DictionaryUtil.getFlag("合同档案管理", "1");	
			
			email = LeaseUtil.getEmailByUserId(bussManager);	
			Agent agent = agentService.getAgent(Integer.parseInt(bussManager), "合同档案借出");
			if(agent!=null){
				email += ";" + LeaseUtil.getEmailByUserId(agent.getAgentUserId().toString());
			}
			mailSettingTo.setEmailContent("您好：合同号【"+leaseCode+"】的档案申请单位主管审核已通过，请在租赁系统中【合同管理】---【档案借出管理】中进行审批。");
		}else if(apply_status==10){//档案借出管理人邮件提醒
			String dealers = DictionaryUtil.getFlag("合同档案管理", "2");//判断是否是业管窗口登陆
			String [] dealerArr = dealers.split(",");
			for(String dealer:dealerArr){
				if(email!=null){
					email += ";" + LeaseUtil.getEmailByUserId(dealer);	
					Agent agent = agentService.getAgent(Integer.parseInt(dealer), "合同档案借出");
					if(agent!=null){
						email += ";" + LeaseUtil.getEmailByUserId(agent.getAgentUserId().toString());
					}
				}else{
					email = LeaseUtil.getEmailByUserId(dealer);	
					Agent agent = agentService.getAgent(Integer.parseInt(dealer), "合同档案借出");
					if(agent!=null){
						email += ";" + LeaseUtil.getEmailByUserId(agent.getAgentUserId().toString());
					}
				}			
			}
			mailSettingTo.setEmailContent("您好：合同号【"+leaseCode+"】的档案申请借出已通过审核，请准备相关文件。");
		}
		mailSettingTo.setEmailTo(email);
		mailUtilService.sendMail(mailSettingTo);
	}
	
	@Transactional(rollbackFor=Exception.class)
	public void returnRentContractRecord(String userId,String record_id, 
			String to_area,String to_room,String to_chest,String to_floor,String return_comment){
		StringBuffer to_position = new StringBuffer("");
		to_position.append(recordDAO.getNameByType(RentContractRecordDAO.AREA, to_area));
		to_position.append(" ");
		to_position.append(recordDAO.getNameByType(RentContractRecordDAO.ROOM, to_room));
		to_position.append(" ");
		to_position.append(recordDAO.getNameByType(RentContractRecordDAO.CHEST, to_chest));
		to_position.append(" ");
		to_position.append(recordDAO.getNameByType(RentContractRecordDAO.FLOOR, to_floor));
		recordDAO.updateRecord(record_id, 3,to_area,to_room,to_chest,to_floor);//借出后归还状态
		Map detail = recordDAO.getUnReturnRentContractRecordByRecordId(record_id);
		recordDAO.updateRentContractRecordDetail(detail.get("ID").toString(), userId,to_area, 
				to_room,to_chest,to_floor,to_position.toString(),return_comment);
		//申请流程需注销
		Map record = recordDAO.getRentContractRecordByRecordId(record_id);
		String rect_id = String.valueOf(record.get("RECT_ID"));
		Integer applyId = recordDAO.getUnCompleteApplyIdByRectId(rect_id);
		if(applyId!=null){
			cancelRecordApply(String.valueOf(applyId));
		}
	}
	
	public void cancelRecordApply(String id){
		recordDAO.cancelRecordApply(id);
	}
	@Transactional(rollbackFor=Exception.class)
	public void transferRentContractRecord(String record_id,String userId,
			String from_position,String from_area,String from_room,String from_chest,String from_floor,
			String to_area,String to_room,String to_chest,String to_floor,String comment){
		
		StringBuffer to_position = new StringBuffer("");
		to_position.append(recordDAO.getNameByType(RentContractRecordDAO.AREA, to_area));
		to_position.append(" ");
		to_position.append(recordDAO.getNameByType(RentContractRecordDAO.ROOM, to_room));
		to_position.append(" ");
		to_position.append(recordDAO.getNameByType(RentContractRecordDAO.CHEST, to_chest));
		to_position.append(" ");
		to_position.append(recordDAO.getNameByType(RentContractRecordDAO.FLOOR, to_floor));
		recordDAO.updateRecordPosition(record_id, to_area, to_room,to_chest,to_floor);
		recordDAO.transferRentContractRecord(record_id, userId, from_position, from_area, from_room, from_chest, from_floor, to_position.toString(), to_area, to_room, to_chest, to_floor, comment);
	}
	
	public void sendEmailForOverdueRecord() throws Exception{

		List list = recordDAO.queryForList("rentContractRecord.getOverdueRecord");
		String cc = DictionaryUtil.getFlag("档案借出逾期提醒", "1");
		if(list!=null){
			MailSettingTo mailSettingTo = null;
			StringBuffer content = null;
			
			for(int i=0,len=list.size();i<len;i++){
				Map obj = (Map) list.get(i);
				String email = (String) obj.get("EMAIL");
				String leaseCode = (String) obj.get("LEASE_CODE");
				if(i==0 || !email.equals(mailSettingTo.getEmailTo())){
					if(i>0){
						mailSettingTo.setEmailContent(content.toString());
						mailUtilService.sendMail(mailSettingTo);
					}
					mailSettingTo = new MailSettingTo();
					mailSettingTo.setEmailSubject("合同档案借出逾期提醒");
					mailSettingTo.setEmailCc(cc);
					mailSettingTo.setEmailTo(email);
					content = new StringBuffer("您好：您借出的合同档案已超出计划归还日，请及时归还或者申请延期。以下为相关合同号：</br>");
				}
				content.append(leaseCode);
				content.append("</br>");
			}
			if(mailSettingTo!=null){
				mailSettingTo.setEmailContent(content.toString());
				mailUtilService.sendMail(mailSettingTo);
			}
		}

	}
	
	public List getRentContractRecordDetailByRecordId(String record_id){
		return recordDAO.getRentContractRecordDetailByRecordId(record_id);
	}
	public Map getUnReturnRentContractRecordByRecordId(String record_id){
		return recordDAO.getUnReturnRentContractRecordByRecordId(record_id);
	}
	
	public Map getRentContractRecordByRectId(String rect_id){
		return recordDAO.getRentContractRecordByRectId(rect_id);
	}
	
	public Map getRentContractRecordByRecordId(String record_id){
		return recordDAO.getRentContractRecordByRecordId(record_id);
	}
	
	public PagingInfo getRentContractRecordLog(Map params){
		return this.queryForListWithPaging("rentContractRecord.getRentContractRecordLog", params, "ID",ORDER_TYPE.DESC);
	}
	public List queryFilesByRectId(String rect_id){
		return recordDAO.queryFilesByRectId(rect_id);
	}
	
	public void updateRecordComment(String record_id,String comment){
		recordDAO.updateRecordComment(record_id, comment);
	}
	
	public void delayRecordDetail(String record_id,String delay_days){
		Map recordDetail = recordDAO.getUnReturnRentContractRecordByRecordId(record_id);
		recordDAO.delayRecordDetail(recordDetail.get("ID").toString(),delay_days);
	}
	
	public boolean insertRecordArea(String area_name,String comment,String userId){
		boolean  flag = false;
		int count = recordDAO.getAreaCountByName(area_name);
		if(count==0){
			recordDAO.insertRecordArea(area_name, comment, userId);
			flag = true;
		}		
		return flag;
	}
	
	public boolean updateRecordArea(String id,String area_name,String comment,String userId){
		boolean  flag = false;
		int count = recordDAO.getAreaCountByName(area_name);
		if(count == 0){			
			flag = true;
		}else{
			String name = recordDAO.getNameByType(RentContractRecordDAO.AREA, id);
			if(name.equals(area_name)){
				flag = true;
			}
		}
		if(flag){
			recordDAO.updateRecordArea(id, area_name, comment, userId);
		}
		return flag;
	}
	
	
	public boolean insertRecordRoom(String room_name,String comment,String area_id,String userId){
		boolean  flag = false;
		int count = recordDAO.getRoomCountByName(room_name,area_id);
		if(count==0){
			recordDAO.insertRecordRoom(room_name, comment, area_id, userId);
			flag = true;
		}		
		return flag;
	}
	
	public boolean updateRecordRoom(String id,String room_name,String comment,String area_id,String userId){
		boolean  flag = false;
		int count = recordDAO.getRoomCountByName(room_name,area_id);
		if(count == 0){			
			flag = true;
		}else{
			String name = recordDAO.getNameByType(RentContractRecordDAO.ROOM, id);
			if(name.equals(room_name)){
				flag = true;
			}
		}
		if(flag){
			recordDAO.updateRecordRoom(id, room_name, comment, userId);
		}
		return flag;
	}
	
	
	public boolean insertRecordChest(String chest_name,String comment,String room_id,String userId){
		boolean  flag = false;
		int count = recordDAO.getChestCountByName(chest_name, room_id);
		if(count==0){
			recordDAO.insertRecordChest(chest_name, comment, room_id, userId);
			flag = true;
		}		
		return flag;
	}
	
	
	public boolean updateRecordChest(String id,String chest_name,String comment,String room_id,String userId){
		boolean  flag = false;
		int count = recordDAO.getChestCountByName(chest_name, room_id);
		if(count == 0){			
			flag = true;
		}else{
			String name = recordDAO.getNameByType(RentContractRecordDAO.CHEST, id);
			if(name.equals(chest_name)){
				flag = true;
			}
		}
		if(flag){
			recordDAO.updateRecordChest(id, chest_name, comment, userId);
		}
		return flag;
	}
	
	public boolean insertRecordFloor(String floor_name,String capacity,String type,String comment,String chest_id,String userId){
		boolean  flag = false;
		int count = recordDAO.getFloorCountByName(floor_name, chest_id);
		if(count==0){
			recordDAO.insertRecordFloor(floor_name,capacity,type,comment, chest_id, userId);
			flag = true;
		}		
		return flag;
	}
	
	public boolean updateRecordFloor(String id,String floor_name,String capacity,String type,String comment,String chest_id,String userId){
		boolean  flag = false;
		int count = recordDAO.getFloorCountByName(floor_name, chest_id);
		if(count == 0){			
			flag = true;
		}else{
			String name = recordDAO.getNameByType(RentContractRecordDAO.FLOOR, id);
			if(name.equals(floor_name)){
				flag = true;
			}
		}
		if(flag){
			recordDAO.updateRecordFloor(id, floor_name,capacity,type,comment, userId);
		}
		return flag;
	}
	
	@Transactional(rollbackFor=Exception.class)
	public void updateRecordApplyStatus(String id,String apply_status,String userId,String rect_id) throws Exception{
		
		String leaseCode = LeaseUtil.getLeaseCodeByRectId(Long.parseLong(rect_id));
		MailSettingTo mailSettingTo = new MailSettingTo();
		mailSettingTo.setEmailSubject("合同档案借出审批提醒");
		Map apply = recordDAO.getRecordApplyById(id);
		Integer applyUser = (Integer) apply.get("APPLY_USER");
		String  cc = LeaseUtil.getEmailByUserId(String.valueOf(applyUser));
		mailSettingTo.setEmailCc(cc);
		
		String email = null;
		int deal_user = -1;
		if("5".equals(apply_status)){//单位主管审核通过
			String bussManager = DictionaryUtil.getFlag("合同档案管理", "1");	
			deal_user = Integer.parseInt(bussManager);
			email = LeaseUtil.getEmailByUserId(bussManager);
			Agent agent = agentService.getAgent(Integer.parseInt(bussManager), "合同档案借出");
			if(agent!=null){
				email += ";" + LeaseUtil.getEmailByUserId(agent.getAgentUserId().toString());
			}
			mailSettingTo.setEmailContent("您好：合同号【"+leaseCode+"】的档案申请单位主管审核已通过，请在租赁系统中【合同管理】---【档案借出管理】中进行审批。");
		}else if("10".equals(apply_status)){//业管处主管审核通过
			mailSettingTo.setEmailContent("您好：合同号【"+leaseCode+"】的档案申请借出已通过审核，请准备相关文件。");
			String dealers = DictionaryUtil.getFlag("合同档案管理", "2");
			String [] dealerArr = dealers.split(",");
			for(String dealer:dealerArr){
				if(email!=null){
					email += ";" + LeaseUtil.getEmailByUserId(dealer);	
					Agent agent = agentService.getAgent(Integer.parseInt(dealer), "合同档案借出");
					if(agent!=null){
						email += ";" + LeaseUtil.getEmailByUserId(agent.getAgentUserId().toString());
					}
				}else{
					email = LeaseUtil.getEmailByUserId(dealer);	
					Agent agent = agentService.getAgent(Integer.parseInt(dealer), "合同档案借出");
					if(agent!=null){
						email += ";" + LeaseUtil.getEmailByUserId(agent.getAgentUserId().toString());
					}
				}			
			}
			mailSettingTo.setEmailContent("您好：合同号【"+leaseCode+"】的档案申请借出已通过审核，请准备相关文件。");
		}
		recordDAO.updateRecordApplyStatus(id,apply_status,deal_user);

		mailSettingTo.setEmailTo(email);
		mailUtilService.sendMail(mailSettingTo);
		
		Map record = this.getRentContractRecordByRectId(rect_id);
		int status = (Integer)record.get("STATUS");

		if("15".equals(apply_status) && (status == 1 || status == 3)){//归档 和 借出后归还
			
			String reason =  (String) apply.get("REASON");
			String comment =  (String) apply.get("COMMENT");
			String borrower = String.valueOf(apply.get("APPLY_USER"));
			String plan_return_date = (String) apply.get("RETURN_DATE");
			
			StringBuffer position = new StringBuffer("");
			position.append(record.get("AREA"));
			position.append(" ");
			position.append(record.get("ROOM"));
			position.append(" ");
			position.append(record.get("CHEST"));
			position.append(" ");
			position.append(record.get("FLOOR"));
			this.borrowRentContractRecord(userId, record.get("ID").toString(), borrower, plan_return_date, reason, 
					String.valueOf(record.get("AREA_ID")),String.valueOf(record.get("ROOM_ID")),
					String.valueOf(record.get("CHEST_ID")),String.valueOf(record.get("FLOOR_ID")), position.toString(), comment);
		}

	}
	
	public Map getUnuserdFloor(int type){
		return recordDAO.getUnuserdFloor(type);	
	}
	
	public List getRecordAreas(){
		return recordDAO.getRecordAreas();
	}
	
	public List getRecordRooms(String area_id){
		return recordDAO.getRecordRooms(area_id);
	}
	
	public List getRecordChests(String room_id){
		return recordDAO.getRecordChests(room_id);
	}
	
	public List getRecordFloors(String chest_id,Integer type){
		return recordDAO.getRecordFloors(chest_id,type);
	}
	
	public Map getAreaById(String id){
		return recordDAO.getPostionById(RentContractRecordDAO.AREA, id);
	}
	
	public Map getRoomById(String id){
		return recordDAO.getPostionById(RentContractRecordDAO.ROOM, id);
	}
	
	public Map getChestById(String id){
		return recordDAO.getPostionById(RentContractRecordDAO.CHEST, id);
	}
	
	public Map getFloorById(String id){
		return recordDAO.getPostionById(RentContractRecordDAO.FLOOR, id);
	}
	
	public int getRecordFloorQtyById(String id){	
		return recordDAO.getRecordFloorQtyById(id);
	}


	public MailUtilService getMailUtilService() {
		return mailUtilService;
	}


	public void setMailUtilService(MailUtilService mailUtilService) {
		this.mailUtilService = mailUtilService;
	}


	public UserService getUserService() {
		return userService;
	}


	public void setUserService(UserService userService) {
		this.userService = userService;
	}


	public AgentService getAgentService() {
		return agentService;
	}


	public void setAgentService(AgentService agentService) {
		this.agentService = agentService;
	}
	
	
}
