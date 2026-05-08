package com.zsgs.busbooking.features.signin;

import com.zsgs.busbooking.data.dto.LoginRequest;
import com.zsgs.busbooking.data.dto.User;
import com.zsgs.busbooking.data.repository.BusBookingDB;

import java.util.regex.Pattern;

class SignInModel {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private final SignInView signInView;

    SignInModel(SignInView signInView) {
        this.signInView = signInView;
    }

    String validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return "Email cannot be empty";
        }
        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            return "Enter a valid email address";
        }
        return null;
    }

    String validatePassword(String password) {
        if (password == null || password.isEmpty()) {
            return "Password cannot be empty";
        }
        return null;
    }

    void authenticate(LoginRequest request) {
        if (request == null) {
            signInView.onSignInFailed("Invalid email or password");
            return;
        }
        String emailError = validateEmail(request.getEmail());
        if (emailError != null) {
            signInView.onSignInFailed(emailError);
            return;
        }
        String passwordError = validatePassword(request.getPassword());
        if (passwordError != null) {
            signInView.onSignInFailed(passwordError);
            return;
        }

        User user = BusBookingDB.getInstance().authenticateUser(
                request.getEmail(), request.getPassword());
        if (user == null) {
            signInView.onSignInFailed("Invalid email or password");
            return;
        }
        if (user.getStatus() == User.UserStatus.INACTIVE) {
            signInView.onSignInFailed("Your account is not active. Contact your administrator.");
            return;
        }
        signInView.onSignInSuccessful(user);
    }
}
