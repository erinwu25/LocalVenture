package com.example.personalfbu;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

// to confirm deleting a listing
public class ConfirmDeleteListing extends AppCompatDialogFragment {

    private ConfirmDeleteListingListener listener;

    // necessary empty constructor
    public ConfirmDeleteListing() {
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Delete this listing?")
                .setMessage("It will not be recoverable.")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // do nothing
                    }
                })
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        listener.onDeleteClicked();
                    }
                });
        return builder.create();
    }

    public interface ConfirmDeleteListingListener {
        void onDeleteClicked();

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (ConfirmDeleteListingListener) context;
        }
        catch (ClassCastException e){
            Log.e("ConfirmDeleteListing", "error on attachment", e);
        }
    }
}
