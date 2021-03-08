package ir.mostafa.nematpour.khodhesab.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ir.mostafa.nematpour.khodhesab.R
import ir.mostafa.nematpour.khodhesab.model.Person
import java.text.DecimalFormat

class AddSpentAdapter(var items: List<Person>, val callback: Callback) :
    RecyclerView.Adapter<AddSpentAdapter.MainHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddSpentAdapter.MainHolder =
            MainHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_model_add_spent, parent, false)
            )

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: AddSpentAdapter.MainHolder, position: Int) {
        holder.bind(items[position], position)

    }

    inner class MainHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val name = itemView.findViewById<TextView>(R.id.text_name)
        private val checkBox = itemView.findViewById<CheckBox>(R.id.checkBox)
        fun bind(item: Person, position: Int) {
            name.text = item.name
            //money.text = item.money.toString()
            if (item.money.toString().isNotEmpty()) {
                val sdd = DecimalFormat("#,###")
                val doubleNumber = item.money.toString().toDouble()
                val format: String = sdd.format(doubleNumber)
                checkBox.isChecked=item.flag

            }

            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {

                    callback.onItemClicked(position,checkBox)
                }
            }
        }
    }


    interface Callback {
        fun onItemClicked(position: Int, checkBox: CheckBox)
    }
}