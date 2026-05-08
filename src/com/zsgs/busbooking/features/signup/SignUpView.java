package com.zsgs.busbooking.features.signup;

import com.zsgs.busbooking.data.dto.User;
import com.zsgs.busbooking.features.signin.SignInView;
import com.zsgs.busbooking.util.ConsoleInput;
import com.zsgs.busbooking.util.ParseHelper;

import java.util.Scanner;

public class SignUpView {

    private final SignUpModel signUpModel;
    private final Scanner scanner;

    public SignUpView() {
        this.signUpModel = new SignUpModel(this);
        this.scanner = ConsoleInput.getScanner();
    }

    public void init() {
        startSignUp();
    }

    private void startSignUp() {
        System.out.println();
        System.out.println("Create your BusBookingSystem account");

        boolean firstUser = signUpModel.isFirstUser();

        String name = promptName();
        String email = promptEmail();
        String password = promptPassword();
        String mobile = promptMobile();
        Integer age = promptAge();
        User.Gender gender = promptGender();
        User.Role role;
        if (firstUser) {
            System.out.println("As the first user in the system, you will be registered as an Admin.");
            role = User.Role.ADMIN;
        } else {
            role = User.Role.PASSENGER;
        }

        signUpModel.registerUser(name, email, password, mobile, age, gender, role);
    }

    private String promptName() {
        while (true) {
            System.out.print("Enter your full name: ");
            String input = scanner.nextLine();
            String error = signUpModel.validateName(input);
            if (error == null) return input.trim();
            showErrorMessage(error);
        }
    }

    private String promptEmail() {
        while (true) {
            System.out.print("Enter your email: ");
            String input = scanner.nextLine();
            String error = signUpModel.validateEmail(input);
            if (error == null) return input.trim();
            showErrorMessage(error);
        }
    }

    private String promptPassword() {
        while (true) {
            System.out.print("Enter password (minimum 8 characters with letters and numbers): ");
            String input = scanner.nextLine();
            String error = signUpModel.validatePassword(input);
            if (error != null) {
                showErrorMessage(error);
                continue;
            }
            System.out.print("Confirm password: ");
            String confirm = scanner.nextLine();
            String confirmError = signUpModel.validateConfirmPassword(input, confirm);
            if (confirmError != null) {
                showErrorMessage(confirmError);
                continue;
            }
            return input;
        }
    }

    private String promptMobile() {
        while (true) {
            System.out.print("Enter your 10 digit mobile number: ");
            String input = scanner.nextLine();
            String error = signUpModel.validateMobile(input);
            if (error == null) return input.trim();
            showErrorMessage(error);
        }
    }

    private Integer promptAge() {
        while (true) {
            System.out.print("Enter your age: ");
            Integer age = ParseHelper.parseNonNegativeInt(scanner.nextLine());
            String error = signUpModel.validateAge(age);
            if (error == null) return age;
            showErrorMessage(error);
        }
    }

    private User.Gender promptGender() {
        while (true) {
            System.out.println("Select gender:");
            System.out.println("1. Male");
            System.out.println("2. Female");
            System.out.println("3. Other");
            System.out.print("Choose an option: ");
            User.Gender gender = signUpModel.parseGender(scanner.nextLine());
            if (gender != null) return gender;
            showErrorMessage("Select a valid option.");
        }
    }

    void onSignUpSuccessful(User user) {
        System.out.println();
        System.out.println("Account created successfully.");
        System.out.println("Your user id is " + user.getUserId() + ".");
        System.out.println("Please sign in to continue.");
        new SignInView().init();
    }

    void showErrorMessage(String errorMessage) {
        System.out.println(errorMessage);
    }
}
