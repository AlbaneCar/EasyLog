package fr.eseo.ld.android.xd.bde_cafet.ui.screens.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import fr.eseo.ld.android.xd.bde_cafet.R
import fr.eseo.ld.android.xd.bde_cafet.model.Product
import fr.eseo.ld.android.xd.bde_cafet.repositories.FirebaseRepository
import fr.eseo.ld.android.xd.bde_cafet.ui.theme.btnConfirmer
import fr.eseo.ld.android.xd.bde_cafet.ui.theme.stockColor
import fr.eseo.ld.android.xd.bde_cafet.viewmodels.CafetViewModel
import kotlinx.coroutines.launch


@Composable
fun ProductItemStock(
    category: String,
    viewModel: CafetViewModel = hiltViewModel(),
    product: Product,
    repository: FirebaseRepository,
    snackbarHostState: SnackbarHostState,
) {
    var showStockDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var quantityText by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .padding(8.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp))
            .padding(12.dp)
            .pointerInput(Unit) { // Détection de l'appui long
                detectTapGestures(
                    onLongPress = { showDeleteDialog = true }
                )
            }
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = product.name,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (product.description != "") {
                product.description?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Text(
                text = "${product.price}€",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { showStockDialog = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = btnConfirmer
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = stringResource(R.string.stock), color = MaterialTheme.colorScheme.onPrimary)
                }

                // Popup de confirmation avec TextField pour la quantité
                if (showStockDialog) {
                    AlertDialog(
                        onDismissRequest = { showStockDialog = false },
                        title = { Text(product.name) },
                        text = {
                            Column {

                                Text(
                                    text = stringResource(R.string.current_stock) + " : ${product.stock}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                Text(text = stringResource(R.string.add_new_quantity));
                                Spacer(modifier = Modifier.height(8.dp))

                                // Zone de texte pour entrer la quantité
                                TextField(
                                    value = quantityText,
                                    onValueChange = { quantityText = it },
                                    label = { Text(text = stringResource(R.string.quantity)) },
                                    placeholder = { Text("0") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    keyboardOptions = KeyboardOptions.Default.copy(
                                        keyboardType = KeyboardType.Number // Ouvre le clavier numérique
                                    )
                                )
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = {
                                val quantity = quantityText.toIntOrNull() ?: 0
                                if (quantity > 0) {
                                    showStockDialog = false
                                    coroutineScope.launch {
                                        viewModel.updateProductStockAndRefresh(
                                            product.id,
                                            quantity,
                                            category
                                        )
                                        snackbarHostState.showSnackbar(
                                            message = context.getString(R.string.add, quantity, product.name))
                                    }
                                }
                            }) {
                                Text(text = stringResource(R.string.yes))
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = {
                                showStockDialog = false
                                quantityText = ""  // Réinitialiser la quantité à chaque fermeture
                            }) {
                                Text(text = stringResource(R.string.no))
                            }
                        }
                    )
                }
                // Popup de confirmation pour suppression
                if (showDeleteDialog) {
                    AlertDialog(
                        onDismissRequest = { showDeleteDialog = false },
                        title = { Text(text = stringResource(R.string.delete_product)) },
                        text = { Text(text = stringResource(R.string.want_to_delete)+" ${product.name}") },
                        confirmButton = {
                            TextButton(onClick = {
                                coroutineScope.launch {
                                    viewModel.deleteProduct(product) // Appeler la méthode pour supprimer le produit
                                    snackbarHostState.showSnackbar("${product.name} supprimé")
                                }
                                showDeleteDialog = false
                            }) {
                                Text(text = stringResource(R.string.no))
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = {
                                showDeleteDialog = false
                            }) {
                                Text(text = stringResource(R.string.yes))
                            }
                        }
                    )
                }
                Text(
                    text = "   " + stringResource(R.string.stock) +" : ${product.stock}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
