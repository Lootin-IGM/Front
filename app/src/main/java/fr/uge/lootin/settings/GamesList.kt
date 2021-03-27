package fr.uge.lootin.settings

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import fr.uge.lootin.DefaultBadTokenHandler
import fr.uge.lootin.ProfilesSwipingActivity
import fr.uge.lootin.R
import fr.uge.lootin.config.Configuration
import fr.uge.lootin.form.FormActivity
import fr.uge.lootin.form.Game
import fr.uge.lootin.form.GameAdapter
import fr.uge.lootin.httpUtils.GsonGETRequest
import fr.uge.lootin.models.GameListDto
import org.json.JSONObject


class GamesList : Fragment() {
    lateinit var gameRV: RecyclerView
    lateinit var gameAdapter: GameAdapter
    lateinit var cards: List<Game>
    lateinit var queue: RequestQueue
    private var token: String = ""
    private var type: String = ""
    private var baseUrl = ""

    private fun getAllGames() {
        val url = "$baseUrl/games/"
        val map = HashMap<String, String>()
        Log.i("test", "get all games request")
        val request = GsonGETRequest(
            url, GameListDto::class.java, map,
            { response ->
                cards = Game.loadCards(activity?.applicationContext, response)!!
                gameAdapter = GameAdapter(cards!!)
                gameRV.adapter = gameAdapter
                gameRV.layoutManager = createLayoutManager()
                if (type == "settings") getActualUserGames()
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
                    activity?.let { DefaultBadTokenHandler.handleBadRequest(it.applicationContext) }
                } else {
                    Thread.sleep(10000)
                    getAllGames()
                }
            })
        queue.add(request)
    }

    private fun setRegisterButton(layout: View) {
        val gamesSelected = ArrayList<String>()
        layout.findViewById<Button>(R.id.validateButtonOnGameFragmentPage)?.setOnClickListener {
            cards.forEach { x ->
                if (x.isSelected()) {
                    gamesSelected.add(x.getName())
                }
            }
            if (gamesSelected.isEmpty()) Toast.makeText(
                activity?.applicationContext,
                activity?.applicationContext?.getString(R.string.gamesMessageError),
                Toast.LENGTH_SHORT
            ).show()
            else {
                (activity as FormActivity).registerRequest(gamesSelected, this)
            }
        }
    }

    private fun createLayoutManager(): RecyclerView.LayoutManager {
        return GridLayoutManager(
            activity?.applicationContext,
            4,
            LinearLayoutManager.VERTICAL,
            false
        )
    }

    private fun selectUserGames(gamesOfUser: GameListDto) {
        for (i in cards.indices) {
            gamesOfUser.games.forEach { g ->
                if (g.gameName == cards[i].getName()) {
                    Log.i("test", "game found " + cards[i])
                    cards[i].clicked()
                    gameAdapter.notifyItemChanged(i)
                }
            }
        }
    }

    private fun getActualUserGames() {
        val url = "$baseUrl/games/my"
        val map = HashMap<String, String>()
        map["Authorization"] = "Bearer $token"
        Log.i("test", "get actual user games request")
        val request = GsonGETRequest(url, GameListDto::class.java, map,
            { response ->
                selectUserGames(response)
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
                if (error is AuthFailureError) {
                    activity?.let { DefaultBadTokenHandler.handleBadRequest(it.applicationContext) }
                } else {
                    Thread.sleep(10000)
                    getActualUserGames()
                }
            })
        queue.add(request)
    }

    private fun closeSettingsFragment() {
        (activity as ProfilesSwipingActivity).supportFragmentManager.beginTransaction().remove(this)
            .commit()
        val settingsFrag = DisplaySettingsFragment.newInstance(token)
        (activity as ProfilesSwipingActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_view, settingsFrag, "settingsFragment")
            .addToBackStack("settingsFragment").commit()
    }

    private fun updateGamesRequest(games: List<String>) {
        val url = "$baseUrl/profile/games"
        Log.i(
            "test",
            "verify connexion request " + JSONObject("{\"games\": " + Gson().toJson(games) + "}")
        )
        val stringRequest = object : JsonObjectRequest(Method.POST,
            url,
            JSONObject("{\"games\": " + Gson().toJson(games) + "}"),
            Response.Listener { response ->
                Log.i("test", "Response ok: $response")
                closeSettingsFragment()
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
                    activity?.let { DefaultBadTokenHandler.handleBadRequest(it.applicationContext) }
                } else {
                    Thread.sleep(10000)
                    updateGamesRequest(games)
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

    private fun setSettingsInterface(layout: View) {
        val gamesSelected = ArrayList<String>()
        layout.findViewById<Button>(R.id.validateButtonOnGameFragmentPage)?.setOnClickListener {
            cards.forEach { x ->
                if (x.isSelected()) {
                    gamesSelected.add(x.getName())
                }
            }
            if (gamesSelected.isEmpty()) Toast.makeText(
                activity?.applicationContext,
                activity?.applicationContext?.getString(R.string.gamesMessageError),
                Toast.LENGTH_SHORT
            ).show()
            else {
                updateGamesRequest(gamesSelected)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        baseUrl = Configuration.getUrl(activity?.applicationContext!!)
        // Inflate the layout for this fragment
        val layout =
            inflater.inflate(R.layout.fragment_games_list, container, false)
        type = requireArguments().getString("type").toString()
        queue = Volley.newRequestQueue(activity?.applicationContext)
        gameRV = layout.findViewById(R.id.gameFragmentRecyclerView)!!
        getAllGames()
        if (type == "register") {
            setRegisterButton(layout)
        }
        if (type == "settings") {
            token = requireArguments().getString("token").toString()
            setSettingsInterface(layout)
        }
        return layout
    }

    companion object {
        fun registerInstance(): GamesList {
            var fragment = GamesList()
            val args = Bundle()
            args.putString("type", "register")
            fragment.arguments = args
            return fragment
        }

        fun settingsInstance(token: String): GamesList {
            var fragment = GamesList()
            val args = Bundle()
            args.putString("type", "settings")
            args.putString("token", token)
            fragment.arguments = args
            return fragment
        }
    }


}