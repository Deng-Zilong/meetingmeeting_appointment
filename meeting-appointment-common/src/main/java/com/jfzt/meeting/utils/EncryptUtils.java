package com.jfzt.meeting.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author zilong.deng
 * @date 2024/05/17
 * @description MD5加盐加密工具类
 */
@Slf4j
public class EncryptUtils {

    /**
     * MD5盐值加密
     */
    public static String encrypt (String password) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        return bCryptPasswordEncoder.encode(password);
    }

    /**
     * 验证明文与密码是否匹配
     */
    public static Boolean match (String password, String encode) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        return bCryptPasswordEncoder.matches(password, encode);
    }

    /**
     * MD5加密（弃用）
     */
    public static String md5encrypt (String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        // 更新输入的字节数组
        md.update(password.getBytes());
        // 计算 MD5 哈希值
        byte[] digest = md.digest();
        // 将字节数组转换为十六进制字符串
        BigInteger no = new BigInteger(1, digest);
        StringBuilder hashText = new StringBuilder(no.toString(16));

        // 补充前导零，确保字符串长度为32位
        while (hashText.length() < 32) {
            hashText.insert(0, "0");
        }
        log.info("MD5加密后：{}", hashText);
        return hashText.toString();
    }

}
