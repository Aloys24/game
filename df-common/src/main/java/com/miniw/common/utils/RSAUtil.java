package com.miniw.common.utils;

import com.alibaba.fastjson.JSON;
import com.miniw.common.base.ResultCode;
import com.miniw.common.constant.SignConstant;
import com.miniw.common.exception.ResultException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.codec.binary.Base64;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

/**
 * RSA加密验签工具
 *
 * @author luoquan
 * @date 2021/08/04
 */
@Slf4j
public class RSAUtil {

    /**
     * RSA最大加密明文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;

    /**
     * RSA最大解密密文大小
     */
    private static final int MAX_DECRYPT_BLOCK = 384;

    /**
     * 获取密钥对
     *
     * @return 密钥对
     */
    public static KeyPair getKeyPair() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        return generator.generateKeyPair();
    }

    /**
     * 获取私钥
     *
     * @param privateKey 私钥字符串
     * @return
     */
    public static PrivateKey getPrivateKey(String privateKey) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        byte[] decodedKey = Base64.decodeBase64(privateKey.getBytes());
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodedKey);
        return keyFactory.generatePrivate(keySpec);
    }

    /**
     * 获取公钥
     *
     * @param publicKey 公钥字符串
     * @return
     */
    public static PublicKey getPublicKey(String publicKey) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        byte[] decodedKey = Base64.decodeBase64(publicKey.getBytes());
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodedKey);
        return keyFactory.generatePublic(keySpec);
    }

    /**
     * 获取加密前字串
     * @param params 请求参数
     */
    public static String getSignContent(Map<String, String> params) {
        if (params == null || params.isEmpty())
            throw new ResultException(ResultCode.SYS_PARAM_ERROR);
        StringBuilder content = new StringBuilder();
        params.keySet().stream().sorted().forEach(_key -> {
            if (StringUtils.isNotBlank(params.get(_key)))
                content.append(_key).append("=").append(params.get(_key)).append("&");
        });
        return content.substring(0, content.length() - 1);
    }


    /**
     * RSA加密
     *
     * @param data 待加密数据
     * @param publicKey 公钥
     * @return
     */
    public static String encrypt(String data, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        int inputLen = data.getBytes().length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offset = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        while (inputLen - offset > 0) {
            if (inputLen - offset > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(data.getBytes(), offset, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data.getBytes(), offset, inputLen - offset);
            }
            out.write(cache, 0, cache.length);
            i++;
            offset = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        // 获取加密内容使用base64进行编码,并以UTF-8为标准转化成字符串
        // 加密后的字符串
        return new String(Base64.encodeBase64String(encryptedData));
    }

    /**
     * RSA解密
     *
     * @param data 待解密数据
     * @param privateKey 私钥
     * @return
     */
    public static String decrypt(String data, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] dataBytes = Base64.decodeBase64(data);
        int inputLen = dataBytes.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offset = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        while (inputLen - offset > 0) {
            if (inputLen - offset > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(dataBytes, offset, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(dataBytes, offset, inputLen - offset);
            }
            out.write(cache, 0, cache.length);
            i++;
            offset = i * MAX_DECRYPT_BLOCK;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        // 解密后的内容
        return new String(decryptedData, StandardCharsets.UTF_8);
    }

    /**
     * 签名
     *
     * @param data 待签名数据
     * @param privateKey 私钥
     * @return 签名
     */
    public static String sign(String data, PrivateKey privateKey) throws Exception {
        byte[] keyBytes = privateKey.getEncoded();
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey key = keyFactory.generatePrivate(keySpec);
        Signature signature = Signature.getInstance("MD5withRSA");
        signature.initSign(key);
        signature.update(data.getBytes());
        return new String(Base64.encodeBase64(signature.sign()));
    }

    /**
     * 验签
     *
     * @param srcData 原始字符串
     * @param publicKey 公钥
     * @param sign 签名
     * @return 是否验签通过
     */
    public static boolean verify(String srcData, PublicKey publicKey, String sign) throws Exception {
        byte[] keyBytes = publicKey.getEncoded();
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey key = keyFactory.generatePublic(keySpec);
        Signature signature = Signature.getInstance("MD5withRSA");
        signature.initVerify(key);
        signature.update(srcData.getBytes());
        return signature.verify(Base64.decodeBase64(sign.getBytes()));
    }


    public static void main(String[] args) {
        try {
            // 生成密钥对
            KeyPair keyPair = getKeyPair();
//            String privateKey = new String(Base64.encodeBase64(keyPair.getPrivate().getEncoded()));
//            String publicKey = new String(Base64.encodeBase64(keyPair.getPublic().getEncoded()));

            String uincomPrivateKey = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCxIhSNTfplzQUYyk80HKtg88t7l1jMUImg56uYDgsTzaa2NGa5E1YKSU7jv5wxW1rRd5AFxuIzTarlEsDKfdDclujGp00LUYMBYO2kBdcmP/P1/XLghB4qlT271BXQsJrgd2qIAC+4R7bQ3tev4OzW6Cu0ap5GSm4284529Kk8LANPmvP5PTtlQt1c4pLrSFhIX6UP7sZcVc/ZOMIlOSR7M+2wSt/Wu2ErNGjIHFM3iHRV3n/0QbgZYUpjhmg2FyfoDxayzxZpvUG9z4264Rj5VyNnhnJvAZmevpdCW97mHNu9jNPHluMSQ4wEyUEM2tAShAd65a+a2FFtekZjWg1xAgMBAAECggEBAJ/rROLOI+ME1jUrcZZ6y78cW/mZCnJrw9WhUypUg2u9n2WgqZ0t9+ARj0fPFhT5hWTXrQ+KH25yEHgAFk66iHafSEkRgl1VmhEEkYgkPboqwvaze6N3pESNuM57C7b9utYs+eEPSgX1uPeaOOBxEWJ0+gBIwdx0Fk1GVrjO6nuHP9N6e71CgJWNUk1WGN3aSGzkC28jEtFPlm1FIOmILWBmi5RUJwu4dO8lcQXJhALypODH1hcrC5Q7u+3eVn5oZM6EvjBhAHpLLhzvl2sjsL4HLw6R5+ytjcV+RjXID5uHeitpJgc7hD6JVxOBMoIA4vkRPz1A4ERMnxB476PxkIECgYEA4XHCtm150fNo3fGz4ZbP1lShMbX6IFygmy8+LAotQL0jON/lElqVeFpkW3uiCDmD+e1Ot1TnXH/5A1AaYU03u4f6gvQdqkb9JpMLcnQPDTE3GEinr9bvNgLzOK3Tc7Br/Sy5DaYLAv3AvXz1xIh351MXUspZRq77St9ap2XaaQcCgYEAySQQqdoirVI5W6bRw4T8JkPaum7wEi5bLXpew97Hq+ifZlVQXMjacxj4njXu93N3XEexd6GzpfOlrlIHE2Jwgibbni0LNKLoXBEGD4fqJ5p4dVvJEGkuhOS1ByEjvW925u397T4PohQs30wMOehEAzfuDe3h/BMg2may7059D8cCgYEAuYp31s4Y0VsD9o3DHqmRGfOCyrfbSKETKUTjZTlm7v36rAdQrx9QHsI+EDCbNSuo25ucCjRdSXqlRT26yFPVx4hs+zPP6mEMXZ/tbt/nt1LG5jx9jbFWJFyfMgmVmoQbDD2ve3UpYOfYpdrfJkWucPqommGgwhSnCoLySA0CiScCgYAhldsOcXT+2c7oBdfavNbZkNTx8lo1vvXMhzVRoV5XzxTmonIzu/n15AKHcFwBh48zomj4n9B+ckuE6v9xditxIAnlxIGtfVItJU6Mw1YO3NUmS3gsPFPfLs0nbpooJHeHIdAXkBhOKO5BLtKPzL+mhe1C+CiWzZ7buPBB0ypAuwKBgF10rtDc1PUgUq71t1YFXLN13ZsxBg6MGAcwOInYePbK4CwHnOOD1YNqz6mTE6zLY3/OSxfGNHNjLF9w5LZ1hUCYPO26TUbbM6EhrfdpJqDCBI1abt2WNtia7Iz3zjpnUzeWGIfwTNBM3wZxlkB/iHmzT6fXBsDT0DQziL4J1U5e";
            String uincomPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsSIUjU36Zc0FGMpPNByrYPPLe5dYzFCJoOermA4LE82mtjRmuRNWCklO47+cMVta0XeQBcbiM02q5RLAyn3Q3JboxqdNC1GDAWDtpAXXJj/z9f1y4IQeKpU9u9QV0LCa4HdqiAAvuEe20N7Xr+Ds1ugrtGqeRkpuNvOOdvSpPCwDT5rz+T07ZULdXOKS60hYSF+lD+7GXFXP2TjCJTkkezPtsErf1rthKzRoyBxTN4h0Vd5/9EG4GWFKY4ZoNhcn6A8Wss8Wab1Bvc+NuuEY+VcjZ4ZybwGZnr6XQlve5hzbvYzTx5bjEkOMBMlBDNrQEoQHeuWvmthRbXpGY1oNcQIDAQAB";

            String miniPrivateKey = SignConstant.MINIW_PRIVATE_KEY_PEM;
            String miniPublicKey = SignConstant.MINIW_PUBLIC_KEY_PEM;

//            System.out.println("私钥:" + privateKey);
//            System.out.println("公钥:" + publicKey);
            // RSA加密 待加密的文字内容
            Map<String, Object> params = new HashMap<>();
            params.put("code", 200);
            params.put("dfOrderNo", "DF757092014UNICOM20210817863275");
            params.put("phone", "18870064467");
            params.put("created", "2021-08-17 20:00");
            String jsonString = JSON.toJSONString(params);
//
//
            String encryptData = encrypt(jsonString, getPublicKey(miniPublicKey));
            System.out.println("加密前内容:" + jsonString);
            System.out.println("加密后内容:" + encryptData);
            // RSA解密
            String decryptData = decrypt(encryptData, getPrivateKey(miniPrivateKey));
            System.out.println("解密后内容:" + decryptData);

//            // RSA签名
            String sign = sign(jsonString, getPrivateKey(uincomPrivateKey));
            System.out.println(sign);
//            // RSA验签
            boolean result = verify(jsonString, getPublicKey(uincomPublicKey), sign);
            System.out.print("验签结果:" + result);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.print("加解密异常");
        }
    }


}
