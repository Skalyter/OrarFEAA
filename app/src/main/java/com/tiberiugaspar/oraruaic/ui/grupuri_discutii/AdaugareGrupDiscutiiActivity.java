package com.tiberiugaspar.oraruaic.ui.grupuri_discutii;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.tiberiugaspar.oraruaic.R;
import com.tiberiugaspar.oraruaic.adapters.MultipleGrupaAdapter;
import com.tiberiugaspar.oraruaic.model.GrupDiscutii;
import com.tiberiugaspar.oraruaic.model.Grupa;
import com.tiberiugaspar.oraruaic.model.Specializare;

import java.util.ArrayList;
import java.util.List;

import static com.tiberiugaspar.oraruaic.util.DateUtil.TAG;
import static com.tiberiugaspar.oraruaic.util.DateUtil.getAnPromotie;

public class AdaugareGrupDiscutiiActivity extends AppCompatActivity {

    private Spinner spinnerNivelStudii, spinnerSpecializari, spinnerAn;
    private ArrayAdapter<CharSequence> adapterSpecializari;
    private RecyclerView recyclerView;
    private MultipleGrupaAdapter adapter;
    private EditText denumire;
    private Button btnGenerareGrup;
    private FirebaseFirestore db;
    FirebaseUser user;
    private final List<Grupa> grupe = new ArrayList<>();
    private final List<Specializare> specializari = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adaugare_grup_discutii);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        findViewsById();
        setListeners();
    }

    private void findViewsById() {
        spinnerSpecializari = findViewById(R.id.spinner_specializare);
        spinnerNivelStudii = findViewById(R.id.spinner_nivel_studii);
        spinnerAn = findViewById(R.id.spinner_an_studiu);
        recyclerView = findViewById(R.id.recycler_grupe);
        recyclerView.setLayoutManager(new LinearLayoutManager(AdaugareGrupDiscutiiActivity.this,
                RecyclerView.VERTICAL, false));
        denumire = findViewById(R.id.text_denumire_grup);
        btnGenerareGrup = findViewById(R.id.btn_adauga_grup);

        adapterSpecializari = new ArrayAdapter<>(AdaugareGrupDiscutiiActivity.this, android.R.layout.simple_spinner_item);
        adapterSpecializari.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSpecializari.setAdapter(adapterSpecializari);
        actualizareSpecializari();
    }

    private void setListeners() {
        spinnerNivelStudii.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                actualizareSpecializari();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerAn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (adapterSpecializari.getCount() > 0){
                    actualizareGrupe();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        btnGenerareGrup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adapter.grupeSelectate.isEmpty()) {
                    Toast.makeText(AdaugareGrupDiscutiiActivity.this, "Selectati cel putin o grupa", Toast.LENGTH_SHORT).show();
                } else if (denumire.getText().toString().trim().length() <= 0) {
                    denumire.setError("Introduceti numele grupului");
                    Toast.makeText(AdaugareGrupDiscutiiActivity.this, "Atribuiti un nume grupului", Toast.LENGTH_SHORT).show();
                } else {
                    DocumentReference docRef = db.collection("grupuri_discutii").document();
                    docRef.set(new GrupDiscutii(docRef.getId(), preluareIdGrupe(), user.getUid(), denumire.getText().toString()))
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(AdaugareGrupDiscutiiActivity.this, "Grupul a fost creat cu succes!", Toast.LENGTH_SHORT).show();
                                    setResult(RESULT_OK);
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AdaugareGrupDiscutiiActivity.this, "Ceva nu a functionat. Incercati mai tarziu", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void actualizareSpecializari() {
        String nivelStudiu;
        if (spinnerNivelStudii.getSelectedItemPosition() == 0) {
            nivelStudiu = "LICENTA";
        } else if (spinnerNivelStudii.getSelectedItemPosition() == 1) {
            nivelStudiu = "MASTER";
        } else {
            nivelStudiu = "DOCTORAT";
        }
        specializari.clear();
        db.collection("specializari")
                .whereEqualTo("eNivelStudiu", nivelStudiu)
                .orderBy("denumire").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                adapterSpecializari.clear();
                for (DocumentSnapshot snap : queryDocumentSnapshots) {
                    Specializare specializare = snap.toObject(Specializare.class);
                    specializari.add(specializare);
                    adapterSpecializari.add(specializare.getDenumire());
                }
            }
        });
    }

    private void actualizareGrupe() {
        int promotia = getAnPromotie(spinnerAn.getSelectedItemPosition() + 1);
        grupe.clear();
        db.collection("grupe")
                .whereEqualTo("idSpecializare", specializari.get(spinnerSpecializari.getSelectedItemPosition()).getIdSpecializare())
                .whereEqualTo("promotia", promotia)
                .orderBy("denumire")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                Log.d(TAG, "citire cu succes grupe");
                for (DocumentSnapshot snap : queryDocumentSnapshots) {
                    grupe.add(snap.toObject(Grupa.class));
                }
                if (adapter == null) {
                    adapter = new MultipleGrupaAdapter(grupe, AdaugareGrupDiscutiiActivity.this);
                } else {
                    adapter.notifyDataSetChanged();
                }
                recyclerView.setAdapter(adapter);
            }
        });
    }

    private List<String> preluareIdGrupe() {
        List<String> idGrupe = new ArrayList<>();
        for (Grupa grupa : adapter.grupeSelectate) {
            if (!idGrupe.contains(grupa.getIdGrupa())) {
                idGrupe.add(grupa.getIdGrupa());
            }
        }
        return idGrupe;
    }
}