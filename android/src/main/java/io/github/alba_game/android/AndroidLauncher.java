package io.github.alba_game.android;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.firebase.database.FirebaseDatabase;

import io.github.alba_game.Main;

/**
 * Launches the Android application.
 */
public class AndroidLauncher extends AndroidApplication {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            FirebaseDatabase.getInstance("https://tapstar-95229-default-rtdb.europe-west1.firebasedatabase.app/");
        } catch (Exception e) {
            // Si ya está inicializada o falla por otra razón, continuamos
        }

        AndroidApplicationConfiguration configuration = new AndroidApplicationConfiguration();
        configuration.useImmersiveMode = true;

        AndroidFirebaseService firebaseService = new AndroidFirebaseService();
        Main game = new Main(firebaseService);

        initialize(game, configuration);

        getWindow().getDecorView().setSystemUiVisibility(
            android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | android.view.View.SYSTEM_UI_FLAG_FULLSCREEN
                | android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
    }
}
