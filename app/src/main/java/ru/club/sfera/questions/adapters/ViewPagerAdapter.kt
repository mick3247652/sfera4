package ru.club.sfera.questions.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class ViewPagerAdapter(fm: FragmentManager): FragmentStatePagerAdapter(fm) {
    private val fragments = mutableListOf<Fragment>()

    override fun getItem(position: Int) = fragments[position]
    override fun getCount() = fragments.size

    fun add(f: Fragment) = fragments.add(f)

}