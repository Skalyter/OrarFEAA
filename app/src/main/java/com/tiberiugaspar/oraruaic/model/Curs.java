package com.tiberiugaspar.oraruaic.model;

import com.google.firebase.Timestamp;
import com.tiberiugaspar.oraruaic.util.DateUtil;

import java.util.Calendar;
import java.util.List;

public class Curs {
    private String id;
    private String denumire;
    private String descriere;
    private String idProfesor;
    private Calendar dataOra;
    private String idDisciplina;
    private String idSala;
    private String codSala;
    private String numeProfesor;
    private List<String> idGrupe;
    private String codValidarePrezenta;
    private ETipSala eTipSala;

    public Curs() {
    }

    public Curs(String id, Calendar dataOra, String idSala, ETipSala eTipSala) {
        this.id = id;
        this.dataOra = dataOra;
        this.idSala = idSala;
        this.eTipSala = eTipSala;
    }

    public Curs(String id, String denumire, String idProfesor, String numeProfesor, Calendar dataOra,
                String idDisciplina, String idSala, String codSala, List<String> idGrupe, ETipSala eTipSala) {
        this.id = id;
        this.denumire = denumire;
        this.idProfesor = idProfesor;
        this.numeProfesor = numeProfesor;
        this.dataOra = dataOra;
        this.idDisciplina = idDisciplina;
        this.idSala = idSala;
        this.codSala = codSala;
        this.idGrupe = idGrupe;
        this.eTipSala = eTipSala;
    }

    public String getCodSala() {
        return codSala;
    }

    public void setCodSala(String codSala) {
        this.codSala = codSala;
    }

    public String getNumeProfesor() {
        return numeProfesor;
    }

    public void setNumeProfesor(String numeProfesor) {
        this.numeProfesor = numeProfesor;
    }

    public void setDataOra(Timestamp dataOra) {
        this.dataOra = DateUtil.getCalendar(dataOra);
    }

    public Timestamp getDataOra(){
        return DateUtil.getTimestamp(dataOra);
    }
    public ETipSala geteTipSala() {
        return eTipSala;
    }

    public void seteTipSala(ETipSala eTipSala) {
        this.eTipSala = eTipSala;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDenumire() {
        return denumire;
    }

    public void setDenumire(String denumire) {
        this.denumire = denumire;
    }

    public String getDescriere() {
        return descriere;
    }

    public void setDescriere(String descriere) {
        this.descriere = descriere;
    }

    public String getIdProfesor() {
        return idProfesor;
    }

    public void setIdProfesor(String idProfesor) {
        this.idProfesor = idProfesor;
    }

    public Calendar preluareCalendar(){
        return this.dataOra;
    }

    public void setareCalendar(Calendar calendar){
        this.dataOra = calendar;
    }

    public void setIdDisciplina(String idDisciplina) {
        this.idDisciplina = idDisciplina;
    }

    public String getIdSala() {
        return idSala;
    }

    public void setIdSala(String idSala) {
        this.idSala = idSala;
    }

    public List<String> getIdGrupe() {
        return idGrupe;
    }

    public void setIdGrupe(List<String> idGrupe) {
        this.idGrupe = idGrupe;
    }

    public String getCodValidarePrezenta() {
        return codValidarePrezenta;
    }


    public String getIdDisciplina() {
        return idDisciplina;
    }

    public void setCodValidarePrezenta(String codValidarePrezenta) {
        this.codValidarePrezenta = codValidarePrezenta;
    }
}