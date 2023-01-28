package com.angiuprojects.medicationtracker.ui.tobuy

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.angiuprojects.medicationtracker.adapters.ProfileRecyclerAdapter
import com.angiuprojects.medicationtracker.adapters.ToBuyRecyclerAdapter
import com.angiuprojects.medicationtracker.databinding.FragmentTobuyBinding
import com.angiuprojects.medicationtracker.entities.Medication
import com.angiuprojects.medicationtracker.utilities.Constants
import com.angiuprojects.medicationtracker.utilities.Utils
import java.time.Duration
import java.time.LocalDate

class ToBuyFragment : Fragment() {

    private var _binding: FragmentTobuyBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        container?.removeAllViews()
        _binding = FragmentTobuyBinding.inflate(inflater, container, false)
        val root: View = binding.root

        Constants.selectedProfile = null

        setRecyclerAdapter()

        return root
    }

    private fun setRecyclerAdapter() {

        var days = Duration.between(LocalDate.now().atStartOfDay(), Constants.currentUser.lastUpdate.atStartOfDay()).toDays()

        val medicationsToBuy: MutableMap<String, Medication> = mutableMapOf()

        Constants.currentUser.profiles.forEach { profile ->
            var i = 0
            profile.pills.forEach { medication ->
                while(days > 0) {
                    Utils.checkSelectedDay(days, medication)
                    days--
                }

                if(Utils.checkIfUnSelectedDaysInPeriod(medication.remainingPills, medication.pillsADay, medication)
                    <=  Constants.currentUser.settings.daysInAdvance) {
                    medicationsToBuy["${profile.profileName}-$i"] = medication
                    i++
                }
            }
        }

        val adapter = ToBuyRecyclerAdapter(medicationsToBuy, requireContext())
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(requireContext())
        binding.toBuyRecyclerView.layoutManager = layoutManager

        binding.toBuyRecyclerView.setHasFixedSize(true)
        binding.toBuyRecyclerView.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}