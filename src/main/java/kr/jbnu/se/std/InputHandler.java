package kr.jbnu.se.std;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import static kr.jbnu.se.std.Framework.gameState;

public class InputHandler implements KeyListener, MouseListener {
    private AudioManager audioManager;
    private GameStateManager gameStateManager;
    private Framework framework;
    private Shop2 shop;
    private Game game;
    public InputHandler(Framework framework) {
        this.framework = framework;
        this.audioManager = framework.audioManager;
        this.gameStateManager = framework.gameStateManager;
    }
    public void keyPressed(KeyEvent e) {
        switch (gameState) {
            case GAMEOVER:
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    audioManager.stopBackgroundMusic();  // 게임 종료 시 배경음악 중지
                    System.exit(0);
                } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    gameStateManager.setCurrentState(Framework.GameState.MAIN_MENU);  // 스페이스바를 누르면 메인 메뉴로 돌아가기
                }
                break;
            case PLAYING:
                if (e.getKeyCode() == KeyEvent.VK_P)
                    gameStateManager.setCurrentState(Framework.GameState.PAUSED);
                else if (e.getKeyCode() == KeyEvent.VK_S)
                    gameStateManager.setCurrentState(Framework.GameState.SHOP);
                break;
            case MAIN_MENU:
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    audioManager.stopBackgroundMusic(); // 게임 종료 시 배경음악 중지
                    System.exit(0);
                } else if (e.getKeyCode() == KeyEvent.VK_P) {
                    handlePauseKeyInput(); // P 키로 일시정지
                }
                break;
            case PAUSED:
                if (e.getKeyCode() == KeyEvent.VK_R) {
                    handleResumeKeyInput(); // R 키로 게임 재개
                }
                break;
            case SHOP:
                if (e.getKeyCode() == KeyEvent.VK_S) {
                    gameStateManager.setCurrentState(Framework.GameState.PLAYING); // S 키로 상점 나가기
                }
                break;
            case DESTROYED: break;
            case OPTIONS: break;
            case VISUALIZING: break;
            case STARTING: break;
            case GAME_CONTENT_LOADING: break;
        }
        if (e.getKeyCode() == KeyEvent.VK_B) {  // 'B' 키를 누르면 불렛타임 활성화
            framework.game.activateBulletTime();  // 불렛타임 활성화
        }
    }

    public void keyReleased(KeyEvent e) {
        // Handle key released
    }

    public void keyTyped(KeyEvent e) {
        // Handle key typed
    }
    public void mouseClicked(MouseEvent e) {
        switch (gameState) {
            case STARTING:
                break;
            case VISUALIZING:
                break;
            case GAME_CONTENT_LOADING:
                break;
            case MAIN_MENU:
                handleMainMenuClick(e);  // 메인 메뉴에서 클릭 처리
                if (Framework.gameState == Framework.GameState.PLAYING) {
                    framework.newGame(framework.mainMenu.getSelectedStage());  // 선택한 스테이지에서 게임 시작
                    gameStateManager.setCurrentState(Framework.GameState.PLAYING);
                }
                break;
            case OPTIONS:
                break;
            case PLAYING:
                break;
            case GAMEOVER:
                break;
            case PAUSED:
                break;
            case DESTROYED:
                break;
            case SHOP:
                handleShopMouseClick(e, shop);
                break;
        }
    }

    public void handleMainMenuClick(MouseEvent e) {
        MainMenu mainMenu = framework.mainMenu;
        // 시작 버튼 클릭 처리
        if (mainMenu.getStartButtonPosition().contains(e.getPoint())) {
            gameStateManager.setCurrentState(Framework.GameState.PLAYING);
            framework.newGame(mainMenu.getSelectedStage());  // 선택된 스테이지로 게임 시작
        }

        // 스테이지 선택 버튼 클릭 처리
        Rectangle[] stageButtonPositions = mainMenu.getStageButtonPositions();
        for (int i = 0; i < stageButtonPositions.length; i++) {
            if (stageButtonPositions[i].contains(e.getPoint())) {
                mainMenu.setSelectedStage(i + 1);  // 스테이지 선택
                gameStateManager.setCurrentState(Framework.GameState.PLAYING);
                framework.newGame(mainMenu.getSelectedStage());
            }
        }
    }
    public void handleShopMouseClick(MouseEvent e, Shop2 shop){
        Point clickPoint = e.getPoint();
        Shop2.setShopMessage("");
        String[] items = Shop2.getshopItems();
        int[] prices = Shop2.getItemPrices();
        int selectedItem = Shop2.getSelectedItems();
        //System.out.println("Mouse clicked at: " + clickPoint);
        for (int i = 0; i < items.length; i++) {
            int x = 150 + i * 200; // 아이템의 X 좌표
            int y = 200; // 아이템의 Y 좌표

            // 아이템의 영역을 확인하여 클릭된 아이템을 선택
            if (clickPoint.x >= x && clickPoint.x <= x + 100 && clickPoint.y >= y && clickPoint.y <= y + 100) {
                Shop2.setSelectedItem(i);
                Shop2.setShopMessage("Selected item: " + items[i]);
                break;
            }
        }

        // 선택된 아이템을 구매
        selectedItem = Shop2.getSelectedItems();
        if (selectedItem != -1 && Money.getMoney() >= prices[selectedItem]) {
            Money.subtractMoney(prices[selectedItem]);
            Shop2.setShopMessage(items[selectedItem] + " purchased! Remaining money: " + Money.getMoney());
            game.setSelectedMenuImage(selectedItem);
        } else {
            Shop2.setShopMessage("Not enough money to buy ");
        }
    }
    public void mouseEntered(MouseEvent e) {
        //필요없음
    }
    public void mouseExited(MouseEvent e) {
        //필요없음
    }
    public void mousePressed(MouseEvent e) {
        //필요없음
    }
    public void mouseReleased(MouseEvent e) {
        //필요없음
    }

    public void handlePauseKeyInput() {
        if (Framework.gameState == Framework.GameState.PLAYING) {
            gameStateManager.setCurrentState(Framework.GameState.PAUSED);
            System.out.println("Game Paused");
        }
    }

    public void handleResumeKeyInput() {
        if (Framework.gameState == Framework.GameState.PAUSED) {
            gameStateManager.setCurrentState(Framework.GameState.PLAYING);
            System.out.println("Game Resumed");
        }
    }

}
