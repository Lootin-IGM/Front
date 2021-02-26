package fr.uge.lootin.form

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
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
import fr.uge.lootin.R

class FormActivity : AppCompatActivity() {
    lateinit var gameRV: RecyclerView
    lateinit var gameAdapter: GameAdapter
    private val GAMES_PATH = "Alls"


    private fun createLayoutManager(): RecyclerView.LayoutManager? {
        return GridLayoutManager(this, 4, LinearLayoutManager.VERTICAL, false)
        //return LinearLayoutManager(this)
        //return GridLayout
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
        var imageView = findViewById<ImageView>(R.id.imageOnPicturePage)
        if (imageView.drawable == null) return false
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 200 && data != null) {
            var imageView = findViewById<ImageView>(R.id.imageOnPicturePage)
            imageView.setImageBitmap(data.extras!!.get("data") as Bitmap)
        }
        if (resultCode == Activity.RESULT_OK && requestCode == 100) {
            var imageView = findViewById<ImageView>(R.id.imageOnPicturePage)
            imageView.setImageURI(data?.data)
            //var bitmap = (imageView.drawable as BitmapDrawable).bitmap // to get bitmap from imageView
        }
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
            else {
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
                        setContentView(R.layout.activity_form)
                        gameRV = findViewById(R.id.gameRecyclerView)
                        var cards = Game.loadCards(this, GAMES_PATH)
                        gameAdapter = GameAdapter(cards!!)
                        gameRV.adapter = gameAdapter
                        gameRV.layoutManager = createLayoutManager()

                        findViewById<Button>(R.id.validateButtonOnFormPage).setOnClickListener {
                            cards!!.forEach { x -> if (x.isSelected()) Log.i("test", x.getName()) }
                        }
                    }
                }
                /*
                setContentView(R.layout.activity_form)
                gameRV = findViewById(R.id.gameRecyclerView)
                var cards = Game.loadCards(this, GAMES_PATH)
                gameAdapter = GameAdapter(cards!!)
                gameRV.adapter = gameAdapter
                gameRV.layoutManager = createLayoutManager()

                findViewById<Button>(R.id.validateButtonOnFormPage).setOnClickListener {
                    cards.forEach { x -> if (x.isSelected()) Log.i("test", x.getName()) }
                }*/
            }
        }
    }
}