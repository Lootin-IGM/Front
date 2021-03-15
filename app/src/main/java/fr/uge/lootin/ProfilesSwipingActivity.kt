package fr.uge.lootin

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.*
import com.android.volley.toolbox.*
import com.google.android.material.button.MaterialButton
import fr.uge.lootin.WebRequestUtils.Companion.onError
import fr.uge.lootin.WebRequestUtils.Companion.onResult
import org.json.JSONObject


class ProfilesSwipingActivity : AppCompatActivity() {

    private lateinit var queue: RequestQueue
    var token: String =
        "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJMb3Vsb3UiLCJleHAiOjE2MTU4NzY1ODQsImlhdCI6MTYxNTg0MDU4NH0.sH3cKuizAZyO8L5ccQCvfZ2MtZmOXT_2MZx2B0FTRhM"
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
            /*val intent = Intent(this, AboutProfileActivity::class.java)
            intent.putExtra("userId", usersList[currentUser].id)
            intent.putExtra("token", token)
            startActivity(intent)*/
            val firstFrag = DisplayProfileFragment();
            this.supportFragmentManager.beginTransaction()
                .replace(R.id.constraintLayoutProfile, firstFrag, "bite")
                .addToBackStack(null)
                .commit();
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
                Log.i("result", "result : $response")
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