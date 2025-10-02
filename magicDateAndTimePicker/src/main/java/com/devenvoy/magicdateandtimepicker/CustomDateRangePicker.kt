package com.devenvoy.magicdateandtimepicker

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DateRangePickerDefaults
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import kotlinx.parcelize.Parcelize
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
class CustomDateRangePicker(
    private val context: Context,
    private val colors: CustomDateRangePickerColors = CustomDateRangePickerColors(),
    private val positiveButtonConfig: DateRangePickerButtonConfig = DateRangePickerButtonConfig(),
    private val negativeButtonConfig: DateRangePickerButtonConfig = DateRangePickerButtonConfig(),
) {

    @Parcelize
    data class CustomDateRangePickerColors(
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
    data class DateRangePickerButtonConfig(
        val labelName: String = "",
        val containerColor: Long = 0x00000000,
        val contentColor: Long = 0xFF000000,
        val disabledContainerColor: Long = 0x00000000,
        val disabledContentColor: Long = 0xFFBBBBBB,
    ) : Parcelable

    fun show(
        startDateMillis: Long? = null,
        endDateMillis: Long? = null,
        startLimitMillis: Long? = null,
        endLimitMillis: Long? = null,
        title: String = "",
        onDateRangeSelected: (startMillis: Long?, endMillis: Long?) -> Unit
    ) {
        if (context is FragmentActivity) {
            context.supportFragmentManager.let { fragmentManager ->
                val existingDialog = fragmentManager.findFragmentByTag("CustomDateRangePicker")
                if (existingDialog != null) {
                    fragmentManager.beginTransaction().remove(existingDialog).commit()
                }

                val dialogFragment = DateRangePickerDialogFragment.newInstance(
                    startDateMillis = startDateMillis,
                    endDateMillis = endDateMillis,
                    startLimitMillis = startLimitMillis,
                    endLimitMillis = endLimitMillis,
                    title = title,
                    colors = colors,
                    positiveButtonConfig = positiveButtonConfig,
                    negativeButtonConfig = negativeButtonConfig,
                    callBack = onDateRangeSelected
                )
                dialogFragment.show(fragmentManager, "CustomDateRangePicker")
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
        fun setCurrentYearContentColor(color: Color) =
            apply { this.currentYearContentColor = color }

        fun setSelectedYearContentColor(color: Color) =
            apply { this.selectedYearContentColor = color }

        fun setSelectedYearContainerColor(color: Color) =
            apply { this.selectedYearContainerColor = color }

        fun setDayContentColor(color: Color) = apply { this.dayContentColor = color }
        fun setDisabledDayContentColor(color: Color) =
            apply { this.disabledDayContentColor = color }

        fun setSelectedDayContentColor(color: Color) =
            apply { this.selectedDayContentColor = color }

        fun setDisabledSelectedDayContentColor(color: Color) =
            apply { this.disabledSelectedDayContentColor = color }

        fun setSelectedDayContainerColor(color: Color) =
            apply { this.selectedDayContainerColor = color }

        fun setDisabledSelectedDayContainerColor(color: Color) =
            apply { this.disabledSelectedDayContainerColor = color }

        fun setTodayContentColor(color: Color) = apply { this.todayContentColor = color }
        fun setTodayDateBorderColor(color: Color) = apply { this.todayDateBorderColor = color }
        fun setDayInSelectionRangeContainerColor(color: Color) =
            apply { this.dayInSelectionRangeContainerColor = color }

        fun setDayInSelectionRangeContentColor(color: Color) =
            apply { this.dayInSelectionRangeContentColor = color }

        fun setPositiveButtonLabelName(labelName: String) =
            apply { this.positiveButtonLabelName = labelName }

        fun setNegativeButtonLabelName(labelName: String) =
            apply { this.negativeButtonLabelName = labelName }

        fun setPositiveContainerColor(color: Color) = apply { this.positiveContainerColor = color }
        fun setPositiveContentColor(color: Color) = apply { this.positiveContentColor = color }
        fun setPositiveDisabledContainerColor(color: Color) =
            apply { this.positiveDisabledContainerColor = color }

        fun setPositiveDisabledContentColor(color: Color) =
            apply { this.positiveDisabledContentColor = color }

        fun setNegativeContainerColor(color: Color) = apply { this.negativeContainerColor = color }
        fun setNegativeContentColor(color: Color) = apply { this.negativeContentColor = color }
        fun setNegativeDisabledContainerColor(color: Color) =
            apply { this.negativeDisabledContainerColor = color }

        fun setNegativeDisabledContentColor(color: Color) =
            apply { this.negativeDisabledContentColor = color }

        fun setNavigationContentColor(color: Color) = apply { this.navigationContentColor = color }

        fun build(): CustomDateRangePicker {
            val colors = CustomDateRangePickerColors(
                containerColor = containerColor.toArgb().toLong() and 0xFFFFFFFF,
                titleContentColor = titleContentColor.toArgb().toLong() and 0xFFFFFFFF,
                headlineContentColor = headlineContentColor.toArgb().toLong() and 0xFFFFFFFF,
                weekdayContentColor = weekdayContentColor.toArgb().toLong() and 0xFFFFFFFF,
                subheadContentColor = subheadContentColor.toArgb().toLong() and 0xFFFFFFFF,
                yearContentColor = yearContentColor.toArgb().toLong() and 0xFFFFFFFF,
                currentYearContentColor = currentYearContentColor.toArgb().toLong() and 0xFFFFFFFF,
                selectedYearContentColor = selectedYearContentColor.toArgb()
                    .toLong() and 0xFFFFFFFF,
                selectedYearContainerColor = selectedYearContainerColor.toArgb()
                    .toLong() and 0xFFFFFFFF,
                dayContentColor = dayContentColor.toArgb().toLong() and 0xFFFFFFFF,
                disabledDayContentColor = disabledDayContentColor.toArgb().toLong() and 0xFFFFFFFF,
                selectedDayContentColor = selectedDayContentColor.toArgb().toLong() and 0xFFFFFFFF,
                disabledSelectedDayContentColor = disabledSelectedDayContentColor.toArgb()
                    .toLong() and 0xFFFFFFFF,
                selectedDayContainerColor = selectedDayContainerColor.toArgb()
                    .toLong() and 0xFFFFFFFF,
                disabledSelectedDayContainerColor = disabledSelectedDayContainerColor.toArgb()
                    .toLong() and 0xFFFFFFFF,
                todayContentColor = todayContentColor.toArgb().toLong() and 0xFFFFFFFF,
                todayDateBorderColor = todayDateBorderColor.toArgb().toLong() and 0xFFFFFFFF,
                dayInSelectionRangeContainerColor = dayInSelectionRangeContainerColor.toArgb()
                    .toLong() and 0xFFFFFFFF,
                dayInSelectionRangeContentColor = dayInSelectionRangeContentColor.toArgb()
                    .toLong() and 0xFFFFFFFF,
                navigationContentColor = navigationContentColor.toArgb().toLong() and 0xFFFFFFFF
            )

            val positiveButtonConfig = DateRangePickerButtonConfig(
                labelName = positiveButtonLabelName,
                containerColor = positiveContainerColor.toArgb().toLong() and 0xFFFFFFFF,
                contentColor = positiveContentColor.toArgb().toLong() and 0xFFFFFFFF,
                disabledContainerColor = positiveDisabledContainerColor.toArgb()
                    .toLong() and 0xFFFFFFFF,
                disabledContentColor = positiveDisabledContentColor.toArgb().toLong() and 0xFFFFFFFF
            )

            val negativeButtonConfig = DateRangePickerButtonConfig(
                labelName = negativeButtonLabelName,
                containerColor = negativeContainerColor.toArgb().toLong() and 0xFFFFFFFF,
                contentColor = negativeContentColor.toArgb().toLong() and 0xFFFFFFFF,
                disabledContainerColor = negativeDisabledContainerColor.toArgb()
                    .toLong() and 0xFFFFFFFF,
                disabledContentColor = negativeDisabledContentColor.toArgb().toLong() and 0xFFFFFFFF
            )

            return CustomDateRangePicker(
                context,
                colors,
                positiveButtonConfig,
                negativeButtonConfig
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateRangePickerDialog(
    startDateMillis: Long?,
    endDateMillis: Long?,
    startLimitMillis: Long?,
    endLimitMillis: Long?,
    title: String,
    colors: CustomDateRangePicker.CustomDateRangePickerColors,
    positiveButtonConfig: CustomDateRangePicker.DateRangePickerButtonConfig,
    negativeButtonConfig: CustomDateRangePicker.DateRangePickerButtonConfig,
    onDismiss: () -> Unit,
    onDateRangeSelected: (Long?, Long?) -> Unit
) {
    var showYearPicker by remember { mutableStateOf(false) }
    var displayedYear by remember {
        val calendar = Calendar.getInstance()
        startDateMillis?.let { calendar.timeInMillis = it }
        mutableStateOf(calendar.get(Calendar.YEAR))
    }

    val dateRangePickerState = rememberDateRangePickerState(
        initialSelectedStartDateMillis = startDateMillis,
        initialSelectedEndDateMillis = endDateMillis,
        initialDisplayMode = DisplayMode.Picker,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                if (startLimitMillis != null && utcTimeMillis < startLimitMillis) return false
                if (endLimitMillis != null && utcTimeMillis > endLimitMillis) return false
                return true
            }
        }
    )

    // Update displayed year when state changes
    LaunchedEffect(dateRangePickerState.displayedMonthMillis) {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = dateRangePickerState.displayedMonthMillis
        displayedYear = calendar.get(Calendar.YEAR)
    }

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
        navigationContentColor = Color(colors.navigationContentColor)
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
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnClickOutside = true,
            dismissOnBackPress = true
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    onDismiss()
                },
            contentAlignment = Alignment.Center,
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .fillMaxHeight(0.75f)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { /* Prevent click through */ },
                colors = CardDefaults.cardColors(containerColor = Color(colors.containerColor))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 16.dp, horizontal = 8.dp)
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

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        Crossfade(
                            showYearPicker,
                            animationSpec = spring(
                                Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            )
                        ) {
                            if (it) {
                                YearPickerContent(
                                    selectedYear = displayedYear,
                                    currentYear = Calendar.getInstance().get(Calendar.YEAR),
                                    colors = colors,
                                    onYearSelected = { year ->
                                        val calendar = Calendar.getInstance()
                                        calendar.timeInMillis =
                                            dateRangePickerState.displayedMonthMillis
                                        calendar.set(Calendar.YEAR, year)
                                        dateRangePickerState.displayedMonthMillis =
                                            calendar.timeInMillis
                                        displayedYear = year
                                        showYearPicker = false
                                    },
                                    onBack = { showYearPicker = false }
                                )
                            } else {
                                Column(modifier = Modifier.fillMaxSize()) {
                                    DateRangePicker(
                                        state = dateRangePickerState,
                                        colors = customColors,
                                        title = {},
                                        headline = {
                                            DateRangePickerDefaults.DateRangePickerHeadline(
                                                selectedStartDateMillis = dateRangePickerState.selectedStartDateMillis,
                                                selectedEndDateMillis = dateRangePickerState.selectedEndDateMillis,
                                                displayMode = dateRangePickerState.displayMode,
                                                remember { DatePickerDefaults.dateFormatter() },
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .align(Alignment.CenterHorizontally)
                                                    .clickable(
                                                        indication = null,
                                                        interactionSource = remember { MutableInteractionSource() }
                                                    ) {
                                                        showYearPicker = true
                                                    }
                                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                                contentColor = customColors.headlineContentColor,
                                            )
                                        },
                                        showModeToggle = false,
                                        modifier = Modifier.fillMaxSize(),
                                    )
                                }
                            }
                        }
                    }

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
                                onDateRangeSelected(
                                    dateRangePickerState.selectedStartDateMillis,
                                    dateRangePickerState.selectedEndDateMillis
                                )
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
}


@Composable
private fun YearPickerContent(
    selectedYear: Int,
    currentYear: Int,
    colors: CustomDateRangePicker.CustomDateRangePickerColors,
    onYearSelected: (Int) -> Unit,
    onBack: () -> Unit
) {
    val years = remember { (1900..2100).toList() }
    val listState = rememberLazyGridState()

    LaunchedEffect(Unit) {
        val index = years.indexOf(selectedYear).coerceAtLeast(0)
        listState.scrollToItem(index.coerceIn(0, years.size - 1))
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Header with back button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    painterResource(R.drawable.ic_arrow_back),
                    contentDescription = "Back",
                    tint = Color(colors.navigationContentColor)
                )
            }
            Text(
                text = "Select Year",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color(colors.headlineContentColor),
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(years) { year ->
                val isSelected = year == selectedYear
                val isCurrent = year == currentYear

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .clickable { onYearSelected(year) },
                    shape = RoundedCornerShape(24.dp),
                    color = when {
                        isSelected -> Color(colors.selectedYearContainerColor)
                        else -> Color.Transparent
                    }
                ) {
                    Text(
                        text = year.toString(),
                        fontSize = 16.sp,
                        fontWeight = if (isSelected || isCurrent) FontWeight.Bold else FontWeight.Normal,
                        color = when {
                            isSelected -> Color(colors.selectedYearContentColor)
                            isCurrent -> Color(colors.currentYearContentColor)
                            else -> Color(colors.yearContentColor)
                        },
                        modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

class DateRangePickerDialogFragment : DialogFragment() {

    companion object {
        private const val ARG_START_DATE = "start_date"
        private const val ARG_END_DATE = "end_date"
        private const val ARG_START_LIMIT = "start_limit"
        private const val ARG_END_LIMIT = "end_limit"
        private const val ARG_TITLE = "title"
        private const val ARG_COLORS = "colors"
        private const val ARG_POSITIVE_CONFIG = "positive_config"
        private const val ARG_NEGATIVE_CONFIG = "negative_config"

        fun newInstance(
            startDateMillis: Long?,
            endDateMillis: Long?,
            startLimitMillis: Long?,
            endLimitMillis: Long?,
            title: String,
            colors: CustomDateRangePicker.CustomDateRangePickerColors,
            positiveButtonConfig: CustomDateRangePicker.DateRangePickerButtonConfig,
            negativeButtonConfig: CustomDateRangePicker.DateRangePickerButtonConfig,
            callBack: ((Long?, Long?) -> Unit)? = null
        ): DateRangePickerDialogFragment {
            val fragment = DateRangePickerDialogFragment()
            fragment.arguments = Bundle().apply {
                if (startDateMillis != null) putLong(ARG_START_DATE, startDateMillis)
                if (endDateMillis != null) putLong(ARG_END_DATE, endDateMillis)
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

    private var callBack: ((Long?, Long?) -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val args = requireArguments()
        val startDateMillis =
            if (args.containsKey(ARG_START_DATE)) args.getLong(ARG_START_DATE) else null
        val endDateMillis = if (args.containsKey(ARG_END_DATE)) args.getLong(ARG_END_DATE) else null
        val startLimitMillis =
            if (args.containsKey(ARG_START_LIMIT)) args.getLong(ARG_START_LIMIT) else null
        val endLimitMillis =
            if (args.containsKey(ARG_END_LIMIT)) args.getLong(ARG_END_LIMIT) else null
        val title = args.getString(ARG_TITLE, "")
        val colors =
            args.getParcelable<CustomDateRangePicker.CustomDateRangePickerColors>(ARG_COLORS)
                ?: CustomDateRangePicker.CustomDateRangePickerColors()
        val positiveButtonConfig =
            args.getParcelable<CustomDateRangePicker.DateRangePickerButtonConfig>(
                ARG_POSITIVE_CONFIG
            )
                ?: CustomDateRangePicker.DateRangePickerButtonConfig()
        val negativeButtonConfig =
            args.getParcelable<CustomDateRangePicker.DateRangePickerButtonConfig>(
                ARG_NEGATIVE_CONFIG
            )
                ?: CustomDateRangePicker.DateRangePickerButtonConfig()

        val dialog = Dialog(requireContext())
        val composeView = ComposeView(requireContext())

        composeView.setContent {
            MaterialTheme {
                DateRangePickerDialog(
                    startDateMillis = startDateMillis,
                    endDateMillis = endDateMillis,
                    startLimitMillis = startLimitMillis,
                    endLimitMillis = endLimitMillis,
                    title = title,
                    colors = colors,
                    positiveButtonConfig = positiveButtonConfig,
                    negativeButtonConfig = negativeButtonConfig,
                    onDismiss = { dismiss() },
                    onDateRangeSelected = { startMillis, endMillis ->
                        callBack?.invoke(startMillis, endMillis)
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