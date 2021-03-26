package fr.uge.lootin

import android.app.Notification
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech.STOPPED
import android.support.v4.media.session.PlaybackStateCompat
import android.telephony.ServiceState
import android.util.Base64
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.button.MaterialButton
import fr.uge.lootin.httpUtils.GsonGETRequest
import fr.uge.lootin.httpUtils.WebRequestUtils.Companion.onError
import fr.uge.lootin.httpUtils.WebRequestUtils.Companion.onResult
import fr.uge.lootin.models.UserList
import fr.uge.lootin.models.Users
import fr.uge.lootin.settings.DisplaySettingsFragment
import kotlinx.coroutines.flow.SharingCommand
import org.json.JSONObject


class ProfilesSwipingActivity : AppCompatActivity() {

    private lateinit var queue: RequestQueue
    var token: String =
        "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJMb3Vsb3UiLCJleHAiOjE2MTY3Mjg3NTcsImlhdCI6MTYxNjY5Mjc1N30.oRc_Vk13CN2mMnjI4d7U9bXQSWCFB-i6s1KPA4SpT8A"
    private val usersList: ArrayList<Users> = ArrayList()
    private var currentUser: Int = 0
    private val url: String = "http://192.168.1.18:8080"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profiles_swiping)
        this.queue = Volley.newRequestQueue(this)
        displayNextProfile()
        findViewById<MaterialButton>(R.id.likeButton).setOnClickListener {
            likeCurrentUser()
            currentUser++
            displayNextProfile()
        }
        findViewById<MaterialButton>(R.id.nextButton).setOnClickListener {
            currentUser++
            displayNextProfile()
        }
        findViewById<MaterialButton>(R.id.moreButton).setOnClickListener {
            val bundle = Bundle();
            bundle.putString("TOKEN", token);
            bundle.putSerializable("USER", usersList[currentUser])
            val firstFrag = DisplayProfileFragment();
            firstFrag.arguments = bundle
            supportFragmentManager.beginTransaction().setCustomAnimations(
                R.anim.slide_in_r_l,
                R.anim.fade_out_l_r, R.anim.fade_in_r_l, R.anim.slide_out_r_l
            ).add(R.id.fragment_container_view, firstFrag, "userMoreFragment")
                .addToBackStack("userMoreFragment").commit()
        }

        findViewById<ImageButton>(R.id.settingsButton).setOnClickListener {
            val settingsFrag = DisplaySettingsFragment.newInstance(token)
            supportFragmentManager.beginTransaction().setCustomAnimations(
                R.anim.slide_in_l_r,
                R.anim.fade_out_l_r, R.anim.fade_in_l_r, R.anim.slide_out_l_r
            )
                .add(R.id.fragment_container_view, settingsFrag, "settingsFragment")
                .addToBackStack("settingsFragment").commit()
        }

        actionOnService()


    }

    private fun actionOnService() {
        Intent(this, EndlessService::class.java).also {
            it.action = SharingCommand.START.name
            it.putExtra("userToken", "4")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Log.i("test", "Starting the service in >=26 Mode")
                startForegroundService(it)
                return
            }
            Log.i("test", "Starting the service in < 26 Mode")
            startService(it)
        }
    }

    private fun displayImage(image: String) {
        val decodedString: ByteArray = Base64.decode(image, Base64.DEFAULT)
        val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
        findViewById<ImageView>(R.id.userPicture).setImageBitmap(decodedByte)
    }

    private fun displayNextProfile() {
        if (currentUser == usersList.size) loadUsers()
        else displayNextUser()
    }

    private fun loadUsers() {
        val url = "$url/profile"
        val map = HashMap<String, String>()
        map["Authorization"] = "Bearer $token"
        val request = GsonGETRequest(url, UserList::class.java, map,
            { response ->
                onResult(response)
                usersList.clear()
                usersList.addAll(response.users)
                currentUser = 0
                displayNextUser()
            },
            { error -> onError(error) }
        )
        queue.add(request)
    }

    private fun getAuthenticatedHeader(): HashMap<String, String> {
        val map = HashMap<String, String>()
        map["Authorization"] = "Bearer $token"
        return map
    }

    private fun likeCurrentUser() {
        if (usersList.size > 0 && currentUser < usersList.size) {
            val url = "$url/like"
            val request = object : JsonObjectRequest(Request.Method.POST,
                url,
                JSONObject("{\"userLikedId\": " + usersList[currentUser].id + "}"),
                { response ->
                    onResult(response)
                    val jsonResponse = JSONObject(response.toString());
                },
                { error -> onError(error) }
            ) {
                override fun getHeaders(): MutableMap<String, String> {
                    var header = getAuthenticatedHeader()
                    header["Content-Type"] = "application/json"
                    return header
                }
            }
            queue.add(request)
        }
    }

    private fun displayNextUser() {

        if (usersList.size > 0 && currentUser < usersList.size) {
            var user = usersList[currentUser]
            var bio = truncateDescription(user.description)
            findViewById<TextView>(R.id.username_textView).text =
                user.login + ", " + user.age.toString()
            findViewById<TextView>(R.id.biography_textView2).text = bio
            displayImage(user.image)
        }
    }

    private fun truncateDescription(description: String): String {
        var res = description
        var i = 0
        if (res.contains("\n")) {
            res = res.substring(0, res.indexOf("\n")) + "..."
        }
        if (res.length > 30) {
            res = res.substring(0, i) + "..."
        }
        return res
    }


}