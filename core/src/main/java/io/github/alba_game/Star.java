package io.github.alba_game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

public class Star {
    Texture texture = new Texture("star.png");
    TextureRegion[] frames;
    Animation<TextureRegion> animation;
    public float hue = 0f;
    float positionX;
    float positionY;
    float velocityX = 1f;
    float velocityY = 1f;
    float stateTime = 0f;
    int frameWidth = 0;
    int frameHeight = 0;
    final int COLUMNS = 11;
    int screenWidth;
    int screenHeight;
    static float velocityXMax = 1f;
    static float velocityYMax = 1f;
    static final float SPEED_LIMIT = 25f;

    public Star(int screenWidth, int screenHeight) {
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

    public void update() {
        stateTime += Gdx.graphics.getDeltaTime();

        positionX += velocityX;
        positionY += velocityY;

        if (positionX <= 0 || positionX + frameWidth >= screenWidth)
            velocityX *= -1;

        if (positionY <= 0 || positionY + frameHeight >= screenHeight)
            velocityY *= -1;

        velocityX += 0.005f * Math.signum(velocityX);
        velocityY += 0.01f * Math.signum(velocityY);

        if (Math.abs(velocityX) > SPEED_LIMIT)
            velocityX = SPEED_LIMIT * Math.signum(velocityX);

        if (Math.abs(velocityY) > SPEED_LIMIT)
            velocityY = SPEED_LIMIT * Math.signum(velocityY);

        if (Math.abs(velocityX) > velocityXMax)
            velocityXMax = Math.signum(velocityX);

        if (Math.abs(velocityY) > velocityXMax)
            velocityXMax = Math.signum(velocityY);

    }

    public void draw(SpriteBatch batch) {
        hue += 100f * Gdx.graphics.getDeltaTime();
        if (hue > 360f) hue -= 360f;
        float[] rgb = hsvToRgb(hue, 1f, 1f);
        batch.setColor(rgb[0], rgb[1], rgb[2], 1f);

        TextureRegion currentFrame = animation.getKeyFrame(stateTime, true);
        batch.draw(currentFrame, positionX, positionY);
        batch.setColor(1f, 1f, 1f, 1f);
    }

    private float[] hsvToRgb(float h, float s, float v) {
        float c = v * s;
        float x = c * (1 - Math.abs((h / 60f) % 2 - 1));
        float m = v - c;
        float r1 = 0, g1 = 0, b1 = 0;

        if (h < 60) {
            r1 = c;
            g1 = x;
            b1 = 0;
        } else if (h < 120) {
            r1 = x;
            g1 = c;
            b1 = 0;
        } else if (h < 180) {
            r1 = 0;
            g1 = c;
            b1 = x;
        } else if (h < 240) {
            r1 = 0;
            g1 = x;
            b1 = c;
        } else if (h < 300) {
            r1 = x;
            g1 = 0;
            b1 = c;
        } else {
            r1 = c;
            g1 = 0;
            b1 = x;
        }

        return new float[]{r1 + m, g1 + m, b1 + m};
    }

    public boolean contains(float x, float y) {
        float margin = 30f;

        Rectangle bounds = new Rectangle(
            positionX - margin,
            positionY - margin,
            frameWidth + margin,
            frameHeight + margin
        );

        return bounds.contains(x, y);
    }

    public void randomPosition() {

        positionX = MathUtils.random(0, screenWidth - frameWidth);
        positionY = MathUtils.random(0, screenHeight - frameHeight);
    }

    public void defaultVelocity(boolean max) {
        velocityX = max ? velocityXMax : velocityXMax / 2;
        velocityY = max ? velocityYMax : velocityYMax / 2;
    }

    public void randomVelocity() {

        float speed = (float) Math.sqrt(velocityX * velocityX + velocityY * velocityY);
        float angle = MathUtils.random(0f, 2 * MathUtils.PI);

        velocityX = speed * MathUtils.cos(angle);
        velocityY = speed * MathUtils.sin(angle);
    }

    public void increaseSpeed() {
        velocityX += 0.5f * Math.signum(velocityX);
        velocityY += 0.25f * Math.signum(velocityY);
    }

    public void dispose() {
        texture.dispose();
    }

    public boolean collides(Star other) {

        float margin = -30f;

        Rectangle r1 = new Rectangle(
            positionX - margin,
            positionY - margin,
            frameWidth + margin,
            frameHeight + margin
        );
        Rectangle r2 = new Rectangle(
            other.positionX - margin,
            other.positionY - margin,
            other.frameWidth + margin,
            other.frameHeight + margin
        );

        return r1.overlaps(r2);
    }
}
