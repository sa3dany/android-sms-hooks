package com.tanglycohort.smshooks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.tanglycohort.smshooks.databinding.FragmentPermissionsEduBinding

class PermissionsEduFragment : Fragment() {
    private var _binding: FragmentPermissionsEduBinding? = null
    private val binding get() = _binding!!

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
        _binding = FragmentPermissionsEduBinding.inflate(inflater, container, false)
        binding.apply {
            tryAgainButton.setOnClickListener { onButtonClick() }
            return root
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun onButtonClick() {
        findNavController().popBackStack(R.id.homeFragment, false)
    }
}