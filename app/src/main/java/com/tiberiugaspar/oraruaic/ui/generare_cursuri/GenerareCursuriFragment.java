package com.tiberiugaspar.oraruaic.ui.generare_cursuri;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.tiberiugaspar.oraruaic.R;
import com.tiberiugaspar.oraruaic.adapters.MultipleGrupaAdapter;
import com.tiberiugaspar.oraruaic.adapters.SingleGrupaAdapter;
import com.tiberiugaspar.oraruaic.model.Curs;
import com.tiberiugaspar.oraruaic.model.Disciplina;
import com.tiberiugaspar.oraruaic.model.ETipSala;
import com.tiberiugaspar.oraruaic.model.Grupa;
import com.tiberiugaspar.oraruaic.model.Sala;
import com.tiberiugaspar.oraruaic.model.Specializare;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.tiberiugaspar.oraruaic.util.DateUtil.TAG;
import static com.tiberiugaspar.oraruaic.util.DateUtil.getAnPromotie;
import static com.tiberiugaspar.oraruaic.util.DateUtil.getCalendar;
import static com.tiberiugaspar.oraruaic.util.DateUtil.getHourFromPosition;
import static com.tiberiugaspar.oraruaic.util.DateUtil.getStringDateFromCalendar;
import static com.tiberiugaspar.oraruaic.util.DateUtil.getStringTimeFromCalendar;
import static com.tiberiugaspar.oraruaic.util.DateUtil.getTimestamp;
import static com.tiberiugaspar.oraruaic.util.DateUtil.getZiuaSaptamanii;
import static com.tiberiugaspar.oraruaic.util.SharedPreferencesUtil.*;

public class GenerareCursuriFragment extends Fragment {

    Spinner nivelStudii, specializare, anStudii, disciplina, ziuaSaptamanii, intervalOrar, sala;
    RadioButton tipOraCurs;
    RadioButton frecventaSaptamanal;
    CalendarView calendarView;
    Button btnAdaugare;

    RecyclerView recyclerView;
    SingleGrupaAdapter singleGrupaAdapter = null;
    MultipleGrupaAdapter multipleGrupaAdapter = null;

    private final List<Specializare> specializari = new ArrayList<>();
    private final List<Disciplina> discipline = new ArrayList<>();
    private final List<Grupa> grupe = new ArrayList<>();
    private final List<Sala> sali = new ArrayList<>();
    private FirebaseFirestore db;
    private boolean firstTime = true;
    String idSpecializare;

    Calendar calendar;

    ArrayAdapter<CharSequence> adapterNivelStudii, adapterSpecializare, adapterDisciplina, adapterSala;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_generare_cursuri, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViewsById(view);
        setListeners();
    }

    private void findViewsById(View view) {
        calendar = Calendar.getInstance();

        getActivity().findViewById(R.id.fab).setVisibility(View.GONE);
        nivelStudii = view.findViewById(R.id.spinner_nivel_studii);
        specializare = view.findViewById(R.id.spinner_specializare);
        anStudii = view.findViewById(R.id.spinner_an_studiu);
        disciplina = view.findViewById(R.id.spinner_disciplina);
        ziuaSaptamanii = view.findViewById(R.id.spinner_ziua);
        intervalOrar = view.findViewById(R.id.spinner_ora);
        sala = view.findViewById(R.id.spinner_sala);
        tipOraCurs = view.findViewById(R.id.radio_curs);
        frecventaSaptamanal = view.findViewById(R.id.radio_curs_saptamanal);
        calendarView = view.findViewById(R.id.calendar_primul_curs);
        btnAdaugare = view.findViewById(R.id.btn_adauga_curs);
        recyclerView = view.findViewById(R.id.recycler_grupe);
        recyclerView.setLayoutManager(
                new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));

        db = FirebaseFirestore.getInstance();
    }

    private void setListeners() {
        adapterNivelStudii = ArrayAdapter.createFromResource(getContext(), R.array.lista_nivel_studii, android.R.layout.simple_spinner_item);
        adapterNivelStudii.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        adapterSpecializare = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item);
        adapterSpecializare.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        adapterDisciplina = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item);
        adapterDisciplina.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        adapterSala = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item);
        adapterSala.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        nivelStudii.setAdapter(adapterNivelStudii);
        specializare.setAdapter(adapterSpecializare);
        disciplina.setAdapter(adapterDisciplina);
        sala.setAdapter(adapterSala);

        actualizareSali(ETipSala.AMFITEATRU);

        nivelStudii.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                actualizareSpecializari();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        specializare.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                actualizareDiscipline();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        anStudii.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (firstTime) {
                    firstTime = false;
                } else {
                    actualizareDiscipline();
                }
                actualizareGrupe();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        tipOraCurs.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                actualizareGrupe();
                if (isChecked) {
                    actualizareSali(ETipSala.AMFITEATRU);
                } else {
                    actualizareSali(ETipSala.SEMINAR);
                }
            }
        });

        btnAdaugare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validareDate()) {
                    //datele sunt completate corect, verificam daca pozitia este libera
                    verificareDisponibilitateSala();
                }
            }
        });

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                calendar.set(year, month, dayOfMonth);
            }
        });
    }

    private void actualizareSpecializari() {
        String nivelStudiu;
        if (nivelStudii.getSelectedItemPosition() == 0) {
            nivelStudiu = "LICENTA";
        } else if (nivelStudii.getSelectedItemPosition() == 1) {
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
                adapterSpecializare.clear();
                for (DocumentSnapshot snap : queryDocumentSnapshots) {
                    Specializare specializare = snap.toObject(Specializare.class);
                    specializari.add(specializare);
                    adapterSpecializare.add(specializare.getDenumire());
                }
                specializare.setSelection(0);
                actualizareDiscipline();
            }
        });
    }

    private void actualizareDiscipline() {
        discipline.clear();
        idSpecializare = specializari.get(specializare.getSelectedItemPosition()).getIdSpecializare();
        int anStudiu = anStudii.getSelectedItemPosition() + 1;
        db.collection("discipline")
                .whereEqualTo("idSpecializare", idSpecializare)
                .whereEqualTo("anStudiu", anStudiu)
                .orderBy("denumire").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                adapterDisciplina.clear();
                for (DocumentSnapshot snap : queryDocumentSnapshots) {
                    Disciplina disciplina = snap.toObject(Disciplina.class);
                    discipline.add(disciplina);
                    adapterDisciplina.add(disciplina.getDenumire());
                }
            }
        });
    }

    private void actualizareGrupe() {
        int promotia = getAnPromotie(anStudii.getSelectedItemPosition() + 1);
        grupe.clear();
        db.collection("grupe")
                .whereEqualTo("idSpecializare", idSpecializare)
                .whereEqualTo("promotia", promotia)
                .orderBy("denumire")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                Log.d(TAG, "citire cu succes grupe");
                for (DocumentSnapshot snap : queryDocumentSnapshots) {
                    grupe.add(snap.toObject(Grupa.class));
                }
                if (tipOraCurs.isChecked()) {
                    if (multipleGrupaAdapter == null) {
                        multipleGrupaAdapter = new MultipleGrupaAdapter(grupe, getContext());
                    } else {
                        multipleGrupaAdapter.notifyDataSetChanged();
                    }
                    recyclerView.setAdapter(multipleGrupaAdapter);
                } else {
                    if (singleGrupaAdapter == null) {
                        singleGrupaAdapter = new SingleGrupaAdapter(grupe, getContext());
                    } else {
                        singleGrupaAdapter.notifyDataSetChanged();
                    }
                    recyclerView.setAdapter(singleGrupaAdapter);
                }
            }
        });
    }

    private void actualizareSali(ETipSala tipSala) {
        sali.clear();
        db.collection("sali")
                .whereEqualTo("eTipSala", tipSala)
                .orderBy("codSala").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                Log.d(TAG, "onSuccess: citire cu succes sali");
                adapterSala.clear();
                for (DocumentSnapshot snap : queryDocumentSnapshots) {
                    Sala sala = snap.toObject(Sala.class);
                    sali.add(sala);
                    adapterSala.add(sala.getCodSala());
                }
            }
        });
    }

    private boolean validareDate() {
        boolean valid = true;
        if (disciplina.getSelectedItem() == null || disciplina.getSelectedItem().toString().trim().equals("")) {
            Toast.makeText(getContext(), "Selectati mai intai disciplina!", Toast.LENGTH_SHORT).show();
            valid = false;
        }
        if (getZiuaSaptamanii(ziuaSaptamanii.getSelectedItemPosition()) != calendar.get(Calendar.DAY_OF_WEEK)) {
            Log.d(TAG, "ziuaSaptamaniiSpinner: " + getZiuaSaptamanii(ziuaSaptamanii.getSelectedItemPosition())
                    + " vs calendar: " + calendar.get(Calendar.DAY_OF_WEEK));
            Log.d(TAG, "validareDate: " + getStringDateFromCalendar(calendar));
            Toast.makeText(getContext(), "Data primului curs trebuie sa fie in ziua selectata anterior!", Toast.LENGTH_SHORT).show();
            valid = false;
        }
        if (tipOraCurs.isChecked()) {
            if (multipleGrupaAdapter.grupeSelectate == null || multipleGrupaAdapter.grupeSelectate.size() == 0) {
                Toast.makeText(getContext(), "Selectati cel putin o grupa!", Toast.LENGTH_SHORT).show();
                valid = false;
            }
        } else {
            if (singleGrupaAdapter.mSelectedItem == -1) {
                Toast.makeText(getContext(), "Selectati grupa pentru care se sustine seminarul.", Toast.LENGTH_SHORT).show();
            }
        }
        return valid;
    }

    private List<Timestamp> preluareDateCurs() {
        Calendar calendar = Calendar.getInstance();
        List<Timestamp> dateCurs = new ArrayList<>();
        calendar.setTimeInMillis(this.calendar.getTimeInMillis());
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, getHourFromPosition(intervalOrar.getSelectedItemPosition()));
        if (frecventaSaptamanal.isChecked()) {
            int increment = 7;
            for (int i = 0; i < 14; i++) {
                calendar.add(Calendar.DAY_OF_YEAR, increment);
                dateCurs.add(getTimestamp(calendar));
                Log.d(TAG, "preluareDateCurs: " + dateCurs.get(dateCurs.size() - 1).toString());
            }
        } else {
            int increment = 14;
            for (int i = 0; i < 7; i++) {
                calendar.add(Calendar.DAY_OF_MONTH, increment);
                dateCurs.add(getTimestamp(calendar));
                Log.d(TAG, "preluareDateCurs: " + dateCurs.get(dateCurs.size() - 1).toString());
            }
        }
        return dateCurs;
    }

    private List<String> preluareIdGrupe() {
        List<String> idGrupe = new ArrayList<>();
        if (tipOraCurs.isChecked()) {
            for (Grupa grupa : multipleGrupaAdapter.grupeSelectate) {
                if (!idGrupe.contains(grupa.getIdGrupa())) {
                    idGrupe.add(grupa.getIdGrupa());
                }
            }
        } else {
            idGrupe.add(grupe.get(singleGrupaAdapter.mSelectedItem).getIdGrupa());
        }
        return idGrupe;
    }

    private void verificareDisponibilitateSala() {
        List<Timestamp> dateCursuri = preluareDateCurs();
        if (preluareDateCurs().size()>10){
            dateCursuri = dateCursuri.subList(0, 9);
        }
        db.collection("cursuri_sali_ocupate")
                .whereEqualTo("idSala", sali.get(sala.getSelectedItemPosition()).getIdSala())
                .whereIn("dataOra", dateCursuri).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<Curs> pozitiiOcupate = new ArrayList<>();
                        for (DocumentSnapshot snap : queryDocumentSnapshots) {
                            pozitiiOcupate.add(snap.toObject(Curs.class));
                        }
                        if (pozitiiOcupate.size() > 0) { //sala este ocupata in intervalul orar selectat intr-una sau mai multe saptamani
                            new AlertDialog.Builder(getContext()).setTitle("Interval orar indisponibil")
                                    .setMessage("Verificati integritatea introducerii datelor, deoarece intervalul orar selectat nu este disponibil")
                                    .setPositiveButton("Ok", null).show();
                        } else { //sala este disponibila, putem introduce cursul
                            generareCursuri();
                        }
                    }
                });
    }

    private void generareCursuri() {
        String idProfesor = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String numeProfesor = getSharedPreference(getContext().getApplicationContext(),
                getString(R.string.saved_user_name), "Anonim");
        String idSala = sali.get(sala.getSelectedItemPosition()).getIdSala();
        String codSala = sali.get(sala.getSelectedItemPosition()).getCodSala();
        String idDisciplina = discipline.get(disciplina.getSelectedItemPosition()).getIdDisciplina();
        String denumire;
        ETipSala eTipSala;
        if (tipOraCurs.isChecked()) {
            denumire = disciplina.getSelectedItem().toString() + " C";
            eTipSala = ETipSala.AMFITEATRU;
        } else {
            eTipSala = ETipSala.SEMINAR;
            denumire = disciplina.getSelectedItem().toString() + " S";
        }
        int increment = 1;
        for (Timestamp timestamp : preluareDateCurs()) {
            String denumireIndividuala = denumire + increment;
            increment++;
            DocumentReference docRefCursuri = db.collection("cursuri").document();
            Curs curs = new Curs(docRefCursuri.getId(), denumireIndividuala, idProfesor, numeProfesor, getCalendar(timestamp),
                    idDisciplina, idSala, codSala, preluareIdGrupe(), eTipSala);
            docRefCursuri.set(curs);
            DocumentReference docRefPozitii = db.collection("cursuri_sali_ocupate").document();
            docRefPozitii.set(new Curs(curs.getId(), getCalendar(timestamp), idSala, eTipSala));
            Log.d(TAG, "generareCursuri: " + getStringDateFromCalendar(getCalendar(timestamp))
            + " " + getStringTimeFromCalendar(getCalendar(timestamp)));
        }
        Toast.makeText(getContext(), "Cursuri inserate cu succes!", Toast.LENGTH_SHORT).show();
    }
}