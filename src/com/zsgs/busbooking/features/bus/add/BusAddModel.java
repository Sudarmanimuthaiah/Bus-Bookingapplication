package com.zsgs.busbooking.features.bus.add;

import com.zsgs.busbooking.data.dto.Bus;
import com.zsgs.busbooking.data.dto.User;
import com.zsgs.busbooking.data.repository.BusBookingDB;

import java.util.Locale;

class BusAddModel {

    private static final int MIN_NAME = 2;
    private static final int MAX_NAME = 60;
    private static final int MIN_CITY = 2;
    private static final int MAX_CITY = 40;
    private static final int MIN_SEATS = 1;
    private static final int MAX_SEATS = 80;
    private static final double MAX_FARE = 100000.0;

    private final BusAddView busAddView;

    BusAddModel(BusAddView busAddView) {
        this.busAddView = busAddView;
    }

    boolean canManageBuses(User user) {
        return user != null && user.getRole() == User.Role.ADMIN;
    }

    String validateBusName(String name) {
        if (name == null || name.trim().isEmpty()) return "Bus name cannot be empty";
        String trimmed = name.trim();
        if (trimmed.length() < MIN_NAME || trimmed.length() > MAX_NAME) {
            return "Bus name must be between " + MIN_NAME + " and " + MAX_NAME + " characters";
        }
        return null;
    }

    String validateCity(String city, String label) {
        if (city == null || city.trim().isEmpty()) {
            return capitalize(label) + " cannot be empty";
        }
        String trimmed = city.trim();
        if (trimmed.length() < MIN_CITY || trimmed.length() > MAX_CITY) {
            return capitalize(label) + " must be between " + MIN_CITY + " and " + MAX_CITY + " characters";
        }
        return null;
    }

    String validateDestination(String source, String destination) {
        if (source != null && destination != null
                && source.trim().equalsIgnoreCase(destination.trim())) {
            return "Destination must be different from source";
        }
        return null;
    }

    String validateTotalSeats(Integer seats) {
        if (seats == null) return "Enter a valid number of seats";
        if (seats < MIN_SEATS || seats > MAX_SEATS) {
            return "Total seats must be between " + MIN_SEATS + " and " + MAX_SEATS;
        }
        return null;
    }

    String validateFare(Double fare) {
        if (fare == null) return "Enter a valid fare amount";
        if (fare <= 0 || fare > MAX_FARE) {
            return "Fare must be greater than 0 and at most " + MAX_FARE;
        }
        return null;
    }

    void addBus(String busName, String source, String destination, Integer totalSeats, Double fare) {
        Bus bus = new Bus();
        bus.setBusName(busName.trim());
        bus.setSource(normalizeCity(source));
        bus.setDestination(normalizeCity(destination));
        bus.setTotalSeats(totalSeats);
        bus.setAvailableSeats(totalSeats);
        bus.setFare(fare);
        bus.setStatus(Bus.BusStatus.ACTIVE);

        Bus saved = BusBookingDB.getInstance().addBus(bus);
        if (saved == null) {
            busAddView.onBusAddFailed("Could not add bus. Please try again.");
            return;
        }
        busAddView.onBusAdded(saved);
    }

    private String normalizeCity(String city) {
        String trimmed = city.trim();
        if (trimmed.isEmpty()) return trimmed;
        return trimmed.substring(0, 1).toUpperCase(Locale.ROOT)
                + trimmed.substring(1).toLowerCase(Locale.ROOT);
    }

    private String capitalize(String text) {
        if (text == null || text.isEmpty()) return text;
        return text.substring(0, 1).toUpperCase(Locale.ROOT) + text.substring(1);
    }
}
