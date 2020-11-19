package com.tiberiugaspar.oraruaic.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tiberiugaspar.oraruaic.R;
import com.tiberiugaspar.oraruaic.model.Grupa;

import java.util.List;

public class SingleGrupaAdapter extends RecyclerView.Adapter<SingleGrupaAdapter.SingleGrupaViewHolder> {
    final private List<Grupa> grupe;
    private final Context context;
    public int mSelectedItem = -1;

    public SingleGrupaAdapter(List<Grupa> grupe, Context context) {
        this.grupe = grupe;
        this.context = context;
    }

    @NonNull
    @Override
    public SingleGrupaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.item_radio_grupa, parent, false);
        return new SingleGrupaViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SingleGrupaViewHolder holder, final int position) {
        final Grupa grupa = grupe.get(position);
        holder.radioGrupa.setText(grupa.getDenumire());
        holder.radioGrupa.setChecked(position == mSelectedItem);
        holder.radioGrupa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSelectedItem = position;
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return grupe.size();
    }

    static class SingleGrupaViewHolder extends RecyclerView.ViewHolder {
        final RadioButton radioGrupa;

        public SingleGrupaViewHolder(@NonNull View itemView) {
            super(itemView);
            radioGrupa = (RadioButton) itemView.findViewById(R.id.radio_grupa);
        }
    }
}
