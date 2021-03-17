package fr.uge.lootin

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.google.android.material.button.MaterialButton
import fr.uge.lootin.httpUtils.GsonGETRequest
import fr.uge.lootin.httpUtils.WebRequestUtils
import fr.uge.lootin.models.UserFull
import fr.uge.lootin.models.Users

class DisplayProfileFragment : DialogFragment() {

    private val url: String = "http://192.168.1.18:8080"
    private var userId: String = ""
    private var token: String = ""
    private lateinit var queue: RequestQueue
    private lateinit var user: Users

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        user = arguments?.getSerializable("USER") as Users
        var view = inflater.inflate(R.layout.fragment_display_profile, container, false)
        displayUserDetails(user, view)
        displayUserImage(user, view)
        loadCompleteUser()
        isCancelable = false
        return view
    }

    fun displayUserImage(user: Users, view: View){
        val decodedString: ByteArray = Base64.decode(user.image, Base64.DEFAULT)
        val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
        view.findViewById<ImageView>(R.id.userPic).setImageBitmap(decodedByte)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.queue = Volley.newRequestQueue(activity?.applicationContext)
        view?.findViewById<TextView>(R.id.userBiography)?.text = "ok"

        userId = arguments?.getString("USER_ID").toString()
        token = arguments?.getString("TOKEN").toString()

    }

    override fun getTheme(): Int {
        return R.style.DialogTheme
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<MaterialButton>(R.id.lessButton).setOnClickListener {
            // activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit();
            activity?.supportFragmentManager?.popBackStack();
        }

    }


    private fun loadCompleteUser() {
        val url = "$url/profile/full/$userId"
        val map = HashMap<String, String>()
        map["Authorization"] = "Bearer $token"
        val request = GsonGETRequest(url, UserFull::class.java, map,
            { response ->
                Log.i("result", "result : $response")
                WebRequestUtils.onResult(response)
                displayUserGames(response)
            },
            { error -> WebRequestUtils.onError(error) }
        )
        queue.add(request)
    }

    private fun displayUserDetails(user: Users, view: View){
        view.findViewById<TextView>(R.id.userName).text = user.login + ", " + user.age
        view.findViewById<TextView>(R.id.userBiography).text = user.description
        view.findViewById<TextView>(R.id.userGender).text = user.gender

    }
    private fun displayUserGames(user: UserFull) {
     /*   view?.findViewById<TextView>(R.id.userName)?.text =
            user.login + ", " + user.age.toString()
        view?.findViewById<TextView>(R.id.userBiography)?.setText(user.description)
        val decodedString: ByteArray = Base64.decode(user.image, Base64.DEFAULT)
        val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)*/
        //view?.findViewById<ImageView>(R.id.userPic)?.setImageBitmap(decodedByte)
    }




}