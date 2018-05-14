package com.maggie.dating.daos.mysql;

import com.maggie.dating.beans.UserKey;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserKeyDao extends CrudRepository<UserKey,Long> {

    /**
     * 根据手机号码和设备号检索用户
     * @param mobile
     * @return
     */
    UserKey findByMobileAndEquipmentCode(@Param("mobile")String mobile,@Param("equipmentCode")String equipmentCode);

    /**
     * 根据用户手机号 设备码 公钥 查找激活状态的证书
     * @param mobile
     * @param equipmentCode
     * @param publicKey
     * @param status
     * @return
     */
    UserKey findByMobileAndEquipmentCodeAndPublicKeyAndStatus(@Param("mobile")String mobile,@Param("equipmentCode")String equipmentCode,@Param("publicKey")String publicKey,@Param("status")Integer status);

    /**
     * 根据手机号检索用户密钥信息
     * @param mobile
     * @return
     */
    List<UserKey> findByMobile(@Param("mobile")String mobile);

    /**
     * 根据手机号状态检索数据
     * @param userId
     * @param status
     * @return
     */
    UserKey findByUserIdAndStatus(@Param("userId")String userId,@Param("status")Integer status);


}
