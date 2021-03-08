package ir.mostafa.nematpour.khodhesab.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ir.mostafa.nematpour.khodhesab.R
import ir.mostafa.nematpour.khodhesab.model.Person
import java.text.DecimalFormat
import javax.security.auth.callback.Callback

class GetPersonAdapter(var items: List<Person>, val callback: Callback) :
    RecyclerView.Adapter<GetPersonAdapter.MainHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GetPersonAdapter.MainHolder =
        MainHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_model_get_person, parent, false)
        )

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: GetPersonAdapter.MainHolder, position: Int) {
        holder.bind(items[position],position)

    }

    inner class MainHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val name = itemView.findViewById<TextView>(R.id.text_name)
        private val money = itemView.findViewById<TextView>(R.id.text_money)
        private val delete = itemView.findViewById<ImageView>(R.id.imageView_delete)

        fun bind(item: Person,position: Int) {
            name.text = item.name
            money.text = item.money.toString()
            if (item.money.toString().isNotEmpty()) {
                val sdd = DecimalFormat("#,###")
                val doubleNumber = item.money.toString().toDouble()
                val format: String = sdd.format(doubleNumber)
                money.text = format

            }
            delete.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) callback.delete(position)
            }
            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) callback.onItemClicked(position)
            }
        }
    }


    interface Callback {
        fun onItemClicked(position: Int)
        fun delete(position:Int)
    }
}