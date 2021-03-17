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
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import fr.uge.lootin.GsonGETRequest
import fr.uge.lootin.R
import java.io.ByteArrayOutputStream


class FormActivity : AppCompatActivity() {
    lateinit var gameRV: RecyclerView
    lateinit var gameAdapter: GameAdapter
    lateinit var profilImage: Bitmap
    private var description: String = ""
    private var username: String = ""
    private var password: String = ""
    private var firstName: String = ""
    private var lastName: String = ""
    private var age: Int = 0
    private var gender: String = ""
    private var attraction: String = ""

    private fun createLayoutManager(): RecyclerView.LayoutManager {
        return GridLayoutManager(this, 4, LinearLayoutManager.VERTICAL, false)
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
        val imageView = findViewById<ImageView>(R.id.imageOnPicturePage)
        if (imageView.drawable == null) return false
        return true
    }

    private fun registerRequest(queue: RequestQueue, image: Bitmap, username: String, password: String, firstName: String, lastName: String, games: List<String>, description: String, age: Int, gender: String, attraction: String) {
        val url = "http://192.168.1.86:8080/register"
        Log.i("test", "post upload image request")
        val jsonObjectRequest = object : VolleyFileUploadRequest(Method.POST, url,
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

            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                //Log.i("test", "username=$username, password=$password, firstname=$firstName, lastname=$lastName, game=${games.joinToString()}, age=${age.toString()}, gender=$gender, attraction=$attraction")
                params["username"] = username
                params["password"] = password
                params["firstName"] = firstName
                params["lastName"] = lastName
                params["description"] = description
                params["age"] = age.toString()
                params["gender"] = gender
                params["attraction"] = attraction
                params["games"] = games.joinToString()

                return params
            }

            override fun getByteData(): Map<String, FileDataPart> {
                val params = HashMap<String, FileDataPart>()
                val stream = ByteArrayOutputStream()
                image.compress(Bitmap.CompressFormat.JPEG, 80, stream)
                params["file"] = FileDataPart("image", stream.toByteArray(), "jpeg")
                return params
            }
        }

        queue.add(jsonObjectRequest)
    }

    private fun launchGamesActivity(response: GameListDto, queue: RequestQueue) {
        setContentView(R.layout.activity_form)
        val gamesSelected = ArrayList<String>()
        gameRV = findViewById(R.id.gameRecyclerView)
        val cards = Game.loadCards(this, response)
        gameAdapter = GameAdapter(cards!!)
        gameRV.adapter = gameAdapter
        gameRV.layoutManager = createLayoutManager()

        findViewById<Button>(R.id.validateButtonOnFormPage).setOnClickListener {
            cards.forEach { x ->
                if (x.isSelected()) {
                    gamesSelected.add(x.getName())
                }
            }
            if (gamesSelected.isEmpty()) Toast.makeText(
                    applicationContext,
                    applicationContext.getString(R.string.gamesMessageError),
                    Toast.LENGTH_SHORT
            ).show()
            else registerRequest(queue, this.profilImage, this.username, this.password, this.firstName, this.lastName, gamesSelected, this.description, this.age, this.gender, this.attraction)
        }
    }

    private fun getAllGames(queue: RequestQueue) {
        val url = "http://192.168.1.86:8080/games/"
        val map = HashMap<String, String>()
        Log.i("test", "get all games request")
        val request = GsonGETRequest(url, GameListDto::class.java, map,
                { response ->
                    launchGamesActivity(response, queue)
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
            val imageView = findViewById<ImageView>(R.id.imageOnPicturePage)
            imageView.setImageBitmap(data.extras!!.get("data") as Bitmap)
            this.profilImage = data.extras!!.get("data") as Bitmap
        }
        if (resultCode == Activity.RESULT_OK && requestCode == 100) {
            val imageView = findViewById<ImageView>(R.id.imageOnPicturePage)
            imageView.setImageURI(data?.data)
            this.profilImage =
                    (imageView.drawable as BitmapDrawable).bitmap // to get bitmap from imageView
        }
    }

    private fun launchTakePictureActivity() {
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
                getAllGames(queue)
            }
        }
    }

    private fun launchDescriptionActivity() {
        setContentView(R.layout.description_layout)
        findViewById<Button>(R.id.nextButtonDescription).setOnClickListener {
            if (findViewById<EditText>(R.id.descriptionText).text.toString() == "") Toast.makeText(
                    applicationContext,
                    applicationContext.getString(R.string.descriptionError),
                    Toast.LENGTH_SHORT
            ).show()
            else {
                this.description = findViewById<EditText>(R.id.descriptionText).text.toString()
                launchTakePictureActivity()
            }
        }
    }

    private fun getIntentValues() {
        this.username = intent.getStringExtra("username").toString()
        this.password = intent.getStringExtra("password").toString()
        this.firstName = intent.getStringExtra("firstName").toString()
        this.lastName = intent.getStringExtra("lastName").toString()
        this.age = intent.getIntExtra("age", -1)
        this.gender = intent.getStringExtra("gender").toString()
        this.attraction = intent.getStringExtra("attraction").toString()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getIntentValues()
        launchDescriptionActivity()
    }
}