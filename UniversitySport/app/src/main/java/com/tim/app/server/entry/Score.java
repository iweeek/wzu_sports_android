package com.tim.app.server.entry;


import java.io.Serializable;

/**
 * 成绩
 */
public class Score implements Serializable {


    private static final long serialVersionUID = 6187447685293862071L;
    private String sportDesc;//运动描述
    private String scoreDesc; //成绩描述
    private int score;//得分

    public String getSportDesc() {
        return sportDesc;
    }

    public void setSportDesc(String sportDesc) {
        this.sportDesc = sportDesc;
    }

    public String getScoreDesc() {
        return scoreDesc;
    }

    public void setScoreDesc(String scoreDesc) {
        this.scoreDesc = scoreDesc;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
