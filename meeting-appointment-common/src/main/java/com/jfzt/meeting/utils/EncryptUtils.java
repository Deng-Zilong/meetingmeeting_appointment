package com.jfzt.meeting.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * @author zilong.deng
 * @date 2024/05/17
 * @description MD5加盐加密工具类
 */
public class EncryptUtils {

    /**
     * 加密
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

}
