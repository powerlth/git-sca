package kr.jbnu.se.std;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import javax.sound.sampled.Clip;


public class Framework extends Canvas {

    public static int highScore = 0;
    protected transient MainMenu mainMenu;
    public static int frameWidth;
    public static int frameHeight;
    public static final long SECINNANOSEC = 1000000000L;
    public static final long MILISEC_IN_NANOSEC = 1000000L;
    private final int GAME_FPS = 60;
    private final long GAME_UPDATE_PERIOD = SECINNANOSEC / GAME_FPS;
    public static enum GameState {STARTING, VISUALIZING, GAME_CONTENT_LOADING, MAIN_MENU, OPTIONS, PLAYING, GAMEOVER, PAUSED, DESTROYED, SHOP}
    public static GameState gameState;
    transient Game game;
    transient private Clip backgroundMusicClip;
    transient AudioManager audioManager = new AudioManager();
    private transient GraphicsManager graphicsManager = new GraphicsManager();
    transient GameStateManager gameStateManager = new GameStateManager();
    private transient InputHandler inputHandler = new InputHandler(this);

    public static void setFrameSize(int width, int height) {
        frameWidth = width;
        frameHeight = height;
    }
    public static void setHighScore(int newScore) {
        if (newScore > highScore) {
            Framework.highScore = newScore;
        }
    }

    public Framework() {
        super();
        this.addMouseListener(inputHandler);
        this.addKeyListener(inputHandler);
        this.audioManager = new AudioManager();
        this.graphicsManager = new GraphicsManager();
        this.gameStateManager = new GameStateManager(this);
        this.inputHandler = new InputHandler(this);
        this.mainMenu = new MainMenu();
        gameState = GameState.VISUALIZING;
        gameStateManager.setCurrentState(GameState.VISUALIZING);

        //We start game in new thread.
        Thread gameThread = new Thread() {
            @Override
            public void run(){
                GameLoop();
            }
        };
        gameThread.start();
    }

    private void GameLoop() {
        // This two variables are used in VISUALIZING state of the game. We used them to wait some time so that we get correct frame/window resolution.
        long visualizingTime = 0, lastVisualizingTime = System.nanoTime();
        long beginTime, timeTaken, timeLeft;

        while (gameState != GameState.DESTROYED) {
            beginTime = System.nanoTime();
            /*switch (gameState) {
                case MAIN_MENU:
                    setCursor(Cursor.getDefaultCursor());  // 메인 메뉴에서는 기본 커서 표시
                    // 메인 메뉴 로직 (추가적으로 필요한 경우)
                    break;
                case PLAYING:
                    Toolkit toolkit = Toolkit.getDefaultToolkit();
                    Image cursorImage = toolkit.getImage(""); // 빈 이미지 사용
                    Point cursorHotSpot = new Point(0, 0);
                    Cursor invisibleCursor = toolkit.createCustomCursor(cursorImage, cursorHotSpot, "InvisibleCursor");
                    setCursor(invisibleCursor);  // 게임 중일 때 커서 숨기기

                    gameTime += System.nanoTime() - lastTime;
                    game.UpdateGame(gameTime, mousePosition());
                    lastTime = System.nanoTime();
                    break;
                case PAUSED:
                    // 일시정지 상태에서는 아무 작업도 하지 않음
                    break;
                case GAMEOVER:
                    //...
                    break;
                case OPTIONS:
                    //...
                    break;
                case GAME_CONTENT_LOADING:
                    //...
                    break;
                case STARTING:
                    // Sets variables and objects.
                    Initialize();
                    // Load files - images, sounds, ...
                    LoadContent();

                    // When all things that are called above finished, we change game status to main menu.
                    gameState = GameState.MAIN_MENU;
                    break;
                case SHOP:
                    break;
                case VISUALIZING:
                    if (this.getWidth() > 1 && visualizingTime > SECINNANOSEC) {
                        frameWidth = this.getWidth();
                        frameHeight = this.getHeight();
                        gameState = GameState.STARTING;
                    } else {
                        visualizingTime += System.nanoTime() - lastVisualizingTime;
                        lastVisualizingTime = System.nanoTime();
                    }
                    break;
            }

            repaint();

            timeTaken = System.nanoTime() - beginTime;
            timeLeft = (GAME_UPDATE_PERIOD - timeTaken) / MILISEC_IN_NANOSEC;
            if (timeLeft < 10) timeLeft = 10;
            try {
                Thread.sleep(timeLeft);
            } catch (InterruptedException ex) { }*/
            gameStateManager.updateState(gameState);
            repaint();
            sleepTimeCalculation(beginTime);
        }
    }

    private void sleepTimeCalculation(long beginTime) {
        long timeTaken = System.nanoTime() - beginTime;
        long timeLeft = (GAME_UPDATE_PERIOD - timeTaken) / MILISEC_IN_NANOSEC;
        if (timeLeft < 10) timeLeft = 10;  // Ensure a minimal sleep time
        try {
            Thread.sleep(timeLeft);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    /*@Override
    public void paint(Graphics g) {
        super.paint(g);
        graphicsManager.drawComponents((Graphics2D) g, gameStateManager.getCurrentState());
    }*/

    @Override
    public void Draw(Graphics2D g2d) {
        if (gameState == null) {
            gameState = GameState.VISUALIZING;
            gameStateManager.getGameState(GameState.MAIN_MENU); // Default to MAIN_MENU or another appropriate state
        }
        switch (gameState) {
            case MAIN_MENU:
                if (mainMenu != null) {
                    mainMenu.draw(g2d);  // mainMenu 객체가 null이 아닐 때만 그리기
                }
                break;
            case PLAYING:
                if (game != null) {
                    game.Draw(g2d, mousePosition());  // game 객체가 null이 아닐 때만 그리기
                }
                break;
            case GAMEOVER:
                if (game != null) {
                    game.DrawGameOver(g2d, mousePosition());  // game 객체가 null이 아닐 때만 그리기
                }
                break;
            case PAUSED:
                if (game != null) {
                    game.Draw(g2d, mousePosition());  // 게임이 멈춘 상태에서도 화면을 그립니다.
                    g2d.setColor(Color.RED);
                    g2d.setFont(new Font("monospaced", Font.BOLD, 50));
                    g2d.drawString("Paused", Framework.frameWidth / 2 - 100, Framework.frameHeight / 2);
                }
                break;
            case OPTIONS:
                // 옵션 메뉴 처리
                break;
            case GAME_CONTENT_LOADING:
                g2d.setColor(Color.white);
                g2d.drawString("GAME is LOADING", frameWidth / 2 - 50, frameHeight / 2);
                break;
            case SHOP:
                try {
                    Shop2.drawShopUI(g2d, frameWidth, frameHeight, this);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            case DESTROYED: break;
            case VISUALIZING: break;
            case STARTING: break;
        }
        repaint();
    }

    private void stopBackgroundMusic() {
        if (backgroundMusicClip != null && backgroundMusicClip.isRunning()) {
            backgroundMusicClip.stop();
        }
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



    void newGame(int selectedStage) {
        game = new Game(this, selectedStage);  // 선택한 스테이지로 게임 시작
        System.out.println("stage start");
        gameStateManager.setCurrentState(GameState.PLAYING);
    }

    private void restartGame() {
        if (game != null) {
            game.reStartGame();  // Game 객체의 reStartGame() 호출
        } else {
            newGame(1);  // Game 객체가 없을 경우 새로 생성
        }
    }
    protected Point mousePosition() {
        try {
            Point mp = this.getMousePosition();
            return mp != null ? mp : new Point(0, 0);
        } catch (NullPointerException e) {
            return new Point(0, 0); // 더 구체적인 예외 처리
        }
    }

    @Override
    public void keyReleasedFramework(KeyEvent e) {
        switch (gameState) {
            case GAMEOVER:
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    stopBackgroundMusic();  // 게임 종료 시 배경음악 중지
                    System.exit(0);
                } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    gameStateManager.setCurrentState(GameState.MAIN_MENU);  // 스페이스바를 누르면 메인 메뉴로 돌아가기
                }
                break;
            case PLAYING:
                if (e.getKeyCode() == KeyEvent.VK_P)
                    gameStateManager.setCurrentState(GameState.PAUSED);
                else if (e.getKeyCode() == KeyEvent.VK_S)
                    gameStateManager.setCurrentState(GameState.SHOP);
                break;
            case MAIN_MENU:
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    stopBackgroundMusic();  // 게임 종료 시 배경음악 중지
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
                    gameStateManager.setCurrentState(GameState.PLAYING); // S 키로 상점 나가기
                }
                break;
            case STARTING: break;
            case VISUALIZING: break;
            case OPTIONS: break;
            case DESTROYED: break;
            case GAME_CONTENT_LOADING: break;
        }
    }
    @Override
    public void addMouseListener(MouseListener l) {
        super.addMouseListener(l);
    }
}
