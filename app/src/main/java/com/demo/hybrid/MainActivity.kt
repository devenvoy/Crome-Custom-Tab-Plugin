package com.demo.hybrid


import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.graphics.Color
import com.apnacomplex.common.util.views.util.CustomTimePicker
import com.demo.hybrid.databinding.ActivityMainButtonsBinding
import java.util.Calendar.HOUR_OF_DAY
import java.util.Calendar.MINUTE

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainButtonsBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Example 1: Simple usage with default colors
        binding.btnSimpleTimePicker.setOnClickListener {
            val timePicker = CustomTimePicker(this)
            timePicker.show { cal ->
                // Handle selected time
                Toast.makeText(
                    this,
                    "Selected: ${cal.get(HOUR_OF_DAY)}:${
                        cal.get(MINUTE).toString().padStart(2, '0')
                    }",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // Example 2: Custom colors using builder pattern
        binding.btnCustomTimePicker.setOnClickListener {
            val customTimePicker = CustomTimePicker.builder(this)
                .setClockDialColor(Color(0xFF2196F3))           // Blue background
                .setSelectorColor(Color(0xFFFF5722))            // Orange selector/handle
                .setContainerColor(Color(0xFF303030))           // Dark container
                .setTitleColor(Color.White)                     // White title
                .setClockDialSelectedContentColor(Color.White)   // White selected numbers
                .setClockDialUnselectedContentColor(Color(0xFFBBBBBB)) // Gray unselected numbers
                .setPeriodSelectorSelectedContainerColor(Color(0xFFFF5722)) // Orange AM/PM selected
                .setPeriodSelectorUnselectedContainerColor(Color.Transparent) // Transparent AM/PM unselected
                .setPeriodSelectorSelectedContentColor(Color.White) // White AM/PM selected text
                .setPeriodSelectorUnselectedContentColor(Color(0xFFBBBBBB)) // Gray AM/PM unselected text
                .build()

            customTimePicker.show(
                initialHour = 14,
                initialMinute = 30,
                is24Hour = false,
                title = "Pick Your Time"
            ) { cal ->
                Toast.makeText(
                    this,
                    "Selected: ${cal.get(HOUR_OF_DAY)}:${
                        cal.get(MINUTE).toString().padStart(2, '0')
                    }",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // Example 3: Dark theme time picker
        binding.btnDarkTimePicker.setOnClickListener {
            val darkTimePicker = CustomTimePicker.builder(this)
                .setClockDialColor(Color(0xFF424242))           // Dark gray dial
                .setSelectorColor(Color(0xFF00BCD4))            // Cyan selector
                .setContainerColor(Color(0xFF212121))           // Very dark container
                .setTitleColor(Color(0xFFE0E0E0))              // Light gray title
                .setClockDialSelectedContentColor(Color.Black)  // Black text on cyan
                .setClockDialUnselectedContentColor(Color(0xFF9E9E9E)) // Gray unselected
                .setPeriodSelectorSelectedContainerColor(Color(0xFF00BCD4)) // Cyan AM/PM
                .setPeriodSelectorBorderColor(Color(0xFF00BCD4)) // Cyan border
                .build()

            darkTimePicker.show(
                is24Hour = true,
                title = "Dark Time Picker"
            ) { cal ->
                Toast.makeText(
                    this,
                    "Selected: ${cal.get(HOUR_OF_DAY)}:${
                        cal.get(MINUTE).toString().padStart(2, '0')
                    }",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // Example 4: Material You style colors
        binding.btnMaterialYouTimePicker.setOnClickListener {
            val materialYouPicker = CustomTimePicker.builder(this)
                .setClockDialColor(Color(0xFFF3E5F5))           // Light purple
                .setSelectorColor(Color(0xFF9C27B0))            // Purple selector
                .setContainerColor(Color(0xFFFFFBFE))           // Almost white
                .setTitleColor(Color(0xFF1C1B1F))              // Near black
                .setClockDialSelectedContentColor(Color.White)
                .setClockDialUnselectedContentColor(Color(0xFF49454F))
                .setPeriodSelectorSelectedContainerColor(Color(0xFF9C27B0))
                .setPeriodSelectorUnselectedContainerColor(Color(0xFFF7F2FA))
                .setTimeSelectorSelectedContainerColor(Color(0xFFE8DEF8))
                .build()

            materialYouPicker.show(
                title = "Material You Style"
            ) { cal ->
                Toast.makeText(
                    this,
                    "Selected: ${cal.get(HOUR_OF_DAY)}:${
                        cal.get(MINUTE).toString().padStart(2, '0')
                    }",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // Example 5: Using with specific initial time
        binding.btnPresetTimePicker.setOnClickListener {
            val presetTimePicker = CustomTimePicker(this)
            presetTimePicker.show(
                initialHour = 9,        // 9 AM
                initialMinute = 15,     // 15 minutes
                is24Hour = false,       // 12-hour format
                title = "Meeting Time"
            ) { cal ->
                val hour = cal.get(HOUR_OF_DAY)


                val minute = cal.get(MINUTE)
                val period = if (hour < 12) "AM" else "PM"
                val displayHour = if (hour == 0) 12 else if (hour > 12) hour - 12 else hour
                Toast.makeText(
                    this,
                    "Meeting at: $displayHour:${minute.toString().padStart(2, '0')} $period",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}
