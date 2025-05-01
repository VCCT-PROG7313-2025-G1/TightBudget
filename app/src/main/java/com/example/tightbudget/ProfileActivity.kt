package com.example.tightbudget

import android.os.Bundle
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.example.tightbudget.databinding.ActivityProfileBinding
import com.example.tightbudget.utils.ChartUtils
import com.example.tightbudget.utils.ProgressBarUtils

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBackButton()
        setupLevelProgress()
        setupPointsChart()
    }

    /**
     * Closes this screen and returns to the previous one
     */
    private fun setupBackButton() {
        binding.backButton.setOnClickListener {
            onBackPressed()
        }
    }

    /**
     * Applies XP bar styling and sets dynamic text
     */
    private fun setupLevelProgress() {
        val currentPoints = 680.0
        val nextLevelThreshold = 1000.0

        // Styled progress bar
        ProgressBarUtils.applyBudgetStatusProgressBar(
            binding.levelProgressBar,
            this,
            currentPoints.toFloat(),
            nextLevelThreshold.toFloat()
        )

        binding.pointsText.text = "${currentPoints.toInt()} pts"
        binding.nextLevelText.text = "Next Level"

        val remaining = nextLevelThreshold - currentPoints
        binding.levelProgressBar.progress = ((currentPoints / nextLevelThreshold) * 100).toInt()

        binding.levelProgressBar.contentDescription =
            "Progress to next level: ${binding.levelProgressBar.progress}%"
    }

    /**
     * Displays a fake points chart using ChartUtils
     * TODO: Replace with real data
     */
    private fun setupPointsChart() {
        val container: FrameLayout = binding.pointsChartContainer
        val samplePoints = mapOf(
            "Mon" to 50f,
            "Tue" to 80f,
            "Wed" to 60f,
            "Thu" to 90f,
            "Fri" to 30f,
            "Sat" to 100f,
            "Sun" to 70f
        )

        container.removeAllViews()
        val lineChart = ChartUtils.EnhancedLineChartView(this, samplePoints, false)
        container.addView(
            lineChart,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
    }
}