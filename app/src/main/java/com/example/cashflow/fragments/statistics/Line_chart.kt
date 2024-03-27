package com.example.cashflow.fragments.statistics

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.cashflow.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

import com.example.cashflow.dataClass.*
import com.example.cashflow.db.*

class Line_chart(private val readSQL: ReadSQL, private val writeSQL: WriteSQL)  : Fragment() {
    private var openStartDatePickerButton: Button? = null
    private var openEndDatePickerButton: Button? = null
    private var startDateTextView: TextView? = null
    private var endDateTextView: TextView? = null
    private var startDate: Calendar? = null
    private var endDate: Calendar? = null
    private var lineChart: LineChart? = null

    private var accounts: ArrayList<Account>? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_line_chart, container, false)
        openStartDatePickerButton = view.findViewById(R.id.openStartDatePickerButton)
        openEndDatePickerButton = view.findViewById(R.id.openEndDatePickerButton)
        startDateTextView = view.findViewById(R.id.startDateTextView)
        endDateTextView = view.findViewById(R.id.endDateTextView)
        lineChart = view.findViewById(R.id.lineChart)

        // Calcola le date iniziali e finali
        startDate = Calendar.getInstance()
        startDate?.add(Calendar.DAY_OF_MONTH, -6)
        endDate = Calendar.getInstance()
        endDate?.add(Calendar.DAY_OF_MONTH, 1)
        startDateTextView?.setText(formatDateKey(startDate))
        endDateTextView?.setText(formatDateKey(endDate))
        openStartDatePickerButton?.setOnClickListener(View.OnClickListener { openStartDatePicker() })
        openEndDatePickerButton?.setOnClickListener(View.OnClickListener { openEndDatePicker() })

        accounts = readSQL.getAccounts()

        createLineChart()
        return view
    }

    private fun openStartDatePicker() {
        val year = startDate!![Calendar.YEAR]
        val month = startDate!![Calendar.MONTH]
        val day = startDate!![Calendar.DAY_OF_MONTH]
        val datePickerDialog = DatePickerDialog(requireContext(), { view, year, month, dayOfMonth ->
            val selectedDate = Calendar.getInstance()
            selectedDate[year, month] = dayOfMonth
            if (selectedDate.before(endDate)) {
                startDate!![year, month] = dayOfMonth
                // Imposta la data selezionata nel TextView
                startDateTextView!!.text = formatDateKey(startDate)
            }
        }, year, month, day)
        datePickerDialog.show()
        createLineChart()

    }

    private fun openEndDatePicker() {
        val year = endDate!![Calendar.YEAR]
        val month = endDate!![Calendar.MONTH]
        val day = endDate!![Calendar.DAY_OF_MONTH]
        val datePickerDialog = DatePickerDialog(requireContext(), { view, year, month, dayOfMonth ->
            val selectedDate = Calendar.getInstance()
            selectedDate[year, month] = dayOfMonth
            if (selectedDate.after(startDate)) {
                endDate!![year, month] = dayOfMonth
                // Imposta la data selezionata nel TextView
                endDateTextView!!.text = formatDateKey(endDate)
            }
        }, year, month, day)
        datePickerDialog.show()
        createLineChart()
    }

    private fun createLineChart() {
        val entries = generateDataEntries(startDate, endDate)

        // Crea un elenco di etichette delle date per l'asse X
        val dateLabels = ArrayList<String>()
        val date = startDate!!.clone() as Calendar
        while (date.compareTo(endDate) <= 0) {
            dateLabels.add(formatDateKeyWithoutYear(date))
            date.add(Calendar.DAY_OF_MONTH, 1)
        }
        val dataSet = LineDataSet(entries, "Daily Total")
        dataSet.axisDependency = YAxis.AxisDependency.LEFT
        dataSet.setColor(Color.parseColor("#00796B"))
        dataSet.valueTextSize = 12f
        dataSet.setLineWidth(2f)
        dataSet.valueTypeface = Typeface.DEFAULT_BOLD
        val lineData = LineData(dataSet)
        val xAxis = lineChart!!.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM

        // Configura il formatter per le etichette dell'asse X
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                val index = value.toInt()
                return if (index >= 0 && index < dateLabels.size) {
                    dateLabels[index]
                } else {
                    ""
                }
            }
        }
        val yAxis = lineChart!!.axisLeft
        yAxis.setGranularity(1f)
        lineChart!!.setData(lineData)
        lineChart!!.legend.isEnabled = false
        lineChart!!.invalidate()
    }

    private fun generateDataEntries(startDate: Calendar?, endDate: Calendar?): ArrayList<Entry> {
        val entries = ArrayList<Entry>()

        // Inizializza il saldo iniziale con 0
        var initialBalance = 0.0

        // Copia la data di inizio in una variabile temporanea per usarla durante l'iterazione
        val currentDate = startDate!!.clone() as Calendar

        while (currentDate.compareTo(endDate) <= 0) {
            val dayKey = formatDateKeyWithoutYear(currentDate)

            var dailyTotal = 0.0

            for (account in accounts!!) {
                // Itera su tutte le transazioni all'interno di ciascun account
                for (transaction in readSQL.getTransactionsByAccountId(account.id)) {
                    val transactionDate = transaction.date
                    if (isSameDay(transactionDate, currentDate)) {
                        dailyTotal += transaction.amountValue // Utilizza amountValue per ottenere l'importo della transazione
                    }
                }
            }

            // Calcola il saldo per questa data come saldo iniziale + totale delle transazioni
            val dailyBalance = initialBalance + dailyTotal

            // Aggiungi il saldo al grafico
            entries.add(Entry((entries.size + 1).toFloat(), dailyBalance.toFloat()))

            // Aggiorna il saldo iniziale per il prossimo giorno
            initialBalance = dailyBalance

            // Vai alla data successiva
            currentDate.add(Calendar.DAY_OF_MONTH, 1)
        }
        return entries
    }

    // Funzione per verificare se due date sono dello stesso giorno
    private fun isSameDay(date1: Calendar, date2: Calendar): Boolean {
        return date1[Calendar.YEAR] == date2[Calendar.YEAR] && date1[Calendar.MONTH] == date2[Calendar.MONTH] && date1[Calendar.DAY_OF_MONTH] == date2[Calendar.DAY_OF_MONTH]
    }

    private fun formatDateKey(date: Calendar?): String {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.US)
        return dateFormat.format(date!!.time)
    }

    private fun formatDateKeyWithoutYear(date: Calendar): String {
        val dateFormat = SimpleDateFormat("dd-MM", Locale.US)
        return dateFormat.format(date.time)
    }
}
