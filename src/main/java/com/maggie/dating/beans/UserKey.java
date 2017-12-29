package com.maggie.dating.beans;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 用户密钥实体类
 */
@Entity
@Table(name = "t_user_userkey")
public class UserKey implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "USER_ID")
    private String userId;
    /**
     * 用户公钥
     */
    @Column(name = "PUBLIC_KEY")
    private String publicKey;
    /**
     * 用户证书
     */
    @Column(name = "USER_CA")
    private String userCa;
    /**
     * 证书有效期开始时间
     */
    @Column(name = "CA_START_TIME")
    private Date caStartTime;
    /**
     * 证书有效期结束时间
     */
    @Column(name = "CA_END_TIME")
    private Date caEndTime;
    @Column(name = "EQUIPMENT_CODE")
    private String equipmentCode;
    @Column(name = "MOBILE")
    private String mobile;
    /**
     * 0:正常 -1:禁用 -2:删除
     */
    @Column(name = "STATUS", length = 1)
    private Integer status;

    @Column(name = "CREATE_ID")
    private String createId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATE_TIME")
    private Date createTime;

    @Column(name = "MODIFY_ID")
    private Long modifyId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "MODIFY_TIME")
    private Date modifyTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getUserCa() {
        return userCa;
    }

    public void setUserCa(String userCa) {
        this.userCa = userCa;
    }

    public Date getCaStartTime() {
        return caStartTime;
    }

    public void setCaStartTime(Date caStartTime) {
        this.caStartTime = caStartTime;
    }

    public Date getCaEndTime() {
        return caEndTime;
    }

    public void setCaEndTime(Date caEndTime) {
        this.caEndTime = caEndTime;
    }


    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCreateId() {
        return createId;
    }

    public void setCreateId(String createId) {
        this.createId = createId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Long getModifyId() {
        return modifyId;
    }

    public void setModifyId(Long modifyId) {
        this.modifyId = modifyId;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    public String getEquipmentCode() {
        return equipmentCode;
    }

    public void setEquipmentCode(String equipmentCode) {
        this.equipmentCode = equipmentCode;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
