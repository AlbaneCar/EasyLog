package fr.eseo.ld.android.xd.bde_cafet.model

data class Product(
    val id: String = "",
    val name: String = "",
    val category: String = "",
    val price: Double = 0.0,
    val description: String? = null,
    val stock: Int = 0
) {
    // Constructeur sans argument pour Firebase
    constructor() : this("", "", "", 0.0, "", 0)
}
