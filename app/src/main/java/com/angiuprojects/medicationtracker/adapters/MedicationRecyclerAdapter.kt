package com.angiuprojects.medicationtracker.adapters

import android.app.Dialog
import android.content.Context
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.angiuprojects.medicationtracker.R
import com.angiuprojects.medicationtracker.entities.Medication
import com.angiuprojects.medicationtracker.utilities.ReadWriteJson
import com.angiuprojects.medicationtracker.utilities.Utils

class MedicationRecyclerAdapter (private val dataSet : MutableList<Medication>, private val context: Context) : RecyclerView.Adapter<MedicationRecyclerAdapter.MyViewHolder>() {
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var medicationName: TextView
        var days: TextView
        var editButton: ImageButton
        var deleteButton: ImageButton

        init {
            medicationName = view.findViewById(R.id.medication_name)
            days = view.findViewById(R.id.days)
            editButton = view.findViewById(R.id.edit)
            deleteButton = view.findViewById(R.id.delete)
        }
    }

    private lateinit var dialog: Dialog

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.medication_view, parent, false)

        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val m = dataSet[holder.adapterPosition]

        holder.medicationName.text = m.medicationName
        holder.days.text = buildString {
            append("Giorni rimanenti: ")
            append(Utils.checkIfUnSelectedDaysInPeriod(m.remainingPills, m.pillsADay, m).toString())
        }

        holder.editButton.setOnClickListener{ Utils.openMedicationPopUp(context,
                                                                        null,
                                                                        null,
                                                                        dataSet[holder.adapterPosition],
                                                                        this,
                                                                        holder.adapterPosition)}
        holder.deleteButton.setOnClickListener{ onClickOpenDeletePopUp(holder.adapterPosition) }
    }

    private fun onClickDelete(position: Int) {
        dataSet.removeAt(position)
        notifyItemRemoved(position)
        dialog.dismiss()

        ReadWriteJson.getInstance().write(context, false)
    }

    private fun onClickOpenDeletePopUp(position: Int) {
        dialog = Dialog(context)
        val inflater = context.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popUpView: View = inflater.inflate(R.layout.warning_popup, null)
        dialog.setContentView(popUpView)

        popUpView.findViewById<Button>(R.id.si).setOnClickListener { onClickDelete(position) }
        popUpView.findViewById<Button>(R.id.no).setOnClickListener { dialog.dismiss() }

        popUpView.findViewById<TextView>(R.id.text).text = buildString {
            append("Eliminare ")
            append(dataSet[position].medicationName + "?")
        }

        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    fun onClickEdit(popUpView: View, position: Int, dialog: Dialog) {

        dataSet[position] = Utils.createMedicationFromPopUp(popUpView) ?: return

        notifyItemChanged(position)

        ReadWriteJson.getInstance().write(context, false)

        dialog.dismiss()
    }

    override fun getItemCount() = dataSet.size
}