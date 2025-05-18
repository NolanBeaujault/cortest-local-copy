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
    finalTimeInSeconds: Int, // Temps final du test
    motCode: String
): File? {
    val pdfDocument = PdfDocument()
    val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
    var page = pdfDocument.startPage(pageInfo)
    var canvas = page.canvas

    Log.d("SaveTestInstructions", "Instruction log : $instructionsLog")

    val titlePaint = Paint().apply {
        textSize = AppTypography.displayLarge.fontSize.value
        color = TextColor.hashCode()
        typeface = Typeface.create("sans-serif", Typeface.BOLD)
    }

    val textPaint = Paint().apply {
        textSize = AppTypography.bodyLarge.fontSize.value
        color = TextColor.hashCode()
        typeface = Typeface.create("sans-serif", Typeface.NORMAL)
    }

    val motCodeText = "Mot code : $motCode"
    val motCodeTextWidth = textPaint.measureText(motCodeText)
    val xPositionMotCode = pageInfo.pageWidth - motCodeTextWidth - 50f // Marge de 50 à droite
    canvas.drawText(motCodeText, xPositionMotCode, 60f, textPaint)


    // Titre du document
    canvas.drawText("Déroulé du Test", 50f, 50f, titlePaint)

    var yPosition = 100f
    val maxPageHeight = 800f

    for ((instruction, timeInSeconds) in instructionsLog) {
        val formattedTime = formatTime(timeInSeconds)

        val isImageInstruction = instruction.startsWith(" \uD83D\uDDBC Image choisie au hasard:") ||
                instruction.startsWith(" \uD83D\uDDBC Image cliquée:")

        if (isImageInstruction) {
            val imageName = instruction.substringAfter(":").trim()

            // Charger l'image uniquement, ne pas afficher le nom
            Log.d("SaveTestInstructions", "Recherche de l'image : ${imageName}_foreground dans mipmap")

            val resId = context.resources.getIdentifier("${imageName}_foreground", "mipmap", context.packageName)
            if (resId != 0) {
                val bitmap = BitmapFactory.decodeResource(context.resources, resId)
                if (bitmap != null) {
                    val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 80, 80, true)

                    // Vérifier la place
                    if (yPosition + 80f > maxPageHeight) {
                        pdfDocument.finishPage(page)
                        page = pdfDocument.startPage(pageInfo)
                        canvas = page.canvas
                        canvas.drawText("Suite du Test", 50f, 50f, titlePaint)
                        yPosition = 100f
                    }

                    // Réduire l'espace avant l'image
                    yPosition += 10f
                    canvas.drawBitmap(scaledBitmap, 70f, yPosition, null)
                    yPosition += 90f // 80px image + 10px marge dessous
                } else {
                    canvas.drawText("\t[Image introuvable]", 50f, yPosition, textPaint)
                    yPosition += 30f
                }
            } else {
                canvas.drawText("\t[Image introuvable]", 50f, yPosition, textPaint)
                yPosition += 30f
            }
        } else {
            // Instruction normale
            canvas.drawText("\t$instruction ($formattedTime)", 50f, yPosition, textPaint)
            yPosition += 30f
        }

        if (yPosition > maxPageHeight) {
            pdfDocument.finishPage(page)
            page = pdfDocument.startPage(pageInfo)
            canvas = page.canvas
            canvas.drawText("Suite du Test", 50f, 50f, titlePaint)
            yPosition = 100f
        }
    }

    // Espace avant le temps final
    yPosition += 30f

    val formattedFinalTime = formatTime(finalTimeInSeconds)
    canvas.drawText("Temps Final du Test: $formattedFinalTime", 50f, yPosition, textPaint)

    // Logo en bas du PDF
    val logo = BitmapFactory.decodeResource(context.resources, R.mipmap.ic_brain_logo_foreground)
    if (logo != null) {
        val scaledLogo = Bitmap.createScaledBitmap(logo, 100, 100, true)
        val logoX = (pageInfo.pageWidth - 100) / 2f
        val logoY = pageInfo.pageHeight - 150f
        canvas.drawBitmap(scaledLogo, logoX, logoY, null)
    } else {
        Log.e("SaveTestInstructions", "Erreur : logo introuvable")
    }

    pdfDocument.finishPage(page)

    val formatter = SimpleDateFormat("yyyy-MM-dd_HH:mm", Locale.getDefault())
    val formattedDate = formatter.format(Date())
    val fileName = "Instructions_$formattedDate.pdf"

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
