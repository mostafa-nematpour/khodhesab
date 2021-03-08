package ir.mostafa.nematpour.khodhesab

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import ir.mostafa.nematpour.khodhesab.dataBase.DataBaseManager

class ManageSpaceActivity : AppCompatActivity() {
    private val sharedPrefFile = "applicationState"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_space)
        val button = findViewById<Button>(R.id.delete_space)
        button.setOnClickListener {
            val db = DataBaseManager(applicationContext)
            db.deleteAll()
            val sharedPreferences: SharedPreferences =
                this.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            editor.putString("state", "PersonAddNoComplete")
            editor.apply()
            editor.commit()
        }
    }
}