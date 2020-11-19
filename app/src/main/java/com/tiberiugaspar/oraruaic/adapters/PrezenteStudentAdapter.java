package com.tiberiugaspar.oraruaic.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tiberiugaspar.oraruaic.R;
import com.tiberiugaspar.oraruaic.model.Student;

import java.util.List;

public class PrezenteStudentAdapter extends RecyclerView.Adapter<PrezenteStudentAdapter.PrezenteStudentViewHolder> {
    private final Context context;
    private final List<Student> prezente;

    public PrezenteStudentAdapter(Context context, List<Student> prezente) {
        this.context = context;
        this.prezente = prezente;
    }

    @NonNull
    @Override
    public PrezenteStudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.item_prezente_student, parent, false);
        return new PrezenteStudentViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PrezenteStudentViewHolder holder, int position) {
        Student student = prezente.get(position);
        holder.numeCurs.setText(student.getNumeCurs());
    }

    @Override
    public int getItemCount() {
        return prezente.size();
    }

    static class PrezenteStudentViewHolder extends RecyclerView.ViewHolder{

        final TextView numeCurs;

        public PrezenteStudentViewHolder(@NonNull View itemView) {
            super(itemView);
            numeCurs = itemView.findViewById(R.id.item_nume_curs);
        }
    }
}
