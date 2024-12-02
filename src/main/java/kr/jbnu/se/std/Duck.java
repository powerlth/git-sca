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
