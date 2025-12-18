package com.example.healthproject.ui.coordinateur.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.example.healthproject.data.model.Materiel
import com.example.healthproject.data.model.MaterielSelection
import com.example.healthproject.databinding.ItemMaterielSelectionBinding

class MaterielSelectionAdapter(
    private val materiels: List<Materiel>,
    private val selections: MutableList<MaterielSelection>,
    private val onRemoveClick: (position: Int) -> Unit
) : RecyclerView.Adapter<MaterielSelectionAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemMaterielSelectionBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMaterielSelectionBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val selection = selections[position]

        // Spinner avec tous les noms de matériels
        val adapter = ArrayAdapter(
            holder.itemView.context,
            android.R.layout.simple_spinner_item,
            materiels.map { it.nom }
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        holder.binding.spinnerMateriel.adapter = adapter

        // Si un materiel est déjà sélectionné, mettre le spinner à la bonne position
        val selectedIndex = materiels.indexOfFirst { it.id == selection.materielId }
        if (selectedIndex >= 0) {
            holder.binding.spinnerMateriel.setSelection(selectedIndex)
        }

        // Quand l'utilisateur choisit un matériel
        holder.binding.spinnerMateriel.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                val materiel = materiels[pos]
                selection.materielId = materiel.id
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                selection.materielId = null
            }
        }

        // Quantité editable
        // Quantité editable
        holder.binding.etQuantite.doAfterTextChanged {
            val value = it.toString().toIntOrNull() ?: 0
            // On récupère le materiel sélectionné dans le spinner
            val selectedPosition = holder.binding.spinnerMateriel.selectedItemPosition
            val materiel = materiels.getOrNull(selectedPosition)

            if (materiel != null) {
                if (value > materiel.quantiteInitiale) {
                    // Si la quantité dépasse le stock, on corrige automatiquement
                    holder.binding.etQuantite.setText(materiel.quantiteInitiale.toString())
                    selection.quantite = materiel.quantiteInitiale
                    holder.binding.etQuantite.setSelection(holder.binding.etQuantite.text.length)
                } else {
                    selection.quantite = value
                }
            } else {
                selection.quantite = value
            }
        }



        // Supprimer
        holder.binding.btnRemove.setOnClickListener {
            onRemoveClick(position)
        }
    }


    override fun getItemCount() = selections.size
}
