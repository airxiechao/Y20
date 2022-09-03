package com.airxiechao.y20.auth.util;

import com.airxiechao.axcboot.config.factory.ConfigFactory;
import com.airxiechao.axcboot.crypto.AesUtil;
import com.airxiechao.axcboot.crypto.Md5Util;
import com.airxiechao.axcboot.crypto.ShaUtil;
import com.airxiechao.y20.auth.pojo.config.AuthConfig;

public class AuthUtil {

    private static AuthConfig authConfig = ConfigFactory.get(AuthConfig.class);

    public static String encrypt(String original) {
        try {
            return AesUtil.encryptByPBKDF2(authConfig.getAccessTokenEncryptKey(), authConfig.getAccessTokenEncryptKey(),original);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String decrypt(String encrypted) {
        try {
            return AesUtil.decryptByPBKDF2(authConfig.getAccessTokenEncryptKey(), authConfig.getAccessTokenEncryptKey(), encrypted);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String hash(String original) {
        return Md5Util.md5(original);
    }

    public static String hashPassword(String original) {
        StringBuilder sb = new StringBuilder();
        sb.append(original);

        // padding
        for(int i = 0; i < 50 - original.length(); ++i){
            sb.append("0");
        }

        return ShaUtil.sha1(sb.toString());
    }

}
