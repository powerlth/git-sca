package kr.jbnu.se.std;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.Point;

public class Duck {

    public int x;
    public int y;
    private int speed;
    public int score;
    private BufferedImage duckImg;

    public static long timeBetweenDucks = Framework.secInNanosec / 2;
    public static long lastDuckTime = 0;

    public static int[][] duckLines = {
            {Framework.frameWidth, (int) (Framework.frameHeight * 0.60), -2, 20},
            {Framework.frameWidth, (int) (Framework.frameHeight * 0.65), -3, 30},
            {Framework.frameWidth, (int) (Framework.frameHeight * 0.70), -4, 40},
            {Framework.frameWidth, (int) (Framework.frameHeight * 0.78), -5, 50}
    };
    public static int nextDuckLines = 0;

    public Duck(int x, int y, int speed, int score, BufferedImage duckImg) {
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.score = score;
        this.duckImg = duckImg;
    }

    public void Update() {
        x += speed;
    }

    public void Draw(Graphics2D g2d) {
        g2d.drawImage(duckImg, x, y, null);
    }

    public boolean isHit(Point mousePosition) {
        int duckWidth = duckImg.getWidth();
        int duckHeight = duckImg.getHeight();

        return (mousePosition.x >= x && mousePosition.x <= x + duckWidth) &&
                (mousePosition.y >= y && mousePosition.y <= y + duckHeight);
    }
}
