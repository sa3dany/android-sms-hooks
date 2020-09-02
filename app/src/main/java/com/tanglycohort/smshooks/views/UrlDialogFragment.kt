package com.tanglycohort.smshooks.views

import android.app.Dialog
import android.content.DialogInterface
import android.content.DialogInterface.BUTTON_POSITIVE
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.textfield.TextInputLayout
import com.tanglycohort.smshooks.R
import okhttp3.HttpUrl.Companion.toHttpUrl

class UrlDialogFragment() : DialogFragment() {
    private val args: UrlDialogFragmentArgs by navArgs()
    private lateinit var editTextLayout: TextInputLayout

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder: AlertDialog.Builder

        requireActivity().apply {
            layoutInflater.inflate(R.layout.dialog_edit_text, null).also { view ->
                editTextLayout = view.findViewById(R.id.editTextLayout)
                builder = AlertDialog.Builder(this)
                    .setView(view)
                    .setPositiveButton("Save") { _, _ -> onPositiveButtonClick() }
                    .setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
            }
        }

        editTextLayout.apply {
            addOnEditTextAttachedListener { onEditTextAttached(it) }
            editTextLayout.prefixText = (if (args.forceHttps) "https://" else null)
        }

        return builder.create().also {
            it.setOnShowListener { dialog -> onDialogShow(dialog) }
        }
    }

    private fun onDialogShow(dialog: DialogInterface) {
        (dialog as AlertDialog).getButton(BUTTON_POSITIVE).apply {
            isEnabled = false
        }
    }

    private fun onEditTextAttached(textInputLayout: TextInputLayout) {
        textInputLayout.hint = getString(R.string.preference_webhook_url)
        textInputLayout.editText?.apply {
            if (args.forceHttps && args.initialValue.startsWith("https://")) {
                setText(args.initialValue.drop(8))
            } else {
                setText(args.initialValue)
            }
            inputType = InputType.TYPE_TEXT_VARIATION_URI
            doAfterTextChanged { editable -> onEditTextChanged(editable) }
        }
    }

    private fun onEditTextChanged(editable: Editable?) {
        if (isValidUrl(editable.toString())) {
            setError(null)
        } else {
            setError("Invalid URL")
        }
    }

    private fun onPositiveButtonClick() {
        findNavController().getBackStackEntry(R.id.settingsFragment).savedStateHandle.set(
            args.key, editTextLayout.editText?.text.toString()
        )
    }

    private fun setError(message: String?) {
        editTextLayout.error = message
        (dialog as AlertDialog).getButton(BUTTON_POSITIVE).apply {
            isEnabled = (message == null)
        }
    }

    private fun isValidUrl(url: String): Boolean {
        return try {
            true.also {
                if (args.forceHttps) "https://$url".toHttpUrl() else url.toHttpUrl()
            }
        } catch (e: IllegalArgumentException) {
            false
        }
    }
}