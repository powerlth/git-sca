package kr.jbnu.se.std;

public class BulletTime {
    private boolean active = false;
    private long startTime;
    private static final long DURATION = 5000; // 10초

    // 불렛타임 스킬 활성화
    public void activate() {
        active = true;
        startTime = System.currentTimeMillis(); // 시작 시간 기록
        System.out.println("bullet time!!");
    }

    // 불렛타임 상태 업데이트
    public void update() {
        if (active && System.currentTimeMillis() - startTime > DURATION) {
            active = false;  // 10초 후 비활성화
        }
    }

    // 불렛타임이 활성화 중인지 확인
    public boolean isActive() {
        return active;
    }
}
