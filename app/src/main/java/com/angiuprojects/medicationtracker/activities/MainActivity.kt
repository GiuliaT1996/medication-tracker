package com.angiuprojects.medicationtracker.activities

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.angiuprojects.medicationtracker.R
import com.angiuprojects.medicationtracker.databinding.ActivityMainBinding
import com.angiuprojects.medicationtracker.entities.Medication
import com.angiuprojects.medicationtracker.entities.Profile
import com.angiuprojects.medicationtracker.ui.home.HomeFragment
import com.angiuprojects.medicationtracker.utilities.Constants
import com.angiuprojects.medicationtracker.utilities.ReadWriteJson
import com.angiuprojects.medicationtracker.utilities.Utils
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import java.time.Duration
import java.time.LocalDate


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var dialog: Dialog

    private lateinit var homeFragment: HomeFragment
    private lateinit var fragmentManager: FragmentManager
    private lateinit var fragmentTransaction: FragmentTransaction

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dialog = Dialog(this)

        Constants.initializeSingleton()
        ReadWriteJson.initializeSingleton()
        Constants.currentUser = ReadWriteJson.getInstance().getUser(this, resetUserDTO = false)

        ReadWriteJson.getInstance().write(this, false)

        homeFragment = HomeFragment()
        fragmentManager = supportFragmentManager
        fragmentTransaction = fragmentManager.beginTransaction()

        handleLayout()

        generateWarnings()

        binding.appBarMain.toolbar.menu
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.action_settings -> {
                Log.i(Constants.logger, "Cliccate impostazioni")
                val i = Intent(this, SettingsActivity::class.java)
                startActivity(i)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun generateWarnings() {

        var days = Duration.between(LocalDate.now().atStartOfDay(), Constants.currentUser.lastUpdate.atStartOfDay()).toDays()

        Constants.currentUser.profiles.forEach { profile ->
            profile.pills.forEach { medication ->
                // per ogni giorno passato dall'ultimo aggiornamento, controllo se è uno dei giorni selezionati:
                // se sì scalo le pillole, altrimenti no
                while(days > 0) {
                    Utils.checkSelectedDay(days, medication)
                    days--
                }

                //controllo quanti giorni mancano al termine considerando anche che alcuni giorni non sono selezionati
                if(Utils.checkIfUnSelectedDaysInPeriod(medication.remainingPills, medication.pillsADay, medication)
                    <=  Constants.currentUser.settings.daysInAdvance)
                    showWarningPopUp(profile, medication)
            }
        }
    }

    private fun showWarningPopUp(profile: Profile, medication: Medication) {
        val dialog = Dialog(this)
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popUpView: View = inflater.inflate(R.layout.warning_popup, null)
        dialog.setContentView(popUpView)

        popUpView.findViewById<Button>(R.id.si).setOnClickListener { updateBoughtMedication(medication, dialog) }
        popUpView.findViewById<Button>(R.id.no).setOnClickListener { dialog.dismiss() }
        popUpView.findViewById<TextView>(R.id.text).text = buildString {
            append(medication.medicationName.uppercase())
            append(" di ")
            append(profile.profileName)
            append(" finirà tra ")
            append(medication.remainingPills / medication.pillsADay)
            append(" giorni. E' stata già comprata?")
        }

        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    private fun updateBoughtMedication(medication: Medication, dialog: Dialog) {
        medication.remainingPills = medication.remainingPills + medication.totalPills
        ReadWriteJson.getInstance().write(this, false)

        dialog.dismiss()
    }

    private fun handleLayout() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        binding.appBarMain.fab.setOnClickListener { view ->
            binding.appBarMain.fab.visibility = View.VISIBLE
            onClickAddMedication(view)
        }

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_share, R.id.nav_tobuy
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    private fun onClickAddMedication(view: View) {

        if(Constants.selectedProfile == null) {
            Snackbar.make(view, "Selezionare un profilo", Snackbar.LENGTH_LONG)
                .setAction("Azione", null).show()
            return
        }

        Utils.openMedicationPopUp(this, homeFragment, fragmentTransaction, null, null, null)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}