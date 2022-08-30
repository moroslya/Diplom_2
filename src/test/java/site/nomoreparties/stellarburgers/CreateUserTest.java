package site.nomoreparties.stellarburgers;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import site.nomoreparties.stellarburgers.data.User;
import site.nomoreparties.stellarburgers.constants.Constants;
import site.nomoreparties.stellarburgers.steps.UserSteps;

public class CreateUserTest {

    UserSteps userSteps = new UserSteps();

    private User user;

    @Before
    public void setUp() {
        RestAssured.baseURI = Constants.URL;
    }

    @Test
    @DisplayName("Создание нового пользователя")
    public void createOneNewUser() {

        user = new User(userSteps.getRandomUserEmail(), "qwerty12345", "Иванов Иван");

        userSteps.checkSuccessCreateNewUser(user);

    }

    @Test
    @DisplayName("Создание пользователя, который уже зарегистрирован")
    public void createTwoUsersWithSameEmail() {

        //Создаем нового пользователя
        user = new User(userSteps.getRandomUserEmail(), "qwerty12345", "Иванов Иван");

        userSteps.checkSuccessCreateNewUser(user);

        //Пытаемся создать второго пользователя с таким же email
        User secondUser = new User(user.getEmail(), "qwerty54321", "Петров Петр");

        userSteps.checkErrorWhenCreatingUserWithExistingEmail(secondUser);

    }

    @Test
    @DisplayName("Создание нового пользователя с незаполненным email")
    public void createNewUserWithoutEmail() {

        userSteps.checkErrorWhenCreatingUserNoRequiredFields(null, "qwerty12345", "Иванов Иван");

    }

    @Test
    @DisplayName("Создание нового пользователя с незаполненным паролем")
    public void createNewUserWithoutPassword() {

        userSteps.checkErrorWhenCreatingUserNoRequiredFields(userSteps.getRandomUserEmail(), null, "Иванов Иван");

    }

    @Test
    @DisplayName("Создание нового пользователя с незаполненным именем")
    public void createNewUserWithoutName() {

        userSteps.checkErrorWhenCreatingUserNoRequiredFields(userSteps.getRandomUserEmail(), "qwerty12345", null);

    }

    @After
    public void cleanUp() {

        if (user != null && user.getAccessToken() != null) {

            userSteps.deleteUser(user);
            user = null;

        }

    }

}
