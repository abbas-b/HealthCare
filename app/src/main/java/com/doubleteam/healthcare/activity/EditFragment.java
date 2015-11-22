package com.doubleteam.healthcare.activity;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;

import com.doubleteam.healthcare.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditFragment extends Fragment {

    SharedPreferences sharedPreferences;
    String name;
    String birthday;
    String weight;
    String height;
    String athlete;
    String days;
    String doctor;
    String primary;
    String secondary;

    EditText et_name;
    EditText et_birthday;
    EditText et_weight;
    EditText et_height;
    Switch sw_athlete;
    Spinner sp_days;
    EditText et_doctor;
    EditText et_primary;
    EditText et_secondary;


    public EditFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_edit, container, false);

        et_name = (EditText) view.findViewById(R.id.et_name);
        et_birthday = (EditText) view.findViewById(R.id.et_birthday);
        et_weight = (EditText) view.findViewById(R.id.et_weight);
        et_height = (EditText) view.findViewById(R.id.et_height);
        sw_athlete = (Switch) view.findViewById(R.id.sw_athlete);
        sp_days = (Spinner) view.findViewById(R.id.spinner);
        et_doctor = (EditText) view.findViewById(R.id.et_doctor);
        et_primary = (EditText) view.findViewById(R.id.et_primary);
        et_secondary = (EditText) view.findViewById(R.id.et_secondary);

        sharedPreferences = getActivity().getSharedPreferences("profile", 0);
        name = sharedPreferences.getString("?name", "empty");
        birthday = sharedPreferences.getString("?birthday", "empty");
        weight = sharedPreferences.getString("?weight", "0");
        height = sharedPreferences.getString("?height", "0");
        athlete = sharedPreferences.getString("?athlete", "false");
        days = sharedPreferences.getString("?days", "0");
        doctor = sharedPreferences.getString("?doctor", "0");
        primary = sharedPreferences.getString("?primary","0");
        secondary = sharedPreferences.getString("?secondary","0");

        et_name.setText(name);
        et_birthday.setText(birthday);
        et_weight.setText(weight);
        et_height.setText(height);

        //:TODO not work switch
        if (athlete=="true")
            sw_athlete.toggle();

        //:TODO selection
        sp_days.setSelection(1);

        et_doctor.setText(doctor);
        et_primary.setText(primary);
        et_secondary.setText(secondary);

        Button button = (Button) view.findViewById(R.id.btn_save);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                name = et_name.getText().toString();
                birthday = et_birthday.getText().toString();
                weight = et_weight.getText().toString();
                height = et_height.getText().toString();
                if (sw_athlete.isChecked()){
                    athlete = "true";
                } else {
                    athlete = "false";
                }
                days = sp_days.getSelectedItem().toString();
                Log.d("TAG_ITEM", "-->" + sp_days.getSelectedItemPosition());

                doctor = et_doctor.getText().toString();
                primary = et_primary.getText().toString();
                secondary = et_secondary.getText().toString();

                SharedPreferences.Editor spe = sharedPreferences.edit();

                spe.putString("?name",name);
                spe.putString("?birthday",birthday);
                spe.putString("?weight",weight);
                spe.putString("?height",height);
                spe.putString("?athlete",athlete);
                spe.putString("?days",days);
                spe.putString("?doctor",doctor);
                spe.putString("?primary",primary);
                spe.putString("?secondary",secondary);

                spe.commit();

                ((MainActivity) getActivity()).displayView(0);

            }
        });

        return view;
    }


}
