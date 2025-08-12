package com.with_runn

import android.content.res.Resources
import android.icu.util.Calendar
import android.util.Log
import android.view.LayoutInflater
import com.google.android.material.bottomnavigation.BottomNavigationView
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
    if (raw.isNullOrBlank()) return 0 to 0

    val cleaned = raw
        .replace(Regex("\\(.*?\\)"), "")      // 괄호 내 보조 문구 제거: (일 18:00) 등
        .replace("법정공휴일", "")              // 노이즈 제거
        .replace(Regex("[-–—－]"), "-")        // 대시류 정규화  ← 여기 고침!
        .replace(Regex("[〜∼~]"), "~")         // 물결류 정규화
        .replace(Regex("\\s+"), " ")           // 공백 정규화
        .trim()

    // 순수 HH:MM~HH:MM만 있는 경우 빠르게 처리
    val plain = Regex("""^\d{1,2}:\d{2}\s*~\s*\d{1,2}:\d{2}$""")
    if (plain.matches(cleaned)) {
        val (o, c) = cleaned.split("~").map { it.trim() }
        return hhmmFromColon(o) to hhmmFromColon(c)
    }

    // 요일/매일 + 시간 패턴
    val pattern = Regex(
        """(?:(매일|[일월화수목금토](?:\s*[~\-]\s*[일월화수목금토])?(?:\s*,\s*[일월화수목금토](?:\s*[~\-]\s*[일월화수목금토])*)*))\s+(\d{1,2}):(\d{2})\s*[~\-]\s*(\d{1,2}):(\d{2})"""
    )

    val days = charArrayOf('일','월','화','수','목','금','토')
    val todayChar = days[(java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_WEEK) + 6) % 7]

    fun expandDays(spec: String): Set<Char> {
        if (spec == "매일") return days.toSet()
        val set = linkedSetOf<Char>()
        spec.split(Regex("\\s*,\\s*")).forEach { token ->
            val t = token.trim()
            val range = Regex("([일월화수목금토])\\s*[~\\-]\\s*([일월화수목금토])").matchEntire(t)
            if (range != null) {
                val s = range.groupValues[1][0]
                val e = range.groupValues[2][0]
                val si = days.indexOf(s); val ei = days.indexOf(e)
                if (si >= 0 && ei >= 0) {
                    var i = si
                    while (true) {
                        set += days[i]
                        if (i == ei) break
                        i = (i + 1) % 7
                    }
                }
            } else {
                t.firstOrNull { it in days }?.let(set::add)
            }
        }
        return set
    }

    fun toHHmm(h: String, m: String): Int {
        val hh = h.toIntOrNull() ?: return 0
        val mm = m.toIntOrNull() ?: 0
        return if (hh == 24 && mm == 0) 2400 else (hh.coerceIn(0,24) * 100 + mm.coerceIn(0,59))
    }

    var minOpen: Int? = null
    var maxClose: Int? = null

    pattern.findAll(cleaned).forEach { m ->
        val daySpec = m.groupValues[1].trim()
        val o = toHHmm(m.groupValues[2], m.groupValues[3])
        val c = toHHmm(m.groupValues[4], m.groupValues[5])
        val applies = daySpec == "매일" || todayChar in expandDays(daySpec)
        if (applies) {
            minOpen = minOf(minOpen ?: o, o)
            maxClose = maxOf(maxClose ?: c, c)
        }
    }

    return if (minOpen != null && maxClose != null) (minOpen!! to maxClose!!) else 0 to 0
}

private fun hhmmFromColon(s: String): Int {
    val parts = s.trim().split(":")
    val h = parts.getOrNull(0)?.toIntOrNull() ?: return 0
    val m = parts.getOrNull(1)?.toIntOrNull() ?: 0
    return if (h == 24 && m == 0) 2400 else h.coerceIn(0,24) * 100 + m.coerceIn(0,59)
}

fun getCurrentTimeInt(): Int {
    val now = java.util.Calendar.getInstance()
    val hour = now.get(java.util.Calendar.HOUR_OF_DAY)
    val minute = now.get(java.util.Calendar.MINUTE)
    return hour * 100 + minute
}

fun BottomNavigationView.slideDown(){
    animate()
        .translationY(height.toFloat())
        .setDuration(500)
        .start()
}

fun BottomNavigationView.slideUp(){
    animate()
        .translationY(0f)
        .setDuration(500)
        .start()
}

fun formatMinutesToHM(minutes: Int): String {
    val hours = minutes / 60
    val mins = minutes % 60
    return buildString {
        if (hours > 0) append("${hours}H ")
        if (mins > 0) append("${mins}M")
        if (hours == 0 && mins == 0) append("0M")
    }.trim()
}

fun Int.toHM(): String {
    val hours = this / 60
    val mins = this % 60
    return buildString {
        if (hours > 0) append("${hours}H ")
        if (mins > 0) append("${mins}M")
        if (hours == 0 && mins == 0) append("0M")
    }.trim()
}

val Int.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()