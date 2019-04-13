package cn.seppel.service;

import cn.seppel.entity.SellerInfo;

public interface SellerService {

    SellerInfo findSellerInfoByOpenid(String openid);
}
