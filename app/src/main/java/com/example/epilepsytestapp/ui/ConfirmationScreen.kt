import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.epilepsytestapp.R
import com.example.epilepsytestapp.ui.theme.AppTheme

@Composable
fun ConfirmationScreen(
    onStopTestConfirmed: () -> Unit,
    onCancelTest: () -> Unit
) {
    AppTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            // Titre
            Text(
                text = "Arrêter\nle test\nen cours ?",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontSize = 60.sp,
                    lineHeight = 60.sp,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 60.dp), // Titre placé plus haut
                color = MaterialTheme.colorScheme.onBackground
            )

            // Boutons d'action
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(80.dp)) // Plus d'espace entre le titre et les boutons

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly // Espacement uniforme
                ) {
                    // Bouton Oui (Check)
                    ImageClickableButton(
                        iconResId = R.mipmap.ic_check_foreground,
                        label = "Oui j'arrête\nle test",
                        onClick = onStopTestConfirmed
                    )

                    // Bouton Non (Close)
                    ImageClickableButton(
                        iconResId = R.mipmap.ic_close_foreground,
                        label = "Non je continue\nle test",
                        onClick = onCancelTest
                    )
                }
            }

            // Logo en bas
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(32.dp))
                Image(
                    painter = painterResource(id = R.mipmap.ic_brain_logo_foreground),
                    contentDescription = "Logo",
                    modifier = Modifier.size(140.dp) // Logo agrandi
                )
            }
        }
    }
}

@Composable
fun ImageClickableButton(iconResId: Int, label: String, onClick: () -> Unit) {
    Column(
        modifier = Modifier.padding(1.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Image cliquable
        Image(
            painter = painterResource(id = iconResId),
            contentDescription = label,
            modifier = Modifier
                .size(140.dp) // Taille identique pour toutes les icônes
                .clickable { onClick() }
        )
        Spacer(modifier = Modifier.height(4.dp)) // Moins d'espace entre l'image et le texte
        // Texte sous l'image
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 24.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )
        )
    }
}
