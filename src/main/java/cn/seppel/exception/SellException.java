package cn.seppel.exception;

import cn.seppel.enums.ResultEnum;
import lombok.Getter;

/**
 * @description:
 * @author: Huangsp
 * @create: 2019-03-17
 **/
@Getter
public class SellException extends RuntimeException {
    private Integer code;

    public SellException(ResultEnum resultEnum) {
        super(resultEnum.getMessage());
        this.code = resultEnum.getCode();
    }

    public SellException(Integer code, String message) {
        super(message);
        this.code = code;
    }


}
