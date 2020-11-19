package com.tiberiugaspar.oraruaic.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tiberiugaspar.oraruaic.R;
import com.tiberiugaspar.oraruaic.model.Grupa;

import java.util.ArrayList;
import java.util.List;

public class MultipleGrupaAdapter extends RecyclerView.Adapter<MultipleGrupaAdapter.MultipleGrupaViewHolder> {
    private final List<Grupa> grupe;
    private final Context context;
    public final List<Grupa> grupeSelectate = new ArrayList<>();

    public MultipleGrupaAdapter(List<Grupa> grupe, Context context) {
        this.grupe = grupe;
        this.context = context;
    }

    @NonNull
    @Override
    public MultipleGrupaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.item_check_grupa, parent, false);
        return new MultipleGrupaViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MultipleGrupaViewHolder holder, int position) {
        final Grupa grupa = grupe.get(position);
        holder.checkGrupa.setText(grupa.getDenumire());
        holder.checkGrupa.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    if (!grupeSelectate.contains(grupa)){
                        grupeSelectate.add(grupa);
                    }
                } else {
                    grupeSelectate.remove(grupa);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return grupe.size();
    }

    static class MultipleGrupaViewHolder extends RecyclerView.ViewHolder {
        final CheckBox checkGrupa;

        public MultipleGrupaViewHolder(@NonNull View itemView) {
            super(itemView);
            checkGrupa = itemView.findViewById(R.id.check_grupa);
        }
    }
}
