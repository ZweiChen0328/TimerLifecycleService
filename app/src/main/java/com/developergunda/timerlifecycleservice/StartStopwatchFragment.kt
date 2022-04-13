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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.developergunda.timerlifecycleservice.databinding.StartStopwatchBinding

/**
 * Main fragment displaying details for all items in the database.
 */
class StartStopwatchFragment : Fragment() {
    private var _binding: StartStopwatchBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = StartStopwatchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.hourPicker.maxValue = 2
        binding.minutePicker.maxValue = 59
        binding.secondPicker.maxValue = 59
        binding.start.setOnClickListener {
            val totalTime =
                binding.secondPicker.value * 1000 + binding.minutePicker.value * 1000 * 60 + binding.hourPicker.value * 1000 * 60 * 60
            val action =
                StartStopwatchFragmentDirections.actionStartStopwatchFragmentToStopwatchFragment(
                    totalTime.toLong(), System.currentTimeMillis()
                )
            this.findNavController().navigate(action)
        }
        binding.bottomNavigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_record -> record()
            }
            true
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun record() {
        findNavController().navigate(R.id.action_startStopwatchFragment_to_recordFragment)
    }
}
