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

class SearchingFor : Fragment() {
    lateinit var layout: View

    private fun showToastNotSelectedError() {
        Toast.makeText(
            activity?.applicationContext,
            activity?.applicationContext?.getString(R.string.notSelectedError),
            Toast.LENGTH_SHORT
        ).show()
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

    private fun checkParamsSearchingFor(
        man: Boolean,
        woman: Boolean,
        manAndWoman: Boolean
    ): Boolean {
        if (!man && !woman && !manAndWoman) {
            showToastNotSelectedError()
            return false
        }
        if (man) (activity as RegisterActivity).setAttraction("MEN")
        if (woman) (activity as RegisterActivity).setAttraction("WOMEN")
        if (manAndWoman) (activity as RegisterActivity).setAttraction("BOTH")
        return true
    }

    private fun loadFormActivty() {
        (activity as RegisterActivity).supportFragmentManager.beginTransaction().remove(this)
            .commit()
        (activity as RegisterActivity).launchFormActivity()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        layout = inflater.inflate(R.layout.searching_for_layout, container, false)
        var man = false
        var woman = false
        var manAndWoman = false
        layout.findViewById<TextView>(R.id.searchingForMan).setOnClickListener {
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
        layout.findViewById<TextView>(R.id.searchingForWoman).setOnClickListener {
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
        layout.findViewById<TextView>(R.id.searchingForManAndWoman).setOnClickListener {
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
        layout.findViewById<Button>(R.id.nextButtonSearchingFor).setOnClickListener {
            if (checkParamsSearchingFor(
                    man,
                    woman,
                    manAndWoman
                )
            ) loadFormActivty()
        }
        return layout
    }

    companion object {
        fun registerInstance(): SearchingFor {
            return SearchingFor()
        }
    }
}