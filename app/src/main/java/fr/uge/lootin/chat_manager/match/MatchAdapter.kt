package fr.uge.lootin.chat_manager.match

import android.app.Activity
import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import de.hdodenhof.circleimageview.CircleImageView
import fr.uge.lootin.ProfilesSwipingActivity
import fr.uge.lootin.R
import fr.uge.lootin.chat.ChatFragment
import java.io.ByteArrayOutputStream
import kotlin.collections.ArrayList

class MatchAdapter (var matches: ArrayList<Match>, private val activity: Activity) : RecyclerView.Adapter<MatchAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var photo: CircleImageView = itemView.findViewById(R.id.profile_image)
        fun update(image: Bitmap) {
            photo.setImageBitmap(image)

            /* num = (0..3).random()
            if (num == 0) {
                photo.setImageResource(R.drawable.aarmand)
                //photo.setBackgroundResource()
            }
            if (num == 1) {
                photo.setImageResource(R.drawable.armand_dort)
            }
            if (num == 2) {
                photo.setImageResource(R.drawable.loulou)
            }
            if (num == 3) {
                photo.setImageResource(R.drawable.loulou_content)
            }*/

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vh = ViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.match_layout, parent, false)
        )
        return vh
    }

    override fun getItemCount(): Int {
        return matches.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.update(matches[position].image)

        holder.itemView.setOnClickListener {
            Log.i("my_log", "on a cliqué sur match : " + matches[position].pseudo + " - id matcher : " + matches[position].id_matcher)
            val bitmap : Bitmap = matches[position].image
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream)
            val image = stream.toByteArray()
            val settingsFrag = ChatFragment.chatInstance((matches[position].id_match).toLong()/*,image*/, matches[position].pseudo)
            (activity as ProfilesSwipingActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view, settingsFrag, "chatFragment")
                .addToBackStack("chatFragment").commit()
        }
    }
}