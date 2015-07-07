package com.brick.record;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.base.command.BaseCommand;
import com.brick.base.to.PagingInfo;
import com.brick.base.util.LeaseUtil;
import com.brick.dataDictionary.service.DictionaryUtil;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.util.DateUtil;
import com.brick.util.StringUtils;
import com.tac.agent.service.AgentService;
import com.tac.agent.to.Agent;
import com.tac.user.service.UserService;

public class RentContractRecordCommand extends BaseCommand {
	
	Log logger=LogFactory.getLog(this.getClass());
	
	private RentContractRecordService recordService;
	
	private UserService userService;
	
	private AgentService agentService;

	public void queryRentContractRecord(Context context){
		Map<String, Object> outputMap = new HashMap<String, Object>();
		PagingInfo pagingInfo = recordService.getRentContractRecord(context.contextMap);
		outputMap.put("dw", pagingInfo);
		outputMap.put("content", context.contextMap.get("content"));
		outputMap.put("status", context.contextMap.get("status"));
		outputMap.put("startDate", context.contextMap.get("startDate"));
		outputMap.put("endDate", context.contextMap.get("endDate"));
		outputMap.put("room", context.contextMap.get("room"));
		outputMap.put("area", context.contextMap.get("area"));
		outputMap.put("chest", context.contextMap.get("chest"));
		outputMap.put("floor", context.contextMap.get("floor"));
		outputMap.put("production_type", context.contextMap.get("production_type"));
		outputMap.put("isOverdue", context.contextMap.get("isOverdue"));
		outputMap.put("recp_status", context.contextMap.get("recp_status"));
		
		Output.jspOutput(outputMap,context,"/record/rentContractRecord.jsp");
	}
	
	
	public void queryRentContractRecordForApply(Context context) throws Exception{
		Map<String, Object> outputMap = new HashMap<String, Object>();
		int userid = (Integer) context.request.getSession().getAttribute("s_employeeId");
		String bussManager = DictionaryUtil.getFlag("合同档案管理", "1");
		List<Agent> agents = agentService.getAgentByAgentUserId(userid, "合同档案借出");

		
		
		boolean isBussManager = false;
		if(String.valueOf(userid).equals(bussManager)){//业管处主管权限
			isBussManager = true;	
		}else{
			if(agents!=null && agents.size()>0){
				for(Agent agent:agents){
					if(String.valueOf(agent.getUserId()).equals(bussManager)){
						isBussManager = true;
						break;
					}
				}			
			}
		}
		outputMap.put("isBussManager",isBussManager);//业管部主管权限
		
		
		String dealers = DictionaryUtil.getFlag("合同档案管理", "2");//业管窗口权限
		String [] dealerArr = dealers.split(",");
		boolean isDealer = false;
		
		for(String dealer:dealerArr){
			if(String.valueOf(userid).equals(dealer)){
				isDealer = true;
				break;
			}else{
				if(agents!=null && agents.size()>0){
					for(Agent agent:agents){
						if(String.valueOf(agent.getUserId()).equals(dealer)){
							isDealer = true;
							break;
						}
					}			
				}
			}
		}

		outputMap.put("isDealer", isDealer);
		
		
		PagingInfo pagingInfo = recordService.getRentContractRecordApply(context.contextMap);
		List<Map> list = pagingInfo.getResultList();
		if(list!=null){
			for(Map m:list){//撤销申请、延期权限和单位主管权限
				if(m.get("APPLY_USER")!=null && userid == (Integer)m.get("APPLY_USER")){
					m.put("isApplyUser", true);
				}else{
					m.put("isApplyUser", false);
				}
				if(m.get("UPPER_USER")!=null && (userid == (Integer)m.get("UPPER_USER"))){
					m.put("isDeptLeader", true);
				}else{
					if(agents!=null && agents.size()>0){
						for(Agent agent:agents){
							if((int)agent.getUserId()== (Integer)m.get("UPPER_USER")){
								m.put("isDeptLeader", true);
								break;
							}else{
								m.put("isDeptLeader", false);
							}
						}			
					}else{
						m.put("isDeptLeader", false);
					}				
				}
			}
		}
		outputMap.put("dw", pagingInfo);
		outputMap.put("lease_code", context.contextMap.get("lease_code"));	
		outputMap.put("apply_status", context.contextMap.get("apply_status"));	
		Output.jspOutput(outputMap,context,"/record/rentContractRecordForApply.jsp");
	}
	
	
	public void returnRecordForApply(Context context){
		Map outputMap = new HashMap();
		boolean flag = false;
		String record_id = (String) context.contextMap.get("return_record_id");
		Map record = recordService.getRentContractRecordByRecordId(record_id);
		
		int status = (Integer)record.get("STATUS");
		if(status == 2){//借出			
			Integer userid = (Integer) context.request.getSession().getAttribute("s_employeeId");
			String area = (String) context.contextMap.get("return_to_area");
			String room = (String) context.contextMap.get("return_to_room");
			String chest = (String) context.contextMap.get("return_to_chest");
			String floor = (String) context.contextMap.get("return_to_floor");
			String return_comment = (String) context.contextMap.get("return_comment");
			int leftQty = recordService.getRecordFloorQtyById(floor);
			if(leftQty>0){
				recordService.returnRentContractRecord(userid.toString(), record_id, area,room,chest,floor, return_comment);
				flag = true;
			}else{
				outputMap.put("msg", "存放位置已满，请重新选择！");
			}
						
		}else{
			outputMap.put("msg", "已被他人归还，请刷新数据！");		
		}	
		outputMap.put("flag", flag);
		Output.jsonOutput(outputMap, context);
	}
	public void getRecordPositions(Context context){
		Map<String, Object> outputMap = new HashMap<String, Object>();
		PagingInfo pagingInfo = recordService.getRecordPositions(context.contextMap);
		outputMap.put("dw", pagingInfo);
		outputMap.put("area", context.contextMap.get("area"));
		outputMap.put("room", context.contextMap.get("room"));
		outputMap.put("type", context.contextMap.get("type"));
		outputMap.put("content", context.contextMap.get("content"));
		Output.jspOutput(outputMap,context,"/record/recordPositions.jsp");
	}
	
	public void getRentContractRecordLog(Context context){
		Map<String, Object> outputMap = new HashMap<String, Object>();
		PagingInfo pagingInfo = recordService.getRentContractRecordLog(context.contextMap);
		outputMap.put("dw", pagingInfo);
		outputMap.put("comment", context.contextMap.get("comment"));
		outputMap.put("startDate", context.contextMap.get("startDate"));
		outputMap.put("endDate", context.contextMap.get("endDate"));
		outputMap.put("room", context.contextMap.get("room"));
		outputMap.put("area", context.contextMap.get("area"));
		outputMap.put("createUser", context.contextMap.get("createUser"));
		outputMap.put("borrower", context.contextMap.get("borrower"));
		outputMap.put("reason", context.contextMap.get("reason"));
		outputMap.put("status", context.contextMap.get("status"));
		Output.jspOutput(outputMap,context,"/record/rentContractRecordLog.jsp");
	}
	
	
	public void recordCreate(Context context){
		Map outputMap = new HashMap();
		boolean flag = false;
		String rect_id = (String) context.contextMap.get("record_rect_id");
		Map record = recordService.getRentContractRecordByRectId(rect_id);
		if(record == null){
			String area = (String) context.contextMap.get("record_area");
			String room = (String) context.contextMap.get("record_room");
			String chest = (String) context.contextMap.get("record_chest");
			String floor = (String) context.contextMap.get("record_floor");
			String comment = (String) context.contextMap.get("record_comment");
			if(comment!=null){
				comment += "<br/>";
			}
			int leftQty = recordService.getRecordFloorQtyById(floor);
			if(leftQty>0){
				Integer userid = (Integer) context.request.getSession().getAttribute("s_employeeId");
				recordService.insertRentContractRecord(rect_id,userid.toString(),area,room,chest,floor,comment);
				flag = true;
			}else{	
				outputMap.put("msg", "存放位置已满，请重新选择！");
			}
		}else{
			outputMap.put("msg", "已被他人归档，请刷新数据！");
		}
		outputMap.put("flag", flag);
		Output.jsonOutput(outputMap, context);
	}
	
	public void borrowRecord(Context context){

		String rect_id = (String) context.contextMap.get("borrow_rect_id");
		Map record = recordService.getRentContractRecordByRectId(rect_id);
		int status = (Integer)record.get("STATUS");
		if(status == 1 || status == 3){//归档 和 借出后归还
			Integer userid = (Integer) context.request.getSession().getAttribute("s_employeeId");
			String borrower = (String) context.contextMap.get("borrow_borrower");
			String plan_return_date = (String) context.contextMap.get("borrow_plan_return_date");
			String reason = (String) context.contextMap.get("borrow_reason");
			String comment = (String) context.contextMap.get("borrow_comment");
			StringBuffer position = new StringBuffer("");
			position.append(record.get("AREA"));
			position.append(" ");
			position.append(record.get("ROOM"));
			position.append(" ");
			position.append(record.get("CHEST"));
			position.append(" ");
			position.append(record.get("FLOOR"));
			recordService.borrowRentContractRecord(userid.toString(), record.get("ID").toString(), borrower,  plan_return_date, 
					reason,String.valueOf(record.get("AREA_ID")),String.valueOf(record.get("ROOM_ID")),
					String.valueOf(record.get("CHEST_ID")),String.valueOf(record.get("FLOOR_ID")),position.toString(), comment);
			Output.jsonFlageOutput(true, context);
		}else{
			Output.jsonFlageOutput(false, context);
		}	

	}
	
	public void borrowRecordForApply(Context context) throws Exception{

		String rect_id = (String) context.contextMap.get("borrow_rect_id");
		Map record = recordService.getRentContractRecordByRectId(rect_id);
		int status = (Integer)record.get("STATUS");
		if(status == 1 || status == 3){//归档 和 借出后归还
			Integer userid = (Integer) context.request.getSession().getAttribute("s_employeeId");
			String plan_return_date = (String) context.contextMap.get("borrow_plan_return_date");
			String reason = (String) context.contextMap.get("borrow_reason");
			String comment = (String) context.contextMap.get("borrow_comment");
			int apply_status = 1;
			
			String bussManager = DictionaryUtil.getFlag("合同档案管理", "1");
			int dealUser = -1;
			if(String.valueOf(userid).equals(bussManager)){//业管部主管借出直接到待借出状态
				apply_status = 10;
			}else{
				if(userService.isDeptLeader(userid)){//如果是主管借出，直接到业管处主管状态
					apply_status = 5;
					dealUser = Integer.parseInt(DictionaryUtil.getFlag("合同档案管理", "1"));
				}else{
					dealUser = userService.getDeptLeaderByUserId(userid).getId();
				}
			}

			recordService.insertRentContractRecordApply(userid, plan_return_date, reason, comment, rect_id,apply_status,dealUser);
			Output.jsonFlageOutput(true, context);
		}else{
			Output.jsonFlageOutput(false, context);
		}	

	}
	
	public void cancelRecordApply(Context context){
		String apply_id = (String) context.contextMap.get("apply_id");
		recordService.cancelRecordApply(apply_id);
		Output.jsonFlageOutput(true, context);
	}
	
	public void returnRecord(Context context){
		Map outputMap = new HashMap();
		boolean flag = false;
		String record_id = (String) context.contextMap.get("return_record_id");
		Map record = recordService.getRentContractRecordByRecordId(record_id);
		
		int status = (Integer)record.get("STATUS");
		if(status == 2){//借出
			
			Integer userid = (Integer) context.request.getSession().getAttribute("s_employeeId");
			String area = (String) context.contextMap.get("return_to_area");
			String room = (String) context.contextMap.get("return_to_room");
			String chest = (String) context.contextMap.get("return_to_chest");
			String floor = (String) context.contextMap.get("return_to_floor");
			String return_comment = (String) context.contextMap.get("return_comment");
			int leftQty = recordService.getRecordFloorQtyById(floor);
			if(leftQty>0){
				recordService.returnRentContractRecord(userid.toString(), record_id, area,room,chest,floor, return_comment);
				flag = true;
			}else{
				outputMap.put("msg", "存放位置已满，请重新选择！");
			}
						
		}else{
			outputMap.put("msg", "已被他人归还，请刷新数据！");		
		}	
		outputMap.put("flag", flag);
		Output.jsonOutput(outputMap, context);
	}
	
	public void getUnReturnRecordDetail(Context context){
		String rect_id = (String) context.contextMap.get("rect_id");
		Map record = recordService.getRentContractRecordByRectId(rect_id);
		Map detail = recordService.getUnReturnRentContractRecordByRecordId(record.get("ID").toString());
		Output.jsonOutput(detail, context);
	}
	
	public void getRecordDeatil(Context context){
		Map<String, Object> outputMap = new HashMap<String, Object>();
		String rect_id = (String) context.contextMap.get("rect_id");
		Map record = recordService.getRentContractRecordByRectId(rect_id);
		if(record != null){
			List list = recordService.getRentContractRecordDetailByRecordId(record.get("ID").toString());
			outputMap.put("list", list);
		}
		Output.jspOutput(outputMap, context, "/record/rentContractRecordDetail.jsp");
	}
	
	public void transferRecord(Context context){
		Map outputMap = new HashMap();
		boolean flag = false;
		String rect_id = (String) context.contextMap.get("transfer_rect_id");
		Map record = recordService.getRentContractRecordByRectId(rect_id);
		int status = (Integer)record.get("STATUS");
		if(status == 1 || status == 3){//归档 和 借出后归还
			Integer userid = (Integer) context.request.getSession().getAttribute("s_employeeId");
			String from_area = String.valueOf(record.get("AREA_ID"));
			String from_room = String.valueOf(record.get("ROOM_ID"));
			String from_chest = String.valueOf(record.get("AREA_ID"));
			String from_floor = String.valueOf(record.get("ROOM_ID"));
			StringBuffer from_position = new StringBuffer("");
			from_position.append(record.get("AREA"));
			from_position.append(" ");
			from_position.append(record.get("ROOM"));
			from_position.append(" ");
			from_position.append(record.get("CHEST"));
			from_position.append(" ");
			from_position.append(record.get("FLOOR"));
			
			String to_area = (String) context.contextMap.get("transfer_to_area");
			String to_room = (String) context.contextMap.get("transfer_to_room");
			String to_chest = (String) context.contextMap.get("transfer_to_chest");
			String to_floor = (String) context.contextMap.get("transfer_to_floor");
			String comment = (String) context.contextMap.get("transfer_comment");
			int leftQty = recordService.getRecordFloorQtyById(to_floor);
			if(leftQty>0){
				recordService.transferRentContractRecord(record.get("ID").toString(),userid.toString(), 
						from_position.toString(),from_area, from_room,from_chest,from_floor,
						to_area, to_room,to_chest, to_floor, comment);
				flag = true;
			}else{
				outputMap.put("msg", "存放位置已满，请重新选择！");
			}			
		}else{
			outputMap.put("msg", "已被他人借出，无法转移！");		
		}
		outputMap.put("flag", flag);
		Output.jsonOutput(outputMap, context);
	}
	
	public void batchTransferRecords(Context context) throws NumberFormatException, SQLException{
		String [] ids = (String []) context.request.getParameterValues("batch_rect_id");
		String comment = (String) context.contextMap.get("form_comment");
		String to_area = (String) context.contextMap.get("form_to_area");
		String to_room = (String) context.contextMap.get("form_to_room");
		String to_chest = (String) context.contextMap.get("form_to_chest");
		String to_floor = (String) context.contextMap.get("form_to_floor");
		StringBuffer msg = new StringBuffer();
		List failList = new ArrayList();;
		int success = 0;
		int fail = 0;
		for(String id:ids){
			Map record = recordService.getRentContractRecordByRectId(id);
			int status = 0;
			if(record!=null){
				status = (Integer)record.get("STATUS");
			}			
			if(status == 1 || status == 3){//归档 和 借出后归还
				Integer userid = (Integer) context.request.getSession().getAttribute("s_employeeId");
				String from_area = String.valueOf(record.get("AREA_ID"));
				String from_room = String.valueOf(record.get("ROOM_ID"));
				String from_chest = String.valueOf(record.get("AREA_ID"));
				String from_floor = String.valueOf(record.get("ROOM_ID"));
				StringBuffer from_position = new StringBuffer("");
				from_position.append(record.get("AREA"));
				from_position.append(" ");
				from_position.append(record.get("ROOM"));
				from_position.append(" ");
				from_position.append(record.get("CHEST"));
				from_position.append(" ");
				from_position.append(record.get("FLOOR"));
				int leftQty = recordService.getRecordFloorQtyById(to_floor);
				int productype = LeaseUtil.getProductionTypeByRectId(Long.parseLong(id));
				Map floorInfo = recordService.getFloorById(to_floor);
				int type = (Integer) floorInfo.get("TYPE");
				if(leftQty>0 && type == productype){
					recordService.transferRentContractRecord(record.get("ID").toString(),userid.toString(), 
							from_position.toString(),from_area, from_room,from_chest,from_floor,
							to_area, to_room,to_chest, to_floor, comment);
					success++;
				}else{
					failList.add(LeaseUtil.getLeaseCodeByRectId(Long.parseLong(id)));
					fail ++;
				}
			}else{
				failList.add(LeaseUtil.getLeaseCodeByRectId(Long.parseLong(id)));
				fail ++;
			}
		}
		if(failList!=null && failList.size()>0){
			msg.append("成功");
			msg.append(success);
			msg.append("笔，失败");
			msg.append(fail);
			msg.append("笔，以下合同号的档案未转移成功：\n");
			for(int i=0,j=failList.size();i<j;i++){
				msg.append(failList.get(i) + "\n");		
			}
		}else{
			msg.append("操作成功！");
		}
		Map outputMap = new HashMap();
		outputMap.put("msg", msg.toString());
		Output.jsonOutput(outputMap, context);
		
	}
	
	public void batchCreateRecords(Context context) throws NumberFormatException, SQLException{
		String [] ids = (String []) context.request.getParameterValues("batch_rect_id");
		String comment = (String) context.contextMap.get("form_comment");
		if(comment!=null){
			comment += "<br/>";
		}
		String area = (String) context.contextMap.get("form_to_area");
		String room = (String) context.contextMap.get("form_to_room");
		String chest = (String) context.contextMap.get("form_to_chest");
		String floor = (String) context.contextMap.get("form_to_floor");
		
		StringBuffer msg = new StringBuffer();
		List failList = new ArrayList();;
		int success = 0;
		int fail = 0;
		for(String id:ids){
			Map record = recordService.getRentContractRecordByRectId(id);
			if(record == null){//未归档
				Integer userid = (Integer) context.request.getSession().getAttribute("s_employeeId");
				int leftQty = recordService.getRecordFloorQtyById(floor);
				int productype = LeaseUtil.getProductionTypeByRectId(Long.parseLong(id));
				Map floorInfo = recordService.getFloorById(floor);
				int type = (Integer) floorInfo.get("TYPE");
				if(leftQty>0 && type == productype){
					recordService.insertRentContractRecord(id,userid.toString(),area,room,chest,floor,comment);
					success++;
				}else{
					failList.add(LeaseUtil.getLeaseCodeByRectId(Long.parseLong(id)));
					fail ++;
				}
			}else{
				failList.add(LeaseUtil.getLeaseCodeByRectId(Long.parseLong(id)));
				fail ++;
			}
		}
		if(failList!=null && failList.size()>0){
			msg.append("成功");
			msg.append(success);
			msg.append("笔，失败");
			msg.append(fail);
			msg.append("笔，以下合同号的档案未归档成功：\n");
			for(int i=0,j=failList.size();i<j;i++){
				msg.append(failList.get(i) + "\n");		
			}
		}else{
			msg.append("操作成功！");
		}
		Map outputMap = new HashMap();
		outputMap.put("msg", msg.toString());
		Output.jsonOutput(outputMap, context);
	}
	
	public void getRecordByRectId(Context context){
		String rect_id = (String) context.contextMap.get("rect_id");
		Map record = recordService.getRentContractRecordByRectId(rect_id);
		List list = recordService.queryFilesByRectId(rect_id);
		record.put("list", list);
		Output.jsonOutput(record, context);
	}
	
	public void getRentFilesByRectId(Context context) throws NumberFormatException, SQLException{
		String rect_id = (String) context.contextMap.get("rect_id");
		List list = recordService.queryFilesByRectId(rect_id);

		int type = LeaseUtil.getProductionTypeByRectId(Long.parseLong(rect_id));
		Map position = recordService.getUnuserdFloor(type);
		Map outputMap = new HashMap();
		if(position!=null){
			outputMap.put("flag", true);
			outputMap.put("position", position);
		}else{
			outputMap.put("flag", false);
		}
		outputMap.put("list", list);
		Output.jsonOutput(outputMap, context);
	}
	
	public void modifyRecord(Context context){
		String record_id = (String) context.contextMap.get("modify_id");
		Map record = recordService.getRentContractRecordByRecordId(record_id);		
		String comment  = (String) record.get("COMMENT");
		
		String content = (String) context.contextMap.get("modify_comment");
		content += "   (" + context.request.getSession().getAttribute("s_employeeName") +" " +DateUtil.dateToFullStr(new Date())+")<br/>";

		if(comment!=null){
			comment += content;
		}else{
			comment = content;
		}
		
		recordService.updateRecordComment(record_id, comment);
		Output.jsonFlageOutput(true, context);
	}
	
	public void delayRecord(Context context){
		String record_id = (String) context.contextMap.get("delay_record_id");
		Map record = recordService.getRentContractRecordByRecordId(record_id);
		int status = (Integer)record.get("STATUS");
		if(status == 2){//借出
			String delay_days = (String) context.contextMap.get("delay_days");
			recordService.delayRecordDetail(record_id, delay_days);
			Output.jsonFlageOutput(true, context);
		}else{
			Output.jsonFlageOutput(false, context);
		}	
	}
	
	public void getRecordAreas(Context context){
		List areas = recordService.getRecordAreas();
		Output.jsonArrayOutput(areas, context);
	}
	
	public void getArea(Context context){
		String id = (String) context.contextMap.get("id");
		Map area = recordService.getAreaById(id);
		Output.jsonOutput(area, context);
	}
	
	public void saveArea(Context context){
		String id = (String) context.contextMap.get("area_id");
		String area_name = (String) context.contextMap.get("area_name");
		String comment = (String) context.contextMap.get("area_comment");
		Integer userid = (Integer) context.request.getSession().getAttribute("s_employeeId");
		boolean flag = false;
		if(!StringUtils.isEmpty(id)){//更新
			flag = recordService.updateRecordArea(id, area_name, comment, String.valueOf(userid));
		}else{//插入
			flag = recordService.insertRecordArea(area_name, comment, String.valueOf(userid));
		}
		Output.jsonFlageOutput(flag, context);
	}
	
	public void getRoom(Context context){
		String id = (String) context.contextMap.get("id");
		Map room = recordService.getRoomById(id);
		Output.jsonOutput(room, context);
	}
	
	public void saveRoom(Context context){
		String id = (String) context.contextMap.get("room_id");
		String area_id = (String) context.contextMap.get("room_area");
		String room_name = (String) context.contextMap.get("room_name");
		String comment = (String) context.contextMap.get("room_comment");
		Integer userid = (Integer) context.request.getSession().getAttribute("s_employeeId");
		boolean flag = false;
		if(!StringUtils.isEmpty(id)){//更新
			flag = recordService.updateRecordRoom(id, room_name, comment,area_id, String.valueOf(userid));
		}else{//插入
			flag = recordService.insertRecordRoom(room_name, comment, area_id, String.valueOf(userid));
		}
		Output.jsonFlageOutput(flag, context);
	}
	
	public void saveChest(Context context){
		String id = (String) context.contextMap.get("chest_id");
		String room_id = (String) context.contextMap.get("chest_room");
		String chest_name = (String) context.contextMap.get("chest_name");
		String comment = (String) context.contextMap.get("chest_comment");
		Integer userid = (Integer) context.request.getSession().getAttribute("s_employeeId");
		boolean flag = false;
		if(!StringUtils.isEmpty(id)){//更新
			flag = recordService.updateRecordChest(id, chest_name, comment,room_id, String.valueOf(userid));
		}else{//插入
			flag = recordService.insertRecordChest(chest_name, comment, room_id, String.valueOf(userid));
		}
		Output.jsonFlageOutput(flag, context);
	}
	
	public void getChest(Context context){
		String id = (String) context.contextMap.get("id");
		Map chest = recordService.getChestById(id);
		Output.jsonOutput(chest, context);
	}
	
	public void saveFloor(Context context){
		String id = (String) context.contextMap.get("floor_id");
		String chest_id = (String) context.contextMap.get("floor_chest");
		String floor_name = (String) context.contextMap.get("floor_name");
		String capacity = (String) context.contextMap.get("floor_capacity");
		String type = (String) context.contextMap.get("floor_type");
		String comment = (String) context.contextMap.get("floor_comment");
		Integer userid = (Integer) context.request.getSession().getAttribute("s_employeeId");
		boolean flag = false;
		if(!StringUtils.isEmpty(id)){//更新
			flag = recordService.updateRecordFloor(id, floor_name,capacity,type,comment, chest_id, String.valueOf(userid));
		}else{//插入
			flag = recordService.insertRecordFloor(floor_name,capacity,type, comment, chest_id, String.valueOf(userid));
		}
		Output.jsonFlageOutput(flag, context);
	}
	
	public void getQtyByFloorId(Context context){
		String id = (String) context.contextMap.get("floor_id");
		int qty = recordService.getRecordFloorQtyById(id);
		Map outputMap = new HashMap();
		outputMap.put("qty", qty);
		Output.jsonOutput(outputMap, context);
	}
	
	public void getFloor(Context context){
		String id = (String) context.contextMap.get("id");
		Map floor = recordService.getFloorById(id);
		Output.jsonOutput(floor, context);
	}
	
	public void getRecordRooms(Context context){
		String id = (String) context.contextMap.get("id");
		List rooms = recordService.getRecordRooms(id);
		Output.jsonArrayOutput(rooms, context);
	}
	
	public void getRecordChests(Context context){
		String id = (String) context.contextMap.get("id");
		List chests = recordService.getRecordChests(id);
		Output.jsonArrayOutput(chests, context);
	}
	
	public void getRecordFloors(Context context) throws NumberFormatException, SQLException{
		String id = (String) context.contextMap.get("id");
		String rect_id = (String) context.contextMap.get("rect_id");
		List floors = null;
		if(!StringUtils.isEmpty(rect_id)){
			int type = LeaseUtil.getProductionTypeByRectId(Long.parseLong(rect_id));
			floors = recordService.getRecordFloors(id,type);
		}else{
			floors = recordService.getRecordFloors(id,null);
		}
		Output.jsonArrayOutput(floors, context);
	}
	
	public void getUnuserdFloor(Context context) throws Exception{
		String id = (String) context.contextMap.get("rect_id");
		int type = LeaseUtil.getProductionTypeByRectId(Long.parseLong(id));
		Map position = recordService.getUnuserdFloor(type);
		Map outputMap = new HashMap();
		if(position!=null){
			outputMap.put("flag", true);
			outputMap.put("position", position);
		}else{
			outputMap.put("flag", false);
		}
		Output.jsonOutput(outputMap, context);
	}
	
	public void updateRecordApplyStatus(Context context) throws Exception{
		String id = (String) context.contextMap.get("id");
		String rect_id = (String) context.contextMap.get("rect_id");
		String apply_status = (String) context.contextMap.get("apply_status");
		Integer userid = (Integer) context.request.getSession().getAttribute("s_employeeId");
		recordService.updateRecordApplyStatus(id, apply_status, String.valueOf(userid),rect_id);
		Output.jsonFlageOutput(true, context);
	}
	
	public RentContractRecordService getRecordService() {
		return recordService;
	}

	public void setRecordService(RentContractRecordService recordService) {
		this.recordService = recordService;
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
