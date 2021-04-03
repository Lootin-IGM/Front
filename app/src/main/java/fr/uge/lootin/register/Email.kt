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

class Email : Fragment() {
    lateinit var layout: View

    private fun showToastError(id: Int) {
        Toast.makeText(
            activity?.applicationContext,
            activity?.applicationContext?.getString(id),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun loadNameFragment() {
        val firstFrag = Name.registerInstance()
        (activity as RegisterActivity).supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in_r_l,
                R.anim.fade_out_r_l, R.anim.fade_in_r_l, R.anim.slide_out_r_l
            )
            .replace(R.id.register_fragment, firstFrag, "nameFragment")
            .addToBackStack("nameFragment").commit()
    }

    private fun checkParamEmail(): Boolean {
        val email = layout.findViewById<EditText>(R.id.FragmentEmailText).text.toString()
        if (email == "") {
            showToastError(R.string.formError)
            return false
        }
        if (!email.contains("@")) {
            showToastError(R.string.emailError)
            return false
        }
        (activity as RegisterActivity).setEmail(email)
        return true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        layout = inflater.inflate(R.layout.fragment_email, container, false)
        layout.findViewById<Button>(R.id.FragmentNextButtonEmail).setOnClickListener {
            if (checkParamEmail()) {
                loadNameFragment()
            }
        }
        return layout
    }

    companion object {
        fun registerInstance(): Email {
            return Email()
        }
    }
}
