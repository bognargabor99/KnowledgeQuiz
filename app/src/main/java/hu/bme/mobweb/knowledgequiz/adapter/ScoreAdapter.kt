package hu.bme.mobweb.knowledgequiz.adapter

import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView
import hu.bme.mobweb.knowledgequiz.MainActivity
import hu.bme.mobweb.knowledgequiz.R
import hu.bme.mobweb.knowledgequiz.data.Score
import java.util.*

class ScoreAdapter(private val listener: ScoreClickListener) :
    RecyclerView.Adapter<ScoreAdapter.ScoreViewHolder>() {

    private val items = mutableListOf<Score>()
    private val allItems = mutableListOf<Score>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScoreViewHolder {
        val itemView: View = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_score, parent, false)
        return ScoreViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ScoreViewHolder, position: Int) {
        val item = items[position]
        holder.playerNameTextView.text = item.player
        holder.categoryTextView.text = MainActivity.context.getString(getCategoryStringId(item.category))
        holder.scoreTextView.text = MainActivity.context.getString(R.string.out_of_10, item.goodAnswers)
        holder.timeChronometer.base = SystemClock.elapsedRealtime()-item.time * 1000
        holder.categoryImageView.setImageResource(getImageResource(item.category))

        holder.item = item
    }

    @DrawableRes
    private fun getImageResource(category: String): Int = when (category){
        "9" -> R.drawable.category_general
        "19" -> R.drawable.category_math
        "18" -> R.drawable.category_computers
        "21" -> R.drawable.category_sport
        "23" -> R.drawable.category_history
        "27" -> R.drawable.category_animals
        "24" -> R.drawable.category_politics
        else -> R.drawable.category_any
    }

    private fun getCategoryStringId(categoryApiId: String): Int = when (categoryApiId) {
        "9" -> R.string.category_general
        "19" -> R.string.category_math
        "18" -> R.string.category_computers
        "21" -> R.string.category_sports
        "23" -> R.string.category_history
        "27" -> R.string.category_animals
        "24" -> R.string.category_politics
        else -> R.string.category_any
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun filterCategoryByString(search: String) {
        items.clear()
        val filteredItems = items
        if (search.isNotEmpty()) {
            val lowerSearch = search.toLowerCase(Locale.ROOT)
            allItems.forEach {
                if (MainActivity.context.getString(getCategoryStringId(it.category)).toLowerCase(Locale.ROOT).contains(lowerSearch))
                    items.add(it)
            }
        }
        else
            items.addAll(allItems)
    }

    fun update(scores: List<Score>) {
        items.clear()
        allItems.clear()
        items.addAll(scores.sortedByDescending{ it.goodAnswers })
        allItems.addAll(scores.sortedByDescending{ it.goodAnswers }) 
        notifyDataSetChanged()
    }

    private fun updateView(scores: List<Score>) {
        items.clear()
        items.addAll(scores.sortedByDescending{ it.goodAnswers })
        notifyDataSetChanged()
    }

    fun restoreView() {
        items.clear()
        items.addAll(allItems)
        notifyDataSetChanged()
    }

    fun filterBestScores() {
        val catIds = listOf("0","9","19","18","21","23","27","24")
        val managed = mutableListOf<Score>()
        for (cat in catIds) {
            items.filter { it.category == cat }
                    .sortedByDescending { it.goodAnswers }
                    .firstOrNull()?.let {
                        managed.add(
                                it
                        )
                    }
        }
        updateView(managed)
    }

    interface ScoreClickListener {
        fun onItemDeleted(item: Score)
        fun onAllItemsDeleted()
    }

    fun removeAll() {
        items.clear()
        allItems.clear()
        notifyDataSetChanged()
    }

    inner class ScoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val categoryImageView: ImageView
        val playerNameTextView: TextView
        val categoryTextView: TextView
        val scoreTextView: TextView
        val timeChronometer: Chronometer
        val removeButton: ImageButton

        var item: Score? = null

        init {
            categoryImageView = itemView.findViewById(R.id.category_imageview)
            playerNameTextView = itemView.findViewById(R.id.tvPlayer)
            categoryTextView = itemView.findViewById(R.id.tvCategory)
            scoreTextView = itemView.findViewById(R.id.tvGoodQuestion)
            timeChronometer = itemView.findViewById(R.id.showTime)
            removeButton = itemView.findViewById(R.id.btn_remove)
            removeButton.setOnClickListener {
                val position = items.indexOf(item)
                listener.onItemDeleted(item!!)
                items.remove(item!!)
                allItems.remove(item!!)
                notifyItemRemoved(position)
            }
        }

    }
}