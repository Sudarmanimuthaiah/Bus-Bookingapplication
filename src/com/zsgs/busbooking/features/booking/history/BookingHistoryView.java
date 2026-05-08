package com.zsgs.busbooking.features.booking.history;

import com.zsgs.busbooking.data.dto.Booking;
import com.zsgs.busbooking.data.dto.Bus;
import com.zsgs.busbooking.data.dto.User;
import com.zsgs.busbooking.data.repository.BusBookingDB;
import com.zsgs.busbooking.util.ConsoleInput;
import com.zsgs.busbooking.util.ParseHelper;

import java.util.List;
import java.util.Scanner;

public class BookingHistoryView {

    private final BookingHistoryModel bookingHistoryModel;
    private final Scanner scanner;
    private final User currentUser;

    public BookingHistoryView(User currentUser) {
        this.bookingHistoryModel = new BookingHistoryModel(this);
        this.scanner = ConsoleInput.getScanner();
        this.currentUser = currentUser;
    }

    public void init() {
        System.out.println();
        System.out.println("My Bookings");
        Long userId = currentUser == null ? null : currentUser.getId();
        List<Booking> bookings = bookingHistoryModel.getMyBookings(userId);
        if (bookings.isEmpty()) {
            System.out.println("You have no bookings yet.");
        } else {
            renderBookings(bookings);
        }
        System.out.print("Press Enter to return: ");
        scanner.nextLine();
    }

    public static void renderBookings(List<Booking> bookings) {
        if (bookings == null || bookings.isEmpty()) return;
        System.out.println("#   BookingId Bus            Source          Destination     Date         Seat Status");
        for (int i = 0; i < bookings.size(); i++) {
            Booking b = bookings.get(i);
            Bus bus = BusBookingDB.getInstance().getBusById(b.getBusId());
            String row = String.format(
                    "%-3d %-9s %-14s %-15s %-15s %-12s %-4s %s",
                    (i + 1),
                    safe(b.getBookingId()),
                    truncate(busName(bus), 14),
                    truncate(busSource(bus), 15),
                    truncate(busDestination(bus), 15),
                    ParseHelper.formatDate(b.getTravelDate()),
                    b.getSeatNumber() == null ? "-" : String.valueOf(b.getSeatNumber()),
                    b.getBookingStatus() == null ? "-" : b.getBookingStatus().name());
            System.out.println(row);
        }
    }

    static String busName(Bus bus) {
        return bus == null || bus.getBusName() == null ? "-" : bus.getBusName();
    }

    static String busSource(Bus bus) {
        return bus == null || bus.getSource() == null ? "-" : bus.getSource();
    }

    static String busDestination(Bus bus) {
        return bus == null || bus.getDestination() == null ? "-" : bus.getDestination();
    }

    private static String safe(String value) {
        return value == null ? "-" : value;
    }

    private static String truncate(String value, int max) {
        if (value.length() <= max) return value;
        return value.substring(0, max - 1) + "~";
    }
}
