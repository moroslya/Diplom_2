package site.nomoreparties.stellarburgers;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import site.nomoreparties.stellarburgers.data.User;
import site.nomoreparties.stellarburgers.constants.Constants;
import site.nomoreparties.stellarburgers.steps.UserSteps;

public class DataUpdateUserTest {

    UserSteps userSteps = new UserSteps();

    private User user;

    @Before
    public void setUp() {
        RestAssured.baseURI = Constants.URL;

        //Создаем тестового пользователя
        user = new User(userSteps.getRandomUserEmail(), "qwerty12345", "Иванов Иван");

        userSteps.createNewUserAndSetTokens(user);

    }

    @Test
    @DisplayName("Изменение email пользователя с авторизацией")
    public void changeUserEmailWithAuthorization() {

        //Изменяем email тестовому пользователю
        user.setEmail(userSteps.getRandomUserEmail());

        userSteps.checkSuccessUserDataUpdateWithAuthorization(user);

        //Проверяем, что можно авторизоваться с новым email
        userSteps.checkSuccessAuthorizationExistingUser(user);

        //Проверяем соответствие email в информации о пользователе новому значению
        userSteps.checkCorrectnessUserInformation(user);

    }

    @Test
    @DisplayName("Изменение пароля пользователя с авторизацией")
    public void changeUserPasswordWithAuthorization() {

        //Изменяем пароль тестовому пользователю
        user.setPassword("qwerty12346");

        userSteps.checkSuccessUserDataUpdateWithAuthorization(user);

        //Проверяем, что можно авторизоваться с новым паролем
        userSteps.checkSuccessAuthorizationExistingUser(user);

    }

    @Test
    @DisplayName("Изменение имени пользователя с авторизацией")
    public void changeUserNameWithAuthorization() {

        //Изменяем имя тестовому пользователю
        user.setName("Петров Петр");

        userSteps.checkSuccessUserDataUpdateWithAuthorization(user);

        //Проверяем соответствие имени в информации о пользователе новому значению
        userSteps.checkCorrectnessUserInformation(user);

    }

    @Test
    @DisplayName("Изменение email пользователя без авторизации")
    public void changeUserEmailWithoutAuthorization() {

        String oldEmail = user.getEmail();

        //Пытаемся изменить email тестовому пользователю без авторизации и получаем ошибку
        user.setEmail(userSteps.getRandomUserEmail());

        userSteps.checkErrorWhenUserDataUpdateWithoutAuthorization(user);

        //Проверяем, что по прежнему можно авторизоваться со старым email
        user.setEmail(oldEmail);

        userSteps.checkSuccessAuthorizationExistingUser(user);

        //Проверяем соответствие email в информации о пользователе старому значению
        userSteps.checkCorrectnessUserInformation(user);

    }

    @Test
    @DisplayName("Изменение пароля пользователя без авторизации")
    public void changeUserPasswordWithoutAuthorization() {

        String oldPassword = user.getPassword();

        //Пытаемся изменить пароль тестовому пользователю без авторизации и получаем ошибку
        user.setPassword("qwerty12346");

        userSteps.checkErrorWhenUserDataUpdateWithoutAuthorization(user);

        //Проверяем, что по прежнему можно авторизоваться со старым паролем
        user.setPassword(oldPassword);

        userSteps.checkSuccessAuthorizationExistingUser(user);

    }

    @Test
    @DisplayName("Изменение имени пользователя без авторизации")
    public void changeUserNameWithoutAuthorization() {

        String oldName = user.getName();

        //Пытаемся изменить имя тестовому пользователю без авторизации и получаем ошибку
        user.setName("Петров Петр");

        userSteps.checkErrorWhenUserDataUpdateWithoutAuthorization(user);

        //Проверяем соответствие имени в информации о пользователе старому значению
        user.setName(oldName);

        userSteps.checkCorrectnessUserInformation(user);

    }

    @After
    public void cleanUp() {

        if (user != null && user.getAccessToken() != null) {

            userSteps.deleteUser(user);
            user = null;

        }

    }

}
