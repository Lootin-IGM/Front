package fr.uge.lootin.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import fr.uge.lootin.R
import fr.uge.lootin.form.FormActivity

class Description : Fragment() {

    private fun loadFragmentPicture(description: String) {
        (activity as FormActivity).setDescription(description)
        (activity as FormActivity).supportFragmentManager.beginTransaction().remove(this).commit()
        val firstFrag = TakePicture()
        (activity as FormActivity).supportFragmentManager.beginTransaction()
            .add(R.id.form_fragment, firstFrag, "PictureFragment")
            .addToBackStack("PrictureFragment").commit()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layout = inflater.inflate(R.layout.fragment_description, container, false)
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
        return layout
    }
}
