package com.zsgs.busbooking.features.booking.cancel;

import com.zsgs.busbooking.data.dto.Booking;
import com.zsgs.busbooking.data.dto.Bus;
import com.zsgs.busbooking.data.dto.Payment;
import com.zsgs.busbooking.data.repository.BusBookingDB;

import java.util.ArrayList;
import java.util.List;

class BookingCancelModel {

    private final BookingCancelView bookingCancelView;

    BookingCancelModel(BookingCancelView bookingCancelView) {
        this.bookingCancelView = bookingCancelView;
    }

    List<Booking> getActiveBookings(Long userId) {
        List<Booking> result = new ArrayList<>();
        for (Booking b : BusBookingDB.getInstance().getBookingsByUser(userId)) {
            if (b.getBookingStatus() != Booking.BookingStatus.CANCELLED) {
                result.add(b);
            }
        }
        return result;
    }

    void cancelBooking(Booking booking) {
        if (booking == null || booking.getId() == null) {
            bookingCancelView.onBookingCancelFailed("Invalid booking selected.");
            return;
        }
        if (booking.getBookingStatus() == Booking.BookingStatus.CANCELLED) {
            bookingCancelView.onBookingCancelFailed("This booking is already cancelled.");
            return;
        }

        booking.setBookingStatus(Booking.BookingStatus.CANCELLED);
        Booking saved = BusBookingDB.getInstance().updateBooking(booking);
        if (saved == null) {
            bookingCancelView.onBookingCancelFailed("Could not cancel booking. Please try again.");
            return;
        }

        // Refund payment if any
        if (booking.getPaymentId() != null) {
            Payment payment = BusBookingDB.getInstance().getPaymentById(booking.getPaymentId());
            if (payment != null && payment.getPaymentStatus() == Payment.PaymentStatus.COMPLETED) {
                payment.setPaymentStatus(Payment.PaymentStatus.REFUNDED);
                BusBookingDB.getInstance().updatePayment(payment);
            }
        }

        // Restore seat availability
        Bus bus = BusBookingDB.getInstance().getBusById(booking.getBusId());
        if (bus != null && bus.getAvailableSeats() != null && bus.getTotalSeats() != null
                && bus.getAvailableSeats() < bus.getTotalSeats()) {
            bus.setAvailableSeats(bus.getAvailableSeats() + 1);
            BusBookingDB.getInstance().updateBus(bus);
        }

        bookingCancelView.onBookingCancelled(saved);
    }
}
