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

import java.util.List;

public class CreateOrderTest {

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

    }

    @Test
    @DisplayName("Создание нового заказа с ингредиентами")
    public void createNewOrderWithIngredients() {

        //Создаем заказ с ингредиентами
        order = orderSteps.formingOrderFromIngredients(2);

        orderSteps.checkSuccessCreateNewOrder(order, user);

        //Проверяем, что созданный заказ существует в списке заказов пользователя
        orderSteps.checkExistenceOrderInUserOrders(orderSteps.sendRequestGetUserOrders(user), order);

    }

    @Test
    @DisplayName("Создание нового заказа без ингредиентов")
    public void createNewOrderWithoutIngredients() {

        //Создаем заказ без ингредиентов
        order = orderSteps.formingOrderFromIngredients(0);

        orderSteps.checkErrorWhenCreatingOrderWithoutIngredients(order, user);

        //Проверяем, что список заказов пользователя пуст
        orderSteps.checkThatUserHasNoOrders(user);

    }

    @Test
    @DisplayName("Создание нового заказа с неверным хешем ингредиентов")
    public void createNewOrderWithIncorrectHashOfIngredients() {

        //Создаем заказ с неверным хешем ингредиентов
        order = orderSteps.formingOrderFromIngredients(2);

        List<String> incorrectIngredients = order.getIngredients();
        incorrectIngredients.set(0, incorrectIngredients.get(0) + "1");

        orderSteps.checkErrorWhenCreatingOrderIncorrectHashOfIngredients(order, user);

        //Проверяем, что список заказов пользователя пуст
        orderSteps.checkThatUserHasNoOrders(user);

    }

    @Test
    @DisplayName("Создание нового заказа без авторизации")
    public void createNewOrderWithoutAuthorization() {

        order = orderSteps.formingOrderFromIngredients(2);

        orderSteps.checkNotReturnIdAndIngredientsWhenCreatingOrderWithoutAuthorization(order);

    }

    @After
    public void cleanUp() {

        if (user != null && user.getAccessToken() != null) {

            userSteps.deleteUser(user);
            user = null;

        }

    }

}
