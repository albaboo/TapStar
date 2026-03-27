package io.github.alba_game;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class BubbleStar {

    public Star star;
    public Collision collision;
    public boolean finished = false;

    public BubbleStar(Star star, Sound sound) {
        this.star = star;
        star.stateTime = 0;
        float centerX = star.positionX + star.frameWidth / 2f;
        float centerY = star.positionY + star.frameHeight / 2f;
        this.collision = new Collision(centerX, centerY, false, sound);
    }

    public void update(float delta) {
        collision.update(delta);
        if (collision.finished) {
            finished = true;
        }
    }

    public void draw(SpriteBatch batch) {
        float halfDuration = collision.animation.getAnimationDuration() / 2f;
        if (collision.stateTime < halfDuration)
            star.draw(batch);
        collision.draw(batch);
    }

    public void dispose() {
        collision.dispose();
        star.dispose();
    }

    public void disposeBubble() {
        collision.dispose();
    }

    public void revive() {
        star.increaseSpeed();
        star.randomPosition();
        star.randomVelocity();
    }
}
