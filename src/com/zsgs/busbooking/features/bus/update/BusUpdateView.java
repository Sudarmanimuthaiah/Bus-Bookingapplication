package com.zsgs.busbooking.features.bus.update;

import com.zsgs.busbooking.data.dto.Bus;
import com.zsgs.busbooking.data.dto.User;
import com.zsgs.busbooking.util.ConsoleInput;
import com.zsgs.busbooking.util.ParseHelper;

import java.util.List;
import java.util.Scanner;

public class BusUpdateView {

    private final BusUpdateModel busUpdateModel;
    private final Scanner scanner;
    private final User currentUser;

    public BusUpdateView(User currentUser) {
        this.busUpdateModel = new BusUpdateModel(this);
        this.scanner = ConsoleInput.getScanner();
        this.currentUser = currentUser;
    }

    public void init() {
        System.out.println();
        System.out.println("Update bus details");
        if (!busUpdateModel.canManageBuses(currentUser)) {
            System.out.println("Only administrators can update buses.");
            return;
        }

        Bus bus = chooseBus();
        if (bus == null) return;

        System.out.println("Leave a field blank to keep the existing value.");
        String busName = promptOptional("Bus name [" + bus.getBusName() + "]: ");
        String source = promptOptional("Source [" + bus.getSource() + "]: ");
        String destination = promptOptional("Destination [" + bus.getDestination() + "]: ");
        Integer totalSeats = promptOptionalInt("Total seats [" + bus.getTotalSeats() + "]: ");
        Double fare = promptOptionalDouble("Fare [" + bus.getFare() + "]: ");
        Bus.BusStatus status = promptOptionalStatus(bus.getStatus());

        busUpdateModel.updateBus(bus, busName, source, destination, totalSeats, fare, status);
    }

    private Bus chooseBus() {
        List<Bus> buses = busUpdateModel.getAllBuses();
        if (buses.isEmpty()) {
            System.out.println("No buses available to update.");
            return null;
        }
        com.zsgs.busbooking.features.bus.list.BusListView.renderBuses(buses);
        while (true) {
            System.out.print("Enter the # of the bus to update (or 0 to cancel): ");
            Integer index = ParseHelper.parseNonNegativeInt(scanner.nextLine());
            if (index != null && index == 0) return null;
            if (index != null && index >= 1 && index <= buses.size()) {
                return buses.get(index - 1);
            }
            System.out.println("Select a valid option.");
        }
    }

    private String promptOptional(String label) {
        System.out.print(label);
        String input = scanner.nextLine();
        return input == null ? "" : input.trim();
    }

    private Integer promptOptionalInt(String label) {
        System.out.print(label);
        String input = scanner.nextLine();
        if (input == null || input.trim().isEmpty()) return null;
        return ParseHelper.parseNonNegativeInt(input);
    }

    private Double promptOptionalDouble(String label) {
        System.out.print(label);
        String input = scanner.nextLine();
        if (input == null || input.trim().isEmpty()) return null;
        return ParseHelper.parseNonNegativeDouble(input);
    }

    private Bus.BusStatus promptOptionalStatus(Bus.BusStatus current) {
        System.out.println("Status: 1. Active 2. Inactive  (current: " + (current == null ? "-" : current.name()) + ")");
        System.out.print("Choose an option (blank to keep): ");
        String input = scanner.nextLine();
        if (input == null) return null;
        String c = input.trim();
        if (c.isEmpty()) return null;
        if (c.equals("1") || c.equalsIgnoreCase("Active")) return Bus.BusStatus.ACTIVE;
        if (c.equals("2") || c.equalsIgnoreCase("Inactive")) return Bus.BusStatus.INACTIVE;
        System.out.println("Invalid status; keeping existing value.");
        return null;
    }

    void onBusUpdated(Bus bus) {
        System.out.println("Bus " + bus.getBusId() + " updated successfully.");
    }

    void onBusUpdateFailed(String message) {
        System.out.println(message);
    }
}
