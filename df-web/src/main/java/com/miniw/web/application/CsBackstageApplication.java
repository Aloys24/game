package com.miniw.web.application;


import com.miniw.common.base.ResultMsg;
import com.miniw.persistence.model.TDfOrderLog;
import com.miniw.web.param.req.DetailQueryRequest;
import com.miniw.web.param.vo.DfStatusDetailVO;

import java.util.List;

/**
 * 客服后台
 *
 * @author luoquan
 * @date 2021/08/03
 */
public interface CsBackstageApplication {

    /**
     * 获取迷你号免流卡运营商状态
     *
     * @param uin 迷你号
     * @return {@link ResultMsg}
     */
    ResultMsg getDfStatusByUin(String uin);


    /**
     * 免流套餐详情查询
     *
     * @param request 请求
     * @return {@link List<TDfOrderLog>}
     */
    List<DfStatusDetailVO> getDfStatusDetail(DetailQueryRequest request);
}
