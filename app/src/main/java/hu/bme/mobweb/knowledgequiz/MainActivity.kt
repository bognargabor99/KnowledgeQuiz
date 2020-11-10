package hu.bme.mobweb.knowledgequiz

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityOptionsCompat
import hu.bme.mobweb.knowledgequiz.gameplay.GamePlayActivity
import hu.bme.mobweb.knowledgequiz.gameplay.HighScoresActivity
import hu.bme.mobweb.knowledgequiz.settings.SettingsActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.util.Locale

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn_play.setOnClickListener { newGameClick()}
        if (Locale.getDefault().displayLanguage.toString() != "English")
            AlertDialog.Builder(this)
                    .setTitle(R.string.language_alert_title)
                    .setMessage(R.string.language_alert_message)
                    .setPositiveButton(R.string.ok, null)
                    .show()
    }

    private fun newGameClick() {
        AlertDialog.Builder(this)
                .setTitle(R.string.pick_category)
                .setItems(R.array.categories_array
                ) { _, which ->
                    val intent = Intent(this@MainActivity, GamePlayActivity::class.java)
                    intent.putExtra("categoryId", getCategory(which))
                    startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle())
                }
                .create()
                .show()
    }

    private fun getCategory(selectedIndex: Int): String = when (selectedIndex) {
        0 -> "0"
        1 -> "9"
        2 -> "19"
        3 -> "18"
        4 -> "21"
        5 -> "23"
        6 -> "27"
        7 -> "24"
        else -> "0"
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                val i = Intent(this, SettingsActivity::class.java)
                startActivity(i, ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle())
                true
            }
            R.id.action_high_scores -> {
                val i = Intent(this, HighScoresActivity::class.java)
                startActivity(i, ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle())
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
