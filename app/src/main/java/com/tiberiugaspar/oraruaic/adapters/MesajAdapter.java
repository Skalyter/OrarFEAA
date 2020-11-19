package com.tiberiugaspar.oraruaic.adapters;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.tiberiugaspar.oraruaic.R;
import com.tiberiugaspar.oraruaic.adapter_utils.Mesaj;
import com.tiberiugaspar.oraruaic.util.DateUtil;

import java.io.File;
import java.util.List;

public class MesajAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context context;
    private final List<Mesaj> mesaje;

    public MesajAdapter(Context context, List<Mesaj> mesaje) {
        this.context = context;
        this.mesaje = mesaje;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        if (viewType==0){
            View item = inflater.inflate(R.layout.item_mesaj, parent, false);
            return new MesajViewHolder(item);
        } else {
            View item = inflater.inflate(R.layout.item_imagine, parent, false);
            return new ImagineViewHolder(item);
        }
    }

    @Override
    public int getItemViewType(int position) {
        Mesaj mesaj = mesaje.get(position);
        if (mesaj.getUrlImagine()== null){
            return 0; //text/document
        }
        return 1; //imagine
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final Mesaj mesaj = mesaje.get(position);
        if (mesaj.getUrlImagine() == null ){
            if (mesaj.getMesaj() != null){
                //text
                ((MesajViewHolder)holder).itemMesaj.setText(mesaj.getMesaj());
            } else {
                //document
                ((MesajViewHolder)holder).itemMesaj.setText(Uri.parse(mesaj.getUrlDocument()).getLastPathSegment());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        download(mesaj.getUrlDocument());
                    }
                });
            }
            ((MesajViewHolder)holder).itemNume.setText(mesaj.getNume());
        } else {
            //imagine
            Glide.with(context).load(mesaj.getUrlImagine()).into(((ImagineViewHolder)holder).itemImagine);
            ((ImagineViewHolder)holder).itemNume.setText(mesaj.getNume());
        }
    }

    @Override
    public int getItemCount() {
        return mesaje.size();
    }

    static class MesajViewHolder extends RecyclerView.ViewHolder{

        final TextView itemNume;
        final TextView itemMesaj;

        public MesajViewHolder(@NonNull View itemView) {
            super(itemView);
            itemNume = itemView.findViewById(R.id.item_nume);
            itemMesaj = itemView.findViewById(R.id.item_mesaj);
        }
    }

    static class ImagineViewHolder extends RecyclerView.ViewHolder{

        final ImageView itemImagine;
        final TextView itemNume;

        public ImagineViewHolder(@NonNull View itemView) {
            super(itemView);
            itemNume = itemView.findViewById(R.id.item_nume);
            itemImagine = itemView.findViewById(R.id.item_imagine);
        }
    }
    private void download(String url) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl(url);
        StorageReference  islandRef = storageRef.child(Uri.parse(url).getLastPathSegment());

        File radacina = new File(Environment.getExternalStorageDirectory(), islandRef.getName());
        if(!radacina.exists()) {
            radacina.mkdirs();
        }

        final File localFile = new File(radacina,islandRef.getName());

        islandRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Log.e(DateUtil.TAG,";local tem file created  created " +localFile.toString());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e(DateUtil.TAG,";local tem file not created  created " +exception.toString());
            }
        });
    }
}
