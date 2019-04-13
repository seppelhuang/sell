package cn.seppel.service.impl;

import cn.seppel.converter.Order2OrderDTOConverter;
import cn.seppel.dto.CartDTO;
import cn.seppel.dto.OrderDTO;
import cn.seppel.entity.Order;
import cn.seppel.entity.OrderItem;
import cn.seppel.entity.ProductInfo;
import cn.seppel.enums.OrderStatusEnum;
import cn.seppel.enums.PayStatusEnum;
import cn.seppel.enums.ResultEnum;
import cn.seppel.exception.SellException;
import cn.seppel.repository.OrderItemRepository;
import cn.seppel.repository.OrderRepository;
import cn.seppel.service.OrderService;
import cn.seppel.service.ProductService;
import cn.seppel.service.WebSocket;
import cn.seppel.utils.KeyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @description: 订单服务
 * @author: Huangsp
 * @create: 2019-03-16
 **/
@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    @Autowired
    private ProductService productService;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private WebSocket webSocket;

    @Override
    public OrderDTO create(OrderDTO orderDTO) {
        String orderId = KeyUtil.genUniqueKey();

        BigDecimal orderAmount = new BigDecimal(BigInteger.ZERO);


        for (OrderItem orderItem : orderDTO.getOrderItemList()) {
            ProductInfo productInfo = productService.findOne(orderItem.getProductId());
            if (productInfo == null) {
                throw new SellException(ResultEnum.PRODUCT_NOT_EXI);
            }
            //2. 计算订单总价
            orderAmount = productInfo.getProductPrice()
                    .multiply(new BigDecimal(orderItem.getProductQuantity()))
                    .add(orderAmount);


            orderItem.setId(KeyUtil.genUniqueKey());
            orderItem.setOrderId(orderId);
            BeanUtils.copyProperties(productInfo, orderItem);
            orderItemRepository.save(orderItem);
        }

        Order order = new Order();
        orderDTO.setOrderId(orderId);
        BeanUtils.copyProperties(orderDTO, order);
        order.setOrderId(orderId);
        order.setOrderStatus(OrderStatusEnum.NEW.getCode());
        order.setPayStatus(PayStatusEnum.WAIT.getCode());
        order.setOrderAmount(orderAmount);
        orderRepository.save(order);
        //4. 扣库存
        List<CartDTO> cartDTOList = orderDTO.getOrderItemList().stream().map(e ->
                new CartDTO(e.getProductId(), e.getProductQuantity())
        ).collect(Collectors.toList());
        productService.decreaseStock(cartDTOList);
        //发送websocket消息
        webSocket.sendMessage(orderDTO.getOrderId());
        return orderDTO;
    }

    @Override
    public OrderDTO findOne(String orderId) {
        Order order = orderRepository.findOne(orderId);
        if (order == null) {
            log.error("【查询订单列表】openid为空");
            throw new SellException(ResultEnum.ORDER_NOT_EX);
        }

        List<OrderItem> orderItemList = orderItemRepository.findByOrderId(orderId);
        if (CollectionUtils.isEmpty(orderItemList)) {
            throw new SellException(ResultEnum.ORDERDETAIL_NOT_EXIST);
        }

        OrderDTO orderDTO = new OrderDTO();
        BeanUtils.copyProperties(order, orderDTO);
        orderDTO.setOrderItemList(orderItemList);

        return orderDTO;
    }

    @Override
    public Page<OrderDTO> findList(String buyerOpenid, Pageable pageable) {
        Page<Order> orderDTOPage = orderRepository.findByBuyerOpenid(buyerOpenid, pageable);
        List<OrderDTO> orderDTOList = Order2OrderDTOConverter.convert(orderDTOPage.getContent());
        Page<OrderDTO> dtoPage = new PageImpl<OrderDTO>(orderDTOList, pageable, orderDTOPage.getTotalElements());
        return dtoPage;
    }

    @Override
    public Page<OrderDTO> findList(Pageable pageable) {
        Page<Order> orderMasterPage = orderRepository.findAll(pageable);
        List<OrderDTO> orderDTOList = Order2OrderDTOConverter.convert(orderMasterPage.getContent());
        return new PageImpl<OrderDTO>(orderDTOList, pageable, orderMasterPage.getTotalElements());
    }

    @Override
    public OrderDTO cancel(OrderDTO orderDTO) {
        Order order = new Order();
        if (!orderDTO.getOrderStatus().equals(OrderStatusEnum.NEW.getCode())) {
            log.error("【取消订单操作】 订单状态不正确 orderId={},orderStats={}", orderDTO.getOrderId(), orderDTO.getOrderStatus());
            throw new SellException(ResultEnum.ORDER_STATUS_ERROR);
        }
        orderDTO.setOrderStatus(OrderStatusEnum.CANCEL.getCode());
        BeanUtils.copyProperties(orderDTO, order);
        Order updateResult = orderRepository.save(order);

        if (updateResult == null) {
            log.error("取消订单失败,order={}", order);
            throw new SellException(ResultEnum.ORDER_UPDATE_FAIL);
        }

        //返回库存
        if (CollectionUtils.isEmpty(orderDTO.getOrderItemList())) {
            log.error("【取消订单】订单中无商品详情, orderDTO={}", orderDTO);
            throw new SellException(ResultEnum.ORDER_DETAIL_EMPTY);
        }
        List<CartDTO> cartDTOList = orderDTO.getOrderItemList().stream()
                .map(e -> new CartDTO(e.getProductId(), e.getProductQuantity()))
                .collect(Collectors.toList());
        productService.increaseStock(cartDTOList);

        return orderDTO;
    }

    @Override
    public OrderDTO finish(OrderDTO orderDTO) {
        if (OrderStatusEnum.NEW.getCode() != orderDTO.getOrderStatus()) {
            log.error("");
            throw new SellException(ResultEnum.ORDER_STATUS_ERROR);
        }
        orderDTO.setOrderStatus(OrderStatusEnum.FINISHED.getCode());
        Order order = new Order();
        BeanUtils.copyProperties(orderDTO, order);

        Order updateResult = orderRepository.save(order);

        if (updateResult == null) {
            log.error("取消完结失败,order={}", order);
            throw new SellException(ResultEnum.ORDER_UPDATE_FAIL);
        }
        //todo  推送消息
        return orderDTO;
    }

    @Override
    public OrderDTO paid(OrderDTO orderDTO) {
        if (!orderDTO.getOrderStatus().equals(OrderStatusEnum.NEW.getCode())) {
            log.error("【支付订单操作】 订单状态不正确 orderId={},orderStats={}", orderDTO.getOrderId(), orderDTO.getOrderStatus());
            throw new SellException(ResultEnum.ORDER_STATUS_ERROR);
        }
        if (!orderDTO.getPayStatus().equals(PayStatusEnum.WAIT.getCode())) {
            log.error("【支付订单操作】 订单支付状态不正确 orderId={},orderStats={}", orderDTO.getOrderId(), orderDTO.getOrderStatus());
            throw new SellException(ResultEnum.ORDER_PAY_STATUS_ERROR);
        }
        //修改支付状态
        orderDTO.setPayStatus(PayStatusEnum.SUCCESS.getCode());
        Order order = new Order();
        BeanUtils.copyProperties(orderDTO, order);

        Order updateResult = orderRepository.save(order);

        if (updateResult == null) {
            log.error("支付订单失败,order={}", order);
            throw new SellException(ResultEnum.ORDER_UPDATE_FAIL);
        }
        return orderDTO;
    }
}
