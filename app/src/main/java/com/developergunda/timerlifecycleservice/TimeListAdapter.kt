/*
 * Copyright (C) 2021 The Android Open Source Project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.developergunda.timerlifecycleservice

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.developergunda.timerlifecycleservice.data.Time
import com.developergunda.timerlifecycleservice.databinding.RecordItemBinding
import com.developergunda.timerlifecycleservice.util.TimerUtil

/**
 * [ListAdapter] implementation for the recyclerview.
 */

class TimeListAdapter(private val onItemClicked: (Time) -> Unit) :
    ListAdapter<Time, TimeListAdapter.TimeViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeViewHolder {
        return TimeViewHolder(
            RecordItemBinding.inflate(
                LayoutInflater.from(
                    parent.context
                )
            )
        )
    }

    override fun onBindViewHolder(holder: TimeViewHolder, position: Int) {
        val current = getItem(position)
        holder.itemView.setOnClickListener {
            onItemClicked(current)
        }
        holder.bind(current)
    }

    class TimeViewHolder(private var binding: RecordItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Time) {
            if (item.timeSuccess) {
                binding.itemSuccess.text = "成功"
                binding.itemTime.text = TimerUtil.getFormattedTime(item.time, false)
            } else {
                binding.itemSuccess.text = "失敗"
                binding.itemTime.text = ""
            }
            binding.itemDate.text = TimerUtil.getFormattedDay(item.timeDate)
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Time>() {
            override fun areItemsTheSame(oldItem: Time, newItem: Time): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: Time, newItem: Time): Boolean {
                return oldItem.timeDate == newItem.timeDate
            }
        }
    }
}
