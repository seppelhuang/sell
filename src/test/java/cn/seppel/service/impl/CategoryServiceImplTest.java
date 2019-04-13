package cn.seppel.service.impl;

import cn.seppel.entity.ProductCategory;
import cn.seppel.mapper.ProductCategoryMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CategoryServiceImplTest {
    @Autowired
    private CategoryServiceImpl categoryService;
    @Autowired
    private ProductCategoryMapper productCategoryMapper;

    @Test
    public void saveTest() {
        ProductCategory productCategory = new ProductCategory();
        productCategory.setCategoryType(30);
        productCategory.setCategoryName("dd");
        Assert.assertNotNull(categoryService.save(productCategory));
    }

    @Test
    public void findCategoryTest() {

        List<ProductCategory> productCategories = this.productCategoryMapper.findByCategoryName("冰爽饮品限时特惠");
        Assert.assertNotEquals(0, productCategories.size());
    }
}