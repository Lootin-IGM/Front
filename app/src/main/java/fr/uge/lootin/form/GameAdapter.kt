package fr.uge.lootin.form

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import fr.uge.lootin.R

class GameAdapter(val gameList: List<Game>, val clickable: Boolean = true) : RecyclerView.Adapter<GameAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cardImage: ImageView = itemView.findViewById(R.id.gameImage)

        fun changeImage(position: Int) {
            var game = gameList[position]
            if (game.isSelected()) {
                cardImage.alpha = 0.5F
            } else {
                cardImage.alpha = 1F
            }
        }

        fun update(game: Game, position: Int) {
            cardImage.setImageBitmap(game.getBitmap())
            changeImage(position)
            if (clickable){
                itemView.setOnClickListener {
                    game.clicked()
                    changeImage(position)
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