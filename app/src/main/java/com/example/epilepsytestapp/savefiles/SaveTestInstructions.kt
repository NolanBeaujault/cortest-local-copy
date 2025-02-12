package com.example.epilepsytestapp.savefiles

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.util.Log
import com.example.epilepsytestapp.R
import com.example.epilepsytestapp.ui.theme.AppTypography
import com.example.epilepsytestapp.ui.theme.TextColor
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun saveTestInstructionsAsPDF(
    context: Context,
    instructionsLog: List<Pair<String, Int>>, // (Consigne, Temps en secondes)
    finalTimeInSeconds: Int // Temps final du test
): File? {
    val pdfDocument = PdfDocument()
    val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
    val page = pdfDocument.startPage(pageInfo)

    val canvas = page.canvas

    // Peinture pour le titre
    val titlePaint = Paint().apply {
        textSize = AppTypography.displayLarge.fontSize.value
        color = TextColor.hashCode()
        typeface = Typeface.create("sans-serif", Typeface.BOLD)
    }

    // Peinture pour le texte normal
    val textPaint = Paint().apply {
        textSize = AppTypography.bodyLarge.fontSize.value
        color = TextColor.hashCode()
        typeface = Typeface.create("sans-serif", Typeface.NORMAL)
    }

    // Titre du document
    canvas.drawText("Déroulé du Test", 50f, 50f, titlePaint)

    var yPosition = 100f

    // Ajout des consignes et leur temps
    for ((instruction, timeInSeconds) in instructionsLog) {
        val formattedTime = formatTime(timeInSeconds)
        canvas.drawText("• $instruction ($formattedTime)", 50f, yPosition, textPaint)
        yPosition += 30f
    }

    // Ajouter un espace avant le temps final pour que ce soit bien séparé
    yPosition += 30f

    // Format du temps final du test
    val formattedFinalTime = formatTime(finalTimeInSeconds)

    // Ajouter le temps final à la fin
    canvas.drawText("Temps Final du Test: $formattedFinalTime", 50f, yPosition, textPaint)

    // Ajout du logo en bas du PDF
    val logo = BitmapFactory.decodeResource(context.resources, R.mipmap.ic_brain_logo_foreground)
    val scaledLogo = Bitmap.createScaledBitmap(logo, 100, 100, true)

    val logoX = (pageInfo.pageWidth - 100) / 2f
    val logoY = pageInfo.pageHeight - 150f
    canvas.drawBitmap(scaledLogo, logoX, logoY, null)

    pdfDocument.finishPage(page)

    // Création du nom de fichier avec la date et l'heure actuelles
    val dateTimeFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
    val dateTime = dateTimeFormat.format(Date())
    val fileName = "test_instructions_$dateTime.pdf"

    // Dossier de sauvegarde
    val directory = File(context.getExternalFilesDir(null), "EpilepsyTests/Consignes")
    if (!directory.exists()) directory.mkdirs()

    val file = File(directory, fileName)
    return try {
        pdfDocument.writeTo(FileOutputStream(file))
        pdfDocument.close()
        Log.d("SaveTestInstructions", "PDF enregistré : ${file.absolutePath}")
        file
    } catch (e: IOException) {
        Log.e("SaveTestInstructions", "Erreur lors de la sauvegarde du PDF", e)
        pdfDocument.close()
        null
    }
}



// Fonction pour formater le temps en hh:mm:ss
fun formatTime(seconds: Int): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val sec = seconds % 60
    return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, sec)
}
