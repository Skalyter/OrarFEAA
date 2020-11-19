package com.tiberiugaspar.oraruaic.ui.orar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.goodiebag.pinview.Pinview;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.tiberiugaspar.oraruaic.R;
import com.tiberiugaspar.oraruaic.adapters.ParticipantAdapter;
import com.tiberiugaspar.oraruaic.model.Curs;
import com.tiberiugaspar.oraruaic.model.Student;
import com.tiberiugaspar.oraruaic.util.DateUtil;
import com.tiberiugaspar.oraruaic.util.KeyboardUtil;
import com.tiberiugaspar.oraruaic.util.Random;
import com.tiberiugaspar.oraruaic.util.SharedPreferencesUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static androidx.appcompat.app.AlertDialog.Builder;
import static com.tiberiugaspar.oraruaic.adapters.VizualizareZiAdapter.EXTRA_ID_CURS;
import static com.tiberiugaspar.oraruaic.util.DateUtil.TAG;

public class DetaliiCursActivity extends AppCompatActivity {

    public static final String EXTRA_ID_DISCIPLINA = "id_disciplina";

    private String tipUtilizator;

    private TextView disciplina, data, ora, sala, profesor, detalii, codPrezenta, timerTextView;
    private EditText detaliiCurs;
    private LinearLayout layoutStudent, layoutProfesor, layoutPrezenta;
    private Button adaugaCod, salvareDetalii, generareCod, adaugareStudent;
    private ImageView editDetalii;
    private RecyclerView recyclerView;
    private ParticipantAdapter adapter;
    long startTime = 0;

    private Student student = null;

    private Curs curs;
    private final List<Student> studentList = new ArrayList<>();

    FirebaseFirestore db;

    final Handler timerHandler = new Handler();
    final Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "run: merge");
            if ((System.currentTimeMillis() - startTime) < (3 * 60 * 1000)) {
                long millis = System.currentTimeMillis() - (startTime + 3 * 60 * 1000);
                int seconds = (int) (millis / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;

                timerTextView.setText(String.format("%d:%02d", Math.abs(minutes), Math.abs(seconds)));


                timerHandler.postDelayed(this, 1000);
            } else {
                stergeCod(this);
            }
        }
    };

    private void stergeCod(Runnable r) {
        Log.d(TAG, "run: nu merge " + (System.currentTimeMillis() - startTime) + " " + (3 * 60 * 1000));
        db.collection("cursuri").document(curs.getId())
                .update("codValidarePrezenta", null);
        timerHandler.removeCallbacks(r);
        timerTextView.setVisibility(View.GONE);
    }

    private void findViewsById() {
        disciplina = findViewById(R.id.text_disciplina);
        data = findViewById(R.id.text_data);
        ora = findViewById(R.id.text_ora);
        sala = findViewById(R.id.text_sala);
        profesor = findViewById(R.id.text_profesor);
        detalii = findViewById(R.id.text_detalii);
        codPrezenta = findViewById(R.id.text_cod_prezenta);
        timerTextView = findViewById(R.id.timer);

        detaliiCurs = findViewById(R.id.edit_text_detalii);

        layoutStudent = findViewById(R.id.layout_student);
        layoutProfesor = findViewById(R.id.layout_profesor);
        layoutPrezenta = findViewById(R.id.layout_prezente);

        adaugaCod = findViewById(R.id.btn_adaugare_cod);
        editDetalii = findViewById(R.id.btn_edit_detalii);
        salvareDetalii = findViewById(R.id.btn_salvare_detalii);
        generareCod = findViewById(R.id.btn_generare_cod);
        adaugareStudent = findViewById(R.id.btn_adauga_student);

        recyclerView = findViewById(R.id.recycler_participanti);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalii_curs);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Detalii curs");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_ID_CURS)) {
            String idCurs = intent.getStringExtra(EXTRA_ID_CURS);
            db = FirebaseFirestore.getInstance();
            DocumentReference docCurs = db.collection("cursuri").document(idCurs);
            docCurs.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    findViewsById();
                    setTipUtilizator();
                    curs = documentSnapshot.toObject(Curs.class);
                    setListeners();
                    setFields();
                }
            });
        } else {
            Toast.makeText(this, "Ceva nu a functionat!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (tipUtilizator.equals(getString(R.string.user_profesor))) {
            stergeCod(timerRunnable);
        }
    }

    private void setTipUtilizator() {
        tipUtilizator = SharedPreferencesUtil.getSharedPreference(getApplicationContext(),
                getString(R.string.saved_user_type),
                getString(R.string.user_student));
        if (tipUtilizator.equals(getString(R.string.user_student))) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            layoutStudent.setVisibility(View.VISIBLE);
            layoutProfesor.setVisibility(View.GONE);
            db.collection("users").document(user.getUid())
                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    student = documentSnapshot.toObject(Student.class);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(DetaliiCursActivity.this, "Ceva nu a functionat cum ne asteptam", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        } else {
            layoutProfesor.setVisibility(View.VISIBLE);
            layoutStudent.setVisibility(View.GONE);
        }
    }

    private void setListeners() {
        adaugaCod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Builder builder = new Builder(DetaliiCursActivity.this);
                builder.setView(R.layout.dialog_code_validation);
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(DetaliiCursActivity.this, "Validare anulata!",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                final androidx.appcompat.app.AlertDialog dialog = builder.create();
                dialog.show();
                Pinview pinview = dialog.findViewById(R.id.input_code);
                pinview.requestPinEntryFocus();
                pinview.setPinViewEventListener(new Pinview.PinViewEventListener() {
                    @Override
                    public void onDataEntered(final Pinview pinview, boolean fromUser) {
                        db.collection("cursuri").document(curs.getId())
                                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot.exists()) {
                                    Curs cursPrezenta = documentSnapshot.toObject(Curs.class);
                                    if (cursPrezenta.getCodValidarePrezenta() != null && cursPrezenta.getCodValidarePrezenta().equals(pinview.getValue())) {
                                        Map<String, String> participant = new HashMap<>();
                                        participant.put("idStudent", student.getIdStudent());
                                        participant.put("idCurs", curs.getId());
                                        participant.put("idDisciplina", curs.getIdDisciplina());
                                        participant.put("numeCurs", curs.getDenumire());
                                        participant.put("nume", student.getNume());
                                        participant.put("prenume", student.getPrenume());
                                        db.collection("participanti_curs")
                                                .document(curs.getId() + "_" + student.getIdStudent())
                                                .set(participant).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(DetaliiCursActivity.this, "Prezenta validata cu succes", Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(DetaliiCursActivity.this, "Ceva nu a functionat. Reincercati", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    } else {
                                        Toast.makeText(DetaliiCursActivity.this, "Codul de prezenta nu a fost generat sau a expirat", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    }
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(DetaliiCursActivity.this, "Ceva nu a functionat", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        });
                    }
                });
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        KeyboardUtil.hideKeyboard(DetaliiCursActivity.this);
                    }
                });
            }
        });

        editDetalii.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detaliiCurs.setEnabled(true);
                editDetalii.setEnabled(false);
                salvareDetalii.setVisibility(View.VISIBLE);
            }
        });

        salvareDetalii.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detaliiCurs.setEnabled(false);
                editDetalii.setEnabled(true);
                salvareDetalii.setVisibility(View.GONE);
                db.collection("cursuri")
                        .document(curs.getId()).update("descriere", detaliiCurs.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(DetaliiCursActivity.this, "Modificari salvate cu succes", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(DetaliiCursActivity.this, "Ceva nu a functionat", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                    }
                });
            }
        });

        generareCod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String cod = Random.getCodPrezenta();
                Log.d(TAG, "cod prezenta: " + cod);
                db.collection("cursuri").document(curs.getId())
                        .update("codValidarePrezenta", cod).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        layoutPrezenta.setVisibility(View.VISIBLE);
                        codPrezenta.setText(cod);
                        startTime = System.currentTimeMillis();
                        timerHandler.postDelayed(timerRunnable, 0);
                        generareCod.setEnabled(false);
                        adapter = new ParticipantAdapter(studentList,
                                DetaliiCursActivity.this, curs.getId());
                        recyclerView.setLayoutManager(
                                new LinearLayoutManager(DetaliiCursActivity.this,
                                        RecyclerView.VERTICAL, false));
                        recyclerView.setAdapter(adapter);
                        db.collection("participanti_curs")
                                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                        if (e != null) {
                                            Log.w(TAG, "listen:error", e);
                                            return;
                                        }
                                        for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                                            if (dc.getType() == DocumentChange.Type.ADDED) {
                                                Student student = dc.getDocument().toObject(Student.class);
                                                studentList.add(student);
                                                adapter.notifyItemInserted(studentList.size() - 1);
                                                adapter.notifyItemRangeInserted(studentList.size() - 1, studentList.size());
                                            }
                                        }
                                    }
                                });
                    }
                });
            }
        });
        adaugareStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final List<Student> studenti = new ArrayList<>();
                db.collection("users")
                        .whereIn("idGrupa", curs.getIdGrupe()).orderBy("nume")
                        .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        String[] numeStudenti = new String[queryDocumentSnapshots.getDocuments().size()];
                        int i = 0;
                        for (DocumentSnapshot snap : queryDocumentSnapshots) {
                            Student student = snap.toObject(Student.class);
                            studenti.add(student);
                            numeStudenti[i] = String.format("%s %s", student.getNume(), student.getPrenume());
                            i++;
                        }
                        new AlertDialog.Builder(DetaliiCursActivity.this).setTitle("Selectati un student")
                                .setItems(numeStudenti, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Student student = studenti.get(which);
                                        HashMap<String, String> participant = new HashMap<>();
                                        participant.put("idCurs", curs.getId());
                                        participant.put("numeCurs", curs.getDenumire());
                                        participant.put("idDisciplina", curs.getIdDisciplina());
                                        participant.put("idStudent", student.getIdStudent());
                                        participant.put("nume", student.getNume());
                                        participant.put("prenume", student.getPrenume());
                                        String idParticipare = String.format("%s_%s", curs.getId(), student.getIdStudent());
                                        db.collection("participanti_curs")
                                                .document(idParticipare)
                                                .set(participant)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d(TAG, "onSuccess: student adaugat cu succes");
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(DetaliiCursActivity.this, "Eroare", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                        if (studentList.contains(student)) {
                                            Toast.makeText(DetaliiCursActivity.this, "Studentul si-a validat prezenta!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: ceva nu a functionat" + e.getMessage());
                    }
                });
            }
        });
    }

    private void setFields() {
        disciplina.setText(curs.getDenumire());
        data.setText(DateUtil.getStringDateFromCalendar(curs.preluareCalendar()));
        ora.setText(DateUtil.getStringTimeFromCalendar(curs.preluareCalendar()));
        sala.setText(curs.getCodSala());
        profesor.setText(curs.getNumeProfesor());
        if (curs.getDescriere() != null) {
            detaliiCurs.setText(curs.getDescriere());
            detalii.setText(curs.getDescriere());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_raport_prezenta, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.option_raport_prezente) {
            Intent intent = new Intent(DetaliiCursActivity.this, RaportPrezenteActivity.class);
            intent.putExtra(EXTRA_ID_DISCIPLINA, curs.getIdDisciplina());
            startActivity(intent);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}