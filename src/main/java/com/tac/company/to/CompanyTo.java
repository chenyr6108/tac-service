package com.tac.company.to;

import com.brick.base.to.BaseTo;

public class CompanyTo extends BaseTo{

	private static final long serialVersionUID = 1L;
	
	private Integer id;

	private String name;

	private String code;

	private String businessLicenceCode;

	private String taxRegistrationNumber;

	private java.util.Date incorporatingDate;

	private String telephone;

	private String fax;

	private String url;

	private String postcode;

	private String legalPerson;

	private String legalHomeAddress;

	private String address;

	private String registeredAddress;

	private String openAccountBank;

	private String bankAccount;

	private Integer status;

	private Integer orderNo;

	private Integer createBy;

	private java.util.Date createDate;

	private Integer modifyBy;

	private java.util.Date modifyDate;

	private Integer parentId;

	private String comment;
	
	private String parentCompanyName;
	
	public void setId(Integer id){
		this.id=id;
	}
	public Integer getId(){
		return this.id;
	}

	public void setName(String name){
		this.name=name;
	}
	public String getName(){
		return this.name;
	}

	public void setCode(String code){
		this.code=code;
	}
	public String getCode(){
		return this.code;
	}

	public void setBusinessLicenceCode(String businessLicenceCode){
		this.businessLicenceCode=businessLicenceCode;
	}
	public String getBusinessLicenceCode(){
		return this.businessLicenceCode;
	}

	public void setTaxRegistrationNumber(String taxRegistrationNumber){
		this.taxRegistrationNumber=taxRegistrationNumber;
	}
	public String getTaxRegistrationNumber(){
		return this.taxRegistrationNumber;
	}

	public void setIncorporatingDate(java.util.Date incorporatingDate){
		this.incorporatingDate=incorporatingDate;
	}
	public java.util.Date getIncorporatingDate(){
		return this.incorporatingDate;
	}

	public void setTelephone(String telephone){
		this.telephone=telephone;
	}
	public String getTelephone(){
		return this.telephone;
	}

	public void setFax(String fax){
		this.fax=fax;
	}
	public String getFax(){
		return this.fax;
	}

	public void setUrl(String url){
		this.url=url;
	}
	public String getUrl(){
		return this.url;
	}

	public void setPostcode(String postcode){
		this.postcode=postcode;
	}
	public String getPostcode(){
		return this.postcode;
	}

	public void setLegalPerson(String legalPerson){
		this.legalPerson=legalPerson;
	}
	public String getLegalPerson(){
		return this.legalPerson;
	}

	public void setLegalHomeAddress(String legalHomeAddress){
		this.legalHomeAddress=legalHomeAddress;
	}
	public String getLegalHomeAddress(){
		return this.legalHomeAddress;
	}

	public void setAddress(String address){
		this.address=address;
	}
	public String getAddress(){
		return this.address;
	}

	public void setRegisteredAddress(String registeredAddress){
		this.registeredAddress=registeredAddress;
	}
	public String getRegisteredAddress(){
		return this.registeredAddress;
	}

	public void setOpenAccountBank(String openAccountBank){
		this.openAccountBank=openAccountBank;
	}
	public String getOpenAccountBank(){
		return this.openAccountBank;
	}

	public void setBankAccount(String bankAccount){
		this.bankAccount=bankAccount;
	}
	public String getBankAccount(){
		return this.bankAccount;
	}

	public void setStatus(Integer status){
		this.status=status;
	}
	public Integer getStatus(){
		return this.status;
	}

	public void setOrderNo(Integer orderNo){
		this.orderNo=orderNo;
	}
	public Integer getOrderNo(){
		return this.orderNo;
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

	public void setParentId(Integer parentId){
		this.parentId=parentId;
	}
	public Integer getParentId(){
		return this.parentId;
	}

	public void setComment(String comment){
		this.comment=comment;
	}
	public String getComment(){
		return this.comment;
	}
	public String getParentCompanyName() {
		return parentCompanyName;
	}
	public void setParentCompanyName(String parentCompanyName) {
		this.parentCompanyName = parentCompanyName;
	}
}
