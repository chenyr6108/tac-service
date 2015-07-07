package com.brick.sys.TO;

import java.sql.Date;

import com.brick.base.to.BaseTo;

public class DataDictionaryTO extends BaseTo {

	private static final long serialVersionUID=1L;

	private int id;
	private String tableName;
	private String tableChineseName;
	private Date tableCreateTime;
	private String columnName;
	private String description;
	private String lastUpdatedBy;
	private Date lastUpdateOn;
	private String dataType;
	
	private String [] columnNameInsert;
	private String [] dataTypeInsert;
	private String [] descriptionInsert;
	
	private String s_employeeId;
	
	private String constraintName;
	
	private String isMaintenanceTable;
	
	private String name;
	private int version;
	
	private String remark;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getTableChineseName() {
		return tableChineseName;
	}
	public void setTableChineseName(String tableChineseName) {
		this.tableChineseName = tableChineseName;
	}
	public Date getTableCreateTime() {
		return tableCreateTime;
	}
	public void setTableCreateTime(Date tableCreateTime) {
		this.tableCreateTime = tableCreateTime;
	}
	public String getColumnName() {
		return columnName;
	}
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getLastUpdatedBy() {
		return lastUpdatedBy;
	}
	public void setLastUpdatedBy(String lastUpdatedBy) {
		this.lastUpdatedBy = lastUpdatedBy;
	}
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	public String[] getColumnNameInsert() {
		return columnNameInsert;
	}
	public void setColumnNameInsert(String[] columnNameInsert) {
		this.columnNameInsert = columnNameInsert;
	}
	public String[] getDataTypeInsert() {
		return dataTypeInsert;
	}
	public void setDataTypeInsert(String[] dataTypeInsert) {
		this.dataTypeInsert = dataTypeInsert;
	}
	public String[] getDescriptionInsert() {
		return descriptionInsert;
	}
	public void setDescriptionInsert(String[] descriptionInsert) {
		this.descriptionInsert = descriptionInsert;
	}
	public String getS_employeeId() {
		return s_employeeId;
	}
	public void setS_employeeId(String s_employeeId) {
		this.s_employeeId = s_employeeId;
	}
	public String getIsMaintenanceTable() {
		if(isMaintenanceTable==null||"".equals(isMaintenanceTable)) {
			isMaintenanceTable="N";
		}
		return isMaintenanceTable;
	}
	public void setIsMaintenanceTable(String isMaintenanceTable) {
		this.isMaintenanceTable = isMaintenanceTable;
	}
	public String getConstraintName() {
		return constraintName;
	}
	public void setConstraintName(String constraintName) {
		this.constraintName = constraintName;
	}
	public Date getLastUpdateOn() {
		return lastUpdateOn;
	}
	public void setLastUpdateOn(Date lastUpdateOn) {
		this.lastUpdateOn = lastUpdateOn;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
}
