package com.zsgs.busbooking.features.bus.list;

import com.zsgs.busbooking.data.dto.Bus;
import com.zsgs.busbooking.util.ConsoleInput;

import java.util.List;
import java.util.Scanner;

public class BusListView {

    private final BusListModel busListModel;
    private final Scanner scanner;

    public BusListView() {
        this.busListModel = new BusListModel(this);
        this.scanner = ConsoleInput.getScanner();
    }

    public void init() {
        System.out.println();
        System.out.println("All Buses");
        List<Bus> buses = busListModel.getAllBuses();
        renderBuses(buses);
        System.out.print("Press Enter to return: ");
        scanner.nextLine();
    }

    public static void renderBuses(List<Bus> buses) {
        if (buses == null || buses.isEmpty()) {
            System.out.println("No buses available.");
            return;
        }
        System.out.println("#   Id       Name                      Source          Destination     Seats(A/T)  Fare       Status");
        for (int i = 0; i < buses.size(); i++) {
            Bus b = buses.get(i);
            String row = String.format(
                    "%-3d %-8s %-25s %-15s %-15s %-11s %-10s %s",
                    (i + 1),
                    safe(b.getBusId()),
                    truncate(safe(b.getBusName()), 25),
                    truncate(safe(b.getSource()), 15),
                    truncate(safe(b.getDestination()), 15),
                    nullSafeInt(b.getAvailableSeats()) + "/" + nullSafeInt(b.getTotalSeats()),
                    String.format("%.2f", b.getFare() == null ? 0.0 : b.getFare()),
                    b.getStatus() == null ? "-" : b.getStatus().name());
            System.out.println(row);
        }
    }

    private static String safe(String value) {
        return value == null ? "-" : value;
    }

    private static String nullSafeInt(Integer value) {
        return value == null ? "-" : String.valueOf(value);
    }

    private static String truncate(String value, int max) {
        if (value.length() <= max) return value;
        return value.substring(0, max - 1) + "~";
    }
}
