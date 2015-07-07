package com.brick.payer.to;

import com.brick.base.to.BaseTo;

public class PayerTo extends BaseTo{

	private static final long serialVersionUID = 1L;
	
	private Integer id;

	private String linkmanName;

	private String linkmanMobile;

	private Integer creditId;

	private Integer status;

	private Integer createBy;

	private java.util.Date createDate;

	private Integer modifyBy;

	private java.util.Date modifyDate;

	public void setId(Integer id){
		this.id=id;
	}
	public Integer getId(){
		return this.id;
	}

	public void setLinkmanName(String linkmanName){
		this.linkmanName=linkmanName;
	}
	public String getLinkmanName(){
		return this.linkmanName;
	}

	public void setLinkmanMobile(String linkmanMobile){
		this.linkmanMobile=linkmanMobile;
	}
	public String getLinkmanMobile(){
		return this.linkmanMobile;
	}

	public void setCreditId(Integer creditId){
		this.creditId=creditId;
	}
	public Integer getCreditId(){
		return this.creditId;
	}

	public void setStatus(Integer status){
		this.status=status;
	}
	public Integer getStatus(){
		return this.status;
	}

	public void setCreateBy(Integer createBy){
		this.createBy=createBy;
	}
	public Integer getCreateBy(){
		return this.createBy;
	}

	public void setCreateDate(java.util.Date createDate){
		this.createDate=createDate;
	}
	public java.util.Date getCreateDate(){
		return this.createDate;
	}

	public void setModifyBy(Integer modifyBy){
		this.modifyBy=modifyBy;
	}
	public Integer getModifyBy(){
		return this.modifyBy;
	}

	public void setModifyDate(java.util.Date modifyDate){
		this.modifyDate=modifyDate;
	}
	public java.util.Date getModifyDate(){
		return this.modifyDate;
	}


	
}
