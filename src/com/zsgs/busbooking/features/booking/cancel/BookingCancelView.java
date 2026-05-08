package com.zsgs.busbooking.features.booking.cancel;

import com.zsgs.busbooking.data.dto.Booking;
import com.zsgs.busbooking.data.dto.User;
import com.zsgs.busbooking.features.booking.history.BookingHistoryView;
import com.zsgs.busbooking.util.ConsoleInput;
import com.zsgs.busbooking.util.ParseHelper;

import java.util.List;
import java.util.Scanner;

public class BookingCancelView {

    private final BookingCancelModel bookingCancelModel;
    private final Scanner scanner;
    private final User currentUser;

    public BookingCancelView(User currentUser) {
        this.bookingCancelModel = new BookingCancelModel(this);
        this.scanner = ConsoleInput.getScanner();
        this.currentUser = currentUser;
    }

    public void init() {
        System.out.println();
        System.out.println("Cancel a booking");
        if (currentUser == null) {
            System.out.println("Sign in to cancel a booking.");
            return;
        }
        List<Booking> bookings = bookingCancelModel.getActiveBookings(currentUser.getId());
        if (bookings.isEmpty()) {
            System.out.println("You have no active bookings to cancel.");
            return;
        }
        BookingHistoryView.renderBookings(bookings);

        Booking selected;
        while (true) {
            System.out.print("Enter the # of the booking to cancel (or 0 to cancel): ");
            Integer index = ParseHelper.parseNonNegativeInt(scanner.nextLine());
            if (index != null && index == 0) return;
            if (index != null && index >= 1 && index <= bookings.size()) {
                selected = bookings.get(index - 1);
                break;
            }
            System.out.println("Select a valid option.");
        }

        System.out.print("Confirm cancellation of booking " + selected.getBookingId() + "? (Y/N): ");
        if (!ParseHelper.isYes(scanner.nextLine())) {
            System.out.println("Cancellation aborted.");
            return;
        }

        bookingCancelModel.cancelBooking(selected);
    }

    void onBookingCancelled(Booking booking) {
        System.out.println("Booking " + booking.getBookingId() + " cancelled.");
        if (booking.getPaymentId() != null) {
            System.out.println("Refund initiated for payment id (Payment status: REFUNDED).");
        }
    }

    void onBookingCancelFailed(String message) {
        System.out.println(message);
    }
}
