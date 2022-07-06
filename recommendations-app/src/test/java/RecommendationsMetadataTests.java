import com.scalable.recommendations.RecommendationsApp;
import com.scalable.recommendations.constants.DatabaseConstants;
import org.junit.BeforeClass;
import scalable.com.shared.classes.Arango;
import scalable.com.shared.testsHelper.TestHelper;

import static org.junit.Assert.fail;


public abstract class RecommendationsMetadataTests {
    public static Arango arangoPool;

    @BeforeClass
    public static void setUp(){
        try {
            RecommendationsApp app = new RecommendationsApp();
            app.start();
            TestHelper.appBeingTested=app;
            arangoPool = Arango.getConnectedInstance();
            arangoPool.createCollectionIfNotExists(DatabaseConstants.DATABASE_NAME, DatabaseConstants.USER_DOCUMENT_COLLECTION, false);
            arangoPool.createCollectionIfNotExists(DatabaseConstants.DATABASE_NAME, DatabaseConstants.MUSIC_DOCUMENT_COLLECTION, false);
            arangoPool.createCollectionIfNotExists(DatabaseConstants.DATABASE_NAME, DatabaseConstants.USER_EDGE_COLLECTION, true);
            arangoPool.createCollectionIfNotExists(DatabaseConstants.DATABASE_NAME, DatabaseConstants.MUSIC_EDGE_COLLECTION, true);
        }
        catch (Exception e){
            fail(e.getMessage());
        }

    }
}
