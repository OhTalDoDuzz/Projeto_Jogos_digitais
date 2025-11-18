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

public class MenuScreen implements Screen {
    @SuppressWarnings("unused")
    private final Main game;  
    private final Stage stage;
    private final BitmapFont font;

    public MenuScreen(Main game) {
        this.game = game;

        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        font = new BitmapFont();
        font.getData().setScale(2);

        LabelStyle labelStyle = new LabelStyle(font, Color.WHITE);
        TextButtonStyle buttonStyle = new TextButtonStyle();
        buttonStyle.font = font;

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        Label title = new Label("SELECIONAR FASE", labelStyle);
        table.add(title).padBottom(40);
        table.row();

        for (int i = 1; i <= 3; i++) {
            int fase = i;
            float progresso = game.getPhaseProgress(i);

            TextButton btn = new TextButton(
                    "Fase " + fase + " — " + Math.round(progresso) + "%",
                    buttonStyle
            );

            btn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    game.startLevel(fase);
                }
            });

            table.add(btn).padBottom(20).fillX();
            table.row();
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override public void show() {}
    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
        font.dispose();
    }
}
