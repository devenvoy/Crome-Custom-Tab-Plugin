package com.demo.hybrid


import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.graphics.Color
import com.apnacomplex.common.util.views.util.CustomDatePicker
import com.demo.hybrid.databinding.ActivityDatePickerBinding
import java.util.Calendar
import java.util.Date
import androidx.core.graphics.toColorInt

class MainActivity2 : AppCompatActivity() {

    private val binding by lazy { ActivityDatePickerBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Example 1: Simple usage with default colors
        findViewById<Button>(R.id.btnSimpleDatePicker).setOnClickListener {
            val datePicker = CustomDatePicker(this)
            datePicker.show { dateMillis ->
                // Handle selected date
                dateMillis?.let {
                    val date = Date(it.timeInMillis)
                    val formatter =
                        java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault())
                    Toast.makeText(this, "Selected: ${formatter.format(date)}", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        // Example 2: Custom colors using builder pattern (Dark theme)
        findViewById<Button>(R.id.btnDarkDatePicker).setOnClickListener {
            val darkDatePicker = CustomDatePicker.builder(this)
                .setContainerColor(Color(0xFF212121))                    // Dark container
                .setTitleContentColor(Color(0xFFE0E0E0))                // Light title
                .setHeadlineContentColor(Color(0xFFE0E0E0))             // Light headline
                .setWeekdayContentColor(Color(0xFFBBBBBB))              // Gray weekdays
                .setDayContentColor(Color(0xFFE0E0E0))                  // Light day text
                .setSelectedDayContentColor(Color.White)                 // White selected text
                .setSelectedDayContainerColor(Color(0xFF00BCD4))        // Cyan selected background
                .setTodayContentColor(Color(0xFF00BCD4))                // Cyan today
                .setTodayDateBorderColor(Color(0xFF00BCD4))             // Cyan today border
                .setCurrentYearContentColor(Color(0xFF00BCD4))          // Cyan current year
                .setSelectedYearContainerColor(Color(0xFF00BCD4))       // Cyan selected year bg
                .build()

            darkDatePicker.show(
                title = "Dark Date Picker"
            ) { dateMillis ->
                dateMillis?.let {
                    val date =Date(it.timeInMillis)
                    val formatter = java.text.SimpleDateFormat(
                        "EEE, MMM dd, yyyy",
                        java.util.Locale.getDefault()
                    )
                    Toast.makeText(
                        this,
                        "Dark theme: ${formatter.format(date)}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        // Example 3: Custom colors (Purple theme)
        findViewById<Button>(R.id.btnPurpleDatePicker).setOnClickListener {
            val purpleDatePicker = CustomDatePicker.builder(this)
                .setContainerColor(Color(0xFFF3E5F5))                   // Light purple background
                .setTitleContentColor(Color(0xFF4A148C))                // Deep purple title
                .setHeadlineContentColor(Color(0xFF6A1B9A))             // Purple headline
                .setSelectedDayContainerColor(Color(0xFF9C27B0))        // Purple selected
                .setSelectedDayContentColor(Color.White)                 // White text on purple
                .setTodayContentColor(Color(0xFF9C27B0))                // Purple today
                .setTodayDateBorderColor(Color(0xFF9C27B0))             // Purple border
                .setCurrentYearContentColor(Color(0xFF9C27B0))          // Purple current year
                .setSelectedYearContainerColor(Color(0xFF9C27B0))       // Purple selected year
                .setDayInSelectionRangeContainerColor(Color(0xFFE1BEE7)) // Light purple range
                .build()

            purpleDatePicker.show(
                title = "Purple Theme Picker"
            ) { dateMillis ->
                dateMillis?.let {
                    val date = Date(it.timeInMillis)
                    val formatter =
                        java.text.SimpleDateFormat("MMMM dd, yyyy", java.util.Locale.getDefault())
                    Toast.makeText(
                        this,
                        "Purple theme: ${formatter.format(date)}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        // Example 4: Orange/Red theme
        findViewById<Button>(R.id.btnOrangeDatePicker).setOnClickListener {
            val orangeDatePicker = CustomDatePicker.builder(this)
                .setContainerColor(Color.White)                         // White background
                .setTitleContentColor(Color(0xFFFF5722))                // Orange title
                .setHeadlineContentColor(Color(0xFFFF5722))             // Orange headline
                .setSelectedDayContainerColor(Color(0xFFFF5722))        // Orange selected
                .setSelectedDayContentColor(Color.White)                 // White text
                .setTodayContentColor(Color(0xFFFF5722))                // Orange today
                .setTodayDateBorderColor(Color(0xFFFF5722))             // Orange border
                .setCurrentYearContentColor(Color(0xFFFF5722))          // Orange current year
                .setSelectedYearContainerColor(Color(0xFFFF5722))       // Orange selected year
                .setDayInSelectionRangeContainerColor(Color(0xFFFFE0B2)) // Light orange range
                .setWeekdayContentColor(Color(0xFFFF8A65))              // Light orange weekdays
                .build()

            orangeDatePicker.show(
                title = "Orange Theme"
            ) { dateMillis ->
                dateMillis?.let {
                    val date = Date(it.timeInMillis)
                    val formatter =
                        java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
                    Toast.makeText(
                        this,
                        "Orange theme: ${formatter.format(date)}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        // Example 5: Green theme with initial date
        findViewById<Button>(R.id.btnGreenDatePicker).setOnClickListener {
            // Set initial date to 30 days from now
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_MONTH, 30)
            val futureDate = calendar.timeInMillis

            val greenDatePicker = CustomDatePicker.builder(this)
                .setContainerColor(Color(0xFFE8F5E8))                   // Very light green
                .setTitleContentColor(Color(0xFF2E7D32))                // Dark green title
                .setHeadlineContentColor(Color(0xFF388E3C))             // Medium green headline
                .setSelectedDayContainerColor(Color(0xFF4CAF50))        // Green selected
                .setSelectedDayContentColor(Color.White)                 // White text
                .setTodayContentColor(Color(0xFF4CAF50))                // Green today
                .setTodayDateBorderColor(Color(0xFF4CAF50))             // Green border
                .setCurrentYearContentColor(Color(0xFF4CAF50))          // Green current year
                .setSelectedYearContainerColor(Color(0xFF4CAF50))       // Green selected year
                .setDayInSelectionRangeContainerColor(Color(0xFFC8E6C9)) // Light green range
                .setWeekdayContentColor(Color(0xFF66BB6A))              // Medium green weekdays
                .build()

            greenDatePicker.show(
                initialDateMillis = futureDate,
                title = "Future Date Picker"
            ) { dateMillis ->
                dateMillis?.let {
                    val date = Date(it.timeInMillis)
                    val formatter = java.text.SimpleDateFormat(
                        "EEE, dd MMM yyyy",
                        java.util.Locale.getDefault()
                    )
                    Toast.makeText(
                        this,
                        "Green theme: ${formatter.format(date)}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        // Example 6: Using current date as initial
        findViewById<Button>(R.id.btnCurrentDatePicker).setOnClickListener {
            val currentDatePicker = CustomDatePicker(this)
            currentDatePicker.show(
                initialDateMillis = System.currentTimeMillis(), // Today
                title = "Select Date (Today Preselected)"
            ) { dateMillis ->
                dateMillis?.let {
                    val date = Date(it.timeInMillis)
                    val formatter = java.text.SimpleDateFormat(
                        "EEEE, MMMM dd, yyyy",
                        java.util.Locale.getDefault()
                    )
                    Toast.makeText(
                        this,
                        "Current date picker: ${formatter.format(date)}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}


fun String.toColor(): Color {
    return Color(this.toColorInt())
}

// Extension function to format date easily
fun Long.toFormattedDate(pattern: String = "MMM dd, yyyy"): String {
    val date = java.util.Date(this)
    val formatter = java.text.SimpleDateFormat(pattern, java.util.Locale.getDefault())
    return formatter.format(date)
}
