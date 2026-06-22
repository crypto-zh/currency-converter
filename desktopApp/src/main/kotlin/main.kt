import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import java.awt.Dimension
import io.github.currencyconverter.App
import io.github.currencyconverter.di.getCommonModules
import org.koin.core.context.startKoin

fun main() = application {
    startKoin {
        modules(getCommonModules())
    }

    Window(
        title = "Currency Converter",
        state = rememberWindowState(width = 800.dp, height = 600.dp),
        onCloseRequest = ::exitApplication,
    ) {
        window.minimumSize = Dimension(350, 600)
        App()
    }
}

