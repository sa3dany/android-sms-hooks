package com.tanglycohort.smshooks

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.tanglycohort.smshooks.databinding.FragmentPermissionsDeniedBinding

class PermissionsDeniedFragment : Fragment() {
    private var _binding: FragmentPermissionsDeniedBinding? = null
    private val binding get() = _binding!!
    private var permissionsGranted = false
    private val registrationToOpenAppInfo =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { onAppInfoResult() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().also { activity ->
            activity.onBackPressedDispatcher.addCallback(this) {
                activity.finish()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPermissionsDeniedBinding.inflate(inflater, container, false)
        binding.apply {
            button.setOnClickListener { onButtonClick() }
            return root
        }
    }

    override fun onResume() {
        super.onResume()
        if (permissionsGranted) {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun onButtonClick() {
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.parse("package:${context?.packageName}")
            // addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            registrationToOpenAppInfo.launch(this)
        }
    }

    /**
     * After the user returns from the app info screen.
     * No useful result. Used as hook to check if user has granted permissions
     */
    private fun onAppInfoResult() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.RECEIVE_SMS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            permissionsGranted = true
        }
    }
}