package myPedal.Game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;
import java.util.Random;

public class Main extends ApplicationAdapter {

    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private Texture bikeTexture;
    private Texture obstacleTexture;
    private Texture streetTexture;
    private BitmapFont font;

    private Rectangle bike;
    private Array<Rectangle> obstacles;
    private long lastObstacleTime;
    private Random random;

    private boolean gameOver;
    private int score;

    private float bgY1;
    private float bgY2;
    private float backgroundSpeed = 200f;
    private float bgWidth;
    private float bgHeight;

    private float paddingBike = 8;
    private float obstacleScale = 0.5f;

    private long startTime;

    @Override
    public void create() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        bikeTexture = new Texture("moto_biker.png");
        obstacleTexture = new Texture("lixo.png");
        streetTexture = new Texture("av.jpg");

        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(2);

        bgWidth = Gdx.graphics.getWidth();
        bgHeight = Gdx.graphics.getHeight();

        bgY1 = 0;
        bgY2 = bgHeight;

        bike = new Rectangle();
        bike.x = Gdx.graphics.getWidth() / 2f - 32 + paddingBike;
        bike.y = 50 + paddingBike;
        bike.width = 50 - 2 * paddingBike;
        bike.height = 64 - 2 * paddingBike;

        obstacles = new Array<>();
        random = new Random();

        startTime = TimeUtils.millis();
        spawnObstacle();
    }

    private void spawnObstacle() {
        float spriteSize = 64f;
        float hbWidth = spriteSize * obstacleScale;
        float hbHeight = spriteSize * obstacleScale;

        Rectangle obstacle = new Rectangle();
        obstacle.width = hbWidth;
        obstacle.height = hbHeight;
        obstacle.x = random.nextInt((int)(bgWidth - spriteSize)) + (spriteSize - hbWidth) / 2f;
        obstacle.y = Gdx.graphics.getHeight() + (spriteSize - hbHeight) / 2f;

        obstacles.add(obstacle);
        lastObstacleTime = TimeUtils.nanoTime();
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        float delta = Gdx.graphics.getDeltaTime();

        if (!gameOver) {
            bgY1 -= backgroundSpeed * delta;
            bgY2 -= backgroundSpeed * delta;

            if (bgY1 + bgHeight <= 0) bgY1 = bgY2 + bgHeight;
            if (bgY2 + bgHeight <= 0) bgY2 = bgY1 + bgHeight;

            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) bike.x -= 300 * delta;
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) bike.x += 300 * delta;

            if (bike.x < paddingBike) bike.x = paddingBike;
            if (bike.x > bgWidth - bike.width - paddingBike)
                bike.x = bgWidth - bike.width - paddingBike;

            if (TimeUtils.nanoTime() - lastObstacleTime > 1000000000) spawnObstacle();

            Iterator<Rectangle> iter = obstacles.iterator();
            while (iter.hasNext()) {
                Rectangle obs = iter.next();
                obs.y -= backgroundSpeed * delta;

                if (obs.y + obs.height < 0) {
                    iter.remove();
                    score++;
                }

                if (obs.overlaps(bike)) {
                    gameOver = true;
                }
            }
        }

        batch.begin();
        batch.draw(streetTexture, 0, bgY1, bgWidth, bgHeight);
        batch.draw(streetTexture, 0, bgY2, bgWidth, bgHeight);
        batch.draw(bikeTexture, bike.x - paddingBike, bike.y - paddingBike, bike.width + 2*paddingBike, bike.height + 2*paddingBike);

        for (Rectangle obs : obstacles) {
            batch.draw(obstacleTexture, obs.x - (64*obstacleScale/2f), obs.y - (64*obstacleScale/2f), 64, 64);
        }

        long elapsed = (TimeUtils.millis() - startTime) / 1000;
        font.draw(batch, "Pontuação: " + score, 20, bgHeight - 20);
        font.draw(batch, "Tempo: " + elapsed + "s", 20, bgHeight - 60);

        if (gameOver) {
            font.setColor(Color.RED);
            font.draw(batch, "GAME OVER!", bgWidth / 2f - 100, bgHeight / 2f);
            font.setColor(Color.WHITE);
        }

        batch.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(bike.x, bike.y, bike.width, bike.height);
        for (Rectangle obs : obstacles) {
            shapeRenderer.rect(obs.x, obs.y, obs.width, obs.height);
        }
        shapeRenderer.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        bikeTexture.dispose();
        obstacleTexture.dispose();
        streetTexture.dispose();
        font.dispose();
    }
}
