package com.tiberiugaspar.oraruaic.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tiberiugaspar.oraruaic.R;
import com.tiberiugaspar.oraruaic.model.Curs;
import com.tiberiugaspar.oraruaic.adapter_utils.DayListItem;
import com.tiberiugaspar.oraruaic.ui.orar.DetaliiCursActivity;
import com.tiberiugaspar.oraruaic.ui.orar.EditareCursActivity;

import java.util.List;

public class VizualizareZiAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final String EXTRA_ID_CURS = "id_curs";
    public static final int REQ_EDITARE_CURS =  102;

    final private List<DayListItem> itemList;
    private final Context context;
    private final String tipUtilizator;

    public VizualizareZiAdapter(List<DayListItem> itemList, Context context, String tipUtilizator) {
        this.itemList = itemList;
        this.context = context;
        this.tipUtilizator = tipUtilizator;
    }

    static class OraViewHolder extends RecyclerView.ViewHolder {
        final TextView hour;

        public OraViewHolder(@NonNull View itemView) {
            super(itemView);
            hour = itemView.findViewById(R.id.item_ora);
        }
    }

    static class CursViewHolder extends RecyclerView.ViewHolder {
        final TextView name;
        final TextView location;
        final TextView description;
        final ImageView edit;

        public CursViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.item_course_name);
            location = itemView.findViewById(R.id.item_location_text);
            description = itemView.findViewById(R.id.item_course_description);
            edit = itemView.findViewById(R.id.item_edit_event);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return itemList.get(position).getType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView;
        switch (viewType) {
            case 0:
                itemView = inflater.inflate(R.layout.item_header_ora, parent, false);
                return new OraViewHolder(itemView);
            case 1:
                itemView = inflater.inflate(R.layout.item_curs, parent, false);
                return new CursViewHolder(itemView);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case 0:
                String header = itemList.get(position).getHeader();
                OraViewHolder viewHolder = (OraViewHolder) holder;
                viewHolder.hour.setText(header);
                break;
            case 1:
                final Curs curs = itemList.get(position).getCurs();
                CursViewHolder cursViewHolder = (CursViewHolder) holder;
                cursViewHolder.name.setText(curs.getDenumire());
                cursViewHolder.description.setText(curs.getDescriere());
                cursViewHolder.location.setText(curs.getCodSala());
                if (tipUtilizator.equals(context.getString(R.string.user_student))){
                    cursViewHolder.edit.setVisibility(View.GONE);
                } else {
                    cursViewHolder.edit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, EditareCursActivity.class);
                            intent.putExtra(EXTRA_ID_CURS, curs.getId());
                            ((Activity)context).startActivityForResult(intent, REQ_EDITARE_CURS);
                        }
                    });
                }
                cursViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, DetaliiCursActivity.class);
                        intent.putExtra(EXTRA_ID_CURS, curs.getId());
                        ((Activity)context).startActivityForResult(intent, REQ_EDITARE_CURS);
                    }
                });
                break;
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
