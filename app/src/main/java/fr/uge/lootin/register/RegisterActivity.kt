package fr.uge.lootin.register

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import fr.uge.lootin.R

class RegisterActivity : AppCompatActivity() {
    private var email: String = ""
    private var password: String = ""
    private var confirmPassword: String = ""

    private fun launchFormActivity() {
        //TODO
    }

    private fun checkParams(): Boolean {
        email = findViewById<EditText>(R.id.mail_input).text.toString()
        password = findViewById<EditText>(R.id.password_input).text.toString()
        confirmPassword = findViewById<EditText>(R.id.password_confirm_input).text.toString()

        if (email == "" || password == "" || confirmPassword == "") {
            Toast.makeText(applicationContext, "Veuillez remplir le formulaire", Toast.LENGTH_SHORT)
                .show()
            return false
        }
        if (password != confirmPassword) {
            Toast.makeText(
                applicationContext,
                "Veuillez écrire un mot de passe identique dans le champ 'confirmer mot de passe'",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        findViewById<Button>(R.id.validate).setOnClickListener { if (checkParams()) launchFormActivity() }
    }
}