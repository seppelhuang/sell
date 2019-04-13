package cn.seppel.service.impl;

import cn.seppel.entity.ProductInfo;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProductServiceImplTest {

    @Autowired
    private ProductServiceImpl productService;

    @Test
    public void findUpAll() {
        List<ProductInfo> productInfoList = this.productService.findUpAll();
        Assert.assertNotEquals(0, productInfoList.size());
    }
}