package fr.eseo.ld.android.xd.bde_cafet.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import fr.eseo.ld.android.xd.bde_cafet.R
import fr.eseo.ld.android.xd.bde_cafet.repositories.FirebaseRepository
import fr.eseo.ld.android.xd.bde_cafet.ui.navigation.BdeCafetScreens
import fr.eseo.ld.android.xd.bde_cafet.ui.theme.btnAnnuler
import fr.eseo.ld.android.xd.bde_cafet.ui.theme.btnConfirmer
import fr.eseo.ld.android.xd.bde_cafet.ui.theme.inversePrimaryLight
import fr.eseo.ld.android.xd.bde_cafet.ui.theme.primaryContainerLight
import fr.eseo.ld.android.xd.bde_cafet.viewmodels.AuthenticationViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionScreen(navController: NavController) {

    val authenticationViewModel: AuthenticationViewModel = hiltViewModel()
    val currentUser = authenticationViewModel.user.value
    val firebaseRepository = FirebaseRepository()
    var userRole by remember { mutableStateOf("Unknown") }
    var users by remember { mutableStateOf(listOf<Triple<String, String, String>>()) }
    val selectedRoles by remember { mutableStateOf(mutableMapOf<String, String>()) }
    val context = LocalContext.current
    var showDeleteDialog by remember { mutableStateOf(false) }
    var userToDelete by remember { mutableStateOf<Triple<String, String, String>?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(currentUser) {
        currentUser?.let {
            firebaseRepository.getUserRole(it.uid, onSuccess = { role ->
                userRole = role
                firebaseRepository.getAllUsersWithRoles(onSuccess = { userList ->
                    users = userList
                }, onFailure = { exception ->
                    Log.e("GestionScreen", "Error fetching users: ${exception.message}")
                })

            }, onFailure = { exception ->
                Log.e("GestionScreen", "Error fetching user role: ${exception.message}")
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
                    title = { Text(text = stringResource(id = R.string.app_name))},
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "back",
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
                                contentColor = Color.White,
                                containerColor = Color.Transparent // Rendre le fond du bouton transparent
                            ),


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
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {

                    item {
                        Text(
                            text = stringResource(R.string.user_management),
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                    item {
                        users.forEach { (userId, email, role) ->
                            var expanded by remember { mutableStateOf(false) }
                            val roles = listOf(
                                stringResource(R.string.member),
                                stringResource(R.string.manager)
                            )

                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                shape = MaterialTheme.shapes.medium,
                                color = primaryContainerLight,
                            ) {
                                Column(
                                    modifier = Modifier
                                        .padding(16.dp)
                                ) {
                                    Text(
                                        text = "Email: $email",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    ExposedDropdownMenuBox(
                                        expanded = expanded,
                                        onExpandedChange = { expanded = !expanded }
                                    ) {
                                        OutlinedTextField(
                                            value = selectedRoles[email] ?: role,
                                            onValueChange = { selectedRoles[email] = it },
                                            label = { Text(stringResource(R.string.role2)) },
                                            readOnly = true,
                                            trailingIcon = {
                                                ExposedDropdownMenuDefaults.TrailingIcon(
                                                    expanded = expanded
                                                )
                                            },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(bottom = 8.dp)
                                                .menuAnchor(
                                                    MenuAnchorType.PrimaryEditable,
                                                    true
                                                )
                                        )
                                        ExposedDropdownMenu(
                                            expanded = expanded,
                                            onDismissRequest = { expanded = false }
                                        ) {
                                            roles.forEach { selectionOption ->
                                                DropdownMenuItem(
                                                    text = { Text(selectionOption) },
                                                    onClick = {
                                                        selectedRoles[email] = selectionOption
                                                        expanded = false
                                                    }
                                                )
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Button(
                                            onClick = {
                                                firebaseRepository.updateUserRole(
                                                    userId,
                                                    email,
                                                    selectedRoles[email] ?: role,
                                                    onSuccess = {
                                                        coroutineScope.launch {
                                                            snackbarHostState.showSnackbar(
                                                                message = context.getString(R.string.toast_sucess)
                                                            )
                                                        }
                                                    },
                                                    onFailure = { exception ->
                                                        coroutineScope.launch {
                                                            snackbarHostState.showSnackbar(
                                                                message = context.getString(
                                                                    R.string.error_updating_user_role,
                                                                    exception.message
                                                                )
                                                            )
                                                        }
                                                    }
                                                )
                                            },
                                            modifier = Modifier.weight(1f),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = btnConfirmer
                                            )
                                        ) {
                                            Text(text = stringResource(R.string.confirm),
                                                color = Color.White)
                                        }
                                        Button(
                                            onClick = {
                                                userToDelete = Triple(userId, email, role)
                                                showDeleteDialog = true
                                            },
                                            colors = ButtonColors(
                                                containerColor = btnAnnuler,
                                                contentColor = Color.White,
                                                disabledContentColor = btnAnnuler,
                                                disabledContainerColor = btnAnnuler
                                            ),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text(text = stringResource(R.string.delete))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            },
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            }
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.delete)) },
            text = { Text(stringResource(R.string.are_you_sure_delete_user)) },
            confirmButton = {
                TextButton(onClick = {
                    userToDelete?.let { (userId, email, _) ->
                        firebaseRepository.deleteUser(
                            userId,
                            onSuccess = {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = context.getString(R.string.user_deleted)
                                    )
                                }
                                users = users.filterNot { it.first == userId }
                                showDeleteDialog = false
                            },
                            onFailure = { exception ->
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = context.getString(R.string.error_deleting_user)
                                    )
                                }
                            }
                        )
                    }
                }) {
                    Text(stringResource(R.string.yes))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(R.string.no))
                }
            }
        )
    }
}