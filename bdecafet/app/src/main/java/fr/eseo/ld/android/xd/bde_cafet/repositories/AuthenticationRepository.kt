package fr.eseo.ld.android.xd.bde_cafet.repositories

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import javax.inject.Inject

class AuthenticationRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val googleSignInClient: GoogleSignInClient
) {
    fun signUpWithEmail(email: String, password: String) =
        firebaseAuth.createUserWithEmailAndPassword(email, password)

    fun loginWithEmail(email: String, password: String) =
        firebaseAuth.signInWithEmailAndPassword(email, password)

    fun logout() = firebaseAuth.signOut()

    fun getGoogleSignInClient() = googleSignInClient

    fun firebaseAuthWithGoogle(account: GoogleSignInAccount) =
        firebaseAuth.signInWithCredential(
            GoogleAuthProvider.getCredential(account.idToken, null)
        )

    fun getCurrentUser() = firebaseAuth.currentUser
}