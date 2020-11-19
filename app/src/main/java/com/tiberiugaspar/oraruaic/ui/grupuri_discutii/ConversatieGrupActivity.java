package com.tiberiugaspar.oraruaic.ui.grupuri_discutii;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tiberiugaspar.oraruaic.R;
import com.tiberiugaspar.oraruaic.adapter_utils.Mesaj;
import com.tiberiugaspar.oraruaic.adapters.MesajAdapter;
import com.tiberiugaspar.oraruaic.model.Profesor;
import com.tiberiugaspar.oraruaic.model.Student;

import java.util.ArrayList;
import java.util.List;

import static com.tiberiugaspar.oraruaic.adapters.GrupDiscutiiAdapter.EXTRA_DENUMIRE_GRUP;
import static com.tiberiugaspar.oraruaic.adapters.GrupDiscutiiAdapter.EXTRA_ID_GRUP;
import static com.tiberiugaspar.oraruaic.ui.MainActivity.RC_DOCUMENT_PICKER;
import static com.tiberiugaspar.oraruaic.ui.MainActivity.RC_PHOTO_PICKER;

public class ConversatieGrupActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MesajAdapter adapter;
    private ImageView btnTrimite, btnImagine, btnDocument;
    private EditText textMesaj;
    private final List<Mesaj> mesaje = new ArrayList<>();
    private String numeUtilizator;

    private ChildEventListener childEventListener;
    private DatabaseReference databaseReference;

    private StorageReference imaginiChatReference;
    private StorageReference documenteChatReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversatie_grup);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_ID_GRUP)) {
            String idGrup = intent.getStringExtra(EXTRA_ID_GRUP);
            String denumireGrup = intent.getStringExtra(EXTRA_DENUMIRE_GRUP);
            toolbar.setTitle(denumireGrup);

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

            databaseReference = firebaseDatabase.getReference().child("mesaje_" + idGrup);
            imaginiChatReference = firebaseStorage.getReference().child("imagini_" + idGrup);
            documenteChatReference = firebaseStorage.getReference().child("documente_" + idGrup);

            findViewsById();
            setListeners();
            db.collection("users").document(firebaseUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.getString("matricol") != null) {
                        Student student = documentSnapshot.toObject(Student.class);
                        numeUtilizator = String.format("%s %s", student.getNume(), student.getPrenume());
                    } else {
                        Profesor profesor = documentSnapshot.toObject(Profesor.class);
                        numeUtilizator = String.format("%s %s %s", profesor.getTitulatura(), profesor.getNume(), profesor.getPrenume());
                    }
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        attachDatabaseReadListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        detachDatabaseReadListener();
    }

    private void findViewsById() {
        recyclerView = findViewById(R.id.recycler_mesaje);
        adapter = new MesajAdapter(ConversatieGrupActivity.this, mesaje);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

        btnTrimite = findViewById(R.id.btn_trimite);
        btnTrimite.setEnabled(false);
        btnImagine = findViewById(R.id.btn_adauga_imagine);
        btnDocument = findViewById(R.id.btn_adauga_document);
        textMesaj = findViewById(R.id.text_mesaj);
    }

    private void setListeners() {
        textMesaj.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > 0) {
                    btnTrimite.setEnabled(true);
                } else {
                    btnTrimite.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        btnTrimite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Mesaj mesaj = new Mesaj(numeUtilizator, textMesaj.getText().toString(), null, null);
                databaseReference.push().setValue(mesaj);
                textMesaj.setText("");
            }
        });

        btnImagine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Alege o aplicatie pentru a continua"), RC_PHOTO_PICKER);
            }
        });

        btnDocument.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                intent.setType("*/*");
                startActivityForResult(Intent.createChooser(intent, "Alege o aplicatie pentru a continua"), RC_DOCUMENT_PICKER);
            }
        });
    }

    private void attachDatabaseReadListener() {

        if (childEventListener == null) {
            childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Mesaj mesaj = dataSnapshot.getValue(Mesaj.class);
                    mesaje.add(mesaj);
                    adapter.notifyItemInserted(mesaje.size() - 1);
//                    adapter.notifyItemRangeInserted(mesaje.size() - 1, mesaje.size());
                    recyclerView.scrollToPosition(mesaje.size()-1);
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            };
            databaseReference.addChildEventListener(childEventListener);
        }
    }

    private void detachDatabaseReadListener() {
        if (childEventListener != null)
            databaseReference.removeEventListener(childEventListener);
        childEventListener = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == RC_PHOTO_PICKER) {
                final Uri selectedImageUri = data.getData();
                final StorageReference photoReference = imaginiChatReference.child(selectedImageUri.getLastPathSegment());
                UploadTask uploadTask = photoReference.putFile(selectedImageUri);
                uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        // Continue with the task to get the download URL
                        return photoReference.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                            Mesaj mesaj = new Mesaj(numeUtilizator, null, downloadUri.toString(), null);
                            databaseReference.push().setValue(mesaj);
                        } else {
                            Toast.makeText(ConversatieGrupActivity.this,
                                    "Ceva nu a functionat",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else if (requestCode == RC_DOCUMENT_PICKER){
                final Uri selectedDocumentUri = data.getData();
                final StorageReference documentReference = documenteChatReference.child(selectedDocumentUri.getLastPathSegment());
                UploadTask uploadTask = documentReference.putFile(selectedDocumentUri);
                uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()){
                            throw task.getException();
                        }
                        return documentReference.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()){
                            Uri downloadUri = task.getResult();
                            Mesaj mesaj = new Mesaj(numeUtilizator, null, null, downloadUri.toString());
                            databaseReference.push().setValue(mesaj);
                        } else{
                            Toast.makeText(ConversatieGrupActivity.this, "Ceva nu a functionat",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }
}