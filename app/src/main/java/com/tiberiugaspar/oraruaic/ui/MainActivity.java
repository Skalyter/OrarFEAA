package com.tiberiugaspar.oraruaic.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuth.AuthStateListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tiberiugaspar.oraruaic.R;
import com.tiberiugaspar.oraruaic.model.Profesor;
import com.tiberiugaspar.oraruaic.model.Student;
import com.tiberiugaspar.oraruaic.ui.login_register.LoginActivity;
import com.tiberiugaspar.oraruaic.ui.login_register.RegisterActivity;
import com.tiberiugaspar.oraruaic.util.SharedPreferencesUtil;

import static com.tiberiugaspar.oraruaic.util.SharedPreferencesUtil.getSharedPreference;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

    private ImageView imagine;
    private FirebaseFirestore db;
    private AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    private StorageReference photosReference;
    public static final int RC_PHOTO_PICKER = 2;
    public static final int RC_DOCUMENT_PICKER = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView nume1 = headerView.findViewById(R.id.header_user_name);
        TextView matricol = headerView.findViewById(R.id.header_user_matricol);
        imagine = headerView.findViewById(R.id.header_user_image);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        photosReference = FirebaseStorage.getInstance().getReference().child("poze_utilizatori");
        if (currentUser == null) {
            startActivity(new Intent(this, RegisterActivity.class));
            finish();
        }
        verificareUtilizatorValidat();
        imagine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
            }
        });
        final String tipUtilizator = getSharedPreference(getApplicationContext(), getString(R.string.saved_user_type),
                getString(R.string.user_student));
        String nume = getSharedPreference(getApplicationContext(), getString(R.string.saved_user_name), "Anonim");
        String numeMatricol = getSharedPreference(getApplicationContext(), getString(R.string.saved_user_matricol), "");
        if (tipUtilizator.equals(getString(R.string.user_student))) {
            navigationView.getMenu().findItem(R.id.nav_profesor_generare_cursuri).setVisible(false);
            mAppBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.nav_home, R.id.nav_anunturi, R.id.nav_search_class, R.id.nav_chat_groups,
                    R.id.nav_student_info_profesori)
                    .setDrawerLayout(drawer)
                    .build();
        } else {
            navigationView.getMenu().findItem(R.id.nav_student_info_profesori).setVisible(false);
            mAppBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.nav_home, R.id.nav_anunturi, R.id.nav_search_class,
                    R.id.nav_chat_groups, R.id.nav_profesor_generare_cursuri)
                    .setDrawerLayout(drawer)
                    .build();
        }
        nume1.setText(nume);
        matricol.setText(numeMatricol);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tip_adapter, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public void logout(View view) {
        startActivity(new Intent(this, LoginActivity.class));
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        FirebaseAuth.getInstance().signOut();
        finish();
    }

    private void verificareUtilizatorValidat() {
        final String tipUtilizator = SharedPreferencesUtil.getSharedPreference(getApplicationContext(),
                getString(R.string.saved_user_type),
                getString(R.string.user_student));
        if (currentUser!= null) {
            db.collection("users").document(currentUser.getUid())
                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Student student;
                    Profesor profesor;
                    if (tipUtilizator.equals(getString(R.string.user_student))) {
                        student = documentSnapshot.toObject(Student.class);
                        if (student.getUrlImagine() != null) {
                            Glide.with(imagine.getContext())
                                    .load(student.getUrlImagine())
                                    .into(imagine);
                        }
                        if (student.isUtilizatorNevalidat()) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setPositiveButton(getString(R.string.inteles), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    validareEsuata();
                                }
                            }).setTitle("Cont invalid").setMessage("Acest cont nu a fost validat. Reveniti mai tarziu");
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }

                    } else {
                        profesor = documentSnapshot.toObject(Profesor.class);
                        if (profesor.getUrlImagine() != null) {
                            Glide.with(imagine.getContext())
                                    .load(profesor.getUrlImagine())
                                    .into(imagine);
                        }
                        if (profesor.isUtilizatorNevalidat()) {

                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setPositiveButton(getString(R.string.inteles), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    validareEsuata();
                                }
                            }).setTitle("Cont invalid").setMessage("Acest cont nu a fost validat. Reveniti mai tarziu");
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity.this, "Nu am putut verifica integritatea contului. Verificati mai tarziu", Toast.LENGTH_SHORT).show();
                    validareEsuata();
                }
            });
        }
    }

    private void validareEsuata() {
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {
            final Uri selectedImageUri = data.getData();
            final StorageReference photoReference = photosReference.child(selectedImageUri.getLastPathSegment());
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
                        final Uri downloadUri = task.getResult();
                        db.collection("users").document(currentUser.getUid())
                                .update("urlImagine", downloadUri.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Glide.with(imagine.getContext())
                                        .load(downloadUri)
                                        .into(imagine);
                                Toast.makeText(MainActivity.this, "Imagine actualizata cu succes", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this, "Ceva nu a functionat cum ne asteptam", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(MainActivity.this,
                                "S-a produs o eroare.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}

