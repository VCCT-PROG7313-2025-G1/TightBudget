// ChartUtils.kt
package com.example.tightbudget.utils

import android.content.Context
import android.graphics.*
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import java.util.*
import kotlin.math.min

/**
 * Utility class for chart creation and manipulation
 */
object ChartUtils {

    /**
     * Create a simple donut chart view with category spending data
     */
    fun createDonutChartView(context: Context, categoryAmounts: Map<String, Float>): DonutChartView {
        return DonutChartView(context, categoryAmounts)
    }

    /**
     * Custom view for displaying a donut chart
     */
    class DonutChartView(context: Context, private val data: Map<String, Float>) : View(context) {
        private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        private val rect = RectF()
        private val centerText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            textSize = 54f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        private var total: Float = data.values.sum()
        private var centerTextString = "R${String.format(Locale.getDefault(), "%.2f", total)}"

        init {
            if (total == 0f) total = 1f
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)

            val width = width.toFloat()
            val height = height.toFloat()
            val radius = (min(width, height) / 2 * 0.8).toFloat()
            val strokeWidth = radius * 0.2f

            rect.set(
                width / 2 - radius,
                height / 2 - radius,
                width / 2 + radius,
                height / 2 + radius
            )

            var startAngle = 0f
            data.forEach { (categoryName, amount) ->
                val sweepAngle = 360f * (amount / total)

                paint.style = Paint.Style.STROKE
                paint.strokeWidth = strokeWidth
                paint.color = DrawableUtils.getCategoryColor(context, categoryName)

                canvas.drawArc(rect, startAngle, sweepAngle, false, paint)
                startAngle += sweepAngle
            }

            canvas.drawText(
                centerTextString,
                width / 2,
                height / 2 + centerText.textSize / 3f,
                centerText
            )
        }
    }

    /**
     * Creates a list of default category spending data for preview/placeholder
     */
    fun getDefaultCategoryAmounts(): Map<String, Float> {
        return mapOf(
            CategoryConstants.HOUSING to 650.0f,
            CategoryConstants.FOOD to 425.75f,
            CategoryConstants.TRANSPORT to 232.50f,
            CategoryConstants.ENTERTAINMENT to 205.02f
        )
    }

    /**
     * Helper function to create and add a donut chart to a container
     */
    fun addDonutChartToContainer(context: Context, containerId: Int, activity: AppCompatActivity) {
        val container = activity.findViewById<FrameLayout>(containerId)
        container.removeAllViews()

        val categoryAmounts = getDefaultCategoryAmounts()
        val donutChart = createDonutChartView(context, categoryAmounts)

        container.addView(donutChart)
    }

    /**
     * Adds a line chart to a specified container using provided labelled point data.
     */
    fun addLineChartToContainer(
        context: Context,
        container: ViewGroup,
        data: Map<String, Float>
    ) {
        val chart = LineChartView(context, data)
        container.removeAllViews()
        container.addView(chart)
    }

    /**
     * A custom view that draws a connected line chart from key-value data points.
     */
    class LineChartView(context: Context, private val data: Map<String, Float>) : View(context) {
        private val linePaint = Paint().apply {
            color = Color.parseColor("#66BB6A")
            strokeWidth = 6f
            style = Paint.Style.STROKE
            isAntiAlias = true
        }

        private val pointPaint = Paint().apply {
            color = Color.parseColor("#66BB6A")
            style = Paint.Style.FILL
            isAntiAlias = true
        }

        private val textPaint = Paint().apply {
            color = Color.DKGRAY
            textSize = 24f
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)

            if (data.isEmpty()) return

            val padding = 60f
            val chartWidth = width - 2 * padding
            val chartHeight = height - 2 * padding
            val entries = data.entries.toList()
            val maxY = (data.values.maxOrNull() ?: 1f).coerceAtLeast(1f)

            for (i in 0 until entries.size - 1) {
                val x1 = padding + (i * chartWidth / (entries.size - 1))
                val y1 = padding + chartHeight * (1 - (entries[i].value / maxY))
                val x2 = padding + ((i + 1) * chartWidth / (entries.size - 1))
                val y2 = padding + chartHeight * (1 - (entries[i + 1].value / maxY))

                canvas.drawLine(x1, y1, x2, y2, linePaint)
                canvas.drawCircle(x1, y1, 6f, pointPaint)
                canvas.drawText(entries[i].key, x1, height - 16f, textPaint)
            }

            val lastX = padding + ((entries.size - 1) * chartWidth / (entries.size - 1))
            val lastY = padding + chartHeight * (1 - (entries.last().value / maxY))
            canvas.drawCircle(lastX, lastY, 6f, pointPaint)
            canvas.drawText(entries.last().key, lastX, height - 16f, textPaint)
        }
    }
}
