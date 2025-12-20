package com.example.healthproject.ui.superviseur.materiel

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.healthproject.R
import com.example.healthproject.data.model.AffectationMateriel
import com.example.healthproject.viewmodel.SuperviseurViewModel

class MaterielAdapter(
    private val viewModel: SuperviseurViewModel
) : RecyclerView.Adapter<MaterielAdapter.ViewHolder>() {

    private val items = mutableListOf<AffectationMateriel>()

    fun submitList(list: List<AffectationMateriel>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtNom: TextView = view.findViewById(R.id.txtNomMateriel)
        val txtQuantiteAvant: TextView = view.findViewById(R.id.txtQuantiteAvant)
        val edtQuantiteApres: EditText = view.findViewById(R.id.edtQuantiteApres)
        val edtEtat: EditText = view.findViewById(R.id.edtEtat)
        val btnSave: Button = view.findViewById(R.id.btnSave)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_materiel, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mat = items[position]

        holder.txtNom.text = mat.nomMateriel ?: "N/A"
        holder.txtQuantiteAvant.text = "Quantité affectée: ${mat.quantiteAffectee ?: 0}"
        holder.edtQuantiteApres.setText(mat.quantiteApres?.toString() ?: "")
        holder.edtEtat.setText(mat.etatApres ?: "")

        holder.btnSave.isEnabled = mat.quantiteApres == null

        holder.btnSave.setOnClickListener {
            val etat = holder.edtEtat.text.toString()
            val quantite = holder.edtQuantiteApres.text.toString().toIntOrNull() ?: 0

            mat.id?.let { affectId ->
                viewModel.updateEtatMateriel(mat, etat, quantite) {
                    holder.btnSave.isEnabled = false
                }
            }
        }
    }
}
