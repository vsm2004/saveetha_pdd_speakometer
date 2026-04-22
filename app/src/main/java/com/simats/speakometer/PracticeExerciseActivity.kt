package com.simats.speakometer

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout

/**
 * Slide #18: Personalized Practice Exercise.
 * Shows exercise prompt when not yet done, results when completed.
 */
class PracticeExerciseActivity : AppCompatActivity() {

    private lateinit var mistakeType: String
    private lateinit var accent: String

    // Prompt views
    private lateinit var cardPrompt: ConstraintLayout
    private lateinit var ivVisualizer: ImageView
    private lateinit var btnStart: AppCompatButton

    // Results views
    private lateinit var cardResults: ConstraintLayout
    private lateinit var tvLastScore: TextView
    private lateinit var tvResultDetail: TextView
    private lateinit var btnDoAgain: AppCompatButton
    private lateinit var btnViewFull: AppCompatButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_practice_exercise)

        mistakeType = intent.getStringExtra("MISTAKE_TYPE") ?: "Filler Words"
        val prefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        accent = prefs.getString("ACCENT_PREF", "Indian") ?: "Indian"

        // Init common views
        val btnBack = findViewById<ImageView>(R.id.btn_back_to_tips)
        val tvTitle = findViewById<TextView>(R.id.tv_exercise_title)
        val tvMeta = findViewById<TextView>(R.id.tv_exercise_meta)
        val tvPromptContent = findViewById<TextView>(R.id.tv_prompt_content)
        val tvTipText = findViewById<TextView>(R.id.tv_tip_text)

        // Init prompt / results view groups
        cardPrompt = findViewById(R.id.card_prompt)
        ivVisualizer = findViewById(R.id.iv_visualizer)
        btnStart = findViewById(R.id.btn_start_recording)
        cardResults = findViewById(R.id.card_results)
        tvLastScore = findViewById(R.id.tv_last_score_value)
        tvResultDetail = findViewById(R.id.tv_result_detail)
        btnDoAgain = findViewById(R.id.btn_do_again)
        btnViewFull = findViewById(R.id.btn_view_full_results)

        // Populate exercise content (accent-aware)
        setupExerciseContent(mistakeType, accent, tvTitle, tvMeta, tvPromptContent, tvTipText)

        btnBack.setOnClickListener { finish() }

        // Start Recording
        btnStart.setOnClickListener {
            val intent = Intent(this, RecordingAudioActivity::class.java)
            intent.putExtra("IS_PRACTICE_MODE", true)
            intent.putExtra("PRACTICE_TOPIC", mistakeType)
            startActivity(intent)
            Toast.makeText(this, getString(R.string.toast_starting_practice, mistakeType), Toast.LENGTH_SHORT).show()
        }

        // Do it Again — clears completion so prompt shows again
        btnDoAgain.setOnClickListener {
            prefs.edit()
                .remove("COMPLETED_PRACTICE_$mistakeType")
                .remove("PRACTICE_SCORE_$mistakeType")
                .apply()
            showPromptView()
            // Start recording immediately
            val intent = Intent(this, RecordingAudioActivity::class.java)
            intent.putExtra("IS_PRACTICE_MODE", true)
            intent.putExtra("PRACTICE_TOPIC", mistakeType)
            startActivity(intent)
        }

        // View Full Analysis — navigate to AnalysisResultsActivity if session ID stored
        btnViewFull.setOnClickListener {
            Toast.makeText(this, "Open History to view full session details", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh view each time we come back (e.g., after a recording completes)
        val prefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val isCompleted = prefs.getBoolean("COMPLETED_PRACTICE_$mistakeType", false)
        if (isCompleted) {
            val score = prefs.getInt("PRACTICE_SCORE_$mistakeType", -1)
            val fillerCount = prefs.getInt("PRACTICE_FILLER_$mistakeType", -1)
            val hesCount = prefs.getInt("PRACTICE_HES_$mistakeType", -1)
            val pace = prefs.getInt("PRACTICE_PACE_$mistakeType", -1)
            showResultsView(score, fillerCount, hesCount, pace)
        } else {
            showPromptView()
        }
    }

    private fun showPromptView() {
        cardPrompt.visibility = View.VISIBLE
        ivVisualizer.visibility = View.VISIBLE
        btnStart.visibility = View.VISIBLE
        cardResults.visibility = View.GONE
        btnDoAgain.visibility = View.GONE
        btnViewFull.visibility = View.GONE
    }

    private fun showResultsView(score: Int, fillerCount: Int, hesCount: Int, pace: Int) {
        cardPrompt.visibility = View.GONE
        ivVisualizer.visibility = View.GONE
        btnStart.visibility = View.GONE
        cardResults.visibility = View.VISIBLE
        btnDoAgain.visibility = View.VISIBLE
        btnViewFull.visibility = View.VISIBLE

        tvLastScore.text = if (score >= 0) score.toString() else "—"

        val details = buildString {
            if (fillerCount >= 0) append("🗣 Filler words: $fillerCount instances\n")
            if (hesCount >= 0) append("⏸ Hesitations: $hesCount instances\n")
            if (pace >= 0) append("⚡ Pace score: $pace / 100")
        }
        tvResultDetail.text = details.trim()
    }

    private fun setupExerciseContent(
        type: String, accent: String,
        title: TextView, meta: TextView, prompt: TextView, tip: TextView
    ) {
        when (type) {
            "Filler Words" -> when (accent) {
                "American" -> {
                    title.text = "Reduce Filler Words"
                    meta.text = "Focus: Clean, confident American delivery"
                    prompt.text = "Describe your ideal weekend in New York City without using 'um', 'uh', 'like', or 'you know'. Speak for 90 seconds."
                    tip.text = "American broadcasters use the 'pause and breathe' technique — replace every filler word with a 1-second silent pause instead."
                }
                "British" -> {
                    title.text = "Reduce Filler Words"
                    meta.text = "Focus: Precise and measured British expression"
                    prompt.text = "Describe your favourite British television programme without using 'sort of', 'kind of', or 'basically'. Speak for 90 seconds."
                    tip.text = "BBC presenters train by recording themselves and counting each filler. Aim to halve your count each week."
                }
                else -> {
                    title.text = getString(R.string.reduce_filler_words)
                    meta.text = getString(R.string.focus_filler)
                    prompt.text = getString(R.string.prompt_filler)
                    tip.text = getString(R.string.tip_filler)
                }
            }
            "Pace Variation" -> when (accent) {
                "American" -> {
                    title.text = "Pace Variation"
                    meta.text = "Focus: Dynamic American conversational rhythm"
                    prompt.text = "Give a 2-minute mock TED-style talk about a topic you're passionate about. Deliberately slow down for key sentences and speed up during transitions."
                    tip.text = "American speakers often vary pace dramatically — slow to 100 WPM for emphasis, speed up to 180 WPM during narratives."
                }
                "British" -> {
                    title.text = "Pace Variation"
                    meta.text = "Focus: Measured British rhetorical pacing"
                    prompt.text = "Deliver a 2-minute formal argument on a topic like education reform. Use deliberate pauses after key points as British parliamentary speakers do."
                    tip.text = "British formal speech uses 'pregnant pauses' — 2-3 seconds of silence after an important point signals authority."
                }
                else -> {
                    title.text = getString(R.string.title_pace)
                    meta.text = getString(R.string.focus_pace)
                    prompt.text = getString(R.string.prompt_pace)
                    tip.text = getString(R.string.tip_pace)
                }
            }
            "Hesitation" -> when (accent) {
                "American" -> {
                    title.text = "Eliminate Hesitation"
                    meta.text = "Focus: Confident, direct American assertiveness"
                    prompt.text = "Answer 'What's your biggest strength and why?' as if in a US tech company interview. Speak directly for 60 seconds without trailing off."
                    tip.text = "Prepare your first sentence in advance — a strong opening eliminates 80% of hesitation."
                }
                "British" -> {
                    title.text = "Eliminate Hesitation"
                    meta.text = "Focus: Composed, considered British delivery"
                    prompt.text = "Introduce yourself professionally in 60 seconds as you would at a British networking event. Project calm confidence without filler words or false starts."
                    tip.text = "A deliberate pause before speaking signals thoughtfulness in British culture — it is not a weakness."
                }
                else -> {
                    title.text = getString(R.string.title_hesitation)
                    meta.text = getString(R.string.focus_hesitation)
                    prompt.text = getString(R.string.prompt_hesitation)
                    tip.text = getString(R.string.tip_hesitation)
                }
            }
            "Art & Storytelling" -> when (accent) {
                "American" -> {
                    title.text = "Art & Storytelling"
                    meta.text = "Focus: American narrative storytelling style"
                    prompt.text = "Tell a 2-minute story set in an American context — a road trip, a county fair, or a small-town diner. Use vivid sensory details."
                    tip.text = "American storytelling: 'show, don't tell'. Say 'the steering wheel was ice under my fingers' not 'it was cold'."
                }
                "British" -> {
                    title.text = "Art & Storytelling"
                    meta.text = "Focus: British wit and understatement in narrative"
                    prompt.text = "Tell a 2-minute anecdote with dry British humour about something going charmingly wrong. Use understatement and irony."
                    tip.text = "British storytelling uses understatement — 'It was a bit of a disaster' for a major calamity creates instant wit."
                }
                else -> {
                    title.text = "Art & Storytelling"
                    meta.text = "Focus: Narrative flow and descriptive language"
                    prompt.text = "Tell a 2-minute original story about a person finding a mysterious key. Use vivid descriptions."
                    tip.text = "Use sensory details (sight, sound, smell) to make your story more immersive."
                }
            }
            "Presentation Skills" -> when (accent) {
                "American" -> {
                    title.text = "Presentation Skills"
                    meta.text = "Focus: American business presentation power"
                    prompt.text = "Pitch a startup idea in 3 minutes as if presenting to Silicon Valley investors. Open with a bold hook, state problem, solution, call to action."
                    tip.text = "American presentations: 'bottom line up front'. State your conclusion first, then support it."
                }
                "British" -> {
                    title.text = "Presentation Skills"
                    meta.text = "Focus: Structured British formal presentation"
                    prompt.text = "Present a 3-minute analysis of a current issue to a British board of directors. Use formal register and clear signposting language."
                    tip.text = "Signpost your speech: 'Firstly... Secondly... In conclusion...' — it signals intellectual rigour."
                }
                else -> {
                    title.text = "Presentation Skills"
                    meta.text = "Focus: Structured delivery and vocal projection"
                    prompt.text = "Present a 3-minute pitch for a new app idea. Focus on your opening and closing statements."
                    tip.text = "Stand tall and imagine you are speaking to the back of the room to improve projection."
                }
            }
        }
    }
}


