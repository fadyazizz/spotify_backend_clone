package scalable.com.user_to_user.commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import scalable.com.shared.classes.Arango;
import scalable.com.shared.testsHelper.TestHelper;
import scalable.com.user_to_user.UserToUserApp;

public class UserToUserTest {
    private static String userId1 = "1", userId2 = "2", userId3 = "3";
    private static Arango arango;
    @BeforeClass
    public static void setUp() {
        try {

//            arango.dropDatabase("user_to_user");
            UserToUserApp app= new UserToUserApp();
            app.start();
            TestHelper.appBeingTested=app;
            arango = Arango.getConnectedInstance();
            arango.createDatabaseIfNotExists("user_to_user");
            arango.createCollectionIfNotExists("user_to_user","blocked_ids",false);
            arango.createCollectionIfNotExists("user_to_user","followed_ids",false);
            arango.createCollectionIfNotExists("user_to_user","reports",false);

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    public static String reportUser(int reported_Id, String comment) {
        JSONObject body = new JSONObject();
        body.put("reported_id", reported_Id);
        body.put("comment", comment);

        JSONObject uriParams = new JSONObject();

        JSONObject token= new JSONObject();
        token.put("id", "1");

        JSONObject request = new JSONObject();
        request.put("body", body);
        request.put("methodType", "POST");
        request.put("uriParams", uriParams);
        request.put("isAuthenticated",true);
        request.put("tokenPayload", token);

        ReportUser reportUser= new ReportUser();
        return TestHelper.execute(reportUser,request);
    }

    @Test
    public void reportUserTest() {
        int beforeCount= arango.documentCount("user_to_user", "reports");
        String response = reportUser(200, "test comment");
        JSONObject responseJson = new JSONObject(response);
        int afterCount =arango.documentCount("user_to_user", "reports");
        assertEquals("Reported", responseJson.getString("msg"));
        assertEquals(beforeCount+1, afterCount);
    }

    public static String postFollowUser(int followed_id){

        JSONObject body = new JSONObject();
        body.put("followed_id", followed_id);

        JSONObject uriParams = new JSONObject();

        JSONObject token= new JSONObject();
        token.put("id", "1");

        JSONObject request = new JSONObject();
        request.put("body", body);
        request.put("methodType", "POST");
        request.put("uriParams", uriParams);
        request.put("isAuthenticated",true);
        request.put("tokenPayload", token);

        PostFollowUser postFollowUser = new PostFollowUser();
        return TestHelper.execute(postFollowUser,request);
    }

    @Test
    public void postFollowUserTest(){
        arango.createCollectionIfNotExists("user_to_user","followed_ids",false);
        int beforeCount= arango.documentCount("user_to_user", "followed_ids");
        String response = postFollowUser(3);
        JSONObject responseJson = new JSONObject(response);
        int afterCount =arango.documentCount("user_to_user", "followed_ids");
        assertEquals("Following: 3", responseJson.getString("msg"));
        assertEquals(beforeCount+1, afterCount);
        arango.dropCollection("user_to_user", "followed_ids");
    }

    public static String postBlockUser(int blocked_id){

        JSONObject body = new JSONObject();
        body.put("blocked_id", blocked_id);

        JSONObject uriParams = new JSONObject();

        JSONObject token = new JSONObject();
        token.put("id", "1");

        JSONObject request = new JSONObject();
        request.put("body", body);
        request.put("methodType", "POST");
        request.put("uriParams", uriParams);
        request.put("isAuthenticated", true);
        request.put("tokenPayload", token);

        PostBlockUser postBlockUser = new PostBlockUser();
        return TestHelper.execute(postBlockUser,request);
    }

    @Test
    public void postBlockUserTest(){
        arango.createCollectionIfNotExists("user_to_user","blocked_ids",false);
        int beforeCount= arango.documentCount("user_to_user", "blocked_ids");
        String response = postBlockUser(4);
        JSONObject responseJson = new JSONObject(response);
        int afterCount =arango.documentCount("user_to_user", "blocked_ids");
        assertEquals("Blocked: "+"4", responseJson.getString("msg"));
        assertEquals(beforeCount+1, afterCount);
        arango.dropCollection("user_to_user", "blocked_ids");
    }

    public static String blockUser(int blocked_id) {
        JSONObject body = new JSONObject();
        body.put("blocked_id", blocked_id);

        JSONObject uriParams = new JSONObject();

        JSONObject token= new JSONObject();
        token.put("id", userId1);

        JSONObject request = new JSONObject();
        request.put("body", body);
        request.put("methodType", "POST");
        request.put("uriParams", uriParams);
        request.put("isAuthenticated",true);
        request.put("tokenPayload", token);

        PostBlockUser blockUser= new PostBlockUser();
        return TestHelper.execute(blockUser, request);
    }

    public static String followUser(int followed_id) {
        JSONObject body = new JSONObject();
        body.put("followed_id", followed_id);

        JSONObject uriParams = new JSONObject();

        JSONObject token= new JSONObject();
        token.put("id", userId1);

        JSONObject request = new JSONObject();
        request.put("body", body);
        request.put("methodType", "POST");
        request.put("uriParams", uriParams);
        request.put("isAuthenticated",true);
        request.put("tokenPayload", token);

        PostFollowUser followUser= new PostFollowUser();
        return TestHelper.execute(followUser, request);
    }

    public static String getBlockedUsers() {
        GetBlockedUsers getBlockedUsers = new GetBlockedUsers();
        JSONObject body = new JSONObject();

        JSONObject token= new JSONObject();
        token.put("id", userId1);

        JSONObject uriParams = new JSONObject();

        JSONObject request = new JSONObject();
        request.put("isAuthenticated",true);
        request.put("tokenPayload", token);
        request.put("body", body);
        request.put("methodType", "GET");
        request.put("uriParams", uriParams);

        return TestHelper.execute(getBlockedUsers, request);
    }

    public static String getFollowedUsers() {
        GetFollowedUsers getFollowedUsers = new GetFollowedUsers();
        JSONObject body = new JSONObject();

        JSONObject token= new JSONObject();
        token.put("id", userId1);

        JSONObject uriParams = new JSONObject();
        JSONObject request = new JSONObject();
        request.put("isAuthenticated",true);
        request.put("tokenPayload", token);
        request.put("body", body);
        request.put("methodType", "GET");
        request.put("uriParams", uriParams);

        return TestHelper.execute(getFollowedUsers, request);
    }

    @Test
    public void getBlockedUsersTest() {
        blockUser(Integer.parseInt(userId2));
        blockUser(Integer.parseInt(userId3));

        String response = getBlockedUsers();
        JSONObject responseJson = new JSONObject(response);
        assertEquals(200, responseJson.getInt("statusCode"));
        JSONArray dataArr = (JSONArray) (responseJson.get("data"));
        assertEquals(2, dataArr.length());
        arango.dropCollection("user_to_user", "blocked_ids");
    }

    @Test
    public void getFollowedUsersTest() {
        followUser(Integer.parseInt(userId2));
        followUser(Integer.parseInt(userId3));

        String response = getFollowedUsers();
        JSONObject responseJson = new JSONObject(response);
        assertEquals(200, responseJson.getInt("statusCode"));
        JSONArray dataArr = (JSONArray) (responseJson.get("data"));
        assertEquals(2, dataArr.length());
        arango.dropCollection("user_to_user", "followed_ids");
    }

    @AfterClass
    public static void deleteAll(){
        arango.dropDatabase("user_to_user");
    }
}
