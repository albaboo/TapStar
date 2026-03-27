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
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import org.jetbrains.annotations.NotNull;

public class UsernameScreen implements Screen {

    private final Main game;
    private SpriteBatch batch;
    private Stage stage;
    private Skin skin;
    private Label messageLabel;

    public UsernameScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        skin.getFont("default").getData().setScale(3.0f);

        TextField usernameField = new TextField("", skin);
        usernameField.setMessageText("Username...");
        usernameField.getStyle().background.setLeftWidth(50f);
        usernameField.getStyle().focusedBackground.setLeftWidth(50f);

        Label chooseUsernameLabel = new Label("CHOOSE A USERNAME:", skin);
        chooseUsernameLabel.setFontScale(2.5f);
        chooseUsernameLabel.setColor(Color.WHITE);

        messageLabel = new Label("", skin);
        messageLabel.setFontScale(2.0f);
        messageLabel.setColor(Color.RED);

        TextButton okButton = getTextButton(usernameField);

        Label developedByLabel = new Label("Developed by Albaboo :)", skin);
        developedByLabel.setFontScale(3f);
        developedByLabel.setColor(Color.WHITE);

        Table table = new Table();
        table.setFillParent(true);
        table.center();

        table.add(messageLabel).padTop(200).padBottom(10).row();
        table.add(chooseUsernameLabel).padBottom(20).row();
        table.add(usernameField).width(800).height(150).pad(20).padBottom(100).row();
        table.add(okButton).width(300).height(120).pad(20).padBottom(200).row();
        table.add(developedByLabel);

        stage.addActor(table);
    }

    @NotNull
    private TextButton getTextButton(TextField usernameField) {
        TextButton okButton = new TextButton("OK", skin);

        okButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                final String text = usernameField.getText().trim();
                if (text.isEmpty()) return;

                okButton.setDisabled(true);
                okButton.setText("CHECKING...");
                messageLabel.setText("");

                if (game.firebaseService != null) {
                    game.firebaseService.isUsernameAvailable(text, available -> Gdx.app.postRunnable(() -> {
                        if (available) {
                            game.username = text;
                            game.prefs.putString("username", text);
                            game.prefs.flush();
                            game.firebaseService.createUser(text);
                            game.setScreen(new ModeScreen(game));
                        } else {
                            okButton.setDisabled(false);
                            okButton.setText("OK");
                            messageLabel.setText("USERNAME ALREADY TAKEN!");
                        }
                    }));
                } else {
                    game.username = text;
                    game.setScreen(new ModeScreen(game));
                }
            }
        });
        return okButton;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(game.background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override public void pause() { }
    @Override public void resume() { }

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
