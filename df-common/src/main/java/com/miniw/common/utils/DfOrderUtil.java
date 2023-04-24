package com.miniw.common.utils;

import cn.hutool.core.date.DateUtil;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 免流订单号工具类
 * <p>
 * 订单号生成规则：前缀 + mini号 + 时间戳
 * eg： DF_10001_1627375467
 * </p>
 *
 * @author luoquan
 * @date 2021/07/27
 */
public class DfOrderUtil {

    private static final String PAYCOIN_PREFIX = "DFPAY";
    private static final String DFORDER_PREFIX = "DF";
    private static AtomicInteger ATOMICINTEGER = new AtomicInteger();
    private static final Set<Integer> RANDOMSET = new HashSet<>();

    /**
     * 生成订单号
     * <p>
     *     规则： 前缀 + 迷你号 + 运营商类型 + 年月日 + 随机六位数
     *      eg: DF100002UNICOM20210804379532
     *
     * </p>
     *
     *
     * @param uin  迷你号
     * @param isp  运营商类型（1-中国移动，2-中国联通，3-中国电信（预留）
     * @return {@link String}
     */
    public static synchronized String generateDfOrder(String uin, String isp) {
        // TODO
        isp = "UNICOM";
        StringBuilder orderNo = new StringBuilder();
        final String today = DateUtil.today().replaceAll("-", "");
        orderNo.append(DFORDER_PREFIX).append(uin).append(isp).append(today);
            do {
                // 随机6位数值
                int x = ThreadLocalRandom.current().nextInt(999999);
                // 未重复
                if (RANDOMSET.add(x)) {
                    if (x < 100000)
                        orderNo.append(x + 100000);
                    else
                        orderNo.append(x);
                    break;
                }
                // 重复十次后重置Set、计数器
                if (ATOMICINTEGER.incrementAndGet() >= 10) {
                    ATOMICINTEGER = new AtomicInteger(0);
                    RANDOMSET.clear();
                }
            } while (true);
            return orderNo.toString();
    }


    /**
     * 生成充值服transaction_id
     * <p>
     *     规则： 前缀 + 迷你号 + 运营商类型 + 年月日 + 随机六位数
     *      eg: DFPAY100002UNICOM20210804379532
     *
     * </p>
     *
     *
     * @param uin  迷你号
     * @param isp  运营商类型（1-中国移动，2-中国联通，3-中国电信（预留）
     * @return {@link String}
     */
    public static synchronized String generatePayCoinOrderNo(String uin, String isp) {
        // TODO
        isp = "UNICOM";
        StringBuilder orderNo = new StringBuilder();
        final String today = DateUtil.today().replaceAll("-", "");
        orderNo.append(PAYCOIN_PREFIX).append(uin).append(isp).append(today);
        do {
            // 随机6位数值
            int x = ThreadLocalRandom.current().nextInt(999999);
            // 未重复
            if (RANDOMSET.add(x)) {
                if (x < 100000)
                    orderNo.append(x + 100000);
                else
                    orderNo.append(x);
                break;
            }
            // 重复十次后重置Set、计数器
            if (ATOMICINTEGER.incrementAndGet() >= 10) {
                ATOMICINTEGER = new AtomicInteger(0);
                RANDOMSET.clear();
            }
        } while (true);
        return orderNo.toString();
    }


//    public static void main(String[] args) {
//        System.out.println(generateDfOrder("1001", "UNICOM"));
//
//
//    }



}
