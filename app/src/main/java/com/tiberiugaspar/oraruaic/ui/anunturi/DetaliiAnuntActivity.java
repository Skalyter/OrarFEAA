package com.tiberiugaspar.oraruaic.ui.anunturi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tiberiugaspar.oraruaic.R;
import com.tiberiugaspar.oraruaic.model.Anunt;
import com.tiberiugaspar.oraruaic.util.DateUtil;

import static com.tiberiugaspar.oraruaic.adapters.AnuntAdapter.EXTRA_ID_ANUNT;

public class DetaliiAnuntActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalii_anunt);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        final TextView data, descriere;
        data = findViewById(R.id.data_anunt);
        descriere = findViewById(R.id.descriere_anunt);
        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_ID_ANUNT)) {
            String idAnunt = intent.getStringExtra(EXTRA_ID_ANUNT);
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference docRef = db.collection("anunturi").document(idAnunt);
            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Anunt anunt = documentSnapshot.toObject(Anunt.class);
                    toolbar.setTitle(anunt.getTitlu());
                    data.setText(DateUtil.getDataAnunt(anunt.preluareCalendar()));
                    descriere.setText(anunt.getDescriere());
                }
            });
        }
    }
}