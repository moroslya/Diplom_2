package site.nomoreparties.stellarburgers.steps;

import io.qameta.allure.Step;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import site.nomoreparties.stellarburgers.constants.Constants;
import site.nomoreparties.stellarburgers.data.User;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class UserSteps {

    @Step("Проверка успешного создания нового пользователя")
    public void checkSuccessCreateNewUser(User user) {

        Response response = sendRequestCreateUser(user);

        response.then().statusCode(200)
                .and()
                .assertThat().body("success", equalTo(true))
                .and()
                .assertThat().body("accessToken", startsWith("Bearer "))
                .and()
                .assertThat().body("refreshToken", notNullValue())
                .and()
                .assertThat().body("user", notNullValue());

        user.setAccessToken(response.jsonPath().get("accessToken"));
        user.setToken(response.jsonPath().get("refreshToken"));

    }

    @Step("Проверка ошибки при создании пользователя с уже зарегистрированным email")
    public void checkErrorWhenCreatingUserWithExistingEmail(User user) {

        Response response = sendRequestCreateUser(user);

        response.then().statusCode(403)
                .and()
                .assertThat().body("success", equalTo(false))
                .and()
                .assertThat().body("message", equalTo("User already exists"));

    }

    @Step("Проверка ошибки при создании нового пользователя с незаполненными обязательными полями")
    public void checkErrorWhenCreatingUserNoRequiredFields(String email, String password, String name) {

        User user = new User(email, password, name);

        Response response = sendRequestCreateUser(user);

        response.then().statusCode(403)
                .and()
                .assertThat().body("success", equalTo(false))
                .and()
                .assertThat().body("message", equalTo("Email, password and name are required fields"));

    }

    @Step("Проверка успешной авторизации под существующим пользователем")
    public void checkSuccessAuthorizationExistingUser(User user) {

        Response response = sendRequestAuthorizationUser(user);

        response.then().statusCode(200)
                .and()
                .assertThat().body("success", equalTo(true))
                .and()
                .assertThat().body("accessToken", startsWith("Bearer "))
                .and()
                .assertThat().body("refreshToken", notNullValue())
                .and()
                .assertThat().body("user", notNullValue());

        user.setAccessToken(response.jsonPath().get("accessToken"));
        user.setToken(response.jsonPath().get("refreshToken"));

    }

    @Step("Проверка ошибки при авторизации с неверным учетными данными")
    public void checkErrorWhenAuthorizationWithIncorrectData(User user) {

        Response response = sendRequestAuthorizationUser(user);

        response.then().statusCode(401)
                .and()
                .assertThat().body("success", equalTo(false))
                .and()
                .assertThat().body("message", equalTo("email or password are incorrect"));

    }

    @Step("Проверка успешного изменения данных пользователя (с авторизацией)")
    public void checkSuccessUserDataUpdateWithAuthorization(User user) {

        Response response = sendRequestUserDataUpdateWithAuthorization(user, true);

        response.then().statusCode(200)
                .and()
                .assertThat().body("success", equalTo(true))
                .and()
                .assertThat().body("user", notNullValue());

    }

    @Step("Проверка корректности информации о пользователе")
    public void checkCorrectnessUserInformation(User user) {

        Response response = sendRequestToGetUserInformation(user);

        response.then().statusCode(200)
                .and()
                .assertThat().body("success", equalTo(true))
                .and()
                .assertThat().body("user.email", equalTo(user.getEmail()))
                .and()
                .assertThat().body("user.name", equalTo(user.getName()));

    }

    @Step("Проверка ошибки при попытке изменения данных пользователя без авторизации")
    public void checkErrorWhenUserDataUpdateWithoutAuthorization(User user) {

        Response response = sendRequestUserDataUpdateWithAuthorization(user, false);

        response.then().statusCode(401)
                .and()
                .assertThat().body("success", equalTo(false))
                .and()
                .assertThat().body("message", equalTo("You should be authorised"));

    }

    public String getRandomUserEmail() {

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        return "user2022_" + timestamp.getTime() + "@yandex.ru";

    }

    public void createNewUserAndSetTokens(User user) {

        Response response = sendRequestCreateUser(user);

        user.setAccessToken(response.jsonPath().get("accessToken"));
        user.setToken(response.jsonPath().get("refreshToken"));

    }

    public void deleteUser(User user) {

        Response response = sendRequestDeleteUser(user.getAccessToken());

        if (response.getStatusCode() == 403
                && response.jsonPath().getString("message").equals("jwt expired")) {

            String newAccessToken = sendRequestRefreshAccessToken(user);
            sendRequestDeleteUser(newAccessToken);

        }

    }

    public Response sendRequestCreateUser(User user) {

        return given()
                .header("Content-type", "application/json")
                .and()
                .body(user)
                .post(Constants.API_REGISTER_USER);

    }

    public Response sendRequestDeleteUser(String accessToken) {

        return given()
                .header("Authorization", accessToken)
                .delete(Constants.API_WORK_WITH_USER_DATA);

    }

    public String sendRequestRefreshAccessToken(User user) {

        return given()
                .header("Content-type", "application/json")
                .and()
                .body(user)
                .post(Constants.API_REFRESH_TOCKEN)
                .jsonPath().getString("accessToken");

    }

    public Response sendRequestAuthorizationUser(User user) {

        return given()
                .header("Content-type", "application/json")
                .and()
                .body(user)
                .post(Constants.API_LOGIN_USER);

    }

    public Response sendRequestUserDataUpdateWithAuthorization(User user, boolean withAuthorization) {

        List<Header> headerList = new ArrayList<Header>();
        headerList.add(new Header("Content-type", "application/json"));

        if (withAuthorization) {

            headerList.add(new Header("Authorization", user.getAccessToken()));

        }

        Headers headers = new Headers(headerList);

        return given()
                .headers(headers)
                .and()
                .body(user)
                .patch(Constants.API_WORK_WITH_USER_DATA);

    }

    public Response sendRequestToGetUserInformation(User user) {

        return given()
                .header("Authorization", user.getAccessToken())
                .get(Constants.API_WORK_WITH_USER_DATA);

    }

}
