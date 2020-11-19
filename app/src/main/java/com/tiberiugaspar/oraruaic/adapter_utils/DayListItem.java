package com.tiberiugaspar.oraruaic.adapter_utils;

import com.tiberiugaspar.oraruaic.model.Curs;

public class DayListItem {
    private String header = null;
    private Curs curs = null;

    public DayListItem(String header) {
        this.header = header;
    }

    public DayListItem(Curs curs) {
        this.curs = curs;
    }

    public String getHeader() {
        return header;
    }

    public Curs getCurs() {
        return curs;
    }

    public int getType() {
        if (this.header == null) {
            return 1;
        } else {
            return 0;
        }
    }
}
