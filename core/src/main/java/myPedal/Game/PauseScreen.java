package myPedal.Game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class PauseScreen implements Screen {

    private final Main game;
    private final int currentLevel;
    private final GameScreen gameScreen; 

    private Stage stage;
    private BitmapFont font;

    public PauseScreen(Main game, int currentLevel, GameScreen gameScreen) {
        this.game = game;
        this.currentLevel = currentLevel;
        this.gameScreen = gameScreen;
    }

    @Override
    public void show() {
        stage = new Stage(new FitViewport(800, 1200));
        Gdx.input.setInputProcessor(stage);

        font = new BitmapFont();

        LabelStyle labelStyle = new LabelStyle(font, Color.WHITE);
        Label title = new Label("PAUSE", labelStyle);
        title.setFontScale(3);
        title.setPosition(stage.getViewport().getWorldWidth() / 2 - 80,
                          stage.getViewport().getWorldHeight() - 150);
        stage.addActor(title);

        TextButtonStyle textButtonStyle = new TextButtonStyle();
        textButtonStyle.font = font;
        textButtonStyle.fontColor = Color.WHITE;
        
        TextButton continueBtn = new TextButton("Continuar", textButtonStyle);
        continueBtn.setSize(250, 60);
        continueBtn.setPosition(stage.getViewport().getWorldWidth() / 2 - 125,
                                stage.getViewport().getWorldHeight() / 2 + 40);
        continueBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(gameScreen); 
                gameScreen.resumeMusic();  
            }
        });
        stage.addActor(continueBtn);

        TextButton restartBtn = new TextButton("Reiniciar Fase", textButtonStyle);
        restartBtn.setSize(250, 60);
        restartBtn.setPosition(stage.getViewport().getWorldWidth() / 2 - 125,
                               stage.getViewport().getWorldHeight() / 2 - 40);
        restartBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new GameScreen(game, currentLevel)); 
            }
        });
        stage.addActor(restartBtn);

        TextButton menuBtn = new TextButton("Menu Principal", textButtonStyle);
        menuBtn.setSize(250, 60);
        menuBtn.setPosition(stage.getViewport().getWorldWidth() / 2 - 125,
                            stage.getViewport().getWorldHeight() / 2 - 120);
        menuBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game));
            }
        });
        stage.addActor(menuBtn);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
        font.dispose();
    }
}
