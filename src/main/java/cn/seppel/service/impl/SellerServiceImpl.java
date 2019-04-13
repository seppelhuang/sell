package cn.seppel.service.impl;

import cn.seppel.entity.SellerInfo;
import cn.seppel.repository.SellerInfoRepository;
import cn.seppel.service.SellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @description: 卖家服务
 * @author: Huangsp
 * @create: 2019-03-16
 **/
@Service
public class SellerServiceImpl implements SellerService {
    @Autowired
    private SellerInfoRepository sellerInfoRepository;

    @Override
    public SellerInfo findSellerInfoByOpenid(String openid) {
        return this.sellerInfoRepository.findByOpenid(openid);
    }
}
