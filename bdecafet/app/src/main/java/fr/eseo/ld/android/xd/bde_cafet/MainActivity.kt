package fr.eseo.ld.android.xd.bde_cafet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import fr.eseo.ld.android.xd.bde_cafet.ui.BdeCafetApp
import fr.eseo.ld.android.xd.bde_cafet.ui.theme.BdecafetTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BdecafetTheme {
                BdeCafetApp()
            }
        }
    }
}