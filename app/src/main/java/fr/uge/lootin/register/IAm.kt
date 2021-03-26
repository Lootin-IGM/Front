package fr.uge.lootin.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import fr.uge.lootin.R

class IAm : Fragment() {
    lateinit var layout: View

    private fun showToastNotSelectedError() {
        Toast.makeText(
            activity?.applicationContext,
            activity?.applicationContext?.getString(R.string.notSelectedError),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun checkParamsIAm(man: Boolean, woman: Boolean): Boolean {
        if (!man && !woman) {
            showToastNotSelectedError()
            return false
        }
        if (man) (activity as RegisterActivity).setGender("MALE")
        if (woman) (activity as RegisterActivity).setGender("FEMALE")
        return true
    }

    private fun setColorToSelected(id: Int) {
        layout.findViewById<TextView>(id)
            .setTextColor(ContextCompat.getColor(layout.context, R.color.red_lootin))
        layout.findViewById<TextView>(id).background =
            ContextCompat.getDrawable(layout.context, R.drawable.rounded_selected_edit_text)
    }

    private fun setColorToNotSelected(id: Int) {
        layout.findViewById<TextView>(id)
            .setTextColor(ContextCompat.getColor(layout.context, R.color.white))
        layout.findViewById<TextView>(id).background =
            ContextCompat.getDrawable(layout.context, R.drawable.rounded_edit_text)
    }

    private fun loadSearchingForFragment() {
        val firstFrag = SearchingFor.registerInstance()
        (activity as RegisterActivity).supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in_r_l,
                R.anim.fade_out_r_l, R.anim.fade_in_r_l, R.anim.slide_out_r_l
            )
            .replace(R.id.register_fragment, firstFrag, "searchingForFragment")
            .addToBackStack("searchingForFragment").commit()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        layout = inflater.inflate(R.layout.i_am_layout, container, false)
        var man = false
        var woman = false
        layout.findViewById<TextView>(R.id.man).setOnClickListener {
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
        layout.findViewById<TextView>(R.id.woman).setOnClickListener {
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
        layout.findViewById<Button>(R.id.nextButtonWhatAmI).setOnClickListener {
            if (checkParamsIAm(man, woman)) {
                loadSearchingForFragment()
            }
        }
        return layout
    }

    companion object {
        fun registerInstance(): IAm {
            return IAm()
        }
    }
}