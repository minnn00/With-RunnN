package com.with_runn

import android.view.LayoutInflater
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

fun populateChips(
    chipGroup: ChipGroup,
    inflater: LayoutInflater,
    keywords: List<String>,
    onClick: ((Chip) -> Unit)? = null
){
    chipGroup.removeAllViews();

    for (keyword in keywords){
        val chip = inflater.inflate(R.layout.view_chip, chipGroup, false) as Chip
        chip.text = keyword
        chip.setOnClickListener { onClick?.invoke(chip) }
        chipGroup.addView(chip)
    }
}