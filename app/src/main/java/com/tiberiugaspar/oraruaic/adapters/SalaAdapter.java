package com.tiberiugaspar.oraruaic.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tiberiugaspar.oraruaic.R;
import com.tiberiugaspar.oraruaic.model.Sala;

import java.util.List;

public class SalaAdapter extends RecyclerView.Adapter<SalaAdapter.SalaViewHolder> {

    private final Context context;
    private final List<Sala> itemList;

    public SalaAdapter(Context context, List<Sala> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public SalaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.item_sala, parent, false);
        return new SalaViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SalaViewHolder holder, int position) {
        Sala sala = itemList.get(position);
        holder.codSala.setText(sala.getCodSala());
        holder.tipSala.setText(sala.geteTipSala().toString());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    static class SalaViewHolder extends RecyclerView.ViewHolder{

        final TextView codSala;
        final TextView tipSala;

        public SalaViewHolder(@NonNull View itemView) {
            super(itemView);
            codSala = itemView.findViewById(R.id.item_cod_sala);
            tipSala = itemView.findViewById(R.id.item_tip_sala);
        }
    }
}
