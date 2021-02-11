package fr.uge.lootin.form

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import fr.uge.lootin.R

class GameAdapter(val gameList: List<Game>) : RecyclerView.Adapter<GameAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cardImage: ImageView = itemView.findViewById(R.id.gameImage)

        fun update(game: Game, position: Int) {
            cardImage.setImageBitmap(game.getBitmap(cardImage.context))
            //notifyItemChanged(position)
            itemView.setOnClickListener {
                game.clicked()
                if (game.isSelected()) {
                    cardImage.alpha = 0.5F
                } else {
                    cardImage.alpha = 1F
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.game, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.update(gameList[position], position)
    }

    override fun getItemCount(): Int {
        return gameList.size
    }
}