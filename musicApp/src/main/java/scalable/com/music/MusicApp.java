package scalable.com.music;

import scalable.com.databaseHelper.DatabaseHelper;
import scalable.com.shared.App;
import scalable.com.shared.classes.Arango;
import scalable.com.music.constants.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.TimeoutException;

public class MusicApp extends App {
    public static Arango arangoPool;

    public static void main(String[] args) throws TimeoutException, IOException, ClassNotFoundException, SQLException {
        MusicApp app = new MusicApp();
        app.start();
        Arango arango = Arango.getInstance();
        arango.createPool(15);
        arango.createDatabaseIfNotExists(DatabaseConstants.DATABASE_NAME);
        arango.createCollectionIfNotExists(DatabaseConstants.DATABASE_NAME, DatabaseConstants.SONGS_COLLECTION, false);

        // comment this line after development
        // DatabaseHelper.createSchema();
        // DatabaseHelper.createProcs();

    }

    @Override
    public void dbInit() throws IOException {

    }

    @Override
    protected String getAppName() {
        return "Music";
    }

}
