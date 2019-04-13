package cn.seppel.converter;

import cn.seppel.dto.OrderDTO;
import cn.seppel.entity.Order;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: Huangsp
 * @create: 2019-03-17
 **/
public class Order2OrderDTOConverter {
    public static OrderDTO convert(Order order) {
        OrderDTO orderDTO = new OrderDTO();
        BeanUtils.copyProperties(order, orderDTO);
        return orderDTO;
    }

    public static List<OrderDTO> convert(List<Order> orderList) {
        return orderList.stream().map(e ->
                convert(e)
        ).collect(Collectors.toList());
    }
}
