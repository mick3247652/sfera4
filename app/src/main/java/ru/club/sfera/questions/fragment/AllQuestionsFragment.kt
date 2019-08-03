package ru.club.sfera.questions.fragment


import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_all_questions.view.*

import ru.club.sfera.R
import ru.club.sfera.questions.viewmodel.QuestionViewModel

class AllQuestionsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_all_questions, container, false)

        val model = ViewModelProviders.of(activity!!).get(QuestionViewModel::class.java)

        val recycler = v.questionsList
        recycler.adapter = model.allQuestionRecyclerAdapter
        recycler.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(activity)

        return v
    }


}
