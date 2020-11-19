package com.tiberiugaspar.oraruaic.adapter_utils;

public class Mesaj {
    private String nume;
    private String mesaj;
    private String urlImagine;
    private String urlDocument;

    public Mesaj() {
    }

    public Mesaj(String nume, String mesaj, String urlImagine, String urlDocument) {
        this.nume = nume;
        this.mesaj = mesaj;
        this.urlImagine = urlImagine;
        this.urlDocument = urlDocument;
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public String getMesaj() {
        return mesaj;
    }

    public void setMesaj(String mesaj) {
        this.mesaj = mesaj;
    }

    public String getUrlImagine() {
        return urlImagine;
    }

    public void setUrlImagine(String urlImagine) {
        this.urlImagine = urlImagine;
    }

    public String getUrlDocument() {
        return urlDocument;
    }

    public void setUrlDocument(String urlDocument) {
        this.urlDocument = urlDocument;
    }
}
