package fr.uge.lootin.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import fr.uge.lootin.ProfilesSwipingActivity
import fr.uge.lootin.R

class DisplaySettingsFragment : Fragment() {
    private var token: String = ""

    private fun launchGameListFragment() {
        val gamesSettingsFrag = GamesList.settingsInstance(token)
        (activity as ProfilesSwipingActivity).supportFragmentManager.beginTransaction().remove(this)
            .commit()
        (activity as ProfilesSwipingActivity).supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container_view, gamesSettingsFrag, "gamesSettingsFragment")
            .addToBackStack("gamesSettingsFragment").commit()
    }

    private fun launchProfilePictureFragment() {
        val profileFragment = TakePicture.settingsInstance(token)
        (activity as ProfilesSwipingActivity).supportFragmentManager.beginTransaction().remove(this)
            .commit()
        (activity as ProfilesSwipingActivity).supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container_view, profileFragment, "pictureSettingsFragment")
            .addToBackStack("pictureSettingsFragment").commit()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        token = requireArguments().getString("token").toString()
        val layout = inflater.inflate(R.layout.fragment_settings, container, false)
        layout.findViewById<TextView>(R.id.changeGames).setOnClickListener {
            launchGameListFragment()
        }
        layout.findViewById<TextView>(R.id.changePP)
            .setOnClickListener { launchProfilePictureFragment() }
        return layout
    }

    companion object {
        fun newInstance(token: String): DisplaySettingsFragment {
            var fragment = DisplaySettingsFragment()
            val args = Bundle()
            args.putString("token", token)
            fragment.arguments = args
            return fragment
        }
    }
}