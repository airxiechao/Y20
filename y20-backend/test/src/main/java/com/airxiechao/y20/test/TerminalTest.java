package com.airxiechao.y20.test;

import com.airxiechao.y20.util.ProcessUtil;

import java.io.*;

public class TerminalTest {

    public static void main(String[] args){
        String[] cmd = { "cmd.exe" };
        try {
            Process process = ProcessUtil.startPtyProcess(cmd, ".", null);
            OutputStream os = process.getOutputStream();
            InputStream is = process.getInputStream();

//            new Thread(()->{
//                try {
//                    StreamUtil.readStringInputStream(is, 100, Charset.forName("UTF-8"), (log)->{
//                        System.out.print(log);
//                    });
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }).start();
//
//            new Thread(()->{
//
//                try {
//
//                    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
//                    PrintWriter pw = new PrintWriter(new OutputStreamWriter(os));
//                    while(true){
//                        String line = br.readLine();
//                        pw.println(line);
//                        pw.flush();
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }).start();

            Thread t = new Thread(()->{
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("----");
                process.destroy();
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("+++");
            });
            t.setDaemon(true);
            t.start();

            System.out.println("111");
            process.waitFor();
            System.out.println("222");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
