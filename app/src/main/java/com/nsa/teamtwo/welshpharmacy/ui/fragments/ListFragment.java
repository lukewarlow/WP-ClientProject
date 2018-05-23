package com.nsa.teamtwo.welshpharmacy.ui.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;
import com.nsa.teamtwo.welshpharmacy.R;
import com.nsa.teamtwo.welshpharmacy.data.FilterCallback;
import com.nsa.teamtwo.welshpharmacy.data.pharmacy.Pharmacy;
import com.nsa.teamtwo.welshpharmacy.data.pharmacy.PharmacyAdapter;
import com.nsa.teamtwo.welshpharmacy.data.pharmacy.Service;
import com.nsa.teamtwo.welshpharmacy.handlers.LocationHandler;
import com.nsa.teamtwo.welshpharmacy.handlers.PermissionHandler;
import com.nsa.teamtwo.welshpharmacy.ui.activities.DisplayPharmacyActivity;
import com.nsa.teamtwo.welshpharmacy.ui.activities.NavigationActivity;
import com.nsa.teamtwo.welshpharmacy.util.KeyValueHelper;
import com.nsa.teamtwo.welshpharmacy.util.Util;
import com.nsa.teamtwo.welshpharmacy.util.UtilCallback;

import java.util.ArrayList;
import java.util.List;


public class ListFragment extends Fragment {

    private static final String TAG = "List Fragment";

    private ListView listView;
    private FilterFragment selectorFragment;
    private LatLng location;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View loadView = inflater.inflate(R.layout.fragment_list, container, false);
        listView = loadView.findViewById(R.id.list_pharmacies);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Pharmacy pharmacy = (Pharmacy) listView.getItemAtPosition(position);
                Intent intent = new Intent(getActivity(), DisplayPharmacyActivity.class);
                intent.putExtra("Pharmacy", pharmacy);
                getActivity().startActivity(intent);
            }
        });
        enableMyLocation();
        setHasOptionsMenu(true);
        return loadView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_filter, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.filter) {
            selectService(getView());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void enableMyLocation() {
        Log.d(TAG, "Method: enableMyLocation");
        LocationHandler locationHandler = new LocationHandler(getActivity());

        locationHandler.getDeviceLocation(getActivity());

        locationHandler.setCallback(new UtilCallback() {
            @Override
            public void onSuccess(Object... obj) {
                Location location = (Location) obj[0];
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                initiliseListView(latLng);
            }

            @Override
            public void onFail(Object... obj) {
                LatLng latLng = new LatLng(52.374712, -3.727406);
                initiliseListView(latLng);
            }
        });


        if (!PermissionHandler.isLocationAccessGranted(getActivity())) {
            LatLng location = ((NavigationActivity) getActivity()).getLocation();
            if (location != null) {
                initiliseListView(location);
            } else {
                location = new LatLng(52.374712, -3.727406);
                initiliseListView(location);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void selectService(View v) {
        if (selectorFragment == null) {
            selectorFragment = new FilterFragment();
            selectorFragment.setCallback(new FilterCallback() {
                @Override
                public void onFilterSelected(boolean welshAvailableOnly, boolean openOnly, List<Service> selectedServices) {
                    if (selectedServices == null) {
                        Util.shortToast(getActivity(), R.string.filters_cleared);
                    } else {
                        Util.shortToast(getActivity(), R.string.filter_applied);
                    }

                    if (selectedServices != null) {
                        List<Pharmacy> pharmacies = new ArrayList<>();
                        for (Pharmacy pharmacy : Util.pharmacies) {
                            if (welshAvailableOnly) {
                                if (!pharmacy.isWelshAvailable()) continue;
                            }
                            if (openOnly) {
                                if (!pharmacy.isOpenNow()) continue;
                            }
                            if (selectedServices.size() > 0) {
                                if (Util.isWithinRadius(getActivity(), pharmacy.getLocation(), location)) {
                                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences(KeyValueHelper.KEY_PREFERENCES, Context.MODE_PRIVATE);
                                    String filterLogic = sharedPreferences.getString(KeyValueHelper.KEY_FILTER_LOGIC, KeyValueHelper.DEFAULT_FILTER_LOGIC);
                                    if (filterLogic.equals("OR")) {
                                        for (Service service : selectedServices) {
                                            if (pharmacy.doesService(service)) {
                                                pharmacies.add(pharmacy);
                                                break;
                                            }
                                        }
                                    } else {
                                        boolean needsRemoving = false;
                                        for (Service service : selectedServices) {
                                            if (!pharmacy.doesService(service)) {
                                                needsRemoving = true;
                                                break;
                                            }
                                        }
                                        if (!needsRemoving) pharmacies.add(pharmacy);;
                                    }
                                }

                            } else if (Util.isWithinRadius(getActivity(), pharmacy.getLocation(), location)) {
                                pharmacies.add(pharmacy);
                            }
                        }
                        updateListView(pharmacies);
                    } else {
                        initiliseListView(location);
                    }
                }
            });
        }
        selectorFragment.show(getActivity().getFragmentManager(), "Select Service");
    }

    private void initiliseListView(LatLng latLng) {
        location = latLng;
        List<Pharmacy> listData = new ArrayList<>();
        for (int i = 0; i < Util.pharmacies.size(); i++) {
            Pharmacy pharmacy = Util.pharmacies.get(i);
            if (Util.isWithinRadius(getActivity(), pharmacy.getLocation(), latLng)) {
                listData.add(Util.pharmacies.get(i));
            }
        }
        updateListView(listData);
    }

    private void updateListView(List<Pharmacy> pharmacies) {
        listView.setAdapter(new PharmacyAdapter(getActivity(), pharmacies));
    }
}
