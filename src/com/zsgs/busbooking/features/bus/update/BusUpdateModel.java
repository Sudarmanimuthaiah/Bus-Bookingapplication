package com.zsgs.busbooking.features.bus.update;

import com.zsgs.busbooking.data.dto.Bus;
import com.zsgs.busbooking.data.dto.User;
import com.zsgs.busbooking.data.repository.BusBookingDB;

import java.util.List;
import java.util.Locale;

class BusUpdateModel {

    private static final int MIN_NAME = 2;
    private static final int MAX_NAME = 60;
    private static final int MIN_CITY = 2;
    private static final int MAX_CITY = 40;
    private static final int MIN_SEATS = 1;
    private static final int MAX_SEATS = 60;
    private static final double MAX_FARE = 10000.0;

    private final BusUpdateView busUpdateView;

    BusUpdateModel(BusUpdateView busUpdateView) {
        this.busUpdateView = busUpdateView;
    }

    boolean canManageBuses(User user) {
        return user != null && user.getRole() == User.Role.ADMIN;
    }

    List<Bus> getAllBuses() {
        return BusBookingDB.getInstance().getBuses();
    }

    void updateBus(Bus existing, String busName, String source, String destination,
                   Integer totalSeats, Double fare, Bus.BusStatus status) {
        if (existing == null) {
            busUpdateView.onBusUpdateFailed("Bus not found.");
            return;
        }

        if (busName != null && !busName.isEmpty()) {
            if (busName.length() < MIN_NAME || busName.length() > MAX_NAME) {
                busUpdateView.onBusUpdateFailed("Bus name must be " + MIN_NAME + "-" + MAX_NAME + " chars.");
                return;
            }
            existing.setBusName(busName);
        }
        if (source != null && !source.isEmpty()) {
            if (source.length() < MIN_CITY || source.length() > MAX_CITY) {
                busUpdateView.onBusUpdateFailed("Source must be " + MIN_CITY + "-" + MAX_CITY + " chars.");
                return;
            }
            existing.setSource(normalizeCity(source));
        }
        if (destination != null && !destination.isEmpty()) {
            if (destination.length() < MIN_CITY || destination.length() > MAX_CITY) {
                busUpdateView.onBusUpdateFailed("Destination must be " + MIN_CITY + "-" + MAX_CITY + " chars.");
                return;
            }
            existing.setDestination(normalizeCity(destination));
        }
        if (existing.getSource() != null && existing.getDestination() != null
                && existing.getSource().equalsIgnoreCase(existing.getDestination())) {
            busUpdateView.onBusUpdateFailed("Source and destination must be different.");
            return;
        }
        if (totalSeats != null) {
            if (totalSeats < MIN_SEATS || totalSeats > MAX_SEATS) {
                busUpdateView.onBusUpdateFailed("Total seats must be " + MIN_SEATS + "-" + MAX_SEATS + ".");
                return;
            }
            int booked = (existing.getTotalSeats() == null ? 0 : existing.getTotalSeats())
                    - (existing.getAvailableSeats() == null ? 0 : existing.getAvailableSeats());
            if (totalSeats < booked) {
                busUpdateView.onBusUpdateFailed("New total seats cannot be less than already booked seats (" + booked + ").");
                return;
            }
            existing.setTotalSeats(totalSeats);
            existing.setAvailableSeats(totalSeats - booked);
        }
        if (fare != null) {
            if (fare <= 0 || fare > MAX_FARE) {
                busUpdateView.onBusUpdateFailed("Fare must be > 0 and <= " + MAX_FARE + ".");
                return;
            }
            existing.setFare(fare);
        }
        if (status != null) {
            existing.setStatus(status);
        }

        Bus saved = BusBookingDB.getInstance().updateBus(existing);
        if (saved == null) {
            busUpdateView.onBusUpdateFailed("Could not update bus. Please try again.");
            return;
        }
        busUpdateView.onBusUpdated(saved);
    }

    private String normalizeCity(String city) {
        String trimmed = city.trim();
        if (trimmed.isEmpty()) return trimmed;
        return trimmed.substring(0, 1).toUpperCase(Locale.ROOT)
                + trimmed.substring(1).toLowerCase(Locale.ROOT);
    }
}
