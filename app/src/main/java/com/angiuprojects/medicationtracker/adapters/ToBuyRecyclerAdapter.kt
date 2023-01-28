package com.angiuprojects.medicationtracker.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.angiuprojects.medicationtracker.R
import com.angiuprojects.medicationtracker.entities.Medication
import com.angiuprojects.medicationtracker.utilities.Constants
import com.angiuprojects.medicationtracker.utilities.ReadWriteJson
import com.angiuprojects.medicationtracker.utilities.Utils
import kotlin.math.floor

class ToBuyRecyclerAdapter (private val dataSet : MutableMap<String, Medication>, private val context: Context) : RecyclerView.Adapter<ToBuyRecyclerAdapter.MyViewHolder>() {
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var medicationName: TextView
        var profileName: TextView
        var remainingDays: TextView
        var buyButton: ImageButton

        init {
            medicationName = view.findViewById(R.id.medication_name)
            profileName = view.findViewById(R.id.profile_name)
            remainingDays = view.findViewById(R.id.remaining_days)
            buyButton = view.findViewById(R.id.check)
        }
    }

    private lateinit var profileNames: MutableList<String>
    private lateinit var medications: MutableList<Medication>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.to_buy_view, parent, false)

        profileNames = dataSet.keys.toMutableList()
        medications = dataSet.values.toMutableList()

        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.medicationName.text = medications[holder.adapterPosition].medicationName
        holder.profileName.text = profileNames[holder.adapterPosition].split("-")[0]
        holder.remainingDays.text = Utils.checkIfUnSelectedDaysInPeriod(medications[holder.adapterPosition].remainingPills, medications[holder.adapterPosition].pillsADay, medications[holder.adapterPosition]).toString()

        holder.buyButton.setOnClickListener { onCheckUpdateDays(holder)}
    }

    private fun onCheckUpdateDays(holder: MyViewHolder) {

        medications[holder.adapterPosition].remainingPills =
            medications[holder.adapterPosition].remainingPills + medications[holder.adapterPosition].totalPills
        holder.remainingDays.text = floor((medications[holder.adapterPosition].remainingPills / medications[holder.adapterPosition].pillsADay).toDouble())
            .toInt().toString()
        ReadWriteJson.getInstance().write(context, false)
        holder.buyButton.background = ContextCompat.getDrawable(context, R.drawable.icons8_done_100)
        holder.buyButton.isClickable = false

        dataSet.remove(profileNames[holder.adapterPosition])
        profileNames.removeAt(holder.adapterPosition)
        medications.removeAt(holder.adapterPosition)
        notifyItemRemoved(holder.adapterPosition)
    }

    override fun getItemCount() = dataSet.size

}