package site.nomoreparties.stellarburgers;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import site.nomoreparties.stellarburgers.data.Order;
import site.nomoreparties.stellarburgers.data.User;
import site.nomoreparties.stellarburgers.constants.Constants;
import site.nomoreparties.stellarburgers.steps.*;

public class GetUserOrdersTest {

    UserSteps userSteps = new UserSteps();
    OrderSteps orderSteps = new OrderSteps();

    private User user;
    private Order order;

    @Before
    public void setUp() {

        RestAssured.baseURI = Constants.URL;

        //Создаем тестового пользователя
        user = new User(userSteps.getRandomUserEmail(), "qwerty12345", "Иванов Иван");

        userSteps.createNewUserAndSetTokens(user);

        //Создание тестового заказа
        order = orderSteps.formingOrderFromIngredients(2);

        orderSteps.createNewOrderAndSetId(order, user);

    }

    @Test
    @DisplayName("Получение заказов пользователя с авторизацией")
    public void getUserOrdersWithAuthorization() {

        orderSteps.checkSuccessGetUserOrdersWithAuthorization(user);

    }

    @Test
    @DisplayName("Получение заказов пользователя без авторизации")
    public void getUserOrdersWithoutAuthorization() {

        orderSteps.checkErrorWhenGetUserOrdersWithoutAuthorization();

    }

    @After
    public void cleanUp() {

        if (user != null && user.getAccessToken() != null) {

            userSteps.deleteUser(user);
            user = null;

        }

    }

}
