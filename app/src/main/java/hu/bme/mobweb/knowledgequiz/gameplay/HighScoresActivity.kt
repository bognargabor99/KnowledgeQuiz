package hu.bme.mobweb.knowledgequiz.gameplay

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.room.Room
import hu.bme.mobweb.knowledgequiz.R
import hu.bme.mobweb.knowledgequiz.adapter.ScoreAdapter
import hu.bme.mobweb.knowledgequiz.data.Score
import hu.bme.mobweb.knowledgequiz.data.ScoreDatabase
import kotlinx.android.synthetic.main.content_high_scores.*
import kotlin.concurrent.thread

class HighScoresActivity : AppCompatActivity(), ScoreAdapter.ScoreClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ScoreAdapter
    private lateinit var database: ScoreDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_high_scores)

        database = Room.databaseBuilder(
            applicationContext,
            ScoreDatabase::class.java,
            "knowledgequiz"
        ).build()
        initRecyclerView()
    }

    private fun initRecyclerView() {
        recyclerView = MainRecyclerView
        adapter = ScoreAdapter(this)
        loadItemsInBackground()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun loadItemsInBackground() {
        thread {
            val items = database.scoreDao().getAll()
            runOnUiThread {
                adapter.update(items)
            }
        }
    }

    override fun onItemDeleted(item: Score) {
        thread {
            database.scoreDao().delete(item)
            Log.d("HighScoreActivity", "Score deletion was successful")
        }
    }

    override fun onAllItemsDeleted() {
        thread {
            database.scoreDao().removeAll()
            Log.d("HighScoreActivity", "All Score deletion was successful")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_high_scores, menu)
        val searchItem =  menu?.findItem(R.id.action_search)
        if (searchItem!=null) {
            val searchView = searchItem.actionView as SearchView

            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean = true

                override fun onQueryTextChange(newText: String?): Boolean {
                    adapter.filterCategoryByString(newText?:"")
                    return true
                }

            })
        }
        return true
    }

    /*override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.let {
            val bestOnly = it.findItem(R.id.action_filter_best)
            val viewAll = it.findItem(R.id.action_restore)
            bestOnly.isVisible = !bestOnly.isVisible
            viewAll.isVisible = !viewAll.isVisible
        }
        return true
    }*/
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_remove_all -> {
                removeAllScores()
                true
            }
            R.id.action_filter_best -> {
                adapter.filterBestScores()
                true
            }
            R.id.action_restore -> {
                adapter.restoreView()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun removeAllScores() {
        AlertDialog.Builder(this)
                .setTitle(R.string.are_you_sure)
                .setPositiveButton(R.string.ok) { _, _ ->
                    thread {
                        database.scoreDao().removeAll()
                        runOnUiThread {
                            adapter.removeAll()
                        }
                    }
                }
                .setNegativeButton(R.string.cancel, null)
                .create().show()
    }
}