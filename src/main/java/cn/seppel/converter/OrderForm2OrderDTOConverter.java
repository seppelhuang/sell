package cn.seppel.converter;

import cn.seppel.dto.OrderDTO;
import cn.seppel.entity.OrderItem;
import cn.seppel.enums.ResultEnum;
import cn.seppel.exception.SellException;
import cn.seppel.form.OrderForm;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @author: Huangsp
 * @create: 2019-03-17
 **/
public class OrderForm2OrderDTOConverter {

    public static OrderDTO convert(OrderForm orderForm) {
        Gson gson = new Gson();

        OrderDTO orderDTO = new OrderDTO();

        orderDTO.setBuyerName(orderForm.getName());
        orderDTO.setBuyerPhone(orderForm.getPhone());
        orderDTO.setBuyerAddress(orderForm.getAddress());
        orderDTO.setBuyerOpenid(orderForm.getOpenid());
        List<OrderItem> orderItemList = new ArrayList<>();
        try {
            orderItemList = gson.fromJson(orderForm.getItems(), new TypeToken<List<OrderItem>>() {
            }.getType());
        } catch (Exception e) {
            throw new SellException(ResultEnum.PARAM_ERROR);
        }
        orderDTO.setOrderItemList(orderItemList);
        return orderDTO;
    }
}
