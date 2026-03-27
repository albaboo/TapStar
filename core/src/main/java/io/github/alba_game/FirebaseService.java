package io.github.alba_game;

public interface FirebaseService {
    void createUser(String username);

    interface OnRecordsLoadedListener {
        void onLoaded(int value, float time, String username);
    }

    void getRecord(String path, OnRecordsLoadedListener listener);

    void updateGlobalRecord(String path, int value, float time, String username);

    interface ConnectionListener {
        void onResult(boolean connected);
    }

    void checkConnection(ConnectionListener listener);

    interface UserCheckListener {
        void onResult(boolean exists);
    }

    void isUsernameAvailable(String username, UserCheckListener listener);
}
