package com.airxiechao.y20.test;

import com.airxiechao.axcboot.config.factory.ConfigFactory;
import com.airxiechao.y20.agent.pojo.config.AgentClientConfig;

public class ConfigTest {
    public static void main(String[] args){
        AgentClientConfig agentClientConfig = ConfigFactory.get(AgentClientConfig.class);
        System.out.println(agentClientConfig.getServerRestPort());
    }
}
