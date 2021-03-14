package fr.uge.lootin.register

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import fr.uge.lootin.R
import fr.uge.lootin.form.FormActivity
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class RegisterActivity : AppCompatActivity() {
    private var username: String = ""
    private var password: String = ""
    private var firstName: String = ""
    private var lastName: String = ""
    private var age: Int = 0
    private var gender: String = ""
    private var attraction: String = ""

    private fun launchFormActivity() {
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

    private fun showToastError() {
        Toast.makeText(
                applicationContext,
                applicationContext.getString(R.string.formError),
                Toast.LENGTH_SHORT
        ).show()
    }

    private fun showBadPasswordError() {
        Toast.makeText(
                applicationContext,
                applicationContext.getString(R.string.wrongPassword),
                Toast.LENGTH_SHORT
        ).show()
    }

    private fun showToastNotSelectedError() {
        Toast.makeText(
                applicationContext,
                applicationContext.getString(R.string.notSelectedError),
                Toast.LENGTH_SHORT
        ).show()
    }

    private fun checkParamsWhatsYourName(): Boolean {
        val firstName = findViewById<EditText>(R.id.firstNameText).text.toString()
        val lastName = findViewById<EditText>(R.id.lastNameText).text.toString()
        if (firstName == "" || lastName == "") {
            showToastError()
            return false
        }
        this.firstName = firstName
        this.lastName = lastName
        return true
    }

    private fun checkParamsBirthday(): Boolean {
        val day = findViewById<EditText>(R.id.dayText).text.toString()
        val month = findViewById<EditText>(R.id.monthText).text.toString()
        val year = findViewById<EditText>(R.id.yearText).text.toString()
        if (day == "" || month == "" || year == "") {
            showToastError()
            return false
        }
        val date = SimpleDateFormat("dd-MM-yyyy", Locale.FRANCE).parse(day + "-" + month + "-" + year)
        val actual = SimpleDateFormat("dd-MM-yyyy", Locale.FRANCE).format(Date())
        val ageInDays = SimpleDateFormat("dd-MM-yyyy", Locale.FRANCE).parse(actual).time - date.time
        this.age = (TimeUnit.DAYS.convert(ageInDays, TimeUnit.MILLISECONDS) / 365).toInt()

        return true
    }

    private fun checkParamsIAm(man: Boolean, woman: Boolean): Boolean {
        if (!man && !woman) {
            showToastNotSelectedError()
            return false
        }
        if (man) this.gender = "MALE"
        if (woman) this.gender = "FEMALE"
        return true
    }

    private fun checkParamsSearchingFor(
        man: Boolean,
        woman: Boolean,
        manAndWoman: Boolean
    ): Boolean {
        if (!man && !woman && !manAndWoman) {
            showToastNotSelectedError()
            return false
        }
        if (man) this.attraction = "MEN"
        if (woman) this.attraction = "WOMEN"
        if (manAndWoman) this.attraction = "BOTH"
        return true
    }

    private fun launchSearchingForActivity() {
        var man = false
        var woman = false
        var manAndWoman = false
        findViewById<TextView>(R.id.searchingForMan).setOnClickListener {
            man = !man
            woman = false
            manAndWoman = false
            if (man) {
                setColorToSelected(R.id.searchingForMan)
                setColorToNotSelected(R.id.searchingForWoman)
                setColorToNotSelected(R.id.searchingForManAndWoman)
            } else {
                setColorToNotSelected(R.id.searchingForMan)
                setColorToNotSelected(R.id.searchingForWoman)
                setColorToNotSelected(R.id.searchingForManAndWoman)
            }

        }
        findViewById<TextView>(R.id.searchingForWoman).setOnClickListener {
            woman = !woman
            man = false
            manAndWoman = false
            if (woman) {
                setColorToSelected(R.id.searchingForWoman)
                setColorToNotSelected(R.id.searchingForMan)
                setColorToNotSelected(R.id.searchingForManAndWoman)
            } else {
                setColorToNotSelected(R.id.searchingForWoman)
                setColorToNotSelected(R.id.searchingForMan)
                setColorToNotSelected(R.id.searchingForManAndWoman)
            }
        }
        findViewById<TextView>(R.id.searchingForManAndWoman).setOnClickListener {
            woman = false
            man = false
            manAndWoman = !manAndWoman
            if (manAndWoman) {
                setColorToSelected(R.id.searchingForManAndWoman)
                setColorToNotSelected(R.id.searchingForMan)
                setColorToNotSelected(R.id.searchingForWoman)
            } else {
                setColorToNotSelected(R.id.searchingForWoman)
                setColorToNotSelected(R.id.searchingForMan)
                setColorToNotSelected(R.id.searchingForManAndWoman)
            }
        }
        findViewById<Button>(R.id.nextButtonSearchingFor).setOnClickListener {
            if (checkParamsSearchingFor(
                            man,
                            woman,
                            manAndWoman
                    )) launchFormActivity()
        }
    }

    private fun setColorToSelected(id: Int) {
        findViewById<TextView>(id).setTextColor(ContextCompat.getColor(this, R.color.red_lootin))
        findViewById<TextView>(id).background =
            ContextCompat.getDrawable(this, R.drawable.rounded_selected_edit_text)
    }

    private fun setColorToNotSelected(id: Int) {
        findViewById<TextView>(id).setTextColor(ContextCompat.getColor(this, R.color.white))
        findViewById<TextView>(id).background =
            ContextCompat.getDrawable(this, R.drawable.rounded_edit_text)
    }

    private fun launchIAmActivity() {
        var man = false
        var woman = false
        findViewById<TextView>(R.id.man).setOnClickListener {
            man = !man
            woman = false
            if (man) {
                setColorToSelected(R.id.man)
                setColorToNotSelected(R.id.woman)
            } else {
                setColorToNotSelected(R.id.man)
                setColorToNotSelected(R.id.woman)
            }

        }
        findViewById<TextView>(R.id.woman).setOnClickListener {
            woman = !woman
            man = false
            if (woman) {
                setColorToNotSelected(R.id.man)
                setColorToSelected(R.id.woman)
            } else {
                setColorToNotSelected(R.id.man)
                setColorToNotSelected(R.id.woman)
            }
        }
        findViewById<Button>(R.id.nextButtonWhatAmI).setOnClickListener {
            if (checkParamsIAm(man, woman)) {
                setContentView(R.layout.searching_for_layout)
                launchSearchingForActivity()
            }
        }
    }

    private fun launchBirthdayActivity() {
        setContentView(R.layout.birthday_layout)
        findViewById<EditText>(R.id.dayText).filters = arrayOf(InputFilterMinMax(1, 31))
        findViewById<EditText>(R.id.monthText).filters = arrayOf(InputFilterMinMax(1, 12))
        findViewById<EditText>(R.id.yearText).filters = arrayOf(InputFilterMinMax(1, 2020))
        findViewById<Button>(R.id.nextButtonBirthday).setOnClickListener {
            if (checkParamsBirthday()) {
                setContentView(R.layout.i_am_layout)
                launchIAmActivity()
            }
        }
    }

    private fun launchNameActivity() {
        setContentView(R.layout.name_layout)

        findViewById<Button>(R.id.nextButtonWhatsYourName).setOnClickListener {
            if (checkParamsWhatsYourName()) {
                launchBirthdayActivity()
            }
        }
    }

    private fun checkParamsRegister(): Boolean {
        val pseudo = findViewById<EditText>(R.id.choosePseudoInput).text.toString()
        val password = findViewById<EditText>(R.id.choosePasswordInput).text.toString()
        val confirmPassword = findViewById<EditText>(R.id.confirmPasswordInput).text.toString()
        if (pseudo.equals("") || password.equals("") || confirmPassword.equals("")) {
            showToastError()
            return false
        }
        if (!password.equals(confirmPassword)) {
            showBadPasswordError()
            return false
        }
        this.username = pseudo
        this.password = password
        return true
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_layout)

        findViewById<Button>(R.id.nextButtonChoosePseudo).setOnClickListener {
            if (checkParamsRegister()) {
                launchNameActivity()
            }
        }
    }
}