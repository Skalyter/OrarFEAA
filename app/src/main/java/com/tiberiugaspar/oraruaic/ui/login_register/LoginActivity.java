package com.tiberiugaspar.oraruaic.ui.login_register;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tiberiugaspar.oraruaic.R;
import com.tiberiugaspar.oraruaic.ui.MainActivity;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private EditText email, parola;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email = findViewById(R.id.editTextEmail);
        parola = findViewById(R.id.editTextPassword);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void onRegisterClick(View view) {
        startActivity(new Intent(this, RegisterActivity.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.stay);
        finish();
    }

    public void onLogInClick(View view) {
        if (validareCampuri()) {
            String mEmail = email.getText().toString(), mParola = parola.getText().toString();
            firebaseAuth.signInWithEmailAndPassword(mEmail, mParola)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                                DocumentReference docRef = firestore.collection("users").document(firebaseAuth.getUid());
                                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot snapshot = task.getResult();
                                            if (snapshot.exists()) {
                                                Log.d(TAG, "signInWithEmail:success");
                                                if (snapshot.get("titulatura") != null) {
                                                    saveStringSharedPreference(getString(R.string.saved_user_type),
                                                            getString(R.string.user_profesor));
                                                    saveStringSharedPreference(getString(R.string.saved_user_name),
                                                            String.format("%s %s %s", snapshot.get("titulatura"),
                                                                    snapshot.get("nume"), snapshot.get("prenume")));
                                                    saveStringSharedPreference(getString(R.string.saved_user_matricol),
                                                            String.format("%s", snapshot.get("email")));
                                                } else {
                                                    saveStringSharedPreference(getString(R.string.saved_user_type),
                                                            getString(R.string.user_student));
                                                    saveStringSharedPreference(getString(R.string.saved_user_name),
                                                            String.format("%s %s", snapshot.get("nume"), snapshot.get("prenume")));
                                                    saveStringSharedPreference(getString(R.string.saved_user_matricol),
                                                            String.format("%s", snapshot.get("matricol")));
                                                }
                                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                                overridePendingTransition(R.anim.slide_in_right, R.anim.stay);
                                                finish();
                                            } else {
                                                email.setError("");
                                                parola.setError("");
                                                Toast.makeText(LoginActivity.this, "Adresa de email sau parola incorecta",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                    }
                                });
                            }
                        }
                    });
        }
    }

    private boolean validareCampuri() {
        boolean valid = true;
        if (!email.getText().toString().matches("^[A-z0-9._%+-]+@[A-z0-9.-]+\\.[A-z]{2,6}$")) {
            valid = false;
            email.setError("Introduceti o adresa de email valida");
        }
        if (parola.getText() == null || parola.getText().toString().length() < 7) {
            valid = false;
            parola.setError("Introduceti o parola valida");
        }
        return valid;
    }

    private void saveStringSharedPreference(String key, String value) {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

}