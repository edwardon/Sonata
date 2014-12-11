package com.app.musicplayer.Custom;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;

import com.app.musicplayer.R;

/**
 * Created by Yuwei on 2014-12-09.
 */
public class AddDialogFragment extends DialogFragment {
    AlertDialog.Builder builder;
    LayoutInflater inflater;

    public AddDialogFragment(){
        Activity context = getActivity();
        //inflater = context.getLayoutInflater();
    }

    @Override
    public Dialog onCreateDialog (Bundle savedInstanceState){
        builder = new AlertDialog.Builder(getActivity());
        inflater = getActivity().getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.rename_layout,null))
                .setPositiveButton(R.string.name_confirm, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id){

                    }
                })
                .setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        AddDialogFragment.this.getDialog().cancel();
                    }
                });

        // Create the AlertDialog object and return it
        return builder.create();
    }
}
