package com.airxiechao.y20.common.verification;

import java.util.Random;

public class VerificationUtil {

    public static String randomVerificationCode(){
        StringBuilder sb = new StringBuilder();

        Random random = new Random();
        for(int i = 0; i < 6; ++i){
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}
