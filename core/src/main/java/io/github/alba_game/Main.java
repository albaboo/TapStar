package io.github.alba_game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Texture;

/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms.
 */
public class Main extends Game {
    public FirebaseService firebaseService;
    protected Preferences prefs;
    protected Texture background;
    protected int screenWidth;
    protected int screenHeight;
    protected boolean playOnline = true;
    protected int maxScore = 0;
    protected float scoreTime = 0f;
    protected String scoreUsername = "";
    protected int maxPoints = 0;
    protected float pointsTime = 0;
    protected String pointsUsername = "";
    protected int bestScorePerTime = 0;
    protected float bestTime = Float.MAX_VALUE;
    protected String efficiencyUsername = "";
    protected String username;

    public Main(FirebaseService firebaseService) {
        this.firebaseService = firebaseService;
    }

    @Override
    public void create() {
        prefs = Gdx.app.getPreferences("TapStarByAlba-boo");
        username = prefs.getString("username", null);

        background = new Texture("bg.jpg");
        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();

        if (username == null)
            setScreen(new UsernameScreen(this));
        else
            setScreen(new ModeScreen(this));
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        if (background != null) background.dispose();
        super.dispose();
    }

    protected void loadRecords() {
        if (playOnline && firebaseService != null)
            loadOnlineRecords();
        else
            loadOfflineRecords();
    }

    protected void loadOnlineRecords() {
        if (playOnline && firebaseService != null) {
            firebaseService.getRecord("maxScore", (value, time, username) -> {
                if (value > maxScore) {
                    maxScore = value;
                    scoreTime = time;
                    scoreUsername = username;
                }
            });

            firebaseService.getRecord("maxPoints", (value, time, username) -> {
                if (value > maxPoints) {
                    maxPoints = value;
                    pointsTime = time;
                    pointsUsername = username;
                }
            });

            firebaseService.getRecord("bestScorePerTime", (value, time, username) -> {
                if (value > bestScorePerTime || (value == bestScorePerTime && time < bestTime)) {
                    bestScorePerTime = value;
                    bestTime = time;
                    efficiencyUsername = username;
                }
            });
        }
    }

    protected void loadOfflineRecords() {
        maxScore = prefs.getInteger("maxScore", 0);
        scoreTime = prefs.getFloat("scoreTime", 0f);
        maxPoints = prefs.getInteger("maxPoints", 0);
        pointsTime = prefs.getFloat("pointsTime", 0f);
        bestScorePerTime = prefs.getInteger("bestScorePerTime", 0);
        bestTime = prefs.getFloat("bestTime", Float.MAX_VALUE);
    }

    private void saveOfflineRecords() {
        prefs.putInteger("maxScore", maxScore);
        prefs.putFloat("scoreTime", scoreTime);
        prefs.putInteger("maxPoints", maxPoints);
        prefs.putFloat("pointsTime", pointsTime);
        prefs.putInteger("bestScorePerTime", bestScorePerTime);
        prefs.putFloat("bestTime", bestTime);
        prefs.flush();
    }

    protected void saveOnlineRecords() {
        firebaseService.updateGlobalRecord("maxScore", this.maxScore, this.scoreTime, this.username);
        firebaseService.updateGlobalRecord("maxPoints", this.maxPoints, this.pointsTime, this.username);
        firebaseService.updateGlobalRecord("bestScorePerTime", this.bestScorePerTime, this.bestTime, this.username);
    }

    public void updateRecordsIfBetter() {
        if (username == null || username.isEmpty()) return;

        if (playOnline && firebaseService != null) {
            saveOnlineRecords();
        } else
            saveOfflineRecords();
    }
}
