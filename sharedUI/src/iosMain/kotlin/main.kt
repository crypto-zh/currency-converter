import androidx.compose.ui.window.ComposeUIViewController
import io.github.currencyconverter.App
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController = ComposeUIViewController { 
    App()
}