package com.miniw.web.ctl;


import com.miniw.common.base.ResultCode;
import com.miniw.common.base.ResultMsg;
import com.miniw.common.constant.RedisKeyConstant;
import com.miniw.common.exception.ResultException;
import com.miniw.common.utils.RedisUtil;
import com.miniw.external.client.minicoin.exception.CoinPayException;
import com.miniw.persistence.service.TDfOrderLogService;
import com.miniw.web.application.TDfOrderLogApplication;
import com.miniw.web.param.req.CallBackRequest;
import com.miniw.web.param.req.GenerateDfOrderRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.Cursor;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.io.IOException;
import java.text.ParseException;
import java.util.Map;


/**
 * 免流应用层
 *
 * @author luoquan
 * @date 2021/07/28
 */
@Slf4j
@RestController
@RequestMapping("/v2/data_free")
public class DfOrderController {

    @Resource
    private TDfOrderLogApplication tDfOrderLogApplication;
    @Resource
    private TDfOrderLogService tDfOrderLogService;
    @Resource
    private RedisUtil redisUtil;


    /**
     * 验证迷你号绑定手机次数
     *
     * @param uin 迷你号
     * @return {@link ResultMsg}
     */
    @GetMapping("/check/{uin}")
    public ResultMsg bindPhoneNumVerify(@PathVariable(required = false) String uin){
        try{
            return tDfOrderLogApplication.bindPhoneNumVerify(uin);
        }catch (ResultException resultException){
            log.error(resultException.getMessage(), resultException);
            return new ResultMsg(resultException.getCode(), resultException.getReason());
        }catch (Exception e){
            log.error(e.getMessage(), e);
            return new ResultMsg(ResultCode.SYS_ERROR.getCode(), ResultCode.SYS_ERROR.getMsg());
        }
    }

    /**
     * 生成免流订单号
     *
     * @param request 请求集
     * @return {@link ResultMsg}
     */
    @PostMapping("/generateDfOrder")
    public ResultMsg generateDfOrder(@RequestBody @Valid GenerateDfOrderRequest request){
        try{
            return tDfOrderLogApplication.generateDfOrder(request);
        }catch (ResultException resultException){
            log.error(resultException.getMessage(), resultException);
            return new ResultMsg(resultException.getCode(), resultException.getReason());
        }catch (Exception e){
            log.error(e.getMessage(), e);
            return new ResultMsg(ResultCode.SYS_ERROR.getCode(), ResultCode.SYS_ERROR.getMsg());
        }
    }


    /**
     * 联通运营商回调函数
     *
     * @param callBackRequest 回调请求
     * @return {@link ResultMsg}
     */
    @PostMapping("/callBack")
    public ResultMsg successFunction(@RequestBody CallBackRequest callBackRequest){
        try{
            return tDfOrderLogApplication.callBack(callBackRequest.getData(), callBackRequest.getSign());
        }catch (ResultException resultException){
            log.error(resultException.getMessage(), resultException);
            return new ResultMsg(resultException.getCode(), resultException.getReason());
        } catch (CoinPayException coinPayException){
            log.error(coinPayException.getMessage(), coinPayException);
            return new ResultMsg(ResultCode.SYS_BUSY.getCode(), coinPayException.getReason());
        } catch (Exception e){
            log.error(e.getMessage(), e);
            return new ResultMsg(ResultCode.SYS_ERROR.getCode(), ResultCode.SYS_ERROR.getMsg());
        }
    }

    /**
     * 联通运营商续订回调函数
     *
     * @param callBackRequest 回调请求
     * @return {@link ResultMsg}
     */
    @PostMapping("/renew/callback")
    public ResultMsg renewFunction(@RequestBody CallBackRequest callBackRequest){
        try{
            return tDfOrderLogApplication.renewFunction(callBackRequest.getData(), callBackRequest.getSign());
        }catch (CoinPayException coinPayException){
            log.error(coinPayException.getMessage(), coinPayException);
            return new ResultMsg(ResultCode.SYS_BUSY.getCode(), coinPayException.getReason());
        }catch (ResultException resultException){
            log.error(resultException.getMessage(), resultException);
            return new ResultMsg(resultException.getCode(), resultException.getReason());
        }catch (Exception e){
            log.error(e.getMessage(), e);
            return new ResultMsg(ResultCode.SYS_ERROR.getCode(), ResultCode.SYS_ERROR.getMsg());
        }


    }


}
