package com.tiberiugaspar.oraruaic.model;

public class Disciplina {
    private String idDisciplina;
    private String denumire;
    private String idSpecializare;
    private int anStudiu;

    public Disciplina(String idDisciplina, String denumire) {
        this.idDisciplina = idDisciplina;
        this.denumire = denumire;
    }


    public Disciplina(String idDisciplina, String denumire, String idSpecializare, int anStudiu) {
        this.idDisciplina = idDisciplina;
        this.denumire = denumire;
        this.idSpecializare = idSpecializare;
        this.anStudiu = anStudiu;
    }

    public Disciplina(String denumire) {
        this.denumire = denumire;
    }

    public Disciplina() {
    }

    public int getAnStudiu() {
        return anStudiu;
    }

    public void setAnStudiu(int anStudiu) {
        this.anStudiu = anStudiu;
    }

    public String getIdDisciplina() {
        return idDisciplina;
    }

    public void setIdDisciplina(String idDisciplina) {
        this.idDisciplina = idDisciplina;
    }

    public String getDenumire() {
        return denumire;
    }

    public String getIdSpecializare() {
        return idSpecializare;
    }

    public void setIdSpecializare(String idSpecializare) {
        this.idSpecializare = idSpecializare;
    }

    public void setDenumire(String denumire) {
        this.denumire = denumire;
    }

}
