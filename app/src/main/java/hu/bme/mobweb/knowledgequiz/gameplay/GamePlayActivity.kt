package hu.bme.mobweb.knowledgequiz.gameplay

import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.core.text.HtmlCompat.fromHtml
import androidx.preference.PreferenceManager
import androidx.room.Room
import hu.bme.mobweb.knowledgequiz.R
import hu.bme.mobweb.knowledgequiz.data.Score
import hu.bme.mobweb.knowledgequiz.data.ScoreDatabase
import hu.bme.mobweb.knowledgequiz.model.Question
import hu.bme.mobweb.knowledgequiz.model.Response
import hu.bme.mobweb.knowledgequiz.network.NetworkManager
import kotlinx.android.synthetic.main.activity_game_play.*
import kotlin.concurrent.thread

class GamePlayActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var categoryId: String
    private lateinit var response: Response
    private var questionNum: Int = 0
    private lateinit var answers: Array<String>
    private lateinit var database: ScoreDatabase
    private var goodAnswers: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_play)
        categoryId = intent.getStringExtra("categoryId")?:"0"
        answers = Array(10) { "" }
        btn_answerA.setOnClickListener(this)
        btn_answerB.setOnClickListener(this)
        btn_answerC.setOnClickListener(this)
        btn_answerD.setOnClickListener(this)

        database = Room.databaseBuilder(
            applicationContext,
            ScoreDatabase::class.java,
            "knowledgequiz")
            .build()
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this)
                .setMessage(R.string.are_you_sure_want_to_exit)
                .setPositiveButton(R.string.ok) { _, _ -> super.onBackPressed() }
                .setNegativeButton(R.string.cancel, null)
                .show()
    }

    override fun onResume() {
        super.onResume()
        loadQuestions()
        goodAnswers = 0
    }

    private fun onSuccess(apiResponse: Response) {
        questionNum = 0
        response = apiResponse
        response.let {
            randomizeAnswers()
            displayQuestion()
            setQuestionVisible()
            timer.start()
        }
    }

    private fun displayQuestion() {
        // for quotation marks etc.
        tvQuestion.text = fromHtml(response.results[questionNum].question, HtmlCompat.FROM_HTML_MODE_COMPACT)
        val questionType = Question.Type.from(response.results[questionNum].type)
        tvChoices.text = fromHtml(getChoicesText(questionType), HtmlCompat.FROM_HTML_MODE_COMPACT)
        val visibilityCD = getVisibleCDAnswers(questionType)
        btn_answerC.visibility = visibilityCD
        btn_answerD.visibility = visibilityCD
    }

    private fun getVisibleCDAnswers(questionType: Question.Type) : Int =  when (questionType) {
        Question.Type.MULTIPLE -> View.VISIBLE
        Question.Type.TRUE_FALSE -> View.INVISIBLE
    }

    private fun getChoicesText(questionType: Question.Type): String = when (questionType) {
        Question.Type.MULTIPLE -> getString(R.string.answer_choices_multiple,
            response.results[questionNum].allAnswers[0],
            response.results[questionNum].allAnswers[1],
            response.results[questionNum].allAnswers[2],
            response.results[questionNum].allAnswers[3])
        else -> getString(R.string.answer_choices_true_false,
            response.results[questionNum].allAnswers[0],
            response.results[questionNum].allAnswers[1])
    }

    private fun setQuestionVisible() {
        progressbar.visibility = View.INVISIBLE
        tvLoading.visibility = View.INVISIBLE
        timer.visibility = View.VISIBLE
        tvQuestion.visibility = View.VISIBLE
        tvChoices.visibility = View.VISIBLE
        table.visibility = View.VISIBLE
    }

    private fun randomizeAnswers() {
        for (i in 0..9) {
            response.let {
                if (it.results[i].type == "multiple") {
                    val randList = it.results[i].incorrect_answers
                    randList.add(it.results[i].correct_answer)
                    // randomizing the answers
                    randList.shuffle()
                    it.results[i].allAnswers = randList
                }
                else
                    it.results[i].allAnswers = mutableListOf(getString(R.string.answer_true), getString(R.string.answer_false))
            }
        }
    }

    private fun onError(throwable: Throwable) {
        throwable.printStackTrace()
        AlertDialog.Builder(this)
                .setMessage("Network error occured\nPlease check your internet connection or try again later")
                .show()
        super.onBackPressed()
    }

    private fun loadQuestions() {
        NetworkManager.getResponse(::onSuccess, ::onError, categoryId)
    }

    override fun onClick(p0: View?) {
        if (p0 is Button) {
            val num = p0.text[0] - 65
            answers[questionNum] = response.results[questionNum].allAnswers[num.toInt()]
            if (questionNum!=9) {
                questionNum++
                displayQuestion()
            }
            else
                evaluateAnswers()
        }
    }

    private fun evaluateAnswers() {
        for (i in 0..9)
            if (answers[i]==response.results[i].correct_answer)
                goodAnswers++

        val playerName: String? = PreferenceManager.getDefaultSharedPreferences(this).getString("signature", "")
        val elapsedSeconds: Int = ((SystemClock.elapsedRealtime() - timer.base)/1000).toInt()
        val score = Score(null, playerName?:"Unknown", categoryId, goodAnswers, elapsedSeconds)
        thread {
            database.scoreDao().insert(score)
        }
        AlertDialog.Builder(this)
                .setPositiveButton(R.string.ok) { _, _ -> super.onBackPressed()}
                .setOnDismissListener { super.onBackPressed()}
                .setMessage(getString(R.string.your_score_out_of_10, goodAnswers))
                .create()
                .show()
    }
}