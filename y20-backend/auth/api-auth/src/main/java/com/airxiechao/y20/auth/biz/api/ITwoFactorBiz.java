package com.airxiechao.y20.auth.biz.api;

import com.airxiechao.y20.auth.pojo.TwoFactorPrincipal;

public interface ITwoFactorBiz {
    String createTwoFactorToken(Long userId);
    TwoFactorPrincipal extractTwoFactorPrincipal(String twoFactorToken) throws Exception;
    String generateTwoFactorSecret();
    boolean verifyTwoFactorCode(String secret, String code);
}
