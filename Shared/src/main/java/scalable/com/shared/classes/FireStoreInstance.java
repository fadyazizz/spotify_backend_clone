package scalable.com.shared.classes;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.DocumentReference;
// [START firestore_deps]
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
// [END firestore_deps]
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.common.collect.ImmutableMap;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple Quick start application demonstrating how to connect to Firestore
 * and add and query documents.
 */
public class FireStoreInstance {

    private static Firestore db;

    public FireStoreInstance(String projectId) throws Exception {

    }

    /**
     * Initialize Firestore using default project ID.
     */
    public FireStoreInstance() throws IOException {
        // [START firestore_setup_client_create]
        // Option 1: Initialize a Firestore client with a specific `projectId` and
        //           authorization credential.
        // [START fs_initialize_project_id]
        // [START firestore_setup_client_create_with_project_id]
        System.out.println("awel el bta3");

        File file = new File(
                getClass().getClassLoader().getResource("key.json").getFile());
        FileInputStream fis = new FileInputStream(file);
        System.out.println("Read the json key file");

        FirestoreOptions firestoreOptions =
                FirestoreOptions.getDefaultInstance().toBuilder()
                        .setCredentials(GoogleCredentials.fromStream(fis))
                        .build();
        Firestore db = firestoreOptions.getService();
        // [END fs_initialize_project_id]
        // [END firestore_setup_client_create_with_project_id]
        // [END firestore_setup_client_create]
        this.db = db;
    }

    Firestore getDb() {
        return db;
    }

    /**
     * Add named test documents with fields first, last, middle (optional), born.
     *
     * @param docName document name
     */
    void addDocument(String docName) throws Exception {
        switch (docName) {
            case "alovelace": {
                // [START fs_add_data_1]
                // [START firestore_setup_dataset_pt1]
                DocumentReference docRef = db.collection("users").document("alovelace");
                // Add document data  with id "alovelace" using a hashmap
                Map<String, Object> data = new HashMap<>();
                data.put("first", "Ada");
                data.put("last", "Lovelace");
                data.put("born", 1815);
                //asynchronously write data
                ApiFuture<WriteResult> result = docRef.set(data);
                // ...
                // result.get() blocks on response
                System.out.println("Update time : " + result.get().getUpdateTime());
                // [END firestore_setup_dataset_pt1]
                // [END fs_add_data_1]
                break;
            }
            case "aturing": {
                // [START fs_add_data_2]
                // [START firestore_setup_dataset_pt2]
                DocumentReference docRef = db.collection("users").document("aturing");
                // Add document data with an additional field ("middle")
                Map<String, Object> data = new HashMap<>();
                data.put("first", "Alan");
                data.put("middle", "Mathison");
                data.put("last", "Turing");
                data.put("born", 1912);

                ApiFuture<WriteResult> result = docRef.set(data);
                System.out.println("Update time : " + result.get().getUpdateTime());
                // [END firestore_setup_dataset_pt2]
                // [END fs_add_data_2]
                break;
            }
            case "cbabbage": {
                DocumentReference docRef = db.collection("users").document("cbabbage");
                Map<String, Object> data =
                        new ImmutableMap.Builder<String, Object>()
                                .put("first", "Charles")
                                .put("last", "Babbage")
                                .put("born", 1791)
                                .build();
                ApiFuture<WriteResult> result = docRef.set(data);
                System.out.println("Update time : " + result.get().getUpdateTime());
                break;
            }
            default:
        }
    }


     public static void addDocument(String collection, String docName , HashMap<String, Object> documentObject)throws Exception
    {
        DocumentReference docRef = db.collection(collection).document(docName);
        ApiFuture<WriteResult> result = docRef.set(documentObject);
        System.out.println("Added document " + docName + "into collection " + collection);
        System.out.println("adding time : " + result.get().getUpdateTime());


    }

    ArrayList runQuery(String collection, String condition , String field , Object value) throws  Exception{
        ApiFuture<QuerySnapshot> query= db.collection(collection).whereEqualTo(field, value).get();
        switch (condition){
            case ("equal") :  query =
                    db.collection(collection).whereEqualTo(field, value).get();  break;
            case ("less") :  query =
                    db.collection(collection).whereLessThan(field, value).get();  break;

            case ("more") :  query =
                    db.collection(collection).whereGreaterThan(field, value).get();  break;
        }

        // query.get() blocks on response
        QuerySnapshot querySnapshot = query.get();
        List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
        ArrayList<QueryDocumentSnapshot> ArrayOfQueries = new ArrayList<>();
        for (QueryDocumentSnapshot document : documents) {
            System.out.println("Result : " + document.getId());
            ArrayOfQueries.add(document);

        }
        return ArrayOfQueries;

    }
    void runQuery() throws Exception {
        // [START fs_add_query]
        // asynchronously query for all users born before 1900
        ApiFuture<QuerySnapshot> query =
                db.collection("users").whereLessThan("born", 1900).get();
        // ...
        // query.get() blocks on response
        QuerySnapshot querySnapshot = query.get();
        List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
        for (QueryDocumentSnapshot document : documents) {
            System.out.println("User: " + document.getId());
            System.out.println("First: " + document.getString("first"));
            if (document.contains("middle")) {
                System.out.println("Middle: " + document.getString("middle"));
            }
            System.out.println("Last: " + document.getString("last"));
            System.out.println("Born: " + document.getLong("born"));
        }
        // [END fs_add_query]
    }

    public static ArrayList<QueryDocumentSnapshot> retrieveAllDocuments(String collection) throws Exception {
        // [START fs_get_all]
        // asynchronously retrieve all users
        ApiFuture<QuerySnapshot> query = db.collection(collection).get();
        // ...
        // query.get() blocks on response
        QuerySnapshot querySnapshot = query.get();
        ArrayList<QueryDocumentSnapshot> finalArr = new ArrayList<>();
        List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
        for (QueryDocumentSnapshot document : documents) {
            finalArr.add(document);

        }
        // [END fs_get_all]
        return finalArr;
    }

    void run() throws Exception {
        String[] docNames = {"alovelace", "aturing", "cbabbage"};

        // Adding document 1
        System.out.println("########## Adding document 1 ##########");
        addDocument(docNames[0]);

        // Adding document 2
        System.out.println("########## Adding document 2 ##########");
        addDocument(docNames[1]);

        // Adding document 3
        System.out.println("########## Adding document 3 ##########");
        addDocument(docNames[2]);

        // retrieve all users born before 1900
        System.out.println("########## users born before 1900 ##########");
        runQuery();

        // retrieve all users
        System.out.println("########## All users ##########");
        //retrieveAllDocuments();
        System.out.println("###################################");
    }

    /**
     * A quick start application to get started with Firestore.
     *
     * @param args firestore-project-id (optional)
     */
    public static void main(String[] args) throws Exception {
        // default project is will be used if project-id argument is not available
        System.out.println("Main method");
        FireStoreInstance quickStart = new FireStoreInstance();
        quickStart.run();
        quickStart.close();
    }

    /** Closes the gRPC channels associated with this instance and frees up their resources. */
    void close() throws Exception {
        db.close();
    }

    public static Firestore getInstance() throws IOException {
        return db;
    }
}