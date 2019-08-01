package ru.club.sfera.questions

object ConfigQuestions {
    const val BASE_ADDRESS = "https://questionserver.herokuapp.com"
    const val API_HELLO = "/api/home" //GET method, return text
    const val API_VERSION = "/api/version" //GET method, return JSON
    const val API_ADD_QUESTION = "/api/add_question" //POST method
    const val API_DELETE_QUESTION = "/api/delete_question" //POST method
    const val API_GET_QUESTIONS = "/api/get_questions" //GET method
    const val API_GET_ALL_QUESTIONS = "/api/get_all_questions" //GET method
    const val API_GET_QUESTION = "/api/get_question" //POST method
    const val API_ADD_ANSWER = "/api/add_answer" //POST method

}

