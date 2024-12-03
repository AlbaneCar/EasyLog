package fr.eseo.ld.android.xd.bde_cafet.ui

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import fr.eseo.ld.android.xd.bde_cafet.repositories.FirebaseRepository
import fr.eseo.ld.android.xd.bde_cafet.ui.navigation.BdeCafetScreens
import fr.eseo.ld.android.xd.bde_cafet.ui.screens.AddProductScreen
import fr.eseo.ld.android.xd.bde_cafet.ui.screens.DashboardScreen
import fr.eseo.ld.android.xd.bde_cafet.ui.screens.GestionScreen
import fr.eseo.ld.android.xd.bde_cafet.ui.screens.HistoryScreen
import fr.eseo.ld.android.xd.bde_cafet.ui.screens.LoginScreen
import fr.eseo.ld.android.xd.bde_cafet.ui.screens.RoleScreen
import fr.eseo.ld.android.xd.bde_cafet.ui.screens.SaleScreen
import fr.eseo.ld.android.xd.bde_cafet.ui.screens.StockScreen
import fr.eseo.ld.android.xd.bde_cafet.viewmodels.AuthenticationViewModel

@Composable
fun BdeCafetApp() {
    val navController = rememberNavController()
    val authenticationViewModel: AuthenticationViewModel = hiltViewModel()
    val firebaseRepository = FirebaseRepository()

    val user by authenticationViewModel.user.observeAsState()

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val data = result.data
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.getResult(ApiException::class.java)!!
            authenticationViewModel.firebaseAuthWithGoogle(account)
        } catch (e: ApiException) {
            e.printStackTrace()
        }
    }

    val startDestination = if (user != null) BdeCafetScreens.WELCOME_SCREEN.id else "start"

    LaunchedEffect(user) {
        user?.let {
            navigateToRoleScreen(navController, it.uid, firebaseRepository)
        }
    }

    NavHost(
        navController,
        startDestination = startDestination
    ) {
        composable("start") {
            LoginScreen(authenticationViewModel, googleSignInLauncher)
        }
        composable(BdeCafetScreens.WELCOME_SCREEN.id) {

            DashboardScreen(navController)
        }
        composable(BdeCafetScreens.CONNECTION_SCREEN.id) {
            LoginScreen(authenticationViewModel, googleSignInLauncher)
        }
        composable(BdeCafetScreens.SALE_SCREEN.id) {
            SaleScreen(
                navController = navController,
                firebaseRepository = firebaseRepository,
                authenticationViewModel = authenticationViewModel
            )
        }

        composable(BdeCafetScreens.ADD_PRODUCT_SCREEN.id) {
            AddProductScreen(navController = navController)
        }
        composable(BdeCafetScreens.STOCK_SCREEN.id) {
            StockScreen(
                navController = navController,
                firebaseRepository = firebaseRepository,
                authenticationViewModel = authenticationViewModel
            )
        }
        composable(BdeCafetScreens.HISTORY_SCREEN.id) {
            HistoryScreen(
                navController = navController,
                authenticationViewModel = authenticationViewModel
            )
        }
        composable(BdeCafetScreens.GESTION_SCREEN.id) {
            GestionScreen(navController = navController)
        }
        composable(BdeCafetScreens.ROLE_SCREEN.id) {
            RoleScreen(
                navController = navController,
                firebaseRepository = firebaseRepository,
                authenticationViewModel = authenticationViewModel
            )
        }
    }
}


fun navigateToRoleScreen(
    navController: NavController,
    userId: String,
    firebaseRepository: FirebaseRepository
) {
    firebaseRepository.hasUserRole(userId, onSuccess = { hasRole ->
        if (hasRole) {
            navController.navigate(BdeCafetScreens.WELCOME_SCREEN.id)
        } else {
            navController.navigate(BdeCafetScreens.ROLE_SCREEN.id)
        }
    }, onFailure = { exception ->
        Log.e("Navigation", "Error checking user role: ${exception.message}")
    })
}