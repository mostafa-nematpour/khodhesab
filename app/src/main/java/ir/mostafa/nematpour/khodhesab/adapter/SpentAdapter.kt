package ir.mostafa.nematpour.khodhesab.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ir.mostafa.nematpour.khodhesab.R
import ir.mostafa.nematpour.khodhesab.model.Person
import ir.mostafa.nematpour.khodhesab.model.Spent
import java.text.DecimalFormat

class SpentAdapter(var items: List<Spent>, val callback: Callback) :
    RecyclerView.Adapter<SpentAdapter.MainHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpentAdapter.MainHolder =
        MainHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_model_spent, parent, false)
        )

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: SpentAdapter.MainHolder, position: Int) {
        holder.bind(items[position], position)

    }

    inner class MainHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val name = itemView.findViewById<TextView>(R.id.textV_view_spent_name)
        private val money = itemView.findViewById<TextView>(R.id.text_view_spent_money)
        private val dateAndTime = itemView.findViewById<TextView>(R.id.textViewDate)

        @SuppressLint("SetTextI18n")
        fun bind(item: Spent, position: Int) {
            name.text = item.buyerId.toString()
            name.text = callback.setBuyerName(item.buyerId)
            money.text = item.money.toString()
            dateAndTime.text="${item.time}   "
            if (item.money.toString().isNotEmpty()) {
                val sdd = DecimalFormat("#,###")
                val doubleNumber = item.money.toString().toDouble()
                val format: String = sdd.format(doubleNumber)
                money.text = format
            }

//            delete.setOnClickListener {
//                if (adapterPosition != RecyclerView.NO_POSITION) callback.delete(position)
//            }
            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) callback.onItemClicked(item.id)
            }
        }
    }

    interface Callback {
        fun onItemClicked(id: Int)
        fun setBuyerName(id:Int): String
        //fun delete(position: Int)
    }
}