package com.zsgs.busbooking.features.signup;

import com.zsgs.busbooking.data.dto.User;
import com.zsgs.busbooking.data.repository.BusBookingDB;
import com.zsgs.busbooking.util.HashHelper;

import java.util.regex.Pattern;

class SignUpModel {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern MOBILE_PATTERN = Pattern.compile("^[6-9]\\d{9}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[A-Za-z])(?=.*\\d).{8,}$");

    private static final int MIN_NAME_LENGTH = 3;
    private static final int MAX_NAME_LENGTH = 50;
    private static final int MIN_AGE = 18;
    private static final int MAX_AGE = 120;

    private final SignUpView signUpView;

    SignUpModel(SignUpView signUpView) {
        this.signUpView = signUpView;
    }

    String validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "Name cannot be empty";
        }
        String trimmed = name.trim();
        if (trimmed.length() < MIN_NAME_LENGTH || trimmed.length() > MAX_NAME_LENGTH) {
            return "Name must be between " + MIN_NAME_LENGTH + " and " + MAX_NAME_LENGTH + " characters";
        }
        return null;
    }

    String validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return "Email cannot be empty";
        }
        String trimmed = email.trim();
        if (!EMAIL_PATTERN.matcher(trimmed).matches()) {
            return "Enter a valid email address";
        }
        if (BusBookingDB.getInstance().getUserByEmail(trimmed) != null) {
            return "This email is already registered";
        }
        return null;
    }

    String validatePassword(String password) {
        if (password == null || password.isEmpty()) {
            return "Password cannot be empty";
        }
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            return "Password must be at least 8 characters and contain letters and numbers";
        }
        return null;
    }

    String validateConfirmPassword(String password, String confirmPassword) {
        if (confirmPassword == null || !confirmPassword.equals(password)) {
            return "Passwords do not match";
        }
        return null;
    }

    String validateMobile(String mobile) {
        if (mobile == null || mobile.trim().isEmpty()) {
            return "Mobile number cannot be empty";
        }
        if (!MOBILE_PATTERN.matcher(mobile.trim()).matches()) {
            return "Enter a valid 10 digit mobile number";
        }
        return null;
    }

    String validateAge(Integer age) {
        if (age == null) return "Enter a valid age";
        if (age < MIN_AGE || age > MAX_AGE) {
            return "Age must be between " + MIN_AGE + " and " + MAX_AGE;
        }
        return null;
    }

    User.Gender parseGender(String choice) {
        if (choice == null) return null;
        String c = choice.trim();
        if (c.equals("1") || c.equalsIgnoreCase("Male")) return User.Gender.MALE;
        if (c.equals("2") || c.equalsIgnoreCase("Female")) return User.Gender.FEMALE;
        if (c.equals("3") || c.equalsIgnoreCase("Other")) return User.Gender.OTHER;
        return null;
    }

    boolean isFirstUser() {
        return !BusBookingDB.getInstance().hasAnyUser();
    }

    void registerUser(String name, String email, String password,
                      String mobile, Integer age, User.Gender gender,
                      User.Role role) {
        User user = new User();
        user.setName(name.trim());
        user.setEmail(email.trim());
        user.setPassword(HashHelper.sha256(password));
        user.setMobileNo(mobile.trim());
        user.setAge(age);
        user.setGender(gender);
        user.setRole(role);
        user.setStatus(User.UserStatus.ACTIVE);

        User saved = BusBookingDB.getInstance().addUser(user);
        if (saved == null) {
            signUpView.showErrorMessage("Could not create account. Please try again.");
            return;
        }
        signUpView.onSignUpSuccessful(saved);
    }
}
