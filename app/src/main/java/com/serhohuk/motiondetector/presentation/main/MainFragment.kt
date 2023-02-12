package com.serhohuk.motiondetector.presentation.main

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.serhohuk.motiondetector.R
import com.serhohuk.motiondetector.adapter.NavigationAdapter
import com.serhohuk.motiondetector.databinding.FragmentMainBinding
import com.serhohuk.motiondetector.presentation.detection.DetectionStartFragment
import com.serhohuk.motiondetector.presentation.settings.SettingsFragment
import com.serhohuk.motiondetector.utils.viewBinding

class MainFragment : Fragment(R.layout.fragment_main) {

    private val binding by viewBinding(FragmentMainBinding::bind)

    private lateinit var adapter: NavigationAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewPager()

        binding.bottomNavView.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.fragment_detection -> binding.viewPager.setCurrentItem(0, false)
                R.id.fragment_settings -> binding.viewPager.setCurrentItem(1, false)
            }
            true
        }
    }

    private fun initViewPager() {
        binding.viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.viewPager.offscreenPageLimit = 4
        adapter = NavigationAdapter(this).apply {
            addFragment(DetectionStartFragment())
            addFragment(SettingsFragment())
        }
        binding.viewPager.adapter = adapter
        binding.viewPager.isUserInputEnabled = false
    }
}