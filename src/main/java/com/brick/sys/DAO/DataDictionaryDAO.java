package com.brick.sys.DAO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.brick.base.dao.BaseDAO;
import com.brick.sys.TO.DataDictionaryTO;

public class DataDictionaryDAO extends BaseDAO {

	public List<DataDictionaryTO> getDBTableDetail(Map<String,String> param) throws Exception {

		List<DataDictionaryTO> resultList=new ArrayList<DataDictionaryTO>();
		List<DataDictionaryTO> resultList1=null;
		List<DataDictionaryTO> resultList2=null;

		resultList1=this.getSqlMapClientTemplate().queryForList("common.getDBTableDetail1",param);
		resultList2=this.getSqlMapClientTemplate().queryForList("common.getDBTableDetail2",param);

		//同步栏位,如果表有新增栏位,需要体现在View Detail里
		if(resultList2.size()!=0) {
			for(int i=0;i<resultList1.size();i++) {
				boolean newColumnFlag=false;
				for(int j=0;j<resultList2.size();j++) {
					if(resultList1.get(i).getColumnName().equals(resultList2.get(j).getColumnName())) {
						resultList.add(resultList2.get(j));
						break;
					} else {
						if(j==resultList2.size()-1) {
							newColumnFlag=true;
						}
					}
					if(newColumnFlag) {
						resultList.add(resultList1.get(i));
						break;
					}
				}
			}
		} else {
			resultList.addAll(resultList1);
		}

		//如果数据字典中此表的栏位多余数据库表的栏位,需要删除数据字典中此表的此栏位
		if(resultList1.size()<resultList2.size()) {
			for(int i=0;i<resultList2.size();i++) {
				boolean removeFlag=false;
				for(int j=0;j<resultList1.size();j++) {
					if(resultList2.get(i).getColumnName().equals(resultList1.get(j).getColumnName())) {
						break;
					} else {
						if(j==resultList1.size()-1) {
							removeFlag=true;
						}
					}
					if(removeFlag) {
						DataDictionaryTO dataDictionaryTO=new DataDictionaryTO();
						dataDictionaryTO.setTableName(resultList2.get(i).getTableName());
						dataDictionaryTO.setColumnName(resultList2.get(i).getColumnName());
						this.getSqlMapClientTemplate().delete("common.deleteColumn",dataDictionaryTO);
					}
				}
			}
		}
		return resultList;
	}
	
	public List<DataDictionaryTO> isMaintenanceTable() throws Exception {
		
		List<DataDictionaryTO> resultList=null;
		resultList=this.getSqlMapClientTemplate().queryForList("common.isMaintenanceTable");
		
		if(resultList==null) {
			resultList=new ArrayList<DataDictionaryTO>();
		}
		
		return resultList;
	}
	
	public void deleteTableInfo(DataDictionaryTO dataDictionaryTO) throws Exception {
		
		this.getSqlMapClientTemplate().delete("common.deleteTableInfo",dataDictionaryTO);
	}
	
	public void insertTableInfo(DataDictionaryTO dataDictionaryTO) throws Exception {
		
		this.getSqlMapClientTemplate().insert("common.insertTableInfo",dataDictionaryTO);
	}
	
	public List<DataDictionaryTO> getDBTableConstraint(Map<String,String> param) throws Exception {
		
		List<DataDictionaryTO> resultList=null;
		resultList=this.getSqlMapClientTemplate().queryForList("common.getDBTableConstraint",param);
		
		if(resultList==null) {
			resultList=new ArrayList<DataDictionaryTO>();
		}
		
		return resultList;
	}

	//获得最新表结构
	public List<DataDictionaryTO> getDBTableDetail1(Map<String,String> param) throws Exception {
		
		List<DataDictionaryTO> resultList=null;
		
		resultList=this.getSqlMapClientTemplate().queryForList("common.getDBTableDetail1",param);
		
		if(resultList==null) {
			resultList=new ArrayList<DataDictionaryTO>();
		}

		return resultList;
	}
	
	//获得此表是否维护过
	public List<DataDictionaryTO> getDBTableDetail2(Map<String,String> param) throws Exception {
		
		List<DataDictionaryTO> resultList=null;
		
		resultList=this.getSqlMapClientTemplate().queryForList("common.getDBTableDetail2",param);
		
		if(resultList==null) {
			resultList=new ArrayList<DataDictionaryTO>();
		}

		return resultList;
	}
	
	public List<DataDictionaryTO> getItUserList() throws Exception {
		
		List<DataDictionaryTO> resultList=null;
		
		resultList=this.getSqlMapClientTemplate().queryForList("common.getItUserList");
		
		if(resultList==null) {
			resultList=new ArrayList<DataDictionaryTO>();
		}

		return resultList;
		
	}
}
