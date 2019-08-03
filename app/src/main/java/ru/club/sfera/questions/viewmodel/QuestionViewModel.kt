package ru.club.sfera.questions.viewmodel

import androidx.lifecycle.ViewModel
import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import ru.club.sfera.app.App
import ru.club.sfera.questions.ConfigQuestions.BASE_ADDRESS
import ru.club.sfera.questions.QuestionsApi
import ru.club.sfera.questions.adapters.QuestionRecyclerAdapter
import ru.club.sfera.questions.model.Question
import org.json.JSONObject


class QuestionViewModel : ViewModel() {
    private val allQuestions = mutableListOf<Question>()
    private val myQuestions = mutableListOf<Question>()

    var isLoadingAllQuestion = false
    var isLoadingMyQuestion = false
    private var questionsApi: QuestionsApi

    val allQuestionRecyclerAdapter: QuestionRecyclerAdapter = QuestionRecyclerAdapter()
    val myQuestionRecyclerAdapter: QuestionRecyclerAdapter = QuestionRecyclerAdapter()

    init {
        questionsApi = Retrofit.Builder()
            .baseUrl(BASE_ADDRESS)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(QuestionsApi::class.java)
        loadAllQuestions()
        loadMyQuestions()
        allQuestionRecyclerAdapter.onDeleteClick = { v -> onDeleteQuestion(v) }
        myQuestionRecyclerAdapter.onDeleteClick = { v -> onDeleteQuestion(v) }
    }

    fun addQuestion(question: String) {
        val profileId = App.getInstance().getId().toString()
        val userName = App.getInstance().getUsername()
        val photo = App.getInstance().getPhotoUrl()
        Log.v("ViewModel", "userName: ${userName}  prifileId: ${profileId}")
        val paramObject = JSONObject()
        paramObject.put("email", profileId)
        paramObject.put("name", userName)
        paramObject.put("question", question)
        paramObject.put("photo", photo)
        questionsApi.addQuestion(paramObject.toString()).enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {}
            override fun onResponse(call: Call<String>, response: Response<String>) {
                loadAllQuestions()
                loadMyQuestions()
            }
        })

    }

    private fun loadAllQuestions() {
        if (isLoadingAllQuestion) return
        isLoadingAllQuestion = true

        questionsApi.getAllQuestions().enqueue(object : Callback<List<Question>> {
            override fun onResponse(call: Call<List<Question>>, response: Response<List<Question>>) {
                Log.v("Responce", "Данные пришли")
                if (response.body() == null) {
                    Log.v("Responce", "но их нету")
                    return
                }

                Log.v("Responce", "Данные в наличии")

                val list = response.body()
                allQuestions.clear()
                allQuestions.addAll(list!!)

                allQuestionRecyclerAdapter.setQuestions(allQuestions)
                allQuestionRecyclerAdapter.updateList()
                //Список есть
                isLoadingAllQuestion = false
            }

            override fun onFailure(call: Call<List<Question>>, t: Throwable) {
                Log.v("Responce", "Данные не пришли")
                isLoadingAllQuestion = false
            }
        })

    }

    private fun loadMyQuestions() {
        if (isLoadingMyQuestion) return
        isLoadingMyQuestion = true

        val profileId = App.getInstance().getId().toString()
        val paramObject = JSONObject()
        paramObject.put("email", profileId)

        questionsApi.getQuestions(paramObject.toString()).enqueue(object : Callback<List<Question>> {
            override fun onResponse(call: Call<List<Question>>, response: Response<List<Question>>) {
                Log.v("Responce", "Данные пришли")
                if (response.body() == null) {
                    Log.v("Responce", "но их нету")
                    return
                }

                Log.v("Responce", "Данные в наличии")

                val list = response.body()
                myQuestions.clear()
                myQuestions.addAll(list!!)

                myQuestionRecyclerAdapter.setQuestions(myQuestions)
                myQuestionRecyclerAdapter.updateList()
                //Список есть
                isLoadingMyQuestion = false
            }

            override fun onFailure(call: Call<List<Question>>, t: Throwable) {
                Log.v("Responce", "Данные не пришли")
                isLoadingMyQuestion = false
            }
        })
    }

    fun onDeleteQuestion(id: String) {
        Log.v("Question", "delte question ${id}")
        val paramObject = JSONObject()
        paramObject.put("_id", id)
        questionsApi.deleteQuestion(paramObject.toString()).enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {}
            override fun onResponse(call: Call<String>, response: Response<String>) {
                loadAllQuestions()
                loadMyQuestions()
            }
        })


    }
}