package com.tiberiugaspar.oraruaic.model;

public class Grupa {
    private String idGrupa;
    private String denumire;
    private String idSpecializare;
    private int promotia;

    public Grupa() {
    }

    public Grupa(String denumire, String idSpecializare, int promotia) {
        this.denumire = denumire;
        this.idSpecializare = idSpecializare;
        this.promotia = promotia;
    }

    public Grupa(String idGrupa, String denumire, String idSpecializare, int promotia) {
        this.idGrupa = idGrupa;
        this.denumire = denumire;
        this.idSpecializare = idSpecializare;
        this.promotia = promotia;
    }

    public String getIdGrupa() {
        return idGrupa;
    }

    public void setIdGrupa(String idGrupa) {
        this.idGrupa = idGrupa;
    }

    public String getDenumire() {
        return denumire;
    }

    public void setDenumire(String denumire) {
        this.denumire = denumire;
    }

    public String getIdSpecializare() {
        return idSpecializare;
    }

    public void setIdSpecializare(String idSpecializare) {
        this.idSpecializare = idSpecializare;
    }

    public int getPromotia() {
        return promotia;
    }

    public void setPromotia(int promotia) {
        this.promotia = promotia;
    }
}
