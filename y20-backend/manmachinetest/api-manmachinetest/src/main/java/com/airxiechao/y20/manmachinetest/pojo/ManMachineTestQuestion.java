package com.airxiechao.y20.manmachinetest.pojo;

import java.util.Date;
import java.util.List;

public class ManMachineTestQuestion {

    private String token;
    private List<Point> detail;

    public ManMachineTestQuestion(List<Point> detail, String token) {
        this.token = token;
        this.detail = detail;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public List<Point> getDetail() {
        return detail;
    }

    public void setDetail(List<Point> detail) {
        this.detail = detail;
    }

    public static class TokenDetail{
        private String answer;
        private Date expireTime;

        public TokenDetail(String answer, Date expireTime) {
            this.answer = answer;
            this.expireTime = expireTime;
        }

        public String getAnswer() {
            return answer;
        }

        public void setAnswer(String answer) {
            this.answer = answer;
        }

        public Date getExpireTime() {
            return expireTime;
        }

        public void setExpireTime(Date expireTime) {
            this.expireTime = expireTime;
        }
    }

    public static class Point{
        private int x;
        private int y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }
    }
}
