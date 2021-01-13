package fr.uge.lootin.signin

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import fr.uge.lootin.R


class SignInActivity : AppCompatActivity() {
    private var email: String = ""
    private var password: String = ""

    private fun checkParams(): Boolean {
        email = findViewById<EditText>(R.id.EmailOnsignInPage).text.toString()
        password = findViewById<EditText>(R.id.PasswordOnsignInPage).text.toString()

        //TODO

        return true
    }


    private fun googleConnect(): Boolean {

        //TODO

        return true
    }

    private fun launchRegisterActivity() {
        //TODO
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        //google button
        findViewById<Button>(R.id.GoogleButtonOnsignInPage).setOnClickListener { googleConnect() }

        //connection button
        findViewById<Button>(R.id.ConnectionButtonOnsignInPage).setOnClickListener { checkParams() }

        //Register button
        findViewById<TextView>(R.id.RegisterLinkOnsignInPage).setOnClickListener { launchRegisterActivity() }
    }
}