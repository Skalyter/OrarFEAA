package com.tiberiugaspar.oraruaic.util;

import android.content.Context;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tiberiugaspar.oraruaic.R;
import com.tiberiugaspar.oraruaic.model.Disciplina;
import com.tiberiugaspar.oraruaic.model.ENivelStudiu;
import com.tiberiugaspar.oraruaic.model.ETipSala;
import com.tiberiugaspar.oraruaic.model.Grupa;
import com.tiberiugaspar.oraruaic.model.Sala;
import com.tiberiugaspar.oraruaic.model.Specializare;

public class DatabaseInsertUtil {
    private static final String idInfoEc = "3NQoYqJ2imhxX57Wpo47";
    public static void adaugareSpecializari(Context context) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        for (int i = 0; i < context.getResources().getStringArray(R.array.lista_specializari_licenta).length; i++) {
            DocumentReference docRef = db.collection("specializari").document();
            docRef.set(new Specializare(docRef.getId(),
                    context.getResources().getStringArray(R.array.lista_specializari_licenta)[i],
                    ENivelStudiu.LICENTA));
        }
        for (int i = 0; i < context.getResources().getStringArray(R.array.lista_specializari_master).length; i++) {
            DocumentReference docRef = db.collection("specializari").document();
            docRef.set(new Specializare(docRef.getId(),
                    context.getResources().getStringArray(R.array.lista_specializari_master)[i],
                    ENivelStudiu.MASTER));
        }
    }

    public static void adaugareDiscipline(Context context) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        for (int i = 0; i < context.getResources().getStringArray(R.array.discipline_info_ec_anul_1).length; i++) {
            DocumentReference docRef = db.collection("discipline").document();
            docRef.set(new Disciplina(docRef.getId(),
                    context.getResources().getStringArray(R.array.discipline_info_ec_anul_1)[i],
                    idInfoEc, 1));
        }
        for (int i = 0; i < context.getResources().getStringArray(R.array.discipline_info_ec_anul_2).length; i++) {
            DocumentReference docRef = db.collection("discipline").document();
            docRef.set(new Disciplina(docRef.getId(),
                    context.getResources().getStringArray(R.array.discipline_info_ec_anul_2)[i],
                    idInfoEc, 2));
        }
        for (int i = 0; i < context.getResources().getStringArray(R.array.discipline_info_ec_anul_3).length; i++) {
            DocumentReference docRef = db.collection("discipline").document();
            docRef.set(new Disciplina(docRef.getId(),
                    context.getResources().getStringArray(R.array.discipline_info_ec_anul_3)[i],
                    idInfoEc, 3));
        }
    }

    public static void adaugareGrupe() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        for (int i=1; i<=3;i++){
            int promotia = DateUtil.getAnPromotie(i);
            for (int j=1;j<=5; j++){
                String denumire = String.format("IE%d%d", i, j);
                DocumentReference docRef = db.collection("grupe").document();
                docRef.set(new Grupa(docRef.getId(), denumire, idInfoEc, promotia));
            }
        }
    }

    public static void adaugareSali(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        for (int i=1; i<=29; i++){
//            String codSala;
//            if (i<10){
//                codSala = "B60";
//            } else {
//                codSala = "B6";
//            }
//            DocumentReference docRef = db.collection("sali").document();
//            docRef.set(new Sala(docRef.getId(), String.format("%s%d", codSala, i), ETipSala.SEMINAR));
//        }
        DocumentReference docRef = db.collection("sali").document();
        docRef.set(new Sala(docRef.getId(), "B4", ETipSala.AMFITEATRU));
//        DocumentReference docRef2 = db.collection("sali").document();
//        docRef2.set(new Sala(docRef.getId(), "B5", ETipSala.AMFITEATRU));
    }

//    StringBuilder initialeSpecializare = new StringBuilder();
//        for (int i = 0; i < specializare.length(); i++) {
//        if (Character.isUpperCase(specializare.charAt(i))) {
//            initialeSpecializare.append(specializare.charAt(i));
//        }
//    }
}
