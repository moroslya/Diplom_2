package site.nomoreparties.stellarburgers;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import site.nomoreparties.stellarburgers.data.User;
import site.nomoreparties.stellarburgers.stepsAndConstants.Constants;
import site.nomoreparties.stellarburgers.stepsAndConstants.Steps;

public class CreateUserTest extends Steps {

    private User user;

    @Before
    public void setUp() {
        RestAssured.baseURI = Constants.URL;
    }

    @Test
    @DisplayName("Создание нового пользователя")
    public void createOneNewUser() {

        user = new User(getRandomUserEmail(), "qwerty12345", "Иванов Иван");

        checkSuccessCreateNewUser(user);

    }

    @Test
    @DisplayName("Создание пользователя, который уже зарегистрирован")
    public void createTwoUsersWithSameEmail() {

        //Создаем нового пользователя
        user = new User(getRandomUserEmail(), "qwerty12345", "Иванов Иван");

        checkSuccessCreateNewUser(user);

        //Пытаемся создать второго пользователя с таким же email
        User secondUser = new User(user.getEmail(), "qwerty54321", "Петров Петр");

        checkErrorWhenCreatingUserWithExistingEmail(secondUser);

    }

    @Test
    @DisplayName("Создание нового пользователя с незаполненным email")
    public void createNewUserWithoutEmail() {

        checkErrorWhenCreatingUserNoRequiredFields(null, "qwerty12345", "Иванов Иван");

    }

    @Test
    @DisplayName("Создание нового пользователя с незаполненным паролем")
    public void createNewUserWithoutPassword() {

        checkErrorWhenCreatingUserNoRequiredFields(getRandomUserEmail(), null, "Иванов Иван");

    }

    @Test
    @DisplayName("Создание нового пользователя с незаполненным именем")
    public void createNewUserWithoutName() {

        checkErrorWhenCreatingUserNoRequiredFields(getRandomUserEmail(), "qwerty12345", null);

    }

    @After
    public void cleanUp() {

        if (user != null && user.getAccessToken() != null) {

            deleteUser(user);
            user = null;

        }

    }

}
