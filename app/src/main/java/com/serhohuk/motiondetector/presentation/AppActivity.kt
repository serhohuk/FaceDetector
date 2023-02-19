package com.serhohuk.motiondetector.presentation

import android.os.Bundle
import android.view.KeyEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.serhohuk.motiondetector.R
import com.serhohuk.motiondetector.isFragmentExistsAndVisible
import com.serhohuk.motiondetector.presentation.detection.camera.CameraDetectionFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AppActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.app_activity)
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        when(event.keyCode) {
            KeyEvent.KEYCODE_VOLUME_DOWN -> {
                if (event.action == KeyEvent.ACTION_DOWN) {
                    if (isFragmentExistsAndVisible(CameraDetectionFragment::class.java.simpleName)) {
                        //TODO do notifier for handle volume down click
                        Toast.makeText(this, "action down", Toast.LENGTH_SHORT).show()
                        return true
                    }
                }
                return super.dispatchKeyEvent(event)
            }
        }
        return super.dispatchKeyEvent(event)
    }
}