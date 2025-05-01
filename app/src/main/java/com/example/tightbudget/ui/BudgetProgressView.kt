package com.example.tightbudget.ui

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.example.tightbudget.R
import kotlin.math.min

/**
 * Custom view for visualizing budget progress with min/max indicators
 */
class BudgetProgressView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // Budget data
    private var spent: Float = 0f
    private var maxBudget: Float = 1000f
    private var minBudget: Float = 0f

    // Drawing tools
    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.background_gray)
        style = Paint.Style.FILL
    }

    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val minBudgetPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.blue_light)
        style = Paint.Style.STROKE
        strokeWidth = 2f
        pathEffect = DashPathEffect(floatArrayOf(5f, 5f), 0f)
    }

    private val maxBudgetPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.red_light)
        style = Paint.Style.STROKE
        strokeWidth = 2f
        pathEffect = DashPathEffect(floatArrayOf(5f, 5f), 0f)
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.text_dark)
        textSize = 30f
        textAlign = Paint.Align.CENTER
    }

    private val smallTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.text_medium)
        textSize = 24f
        textAlign = Paint.Align.CENTER
    }

    // Drawing dimensions
    private val cornerRadius = 12f
    private val progressRect = RectF()
    private val backgroundRect = RectF()

    /**
     * Set budget data and redraw
     */
    fun setBudgetData(spent: Float, minBudget: Float, maxBudget: Float) {
        this.spent = spent
        this.minBudget = minBudget
        this.maxBudget = maxBudget.coerceAtLeast(minBudget + 1) // Ensure max > min

        // Set progress bar color based on status
        progressPaint.color = when {
            spent > maxBudget -> ContextCompat.getColor(context, R.color.red_light)
            spent < minBudget -> ContextCompat.getColor(context, R.color.orange)
            else -> ContextCompat.getColor(context, R.color.teal_light)
        }

        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = width.toFloat()
        val height = height.toFloat()
        val progressHeight = height * 0.35f

        // Draw background bar
        backgroundRect.set(0f, (height - progressHeight) / 2, width, (height + progressHeight) / 2)
        canvas.drawRoundRect(backgroundRect, cornerRadius, cornerRadius, backgroundPaint)

        // Calculate progress width (capped at max width)
        val progressWidth = (spent / maxBudget.coerceAtLeast(1f)).coerceIn(0f, 1f) * width

        // Draw progress bar
        if (progressWidth > 0) {
            progressRect.set(0f, (height - progressHeight) / 2, progressWidth, (height + progressHeight) / 2)
            canvas.drawRoundRect(progressRect, cornerRadius, cornerRadius, progressPaint)
        }

        // Draw minimum spending indicator if applicable
        if (minBudget > 0) {
            val minX = (minBudget / maxBudget) * width
            canvas.drawLine(
                minX,
                (height - progressHeight) / 2 - 10f,
                minX,
                (height + progressHeight) / 2 + 10f,
                minBudgetPaint
            )

            canvas.drawText(
                "Min",
                minX,
                (height - progressHeight) / 2 - 20f,
                smallTextPaint
            )
        }

        // Draw maximum budget indicator
        canvas.drawLine(
            width,
            (height - progressHeight) / 2 - 10f,
            width,
            (height + progressHeight) / 2 + 10f,
            maxBudgetPaint
        )

        canvas.drawText(
            "Max",
            width,
            (height - progressHeight) / 2 - 20f,
            smallTextPaint
        )

        // Draw spending amount text
        val spendingText = "R${String.format("%,.0f", spent)}"

        // Position text either within the progress bar or after it, depending on width
        val textX = if (progressWidth > width * 0.3f) {
            min(progressWidth - 10f, width / 2)
        } else {
            progressWidth + 10f
        }

        // Use white text color if text is within a significant portion of the progress bar
        if (progressWidth > width * 0.3f && textX < progressWidth - 20f) {
            textPaint.color = Color.WHITE
        } else {
            textPaint.color = ContextCompat.getColor(context, R.color.text_dark)
        }

        canvas.drawText(
            spendingText,
            textX,
            height / 2 + textPaint.textSize / 3,
            textPaint
        )
    }
}