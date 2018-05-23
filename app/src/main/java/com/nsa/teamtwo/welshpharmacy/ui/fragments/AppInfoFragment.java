package com.nsa.teamtwo.welshpharmacy.ui.fragments;

import android.app.Fragment;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nsa.teamtwo.welshpharmacy.R;

/**
 * Created by c1716791 on 25/04/2018.
 */

public class AppInfoFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View loadView = inflater.inflate(R.layout.fragment_app_info, container, false);
        TextView version = loadView.findViewById(R.id.text_version);
        try {
            PackageInfo info = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            String versionName = info.versionName;
            int versionNumber = info.versionCode;
            version.setText(String.format("%s: %s", getString(R.string.app_version), versionName));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return loadView;
    }
}
