package com.example.nuviofrontend.feature.statistics.report

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.core.settings.CurrencyConverter
import com.example.core.statistics.dto.TransactionStatisticsData
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Calendar
import javax.inject.Inject
import kotlin.math.min
import kotlin.math.max

class StatisticsPdfReportGenerator @Inject constructor() {

    fun generate(
        context: Context,
        title: String,
        data: TransactionStatisticsData,
        currencyIndex: Int
    ): Uri {
        val locale = Locale("hr", "HR")
        val dateFormat = SimpleDateFormat("dd.MM.yyyy. HH:mm", locale)
        val createdAtText = dateFormat.format(Date())

        val pdf = PdfDocument()
        val pageWidth = 595
        val pageHeight = 842
        val margin = 44
        val topStart = margin + 22
        val bottomSafe = pageHeight - margin - 52

        val coverBg = decodeBitmap(context, com.example.core.R.drawable.background_light)
        val coverLogo = decodeBitmap(context, com.example.core.R.drawable.logo_light_full)

        val paintCoverTitle = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = 22f
            isFakeBoldText = true
        }

        val paintCoverSub = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = 11f
        }

        val paintH1 = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            textSize = 16f
            isFakeBoldText = true
        }

        val paintH2 = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            textSize = 12f
            isFakeBoldText = true
        }

        val paintText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            textSize = 11f
        }

        val paintSmall = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.DKGRAY
            textSize = 10f
        }

        val paintCardBg = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(247, 247, 247)
            style = Paint.Style.FILL
        }

        val paintCardStroke = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(215, 215, 215)
            style = Paint.Style.STROKE
            strokeWidth = 2f
        }

        val paintHeaderBg = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(235, 235, 235)
            style = Paint.Style.FILL
        }

        val paintGrid = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(220, 220, 220)
            style = Paint.Style.STROKE
            strokeWidth = 1.5f
        }

        val statuses = mapStatusesToCroatian(data)

        val totalRevenue = CurrencyConverter.convertPrice(data.totalRevenue, currencyIndex)
        val totalTransactions = data.totalTransactions.toString()
        val avgTransaction = CurrencyConverter.convertPrice(data.averageTransactionValue, currencyIndex)

        val total = data.totalTransactions.coerceAtLeast(1)
        val approved = statuses.firstOrNull { it.key == "Odobreno" }?.count ?: 0
        val approvalRate = approved.toFloat() / total.toFloat()

        val recent = tryGetRecentTransactions(data)
        android.util.Log.d("PDF_DEBUG", "Got ${recent.size} recent transactions")

        val last30 = buildLastNDaysSeries(recent, 30)
        android.util.Log.d("PDF_DEBUG", "Built series with ${last30.size} points")
        android.util.Log.d("PDF_DEBUG", "last30 isEmpty: ${last30.isEmpty()}")

        run {
            val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
            val page = pdf.startPage(pageInfo)
            val canvas = page.canvas

            if (coverBg != null) {
                drawBitmapCover(canvas, coverBg, pageWidth, pageHeight)
            } else {
                canvas.drawColor(Color.WHITE)
            }

            val rectW = 320
            val rectH = 110
            val rectX = pageWidth - margin - rectW
            val rectY = margin + 12

            val paintRect = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.argb(215, 18, 22, 30)
                style = Paint.Style.FILL
            }

            canvas.drawRoundRect(
                RectF(rectX.toFloat(), rectY.toFloat(), (rectX + rectW).toFloat(), (rectY + rectH).toFloat()),
                14f,
                14f,
                paintRect
            )

            val titleX = rectX + 18
            val titleY = rectY + 46

            canvas.drawText("Izvještaj poslovanja", titleX.toFloat(), titleY.toFloat(), paintCoverTitle)
            canvas.drawText("Datum izrade: $createdAtText", titleX.toFloat(), (titleY + 26).toFloat(), paintCoverSub)

            if (coverLogo != null) {
                val maxW = 90
                val scaled = scaleBitmapToWidth(coverLogo, maxW)
                val logoX = pageWidth - margin - scaled.width
                val logoY = pageHeight - margin - scaled.height
                canvas.drawBitmap(scaled, logoX.toFloat(), logoY.toFloat(), null)
            }

            pdf.finishPage(page)
        }

        val flow = FlowPages(
            pdf = pdf,
            pageWidth = pageWidth,
            pageHeight = pageHeight,
            margin = margin,
            topStart = topStart,
            bottomSafe = bottomSafe,
            footerPaint = paintSmall,
            createdAtText = createdAtText
        )

        flow.start(pageNumber = 2)

        val fullW = pageWidth - margin - margin
        val sectionGap = 34

        run {
            flow.ensureSpace(44 + 90)
            flow.canvas.drawText("Ključni pokazatelji (KPI)", margin.toFloat(), flow.y.toFloat(), paintH1)
            flow.y += 18

            val gap = 12
            val cardH = 74
            val cardW = (fullW - gap * 2) / 3

            flow.ensureSpace(cardH + sectionGap)

            drawKpiCard(flow.canvas, margin, flow.y, cardW, cardH, "Ukupan prihod", totalRevenue, paintCardBg, paintCardStroke)
            drawKpiCard(flow.canvas, margin + cardW + gap, flow.y, cardW, cardH, "Ukupan broj transakcija", totalTransactions, paintCardBg, paintCardStroke)
            drawKpiCard(flow.canvas, margin + (cardW + gap) * 2, flow.y, cardW, cardH, "Prosječna vrijednost", avgTransaction, paintCardBg, paintCardStroke)

            flow.y += cardH + sectionGap
        }

        run {
            flow.ensureSpace(18 + 14 + 190 + sectionGap)
            flow.canvas.drawText("Stopa odobrenja", margin.toFloat(), flow.y.toFloat(), paintH2)
            flow.y += 14

            val gaugeSize = 170
            val gaugeX = (pageWidth - gaugeSize) / 2
            drawApprovalGauge(flow.canvas, gaugeX, flow.y, gaugeSize, approvalRate)

            flow.y += gaugeSize + sectionGap
        }

        run {
            val donutSize = 140
            val donutX = (pageWidth - donutSize) / 2

            val legendPaddingTop = 34
            val expectedLegendH = 40

            flow.ensureSpace(18 + 14 + donutSize + legendPaddingTop + expectedLegendH + sectionGap)

            flow.canvas.drawText("Udio transakcija po statusu", margin.toFloat(), flow.y.toFloat(), paintH2)
            flow.y += 14

            drawDonutChart(flow.canvas, donutX, flow.y, donutSize, statuses)

            val legendStartY = flow.y + donutSize + legendPaddingTop
            val legendHeight = drawLegendFlow(
                canvas = flow.canvas,
                x = margin,
                y = legendStartY,
                maxWidth = fullW,
                statuses = statuses,
                locale = locale
            )

            flow.y = legendStartY + legendHeight + sectionGap
        }

        run {
            val last10 = recent.take(10)

            if (last10.isNotEmpty()) {
                val rowH = 28
                val tableH = rowH + last10.size * rowH

                flow.ensureSpace(18 + 14 + tableH + sectionGap)

                flow.canvas.drawText("Zadnjih 10 transakcija", margin.toFloat(), flow.y.toFloat(), paintH2)
                flow.y += 14

                val tableW = fullW

                drawTransactionsTableHeader(flow.canvas, margin, flow.y, tableW, rowH, paintHeaderBg, paintGrid)
                flow.y += rowH

                last10.forEach { tx ->
                    drawTransactionRow(
                        canvas = flow.canvas,
                        x = margin,
                        y = flow.y,
                        w = tableW,
                        h = rowH,
                        transaction = tx,
                        currencyIndex = currencyIndex,
                        paintGrid = paintGrid,
                        paintText = paintText,
                        locale = locale
                    )
                    flow.y += rowH
                }

                flow.y += sectionGap
            }
        }

        run {
            val barsW = min(480, fullW)
            val barsX = (pageWidth - barsW) / 2

            val barsTitleToChartGap = 18
            val barsRowH = 36
            val barsTopPad = 10
            val barsBottomPad = 22
            val barsH = barsTopPad + statuses.size * barsRowH + barsBottomPad

            flow.ensureSpace(18 + 14 + barsTitleToChartGap + barsH + sectionGap)

            flow.canvas.drawText("Graf: udio po statusima", margin.toFloat(), flow.y.toFloat(), paintH2)
            flow.y += 14 + barsTitleToChartGap

            drawHorizontalPercentBars(
                canvas = flow.canvas,
                x = barsX,
                y = flow.y + barsTopPad,
                w = barsW,
                rowH = barsRowH,
                statuses = statuses,
                locale = locale
            )

            flow.y += barsH + sectionGap
        }

        run {
            val chartW = min(500, fullW)
            val chartH = 220
            val chartX = (pageWidth - chartW) / 2

            val titleToChartGap = 18
            val xLabelPad = 26
            val blockH = 14 + titleToChartGap + chartH + xLabelPad

            flow.ensureSpace(18 + 14 + blockH + sectionGap)

            flow.canvas.drawText("Transakcije u zadnjih 30 dana (broj po danu)", margin.toFloat(), flow.y.toFloat(), paintH2)
            flow.y += 14 + titleToChartGap

            if (last30.isEmpty()) {
                flow.y = drawWrappedText(
                    canvas = flow.canvas,
                    text = "Nema dovoljno podataka za izradu vremenskog prikaza (zadnjih 30 dana).",
                    x = margin,
                    y = flow.y,
                    maxWidth = fullW,
                    paint = paintText,
                    lineHeight = 15
                )
                flow.y += sectionGap
            } else {
                drawAreaLineChart(
                    canvas = flow.canvas,
                    x = chartX,
                    y = flow.y,
                    w = chartW,
                    h = chartH,
                    points = last30,
                    labelEvery = 5
                )
                flow.y += chartH + xLabelPad + sectionGap
            }
        }

        flow.finish()

        val outFile = File(context.cacheDir, "izvjestaj_poslovanja_${System.currentTimeMillis()}.pdf")
        FileOutputStream(outFile).use { out ->
            pdf.writeTo(out)
        }
        pdf.close()

        return FileProvider.getUriForFile(
            context,
            context.packageName + ".fileprovider",
            outFile
        )
    }

    private class FlowPages(
        private val pdf: PdfDocument,
        private val pageWidth: Int,
        private val pageHeight: Int,
        private val margin: Int,
        private val topStart: Int,
        private val bottomSafe: Int,
        private val footerPaint: Paint,
        private val createdAtText: String
    ) {
        lateinit var canvas: Canvas
        private lateinit var page: PdfDocument.Page
        var pageNumber: Int = 2
        var y: Int = topStart

        fun start(pageNumber: Int) {
            this.pageNumber = pageNumber
            startNewPage()
        }

        fun ensureSpace(needed: Int) {
            if (y + needed > bottomSafe) {
                finishPage()
                startNewPage()
            }
        }

        fun finish() {
            finishPage()
        }

        private fun startNewPage() {
            val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
            page = pdf.startPage(pageInfo)
            canvas = page.canvas
            canvas.drawColor(Color.WHITE)
            y = topStart
        }

        private fun finishPage() {
            canvas.drawText("Stranica $pageNumber", (pageWidth - margin - 90).toFloat(), (pageHeight - 30).toFloat(), footerPaint)
            canvas.drawText("Izrada: $createdAtText", margin.toFloat(), (pageHeight - 30).toFloat(), footerPaint)
            pdf.finishPage(page)
            pageNumber += 1
        }
    }

    private data class StatusMapped(
        val key: String,
        val count: Int,
        val percentage: Double,
        val color: Int
    )

    private fun mapStatusesToCroatian(data: TransactionStatisticsData): List<StatusMapped> {
        val approved = data.statusBreakdown.firstOrNull { it.status == "APPROVED" }?.let {
            StatusMapped("Odobreno", it.count, it.percentage, Color.rgb(46, 204, 113))
        }
        val declined = data.statusBreakdown.firstOrNull { it.status == "DECLINED" }?.let {
            StatusMapped("Odbijeno", it.count, it.percentage, Color.rgb(231, 76, 60))
        }
        val pending = data.statusBreakdown.firstOrNull { it.status == "PENDING" }?.let {
            StatusMapped("Na čekanju", it.count, it.percentage, Color.rgb(52, 152, 219))
        }
        val voided = data.statusBreakdown.firstOrNull { it.status == "VOIDED" }?.let {
            StatusMapped("Stornirano", it.count, it.percentage, Color.rgb(241, 196, 15))
        }

        val ordered = listOfNotNull(approved, declined, pending, voided)
        return if (ordered.isNotEmpty()) ordered else data.statusBreakdown.map {
            val s = it.status.lowercase()
            StatusMapped(s.replaceFirstChar { c -> c.titlecase() }, it.count, it.percentage, Color.rgb(120, 120, 120))
        }
    }

    private fun decodeBitmap(context: Context, resId: Int): Bitmap? {
        return runCatching { BitmapFactory.decodeResource(context.resources, resId) }.getOrNull()
    }

    private fun scaleBitmapToWidth(bitmap: Bitmap, targetWidth: Int): Bitmap {
        val ratio = targetWidth.toFloat() / bitmap.width.toFloat()
        val targetHeight = (bitmap.height * ratio).toInt().coerceAtLeast(1)
        return Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true)
    }

    private fun drawBitmapCover(canvas: Canvas, bitmap: Bitmap, pageW: Int, pageH: Int) {
        val bw = bitmap.width
        val bh = bitmap.height
        val scale = maxOf(pageW.toFloat() / bw.toFloat(), pageH.toFloat() / bh.toFloat())
        val dw = (bw * scale).toInt()
        val dh = (bh * scale).toInt()
        val left = (pageW - dw) / 2
        val top = (pageH - dh) / 2
        val dst = Rect(left, top, left + dw, top + dh)
        canvas.drawBitmap(bitmap, null, dst, null)
    }

    private fun drawKpiCard(
        canvas: Canvas,
        x: Int,
        y: Int,
        w: Int,
        h: Int,
        label: String,
        value: String,
        bg: Paint,
        stroke: Paint
    ) {
        val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.DKGRAY
            textSize = 10f
        }
        val valuePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            textSize = 16f
            isFakeBoldText = true
        }

        canvas.drawRoundRect(RectF(x.toFloat(), y.toFloat(), (x + w).toFloat(), (y + h).toFloat()), 12f, 12f, bg)
        canvas.drawRoundRect(RectF(x.toFloat(), y.toFloat(), (x + w).toFloat(), (y + h).toFloat()), 12f, 12f, stroke)

        canvas.drawText(label, (x + 12).toFloat(), (y + 26).toFloat(), labelPaint)
        canvas.drawText(value, (x + 12).toFloat(), (y + 56).toFloat(), valuePaint)
    }

    private fun drawApprovalGauge(
        canvas: Canvas,
        x: Int,
        y: Int,
        size: Int,
        rate: Float
    ) {
        val bgRing = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(230, 230, 230)
            style = Paint.Style.STROKE
            strokeWidth = 18f
            strokeCap = Paint.Cap.ROUND
        }

        val fgRing = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(46, 204, 113)
            style = Paint.Style.STROKE
            strokeWidth = 18f
            strokeCap = Paint.Cap.ROUND
        }

        val rect = RectF(x.toFloat(), y.toFloat(), (x + size).toFloat(), (y + size).toFloat())
        canvas.drawArc(rect, -210f, 240f, false, bgRing)
        canvas.drawArc(rect, -210f, 240f * rate.coerceIn(0f, 1f), false, fgRing)

        val textTop = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.DKGRAY
            textSize = 10f
            textAlign = Paint.Align.CENTER
        }
        val textValue = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            textSize = 20f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
        }

        val pct = rate.coerceIn(0f, 1f) * 100f
        canvas.drawText("Udio odobrenih transakcija", (x + size / 2f), (y + size / 2f - 6f), textTop)
        canvas.drawText(String.format(Locale("hr", "HR"), "%.1f%%", pct), (x + size / 2f), (y + size / 2f + 20f), textValue)
    }

    private fun drawTableHeader(
        canvas: Canvas,
        x: Int,
        y: Int,
        w: Int,
        h: Int,
        col1: Int,
        col2: Int,
        bg: Paint,
        grid: Paint
    ) {
        val text = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            textSize = 11f
            isFakeBoldText = true
        }

        canvas.drawRect(x.toFloat(), y.toFloat(), (x + w).toFloat(), (y + h).toFloat(), bg)
        canvas.drawRect(x.toFloat(), y.toFloat(), (x + w).toFloat(), (y + h).toFloat(), grid)

        canvas.drawText("Status", (x + 12).toFloat(), (y + 18).toFloat(), text)
        canvas.drawText("Broj", (x + col1 + 12).toFloat(), (y + 18).toFloat(), text)
        canvas.drawText("Udio", (x + col1 + col2 + 12).toFloat(), (y + 18).toFloat(), text)
    }

    private fun drawTableRow(
        canvas: Canvas,
        x: Int,
        y: Int,
        w: Int,
        h: Int,
        col1: Int,
        col2: Int,
        v1: String,
        v2: String,
        v3: String,
        paintGrid: Paint,
        paintText: Paint
    ) {
        canvas.drawRect(x.toFloat(), y.toFloat(), (x + w).toFloat(), (y + h).toFloat(), paintGrid)
        canvas.drawText(v1, (x + 12).toFloat(), (y + 18).toFloat(), paintText)
        canvas.drawText(v2, (x + col1 + 12).toFloat(), (y + 18).toFloat(), paintText)
        canvas.drawText(v3, (x + col1 + col2 + 12).toFloat(), (y + 18).toFloat(), paintText)
    }

    private fun drawTransactionsTableHeader(
        canvas: Canvas,
        x: Int,
        y: Int,
        w: Int,
        h: Int,
        bg: Paint,
        grid: Paint
    ) {
        val text = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            textSize = 11f
            isFakeBoldText = true
        }

        canvas.drawRect(x.toFloat(), y.toFloat(), (x + w).toFloat(), (y + h).toFloat(), bg)
        canvas.drawRect(x.toFloat(), y.toFloat(), (x + w).toFloat(), (y + h).toFloat(), grid)

        val col1Width = (w * 0.25f).toInt()
        val col2Width = (w * 0.25f).toInt()
        val col3Width = (w * 0.25f).toInt()

        canvas.drawText("Datum", (x + 12).toFloat(), (y + 18).toFloat(), text)
        canvas.drawText("Iznos", (x + col1Width + 12).toFloat(), (y + 18).toFloat(), text)
        canvas.drawText("Status", (x + col1Width + col2Width + 12).toFloat(), (y + 18).toFloat(), text)
        canvas.drawText("Tip", (x + col1Width + col2Width + col3Width + 12).toFloat(), (y + 18).toFloat(), text)
    }

    private fun readField(item: Any, fieldName: String): Any? {
        return try {
            val methodName = "get${fieldName.replaceFirstChar { it.uppercase() }}"
            val method = item.javaClass.methods.firstOrNull {
                it.name == methodName && it.parameterTypes.isEmpty()
            }
            if (method != null) {
                return method.invoke(item)
            }

            val field = item.javaClass.declaredFields.firstOrNull {
                it.name == fieldName
            }
            if (field != null) {
                field.isAccessible = true
                return field.get(item)
            }
            null
        } catch (e: Exception) {
            null
        }
    }

    private fun drawTransactionRow(
        canvas: Canvas,
        x: Int,
        y: Int,
        w: Int,
        h: Int,
        transaction: RecentTx,
        currencyIndex: Int,
        paintGrid: Paint,
        paintText: Paint,
        locale: Locale
    ) {
        canvas.drawRect(x.toFloat(), y.toFloat(), (x + w).toFloat(), (y + h).toFloat(), paintGrid)

        val col1Width = (w * 0.25f).toInt()
        val col2Width = (w * 0.25f).toInt()
        val col3Width = (w * 0.25f).toInt()

        val dateFormatted = formatTransactionDate(transaction.createdAt)
        val amountFormatted = CurrencyConverter.convertPrice(transaction.amount.toDouble(), currencyIndex)
        val statusFormatted = translateStatus(transaction.status)
        val typeFormatted = translateType(transaction.type)

        canvas.drawText(dateFormatted, (x + 12).toFloat(), (y + 18).toFloat(), paintText)
        canvas.drawText(amountFormatted, (x + col1Width + 12).toFloat(), (y + 18).toFloat(), paintText)
        canvas.drawText(statusFormatted, (x + col1Width + col2Width + 12).toFloat(), (y + 18).toFloat(), paintText)
        canvas.drawText(typeFormatted, (x + col1Width + col2Width + col3Width + 12).toFloat(), (y + 18).toFloat(), paintText)
    }

    private fun translateStatus(status: String): String {
        return when (status.uppercase()) {
            "APPROVED" -> "Odobreno"
            "DECLINED" -> "Odbijeno"
            "PENDING" -> "Na čekanju"
            "VOIDED" -> "Stornirano"
            else -> status
        }
    }

    private fun translateType(type: String): String {
        return when (type.uppercase()) {
            "SALE" -> "Prodaja"
            "VOID" -> "Storno"
            "REFUND" -> "Povrat"
            else -> type
        }
    }

    private fun formatTransactionDate(createdAt: String): String {
        return try {
            val parts = createdAt.split("T")
            if (parts.size >= 2) {
                val datePart = parts[0]
                val timePart = parts[1].split(".")[0].substring(0, 5)
                val dateParts = datePart.split("-")
                if (dateParts.size == 3) {
                    "${dateParts[2]}.${dateParts[1]}. ${timePart}"
                } else {
                    createdAt.take(16)
                }
            } else {
                createdAt.take(16)
            }
        } catch (e: Exception) {
            createdAt.take(16)
        }
    }

    private fun drawDonutChart(
        canvas: Canvas,
        x: Int,
        y: Int,
        size: Int,
        statuses: List<StatusMapped>
    ) {
        val rect = RectF(x.toFloat(), y.toFloat(), (x + size).toFloat(), (y + size).toFloat())
        val total = statuses.sumOf { it.count }.coerceAtLeast(1)

        var startAngle = -90f
        statuses.forEach { s ->
            val sweep = 360f * (s.count.toFloat() / total.toFloat())
            val p = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = s.color
                style = Paint.Style.FILL
            }
            canvas.drawArc(rect, startAngle, sweep, true, p)
            startAngle += sweep
        }

        val hole = RectF(
            (x + size * 0.30f),
            (y + size * 0.30f),
            (x + size * 0.70f),
            (y + size * 0.70f)
        )
        val holePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            style = Paint.Style.FILL
        }
        canvas.drawOval(hole, holePaint)

        val centerText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            textSize = 11f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("Ukupno", (x + size / 2f), (y + size / 2f - 3f), centerText)
        centerText.isFakeBoldText = false
        centerText.textSize = 11f
        canvas.drawText(total.toString(), (x + size / 2f), (y + size / 2f + 14f), centerText)
    }

    private fun drawLegendFlow(
        canvas: Canvas,
        x: Int,
        y: Int,
        maxWidth: Int,
        statuses: List<StatusMapped>,
        locale: Locale
    ): Int {
        val legendText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.DKGRAY
            textSize = 10f
        }

        val boxSize = 10
        val itemGap = 16
        val boxTextGap = 8
        val rowGap = 16

        var cx = x
        var cy = y
        var maxY = y

        statuses.forEach { s ->
            val label = "${s.key}  ${String.format(locale, "%.2f%%", s.percentage)}"
            val textW = legendText.measureText(label).toInt()
            val itemW = boxSize + boxTextGap + textW + itemGap

            if (cx + itemW > x + maxWidth) {
                cx = x
                cy += rowGap
            }

            val boxPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = s.color
                style = Paint.Style.FILL
            }

            canvas.drawRect(cx.toFloat(), (cy - boxSize + 2).toFloat(), (cx + boxSize).toFloat(), (cy + 2).toFloat(), boxPaint)
            canvas.drawText(label, (cx + boxSize + boxTextGap).toFloat(), cy.toFloat(), legendText)

            cx += itemW
            maxY = maxOf(maxY, cy)
        }

        return (maxY - y) + rowGap
    }

    private fun drawHorizontalPercentBars(
        canvas: Canvas,
        x: Int,
        y: Int,
        w: Int,
        rowH: Int,
        statuses: List<StatusMapped>,
        locale: Locale
    ) {
        val label = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            textSize = 10.5f
            isFakeBoldText = true
        }

        val value = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.DKGRAY
            textSize = 10f
        }

        val bg = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(235, 235, 235)
            style = Paint.Style.FILL
        }

        val barYOffset = 12

        var yy = y
        statuses.forEach { s ->
            val barX = x
            val barY = yy + barYOffset
            val barW = w
            val barH = 10

            val fg = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = s.color
                style = Paint.Style.FILL
            }

            canvas.drawText(s.key, x.toFloat(), yy.toFloat(), label)

            val pctText = String.format(locale, "%.2f%%", s.percentage)
            val pctW = value.measureText(pctText)
            canvas.drawText(pctText, (x + w - pctW).toFloat(), yy.toFloat(), value)

            canvas.drawRoundRect(RectF(barX.toFloat(), barY.toFloat(), (barX + barW).toFloat(), (barY + barH).toFloat()), 6f, 6f, bg)

            val filled = (barW * (s.percentage / 100.0)).toInt()
            canvas.drawRoundRect(RectF(barX.toFloat(), barY.toFloat(), (barX + filled).toFloat(), (barY + barH).toFloat()), 6f, 6f, fg)

            yy += rowH
        }
    }

    private fun drawWrappedText(
        canvas: Canvas,
        text: String,
        x: Int,
        y: Int,
        maxWidth: Int,
        paint: Paint,
        lineHeight: Int
    ): Int {
        val words = text.split(" ")
        val lines = ArrayList<String>()
        var current = ""

        words.forEach { w ->
            val test = if (current.isEmpty()) w else "$current $w"
            if (paint.measureText(test) <= maxWidth) {
                current = test
            } else {
                if (current.isNotEmpty()) lines.add(current)
                current = w
            }
        }
        if (current.isNotEmpty()) lines.add(current)

        var yy = y
        lines.forEach {
            canvas.drawText(it, x.toFloat(), yy.toFloat(), paint)
            yy += lineHeight
        }
        return yy
    }

    data class RecentTx(
        val createdAt: String,
        val amount: Int,
        val status: String,
        val type: String
    )

    private fun tryGetRecentTransactions(data: TransactionStatisticsData): List<RecentTx> {
        android.util.Log.d("PDF_DEBUG", "Starting tryGetRecentTransactions")

        return try {
            val method = data.javaClass.methods.firstOrNull {
                it.name == "getRecentTransactions" && it.parameterTypes.isEmpty()
            }

            if (method != null) {
                android.util.Log.d("PDF_DEBUG", "Found getRecentTransactions method")
                val transactions = method.invoke(data) as? List<*>
                android.util.Log.d("PDF_DEBUG", "Method returned ${transactions?.size ?: 0} transactions")

                if (transactions.isNullOrEmpty()) {
                    android.util.Log.w("PDF_DEBUG", "No transactions returned")
                    return emptyList()
                }

                val result = transactions.mapNotNull { tx ->
                    if (tx == null) return@mapNotNull null

                    val createdAt = readCreatedAt(tx)
                    val amount = readField(tx, "amount") as? Int ?: 0
                    val status = readField(tx, "status")?.toString() ?: ""
                    val type = readField(tx, "type")?.toString() ?: ""

                    if (createdAt != null) {
                        android.util.Log.d("PDF_DEBUG", "Transaction: createdAt=$createdAt, amount=$amount, status=$status, type=$type")
                        RecentTx(createdAt, amount, status, type)
                    } else {
                        android.util.Log.w("PDF_DEBUG", "Transaction has no createdAt")
                        null
                    }
                }

                android.util.Log.d("PDF_DEBUG", "Returning ${result.size} transactions with createdAt")
                return result
            } else {
                android.util.Log.w("PDF_DEBUG", "No getRecentTransactions method found")
            }

            emptyList()
        } catch (e: Exception) {
            android.util.Log.e("PDF_GENERATOR", "Failed to get recent transactions", e)
            emptyList()
        }
    }

    private fun readCreatedAt(item: Any): String? {
        return try {
            val method = item.javaClass.methods.firstOrNull {
                it.name == "getCreatedAt" && it.parameterTypes.isEmpty()
            }
            if (method != null) {
                method.invoke(item)?.toString()
            } else {
                android.util.Log.w("PDF_DEBUG", "No getCreatedAt method found on ${item.javaClass.simpleName}")
                null
            }
        } catch (e: Exception) {
            android.util.Log.e("PDF_GENERATOR", "Failed to read createdAt", e)
            null
        }
    }

    private fun extractDateKey(createdAt: String): String? {
        val m = Regex("""\d{4}-\d{2}-\d{2}""").find(createdAt)
        return m?.value
    }

    private fun buildLastNDaysSeries(recent: List<RecentTx>, days: Int): List<Pair<String, Int>> {
        android.util.Log.d("PDF_DEBUG", "buildLastNDaysSeries: recent.size=${recent.size}, days=$days")

        if (recent.isEmpty() || days <= 1) {
            android.util.Log.d("PDF_DEBUG", "Empty or invalid input, returning empty list")
            return emptyList()
        }

        val countsByDate = HashMap<String, Int>()
        recent.forEach { tx ->
            val key = extractDateKey(tx.createdAt)
            android.util.Log.d("PDF_DEBUG", "Transaction createdAt: ${tx.createdAt}, extracted key: $key")
            if (key == null) return@forEach
            countsByDate[key] = (countsByDate[key] ?: 0) + 1
        }

        android.util.Log.d("PDF_DEBUG", "Counts by date: $countsByDate")

        if (countsByDate.isEmpty()) {
            android.util.Log.d("PDF_DEBUG", "No valid dates extracted, returning empty list")
            return emptyList()
        }

        val mostRecentKey = countsByDate.keys.maxOrNull()
        android.util.Log.d("PDF_DEBUG", "Most recent key: $mostRecentKey")

        if (mostRecentKey == null) return emptyList()

        val parts = mostRecentKey.split("-")
        if (parts.size != 3) {
            android.util.Log.d("PDF_DEBUG", "Invalid date format: $mostRecentKey")
            return emptyList()
        }

        val year = parts[0].toIntOrNull()
        val month = (parts[1].toIntOrNull() ?: return emptyList()) - 1
        val day = parts[2].toIntOrNull()

        android.util.Log.d("PDF_DEBUG", "Parsed date: year=$year, month=$month, day=$day")

        if (year == null || day == null) return emptyList()

        val cal = Calendar.getInstance(Locale("hr", "HR"))
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, month)
        cal.set(Calendar.DAY_OF_MONTH, day)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)

        val labelFmt = SimpleDateFormat("dd.MM", Locale("hr", "HR"))
        val keyFmt = SimpleDateFormat("yyyy-MM-dd", Locale.US)

        val points = ArrayList<Pair<String, Int>>()
        val tmp = cal.clone() as Calendar
        tmp.add(Calendar.DAY_OF_MONTH, -(days - 1))

        for (i in 0 until days) {
            val key = keyFmt.format(tmp.time)
            val label = labelFmt.format(tmp.time)
            val count = countsByDate[key] ?: 0
            points.add(label to count)
            tmp.add(Calendar.DAY_OF_MONTH, 1)
        }

        android.util.Log.d("PDF_DEBUG", "Generated ${points.size} data points")
        android.util.Log.d("PDF_DEBUG", "Sample points: ${points.take(5)}")

        return points
    }

    private fun drawAreaLineChart(
        canvas: Canvas,
        x: Int,
        y: Int,
        w: Int,
        h: Int,
        points: List<Pair<String, Int>>,
        labelEvery: Int
    ) {
        val grid = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(225, 225, 225)
            style = Paint.Style.STROKE
            strokeWidth = 1.5f
        }

        val axis = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(200, 200, 200)
            strokeWidth = 2f
        }

        val line = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(52, 152, 219)
            style = Paint.Style.STROKE
            strokeWidth = 3f
        }

        val fill = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(55, 52, 152, 219)
            style = Paint.Style.FILL
        }

        val label = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.DKGRAY
            textSize = 9.5f
        }

        val maxVal = max(1, points.maxOfOrNull { it.second } ?: 1)
        val left = x
        val top = y
        val right = x + w
        val bottom = y + h

        val gridLines = 4
        for (i in 0..gridLines) {
            val yy = top + (h * i / gridLines.toFloat())
            canvas.drawLine(left.toFloat(), yy, right.toFloat(), yy, grid)
        }

        canvas.drawLine(left.toFloat(), bottom.toFloat(), right.toFloat(), bottom.toFloat(), axis)

        val stepX = w / (points.size - 1).toFloat()

        val coords = ArrayList<Pair<Float, Float>>()
        points.forEachIndexed { i, p ->
            val px = left + stepX * i
            val py = bottom - (h * (p.second / maxVal.toFloat()))
            coords.add(px to py)
        }

        val areaPath = android.graphics.Path()
        areaPath.moveTo(coords.first().first, bottom.toFloat())
        coords.forEach { (px, py) -> areaPath.lineTo(px, py) }
        areaPath.lineTo(coords.last().first, bottom.toFloat())
        areaPath.close()
        canvas.drawPath(areaPath, fill)

        val linePath = android.graphics.Path()
        linePath.moveTo(coords.first().first, coords.first().second)
        coords.drop(1).forEach { (px, py) -> linePath.lineTo(px, py) }
        canvas.drawPath(linePath, line)

        val dot = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(52, 152, 219)
            style = Paint.Style.FILL
        }

        coords.forEachIndexed { i, (px, py) ->
            canvas.drawCircle(px, py, 4.2f, dot)

            val shouldLabel = (labelEvery <= 1) || (i % labelEvery == 0) || (i == points.lastIndex)
            if (shouldLabel) {
                val t = points[i].first
                val tw = label.measureText(t)
                canvas.drawText(t, (px - tw / 2f), (bottom + 16).toFloat(), label)
            }
        }

        val valuePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.DKGRAY
            textSize = 9.5f
        }
        canvas.drawText("0", (left - 2).toFloat(), (bottom - 2).toFloat(), valuePaint)
        canvas.drawText(maxVal.toString(), (left - 2).toFloat(), (top + 10).toFloat(), valuePaint)
    }
}