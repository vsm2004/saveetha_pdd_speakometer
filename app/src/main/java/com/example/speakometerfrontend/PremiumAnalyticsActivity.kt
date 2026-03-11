package com.example.speakometerfrontend

import android.content.Intent
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.Environment
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import java.io.File
import java.io.FileOutputStream
import androidx.core.graphics.toColorInt

class PremiumAnalyticsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_premium_analytics)

        // 1. Initialize Views
        val btnBackToHome = findViewById<AppCompatButton>(R.id.btnBackToHome)
        val llSquareCards = findViewById<LinearLayout>(R.id.llSquareCards)
        val cardGeneratePdf = llSquareCards.getChildAt(0)
        val cardAchievements = llSquareCards.getChildAt(1)
        val ivChartIcon = findViewById<ImageView>(R.id.ivChartIcon)

        // 2. Navigation: Back to Home
        btnBackToHome.setOnClickListener {
            val intent = Intent(this, HomePageActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        // 3. Feature: Generate PDF (Dynamic Multi-page Report)
        cardGeneratePdf.setOnClickListener {
            Toast.makeText(this, "Generating your detailed speech report...", Toast.LENGTH_SHORT).show()
            generateSpeechReport()
        }

        // 4. Feature: View Achievements (Passing actual data)
        cardAchievements.setOnClickListener {
            val intent = Intent(this, PremiumViewAchievementsActivity::class.java)
            intent.putExtra("IMPROVEMENT_PERCENT", "12%")
            intent.putExtra("FILLER_COUNT", "08")
            intent.putExtra("PACE_SCORE", "85")
            startActivity(intent)
        }

        // 5. Interactive UI Element
        ivChartIcon.setOnClickListener {
            Toast.makeText(this, "Weekly trend: Up by 12% this week!", Toast.LENGTH_LONG).show()
        }
    }

    private fun generateSpeechReport() {
        val pdfDocument = PdfDocument()

        // Setup Paints for drawing
        val titlePaint = Paint().apply {
            color = "#1B1736".toColorInt()
            textSize = 24f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val subTitlePaint = Paint().apply {
            color = "#8A4DFF".toColorInt()
            textSize = 18f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val bodyPaint = Paint().apply {
            color = Color.BLACK
            textSize = 14f
        }
        val accentPaint = Paint().apply {
            color = "#06B6D4".toColorInt()
        }

        // --- PAGE 1: SESSION PERFORMANCE ---
        val pageInfo1 = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 Size
        val page1 = pdfDocument.startPage(pageInfo1)
        val canvas1 = page1.canvas

        canvas1.drawText("Speakometer: Session Performance Report", 50f, 60f, titlePaint)
        canvas1.drawText("Overview of your latest speech metrics", 50f, 90f, bodyPaint)

        // Draw a simple bar chart visual
        accentPaint.color = "#06B6D4".toColorInt()
        canvas1.drawRect(50f, 150f, 150f, 400f, accentPaint) // Bar 1
        canvas1.drawText("Pace: 85", 60f, 420f, bodyPaint)

        accentPaint.color = "#8A4DFF".toColorInt()
        canvas1.drawRect(180f, 250f, 280f, 400f, accentPaint) // Bar 2
        canvas1.drawText("Confidence: High", 170f, 420f, bodyPaint)

        pdfDocument.finishPage(page1)

        // --- PAGE 2: COMPARISON & MAGNITUDE ---
        val pageInfo2 = PdfDocument.PageInfo.Builder(595, 842, 2).create()
        val page2 = pdfDocument.startPage(pageInfo2)
        val canvas2 = page2.canvas

        canvas2.drawText("Comparison & Improvement", 50f, 60f, subTitlePaint)
        canvas2.drawText("Magnitude of change vs. last session:", 50f, 100f, bodyPaint)

        // Positive Improvement Logic
        bodyPaint.color = "#2E7D32".toColorInt() // Dark Green
        canvas2.drawText("Fillers: -15% (Significant Improvement)", 70f, 140f, bodyPaint)
        canvas2.drawText("Pauses: +10% (Better flow control)", 70f, 170f, bodyPaint)
        canvas2.drawText("Confidence: +12% (Steady Growth)", 70f, 200f, bodyPaint)

        bodyPaint.color = Color.BLACK
        pdfDocument.finishPage(page2)

        // --- PAGE 3: COACH'S NOTE & TIPS ---
        val pageInfo3 = PdfDocument.PageInfo.Builder(595, 842, 3).create()
        val page3 = pdfDocument.startPage(pageInfo3)
        val canvas3 = page3.canvas

        canvas3.drawText("Direct Note from your Coach", 50f, 60f, subTitlePaint)

        val coachNote = """
            Analysis: You are speaking clearly, but you tend to rush 
            when answering difficult questions. This is where your 
            fillers (08) appear.
            
            Improvement Tips:
            1. Breathe: Take a deep breath before starting a sentence.
            2. Pause: Use 2-second silences to emphasize points.
            3. Preparation: Review technical vocabulary daily.
            
            Conclusion: You have improved by 12% this week. 
            Keep up the celebration streak!
        """.trimIndent()

        var yPos = 120f
        for (line in coachNote.split("\n")) {
            canvas3.drawText(line.trim(), 50f, yPos, bodyPaint)
            yPos += 28f
        }

        pdfDocument.finishPage(page3)

        // --- SAVE TO STORAGE ---
        val fileName = "Speakometer_Report_${System.currentTimeMillis()}.pdf"
        val filePath = File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)

        try {
            pdfDocument.writeTo(FileOutputStream(filePath))
            Toast.makeText(this, "PDF Downloaded to Documents folder", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to generate PDF: ${e.message}", Toast.LENGTH_SHORT).show()
        } finally {
            pdfDocument.close()
        }
    }
}