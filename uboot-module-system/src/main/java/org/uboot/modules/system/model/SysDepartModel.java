package org.uboot.modules.system.model;

import lombok.Data;
import org.apache.poi.ss.formula.functions.T;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author: LiYang
 * @Email: lyflyyvip@163.com
 * @create: 2020-02-17 00:13
 * @Description:
 **/
public abstract class SysDepartModel<T> implements Serializable {

    protected static final long serialVersionUID = 1L;

    /** 对应SysDepart中的id字段,前端数据树中的key*/
    protected String key;

    /** 对应SysDepart中的id字段,前端数据树中的value*/
    protected String value;

    /** 对应depart_name字段,前端数据树中的title*/
    protected String title;


    protected boolean isLeaf;
    // 以下所有字段均与SysDepart相同

    protected String id;

    protected String parentId;

    protected String departName;

    protected String departNameEn;

    protected String departNameAbbr;

    protected Integer departOrder;

    protected Object description;

    protected String orgCategory;

    protected String orgType;

    protected String orgCode;

    protected String mobile;

    protected String fax;

    protected String address;

    protected String memo;

    protected String status;

    protected String delFlag;

    protected String createBy;

    protected Date createTime;

    protected String updateBy;

    protected Date updateTime;

    protected Integer organization;

    public Integer getOrganization() {
        return organization;
    }

    public void setOrganization(Integer organization) {
        this.organization = organization;
    }

    List<T> children = new ArrayList<>();

    public List<T> getChildren() {
        return children;
    }

    public void setChildren(List<T> children) {
        if (children==null){
            this.isLeaf=true;
        }
        this.children = children;
    }

    public boolean getIsLeaf() {
        return isLeaf;
    }

    public void setIsLeaf(boolean isleaf) {
        this.isLeaf = isleaf;
    }

    public String getKey() {
        return key;
    }


    public void setKey(String key) {
        this.key = key;
    }


    public String getValue() {
        return value;
    }


    public void setValue(String value) {
        this.value = value;
    }


    public String getTitle() {
        return title;
    }


    public void setTitle(String title) {
        this.title = title;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getDepartName() {
        return departName;
    }

    public void setDepartName(String departName) {
        this.departName = departName;
    }

    public String getOrgCategory() {
        return orgCategory;
    }

    public void setOrgCategory(String orgCategory) {
        this.orgCategory = orgCategory;
    }

    public String getOrgType() {
        return orgType;
    }

    public void setOrgType(String orgType) {
        this.orgType = orgType;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getDepartNameEn() {
        return departNameEn;
    }

    public void setDepartNameEn(String departNameEn) {
        this.departNameEn = departNameEn;
    }

    public String getDepartNameAbbr() {
        return departNameAbbr;
    }

    public void setDepartNameAbbr(String departNameAbbr) {
        this.departNameAbbr = departNameAbbr;
    }

    public Integer getDepartOrder() {
        return departOrder;
    }

    public void setDepartOrder(Integer departOrder) {
        this.departOrder = departOrder;
    }

    public Object getDescription() {
        return description;
    }

    public void setDescription(Object description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

}
