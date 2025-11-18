package myPedal.Game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;

public class MainMenuScreen implements Screen {
    @SuppressWarnings("unused")
    private Main game;
    private Stage stage;
    private BitmapFont font;

    public MainMenuScreen(Main game) {
        this.game = game;
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        font = new BitmapFont();
        font.getData().setScale(2);

        LabelStyle labelStyle = new LabelStyle(font, Color.WHITE);
        TextButtonStyle btnStyle = new TextButtonStyle();
        btnStyle.font = font;

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        Label title = new Label("Ride & Recover", labelStyle);

        TextButton startBtn = new TextButton("Iniciar Jogo", btnStyle);
        TextButton continueBtn = new TextButton("Continuar", btnStyle);
        TextButton restartBtn = new TextButton("Reiniciar Última Fase", btnStyle);
        TextButton fasesBtn = new TextButton("Selecionar Fase", btnStyle);

        startBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MenuScreen(game));
            }
        });

        continueBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.resumeLastLevel();
            }
        });

        restartBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.restartLastLevel();
            }
        });

        fasesBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MenuScreen(game));
            }
        });

        table.add(title).padBottom(40);
        table.row();

        table.add(startBtn).padBottom(20).fillX();
        table.row();

        table.add(continueBtn).padBottom(20).fillX();
        table.row();

        table.add(restartBtn).padBottom(20).fillX();
        table.row();

        table.add(fasesBtn).padBottom(20).fillX();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override public void dispose() { stage.dispose(); font.dispose(); }
    @Override public void show() {}
    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}

