package com.example.healthproject.ui.pharmaassistant

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.healthproject.R
import com.example.healthproject.databinding.FragmentPharmaChatBinding
import com.example.healthproject.viewmodel.PharmaChatViewModel

class PharmaChatFragment : Fragment(R.layout.fragment_pharma_chat) {

    private var _binding: FragmentPharmaChatBinding? = null
    private val binding get() = _binding!!
    private val vm: PharmaChatViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPharmaChatBinding.bind(view)

        // Envoi du nom du médicament
        binding.btnSubmit.setOnClickListener {
            val medName = binding.etMedicament.text.toString().trim()
            if (medName.isNotEmpty()) {
                binding.tvResult.text = "Chargement..."
                vm.sendMedicationName(medName)
            } else {
                binding.tvResult.text = "Veuillez entrer le nom du médicament."
            }
        }

        // Observer la réponse du ViewModel
        vm.response.observe(viewLifecycleOwner) { result ->
            binding.tvResult.text = result
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
