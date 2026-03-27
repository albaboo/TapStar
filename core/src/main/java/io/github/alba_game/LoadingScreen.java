package io.github.alba_game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

public class LoadingScreen implements Screen {

    private final Main game;
    private Music backgroundMusic;
    private CounterStart counter;
    private float time = 0;
    private SpriteBatch batch;

    public LoadingScreen(Main game) {
        this.game = game;
        game.loadRecords();
    }


    @Override
    public void show() {
        batch = new SpriteBatch();
        int screenWidth = Gdx.graphics.getWidth();
        int screenHeight = Gdx.graphics.getHeight();
        counter = new CounterStart(screenWidth, screenHeight);
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("countdown.mp3"));
        backgroundMusic.setLooping(true);
        backgroundMusic.setVolume(0.5f);
        backgroundMusic.play();
    }

    @Override
    public void render(float delta) {
        time += delta;

        ScreenUtils.clear(0, 0, 0, 1);

        if (time < 3f) {
            counter.update(3 - (int) time);

            batch.begin();
            batch.setColor(0.5f, 0.5f, 0.5f, 1);
            batch.draw(game.background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            batch.setColor(1, 1, 1, 1);
            counter.draw(batch);
            batch.end();
        } else {
            game.setScreen(new GameScreen(game));
        }
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        if (backgroundMusic != null) {
            backgroundMusic.stop();
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        if (backgroundMusic != null)
            backgroundMusic.dispose();
    }
}
