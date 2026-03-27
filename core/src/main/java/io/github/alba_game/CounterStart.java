package io.github.alba_game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class CounterStart {
    Texture texture = new Texture("starting.png");
    TextureRegion[] frames;
    Animation<TextureRegion> animation;

    float positionX;
    float positionY;
    final int COLUMNS = 3;
    int frameWidth = 0;
    int frameHeight = 0;
    int screenWidth;
    int screenHeight;
    float stateTime = 0f;

    public CounterStart(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        TextureRegion[][] tmp = TextureRegion.split(texture, texture.getWidth() / COLUMNS, texture.getHeight());
        frames = new TextureRegion[COLUMNS];
        System.arraycopy(tmp[0], 0, frames, 0, COLUMNS);

        animation = new Animation<>(0.1f, frames);
        frameWidth = texture.getWidth() / COLUMNS;
        frameHeight = texture.getHeight();

        positionX = (screenWidth - frameWidth) / 2f;
        positionY = (screenHeight - frameHeight) / 2f;

    }

    public void update(float stateTime) {
        this.stateTime = stateTime - 1;
    }

    public void draw(SpriteBatch batch) {
        TextureRegion currentFrame = animation.getKeyFrame(stateTime, true);
        batch.draw(currentFrame, positionX, positionY);
    }

    public void dispose() {
        texture.dispose();
    }
}
