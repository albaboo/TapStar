package io.github.alba_game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

public class GameOverScreen implements Screen {

    private final Main game;
    private SpriteBatch batch;
    private BitmapFont font;
    private BitmapFont fontInfo;
    private Music backgroundMusic;
    private Music gameOverVoice;
    private float time = 0.6f;
    private int direction = 1;
    private final int score;
    private final int gameTime;
    private final int points;

    public GameOverScreen(Main game, int score, int points, int gameTime) {
        this.game = game;
        this.score = score;
        this.gameTime = gameTime;
        this.points = points;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(5f);
        fontInfo = new BitmapFont();
        fontInfo.getData().setScale(2.5f);
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("background_over.mp3"));
        backgroundMusic.setLooping(true);
        backgroundMusic.setVolume(0.5f);
        backgroundMusic.play();
        gameOverVoice = Gdx.audio.newMusic(Gdx.files.internal("game_over.mp3"));
        gameOverVoice.setLooping(true);
        gameOverVoice.setVolume(0.5f);
        gameOverVoice.play();
    }

    @Override
    public void render(float delta) {

        if (time > 1)
            direction = -1;
        else if (time < 0.6)
            direction = 1;

        time += (delta * direction);

        ScreenUtils.clear(0, 0, 0, 1);
        batch.begin();
        batch.setColor(1, 0.3f, 0.3f, time);
        batch.draw(game.background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.setColor(1, 1, 1, 1);
        GlyphLayout layout = new GlyphLayout();
        int screenWidth = Gdx.graphics.getWidth();
        int screenHeight = Gdx.graphics.getHeight();
        layout.setText(font, "GAME OVER");
        font.draw(batch, layout, (screenWidth - layout.width) / 2f, ((screenHeight + layout.height) / 2f) + 260f);
        layout.setText(font, "You have failed this universe,");
        font.draw(batch, layout, (screenWidth - layout.width) / 2f, ((screenHeight + layout.height) / 2f) + 130f);
        layout.setText(font, "now you must die");
        font.draw(batch, layout, (screenWidth - layout.width) / 2f, ((screenHeight + layout.height) / 2f) + 20);
        font.setColor(0, 1, 0, 1);
        layout.setText(font, "- Arrow Green");
        font.draw(batch, layout, (screenWidth - layout.width) / 2f, ((screenHeight + layout.height) / 2f) - 70f);
        font.setColor(1, 1, 1, 1);
        layout.setText(font, "Score: " + score);
        font.draw(batch, layout, (screenWidth - layout.width) / 2f, ((screenHeight + layout.height) / 2f) - 230f);
        layout.setText(font, "Points: " + points);
        font.draw(batch, layout, (screenWidth - layout.width) / 2f, ((screenHeight + layout.height) / 2f) - 330f);
        layout.setText(font, "Time: " + gameTime);
        font.draw(batch, layout, (screenWidth - layout.width) / 2f, ((screenHeight + layout.height) / 2f) - 430f);

        fontInfo.draw(batch, "Max Score: " + game.maxScore, 60, game.screenHeight - 30);
        fontInfo.draw(batch, "Score Time: " + (int) game.scoreTime + "s", 60, game.screenHeight - 90);

        fontInfo.draw(batch, "Max Points: " + game.maxPoints, (float) game.screenWidth / 2 - 60, game.screenHeight - 30);
        fontInfo.draw(batch, "Points Time: " + (int) game.pointsTime, (float) game.screenWidth / 2 - 60, game.screenHeight - 90);

        if (game.bestScorePerTime != 0) {
            layout.setText(fontInfo, "Best Score Per Time: " + game.bestScorePerTime);
            fontInfo.draw(batch, layout, game.screenWidth - layout.width - 60, game.screenHeight - 30);
            layout.setText(fontInfo, "Best Time: " + (int) game.bestTime + "s");
            fontInfo.draw(batch, layout, game.screenWidth - layout.width - 60, game.screenHeight - 90);
        }

        if (game.playOnline) {
            fontInfo.draw(batch, game.scoreUsername, 60, game.screenHeight - 150);
            fontInfo.draw(batch, game.pointsUsername, (float) game.screenWidth / 2 - 60, game.screenHeight - 150);
            fontInfo.draw(batch, game.efficiencyUsername, game.screenWidth - layout.width - 60, game.screenHeight - 150);
        }

        batch.end();

        if (Gdx.input.justTouched()) {
            game.setScreen(new ModeScreen(game));
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
        if (gameOverVoice != null) {
            gameOverVoice.stop();
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        fontInfo.dispose();
        if (backgroundMusic != null)
            backgroundMusic.dispose();
        if (gameOverVoice != null)
            gameOverVoice.dispose();
    }
}
