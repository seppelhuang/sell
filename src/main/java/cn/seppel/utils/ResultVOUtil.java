package cn.seppel.utils;

import cn.seppel.vo.ResultVO;

/**
 * @description:
 * @author: Huangsp
 * @create: 2019-03-17
 **/
public class ResultVOUtil {
    public static ResultVO success(Object object) {
        ResultVO resultVO = new ResultVO();
        resultVO.setCode(0);
        resultVO.setData(object);
        return resultVO;
    }

    public static ResultVO success() {
        return success(null);
    }

    public static ResultVO error(Integer code, String message) {
        ResultVO resultVO = new ResultVO();
        resultVO.setCode(code);
        resultVO.setMesage(message);
        return resultVO;
    }
}
