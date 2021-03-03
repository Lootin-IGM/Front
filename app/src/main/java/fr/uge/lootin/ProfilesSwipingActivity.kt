package fr.uge.lootin

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
    var token: String = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJMb3Vsb3UiLCJleHAiOjE2MTQ4NDk0MDAsImlhdCI6MTYxNDgxMzQwMH0.HfAvwk3aNsYppFaRFNlZ2w2F6JtsdIZgLvkZDGaoQ84"
    private val usersList: ArrayList<Users> = ArrayList()
    private var currentUser: Int = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profiles_swiping)
        this.queue = Volley.newRequestQueue(this)
        //connect()
        displayNextProfile()
        findViewById<MaterialButton>(R.id.likeButton).setOnClickListener {
            likeCurrentUser()
            displayNextProfile()
        }
        findViewById<MaterialButton>(R.id.nextButton).setOnClickListener {
            displayNextProfile()
        }
        findViewById<MaterialButton>(R.id.moreButton).setOnClickListener {
            val intent = Intent(this, AboutProfileActivity::class.java)
            intent.putExtra("userId", usersList[currentUser].id)
            intent.putExtra("token", token)
            startActivity(intent)
        }
    }


    /*
    1. afficher photo
    2. créer rview + afficher jeux
    3. créer écran de chargement si pas de nouveaux résultats (optionnel)
     private fun imageRequest(url: String, imageView: ImageView) {
         //val imageView: ImageView = findViewById(R.id.userPicture)
         val ir = ImageRequest(
             url,
             { response -> imageView.setImageBitmap(response) },
             300,
             300,
             ImageView.ScaleType.CENTER,
             Bitmap.Config.RGB_565
         ) { Log.e("http_error", "Image Load Error: ") }

         queue.add(ir)
     }
 */


    private fun displayNextProfile() {
        currentUser++
        if (currentUser == usersList.size)  loadUsers()
        else displayNextUser()
    }

    private fun loadUsers() {
        val url = "http://10.188.201.141:8080/profile"
        val map = HashMap<String, String>()
        map["Authorization"] = "Bearer $token"
        val request = GsonGETRequest(url, UserList::class.java, map,
            { response ->
                onResult(response)
                usersList.addAll(response.users)
                displayNextUser()
            },
            { error -> onError(error) }
        )
        queue.add(request)
    }

    private fun getAuthentifiedHeader(): HashMap<String, String>{
        val map = HashMap<String, String>()
        map["Authorization"] = "Bearer $token"
        return map
    }

    private fun likeCurrentUser() {
        val url = "http://10.188.201.141:8080/like"
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
                var header = getAuthentifiedHeader()
                header["Content-Type"] = "application/json"
                return header
            }
        }
        queue.add(request)
    }

    private fun displayNextUser() {
        var user = usersList[currentUser]
        var bio =
            user.firstName + " " + user.lastName + " " + user.lastName + " " + user.lastName + " " + user.lastName
        if (bio.length > 30) bio = bio.substring(0, 30) + "..."
        findViewById<TextView>(R.id.username_textView).text = user.login + ", " + user.firstName
        findViewById<TextView>(R.id.biography_textView2).text = bio
    }

    private fun connect() {
        val url = "http://10.188.201.141:8080/login"
        Log.i("my_log", "connect request")
        val jsonObjectRequest = JsonObjectRequest(Request.Method.POST,
            url,
            JSONObject("{\"username\": \"Loulou\",\"password\": \"Yvette\"}"),
            { response ->
                onResult(response)
                val jsonResponse = JSONObject(response.toString());
                this.token = jsonResponse.getString("jwt")
            },
            { error -> onError(error) }
        )
        queue.add(jsonObjectRequest)
    }




}