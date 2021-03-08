package ir.mostafa.nematpour.khodhesab

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)


    }

    override fun onResume() {
        super.onResume()
        val sharedPreferences: SharedPreferences =
            this.getSharedPreferences("applicationState", Context.MODE_PRIVATE)
        val state = sharedPreferences.getString("state", "PersonAddNoComplete")

        val textView = findViewById<TextView>(R.id.text_splash)
        Handler().postDelayed({
            val intent = Intent(
                this@SplashActivity,
                if (state == "PersonAddComplete") MainActivity::class.java else GetPerson::class.java
            )


            startActivity(intent)
            finish()
        }, 2000)
    }
}