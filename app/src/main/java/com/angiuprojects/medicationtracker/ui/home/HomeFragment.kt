package com.angiuprojects.medicationtracker.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.angiuprojects.medicationtracker.adapters.MedicationRecyclerAdapter
import com.angiuprojects.medicationtracker.adapters.ProfileRecyclerAdapter
import com.angiuprojects.medicationtracker.databinding.FragmentHomeBinding
import com.angiuprojects.medicationtracker.entities.Profile
import com.angiuprojects.medicationtracker.utilities.Constants

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        container?.removeAllViews()
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setRecyclerAdapter()

        return root
    }

    fun setRecyclerAdapter() {

        Constants.selectedProfile?.let { setSelectedProfileInfo(it) }

        val adapter = ProfileRecyclerAdapter(Constants.currentUser.profiles, requireContext(), this)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerView.layoutManager = layoutManager

        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.adapter = adapter

        binding.profileButton.setOnClickListener { adapter.onClickOpenPopUp() }
    }

    fun setMedicationsRecyclerAdapter(profile: Profile) {
        val adapter = MedicationRecyclerAdapter(profile.pills, requireContext())
        val recyclerView = binding.medicationRecyclerView
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = layoutManager

        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = adapter
    }

    fun setSelectedProfileInfo(profile: Profile) {
        Log.i(Constants.logger, "Profile name = " + profile.profileName)
        binding.profileName.text = profile.profileName
        setMedicationsRecyclerAdapter(profile)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}