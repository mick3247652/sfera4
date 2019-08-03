package ru.club.sfera.questions.activity

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModelProviders
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_questions.*
import ru.club.sfera.R
import ru.club.sfera.questions.adapters.ViewPagerAdapter
import ru.club.sfera.questions.fragment.AllQuestionsFragment
import ru.club.sfera.questions.fragment.MyQuestionsFragment
import androidx.viewpager.widget.ViewPager
import android.util.Log
import android.view.View
import ru.club.sfera.questions.viewmodel.QuestionViewModel
import android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT
import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.EditText




class QuestionsActivity : AppCompatActivity() {
    private var prevMenuItem: MenuItem? = null
    private lateinit var model: QuestionViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_questions)

        setSupportActionBar(toolbarOrientation)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.title = getString(R.string.menu_questions_title)
        model = ViewModelProviders.of(this).get(QuestionViewModel::class.java)

        setupViewPager()
        bottomNavigation()
        pagerNavigation()
        addQuestionButton()
        questionWindow()

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

    private fun setupViewPager() {
        val pagerAdapter = ViewPagerAdapter(supportFragmentManager)
        pagerAdapter.add(MyQuestionsFragment())
        pagerAdapter.add(AllQuestionsFragment())
        viewPager.adapter = pagerAdapter
    }

    private fun bottomNavigation() {
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.my_question_id -> viewPager.currentItem = 0
                R.id.all_question_id -> viewPager.currentItem = 1
            }
            false
        }
    }

    private fun pagerNavigation() {
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                if (prevMenuItem != null)
                    prevMenuItem?.isChecked = false
                else
                    bottomNavigationView.menu.getItem(0).isChecked = false

                bottomNavigationView.menu.getItem(position).isChecked = true
                prevMenuItem = bottomNavigationView.menu.getItem(position)
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })
    }

    @SuppressLint("RestrictedApi")
    private fun addQuestionButton(){
        addQuestion.setOnClickListener {
            Log.v("QUESTION", "Add question")
            //model.addQuestion("Test Question")
            questionWindow.visibility = View.VISIBLE
            addQuestion.visibility = View.GONE
            editQuestion.requestFocus()
            showKeyboard(editQuestion)
        }
    }

    @SuppressLint("RestrictedApi")
    private fun questionWindow(){
        closeQuestionWindow.setOnClickListener{
            editQuestion.text.clear()
            editQuestion.clearFocus()
            hideKeyboard(editQuestion)
            questionWindow.visibility = View.GONE
            addQuestion.visibility = View.VISIBLE

        }
        sendQuestion.setOnClickListener{
            val text = editQuestion.text.toString()
            editQuestion.text.clear()
            editQuestion.clearFocus()
            hideKeyboard(editQuestion)
            questionWindow.visibility = View.GONE
            addQuestion.visibility = View.VISIBLE
            if(!text.isEmpty()) model.addQuestion(text)
        }
    }

    fun showKeyboard(et: EditText){
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(et, SHOW_IMPLICIT)
    }

    fun hideKeyboard(et: EditText){
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(et.getWindowToken(), 0)
    }



}
