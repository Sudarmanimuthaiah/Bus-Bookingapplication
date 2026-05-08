package com.zsgs.busbooking.features.booking.all;

import com.zsgs.busbooking.data.dto.Booking;
import com.zsgs.busbooking.data.dto.Bus;
import com.zsgs.busbooking.data.dto.User;
import com.zsgs.busbooking.util.ConsoleInput;
import com.zsgs.busbooking.util.ParseHelper;

import java.util.List;
import java.util.Scanner;

public class BookingAllView {

    private final BookingAllModel bookingAllModel;
    private final Scanner scanner;

    public BookingAllView() {
        this.bookingAllModel = new BookingAllModel(this);
        this.scanner = ConsoleInput.getScanner();
    }
    public void init() {
        System.out.println();
        System.out.println("All Bookings");
        List<Booking> bookings = bookingAllModel.getAllBookings();
        if (bookings.isEmpty()) {
            System.out.println("No bookings yet.");
        } else {
            System.out.println("#   BookingId Passenger            Bus            Date         Seat Status");
            for (int i = 0; i < bookings.size(); i++) {
                Booking b = bookings.get(i);
                User user = bookingAllModel.getUser(b.getUserId());
                Bus bus = bookingAllModel.getBus(b.getBusId());
                String row = String.format(
                        "%-3d %-9s %-20s %-14s %-12s %-4s %s",
                        (i + 1),
                        safe(b.getBookingId()),
                        truncate(user == null ? "-" : safe(user.getName()), 20),
                        truncate(bus == null ? "-" : safe(bus.getBusName()), 14),
                        ParseHelper.formatDate(b.getTravelDate()),
                        b.getSeatNumber() == null ? "-" : String.valueOf(b.getSeatNumber()),
                        b.getBookingStatus() == null ? "-" : b.getBookingStatus().name());
                System.out.println(row);
            }
        }
        System.out.print("Press Enter to return: ");
        scanner.nextLine();
    }

    private static String safe(String value) {
        return value == null ? "-" : value;
    }

    private static String truncate(String value, int max) {
        if (value.length() <= max) return value;
        return value.substring(0, max - 1) + "~";
    }
}
