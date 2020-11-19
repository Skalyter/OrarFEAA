package com.tiberiugaspar.oraruaic.model;

import java.util.List;

public class Specializare {
    private String idSpecializare;
    private String denumire;
    private List<Grupa> grupe;
    private List<Disciplina> discipline;
    private ENivelStudiu eNivelStudiu;

    public Specializare(String idSpecializare, String denumire, ENivelStudiu eNivelStudiu) {
        this.idSpecializare = idSpecializare;
        this.denumire = denumire;
        this.eNivelStudiu = eNivelStudiu;
    }

    public Specializare(String denumire, ENivelStudiu eNivelStudiu){
        this.denumire = denumire;
        this.eNivelStudiu = eNivelStudiu;
    }

    public Specializare(String idSpecializare, String denumire, List<Grupa> grupe, List<Disciplina> discipline, ENivelStudiu eNivelStudiu) {
        this.idSpecializare = idSpecializare;
        this.denumire = denumire;
        this.grupe = grupe;
        this.discipline = discipline;
        this.eNivelStudiu = eNivelStudiu;
    }

    public Specializare() {
    }

    public void adaugareDisciplina(Disciplina disciplina){
        if (!this.discipline.contains(disciplina)){
            this.discipline.add(disciplina);
        }
    }
    public boolean stergeDisciplina(Disciplina disciplina){
        return this.discipline.remove(disciplina);
    }

    public void adaugareGrupa(Grupa grupa){
        if (!this.grupe.contains(grupa)){
            this.grupe.add(grupa);
        }
    }

    public boolean stergeGrupa(Grupa grupa){
        return this.grupe.remove(grupa);
    }

    public String getIdSpecializare() {
        return idSpecializare;
    }

    public void setIdSpecializare(String idSpecializare) {
        this.idSpecializare = idSpecializare;
    }

    public String getDenumire() {
        return denumire;
    }

    public void setDenumire(String denumire) {
        this.denumire = denumire;
    }

    public List<Grupa> getGrupe() {
        return grupe;
    }

    public void setGrupe(List<Grupa> grupe) {
        this.grupe = grupe;
    }

    public List<Disciplina> getDiscipline() {
        return discipline;
    }

    public void setDiscipline(List<Disciplina> discipline) {
        this.discipline = discipline;
    }

    public ENivelStudiu geteNivelStudiu() {
        return eNivelStudiu;
    }

    public void seteNivelStudiu(ENivelStudiu eNivelStudiu) {
        this.eNivelStudiu = eNivelStudiu;
    }
}
