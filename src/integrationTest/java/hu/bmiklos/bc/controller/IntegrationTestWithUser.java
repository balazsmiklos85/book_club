package hu.bmiklos.bc.controller;

import org.springframework.beans.factory.annotation.Autowired;

import hu.bmiklos.bc.model.Email;
import hu.bmiklos.bc.service.UserService;

public abstract class IntegrationTestWithUser {

    @Autowired
    private UserService userService;
    
    private Email testUser;

    protected Email createUserForTest(int externalId, String name, String email, String password) {
        testUser = userService.registerUser("" + externalId, name, email, email, password, password);
        return testUser;
    }

    protected void deleteTestedUser() {
        userService.deleteUser(testUser);
    }
}
