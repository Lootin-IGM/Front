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
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import fr.uge.lootin.R
import fr.uge.lootin.form.FormActivity
import fr.uge.lootin.form.Game
import fr.uge.lootin.form.GameAdapter
import fr.uge.lootin.form.GameListDto
import fr.uge.lootin.httpUtils.GsonGETRequest


class GamesList : Fragment() {
    lateinit var gameRV: RecyclerView
    lateinit var gameAdapter: GameAdapter
    lateinit var cards: List<Game>
    lateinit var queue: RequestQueue

    private fun getAllGames() {
        val url = "http://192.168.1.86:8080/games/"
        val map = HashMap<String, String>()
        Log.i("test", "get all games request")
        val request = GsonGETRequest(url, GameListDto::class.java, map,
            { response ->
                cards = Game.loadCards(activity?.applicationContext, response)!!
                gameAdapter = GameAdapter(cards!!)
                gameRV.adapter = gameAdapter
                gameRV.layoutManager = createLayoutManager()
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
                (activity as FormActivity).registerRequest(gamesSelected)
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val layout =
            inflater.inflate(R.layout.fragment_games_list, container, false)
        queue = Volley.newRequestQueue(activity?.applicationContext)
        gameRV = layout.findViewById(R.id.gameFragmentRecyclerView)!!
        getAllGames()

        val type = requireArguments().getString("type").toString()
        if (type == "register") {
            setRegisterButton(layout)
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
    }


}