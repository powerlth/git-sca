package kr.jbnu.se.std;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.*;
import kr.jbnu.se.std.GraphicsUtils;

public class Game {
    private final String highScoreFilePath = "src/main/resources/highscore.txt"; // 프로젝트 내의 resources 폴더에 저장
    private int highScore = 0;
    private static int stage; // 현재 스테이지
    private int kills = 0; // 플레이어가 잡은 오리 수
    private static double duckSpeedMultiplier; // 오리의 속도 배수
    private long stageMessageTime = 0; // 스테이지 클리어 메시지 표시 시간
    private static final long STAGE_MESSAGE_DURATION = 3 * Framework.secInNanosec; // 메시지 지속 시간
    private int lastTreasureBoxScore = 0;
    private long nextTreasureBoxTime = 0;
    private long treasureBoxMessageTime = 0;
    private static final long MESSAGE_DURATION = 3 * Framework.secInNanosec;
    private long treasureBoxSurvivalTime = 0;
    private long messageDisplayTime = 0;
    private static final long MESSAGE_DISPLAY_DURATION = 3 * Framework.secInNanosec;
    private BufferedImage treasureImg;
    private boolean isMouseButtonPressed = false;
    private int missClicks;
    private TreasureBox treasureBox;
    private long treasureBoxStartTime;
    private boolean treasureBoxCreated = false; // 보물상자가 생성되었는지 여부
    private long gameStartTime; // 게임 시작 시간
    private static BufferedImage backgroundImg;
    private BulletTime bullettime = new BulletTime();
    private volatile boolean isLoaded = false;
    /**
     * We use this to generate a random number.
     */
    private Random random;

    /**
     * Font that we will use to write statistic to the screen.
     */
    private Font font;

    /**
     * Array list of the ducks.
     */
    private List<Duck> ducks;  // List 타입으로 선언

    /**
     * How many ducks leave the screen alive?
     */
    private int runawayDucks;

    /**
     * How many ducks the player killed?
     */
    private int killedDucks;

    /**
     * For each killed duck, the player gets points.
     */
    private int score;

    /**
     * How many times a player is shot?
     */
    private int shoots;

    /**
     * Last time of the shoot.
     */
    private long lastTimeShoot;

    /**
     * The time which must elapse between shots.
     */
    private long timeBetweenShots;

    /**
     * Game background image.
     */

    /**
     * Bottom grass.
     */
    private static BufferedImage grassImg;

    /**
     * Duck image.
     */
    private BufferedImage duckImg;

    private BufferedImage topDuckImg;

    /**
     * Shotgun sight image.
     */
    private BufferedImage sightImg;

    /**
     * Middle width of the sight image.
     */
    private int sightImgMiddleWidth;

    /**
     * Middle height of the sight image.
     */
    private int sightImgMiddleHeight;

    private static BufferedImage[] itemImage = new BufferedImage[4];

    private Framework framework;

    private void setNextTreasureBoxTime() {
        // 10초에서 30초 사이의 랜덤 시간 (초 단위)
        long randomTime = 10 + random.nextInt(21); // 10초 ~ 30초
        nextTreasureBoxTime = System.nanoTime() + randomTime * Framework.secInNanosec;
    }

    private BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        Image scaledImage = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);

        // 새로운 BufferedImage로 변환
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resizedImage.createGraphics();
        g2d.drawImage(scaledImage, 0, 0, null);
        g2d.dispose();

        return resizedImage;
    }

    public Game(Framework framework) {
        this(framework, 1);  // 기본 스테이지는 1로 설정
    }


    public Game(Framework framework,int selectedStage) {
        this.framework = framework;
        this.stage = selectedStage;
        this.duckSpeedMultiplier = Math.pow(1.2, selectedStage - 1);
        Framework.gameState = Framework.GameState.GAME_CONTENT_LOADING;

        Thread threadForInitGame = new Thread() {
            @Override
            public void run(){
                Initialize();
                LoadContent();
                loadHighScore();

                Framework.gameState = Framework.GameState.PLAYING;
                gameStartTime = System.nanoTime(); // 게임 시작 시간 기록

                // 보물상자 생성 시간 초기화
                setNextTreasureBoxTime(); // 게임 시작 시 보물상자 등장 시간 설정
            }
        };
        threadForInitGame.start();
    }
    private void loadHighScore() {
        try (BufferedReader reader = new BufferedReader(new FileReader(highScoreFilePath))) {
            String line = reader.readLine();
            if (line != null) {
                highScore = Integer.parseInt(line);
            }
            else {
                highScore = 0;
            }
        } catch (IOException e) {
            System.out.println("High score 파일을 불러올 수 없습니다."+ e.getMessage());
        }
    }
    private void saveHighScore() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(highScoreFilePath))) {
            writer.write(String.valueOf(highScore));
        } catch (IOException e) {
            System.out.println("High score 파일을 저장할 수 없습니다: " + e.getMessage());
        }
    }


    /**
     * Set variables and objects for the game.
     */
    void Initialize() {
        random = new Random();
        font = new Font("monospaced", Font.BOLD, 18);

        ducks = new ArrayList<Duck>();

        missClicks = 0;
        runawayDucks = 0;
        killedDucks = 0;
        score = 0;
        shoots = 0;

        lastTimeShoot = 0;
        timeBetweenShots = Framework.secInNanosec / 3;
    }
    public void activateBulletTime() {
        bullettime.activate();  // 불렛타임 활성화
    }
    public void update() {
        bullettime.update();  // 불렛타임 상태 업데이트
    }
    public BulletTime getBulletTime() {
        return bullettime;
    }

    public void checkDuckHit(Point mousePosition) {
        Iterator<Duck> iterator = ducks.iterator();
        boolean hitOccurred = false;
        boolean isHit = false; // 오리가 맞았는지 여부

        while (iterator.hasNext()) {
            Duck duck = iterator.next();
            if (duck.isHit(mousePosition)) {
                iterator.remove();  // 오리 제거
                killedDucks++;
                kills++; // kills 증가
                if (bullettime.isActive()){
                    score += duck.score * 2;
                }
                score += duck.score;  // 점수 증가
                kr.jbnu.se.std.Money.addMoney(1);
                // kills가 증가할 때마다 효과음 재생
                if (!hitOccurred) {
                    framework.playSoundEffect("/quack_sound.wav");  // 효과음 재생
                    hitOccurred = true;
                }

                isHit = true; // 오리가 맞았음
                System.out.println("Duck hit! Kills: " + killedDucks);
            }
        }

        // 오리가 맞지 않았을 경우 미스 클릭 카운터 증가
        if (!isHit) {
            missClicks++;
            System.out.println("Miss Clicks: " + missClicks);
        }

        // 미스 클릭이 10번 이상이면 게임 오버
        if (missClicks >= 10) {
            Framework.gameState = Framework.GameState.GAMEOVER;
            System.out.println("Game Over due to miss clicks!");
        }
    }
    /**
     * Load game files - images, sounds, ...
     */
    protected void LoadContent() {
        try {
            itemImage[0] = ImageIO.read(Objects.requireNonNull(this.getClass().getResource("/images/back1.jpg"))); // default
            itemImage[1] = ImageIO.read(Objects.requireNonNull(this.getClass().getResource("/images/back2.jpg"))); // purchased item 1
            itemImage[2] = ImageIO.read(Objects.requireNonNull(this.getClass().getResource("/images/back3.jpg"))); // purchased item 2
            itemImage[3] = ImageIO.read(Objects.requireNonNull(this.getClass().getResource("/images/background.jpg"))); // purchased item 3

            backgroundImg = itemImage[3];

            URL grassImgUrl = this.getClass().getResource("/images/grass.png");
            if (grassImgUrl == null) {
                System.err.println("Cannot find resource: /images/grass.png");
            } else {
                try {
                    grassImg = ImageIO.read(new URL("/images/grass.png"));
                } catch (IOException ex) {
                    System.err.println("Failed to load grass image: " + ex.getMessage());
                }
            }
            /*grassImg = ImageIO.read(Objects.requireNonNull(this.getClass().getResource("/images/grass.png")));
            if (grassImgUrl == null) {
                System.err.println("Cannot find grass image resource");
                return;
            }*/

            URL duckImgUrl = this.getClass().getResource("/images/duck.png");
            duckImg = ImageIO.read(duckImgUrl);

            URL topDuckImgUrl = this.getClass().getResource("/images/topduck.png");
            topDuckImg = ImageIO.read(topDuckImgUrl); //나는오리

            URL sightImgUrl = this.getClass().getResource("/images/sight.png");
            sightImg = ImageIO.read(sightImgUrl);
            sightImgMiddleWidth = sightImg.getWidth() / 2;
            sightImgMiddleHeight = sightImg.getHeight() / 2;

            URL treasureImgUrl = this.getClass().getResource("/images/treasure_box.png");
            BufferedImage originalTreasureImg = ImageIO.read(treasureImgUrl);
            treasureImg = ImageIO.read(treasureImgUrl);
            int scaledWidth = originalTreasureImg.getWidth() / 3;
            int scaledHeight = originalTreasureImg.getHeight() / 3;
            treasureImg = resizeImage(originalTreasureImg, scaledWidth, scaledHeight);

            if (treasureImg == null) {
                System.out.println("Failed to load treasure box image!");
            }
        } catch (IOException ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
        }
        isLoaded = true;
    }

    private void createNewDucks(long gameTime) { // 오리 생성
        if (System.nanoTime() - Duck.lastDuckTime >= Duck.timeBetweenDucks) {
            ducks.add(new Duck(Duck.duckLines[Duck.nextDuckLines][0] + random.nextInt(200),
                    Duck.duckLines[Duck.nextDuckLines][1],
                    (int)(Duck.duckLines[Duck.nextDuckLines][2] * duckSpeedMultiplier),
                    Duck.duckLines[Duck.nextDuckLines][3], duckImg, this));
            ducks.add(new Duck(Duck.FlyingduckLines[Duck.nextDuckLines][0] + random.nextInt(200),
                    Duck.FlyingduckLines[Duck.nextDuckLines][1],
                    Duck.FlyingduckLines[Duck.nextDuckLines][2],
                    Duck.FlyingduckLines[Duck.nextDuckLines][3], topDuckImg, this));
            Duck.nextDuckLines++;
            if (Duck.nextDuckLines >= Duck.duckLines.length)
                Duck.nextDuckLines = 0;
            Duck.lastDuckTime = System.nanoTime();
        }
    }

    private void manageTreasureBox(long gameTime) { //보물상자 관리
        if (!treasureBoxCreated && System.nanoTime() >= nextTreasureBoxTime) {
            treasureBox = new TreasureBox(100, 300, treasureImg);
            treasureBoxCreated = true;
            treasureBoxStartTime = System.nanoTime();
            messageDisplayTime = System.nanoTime();
            treasureBoxSurvivalTime = 0;
            System.out.println("보물상자가 생성되었습니다!");
            setNextTreasureBoxTime();
        }

        if (treasureBox != null && treasureBox.isActive()) {
            treasureBoxSurvivalTime = (System.nanoTime() - treasureBoxStartTime) / Framework.secInNanosec;
            if (treasureBoxSurvivalTime >= 30) {
                lastTreasureBoxScore = 100 + random.nextInt(901);
                score += lastTreasureBoxScore;
                treasureBox = null;
                treasureBoxCreated = false;
                setNextTreasureBoxTime();
                treasureBoxMessageTime = System.nanoTime();
            } else {
                treasureBox.Update();
            }
        }
    }

    private void updateStage(long gameTime) { //스테이지 업데이트
        if (kills >= 50 * stage) {
            stage++;
            duckSpeedMultiplier *= 1.2;
            stageMessageTime = System.nanoTime();
            System.out.println(stage + "번째 스테이지로 이동하였습니다.");
        }
    }

    private void updateDucks(long gameTime) { //오리 업데이트
        Iterator<Duck> iterator = ducks.iterator();
        while (iterator.hasNext()) {
            Duck duck = iterator.next();
            getBulletTime().update();
            duck.Update();
            if (duckImg != null && duck.x < 0 - duckImg.getWidth()) {
                iterator.remove();
                runawayDucks++;
            }
            if (treasureBox != null && treasureBox.isActive() && treasureBox.isHitByDuck(duck.x, duck.y, duckImg.getWidth(), duckImg.getHeight())) {
                treasureBox.incrementHitCount();
            }
        }
    }

    private void handlePlayerShooting(long gameTime, Point mousePosition) {// 사격 처리
        boolean currentMouseButtonState = Canvas.mouseButtonState(MouseEvent.BUTTON1);
        if (currentMouseButtonState) {
            if (!isMouseButtonPressed && System.nanoTime() - lastTimeShoot >= timeBetweenShots) {
                shoots++;
                checkDuckHit(mousePosition);
                lastTimeShoot = System.nanoTime();
            }
            isMouseButtonPressed = true;
        } else {
            if (isMouseButtonPressed) {
                isMouseButtonPressed = false;
            }
        }
    }

    private void checkGameOver(long gameTime) { //게임 오버 조건 검사
        if (runawayDucks >= 50) {
            Framework.gameState = Framework.GameState.GAMEOVER;
        }
    }
    private void waitForLoading(){
        while (!isLoaded) {
            try {
                Thread.sleep(100);  // 로딩이 완료될 때까지 잠시 대기
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();  // 인터럽트 발생 시 스레드 인터럽트 상태 설정
            }
        }
    }

    public void UpdateGame(long gameTime, Point mousePosition) {
        // 새로운 오리 생성 및 업데이트 로직
        waitForLoading();
        if (score > Framework.highScore) {
            Framework.highScore = score;  // 최고 점수를 갱신
            saveHighScore();  // 갱신된 점수를 저장
        }
        createNewDucks(gameTime);
        manageTreasureBox(gameTime);
        updateStage(gameTime);
        updateDucks(gameTime);
        handlePlayerShooting(gameTime, mousePosition);
        checkGameOver(gameTime);
    }
    /**
     * Draw the game to the screen.
     *
     * @param g2d Graphics2D
     * @param mousePosition current mouse position.
     */
    public void Draw(Graphics2D g2d, Point mousePosition) {
        if (mousePosition == null) {
            mousePosition = new Point(0, 0); // Provide a default position or skip drawing that requires mousePosition
        }
        g2d.drawImage(backgroundImg, 0, 0, Framework.frameWidth, Framework.frameHeight, null);

        // Here we draw all the ducks.
        for (Duck duck : ducks) {
            duck.Draw(g2d);
        }

        g2d.drawImage(grassImg, 0, Framework.frameHeight - grassImg.getHeight(), Framework.frameWidth, grassImg.getHeight(), null);

        g2d.drawImage(sightImg, mousePosition.x - sightImgMiddleWidth, mousePosition.y - sightImgMiddleHeight, null);

        if (treasureBox != null && treasureBox.isActive()) {
            treasureBox.Draw(g2d);
            GraphicsUtils.setCommonGraphicsSettings(g2d);
            g2d.drawString("Treasure Box Survival Time: " + treasureBoxSurvivalTime + "s", 10, 50); // 좌상단에 시간 표시
        }
        if (System.nanoTime() - stageMessageTime <= STAGE_MESSAGE_DURATION) {
            GraphicsUtils.setCommonGraphicsSettings(g2d);
            g2d.drawString(stage-1 + "번째 스테이지를 클리어하였습니다!", Framework.frameWidth / 2 - 250, Framework.frameHeight / 2);
        }


        // 보물상자가 나타났을 때 메시지 표시 (3초 동안)
        if (System.nanoTime() - messageDisplayTime <= MESSAGE_DISPLAY_DURATION) {
            GraphicsUtils.setCommonGraphicsSettings(g2d);
            g2d.drawString("보물상자가 나타났습니다! ", Framework.frameWidth / 2 - 100, Framework.frameHeight / 2);
        }
        if (System.nanoTime() - treasureBoxMessageTime <= MESSAGE_DURATION) {
            GraphicsUtils.setCommonGraphicsSettings(g2d);

            // 첫 번째 줄: "보물상자를 지켜 점수를 얻었습니다!"
            g2d.drawString("보물상자를 지켜 점수를 얻었습니다!", Framework.frameWidth / 2 - 200, Framework.frameHeight / 2);

            // 두 번째 줄: 점수 표시 (y 좌표를 아래로 이동시켜서 줄바꿈 효과)
            g2d.drawString("(" + lastTreasureBoxScore + "점)", Framework.frameWidth / 2 - 100, Framework.frameHeight / 2 + 40);
        }
        g2d.setFont(font);
        g2d.setColor(Color.darkGray);


        g2d.drawString("RUNAWAY: " + runawayDucks, 10, 21);
        g2d.drawString("KILLS: " + killedDucks, 160, 21);
        g2d.drawString("SHOOTS: " + shoots, 299, 21);
        g2d.drawString("SCORE: " + score, 440, 21);
        g2d.drawString("MISS: " + missClicks, 580, 21);
        g2d.drawString("MONEY: " + kr.jbnu.se.std.Money.getMoney(), 10, 41);
        // 오리 속도 및 스테이지 정보 표시
        GraphicsUtils.setCommonGraphicsSettings(g2d);
        g2d.drawString("현재 스테이지: " + stage, 10, 95); // 좌상단에 스테이지 표시
        g2d.drawString("오리 속도 배수: " + String.format("%.1f", duckSpeedMultiplier), 10, 125); // 좌상단에 오리 속도 배수 표시

    }

    /**
     * Draw the game over screen.
     *
     * @param g2d Graphics2D
     * @param mousePosition Current mouse position.
     */
    public void DrawGameOver(Graphics2D g2d, Point mousePosition) {
        Draw(g2d, mousePosition);

        g2d.setFont(new Font("monospaced", Font.BOLD, 70));
        g2d.setColor(Color.black);
        g2d.drawString("Game Over", Framework.frameWidth / 2 - 179, (int)(Framework.frameHeight * 0.6) + 1);
        g2d.setFont(new Font("monospaced", Font.BOLD, 30));
        g2d.drawString("Press space to restart.", Framework.frameWidth / 2 - 179, (int)(Framework.frameHeight * 0.65) + 1);
        g2d.setColor(Color.red);
        g2d.setFont(new Font("monospaced", Font.BOLD, 70));
        g2d.drawString("Game Over", Framework.frameWidth / 2 - 179, (int)(Framework.frameHeight * 0.60));
        g2d.setFont(new Font("monospaced", Font.BOLD, 30));
        g2d.drawString("Press space to restart.", Framework.frameWidth / 2 - 179, (int)(Framework.frameHeight * 0.65));
    }

    public void RestartGame() {
        // Removes all of the ducks from the list.
        ducks.clear();

        // Reset variables to their initial state.
        runawayDucks = 0;
        killedDucks = 0;
        score = 0;
        shoots = 0;
        missClicks = 0;
        kr.jbnu.se.std.Money.setMoney(0);
        // Reset the time-related variables.
        lastTimeShoot = 0;
        treasureBoxCreated = false; // 보물상자 초기화
        treasureBox = null; // 보물상자를 null로 초기화

        Duck.lastDuckTime = 0; // 오리 생성 시간을 초기화

        System.out.println("Game has been restarted.");
    }

    public static void setSelectedMenuImage(int index) {
        if (index >= 0 && index < itemImage.length) {
            backgroundImg = itemImage[index]; // Change the background image based on purchased item
        }
    }
}
