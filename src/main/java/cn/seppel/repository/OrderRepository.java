package cn.seppel.repository;

import cn.seppel.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, String> {
    Page<Order> findByBuyerOpenid(String buyerOpenid, Pageable pageable);
}
