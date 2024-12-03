package fr.eseo.ld.android.xd.bde_cafet.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.eseo.ld.android.xd.bde_cafet.model.Command
import fr.eseo.ld.android.xd.bde_cafet.repositories.FirebaseRepository
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(private val repository: FirebaseRepository) :
    ViewModel() {
    private val _commandsOfDay = MutableLiveData<List<Command>>()

    private val _salesByCategory = MutableLiveData<Map<String, Map<String, Int>>>()
    val salesByCategory: LiveData<Map<String, Map<String, Int>>> get() = _salesByCategory

    private val _totalPriceOfDay = MutableLiveData<Double>()
    val totalPriceOfDay: LiveData<Double> get() = _totalPriceOfDay

    private val _totalCommandsOfDay = MutableLiveData<Int>()
    val totalCommandsOfDay: LiveData<Int> get() = _totalCommandsOfDay

    private val _totalCommandsOfMonth = MutableLiveData<Int>()
    val totalCommandsOfMonth: LiveData<Int> get() = _totalCommandsOfMonth

    private val _totalRevenueOfMonth = MutableLiveData<Double>()
    val totalRevenueOfMonth: LiveData<Double> get() = _totalRevenueOfMonth

    fun fetchCommandsByDayAndCategory(day: Int, month: Int, year: Int) {
        repository.getCommandsByDay(day, month, year,
            onSuccess = { commands ->
                _commandsOfDay.value = commands
                // Grouper les commandes par catégorie après récupération
                getCommandsByDayAndCategory(commands)
            },
            onFailure = { exception ->
                Log.e(
                    "CafetViewModel",
                    "Erreur lors de la récupération des commandes : ${exception.message}"
                )
                _commandsOfDay.value =
                    emptyList()
            }
        )
    }

    private fun getCommandsByDayAndCategory(commands: List<Command>) {
        val groupedByCategory = commands.groupBy { it.products.category }
        val result = mutableMapOf<String, MutableMap<String, Int>>()
        groupedByCategory.forEach { (category, commandList) ->
            val productCounts = mutableMapOf<String, Int>()
            commandList.forEach { command ->
                val productName = command.products.name
                productCounts[productName] = productCounts.getOrDefault(productName, 0) + 1
            }
            result[category] = productCounts
        }
        _salesByCategory.value = result
    }

    // Fonction pour récupérer et calculer le total des ventes pour une date spécifique
    fun fetchTotalPriceByDay(day: Int, month: Int, year: Int) {
        repository.getCommandsByDay(day, month, year,
            onSuccess = { commands ->
                val total = commands.sumOf { it.products.price }
                _totalPriceOfDay.value = total
            },
            onFailure = { exception ->
                _totalPriceOfDay.value = 0.0
                Log.e(
                    "CafetViewModel",
                    "Erreur lors de la récupération du prix total : ${exception.message}"
                )
            }
        )
    }

    // Fonction pour récupérer et calculer le total des ventes pour une date spécifique
    fun fetchTotalCommandsByDay(day: Int, month: Int, year: Int) {
        repository.getCommandsByDay(day, month, year,
            onSuccess = { commands ->
                _totalCommandsOfDay.value = commands.size
            },
            onFailure = { exception ->
                _totalCommandsOfDay.value = 0
                Log.e(
                    "CafetViewModel",
                    "Erreur lors de la récupération du prix total : ${exception.message}"
                )
            }
        )
    }

    fun fetchCommandsByMonth(month: Int, year: Int) {
        repository.getCommandsByMonth(month, year,
            onSuccess = { commands ->
                // Calculer le nombre de commandes
                val totalCommands = commands.size
                Log.d("CafetViewModel", "totalCommands: $totalCommands")

                // Calculer le revenu total du mois
                val totalRevenue = commands.sumOf { it.products.price }
                Log.d("CafetViewModel", "totalRevenue: $totalRevenue")

                // Mettre à jour les LiveData avec le résultat
                _totalCommandsOfMonth.value = totalCommands
                _totalRevenueOfMonth.value = totalRevenue
            },
            onFailure = { exception ->
                _totalCommandsOfMonth.value = 0
                _totalRevenueOfMonth.value = 0.0
                Log.e(
                    "CafetViewModel",
                    "Erreur lors de la récupération des commandes du mois : ${exception.message}"
                )
            }
        )
    }
}
