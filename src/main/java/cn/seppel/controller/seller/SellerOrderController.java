package cn.seppel.controller.seller;

import cn.seppel.dto.OrderDTO;
import cn.seppel.enums.ResultEnum;
import cn.seppel.exception.SellException;
import cn.seppel.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

/**
 * @description: 卖家订单服务
 * @author: Huangsp
 * @create: 2019-03-23
 **/
@Controller
@Slf4j
public class SellerOrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/seller/order/list")
    public ModelAndView list(@RequestParam(value = "page", defaultValue = "1") Integer page,
                             @RequestParam(value = "size", defaultValue = "10") Integer size,
                             Map<String, Object> map) {

        PageRequest pageRequest = new PageRequest(page - 1, size);
        Page<OrderDTO> orderDTOPage = orderService.findList(pageRequest);
        map.put("orderDTOPage", orderDTOPage);
        map.put("currentPage", page);
        map.put("size", size);

        return new ModelAndView("order/list", map);
    }

    @GetMapping("/seller/order/detail")
    public String detail(@RequestParam("orderId") String orderId,
                         Map<String, Object> map) {
        OrderDTO orderDTO = new OrderDTO();
        try {
            orderDTO = orderService.findOne(orderId);
        } catch (SellException e) {
            log.error("【卖家端查询订单详情】发生异常{}", e);
            map.put("msg", e.getMessage());
            map.put("url", "/seller/order/list");
            return "common/error";
        }

        map.put("orderDTO", orderDTO);
        return "order/detail";
    }

    @GetMapping("/seller/order/cancel")
    public String cancel(@RequestParam("orderId") String orderId,
                         Map<String, Object> map) {

        try {
            OrderDTO orderDTO = orderService.findOne(orderId);
            orderService.cancel(orderDTO);

        } catch (Exception ex) {

            log.error("【后台取消订单】 查询不到订单");
            map.put("msg", ex.getMessage());
            map.put("url", "/seller/order/list");
            return "common/error";
        }

        map.put("msg", ResultEnum.ORDER_CANCEL_SUCCESS.getMessage());
        map.put("url", "/seller/order/list");
        return "common/success";
    }

    @GetMapping("/seller/order/finish")
    public ModelAndView finished(@RequestParam("orderId") String orderId,
                                 Map<String, Object> map) {
        try {
            OrderDTO orderDTO = orderService.findOne(orderId);
            orderService.finish(orderDTO);
        } catch (SellException e) {
            log.error("【卖家端完结订单】发生异常{}", e);
            map.put("msg", e.getMessage());
            map.put("url", "/seller/order/list");
            return new ModelAndView("common/error", map);
        }

        map.put("msg", ResultEnum.ORDER_FINISH_SUCCESS.getMessage());
        map.put("url", "/seller/order/list");
        return new ModelAndView("common/success");
    }
}
