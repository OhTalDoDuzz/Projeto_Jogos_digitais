package myPedal.Game;

import com.badlogic.gdx.Game;

public class Main extends Game {

    private final int totalLevels = 5;
    private final float[] phaseProgress = new float[totalLevels];

    private int lastLevel = 1;

    @Override
    public void create() {
        for (int i = 0; i < totalLevels; i++) {
            phaseProgress[i] = 0f;
        }

        setScreen(new MainMenuScreen(this));
    }

    public int getTotalLevels() {
        return totalLevels;
    }

    public float getPhaseProgress(int level) {
        if (level < 1 || level > totalLevels) return 0f;
        return phaseProgress[level - 1];
    }

    public void setPhaseProgress(int level, float progress) {
        if (level >= 1 && level <= totalLevels) {
            phaseProgress[level - 1] = Math.min(100f, Math.max(0f, progress));
        }
    }

    public void completePhase(int level) {
        if (level >= 1 && level <= totalLevels) {
            phaseProgress[level - 1] = 100f;
        }
    }

    public void startLevel(int level) {
        if (level < 1 || level > totalLevels) level = 1;
        lastLevel = level;
        setScreen(new GameScreen(this, level));
    }

    public void restartLastLevel() {
        setScreen(new GameScreen(this, lastLevel));
    }

    public void resumeLastLevel() {
        setScreen(new GameScreen(this, lastLevel));
    }

    public int getLastLevel() {
        return lastLevel;
    }
}
