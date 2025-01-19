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

fun saveQuestionnaireAsPDF(context: Context, questionnaireData: List<String>, fileName: String): File? {
    val pdfDocument = PdfDocument()
    val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
    val page = pdfDocument.startPage(pageInfo)

    var canvas = page.canvas
    val paint = Paint().apply {
        textSize = 16f
        color = android.graphics.Color.BLACK
    }

    val titlePaint = Paint().apply {
        textSize = 24f
        color = android.graphics.Color.BLUE
        isFakeBoldText = true
    }

    val linePaint = Paint().apply {
        color = android.graphics.Color.LTGRAY
        strokeWidth = 2f
    }

    val sliderPaint = Paint().apply {
        color = android.graphics.Color.GREEN
        strokeWidth = 4f
    }

    // Titre
    canvas.drawText("Questionnaire Post-test", 10f, 40f, titlePaint)

    var yPosition = 80f
    val lineHeight = 30f
    val marginLeft = 10f
    val sliderLength = 200f

    for ((index, answer) in questionnaireData.withIndex()) {
        // Dessiner la question
        canvas.drawText("Question ${index + 1} :", marginLeft, yPosition, paint)
        yPosition += lineHeight

        // Réponse
        if (answer.startsWith("Slider:")) {
            // Dessiner un curseur pour représenter la réponse du slider
            val sliderValue = answer.removePrefix("Slider:").toFloatOrNull() ?: 0f
            val sliderPosition = marginLeft + (sliderValue / 5f) * sliderLength

            // Barre du slider
            canvas.drawLine(marginLeft, yPosition, marginLeft + sliderLength, yPosition, linePaint)
            // Position du curseur
            canvas.drawCircle(sliderPosition, yPosition, 8f, sliderPaint)
            yPosition += lineHeight
        } else {
            // Réponse textuelle ou option choisie
            canvas.drawText(answer, marginLeft + 20f, yPosition, paint)
            yPosition += lineHeight
        }

        // Ligne séparatrice
        canvas.drawLine(marginLeft, yPosition, pageInfo.pageWidth - marginLeft, yPosition, linePaint)
        yPosition += 20f

        // Gérer les débordements (nouvelle page)
        if (yPosition > pageInfo.pageHeight - 120f) {
            pdfDocument.finishPage(page)
            val newPageInfo = PdfDocument.PageInfo.Builder(595, 842, pdfDocument.pages.size + 1).create()
            val page = pdfDocument.startPage(newPageInfo)
            canvas = page.canvas
            yPosition = 40f
        }
    }

    // Ajouter le logo en bas de la dernière page
    val logoBitmap = BitmapFactory.decodeResource(context.resources, R.mipmap.ic_brain_logo_foreground)
    val logoWidth = 80
    val logoHeight = 80
    val logoX = (pageInfo.pageWidth - logoWidth) / 2f
    val logoY = pageInfo.pageHeight - 100f

    canvas.drawBitmap(
        Bitmap.createScaledBitmap(logoBitmap, logoWidth, logoHeight, true),
        logoX,
        logoY,
        null
    )

    pdfDocument.finishPage(page)

    // Créer le répertoire de destination
    val directory = File(context.getExternalFilesDir(null), "EpilepsyTests")
    if (!directory.exists()) directory.mkdirs()

    // Créer le fichier PDF
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
