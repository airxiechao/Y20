package com.airxiechao.y20.tool;

import com.airxiechao.y20.sms.biz.process.sender.ISmsSender;
import com.airxiechao.y20.sms.biz.process.sender.tencent.TencentSmsSender;
import org.apache.commons.cli.*;

public class SendMonitorAlertSmsMain {
    public static void main(String[] args) {
        CommandLineParser parser = new DefaultParser( );
        Options options = new Options( );
        options.addRequiredOption("m", "mobile", true, "mobile");
        options.addRequiredOption("n", "name", true, "monitor name");

        CommandLine commandLine;
        try {
            commandLine = parser.parse(options, args);
        } catch (ParseException e) {
            new HelpFormatter().printHelp("send-monitor-alert-sms", options);
            return;
        }

        String mobile = commandLine.getOptionValue("m");
        String monitorName = commandLine.getOptionValue("n");
        ISmsSender smsSender = new TencentSmsSender();
        try {
            smsSender.sendMonitorAlert(mobile, monitorName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
