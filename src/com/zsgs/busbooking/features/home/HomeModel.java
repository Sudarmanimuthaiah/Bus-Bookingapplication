package com.zsgs.busbooking.features.home;

import com.zsgs.busbooking.data.dto.User;

class HomeModel {

    private final HomeView homeView;

    HomeModel(HomeView homeView) {
        this.homeView = homeView;
    }

    void init(User user) {
        if (user == null || user.getRole() == null) {
            homeView.showUnauthorized();
            return;
        }
        if (user.getRole() == User.Role.ADMIN) {
            homeView.showAdminMenu();
        } else {
            homeView.showPassengerMenu();
        }
    }
}
