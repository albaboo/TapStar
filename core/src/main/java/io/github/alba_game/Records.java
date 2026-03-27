package io.github.alba_game;

public class Records {
    public SingleRecord maxScore;
    public SingleRecord maxPoints;
    public SingleRecord bestScorePerTime;

    public Records() {
        maxScore = new SingleRecord();
        maxPoints = new SingleRecord();
        bestScorePerTime = new SingleRecord();
    }
}
