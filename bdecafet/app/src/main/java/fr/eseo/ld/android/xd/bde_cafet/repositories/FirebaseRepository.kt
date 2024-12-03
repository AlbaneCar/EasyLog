package fr.eseo.ld.android.xd.bde_cafet.repositories

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import fr.eseo.ld.android.xd.bde_cafet.model.Command
import fr.eseo.ld.android.xd.bde_cafet.model.Product
import kotlinx.coroutines.tasks.await
import java.util.Calendar

class FirebaseRepository {
    private val database: FirebaseDatabase by lazy {
        FirebaseDatabase.getInstance()
    }
    private val produitsRef = database.getReference("produits")
    private val commandesRef = database.getReference("commandes")

    val usersRef = database.getReference("users")

    // Fonction pour ajouter un produit dans Firebase
    fun addProduct(product: Product, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        produitsRef.orderByKey().limitToLast(1).get().addOnSuccessListener { snapshot ->
            val lastId = snapshot.children.firstOrNull()?.key?.toIntOrNull() ?: 0
            val newId = (lastId + 1).toString()
            val productWithId = product.copy(id = newId)
            produitsRef.child(newId).setValue(productWithId)
                .addOnSuccessListener {
                    Log.d("FirebaseRepository", "Produit ajouté : ${productWithId.name}")
                    onSuccess()
                }
                .addOnFailureListener { exception ->
                    Log.e("FirebaseRepository", "Erreur d'ajout : ${exception.message}")
                    onFailure(exception)
                }
        }.addOnFailureListener { exception ->
            Log.e(
                "FirebaseRepository",
                "Erreur de récupération du dernier ID : ${exception.message}"
            )
            onFailure(exception)
        }
    }

    // Fonction pour récupérer la liste des produits
    fun getProducts(onSuccess: (List<Product>) -> Unit, onFailure: (Exception) -> Unit) {
        produitsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val productList = mutableListOf<Product>()
                snapshot.children.forEach { dataSnapshot ->
                    val product = dataSnapshot.getValue(Product::class.java)
                    if (product != null) {
                        productList.add(product)
                    }
                }
                onSuccess(productList)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseRepository", "Erreur de récupération : ${error.message}")
                onFailure(error.toException())
            }
        })
    }

    fun getProductsByCategory(
        category: String,
        onSuccess: (List<Product>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        produitsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val productList = mutableListOf<Product>()
                snapshot.children.forEach { dataSnapshot ->
                    val product = dataSnapshot.getValue(Product::class.java)
                    if (product != null && product.category == category) {
                        productList.add(product)
                    }
                }
                onSuccess(productList)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseRepository", "Erreur de récupération : ${error.message}")
                onFailure(error.toException())
            }
        })
    }

    fun setCommandByProductId(
        productId: String,
        onSuccess: () -> Unit = {},
        onFailure: (Exception) -> Unit = {}
    ) {

        // Récupérer la date actuelle formatée jour/mois/année
        val calendar = Calendar.getInstance()
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month =
            (calendar.get(Calendar.MONTH) + 1).toString() // Le mois commence à 0, donc on ajoute 1
        val year = calendar.get(Calendar.YEAR)

        val formattedDate = "$year-$month-$day"

        // Récupérer les données du produit à partir de son ID
        produitsRef.child(productId).get().addOnSuccessListener { dataSnapshot ->
            val product = dataSnapshot.getValue(Product::class.java)

            if (product != null) {
                // Récupérer la dernière commande et en déduire l'ID
                commandesRef.orderByKey().limitToLast(1).get().addOnSuccessListener { snapshot ->
                    val lastCommand = snapshot.children.firstOrNull()
                    val newCommandId = (lastCommand?.key?.toLongOrNull()
                        ?: 0L) + 1 // Incrémenter l'ID de la commande

                    // Créer la commande avec l'ID incrémenté
                    val command = Command(
                        id = newCommandId.toString(),  // Utiliser l'ID incrémenté comme String
                        products = product,  // Ajouter le produit à la commande
                        date = formattedDate  // Ajouter la date formatée
                    )

                    // Ajouter la commande dans la base de données
                    commandesRef.child(newCommandId.toString()).setValue(command)
                        .addOnSuccessListener {
                            Log.d("FirebaseRepository", "Commande ajoutée avec succès.")
                            onSuccess()
                        }
                        .addOnFailureListener { exception ->
                            Log.e(
                                "FirebaseRepository",
                                "Erreur d'ajout de la commande : ${exception.message}"
                            )
                            onFailure(exception)
                        }
                }.addOnFailureListener { exception ->
                    Log.e(
                        "FirebaseRepository",
                        "Erreur de récupération de la dernière commande : ${exception.message}"
                    )
                    onFailure(exception)
                }
            } else {
                onFailure(Exception("Produit non trouvé"))
            }
        }.addOnFailureListener { exception ->
            Log.e("FirebaseRepository", "Erreur de récupération du produit : ${exception.message}")
            onFailure(exception)
        }
    }

    suspend fun deleteProduct(product: Product) {
        val productRef = produitsRef.child(product.id)
        productRef.removeValue().await()
    }

    fun updateProductStock(
        productId: String,
        decrement: Int,
        onSuccess: () -> Unit = {},
        onFailure: (Exception) -> Unit = {}
    ) {
        val productRef = produitsRef.child(productId)

        productRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val product = snapshot.getValue(Product::class.java)
                if (product != null) {
                    val newStock = product.stock + decrement
                    if (newStock >= 0) { // Vérifier qu'on ne va pas en dessous de 0
                        productRef.child("stock").setValue(newStock)
                            .addOnSuccessListener { onSuccess() }
                            .addOnFailureListener { onFailure(it) }
                    } else {
                        onFailure(Exception("Stock insuffisant"))
                    }
                } else {
                    onFailure(Exception("Produit non trouvé"))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                onFailure(error.toException())
            }
        })
    }

    fun getCommandsByDay(
        day: Int,
        month: Int,
        year: Int,
        onSuccess: (List<Command>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val dateFilter = "$year-$month-$day" // Date sous forme de jour/mois/année
        commandesRef.orderByChild("date").equalTo(dateFilter).get()
            .addOnSuccessListener { snapshot ->
                val commands = snapshot.children.mapNotNull {
                    it.getValue(Command::class.java)
                }
                onSuccess(commands)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun getCommandsByMonth(
        month: Int,
        year: Int,
        onSuccess: (List<Command>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val dateFilterStart = "$year-$month-01" // Le premier jour du mois
        val dateFilterEnd = "$year-$month-31"   // Le dernier jour du mois (ou 28/29 selon le mois)

        commandesRef.orderByChild("date").startAt(dateFilterStart).endAt(dateFilterEnd).get()
            .addOnSuccessListener { snapshot ->
                val commands = snapshot.children.mapNotNull { it.getValue(Command::class.java) }
                onSuccess(commands)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun updateUserRole(
        userId: String,
        email: String,
        role: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val userRef = usersRef.child(userId)

        userRef.child("email").setValue(email)
            .addOnSuccessListener {
                userRef.child("role").setValue(role)
                    .addOnSuccessListener {
                        Log.d("FirebaseRepository", "User role updated to: $role")
                        onSuccess()
                    }
                    .addOnFailureListener { exception ->
                        Log.e(
                            "FirebaseRepository",
                            "Error updating user role: ${exception.message}"
                        )
                        onFailure(exception)
                    }
            }
            .addOnFailureListener { exception ->
                Log.e("FirebaseRepository", "Error updating user email: ${exception.message}")
                onFailure(exception)
            }
    }

    fun getUserRole(userId: String, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        usersRef.child(userId).child("role").get()
            .addOnSuccessListener { snapshot ->
                val role = snapshot.getValue(String::class.java) ?: "Unknown"
                Log.d("FirebaseRepository", "User role fetched: $role")
                onSuccess(role)
            }
            .addOnFailureListener { exception ->
                Log.e("FirebaseRepository", "Error fetching user role: ${exception.message}")
                onFailure(exception)
            }
    }

    fun hasUserRole(userId: String, onSuccess: (Boolean) -> Unit, onFailure: (Exception) -> Unit) {
        usersRef.child(userId).child("role").get()
            .addOnSuccessListener { snapshot ->
                val hasRole =
                    snapshot.exists() && snapshot.getValue(String::class.java)?.isNotEmpty() == true
                onSuccess(hasRole)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }


    fun getAllUsersWithRoles(
        onSuccess: (List<Triple<String, String, String>>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val usersList = mutableListOf<Triple<String, String, String>>()
        val usersRef = FirebaseDatabase.getInstance().getReference("users")

        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (userSnapshot in snapshot.children) {
                    val userId = userSnapshot.key ?: continue
                    val email =
                        userSnapshot.child("email").getValue(String::class.java) ?: "Unknown"
                    val role = userSnapshot.child("role").getValue(String::class.java) ?: "No role"
                    usersList.add(Triple(userId, email, role))
                }
                onSuccess(usersList)
            }

            override fun onCancelled(error: DatabaseError) {
                onFailure(error.toException())
            }
        })
    }

    fun deleteUser(userId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        usersRef.child(userId).removeValue()
            .addOnSuccessListener {
                Log.d("FirebaseRepository", "User deleted successfully")
                onSuccess()
            }
            .addOnFailureListener { exception ->
                Log.e("FirebaseRepository", "Error deleting user: ${exception.message}")
                onFailure(exception)
            }
    }
}