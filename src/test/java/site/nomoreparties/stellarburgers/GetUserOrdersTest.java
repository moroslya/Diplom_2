package site.nomoreparties.stellarburgers;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import site.nomoreparties.stellarburgers.data.Order;
import site.nomoreparties.stellarburgers.data.User;
import site.nomoreparties.stellarburgers.stepsAndConstants.Constants;
import site.nomoreparties.stellarburgers.stepsAndConstants.Steps;

public class GetUserOrdersTest extends Steps {

    private User user;
    private Order order;

    @Before
    public void setUp() {

        RestAssured.baseURI = Constants.URL;

        //Создаем тестового пользователя
        user = new User(getRandomUserEmail(), "qwerty12345", "Иванов Иван");

        createNewUserAndSetTokens(user);

        //Создание тестового заказа
        order = formingOrderFromIngredients(2);

        createNewOrderAndSetId(order, user);

    }

    @Test
    @DisplayName("Получение заказов пользователя с авторизацией")
    public void getUserOrdersWithAuthorization() {

        checkSuccessGetUserOrdersWithAuthorization(user);

    }

    @Test
    @DisplayName("Получение заказов пользователя без авторизации")
    public void getUserOrdersWithoutAuthorization() {

        checkErrorWhenGetUserOrdersWithoutAuthorization();

    }

    @After
    public void cleanUp() {

        if (user != null && user.getAccessToken() != null) {

            deleteUser(user);
            user = null;

        }

    }

}
