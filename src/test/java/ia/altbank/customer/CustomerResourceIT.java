package ia.altbank.customer;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
class CustomerResourceIT {

    @Test
    void shouldReturn404WhenCustomerNotFoundById() {
        given()
                .pathParam("id", UUID.randomUUID())
                .when()
                .get("/customers/{id}")
                .then()
                .statusCode(404)
                .body("message", is("Resource not found"));
    }

    @Test
    void shouldCreateCustomerSuccessfully() {
        var request = """
        {
          "name": "Fulano da Silva",
          "email": "fulano@email.com",
          "documentNumber": "12345678900",
          "address": {
            "street": "Rua A",
            "number": "123",
            "neighborhood": "Centro",
            "city": "Cidade",
            "state": "SP",
            "zipCode": "12345-678",
            "complement": "Apto 1"
          }
        }
        """;

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/customers")
                .then()
                .statusCode(201)
                .body("customerId", notNullValue())
                .body("accountId", notNullValue())
                .body("cardId", notNullValue())
                .body("status", is("ACTIVE"))
                .body("address.city", is("Cidade"));
    }

    @Test
    void shouldReturn400WhenCreatingCustomerWithInvalidData() {
        var request = """
        {
          "name": "",
          "email": "not-an-email",
          "documentNumber": "",
          "address": {}
        }
        """;

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/customers")
                .then()
                .statusCode(400);
    }

    @Test
    void shouldReturnListOfCustomers() {
        given()
                .queryParam("page", 0)
                .queryParam("size", 5)
                .when()
                .get("/customers")
                .then()
                .statusCode(200)
                .body("$", isA(List.class));
    }
}
