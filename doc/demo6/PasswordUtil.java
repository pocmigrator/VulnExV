package com.zyd.blog.util;

import com.zyd.blog.business.consts.CommonConst;

/**
 * @author: yadong.zhang
 * @date: 2017/12/15 17:03
 */
public class PasswordUtil {


    /**
     * AES 解密
     * @param encryptPassword
     *         加密后的密码
     * @param salt
     *         盐值，默认使用用户名就可
     * @return
     * @throws Exception
     */
    public static String decrypt(String encryptPassword, String salt) throws Exception {
        return AesUtil.decrypt(Md5Util.MD5(salt + CommonConst.ZYD_SECURITY_KEY), encryptPassword);
    }
}
