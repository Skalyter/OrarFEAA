package com.tiberiugaspar.oraruaic.ui.grupuri_discutii;

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

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.tiberiugaspar.oraruaic.R;
import com.tiberiugaspar.oraruaic.adapters.GrupDiscutiiAdapter;
import com.tiberiugaspar.oraruaic.model.GrupDiscutii;
import com.tiberiugaspar.oraruaic.model.Student;
import com.tiberiugaspar.oraruaic.util.SharedPreferencesUtil;

import java.util.ArrayList;
import java.util.List;

public class GrupuriDiscutiiFragment extends Fragment {


    private static final int REQ_ADAUGARE_GRUP = 103;

    private GrupDiscutiiAdapter adapter;
    private final List<GrupDiscutii> grupuriDiscutii = new ArrayList<>();
    RecyclerView recyclerView;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_grupuri, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recycler_grupuri_discutii);

        db = FirebaseFirestore.getInstance();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_ADAUGARE_GRUP && resultCode == Activity.RESULT_OK) {
            setList();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setList();
    }

    private void setList() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        adapter = new GrupDiscutiiAdapter(getContext(), grupuriDiscutii);
        recyclerView.setAdapter(adapter);
        grupuriDiscutii.clear();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (SharedPreferencesUtil.getSharedPreference(getActivity().getApplicationContext(),
                getString(R.string.saved_user_type), getString(R.string.user_student))
                .equals(getString(R.string.user_student))) {
            getActivity().findViewById(R.id.fab).setVisibility(View.GONE);
            db.collection("users").document(user.getUid())
                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Student student = documentSnapshot.toObject(Student.class);
                    db.collection("grupuri_discutii")
                            .whereArrayContains("idGrupe", student.getIdGrupa()).get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    grupuriDiscutii.clear();
                                    for (DocumentSnapshot snap : queryDocumentSnapshots) {
                                        GrupDiscutii grup = snap.toObject(GrupDiscutii.class);
                                        grupuriDiscutii.add(grup);
                                        adapter.notifyItemInserted(grupuriDiscutii.size() - 1);
                                        adapter.notifyItemRangeInserted(grupuriDiscutii.size() - 1, grupuriDiscutii.size());
                                    }
                                }
                            });
                }
            });
        } else { //profesor
            FloatingActionButton fab = getActivity().findViewById(R.id.fab);
            fab.setVisibility(View.VISIBLE);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), AdaugareGrupDiscutiiActivity.class);
                    startActivityForResult(intent, REQ_ADAUGARE_GRUP);
                }
            });
            db.collection("grupuri_discutii")
                    .whereEqualTo("idProfesor", user.getUid()).get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            grupuriDiscutii.clear();
                            for (DocumentSnapshot snap : queryDocumentSnapshots) {
                                GrupDiscutii grup = snap.toObject(GrupDiscutii.class);
                                grupuriDiscutii.add(grup);
                                adapter.notifyItemInserted(grupuriDiscutii.size() - 1);
                                adapter.notifyItemRangeInserted(grupuriDiscutii.size() - 1, grupuriDiscutii.size());
                            }
                        }
                    });
        }
    }
}
