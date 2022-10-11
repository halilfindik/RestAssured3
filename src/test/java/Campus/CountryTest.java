package Campus;

import Campus.Model.Country;
import io.restassured.http.ContentType;
import io.restassured.http.Cookies;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.*;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class CountryTest {

    Cookies cookies;

    @BeforeClass
    public void loginCampus() {
        baseURI = "https://demo.mersys.io/";

        // {"username": "richfield.edu","password": "Richfield2020!","rememberMe": "true"}
        // bu login işlemindeki elemanları sadece bir kere kullancağım için Map ile göndereceğim / Map kullancağım
        // eğer pek çok kez kullacanak olsaydım Object olarak tanımlar ve kullanırdım, hata olasılığı da sıfırlanmış
        // olurdu. Map ile yaparken çünkü elle yazıyoruz.

        Map<String, String> credential = new HashMap<>();
        credential.put("username","richfield.edu");
        credential.put("password","Richfield2020!");
        credential.put("rememberMe","true");

        cookies =
        given()
                .contentType(ContentType.JSON)
                .body(credential)

                .when()
                .post("auth/login")

                .then()
                //.log().all()
                //.log().body()
                .statusCode(200)
                .extract().response().getDetailedCookies()
                ;
    }

    public String getRandomName() {
        return RandomStringUtils.randomAlphabetic(8).toLowerCase();
    }
    public String getRandomCode() {
        return RandomStringUtils.randomAlphabetic(3).toLowerCase();
    }

    String countryID;
    String countryName=getRandomName();
    String countryCode=getRandomCode();

    @Test
    public void createCountry() {

        Country country=new Country();
        country.setName(countryName); // generateCountryName
        country.setCode(countryCode); // generateCountryCode

        countryID =
        given()
                .cookies(cookies)
                .contentType(ContentType.JSON)
                .body(country)
                .when()
                .post("school-service/api/countries")

                .then()
                .log().body()
                .statusCode(201)
                .extract().jsonPath().getString("id")
        ;
    }

    @Test(dependsOnMethods = "createCountry")
    public void createCountryNegative() {

        Country countryDuplicate=new Country();
        countryDuplicate.setName(countryName); // generateCountryName
        countryDuplicate.setCode(countryCode); // generateCountryCode

                given()
                        .cookies(cookies)
                        .contentType(ContentType.JSON)
                        .body(countryDuplicate)
                        .when()
                        .post("school-service/api/countries")

                        .then()
                        .log().body()
                        .statusCode(400)
                        .body("message",equalTo("The Country with Name \""+countryName+"\" already exists."))
        ;
    }
    @Test(dependsOnMethods = "createCountry")
    public void updateCountry() {

        countryName=getRandomName();

        Country countryUpdate=new Country();
        countryUpdate.setId(countryID);
        countryUpdate.setName(countryName); // generateCountryName
        countryUpdate.setCode(countryCode); // generateCountryCode

        given()
                .cookies(cookies)
                .contentType(ContentType.JSON)
                .body(countryUpdate)
                .when()
                .put("school-service/api/countries")

                .then()
                .log().body()
                .statusCode(200)
                .body("name",equalTo(countryName))
        ;
    }
    @Test(dependsOnMethods = "updateCountry")
    public void deleteCountryByID() {

        given()
                .cookies(cookies)
                .pathParam("countryID",countryID)
                .when()
                .delete("school-service/api/countries/{countryID}")

                .then()
                .log().body()
                .statusCode(200)
        ;
    }

    @Test(dependsOnMethods = "updateCountryNegative")
    public void deleteCountryByIDNegative() {

        given()
                .cookies(cookies)
                .pathParam("countryID",countryID)
                .when()
                .delete("school-service/api/countries/{countryID}")

                .then()
                .log().body()
                .statusCode(400)
        ;
    }
    @Test(dependsOnMethods = "deleteCountryByID")
    public void updateCountryNegative() {

        countryName=getRandomName();

        Country countryUpdate=new Country();
        countryUpdate.setId(countryID);
        countryUpdate.setName(countryName);
        countryUpdate.setCode(countryCode);

        given()
                .cookies(cookies)
                .contentType(ContentType.JSON)
                .body(countryUpdate)

                .when()
                .put("school-service/api/countries")

                .then()
                .log().body()
                .statusCode(400)
                .body("message",equalTo("Country not found"))
        ;
    }
}