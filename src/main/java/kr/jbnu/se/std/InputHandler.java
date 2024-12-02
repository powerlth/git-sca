package kr.jbnu.se.std;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import static kr.jbnu.se.std.Framework.gameState;

public class InputHandler implements KeyListener, MouseListener {
    private AudioManager audioManager;
    private GameStateManager gameStateManager;
    private Framework framework;

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
                framework.mainMenu.MouseClicked(e);  // 메인 메뉴에서 클릭 처리
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
                kr.jbnu.se.std.Shop2.handleShopMouseClick(e);
                break;
        }
    }
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}

    public void handlePauseKeyInput() {
        if (Framework.gameState == Framework.GameState.PLAYING) {
            Framework.gameState = Framework.GameState.PAUSED;
            System.out.println("Game Paused");
        }
    }

    public void handleResumeKeyInput() {
        if (Framework.gameState == Framework.GameState.PAUSED) {
            Framework.gameState = Framework.GameState.PLAYING;
            System.out.println("Game Resumed");
        }
    }

}
