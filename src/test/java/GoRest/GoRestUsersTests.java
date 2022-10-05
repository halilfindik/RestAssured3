package GoRest;

import io.restassured.http.ContentType;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.*;

public class GoRestUsersTests {

    @Test
    public void createUser() {

        given()
                // api methoduna gitmeden önceki hazırlıklar: token, gidecek body, parametreler

                .when()
                .post("https://gorest.co.in/public/v2/users")

                .then()
                .log().body()
                .statusCode(201)
                .contentType(ContentType.JSON)
        ;

    }

}
