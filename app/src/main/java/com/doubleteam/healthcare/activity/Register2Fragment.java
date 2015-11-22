package com.doubleteam.healthcare.activity;


import android.content.Intent;
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
public class Register2Fragment extends Fragment {

    SharedPreferences sharedPreferences;
    String athlete;
    String days;
    String doctor;
    String primary;
    String secondary;

    Switch sw_athlete;
    Spinner sp_days;
    EditText et_doctor;
    EditText et_primary;
    EditText et_secondary;


    public Register2Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register2, container, false);

        sw_athlete = (Switch) view.findViewById(R.id.sw_athlete);
        sp_days = (Spinner) view.findViewById(R.id.spinner);
        et_doctor = (EditText) view.findViewById(R.id.et_doctor);
        et_primary = (EditText) view.findViewById(R.id.et_primary);
        et_secondary = (EditText) view.findViewById(R.id.et_secondary);

        sharedPreferences = getActivity().getSharedPreferences("profile", 0);
        athlete = sharedPreferences.getString("?athlete", "false");
        days = sharedPreferences.getString("?days", "0");
        doctor = sharedPreferences.getString("?doctor", "0");
        primary = sharedPreferences.getString("?primary","0");
        secondary = sharedPreferences.getString("?secondary","0");

        Button btn_reg = (Button) view.findViewById(R.id.btn_reg);
        btn_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(sw_athlete.isChecked()){
                    athlete = "true";
                } else {
                    athlete = "false";
                }

                days = sp_days.getSelectedItem().toString();
                doctor = et_doctor.getText().toString();
                primary = et_primary.getText().toString();
                secondary = et_secondary.getText().toString();



                SharedPreferences.Editor spe = sharedPreferences.edit();

                spe.putString("?athlete",athlete);
                spe.putString("?days",days);
                spe.putString("?doctor",doctor);
                spe.putString("?primary",primary);
                spe.putString("?secondary",secondary);

                spe.putBoolean("?first_time", false);

                spe.commit();

                athlete = sharedPreferences.getString("?athlete", "false");
                days = sharedPreferences.getString("?days", "0");
                doctor = sharedPreferences.getString("?doctor", "0");
                primary = sharedPreferences.getString("?primary","0");
                secondary = sharedPreferences.getString("?secondary","0");
                Log.d("TAG__2","-------->"+athlete+days+doctor+primary+secondary);

                Intent intent = new Intent(getActivity().getApplicationContext(), MainActivity.class);
                startActivity(intent);


            }
        });

        return view;
    }


}
