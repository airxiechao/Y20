package com.airxiechao.y20.auth.biz.process;

import com.airxiechao.axcboot.util.UuidUtil;
import com.airxiechao.y20.auth.biz.api.ITwoFactorBiz;
import com.airxiechao.y20.auth.pojo.TwoFactorPrincipal;
import com.airxiechao.y20.auth.util.AuthUtil;
import com.alibaba.fastjson.JSON;
import dev.samstevens.totp.code.CodeGenerator;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;

import java.util.Calendar;
import java.util.Date;

public class TwoFactorBizProcess implements ITwoFactorBiz {

    @Override
    public String createTwoFactorToken(Long userId) {
        Date beginTime = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(beginTime);
        cal.add(Calendar.MINUTE, 5);
        Date endTime = cal.getTime();

        TwoFactorPrincipal twoFactorPrincipal = new TwoFactorPrincipal();
        twoFactorPrincipal.setTwoFactorUuid(UuidUtil.random());
        twoFactorPrincipal.setTwoFactorUserId(userId);
        twoFactorPrincipal.setTwoFactorBeginTime(beginTime);
        twoFactorPrincipal.setTwoFactorEndTime(endTime);

        String twoFactorToken = AuthUtil.encrypt(JSON.toJSONString(twoFactorPrincipal));
        return twoFactorToken;
    }

    @Override
    public TwoFactorPrincipal extractTwoFactorPrincipal(String twoFactorToken) throws Exception {
        String token;
        try {
            token = AuthUtil.decrypt(twoFactorToken);
        } catch (Exception e) {
            throw new Exception("invalid two factor token");
        }
        TwoFactorPrincipal twoFactorPrincipal = JSON.parseObject(token, TwoFactorPrincipal.class);

        if(twoFactorPrincipal.getTwoFactorEndTime().before(new Date())){
            throw new Exception("two factor token expired");
        }

        return twoFactorPrincipal;
    }

    @Override
    public String generateTwoFactorSecret(){
        SecretGenerator secretGenerator = new DefaultSecretGenerator();
        String secret = secretGenerator.generate();
        return secret;
    }

    @Override
    public boolean verifyTwoFactorCode(String secret, String code){
        TimeProvider timeProvider = new SystemTimeProvider();
        CodeGenerator codeGenerator = new DefaultCodeGenerator();
        DefaultCodeVerifier verifier = new DefaultCodeVerifier(codeGenerator, timeProvider);
        verifier.setTimePeriod(30);

        boolean passed = verifier.isValidCode(secret, code);
        return passed;
    }
}
