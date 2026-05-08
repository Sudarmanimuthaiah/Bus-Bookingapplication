package com.zsgs.busbooking.features.booking.all;

import com.zsgs.busbooking.data.dto.Booking;
import com.zsgs.busbooking.data.dto.Bus;
import com.zsgs.busbooking.data.dto.User;
import com.zsgs.busbooking.data.repository.BusBookingDB;

import java.util.List;

class BookingAllModel {

    private final BookingAllView bookingAllView;

    BookingAllModel(BookingAllView bookingAllView) {
        this.bookingAllView = bookingAllView;
    }

    List<Booking> getAllBookings() {
        return BusBookingDB.getInstance().getAllBookings();
    }

    User getUser(Long id) {
        return BusBookingDB.getInstance().getUserById(id);
    }

    Bus getBus(Long id) {
        return BusBookingDB.getInstance().getBusById(id);
    }
}
