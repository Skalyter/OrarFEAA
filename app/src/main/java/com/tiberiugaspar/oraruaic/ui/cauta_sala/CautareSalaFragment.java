package com.tiberiugaspar.oraruaic.ui.cauta_sala;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.tiberiugaspar.oraruaic.R;
import com.tiberiugaspar.oraruaic.adapters.SalaAdapter;
import com.tiberiugaspar.oraruaic.model.ETipSala;
import com.tiberiugaspar.oraruaic.model.Sala;
import com.tiberiugaspar.oraruaic.util.DateUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CautareSalaFragment extends Fragment {

    private final Calendar calendar = DateUtil.getCalendarOraCurenta(Calendar.getInstance());

    private Button pickDate, pickHour, search;
    private TextView date, hour;
    private Spinner tipSala;
    private RecyclerView recyclerView;
    private SalaAdapter salaAdapter;

    private FirebaseFirestore db;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cauta_sala, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViewsById(view);
        db = FirebaseFirestore.getInstance();
        getActivity().findViewById(R.id.fab).setVisibility(View.GONE);
        setListeners();
    }

    private void findViewsById(View view) {
        pickDate = view.findViewById(R.id.input_date_picker);
        pickHour = view.findViewById(R.id.input_hour_picker);
        search = view.findViewById(R.id.btn_search_room);
        date = view.findViewById(R.id.label_date);
        hour = view.findViewById(R.id.label_hour);
        tipSala = view.findViewById(R.id.spinner_tip_sala);
        recyclerView = view.findViewById(R.id.recycler_sali);

        date.setText(DateUtil.getStringDateFromCalendar(calendar));
        hour.setText(DateUtil.getStringTimeFromCalendar(calendar));
    }

    private void setListeners() {
        pickDate.setOnClickListener(onDateClickListener);
        pickHour.setOnClickListener(onTimeClickListener);
        date.setOnClickListener(onDateClickListener);
        hour.setOnClickListener(onTimeClickListener);
        search.setOnClickListener(onSearchClickListener);
    }

    private final DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            calendar.set(year, month, dayOfMonth);
            date.setText(DateUtil.getStringDateFromCalendar(calendar));
        }
    };

    private final Button.OnClickListener onDateClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                    android.R.style.Theme_Material_Light_Dialog, onDateSetListener,
                    calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        }
    };

    private final TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            if (hourOfDay % 2 == 1) {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay - 1);
            } else {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            }
            calendar.set(Calendar.MINUTE, 0);
            hour.setText(DateUtil.getStringTimeFromCalendar(calendar));
        }
    };

    private final Button.OnClickListener onTimeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                    android.R.style.Theme_Material_Light_Dialog_MinWidth, onTimeSetListener,
                    calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
            timePickerDialog.show();
        }
    };

    private final Button.OnClickListener onSearchClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final ETipSala eTipSala;
            if (tipSala.getSelectedItemPosition() == 0) {
                eTipSala = ETipSala.SEMINAR;
            } else {
                eTipSala = ETipSala.AMFITEATRU;
            }
            db.collection("sali")
                    .whereEqualTo("eTipSala", eTipSala)
                    .orderBy("codSala").get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            final List<Sala> listaSali = new ArrayList<>();
                            for (DocumentSnapshot snap : queryDocumentSnapshots) {
                                listaSali.add(snap.toObject(Sala.class));
                            }
                            db.collection("cursuri_sali_ocupate")
                                    .whereEqualTo("dataOra", DateUtil.getTimestampOraCurenta(calendar))
                                    .whereEqualTo("eTipSala", eTipSala).get()
                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            for (DocumentSnapshot snap : queryDocumentSnapshots) {
                                                Sala sala = snap.toObject(Sala.class);
                                                listaSali.remove(sala);
                                            }
                                            salaAdapter = new SalaAdapter(getContext(), listaSali);
                                            recyclerView.setLayoutManager(
                                                    new LinearLayoutManager(
                                                            getContext(), RecyclerView.VERTICAL, false));
                                            recyclerView.setAdapter(salaAdapter);
                                        }
                                    });
                        }
                    });
        }
    };
}
