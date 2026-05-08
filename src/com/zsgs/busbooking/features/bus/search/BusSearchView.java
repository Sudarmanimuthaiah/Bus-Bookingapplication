package com.zsgs.busbooking.features.bus.search;

import com.zsgs.busbooking.data.dto.Bus;
import com.zsgs.busbooking.util.ConsoleInput;
import com.zsgs.busbooking.util.ParseHelper;

import java.util.List;
import java.util.Scanner;

public class BusSearchView {

    private final BusSearchModel busSearchModel;
    private final Scanner scanner;

    public BusSearchView() {
        this.busSearchModel = new BusSearchModel(this);
        this.scanner = ConsoleInput.getScanner();
    }

    public void init() {
        System.out.println();
        System.out.println("Search buses");

        String source = promptCity("source");
        String destination = promptCity("destination");
        Long travelDate = promptTravelDate();

        List<Bus> results = busSearchModel.search(source, destination, travelDate);
        if (results.isEmpty()) {
            System.out.println("No buses found for the given route on " + ParseHelper.formatDate(travelDate) + ".");
        } else {
            System.out.println("Results for " + source + " -> " + destination
                    + " on " + ParseHelper.formatDate(travelDate));
            com.zsgs.busbooking.features.bus.list.BusListView.renderBuses(results);
        }
        System.out.print("Press Enter to return: ");
        scanner.nextLine();
    }

    private String promptCity(String label) {
        while (true) {
            System.out.print("Enter " + label + ": ");
            String input = scanner.nextLine();
            String error = busSearchModel.validateCity(input, label);
            if (error == null) return input.trim();
            System.out.println(error);
        }
    }

    private Long promptTravelDate() {
        while (true) {
            System.out.print("Enter travel date (dd-MM-yyyy): ");
            Long date = busSearchModel.parseTravelDate(scanner.nextLine());
            if (date != null) return date;
            System.out.println("Enter a valid date. Travel date must be today or later.");
        }
    }
}
