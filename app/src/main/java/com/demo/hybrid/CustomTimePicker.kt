package com.apnacomplex.common.util.views.util

import android.content.Context
import android.os.Parcelable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.fragment.app.FragmentActivity
import kotlinx.parcelize.Parcelize
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
class CustomTimePicker(
    private val context: Context,
    private val colors: CustomTimePickerColors = CustomTimePickerColors(),
    private val positiveButtonConfig: TimePickerButtonConfig = TimePickerButtonConfig(),
    private val negativeButtonConfig: TimePickerButtonConfig = TimePickerButtonConfig(),
) {

    @Parcelize
    data class CustomTimePickerColors(
        val clockDialColor: Long = 0xFFE3F2FD,
        val selectorColor: Long = 0xFF1976D2,
        val containerColor: Long = 0xFFFFFFFF,
        val titleColor: Long = 0xFF000000,
        val clockDialSelectedContentColor: Long = 0xFFFFFFFF,
        val clockDialUnselectedContentColor: Long = 0xFF000000,
        val periodSelectorBorderColor: Long = 0xFF1976D2,
        val periodSelectorSelectedContainerColor: Long = 0xFF1976D2,
        val periodSelectorUnselectedContainerColor: Long = 0x00000000,
        val periodSelectorSelectedContentColor: Long = 0xFFFFFFFF,
        val periodSelectorUnselectedContentColor: Long = 0xFF1976D2,
        val timeSelectorSelectedContainerColor: Long = 0xFFE3F2FD,
        val timeSelectorUnselectedContainerColor: Long = 0x00000000,
        val timeSelectorSelectedContentColor: Long = 0xFF1976D2,
        val timeSelectorUnselectedContentColor: Long = 0xFF000000
    ) : Parcelable

    @Parcelize
    data class TimePickerButtonConfig(
        val labelName: String = "",
        val containerColor: Long = 0x00000000,
        val contentColor: Long = 0xFF000000,
        val disabledContainerColor: Long = 0x00000000,
        val disabledContentColor: Long = 0xFFBBBBBB,
    ) : Parcelable

    private var currentCallback: ((Calendar) -> Unit)? = null
    private var composeView: ComposeView? = null

    fun show(
        initialHour: Int = Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
        initialMinute: Int = Calendar.getInstance().get(Calendar.MINUTE),
        is24Hour: Boolean = true,
        title: String = "",
        callBack: (Calendar) -> Unit
    ) {
        currentCallback = callBack

        if (context is FragmentActivity) {
            context.supportFragmentManager.let { fragmentManager ->
                val existingDialog = fragmentManager.findFragmentByTag("CustomTimePicker")
                if (existingDialog != null) {
                    fragmentManager.beginTransaction().remove(existingDialog).commit()
                }

                val dialogFragment = TimePickerDialogFragment.newInstance(
                    initialHour = initialHour,
                    initialMinute = initialMinute,
                    is24Hour = is24Hour,
                    title = title,
                    colors = colors,
                    positiveButtonConfig = positiveButtonConfig,
                    negativeButtonConfig = negativeButtonConfig,
                    callBack = currentCallback
                )
                dialogFragment.show(fragmentManager, "CustomTimePicker")
            }
        }
    }

    fun showInView(
        parent: ComposeView,
        initialHour: Int = Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
        initialMinute: Int = Calendar.getInstance().get(Calendar.MINUTE),
        is24Hour: Boolean = true,
        title: String = "",
        callBack: (Calendar) -> Unit
    ) {
        composeView = parent
        parent.setContent {
            MaterialTheme {
                TimePickerDialog(
                    initialHour = initialHour,
                    initialMinute = initialMinute,
                    is24Hour = is24Hour,
                    title = title,
                    colors = colors,
                    positiveButtonConfig = positiveButtonConfig,
                    negativeButtonConfig = negativeButtonConfig,
                    onDismiss = { composeView?.setContent { } },
                    callBack = callBack
                )
            }
        }
    }

    companion object {
        fun builder(context: Context): Builder {
            return Builder(context)
        }
    }

    class Builder(private val context: Context) {
        private var clockDialColor: Color = Color(0xFFE3F2FD)
        private var selectorColor: Color = Color(0xFF1976D2)
        private var containerColor: Color = Color.White
        private var titleColor: Color = Color.Black
        private var clockDialSelectedContentColor: Color = Color.White
        private var clockDialUnselectedContentColor: Color = Color.Black
        private var periodSelectorBorderColor: Color = Color(0xFF1976D2)
        private var periodSelectorSelectedContainerColor: Color = Color(0xFF1976D2)
        private var periodSelectorUnselectedContainerColor: Color = Color.Transparent
        private var periodSelectorSelectedContentColor: Color = Color.White
        private var periodSelectorUnselectedContentColor: Color = Color(0xFF1976D2)
        private var timeSelectorSelectedContainerColor: Color = Color(0xFFE3F2FD)
        private var timeSelectorUnselectedContainerColor: Color = Color.Transparent
        private var timeSelectorSelectedContentColor: Color = Color(0xFF1976D2)
        private var timeSelectorUnselectedContentColor: Color = Color.Black
        private var positiveButtonLabelName: String = "OK"
        private var negativeButtonLabelName: String = "CANCEL"
        private var positiveContainerColor : Color = Color.Transparent
        private var positiveContentColor : Color = Color.Black
        private var positiveDisabledContainerColor : Color =  Color.Transparent
        private var positiveDisabledContentColor : Color = Color(0xFFBBBBBB)
        private var negativeContainerColor : Color =  Color.Transparent
        private var negativeContentColor : Color = Color.Black
        private var negativeDisabledContainerColor : Color = Color.Transparent
        private var negativeDisabledContentColor : Color = Color(0xFFBBBBBB)

        fun setClockDialColor(color: Color) = apply { this.clockDialColor = color }
        fun setSelectorColor(color: Color) = apply { this.selectorColor = color }
        fun setContainerColor(color: Color) = apply { this.containerColor = color }
        fun setTitleColor(color: Color) = apply { this.titleColor = color }
        fun setClockDialSelectedContentColor(color: Color) = apply { this.clockDialSelectedContentColor = color }
        fun setClockDialUnselectedContentColor(color: Color) = apply { this.clockDialUnselectedContentColor = color }
        fun setPeriodSelectorBorderColor(color: Color) = apply { this.periodSelectorBorderColor = color }
        fun setPeriodSelectorSelectedContainerColor(color: Color) = apply { this.periodSelectorSelectedContainerColor = color }
        fun setPeriodSelectorUnselectedContainerColor(color: Color) = apply { this.periodSelectorUnselectedContainerColor = color }
        fun setPeriodSelectorSelectedContentColor(color: Color) = apply { this.periodSelectorSelectedContentColor = color }
        fun setPeriodSelectorUnselectedContentColor(color: Color) = apply { this.periodSelectorUnselectedContentColor = color }
        fun setTimeSelectorSelectedContainerColor(color: Color) = apply { this.timeSelectorSelectedContainerColor = color }
        fun setTimeSelectorUnselectedContainerColor(color: Color) = apply { this.timeSelectorUnselectedContainerColor = color }
        fun setTimeSelectorSelectedContentColor(color: Color) = apply { this.timeSelectorSelectedContentColor = color }
        fun setTimeSelectorUnselectedContentColor(color: Color) = apply { this.timeSelectorUnselectedContentColor = color }
        fun  setPositiveButtonLabelName(labelName: String) = apply { this.positiveButtonLabelName = labelName }
        fun  setNegativeButtonLabelName(labelName: String) = apply { this.negativeButtonLabelName = labelName }
        fun  setPositiveContainerColor(color: Color) = apply { this.positiveContainerColor = color }
        fun  setPositiveContentColor(color: Color) = apply { this.positiveContentColor = color }
        fun  setPositiveDisabledContainerColor(color: Color) = apply { this.positiveDisabledContainerColor = color }
        fun  setPositiveDisabledContentColor (color: Color) = apply { this.positiveDisabledContentColor = color }
        fun  setNegativeContainerColor (color: Color) = apply { this.negativeContainerColor = color }
        fun  setNegativeContentColor (color: Color) = apply { this.negativeContentColor = color }
        fun  setNegativeDisabledContainerColor(color: Color) = apply { this.negativeDisabledContainerColor = color }
        fun  setNegativeDisabledContentColor(color: Color) = apply { this.negativeDisabledContentColor = color }

        fun build(): CustomTimePicker {
            val colors = CustomTimePickerColors(
                clockDialColor = clockDialColor.toArgb().toLong() and 0xFFFFFFFF,
                selectorColor = selectorColor.toArgb().toLong() and 0xFFFFFFFF,
                containerColor = containerColor.toArgb().toLong() and 0xFFFFFFFF,
                titleColor = titleColor.toArgb().toLong() and 0xFFFFFFFF,
                clockDialSelectedContentColor = clockDialSelectedContentColor.toArgb().toLong() and 0xFFFFFFFF,
                clockDialUnselectedContentColor = clockDialUnselectedContentColor.toArgb().toLong() and 0xFFFFFFFF,
                periodSelectorBorderColor = periodSelectorBorderColor.toArgb().toLong() and 0xFFFFFFFF,
                periodSelectorSelectedContainerColor = periodSelectorSelectedContainerColor.toArgb().toLong() and 0xFFFFFFFF,
                periodSelectorUnselectedContainerColor = periodSelectorUnselectedContainerColor.toArgb().toLong() and 0xFFFFFFFF,
                periodSelectorSelectedContentColor = periodSelectorSelectedContentColor.toArgb().toLong() and 0xFFFFFFFF,
                periodSelectorUnselectedContentColor = periodSelectorUnselectedContentColor.toArgb().toLong() and 0xFFFFFFFF,
                timeSelectorSelectedContainerColor = timeSelectorSelectedContainerColor.toArgb().toLong() and 0xFFFFFFFF,
                timeSelectorUnselectedContainerColor = timeSelectorUnselectedContainerColor.toArgb().toLong() and 0xFFFFFFFF,
                timeSelectorSelectedContentColor = timeSelectorSelectedContentColor.toArgb().toLong() and 0xFFFFFFFF,
                timeSelectorUnselectedContentColor = timeSelectorUnselectedContentColor.toArgb().toLong() and 0xFFFFFFFF
            )

            val positiveButtonConfig = TimePickerButtonConfig(
                labelName = positiveButtonLabelName,
                containerColor = positiveContainerColor.toArgb().toLong() and 0xFFFFFFFF,
                contentColor = positiveContentColor.toArgb().toLong() and 0xFFFFFFFF,
                disabledContainerColor = positiveDisabledContainerColor.toArgb().toLong() and 0xFFFFFFFF,
                disabledContentColor = positiveDisabledContentColor.toArgb().toLong() and 0xFFFFFFFF
            )

            val negativeButtonConfig = TimePickerButtonConfig(
                labelName = negativeButtonLabelName,
                containerColor = negativeContainerColor.toArgb().toLong() and 0xFFFFFFFF,
                contentColor = negativeContentColor.toArgb().toLong() and 0xFFFFFFFF,
                disabledContainerColor = negativeDisabledContainerColor.toArgb().toLong() and 0xFFFFFFFF,
                disabledContentColor = negativeDisabledContentColor.toArgb().toLong() and 0xFFFFFFFF
            )

            return CustomTimePicker(context, colors, positiveButtonConfig, negativeButtonConfig)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePickerDialog(
    initialHour: Int,
    initialMinute: Int,
    is24Hour: Boolean,
    title: String,
    colors: CustomTimePicker.CustomTimePickerColors,
    positiveButtonConfig: CustomTimePicker.TimePickerButtonConfig,
    negativeButtonConfig: CustomTimePicker.TimePickerButtonConfig,
    onDismiss: () -> Unit,
    callBack: (Calendar) -> Unit
) {
    val timePickerState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = is24Hour
    )

    val customColors = TimePickerDefaults.colors(
        clockDialColor = Color(colors.clockDialColor),
        selectorColor = Color(colors.selectorColor),
        containerColor = Color(colors.containerColor),
        clockDialSelectedContentColor = Color(colors.clockDialSelectedContentColor),
        clockDialUnselectedContentColor = Color(colors.clockDialUnselectedContentColor),
        periodSelectorBorderColor = Color(colors.periodSelectorBorderColor),
        periodSelectorSelectedContainerColor = Color(colors.periodSelectorSelectedContainerColor),
        periodSelectorUnselectedContainerColor = Color(colors.periodSelectorUnselectedContainerColor),
        periodSelectorSelectedContentColor = Color(colors.periodSelectorSelectedContentColor),
        periodSelectorUnselectedContentColor = Color(colors.periodSelectorUnselectedContentColor),
        timeSelectorSelectedContainerColor = Color(colors.timeSelectorSelectedContainerColor),
        timeSelectorUnselectedContainerColor = Color(colors.timeSelectorUnselectedContainerColor),
        timeSelectorSelectedContentColor = Color(colors.timeSelectorSelectedContentColor),
        timeSelectorUnselectedContentColor = Color(colors.timeSelectorUnselectedContentColor)
    )

    val customPositiveButtonColor = ButtonDefaults.textButtonColors(
        containerColor = Color(positiveButtonConfig.containerColor),
        contentColor = Color(positiveButtonConfig.contentColor),
        disabledContainerColor = Color(positiveButtonConfig.disabledContainerColor),
        disabledContentColor = Color(positiveButtonConfig.disabledContentColor),
    )

    val customNegativeButtonColor = ButtonDefaults.textButtonColors(
        containerColor = Color(negativeButtonConfig.containerColor),
        contentColor = Color(negativeButtonConfig.contentColor),
        disabledContainerColor = Color(negativeButtonConfig.disabledContainerColor),
        disabledContentColor = Color(negativeButtonConfig.disabledContentColor),
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .wrapContentHeight(),
            colors = CardDefaults.cardColors(containerColor = Color(colors.containerColor))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    fontSize = 20.sp,
                    color = Color(colors.titleColor),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                TimePicker(state = timePickerState, colors = customColors)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss, colors = customNegativeButtonColor) {
                        Text(negativeButtonConfig.labelName)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(
                        onClick = {
                            val cal = Calendar.getInstance()
                            cal.set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                            cal.set(Calendar.MINUTE, timePickerState.minute)
                            callBack(cal)
                            onDismiss()
                        },
                        colors = customPositiveButtonColor
                    ) {
                        Text(positiveButtonConfig.labelName)
                    }
                }
            }
        }
    }
}

class TimePickerDialogFragment : androidx.fragment.app.DialogFragment() {

    companion object {
        private const val ARG_INITIAL_HOUR = "initial_hour"
        private const val ARG_INITIAL_MINUTE = "initial_minute"
        private const val ARG_IS_24_HOUR = "is_24_hour"
        private const val ARG_TITLE = "title"
        private const val ARG_COLORS = "colors"
        private const val ARG_POSITIVE_CONFIG = "positive_config"
        private const val ARG_NEGATIVE_CONFIG = "negative_config"

        fun newInstance(
            initialHour: Int,
            initialMinute: Int,
            is24Hour: Boolean,
            title: String,
            colors: CustomTimePicker.CustomTimePickerColors,
            positiveButtonConfig: CustomTimePicker.TimePickerButtonConfig,
            negativeButtonConfig: CustomTimePicker.TimePickerButtonConfig,
            callBack: ((Calendar) -> Unit)? = null
        ): TimePickerDialogFragment {
            val fragment = TimePickerDialogFragment()
            fragment.arguments = android.os.Bundle().apply {
                putInt(ARG_INITIAL_HOUR, initialHour)
                putInt(ARG_INITIAL_MINUTE, initialMinute)
                putBoolean(ARG_IS_24_HOUR, is24Hour)
                putString(ARG_TITLE, title)
                putParcelable(ARG_COLORS, colors)
                putParcelable(ARG_POSITIVE_CONFIG, positiveButtonConfig)
                putParcelable(ARG_NEGATIVE_CONFIG, negativeButtonConfig)
            }
            fragment.callBack = callBack
            return fragment
        }
    }

    private var callBack: ((Calendar) -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: android.os.Bundle?): android.app.Dialog {
        val args = requireArguments()
        val initialHour = args.getInt(ARG_INITIAL_HOUR, 0)
        val initialMinute = args.getInt(ARG_INITIAL_MINUTE, 0)
        val is24Hour = args.getBoolean(ARG_IS_24_HOUR, true)
        val title = args.getString(ARG_TITLE, "")
        val colors = args.getParcelable(ARG_COLORS)
            ?: CustomTimePicker.CustomTimePickerColors()
        val positiveButtonConfig = args.getParcelable(ARG_POSITIVE_CONFIG)
            ?: CustomTimePicker.TimePickerButtonConfig()
        val negativeButtonConfig = args.getParcelable(ARG_NEGATIVE_CONFIG)
            ?: CustomTimePicker.TimePickerButtonConfig()

        val dialog = android.app.Dialog(requireContext())
        val composeView = ComposeView(requireContext())

        composeView.setContent {
            MaterialTheme {
                TimePickerDialog(
                    initialHour = initialHour,
                    initialMinute = initialMinute,
                    is24Hour = is24Hour,
                    title = title,
                    colors = colors,
                    positiveButtonConfig = positiveButtonConfig,
                    negativeButtonConfig = negativeButtonConfig,
                    onDismiss = { dismiss() },
                    callBack = { cal ->
                        callBack?.invoke(cal)
                        dismiss()
                    }
                )
            }
        }

        dialog.setContentView(composeView)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        return dialog
    }


}