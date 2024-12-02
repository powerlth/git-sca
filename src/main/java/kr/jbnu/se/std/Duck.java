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
    public static long timeBetweenDucks = Framework.SECINNANOSEC / 2;
    public static long lastDuckTime = 0;

    public static int[][] duckLines = {
            {Framework.frameWidth, (int) (Framework.frameHeight * 0.60), -2, 20},
            {Framework.frameWidth, (int) (Framework.frameHeight * 0.65), -3, 30},
            {Framework.frameWidth, (int) (Framework.frameHeight * 0.70), -4, 40},
            {Framework.frameWidth, (int) (Framework.frameHeight * 0.78), -5, 50}
    };
    public static int[][] FlyingduckLines = {
            {Framework.frameWidth, (int)(Framework.frameHeight * 0.0), -2, 40},
            {Framework.frameWidth, (int)(Framework.frameHeight * 0.10), -3, 50},
            {Framework.frameWidth, (int)(Framework.frameHeight * 0.20), -4, 60},
            {Framework.frameWidth, (int)(Framework.frameHeight * 0.30), -5, 70}
    };
    public static int nextDuckLines = 0;

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
}
