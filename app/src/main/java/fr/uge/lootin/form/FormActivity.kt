package fr.uge.lootin.form

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import fr.uge.lootin.DefaultBadTokenHandler
import fr.uge.lootin.R
import fr.uge.lootin.config.Configuration
import fr.uge.lootin.settings.Description
import fr.uge.lootin.settings.GamesList
import fr.uge.lootin.signin.SignInActivity
import java.io.ByteArrayOutputStream


class FormActivity : AppCompatActivity() {
    lateinit var profilImage: Bitmap
    private var description: String = ""
    private var username: String = ""
    private var password: String = ""
    private var firstName: String = ""
    private var lastName: String = ""
    private var age: Int = 0
    private var gender: String = ""
    private var attraction: String = ""
    private var email: String = ""
    private var baseUrl = ""

    fun setDescription(desc: String) {
        description = desc
    }

    fun setProfileImage(image: Bitmap) {
        this.profilImage = image
    }

    private fun loadSignInActivity(gl: GamesList) {
        supportFragmentManager.beginTransaction().remove(gl).commit()
        val intent = Intent(this, SignInActivity::class.java)
        startActivity(intent)
    }

    fun registerRequest(games: List<String>, gl: GamesList) {
        val queue = Volley.newRequestQueue(this.applicationContext)
        val url = "$baseUrl/register"
        Log.i("test", "post upload image request")
        val jsonObjectRequest = object : VolleyFileUploadRequest(Method.POST, url,
            Response.Listener { response ->
                Log.i("test", response.statusCode.toString())
                loadSignInActivity(gl)
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
                if (error is AuthFailureError) {
                    DefaultBadTokenHandler.handleBadRequest(this@FormActivity)
                } else {
                    Thread.sleep(10000)
                    registerRequest(games, gl)
                }
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
                params["email"] = email
                params["games"] = games.joinToString()

                return params
            }

            override fun getByteData(): Map<String, FileDataPart> {
                val params = HashMap<String, FileDataPart>()
                val stream = ByteArrayOutputStream()
                profilImage.compress(Bitmap.CompressFormat.JPEG, 80, stream)
                params["file"] = FileDataPart("image", stream.toByteArray(), "jpeg")
                return params
            }
        }

        queue.add(jsonObjectRequest)
    }

    private fun getIntentValues() {
        this.username = intent.getStringExtra("username").toString()
        this.password = intent.getStringExtra("password").toString()
        this.firstName = intent.getStringExtra("firstName").toString()
        this.lastName = intent.getStringExtra("lastName").toString()
        this.age = intent.getIntExtra("age", -1)
        this.gender = intent.getStringExtra("gender").toString()
        this.attraction = intent.getStringExtra("attraction").toString()
        this.email = intent.getStringExtra("email").toString()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        baseUrl = Configuration.getUrl(this)
        getIntentValues()
        setContentView(R.layout.activity_form)
        val firstFrag = Description.registerInstance()
        supportFragmentManager.beginTransaction()
            .add(R.id.form_fragment, firstFrag, "DescriptionFragment")
            .addToBackStack("DescriptionFragment").commit()
    }
}