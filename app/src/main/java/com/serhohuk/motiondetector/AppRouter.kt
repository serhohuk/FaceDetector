package com.serhohuk.motiondetector

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.serhohuk.motiondetector.presentation.detection.camera.CameraDetectionFragment
import com.serhohuk.motiondetector.presentation.detection.gallery.GalleryDetectionFragment

class AppRouter {


    fun navigateToCameraRecognitionScreen(fm: FragmentManager) {
        replaceFragmentWithBackStack(fm, CameraDetectionFragment())
    }

    fun navigateToGalleryRecognitionScreen(fm: FragmentManager) {
        replaceFragmentWithBackStack(fm, GalleryDetectionFragment())
    }

    private fun replaceFragmentWithBackStack(fm: FragmentManager, fragment: Fragment) {
        fm.beginTransaction()
            .replace(R.id.container, fragment, fragment.javaClass.simpleName)
            .addToBackStack(fragment.javaClass.simpleName)
            .commit()
    }

    private fun replaceFragment(fm: FragmentManager, fragment: Fragment) {
        fm.beginTransaction()
            .replace(R.id.container, fragment, fragment.javaClass.simpleName)
            .commit()
    }

}