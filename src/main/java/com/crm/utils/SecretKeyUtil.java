package com.crm.utils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.UUID;

/**
 * @author flobby
 */
public class SecretKeyUtil {

    // 服务端私钥盐，用于生成 SK（请务必保密）
    private static final String SECRET_SEED = "vact-project";

    /**
     * 生成一个 AK：UUID（去除中划线）
     */
    public static String generateAk() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 根据 AK 生成固定的 16 字节 SK（Base64 编码字符串）
     */
    public static String generateSk(String ak) throws Exception {
        Mac hmacSha256 = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(SECRET_SEED.getBytes(), "HmacSHA256");
        hmacSha256.init(secretKey);
        byte[] hmac = hmacSha256.doFinal(ak.getBytes());

        // 截取前16字节作为SM4的密钥
        byte[] sm4Key = new byte[8];
        System.arraycopy(hmac, 0, sm4Key, 0, 8);

        return toHex(sm4Key);
    }

    public static String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }


    /**
     * 示例
     */
    public static void main(String[] args) throws Exception {
        String ak = generateAk();
        String sk = generateSk(ak);

        System.out.println("AK: " + ak);
        System.out.println("SK: " + sk);
    }
}
