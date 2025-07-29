package com.with_runn

import android.content.res.Resources
import android.icu.util.Calendar
import android.view.LayoutInflater
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

fun populateChips(
    chipGroup: ChipGroup,
    inflater: LayoutInflater,
    keywords: List<String>,
    chipLayoutRes: Int = R.layout.view_chip,
    onClick: ((Chip) -> Unit)? = null
){
    chipGroup.removeAllViews();

    for (keyword in keywords){
        val chip = inflater.inflate(chipLayoutRes, chipGroup, false) as Chip
        chip.text = keyword
        chip.setOnClickListener { onClick?.invoke(chip) }
        chipGroup.addView(chip)
    }
}

fun formatToHHMM(time: Int): String {
    val s = time.toString().padStart(4, '0')
    val hour = s.substring(0, 2)
    val minute = s.substring(2, 4)
    return "$hour:$minute"
}

fun getTodayWeekIndex(): Int {
    val calendar = Calendar.getInstance()
    return when (calendar.get(Calendar.DAY_OF_WEEK)) {
        Calendar.MONDAY -> 0
        Calendar.TUESDAY -> 1
        Calendar.WEDNESDAY -> 2
        Calendar.THURSDAY -> 3
        Calendar.FRIDAY -> 4
        Calendar.SATURDAY -> 5
        Calendar.SUNDAY -> 6
        else -> 0
    }
}

fun parseHours(text: String?): Pair<Int, Int> {
    // 예시: "Monday: 9:00 AM – 5:00 PM"
    if (text == null || text.contains("Closed", ignoreCase = true)) return Pair(-1, -1)

    val timeRegex = Regex("""(\d{1,2}):?(\d{0,2})\s?(AM|PM)""")
    val matches = timeRegex.findAll(text)

    val times = matches.map { match ->
        val hour = match.groupValues[1].toInt()
        val minute = match.groupValues[2].takeIf { it.isNotBlank() }?.toIntOrNull() ?: 0
        val ampm = match.groupValues[3]
        val h24 = when {
            ampm.equals("AM", true) && hour == 12 -> 0
            ampm.equals("AM", true) -> hour
            ampm.equals("PM", true) && hour < 12 -> hour + 12
            else -> hour
        }
        h24 * 100 + minute
    }.toList()

    return when (times.size) {
        2 -> Pair(times[0], times[1])
        else -> Pair(-1, -1)
    }
}

fun parseOperatingHours(raw: String?): Pair<Int, Int> {
    if (raw == null || !raw.contains("~")) return 0 to 0
    val parts = raw.split("~").map { it.trim() }
    val open = parts.getOrNull(0)?.replace(":", "")?.toIntOrNull() ?: 0
    val close = parts.getOrNull(1)?.replace(":", "")?.toIntOrNull() ?: 0
    return open to close
}

fun getCurrentTimeInt(): Int {
    val now = java.util.Calendar.getInstance()
    val hour = now.get(java.util.Calendar.HOUR_OF_DAY)
    val minute = now.get(java.util.Calendar.MINUTE)
    return hour * 100 + minute
}

val Int.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()