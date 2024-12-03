package fr.eseo.ld.android.xd.bde_cafet.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import fr.eseo.ld.android.xd.bde_cafet.R
import fr.eseo.ld.android.xd.bde_cafet.repositories.FirebaseRepository
import fr.eseo.ld.android.xd.bde_cafet.ui.navigation.BdeCafetScreens
import fr.eseo.ld.android.xd.bde_cafet.ui.theme.addProductColor
import fr.eseo.ld.android.xd.bde_cafet.ui.theme.historyColor
import fr.eseo.ld.android.xd.bde_cafet.ui.theme.salesColors
import fr.eseo.ld.android.xd.bde_cafet.ui.theme.stockColor
import fr.eseo.ld.android.xd.bde_cafet.ui.theme.userManagement
import fr.eseo.ld.android.xd.bde_cafet.viewmodels.AuthenticationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavController) {
    val authenticationViewModel: AuthenticationViewModel = hiltViewModel()
    val currentUser = authenticationViewModel.user.value
    val firebaseRepository = FirebaseRepository()
    var userRole by remember { mutableStateOf("Unknown") }

    LaunchedEffect(currentUser) {
        currentUser?.let {
            firebaseRepository.getUserRole(it.uid, onSuccess = { role ->
                userRole = role
            }, onFailure = { exception ->
                Log.e("DashboardScreen", "Error fetching user role: ${exception.message}")
            })
        }
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
                LazyColumn(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            DashboardItem(
                                color = salesColors,
                                text = stringResource(id = R.string.sale),
                                icon = R.drawable.icon_lunch,
                                onClick = {
                                    navController.navigate(BdeCafetScreens.SALE_SCREEN.id) {
                                        popUpTo("start") { inclusive = true }
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            )
                            if (userRole in listOf("responsable", "manager")) {
                                DashboardItem(
                                    color = addProductColor,
                                    text = stringResource(id = R.string.new_product),
                                    icon = R.drawable.icon_add_product,
                                    onClick = {
                                        navController.navigate(BdeCafetScreens.ADD_PRODUCT_SCREEN.id) {
                                            popUpTo("start") { inclusive = true }
                                        }
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .height(IntrinsicSize.Min),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            DashboardItem(
                                color = stockColor,
                                text = stringResource(id = R.string.stock),
                                icon = R.drawable.icon_graphic,
                                onClick = {
                                    navController.navigate(BdeCafetScreens.STOCK_SCREEN.id) {
                                        popUpTo("start") { inclusive = true }
                                    }
                                }, modifier = Modifier.weight(1f)
                            )
                            DashboardItem(
                                color = historyColor,
                                text = stringResource(id = R.string.history),
                                icon = R.drawable.icon_calendar,
                                onClick = {
                                    navController.navigate(BdeCafetScreens.HISTORY_SCREEN.id) {
                                        popUpTo("start") { inclusive = true }
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        if (userRole in listOf("responsable", "manager")) {
                            Row(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .height(IntrinsicSize.Min),
                            ) {
                                DashboardItem(
                                    color = userManagement,
                                    text = stringResource(id = R.string.user_management),
                                    icon = R.drawable.icon_add_user,
                                    onClick = {
                                        navController.navigate(BdeCafetScreens.GESTION_SCREEN.id) {
                                            popUpTo("start") { inclusive = true }
                                        }
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                    item {
                        Text(
                            text = stringResource(
                                R.string.connected_as,
                                currentUser?.email ?: "Unknown"
                            ),
                            fontSize = 13.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = stringResource(R.string.role, userRole),
                            fontSize = 13.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        )
    }
}

@Composable
fun DashboardItem(
    color: Color,
    text: String,
    icon: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(color)
            .aspectRatio(1f)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                modifier = Modifier.size(60.dp)
            )
            Text(
                text = text,
                fontSize = 20.sp,
                color = Color.Black,
                modifier = Modifier.padding(16.dp),
                textAlign = TextAlign.Center,
                fontFamily = FontFamily.SansSerif
            )
        }
    }
}
