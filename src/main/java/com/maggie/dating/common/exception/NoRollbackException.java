package com.maggie.dating.common.exception;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import vip.maggie.dating.beans.vos.RespDataVo;
import vip.maggie.dating.common.enums.RespStatusEnum;

/**
 * @program: dating
 * @description: 要抛出异常但是不要回滚的时候，使用这个异常，并且指定  @Transactional(noRollbackFor=NoRollbackException.class)
 * @author: Niuxiaozu
 * @create: 2018-01-30 15:41
 **/
@Data
@NoArgsConstructor
@ToString
public class NoRollbackException extends BusiException {
    public NoRollbackException(String status, String msg) {
        super(status,msg);
    }

    public NoRollbackException(RespDataVo resp) {
        super(resp);
    }

    public NoRollbackException(RespStatusEnum rse) {
        super(rse);
    }
}
