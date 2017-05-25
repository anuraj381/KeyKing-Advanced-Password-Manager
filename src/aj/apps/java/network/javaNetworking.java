package aj.apps.java.network;

import java.io.*;
import aj.apps.java.Main.popup;
import javafx.concurrent.Task;
import okhttp3.*;

/**
 * This class includes methods for connecting and performing operations in which network connectivity is required
 * @author Anuraj Jain
 */
public class javaNetworking extends popup {

    /**
     * This method contains functionality for sending OTP(one time password) to user mobile number
     * <p>this method performs all the tasks for sending OTP on the background thread</p>
     * @param mobile mobile number with which user logged in
     * @param msg OTP to be sent
     * @return true
     */
    public static boolean sendOtp(String mobile, String msg) {

        String urlLink = "http://your-url-here?mobile=" + mobile + "&msg=" + msg + "%20is%20your%20KeyKing%20OTP";

        Task<Boolean> backgroundTask = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                return null;
            }
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(urlLink)
                        .get()
                        .build();

                Response response = null;
                try {
                    response = client.newCall(request).execute();
                    popup.getResult = "sent";
                } catch (IOException e) {
                    popup.getResult = "failed";
                    e.printStackTrace();
                }
                System.out.println(response.toString());
                response.body().close();
            }
        };

        Thread backgroundThread = new Thread(backgroundTask);
        backgroundThread.start();

        return true;
    }
}
