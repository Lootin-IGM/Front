package fr.uge.lootin.httpUtils

import android.util.Log
import com.android.volley.VolleyError

class WebRequestUtils {
    companion object{
        fun <T> onResult(result: T) {
            Log.i("my_log", "Connect Response: %s".format(result.toString()));
        }

        fun onError(error: VolleyError) {
            Log.i(
                "my_log", "error while trying to connect\n"
                        + error.toString() + "\n"
                        + error.networkResponse + "\n"
                        + error.localizedMessage + "\n"
                        + error.message + "\n"
                        + error.cause + "\n"
                        + error.stackTrace.toString()
            )
        }
    }
}