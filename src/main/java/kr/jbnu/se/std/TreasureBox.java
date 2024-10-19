package kr.jbnu.se.std;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class TreasureBox {
    public int x;
    public int y;
    private int hitCount; // 오리와 닿은 횟수
    private boolean isActive; // 보물상자 활성화 상태
    private BufferedImage treasureImg;

    public TreasureBox(int x, int y, BufferedImage treasureImg) {
        this.x = x;
        this.y = y;
        this.treasureImg = treasureImg;
        this.hitCount = 0;
        this.isActive = true;

        // 이미지가 없을 경우 비활성화
        if (this.treasureImg == null) {
            System.out.println("Failed to load treasure image");
            isActive = false; // 이미지가 없으면 비활성화
        }
    }

    /**
     * 보물상자를 10번 맞으면 비활성화
     */
    public void Update() {
        if (hitCount >= 10) {
            isActive = false; // 10번 맞으면 비활성화
        }
    }

    /**
     * 보물상자를 그리기
     * @param g2d 그리기 위한 Graphics2D 객체
     */
    public void Draw(Graphics2D g2d) {
        if (isActive && treasureImg != null) {
            g2d.drawImage(treasureImg, x, y, null);
        }
    }

    /**
     * 오리가 보물상자를 맞췄는지 확인
     * @param duckX 오리의 x 좌표
     * @param duckY 오리의 y 좌표
     * @param duckWidth 오리의 너비
     * @param duckHeight 오리의 높이
     * @return 오리가 보물상자를 맞았는지 여부
     */
    public boolean isHitByDuck(int duckX, int duckY, int duckWidth, int duckHeight) {
        if (treasureImg == null) return false; // 이미지가 없으면 false 반환

        int width = treasureImg.getWidth();
        int height = treasureImg.getHeight();

        // 오리와 보물상자가 겹치는지 확인
        return (duckX + duckWidth >= x && duckX <= x + width) &&
                (duckY + duckHeight >= y && duckY <= y + height);
    }

    /**
     * 오리가 보물상자를 맞을 때마다 hitCount 증가
     */
    public void incrementHitCount() {
        if (isActive) {
            hitCount++;
            System.out.println("Treasure box hit by duck! Hit count: " + hitCount);
        }
    }

    /**
     * 보물상자가 활성 상태인지 여부를 반환
     * @return 활성 상태일 때 true 반환
     */
    public boolean isActive() {
        return isActive;
    }
}
