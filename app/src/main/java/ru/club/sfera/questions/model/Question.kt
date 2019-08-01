package ru.club.sfera.questions.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Answer (
        @SerializedName("_id")
        @Expose
        val _id: String,
        @SerializedName("fromEmail")
        @Expose
        val fromEmail: String,
        @SerializedName("name")
        @Expose
        val name: String,
        @SerializedName("answer")
        @Expose
        val answer: String,
        @SerializedName("photo")
        @Expose
        val photo: String?

        )

data class Question(
        @SerializedName("_id")
        @Expose
        val _id: String,
        @SerializedName("email")
        @Expose
        val email: String,
        @SerializedName("name")
        @Expose
        val name: String,
        @SerializedName("photo")
        @Expose
        val photo: String?,
        @SerializedName("question")
        @Expose
        val question: String,
        @SerializedName("created")
        @Expose
        val created: String,
        @SerializedName("answers")
        @Expose
        val answers: List<Answer>
)