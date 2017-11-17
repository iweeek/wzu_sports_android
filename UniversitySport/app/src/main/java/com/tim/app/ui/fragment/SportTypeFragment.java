package com.tim.app.ui.fragment;

import com.tim.app.ui.activity.MainActivity;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.application.library.base.BaseFragment;
import com.tim.app.R;
import com.tim.app.server.entry.SportEntry;
import com.tim.app.ui.activity.MainActivity;

public class SportTypeFragment extends BaseFragment {
    MainActivity mainActivity;

    SportEntry sportEntry;

    String SportName;

    //    public Handler mHandler = new Handler() {
    //        public void handleMessage(android.os.Message msg) {
    //            switch (msg.what) {
    //                case 1:
    //
    //                    break;
    //            }
    //        }
    //    };
    //
    //    @Override
    //    public void onAttach(Context context) {
    //        super.onAttach(context);
    //        mainActivity = (MainActivity) context;
    //        mainActivity.setHandler(mHandler);
    //    }


    public void setSportEntry(SportEntry sportEntry) {
        this.sportEntry = sportEntry;
    }

    public static SportTypeFragment newInstance(SportEntry sportEntry) {
        SportTypeFragment fragment = new SportTypeFragment();
        fragment.setSportEntry(sportEntry);
        //      Bundle args = new Bundle();
        //      args.putString("SportName", SportName);
        //      fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sport_type, container, false);
        TextView tv = (TextView) rootView.findViewById(R.id.SportTest);

        tv.setText(sportEntry.getName());

        return rootView;


    }
}