package com.example.to_dolist

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class ExitDialog : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builder = AlertDialog.Builder(requireActivity())

        builder.setTitle("Exit")
            .setMessage("Any unsaved changes will be lost. Are you sure you want to exit?")
            .setPositiveButton("Yes") { dialog, which ->
                activity?.finish()
            }.setNegativeButton("No") { dialog, which ->
                dialog.cancel()
            }
        return builder.create()
    }
}