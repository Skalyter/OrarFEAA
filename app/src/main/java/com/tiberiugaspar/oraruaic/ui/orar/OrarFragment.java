package com.tiberiugaspar.oraruaic.ui.orar;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.goodiebag.pinview.Pinview;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.tiberiugaspar.oraruaic.R;
import com.tiberiugaspar.oraruaic.adapter_utils.DayListItem;
import com.tiberiugaspar.oraruaic.adapter_utils.WeekListItem;
import com.tiberiugaspar.oraruaic.adapters.VizualizareSaptamanaAdapter;
import com.tiberiugaspar.oraruaic.adapters.VizualizareZiAdapter;
import com.tiberiugaspar.oraruaic.model.Curs;
import com.tiberiugaspar.oraruaic.model.Student;
import com.tiberiugaspar.oraruaic.util.DateUtil;
import com.tiberiugaspar.oraruaic.util.KeyboardUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.tiberiugaspar.oraruaic.adapters.VizualizareZiAdapter.EXTRA_ID_CURS;
import static com.tiberiugaspar.oraruaic.util.DateUtil.TAG;
import static com.tiberiugaspar.oraruaic.util.DateUtil.getZiuaFromCalendar;
import static com.tiberiugaspar.oraruaic.util.SharedPreferencesUtil.getSharedPreference;
import static com.tiberiugaspar.oraruaic.util.SharedPreferencesUtil.saveSharedPreference;

public class OrarFragment extends Fragment {

    private static final int REQ_MODIFICARE_CURS = 101;
    VizualizareZiAdapter vizualizareZiAdapter;
    VizualizareSaptamanaAdapter vizualizareSaptamanaAdapter;

    RecyclerView recyclerView;
    ImageView previous, next;
    TextView ziuaCurenta;
    FloatingActionButton fab;

    final List<DayListItem> dayListItems = new ArrayList<>();
    final List<WeekListItem> weekListItems = new ArrayList<>();
    private FirebaseFirestore db;
    private FirebaseUser firebaseUser;
    String tipUtilizatorSharedPreferences;
    private Student student = null;

    private Calendar calendar;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_orar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        calendar = Calendar.getInstance();
        tipUtilizatorSharedPreferences = getSharedPreference(getContext().getApplicationContext(),
                getString(R.string.saved_user_type), getString(R.string.user_student));
        findViewsById(view);
        setListeners();
        db = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        initializareDateUtilizator();
    }

    private void findViewsById(View view) {
        fab = getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);
        fab.setOnClickListener(fabListener);
        recyclerView = view.findViewById(R.id.recycler_view);
        previous = view.findViewById(R.id.nav_back);
        next = view.findViewById(R.id.nav_next);
        ziuaCurenta = view.findViewById(R.id.text_day);
    }

    private void setListeners() {
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recyclerView.getAdapter() == vizualizareZiAdapter) {
                    calendar.add(Calendar.DAY_OF_YEAR, -1);
                    setareAdapterZi();
                } else {
                    calendar.add(Calendar.WEEK_OF_YEAR, -1);
                    setareAdapterSaptamana();
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recyclerView.getAdapter() == vizualizareZiAdapter) {
                    calendar.add(Calendar.DAY_OF_YEAR, 1);
                    setareAdapterZi();
                } else {
                    calendar.add(Calendar.WEEK_OF_YEAR, 1);
                    setareAdapterSaptamana();
                }
            }
        });
    }

    private void initializareDateUtilizator() {
        DocumentReference documentReference = db.collection("users").document(firebaseUser.getUid());
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().getString("matricol") != null) { //student
                        student = task.getResult().toObject(Student.class);
                    }
                    if (getSharedPreference(getActivity().getApplicationContext(),
                            getString(R.string.saved_adapter),
                            getString(R.string.vizualizare_zilnic))
                            .equals(getString(R.string.vizualizare_zilnic))) {
                        setareAdapterSaptamana();
                    } else {
                        setareAdapterZi();
                    }
                }
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.item_tip_adapter)
                .setTitle(
                        getSharedPreference
                                (getContext().getApplicationContext(),
                                        getString(R.string.saved_adapter),
                                        getString(R.string.vizualizare_saptamanal)));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.item_tip_adapter) {
            if (item.getTitle().toString().equals(getString(R.string.vizualizare_saptamanal))) {
                saveSharedPreference(getContext().getApplicationContext(),
                        getString(R.string.saved_adapter),
                        getString(R.string.vizualizare_zilnic));
                setareAdapterSaptamana();
            } else {
                saveSharedPreference(getContext().getApplicationContext(),
                        getString(R.string.saved_adapter),
                        getString(R.string.vizualizare_saptamanal));
                setareAdapterZi();
            }
        }
        return true;
    }

    private void setareAdapterZi() {
        dayListItems.clear();
        ziuaCurenta.setText(DateUtil.getDataAfisare(calendar));
        if (student != null) {
            db.collection("cursuri").orderBy("dataOra")
                    .whereGreaterThan("dataOra", DateUtil.getCalendarDimineata(calendar))
                    .whereLessThan("dataOra", DateUtil.getCalendarSeara(calendar))
                    .whereArrayContains("idGrupe", student.getIdGrupa()).get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                                Curs curs = snapshot.toObject(Curs.class);
                                dayListItems.add(new DayListItem(DateUtil.getStringTimeFromCalendar(curs.preluareCalendar())));
                                dayListItems.add(new DayListItem(curs));
                            }
                            vizualizareZiAdapter = new VizualizareZiAdapter(dayListItems, getActivity(), tipUtilizatorSharedPreferences);
                            recyclerView.setAdapter(vizualizareZiAdapter);
                            recyclerView.setLayoutManager(
                                    new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
                        }
                    });
        } else { //profesor
            db.collection("cursuri").orderBy("dataOra")
                    .whereGreaterThan("dataOra", DateUtil.getCalendarDimineata(calendar))
                    .whereLessThan("dataOra", DateUtil.getCalendarSeara(calendar))
                    .whereEqualTo("idProfesor", firebaseUser.getUid())
                    .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                        Curs curs = snapshot.toObject(Curs.class);
                        Log.d(TAG, "onSuccess: " + calendar.toString());
                        dayListItems.add(new DayListItem(DateUtil.getStringTimeFromCalendar(curs.preluareCalendar())));
                        dayListItems.add(new DayListItem(curs));
                    }
                    vizualizareZiAdapter = new VizualizareZiAdapter(dayListItems, getActivity(), tipUtilizatorSharedPreferences);
                    recyclerView.setAdapter(vizualizareZiAdapter);
                    recyclerView.setLayoutManager(
                            new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
                }
            });
        }
    }

    private void setareAdapterSaptamana() {
        weekListItems.clear();
        ziuaCurenta.setText(DateUtil.getSaptamanaAfisare(calendar));
        if (student != null) {
            db.collection("cursuri").orderBy("dataOra")
                    .whereGreaterThan("dataOra", DateUtil.getTimestampLuni(calendar))
                    .whereLessThan("dataOra", DateUtil.getTimestampDuminica(calendar))
                    .whereArrayContains("idGrupe", student.getIdGrupa()).get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                                Curs curs = snapshot.toObject(Curs.class);
                                if (weekListItems.isEmpty() ||
                                        (weekListItems.get(weekListItems.size() - 1).getCurs() != null
                                                && weekListItems.get(weekListItems.size() - 1).getCurs().preluareCalendar().get(Calendar.DAY_OF_MONTH)
                                                != curs.preluareCalendar().get(Calendar.DAY_OF_MONTH))) {
                                    weekListItems.add(new WeekListItem(getZiuaFromCalendar(curs.preluareCalendar()), null));
                                }
                                weekListItems.add(new WeekListItem(null, DateUtil.getStringTimeFromCalendar(curs.preluareCalendar())));
                                weekListItems.add(new WeekListItem(curs));
                            }
                            vizualizareSaptamanaAdapter = new VizualizareSaptamanaAdapter(weekListItems, getActivity(), tipUtilizatorSharedPreferences);
                            recyclerView.setAdapter(vizualizareSaptamanaAdapter);
                            recyclerView.setLayoutManager(
                                    new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
                        }
                    });
        } else { //profesor
            db.collection("cursuri").orderBy("dataOra")
                    .whereGreaterThan("dataOra", DateUtil.getTimestampLuni(calendar))
                    .whereLessThan("dataOra", DateUtil.getTimestampDuminica(calendar))
                    .whereEqualTo("idProfesor", firebaseUser.getUid())
                    .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                        Curs curs = snapshot.toObject(Curs.class);
                        if (weekListItems.isEmpty() ||
                                (weekListItems.get(weekListItems.size() - 1).getCurs() != null
                                        && weekListItems.get(weekListItems.size() - 1).getCurs().preluareCalendar().get(Calendar.DAY_OF_MONTH)
                                        != curs.preluareCalendar().get(Calendar.DAY_OF_MONTH))) {
                            weekListItems.add(new WeekListItem(getZiuaFromCalendar(curs.preluareCalendar()), null));
                        }
                        weekListItems.add(new WeekListItem(null, DateUtil.getStringTimeFromCalendar(curs.preluareCalendar())));
                        weekListItems.add(new WeekListItem(curs));
                    }
                    vizualizareSaptamanaAdapter = new VizualizareSaptamanaAdapter(weekListItems, getActivity(), tipUtilizatorSharedPreferences);
                    recyclerView.setAdapter(vizualizareSaptamanaAdapter);
                    recyclerView.setLayoutManager(
                            new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
                }
            });
        }
    }

    final View.OnClickListener fabListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (student != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setView(R.layout.dialog_code_validation);
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getActivity(), "Validare anulata!",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                final AlertDialog dialog = builder.create();
                dialog.show();
                Pinview pinview = dialog.findViewById(R.id.input_code);
                pinview.requestPinEntryFocus();
                pinview.setPinViewEventListener(new Pinview.PinViewEventListener() {
                    @Override
                    public void onDataEntered(final Pinview pinview, boolean fromUser) {
                        db.collection("cursuri")
                                .whereEqualTo("dataOra", DateUtil.getTimestampOraCurenta())
                                .whereArrayContains("idGrupe", student.getIdGrupa()).get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        if (queryDocumentSnapshots.size() > 1) {
                                            Toast.makeText(getContext(), "Ceva nu a functionat", Toast.LENGTH_SHORT).show();
                                        } else if (queryDocumentSnapshots.size() == 0) {
                                            Toast.makeText(getContext(), "Nu aveti niciun curs in desfasurare sau codul nu a fost inca generat",
                                                    Toast.LENGTH_SHORT).show();
                                        } else {
                                            final Curs curs = queryDocumentSnapshots.getDocuments().get(0).toObject(Curs.class);
                                            if (curs.getCodValidarePrezenta() != null && curs.getCodValidarePrezenta().equals(pinview.getValue())) {
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
                                                        Snackbar.make(getView(), "Prezenta validata", Snackbar.LENGTH_LONG)
                                                                .setAction("Raport prezente", new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View v) {
                                                                        Intent intent = new Intent(getActivity(), RaportPrezenteActivity.class);
                                                                        intent.putExtra(DetaliiCursActivity.EXTRA_ID_DISCIPLINA, curs.getIdDisciplina());
                                                                        startActivity(intent);
                                                                    }
                                                                }).show();
                                                        dialog.dismiss();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(getContext(), "Prezenta a fost deja validata!", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            } else {
                                                Toast.makeText(getContext(), "Cod invalid", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }
                                });
                    }
                });
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        KeyboardUtil.hideKeyboard(getActivity());
                    }
                });
            } else { //profesor
                db.collection("cursuri")
                        .whereEqualTo("dataOra", DateUtil.getTimestampOraCurenta())
                        .whereEqualTo("idProfesor", firebaseUser.getUid()).get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                if (queryDocumentSnapshots.size() > 1) {
                                    Toast.makeText(getContext(), "Ceva nu a functionat cum ne asteptam. Reincercati!", Toast.LENGTH_SHORT).show();
                                } else if (queryDocumentSnapshots.size() == 0) {
                                    Toast.makeText(getContext(), "In prezent nu sustineti niciun curs", Toast.LENGTH_SHORT).show();
                                } else {
                                    Curs curs = queryDocumentSnapshots.getDocuments().get(0).toObject(Curs.class);
                                    Intent intent = new Intent(getActivity(), DetaliiCursActivity.class);
                                    intent.putExtra(EXTRA_ID_CURS, curs.getId());
                                    startActivityForResult(intent, REQ_MODIFICARE_CURS);
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Niciun curs in desfasurare in prezent.", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        if (vizualizareZiAdapter != null){
            setareAdapterZi();
        } else {
            setareAdapterSaptamana();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_MODIFICARE_CURS && resultCode == Activity.RESULT_OK){
            if (vizualizareZiAdapter != null){
                setareAdapterZi();
            } else {
                setareAdapterSaptamana();
            }
        }
    }
}
