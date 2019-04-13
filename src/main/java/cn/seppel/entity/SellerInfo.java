package cn.seppel.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @description: 卖家实体类
 * @author: Huangsp
 * @create: 2019-03-16
 **/
@Entity
@Data
public class SellerInfo {
    @Id
    private String id;

    private String username;

    private String password;

    private String openid;
}
