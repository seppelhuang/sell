package cn.seppel.repository;


import cn.seppel.entity.ProductInfo;
import cn.seppel.enums.ProductStatusEnum;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProductRepositoryTest {
    @Autowired
    private ProductRepository productRepository;

    @Test
    public void saveTest() {
        ProductInfo productInfo = new ProductInfo();
        productInfo.setProductId("123123456456");
        productInfo.setProductName("milk");
        productInfo.setProductPrice(new BigDecimal(5.5));
        productInfo.setProductStock(100);
        productInfo.setProductDescription("good milk");
        productInfo.setProductIcon("http://aa.jpg");
        productInfo.setProductStatus(ProductStatusEnum.UP.getCode());
        productInfo.setCategoryType(2);
        Assert.assertNotNull(productRepository.save(productInfo));
    }
}