package kr.jbnu.se.std;

import java.awt.*;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.event.MouseEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.net.URL;

public class MainMenu {
    private JButton startButton;
    private JButton[] stageButtons; // 스테이지 선택 버튼 배열
    private int selectedStage = 1;
    private Image backgroundImage;// 선택된 스테이지

    public MainMenu() {
        try {
            URL imageUrl = this.getClass().getResource("/images/menu_background.jpg"); // 이미지 경로 설정
            backgroundImage = ImageIO.read(imageUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 게임 시작 버튼
        startButton = new JButton("Start Game");

        // 스테이지 선택 버튼
        stageButtons = new JButton[5];
        for (int i = 0; i < 5; i++) {
            stageButtons[i] = new JButton("Stage " + (i + 1));
            final int stage = i + 1;
            stageButtons[i].addActionListener(e -> {
                selectedStage = stage; // 선택된 스테이지 설정
                System.out.println("Selected Stage: " + selectedStage);
            });
        }
    }

    // 직접 draw 메서드를 구현
    public void draw(Graphics2D g2d) {
        if (backgroundImage != null) {
            g2d.drawImage(backgroundImage, 0, -20, Framework.frameWidth, Framework.frameHeight, null);
        }
        // 화면 크기 가져오기
        int frameWidth = Framework.frameWidth;
        int frameHeight = Framework.frameHeight;

        GraphicsUtils.setCommonGraphicsSettings(g2d);
        g2d.drawString("High Score: " + Framework.highScore, Framework.frameWidth / 2 - -100, Framework.frameHeight / 2 - 160);


        // 버튼 크기
        int buttonWidth = 200;
        int buttonHeight = 50;

        // 중앙 위치 계산
        int centerX = frameWidth / 2 - buttonWidth / 2;
        int startY = frameHeight / 2 - 150; // 버튼들이 중간에 모이도록 위치 설정

        // 시작 버튼 위치 설정 및 그리기
        startButton.setBounds(centerX, startY, buttonWidth, buttonHeight);
        g2d.setColor(Color.GREEN);
        g2d.fillRect(startButton.getX(), startButton.getY(), startButton.getWidth(), startButton.getHeight());
        g2d.setColor(Color.BLACK);
        g2d.drawString("Start Game", startButton.getX() + 40, startButton.getY() + 30);

        // 스테이지 선택 버튼들 위치 설정 및 그리기
        for (int i = 0; i < stageButtons.length; i++) {
            int buttonY = startY + (i + 1) * 60; // 각 버튼의 Y좌표 계산
            stageButtons[i].setBounds(centerX, buttonY, buttonWidth, buttonHeight);
            g2d.setColor(Color.ORANGE);
            g2d.fillRect(stageButtons[i].getX(), stageButtons[i].getY(), stageButtons[i].getWidth(), stageButtons[i].getHeight());
            g2d.setColor(Color.BLACK);
            g2d.drawString(stageButtons[i].getText(), stageButtons[i].getX() + 40, stageButtons[i].getY() + 30);
        }
    }

    public int getSelectedStage() {
        return selectedStage;
    }

    public void MouseClicked(MouseEvent e) {
        // 시작 버튼 클릭 처리
        if (new Rectangle(startButton.getX(), startButton.getY(), startButton.getWidth(), startButton.getHeight()).contains(e.getPoint())) {
            Framework.gameState = Framework.GameState.PLAYING;
            Framework.selectedStage = selectedStage; // 선택된 스테이지로 게임 시작
        }

        // 스테이지 버튼 클릭 처리
        for (int i = 0; i < stageButtons.length; i++) {
            if (new Rectangle(stageButtons[i].getX(), stageButtons[i].getY(), stageButtons[i].getWidth(), stageButtons[i].getHeight()).contains(e.getPoint())) {
                selectedStage = i + 1; // 선택된 스테이지 설정
                Framework.gameState = Framework.GameState.PLAYING;
                Framework.selectedStage = selectedStage; //
                System.out.println("Selected Stage: " + selectedStage);
            }
        }
    }
}
