package kr.jbnu.se.std;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.Point;

public class Duck {

    private int basespeed;
    public int x;
    public int y;
    private double speed;
    public int score;
    private BufferedImage duckImg;
    private Game game;
    public long timeBetweenDucks = Framework.SECINNAMOSEC / 2;
    public long lastDuckTime = 0;
    private Framework framework;

    public int[][] duckLines = {
            {framework.frameWidth, (int) (framework.frameHeight * 0.60), -2, 20},
            {framework.frameWidth, (int) (framework.frameHeight * 0.65), -3, 30},
            {framework.frameWidth, (int) (framework.frameHeight * 0.70), -4, 40},
            {framework.frameWidth, (int) (framework.frameHeight * 0.78), -5, 50}
    };
    public int[][] FlyingduckLines = {
            {framework.frameWidth, (int)(framework.frameHeight * 0.0), -2, 40},
            {framework.frameWidth, (int)(framework.frameHeight * 0.10), -3, 50},
            {framework.frameWidth, (int)(framework.frameHeight * 0.20), -4, 60},
            {framework.frameWidth, (int)(framework.frameHeight * 0.30), -5, 70}
    };
    public int nextDuckLines = 0;

    public Duck(int x, int y, int speed, int score, BufferedImage duckImg, Game game) {
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.score = score;
        this.duckImg = duckImg;
        this.game = game;
        this.basespeed = speed;

    }

    public void Update() {
        if (game.getBulletTime().isActive()) {
            x += speed / 2;// 속도를 절반으로 감소
        } else {
            x += speed;// 정상 속도
        }
    }

    public void Draw(Graphics2D g2d) {
        g2d.drawImage(duckImg, x, y, null);
    }

    public boolean isHit(Point mousePosition) {
        if (duckImg == null) {
            System.err.println("Duck image is null");
            return false;
        }
        int duckWidth = duckImg.getWidth();
        int duckHeight = duckImg.getHeight();

        return (mousePosition.x >= x && mousePosition.x <= x + duckWidth) &&
                (mousePosition.y >= y && mousePosition.y <= y + duckHeight);
    }

    public void ResetDuckLine(){
        nextDuckLines = 0;
    }
    public void SyncDuckTime(){
        lastDuckTime = System.nanoTime();
    }
    public void ResetDuckTime(){
        lastDuckTime = 0;
    }
    public void plusDuckLine(){
        nextDuckLines++;
    }
}
