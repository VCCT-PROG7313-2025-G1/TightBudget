package com.example.tightbudget.ui

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.tightbudget.R

class ConfirmDeleteDialogFragment(
    private val onConfirm: () -> Unit
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext(), R.style.TransparentDialogTheme)
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_confirm_delete, null)

        val cancelButton = view.findViewById<Button>(R.id.cancelButton)
        val confirmButton = view.findViewById<Button>(R.id.confirmDeleteButton)

        cancelButton.setOnClickListener { dismiss() }
        confirmButton.setOnClickListener {
            onConfirm()
            dismiss()
        }

        builder.setView(view)
        return builder.create()
    }
}
