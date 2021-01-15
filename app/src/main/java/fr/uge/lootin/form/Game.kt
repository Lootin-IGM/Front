package fr.uge.lootin.form

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import java.io.IOException
import java.io.InputStream
import java.io.Serializable
import java.util.*

class Game(private val path: String, private val name: String) : Comparable<Game>, Serializable {

    companion object {
        fun loadCards(context: Context, path: String): List<Game>? {
            val l: MutableList<Game> = ArrayList<Game>()
            try {
                for (filename in context.assets.list(path)!!) {
                    Log.i("test", filename)
                    val name = filename.substring(0, filename.indexOf("."))
                    l.add(Game("$path/$filename", name))
                }
            } catch (e: IOException) {
                Log.e("error", e.message, e)
            }
            return l
        }
    }

    @Transient
    private var cachedBitmap: Bitmap? = null

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
    }

    override fun compareTo(other: Game): Int {
        return name.compareTo(other.name)
    }
}