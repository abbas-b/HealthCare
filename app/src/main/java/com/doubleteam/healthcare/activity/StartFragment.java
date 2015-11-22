package com.doubleteam.healthcare.activity;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.doubleteam.healthcare.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class StartFragment extends Fragment {


    public StartFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_start, container, false);

        ImageButton imageButton = (ImageButton) view.findViewById(R.id.btn_start);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "we start collecting", Toast.LENGTH_LONG).show();
                
            }
        });

        return view;
    }


}
