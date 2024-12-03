package fr.eseo.ld.android.xd.bde_cafet.ui.screens.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import fr.eseo.ld.android.xd.bde_cafet.R
import fr.eseo.ld.android.xd.bde_cafet.model.Product
import fr.eseo.ld.android.xd.bde_cafet.ui.theme.btnConfirmer
import fr.eseo.ld.android.xd.bde_cafet.viewmodels.CafetViewModel
import kotlinx.coroutines.launch

@Composable
fun ProductItemSale(
    category: String,
    viewModel: CafetViewModel = hiltViewModel(),
    product: Product,
    snackbarHostState: SnackbarHostState,
) {
    var showDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .padding(8.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp))
            .padding(12.dp)
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
                    .padding(vertical = 8.dp), // Ajout d'un padding pour espacer un peu le contenu
                verticalAlignment = Alignment.CenterVertically // Centrer verticalement le contenu de la Row
            ) {
                Button(
                    onClick = { showDialog = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = btnConfirmer
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text= stringResource(R.string.to_sale), color = MaterialTheme.colorScheme.onPrimary)
                }

                // Popup de confirmation
                if (showDialog) {
                    AlertDialog(
                        onDismissRequest = { showDialog = false },
                        title = { Text(text= stringResource(R.string.validate_sale)) },
                        confirmButton = {
                            TextButton(onClick = {
                                showDialog = false
                                coroutineScope.launch {
                                    if (product.stock > 0) {
                                        viewModel.updateProductStockAndRefresh(
                                            product.id,
                                            -1,
                                            category
                                        )
                                        viewModel.setCommand(product.id)

                                        snackbarHostState.showSnackbar(
                                            message = context.getString(R.string.to_sale, product.name))

                                    } else {
                                        snackbarHostState.showSnackbar(
                                            message = context.getString(R.string.lack_stock, product.name))                                    }
                                }
                            }) {
                                Text(text = stringResource(R.string.yes))
                            }
                        },

                    dismissButton = {
                            TextButton(onClick = { showDialog = false },) {
                                Text(text = stringResource(R.string.no))
                            }
                        }
                    )
                }
                Text(
                    text = "   " + stringResource(R.string.stock) + " : ${product.stock}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}


