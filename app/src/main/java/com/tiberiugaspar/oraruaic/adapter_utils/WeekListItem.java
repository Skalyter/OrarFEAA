package com.tiberiugaspar.oraruaic.adapter_utils;

import com.tiberiugaspar.oraruaic.model.Curs;

public class WeekListItem {
    private String ziua;
    private String ora;
    private Curs curs;

    public WeekListItem(String ziua, String ora) {
        this.ziua = ziua;
        this.ora = ora;
    }

    public String getOra() {
        return ora;
    }

    public void setOra(String ora) {
        this.ora = ora;
    }

    public WeekListItem(Curs curs) {
        this.curs = curs;
    }

    public String getZiua() {
        return ziua;
    }

    public void setZiua(String ziua) {
        this.ziua = ziua;
    }

    public Curs getCurs() {
        return curs;
    }

    public void setCurs(Curs curs) {
        this.curs = curs;
    }

    public int getType() {
        if (this.ziua != null) {//header ziua
            return 0;
        } else if (this.ora != null) { //header ora
            return 1;
        } else if (this.curs != null){ //curs
            return 2;
        }
        return -1;
    }
}
