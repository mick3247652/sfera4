package ru.club.sfera.questions.adapters

import android.content.Intent
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.android.volley.toolbox.ImageLoader
import com.mikhaellopez.circularimageview.CircularImageView
import ru.club.sfera.R
import ru.club.sfera.app.App
import ru.club.sfera.questions.activity.AnswersActivity
import ru.club.sfera.questions.model.Question


class QuestionRecyclerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var questions: List<Question>? = null
    internal var imageLoader: ImageLoader? = App.getInstance().imageLoader

    var onDeleteClick: (String) -> Unit = { v -> Log.v("NOT BINDING", "v = $v") }

    fun setQuestions(q: List<Question>) {
        questions = q
    }

    fun updateList(){
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        val v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_question, viewGroup, false) as CardView
        return object : RecyclerView.ViewHolder(v) {}

    }

    override fun getItemCount() = questions?.size ?: 0

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        questions?: return
        val v = viewHolder.itemView as CardView
        val photo = v.findViewById<CircularImageView?>(R.id.questionUserPhoto)
        val name = v.findViewById<TextView>(R.id.questionUserName)
        val questionText = v.findViewById<TextView>(R.id.questionText)
        val answersCount = v.findViewById<TextView>(R.id.answersCount)
        val deleteQuestion = v.findViewById<ImageView>(R.id.deleteQuestion)
        val profileId = App.getInstance().getId().toString()

        if(questions!![position].email.equals(profileId)) deleteQuestion.visibility = View.VISIBLE
        else deleteQuestion.visibility = View.GONE

        deleteQuestion.setOnClickListener {
            onDeleteClick(questions!![position]._id)
        }

        name.text = questions!![position].name
        questionText.text = questions!![position].question
        answersCount.text = questions!![position].answers.size.toString()

        val photoUrl = questions!![position].photo
        if(photoUrl != null){
            if (imageLoader == null) {
                imageLoader = App.getInstance().imageLoader
            }
            imageLoader?.get(photoUrl, ImageLoader.getImageListener(photo, R.drawable.profile_default_photo, R.drawable.profile_default_photo))
        }

        v.setOnClickListener {
            val i = Intent(it.context, AnswersActivity::class.java)
            i.putExtra("questionId", questions!![position]._id)
            it.context.startActivity(i)
        }
    }
}