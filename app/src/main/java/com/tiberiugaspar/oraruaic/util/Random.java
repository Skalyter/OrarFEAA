package com.tiberiugaspar.oraruaic.util;

public class Random {

    public static String getCodPrezenta() {
        String numere = "0123456789";
        int lungime = 6;
        StringBuilder builder = new StringBuilder(lungime);

        for (int i = 0; i < lungime; i++) {
            int index = (int) (numere.length() * Math.random());
            builder.append(numere.charAt(index));
        }
        return builder.toString();
    }
}
