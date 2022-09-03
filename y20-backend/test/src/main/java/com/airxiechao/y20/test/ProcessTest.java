package com.airxiechao.y20.test;

import com.airxiechao.y20.util.ProcessUtil;

public class ProcessTest {

    public static void main(String[] args) throws Exception {
        ProcessUtil.startProcess(new String[]{"cmd", "/C", "dir"}, "d:/test", null);
    }
}
