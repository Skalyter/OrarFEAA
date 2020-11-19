package com.tiberiugaspar.oraruaic.ui.orar;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.tiberiugaspar.oraruaic.R;
import com.tiberiugaspar.oraruaic.model.Curs;
import com.tiberiugaspar.oraruaic.model.Sala;
import com.tiberiugaspar.oraruaic.util.DateUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.tiberiugaspar.oraruaic.adapters.VizualizareZiAdapter.EXTRA_ID_CURS;

public class EditareCursActivity extends AppCompatActivity {

    private Spinner spinnerOra, spinnerSala;
    private TextView textData;
    private EditText detalii;
    private Button btnSalvare;
    private final List<Sala> listaSali = new ArrayList<>();
    private ArrayAdapter<CharSequence> adapter;
    private Calendar calendar = Calendar.getInstance();
    private Curs curs;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editare_curs);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_ID_CURS)) {
            String idCurs = intent.getStringExtra(EXTRA_ID_CURS);
            findViewsById();
            setListeners();

            db = FirebaseFirestore.getInstance();
            db.collection("cursuri").document(idCurs)
                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    curs = documentSnapshot.toObject(Curs.class);
                    spinnerOra.setSelection(DateUtil.getPozitieIntervalOrarSpinner(curs.preluareCalendar()));
                    textData.setText(DateUtil.getStringDateFromCalendar(curs.preluareCalendar()));
                    if (curs.getDescriere() != null) {
                        detalii.setText(curs.getDescriere());
                    }
                    calendar = curs.preluareCalendar();
                    toolbar.setTitle(curs.getDenumire());
                    textData.setOnClickListener(onDateClickListener);
                    db.collection("sali")
                            .whereEqualTo("eTipSala", curs.geteTipSala())
                            .orderBy("codSala").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if (!queryDocumentSnapshots.isEmpty()) {
                                for (DocumentSnapshot snap : queryDocumentSnapshots) {
                                    Sala sala = snap.toObject(Sala.class);
                                    listaSali.add(sala);
                                    if (curs.getCodSala().equals(sala.getCodSala())) {
                                        spinnerSala.setSelection(listaSali.size() - 1);
                                    }
                                    adapter.add(sala.getCodSala());
                                }
                            }
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(EditareCursActivity.this, "Ceva nu a functionat", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        } else {
            Toast.makeText(this, "Ceva nu a functionat", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void findViewsById() {
        spinnerOra = findViewById(R.id.spinner_ora);
        spinnerSala = findViewById(R.id.spinner_sala);
        textData = findViewById(R.id.text_data);
        detalii = findViewById(R.id.edit_text_detalii);
        btnSalvare = findViewById(R.id.btn_salvare);
    }

    private void setListeners() {
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSala.setAdapter(adapter);
        spinnerOra.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                calendar.set(Calendar.HOUR_OF_DAY, DateUtil.getHourFromPosition(spinnerOra.getSelectedItemPosition()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        btnSalvare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String idCurs = curs.getId();
                String idSala = listaSali.get(spinnerSala.getSelectedItemPosition()).getIdSala();
                String codSala = listaSali.get(spinnerSala.getSelectedItemPosition()).getCodSala();
                String descriere = detalii.getText().toString();
                db.collection("cursuri").document(idCurs)
                        .update("dataOra", DateUtil.getTimestampOraCurenta(calendar),
                                "idSala", idSala,
                                "codSala", codSala,
                                "descriere", descriere).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        db.collection("cursuri_sali_ocupate").whereEqualTo("id", curs.getId()).get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        if (!queryDocumentSnapshots.isEmpty() && queryDocumentSnapshots.size() == 1) {
                                            Curs curs1 = queryDocumentSnapshots.getDocuments().get(0).toObject(Curs.class);
                                            if (!curs1.preluareCalendar().equals(curs.preluareCalendar())) {
                                                DocumentReference docRef = queryDocumentSnapshots.getDocuments().get(0).getReference();
                                                docRef.update("dataOra", DateUtil.getTimestampOraCurenta(calendar),
                                                        "idSala", listaSali.get(spinnerSala.getSelectedItemPosition()).getIdSala());
                                            }
                                            Toast.makeText(EditareCursActivity.this, "Modificari efectuate cu succes!", Toast.LENGTH_SHORT).show();
                                            setResult(RESULT_OK);
                                            finish();
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(EditareCursActivity.this, "Ceva nu a functionat", Toast.LENGTH_SHORT).show();
                                Log.d(DateUtil.TAG, "onFailure: " + e.getMessage());
                            }
                        });
                    }
                });
            }
        });
    }

    private final DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            calendar.set(year, month, dayOfMonth);
            textData.setText(DateUtil.getStringDateFromCalendar(calendar));
        }
    };
    private final Button.OnClickListener onDateClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            DatePickerDialog datePickerDialog = new DatePickerDialog(EditareCursActivity.this,
                    android.R.style.Theme_Material_Light_Dialog, onDateSetListener,
                    calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        }
    };
}