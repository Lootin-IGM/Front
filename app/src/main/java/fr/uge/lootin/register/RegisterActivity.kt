package fr.uge.lootin.register

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import fr.uge.lootin.R
import fr.uge.lootin.form.FormActivity

class RegisterActivity : AppCompatActivity() {
    private var username: String = ""
    private var password: String = ""
    private var firstName: String = ""
    private var lastName: String = ""
    private var age: Int = 0
    private var gender: String = ""
    private var attraction: String = ""

    fun setUsername(username: String) {
        this.username = username
    }

    fun setPassword(password: String) {
        this.password = password
    }

    fun setFirstName(firstName: String) {
        this.firstName = firstName
    }

    fun setLastName(lastName: String) {
        this.lastName = lastName
    }

    fun setAge(age: Int) {
        this.age = age
    }

    fun setGender(gender: String) {
        this.gender = gender
    }

    fun setAttraction(attraction: String) {
        this.attraction = attraction
    }

    fun launchFormActivity() {
        val intent = Intent(this, FormActivity::class.java).apply {
            putExtra("username", username)
            putExtra("password", password)
            putExtra("firstName", firstName)
            putExtra("lastName", lastName)
            putExtra("age", age)
            putExtra("gender", gender)
            putExtra("attraction", attraction)
        }
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.empty_register_layout)
        val firstFrag = Pseudo.registerInstance()
        supportFragmentManager.beginTransaction()
            .add(R.id.register_fragment, firstFrag, "pseudoFragment")
            .addToBackStack("pseudoFragment").commit()
    }
}