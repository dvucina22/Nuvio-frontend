package com.example.core.sale.dto

import java.time.Instant
import java.util.UUID

data class SaleTransaction(
    val ID: Long,
    val UserID: UUID,
    val BankCardID: String?,
    val Type: String,
    val Status: String,
    val PANMasked: String,
    val CardFirstDigit: String,
    val CardExpirationYY: String,
    val CardExpirationMM: String,
    val ProcessingCode: String,
    val Amount: Long,
    val CurrencyCode: String,
    val STAN: String,
    val TransactionTime: String,
    val TransactionDate: String,
    val RRN: String,
    val TerminalTID: String,
    val MerchantMID: String,
    val HostType: String,
    val OriginalTransactionID: Long?,
    val RequestPayload: String,
    val CreatedAt: Instant,
    val UpdatedAt: Instant
)
