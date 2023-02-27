package com.serhohuk.facedetector.di

import android.content.Context
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.serhohuk.facedetector.AppRouter
import com.serhohuk.facedetector.system.PreferencesManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun providesFaceDetectorOptions(): FaceDetectorOptions {
        return FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .enableTracking()
            .build()
    }

    @Provides
    @Singleton
    fun providesFaceDetector(highAccuracyOpts: FaceDetectorOptions): FaceDetector {
        return FaceDetection.getClient(highAccuracyOpts)
    }

    @Provides
    @Singleton
    fun providesAppRouter() : AppRouter {
        return AppRouter()
    }

    @Provides
    @Singleton
    fun providesPreferencesManager(@ApplicationContext context: Context) : PreferencesManager {
        return PreferencesManager(context)
    }

}