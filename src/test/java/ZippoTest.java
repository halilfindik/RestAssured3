import POJO.Location;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.hamcrest.Matchers;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class ZippoTest {

    @Test
    public void test() {

        given().
                        // hazırlık işlemlerini yapacağız (token, send body, parametreler)
                when().
                        // linki ve metodu veriyoruz

                then()
                        // test(assertion) ve verileri ele alma (extract)
        ;


    }
    @Test
    public void statusCodeTest() {
        given()
                .when()
                .get("http://api.zippopotam.us/us/90210")
                .then()
                .log().body()  // log().all bütün response'u gösterir
                .statusCode(200);
        // test(assertion) ve verileri ele alma (extract)
    }
    @Test
    public void contentTypeTest() {
        given()
                .when()
                .get("http://api.zippopotam.us/us/90210")
                .then()
                .log().body()  // log().all bütün response'u gösterir
                .statusCode(200)
                .contentType(ContentType.JSON);
        // test(assertion) ve verileri ele alma (extract)
    }

    @Test
    public void checkStateInResponseBodyTest() {
        String postCode="90210";
        given()
                .when()
                .get("http://api.zippopotam.us/us/"+postCode)
                .then()
                .log().body()
                .body("country", equalTo("United States")) // body içine verilen string equalTo içindekine eşit mi? assertion test
                .statusCode(200);
    }
    @Test
    public void bodyJsonPathTest2() {
        String postCode="90210";
        given()
                .when()
                .get("http://api.zippopotam.us/us/"+postCode)
                .then()
                .log().body()
                .body("places[0].state", equalTo("California")) // body.places[0].state içine verilen string equalTo içindekine eşit mi? assertion test
                .statusCode(200);
    }
    @Test
    public void bodyJsonPathTest3() {
        String postCode="tr/01000";
        given()
                .when()
                .get("http://api.zippopotam.us/"+postCode)
                .then()
                .log().body()
                .body("places.'place name'", hasItem("Çaputçu Köyü")) // body.places[0].state içine verilen string equalTo içindekine eşit mi? assertion test
                .statusCode(200);
    }
    @Test
    public void bodyArrayHasSizeTest() {
        String postCode="90210";
        given()
                .when()
                .get("http://api.zippopotam.us/us/"+postCode)
                .then()
                .log().body()
                .body("places", hasSize(1))
                .statusCode(200);
    }
    @Test
    public void combiningTest() {
        String postCode="90210";
        given()
                .when()
                .get("http://api.zippopotam.us/us/"+postCode)
                .then()
                .log().body()
                .body("places", hasSize(1)) // verilen path'deki listin size kontrolü
                .body("places.state", hasItem("California"))
                .body("places[0].'place name'", equalTo("Beverly Hills"))
                .statusCode(200);
    }
    @Test
    public void pathParamTest() {

        given()
                .pathParam("Country","us")
                .pathParam("ZipCode","90210")
                .log().uri()

                .when()
                .get("http://api.zippopotam.us/{Country}/{ZipCode}")
                .then()
                .log().body()
                .statusCode(200);
    }
    @Test
    public void pathParamTest2() {
        // 90210'dan 90250'ye kadar test sonuçlarında places'ın size'nın hepsinde 1 geldiğini test ediniz

        for (int i=90210; i<90260; i+=10) {

            given()
                    .pathParam("Country", "us")
                    .pathParam("ZipCode", i)
                    .log().uri()

                    .when()
                    .get("http://api.zippopotam.us/{Country}/{ZipCode}")
                    .then()
                    .log().body()
                    .body("places", hasSize(1))
                    .statusCode(200);
        }
    }
    @Test
    public void queryParamTest() {
        // https://gorest.co.in/public/v1/users?page=1
        // get ile vermiş olduğum linkin sonuna parametre olarak verdiğim "page" ile
        // başına "?" ve sonuna da "=" koyarak son olarak da ikinci parametre olan ve döngü içinde
        // verdiğim sayıları sırasıyla koyarak farklı sayfalarda çalıştırmış oluyorum

            for (int j=1; j<=10; j++) {
                given()
                        .param("page", j)
                        .log().uri() // request linkii yazdırmış oluyoruz bununla

                        .when()
                        .get("https://gorest.co.in/public/v1/users")

                        .then()
                        .log().body()
                        .body("meta.pagination.page", equalTo(j))
                        .statusCode(200);
            }
    }

    RequestSpecification requestSpecs;
    ResponseSpecification responseSpecs;

    @BeforeClass
    void Setup() {

        // RestAssured kendi statik değişkeni tanımlı değer atanıyor
        // get içinde http görürse sıkıntı yok, görmezse bunu başına ekliyor
        baseURI="https://gorest.co.in/public/v1";

        requestSpecs = new RequestSpecBuilder()
                .log(LogDetail.URI)
                .setAccept(ContentType.JSON)
                .build();

        responseSpecs = new ResponseSpecBuilder()
                .expectStatusCode(200)
                .expectContentType(ContentType.JSON)
                .log(LogDetail.BODY)
                .build();
    }

    @Test
    public void requestResponseSpecification() {

        given()
                .param("page", 1)
                .spec(requestSpecs)

                .when()
                .get("/users")

                .then()
                .log().body()
                .body("meta.pagination.page", equalTo(1))
                .spec(responseSpecs);
    }

    @Test
    public void extractingJsonPath() {

        String placeName=
        given()
                .when()
                .get("http://api.zippopotam.us/us/90210")

                .then()
                .log().body()
                .statusCode(200)
                .extract().path("places[0].'place name'");
                // extract metodu ile given ile başlayan satır, bir değere döndürür gale geldi
                // en sonunda extract olmalı
        System.out.println("placeName = " + placeName);
    }

    // Ders: 03.10.2022 Pazartesi
    @Test
    public void extractingJsonPathInt() {

        int limit =
        given()

                .when()
                .get("https://gorest.co.in/public/v1/users")

                .then()
                .log().body()
                .statusCode(200)
                .extract().path("meta.pagination.limit");

        System.out.println("limit = " + limit);
        Assert.assertEquals(limit,10,"test result");
    }
    @Test
    public void extractingJsonPathInt2() {

        int id =
                given()

                        .when()
                        .get("https://gorest.co.in/public/v1/users")

                        .then()
                        //.log().body()
                        .statusCode(200)
                        .extract().path("data[2].id");

        System.out.println("id = " + id);
    }
    @Test
    public void extractingJsonPathIntList() {

        List<Integer> ids=
                given()

                        .when()
                        .get("https://gorest.co.in/public/v1/users")

                        .then()
                        //.log().body()
                        .statusCode(200)
                        .extract().path("data.id"); // data'daki tüm id'leri bir list şeklinde verir

        System.out.println("ids = " + ids);
        Assert.assertTrue(ids.contains(3045));
    }
    @Test
    public void extractingJsonPathStringList() {

        List<String> names=
                given()

                        .when()
                        .get("https://gorest.co.in/public/v1/users")

                        .then()
                        //.log().body()
                        .statusCode(200)
                        .extract().path("data.name"); // data'daki tüm name'leri bir list şeklinde verir

        System.out.println("names = " + names);
        Assert.assertTrue(names.contains("Vimala Adiga"));
    }
    @Test
    public void extractingJsonPathResponseAll() {

        Response response=
                given()

                        .when()
                        .get("https://gorest.co.in/public/v1/users")

                        .then()
                        //.log().body()
                        .statusCode(200)
                        .extract().response();

        List<Integer> ids=response.path("data.id");
        List<String> names=response.path("data.name");
        int limit=response.path("meta.pagination.limit");

        System.out.println("ids = " + ids);
        System.out.println("names = " + names);
        System.out.println("limit = " + limit);

    }

    @Test
    public void extractingJsonPOJO() { // POJO Json Object'i (Plain Old....)
        Location yer =
        given()
                .when()
                .get("http://api.zippopotam.us/us/90210")

                .then()
                .extract().as(Location.class); // Location
        System.out.println("yer = " + yer);

        System.out.println("yer.getCountry() = " + yer.getCountry());
        System.out.println("yer.getPlaces().get(0).getPlaceName() = " + yer.getPlaces().get(0).getPlaceName());
    }


}
// Genel kullanılanlar
//
//equalTo(X) - used to check whether an actual element value is equal to a pre-specified expected element value
//hasItem("value") - used to see whether a collection of elements contains a specific pre-specified item value
//hasSize(3) - used to verify the actual number of elements in a collection
//not(equalTo(X)) - inverts any given matcher that exists in the Hamcrest
//
//Number related assertions
//
//equalTo – It checks whether the retrieved number from response is equal to the expected number.
//greaterThan – checks extracted number is greater than the expected number.
//greaterThanOrEqualTo – checks whether the extracted number is greater than equal to the expected number.
//lessThan – It checks whether the retrieved number from response is lesser than to the expected number.
//lessThanOrEqualTo – It checks whether the retrieved number from response is lesser than or equal to the expected number.
//
//String related Assertions
//
//equalTo – It checks whether the extracted string from JSON is equal to the expected string.
//equalToIgnoringCase – It checks whether the extracted string from JSON is equal to the expected string without considering the case (small or capital).
//equalToIgnoringWhiteSpace – It checks whether the extracted string from JSON is equal to the expected string by considering the white spaces.
//containsString – It checks whether the extracted string from JSON contains the expected string as a substring.
//startsWith – It checks whether the extracted string from JSON is starting with a given string or character.
//endsWith – It checks whether the extracted string from JSON is ending with a given string or character.


// 1.yöntem
//http://api.zippopotam.us/  -> Api linki
//     / us / 90210 -> parametre

//2.Yöntem
///public/v2/users
//      ?page=1    -> parametre

//search
// ?
// q=selenium
// &sxsrf=ALiCzsatAuurS8Rt1FONk40zHXjJjna6tg%3A1664472470642
// &source=hp

//https://sonuc.osym.gov.tr/Sorgu.aspx
//     ?
//     SonucID=9504

//metod(int sayi1, int sayi2)
//Zippopotamus- Zip Code Galore