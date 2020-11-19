package com.tiberiugaspar.oraruaic.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tiberiugaspar.oraruaic.R;
import com.tiberiugaspar.oraruaic.model.Profesor;

import java.util.List;

public class ContactProfesoriAdapter extends RecyclerView.Adapter<ContactProfesoriAdapter.ContactProfesoriViewHolder> {

    private final Context context;
    private final List<Profesor> itemList;

    public ContactProfesoriAdapter(Context context, List<Profesor> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ContactProfesoriViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.item_contact_profesori, parent, false);
        return new ContactProfesoriViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactProfesoriViewHolder holder, int position) {
        final Profesor profesor = itemList.get(position);
        holder.email.setText(profesor.getEmail());
        holder.numeProfesor.setText(String.format("%s %s %s", profesor.getTitulatura(), profesor.getNume(), profesor.getPrenume()));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deschideMail(new String[] {profesor.getEmail()});
            }
        });
        if (profesor.getUrlImagine() != null){
            Glide.with(context).load(profesor.getUrlImagine()).into(holder.imagineProfesor);
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    static class ContactProfesoriViewHolder extends RecyclerView.ViewHolder{
        final TextView numeProfesor;
        final TextView email;
        final ImageView imagineProfesor;
        public ContactProfesoriViewHolder(@NonNull View itemView) {
            super(itemView);
            numeProfesor = itemView.findViewById(R.id.item_nume_profesor);
            email = itemView.findViewById(R.id.item_mail_profesor);
            imagineProfesor = itemView.findViewById(R.id.item_imagine_profesor);
        }
    }

    public void deschideMail(String[] email) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // doar aplicatiile de email pot accesa intentul
        intent.putExtra(Intent.EXTRA_EMAIL, email);
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }
    }
}
