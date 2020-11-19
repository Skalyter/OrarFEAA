package com.tiberiugaspar.oraruaic.ui.orar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.tiberiugaspar.oraruaic.R;
import com.tiberiugaspar.oraruaic.adapters.PrezenteProfesorAdapter;
import com.tiberiugaspar.oraruaic.adapters.PrezenteStudentAdapter;
import com.tiberiugaspar.oraruaic.model.Student;
import com.tiberiugaspar.oraruaic.util.DateUtil;

import java.util.ArrayList;
import java.util.List;

import static com.tiberiugaspar.oraruaic.ui.orar.DetaliiCursActivity.EXTRA_ID_DISCIPLINA;
import static com.tiberiugaspar.oraruaic.util.SharedPreferencesUtil.getSharedPreference;

public class RaportPrezenteActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_raport_prezente);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        toolbar.setTitle("Raport prezente");
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_ID_DISCIPLINA)) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            final List<Student> studentList = new ArrayList<>();
            final RecyclerView recyclerView = findViewById(R.id.recycler_prezente);
            final TextView totalPrezente = findViewById(R.id.student_total_prezente);
            final TextView lipsaPrezente = findViewById(R.id.lipsa_prezente);
            recyclerView.setLayoutManager(new LinearLayoutManager(
                    this, RecyclerView.VERTICAL, false));
            LinearLayout studentLayout = findViewById(R.id.layout_total_student);
            String idDisciplina = intent.getStringExtra(EXTRA_ID_DISCIPLINA);
            if (getSharedPreference(getApplicationContext(),
                    getString(R.string.saved_user_type), getString(R.string.user_student))
                    .equals(getString(R.string.user_student))) {
                studentLayout.setVisibility(View.VISIBLE);
                db.collection("participanti_curs")
                        .whereEqualTo("idStudent", userId)
                        .whereEqualTo("idDisciplina", idDisciplina)
                        .orderBy("numeCurs").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.size()>0) {
                            for (DocumentSnapshot snap : queryDocumentSnapshots) {
                                Student student = snap.toObject(Student.class);
                                studentList.add(student);
                            }
                            PrezenteStudentAdapter studentAdapter =
                                    new PrezenteStudentAdapter(
                                            RaportPrezenteActivity.this, studentList);
                            recyclerView.setAdapter(studentAdapter);
                            totalPrezente.setText(String.format("%d", studentList.size()));
                            lipsaPrezente.setVisibility(View.GONE);
                        }
                        else{
                            totalPrezente.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.GONE);
                            lipsaPrezente.setVisibility(View.VISIBLE);
                            lipsaPrezente.setText("Nu aveti nicio prezenta inregistrata.");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RaportPrezenteActivity.this, "S-a produs o eroare, incercati mai tarziu", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            } else { //profesor
                studentLayout.setVisibility(View.GONE);
                db.collection("participanti_curs")
                        .whereEqualTo("idDisciplina", idDisciplina)
                        .orderBy("idStudent").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.size() > 0) {
                            Student student1 = null;
                            for (DocumentSnapshot snap : queryDocumentSnapshots) {
                                Student student = snap.toObject(Student.class);
                                if (student1 == null) {
                                    student1 = student;
                                }
                                if (student.equals(student1)) {
                                    student1.adaugaPrezenta();
                                } else {
                                    studentList.add(student1);
                                    student1 = student;
                                }
                            }
                            studentList.add(student1);
                            PrezenteProfesorAdapter profesorAdapter =
                                    new PrezenteProfesorAdapter(
                                            studentList, RaportPrezenteActivity.this);
                            recyclerView.setAdapter(profesorAdapter);
                        } else {
                            recyclerView.setVisibility(View.GONE);
                            lipsaPrezente.setText("Niciun student nu si-a validat prezentele momentan");
                            lipsaPrezente.setVisibility(View.VISIBLE);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(DateUtil.TAG, "onFailure: " + e.getMessage());
                        Toast.makeText(RaportPrezenteActivity.this, "S-a produs o eroare, incercati mai tarziu", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

}