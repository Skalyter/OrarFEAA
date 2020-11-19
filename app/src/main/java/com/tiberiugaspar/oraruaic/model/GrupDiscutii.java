package com.tiberiugaspar.oraruaic.model;

import java.util.List;

public class GrupDiscutii {
    private String idGrup;
    private List<String> idGrupe;
    private String idProfesor;
    private String denumire;

    public GrupDiscutii() {
    }

    public GrupDiscutii(String idGrup, List<String> idGrupe, String idProfesor, String denumire) {
        this.idGrup = idGrup;
        this.idGrupe = idGrupe;
        this.idProfesor = idProfesor;
        this.denumire = denumire;
    }

    public String getIdGrup() {
        return idGrup;
    }

    public void setIdGrup(String idGrup) {
        this.idGrup = idGrup;
    }

    public List<String> getIdGrupe() {
        return idGrupe;
    }

    public void setIdGrupe(List<String> idGrupe) {
        this.idGrupe = idGrupe;
    }

    public String getIdProfesor() {
        return idProfesor;
    }

    public void setIdProfesor(String idProfesor) {
        this.idProfesor = idProfesor;
    }

    public String getDenumire() {
        return denumire;
    }

    public void setDenumire(String denumire) {
        this.denumire = denumire;
    }
}
