package com.example.autobancalculator

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.autobancalculator.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "ban_status")

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        CoroutineScope(Dispatchers.Main).launch {
            val isBanned = viewModel.isBanned(dataStore).first()
            if (isBanned) {
                showBanMessage()
            } else {
                setupCalculator()
            }
        }
    }

    private fun setupCalculator() {
        binding.calculateButton.setOnClickListener {
            try {
                val expression = binding.expressionEditText.text.toString()
                if (isSimpleCalculation(expression)) {
                    banUser()
                } else {
                    val result = evaluateExpression(expression)
                    binding.resultTextView.text = "Result: $result"
                }
            } catch (e: Exception) {
                binding.resultTextView.text = "Error"
            }
        }
    }

    private fun isSimpleCalculation(expression: String): Boolean {
        val simpleCalculations = listOf("1+1", "2+2", "3+3", "4+4", "5+5") // Extend as needed
        return simpleCalculations.any { expression.replace(" ", "").equals(it, ignoreCase = true) }
    }

    private fun evaluateExpression(expression: String): Double {
        // In a real app, use a proper expression evaluator (e.g., a library)
        // For simplicity, this example only handles basic addition and subtraction
        return try {
            val parts = expression.split("+", "-").map { it.trim().toDouble() }
            if (parts.size == 1) {
                parts[0]
            } else {
                if (expression.contains("+")) parts[0] + parts[1] else parts[0] - parts[1]
            }
        } catch (e: NumberFormatException) {
            throw IllegalArgumentException("Invalid expression")
        }
    }

    private fun banUser() {
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.setBanned(dataStore, true)
            CoroutineScope(Dispatchers.Main).launch {
                showBanMessage()
            }
        }
    }


    private fun showBanMessage() {
        binding.expressionEditText.isEnabled = false
        binding.calculateButton.isEnabled = false
        binding.resultTextView.text = "You have been permanently banned from using this calculator."
        Toast.makeText(this, "You are banned!", Toast.LENGTH_SHORT).show()
    }
}