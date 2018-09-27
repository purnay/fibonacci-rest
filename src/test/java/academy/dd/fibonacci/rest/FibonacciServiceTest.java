package academy.dd.fibonacci.rest;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.jayway.restassured.RestAssured.given;

/**
 * Created by Purnachandra on 27/09/2018.
 */
public class FibonacciServiceTest {

    private static Logger LOGGER = LoggerFactory.getLogger(FibonacciServiceTest.class);

    @BeforeClass
    public static void setup(){
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 7003;
        RestAssured.basePath = "/fib";
    }

    @Test
    public void verifyDefaultEndpoint() {
        String body = given().
                              header("Content-Type", "application/json").
                      when().
                              get().
                      then().
                              contentType(ContentType.JSON).
                      extract().
                              response().body().asString();
        int index = 1;
        // Verify if each number is a fibonacci number else fail the test when the number is not a fibonacci
        for (Long outputNumber : convertBodyToArray(body)) {
            if (isFibonacciNumber(outputNumber)) {
                LOGGER.info(outputNumber + " is a fibonacci number. Sequence term number: " + index);
            } else {
                LOGGER.info(outputNumber + " is not a fibonacci number. Sequence term number: " + (index));
                Assert.fail("Application stopped returning correct Fibonacci number from sequence term number: " +(index));
            }
            index++;
        }
    }

    @Test
    public void verifySingleIndexEndpoint() {
        int index = 0;
        long outputNumber;
        // Call the single index endpoint indeﬁnitely until application stops returning fibonacci number
        // (assuming that application will stop giving fibonacci numbers at some point as per spec)
        while (index >= 0) {
            String body =
                    given().
                            header("Content-Type", "application/json").
                    when().
                            get("/{index}", index).
                    then().
                            contentType(ContentType.JSON).
                    extract().
                            response().body().asString();
            index++;
            outputNumber = Long.parseLong(body);
            // Verify if each number is a fibonacci number else fail the test when the number is not a fibonacci
            if (isFibonacciNumber(outputNumber)) {
                LOGGER.info(outputNumber + " is a fibonacci number. Sequence term number: " + (index));
            } else {
                LOGGER.info(outputNumber + " is not a fibonacci number. Sequence term number: " + (index));
                Assert.fail("Application stopped returning correct Fibonacci number from sequence term number: " +(index));
                break;
            }
        }
    }

    @Test
    public void verifyRangeEndpoint() {
        int startIndex = 0;
        int finishIndex = 10;
        // Call the range endpoint indeﬁnitely to return 10 numbers each time to find when the application stops returning fibonacci number
        // (assuming that application will stop giving fibonacci numbers at some point as per spec)
        while (startIndex >= 0) {
            String body =
                    given().
                            header("Content-Type", "application/json").
                            queryParam("startIndex", startIndex).
                            queryParam("finishIndex", finishIndex).
                    when().
                            get("/range").
                    then().
                            contentType(ContentType.JSON).
                    extract().
                            response().body().asString();

            List<Long> outputList = convertBodyToArray(body);
            // Verify if each number is a fibonacci number else fail the test when an invalid fibonacci is returned
            for (Long outputNumber : outputList) {
                int index = startIndex + outputList.indexOf(outputNumber) + 1;
                if (isFibonacciNumber(outputNumber)) {
                    LOGGER.info(outputNumber + " is a fibonacci number. Sequence term number: " + (index));
                } else {
                    LOGGER.info(outputNumber + " is not a fibonacci number. Sequence term number: " + (index));
                    Assert.fail("Application stopped returning correct Fibonacci number from sequence term number: " + (index));
                }

            }
            startIndex = startIndex + 11;
            finishIndex = finishIndex + 11;
        }
    }

    private List<Long> convertBodyToArray(String body) {
        String bodyWithoutBrackets = body.substring(1, body.length() - 1);
        String bodyWithoutQuotes = bodyWithoutBrackets.replaceAll("\"", "");
        List<Long> numberList = new ArrayList();
        List<String> result = Arrays.asList(bodyWithoutQuotes.split(","));
        for(String current : result){
            numberList.add(Long.parseLong(current));
        }
        return numberList;
    }

    private boolean isFibonacciNumber(long n) {
        // A positive integer n is Fibonacci number if and only if either (5*n*n + 4) or (5*n*n - 4) is a square
        return isPerfectSquare(5 * n * n + 4) ||
                isPerfectSquare(5 * n * n - 4);
    }

    private boolean isPerfectSquare(long x) {
        long s = (long) Math.sqrt(x);
        return (s * s == x);
    }
}
