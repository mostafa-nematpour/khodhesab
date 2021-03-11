package ir.mostafa.nematpour.khodhesab.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import ir.mostafa.nematpour.khodhesab.R
import ir.mostafa.nematpour.khodhesab.adapter.ResultAdapter
import ir.mostafa.nematpour.khodhesab.computing.ComputingResult
import ir.mostafa.nematpour.khodhesab.dataBase.DataBaseManager
import ir.mostafa.nematpour.khodhesab.model.Answer
import ir.mostafa.nematpour.khodhesab.model.Person


class DashboardFragment : Fragment() {

    private var items = mutableListOf<Answer>()
    var adapter: ResultAdapter? = null

    private lateinit var dashboardViewModel: DashboardViewModel
    var i = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)
        val textView: TextView = root.findViewById(R.id.text_dashboard)
        val imageView = root.findViewById<ImageView>(R.id.imageView2)
        dashboardViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        val db = DataBaseManager(context)
        val computingResult = ComputingResult(db.getSpents(), context)

        items=db.getAnswers()
        val check =
            root.findViewById<ExtendedFloatingActionButton>(R.id.extendedFloatingActionButton)
        check.setOnClickListener {
            computingResult.result()
            items.clear()
            items.addAll(db.getAnswers())
            adapter?.notifyDataSetChanged()
            imageVisibility(imageView, textView)

        }


        val recyclerView = root.findViewById<RecyclerView>(R.id.rec_res)

        recyclerView?.layoutManager =
            LinearLayoutManager(root.context, RecyclerView.VERTICAL, false)
        adapter = ResultAdapter(items, object : ResultAdapter.Callback {

            override fun onItemClicked(id: Int) {

            }
        })

        recyclerView?.adapter = adapter


        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 10) {
                    // Scrolling up
                    check.hide()
                } else if (dy < 0) {
                    // Scrolling down
                    check.show()
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {

                    // Do something
                } else if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    // Do something
                } else {
                    // Do something
                }
            }
        })

        imageVisibility(imageView, textView)

        return root
    }

    private fun imageVisibility(image: ImageView, textView: TextView) {
        if (items.size > 0) {
            image.visibility = View.INVISIBLE
            textView.visibility = View.INVISIBLE
        } else {
            image.visibility = View.VISIBLE
            textView.visibility = View.VISIBLE
        }
    }

    private fun toString(list: MutableList<Person>): String {
        var s = ""
        for (p in list) {
            s += p.toString()
        }
        return s
    }
}