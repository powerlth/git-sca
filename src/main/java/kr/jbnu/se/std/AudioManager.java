package kr.jbnu.se.std;

import javax.sound.sampled.*;
import java.io.InputStream;

public class AudioManager {
    private Clip soundEffectClip;
    private Clip backgroundMusicClip;

    public void playSoundEffect(String resourcePath){
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

    public static void playBackgroundMusic(String resourcePath) {
        try {
            // 리소스 파일을 클래스 경로에서 읽어옴
            InputStream audioSrc = AudioManager.class.getResourceAsStream(resourcePath);
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

}
