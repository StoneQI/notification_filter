package com.stone.notificationfilter.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.stone.notificationfilter.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FloatDanMuSettingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FloatDanMuSettingFragment extends PreferenceFragmentCompat {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FloatDanMuSettingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FloatDanMuSettingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FloatDanMuSettingFragment newInstance(String param1, String param2) {
        FloatDanMuSettingFragment fragment = new FloatDanMuSettingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        getPreferenceManager().setSharedPreferencesName("appSettings");
        addPreferencesFromResource(R.xml.fragment_danmu_setting);

        findPreference("danmu_custom_view").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
//                MainActivity mainActivity = (MainActivity)getActivity();
//                mainActivity.replaceFragment(new FiliterActivity());
//                Navigation.findNavController(getView()).navigate(R.id.action_floatTilesSettingFragment_to_floatTileCustomViewFragment);
                Intent intent = new Intent(getActivity(), DanmuTileCustomViewAictivy.class);
                startActivity(intent);//
                return true;
            }
        });
    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
////        return inflater.inflate(R.layout.fragment_float_dan_mu_custom_view, container, false);
//    }
}