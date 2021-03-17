package fr.uge.lootin

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.google.android.material.button.MaterialButton
import fr.uge.lootin.WebRequestUtils.Companion.onError
import fr.uge.lootin.WebRequestUtils.Companion.onResult

class AboutProfileActivity : AppCompatActivity() {

    private lateinit var queue: RequestQueue
    private lateinit var token: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_profile)
        this.queue = Volley.newRequestQueue(this)
        val userId = intent.getStringExtra("userId")
        token = intent.getStringExtra("token").toString()
        if (!userId.isNullOrEmpty()) {
            displayUserDetails(userId)
        }
        findViewById<MaterialButton>(R.id.lessButton).setOnClickListener {
            val intent = Intent(this, ProfilesSwipingActivity::class.java)
            startActivity(intent)
        }

    }


    private fun displayUserDetails(userId: String) {
        val url = "http://10.188.201.141:8080/profile/full/$userId"
        val map = HashMap<String, String>()
        map["Authorization"] = "Bearer $token"
        val request = GsonGETRequest(url, UserFull::class.java, map,
            { response ->
                onResult(response)
                setUserDetails(22, response.login, response.firstName + " " + response.lastName)

            },
            { error -> onError(error) }
        )
        queue.add(request)
    }

    private fun setUserDetails(
        userAge: Int,
        userName: String,
        userBiography: String
    ) {
        Log.i("bite", userBiography)

        //findViewById<ImageView>(R.id.userPicture).setImageBitmap(profilePicture)
        findViewById<TextView>(R.id.userBiography).text =
            userBiography + "  Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam congue et odio a egestas. Maecenas ac erat vel velit placerat vulputate id id tellus. Nunc risus arcu, luctus eu lectus vitae, dictum porta sapien. Interdum et malesuada fames ac ante ipsum primis in faucibus. Curabitur elementum bibendum mi at aliquam. Ut tristique sollicitudin dui, in lacinia orci lacinia sed. In hac habitasse platea dictumst. Nullam vehicula rhoncus ligula. Vivamus dignissim nibh ac nisi convallis scelerisque. Vestibulum efficitur consectetur quam. Proin ac neque egestas, euismod magna in, fermentum lacus. Maecenas in accumsan ante. Vivamus fringilla, velit vel porta volutpat, neque tellus tempus erat, sed congue nisl nunc in risus. Nunc ornare luctus est, in volutpat enim ultricies eu. Fusce venenatis enim lobortis mi auctor, ut vestibulum quam pulvinar. Ut a viverra arcu, ut viverra nulla.\n" +
                    "\n" +
                    "Nunc iaculis tellus non est suscipit, sed faucibus tortor dapibus. Aliquam tempus condimentum nunc, vitae imperdiet justo convallis a. Praesent quis efficitur eros. Donec porta gravida hendrerit. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Fusce pellentesque lobortis porttitor. Duis lorem risus, tristique a maximus vitae, viverra a lacus.\n" +
                    "\n" +
                    "Vivamus metus velit, pharetra quis consequat eget, imperdiet laoreet neque. Morbi cursus quam nunc, quis eleifend nisl imperdiet a. Vivamus aliquam odio a enim tempus lacinia. Aliquam elit lectus, sollicitudin vitae dictum non, placerat et augue. Proin vel massa eu ipsum varius fermentum. Morbi ut volutpat lorem. Fusce quis fringilla felis, bibendum tristique felis. Nulla finibus egestas arcu eu ullamcorper. Fusce a nulla dapibus, consectetur lectus vel, sollicitudin augue. Phasellus suscipit, sapien non mattis bibendum, magna lorem finibus metus, at vulputate velit augue in neque. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Mauris nec eleifend nisi. Donec tempor placerat erat, in tincidunt lacus malesuada at. Proin iaculis quam non viverra aliquet. "
        findViewById<TextView>(R.id.userName).text = "$userName, $userAge"
    }
}