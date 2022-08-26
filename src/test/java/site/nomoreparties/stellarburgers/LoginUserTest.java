package site.nomoreparties.stellarburgers;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import site.nomoreparties.stellarburgers.data.User;
import site.nomoreparties.stellarburgers.stepsAndConstants.Constants;
import site.nomoreparties.stellarburgers.stepsAndConstants.Steps;

public class LoginUserTest extends Steps {

    private User user;

    @Before
    public void setUp() {
        RestAssured.baseURI = Constants.URL;
    }

    @Test
    @DisplayName("Авторизация под существующим пользователем")
    public void authorizationExistingUser() {

        //Создаем нового пользователя
        user = new User(getRandomUserEmail(), "qwerty12345", "Иванов Иван");

        createNewUserAndSetTokens(user);

        //Авторизуемся созданным пользователем
        checkSuccessAuthorizationExistingUser(user);

    }

    @Test
    @DisplayName("Авторизация с неверным логином")
    public void authorizationWithInvalidEmail() {

        //Авторизуемся с незарегистрированным логином
        user = new User(getRandomUserEmail(), "qwerty12345", "Иванов Иван");

        checkErrorWhenAuthorizationWithIncorrectData(user);

    }

    @Test
    @DisplayName("Авторизация с неверным паролем")
    public void authorizationWithInvalidPassword() {

        //Создаем нового пользователя
        user = new User(getRandomUserEmail(), "qwerty12345", "Иванов Иван");

        createNewUserAndSetTokens(user);

        //Авторизуемся с неверным паролем
        user.setPassword("qwerty12346");

        checkErrorWhenAuthorizationWithIncorrectData(user);

    }

    @After
    public void cleanUp() {

        if (user != null && user.getAccessToken() != null) {

            deleteUser(user);
            user = null;

        }

    }

}
