package ru.movista.presentation.custom

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime
import org.threeten.bp.ZoneId

class DateTimePicker(
    context: Context,
    private val dataSetListener: (LocalDateTime) -> Unit,
    private val cancelListener: (() -> Unit)? = null,
    isShowTImePicker: Boolean = false,
    isLimitToCurrentTime: Boolean = true
) {
    private val datePickerDialog: DatePickerDialog
    private val timePickerDialog: TimePickerDialog
    private lateinit var selectedDate: LocalDate
    private lateinit var selectedTime: LocalTime

    init {
        val today = LocalDateTime.now()
        datePickerDialog = DatePickerDialog(
            context,
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                selectedDate = LocalDate.of(year, month + 1, dayOfMonth)

                if (isShowTImePicker) {
                    showDatePickerTIme()
                } else {
                    dataSetListener.invoke(selectedDate.atStartOfDay())
                }
            },
            today.year,
            today.monthValue - 1,
            today.dayOfMonth
        ).apply {
            setCancelable(false)
        }

        if (isLimitToCurrentTime) {
            datePickerDialog.datePicker.minDate = today.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        }

        timePickerDialog = TimePickerDialog(
            context,
            TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                selectedTime = LocalTime.of(
                    hourOfDay,
                    minute
                )

                dataSetListener.invoke(LocalDateTime.of(selectedDate, selectedTime))
            },
            today.hour,
            today.minute,
            true
        ).apply {
            setCancelable(false)
        }
    }

    fun show() {
        datePickerDialog.apply {
            show()
            getButton(DialogInterface.BUTTON_NEGATIVE)?.setOnClickListener {
                cancel()
                cancelListener?.invoke()
            }
        }
    }

    private fun showDatePickerTIme() {
        timePickerDialog.apply {
            show()
            getButton(DialogInterface.BUTTON_NEGATIVE)?.setOnClickListener {
                cancelListener?.invoke()
                timePickerDialog.cancel()
            }
        }
    }
}