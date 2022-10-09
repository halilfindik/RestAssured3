package GoRest;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class GoRestUsersTests {

    @BeforeClass
    void Setup() {
        baseURI="https://gorest.co.in/public/v2/";
    }

    @Test
    public void createUserObject() {

        newUser=new User();
        newUser.setName(getRandomName());
        newUser.setGender("male");
        newUser.setEmail(getRandomEmail());
        newUser.setStatus("active");

        userID =
                given()
                        // api methoduna gitmeden önceki hazırlıklar: token, gidecek body, parametreler

                        .header("Authorization","Bearer 0f1c97269e7247ab490749691952b1a13841da6275be02e5e1980549d64c30db")
                        .contentType(ContentType.JSON)
                        .body(newUser)
                        .log().body()

                        .when()
                        .post("users")

                        .then()
                        .log().body()
                        .statusCode(201)
                        .contentType(ContentType.JSON)
                        //.extract().path("id") // extract path yerine ikinci bir yöntem daha var
                        .extract().jsonPath().getInt("id")
                ;
        // path: class veya tip dönüşümüne imkan vermeyen direk veriyi verir. List<String> gibi
        // jsonPath class dönüşümüne ve tip dönüşümüne izin vererek, veriyi istediğimiz formatta verir.

        System.out.println("userID = " + userID);
    }
    @Test(dependsOnMethods = "createUserObject", priority = 1)
    public void updateUserObject() {

//        Map<String,String> updateUser=new HashMap<>();
//        updateUser.put("name","hakan fındık");

        newUser.setName("halil fındık");

        given()
                .header("Authorization","Bearer 0f1c97269e7247ab490749691952b1a13841da6275be02e5e1980549d64c30db")
                .contentType(ContentType.JSON)
                .body(newUser)
                .log().body()
                .pathParam("userID", userID)

                .when()
                .put("users/{userID}")

                .then()
                .log().body()
                .statusCode(200)
                .body("name", equalTo("halil fındık"))
        ;
        System.out.println("userID = " + userID);
    }

    @Test(dependsOnMethods = "createUserObject", priority = 2)
    public void getUserByID() {

        given()
                .header("Authorization","Bearer 0f1c97269e7247ab490749691952b1a13841da6275be02e5e1980549d64c30db")
                .contentType(ContentType.JSON)
                .log().body()
                .pathParam("userID", userID)

                .when()
                .put("users/{userID}")

                .then()
                .log().body()
                .statusCode(200)
                .body("id", equalTo(userID))
        ;
        System.out.println("userID = " + userID);
    }

    public String getRandomEmail() {
        return RandomStringUtils.randomAlphabetic(8).toLowerCase()+"@gmail.com";
    }
    public String getRandomName()
    {
        return RandomStringUtils.randomAlphabetic(8);
    }

    int userID=0;
    User newUser;

    @Test(enabled = false)
    public void createUser() {

        userID =
                given()
                        // api methoduna gitmeden önceki hazırlıklar: token, gidecek body, parametreler

                        .header("Authorization","Bearer 0f1c97269e7247ab490749691952b1a13841da6275be02e5e1980549d64c30db")
                        .contentType(ContentType.JSON)
                        .body("{\"name\":\"halil\", \"gender\":\"male\", \"email\":\""+getRandomEmail()+"\", \"status\":\"active\"}")
                        .when()
                        .post("users")

                        .then()
                        .log().body()
                        .statusCode(201)
                        .contentType(ContentType.JSON)
                        .extract().path("id")
                ;
        System.out.println("userID = " + userID);
    }
    @Test(enabled = false)
    public void createUserMap() {

        Map<String,String> newUser= new HashMap<>();
        newUser.put("name","ismet");
        newUser.put("gender","male");
        newUser.put("email",getRandomEmail());
        newUser.put("status","active");

        int userID =
                given()
                        // api methoduna gitmeden önceki hazırlıklar: token, gidecek body, parametreler

                        .header("Authorization","Bearer 0f1c97269e7247ab490749691952b1a13841da6275be02e5e1980549d64c30db")
                        .contentType(ContentType.JSON)
                        .body(newUser)
                        .log().body()

                        .when()
                        .post("users")

                        .then()
                        .log().body()
                        .statusCode(201)
                        .contentType(ContentType.JSON)
                        .extract().path("id")
                ;
        System.out.println("userID = " + userID);
    }

}
class User {
    private String name;
    private String gender;
    private String email;
    private String status;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

