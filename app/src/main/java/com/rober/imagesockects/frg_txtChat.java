package com.rober.imagesockects;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import android.app.Fragment;

public class frg_txtChat extends Fragment {
    private ImageView imagen;

    public frg_txtChat() {

    }

    // TODO: Rename and change types and number of parameters
    public static frg_txtChat newInstance(ImageView image) {
        frg_txtChat fragment = new frg_txtChat();
        Bundle args = new Bundle();
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //crear el fragment
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // crea el diseño de este fragment
        View view =  inflater.inflate(R.layout.frg_txtchat, container, false);
        return view;
    }

}
