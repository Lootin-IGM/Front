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

class Pseudo : Fragment() {
    lateinit var layout: View

    private fun showToastError() {
        Toast.makeText(
            activity?.applicationContext,
            activity?.applicationContext?.getString(R.string.formError),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun showBadPasswordError() {
        Toast.makeText(
            activity?.applicationContext,
            activity?.applicationContext?.getString(R.string.wrongPassword),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun checkParamsRegister(): Boolean {
        val pseudo = layout.findViewById<EditText>(R.id.choosePseudoInput).text.toString()
        val password = layout.findViewById<EditText>(R.id.choosePasswordInput).text.toString()
        val confirmPassword =
            layout.findViewById<EditText>(R.id.confirmPasswordInput).text.toString()
        if (pseudo.equals("") || password.equals("") || confirmPassword.equals("")) {
            showToastError()
            return false
        }
        if (!password.equals(confirmPassword)) {
            showBadPasswordError()
            return false
        }
        (activity as RegisterActivity).setUsername(pseudo)
        (activity as RegisterActivity).setPassword(password)
        return true
    }

    private fun loadEmailFragment() {
        val firstFrag = Email.registerInstance()
        (activity as RegisterActivity).supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in_r_l,
                R.anim.fade_out_r_l, R.anim.fade_in_r_l, R.anim.slide_out_r_l
            )
            .replace(R.id.register_fragment, firstFrag, "emailFragment")
            .addToBackStack("emailFragment").commit()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        layout = inflater.inflate(R.layout.register_layout, container, false)
        layout.findViewById<Button>(R.id.nextButtonChoosePseudo).setOnClickListener {
            if (checkParamsRegister()) {
                loadEmailFragment()
            }
        }
        return layout
    }

    companion object {
        fun registerInstance(): Pseudo {
            return Pseudo()
        }
    }
}