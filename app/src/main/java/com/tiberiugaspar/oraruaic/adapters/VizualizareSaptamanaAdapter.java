package com.tiberiugaspar.oraruaic.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tiberiugaspar.oraruaic.R;
import com.tiberiugaspar.oraruaic.adapter_utils.WeekListItem;
import com.tiberiugaspar.oraruaic.model.Curs;
import com.tiberiugaspar.oraruaic.ui.orar.DetaliiCursActivity;
import com.tiberiugaspar.oraruaic.ui.orar.EditareCursActivity;

import java.util.List;

import static com.tiberiugaspar.oraruaic.adapters.VizualizareZiAdapter.EXTRA_ID_CURS;
import static com.tiberiugaspar.oraruaic.adapters.VizualizareZiAdapter.REQ_EDITARE_CURS;
import static com.tiberiugaspar.oraruaic.util.DateUtil.TAG;

public class VizualizareSaptamanaAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    final private List<WeekListItem> itemList;
    private final Context context;
    private final String tipUtilizator;

    public VizualizareSaptamanaAdapter(List<WeekListItem> itemList, Context context, String tipUtilizator) {
        this.itemList = itemList;
        this.context = context;
        this.tipUtilizator = tipUtilizator;
    }

    static class ZiuaViewHolder extends RecyclerView.ViewHolder{
        final TextView ziua;

        public ZiuaViewHolder(@NonNull View itemView) {
            super(itemView);
            ziua = itemView.findViewById(R.id.item_day);
        }
    }
    static class OraViewHolder extends  RecyclerView.ViewHolder{
        final TextView ora;

        public OraViewHolder(@NonNull View itemView) {
            super(itemView);
            ora = itemView.findViewById(R.id.item_ora);
        }
    }

    static class CursViewHolder extends RecyclerView.ViewHolder{
        final TextView denumire;
        final TextView sala;
        final TextView descriere;
        final ImageView edit;

        public CursViewHolder(@NonNull View itemView) {
            super(itemView);
            denumire = itemView.findViewById(R.id.item_course_name);
            sala = itemView.findViewById(R.id.item_location_text);
            descriere = itemView.findViewById(R.id.item_course_description);
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
        switch (viewType){
            case 0:
                itemView = inflater.inflate(R.layout.item_header_ziua, parent, false);
                return new ZiuaViewHolder(itemView);
            case 1:
                itemView = inflater.inflate(R.layout.item_header_ora, parent, false);
                return new OraViewHolder(itemView);
            case 2:
                itemView = inflater.inflate(R.layout.item_curs, parent, false);
                return new CursViewHolder(itemView);
            default:
                Log.d(TAG, "onCreateViewHolder: eroare la crearea viewHolder saptamana ");
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)){
            case 0:
                String ziua = itemList.get(position).getZiua();
                ZiuaViewHolder ziuaViewHolder = (ZiuaViewHolder) holder;
                ziuaViewHolder.ziua.setText(ziua);
                break;
            case 1:
                String ora = itemList.get(position).getOra();
                OraViewHolder viewHolder = (OraViewHolder) holder;
                viewHolder.ora.setText(ora);
                break;
            case 2:
                final Curs curs = itemList.get(position).getCurs();
                CursViewHolder cursViewHolder = (CursViewHolder) holder;
                cursViewHolder.denumire.setText(curs.getDenumire());
                cursViewHolder.descriere.setText(curs.getDescriere());
                cursViewHolder.sala.setText(curs.getCodSala());
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
