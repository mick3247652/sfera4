package ru.club.sfera.questions

import retrofit2.Call
import retrofit2.http.*
import ru.club.sfera.questions.ConfigQuestions.API_ADD_ANSWER
import ru.club.sfera.questions.ConfigQuestions.API_ADD_QUESTION
import ru.club.sfera.questions.ConfigQuestions.API_DELETE_QUESTION
import ru.club.sfera.questions.ConfigQuestions.API_GET_ALL_QUESTIONS
import ru.club.sfera.questions.ConfigQuestions.API_GET_QUESTION
import ru.club.sfera.questions.ConfigQuestions.API_GET_QUESTIONS
import ru.club.sfera.questions.model.Question

interface QuestionsApi {
    @GET(API_GET_ALL_QUESTIONS)
    abstract fun getAllQuestions(): Call<List<Question>>

    @POST(API_GET_QUESTIONS)
    @Headers("Content-Type: application/json")
    abstract fun getQuestions(@Body body: String): Call<List<Question>>

    @POST(API_ADD_QUESTION)
    @Headers("Content-Type: application/json")
    abstract fun addQuestion(@Body body: String): Call<String>

    @POST(API_DELETE_QUESTION)
    @Headers("Content-Type: application/json")
    abstract fun deleteQuestion(@Body body: String): Call<String>

    @POST(API_GET_QUESTION)
    @Headers("Content-Type: application/json")
    abstract fun getQuestion(@Body body: String): Call<Question>

    @POST(API_ADD_ANSWER)
    @Headers("Content-Type: application/json")
    abstract fun addAnswer(@Body body: String): Call<String>

}