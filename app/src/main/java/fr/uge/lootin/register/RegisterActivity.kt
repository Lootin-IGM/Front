package fr.uge.lootin.register

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import fr.uge.lootin.R

class RegisterActivity : AppCompatActivity() {
    private var email: String = ""
    private var password: String = ""
    private var confirmPassword: String = ""

    private fun launchFormActivity() {
        //TODO
    }

    private fun showToastError() {
        Toast.makeText(
            applicationContext,
            applicationContext.getString(R.string.formError),
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
        var firstName = findViewById<EditText>(R.id.firstNameText).text.toString()
        var lasName = findViewById<EditText>(R.id.lastNameText).text.toString()
        if (firstName == "" || lasName == "") {
            showToastError()
            return false
        }
        return true
    }

    private fun checkParamsBirthday(): Boolean {
        var day = findViewById<EditText>(R.id.dayText).text.toString()
        var month = findViewById<EditText>(R.id.monthText).text.toString()
        var year = findViewById<EditText>(R.id.yearText).text.toString()
        if (day == "" || month == "" || year == "") {
            showToastError()
            return false
        }
        return true
    }

    private fun checkParamsIAm(man: Boolean, woman: Boolean): Boolean {
        if (!man && !woman) {
            showToastNotSelectedError()
            return false
        }
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
        return true
    }

    private fun launchSearchingForActivity() {
        var man: Boolean = false
        var woman: Boolean = false
        var manAndWoman: Boolean = false
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
            checkParamsSearchingFor(
                man,
                woman,
                manAndWoman
            )
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
        var man: Boolean = false
        var woman: Boolean = false
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.name_layout)

        findViewById<Button>(R.id.nextButtonWhatsYourName).setOnClickListener {
            if (checkParamsWhatsYourName()) {
                launchBirthdayActivity()
            }
        }
    }
}