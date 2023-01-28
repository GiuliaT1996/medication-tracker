package com.angiuprojects.medicationtracker.ui.share

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.angiuprojects.medicationtracker.R
import com.angiuprojects.medicationtracker.databinding.FragmentShareBinding
import com.angiuprojects.medicationtracker.utilities.Constants

class ShareFragment : Fragment() {

    private var _binding: FragmentShareBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        container?.removeAllViews()

        Constants.selectedProfile = null

        _binding = FragmentShareBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.textShare.text = getString(R.string.work_in_progress)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}