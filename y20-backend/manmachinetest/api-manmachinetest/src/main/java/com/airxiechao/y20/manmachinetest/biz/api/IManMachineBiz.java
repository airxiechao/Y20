package com.airxiechao.y20.manmachinetest.biz.api;

import com.airxiechao.axcboot.core.annotation.IBiz;
import com.airxiechao.y20.manmachinetest.pojo.ManMachineTestQuestion;

@IBiz
public interface IManMachineBiz {

    ManMachineTestQuestion randomQuestion() throws Exception;
    boolean checkQuestion(String questionToken, String answer) throws Exception;
}
