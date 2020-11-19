package com.tiberiugaspar.oraruaic.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tiberiugaspar.oraruaic.R;
import com.tiberiugaspar.oraruaic.model.Student;

import java.util.List;

public class PrezenteProfesorAdapter extends RecyclerView.Adapter<PrezenteProfesorAdapter.PrezenteProfesorViewHolder> {

    private final List<Student> studentList;
    private final Context context;

    public PrezenteProfesorAdapter(List<Student> studentList, Context context) {
        this.studentList = studentList;
        this.context = context;
    }

    @NonNull
    @Override
    public PrezenteProfesorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.item_prezente_profesor, parent, false);
        return new PrezenteProfesorViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PrezenteProfesorViewHolder holder, int position) {
        Student student = studentList.get(position);
        holder.numeStudent.setText(String.format("%s %s", student.getNume(), student.getPrenume()));
        holder.prezente.setText(String.format("%d", student.getNumarPrezente()));
        if (student.getUrlImagine() != null) {
            Glide.with(context).load(student.getUrlImagine()).into(holder.imagineStudent);
        }
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    static class PrezenteProfesorViewHolder extends RecyclerView.ViewHolder {

        final TextView numeStudent;
        final TextView prezente;
        final ImageView imagineStudent;

        public PrezenteProfesorViewHolder(@NonNull View itemView) {
            super(itemView);
            numeStudent = itemView.findViewById(R.id.item_nume_student);
            prezente = itemView.findViewById(R.id.item_numar_prezente);
            imagineStudent = itemView.findViewById(R.id.item_imagine_student);
        }
    }
}
