package com.tiberiugaspar.oraruaic.ui.login_register;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.tiberiugaspar.oraruaic.R;
import com.tiberiugaspar.oraruaic.model.Grupa;
import com.tiberiugaspar.oraruaic.model.Profesor;
import com.tiberiugaspar.oraruaic.model.Specializare;
import com.tiberiugaspar.oraruaic.model.Student;
import com.tiberiugaspar.oraruaic.ui.MainActivity;

import java.util.ArrayList;
import java.util.List;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;

import static com.tiberiugaspar.oraruaic.util.DateUtil.TAG;
import static com.tiberiugaspar.oraruaic.util.DateUtil.getAnPromotie;

public class RegisterActivity extends AppCompatActivity {

    Spinner facultati, specializare, tipUtilizator, nivelStudii, anStudiu, grupa, titulatura;
    EditText nume, prenume, email, matricol, parola, confirmareParola;
    CircularProgressButton register;
    LinearLayout studentLayout, profesorLayout;
    ArrayAdapter<CharSequence> adapter, adapterSpecializare, adapterGrupe;
    FirebaseFirestore db;

    private final List<Specializare> specializari = new ArrayList<>();
    private final List<Grupa> grupe = new ArrayList<>();

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        findViewsById();
        initializareSpinner();
    }

    public void onLoginClick(View view) {
        startActivity(new Intent(this, LoginActivity.class));
        overridePendingTransition(R.anim.slide_in_left, R.anim.stay);
        finish();
    }

    private void findViewsById() {
        facultati = findViewById(R.id.spinner_faculty);
        specializare = findViewById(R.id.spinner_specializare);
        tipUtilizator = findViewById(R.id.spinner_user_type);
        nivelStudii = findViewById(R.id.spinner_nivel_studii);
        anStudiu = findViewById(R.id.spinner_anul);
        grupa = findViewById(R.id.spinner_grupa);
        nume = findViewById(R.id.editTextNume);
        prenume = findViewById(R.id.editTextPrenume);
        email = findViewById(R.id.editTextEmail);
        matricol = findViewById(R.id.editTextMatricol);
        parola = findViewById(R.id.editTextPassword);
        confirmareParola = findViewById(R.id.editTextPasswordAgain);
        register = findViewById(R.id.cirRegisterButton);
        studentLayout = findViewById(R.id.student_fields);
        profesorLayout = findViewById(R.id.profesor_fields);
        titulatura = findViewById(R.id.spinner_titulatura);

        adapterSpecializare = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        adapterSpecializare.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        specializare.setAdapter(adapterSpecializare);

        adapterGrupe = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        adapterGrupe.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        grupa.setAdapter(adapterGrupe);

        nivelStudii.setOnItemSelectedListener(onNivelStudiiChanged);
        specializare.setOnItemSelectedListener(actualizareGrupaListener);
        anStudiu.setOnItemSelectedListener(actualizareGrupaListener);
    }

    private void initializareSpinner() {

        tipUtilizator.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    studentLayout.setVisibility(View.VISIBLE);
                    profesorLayout.setVisibility(View.GONE);
                } else if (position == 1) {
                    profesorLayout.setVisibility(View.VISIBLE);
                    studentLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    final OnItemSelectedListener onNivelStudiiChanged = new OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (nivelStudii.getSelectedItem().toString().equals("Licență")) {
                db.collection("specializari")
                        .whereEqualTo("eNivelStudiu", "LICENTA").get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                adapterSpecializare.clear();
                                for (DocumentSnapshot snap : queryDocumentSnapshots) {
                                    Specializare specializare = snap.toObject(Specializare.class);
                                    specializari.add(specializare);
                                    adapterSpecializare.add(specializare.getDenumire());
                                }
                                specializare.setAdapter(adapterSpecializare);
                            }
                        });
            } else if (nivelStudii.getSelectedItem().toString().equals("Master")) {
                changeAdapter(anStudiu, R.array.ani_studii_master);
                changeAdapter(specializare, R.array.lista_specializari_master);
                changeGrupaAdapter();
            } else {
                findViewById(R.id.campuri_licenta_master).setVisibility(View.GONE);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    final OnItemSelectedListener actualizareGrupaListener = new OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            changeGrupaAdapter();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    private void changeAdapter(Spinner spinner, int resourceId) {
        adapter = ArrayAdapter.createFromResource(this, resourceId, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        if (findViewById(R.id.campuri_licenta_master).getVisibility() == View.GONE) {
            findViewById(R.id.campuri_licenta_master).setVisibility(View.VISIBLE);
        }
    }

    private void changeGrupaAdapter() {
        grupe.clear();
        adapterGrupe.clear();
        if (!specializari.isEmpty())
            db.collection("grupe")
                    .whereEqualTo("promotia", getAnPromotie(anStudiu.getSelectedItemPosition() + 1))
                    .whereEqualTo("idSpecializare", specializari.get(specializare.getSelectedItemPosition()).getIdSpecializare())
                    .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        Grupa grupa = documentSnapshot.toObject(Grupa.class);
                        grupe.add(grupa);
                        adapterGrupe.add(grupa.getDenumire());
                    }
                }
            });
    }

    public void onRegisterClick(View view) {
        if (validareCampuri()) {
            final String mEmail = email.getText().toString();
            String mParola = parola.getText().toString();

            firebaseAuth.createUserWithEmailAndPassword(mEmail, mParola)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                Log.d(TAG, "createUserWithEmail:success", task.getException());

                                final FirebaseUser currentUser = firebaseAuth.getCurrentUser();

                                if (tipUtilizator.getSelectedItem().toString().equals("Student")) {

                                    Student student =
                                            new Student(currentUser.getUid(), nume.getText().toString(),
                                                    prenume.getText().toString(),
                                                    mEmail, matricol.getText().toString(),
                                                    grupe.get(grupa.getSelectedItemPosition()).getIdGrupa());
                                    student.setUtilizatorNevalidat(true);

                                    db.collection("users").document(currentUser.getUid())
                                            .set(student).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "onSuccess: DocumentSnapshot successfully written with ID " + currentUser.getUid());
                                            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                            SharedPreferences.Editor editor = sharedPref.edit();
                                            editor.putString(getString(R.string.saved_user_type), getString(R.string.user_student));
                                            editor.apply();
                                            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                            finish();

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error adding document", e);
                                        }
                                    });

                                } else{
                                    Profesor profesor = new Profesor(
                                            currentUser.getUid(),nume.getText().toString(),
                                            prenume.getText().toString(),
                                            email.getText().toString(),
                                            titulatura.getSelectedItem().toString());
                                    profesor.setUtilizatorNevalidat(true);

                                    db.collection("users").document(currentUser.getUid())
                                            .set(profesor).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                            SharedPreferences.Editor editor = sharedPref.edit();
                                            editor.putString(getString(R.string.saved_user_type), getString(R.string.user_student));
                                            editor.apply();
                                            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                            finish();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d(TAG, "onFailure: " + e.getMessage());
                                        }
                                    });

                                }
                            } else {
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            }
                        }
                    });
        } else {
            Toast.makeText(this, "Ceva nu a functionat", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validareCampuri() {
        boolean valid = true;
        if (tipUtilizator.getSelectedItem().equals("Student")) {
            if (!matricol.getText().toString().trim().matches("3104[0-9]{5}[A-z]{3}[0-9]{6}")) {
                valid = false;
                matricol.setError("Introduceti un matricol valid!");
            }
        }
        if (nume.getText() == null || nume.getText().toString().trim().equals("") || nume.getText().toString().length() <= 0) {
            nume.setError("Introduceti numele!");
            valid = false;
        }
        if (prenume.getText() == null || prenume.getText().toString().trim().equals("") || prenume.getText().toString().length() <= 0) {
            prenume.setError("Introduceti prenumele!");
            valid = false;
        }
        if (!email.getText().toString().matches("^[A-z0-9._%+-]+@[A-z0-9.-]+\\.[A-z]{2,6}$")) {
            email.setError("Introduceti o adresa valida de email");
            valid = false;
        }
        if (parola.getText() == null
                || !parola.getText().toString().trim().equals(parola.getText().toString())
                || parola.getText().toString().length() < 7) {
            parola.setError("Introduceti o parola de minimum 7 caractere, fara spatii libere");
            valid = false;
        }
        if (confirmareParola.getText() == null
                || !confirmareParola.getText().toString().equals(parola.getText().toString().trim())) {
            confirmareParola.setError("Cele doua parole nu corespund");
            valid = false;
        }
        return valid;
    }
}
