package site.nomoreparties.stellarburgers;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import site.nomoreparties.stellarburgers.data.User;
import site.nomoreparties.stellarburgers.constants.Constants;
import site.nomoreparties.stellarburgers.steps.UserSteps;

public class LoginUserTest {

    UserSteps userSteps = new UserSteps();

    private User user;

    @Before
    public void setUp() {
        RestAssured.baseURI = Constants.URL;
    }

    @Test
    @DisplayName("Авторизация под существующим пользователем")
    public void authorizationExistingUser() {

        //Создаем нового пользователя
        user = new User(userSteps.getRandomUserEmail(), "qwerty12345", "Иванов Иван");

        userSteps.createNewUserAndSetTokens(user);

        //Авторизуемся созданным пользователем
        userSteps.checkSuccessAuthorizationExistingUser(user);

    }

    @Test
    @DisplayName("Авторизация с неверным логином")
    public void authorizationWithInvalidEmail() {

        //Авторизуемся с незарегистрированным логином
        user = new User(userSteps.getRandomUserEmail(), "qwerty12345", "Иванов Иван");

        userSteps.checkErrorWhenAuthorizationWithIncorrectData(user);

    }

    @Test
    @DisplayName("Авторизация с неверным паролем")
    public void authorizationWithInvalidPassword() {

        //Создаем нового пользователя
        user = new User(userSteps.getRandomUserEmail(), "qwerty12345", "Иванов Иван");

        userSteps.createNewUserAndSetTokens(user);

        //Авторизуемся с неверным паролем
        user.setPassword("qwerty12346");

        userSteps.checkErrorWhenAuthorizationWithIncorrectData(user);

    }

    @After
    public void cleanUp() {

        if (user != null && user.getAccessToken() != null) {

            userSteps.deleteUser(user);
            user = null;

        }

    }

}
