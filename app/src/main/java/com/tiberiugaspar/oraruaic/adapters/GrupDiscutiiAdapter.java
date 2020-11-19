package com.tiberiugaspar.oraruaic.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tiberiugaspar.oraruaic.R;
import com.tiberiugaspar.oraruaic.model.GrupDiscutii;
import com.tiberiugaspar.oraruaic.ui.grupuri_discutii.ConversatieGrupActivity;

import java.util.List;

public class GrupDiscutiiAdapter extends RecyclerView.Adapter<GrupDiscutiiAdapter.GrupDiscutiiViewHolder> {

    public static final String EXTRA_ID_GRUP = "extra_id_grup";
    public static final String EXTRA_DENUMIRE_GRUP = "extra_denumire_grup";
    private final Context context;
    private final List<GrupDiscutii> itemList;

    public GrupDiscutiiAdapter(Context context, List<GrupDiscutii> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public GrupDiscutiiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.item_grup_discutii, parent, false);
        return new GrupDiscutiiViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull GrupDiscutiiViewHolder holder, int position) {
        final GrupDiscutii grupDiscutii = itemList.get(position);
        holder.denumire.setText(grupDiscutii.getDenumire());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ConversatieGrupActivity.class);
                intent.putExtra(EXTRA_ID_GRUP, grupDiscutii.getIdGrup());
                intent.putExtra(EXTRA_DENUMIRE_GRUP, grupDiscutii.getDenumire());
                ((Activity) context).startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    static class GrupDiscutiiViewHolder extends RecyclerView.ViewHolder {

        final TextView denumire;

        public GrupDiscutiiViewHolder(@NonNull View itemView) {
            super(itemView);
            denumire = itemView.findViewById(R.id.item_denumire_grup);
        }
    }
}

