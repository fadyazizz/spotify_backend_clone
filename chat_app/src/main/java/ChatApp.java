
import scalable.com.shared.App;
import scalable.com.shared.classes.FireStoreInstance;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class ChatApp extends App {
    public static FireStoreInstance qs;

    public static void main(String[] args) throws TimeoutException, IOException, ClassNotFoundException {

        ChatApp app = new ChatApp();

        app.dbInit();
        app.start();
    }

    @Override
    public void dbInit() throws IOException {
        FireStoreInstance qss = new FireStoreInstance();
        qs = qss;
    }

    @Override
    protected String getAppName() {
        return "Chat";
    }

    public static FireStoreInstance getDBInstance()
    {
        return qs;
    }
}
