package com.zsgs.busbooking.features.bus.delete;

import com.zsgs.busbooking.data.dto.Booking;
import com.zsgs.busbooking.data.dto.Bus;
import com.zsgs.busbooking.data.dto.User;
import com.zsgs.busbooking.data.repository.BusBookingDB;

import java.util.List;

class BusDeleteModel {

    private final BusDeleteView busDeleteView;

    BusDeleteModel(BusDeleteView busDeleteView) {
        this.busDeleteView = busDeleteView;
    }

    boolean canManageBuses(User user) {
        return user != null && user.getRole() == User.Role.ADMIN;
    }

    List<Bus> getAllBuses() {
        return BusBookingDB.getInstance().getBuses();
    }

    void deleteBus(Bus bus) {
        if (bus == null || bus.getId() == null) {
            busDeleteView.onBusDeleteFailed("Invalid bus selected.");
            return;
        }
        if (hasActiveBookings(bus.getId())) {
            busDeleteView.onBusDeleteFailed("Cannot delete: this bus has active bookings. Mark it Inactive instead.");
            return;
        }
        boolean removed = BusBookingDB.getInstance().deleteBus(bus.getId());
        if (!removed) {
            busDeleteView.onBusDeleteFailed("Could not delete bus. Please try again.");
            return;
        }
        busDeleteView.onBusDeleted(bus);
    }

    private boolean hasActiveBookings(Long busId) {
        List<Booking> bookings = BusBookingDB.getInstance().getBookingsByBus(busId);
        for (Booking b : bookings) {
            if (b.getBookingStatus() != Booking.BookingStatus.CANCELLED) return true;
        }
        return false;
    }
}
