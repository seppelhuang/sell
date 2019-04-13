package cn.seppel.controller;

import cn.seppel.config.ProjectUrlConfig;
import cn.seppel.enums.ResultEnum;
import cn.seppel.exception.SellException;
import cn.seppel.utils.SignUtil;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.net.URLEncoder;

/**
 * @description:
 * @author: Huangsp
 * @create: 2019-03-18
 **/
@Controller
@Slf4j
public class WechatController {

    @GetMapping("/wechat/checkSignature")
    public void checkSignature(HttpServletRequest request, HttpServletResponse response) throws Exception {

        String signature = request.getParameter("signature");/// 微信加密签名
        String timestamp = request.getParameter("timestamp");/// 时间戳
        String nonce = request.getParameter("nonce"); /// 随机数
        String echostr = request.getParameter("echostr"); // 随机字符串
        PrintWriter out = response.getWriter();

        if (SignUtil.checkSignature(signature, timestamp, nonce)) {
            out.print(echostr);
        }
        out.close();
        out = null;

    }

    @Autowired
    private WxMpService wxMpService;

    @Autowired
    private WxMpService wxOpenService;

    @Autowired
    private ProjectUrlConfig projectUrlConfig;


    @GetMapping("/wechat/authorize")
    public String authorize(@RequestParam("returnUrl") String returnUrl) {
        //1 配置
        String url = projectUrlConfig.getWechatMpAuthorize() + "/sell/wechat/userInfo";
        String redirectUrl = wxMpService.oauth2buildAuthorizationUrl(url, WxConsts.OAUTH2_SCOPE_USER_INFO, URLEncoder.encode(returnUrl));
        log.info("[微信网页授权获取 code],resule={}", redirectUrl);

        return "redirect:" + redirectUrl;
    }

    @GetMapping("/wechat/userInfo")
    public String userInfo(@RequestParam("code") String code,
                           @RequestParam("state") String returnUrl) {

        WxMpOAuth2AccessToken wxMpOAuth2AccessToken = new WxMpOAuth2AccessToken();
        try {
            wxMpOAuth2AccessToken = wxMpService.oauth2getAccessToken(code);
        } catch (WxErrorException ex) {
            log.error("【微信网页授权】{}", ex);
            throw new SellException(ResultEnum.WECHAT_MP_ERROR.getCode(), ex.getError().getErrorMsg());
        }

        String openId = wxMpOAuth2AccessToken.getOpenId();
        log.info("opiedId: " + openId);
        return "redirect:" + returnUrl + "?openid=" + openId;
    }


    @GetMapping("/wechat/qrauthorize")
    public String qrAuthorize(@RequestParam("returnUrl") String returnUrl) {
        //1 配置
        String url = projectUrlConfig.getWechatOpenAuthorize() + "/wechat/qruserInfo";
        String redirectUrl = wxOpenService.buildQrConnectUrl(url, WxConsts.QRCONNECT_SCOPE_SNSAPI_LOGIN, URLEncoder.encode(returnUrl));
        log.info("[微信网页登陆获取 code],resule={}", redirectUrl);

        return "redirect:" + redirectUrl;
    }

    @GetMapping("/wechat/qruserInfo")
    public String qrUserInfo(@RequestParam("code") String code,
                             @RequestParam("state") String returnUrl) {

        WxMpOAuth2AccessToken wxMpOAuth2AccessToken = new WxMpOAuth2AccessToken();
        try {
            wxMpOAuth2AccessToken = wxOpenService.oauth2getAccessToken(code);
        } catch (WxErrorException ex) {
            log.error("【微信网页登陆】{}", ex);
            throw new SellException(ResultEnum.WECHAT_MP_ERROR.getCode(), ex.getError().getErrorMsg());
        }

        String openId = wxMpOAuth2AccessToken.getOpenId();
        log.info("opiedId: " + openId);
        return "redirect:" + returnUrl + "?openid=" + openId;
    }
}
