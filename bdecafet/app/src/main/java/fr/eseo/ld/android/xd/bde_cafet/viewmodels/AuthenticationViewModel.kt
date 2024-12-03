package fr.eseo.ld.android.xd.bde_cafet.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.eseo.ld.android.xd.bde_cafet.repositories.AuthenticationRepository
import javax.inject.Inject

@HiltViewModel
class AuthenticationViewModel @Inject constructor(
    private val authenticationRepository: AuthenticationRepository,
) : ViewModel() {

    private val _user = MutableLiveData<FirebaseUser?>()
    val user: MutableLiveData<FirebaseUser?> get() = _user

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: MutableLiveData<String?> get() = _errorMessage

    init {
        _user.value = authenticationRepository.getCurrentUser()
    }

    fun signUp(email: String, password: String) {
        authenticationRepository.signUpWithEmail(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                _user.value = authenticationRepository.getCurrentUser()
            } else {
                val exception = task.exception
                if (exception?.message?.contains("The email address is already in use by another account") == true) {
                    _errorMessage.value =
                        "Cet email est déjà utilisé, veuillez en choisir un autre."
                } else {
                    _errorMessage.value = exception?.message
                }
                _user.value = null
            }
        }
    }

    fun login(email: String, password: String) {
        authenticationRepository.loginWithEmail(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                _user.value = authenticationRepository.getCurrentUser()
            } else {
                val exception = task.exception
                if (exception?.message?.contains("There is no user record corresponding to this identifier") == true) {
                    _errorMessage.value = "Ce compte n'existe pas, merci de s'inscrire d'abord"
                } else {
                    _errorMessage.value = exception?.message
                }
                _user.value = null
            }
        }
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    fun logout() {
        authenticationRepository.logout()
    }

    fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        authenticationRepository.firebaseAuthWithGoogle(account).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                _user.value = authenticationRepository.getCurrentUser()
            } else {
                _user.value = null
            }
        }
    }

    fun getGoogleSignInClient() = authenticationRepository.getGoogleSignInClient()
}