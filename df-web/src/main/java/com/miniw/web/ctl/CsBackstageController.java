package com.miniw.web.ctl;

import com.miniw.common.base.ResultCode;
import com.miniw.common.base.ResultMsg;
import com.miniw.web.application.CsBackstageApplication;
import com.miniw.web.param.req.DetailQueryRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 客服后台接口
 *
 * @author luoquan
 * @date 2021/08/10
 */
@Slf4j
@RestController
@RequestMapping("/v2/backstage")
public class CsBackstageController {

    @Resource
    private CsBackstageApplication backstageApplication;



    /**
     * 获取迷你号免流卡运营商状态
     *
     * @param uin 迷你号
     * @return {@link ResultMsg}
     */
    @GetMapping("/getStatus/{uin}")
    public ResultMsg getDfStatusByUin(@PathVariable String uin){
        try {
            return backstageApplication.getDfStatusByUin(uin);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResultMsg(ResultCode.SYS_ERROR.getCode(), ResultCode.SYS_ERROR.getMsg());
        }
    }


    /**
     * 免流套餐查询
     *
     * @param request 请求
     * @return {@link ResultMsg}
     */
    @GetMapping(path = "query")
    public ResultMsg getDfStatusDetail(DetailQueryRequest request){
        try {
            ResultMsg result = new ResultMsg(ResultCode.SYS_SUCCESS.getCode(), ResultCode.SYS_SUCCESS.getMsg());
            result.addData("queryList", backstageApplication.getDfStatusDetail(request));
            return result;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResultMsg(ResultCode.SYS_ERROR.getCode(), ResultCode.SYS_ERROR.getMsg());
        }
    }


}
