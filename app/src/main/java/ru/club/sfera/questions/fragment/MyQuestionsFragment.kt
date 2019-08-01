package ru.club.sfera.questions.fragment


import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_my_questions.view.*

import ru.club.sfera.R
import ru.club.sfera.questions.viewmodel.QuestionViewModel

class MyQuestionsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_my_questions, container, false)

        val model = ViewModelProviders.of(activity!!).get(QuestionViewModel::class.java)

        val recycler = v.questionsList
        recycler.adapter = model.myQuestionRecyclerAdapter
        recycler.layoutManager = LinearLayoutManager(activity)

        return v
    }


}