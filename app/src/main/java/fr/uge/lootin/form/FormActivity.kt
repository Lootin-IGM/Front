package fr.uge.lootin.form

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fr.uge.lootin.R

class FormActivity : AppCompatActivity() {
    lateinit var gameRV: RecyclerView
    lateinit var gameAdapter: GameAdapter
    private val GAMES_PATH = "games"


    private fun createLayoutManager(): RecyclerView.LayoutManager? {
        //return LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        return GridLayoutManager(this, 2, LinearLayoutManager.VERTICAL, false)
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
            else setContentView(R.layout.activity_form)
        }

        /*
        Log.i("test", Game.loadCards(this, GAMES_PATH).toString())

        gameRV = findViewById(R.id.gameRecyclerView)
        gameAdapter = GameAdapter(Game.loadCards(this, GAMES_PATH)!!)
        gameRV.adapter = gameAdapter
        gameRV.layoutManager = createLayoutManager()*/
    }
}