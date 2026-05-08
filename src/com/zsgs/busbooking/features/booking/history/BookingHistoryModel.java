package com.zsgs.busbooking.features.booking.history;

import com.zsgs.busbooking.data.dto.Booking;
import com.zsgs.busbooking.data.dto.Bus;
import com.zsgs.busbooking.data.repository.BusBookingDB;

import java.util.List;

class BookingHistoryModel {

    private final BookingHistoryView bookingHistoryView;

    BookingHistoryModel(BookingHistoryView bookingHistoryView) {
        this.bookingHistoryView = bookingHistoryView;
    }

    List<Booking> getMyBookings(Long userId) {
        return BusBookingDB.getInstance().getBookingsByUser(userId);
    }

    Bus getBus(Long busId) {
        return BusBookingDB.getInstance().getBusById(busId);
    }
}
