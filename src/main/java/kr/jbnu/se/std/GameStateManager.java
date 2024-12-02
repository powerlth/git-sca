package kr.jbnu.se.std;

import java.awt.*;
import java.awt.Toolkit;
import static kr.jbnu.se.std.Framework.SECINNANOSEC;

public class GameStateManager {
    private int gameState;
    private Framework.GameState currentState;
    private AudioManager audioManager;
    private long gameTime;
    private long lastTime;
    private Game game;
    private Framework framework;
    private long visualizingTime = 0, lastVisualizingTime = System.nanoTime();

    public GameStateManager() {
        this.currentState = Framework.GameState.STARTING;  // Default starting state
    }
    public GameStateManager(Framework framework) {
        this.framework = framework;
        this.currentState = Framework.GameState.STARTING;
    }

    public void setCurrentState(Framework.GameState newState){
        System.out.println("Transitioning from " + currentState + " to " + newState);
        if (newState == null) {
            throw new IllegalArgumentException("New state cannot be null");
        }
        transitionToState(newState);
        this.currentState = newState;
        //updateState();
    }

    public synchronized void updateState() {
        switch (currentState) {
            case MAIN_MENU:
                handleMainMenuLogic();
                break;
            case PLAYING:
                game.LoadContent();
                game.Initialize();
                handleGameplayLogic();
                break;
            case PAUSED:
                handlePauseLogic();
                break;
            case GAMEOVER:
                //...
                break;
            case OPTIONS:
                //...
                break;
            case STARTING:
                Initialize();
                LoadContent();
                setCurrentState(Framework.GameState.MAIN_MENU);
                break;
            case VISUALIZING:
                handleVisualizingLogic();
                break;
            case GAME_CONTENT_LOADING:
                //...
                break;
            // Additional cases as needed
            case DESTROYED:
                break;
            case SHOP:
                break;
        }
    }

    private synchronized void transitionToState(Framework.GameState newState) {
        // Handle logic that should run during state transitions
        // For example, stop music when leaving the main menu:
        if (newState != Framework.GameState.MAIN_MENU && currentState == Framework.GameState.MAIN_MENU) {
            audioManager.stopBackgroundMusic();
        }
    }

    public void getGameState(Framework.GameState state) {
        gameState = state.ordinal();
    }

    private void handleMainMenuLogic() {
        // Logic to execute when in the main menu
        framework.setCursor(Cursor.getDefaultCursor());
    }

    private void handleGameplayLogic() {
        // Logic to execute when in gameplay
        System.out.println("Gameplay logic");
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Image cursorImage = toolkit.getImage(""); // 빈 이미지 사용
        Point cursorHotSpot = new Point(0, 0);
        Cursor invisibleCursor = toolkit.createCustomCursor(cursorImage, cursorHotSpot, "InvisibleCursor");
        framework.setCursor(invisibleCursor);  // 게임 중일 때 커서 숨기기

        gameTime += System.nanoTime() - lastTime;
        game.UpdateGame(gameTime, framework.mousePosition());
        lastTime = System.nanoTime();
    }

    private void handlePauseLogic() {
        // Logic to execute when paused
    }

    private void handleVisualizingLogic() {
        if (framework.getWidth() > 1 && visualizingTime > SECINNANOSEC) {
            framework.setFrameSize(framework.getWidth(),framework.getHeight());
            setCurrentState(Framework.GameState.STARTING);
        } else {
            visualizingTime += System.nanoTime() - lastVisualizingTime;
            lastVisualizingTime = System.nanoTime();
        }
    }

    private void Initialize() {
        AudioManager.playBackgroundMusic("/background_music.wav");
    }

    private void LoadContent() {
        GraphicsManager.loadImage();
    }
}
