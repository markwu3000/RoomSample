package com.example.roomwordsample

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.roomwordsample.room.Word
import com.example.roomwordsample.view.WordListAdapter
import com.example.roomwordsample.viewmodel.WordViewModel
import com.idescout.sql.SqlScoutServer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.fab
import kotlinx.android.synthetic.main.activity_main.toolbar

// https://codelabs.developers.google.com/codelabs/android-room-with-a-view-kotlin
// https://codelabs.developers.google.com/codelabs/kotlin-android-training-room-database
// https://codelabs.developers.google.com/android-kotlin-fundamentals/

class MainActivity : AppCompatActivity() {
    private lateinit var wordViewModel: WordViewModel
    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        SqlScoutServer.create(this, getPackageName());

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            val intent = Intent(this@MainActivity, NewWordActivity::class.java)
            startActivityForResult(intent, NEW_WORD_ACTIVITY_REQUEST_CODE)
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        val adapter = WordListAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Deprecated
        // wordViewModel = ViewModelProviders.of(this).get(WordViewModel::class.java)

        // https://stackoverflow.com/questions/53903762/viewmodelproviders-is-deprecated-in-1-1-0
        wordViewModel = ViewModelProvider(this).get(WordViewModel::class.java)

        compositeDisposable.add(
                wordViewModel.allWords
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                                       adapter.setWords(it)
                                       Log.e("MSW", "observing allWords changed.  set words on adapter")
                                   },
                                   {
                                       Log.e("MSW", "error: ${it.message}")
                                   })
        )
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == NEW_WORD_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.let {
                val newWord = it.getStringExtra(NewWordActivity.KEY_NEW_WORD_TO_ADD) ?: ""
                if (newWord.isNotEmpty()) {
                    wordViewModel.insert(Word(newWord, System.currentTimeMillis()))
                }
            }
        } else {
            Toast.makeText(
                    applicationContext,
                    R.string.empty_not_saved,
                    Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        wordViewModel.cleanup()
        compositeDisposable.dispose()
    }

    companion object {
        const val NEW_WORD_ACTIVITY_REQUEST_CODE = 1
    }
}
