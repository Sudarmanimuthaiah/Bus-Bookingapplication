package com.zsgs.busbooking.data.repository;

import com.zsgs.busbooking.data.dto.Booking;
import com.zsgs.busbooking.data.dto.Bus;
import com.zsgs.busbooking.data.dto.Payment;
import com.zsgs.busbooking.data.dto.User;
import com.zsgs.busbooking.util.HashHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BusBookingDB {

    private BusBookingDB() {
    }

    private static BusBookingDB busBookingDB = null;

    public static final BusBookingDB getInstance() {
        if (busBookingDB == null) {
            busBookingDB = new BusBookingDB();
        }
        return busBookingDB;
    }

    private final List<User> users = new ArrayList<>();
    private final List<Bus> buses = new ArrayList<>();
    private final List<Booking> bookings = new ArrayList<>();
    private final List<Payment> payments = new ArrayList<>();
    private long userPk = 0L;
    private long busPk = 0L;
    private long bookingPk = 0L;
    private long paymentPk = 0L;

    // -- Users --

    public User addUser(User user) {
        if (user == null) return null;
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) return null;

        userPk++;
        user.setId(userPk);
        String prefix = user.getRole() == User.Role.ADMIN ? "ADM" : "USR";
        user.setUserId(String.format(Locale.ROOT, "%s%05d", prefix, userPk));
        if (user.getCreatedAt() == null) {
            user.setCreatedAt(System.currentTimeMillis());
        }
        if (user.getStatus() == null) {
            user.setStatus(User.UserStatus.ACTIVE);
        }
        if (user.getRole() == null) {
            user.setRole(User.Role.PASSENGER);
        }
        users.add(user);
        return user;
    }

    public User getUserByEmail(String email) {
        if (email == null) return null;
        String key = email.trim().toLowerCase(Locale.ROOT);
        if (key.isEmpty()) return null;
        for (User current : users) {
            if (current.getEmail() != null
                    && current.getEmail().trim().toLowerCase(Locale.ROOT).equals(key)) {
                return current;
            }
        }
        return null;
    }

    public User authenticateUser(String email, String password) {
        User user = getUserByEmail(email);
        if (user == null) return null;
        if (password == null) return null;
        String hashed = HashHelper.sha256(password);
        if (hashed == null || !hashed.equals(user.getPassword())) return null;
        return user;
    }

    public User getUserById(Long id) {
        if (id == null) return null;
        for (User current : users) {
            if (id.equals(current.getId())) return current;
        }
        return null;
    }

    public List<User> getUsers() {
        return new ArrayList<>(users);
    }

    public boolean hasAnyUser() {
        return !users.isEmpty();
    }

    // -- Buses --

    public Bus addBus(Bus bus) {
        if (bus == null) return null;
        busPk++;
        bus.setId(busPk);
        bus.setBusId(String.format(Locale.ROOT, "BUS%05d", busPk));
        long now = System.currentTimeMillis();
        if (bus.getCreatedAt() == null) bus.setCreatedAt(now);
        bus.setUpdatedAt(now);
        if (bus.getStatus() == null) bus.setStatus(Bus.BusStatus.ACTIVE);
        if (bus.getAvailableSeats() == null) bus.setAvailableSeats(bus.getTotalSeats());
        buses.add(bus);
        return bus;
    }

    public Bus updateBus(Bus bus) {
        if (bus == null || bus.getId() == null) return null;
        for (int i = 0; i < buses.size(); i++) {
            if (bus.getId().equals(buses.get(i).getId())) {
                bus.setUpdatedAt(System.currentTimeMillis());
                buses.set(i, bus);
                return bus;
            }
        }
        return null;
    }

    public boolean deleteBus(Long busId) {
        if (busId == null) return false;
        for (int i = 0; i < buses.size(); i++) {
            if (busId.equals(buses.get(i).getId())) {
                buses.remove(i);
                return true;
            }
        }
        return false;
    }

    public Bus getBusById(Long id) {
        if (id == null) return null;
        for (Bus current : buses) {
            if (id.equals(current.getId())) return current;
        }
        return null;
    }

    public List<Bus> getBuses() {
        return new ArrayList<>(buses);
    }

    public List<Bus> searchBuses(String source, String destination) {
        List<Bus> result = new ArrayList<>();
        String src = source == null ? null : source.trim().toLowerCase(Locale.ROOT);
        String dst = destination == null ? null : destination.trim().toLowerCase(Locale.ROOT);
        for (Bus current : buses) {
            if (current.getStatus() != Bus.BusStatus.ACTIVE) continue;
            if (src != null && !src.isEmpty()
                    && (current.getSource() == null
                        || !current.getSource().trim().toLowerCase(Locale.ROOT).equals(src))) {
                continue;
            }
            if (dst != null && !dst.isEmpty()
                    && (current.getDestination() == null
                        || !current.getDestination().trim().toLowerCase(Locale.ROOT).equals(dst))) {
                continue;
            }
            result.add(current);
        }
        return result;
    }

    // -- Bookings --

    public Booking addBooking(Booking booking) {
        if (booking == null) return null;
        bookingPk++;
        booking.setId(bookingPk);
        booking.setBookingId(String.format(Locale.ROOT, "BKG%05d", bookingPk));
        long now = System.currentTimeMillis();
        if (booking.getCreatedAt() == null) booking.setCreatedAt(now);
        booking.setUpdatedAt(now);
        if (booking.getBookingStatus() == null) {
            booking.setBookingStatus(Booking.BookingStatus.PENDING);
        }
        bookings.add(booking);
        return booking;
    }

    public Booking updateBooking(Booking booking) {
        if (booking == null || booking.getId() == null) return null;
        for (int i = 0; i < bookings.size(); i++) {
            if (booking.getId().equals(bookings.get(i).getId())) {
                booking.setUpdatedAt(System.currentTimeMillis());
                bookings.set(i, booking);
                return booking;
            }
        }
        return null;
    }

    public Booking getBookingById(Long id) {
        if (id == null) return null;
        for (Booking current : bookings) {
            if (id.equals(current.getId())) return current;
        }
        return null;
    }

    public List<Booking> getBookingsByUser(Long userId) {
        List<Booking> result = new ArrayList<>();
        if (userId == null) return result;
        for (Booking current : bookings) {
            if (userId.equals(current.getUserId())) result.add(current);
        }
        return result;
    }

    public List<Booking> getBookingsByBus(Long busId) {
        List<Booking> result = new ArrayList<>();
        if (busId == null) return result;
        for (Booking current : bookings) {
            if (busId.equals(current.getBusId())) result.add(current);
        }
        return result;
    }

    public List<Booking> getAllBookings() {
        return new ArrayList<>(bookings);
    }

    public boolean isSeatTaken(Long busId, Long travelDate, Integer seatNumber) {
        if (busId == null || travelDate == null || seatNumber == null) return false;
        for (Booking current : bookings) {
            if (current.getBookingStatus() == Booking.BookingStatus.CANCELLED) continue;
            if (!busId.equals(current.getBusId())) continue;
            if (!travelDate.equals(current.getTravelDate())) continue;
            if (seatNumber.equals(current.getSeatNumber())) return true;
        }
        return false;
    }

    // -- Payments --

    public Payment addPayment(Payment payment) {
        if (payment == null) return null;
        paymentPk++;
        payment.setId(paymentPk);
        payment.setPaymentId(String.format(Locale.ROOT, "PAY%05d", paymentPk));
        if (payment.getPaymentStatus() == null) {
            payment.setPaymentStatus(Payment.PaymentStatus.PENDING);
        }
        payments.add(payment);
        return payment;
    }

    public Payment updatePayment(Payment payment) {
        if (payment == null || payment.getId() == null) return null;
        for (int i = 0; i < payments.size(); i++) {
            if (payment.getId().equals(payments.get(i).getId())) {
                payments.set(i, payment);
                return payment;
            }
        }
        return null;
    }

    public Payment getPaymentById(Long id) {
        if (id == null) return null;
        for (Payment current : payments) {
            if (id.equals(current.getId())) return current;
        }
        return null;
    }
}
