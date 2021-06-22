package com.yourapp.thirukkural

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import java.util.*
import kotlin.random.Random

var tamilFlag  = false
var orientation = 1

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private var verseNumber = Random.nextInt(0,1330)

    private lateinit var verseArray : Array<String>
    private lateinit var explanationArray : Array<String>

    private lateinit var lang : String

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(savedInstanceState != null)
        {
            verseNumber = savedInstanceState.getInt("verseNumber", 0)
            tamilFlag = savedInstanceState.getBoolean("translationFlag", false)
            orientation = savedInstanceState.getInt("orientation", 1)
        }

        val currentOrientation = resources.configuration.orientation

        if ( orientation == currentOrientation)
        {
            if(!tamilFlag){

                val locale = Locale("ta")
                //Locale.setDefault(locale)
                val config = Configuration()
                config.locale = locale
                resources.updateConfiguration(
                    config,
                    resources.displayMetrics
                )
                this.setContentView(R.layout.activity_main)
                tamilFlag = true
                //Toast.makeText(this,"changed to tamil, tamil flag: " + tamilFlag.toString(), Toast.LENGTH_SHORT).show()
            }
            else
                tamilFlag = false
        }
        else
        {
            orientation = currentOrientation
            if(tamilFlag)
            {
                val locale = Locale("ta")
                //Locale.setDefault(locale)
                val config = Configuration()
                config.locale = locale
                resources.updateConfiguration(
                    config,
                    resources.displayMetrics
                )
                this.setContentView(R.layout.activity_main)
                //tamilFlag = true
            }

        }

        lang = resources.configuration.locales.toLanguageTags()

        val uri = if(lang == "en-IN" || lang == "en")
            "https://en.wikipedia.org/wiki/Thiruvalluvar"
        else
            "https://ta.wikipedia.org/wiki/%E0%AE%A4%E0%AE%BF%E0%AE%B0%E0%AF%81%E0%AE%B5%E0%AE%B3%E0%AF%8D%E0%AE%B3%E0%AF%81%E0%AE%B5%E0%AE%B0%E0%AF%8D"

        val refreshLayout  = findViewById<SwipeRefreshLayout>(R.id.refreshLayout)

        refreshLayout.setOnRefreshListener {
            reloadVerse()
            refreshLayout.isRefreshing = false
        }

        val thiruvalluvarImage = findViewById<ImageView>(R.id.imageView)

        thiruvalluvarImage.setOnLongClickListener {
            val openURL = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            startActivity(openURL)
            true
        }

    }

    override fun onResume() {
        super.onResume()
        getVerse(verseNumber)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("verseNumber", verseNumber)
        outState.putBoolean("translationFlag", tamilFlag)
        outState.putInt("orientation", orientation)
    }

    private fun getVerse(number : Int){
         verseArray  = resources.getStringArray(R.array.kural)
         explanationArray  = resources.getStringArray(R.array.vilakkam)

        val verse : TextView = findViewById(R.id.kural)
        verse.text = verseArray[number]

        val explanation : TextView = findViewById(R.id.vilakkam)
        explanation.text = explanationArray[number]
    }

    fun onReloadClick(view: View){
        reloadVerse()
    }

    private fun reloadVerse() {
        verseNumber = Random.nextInt(0,1330)
        getVerse(verseNumber)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menus, menu)
        return true

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.translate -> {
                toggleLanguage()
                true
            }
            R.id.share -> {
                shareVerse()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun toggleLanguage(){
        if(tamilFlag)
            setLocale("en")
        else
            recreate()
    }

    private fun shareVerse(){
        val message = resources.getString(R.string.verse) + '\n' + verseArray[verseNumber] + '\n' + resources.getString(R.string.explanation)+
                '\n' + explanationArray[verseNumber]

        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, message)
            type = "text/plain"
        }
        startActivity(shareIntent)
    }

    private fun setLocale(locale : String){
        val dm: DisplayMetrics = resources.displayMetrics
        val config: Configuration = resources.configuration
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(Locale(locale.toLowerCase()))
        } else {
            config.locale = Locale(locale.toLowerCase())
        }
        resources.updateConfiguration(config, dm)

        recreate()
    }

}