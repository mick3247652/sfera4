package ru.club.sfera.questions.adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter

class ViewPagerAdapter(fm: FragmentManager): FragmentStatePagerAdapter(fm) {
    private val fragments = mutableListOf<Fragment>()

    override fun getItem(position: Int) = fragments[position]
    override fun getCount() = fragments.size

    fun add(f: Fragment) = fragments.add(f)

}