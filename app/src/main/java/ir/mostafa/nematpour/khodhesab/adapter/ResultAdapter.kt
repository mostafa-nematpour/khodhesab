package ir.mostafa.nematpour.khodhesab.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ir.mostafa.nematpour.khodhesab.R
import ir.mostafa.nematpour.khodhesab.model.Answer
import java.text.DecimalFormat

class ResultAdapter(private val items: List<Answer>, val callback: Callback) :
    RecyclerView.Adapter<ResultAdapter.MainHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultAdapter.MainHolder =
        MainHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_model_result, parent, false)
        )

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        holder.bind(items[position], position)
    }

    inner class MainHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val button: Button = itemView.findViewById<Button>(R.id.button2)
        private val textPrice = itemView.findViewById<TextView>(R.id.textView3)
        private val textTotal = itemView.findViewById<TextView>(R.id.textView5)
        private val textDateAndTime = itemView.findViewById<TextView>(R.id.textView6)

        @SuppressLint("SetTextI18n")
        fun bind(item: Answer, position: Int) {

            if (item.totalExpenses.toString().isNotEmpty()) {
                val sdd = DecimalFormat("#,###")
                val doubleNumber = item.totalExpenses.toString().toDouble()
                val format: String = sdd.format(doubleNumber)
                textPrice.text = format
            }
            textTotal.text = item.totalSpent.toString()
            textDateAndTime.text = "${item.time +  item.palist.toString()} "

            button.setBackgroundResource(R.drawable.custom_button_result)
            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) callback.onItemClicked(item.id)
            }
        }
    }

    interface Callback {
        fun onItemClicked(id: Int)

    }

}