package kr.jbnu.se.std;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;
import java.awt.Font;
import java.io.InputStream;
import java.awt.Toolkit;
import java.awt.Cursor;import java.awt.Toolkit;
import java.awt.Cursor;
import java.awt.Image;




public class Framework extends Canvas {

    public static int highScore = 0;
    private MainMenu mainMenu;
    private Clip soundEffectClip;
    public static int selectedStage = 1;


    public static int frameWidth;

    public static int frameHeight;

    public static final long secInNanosec = 1000000000L;


    public static final long milisecInNanosec = 1000000L;


    private final int GAME_FPS = 60;

    private final long GAME_UPDATE_PERIOD = secInNanosec / GAME_FPS;


    public static enum GameState {STARTING, VISUALIZING, GAME_CONTENT_LOADING, MAIN_MENU, OPTIONS, PLAYING, GAMEOVER, PAUSED, DESTROYED, SHOP}

    public static GameState gameState;

    private long gameTime;
    // It is used for calculating elapsed time.
    private long lastTime;

    // The actual game
    private Game game;

    // 배경음악 재생을 위한 Clip 객체
    private Clip backgroundMusicClip;

    private BufferedImage shootTheDuckMenuImg;


    public void playSoundEffect(String resourcePath) {
        try {
            InputStream audioSrc = getClass().getResourceAsStream(resourcePath);
            if (audioSrc == null) {
                throw new RuntimeException("파일을 찾을 수 없습니다: " + resourcePath);
            }

            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioSrc);
            soundEffectClip = AudioSystem.getClip();
            soundEffectClip.open(audioInputStream);
            soundEffectClip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public Framework() {
        super();

        gameState = GameState.VISUALIZING;
        mainMenu = new MainMenu();

        //We start game in new thread.
        Thread gameThread = new Thread() {
            @Override
            public void run(){
                GameLoop();
            }
        };
        gameThread.start();
    }


    private void Initialize() {
        playBackgroundMusic("/background_music.wav");
    }


    private void LoadContent() {
        try {
            URL shootTheDuckMenuImgUrl = this.getClass().getResource("/images/menu.jpg");
            shootTheDuckMenuImg = ImageIO.read(shootTheDuckMenuImgUrl);
        } catch (IOException ex) {
            Logger.getLogger(Framework.class.getName()).log(Level.SEVERE, null, ex);
        }
    }



    private void GameLoop() {
        // This two variables are used in VISUALIZING state of the game. We used them to wait some time so that we get correct frame/window resolution.
        long visualizingTime = 0, lastVisualizingTime = System.nanoTime();

        // This variables are used for calculating the time that defines for how long we should put threat to sleep to meet the GAME_FPS.
        long beginTime, timeTaken, timeLeft;

        while (true) {
            beginTime = System.nanoTime();

            switch (gameState) {
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
                    if (this.getWidth() > 1 && visualizingTime > secInNanosec) {
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
            timeLeft = (GAME_UPDATE_PERIOD - timeTaken) / milisecInNanosec;
            if (timeLeft < 10) timeLeft = 10;
            try {
                Thread.sleep(timeLeft);
            } catch (InterruptedException ex) { }
        }
    }

    @Override
    public void Draw(Graphics2D g2d) {
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
        }
        repaint();
    }

    public void playBackgroundMusic(String resourcePath) {
        try {
            // 리소스 파일을 클래스 경로에서 읽어옴
            InputStream audioSrc = getClass().getResourceAsStream(resourcePath);
            if (audioSrc == null) {
                throw new RuntimeException("파일을 찾을 수 없습니다: " + resourcePath);
            }

            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioSrc);
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.loop(Clip.LOOP_CONTINUOUSLY);  // 음악을 무한 반복
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopBackgroundMusic() {
        if (backgroundMusicClip != null && backgroundMusicClip.isRunning()) {
            backgroundMusicClip.stop();
        }
    }

    public void closeBackgroundMusic() {
        if (backgroundMusicClip != null) {
            backgroundMusicClip.close();
        }
    }

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



    private void newGame(int selectedStage) {
        game = new Game(this, selectedStage);  // 선택한 스테이지로 게임 시작
    }

    private void restartGame() {
        if (game != null) {
            game.RestartGame();  // Game 객체의 RestartGame() 호출
        } else {
            newGame(1);  // Game 객체가 없을 경우 새로 생성
        }
    }
    private Point mousePosition() {
        try {
            Point mp = this.getMousePosition();
            if (mp != null)
                return this.getMousePosition();
            else
                return new Point(0, 0);
        } catch (Exception e) {
            return new Point(0, 0);
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
                    gameState = GameState.MAIN_MENU;  // 스페이스바를 누르면 메인 메뉴로 돌아가기
                }
                break;
            case PLAYING:
                if (e.getKeyCode() == KeyEvent.VK_P)
                    gameState = GameState.PAUSED;
                else if (e.getKeyCode() == KeyEvent.VK_S)
                    gameState = GameState.SHOP;
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
                    gameState = GameState.PLAYING; // S 키로 상점 나가기
                }
                break;
        }
    }
    @Override
    public synchronized void addMouseListener(MouseListener l) {
        super.addMouseListener(l);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        switch (gameState) {
            case MAIN_MENU:
                mainMenu.MouseClicked(e);  // 메인 메뉴에서 클릭 처리
                if (Framework.gameState == GameState.PLAYING) {
                    newGame(mainMenu.getSelectedStage());  // 선택한 스테이지에서 게임 시작
                }
                break;
            case SHOP:
                kr.jbnu.se.std.Shop2.handleShopMouseClick(e);
                break;
        }
    }

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_B) {  // 'B' 키를 누르면 불렛타임 활성화
            game.activateBulletTime();  // 불렛타임 활성화
        }
    }
}
