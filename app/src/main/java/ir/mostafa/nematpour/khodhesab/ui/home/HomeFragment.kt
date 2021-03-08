package ir.mostafa.nematpour.khodhesab.ui.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import ir.mostafa.nematpour.khodhesab.AddSpentActivity
import ir.mostafa.nematpour.khodhesab.R
import ir.mostafa.nematpour.khodhesab.adapter.SpentAdapter
import ir.mostafa.nematpour.khodhesab.dataBase.DataBaseManager
import ir.mostafa.nematpour.khodhesab.model.Person
import ir.mostafa.nematpour.khodhesab.model.Spent


class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var recyclerView: RecyclerView? = null
    private var items = mutableListOf<Spent>()
    var myAdapter: SpentAdapter? = null
    private val ADD_ITEM = 1
    private val EDIT_ITEM = 2
    private val REQUEST_CODE = 1
    var db: DataBaseManager? = null
    private var root: View? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        this.root = root
        val addButton = root.findViewById<FloatingActionButton>(R.id.FloatingActionButton)
        db = DataBaseManager(context)

        recyclerView = root.findViewById<RecyclerView>(R.id.spent_recycler_view)
        recyclerView?.layoutManager =
            LinearLayoutManager(root.context, RecyclerView.VERTICAL, false)

        myAdapter = SpentAdapter(items, object : SpentAdapter.Callback {
            override fun onItemClicked(id: Int) {
                intent(EDIT_ITEM, addButton,id)
            }

            override fun setBuyerName(id: Int) = db?.getPerson(id.toString())?.name ?: "Error"

        })
        recyclerView?.adapter = myAdapter

        upDAteList()

        addButton.setOnClickListener {
            intent(ADD_ITEM, addButton)
        }
        return root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                upDAteList()
//                Toast.makeText(context, "" + data?.getIntExtra("k", -1), Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun upDAteList() {
        items.clear()
        items.addAll(db!!.getSpents())
        items.reverse()
        myAdapter?.notifyDataSetChanged()
        textVisibility(root!!)
    }


    fun intent(extra: Int, addButton: FloatingActionButton, spentId: Int = -1) {
        val intent = Intent(activity, AddSpentActivity::class.java)
        intent.putExtra("AddSpent", extra)
        intent.putExtra("SpentId", spentId)

        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
            activity!!, (addButton as View?)!!, "fab"
        )
        startActivityForResult(intent, REQUEST_CODE, options.toBundle())
    }

    private fun textVisibility(root: View) {
        val image = root.findViewById<ImageView>(R.id.imageViewSpent)
        val textHelp = root.findViewById<TextView>(R.id.text_home)

        if (myAdapter?.itemCount ?: 0 > 0) {
            image.visibility = View.INVISIBLE
            textHelp.visibility = View.INVISIBLE
        }
    }
}