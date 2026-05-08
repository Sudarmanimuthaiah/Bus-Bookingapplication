package com.zsgs.busbooking.features.bus.list;

import com.zsgs.busbooking.data.dto.Bus;
import com.zsgs.busbooking.data.repository.BusBookingDB;

import java.util.List;

class BusListModel {

    private final BusListView busListView;

    BusListModel(BusListView busListView) {
        this.busListView = busListView;
    }

    List<Bus> getAllBuses() {
        return BusBookingDB.getInstance().getBuses();
    }
}
