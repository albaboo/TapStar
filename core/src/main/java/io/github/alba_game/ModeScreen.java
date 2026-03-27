package io.github.alba_game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class ModeScreen implements Screen {

    private final Main game;
    private Stage stage;
    private Skin skin;
    private SpriteBatch batch;
    private TextButton onlineButton;
    private float checkTimer = 0;

    public ModeScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        skin.getFont("default").getData().setScale(4.0f);

        onlineButton = new TextButton("ONLINE", skin);
        TextButton offlineButton = new TextButton("OFFLINE", skin);

        Label developedByLabel = new Label("Developed by Albaboo :)", skin);
        developedByLabel.setFontScale(3f);
        developedByLabel.setColor(Color.WHITE);

        onlineButton.setDisabled(true);
        onlineButton.setText("CONNECTING...");

        onlineButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (!onlineButton.isDisabled()) {
                    game.playOnline = true;
                    game.setScreen(new LoadingScreen(game));
                }
            }
        });

        offlineButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.playOnline = false;
                game.setScreen(new LoadingScreen(game));
            }
        });

        Table table = new Table();
        table.setFillParent(true);
        table.center();

        table.add(onlineButton).width(900).height(150).pad(20).padTop(250).row();
        table.add(offlineButton).width(900).height(150).pad(20).row();
        table.add(developedByLabel).padTop(250);

        stage.addActor(table);

        updateConnectionStatus();
    }

    @Override
    public void render(float delta) {
        checkTimer += delta;
        if (checkTimer >= 1.0f) {
            checkTimer = 0;
            updateConnectionStatus();
        }

        Gdx.gl.glClearColor(0.15f, 0.15f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(game.background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();

        stage.act(delta);
        stage.draw();
    }

    private void updateConnectionStatus() {
        if (game.firebaseService == null) {
            onlineButton.setDisabled(true);
            onlineButton.setText("ONLINE (UNAVAILABLE)");
        } else {
            game.firebaseService.checkConnection(connected -> Gdx.app.postRunnable(() -> {
                if (connected) {
                    onlineButton.setDisabled(false);
                    onlineButton.setText("ONLINE");
                } else {
                    onlineButton.setDisabled(true);
                    onlineButton.setText("ONLINE (NO CONNECTION)");
                }
            }));
        }
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        batch.dispose();
    }
}
