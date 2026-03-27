package io.github.alba_game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Collision {
    private final Texture texture;
    public final Animation<TextureRegion> animation;
    public float stateTime = 0f;
    public Vector2 position;
    public int frameWidth;
    public int frameHeight;
    public boolean finished = false;
    public boolean collide;

    public Collision(float x, float y, boolean collide, Sound sound) {
        this.collide = collide;

        if (sound != null)
            sound.play(2f);

        Gdx.input.vibrate(200);
        TextureRegion[] frames;
        if (collide) {
            int COLUMNS = 6;
            texture = new Texture("crash.png");
            TextureRegion[][] tmp = TextureRegion.split(texture, texture.getWidth() / COLUMNS, texture.getHeight());
            frames = new TextureRegion[COLUMNS];
            System.arraycopy(tmp[0], 0, frames, 0, COLUMNS);
            animation = new Animation<>(0.08f, frames);
            frameWidth = texture.getWidth() / COLUMNS;
            frameHeight = texture.getHeight();
            position = new Vector2(x - frameWidth / 2f, y - frameHeight / 2f);
        } else {
            texture = new Texture("bubble.png");

            int ROWS = 2;
            int COLUMNS = 4;
            frames = new TextureRegion[ROWS * COLUMNS];

            TextureRegion[][] tmp = TextureRegion.split(
                texture,
                texture.getWidth() / COLUMNS,
                texture.getHeight() / ROWS
            );

            int index = 0;
            for (int row = 0; row < ROWS; row++) {
                for (int col = 0; col < COLUMNS; col++) {
                    frames[index++] = tmp[row][col];
                }
            }

            for (int i = 0; i < frames.length / 2; i++) {
                TextureRegion temp = frames[i];
                frames[i] = frames[frames.length - 1 - i];
                frames[frames.length - 1 - i] = temp;
            }

            TextureRegion[] framesInv = new TextureRegion[frames.length];
            for (int i = 0; i < frames.length; i++) {
                framesInv[i] = frames[frames.length - 1 - i];
            }

            TextureRegion[] framesCombined = new TextureRegion[frames.length * 2];
            System.arraycopy(frames, 0, framesCombined, 0, frames.length);
            System.arraycopy(framesInv, 0, framesCombined, frames.length, framesInv.length);

            animation = new Animation<>(0.03f, framesCombined);

            frameWidth = texture.getWidth() / COLUMNS;
            frameHeight = texture.getHeight() / ROWS;

            position = new Vector2(x, y);
        }

    }

    public void update(float delta) {
        stateTime += delta;
        if (animation.isAnimationFinished(stateTime)) {
            finished = true;
        }
    }

    public void draw(SpriteBatch batch) {
        if (!finished) {
            TextureRegion currentFrame = animation.getKeyFrame(stateTime, false);
            if (collide)
                batch.draw(currentFrame, position.x, position.y);
            else {
                float scale = 0.5f;

                float drawWidth = frameWidth * scale;
                float drawHeight = frameHeight * scale;

                batch.draw(
                    currentFrame,
                    position.x - drawWidth / 2f,
                    position.y - drawHeight / 2f,
                    drawWidth,
                    drawHeight
                );
            }
        }
    }

    public void dispose() {
        texture.dispose();
    }
}
