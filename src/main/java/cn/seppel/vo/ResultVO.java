package cn.seppel.vo;

import lombok.Data;

/**
 * @description: HTTP请求返回对象
 * @author: Huangsp
 * @create: 2019-03-16
 **/
@Data
public class ResultVO<T> {
    private Integer code;

    private String mesage;

    private T data;
}
