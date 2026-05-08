package com.zsgs.busbooking.features.booking.create;

import com.zsgs.busbooking.data.dto.Booking;
import com.zsgs.busbooking.data.dto.Bus;
import com.zsgs.busbooking.data.dto.Payment;
import com.zsgs.busbooking.data.dto.User;
import com.zsgs.busbooking.util.ConsoleInput;
import com.zsgs.busbooking.util.ParseHelper;

import java.util.List;
import java.util.Scanner;

public class BookingCreateView {

    private final BookingCreateModel bookingCreateModel;
    private final Scanner scanner;
    private final User currentUser;

    public BookingCreateView(User currentUser) {
        this.bookingCreateModel = new BookingCreateModel(this);
        this.scanner = ConsoleInput.getScanner();
        this.currentUser = currentUser;
    }

    public void init() {
        System.out.println();
        System.out.println("Book a seat");
        if (!bookingCreateModel.canBook(currentUser)) {
            System.out.println("Only passengers can book seats.");
            return;
        }

        String source = promptCity("source");
        String destination = promptCity("destination");
        Long travelDate = promptTravelDate();

        List<Bus> buses = bookingCreateModel.findBuses(source, destination, travelDate);
        if (buses.isEmpty()) {
            System.out.println("No buses found for the given route on " + ParseHelper.formatDate(travelDate) + ".");
            return;
        }
        com.zsgs.busbooking.features.bus.list.BusListView.renderBuses(buses);

        Bus bus = chooseBus(buses);
        if (bus == null) return;

        Integer seatNumber = promptSeatNumber(bus, travelDate);
        if (seatNumber == null) return;

        Payment.PaymentMethod method = promptPaymentMethod();

        bookingCreateModel.createBooking(currentUser, bus, travelDate, seatNumber, method);
    }

    private String promptCity(String label) {
        while (true) {
            System.out.print("Enter " + label + ": ");
            String input = scanner.nextLine();
            String error = bookingCreateModel.validateCity(input, label);
            if (error == null) return input.trim();
            System.out.println(error);
        }
    }

    private Long promptTravelDate() {
        while (true) {
            System.out.print("Enter travel date (dd-MM-yyyy): ");
            Long date = bookingCreateModel.parseTravelDate(scanner.nextLine());
            if (date != null) return date;
            System.out.println("Enter a valid date. Travel date must be today or later.");
        }
    }

    private Bus chooseBus(List<Bus> buses) {
        while (true) {
            System.out.print("Enter the # of the bus to book (or 0 to cancel): ");
            Integer index = ParseHelper.parseNonNegativeInt(scanner.nextLine());
            if (index != null && index == 0) return null;
            if (index != null && index >= 1 && index <= buses.size()) {
                return buses.get(index - 1);
            }
            System.out.println("Select a valid option.");
        }
    }

    private Integer promptSeatNumber(Bus bus, Long travelDate) {
        while (true) {
            System.out.print("Enter seat number (1-" + bus.getTotalSeats() + ", or 0 to cancel): ");
            Integer seat = ParseHelper.parseNonNegativeInt(scanner.nextLine());
            if (seat != null && seat == 0) return null;
            String error = bookingCreateModel.validateSeat(bus, travelDate, seat);
            if (error == null) return seat;
            System.out.println(error);
        }
    }

    private Payment.PaymentMethod promptPaymentMethod() {
        while (true) {
            System.out.println("Select payment method:");
            System.out.println("1. UPI");
            System.out.println("2. Card");
            System.out.print("Choose an option: ");
            Payment.PaymentMethod method = bookingCreateModel.parsePaymentMethod(scanner.nextLine());
            if (method != null) return method;
            System.out.println("Select a valid option.");
        }
    }

    void onBookingCreated(Booking booking, Payment payment) {
        System.out.println();
        System.out.println("Booking confirmed.");
        System.out.println("Booking id: " + booking.getBookingId());
        System.out.println("Seat: " + booking.getSeatNumber());
        System.out.println("Fare paid: " + payment.getAmount() + " via " + payment.getPaymentMethod().name());
        System.out.println("Payment id: " + payment.getPaymentId() + " (" + payment.getPaymentStatus().name() + ")");
    }

    void onBookingFailed(String message) {
        System.out.println(message);
    }
}
