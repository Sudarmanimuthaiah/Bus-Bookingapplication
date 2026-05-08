package com.zsgs.busbooking.features.bus.delete;

import com.zsgs.busbooking.data.dto.Bus;
import com.zsgs.busbooking.data.dto.User;
import com.zsgs.busbooking.util.ConsoleInput;
import com.zsgs.busbooking.util.ParseHelper;

import java.util.List;
import java.util.Scanner;

public class BusDeleteView {

    private final BusDeleteModel busDeleteModel;
    private final Scanner scanner;
    private final User currentUser;

    public BusDeleteView(User currentUser) {
        this.busDeleteModel = new BusDeleteModel(this);
        this.scanner = ConsoleInput.getScanner();
        this.currentUser = currentUser;
    }

    public void init() {
        System.out.println();
        System.out.println("Delete bus");
        if (!busDeleteModel.canManageBuses(currentUser)) {
            System.out.println("Only administrators can delete buses.");
            return;
        }

        List<Bus> buses = busDeleteModel.getAllBuses();
        if (buses.isEmpty()) {
            System.out.println("No buses available to delete.");
            return;
        }
        com.zsgs.busbooking.features.bus.list.BusListView.renderBuses(buses);

        Bus selected;
        while (true) {
            System.out.print("Enter the # of the bus to delete (or 0 to cancel): ");
            Integer index = ParseHelper.parseNonNegativeInt(scanner.nextLine());
            if (index != null && index == 0) return;
            if (index != null && index >= 1 && index <= buses.size()) {
                selected = buses.get(index - 1);
                break;
            }
            System.out.println("Select a valid option.");
        }

        System.out.print("Are you sure you want to delete bus " + selected.getBusId() + "? (Y/N): ");
        if (!ParseHelper.isYes(scanner.nextLine())) {
            System.out.println("Delete cancelled.");
            return;
        }

        busDeleteModel.deleteBus(selected);
    }

    void onBusDeleted(Bus bus) {
        System.out.println("Bus " + bus.getBusId() + " deleted successfully.");
    }

    void onBusDeleteFailed(String message) {
        System.out.println(message);
    }
}
