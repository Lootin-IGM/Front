package fr.uge.lootin.settings

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import fr.uge.lootin.ProfilesSwipingActivity
import fr.uge.lootin.R
import fr.uge.lootin.config.Configuration
import fr.uge.lootin.form.FileDataPart
import fr.uge.lootin.form.FormActivity
import fr.uge.lootin.form.VolleyFileUploadRequest
import fr.uge.lootin.httpUtils.GsonGETRequest
import fr.uge.lootin.models.ImageDto
import java.io.ByteArrayOutputStream

class TakePicture : Fragment() {
    private var token: String = ""
    lateinit var type: String
    lateinit var layout: View
    lateinit var picture: Bitmap
    private var baseUrl = ""

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
        val imageView = view?.findViewById<ImageView>(R.id.FragmentImageOnPicturePage)
        if (imageView?.drawable == null) return false
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 200 && data != null) {
            val imageView = view?.findViewById<ImageView>(R.id.FragmentImageOnPicturePage)
            imageView?.setImageBitmap(data.extras!!.get("data") as Bitmap)
            if (type == "register") (activity as FormActivity).setProfileImage(data.extras!!.get("data") as Bitmap)
            else picture = data.extras!!.get("data") as Bitmap
        }
        if (resultCode == Activity.RESULT_OK && requestCode == 100) {
            val imageView = view?.findViewById<ImageView>(R.id.FragmentImageOnPicturePage)
            imageView?.setImageURI(data?.data)
            if (type == "register") (activity as FormActivity).setProfileImage((imageView?.drawable as BitmapDrawable).bitmap)
            else picture = (imageView?.drawable as BitmapDrawable).bitmap
        }
    }

    private fun loadGamesFragment() {
        val gamesFrag = GamesList.registerInstance()
        (activity as FormActivity).supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in_r_l,
                R.anim.fade_out_r_l, R.anim.fade_in_r_l, R.anim.slide_out_r_l
            )
            .replace(R.id.form_fragment, gamesFrag, "GamesFragment").addToBackStack("GamesFragment")
            .commit()
    }

    private fun nextButtonRegister() {
        layout.findViewById<Button>(R.id.FragmentNextButtonOnTakePicture).setOnClickListener {
            if (!checkIfNoImage()) Toast.makeText(
                activity?.applicationContext,
                activity?.applicationContext?.getString(R.string.imageError),
                Toast.LENGTH_SHORT
            ).show()
            else {
                loadGamesFragment()
            }
        }
    }

    private fun closePictureFragment() {
        val settingsFrag = DisplaySettingsFragment.newInstance(token)
        (activity as ProfilesSwipingActivity).supportFragmentManager.beginTransaction().remove(this)
            .commit()
        (activity as ProfilesSwipingActivity).supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container_view, settingsFrag, "settingsFragment")
            .addToBackStack("settingsFragment").commit()
    }

    private fun getMyPictureRequest() {
        val queue = Volley.newRequestQueue(activity?.applicationContext)
        val url = "$baseUrl/images/my"
        val map = HashMap<String, String>()
        map["Authorization"] = "Bearer $token"
        Log.i("test", "get my image request")
        val request = GsonGETRequest(
            url, ImageDto::class.java, map,
            { response ->
                val imageBytes = Base64.decode(response.image, Base64.DEFAULT)
                val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                val imageView = view?.findViewById<ImageView>(R.id.FragmentImageOnPicturePage)
                imageView?.setImageBitmap(decodedImage)
                picture = decodedImage
            },
            { error ->
                Log.i(
                    "test", "error while trying to verify connexion\n"
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

    private fun updatePictureRequest() {
        val queue = Volley.newRequestQueue(activity?.applicationContext)
        val url = "$baseUrl/profile/image"
        Log.i("test", "post update picture request")
        val jsonObjectRequest = object : VolleyFileUploadRequest(Method.POST, url,
            Response.Listener { response ->
                Log.i("test", response.statusCode.toString())
                closePictureFragment()
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

            override fun getHeaders(): MutableMap<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["Authorization"] = "Bearer $token"
                return params
            }

            override fun getByteData(): Map<String, FileDataPart> {
                val params = HashMap<String, FileDataPart>()
                val stream = ByteArrayOutputStream()
                picture.compress(Bitmap.CompressFormat.JPEG, 80, stream)
                params["image"] = FileDataPart("image", stream.toByteArray(), "jpeg")
                return params
            }
        }

        queue.add(jsonObjectRequest)
    }

    private fun nextButtonSettings() {
        val button = layout.findViewById<Button>(R.id.FragmentNextButtonOnTakePicture)
        button.text = getString(R.string.validate)
        button.setOnClickListener {
            if (!checkIfNoImage()) Toast.makeText(
                activity?.applicationContext,
                activity?.applicationContext?.getString(R.string.imageError),
                Toast.LENGTH_SHORT
            ).show()
            else {
                updatePictureRequest()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        baseUrl = Configuration.getUrl(activity?.applicationContext!!)
        layout =
            inflater.inflate(R.layout.fragment_take_picture, container, false)
        type = requireArguments().getString("type").toString()
        layout.findViewById<Button>(R.id.FragmentTakePictureButton)
            .setOnClickListener { capturePhoto() }
        layout.findViewById<Button>(R.id.FragmentPickPictureButton)
            .setOnClickListener { openGalleryForImage() }
        if (type == "register") {
            nextButtonRegister()
        }
        if (type == "settings") {
            token = requireArguments().getString("token").toString()
            getMyPictureRequest()
            nextButtonSettings()
        }
        return layout
    }

    companion object {
        fun registerInstance(): TakePicture {
            var fragment = TakePicture()
            val args = Bundle()
            args.putString("type", "register")
            fragment.arguments = args
            return fragment
        }

        fun settingsInstance(token: String): TakePicture {
            var fragment = TakePicture()
            val args = Bundle()
            args.putString("type", "settings")
            args.putString("token", token)
            fragment.arguments = args
            return fragment
        }
    }
}