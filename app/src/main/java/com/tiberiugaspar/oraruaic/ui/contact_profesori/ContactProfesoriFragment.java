package com.tiberiugaspar.oraruaic.ui.contact_profesori;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.collect.Lists;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.tiberiugaspar.oraruaic.R;
import com.tiberiugaspar.oraruaic.adapters.ContactProfesoriAdapter;
import com.tiberiugaspar.oraruaic.model.Profesor;

import java.util.ArrayList;
import java.util.List;

public class ContactProfesoriFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_professors, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final List<Profesor> listaProfesori = new ArrayList<>();
        final RecyclerView recyclerView = view.findViewById(R.id.recycler_contact_profesori);
        recyclerView.setLayoutManager(
                new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .whereIn("titulatura", Lists.newArrayList(getResources().getStringArray(R.array.lista_titulaturi)))
                .orderBy("nume").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot snap : queryDocumentSnapshots) {
                    Profesor profesor = snap.toObject(Profesor.class);
                    listaProfesori.add(profesor);
                }
                ContactProfesoriAdapter adapter = new ContactProfesoriAdapter(getContext(), listaProfesori);
                recyclerView.setAdapter(adapter);
            }
        });
        getActivity().findViewById(R.id.fab).setVisibility(View.GONE);
    }
}
