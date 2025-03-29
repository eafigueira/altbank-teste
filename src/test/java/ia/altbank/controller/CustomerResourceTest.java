package ia.altbank.controller;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
class CustomerResourceTest {

    @Test
    void testCreateCustomer() {
        String customerJson = """
                {
                  "name": "Altbank",
                  "documentNumber": "12345678901",
                  "email": "altbank@example.com",
                  "address": {
                    "city": "São Paulo",
                    "complement": "Apto 101",
                    "neighborhood": "Centro",
                    "number": "100",
                    "state": "SP",
                    "street": "Av. Paulista",
                    "zipCode": "01310-000"
                  }
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(customerJson)
                .when()
                .post("/customers")
                .then()
                .statusCode(201)
                .body("customerId", notNullValue())
                .body("accountId", notNullValue())
                .body("cardId", notNullValue());
    }

    @Test
    void testCreateCustomerInvalid() {
        String customerJson = """
                {
                  "name": "Altbank",
                  "documentNumber": "12345678901",
                  "address": {
                    "city": "São Paulo",
                    "complement": "Apto 101",
                    "neighborhood": "Centro",
                    "number": "100",
                    "state": "SP",
                    "street": "Av. Paulista",
                    "zipCode": "01310-000"
                  }
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(customerJson)
                .when()
                .post("/customers")
                .then()
                .statusCode(400);
    }

    @Test
    void testUpdateCustomer() {
        UUID customerId = createCustomer();

        String updateJson = """
                {
                  "name": "Altbank Updated"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(updateJson)
                .when()
                .put("/customers/{id}", customerId)
                .then()
                .statusCode(204);
    }

    @Test
    void testDeleteCustomer() {
        UUID customerId = createCustomer();

        given()
                .when()
                .delete("/customers/{id}", customerId)
                .then()
                .statusCode(204);
    }

    @Test
    void testDeleteCustomerNotFound() {
        UUID customerId = UUID.randomUUID();

        given()
                .when()
                .get("/customers/{id}", customerId)
                .then()
                .statusCode(404);
    }

    @Test
    void testFindCustomerById() {
        UUID customerId = createCustomer();

        given()
                .when()
                .get("/customers/{id}", customerId)
                .then()
                .statusCode(200)
                .body("id", is(customerId.toString()))
                .body("name", is("Altbank"))
                .body("email", is("altbank@example.com"));
    }

    private UUID createCustomer() {
        String customerJson = """
                {
                  "name": "Altbank",
                  "documentNumber": "12345678901",
                  "email": "altbank@example.com",
                  "address": {
                    "city": "São Paulo",
                    "complement": "Apto 101",
                    "neighborhood": "Centro",
                    "number": "100",
                    "state": "SP",
                    "street": "Av. Paulista",
                    "zipCode": "01310-000"
                  }
                }
                """;

        return UUID.fromString(
                given()
                        .contentType(ContentType.JSON)
                        .body(customerJson)
                        .when()
                        .post("/customers")
                        .then()
                        .statusCode(201)
                        .extract()
                        .path("customerId")
        );
    }
}

