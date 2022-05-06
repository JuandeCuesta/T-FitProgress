package edu.juandecuesta.t_fitprogress.utils

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

/**
 * Clase para cargar los fragments en los tabs correspondientes
 */
class ViewPager2Adapter(fragmentManager: FragmentManager, lifecycle: Lifecycle):

    FragmentStateAdapter(fragmentManager,lifecycle)
{

    private val mFragmentList = ArrayList<Fragment>()
    private val mFragmentTitleList = ArrayList<String>()

    override fun getItemCount(): Int {
        return mFragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return mFragmentList[position]
    }

    fun addFragment(fragment: Fragment, title:String){
        mFragmentList.add(fragment)
        mFragmentTitleList.add(title)
    }

    fun getPageTitle (position: Int):CharSequence{
        return mFragmentTitleList[position]
    }

}