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

import java.util.List;

public class CreateOrderTest extends Steps {

    private User user;
    private Order order;

    @Before
    public void setUp() {

        RestAssured.baseURI = Constants.URL;

        //Создаем тестового пользователя
        user = new User(getRandomUserEmail(), "qwerty12345", "Иванов Иван");

        createNewUserAndSetTokens(user);

    }

    @Test
    @DisplayName("Создание нового заказа с ингредиентами")
    public void createNewOrderWithIngredients() {

        //Создаем заказ с ингредиентами
        order = formingOrderFromIngredients(2);

        checkSuccessCreateNewOrder(order, user);

        //Проверяем, что созданный заказ существует в списке заказов пользователя
        checkExistenceOrderInUserOrders(sendRequestGetUserOrders(user), order);

    }

    @Test
    @DisplayName("Создание нового заказа без ингредиентов")
    public void createNewOrderWithoutIngredients() {

        //Создаем заказ без ингредиентов
        order = formingOrderFromIngredients(0);

        checkErrorWhenCreatingOrderWithoutIngredients(order, user);

        //Проверяем, что список заказов пользователя пуст
        checkThatUserHasNoOrders(user);

    }

    @Test
    @DisplayName("Создание нового заказа с неверным хешем ингредиентов")
    public void createNewOrderWithIncorrectHashOfIngredients() {

        //Создаем заказ с неверным хешем ингредиентов
        order = formingOrderFromIngredients(2);

        List<String> incorrectIngredients = order.getIngredients();
        incorrectIngredients.set(0, incorrectIngredients.get(0) + "1");

        checkErrorWhenCreatingOrderIncorrectHashOfIngredients(order, user);

        //Проверяем, что список заказов пользователя пуст
        checkThatUserHasNoOrders(user);

    }

    @Test
    @DisplayName("Создание нового заказа без авторизации")
    public void createNewOrderWithoutAuthorization() {

        order = formingOrderFromIngredients(2);

        checkNotReturnIdAndIngredientsWhenCreatingOrderWithoutAuthorization(order);

    }

    @After
    public void cleanUp() {

        if (user != null && user.getAccessToken() != null) {

            deleteUser(user);
            user = null;

        }

    }

}
