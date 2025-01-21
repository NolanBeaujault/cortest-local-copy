package com.example.epilepsytestapp.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.epilepsytestapp.MainActivity
import com.example.epilepsytestapp.R

class TestAppWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            val views = createWidgetRemoteViews(context)
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    private fun createWidgetRemoteViews(context: Context): RemoteViews {
        // Intent pour lancer l'activité principale
        val intent = Intent(context, MainActivity::class.java).apply {
            putExtra("startScreen", "test")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        // Charger la mise en page du widget
        val remoteViews = RemoteViews(context.packageName, R.layout.widget_layout)

        // Configurer l'action de clic sur le conteneur principal
        remoteViews.setOnClickPendingIntent(R.id.widget_container, pendingIntent)

        // Définir les textes et les styles pour le widget
        remoteViews.setTextViewText(R.id.widget_text, context.getString(R.string.widget_text)) // Texte principal
        remoteViews.setTextColor(R.id.widget_text, context.getColor(R.color.button_text_color)) // Couleur du texte

        // (Optionnel) Personnaliser les icônes si nécessaire
        // Par exemple : changer dynamiquement l'icône ou la flèche si un état spécifique est requis

        return remoteViews
    }
}
