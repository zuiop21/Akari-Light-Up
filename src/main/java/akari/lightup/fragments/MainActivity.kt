package akari.lightup.fragments

import akari.lightup.FileManager
import akari.lightup.R
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val jsonFileManager = FileManager(applicationContext)
        jsonFileManager.createJSONFile()
    }


}