package com.airxiechao.y20.manmachinetest.biz.process;

import com.airxiechao.axcboot.config.factory.ConfigFactory;
import com.airxiechao.axcboot.crypto.AesUtil;
import com.airxiechao.y20.manmachinetest.biz.api.IManMachineBiz;
import com.airxiechao.y20.manmachinetest.pojo.ManMachineTestQuestion;
import com.airxiechao.y20.manmachinetest.pojo.config.ManMachineTestConfig;
import com.airxiechao.y20.manmachinetest.pojo.constant.EnumManMachineTestQuestionType;
import com.alibaba.fastjson.JSON;
import kotlin.collections.ArrayDeque;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class ManMachineBizProcess implements IManMachineBiz {

    private static final String[] TYPES = {
            EnumManMachineTestQuestionType.LINE,
            EnumManMachineTestQuestionType.RECTANGLE,
            EnumManMachineTestQuestionType.CIRCLE,
            EnumManMachineTestQuestionType.TRIANGLE,
    };
    private static final int EXPIRE_MINUTES = 5;
    private static final int CANVAS_WIDTH = 100;
    private static final int CANVAS_HEIGHT = 100;

    private static final ManMachineTestConfig config = ConfigFactory.get(ManMachineTestConfig.class);

    @Override
    public ManMachineTestQuestion randomQuestion() throws Exception {
        String type = TYPES[new Random().nextInt(TYPES.length)];
        List<ManMachineTestQuestion.Point> detail = null;
        switch (type){
            case EnumManMachineTestQuestionType.LINE:
                detail = createLine();
                break;
            case EnumManMachineTestQuestionType.RECTANGLE:
                detail = createRectangle();
                break;
            case EnumManMachineTestQuestionType.CIRCLE:
                detail = createCircle();
                break;
            case EnumManMachineTestQuestionType.TRIANGLE:
                detail = createTriangle();
                break;
        }

        Date expireTime = createExpireTime();

        ManMachineTestQuestion.TokenDetail tokenDetail = new ManMachineTestQuestion.TokenDetail(type, expireTime);
        String token = AesUtil.encryptByPBKDF2(config.getQuestionTokenEncryptKey(), config.getQuestionTokenEncryptKey(), JSON.toJSONString(tokenDetail));

        return new ManMachineTestQuestion(detail, token);
    }

    @Override
    public boolean checkQuestion(String questionToken, String answer) throws Exception {
        ManMachineTestQuestion.TokenDetail tokenDetail = JSON.parseObject(AesUtil.decryptByPBKDF2(config.getQuestionTokenEncryptKey(), config.getQuestionTokenEncryptKey(), questionToken), ManMachineTestQuestion.TokenDetail.class);
        return tokenDetail.getExpireTime().after(new Date()) && tokenDetail.getAnswer().equals(answer);
    }

    private Date createExpireTime(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MINUTE, EXPIRE_MINUTES);
        return calendar.getTime();
    }

    private List<ManMachineTestQuestion.Point> createLine(){
        Random random = new Random();
        List<ManMachineTestQuestion.Point> samples = new ArrayDeque<>();

        int p0x = random.nextInt(CANVAS_WIDTH);
        int p0y = random.nextInt(CANVAS_HEIGHT);
        samples.add(new ManMachineTestQuestion.Point(p0x, p0y));

        int p1x = random.nextInt(CANVAS_WIDTH);
        int p1y = random.nextInt(CANVAS_HEIGHT);
        samples.add(new ManMachineTestQuestion.Point(p1x, p1y));

        int dx = p0x - p1x;
        int dy = p0y - p1y;

        for(int i = 0; i < 6; ++i){
            float ratio = random.nextFloat();
            samples.add(new ManMachineTestQuestion.Point(p1x + (int)(ratio*dx), p1y + (int)(ratio*dy)));
        }

        return samples;
    }

    private List<ManMachineTestQuestion.Point> createRectangle(){
        Random random = new Random();
        List<ManMachineTestQuestion.Point> samples = new ArrayDeque<>();

        int p0x = random.nextInt(CANVAS_WIDTH);
        int p0y = random.nextInt(CANVAS_HEIGHT);
        int w = random.nextInt(CANVAS_WIDTH / 2);
        int h = random.nextInt(CANVAS_HEIGHT / 2);
        samples.add(new ManMachineTestQuestion.Point(p0x, p0y));
        samples.add(new ManMachineTestQuestion.Point(p0x + w/2, p0y));

        int p1x = p0x + w;
        int p1y = p0y;
        samples.add(new ManMachineTestQuestion.Point(p1x, p1y));
        samples.add(new ManMachineTestQuestion.Point(p1x, p1y + h/2));

        int p2x = p1x;
        int p2y = p1y + h;
        samples.add(new ManMachineTestQuestion.Point(p2x, p2y));
        samples.add(new ManMachineTestQuestion.Point(p2x - w/2, p2y));

        int p3x = p0x;
        int p3y = p2y;
        samples.add(new ManMachineTestQuestion.Point(p3x, p3y));
        samples.add(new ManMachineTestQuestion.Point(p3x, p3y - h/2));

        return samples;
    }

    private List<ManMachineTestQuestion.Point> createCircle(){
        Random random = new Random();
        List<ManMachineTestQuestion.Point> samples = new ArrayDeque<>();

        int cx = random.nextInt(CANVAS_WIDTH);
        int cy = random.nextInt(CANVAS_HEIGHT);
        int r = 10 + random.nextInt(CANVAS_WIDTH / 4);

        for(int i = 0; i < 8; ++i){
            samples.add(new ManMachineTestQuestion.Point(cx + (int)(r*Math.cos(i*Math.PI/4)), (int)(cy + r*Math.sin(i*Math.PI/4))));
        }

        return samples;
    }

    private List<ManMachineTestQuestion.Point> createTriangle(){
        Random random = new Random();
        List<ManMachineTestQuestion.Point> samples = new ArrayDeque<>();

        int p0x = random.nextInt(CANVAS_WIDTH);
        int p0y = random.nextInt(CANVAS_HEIGHT);
        samples.add(new ManMachineTestQuestion.Point(p0x, p0y));

        int p1x = random.nextInt(CANVAS_WIDTH);
        int p1y = random.nextInt(CANVAS_HEIGHT);
        samples.add(new ManMachineTestQuestion.Point(p1x, p1y));

        int p2x = random.nextInt(CANVAS_WIDTH);
        int p2y = random.nextInt(CANVAS_HEIGHT);
        samples.add(new ManMachineTestQuestion.Point(p2x, p2y));

        samples.add(new ManMachineTestQuestion.Point(p0x + (p1x - p0x) / 3, p0y + (p1y - p0y) / 3));
        samples.add(new ManMachineTestQuestion.Point(p0x + (p1x - p0x) / 3 * 2, p0y + (p1y - p0y) / 3 * 2));
        samples.add(new ManMachineTestQuestion.Point(p1x + (p2x - p1x) / 2, p1y + (p2y - p1y) / 2));
        samples.add(new ManMachineTestQuestion.Point(p2x + (p0x - p2x) / 3, p2y + (p0y - p2y) / 3));
        samples.add(new ManMachineTestQuestion.Point(p2x + (p0x - p2x) / 3 * 2, p2y + (p0y - p2y) / 3 * 2));

        return samples;
    }
}
