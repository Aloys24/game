//package com.miniw.common.utils;
//
//import cn.hutool.core.codec.Base64;
//import com.miniw.common.exception.ResultException;
//import com.miniw.common.vo.ResultCode;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//
//import java.nio.charset.StandardCharsets;
//import java.security.*;
//import java.security.spec.InvalidKeySpecException;
//import java.security.spec.PKCS8EncodedKeySpec;
//import java.security.spec.X509EncodedKeySpec;
//import java.util.Map;
//
//@Slf4j
//public class SignUtil {
//
//    /**
//     * RSA2对应的真实非对称加密算法名称
//     */
//    public static final String RSA = "RSA";
//
//    /**
//     * RSA2对应的真实签名算法名称
//     */
//    public static final String SHA_256_WITH_RSA = "SHA256WithRSA";
//    /**
//     * RSA2对应的真实签名算法名称
//     */
//    public static final String PRIVATE_KEY = "SHA256WithRSA";
//
//    /**
//     * 获取加密前字串
//     * @param params 请求参数
//     */
//    public static String getSignContent(Map<String, String> params) {
//        if (params == null || params.isEmpty())
//            throw new ResultException(ResultCode.SYS_PARAM_ERROR);
//        StringBuilder content = new StringBuilder();
//        params.keySet().stream().sorted().forEach(_key -> {
//            if (StringUtils.isNotBlank(params.get(_key)))
//                content.append(_key).append("=").append(params.get(_key)).append("&");
//        });
//        return content.substring(0, content.length() - 1);
//    }
//
//    /**
//     * 验证签名
//     * @param params       待验签的内容
//     * @param sign         签名值的Base64串
//     * @param publicKeyPem SDK公钥
//     * @return true：验证成功；false：验证失败
//     */
//    public static boolean verify(Map<String, String> params, String sign, String publicKeyPem) {
//        String content = getSignContent(params);
//        try {
//            KeyFactory keyFactory = KeyFactory.getInstance(RSA);
//            byte[] encodedKey = Base64.decode(publicKeyPem);
//            PublicKey publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
//
//            Signature signature = Signature.getInstance(SHA_256_WITH_RSA);
//            signature.initVerify(publicKey);
//            signature.update(content.getBytes(StandardCharsets.UTF_8));
//            return signature.verify(Base64.decode(sign));
//        } catch (Exception e) {
//            String errorMessage = "验签遭遇异常，content=" + content + " sign=" + sign +
//                    " publicKey=" + publicKeyPem + " reason=" + e.getMessage();
//            log.error(errorMessage, e);
//            throw new RuntimeException(errorMessage, e);
//        }
//    }
//
//    /**
//     * 获取签名
//     * @param params        待签名的内容
//     * @param privateKeyPem 私钥
//     * @return 签名值的Base64串
//     */
//    public static String sign(Map<String, String> params, String privateKeyPem) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, SignatureException {
//        String content = getSignContent(params);
//        try {
//            byte[] encodedKey = Base64.decode(privateKeyPem);
//            PrivateKey privateKey = KeyFactory.getInstance(RSA).generatePrivate(new PKCS8EncodedKeySpec(encodedKey));
//
//            Signature signature = Signature.getInstance(SHA_256_WITH_RSA);
//            signature.initSign(privateKey);
//            signature.update(content.getBytes(StandardCharsets.UTF_8));
//            byte[] signed = signature.sign();
//            return new String(Base64.encode(signed));
//
//        } catch (Exception e) {
//            log.error("签名异常 {}， {}， {}", content, PRIVATE_KEY, e.getMessage());
//            throw e;
//        }
//    }
//
//
////    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, InvalidKeyException {
////        Map<String, String> params = new HashMap<>();
////        params.put("name", "24");
////        params.put("address", "shenzheng");
////        // 传入公钥 然后获取到运营商私钥后 进行sign
////        final String sign = sign(params, "MIIBVQIBADANBgkqhkiG9w0BAQEFAASCAT8wggE7AgEAAkEAxP0nTIr55q0jA3kg\n" +
////                "bBSyNSFh2cvEBj82oSEcIXaN9R9g2aHSszDXMMZlGZlz2qhLeTtEIY30BWqoulYI\n" +
////                "0xc4xwIDAQABAkEAjDHnMDCQnj2Gd8hS7Njl4644Z7r/bbq7dH2N+brYWSSVI86u\n" +
////                "xcv4jkSr87NFRaUtgBhzc7pn2F/fXD+rKFx88QIhAPORBmSMeBLUPfO2xtGVP2YQ\n" +
////                "dTCcueF85/eAfty9m6xTAiEAzwtxaABaLJ8zso5JMZAYqxG8TErIFm0Ga6bOS31X\n" +
////                "Ez0CIQDQKsbX8OY+0X8RaMmMHo7M4IwIPCrx+RsYgY3rg3IlWQIgWmtn6oCE4Qnh\n" +
////                "3O/QvUltzH/hWPwrp8eTTz44x7UEaw0CIE4uf5JeqOltqAPkCBCtSXC+SAn3w+X7\n" +
////                "kBXcu+yD2Dem\n");
////        // 通过私钥加密
////        System.out.println(sign);
////
////
////        // 模拟服务端，用公钥进行验证
////        System.out.println(verify(params, sign, "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBANoY2Bn2EZ+WAQ1PPvrERgCdpBqt3mu0\n" +
////                "Y61CcTOKqAYMoDrHyiA9QL4GIIp84TE+Jf6rSTyUrUhBJLBKPNkXrj0CAwEAAQ=="));
////
////    }
//
//
//}