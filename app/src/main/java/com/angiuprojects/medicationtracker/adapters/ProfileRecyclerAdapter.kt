package com.angiuprojects.medicationtracker.adapters

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.angiuprojects.medicationtracker.R
import com.angiuprojects.medicationtracker.entities.Profile
import com.angiuprojects.medicationtracker.ui.home.HomeFragment
import com.angiuprojects.medicationtracker.utilities.Constants
import com.angiuprojects.medicationtracker.utilities.ReadWriteJson
import com.google.gson.Gson

class ProfileRecyclerAdapter (private val dataSet : MutableList<Profile>,
                              private val context: Context,
                              private val parentFragment: HomeFragment)
    : RecyclerView.Adapter<ProfileRecyclerAdapter.MyViewHolder>() {
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var profileButton: ImageButton
        var profileName: TextView

        init {
            profileButton = view.findViewById(R.id.profile_button)
            profileName = view.findViewById(R.id.profile_name)
        }
    }

    private lateinit var dialog: Dialog

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.profile_view, parent, false)

        dialog = Dialog(context)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Log.i(Constants.logger, "Position = " + holder.adapterPosition + " - Name = " + dataSet[holder.adapterPosition].profileName)
        dataSet[holder.adapterPosition].pills.forEach {
            Log.i(Constants.logger, "Pill " + it.medicationName)
        }

        holder.profileButton.setOnClickListener{onClickShowProfile(dataSet[holder.adapterPosition])}
        holder.profileName.text = dataSet[holder.adapterPosition].profileName

    }

    fun onClickOpenPopUp() {
        Log.i(Constants.logger, "onCliclOperPopIp")
        dialog = Dialog(context)
        val inflater = context.getSystemService(AppCompatActivity.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popUpView: View = inflater.inflate(R.layout.new_profile_popup, null)
        dialog.setContentView(popUpView)

        popUpView.findViewById<Button>(R.id.add_button).setOnClickListener{onClickAddProfile(popUpView)}
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    private fun onClickAddProfile(popUpView: View) {

        dataSet.add(Profile(popUpView.findViewById<AutoCompleteTextView>(R.id.name_auto_complete).text.toString(), mutableListOf()))

        Constants.currentUser.profiles = dataSet

        ReadWriteJson.getInstance().write(context, false)

        notifyItemInserted(itemCount - 1)

        onClickShowProfile(dataSet[itemCount - 1])

        dialog.dismiss()
    }

    private fun onClickShowProfile(profile: Profile) {
        Log.i(Constants.logger, "onClickShowProfile")
        Constants.selectedProfile = profile
        parentFragment.setSelectedProfileInfo(profile)
    }

    override fun getItemCount() = dataSet.size
}