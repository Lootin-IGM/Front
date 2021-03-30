package fr.uge.lootin.settings

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import fr.uge.lootin.DefaultBadTokenHandler
import fr.uge.lootin.ProfilesSwipingActivity
import fr.uge.lootin.R
import fr.uge.lootin.config.Configuration
import fr.uge.lootin.form.FormActivity
import fr.uge.lootin.httpUtils.GsonGETRequest
import fr.uge.lootin.models.DescriptionDto
import org.json.JSONObject

class Description : Fragment() {
    lateinit var layout: View
    private var token: String = ""
    lateinit var type: String
    private var baseUrl = ""
    private var contextActivity: Context? = null

    private fun loadFragmentPicture(description: String) {
        (activity as FormActivity).setDescription(description)
        (activity as FormActivity).supportFragmentManager.beginTransaction().remove(this).commit()
        val firstFrag = TakePicture.registerInstance()
        (activity as FormActivity).supportFragmentManager.beginTransaction()
            .add(R.id.form_fragment, firstFrag, "PictureFragment")
            .addToBackStack("PictureFragment").commit()
    }

    private fun setNextButtonRegister() {
        layout.findViewById<Button>(R.id.FragmentNextButtonDescription).setOnClickListener {
            if (layout.findViewById<EditText>(R.id.FragmentDescriptionText).text.toString() == "") Toast.makeText(
                activity?.applicationContext,
                activity?.applicationContext?.getString(R.string.descriptionError),
                Toast.LENGTH_SHORT
            ).show()
            else {
                val description =
                    layout.findViewById<EditText>(R.id.FragmentDescriptionText).text.toString()
                loadFragmentPicture(description)
            }
        }
    }

    private fun closeDescriptionFragment() {
        val settingsFrag = DisplaySettingsFragment.newInstance(token)
        (activity as ProfilesSwipingActivity).supportFragmentManager.beginTransaction().remove(this)
            .commit()
        (activity as ProfilesSwipingActivity).supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container_view, settingsFrag, "settingsFragment")
            .addToBackStack("settingsFragment").commit()
    }

    private fun updateDescriptionRequest(description: String) {
        val queue = Volley.newRequestQueue(activity?.applicationContext)
        val url = "$baseUrl/profile/description"
        Log.i(
            "test",
            "verify connexion request " + JSONObject(
                "{\"description\": " + Gson().toJson(
                    description
                ) + "}"
            )
        )
        val stringRequest = object : JsonObjectRequest(
            Method.POST,
            url,
            JSONObject("{\"description\": " + Gson().toJson(description) + "}"),
            Response.Listener { response ->
                Log.i("test", "Response ok: $response")
                closeDescriptionFragment()
            },
            Response.ErrorListener { error ->
                Log.i(
                    "test", "error while trying to verify connexion\n"
                            + error.toString() + "\n"
                            + error.networkResponse + "\n"
                            + error.localizedMessage + "\n"
                            + error.message + "\n"
                            + error.cause + "\n"
                            + error.stackTrace.toString()
                )
                if (error is AuthFailureError) {
                    DefaultBadTokenHandler.handleBadRequest(contextActivity!!)
                }
            }
        ) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String>? {
                val params: MutableMap<String, String> = HashMap()
                params["Authorization"] = "Bearer $token"
                return params
            }
        }
        queue.add(stringRequest)
    }

    private fun setNextButtonSettings() {
        val button = layout.findViewById<Button>(R.id.FragmentNextButtonDescription)
        button.text = getString(R.string.validate)
        button.setOnClickListener {
            if (layout.findViewById<EditText>(R.id.FragmentDescriptionText).text.toString() == "") Toast.makeText(
                activity?.applicationContext,
                activity?.applicationContext?.getString(R.string.descriptionError),
                Toast.LENGTH_SHORT
            ).show()
            else {
                val description =
                    layout.findViewById<EditText>(R.id.FragmentDescriptionText).text.toString()
                updateDescriptionRequest(description)
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        contextActivity = context
    }

    private fun getMyDescriptionRequest() {
        val queue = Volley.newRequestQueue(activity?.applicationContext)
        val url = "$baseUrl/profile/myDescription"
        val map = HashMap<String, String>()
        map["Authorization"] = "Bearer $token"
        Log.i("test", "get my description request")
        val request = GsonGETRequest(
            url, DescriptionDto::class.java, map,
            { response ->
                val description = response.description
                layout.findViewById<EditText>(R.id.FragmentDescriptionText).text =
                    Editable.Factory.getInstance().newEditable(description)
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
                if (error is AuthFailureError) {
                    DefaultBadTokenHandler.handleBadRequest(contextActivity!!)
                }
            })
        queue.add(request)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        baseUrl = Configuration.getUrl(activity?.applicationContext!!)
        layout = inflater.inflate(R.layout.fragment_description, container, false)
        type = requireArguments().getString("type").toString()
        if (type == "register") setNextButtonRegister()
        if (type == "settings") {
            token = requireArguments().getString("token").toString()
            getMyDescriptionRequest()
            setNextButtonSettings()
        }
        return layout
    }

    companion object {
        fun registerInstance(): Description {
            var fragment = Description()
            val args = Bundle()
            args.putString("type", "register")
            fragment.arguments = args
            return fragment
        }

        fun settingsInstance(token: String): Description {
            var fragment = Description()
            val args = Bundle()
            args.putString("type", "settings")
            args.putString("token", token)
            fragment.arguments = args
            return fragment
        }
    }
}
