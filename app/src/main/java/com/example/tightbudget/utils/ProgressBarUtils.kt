package com.example.tightbudget.utils

import android.content.Context
import android.graphics.drawable.ClipDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.view.Gravity
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import com.example.tightbudget.R
import com.example.tightbudget.data.Category

/**
 * Utility class for creating and customizing progress bars
 */
object ProgressBarUtils {

    /**
     * Apply a category-colored progress bar style
     */
    fun applyCategoryProgressBar(progressBar: ProgressBar, context: Context, category: Category) {
        val color = DrawableUtils.getCategoryColor(context, category)
        applyColoredProgressBar(progressBar, context, color)
    }

    /**
     * Apply a status-colored progress bar style based on budget usage percentage
     */
    fun applyBudgetStatusProgressBar(progressBar: ProgressBar, context: Context, spent: Float, budget: Float) {
        val color = DrawableUtils.getBudgetStatusColor(context, spent, budget)
        applyColoredProgressBar(progressBar, context, color)
    }

    /**
     * Creates a custom progress bar drawable with the specified color
     */
    fun applyColoredProgressBar(progressBar: ProgressBar, context: Context, progressColor: Int) {
        // Background track drawable
        val backgroundDrawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(ContextCompat.getColor(context, R.color.background_gray))
            cornerRadius = context.resources.displayMetrics.density * 3 // 3dp corner radius
        }

        // Progress indicator drawable
        val progressDrawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(progressColor)
            cornerRadius = context.resources.displayMetrics.density * 3 // 3dp corner radius
        }

        // Create a clip drawable for the progress
        val clipDrawable = ClipDrawable(
            progressDrawable,
            Gravity.START,
            ClipDrawable.HORIZONTAL
        )

        // Create layer drawable with background and progress
        val layers = arrayOf<Drawable>(backgroundDrawable, clipDrawable)
        val layerDrawable = LayerDrawable(layers)

        // Set ids for the layers
        layerDrawable.setId(0, android.R.id.background)
        layerDrawable.setId(1, android.R.id.progress)

        // Apply the custom drawable to the progress bar
        progressBar.progressDrawable = layerDrawable
    }
}