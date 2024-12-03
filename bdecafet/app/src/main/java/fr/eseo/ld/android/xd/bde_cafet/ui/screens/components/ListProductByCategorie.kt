package fr.eseo.ld.android.xd.bde_cafet.ui.screens.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.MutableLiveData
import fr.eseo.ld.android.xd.bde_cafet.model.Product
import fr.eseo.ld.android.xd.bde_cafet.repositories.FirebaseRepository
import fr.eseo.ld.android.xd.bde_cafet.ui.theme.inverseSurfaceLight
import fr.eseo.ld.android.xd.bde_cafet.viewmodels.CafetViewModel


@Composable
fun ListProductByCategorie(
    category: String,
    viewModel: CafetViewModel = hiltViewModel(),
    firebaseRepository: FirebaseRepository,
    snackbarHostState: SnackbarHostState,
    isSale: Boolean
) {
    val productsLiveData = remember(category) {
        when (category) {
            "Sandwich" -> viewModel.sandwichProducts
            "Dessert" -> viewModel.dessertProducts
            "Salade" -> viewModel.saladeProducts
            "Boisson" -> viewModel.boissonProducts
            else -> MutableLiveData<List<Product?>>(emptyList())
        }
    }
    val products by productsLiveData.observeAsState(emptyList())
    val errorMessage by viewModel.errorMessage.observeAsState()

    LaunchedEffect(category) {
        viewModel.getProductsByCategory(category)
    }

    Column {
        Text(
            text = category,
            style = MaterialTheme.typography.displaySmall,
            color = inverseSurfaceLight,
            modifier = Modifier
                .padding(vertical = 8.dp)
                .padding(start = 8.dp)
        )

        errorMessage?.let {
            Text(text = it, color = Color.Red)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()), // Scroll horizontal pour les items
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (isSale) {
                products.filterNotNull().forEach { product ->
                    ProductItemSale(category, viewModel, product, snackbarHostState)
                }
            } else {
                products.filterNotNull().forEach { product ->
                    ProductItemStock(
                        category,
                        viewModel,
                        product,
                        firebaseRepository,
                        snackbarHostState
                    )
                }
            }
        }
    }
}