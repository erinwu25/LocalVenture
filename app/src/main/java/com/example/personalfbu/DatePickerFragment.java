package com.example.personalfbu;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.text.DateFormat;
import java.util.Calendar;


// for creating date picker fragments
public class DatePickerFragment extends DialogFragment {
    DatePickerDialog.OnDateSetListener onDateSet;
    int year, month, day;


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        onDateSet = (DatePickerDialog.OnDateSetListener) getTargetFragment();
        if (onDateSet != null){
            return new DatePickerDialog(getContext(), onDateSet, year, month, day);
        }
        else {
            return new DatePickerDialog(getActivity(), (DatePickerDialog.OnDateSetListener) getActivity(), year, month, day);
        }
    }

}
