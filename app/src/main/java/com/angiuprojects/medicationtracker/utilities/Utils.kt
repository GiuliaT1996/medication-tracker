package com.angiuprojects.medicationtracker.utilities

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.angiuprojects.medicationtracker.R
import com.angiuprojects.medicationtracker.adapters.MedicationRecyclerAdapter
import com.angiuprojects.medicationtracker.entities.Medication
import com.angiuprojects.medicationtracker.ui.home.HomeFragment
import com.google.android.material.snackbar.Snackbar
import java.time.LocalDate

class Utils {

    companion object{

        private lateinit var daysList: MutableList<Boolean>

        fun checkIfUnSelectedDaysInPeriod(remainingPills: Int, pillsADay: Int, medication: Medication): Int {

            var i = 0
            val netDays: Int = remainingPills / pillsADay
            var grossDays = 0
            while (i < netDays + grossDays) {
                if(!medication.daysList[(LocalDate.now().dayOfWeek.value - 1 + i) % 7]) grossDays++
                i++
            }
            return remainingPills / pillsADay + grossDays
        }

        fun openMedicationPopUp(context: Context, homeFragment: HomeFragment?,
                                fragmentTransaction: FragmentTransaction?,
                                medication: Medication?,
                                recyclerAdapter: MedicationRecyclerAdapter?,
                                position: Int?) {
            val dialog = Dialog(context)

            if(!this::daysList.isInitialized || medication == null) daysList = mutableListOf(true, true, true, true, true, true, true)

            val inflater = context.getSystemService(AppCompatActivity.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val popUpView: View = inflater.inflate(R.layout.new_medication_popup, null)
            dialog.setContentView(popUpView)

            handleAlreadySelectedDays(popUpView, medication)
            popUpView.findViewById<ImageButton>(R.id.switch_days).setOnClickListener { daysList = openDaysSelectionPopUp(context, medication, popUpView) }

            if(medication != null) setInfoFromMedication(medication, popUpView)

            popUpView.findViewById<Button>(R.id.add_button).setOnClickListener {
                if(medication != null && position != null) recyclerAdapter?.onClickEdit(popUpView, position, dialog)
                else onClickAddMedicationToUser(popUpView, context, homeFragment, fragmentTransaction, dialog)
            }

            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()
        }

        private fun setInfoFromMedication(m: Medication, popUpView: View) {
            popUpView.findViewById<AutoCompleteTextView>(R.id.remaining_pills_auto_complete).setText(m.remainingPills.toString())
            popUpView.findViewById<AutoCompleteTextView>(R.id.pills_a_day_auto_complete).setText(m.pillsADay.toString())
            popUpView.findViewById<AutoCompleteTextView>(R.id.total_pills_auto_complete).setText(m.totalPills.toString())
            popUpView.findViewById<AutoCompleteTextView>(R.id.name_auto_complete).setText(m.medicationName)

            popUpView.findViewById<Button>(R.id.add_button).text = "Modifica"
        }

        private fun onClickAddMedicationToUser(popUpView: View,
                                               context: Context,
                                               homeFragment: HomeFragment?,
                                               fragmentTransaction: FragmentTransaction?,
                                               dialog: Dialog) {

            val medication = createMedicationFromPopUp(popUpView) ?: return

            Constants.currentUser.profiles.forEach{
                if(it.profileName == Constants.selectedProfile?.profileName) it.pills.add(medication)
            }

            ReadWriteJson.getInstance().write(context, false)

            if(homeFragment != null && fragmentTransaction != null) {
                Log.i(Constants.logger, "NOT NULL")
                if(!homeFragment.isAdded) {
                    fragmentTransaction.replace(R.id.nav_host_fragment_content_main, homeFragment).commitNow()
                    Log.i(Constants.logger, "IS ADDED")
                }
                else {
                    homeFragment.setRecyclerAdapter()
                    Log.i(Constants.logger, "NOT ADDED")
                }

                Constants.selectedProfile?.let { homeFragment.setMedicationsRecyclerAdapter(it)
                    Log.i(Constants.logger, "MEDICATION RECYCLER ADAPTER")}
            }

            dialog.dismiss()
        }

        fun checkSelectedDay(days: Long, medication: Medication) {

            var dayToConsider = 0
            if(LocalDate.now().dayOfWeek.value - 1 - days < 1)
                dayToConsider = ((LocalDate.now().dayOfWeek.value - 1 - days) % 7).toInt()

            if(medication.daysList[dayToConsider])
                medication.remainingPills = (medication.remainingPills - medication.pillsADay)
        }

        fun createMedicationFromPopUp(popUpView: View) : Medication? {
            val medicationName = popUpView.findViewById<AutoCompleteTextView>(R.id.name_auto_complete)
            val totalPills = popUpView.findViewById<AutoCompleteTextView>(R.id.total_pills_auto_complete)
            val pillsADay = popUpView.findViewById<AutoCompleteTextView>(R.id.pills_a_day_auto_complete)
            val remainingPills = popUpView.findViewById<AutoCompleteTextView>(R.id.remaining_pills_auto_complete)

            try {
                if(medicationName.text.toString() == ""
                    || totalPills.text.toString() == ""
                    || totalPills.text.toString().toInt() == 0
                    || pillsADay.text.toString() == ""
                    || pillsADay.text.toString().toInt() == 0
                    || remainingPills.text.toString() == ""
                    || remainingPills.text.toString().toInt() == 0
                ) {
                    Snackbar.make(popUpView, "Inserire tutte le informazioni richieste!", Snackbar.LENGTH_LONG)
                        .setAction("Azione", null).show()
                    return null
                }
            } catch (e: Exception) {
                Snackbar.make(popUpView, "Alcune informazioni non sono corrette!", Snackbar.LENGTH_LONG)
                    .setAction("Azione", null).show()
                return null
            }

            if(!this::daysList.isInitialized) daysList = mutableListOf(true, true, true, true, true, true, true)

            return Medication(medicationName.text.toString(),
                totalPills.text.toString().toInt(),
                pillsADay.text.toString().toInt(),
                remainingPills.text.toString().toInt(),
                daysList
            )
        }

        //pass medication only if updating
        private fun openDaysSelectionPopUp(context: Context, medication: Medication?, popUpView: View) : MutableList<Boolean> {
           if(!this::daysList.isInitialized) daysList = mutableListOf(true, true, true, true, true, true, true)

            val dialog = Dialog(context)
            val inflater = context.getSystemService(AppCompatActivity.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val newPopUpView: View = inflater.inflate(R.layout.days_selection_popup, null)
            dialog.setContentView(newPopUpView)

            setPreselectedDays(medication, newPopUpView)

            newPopUpView.findViewById<Button>(R.id.ok_button).setOnClickListener {
                onClickSetDays(dialog, newPopUpView, popUpView, medication) }

            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()

            return daysList

        }

        private fun setPreselectedDays(medication: Medication?, newPopUpView: View) {
            if(medication != null) {
                newPopUpView.findViewById<CheckBox>(R.id.checkbox_lun).isChecked = medication.daysList[0]
                newPopUpView.findViewById<CheckBox>(R.id.checkbox_mar).isChecked = medication.daysList[1]
                newPopUpView.findViewById<CheckBox>(R.id.checkbox_mer).isChecked = medication.daysList[2]
                newPopUpView.findViewById<CheckBox>(R.id.checkbox_gio).isChecked = medication.daysList[3]
                newPopUpView.findViewById<CheckBox>(R.id.checkbox_ven).isChecked = medication.daysList[4]
                newPopUpView.findViewById<CheckBox>(R.id.checkbox_sa).isChecked = medication.daysList[5]
                newPopUpView.findViewById<CheckBox>(R.id.checkbox_dom).isChecked = medication.daysList[6]
            }
        }

        private fun onClickSetDays(dialog: Dialog, newPopUpView: View, popUpView: View, m: Medication?)
        : MutableList<Boolean> {

            daysList[0] = newPopUpView.findViewById<CheckBox>(R.id.checkbox_lun).isChecked
            daysList[1] = newPopUpView.findViewById<CheckBox>(R.id.checkbox_mar).isChecked
            daysList[2] = newPopUpView.findViewById<CheckBox>(R.id.checkbox_mer).isChecked
            daysList[3] = newPopUpView.findViewById<CheckBox>(R.id.checkbox_gio).isChecked
            daysList[4] = newPopUpView.findViewById<CheckBox>(R.id.checkbox_ven).isChecked
            daysList[5] = newPopUpView.findViewById<CheckBox>(R.id.checkbox_sa).isChecked
            daysList[6] = newPopUpView.findViewById<CheckBox>(R.id.checkbox_dom).isChecked

            if(!daysList.contains(true)) daysList = mutableListOf(true, true, true, true, true, true, true)

            handleAlreadySelectedDays(popUpView, m)

            dialog.dismiss()
            return daysList
        }

        private fun handleAlreadySelectedDays(popUpView: View, m: Medication?) {
            val daysList = m?.daysList ?: daysList

            if(daysList.contains(false)) {
                val iterator = daysList.iterator()
                var selectedDays = ""
                for((index, value) in iterator.withIndex()){
                    if(value) {
                        when(index) {
                            0 -> selectedDays += "Lun, "
                            1 -> selectedDays += "Mar, "
                            2 -> selectedDays += "Mer, "
                            3 -> selectedDays += "Gio, "
                            4 -> selectedDays += "Ven, "
                            5 -> selectedDays += "Sab, "
                            6 -> selectedDays += "Dom, "
                        }
                    }
                }
                val textView = popUpView.findViewById<TextView>(R.id.selected_days)

                Log.i(Constants.logger, "SELECTED DAYS: $selectedDays")

                selectedDays = if(selectedDays != "") selectedDays.trim().dropLast(1)
                               else textView.text.toString()

                Log.i(Constants.logger, "SELECTED DAYS 2: $selectedDays")

                textView.text = selectedDays
            }
        }
    }
}