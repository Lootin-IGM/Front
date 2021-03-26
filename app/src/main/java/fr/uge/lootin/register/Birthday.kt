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
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class Birthday : Fragment() {
    lateinit var layout: View

    private fun showToastError() {
        Toast.makeText(
            activity?.applicationContext,
            activity?.applicationContext?.getString(R.string.formError),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun loadIAmFragment() {
        val firstFrag = IAm.registerInstance()
        (activity as RegisterActivity).supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in_r_l,
                R.anim.fade_out_r_l, R.anim.fade_in_r_l, R.anim.slide_out_r_l
            )
            .replace(R.id.register_fragment, firstFrag, "iAmFragment")
            .addToBackStack("iAmFragment").commit()
    }

    private fun checkParamsBirthday(): Boolean {
        val day = layout.findViewById<EditText>(R.id.dayText).text.toString()
        val month = layout.findViewById<EditText>(R.id.monthText).text.toString()
        val year = layout.findViewById<EditText>(R.id.yearText).text.toString()
        if (day == "" || month == "" || year == "") {
            showToastError()
            return false
        }
        val date =
            SimpleDateFormat("dd-MM-yyyy", Locale.FRANCE).parse(day + "-" + month + "-" + year)
        val actual = SimpleDateFormat("dd-MM-yyyy", Locale.FRANCE).format(Date())
        val ageInDays = SimpleDateFormat("dd-MM-yyyy", Locale.FRANCE).parse(actual).time - date.time
        (activity as RegisterActivity).setAge(
            (TimeUnit.DAYS.convert(
                ageInDays,
                TimeUnit.MILLISECONDS
            ) / 365).toInt()
        )

        return true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        layout = inflater.inflate(R.layout.birthday_layout, container, false)
        layout.findViewById<EditText>(R.id.dayText).filters = arrayOf(InputFilterMinMax(1, 31))
        layout.findViewById<EditText>(R.id.monthText).filters = arrayOf(InputFilterMinMax(1, 12))
        layout.findViewById<EditText>(R.id.yearText).filters = arrayOf(InputFilterMinMax(1, 2020))
        layout.findViewById<Button>(R.id.nextButtonBirthday).setOnClickListener {
            if (checkParamsBirthday()) {
                loadIAmFragment()
            }
        }
        return layout
    }

    companion object {
        fun registerInstance(): Birthday {
            return Birthday()
        }
    }
}