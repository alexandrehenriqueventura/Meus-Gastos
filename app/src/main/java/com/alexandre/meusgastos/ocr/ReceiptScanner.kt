package com.alexandre.meusgastos.ocr

import android.net.Uri
import android.content.Context
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.regex.Pattern

/**
 * Resultado da leitura de um cupom/nota fiscal.
 * `confidence` indica se os valores foram encontrados com segurança (o usuário sempre revisa antes de salvar).
 */
data class ReceiptScanResult(
    val rawText: String,
    val merchantGuess: String?,
    val totalGuess: Double?,
    val dateGuess: LocalDate?,
    val confidence: ScanConfidence
)

enum class ScanConfidence { HIGH, PARTIAL, LOW }

class ReceiptScanner(private val context: Context) {

    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    // Termos usados por cupons fiscais brasileiros (CF-e, NFC-e) para indicar o valor total
    private val totalKeywords = listOf(
        "valor total", "total a pagar", "total r\\$", "valor a pagar", "total geral", "^total$"
    )
    private val totalLinePattern = Pattern.compile(
        "(?i)(${totalKeywords.joinToString("|")}).*?([0-9]{1,3}(?:\\.[0-9]{3})*,[0-9]{2})"
    )
    // Fallback: qualquer valor em formato R$ 0.000,00 na linha inteira do texto
    private val genericValuePattern = Pattern.compile("R\\$\\s*([0-9]{1,3}(?:\\.[0-9]{3})*,[0-9]{2})")
    private val datePattern = Pattern.compile("([0-3]?[0-9])[/\\-.]([0-1]?[0-9])[/\\-.](20[0-9]{2})")

    suspend fun scan(imageUri: Uri): ReceiptScanResult {
        val image = InputImage.fromFilePath(context, imageUri)
        val visionText = recognizer.process(image).await()
        val rawText = visionText.text

        val total = extractTotal(rawText)
        val date = extractDate(rawText)
        val merchant = extractMerchant(rawText)

        val confidence = when {
            total != null && date != null -> ScanConfidence.HIGH
            total != null || date != null -> ScanConfidence.PARTIAL
            else -> ScanConfidence.LOW
        }

        return ReceiptScanResult(rawText, merchant, total, date, confidence)
    }

    private fun extractTotal(text: String): Double? {
        val matcher = totalLinePattern.matcher(text)
        if (matcher.find()) return parseBrl(matcher.group(2))

        // Sem palavra-chave reconhecida: usa o maior valor em R$ encontrado no texto
        // (heurística razoável, já que o total costuma ser o maior número do cupom)
        val generic = genericValuePattern.matcher(text)
        var maxValue: Double? = null
        while (generic.find()) {
            val value = parseBrl(generic.group(1))
            if (value != null && (maxValue == null || value > maxValue!!)) maxValue = value
        }
        return maxValue
    }

    private fun extractDate(text: String): LocalDate? {
        val matcher = datePattern.matcher(text)
        if (!matcher.find()) return null
        return try {
            val day = matcher.group(1)!!.padStart(2, '0')
            val month = matcher.group(2)!!.padStart(2, '0')
            val year = matcher.group(3)
            LocalDate.parse("$day/$month/$year", DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        } catch (e: Exception) {
            null
        }
    }

    private fun extractMerchant(text: String): String? {
        // Em cupons brasileiros, o nome do estabelecimento normalmente é a primeira linha "cheia"
        // (ignora linhas curtas ou compostas só de números/CNPJ)
        return text.lineSequence()
            .map { it.trim() }
            .firstOrNull { line ->
                line.length in 4..40 &&
                    !line.any { it.isDigit() } == false && // pode ter dígitos (ex: "Padaria 2 Irmãos")
                    line.count { it.isLetter() } >= 4
            }
    }

    private fun parseBrl(raw: String?): Double? {
        if (raw == null) return null
        return try {
            raw.replace(".", "").replace(",", ".").toDouble()
        } catch (e: Exception) {
            null
        }
    }
}
