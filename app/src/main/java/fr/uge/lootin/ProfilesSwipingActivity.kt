package fr.uge.lootin

import android.R.attr.maxHeight
import android.R.attr.maxWidth
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.NetworkImageView
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley


class ProfilesSwipingActivity : AppCompatActivity() {

    // faire dans un asynch sinon erreur => check ce que miranda à fait
    private val profilesList: ArrayList<UserProfile> = ArrayList()
    private var index: Int = 0
    private lateinit var queue: RequestQueue

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profiles_swiping)
        queue = Volley.newRequestQueue(this)
        profilesList.add(UserProfile("XxMichellexX", "J'adore jouer, je m'appelle michelle, ça rime avec echelle ou nique ta mère Ah ça rime pas ?", "https://i.pinimg.com/originals/1f/ce/37/1fce3791e19f6b1e88349808aa7b3010.jpg", 23))
        profilesList.add(
            UserProfile(
                "Playeuse",
                "BlablablablaBDEblablablaECLATEblablablaBDKblablablaPASMIEUXblablabla",
                "https://i2-prod.essexlive.news/incoming/article3540005.ece/ALTERNATES/s1200c/0_Tinder-date-disaster.jpg",
                22

            )
        )
        profilesList.add(
            UserProfile(
                "Rimsky",
                "Kalashcriminel like brrrrrr",
                "https://i.dailymail.co.uk/1s/2019/11/22/08/21327722-7713851-Grace_Millane_pictured_was_on_a_round_the_world_trip_when_she_di-m-18_1574410407590.jpg",
                27
            )
        )
        profilesList.add(
            UserProfile(
                "Paola69",
                "hola me gusta los tacos de El-paso",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQMBLacTzQULDRXLNFomvxHQ-71aY8aVA-9Hg&usqp=CAU",
                26
            )
        )
        displayNextProfile()

        findViewById<com.google.android.material.button.MaterialButton>(R.id.likeButton).setOnClickListener {
            if (profilesList.size <= index + 1) {
                // Ici recharger les profils
            } else {
                // call rest api => like
                index++
                displayNextProfile()
            }
        }

        findViewById<com.google.android.material.button.MaterialButton>(R.id.nextButton).setOnClickListener {
            if (profilesList.size <= index + 1) {
                // Ici recharger les profils
            } else {
                index++
                displayNextProfile()
            }
        }

    }

    private fun httpRequest(
        url: String,
        responseListener: Response.Listener<String>,
        errorListener: Response.ErrorListener
    ){
        // Request a string response from the provided URL.
        val stringRequest = StringRequest(
            Request.Method.GET,
            url,//{ response ->  textView.text = "Response is: ${response.substring(0, 500)}" }
            responseListener,
            errorListener
        )
        // Add the request to the RequestQueue.
        queue.add(stringRequest)
    }


    private fun imageRequest(url: String){
        val requestQueue = Volley.newRequestQueue(applicationContext)
        val imageView: ImageView = findViewById(R.id.userPicture)
        val ir = ImageRequest(
            url,
            { response -> imageView.setImageBitmap(response) }, 300, 300, ImageView.ScaleType.CENTER,  Bitmap.Config.RGB_565
        ) { Log.e("http_error", "Image Load Error: ") }

        requestQueue.add(ir)
    }

    @SuppressLint("WrongViewCast")
    private fun displayNextProfile() {
        val user = profilesList[index]
        val url = user.profilePicture
        /*httpRequest(url, { response ->
            var nv = findViewById<NetworkImageView>(R.id.userPicture);
            //nv.setDefaultImageResId(R.drawable.default_image); // image for loading...
            nv.setImageUrl(imageUrl, ImgController.getInstance().getImageLoader())
        }, { Log.i("http_error", "an error has been encountered while sending http request") })*/
        imageRequest(url)
        var bio = user.biography
        if (bio.length > 30) bio = bio.substring(0, 30) + "..."
        findViewById<TextView>(R.id.username_textView).text = user.username + ", " + user.age.toString()
        findViewById<TextView>(R.id.biography_textView).text = bio
    }






}