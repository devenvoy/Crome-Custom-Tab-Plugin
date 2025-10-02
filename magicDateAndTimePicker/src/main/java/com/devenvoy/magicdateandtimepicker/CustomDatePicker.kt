package com.devenvoy.magicdateandtimepicker

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import kotlinx.parcelize.Parcelize
import java.util.Calendar
@OptIn(ExperimentalMaterial3Api::class)
class CustomDatePicker(
    private val context: Context,
    private val colors: CustomDatePickerColors = CustomDatePickerColors(),
    private val positiveButtonConfig: DatePickerButtonConfig = DatePickerButtonConfig(),
    private val negativeButtonConfig: DatePickerButtonConfig = DatePickerButtonConfig(),
) {

    @Parcelize
    data class CustomDatePickerColors(
        val containerColor: Long = 0xFFFFFFFF,
        val titleContentColor: Long = 0xFF000000,
        val headlineContentColor: Long = 0xFF000000,
        val weekdayContentColor: Long = 0xFF666666,
        val subheadContentColor: Long = 0xFF000000,
        val yearContentColor: Long = 0xFF000000,
        val currentYearContentColor: Long = 0xFF1976D2,
        val selectedYearContentColor: Long = 0xFFFFFFFF,
        val selectedYearContainerColor: Long = 0xFF1976D2,
        val dayContentColor: Long = 0xFF000000,
        val disabledDayContentColor: Long = 0xFFBBBBBB,
        val selectedDayContentColor: Long = 0xFFFFFFFF,
        val disabledSelectedDayContentColor: Long = 0xFFE0E0E0,
        val selectedDayContainerColor: Long = 0xFF1976D2,
        val disabledSelectedDayContainerColor: Long = 0xFFBBBBBB,
        val todayContentColor: Long = 0xFF1976D2,
        val todayDateBorderColor: Long = 0xFF1976D2,
        val dayInSelectionRangeContainerColor: Long = 0xFFE3F2FD,
        val dayInSelectionRangeContentColor: Long = 0xFF000000,
        val navigationContentColor: Long = 0xFF000000
    ) : Parcelable

    @Parcelize
    data class DatePickerButtonConfig(
        val labelName: String = "",
        val containerColor: Long = 0x00000000,
        val contentColor: Long = 0xFF000000,
        val disabledContainerColor: Long = 0x00000000,
        val disabledContentColor: Long = 0xFFBBBBBB,
    ) : Parcelable

    private var currentCallback: ((Calendar?) -> Unit)? = null
    private var composeView: ComposeView? = null

    fun show(
        initialDateMillis: Long? = null,
        startLimitMillis: Long? = null,
        endLimitMillis: Long? = null,
        title: String = "",
        onDateSelected: (cal: Calendar?) -> Unit
    ) {
        currentCallback = onDateSelected

        if (context is FragmentActivity) {
            context.supportFragmentManager.let { fragmentManager ->
                val existingDialog = fragmentManager.findFragmentByTag("CustomDatePicker")
                if (existingDialog != null) {
                    fragmentManager.beginTransaction().remove(existingDialog).commit()
                }

                val dialogFragment = DatePickerDialogFragment.newInstance(
                    initialDateMillis = initialDateMillis,
                    startLimitMillis = startLimitMillis,
                    endLimitMillis = endLimitMillis,
                    title = title,
                    colors = colors,
                    positiveButtonConfig = positiveButtonConfig,
                    negativeButtonConfig = negativeButtonConfig,
                    callBack = onDateSelected
                )
                dialogFragment.show(fragmentManager, "CustomDatePicker")
            }
        }
    }

    fun showInView(
        parent: ComposeView,
        initialDateMillis: Long? = null,
        startLimitMillis: Long? = null,
        endLimitMillis: Long? = null,
        title: String = "",
        onDateSelected: (dateMillis: Long?) -> Unit
    ) {
        composeView = parent
        parent.setContent {
            MaterialTheme {
                DatePickerDialog(
                    initialDateMillis = initialDateMillis,
                    startLimitMillis = startLimitMillis,
                    endLimitMillis = endLimitMillis,
                    title = title,
                    colors = colors,
                    positiveButtonConfig = positiveButtonConfig,
                    negativeButtonConfig = negativeButtonConfig,
                    onDismiss = { composeView?.setContent { } },
                    onDateSelected = onDateSelected
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
        private var containerColor: Color = Color.White
        private var titleContentColor: Color = Color.Black
        private var headlineContentColor: Color = Color.Black
        private var weekdayContentColor: Color = Color(0xFF666666)
        private var subheadContentColor: Color = Color.Black
        private var yearContentColor: Color = Color.Black
        private var currentYearContentColor: Color = Color(0xFF1976D2)
        private var selectedYearContentColor: Color = Color.White
        private var selectedYearContainerColor: Color = Color(0xFF1976D2)
        private var dayContentColor: Color = Color.Black
        private var disabledDayContentColor: Color = Color(0xFFBBBBBB)
        private var selectedDayContentColor: Color = Color.White
        private var disabledSelectedDayContentColor: Color = Color(0xFFE0E0E0)
        private var selectedDayContainerColor: Color = Color(0xFF1976D2)
        private var disabledSelectedDayContainerColor: Color = Color(0xFFBBBBBB)
        private var todayContentColor: Color = Color(0xFF1976D2)
        private var todayDateBorderColor: Color = Color(0xFF1976D2)
        private var dayInSelectionRangeContainerColor: Color = Color(0xFFE3F2FD)
        private var dayInSelectionRangeContentColor: Color = Color.Black
        private var positiveButtonLabelName: String = "OK"
        private var negativeButtonLabelName: String = "CANCEL"
        private var positiveContainerColor: Color = Color.Transparent
        private var positiveContentColor: Color = Color.Black
        private var positiveDisabledContainerColor: Color = Color.Transparent
        private var positiveDisabledContentColor: Color = Color(0xFFBBBBBB)
        private var negativeContainerColor: Color = Color.Transparent
        private var negativeContentColor: Color = Color.Black
        private var negativeDisabledContainerColor: Color = Color.Transparent
        private var negativeDisabledContentColor: Color = Color(0xFFBBBBBB)
        private var navigationContentColor: Color = Color.Black

        fun setContainerColor(color: Color) = apply { this.containerColor = color }
        fun setTitleContentColor(color: Color) = apply { this.titleContentColor = color }
        fun setHeadlineContentColor(color: Color) = apply { this.headlineContentColor = color }
        fun setWeekdayContentColor(color: Color) = apply { this.weekdayContentColor = color }
        fun setSubheadContentColor(color: Color) = apply { this.subheadContentColor = color }
        fun setYearContentColor(color: Color) = apply { this.yearContentColor = color }
        fun setCurrentYearContentColor(color: Color) = apply { this.currentYearContentColor = color }
        fun setSelectedYearContentColor(color: Color) = apply { this.selectedYearContentColor = color }
        fun setSelectedYearContainerColor(color: Color) = apply { this.selectedYearContainerColor = color }
        fun setDayContentColor(color: Color) = apply { this.dayContentColor = color }
        fun setDisabledDayContentColor(color: Color) = apply { this.disabledDayContentColor = color }
        fun setSelectedDayContentColor(color: Color) = apply { this.selectedDayContentColor = color }
        fun setDisabledSelectedDayContentColor(color: Color) = apply { this.disabledSelectedDayContentColor = color }
        fun setSelectedDayContainerColor(color: Color) = apply { this.selectedDayContainerColor = color }
        fun setDisabledSelectedDayContainerColor(color: Color) = apply { this.disabledSelectedDayContainerColor = color }
        fun setTodayContentColor(color: Color) = apply { this.todayContentColor = color }
        fun setTodayDateBorderColor(color: Color) = apply { this.todayDateBorderColor = color }
        fun setDayInSelectionRangeContainerColor(color: Color) = apply { this.dayInSelectionRangeContainerColor = color }
        fun setDayInSelectionRangeContentColor(color: Color) = apply { this.dayInSelectionRangeContentColor = color }
        fun setPositiveButtonLabelName(labelName: String) = apply { this.positiveButtonLabelName = labelName }
        fun setNegativeButtonLabelName(labelName: String) = apply { this.negativeButtonLabelName = labelName }
        fun setPositiveContainerColor(color: Color) = apply { this.positiveContainerColor = color }
        fun setPositiveContentColor(color: Color) = apply { this.positiveContentColor = color }
        fun setPositiveDisabledContainerColor(color: Color) = apply { this.positiveDisabledContainerColor = color }
        fun setPositiveDisabledContentColor(color: Color) = apply { this.positiveDisabledContentColor = color }
        fun setNegativeContainerColor(color: Color) = apply { this.negativeContainerColor = color }
        fun setNegativeContentColor(color: Color) = apply { this.negativeContentColor = color }
        fun setNegativeDisabledContainerColor(color: Color) = apply { this.negativeDisabledContainerColor = color }
        fun setNegativeDisabledContentColor(color: Color) = apply { this.negativeDisabledContentColor = color }
        fun setNavigationContentColor(color: Color) = apply { this.navigationContentColor = color }


        fun build(): CustomDatePicker {
            val colors = CustomDatePickerColors(
                containerColor = containerColor.toArgb().toLong() and 0xFFFFFFFF,
                titleContentColor = titleContentColor.toArgb().toLong() and 0xFFFFFFFF,
                headlineContentColor = headlineContentColor.toArgb().toLong() and 0xFFFFFFFF,
                weekdayContentColor = weekdayContentColor.toArgb().toLong() and 0xFFFFFFFF,
                subheadContentColor = subheadContentColor.toArgb().toLong() and 0xFFFFFFFF,
                yearContentColor = yearContentColor.toArgb().toLong() and 0xFFFFFFFF,
                currentYearContentColor = currentYearContentColor.toArgb().toLong() and 0xFFFFFFFF,
                selectedYearContentColor = selectedYearContentColor.toArgb().toLong() and 0xFFFFFFFF,
                selectedYearContainerColor = selectedYearContainerColor.toArgb().toLong() and 0xFFFFFFFF,
                dayContentColor = dayContentColor.toArgb().toLong() and 0xFFFFFFFF,
                disabledDayContentColor = disabledDayContentColor.toArgb().toLong() and 0xFFFFFFFF,
                selectedDayContentColor = selectedDayContentColor.toArgb().toLong() and 0xFFFFFFFF,
                disabledSelectedDayContentColor = disabledSelectedDayContentColor.toArgb().toLong() and 0xFFFFFFFF,
                selectedDayContainerColor = selectedDayContainerColor.toArgb().toLong() and 0xFFFFFFFF,
                disabledSelectedDayContainerColor = disabledSelectedDayContainerColor.toArgb().toLong() and 0xFFFFFFFF,
                todayContentColor = todayContentColor.toArgb().toLong() and 0xFFFFFFFF,
                todayDateBorderColor = todayDateBorderColor.toArgb().toLong() and 0xFFFFFFFF,
                dayInSelectionRangeContainerColor = dayInSelectionRangeContainerColor.toArgb().toLong() and 0xFFFFFFFF,
                dayInSelectionRangeContentColor = dayInSelectionRangeContentColor.toArgb().toLong() and 0xFFFFFFFF,
                navigationContentColor= navigationContentColor.toArgb().toLong() and 0xFFFFFFFF
            )

            val positiveButtonConfig = DatePickerButtonConfig(
                labelName = positiveButtonLabelName,
                containerColor = positiveContainerColor.toArgb().toLong() and 0xFFFFFFFF,
                contentColor = positiveContentColor.toArgb().toLong() and 0xFFFFFFFF,
                disabledContainerColor = positiveDisabledContainerColor.toArgb().toLong() and 0xFFFFFFFF,
                disabledContentColor = positiveDisabledContentColor.toArgb().toLong() and 0xFFFFFFFF
            )

            val negativeButtonConfig = DatePickerButtonConfig(
                labelName = negativeButtonLabelName,
                containerColor = negativeContainerColor.toArgb().toLong() and 0xFFFFFFFF,
                contentColor = negativeContentColor.toArgb().toLong() and 0xFFFFFFFF,
                disabledContainerColor = negativeDisabledContainerColor.toArgb().toLong() and 0xFFFFFFFF,
                disabledContentColor = negativeDisabledContentColor.toArgb().toLong() and 0xFFFFFFFF
            )

            return CustomDatePicker(context, colors, positiveButtonConfig, negativeButtonConfig)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerDialog(
    initialDateMillis: Long?,
    startLimitMillis: Long?,
    endLimitMillis: Long?,
    title: String,
    colors: CustomDatePicker.CustomDatePickerColors,
    positiveButtonConfig: CustomDatePicker.DatePickerButtonConfig,
    negativeButtonConfig: CustomDatePicker.DatePickerButtonConfig,
    onDismiss: () -> Unit,
    onDateSelected: (Long?) -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDateMillis,
        initialDisplayMode = DisplayMode.Picker,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                if (startLimitMillis != null && utcTimeMillis < startLimitMillis) return false
                if (endLimitMillis != null && utcTimeMillis > endLimitMillis) return false
                return true
            }
        }
    )

    val customColors = DatePickerDefaults.colors(
        containerColor = Color(colors.containerColor),
        titleContentColor = Color(colors.titleContentColor),
        headlineContentColor = Color(colors.headlineContentColor),
        weekdayContentColor = Color(colors.weekdayContentColor),
        subheadContentColor = Color(colors.subheadContentColor),
        yearContentColor = Color(colors.yearContentColor),
        currentYearContentColor = Color(colors.currentYearContentColor),
        selectedYearContentColor = Color(colors.selectedYearContentColor),
        selectedYearContainerColor = Color(colors.selectedYearContainerColor),
        dayContentColor = Color(colors.dayContentColor),
        disabledDayContentColor = Color(colors.disabledDayContentColor),
        selectedDayContentColor = Color(colors.selectedDayContentColor),
        disabledSelectedDayContentColor = Color(colors.disabledSelectedDayContentColor),
        selectedDayContainerColor = Color(colors.selectedDayContainerColor),
        disabledSelectedDayContainerColor = Color(colors.disabledSelectedDayContainerColor),
        todayContentColor = Color(colors.todayContentColor),
        todayDateBorderColor = Color(colors.todayDateBorderColor),
        dayInSelectionRangeContainerColor = Color(colors.dayInSelectionRangeContainerColor),
        dayInSelectionRangeContentColor = Color(colors.dayInSelectionRangeContentColor),
        navigationContentColor =  Color(colors.navigationContentColor)
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


    androidx.compose.ui.window.Dialog(
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
                modifier = Modifier.padding(16.dp)
            ) {
                if (title.isNotBlank()) {
                    Text(
                        text = title,
                        fontSize = 20.sp,
                        color = Color(colors.titleContentColor),
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                }

                DatePicker(
                    state = datePickerState,
                    colors = customColors,
                    title = {},
                    showModeToggle = false,
                    modifier = Modifier.fillMaxWidth(),
                )

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
                            onDateSelected(datePickerState.selectedDateMillis)
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

class DatePickerDialogFragment : DialogFragment() {

    companion object {
        private const val ARG_INITIAL_DATE = "initial_date"
        private const val ARG_START_LIMIT = "start_limit"
        private const val ARG_END_LIMIT = "end_limit"
        private const val ARG_TITLE = "title"
        private const val ARG_COLORS = "colors"
        private const val ARG_POSITIVE_CONFIG = "positive_config"
        private const val ARG_NEGATIVE_CONFIG = "negative_config"

        fun newInstance(
            initialDateMillis: Long?,
            startLimitMillis: Long?,
            endLimitMillis: Long?,
            title: String,
            colors: CustomDatePicker.CustomDatePickerColors,
            positiveButtonConfig: CustomDatePicker.DatePickerButtonConfig,
            negativeButtonConfig: CustomDatePicker.DatePickerButtonConfig,
            callBack: ((Calendar?) -> Unit)? = null
        ): DatePickerDialogFragment {
            val fragment = DatePickerDialogFragment()
            fragment.arguments = Bundle().apply {
                if (initialDateMillis != null) putLong(ARG_INITIAL_DATE, initialDateMillis)
                if (startLimitMillis != null) putLong(ARG_START_LIMIT, startLimitMillis)
                if (endLimitMillis != null) putLong(ARG_END_LIMIT, endLimitMillis)
                putString(ARG_TITLE, title)
                putParcelable(ARG_COLORS, colors)
                putParcelable(ARG_POSITIVE_CONFIG, positiveButtonConfig)
                putParcelable(ARG_NEGATIVE_CONFIG, negativeButtonConfig)
            }
            fragment.callBack = callBack
            return fragment
        }
    }

    private var callBack: ((Calendar?) -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val args = requireArguments()
        val initialDateMillis = if (args.containsKey(ARG_INITIAL_DATE)) args.getLong(ARG_INITIAL_DATE) else null
        val startLimitMillis = if (args.containsKey(ARG_START_LIMIT)) args.getLong(ARG_START_LIMIT) else null
        val endLimitMillis = if (args.containsKey(ARG_END_LIMIT)) args.getLong(ARG_END_LIMIT) else null
        val title = args.getString(ARG_TITLE, "")
        val colors = args.getParcelable<CustomDatePicker.CustomDatePickerColors>(ARG_COLORS)
            ?: CustomDatePicker.CustomDatePickerColors()
        val positiveButtonConfig = args.getParcelable<CustomDatePicker.DatePickerButtonConfig>(ARG_POSITIVE_CONFIG)
            ?: CustomDatePicker.DatePickerButtonConfig()
        val negativeButtonConfig = args.getParcelable<CustomDatePicker.DatePickerButtonConfig>(ARG_NEGATIVE_CONFIG)
            ?: CustomDatePicker.DatePickerButtonConfig()

        val dialog = Dialog(requireContext())
        val composeView = ComposeView(requireContext())

        composeView.setContent {
            MaterialTheme {
                DatePickerDialog(
                    initialDateMillis = initialDateMillis,
                    startLimitMillis = startLimitMillis,
                    endLimitMillis = endLimitMillis,
                    title = title,
                    colors = colors,
                    positiveButtonConfig = positiveButtonConfig,
                    negativeButtonConfig = negativeButtonConfig,
                    onDismiss = { dismiss() },
                    onDateSelected = { dateMillis ->
                        val calendar = Calendar.getInstance().apply {
                            timeInMillis = dateMillis ?: 0L
                        }
                        callBack?.invoke(calendar)
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