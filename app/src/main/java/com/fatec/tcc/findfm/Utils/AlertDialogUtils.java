package com.fatec.tcc.findfm.Utils;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.v7.app.AlertDialog;

/**
 * Classe para facilitar criação de AlertDialogs
 * **/
public class AlertDialogUtils {

    /**
     * Cria AlertDialog com único botão e mensagem personalizada
     * @param usaThis Context da tela, favor usar 'this' ao chamar esse método dentro da activity
     * @param titulo Titulo do AlertDialog,
     * @param icone Icone do AlertDialog, acessado por R.drawable.icone
     * @param mensagem Mensagem no corpo do AlertDialog
     * @param textoPositiveButton Texto que aparecerá no botão com ação positiva (Ex: "OK", "Sim, concordo")
     * @param listenerOk Listener quando o botão com ação positiva for pressionado, faver overwrite/lambda
     * **/
    public static AlertDialog.Builder newSimpleDialog__OneButton(Context usaThis, String titulo, int icone, String mensagem,
                                                         String textoPositiveButton,
                                                         final DialogInterface.OnClickListener listenerOk){
        AlertDialog.Builder builder;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(usaThis, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(usaThis);
        }

        builder.setMessage(mensagem)
                .setCancelable(false)
                .setPositiveButton(textoPositiveButton, listenerOk)
                .setTitle(titulo)
                .setIcon(icone);
        return builder;
    }

    /**
     * Cria AlertDialog com dois botões e mensagem personalizada
     * @param usaThis Context da tela, favor usar 'this' ao chamar esse método dentro da activity
     * @param titulo Titulo do AlertDialog,
     * @param icone Icone do AlertDialog, acessado por R.drawable.icone
     * @param mensagem Mensagem no corpo do AlertDialog
     * @param textoPositiveButton Texto que aparecerá no botão com ação positiva (Ex: "OK", "Sim, concordo")
     * @param textoNegativeButton Texto que aparecerá no botão com ação negativa (Ex: "Não", "Não concordo")
     * @param listenerOk Listener quando o botão com ação positiva for pressionado, faver overwrite/lambda
     * @param listenerNegative Listener quando o botão com ação negativa for pressionado, faver overwrite/lambda
     * **/
    public static AlertDialog newSimpleDialog__TwoButtons(Context usaThis, String titulo, int icone, String mensagem,
                                              String textoPositiveButton, String textoNegativeButton,
                                              final DialogInterface.OnClickListener listenerOk,
                                              final DialogInterface.OnClickListener listenerNegative){
        AlertDialog.Builder builder;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(usaThis, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(usaThis);
        }

        builder.setMessage(mensagem)
                .setTitle(titulo)
                .setIcon(icone)
                .setCancelable(false)
                .setPositiveButton(textoPositiveButton, listenerOk)
                .setNegativeButton(textoNegativeButton, listenerNegative);
        AlertDialog alert = builder.create();
        return alert;
    }


}
