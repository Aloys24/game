package com.miniw.web.application.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.miniw.common.base.ResultCode;
import com.miniw.common.base.ResultMsg;
import com.miniw.persistence.model.TDfOrderLog;
import com.miniw.persistence.service.TDfOrderLogService;
import com.miniw.web.application.CsBackstageApplication;
import com.miniw.web.param.req.DetailQueryRequest;
import com.miniw.web.param.vo.DfStatusDetailVO;
import com.miniw.web.param.vo.DfStatusVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 客服后台
 *
 * @author luoquan
 * @date 2021/08/10
 */
@Slf4j
@Service
public class CsBackstageApplicationImpl implements CsBackstageApplication {

    @Resource
    private TDfOrderLogService tDfOrderLogService;


    /**
     * 获取迷你号免流卡运营商状态
     *
     * @param uin 迷你号
     * @return {@link ResultMsg}
     */
    @Override
    public ResultMsg getDfStatusByUin(String uin) {
        ResultMsg result = new ResultMsg(ResultCode.SYS_SUCCESS.getCode(), ResultCode.SYS_SUCCESS.getMsg());
        final List<TDfOrderLog> list = Optional.ofNullable(tDfOrderLogService.lambdaQuery().eq(TDfOrderLog::getUin, uin).list()).orElse(new ArrayList<>());
        List<DfStatusVO.UinStatus> statusList = new ArrayList<>();
        list.forEach(tDfOrderLog -> {
            DfStatusVO.UinStatus uinStatus = new DfStatusVO.UinStatus();
            uinStatus.setIsp(DfStatusVO.ispConvertTo(tDfOrderLog.getIsp()));
            uinStatus.setPhone(tDfOrderLog.getPhone());
            statusList.add(uinStatus);
        });
        result.addData("DfStatusVO", statusList);
        return result;
    }

    /**
     * 免流套餐详情查询
     *
     * @param request 请求
     * @return {@link List<TDfOrderLog>}
     */
    @Override
    public List<DfStatusDetailVO> getDfStatusDetail(DetailQueryRequest request) {
        DateTime start = null, end = null;
        Integer isp = null;
        if(StringUtils.isNotBlank(request.getStartTime())){
            start = DetailQueryRequest.convertToCoinCreated(request.getStartTime());
        }
        if(StringUtils.isNotBlank(request.getEndTime())){
            end = DetailQueryRequest.convertToCoinCreated(request.getEndTime());
        }

        if(StringUtils.isNotBlank(request.getIsp())){
            isp = DetailQueryRequest.ispConvertTo(request.getIsp());
        }

        List<TDfOrderLog> statusDetail = tDfOrderLogService.getDfStatusDetail(request.getUin(), request.getPhone(), isp, start, end);

        return statusDetail.stream().map(orderLog -> {
            DfStatusDetailVO vo = new DfStatusDetailVO();
            vo.setUin(orderLog.getUin());
            vo.setPhone(orderLog.getPhone());
            vo.setIsp(String.valueOf(orderLog.getIsp()));
            vo.setLoginChanel(orderLog.getLoginChannel());
            vo.setLoginVersion(orderLog.getVersion());
            vo.setCoinCreated(DateUtil.format(orderLog.getCoinCreated(), "yyyy-MM-dd HH:mm"));
            vo.setCreated(DateUtil.format(orderLog.getCreated(), "yyyy-MM-dd HH:mm"));
            vo.setAddNum(orderLog.getAddNum());
            vo.setCountry(orderLog.getCountry());
            return vo;
        }).collect(Collectors.toList());
    }
}
