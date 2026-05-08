package com.zsgs.busbooking.features.bus.add;

import com.zsgs.busbooking.data.dto.Bus;
import com.zsgs.busbooking.data.dto.User;
import com.zsgs.busbooking.util.ConsoleInput;
import com.zsgs.busbooking.util.ParseHelper;

import java.util.Scanner;

public class BusAddView {

    private final BusAddModel busAddModel;
    private final Scanner scanner;
    private final User currentUser;

    public BusAddView(User currentUser) {
        this.busAddModel = new BusAddModel(this);
        this.scanner = ConsoleInput.getScanner();
        this.currentUser = currentUser;
    }

    public void init() {
        System.out.println();
        System.out.println("Add a new bus");
        if (!busAddModel.canManageBuses(currentUser)) {
            System.out.println("Only administrators can add buses.");
            return;
        }

        String busName = promptBusName();
        String source = promptCity("source");
        String destination = promptDestination(source);
        Integer totalSeats = promptTotalSeats();
        Double fare = promptFare();

        busAddModel.addBus(busName, source, destination, totalSeats, fare);
    }

    private String promptBusName() {
        while (true) {
            System.out.print("Enter bus name: ");
            String input = scanner.nextLine();
            String error = busAddModel.validateBusName(input);
            if (error == null) return input.trim();
            System.out.println(error);
        }
    }

    private String promptCity(String label) {
        while (true) {
            System.out.print("Enter " + label + ": ");
            String input = scanner.nextLine();
            String error = busAddModel.validateCity(input, label);
            if (error == null) return input.trim();
            System.out.println(error);
        }
    }

    private String promptDestination(String source) {
        while (true) {
            String destination = promptCity("destination");
            String error = busAddModel.validateDestination(source, destination);
            if (error == null) return destination;
            System.out.println(error);
        }
    }

    private Integer promptTotalSeats() {
        while (true) {
            System.out.print("Enter total number of seats: ");
            Integer seats = ParseHelper.parseNonNegativeInt(scanner.nextLine());
            String error = busAddModel.validateTotalSeats(seats);
            if (error == null) return seats;
            System.out.println(error);
        }
    }

    private Double promptFare() {
        while (true) {
            System.out.print("Enter fare per seat: ");
            Double fare = ParseHelper.parseNonNegativeDouble(scanner.nextLine());
            String error = busAddModel.validateFare(fare);
            if (error == null) return fare;
            System.out.println(error);
        }
    }

    void onBusAdded(Bus bus) {
        System.out.println();
        System.out.println("Bus added successfully.");
        System.out.println("Bus id: " + bus.getBusId());
    }

    void onBusAddFailed(String message) {
        System.out.println(message);
    }
}
