package ru.practicum.android.diploma.features.common.presentation.recycler

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.ItemSearchBinding
import ru.practicum.android.diploma.features.common.presentation.models.VacancySearchUI

class VacancyViewHolder(private val binding: ItemSearchBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(vacancy: VacancySearchUI) {
        val cornerRadius = binding.root.context.resources.getDimensionPixelSize(R.dimen.radius_3x)
        with(binding) {
            professionTextView.text = vacancy.formatedProfession
            fieldTextView.text = vacancy.employerName
            wagesTextView.text = vacancy.formatedSalary

            Glide.with(logoImageView.context)
                .load(vacancy.employerLogoUrl)
                .placeholder(R.drawable.placeholder_32px)
                .transform(CenterInside(), RoundedCorners(cornerRadius))
                .into(logoImageView)
        }
    }

}
