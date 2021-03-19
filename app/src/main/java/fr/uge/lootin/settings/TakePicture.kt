package fr.uge.lootin.settings

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import fr.uge.lootin.R
import fr.uge.lootin.form.FormActivity

class TakePicture : Fragment() {

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
            (activity as FormActivity).setProfileImage(data.extras!!.get("data") as Bitmap)
        }
        if (resultCode == Activity.RESULT_OK && requestCode == 100) {
            val imageView = view?.findViewById<ImageView>(R.id.FragmentImageOnPicturePage)
            imageView?.setImageURI(data?.data)
            (activity as FormActivity).setProfileImage((imageView?.drawable as BitmapDrawable).bitmap)
        }
    }

    private fun loadGamesFragment() {
        (activity as FormActivity).supportFragmentManager.beginTransaction().remove(this).commit()
        val gamesFrag = GamesList.registerInstance()
        (activity as FormActivity).supportFragmentManager.beginTransaction()
            .add(R.id.form_fragment, gamesFrag, "GamesFragment").addToBackStack("GamesFragment")
            .commit()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layout =
            inflater.inflate(R.layout.fragment_take_picture, container, false)
        layout.findViewById<Button>(R.id.FragmentTakePictureButton)
            .setOnClickListener { capturePhoto() }
        layout.findViewById<Button>(R.id.FragmentPickPictureButton)
            .setOnClickListener { openGalleryForImage() }
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
        return layout
    }
}