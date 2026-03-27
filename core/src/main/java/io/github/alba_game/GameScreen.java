package io.github.alba_game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;

public class GameScreen implements Screen {

    private final Main game;
    private SpriteBatch batch;
    private Music backgroundMusic;
    public Array<Star> stars = new Array<>();
    public Array<BubbleStar> bubbleStars = new Array<>();
    public Array<Collision> collisions = new Array<>();
    private int score = 0;
    private int points = 0;
    private int efficiency = 0;
    private float efficiencyTime = 0f;
    private float gameTime = 0f;
    private float spawnTimer = 0f;
    private float spawnInterval = 5f;
    private BitmapFont font;
    private BitmapFont fontUser;
    private Sound crashSound;
    private Sound bubbleSound;

    public GameScreen(Main game) {
        this.game = game;
    }


    @Override
    public void show() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(2.5f);
        fontUser = new BitmapFont();
        fontUser.getData().setScale(5f);

        crashSound = Gdx.audio.newSound(Gdx.files.internal("crash.wav"));
        bubbleSound = Gdx.audio.newSound(Gdx.files.internal("bubble.wav"));

        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("background.mp3"));
        backgroundMusic.setLooping(true);
        backgroundMusic.setVolume(0.5f);
        backgroundMusic.play();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        if ((int) efficiencyTime < (int) (efficiencyTime + delta)) {
            efficiency = 0;
            efficiencyTime = 0;
        }

        gameTime += delta;
        spawnTimer += delta;

        if (efficiency > 0)
            efficiencyTime += delta;

        if (spawnTimer >= spawnInterval) {
            Star newStar = new Star(game.screenWidth, game.screenHeight);
            newStar.defaultVelocity(false);
            newStar.randomPosition();
            newStar.randomVelocity();
            stars.add(newStar);

            spawnTimer = 0f;
            spawnInterval += 5f;
        }
        if (Gdx.input.justTouched()) {

            float touchX = Gdx.input.getX();
            float touchY = game.screenHeight - Gdx.input.getY();


            boolean hit = false;
            for (Star star : stars) {
                if (star.contains(touchX, touchY)) {
                    hit = true;

                    score++;
                    efficiency++;


                    float actualEfficiency = score / gameTime;
                    float bestEfficiency = game.bestScorePerTime / game.bestTime;

                    points += (int) (actualEfficiency * 5);

                    if (points > game.maxPoints) {
                        game.maxPoints = points;
                        game.pointsTime = gameTime;
                    }

                    if (gameTime >= 1 && actualEfficiency > bestEfficiency || ((int) actualEfficiency == (int) bestEfficiency && score > game.bestScorePerTime)) {
                        game.bestScorePerTime = score;
                        game.bestTime = gameTime;
                    }

                    if (score > game.maxScore) {
                        game.maxScore = score;
                        game.scoreTime = gameTime;
                    }

                    game.updateRecordsIfBetter();

                    bubbleStars.add(new BubbleStar(star, bubbleSound));
                    stars.removeValue(star, true);
                    break;
                }
            }

            if (!hit)
                game.setScreen(new GameOverScreen(game, score, points, (int) gameTime));

        } else {
            for (Star star : stars)
                star.update();
        }

        for (int i = 0; i < stars.size; i++) {
            for (int j = i + 1; j < stars.size; j++) {

                Star s1 = stars.get(i);
                Star s2 = stars.get(j);

                if (s1.collides(s2)) {
                    float collisionX = (s1.positionX + s2.positionX) / 2f + s1.frameWidth / 2f;
                    float collisionY = (s1.positionY + s2.positionY) / 2f + s1.frameHeight / 2f;
                    collisions.add(new Collision(collisionX, collisionY, true, crashSound));

                    s1.dispose();
                    s2.dispose();

                    stars.removeIndex(j);
                    stars.removeIndex(i);

                    spawnInterval -= 5f;

                    points -= 25;

                    if (points <= 0)
                        game.setScreen(new GameOverScreen(game, score, points, (int) gameTime));

                    i--;
                    break;
                }


            }
        }

        for (int i = 0; i < collisions.size; i++) {
            Collision c = collisions.get(i);
            c.update(Gdx.graphics.getDeltaTime());
            if (c.finished) {
                c.dispose();
                collisions.removeIndex(i);
                i--;
            }
        }

        for (int i = 0; i < bubbleStars.size; i++) {
            BubbleStar bs = bubbleStars.get(i);
            bs.update(delta);
            if (bs.finished) {
                bs.revive();
                stars.add(bs.star);
                bs.disposeBubble();
                bubbleStars.removeIndex(i);
                i--;
            }
        }


        if (stars.size == 0 && bubbleStars.size == 0) {
            Star newStar = new Star(game.screenWidth, game.screenHeight);
            newStar.defaultVelocity(true);
            newStar.randomPosition();
            newStar.randomVelocity();
            stars.add(newStar);
            spawnInterval = 5f;
            spawnTimer = 0f;
        }

        batch.begin();
        batch.draw(game.background, 0, 0, game.screenWidth, game.screenHeight);
        GlyphLayout layout = new GlyphLayout();

        for (Collision collision : collisions)
            collision.draw(batch);
        for (BubbleStar bs : bubbleStars)
            bs.draw(batch);
        for (Star star : stars)
            star.draw(batch);

        font.draw(batch, "Score: " + score, 60, game.screenHeight - 60);
        font.draw(batch, "Max Score: " + game.maxScore, 60, game.screenHeight - 120);
        font.draw(batch, "Score Time: " + (int) game.scoreTime + "s", 60, game.screenHeight - 180);

        font.draw(batch, "Points: " + points, (float) game.screenWidth / 2 - 60, game.screenHeight - 60);
        font.draw(batch, "Max Points: " + game.maxPoints, (float) game.screenWidth / 2 - 60, game.screenHeight - 120);
        font.draw(batch, "Points Time: " + (int) game.pointsTime, (float) game.screenWidth / 2 - 60, game.screenHeight - 180);

        layout.setText(font, "Efficiency: " + efficiency + " Score/s");
        font.draw(batch, layout, game.screenWidth - layout.width - 60, game.screenHeight - 60);
        if (game.bestScorePerTime != 0) {
            layout.setText(font, "Best Score Per Time: " + game.bestScorePerTime);
            font.draw(batch, layout, game.screenWidth - layout.width - 60, game.screenHeight - 120);
            layout.setText(font, "Best Time: " + (int) game.bestTime + "s");
            font.draw(batch, layout, game.screenWidth - layout.width - 60, game.screenHeight - 180);
        }

        if (game.playOnline) {
            font.draw(batch, game.scoreUsername, 60, game.screenHeight - 240);
            font.draw(batch, game.pointsUsername, (float) game.screenWidth / 2 - 60, game.screenHeight - 240);
            font.draw(batch, game.efficiencyUsername, game.screenWidth - layout.width - 60, game.screenHeight - 240);
        }

        font.draw(batch, "Time: " + (int) gameTime + "s", game.screenWidth - (float) game.screenWidth / 2 - 60, 60);
        font.draw(batch, game.playOnline ? "Online" : "Offline", game.screenWidth - (float) game.screenWidth / 2 - 200, 60);
        fontUser.draw(batch, game.username, 60, 100);
        batch.end();
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
        for (Star star : stars)
            star.dispose();
        for (Collision collision : collisions)
            collision.dispose();
        for (BubbleStar bs : bubbleStars)
            bs.dispose();
        font.dispose();
        fontUser.dispose();
        crashSound.dispose();
        bubbleSound.dispose();
        if (backgroundMusic != null)
            backgroundMusic.dispose();
    }
}
