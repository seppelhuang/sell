package cn.seppel.controller.seller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @description:
 * @author: Huangsp
 * @create: 2019-03-24
 **/
@Controller
public class IndexController {

    @GetMapping(value = "/")
    public String index() {

        return "redirect:/seller/order/list";
    }
}

