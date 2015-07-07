package com.brick.record;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.brick.base.dao.BaseDAO;
import com.brick.base.exception.DaoException;

public class RentContractRecordDAO extends BaseDAO {
	
	static final int AREA  =1;
	static final int ROOM  =2;
	static final int CHEST  =3;
	static final int FLOOR  =4;

	public void insertRentContractRecord(String rect_id,String userId,String area,String room,String chest,String floor,String comment){
		Map record  = new HashMap();
		record.put("rect_id", rect_id);
		record.put("userId", userId);
		record.put("area", area);
		record.put("room", room);
		record.put("chest", chest);
		record.put("floor", floor);
		record.put("comment", comment);
		
		this.getSqlMapClientTemplate().insert("rentContractRecord.insertRentContractRecord",record);
	}
	
	public Map getRentContractRecordByRectId(String rect_id){
		Map param  = new HashMap();
		param.put("rect_id", rect_id);
		return (Map) this.getSqlMapClientTemplate().queryForObject("rentContractRecord.getRentContractRecordByRectId", param);		
	}
	
	public void updateRecord(String record_id,int status,String area,String room,String chest,String floor){
		Map param  = new HashMap();
		param.put("record_id", record_id);
		param.put("status", status);
		param.put("area", area);
		param.put("room", room);
		param.put("chest", chest);
		param.put("floor", floor);
		this.getSqlMapClientTemplate().update("rentContractRecord.updateRecord", param);
	}
	
	
	public void insertRentContractRecordDetail(String record_id,String userId,String borrower,
			String plan_return_date,String reason,String from_area,String from_room,
			String from_chest,String from_floor,String position,String comment){
		Map record  = new HashMap();
		record.put("record_id", record_id);
		record.put("userId", userId);
		record.put("borrower", borrower);
		record.put("plan_return_date", plan_return_date);
		record.put("reason", reason);
		record.put("from_area", from_area);
		record.put("from_room", from_room);
		record.put("from_chest", from_chest);
		record.put("from_floor", from_floor);
		record.put("position", position);
		record.put("comment", comment);
		this.getSqlMapClientTemplate().insert("rentContractRecord.insertRentContractRecordDetail",record);
	}
	
	public void updateRentContractRecordDetail(String id,String userId,String to_area,String to_room,
			String to_chest,String to_floor,String to_position,String return_comment){
		Map record  = new HashMap();
		record.put("id", id);
		record.put("userId", userId);
		record.put("to_area", to_area);
		record.put("to_room", to_room);
		record.put("to_chest", to_chest);
		record.put("to_floor", to_floor);
		record.put("to_position", to_position);
		record.put("return_comment", return_comment);
		this.getSqlMapClientTemplate().update("rentContractRecord.updateRentContractRecordDetail",record);

	}
	
	public Map getUnReturnRentContractRecordByRecordId(String record_id){
		Map param  = new HashMap();
		param.put("record_id", record_id);
		return (Map) this.getSqlMapClientTemplate().queryForObject("rentContractRecord.getUnReturnRentContractRecordByRecordId", param);
	}

	public Map getRentContractRecordByRecordId(String record_id){
		Map param  = new HashMap();
		param.put("record_id", record_id);
		return (Map) this.getSqlMapClientTemplate().queryForObject("rentContractRecord.getRentContractRecordByRecordId", param);		
	}
	
	public List getRentContractRecordDetailByRecordId(String record_id){
		Map param  = new HashMap();
		param.put("record_id", record_id);
		return this.getSqlMapClientTemplate().queryForList("rentContractRecord.getRentContractRecordDetail", param);
	}
	
	public void transferRentContractRecord(String record_id,String userId,
			String from_position,String from_area,String from_room,String from_chest,String from_floor,
			String to_position,String to_area,String to_room,String to_chest,String to_floor,
			String comment){
		Map param  = new HashMap();
		param.put("record_id", record_id);
		param.put("userId", userId);
		param.put("from_position", from_position);
		param.put("from_area", from_area);
		param.put("from_room", from_room);
		param.put("from_chest", from_chest);
		param.put("from_floor", from_floor);
		param.put("to_position", to_position);
		param.put("to_area", to_area);
		param.put("to_room", to_room);
		param.put("to_chest", to_chest);
		param.put("to_floor", to_floor);
		param.put("comment", comment);
		this.getSqlMapClientTemplate().update("rentContractRecord.transferRentContractRecord",param);
	}
	
	public void updateRecordPosition(String record_id,String area,String room,String chest,String floor){
		Map param  = new HashMap();
		param.put("record_id", record_id);
		param.put("area", area);
		param.put("room", room);
		param.put("chest", chest);
		param.put("floor", floor);
		this.getSqlMapClientTemplate().update("rentContractRecord.updateRecordPosition", param);
	}
	
	
	public List queryFilesByRectId(String rect_id){
		Map param  = new HashMap();
		param.put("rect_id", rect_id);
		return this.getSqlMapClientTemplate().queryForList("rentContractRecord.queryFilesByRectId", param);
	}
	
	
	public void updateRecordComment(String record_id,String comment){
		Map param  = new HashMap();
		param.put("record_id", record_id);
		param.put("comment", comment);
		this.getSqlMapClientTemplate().update("rentContractRecord.updateRecordComment",param);
	}
	
	public void delayRecordDetail(String id,String delay_days){
		Map param  = new HashMap();
		param.put("id", id);
		param.put("delay_days", delay_days);
		this.getSqlMapClientTemplate().update("rentContractRecord.delayRecordDetail",param);
	}
	
	
	public List getRecordAreas(){
		return this.getSqlMapClientTemplate().queryForList("rentContractRecord.getRecordAreas");
	}
	
	public List getRecordRooms(String area_id){
		Map param  = new HashMap();
		param.put("area_id", area_id);
		return this.getSqlMapClientTemplate().queryForList("rentContractRecord.getRecordRooms",param);
	}
	
	public List getRecordChests(String room_id){
		Map param  = new HashMap();
		param.put("room_id", room_id);
		return this.getSqlMapClientTemplate().queryForList("rentContractRecord.getRecordChests",param);
	}
	
	public List getRecordFloors(String chest_id,Integer type){
		Map param  = new HashMap();
		param.put("chest_id", chest_id);
		param.put("type", type);
		return this.getSqlMapClientTemplate().queryForList("rentContractRecord.getRecordFloors",param);
	}
	
	public String getNameByType(int type,String id){
		Map param  = new HashMap();
		param.put("id", id);
		String name ="";
		switch(type){
			case 1:
				name = (String) this.getSqlMapClientTemplate().queryForObject("rentContractRecord.getRecordAreaNameById",param);
				break;  
			case 2:
				name = (String) this.getSqlMapClientTemplate().queryForObject("rentContractRecord.getRecordRoomNameById",param);
				break;  
			case 3:
				name = (String) this.getSqlMapClientTemplate().queryForObject("rentContractRecord.getRecordChestNameById",param);
				break;  
			case 4:
				name = (String) this.getSqlMapClientTemplate().queryForObject("rentContractRecord.getRecordFloorNameById",param);
				break;  
		}
		return name;
	}
	
	public Map getPostionById(int type,String id){		
		Map param  = new HashMap();
		param.put("id", id);
		Map object = null;
		switch(type){
			case 1:
				object = (Map) this.getSqlMapClientTemplate().queryForObject("rentContractRecord.getRecordAreaById",param);
				break;  
			case 2:
				object = (Map) this.getSqlMapClientTemplate().queryForObject("rentContractRecord.getRecordRoomById",param);
				break;  
			case 3:
				object = (Map) this.getSqlMapClientTemplate().queryForObject("rentContractRecord.getRecordChestById",param);
				break;  
			case 4:
				object = (Map) this.getSqlMapClientTemplate().queryForObject("rentContractRecord.getRecordFloorById",param);
				break;  
		}
		return object;
	}
	
	public void insertRecordArea(String area_name,String comment,String userId){
		Map area = new HashMap();
		area.put("area_name", area_name);
		area.put("comment", comment);
		area.put("userId", userId);
		this.getSqlMapClientTemplate().insert("rentContractRecord.insertRecordArea", area);
	}
	
	public void updateRecordArea(String id,String area_name,String comment,String userId){
		Map area = new HashMap();
		area.put("id", id);
		area.put("area_name", area_name);
		area.put("comment", comment);
		area.put("userId", userId);
		this.getSqlMapClientTemplate().update("rentContractRecord.updateRecordArea", area);
	}
	
	
	public int getAreaCountByName(String area_name){
		Map param = new HashMap();
		param.put("area_name", area_name);
		return (Integer) this.getSqlMapClientTemplate().queryForObject("rentContractRecord.getAreaCountByName",param);
	}
	
	
	public void insertRecordRoom(String room_name,String comment,String area_id,String userId){
		Map room = new HashMap();
		room.put("room_name", room_name);
		room.put("comment", comment);
		room.put("userId", userId);
		room.put("area_id", area_id);
		this.getSqlMapClientTemplate().insert("rentContractRecord.insertRecordRoom", room);
	}
	
	
	public void updateRecordRoom(String id,String room_name,String comment,String userId){
		Map room = new HashMap();
		room.put("id", id);
		room.put("room_name", room_name);
		room.put("comment", comment);
		room.put("userId", userId);
		this.getSqlMapClientTemplate().update("rentContractRecord.updateRecordRoom", room);
	}
	
	
	public int getRoomCountByName(String room_name,String area_id){
		Map params = new HashMap();
		params.put("room_name", room_name);
		params.put("area_id", area_id);
		return (Integer) this.getSqlMapClientTemplate().queryForObject("rentContractRecord.getRoomCountByName",params);

	}
	
	public int getChestCountByName(String chest_name,String room_id){
		Map params = new HashMap();
		params.put("chest_name", chest_name);
		params.put("room_id", room_id);
		return (Integer) this.getSqlMapClientTemplate().queryForObject("rentContractRecord.getChestCountByName",params);

	}
	
	public void insertRecordChest(String chest_name,String comment,String room_id,String userId){
		Map chest = new HashMap();
		chest.put("chest_name", chest_name);
		chest.put("comment", comment);
		chest.put("userId", userId);
		chest.put("room_id", room_id);
		this.getSqlMapClientTemplate().insert("rentContractRecord.insertRecordChest", chest);
	}
	
	public void updateRecordChest(String id,String chest_name,String comment,String userId){
		Map chest = new HashMap();
		chest.put("id", id);
		chest.put("chest_name", chest_name);
		chest.put("comment", comment);
		chest.put("userId", userId);
		this.getSqlMapClientTemplate().update("rentContractRecord.updateRecordChest", chest);
	}

	
	public int getFloorCountByName(String floor_name,String chest_id){
		Map params = new HashMap();
		params.put("floor_name", floor_name);
		params.put("chest_id", chest_id);
		return (Integer) this.getSqlMapClientTemplate().queryForObject("rentContractRecord.getFloorCountByName",params);

	}
	
	public void insertRecordFloor(String floor_name,String capacity,String type,String comment,String chest_id,String userId){
		Map floor = new HashMap();
		floor.put("floor_name", floor_name);
		floor.put("capacity", capacity);
		floor.put("type", type);
		floor.put("comment", comment);
		floor.put("userId", userId);
		floor.put("chest_id", chest_id);
		this.getSqlMapClientTemplate().insert("rentContractRecord.insertRecordFloor", floor);
	}
	
	public void updateRecordFloor(String id,String floor_name,String capacity,String type,String comment,String userId){
		Map floor = new HashMap();
		floor.put("id", id);
		floor.put("floor_name", floor_name);
		floor.put("capacity", capacity);
		floor.put("type", type);
		floor.put("comment", comment);
		floor.put("userId", userId);
		this.getSqlMapClientTemplate().update("rentContractRecord.updateRecordFloor", floor);
	}
	
	public int getRecordFloorQtyById(String id){
		Map param = new HashMap();
		param.put("id", id);
		return (Integer) this.getSqlMapClientTemplate().queryForObject("rentContractRecord.getRecordFloorQtyById",param);
	}
	
	public Map getUnuserdFloor(int type){
		Map param = new HashMap();
		param.put("type", type);
		return (Map) this.getSqlMapClientTemplate().queryForObject("rentContractRecord.getUnuserdFloor",param);
	}
	
	
	public void insertRentContractRecordApply(int apply_user,
			String plan_return_date,String reason,String comment,String rect_id,int apply_status,int deal_user){
		Map record  = new HashMap();
		record.put("apply_user", apply_user);
		record.put("plan_return_date", plan_return_date);
		record.put("reason", reason);
		record.put("comment", comment);
		record.put("rect_id", rect_id);
		record.put("apply_status", apply_status);
		record.put("deal_user", deal_user);
		this.getSqlMapClientTemplate().insert("rentContractRecord.insertRecordApply",record);
	}
	
	public void updateRecordApplyStatus(String id,String apply_status,int deal_user){
		Map params  = new HashMap();
		params.put("id", id);
		params.put("apply_status", apply_status);
		params.put("deal_user", deal_user);
		this.getSqlMapClientTemplate().insert("rentContractRecord.updateRecordApplyStatus",params);
	}
	
	public Map getRecordApplyById(String id){
		Map params  = new HashMap();
		params.put("id", id);
		return (Map) this.getSqlMapClientTemplate().queryForObject("rentContractRecord.getRecordApplyById",params);
	}
	
	public Integer getUnCompleteApplyIdByRectId(String rect_id){
		Map params  = new HashMap();
		params.put("rect_id", rect_id);
		return (Integer) this.getSqlMapClientTemplate().queryForObject("rentContractRecord.getUnCompleteApplyByRectId", params);
	}
	
	public void cancelRecordApply(String id){
		Map params  = new HashMap();
		params.put("id", id);
		this.getSqlMapClientTemplate().update("rentContractRecord.cancelRecordApply", params);
	}
}
