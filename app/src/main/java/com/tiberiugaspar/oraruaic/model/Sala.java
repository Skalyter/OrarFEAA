package com.tiberiugaspar.oraruaic.model;

import java.util.Objects;

public class Sala {
    private String idSala;
    private String codSala;
    private ETipSala eTipSala;

    public Sala(String idSala, String codSala, ETipSala eTipSala) {
        this.idSala = idSala;
        this.codSala = codSala;
        this.eTipSala = eTipSala;
    }

    public Sala(String codSala, ETipSala eTipSala) {
        this.codSala = codSala;
        this.eTipSala = eTipSala;
    }

    public Sala() {
    }

    public String getIdSala() {
        return idSala;
    }

    public void setIdSala(String idSala) {
        this.idSala = idSala;
    }

    public String getCodSala() {
        return codSala;
    }

    public void setCodSala(String codSala) {
        this.codSala = codSala;
    }

    public ETipSala geteTipSala() {
        return eTipSala;
    }

    public void seteTipSala(ETipSala eTipSala) {
        this.eTipSala = eTipSala;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sala sala = (Sala) o;
        return Objects.equals(idSala, sala.idSala);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idSala);
    }
}
