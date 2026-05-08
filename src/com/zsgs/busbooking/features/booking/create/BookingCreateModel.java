package com.zsgs.busbooking.features.booking.create;

import com.zsgs.busbooking.data.dto.Booking;
import com.zsgs.busbooking.data.dto.Bus;
import com.zsgs.busbooking.data.dto.Payment;
import com.zsgs.busbooking.data.dto.User;
import com.zsgs.busbooking.data.repository.BusBookingDB;
import com.zsgs.busbooking.util.ParseHelper;

import java.util.ArrayList;
import java.util.List;

class BookingCreateModel {

    private final BookingCreateView bookingCreateView;

    BookingCreateModel(BookingCreateView bookingCreateView) {
        this.bookingCreateView = bookingCreateView;
    }

    boolean canBook(User user) {
        return user != null && user.getRole() == User.Role.PASSENGER
                && user.getStatus() == User.UserStatus.ACTIVE;
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

    List<Bus> findBuses(String source, String destination, Long travelDate) {
        List<Bus> matches = BusBookingDB.getInstance().searchBuses(source, destination);
        List<Bus> result = new ArrayList<>();
        for (Bus bus : matches) {
            if (computeAvailableForDate(bus, travelDate) > 0) {
                result.add(bus);
            }
        }
        return result;
    }

    String validateSeat(Bus bus, Long travelDate, Integer seatNumber) {
        if (seatNumber == null) return "Enter a valid seat number";
        if (bus == null || bus.getTotalSeats() == null) return "Invalid bus selection";
        if (seatNumber < 1 || seatNumber > bus.getTotalSeats()) {
            return "Seat must be between 1 and " + bus.getTotalSeats();
        }
        if (BusBookingDB.getInstance().isSeatTaken(bus.getId(), travelDate, seatNumber)) {
            return "That seat is already taken. Please choose another.";
        }
        return null;
    }

    Payment.PaymentMethod parsePaymentMethod(String input) {
        if (input == null) return null;
        String c = input.trim();
        if (c.equals("1") || c.equalsIgnoreCase("UPI")) return Payment.PaymentMethod.UPI;
        if (c.equals("2") || c.equalsIgnoreCase("Card")) return Payment.PaymentMethod.CARD;
        return null;
    }

    void createBooking(User user, Bus bus, Long travelDate, Integer seatNumber,
                       Payment.PaymentMethod method) {
        if (bus == null || user == null) {
            bookingCreateView.onBookingFailed("Invalid booking input.");
            return;
        }

        // Re-check seat to avoid race-style double booking
        if (BusBookingDB.getInstance().isSeatTaken(bus.getId(), travelDate, seatNumber)) {
            bookingCreateView.onBookingFailed("Seat was just taken. Please try a different seat.");
            return;
        }

        Booking booking = new Booking();
        booking.setUserId(user.getId());
        booking.setBusId(bus.getId());
        booking.setTravelDate(travelDate);
        booking.setSeatNumber(seatNumber);
        booking.setFare(bus.getFare());
        booking.setBookingStatus(Booking.BookingStatus.PENDING);

        Booking savedBooking = BusBookingDB.getInstance().addBooking(booking);
        if (savedBooking == null) {
            bookingCreateView.onBookingFailed("Could not create booking. Please try again.");
            return;
        }

        Payment payment = new Payment();
        payment.setBookingId(savedBooking.getId());
        payment.setAmount(bus.getFare());
        payment.setPaymentMethod(method);
        payment.setPaymentStatus(Payment.PaymentStatus.COMPLETED);
        payment.setPaidAt(System.currentTimeMillis());
        Payment savedPayment = BusBookingDB.getInstance().addPayment(payment);

        if (savedPayment == null) {
            bookingCreateView.onBookingFailed("Booking created but payment failed. Please contact support.");
            return;
        }

        savedBooking.setPaymentId(savedPayment.getId());
        savedBooking.setBookingStatus(Booking.BookingStatus.CONFIRMED);
        BusBookingDB.getInstance().updateBooking(savedBooking);

        // Decrement available seats on bus
        if (bus.getAvailableSeats() != null && bus.getAvailableSeats() > 0) {
            bus.setAvailableSeats(bus.getAvailableSeats() - 1);
            BusBookingDB.getInstance().updateBus(bus);
        }

        bookingCreateView.onBookingCreated(savedBooking, savedPayment);
    }

    private int computeAvailableForDate(Bus bus, Long travelDate) {
        if (bus == null || bus.getTotalSeats() == null) return 0;
        int booked = 0;
        for (Booking b : BusBookingDB.getInstance().getBookingsByBus(bus.getId())) {
            if (b.getBookingStatus() == Booking.BookingStatus.CANCELLED) continue;
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
