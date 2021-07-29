package org.mifosplatform.integrationtests.common;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.path.json.JsonPath.from;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.apache.commons.lang.StringUtils;
import org.apache.http.conn.HttpHostConnectException;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

@SuppressWarnings("unchecked")
public class Utils {

	
	 public static final String TENANT_IDENTIFIER = "tenantIdentifier=default";
	
    private static final String LOGIN_URL = "/obsplatform/api/v1/authentication?username=billing&password=password&tenantIdentifier=default";


    public static void initializeRESTAssured() {
        RestAssured.baseURI ="https://localhost";
        RestAssured.port = 8443;
        RestAssured.keystore("../keystore.jks", "openmf");//8985891857
    }

    public static String loginIntoServerAndGetBase64EncodedAuthenticationKey() {
        try {
            System.out.println("-----------------------------------LOGIN-----------------------------------------");
            String json = RestAssured.post(LOGIN_URL).asString();
            assertThat("Failed to login into obs platform", StringUtils.isBlank(json), is(false));
            return JsonPath.with(json).get("base64EncodedAuthenticationKey");
        }
        catch (Exception e) {
            if (e instanceof HttpHostConnectException) {
                HttpHostConnectException hh = (HttpHostConnectException) e;
                fail("Failed to connect to obs platform:"+hh.getMessage());
            }

            throw new RuntimeException(e);
        }
    }

    public static <T> T performServerGet(final RequestSpecification requestSpec, final ResponseSpecification responseSpec,
            final String getURL, final String jsonAttributeToGetBack) {
        String json = given().spec(requestSpec).expect().spec(responseSpec).log().ifError().when().get(getURL).andReturn().asString();
        return (T) from(json).get(jsonAttributeToGetBack);
    }

    public static <T> T performServerPost(final RequestSpecification requestSpec, final ResponseSpecification responseSpec,
            final String postURL, final String jsonBodyToSend, final String jsonAttributeToGetBack) {
        String json = given().spec(requestSpec).body(jsonBodyToSend).expect().spec(responseSpec).log().ifError().when().post(postURL).andReturn().asString();
        return (T) from(json).get(jsonAttributeToGetBack);
    }
    
    public static <T> T performServerPut(final RequestSpecification requestSpec, final ResponseSpecification responseSpec,
            final String putURL, final String jsonBodyToSend, final String jsonAttributeToGetBack) {
        final String json = given().spec(requestSpec).body(jsonBodyToSend).expect().spec(responseSpec).log().ifError().when().put(putURL)
                .andReturn().asString();
        return (T) from(json).get(jsonAttributeToGetBack);
    }

    public static <T> T performServerDelete(final RequestSpecification requestSpec, final ResponseSpecification responseSpec,
            final String deleteURL, final String jsonAttributeToGetBack) {
        final String json = given().spec(requestSpec).expect().spec(responseSpec).log().ifError().when().delete(deleteURL).andReturn()
                .asString();
        return (T) from(json).get(jsonAttributeToGetBack);
    }


    public static String convertDateToURLFormat(String dateToBeConvert){
        SimpleDateFormat oldFormat = new SimpleDateFormat("dd MMMMMM yyyy");
        SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd");
        String reformattedStr="";
        try {
            reformattedStr = newFormat.format(oldFormat.parse(dateToBeConvert));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return reformattedStr;
    }

    public static String randomStringGenerator(final String prefix, final int len, final String sourceSetString) {
        int lengthOfSource = sourceSetString.length();
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append((sourceSetString).charAt(rnd.nextInt(lengthOfSource)));
        return (prefix + (sb.toString()));
    }

    public static String randomStringGenerator(final String prefix, final int len) {
        return randomStringGenerator(prefix, len, "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ");
    }

    public static String randomNameGenerator(final String prefix, final int lenOfRandomSuffix) {
        return randomStringGenerator(prefix, lenOfRandomSuffix);
    }
    
    public static String convertDateToURLFormat(final Calendar dateToBeConvert) {
        return new SimpleDateFormat("dd MMMMMM yyyy").format(dateToBeConvert.getTime());
    }
    
	public static String randomNumberGenerator(final int min, final int max) {
		final Random random = new Random();
		int randomNum = random.nextInt((max - min) + 1) + min;
		return Integer.toString(randomNum);
	}

	public static String convertDateToURLFormat(final LocalDate localDate,final String pattern) {
		final DateTimeFormatter formatter = DateTimeFormat.forPattern(pattern);
		final String formattedDate = formatter.print(localDate);
		return formattedDate;
	}
}
