package fr.uge.lootin.form

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import fr.uge.lootin.GsonGETRequest
import fr.uge.lootin.R
import java.io.ByteArrayOutputStream


class FormActivity : AppCompatActivity() {
    lateinit var gameRV: RecyclerView
    lateinit var gameAdapter: GameAdapter
    private val GAMES_PATH = "Alls"
    private val TOKEN =
        "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJMb3Vsb3UiLCJleHAiOjE2MTU1MzEwMDksImlhdCI6MTYxNTQ5NTAwOX0.GQdL2bDqX0oJ3BS-_ycmvKb0gh448R2ktYTAMdsksm0"
    lateinit var profilImage: Bitmap

    private fun createLayoutManager(): RecyclerView.LayoutManager? {
        return GridLayoutManager(this, 4, LinearLayoutManager.VERTICAL, false)
        //return LinearLayoutManager(this)
        //return GridLayout
    }

    private fun capturePhoto() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, 200)
    }

    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 100)
    }

    private fun checkIfNoImage(): Boolean {
        var imageView = findViewById<ImageView>(R.id.imageOnPicturePage)
        if (imageView.drawable == null) return false
        return true
    }

    private fun uploadImage(queue: RequestQueue, name: String, image: Bitmap) {
        val url = "http://192.168.1.86:8080/images/upload"
        Log.i("test", "post upload image request")
        val jsonObjectRequest = object : VolleyFileUploadRequest(Request.Method.POST, url,
            Response.Listener { response ->
                Log.i("test", response.statusCode.toString())

            }, Response.ErrorListener { error ->
                Log.i(
                    "test", "error while trying to connect\n"
                            + error.toString() + "\n"
                            + error.networkResponse + "\n"
                            + error.localizedMessage + "\n"
                            + error.message + "\n"
                            + error.cause + "\n"
                            + error.stackTrace.toString()
                )
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): MutableMap<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["Authorization"] = "Bearer $TOKEN"

                return params
            }

            override fun getParams(): MutableMap<String, String> {
                var params = HashMap<String, String>()
                params["name"] = name
                return params
            }

            override fun getByteData(): Map<String, FileDataPart>? {
                var params = HashMap<String, FileDataPart>()
                val stream = ByteArrayOutputStream()
                image.compress(Bitmap.CompressFormat.JPEG, 80, stream)
                params["image"] = FileDataPart("image", stream.toByteArray(), "jpeg")
                return params
            }
        }

        queue.add(jsonObjectRequest)
    }

    private fun showGamesForm(response: GameListDto) {
        setContentView(R.layout.activity_form)
        gameRV = findViewById(R.id.gameRecyclerView)
        var cards = Game.loadCards(this, response)
        gameAdapter = GameAdapter(cards!!)
        gameRV.adapter = gameAdapter
        gameRV.layoutManager = createLayoutManager()

        findViewById<Button>(R.id.validateButtonOnFormPage).setOnClickListener {
            cards!!.forEach { x -> if (x.isSelected()) Log.i("test", x.getName()) }
        }
    }

    private fun getAllGames(queue: RequestQueue) {
        val url = "http://192.168.1.86:8080/games/"
        val map = HashMap<String, String>()
        map["Authorization"] = "Bearer $TOKEN"
        Log.i("test", "get all games request")
        val request = GsonGETRequest(url, GameListDto::class.java, map,
            { response ->
                Log.i("test", "response:")
                Log.i("test", response.toString())
                showGamesForm(response)
            },
            { error ->
                Log.i(
                    "my_log", "error while trying to verify connexion\n"
                            + error.toString() + "\n"
                            + error.networkResponse + "\n"
                            + error.localizedMessage + "\n"
                            + error.message + "\n"
                            + error.cause + "\n"
                            + error.stackTrace.toString()
                )
            })
        queue.add(request)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 200 && data != null) {
            var imageView = findViewById<ImageView>(R.id.imageOnPicturePage)
            imageView.setImageBitmap(data.extras!!.get("data") as Bitmap)
            this.profilImage = data.extras!!.get("data") as Bitmap
        }
        if (resultCode == Activity.RESULT_OK && requestCode == 100) {
            var imageView = findViewById<ImageView>(R.id.imageOnPicturePage)
            imageView.setImageURI(data?.data)
            this.profilImage =
                (imageView.drawable as BitmapDrawable).bitmap // to get bitmap from imageView
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pseudo)

        findViewById<Button>(R.id.nextButton).setOnClickListener {
            if (findViewById<EditText>(R.id.pseudoText).text.toString() == "") Toast.makeText(
                    applicationContext,
                    applicationContext.getString(R.string.pseudoMessageError),
                    Toast.LENGTH_SHORT
            ).show()
            else {
                setContentView(R.layout.activity_take_picture)
                findViewById<Button>(R.id.takePictureButton).setOnClickListener { capturePhoto() }
                findViewById<Button>(R.id.pickPictureButton).setOnClickListener { openGalleryForImage() }
                findViewById<Button>(R.id.nextButtonOnTakePicture).setOnClickListener {
                    if (!checkIfNoImage()) Toast.makeText(
                            applicationContext,
                            applicationContext.getString(R.string.imageError),
                            Toast.LENGTH_SHORT
                    ).show()
                    else {
                        val queue = Volley.newRequestQueue(this)
                        uploadImage(queue, "test", this.profilImage)
                        //time for some tests
                        getAllGames(queue)

                    }
                }
            }
        }
    }
}