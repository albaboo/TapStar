package io.github.alba_game.android;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.github.alba_game.FirebaseService;
import io.github.alba_game.SingleRecord;

public class AndroidFirebaseService implements FirebaseService {

    private static final String TAG = "FirebaseService";
    private static final String DB_URL = "https://tapstar-95229-default-rtdb.europe-west1.firebasedatabase.app";

    private DatabaseReference getDbRef(String path) {
        return FirebaseDatabase.getInstance(DB_URL).getReference(path);
    }

    @Override
    public void createUser(String username) {
        Log.d(TAG, "Intentando crear/validar usuario: " + username);
        String safeName = username.replace(".", "_").replace("#", "_").replace("$", "_").replace("[", "_").replace("]", "_");

        getDbRef("users").child(safeName).setValue(true)
            .addOnSuccessListener(aVoid -> Log.d(TAG, "Usuario guardado en Firebase: " + safeName))
            .addOnFailureListener(e -> Log.e(TAG, "Error al guardar usuario", e));
    }

    @Override
    public void getRecord(String path, OnRecordsLoadedListener listener) {
        getDbRef("records").child(path).get()
            .addOnSuccessListener(snapshot -> {
                SingleRecord record = snapshot.getValue(SingleRecord.class);
                if (record != null) {
                    Log.d(TAG, "Record recuperado [" + path + "]: " + record.value);
                    listener.onLoaded(record.value, record.time, record.username);
                }
            })
            .addOnFailureListener(e -> Log.e(TAG, "Error al leer récord global", e));
    }

    @Override
    public void updateGlobalRecord(String path, int value, float time, String username) {
        DatabaseReference recordsRef = getDbRef("records");
        recordsRef.child(path).get().addOnSuccessListener(snapshot -> {
            SingleRecord current = snapshot.getValue(SingleRecord.class);

            boolean isBetter = false;

            if (current == null) {
                Log.d(TAG, "Base de datos vacía. Guardando primer récord global para " + path);
                isBetter = true;
            } else {
                if (path.equals("bestScorePerTime")) {
                    if (value > current.value || (value == current.value && time < current.time)) {
                        isBetter = true;
                    }
                } else {
                    if (value > current.value) {
                        isBetter = true;
                    }
                }
            }

            if (isBetter) {
                Log.d(TAG, "¡NUEVO RÉCORD GLOBAL! Guardando: " + value + " por " + username);
                recordsRef.child(path).setValue(new SingleRecord(value, time, username))
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Récord actualizado con éxito en la nube."))
                    .addOnFailureListener(e -> Log.e(TAG, "Error al subir el nuevo récord", e));
            }
        });
    }

    @Override
    public void checkConnection(ConnectionListener listener) {
        DatabaseReference connectedRef = FirebaseDatabase.getInstance(DB_URL).getReference(".info/connected");
        connectedRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class) != null && Boolean.TRUE.equals(snapshot.getValue(Boolean.class));
                listener.onResult(connected);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onResult(false);
            }
        });
    }

    @Override
    public void isUsernameAvailable(String username, UserCheckListener listener) {
        String safeName = username.replace(".", "_").replace("#", "_").replace("$", "_").replace("[", "_").replace("]", "_");
        getDbRef("users").child(safeName).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                boolean exists = task.getResult().exists();
                listener.onResult(!exists);
            } else {
                listener.onResult(false);
            }
        });
    }
}
