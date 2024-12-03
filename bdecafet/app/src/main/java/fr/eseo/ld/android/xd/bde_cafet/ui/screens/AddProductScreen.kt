package fr.eseo.ld.android.xd.bde_cafet.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import fr.eseo.ld.android.xd.bde_cafet.R
import fr.eseo.ld.android.xd.bde_cafet.model.Product
import fr.eseo.ld.android.xd.bde_cafet.ui.navigation.BdeCafetScreens
import fr.eseo.ld.android.xd.bde_cafet.ui.theme.addProductColor
import fr.eseo.ld.android.xd.bde_cafet.ui.theme.btnConfirmer
import fr.eseo.ld.android.xd.bde_cafet.viewmodels.AuthenticationViewModel
import fr.eseo.ld.android.xd.bde_cafet.viewmodels.CafetViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(navController: NavController) {
    val authenticationViewModel: AuthenticationViewModel = hiltViewModel()
    val cafetViewModel: CafetViewModel = hiltViewModel()

    val name = remember { mutableStateOf("") }
    val category = remember { mutableStateOf("") }
    val price = remember { mutableStateOf("") }
    val description = remember { mutableStateOf("") }
    val stock = remember { mutableStateOf("") }

    val nameError = remember { mutableStateOf(false) }
    val categoryError = remember { mutableStateOf(false) }
    val priceError = remember { mutableStateOf(false) }
    val stockError = remember { mutableStateOf(false) }

    val categories = listOf(
        stringResource(R.string.dessert),
        stringResource(R.string.beverage),
        stringResource(R.string.sandwich),
        stringResource(R.string.salad)
    )
    var expanded by remember { mutableStateOf(false) }

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
                            }
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
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .padding(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.add_new_product),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    OutlinedTextField(
                        value = name.value,
                        onValueChange = {
                            name.value = it
                            nameError.value = it.isEmpty()
                        },
                        label = { Text(stringResource(R.string.name)) },
                        isError = nameError.value,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )
                    if (nameError.value) {
                        Text(
                            text = stringResource(R.string.name_is_required),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = category.value,
                            onValueChange = {
                                category.value = it
                                categoryError.value = it.isEmpty()
                            },
                            label = { Text(stringResource(R.string.category)) },
                            readOnly = true,
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                            },
                            isError = categoryError.value,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                                .menuAnchor(MenuAnchorType.PrimaryEditable, true)
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            categories.forEach { selectionOption ->
                                DropdownMenuItem(
                                    text = { Text(selectionOption) },
                                    onClick = {
                                        category.value = selectionOption
                                        categoryError.value = false
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                    if (categoryError.value) {
                        Text(
                            text = stringResource(R.string.category_is_required),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    OutlinedTextField(
                        value = price.value,
                        onValueChange = {
                            price.value = it
                            priceError.value = it.isEmpty()
                        },
                        label = { Text(stringResource(R.string.price)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = priceError.value,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )
                    if (priceError.value) {
                        Text(
                            text = stringResource(R.string.price_is_required),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    OutlinedTextField(
                        value = description.value,
                        onValueChange = { description.value = it },
                        label = { Text(stringResource(R.string.description)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = stock.value,
                        onValueChange = {
                            stock.value = it
                            stockError.value = it.isEmpty()
                        },
                        label = { Text(stringResource(R.string.stock)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = stockError.value,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    )
                    if (stockError.value) {
                        Text(
                            text = stringResource(R.string.stock_is_required),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    Button(
                        onClick = {
                            val isValid =
                                name.value.isNotEmpty() && category.value.isNotEmpty() && price.value.isNotEmpty() && stock.value.isNotEmpty()
                            if (isValid) {
                                val product = Product(
                                    name = name.value,
                                    category = category.value,
                                    price = price.value.toDoubleOrNull() ?: 0.0,
                                    description = description.value,
                                    stock = stock.value.toIntOrNull() ?: 0
                                )
                                cafetViewModel.addProduct(product)
                                navController.popBackStack()
                            } else {
                                nameError.value = name.value.isEmpty()
                                categoryError.value = category.value.isEmpty()
                                priceError.value = price.value.isEmpty()
                                stockError.value = stock.value.isEmpty()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor  = btnConfirmer
                        )
                    ) {
                        Text(stringResource(R.string.add_product))
                    }
                }
            }
        )
    }
}