package com.example.epilepsytestapp.pdf

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import com.example.epilepsytestapp.R
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

fun saveQuestionnaireAsPDF(context: Context, questionnaireData: List<Pair<String, String>>, fileName: String): File? {
    val pdfDocument = PdfDocument()
    val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
    val page = pdfDocument.startPage(pageInfo)

    val canvas = page.canvas
    val paint = Paint().apply {
        textSize = 16f
        color = android.graphics.Color.BLACK
        typeface = android.graphics.Typeface.create("candara", android.graphics.Typeface.NORMAL)
    }
    val titlePaint = Paint().apply {
        textSize = 24f
        color = android.graphics.Color.BLACK
        typeface = android.graphics.Typeface.create("berlin-sans-fb", android.graphics.Typeface.BOLD)
    }

    // Titre
    canvas.drawText("Questionnaire Post-test", 50f, 50f, titlePaint)

    var yPosition = 100f

    // Ajout des questions et réponses
    for ((index, questionAndAnswer) in questionnaireData.withIndex()) {
        val (question, answer) = questionAndAnswer
        canvas.drawText("${index + 1}. $question", 50f, yPosition, paint)
        yPosition += 20f

        // Vérification si la réponse est une valeur numérique (curseur)
        if (answer.toFloatOrNull() != null) {
            val sliderStart = 50f
            val sliderEnd = 550f
            val sliderValue = answer.toFloat()
            val sliderPosition = sliderStart + (sliderEnd - sliderStart) * (sliderValue / 5f)

            // Dessin de la ligne pour le curseur
            paint.strokeWidth = 3f
            canvas.drawLine(sliderStart, yPosition, sliderEnd, yPosition, paint)

            // Dessin du curseur
            paint.strokeWidth = 0f
            canvas.drawCircle(sliderPosition, yPosition, 8f, paint)

            // Dessin de la valeur numérique sous le curseur
            canvas.drawText("Valeur : $sliderValue", sliderPosition - 20f, yPosition + 20f, paint)
            yPosition += 40f
        } else {
            // Réponse textuelle simple
            canvas.drawText(answer, 70f, yPosition, paint)
            yPosition += 30f
        }
    }

    // Réduction et affichage du logo
    val logo = BitmapFactory.decodeResource(context.resources, R.mipmap.ic_brain_logo_foreground)
    val scaledWidth = logo.width / 2 // Réduction à 50% de la largeur
    val scaledHeight = logo.height / 2 // Réduction à 50% de la hauteur
    val scaledLogo = Bitmap.createScaledBitmap(logo, scaledWidth, scaledHeight, true)

    val logoX = (pageInfo.pageWidth - scaledWidth) / 2f
    val logoY = pageInfo.pageHeight - scaledHeight - 50f
    canvas.drawBitmap(scaledLogo, logoX, logoY, null)

    pdfDocument.finishPage(page)

    // Enregistrement du fichier
    val directory = File(context.getExternalFilesDir(null), "EpilepsyTests")
    if (!directory.exists()) directory.mkdirs()

    val file = File(directory, "$fileName.pdf")
    return try {
        pdfDocument.writeTo(FileOutputStream(file))
        pdfDocument.close()
        file
    } catch (e: IOException) {
        e.printStackTrace()
        pdfDocument.close()
        null
    }
}
