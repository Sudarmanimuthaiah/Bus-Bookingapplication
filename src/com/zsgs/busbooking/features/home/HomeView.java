package com.zsgs.busbooking.features.home;

import com.zsgs.busbooking.data.dto.User;
import com.zsgs.busbooking.features.booking.all.BookingAllView;
import com.zsgs.busbooking.features.booking.cancel.BookingCancelView;
import com.zsgs.busbooking.features.booking.create.BookingCreateView;
import com.zsgs.busbooking.features.booking.history.BookingHistoryView;
import com.zsgs.busbooking.features.bus.add.BusAddView;
import com.zsgs.busbooking.features.bus.delete.BusDeleteView;
import com.zsgs.busbooking.features.bus.list.BusListView;
import com.zsgs.busbooking.features.bus.search.BusSearchView;
import com.zsgs.busbooking.features.bus.update.BusUpdateView;
import com.zsgs.busbooking.util.ConsoleInput;

import java.util.Scanner;

public class HomeView {

    private final HomeModel homeModel;
    private final User user;
    private final Scanner scanner;

    public HomeView(User user) {
        this.homeModel = new HomeModel(this);
        this.user = user;
        this.scanner = ConsoleInput.getScanner();
    }

    public void init() {
        homeModel.init(user);
    }

    void showUnauthorized() {
        System.out.println("Your account role is not set. Contact your administrator.");
    }

    void showAdminMenu() {
        while (true) {
            System.out.println();
            System.out.println("Admin Home");
            System.out.println("1. Add new bus");
            System.out.println("2. Update bus details");
            System.out.println("3. Delete bus");
            System.out.println("4. View all buses");
            System.out.println("5. View all bookings");
            System.out.println("6. Sign out");
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    new BusAddView(user).init();
                    break;
                case "2":
                    new BusUpdateView(user).init();
                    break;
                case "3":
                    new BusDeleteView(user).init();
                    break;
                case "4":
                    new BusListView().init();
                    break;
                case "5":
                    new BookingAllView().init();
                    break;
                case "6":
                    System.out.println("You have been signed out.");
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    void showPassengerMenu() {
        while (true) {
            System.out.println();
            System.out.println("Passenger Home");
            System.out.println("1. Search buses");
            System.out.println("2. Book a seat");
            System.out.println("3. View my bookings");
            System.out.println("4. Cancel a booking");
            System.out.println("5. Sign out");
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    new BusSearchView().init();
                    break;
                case "2":
                    new BookingCreateView(user).init();
                    break;
                case "3":
                    new BookingHistoryView(user).init();
                    break;
                case "4":
                    new BookingCancelView(user).init();
                    break;
                case "5":
                    System.out.println("You have been signed out.");
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }
}
