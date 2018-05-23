package com.nsa.teamtwo.welshpharmacy.data;

import com.nsa.teamtwo.welshpharmacy.data.pharmacy.Service;

import java.util.List;

public interface FilterCallback {
    void onFilterSelected(boolean welshAvailableOnly, boolean openOnly, List<Service> selectedServices);
}