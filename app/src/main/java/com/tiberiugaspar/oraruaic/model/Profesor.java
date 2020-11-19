package com.tiberiugaspar.oraruaic.model;

public class Profesor {
    private String idProfesor;
    private String nume;
    private String prenume;
    private String email;
    private String titulatura;
    private boolean utilizatorNevalidat;
    private String urlImagine;

    public Profesor(String idProfesor, String nume, String prenume, String email, String titulatura) {
        this.idProfesor = idProfesor;
        this.nume = nume;
        this.prenume = prenume;
        this.email = email;
        this.titulatura = titulatura;
    }

    public Profesor() {
    }

    public String getUrlImagine() {
        return urlImagine;
    }

    public void setUrlImagine(String urlImagine) {
        this.urlImagine = urlImagine;
    }

    public String getIdProfesor() {
        return idProfesor;
    }

    public void setIdProfesor(String idProfesor) {
        this.idProfesor = idProfesor;
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public String getPrenume() {
        return prenume;
    }

    public void setPrenume(String prenume) {
        this.prenume = prenume;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTitulatura() {
        return titulatura;
    }

    public void setTitulatura(String titulatura) {
        this.titulatura = titulatura;
    }

    public boolean isUtilizatorNevalidat() {
        return utilizatorNevalidat;
    }

    public void setUtilizatorNevalidat(boolean utilizatorNevalidat) {
        this.utilizatorNevalidat = utilizatorNevalidat;
    }
}
