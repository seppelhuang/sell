package cn.seppel.repository;


import cn.seppel.entity.ProductCategory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProductInfoCategoryRepositoryTest {
    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Test
    public void saveTest() {
        ProductCategory productCategory = this.productCategoryRepository.findOne(4);
        productCategory.setCategoryType(20);
        Assert.assertNotNull(productCategoryRepository.save(productCategory));
    }

}