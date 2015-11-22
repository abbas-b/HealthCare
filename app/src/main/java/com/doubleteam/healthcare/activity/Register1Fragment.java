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
import android.widget.RadioButton;

import com.doubleteam.healthcare.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class Register1Fragment extends Fragment {

    SharedPreferences sharedPreferences;
    String name;
    String mail;
    String birthday;
    String weight;
    String height;
    String gender;

    EditText et_name;
    EditText et_mail;
    EditText et_birthday;
    EditText et_weight;
    EditText et_height;
    RadioButton rdo_f;
    RadioButton rdo_m;




    public Register1Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register1, container, false);

        et_name = (EditText) view.findViewById(R.id.et_name);
        et_mail = (EditText) view.findViewById(R.id.et_mail);
        et_birthday = (EditText) view.findViewById(R.id.et_birthday);
        et_weight = (EditText) view.findViewById(R.id.et_weight);
        et_height = (EditText) view.findViewById(R.id.et_height);
        rdo_f = (RadioButton) view.findViewById(R.id.rdo_f);
        rdo_m = (RadioButton) view.findViewById(R.id.rdo_m);

        sharedPreferences = getActivity().getSharedPreferences("profile", 0);
        name = sharedPreferences.getString("?name", "empty");
        mail = sharedPreferences.getString("?mail", "empty");
        birthday = sharedPreferences.getString("?birthday", "empty");
        gender = sharedPreferences.getString("?gender", "male");
        weight = sharedPreferences.getString("?weight", "0");
        height = sharedPreferences.getString("?height", "0");

        Button button = (Button) view.findViewById(R.id.btn_next);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                name = et_name.getText().toString();
                mail = et_mail.getText().toString();
                birthday = et_birthday.getText().toString();
                weight = et_weight.getText().toString();
                height = et_height.getText().toString();

                if (rdo_f.isChecked()){
                    gender = "female";
                } else {
                    gender = "male";
                }

                SharedPreferences.Editor spe = sharedPreferences.edit();

                spe.putString("?name",name);
                spe.putString("?mail",mail);
                spe.putString("?birthday",birthday);
                spe.putString("?weight",weight);
                spe.putString("?height",height);
                spe.putString("?gender",gender);
                spe.commit();

                ((Register) getActivity()).displayView(1);

                name = sharedPreferences.getString("?name", "empty");
                mail = sharedPreferences.getString("?mail", "empty");
                birthday = sharedPreferences.getString("?birthday", "empty");
                gender = sharedPreferences.getString("?gender", "male");
                weight = sharedPreferences.getString("?weight", "0");
                height = sharedPreferences.getString("?height", "0");
                Log.d("TAG__1","------------>"+name+mail+birthday+gender+weight+height);
            }
        });
        return view;
    }


}
