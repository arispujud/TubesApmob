package com.example.apkit.tugasbesar;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class HomeFragment extends Fragment {
    public String nama;
    TextView txtNama;
    SessionManager sessionManager = new SessionManager();
    public HomeFragment() {
        // Required empty public constructor
    }
    public HomeFragment newInstance(String str){
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString("NAMA", str);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        txtNama =  (TextView) view.findViewById(R.id.txt_home_name);
        nama = sessionManager.getPreferences(getContext(),"NAMA");
        txtNama.setText(nama);
        return view;
    }
}
