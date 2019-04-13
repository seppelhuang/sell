package cn.seppel.repository;

import cn.seppel.entity.ProductInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<ProductInfo, String> {

    List<ProductInfo> findAllByProductStatus(Integer productStatus);

}
