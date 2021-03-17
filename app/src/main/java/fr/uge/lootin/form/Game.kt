package fr.uge.lootin.form

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import java.io.IOException
import java.io.Serializable
import java.util.*

class Game(private val image: Bitmap, private val name: String) : Comparable<Game>, Serializable {
    private var selected: Boolean = false;

    companion object {
        fun loadCards(context: Context, gameList: GameListDto): List<Game>? {
            val l: MutableList<Game> = ArrayList<Game>()
            try {
                for (g in gameList.games) {
                    Log.i("test", g.gameName)
                    val imageBytes = Base64.decode(g.image.image, Base64.DEFAULT)
                    val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                    l.add(Game(decodedImage, g.image.name))
                    Log.i("test", "game added !")
                    /*
                    val name = filename.substring(0, filename.indexOf("."))
                    l.add(Game("$path/$filename", name))*/
                }
            } catch (e: IOException) {
                Log.e("error", e.message, e)
            }
            return l
        }
    }

    @Transient
    private var cachedBitmap: Bitmap? = null

    fun clicked() {
        selected = !selected
    }

    fun getName(): String {
        return name
    }

    fun isSelected(): Boolean {
        return selected
    }

    fun getBitmap(): Bitmap {
        return image
    }

    /*

    fun getBitmap(context: Context): Bitmap? {
        var ins: InputStream? = null
        try {
            ins = context.assets.open(path)
            cachedBitmap = BitmapFactory.decodeStream(ins)
        } catch (e: IOException) {
            try {
                ins!!.close()
            } catch (e2: IOException) {
            }
        }
        return cachedBitmap
    }*/

    override fun compareTo(other: Game): Int {
        return name.compareTo(other.name)
    }
}