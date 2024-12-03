package fr.eseo.ld.android.xd.bde_cafet.ui.screens

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import fr.eseo.ld.android.xd.bde_cafet.R
import fr.eseo.ld.android.xd.bde_cafet.ui.navigation.BdeCafetScreens
import fr.eseo.ld.android.xd.bde_cafet.viewmodels.AuthenticationViewModel
import fr.eseo.ld.android.xd.bde_cafet.viewmodels.HistoryViewModel
import java.util.Calendar


@SuppressLint("DefaultLocale")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    navController: NavController,
    authenticationViewModel: AuthenticationViewModel
) {
    val viewModel: HistoryViewModel = hiltViewModel()
    val totalPriceOfDay by viewModel.totalPriceOfDay.observeAsState(0.0)
    val totalCommandsOfDay by viewModel.totalCommandsOfDay.observeAsState(0)
    val totalCommandsOfMonth by viewModel.totalCommandsOfMonth.observeAsState(0)
    val totalRevenueOfMonth by viewModel.totalRevenueOfMonth.observeAsState(0.0)
    val salesByCategory by viewModel.salesByCategory.observeAsState(emptyMap())

// Variables pour la date sélectionnée
    val calendar = Calendar.getInstance()
    var selectedDay by remember { mutableStateOf(calendar.get(Calendar.DAY_OF_MONTH)) }
    var selectedMonth by remember { mutableStateOf(calendar.get(Calendar.MONTH) + 1) } // Les mois commencent à 0, donc on ajoute 1
    var selectedYear by remember { mutableStateOf(calendar.get(Calendar.YEAR)) }

    // Fonction pour mettre à jour les données en fonction de la date sélectionnée
    fun updateDataForSelectedDate(day: Int, month: Int, year: Int) {
        viewModel.fetchCommandsByDayAndCategory(day, month, year)
        viewModel.fetchTotalCommandsByDay(day, month, year)
        viewModel.fetchTotalPriceByDay(day, month, year)
        viewModel.fetchCommandsByMonth(month, year)
    }

// Initial data fetch
    updateDataForSelectedDate(selectedDay, selectedMonth, selectedYear)

// Fonction pour ouvrir le DatePicker
    val openDatePicker = { context: Context ->
        val datePickerDialog = DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                selectedDay = dayOfMonth
                selectedMonth = month + 1 // Les mois commencent à 0
                selectedYear = year
                updateDataForSelectedDate(selectedDay, selectedMonth, selectedYear)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .safeDrawingPadding(),
        color = MaterialTheme.colorScheme.background
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = stringResource(id = R.string.app_name)) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "back"
                            )
                        }
                    },
                    actions = {
                        Button(
                            onClick = {
                                authenticationViewModel.logout()
                                navController.navigate(BdeCafetScreens.CONNECTION_SCREEN.id) {
                                    popUpTo(BdeCafetScreens.WELCOME_SCREEN.id) { inclusive = true }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent
                            ),
                            elevation = ButtonDefaults.buttonElevation(0.dp)
                        ) {
                            Text(text = stringResource(R.string.logout))
                        }
                    },
                    colors = TopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        scrolledContainerColor = MaterialTheme.colorScheme.primary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    )

                )
            },
            content = { paddingValues ->
                LazyColumn(
                    modifier = Modifier
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp)
                ) {
                    item {
                        Text(
                            text = stringResource(R.string.history),
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.Center,  // Centre l'élément verticalement
                            horizontalAlignment = Alignment.CenterHorizontally  // Centre l'élément horizontalement
                        ) {
                            Button(
                                onClick = { openDatePicker(navController.context) },
                                modifier = Modifier
                                    .padding(16.dp),

                                ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally, // Centrer à l'intérieur du bouton
                                    verticalArrangement = Arrangement.Center // Centrer verticalement si besoin

                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.DateRange,
                                        contentDescription = "Sélectionner une date",
                                        modifier = Modifier
                                            .padding(end = 8.dp)
                                            .size(24.dp)
                                    )
                                    Text(stringResource(R.string.select_date))
                                }
                            }
                        }
                    }
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally // Centre le texte horizontalement
                        ) {
                            Text(
                                text = stringResource(
                                    id = R.string.selected_date,
                                    selectedDay,
                                    selectedMonth,
                                    selectedYear
                                ),
                                fontWeight = FontWeight.Bold,  // Applique le gras
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.padding(vertical = 8.dp),
                            )
                        }
                        Spacer(modifier = Modifier.padding(8.dp))
                        HorizontalDivider()
                    }

                    // Afficher les commandes groupées par catégorie
                    salesByCategory.forEach { (category, productCounts) ->
                        item {
                            Text(
                                text = stringResource(id = R.string.category_label, category),
                                style = MaterialTheme.typography.titleMedium,
                            )

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                // Afficher les produits dans cette catégorie
                                Column(
                                    modifier = Modifier.weight(1f) // Cela permet de donner plus d'espace pour la liste des produits
                                ) {
                                    productCounts.forEach { (productName, count) ->
                                        Text(
                                            text = "$productName: $count",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.padding(8.dp))

                            HorizontalDivider()
                        }
                    }

                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally // Centre le texte horizontalement
                        ) {
                            Text(
                                text = stringResource(id = R.string.sales_of_the_day),
                                fontWeight = FontWeight.Bold,  // Applique le gras
                                style = MaterialTheme.typography.titleLarge, // Tu peux ajuster le style ici si tu veux un autre type
                                modifier = Modifier.padding(vertical = 8.dp),
                            )
                        }

                        Text(
                            text = stringResource(id = R.string.total_orders, totalCommandsOfDay)
                        )
                        Text(
                            text = stringResource(id = R.string.total_sales, totalPriceOfDay),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )

                    }
                    item {
                        Spacer(modifier = Modifier.padding(8.dp))

                        HorizontalDivider()
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally // Centre le texte horizontalement
                        ) {
                            Text(
                                text = stringResource(
                                    id = R.string.sales_of_the_month,
                                    selectedMonth,
                                    selectedYear
                                ),
                                fontWeight = FontWeight.Bold,  // Applique le gras
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.padding(vertical = 8.dp),
                            )
                        }
                        Text(
                            text = stringResource(
                                id = R.string.total_monthly_orders,
                                totalCommandsOfMonth
                            )
                        )
                        Text(
                            text = stringResource(
                                id = R.string.total_monthly_sales,
                                totalRevenueOfMonth
                            ),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                    }
                }
            }
        )
    }
}


