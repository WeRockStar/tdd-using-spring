package com.bank.service.internal

import com.bank.domain.InsufficientFundsException
import com.bank.domain.InvalidTimePolicy
import com.bank.domain.TransferReceipt
import com.bank.repository.AccountRepository
import com.bank.service.FeePolicy
import com.bank.service.TransferService
import org.springframework.transaction.annotation.Transactional
import java.lang.String.format

open class DefaultTransferService(private val accountRepository: AccountRepository, private val feePolicy: FeePolicy, private val timePolicy: TimePolicy) : TransferService {
    private var minimumTransferAmount = 1.00

    override fun setMinimumTransferAmount(minimumTransferAmount: Double) {
        this.minimumTransferAmount = minimumTransferAmount
    }

    @Transactional
    @Throws(InsufficientFundsException::class)
    override fun transfer(amount: Double, srcAcctId: String, dstAcctId: String): TransferReceipt {
        if (amount < minimumTransferAmount) {
            throw IllegalArgumentException(format("transfer amount must be at least $%.2f", minimumTransferAmount))
        }
        if (timePolicy.isTimeValid().not()) {
            throw InvalidTimePolicy("Not allow transfer at 22:00 - 05:59")
        }
        val receipt = TransferReceipt()

        val srcAcct = accountRepository.findById(srcAcctId)
        val dstAcct = accountRepository.findById(dstAcctId)

        receipt.setInitialSourceAccount(srcAcct)
        receipt.setInitialDestinationAccount(dstAcct)

        val fee = feePolicy.calculateFee(amount)
        if (fee > 0) {
            srcAcct.debit(fee)
        }

        receipt.transferAmount = amount
        receipt.feeAmount = fee

        srcAcct.debit(amount)
        dstAcct.credit(amount)

        accountRepository.updateBalance(srcAcct)
        accountRepository.updateBalance(dstAcct)

        receipt.finalSourceAccount = srcAcct
        receipt.finalDestinationAccount = dstAcct

        return receipt
    }
}
