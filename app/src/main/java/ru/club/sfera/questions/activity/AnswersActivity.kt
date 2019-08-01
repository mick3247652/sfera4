package ru.club.sfera.questions.activity

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.android.volley.toolbox.ImageLoader
import kotlinx.android.synthetic.main.activity_answers.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import ru.club.sfera.R
import ru.club.sfera.app.App
import ru.club.sfera.questions.ConfigQuestions
import ru.club.sfera.questions.QuestionsApi
import ru.club.sfera.questions.adapters.AnswerRecyclerAdapter
import ru.club.sfera.questions.model.Question

class AnswersActivity : AppCompatActivity() {

    private val adapter = AnswerRecyclerAdapter()
    private var questionId = ""
    private var question: Question? = null
    internal var imageLoader: ImageLoader? = App.getInstance().imageLoader

    private val questionsApi = Retrofit.Builder()
        .baseUrl(ConfigQuestions.BASE_ADDRESS)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(QuestionsApi::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_answers)

        setSupportActionBar(toolbarOrientation)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.title = getString(R.string.menu_answers_title)

        configRecycler()
        questionId = intent.getStringExtra("questionId")

        loadAnswers()
        addAnswerButton()
        answerWindow()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
    }

    private fun configRecycler(){
        answerRecycler.adapter = adapter
        answerRecycler.layoutManager = LinearLayoutManager(this)
    }

    private fun loadAnswers(){
        if(questionId.isEmpty()) finish()
        val paramObject = JSONObject()
        paramObject.put("_id", questionId)
        questionsApi.getQuestion(paramObject.toString()).enqueue(object : Callback<Question> {
            override fun onFailure(call: Call<Question>, t: Throwable) {}
            override fun onResponse(call: Call<Question>, response: Response<Question>) {

                question = response.body()
                if(question != null) {
                    adapter.setQuestion(question!!)
                    adapter.updateList()

                    val photoUrl = question!!.photo
                    if(photoUrl != null){
                        if (imageLoader == null) {
                            imageLoader = App.getInstance().imageLoader
                        }
                        imageLoader?.get(photoUrl, ImageLoader.getImageListener(questionUserPhoto, R.drawable.profile_default_photo, R.drawable.profile_default_photo))
                    }

                    questionUserName.text = question!!.name
                    questionText.text = question!!.question
                }

            }
        })

    }

    @SuppressLint("RestrictedApi")
    private fun addAnswerButton(){
        addAnswer.setOnClickListener {
            answerWindow.visibility = View.VISIBLE
            addAnswer.visibility = View.GONE
            editAnswer.requestFocus()
            showKeyboard(editAnswer)
        }
    }

    @SuppressLint("RestrictedApi")
    private fun answerWindow(){
        closeAnswerWindow.setOnClickListener{
            editAnswer.text.clear()
            editAnswer.clearFocus()
            hideKeyboard(editAnswer)
            answerWindow.visibility = View.GONE
            addAnswer.visibility = View.VISIBLE
        }
        sendAnswer.setOnClickListener{
            val text = editAnswer.text.toString()
            editAnswer.text.clear()
            editAnswer.clearFocus()
            hideKeyboard(editAnswer)
            answerWindow.visibility = View.GONE
            addAnswer.visibility = View.VISIBLE
            if(!text.isEmpty()) addAnswerRequest(text)
        }
    }

    private fun addAnswerRequest(text: String){
        val paramObject = JSONObject()
        val profileId = App.getInstance().getId().toString()
        val userName = App.getInstance().getUsername()
        val photo = App.getInstance().getPhotoUrl()

        paramObject.put("_id", questionId)
        paramObject.put("email", profileId)
        paramObject.put("answer", text)
        paramObject.put("name", userName)
        paramObject.put("photo", photo)

        questionsApi.addAnswer(paramObject.toString()).enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {}
            override fun onResponse(call: Call<String>, response: Response<String>) {
                loadAnswers()
            }
        })

    }

    fun showKeyboard(et: EditText){
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT)
    }

    fun hideKeyboard(et: EditText){
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(et.getWindowToken(), 0)
    }

}
