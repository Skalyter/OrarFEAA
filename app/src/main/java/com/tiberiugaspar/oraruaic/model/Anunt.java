package com.tiberiugaspar.oraruaic.model;

import com.google.firebase.Timestamp;

import java.util.Calendar;

public class Anunt {
    private String id;
    private String titlu;
    private String descriere;
    private Calendar data;

    public Anunt(String titlu, String descriere, Calendar data) {
        this.titlu = titlu;
        this.descriere = descriere;
        this.data = data;
    }

    public Anunt(String id, String titlu, String descriere, Calendar data) {
        this.id = id;
        this.titlu = titlu;
        this.descriere = descriere;
        this.data = data;
    }

    public Anunt() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitlu() {
        return titlu;
    }

    public void setTitlu(String titlu) {
        this.titlu = titlu;
    }

    public String getDescriere() {
        return descriere;
    }

    public void setDescriere(String descriere) {
        this.descriere = descriere;
    }

    public Calendar preluareCalendar() {
        return data;
    }

    public void setareCalendar(Calendar calendar) {
        this.data = calendar;
    }

    public Timestamp getData() {
        return new Timestamp(this.data.getTime());
    }

    public void setData(Timestamp data) {
        long timestamp = data.getSeconds() * 1000;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        this.data = calendar;
    }
}
