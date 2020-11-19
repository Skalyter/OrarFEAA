package com.tiberiugaspar.oraruaic.ui.anunturi;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tiberiugaspar.oraruaic.R;
import com.tiberiugaspar.oraruaic.model.Anunt;

import java.util.Calendar;

public class AdaugareAnuntActivity extends AppCompatActivity {

    private EditText titlu, descriere;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adaugare_anunt);
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
        titlu = findViewById(R.id.input_titlu);
        descriere = findViewById(R.id.input_descriere);
        Button btnSalvare = findViewById(R.id.btn_salvare_anunt);
        btnSalvare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validareCampuri()) {
                    Anunt anunt = new Anunt();
                    anunt.setareCalendar(Calendar.getInstance());
                    anunt.setTitlu(titlu.getText().toString());
                    anunt.setDescriere(descriere.getText().toString());
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    anunt.setId(db.collection("anunturi").document().getId());
                    DocumentReference docRef = db.collection("anunturi").document();
                    anunt.setId(docRef.getId());
                    docRef.set(anunt);
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });
    }


    private boolean validareCampuri() {
        boolean valid = true;
        if (titlu.getText() == null || titlu.getText().toString().trim().length() < 1) {
            valid = false;
            titlu.setError("Introduceti titlul");
        }
        if (descriere.getText() == null || descriere.getText().toString().trim().length() < 1) {
            valid = false;
            descriere.setError("Introduceti detaliile despre anunt");
        }
        return valid;
    }
}