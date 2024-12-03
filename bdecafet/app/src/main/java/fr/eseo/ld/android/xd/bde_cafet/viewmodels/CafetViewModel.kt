package fr.eseo.ld.android.xd.bde_cafet.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.eseo.ld.android.xd.bde_cafet.model.Product
import fr.eseo.ld.android.xd.bde_cafet.repositories.FirebaseRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CafetViewModel @Inject constructor(private val repository: FirebaseRepository) : ViewModel() {

    private val _productList = MutableLiveData<List<Product?>>()

    private val _sandwichProducts = MutableLiveData<List<Product?>>()
    val sandwichProducts: LiveData<List<Product?>> = _sandwichProducts

    private val _dessertProducts = MutableLiveData<List<Product?>>()
    val dessertProducts: LiveData<List<Product?>> = _dessertProducts

    private val _saladeProducts = MutableLiveData<List<Product?>>()
    val saladeProducts: LiveData<List<Product?>> = _saladeProducts

    private val _boissonProducts = MutableLiveData<List<Product?>>()
    val boissonProducts: LiveData<List<Product?>> = _boissonProducts

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    init {
        getProducts()
    }

    /*
    * Ajouter un produit
     */
    fun addProduct(product: Product) {
        repository.addProduct(product,
            onSuccess = {
                // Mise à jour de la liste des produits après l'ajout
                getProducts()
            },
            onFailure = { exception ->
                _errorMessage.value = exception.message
            }
        )
    }

    /*
    * Récupérer la liste des produits
     */
    fun getProducts() {
        repository.getProducts(
            onSuccess = { products ->
                _productList.value = products
            },
            onFailure = { exception ->
                _errorMessage.value = exception.message
            }
        )
    }

    /*
    * Récupérer les produits par catégorie
     */
    fun getProductsByCategory(category: String) {
        repository.getProductsByCategory(
            category,
            onSuccess = { products ->
                when (category) {
                    "Sandwich" -> _sandwichProducts.value = products
                    "Dessert" -> _dessertProducts.value = products
                    "Salade" -> _saladeProducts.value = products
                    "Boisson" -> _boissonProducts.value = products
                }
            },
            onFailure = { exception ->
                _errorMessage.value = exception.message
            }
        )
    }

    fun setCommand(id: String) {
        repository.setCommandByProductId(id)
    }

    /*
    * Mettre à jour le stock d'un produit
     */
    fun updateProductStockAndRefresh(productId: String, stockChange: Int, category: String) {
        viewModelScope.launch {
            try {
                repository.updateProductStock(productId, stockChange)
                getProductsByCategory(category) // Recharge les produits pour mettre à jour le stock
            } catch (e: Exception) {
                _errorMessage.postValue("Erreur lors de la mise à jour du stock")
            }
        }
    }

    suspend fun deleteProduct(product: Product) {
        val category = product.category;
        repository.deleteProduct(product)
        getProductsByCategory(category)
    }
}
