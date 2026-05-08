package com.zsgs.busbooking.features.bus.search;

import com.zsgs.busbooking.data.dto.Bus;
import com.zsgs.busbooking.data.repository.BusBookingDB;
import com.zsgs.busbooking.util.ParseHelper;

import java.util.ArrayList;
import java.util.List;

class BusSearchModel {

    private final BusSearchView busSearchView;

    BusSearchModel(BusSearchView busSearchView) {
        this.busSearchView = busSearchView;
    }

    String validateCity(String city, String label) {
        if (city == null || city.trim().isEmpty()) {
            return capitalize(label) + " cannot be empty";
        }
        return null;
    }

    Long parseTravelDate(String text) {
        Long date = ParseHelper.parseDate(text);
        if (date == null) return null;
        if (!ParseHelper.isTodayOrFuture(date)) return null;
        return date;
    }

    List<Bus> search(String source, String destination, Long travelDate) {
        List<Bus> matches = BusBookingDB.getInstance().searchBuses(source, destination);
        // Filter by available seats considering bookings on travelDate
        List<Bus> result = new ArrayList<>();
        for (Bus bus : matches) {
            if (computeAvailableForDate(bus, travelDate) > 0) {
                result.add(bus);
            }
        }
        return result;
    }

    private int computeAvailableForDate(Bus bus, Long travelDate) {
        if (bus == null || bus.getTotalSeats() == null) return 0;
        int booked = 0;
        for (com.zsgs.busbooking.data.dto.Booking b
                : BusBookingDB.getInstance().getBookingsByBus(bus.getId())) {
            if (b.getBookingStatus() == com.zsgs.busbooking.data.dto.Booking.BookingStatus.CANCELLED) continue;
            if (travelDate != null && !travelDate.equals(b.getTravelDate())) continue;
            booked++;
        }
        return Math.max(0, bus.getTotalSeats() - booked);
    }

    private String capitalize(String text) {
        if (text == null || text.isEmpty()) return text;
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }
}
