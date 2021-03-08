package ir.mostafa.nematpour.khodhesab

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.transition.Fade
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import ir.mostafa.nematpour.khodhesab.adapter.GetPersonAdapter
import ir.mostafa.nematpour.khodhesab.dataBase.DataBaseManager
import ir.mostafa.nematpour.khodhesab.model.Person
import ir.mostafa.nematpour.khodhesab.ui.MyNumberWatcher
import kotlinx.android.synthetic.main.activity_get_person_include_3.*
import java.text.DecimalFormat


class GetPerson : AppCompatActivity() {
    private var items = mutableListOf<Person>()
    var dbm: DataBaseManager? = null
    private var changeState = -1
    var recyclerView: RecyclerView? = null
    private val sharedPrefFile = "applicationState"
    var myAdapter: GetPersonAdapter? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_person)
        window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL

        val fade = Fade()
        val decor = window.decorView
        fade.excludeTarget(decor.findViewById<View>(R.id.action_bar_container), true)
        fade.excludeTarget(android.R.id.statusBarBackground, true)
        fade.excludeTarget(android.R.id.navigationBarBackground, true)
        window.enterTransition = fade
        window.exitTransition = fade

        dbm = DataBaseManager(this)

        val b = findViewById<ExtendedFloatingActionButton>(R.id.button)
        val sheetLayout: NestedScrollView = findViewById(R.id.nested_scroll_view_for_sheet)
        val bottomSheet: BottomSheetBehavior<*> = BottomSheetBehavior.from(sheetLayout)
        val editTextName = findViewById<EditText>(R.id.edit_name)
        val editTextFMoney = findViewById<EditText>(R.id.edit_money)
        val imageViewCancel = findViewById<ImageView>(R.id.image_cancel)
        val textInputLayout = findViewById<TextInputLayout>(R.id.input_layout_name)
        val textInputLayoutMoney = findViewById<TextInputLayout>(R.id.input_layout_money)
        val fabToNext = findViewById<FloatingActionButton>(R.id.fab_next)
        val imageProfile = findViewById<ImageView>(R.id.image_profile)


        recyclerView = findViewById<RecyclerView>(R.id.get_person_recycler_view)
        recyclerView?.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerView?.addItemDecoration(
            DividerItemDecoration(
                applicationContext,
                DividerItemDecoration.VERTICAL
            )
        )

        myAdapter = GetPersonAdapter(items, object : GetPersonAdapter.Callback {
            override fun onItemClicked(position: Int) {
                val item = items[position]
                editTextName.setText(items[position].name)
                editTextFMoney.setText("0")
                if (item.money.toString().isNotEmpty()) {
                    val sdd = DecimalFormat("#,###")
                    val doubleNumber = item.money.toString().toDouble()
                    val format: String = sdd.format(doubleNumber)
                    editTextFMoney.setText(format)

                }
                bottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
                changeState = position
            }

            override fun delete(position: Int) {
                items.removeAt(position)
                recyclerView?.adapter?.notifyDataSetChanged()
                fabToNextVisibility(fabToNext)
            }
        })
        recyclerView?.adapter = myAdapter

        bottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
        b.shrink()
        bottomSheet.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }

            override fun onStateChanged(bottomSheet1: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        b.shrink()
                        hideKeyboard()
                        changeState = -1
                        fabToNextVisibility(fabToNext)
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        b.extend()
                        fabToNext.hide()

                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        bottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
                    }
                    BottomSheetBehavior.STATE_DRAGGING -> {
                    }
                    BottomSheetBehavior.STATE_SETTLING -> {
                    }
                }
            }
        })
        editTextFMoney.addTextChangedListener(MyNumberWatcher(editTextFMoney))

        imageViewCancel.setOnClickListener(View.OnClickListener {
            b.shrink()
            bottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
        })

        fabToNext.setOnClickListener(View.OnClickListener {
            addDateBase(dbm)
            startActivity(Intent(this, MainActivity::class.java))
        })
        // add button on click
        b.setOnClickListener(View.OnClickListener {
            textInputLayout.isErrorEnabled = false
            textInputLayoutMoney.isErrorEnabled = false
            if (bottomSheet.state == BottomSheetBehavior.STATE_HIDDEN) {

                bottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
                editTextName.setText("فرد شماره ${items.size + 1}")

                edit_money.setText("0")
            } else if (bottomSheet.state == BottomSheetBehavior.STATE_EXPANDED) {
                if (personIsOkAndAdd(
                        editTextName,
                        editTextFMoney,
                        textInputLayout,
                        textInputLayoutMoney,
                        changeState
                    )
                ) {
                    bottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
                    changeState = -1
                } else {

                    val contextView = findViewById<View>(R.id.context_view)

                    Snackbar.make(contextView, "ورودی\u200cها را کنترل کنید.", Snackbar.LENGTH_LONG)
                        .show()

                }
            }
            myAdapter?.notifyDataSetChanged()
        })
        fabToNextVisibility(fabToNext)
    }


    override fun onStart() {
        super.onStart()
        val fabToNext = findViewById<FloatingActionButton>(R.id.fab_next)
        try {
            Thread(Runnable {
                items.clear()
                items.addAll(dbm!!.getPersons())
                this.runOnUiThread {
                    myAdapter?.notifyDataSetChanged()
                }
            }).start()

        } catch (e: java.lang.Exception) {
            Toast.makeText(applicationContext, "مشکلی پیش آمده", Toast.LENGTH_LONG).show()
        }

        fabToNextVisibility(fabToNext)

    }

    // check for is import ok?
    private fun personIsOkAndAdd(
        eName: EditText,
        eMoney: EditText,
        tInPoutName: TextInputLayout,
        tInPoutMoney: TextInputLayout,
        position: Int
    ): Boolean {

        if (eName.text.toString() != "") {
            if (eMoney.text.toString() != "") {
                if (position == -1) {
                    for (item in items) {
                        if (item.name == eName.text.toString()) {
                            tInPoutName.error = "نام تکراری"
                            return false
                        }
                    }
                    var money: Int
                    try {
                        money = eMoney.text.toString().replace(",", "").toInt()
                    } catch (e: Exception) {
                        tInPoutMoney.error = "ورودی غیر مرتبط"
                        return false
                    }
                    items.add(
                        Person(
                            -1,
                            eName.text.toString(), money,
                            "0"
                        )
                    )
                    return true
                } else {
                    items[position] = Person(
                        -1,
                        eName.text.toString(),
                        eMoney.text.toString().replace(",", "").toInt(),
                        "0"
                    )
                    return true
                }
            }
            tInPoutMoney.error = "مقدار پول\u200cاولیه نمی\u200cتواند خالی باشد!"
            return false
        }
        tInPoutName.error = "نام نمی\u200cتواند خالی باشد!"
        return false
    }

    // for hide key bord
    fun AppCompatActivity.hideKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
        // else {
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        // }
    }

    // for change fab visibility if items < 2
    fun fabToNextVisibility(fab: FloatingActionButton) {
        val textHelp = findViewById<TextView>(R.id.text_help)
        if (items.size < 2) {
            fab.hide()
            textHelp.visibility = View.VISIBLE
        } else {
            fab.show()
            textHelp.visibility = View.INVISIBLE
        }
    }

    // on back
    override fun onBackPressed() {
        val sheetLayout: NestedScrollView = findViewById(R.id.nested_scroll_view_for_sheet)
        val bottomSheet: BottomSheetBehavior<*> = BottomSheetBehavior.from(sheetLayout)
        val b = findViewById<ExtendedFloatingActionButton>(R.id.button)
        if (bottomSheet.state == BottomSheetBehavior.STATE_HIDDEN) {
            super.onBackPressed()
        } else {
            b.shrink()
            bottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    private fun addDateBase(dbm: DataBaseManager?) {
        if (dbm?.personCount() ?: -1 > 0) {
            dbm?.deleteAll()
        }
        for (i in items) {
            dbm?.insertPerson(i)
        }
        val sharedPreferences: SharedPreferences =
            this.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString("state", "PersonAddComplete")
        editor.apply()
        editor.commit()
    }
}