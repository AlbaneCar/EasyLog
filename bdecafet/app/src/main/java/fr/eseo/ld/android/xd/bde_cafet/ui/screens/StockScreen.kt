package fr.eseo.ld.android.xd.bde_cafet.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import fr.eseo.ld.android.xd.bde_cafet.R
import fr.eseo.ld.android.xd.bde_cafet.repositories.FirebaseRepository
import fr.eseo.ld.android.xd.bde_cafet.ui.navigation.BdeCafetScreens
import fr.eseo.ld.android.xd.bde_cafet.ui.screens.components.ListProductByCategorie
import fr.eseo.ld.android.xd.bde_cafet.ui.theme.stockColor
import fr.eseo.ld.android.xd.bde_cafet.viewmodels.AuthenticationViewModel
import fr.eseo.ld.android.xd.bde_cafet.viewmodels.CafetViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockScreen(
    navController: NavController,
    firebaseRepository: FirebaseRepository,
    authenticationViewModel: AuthenticationViewModel
) {
    val viewModel: CafetViewModel = hiltViewModel()
    val snackbarHostState = remember { SnackbarHostState() }
    val errorMessage by viewModel.errorMessage.observeAsState()

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
                                containerColor = Color.Transparent // Rendre le fond du bouton transparent
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
                LazyColumn(Modifier.padding(paddingValues)) {
                    item {
                        Text(
                            text = stringResource(R.string.stock),
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                    item {
                        ListProductByCategorie(
                            category = "Sandwich",
                            viewModel = viewModel,
                            firebaseRepository,
                            snackbarHostState,
                            isSale = false
                        )
                    }
                    item {
                        ListProductByCategorie(
                            category = "Salade",
                            viewModel = viewModel,
                            firebaseRepository,
                            snackbarHostState,
                            isSale = false
                        )
                    }
                    item {
                        ListProductByCategorie(
                            category = "Dessert",
                            viewModel = viewModel,
                            firebaseRepository,
                            snackbarHostState,
                            isSale = false
                        )
                    }
                    item {
                        ListProductByCategorie(
                            category = "Boisson",
                            viewModel = viewModel,
                            firebaseRepository,
                            snackbarHostState,
                            isSale = false
                        )
                    }
                }
                errorMessage?.let {
                    Text(text = it, color = Color.Red)
                }
            },
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            }
        )
    }
}
