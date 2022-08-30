package site.nomoreparties.stellarburgers.steps;

import io.qameta.allure.Step;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import site.nomoreparties.stellarburgers.constants.Constants;
import site.nomoreparties.stellarburgers.data.Order;
import site.nomoreparties.stellarburgers.data.User;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;

public class OrderSteps {

    @Step("Проверка успешного создания заказа с ингредиентами")
    public void checkSuccessCreateNewOrder(Order order, User user) {

        Response response = sendRequestCreateOrder(order, user);

        response
                .then().statusCode(200)
                .and()
                .assertThat().body("success", equalTo(true))
                .and()
                .assertThat().body("order", notNullValue())
                .and()
                .assertThat().body("order._id", notNullValue())
                .and()
                .assertThat().body("order.ingredients._id", equalTo(order.getIngredients()));

        order.setId(response.jsonPath().getString("order._id"));

    }

    @Step("Проверка ошибки при создании заказа без ингредиентов")
    public void checkErrorWhenCreatingOrderWithoutIngredients(Order order, User user) {

        Response response = sendRequestCreateOrder(order, user);

        response.then().statusCode(400)
                .and()
                .assertThat().body("success", equalTo(false))
                .and()
                .assertThat().body("message", equalTo("Ingredient ids must be provided"));

    }

    @Step("Проверка ошибки при создании заказа с неверным хешем ингредиентов")
    public void checkErrorWhenCreatingOrderIncorrectHashOfIngredients(Order order, User user) {

        Response response = sendRequestCreateOrder(order, user);

        response.then().statusCode(500);

    }

    @Step("Проверка отсутствия ID созданного заказа в ответе при создании без авторизации")
    public void checkNotReturnIdAndIngredientsWhenCreatingOrderWithoutAuthorization(Order order) {

        Response response = sendRequestCreateOrder(order, null);

        response.then().statusCode(200)
                .and()
                .assertThat().body("success", equalTo(true))
                .and()
                .assertThat().body("order", notNullValue())
                .and()
                .assertThat().body("order.Id", nullValue())
                .and()
                .assertThat().body("order.ingredients", nullValue());

    }

    @Step("Проверка существования созданного заказа в списке заказов пользователя")
    public void checkExistenceOrderInUserOrders(Response response, Order order) {

        response.then()
                .assertThat().body("orders._id", hasItem(order.getId()));

    }

    @Step("Проверка, что список заказов пользователя пуст")
    public void checkThatUserHasNoOrders(User user) {

        Response response = sendRequestGetUserOrders(user);

        response.then().statusCode(200)
                .and()
                .assertThat().body("success", equalTo(true))
                .and()
                .assertThat().body("orders", hasSize(0));

    }

    @Step("Проверка успешного получения списка заказов пользователя (с авторизацией)")
    public void checkSuccessGetUserOrdersWithAuthorization(User user) {

        Response response = sendRequestGetUserOrders(user);

        response.then().statusCode(200)
                .and()
                .assertThat().body("success", equalTo(true))
                .and()
                .assertThat().body("orders", notNullValue());

    }

    @Step("Проверка ошибки при запросе списка заказов без авторизации")
    public void checkErrorWhenGetUserOrdersWithoutAuthorization() {

        Response response = sendRequestGetUserOrders(null);

        response.then().statusCode(401)
                .and()
                .assertThat().body("success", equalTo(false))
                .and()
                .assertThat().body("message", equalTo("You should be authorised"));

    }

    public Order formingOrderFromIngredients(int countIngredients) {

        List<String> ingredients = getListOfIngredients(countIngredients);

        return new Order(ingredients);

    }

    public void createNewOrderAndSetId(Order order, User user) {

        Response response = sendRequestCreateOrder(order, user);

        order.setId(response.jsonPath().getString("order._id"));

    }

    public List<String> getListOfIngredients(int countIngredients) {

        List<String> listOfIngredients = new ArrayList<String>();

        if (countIngredients > 0) {

            Response response = given()
                    .get(Constants.API_WORK_WITH_INGREDIENTS);

            for (int i = 0; i < countIngredients; i++) {

                listOfIngredients.add(response.jsonPath().getMap("data[" + i + "]").get("_id").toString());

            }

        }

        return listOfIngredients;

    }

    public Response sendRequestCreateOrder(Order order, User user) {

        List<Header> headerList = new ArrayList<Header>();
        headerList.add(new Header("Content-type", "application/json"));

        if (user != null) {

            headerList.add(new Header("Authorization", user.getAccessToken()));

        }

        Headers headers = new Headers(headerList);

        return given()
                .headers(headers)
                .and()
                .body(order)
                .post(Constants.API_WORK_WITH_ORDERS);

    }

    public Response sendRequestGetUserOrders(User user) {

        List<Header> headerList = new ArrayList<Header>();

        if (user != null) {

            headerList.add(new Header("Authorization", user.getAccessToken()));

        }

        Headers headers = new Headers(headerList);

        return given()
                .headers(headers)
                .get(Constants.API_WORK_WITH_ORDERS);

    }

}
