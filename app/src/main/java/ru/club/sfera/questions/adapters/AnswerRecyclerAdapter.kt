package ru.club.sfera.questions.adapters

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
import ru.club.sfera.questions.model.Answer
import ru.club.sfera.questions.model.Question

class AnswerRecyclerAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var answers: List<Answer>? = null
    internal var imageLoader: ImageLoader? = App.getInstance().imageLoader

    var onDeleteClick: (String) -> Unit = { v -> Log.v("NOT BINDING", "v = $v") }

    fun setQuestion(q: Question) {
        answers = q.answers
    }

    fun updateList(){
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        val v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_answer, viewGroup, false) as CardView
        return object : RecyclerView.ViewHolder(v) {}
    }

    override fun getItemCount() = answers?.size ?: 0

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        answers?: return
        val v = viewHolder.itemView as CardView
        val photo = v.findViewById<CircularImageView?>(R.id.answerUserPhoto)
        val name = v.findViewById<TextView>(R.id.answerUserName)
        val questionText = v.findViewById<TextView>(R.id.answerText)
        val deleteQuestion = v.findViewById<ImageView>(R.id.deleteAnswer)
        val profileId = App.getInstance().getId().toString()

        if(answers!![position].fromEmail.equals(profileId)) deleteQuestion.visibility = View.GONE
        else deleteQuestion.visibility = View.GONE

        deleteQuestion.setOnClickListener {
            onDeleteClick(answers!![position]._id)
        }

        name.text = answers!![position].name
        questionText.text = answers!![position].answer

        val photoUrl = answers!![position].photo
        if(photoUrl != null){
            if (imageLoader == null) {
                imageLoader = App.getInstance().imageLoader
            }
            imageLoader?.get(photoUrl, ImageLoader.getImageListener(photo, R.drawable.profile_default_photo, R.drawable.profile_default_photo))
        }
    }
}