package com.example.epilepsytestapp.ui

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.epilepsytestapp.R
import com.example.epilepsytestapp.savefiles.mergeVideos
import com.example.epilepsytestapp.ui.theme.AppTheme
import kotlinx.coroutines.launch

@Composable
fun ConfirmationScreen(
    navController: NavHostController,
    recordedVideos: MutableList<String>,
    onStopTestConfirmed: () -> Unit,
    onCancelTest: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    AppTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            // Title
            Text(
                text = "Arrêter\nle test\nen cours ?",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontSize = 60.sp,
                    lineHeight = 60.sp,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 60.dp),
                color = MaterialTheme.colorScheme.onBackground
            )

            // Action buttons
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(80.dp))

                Row(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    ImageClickableButton(
                        iconResId = R.mipmap.ic_check_foreground,
                        label = "Oui j'arrête\nle test",
                        onClick = {
                            coroutineScope.launch {
                                // Fusionner les vidéos enregistrées
                                val mergedVideoPath = mergeVideos(context, recordedVideos)
                                Log.d("ConfirmationScreen", "Vidéo fusionnée : $mergedVideoPath")

                                // Vider la liste recordedVideos après la fusion
                                recordedVideos.clear()

                                onStopTestConfirmed()
                            }
                        }
                    )
                    Spacer(modifier = Modifier.width(16.dp)) // Space between buttons
                    ImageClickableButton(
                        iconResId = R.mipmap.ic_close_foreground,
                        label = "Non je continue\nle test",
                        onClick = onCancelTest
                    )
                }
            }

            // Bottom logo
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
                    modifier = Modifier.size(140.dp)
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
        Image(
            painter = painterResource(id = iconResId),
            contentDescription = label,
            modifier = Modifier
                .size(140.dp)
                .clickable { onClick() }
        )
        Spacer(modifier = Modifier.height(4.dp))
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

@Composable
fun ImageClickable(
    imageResId: Int,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Image(
        painter = painterResource(id = imageResId),
        contentDescription = contentDescription,
        modifier = modifier
            .size(180.dp)
            .padding(6.dp)
            .clickable(onClick = onClick)
    )
}

