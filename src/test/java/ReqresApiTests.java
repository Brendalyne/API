import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.*;
import static io.restassured.RestAssured.*;

public class ReqresApiTests {

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "https://reqres.in";
    }

    @Test
    public void testGetUsers_StatusAndList() {
        given()
                .header("x-api-key", "reqres-free-v1")
                .when()
                .get("/api/users?page=1")
                .then()
                .statusCode(200)
                .body("data", not(empty()))
                .body("page", equalTo(1));
    }

    @Test
    public void testGetUsers_CountUsersOnPage() {
        Response response =
                given()
                        .header("x-api-key", "reqres-free-v1")
                        .when()
                        .get("/api/users?page=1")
                        .then()
                        .statusCode(200)
                        .extract().response();

        int userCount = response.path("data.size()");
        System.out.println("User count on page 1: " + userCount);
    }

    @Test
    public void testGetUsers_FullList_ExtractSpecificId() {
        Response response =
                given()
                        .header("x-api-key", "reqres-free-v1")
                        .queryParam("page", 1)
                        .queryParam("per_page", 12)
                        .when()
                        .get("/api/users")
                        .then()
                        .statusCode(200)
                        .extract().response();

        Integer userId = response.jsonPath().getInt("data.find { it.email == 'charles.morris@reqres.in' }.id");
        System.out.println("User ID for charles.morris@reqres.in: " + userId);

        assert userId != null : "User with email charles.morris@reqres.in not found!";
    }

    @Test
    public void testPost_CreateUser() {
        String name = "John Doe";
        String job = "Tester";

        given()
                .header("x-api-key", "reqres-free-v1")
                .contentType(ContentType.JSON)
                .body("{ \"name\": \"" + name + "\", \"job\": \"" + job + "\" }")
                .when()
                .post("/api/users")
                .then()
                .statusCode(201)
                .body("name", equalTo(name))
                .body("job", equalTo(job))
                .body("id", notNullValue())
                .body("createdAt", notNullValue());
    }

    @Test
    public void testPut_UpdateUser() {
        String updatedJob = "Senior QA Engineer";

        given()
                .header("x-api-key", "reqres-free-v1")
                .contentType(ContentType.JSON)
                .body("{ \"job\": \"" + updatedJob + "\" }")
                .when()
                .put("/api/users/2")
                .then()
                .statusCode(200)
                .body("job", equalTo(updatedJob))
                .body("updatedAt", notNullValue());
    }

    @Test
    public void testDeleteUser() {
        given()
                .header("x-api-key", "reqres-free-v1")
                .when()
                .delete("/api/users/2")
                .then()
                .statusCode(204);
    }
}
