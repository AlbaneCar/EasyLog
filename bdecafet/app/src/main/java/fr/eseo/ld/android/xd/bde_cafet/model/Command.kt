package fr.eseo.ld.android.xd.bde_cafet.model

data class Command(
    val id: String = "",
    val products: Product = Product(),  // Assurez-vous que Product a un constructeur sans argument
    val date: String = ""
)
