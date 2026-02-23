package com.example.speakometerfrontend // Make sure this matches your package name

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.lang.ref.WeakReference

class LoadingToHomePageActivity : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar
    private lateinit var percentText: TextView
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading_to_homepage)

        // Find views from the layout
        progressBar = findViewById(R.id.loading_progress)
        percentText = findViewById(R.id.tv_loading_percent)

        // Start the loading animation
        startLoading()
    }

    private fun startLoading() {
        // Use a static nested class or a coroutine to avoid memory leaks.
        // For this example, we'll ensure the runnable doesn't leak the Activity.
        Thread(LoadingRunnable(this)).start()
    }

    // This static class prevents memory leaks by holding a WeakReference to the activity.
    private class LoadingRunnable(activity: LoadingToHomePageActivity) : Runnable {
        private val activityReference: WeakReference<LoadingToHomePageActivity> = WeakReference(activity)

        override fun run() {
            try {
                for (i in 0..100) {
                    // Simulate work being done
                    Thread.sleep(40) // 40ms * 100 = 4 seconds total

                    // Get the activity and handler from the weak reference
                    val activity = activityReference.get() ?: return

                    // Post the UI update to the main thread
                    activity.handler.post {
                        activity.progressBar.progress = i
                        val progressText = "$i%"
                        activity.percentText.text = progressText
                    }
                }

                // After the loop finishes, navigate to the home screen
                val activity = activityReference.get() ?: return
                navigateToHome(activity)

            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }

        private fun navigateToHome(activity: LoadingToHomePageActivity) {
            // IMPORTANT: Make sure you have created an activity called "HomeActivity"
            val intent = Intent(activity, HomePageActivity::class.java)

            // These flags clear the activity stack, so the user can't go back
            // to the loading screen or login screen by pressing the back button.
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            activity.startActivity(intent)
            activity.finish() // Close the loading activity
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Remove any pending messages from the handler to prevent leaks
        // if the user leaves the screen early.
        handler.removeCallbacksAndMessages(null)
    }
}
