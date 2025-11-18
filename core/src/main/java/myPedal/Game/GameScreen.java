package myPedal.Game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;
import java.util.Random;

public class GameScreen implements Screen {

    private Main game;
    private int level;

    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private Texture bikeTexture, obstacleTexture, streetTexture, starTexture, barrierTexture;
    private Texture newObstacleTexture, newObstacle2Texture;
    private BitmapFont font;

    private Rectangle bike;
    private Array<Rectangle> obstacles, stars;

    private long lastObstacleTime, lastStarTime;
    private Random random;

    private float bgY1, bgY2, backgroundSpeed = 0f, bgWidth, bgHeight;
    private float paddingBike = 8;

    private long startTime;
    private int score;
    private boolean doublePointsActive = false;
    private long doublePointsEndTime;

    private float speed = 0f, maxSpeed = 900f, pedalBoost = 150f, decayRate = 800f;
    private long lastPedalTime = 0;
    private char lastPedalKey = '\0';

    private float speedMultiplier = 1.0f;

    private float progress = 0f;
    private int obstaclesPassed = 0;
    public final int OBSTACLES_PER_STEP = 10;
    public final int PHASE_STEPS = 5;

    private boolean phaseCompleted = false;

    private float movementMinX, movementMaxX;
    private final float movementPadding = 150f;

    private final float visualScale = 1.8f;
    private final float hitboxScale = 0.9f;

    private Music backgroundMusic;

    public GameScreen(Main game, int level) {
        this.game = game;
        this.level = level;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        bikeTexture = new Texture("moto_biker.png");
        obstacleTexture = new Texture("lixo.png");
        starTexture = new Texture("estrela.png");
        newObstacleTexture = new Texture("tampa_agua.png"); 
        newObstacle2Texture = new Texture("tampa_buraco.png"); 

        if (level == 2) {
            streetTexture = new Texture("Street_fase2.png");
            barrierTexture = new Texture("barreira.png");
            speedMultiplier = 2.0f;
            maxSpeed = 1300f;
            pedalBoost = 100f;
            decayRate = 100f;
        } else if (level == 3) {
            streetTexture = new Texture("Street_fase3.jpeg");
            speedMultiplier = 2.5f;
            maxSpeed = 1400f;
            pedalBoost = 120f;
            decayRate = 100f;
        } else {
            streetTexture = new Texture("av.jpg");
            speedMultiplier = 2.0f;
        }

        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(2);

        bgWidth = Gdx.graphics.getWidth();
        bgHeight = Gdx.graphics.getHeight();
        bgY1 = 0;
        bgY2 = bgHeight;

        bike = new Rectangle();
        bike.width = (level == 2) ? (50 - 2 * paddingBike) * 1.5f : 50 - 2 * paddingBike;
        bike.height = (level == 2) ? (64 - 2 * paddingBike) * 1.5f : 64 - 2 * paddingBike;
        bike.x = bgWidth / 2f - bike.width / 2f;
        bike.y = 50 + paddingBike;

        movementMinX = movementPadding;
        movementMaxX = bgWidth - movementPadding - bike.width;

        obstacles = new Array<>();
        stars = new Array<>();
        random = new Random();

        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("background.mp3"));
        backgroundMusic.setLooping(true);
        backgroundMusic.setVolume(0.5f);
        backgroundMusic.play();
        
        startGame();
    }

    private void startGame() {
        speed = 0;
        backgroundSpeed = 0;
        lastPedalKey = '\0';
        lastPedalTime = 0;
        score = 0;
        startTime = TimeUtils.millis();
        doublePointsActive = false;

        obstacles.clear();
        stars.clear();

        spawnObstacle();
        if (level == 2) spawnBarrier();
        spawnStar();

        progress = 0f;
        obstaclesPassed = 0;
        phaseCompleted = false;
    }

    private Rectangle forceNonOverlap(Rectangle newRect) {
        boolean overlapping;
        int attempts = 0;
        do {
            overlapping = false;
            for (Rectangle obs : obstacles) {
                if (obs.overlaps(newRect)) overlapping = true;
            }
            if (newRect.overlaps(bike)) overlapping = true;

            if (overlapping) {
                float minX = movementMinX;
                float maxX = movementMaxX - newRect.width;
                newRect.x = random.nextInt(Math.max(1, (int)(maxX - minX))) + minX;
                attempts++;
            }
        } while (overlapping && attempts < 15);

        return newRect;
    }

    private void spawnObstacle() {
        float spriteSize = (level == 2) ? 80f : 64f;
        if (level == 3) spriteSize *= 0.5f;

        Rectangle obs = new Rectangle();
        obs.width = spriteSize * hitboxScale;
        obs.height = spriteSize * hitboxScale;
        obs.y = bgHeight + 50;

        float minX = movementMinX;
        float maxX = movementMaxX - obs.width;
        obs.x = random.nextInt(Math.max(1, (int)(maxX - minX))) + minX;

        obs = forceNonOverlap(obs);
        obstacles.add(obs);
        lastObstacleTime = TimeUtils.nanoTime();
    }

    private void spawnBarrier() {
        Rectangle bar = new Rectangle();
        bar.width = 110 * hitboxScale;
        bar.height = 90 * hitboxScale;
        bar.y = bgHeight + 50;

        float minX = movementMinX;
        float maxX = movementMaxX - bar.width;
        bar.x = random.nextInt(Math.max(1, (int)(maxX - minX))) + minX;

        bar = forceNonOverlap(bar);
        obstacles.add(bar);
        lastObstacleTime = TimeUtils.nanoTime();
    }

    private void spawnNewObstacle() {
        float width = 70 * hitboxScale, height = 70 * hitboxScale;
        Rectangle obs = new Rectangle();
        obs.width = width;
        obs.height = height;
        obs.y = bgHeight + 50;

        float minX = movementMinX;
        float maxX = movementMaxX - obs.width;
        obs.x = random.nextInt(Math.max(1, (int)(maxX - minX))) + minX;

        obs = forceNonOverlap(obs);
        obstacles.add(obs);
        lastObstacleTime = TimeUtils.nanoTime();
    }

    private void spawnNewObstacle2() {
        float width = 60 * hitboxScale, height = 80 * hitboxScale;
        Rectangle obs = new Rectangle();
        obs.width = width;
        obs.height = height;
        obs.y = bgHeight + 50;

        float minX = movementMinX;
        float maxX = movementMaxX - obs.width;
        obs.x = random.nextInt(Math.max(1, (int)(maxX - minX))) + minX;

        obs = forceNonOverlap(obs);
        obstacles.add(obs);
        lastObstacleTime = TimeUtils.nanoTime();
    }

    private void spawnStar() {
        Rectangle star = new Rectangle();
        star.width = 60;
        star.height = 60;
        star.y = bgHeight + 60;

        int attempts = 0;
        boolean overlap;
        do {
            overlap = false;
            float minX = movementMinX;
            float maxX = movementMaxX - star.width;
            star.x = random.nextInt(Math.max(1, (int)(maxX - minX))) + minX;

            if (star.overlaps(bike)) overlap = true;
            for (Rectangle obs : obstacles) {
                if (star.overlaps(obs)) overlap = true;
            }

            attempts++;
        } while (overlap && attempts < 20);

        stars.add(star);
        lastStarTime = TimeUtils.millis();
    }

    private void updateProgress() {
        int step = obstaclesPassed / OBSTACLES_PER_STEP;
        progress = step / (float) PHASE_STEPS;
        progress = Math.min(1f, Math.max(0f, progress));

        game.setPhaseProgress(level, progress * 100);

        if (progress >= 1f && !phaseCompleted) {
            phaseCompleted = true;
            game.completePhase(level);
        }
    }

    private void handlePedalInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.A)) tryPedal('A');
        if (Gdx.input.isKeyJustPressed(Input.Keys.D)) tryPedal('D');
    }

    private void tryPedal(char key) {
        if (lastPedalKey == key) return;
        pedal(key);
        lastPedalKey = key;
    }

    private void pedal(char key) {
        long now = System.currentTimeMillis();

        if (lastPedalTime != 0) {
            long diff = now - lastPedalTime;
            if (diff < 180) speed += pedalBoost * 1.8f * speedMultiplier;
            else if (diff < 300) speed += pedalBoost * 1.3f * speedMultiplier;
            else speed += pedalBoost * speedMultiplier;
        } else {
            speed += pedalBoost * speedMultiplier;
        }

        lastPedalTime = now;
        if (speed > maxSpeed) speed = maxSpeed;
    }

    private void applySpeedDecay(float delta) {
        if (speed > 0) {
            speed -= decayRate * delta;
            if (speed < 0) speed = 0;
        }
        backgroundSpeed = speed;
    }

    @Override
    public void render(float delta) {
        // Abrir PauseScreen
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            if (backgroundMusic.isPlaying()) backgroundMusic.pause();
            game.setScreen(new PauseScreen(game, level, this));
            return;
        }

        // Fase completa
        if (phaseCompleted) {
            batch.begin();
            font.setColor(Color.GREEN);
            font.draw(batch, "FASE COMPLETA!", bgWidth / 2f - 120, bgHeight / 2f + 40);
            font.draw(batch, "Pressione ENTER para continuar", bgWidth / 2f - 220, bgHeight / 2f - 10);
            batch.end();

            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                int nextLevel = level + 1;
                if (nextLevel > game.getTotalLevels()) nextLevel = 1;
                game.startLevel(nextLevel);
            }
            return;
        }

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        handlePedalInput();
        applySpeedDecay(delta);

        bgY1 -= backgroundSpeed * delta;
        bgY2 -= backgroundSpeed * delta;
        if (bgY1 + bgHeight <= 0) bgY1 = bgY2 + bgHeight;
        if (bgY2 + bgHeight <= 0) bgY2 = bgY1 + bgHeight;

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) bike.x -= 500 * delta;
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) bike.x += 500 * delta;

        if (bike.x < movementMinX) bike.x = movementMinX;
        if (bike.x > movementMaxX) bike.x = movementMaxX;

        if (TimeUtils.millis() - lastStarTime > 4000 + random.nextInt(2000)) spawnStar();

        long obstacleInterval = 900_000_000;
        long obstacleIntervalLixo = 1_800_000_000L;

        if (level == 2) {
            if (TimeUtils.nanoTime() - lastObstacleTime > obstacleInterval) {
                if (random.nextBoolean()) spawnBarrier();
                else if (TimeUtils.nanoTime() - lastObstacleTime > obstacleIntervalLixo) spawnObstacle();
            }
        } else if (level == 3) {
            if (TimeUtils.nanoTime() - lastObstacleTime > obstacleInterval) {
                int choice = random.nextInt(3);
                if (choice == 0) spawnObstacle();
                else if (choice == 1) spawnNewObstacle();
                else spawnNewObstacle2();
            }
        } else {
            if (TimeUtils.nanoTime() - lastObstacleTime > obstacleInterval) spawnObstacle();
        }

        if (doublePointsActive && TimeUtils.millis() > doublePointsEndTime)
            doublePointsActive = false;

        // ------------------- Obstáculos -------------------
        Iterator<Rectangle> iter = obstacles.iterator();
        while (iter.hasNext()) {
            Rectangle obs = iter.next();
            obs.y -= backgroundSpeed * delta;

            if (obs.overlaps(bike)) {
                startGame();
                return;
            }

            // Atualiza progresso sempre que obstáculo sai da tela
            if (obs.y + obs.height < 0) {
                iter.remove();
                obstaclesPassed++;
                updateProgress();
                score += doublePointsActive ? 20 : 10;
            }
        }

        // ------------------- Estrelas -------------------
        Iterator<Rectangle> starIter = stars.iterator();
        while (starIter.hasNext()) {
            Rectangle star = starIter.next();
            star.y -= backgroundSpeed * delta;

            if (star.overlaps(bike)) {
                doublePointsActive = true;
                doublePointsEndTime = TimeUtils.millis() + 5000;
                starIter.remove();
            }

            if (star.y + star.height < 0) starIter.remove();
        }

        // ------------------- Renderização -------------------
        batch.begin();
        batch.draw(streetTexture, 0, bgY1, bgWidth, bgHeight);
        batch.draw(streetTexture, 0, bgY2, bgWidth, bgHeight);
        batch.draw(bikeTexture, bike.x - paddingBike, bike.y - paddingBike, bike.width + 2 * paddingBike, bike.height + 2 * paddingBike);

        for (Rectangle obs : obstacles) {
            float drawWidth, drawHeight, offsetX, offsetY;

            if (level == 2 && obs.width == 110 * hitboxScale) {
                drawWidth = obs.width / hitboxScale * visualScale;
                drawHeight = obs.height / hitboxScale * visualScale;
                offsetX = (drawWidth - obs.width) / 2;
                offsetY = (drawHeight - obs.height) / 2;
                batch.draw(barrierTexture, obs.x - offsetX, obs.y - offsetY, drawWidth, drawHeight);
            } else if (level == 3 && obs.width >= 70 * hitboxScale) {
                drawWidth = obs.width / hitboxScale * 2.2f;
                drawHeight = obs.height / hitboxScale * 2.2f;
                offsetX = (drawWidth - obs.width) / 2;
                offsetY = (drawHeight - obs.height) / 2;
                batch.draw(newObstacleTexture, obs.x - offsetX, obs.y - offsetY, drawWidth, drawHeight);
            } else if (level == 3 && obs.width >= 60 * hitboxScale) {
                drawWidth = obs.width / hitboxScale * 2.0f;
                drawHeight = obs.height / hitboxScale * 2.0f;
                offsetX = (drawWidth - obs.width) / 2;
                offsetY = (drawHeight - obs.height) / 2;
                batch.draw(newObstacle2Texture, obs.x - offsetX, obs.y - offsetY, drawWidth, drawHeight);
            } else {
                drawWidth = obs.width / hitboxScale * 2.0f;
                drawHeight = obs.height / hitboxScale * 2.0f;
                offsetX = (drawWidth - obs.width) / 2;
                offsetY = (drawHeight - obs.height) / 2;
                batch.draw(obstacleTexture, obs.x - offsetX, obs.y - offsetY, drawWidth, drawHeight);
            }
        }

        for (Rectangle star : stars)
            batch.draw(starTexture, star.x, star.y, star.width, star.height);

        long elapsed = (TimeUtils.millis() - startTime) / 1000;
        font.draw(batch, "Pontuação: " + score, 20, bgHeight - 20);
        font.draw(batch, "Tempo: " + elapsed + "s", 20, bgHeight - 60);
        font.draw(batch, "Velocidade: " + (int) speed, bgWidth - 260, bgHeight - 20);

        if (doublePointsActive) {
            font.setColor(Color.YELLOW);
            font.draw(batch, "x2 ATIVO!", bgWidth - 180, bgHeight - 60);
            font.setColor(Color.WHITE);
        }
        batch.end();

        float barWidth = Math.min(bgWidth - 40, 600f);
        float barHeight = 22f;
        float barX = bgWidth / 2f - barWidth / 2f;
        float barY = 20f;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.DARK_GRAY);
        shapeRenderer.rect(barX, barY, barWidth, barHeight);
        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.rect(barX, barY, barWidth * progress, barHeight);
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(barX, barY, barWidth, barHeight);
        shapeRenderer.end();

        batch.begin();
        font.draw(batch, "Fase " + level + " — " + Math.round(progress * 100) + "%", barX, barY + 40);
        batch.end();
    }

    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        bikeTexture.dispose();
        obstacleTexture.dispose();
        streetTexture.dispose();
        starTexture.dispose();
        newObstacleTexture.dispose();
        newObstacle2Texture.dispose();
        if (level == 2) barrierTexture.dispose();
        font.dispose();

        if (backgroundMusic != null) {
            backgroundMusic.stop();
            backgroundMusic.dispose();
        }
    }

    public void resumeMusic() {
        if (!backgroundMusic.isPlaying()) backgroundMusic.play();
    }

    public void pauseMusic() {
        if (backgroundMusic.isPlaying()) backgroundMusic.pause();
    }
}