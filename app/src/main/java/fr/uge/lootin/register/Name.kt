package fr.uge.lootin.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import fr.uge.lootin.R

class Name : Fragment() {
    lateinit var layout: View

    private fun showToastError() {
        Toast.makeText(
            activity?.applicationContext,
            activity?.applicationContext?.getString(R.string.formError),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun loadBirthdayFragment() {
        (activity as RegisterActivity).supportFragmentManager.beginTransaction().remove(this)
            .commit()
        val firstFrag = Birthday.registerInstance()
        (activity as RegisterActivity).supportFragmentManager.beginTransaction()
            .add(R.id.register_fragment, firstFrag, "birthdayFragment")
            .addToBackStack("birthdayFragment").commit()
    }

    private fun checkParamsWhatsYourName(): Boolean {
        val firstName = layout.findViewById<EditText>(R.id.firstNameText).text.toString()
        val lastName = layout.findViewById<EditText>(R.id.lastNameText).text.toString()
        if (firstName == "" || lastName == "") {
            showToastError()
            return false
        }
        (activity as RegisterActivity).setFirstName(firstName)
        (activity as RegisterActivity).setLastName(lastName)
        return true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        layout = inflater.inflate(R.layout.name_layout, container, false)
        layout.findViewById<Button>(R.id.nextButtonWhatsYourName).setOnClickListener {
            if (checkParamsWhatsYourName()) {
                loadBirthdayFragment()
            }
        }
        return layout
    }

    companion object {
        fun registerInstance(): Name {
            return Name()
        }
    }
}