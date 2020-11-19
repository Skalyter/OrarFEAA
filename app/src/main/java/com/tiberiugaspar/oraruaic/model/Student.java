package com.tiberiugaspar.oraruaic.model;

import java.util.Objects;

public class Student {
    private String idStudent;
    private String nume;
    private String prenume;
    private String email;
    private String matricol;
    private String idGrupa;
    private String codValidarePrezenta;
    private boolean utilizatorNevalidat;
    private String numeCurs;
    private String idCurs;
    private String idDisciplina;
    private int numarPrezente;
    private String urlImagine;

    public Student(String nume, String prenume, String email, String matricol, String idGrupa) {
        this.nume = nume;
        this.prenume = prenume;
        this.email = email;
        this.nume = nume;
        this.prenume = prenume;
        this.email = email;
        this.matricol = matricol;
        this.idGrupa = idGrupa;
    }

    public Student(String idStudent, String nume, String prenume, String email, String matricol, String idGrupa) {
        this.idStudent = idStudent;
        this.nume = nume;
        this.prenume = prenume;
        this.email = email;
        this.matricol = matricol;
        this.idGrupa = idGrupa;
    }


    public Student() {
    }

    public String getUrlImagine() {
        return urlImagine;
    }

    public void setUrlImagine(String urlImagine) {
        this.urlImagine = urlImagine;
    }

    public String getIdStudent() {
        return idStudent;
    }

    public void setIdStudent(String idStudent) {
        this.idStudent = idStudent;
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

    public String getMatricol() {
        return matricol;
    }

    public void setMatricol(String matricol) {
        this.matricol = matricol;
    }

    public String getIdGrupa() {
        return idGrupa;
    }

    public void setIdGrupa(String idGrupa) {
        this.idGrupa = idGrupa;
    }

    public String getCodValidarePrezenta() {
        return codValidarePrezenta;
    }

    public void setCodValidarePrezenta(String codValidarePrezenta) {
        this.codValidarePrezenta = codValidarePrezenta;
    }

    public boolean isUtilizatorNevalidat() {
        return utilizatorNevalidat;
    }

    public void setUtilizatorNevalidat(boolean utilizatorNevalidat) {
        this.utilizatorNevalidat = utilizatorNevalidat;
    }

    public String getNumeCurs() {
        return numeCurs;
    }

    public void setNumeCurs(String numeCurs) {
        this.numeCurs = numeCurs;
    }

    public String getIdDisciplina() {
        return idDisciplina;
    }

    public void setIdDisciplina(String idDisciplina) {
        this.idDisciplina = idDisciplina;
    }

    public int getNumarPrezente() {
        return numarPrezente;
    }

    public void setNumarPrezente(int numarPrezente) {
        this.numarPrezente = numarPrezente;
    }

    public String getIdCurs() {
        return idCurs;
    }

    public void setIdCurs(String idCurs) {
        this.idCurs = idCurs;
    }

    public void adaugaPrezenta(){
        this.numarPrezente++;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return Objects.equals(idStudent, student.idStudent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idStudent);
    }
}
