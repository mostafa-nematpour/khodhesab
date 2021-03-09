package ir.mostafa.nematpour.khodhesab

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.snackbar.Snackbar
import ir.mostafa.nematpour.khodhesab.adapter.AddSpentAdapter
import ir.mostafa.nematpour.khodhesab.dataBase.DataBaseManager
import ir.mostafa.nematpour.khodhesab.model.Part
import ir.mostafa.nematpour.khodhesab.model.Person
import ir.mostafa.nematpour.khodhesab.model.Spent
import ir.mostafa.nematpour.khodhesab.ui.MyNumberWatcher
import java.sql.Timestamp;
import java.text.DateFormat
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.*


class AddSpentActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private var buttonAdd: ExtendedFloatingActionButton? = null
    private var spinner: Spinner? = null
    private var recyclerView: RecyclerView? = null
    private var myAdapter: AddSpentAdapter? = null
    private var db: DataBaseManager? = null
    private var list = mutableListOf<Person>()
    private var spinnerIndex = 0
    private var editTextMoney: EditText? = null
    private var editTextAbout: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_spent)
        window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL

        spinner = findViewById(R.id.spinnerAddSpent)
        spinner?.onItemSelectedListener = this
        val intent = Intent()
        intent.putExtra("k", 2)

        buttonAdd = findViewById(R.id.addSpentMain)
        editTextMoney = findViewById(R.id.editTextMoney)
        editTextMoney!!.addTextChangedListener(MyNumberWatcher(editTextMoney!!))
        editTextAbout = findViewById(R.id.editTextAbout)

        db = DataBaseManager(applicationContext)
        list = db?.getPersons() ?: mutableListOf<Person>()

        recyclerView = findViewById<RecyclerView>(R.id.recyclerViewAddSpent)
        recyclerView?.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)


        myAdapter = AddSpentAdapter(list, object : AddSpentAdapter.Callback {
            override fun onItemClicked(position: Int, checkBox: CheckBox) {

                checkBox.isChecked = !checkBox.isChecked
                list[position].flag = checkBox.isChecked


            }
        })
        recyclerView?.adapter = myAdapter
    }

    @SuppressLint("SimpleDateFormat")
    override fun onStart() {
        super.onStart()
        val l = mutableListOf<String>()


        val intent = intent
        val mode = intent.getIntExtra("AddSpent", 0)
        if (mode != 0) {
            when (mode) {
                1 -> {
                    l.add("انتخاب کنید.")
                    for (p in list) {
                        l.add(p.name)
                    }
                    val aa =
                        ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, l)
                    aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinner?.adapter = aa

                    buttonAdd?.setOnClickListener {
                        if (addOk()) {
                            setResult(RESULT_OK, intent)
                            hideKeyboard()
                            super.onBackPressed()
//                            finish()
                        }
                    }
                }
                2 -> {
                    val id = intent.getIntExtra("SpentId", -1)
                    val spent: Spent? = db?.getSpent(id)
                    buttonAdd?.setIconResource(R.drawable.ic_correct)
                    if (id > 0 && spent != null && spent.list != null && spent.list?.size!! > 0) {
                        if (spent.money.toString().isNotEmpty()) {
                            val sdd = DecimalFormat("#,###")
                            val doubleNumber = spent.money.toString().toDouble()
                            val format: String = sdd.format(doubleNumber)
                            editTextMoney?.setText(format)

                        }
                        editTextAbout?.setText(spent.about)
                        spinner?.setSelection(spent.buyerId)

                        l.add(db?.getPerson(spent.buyerId.toString())?.name ?: "Error")
                        val aa =
                            ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, l)
                        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        spinner?.adapter = aa

                        for (i in 0 until list.size) {
                            list[i].flag = false
                            for (p1 in spent.list!!) {
                                if (list[i].id == p1.id) {
                                    list[i].flag = p1.flag

                                }
                            }
                        }
                        buttonAdd?.setOnClickListener {
                            if(changOk(spent)){
                                setResult(RESULT_OK, intent)
                                hideKeyboard()
                                super.onBackPressed()
                            }
                        }

                        myAdapter?.notifyDataSetChanged()
                    } else {
                        finish()
                    }

                }
            }
            buttonAdd?.text = if (intent.getIntExtra("AddSpent", 0) == 1) "افزودن" else "تغیر"


        } else {
            finish()
        }
    }

    private fun changOk(spent: Spent): Boolean {
        if (editTextMoney?.text.toString() == "") {
            editTextMoney?.error = "فیلد نباید خالی باشد."
            return false
        }

        var flag = false
        for (p in list) {
            if (p.flag) {
                flag = true
                break
            }
        }
        if (flag) {

            val spent2 = Spent(
                spent.id,
                spent.buyerId,
                editTextMoney?.text.toString().replace(",","").toInt(),
                list,
                spent.time,
                editTextAbout?.text.toString()
            )

            db?.updateSpent(spent2)
            return true
        } else {
            Snackbar.make(
                editTextMoney!!,
                "یک نفر را از لیست انتخاب کنید",
                Snackbar.LENGTH_LONG
            )
                .show()
        }
        return false

    }

    @SuppressLint("SimpleDateFormat")
    private fun addOk(): Boolean {
        try {
            var currentDate = ""
            var currentTime = ""
            try {
                val calendar = Calendar.getInstance()
                currentDate =
                    DateFormat.getDateInstance(DateFormat.YEAR_FIELD).format(calendar.time)
                val format = SimpleDateFormat("HH:mm")
                currentTime = format.format(calendar.time)

                val timestamp = Timestamp(System.currentTimeMillis())
                currentTime = timestamp.time.toString()

            } catch (e: Exception) {
            }
            if (spinnerIndex == 0) {
                Snackbar.make(editTextMoney!!, "خریدار را انتخاب کنید.", Snackbar.LENGTH_LONG)
                    .show()
                return false
            }
            if (editTextMoney?.text.toString() == "") {
                editTextMoney?.error = "فیلد نباید خالی باشد."
                return false
            }

            val spent = Spent(
                0,
                spinnerIndex,
                editTextMoney?.text.toString().replace(",", "").toInt(),
                null,
                currentTime,
                editTextAbout?.text.toString()
            )
            var flag = false
            for (p in list) {
                if (p.flag) {
                    flag = true
                    break
                }
            }
            if (flag) {
                db?.insertSpent(spent)

                val l = db?.getSpents()
                val n = l?.last()?.id
                for (p in list) {
                    if (p.flag)
                        db?.insertParts(Part(0, n!!, p.id))
                }
                return true
            } else {
                Snackbar.make(
                    editTextMoney!!,
                    "یک نفر را از لیست انتخاب کنید",
                    Snackbar.LENGTH_LONG
                )
                    .show()
            }
            return false
        } catch (e: ExceptionInInitializerError) {
            Snackbar.make(editTextMoney!!, "مشکلی پیش آمده", Snackbar.LENGTH_LONG)
                .show()
            return false
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        spinnerIndex = position
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

    override fun onBackPressed() {
        super.onBackPressed()
        buttonAdd?.setIconResource(R.drawable.ic_plus)
    }
}