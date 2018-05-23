package com.nsa.teamtwo.welshpharmacy.data.map;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;
import com.nsa.teamtwo.welshpharmacy.data.pharmacy.Pharmacy;

//Adapted from https://developers.google.com/maps/documentation/android-api/utility/marker-clustering
//Accessed: 16/04/2018
public class PharmacyMarker implements ClusterItem {
    private LatLng position;
    private String title;
    private BitmapDescriptor icon;
    private Pharmacy pharmacy;

    public PharmacyMarker(LatLng position, String title, BitmapDescriptor icon, Pharmacy pharmacy) {
        this.position = position;
        this.title = title;
        this.icon = icon;
        this.pharmacy = pharmacy;
    }

    @Override
    public LatLng getPosition() {
        return position;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getSnippet() {
        return null;
    }

    public BitmapDescriptor getIcon() {
        return icon;
    }

    public Pharmacy getPharmacy() {
        return pharmacy;
    }
}
