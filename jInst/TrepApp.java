import android.app.Application;
import android.content.Context;

public class TrepApp extends Application {

    private static Context trepContext;

    public void onCreate() {
        super.onCreate();
        trepContext = getApplicationContext();
    }

    public static Context getAppContext() {
        return trepContext;
    }

    public static void setAppContext (Context context){
        trepContext =context;
    }

}
