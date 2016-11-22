package com.lhd.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ken.hauiclass.R;
import com.lhd.activity.MainActivity;


/**
 * Created by Faker on 8/17/2016.
 */

public class ListStudentFragment extends Fragment {
    private RecyclerView recyclerView;
    private TextView textView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.list_layout,container,false);
        recyclerView= (RecyclerView) view.findViewById(R.id.recle_view);
        textView= (TextView) view.findViewById(R.id.text_null);
            recyclerView.setVisibility(View.GONE);
            textView.setVisibility(View.VISIBLE);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MainActivity mainActivityActivity = (MainActivity) getActivity();
                    mainActivityActivity.startLogin();
                }
            });
        return view;
    }
}
