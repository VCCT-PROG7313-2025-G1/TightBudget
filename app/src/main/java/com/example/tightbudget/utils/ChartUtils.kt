package com.example.tightbudget.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.view.View
import android.view.ViewGroup
import com.example.tightbudget.data.Category
import java.util.*

/**
 * Utility class for chart creation and manipulation
 */
object ChartUtils {

    /**
     * Create a simple donut chart view with category spending data
     */
    fun createDonutChartView(context: Context, categoryAmounts: Map<Category, Float>): DonutChartView {
        return DonutChartView(context, categoryAmounts)
    }

    /**
     * Custom view for displaying a donut chart
     */
    class DonutChartView(context: Context, private val data: Map<Category, Float>) : View(context) {
        private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        private val rect = RectF()
        private val centerText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            textSize = 40f
            textAlign = Paint.Align.CENTER
        }

        private var total: Float = data.values.sum()
        private var centerTextString = "R${String.format(Locale.getDefault(), "%.2f", total)}"

        init {
            // Set default values
            if (total == 0f) total = 1f
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)

            val width = width.toFloat()
            val height = height.toFloat()
            val radius = (Math.min(width, height) / 2 * 0.8).toFloat()
            val strokeWidth = radius * 0.2f

            // Set up the rectangle for the donut
            rect.set(
                width / 2 - radius,
                height / 2 - radius,
                width / 2 + radius,
                height / 2 + radius
            )

            // Draw the donut segments
            var startAngle = 0f
            data.forEach { (category, amount) ->
                val sweepAngle = 360f * (amount / total)

                paint.style = Paint.Style.STROKE
                paint.strokeWidth = strokeWidth
                paint.color = DrawableUtils.getCategoryColor(context, category)

                canvas.drawArc(rect, startAngle, sweepAngle, false, paint)
                startAngle += sweepAngle
            }

            // Draw center text
            canvas.drawText(
                centerTextString,
                width / 2,
                height / 2 + centerText.textSize / 3, // Adjust for vertical centering
                centerText
            )
        }
    }

    /**
     * Creates a list of default category spending data for preview/placeholder
     */
    fun getDefaultCategoryAmounts(context: Context): Map<Category, Float> {
        return mapOf(
            Category.HOUSING to 650.0f,
            Category.FOOD to 425.75f,
            Category.TRANSPORT to 232.50f,
            Category.ENTERTAINMENT to 205.02f
        )
    }

    /**
     * Helper function to create and add a donut chart to a container
     */
    fun addDonutChartToContainer(context: Context, containerId: Int, activity: androidx.appcompat.app.AppCompatActivity) {
        val container = activity.findViewById<android.widget.FrameLayout>(containerId)
        container.removeAllViews() // Clear any existing views

        val categoryAmounts = getDefaultCategoryAmounts(context)
        val donutChart = createDonutChartView(context, categoryAmounts)

        container.addView(donutChart)
    }

    fun displayDonutChart(
        context: Context,
        container: ViewGroup,
        categoryData: Map<String, Double>
    ) {
        // Convert string keys to valid Category enums
        val convertedData = categoryData.mapNotNull { (name, amount) ->
            val category = try {
                Category.valueOf(name.uppercase()) // Ensure enum name matches
            } catch (e: IllegalArgumentException) {
                null
            }
            category?.let { it to amount.toFloat() }
        }.toMap()

        val chartView = DonutChartView(context, convertedData)
        container.removeAllViews()
        container.addView(chartView)
    }
}