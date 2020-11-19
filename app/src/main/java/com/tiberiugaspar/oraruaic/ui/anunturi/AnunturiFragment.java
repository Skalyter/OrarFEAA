package com.tiberiugaspar.oraruaic.ui.anunturi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.tiberiugaspar.oraruaic.R;
import com.tiberiugaspar.oraruaic.adapters.AnuntAdapter;
import com.tiberiugaspar.oraruaic.model.Anunt;

import java.util.ArrayList;
import java.util.List;

public class AnunturiFragment extends Fragment {

    private final int REQUEST_ADAUGARE_ANUNT = 101;

    private FirebaseFirestore firestore;
    private RecyclerView recyclerView;
    private AnuntAdapter anuntAdapter;
    private final List<Anunt> anunturi = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_anunturi, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recycler_anunturi);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        firestore = FirebaseFirestore.getInstance();

        final DocumentReference docRef = firestore.collection("users").document(currentUser.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot snapshot = task.getResult();
                    if (snapshot.exists()) {
                        if (snapshot.get("titulatura") == null) {
                            //scenariu student
                            getActivity().findViewById(R.id.fab).setVisibility(View.GONE);
                        } else {
                            //scenariu profesor
                            getActivity().findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    startActivityForResult(
                                            new Intent(getActivity(), AdaugareAnuntActivity.class),
                                            REQUEST_ADAUGARE_ANUNT);
                                }
                            });
                        }
                    }
                }
            }
        });
        actualizareLista();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ADAUGARE_ANUNT && resultCode == Activity.RESULT_OK) {
            actualizareLista();
        }
    }

    private void actualizareLista(){
        firestore.collection("anunturi")
                .orderBy("data", Query.Direction.DESCENDING).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        anunturi.clear();
                        for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                            anunturi.add(snapshot.toObject(Anunt.class));
                        }
                        anuntAdapter = new AnuntAdapter(anunturi, getContext());
                        recyclerView.setLayoutManager(
                                new LinearLayoutManager(getContext(),
                                        RecyclerView.VERTICAL, false));
                        recyclerView.setAdapter(anuntAdapter);
                    }
                });
    }
}
