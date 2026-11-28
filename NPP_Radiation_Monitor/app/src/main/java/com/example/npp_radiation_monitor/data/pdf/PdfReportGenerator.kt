package com.example.npp_radiation_monitor.data.pdf

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import com.example.npp_radiation_monitor.data.model.RadiationReading
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Клас асинхронної генерації, збереження та відкриття PDF-звітів про стан мережі АЕС.
 */
class PdfReportGenerator(private val context: Context) {

    /**
     * Генерація локального PDF-документа з таблицею замірів та збереження у папку Downloads.
     */
    suspend fun generatePdfReport(readings: List<RadiationReading>): Result<File> {
        return withContext(Dispatchers.IO) {
            try {
                val pdfDocument = PdfDocument()
                val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
                val page = pdfDocument.startPage(pageInfo)
                val canvas: Canvas = page.canvas

                val paint = Paint().apply {
                    color = Color.BLACK
                    textSize = 14f
                    isAntiAlias = true
                }

                val titlePaint = Paint().apply {
                    color = Color.rgb(25, 118, 210)
                    textSize = 20f
                    isFakeBoldText = true
                    isAntiAlias = true
                }

                var y = 50f

                // Заголовок PDF-звіту
                canvas.drawText("Аналітичний звіт стану мережі АЕС", 40f, y, titlePaint)
                y += 30f

                val timeStr = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
                canvas.drawText("Дата формування звіту: $timeStr", 40f, y, paint)
                y += 30f

                // Заголовок таблиці
                val headerPaint = Paint().apply {
                    color = Color.DKGRAY
                    textSize = 12f
                    isFakeBoldText = true
                }
                canvas.drawText("ID | Час | Показник (мкЗв/год) | Статус безпеки", 40f, y, headerPaint)
                y += 15f

                // Роздільна лінія
                val linePaint = Paint().apply {
                    color = Color.LTGRAY
                    strokeWidth = 2f
                }
                canvas.drawLine(40f, y, 550f, y, linePaint)
                y += 20f

                // Таблиця даних замірів
                readings.forEach { reading ->
                    val rowText = String.format(
                        Locale.UK,
                        "#%d  |  %s  |  %.2f  |  %s",
                        reading.id,
                        reading.timestamp,
                        reading.value,
                        reading.status.title
                    )
                    canvas.drawText(rowText, 40f, y, paint)
                    y += 22f
                }

                pdfDocument.finishPage(page)

                // Збереження PDF-файлу у публічну папку Downloads на пристрої
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                if (!downloadsDir.exists()) {
                    downloadsDir.mkdirs()
                }
                val file = File(downloadsDir, "radiation_report_${System.currentTimeMillis()}.pdf")
                FileOutputStream(file).use { out ->
                    pdfDocument.writeTo(out)
                }
                pdfDocument.close()

                Result.success(file)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
