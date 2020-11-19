package com.tiberiugaspar.oraruaic.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tiberiugaspar.oraruaic.R;
import com.tiberiugaspar.oraruaic.model.Student;

import java.util.List;

public class ParticipantAdapter extends RecyclerView.Adapter<ParticipantAdapter.StudentViewHolder> {

    private final List<Student> studentList;
    private final Context context;
    private final String idCurs;
    private final FirebaseFirestore db;

    public ParticipantAdapter(List<Student> studentList, Context context, String idCurs) {
        this.studentList = studentList;
        this.context = context;
        this.idCurs = idCurs;
        db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.item_participant, parent, false);
        return new StudentViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, final int position) {
        final Student student = studentList.get(position);
        holder.nume.setText(String.format("%s %s", student.getNume(), student.getPrenume()));
        holder.stergere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("participanti_curs")
                        .document(String.format("%s_%s", idCurs, student.getIdStudent()))
                        .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        studentList.remove(student);
                        notifyItemRemoved(position);
                        notifyItemRangeRemoved(position, studentList.size());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Ceva nu a functionat!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        if (student.getUrlImagine() != null){
            Glide.with(context).load(student.getUrlImagine()).into(holder.imagineStudent);
        }
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    static class StudentViewHolder extends RecyclerView.ViewHolder{

        final TextView nume;
        final ImageView stergere;
        final ImageView imagineStudent;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            nume = itemView.findViewById(R.id.item_nume_student);
            stergere = itemView.findViewById(R.id.btn_stergere_student);
            imagineStudent = itemView.findViewById(R.id.item_imagine_student);
        }
    }
}
