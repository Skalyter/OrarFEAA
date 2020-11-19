package com.tiberiugaspar.oraruaic.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tiberiugaspar.oraruaic.R;
import com.tiberiugaspar.oraruaic.model.Anunt;
import com.tiberiugaspar.oraruaic.ui.anunturi.DetaliiAnuntActivity;
import com.tiberiugaspar.oraruaic.util.DateUtil;

import java.util.List;

public class AnuntAdapter extends RecyclerView.Adapter<AnuntAdapter.AnuntViewHolder> {

    public static final String EXTRA_ID_ANUNT = "id_anunt";

    final private List<Anunt> itemList;
    private final Context context;

    public AnuntAdapter(List<Anunt> itemList, Context context) {
        this.itemList = itemList;
        this.context = context;
    }

    @NonNull
    @Override
    public AnuntViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.item_anunt, parent, false);
        return new AnuntViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AnuntViewHolder holder, int position) {
        final Anunt anunt = itemList.get(position);
        holder.titlu.setText(anunt.getTitlu());
        holder.data.setText(DateUtil.getDataAnunt(anunt.preluareCalendar()));
        holder.descriere.setText(anunt.getDescriere());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetaliiAnuntActivity.class);
                intent.putExtra(EXTRA_ID_ANUNT, anunt.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    static class AnuntViewHolder extends RecyclerView.ViewHolder {

        final TextView titlu;
        final TextView descriere;
        final TextView data;

        public AnuntViewHolder(@NonNull View itemView) {
            super(itemView);
            titlu = itemView.findViewById(R.id.titlu_anunt);
            descriere = itemView.findViewById(R.id.continut_anunt);
            data = itemView.findViewById(R.id.data_anunt);
        }
    }
}
