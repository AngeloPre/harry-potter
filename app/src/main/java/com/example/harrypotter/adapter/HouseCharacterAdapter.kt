package com.example.harrypotter.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.harrypotter.R
import com.example.harrypotter.model.HouseCharacter
import com.squareup.picasso.Picasso

class HouseCharacterAdapter(
    private val characters: MutableList<HouseCharacter> = mutableListOf()
) : RecyclerView.Adapter<HouseCharacterAdapter.HouseCharacterViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HouseCharacterViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_view_student, parent, false)
        return HouseCharacterViewHolder(view)
    }

    override fun onBindViewHolder(holder: HouseCharacterViewHolder, position: Int) {
        holder.bind(characters[position])
    }

    override fun getItemCount(): Int {
        return characters.size
    }

    fun updateCharacters(newCharacters: List<HouseCharacter>) {
        characters.clear()
        characters.addAll(newCharacters)
        notifyDataSetChanged()
    }

    class HouseCharacterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val photoImageView: ImageView = itemView.findViewById(R.id.ivCharRV)
        private val nameTextView: TextView = itemView.findViewById(R.id.tvNomeRV)
        private val houseTextView: TextView = itemView.findViewById(R.id.tvHouseRV)

        fun bind(character: HouseCharacter) {
            nameTextView.text = character.name
            houseTextView.text = character.house.ifBlank { "Sem casa" }

            if (character.photo.isNotBlank()) {
                Picasso.get().load(character.photo).into(photoImageView)
            } else {
                photoImageView.setImageDrawable(null)
            }
        }
    }
}
