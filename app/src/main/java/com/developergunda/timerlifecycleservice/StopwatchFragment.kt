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

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.developergunda.timerlifecycleservice.databinding.StopwatchBinding
import com.developergunda.timerlifecycleservice.model.TimeViewModel
import com.developergunda.timerlifecycleservice.model.TimeViewModelFactory
import com.developergunda.timerlifecycleservice.model.TimerEvent
import com.developergunda.timerlifecycleservice.service.TimerService
import com.developergunda.timerlifecycleservice.util.TimerUtil
import timber.log.Timber
import kotlin.properties.Delegates

/**
 * Main fragment displaying details for all items in the database.
 */
class StopwatchFragment : Fragment() {
    private val viewModel: TimeViewModel by activityViewModels {
        TimeViewModelFactory(
            (activity?.application as App).database.timeDao()
        )
    }

    companion object {
        val TOTALTIME = "setTimeValue"
        val RECORDDATE = "recordDate"
    }

    private var totalTime by Delegates.notNull<Long>()
    private var date by Delegates.notNull<Long>()

    private var _binding: StopwatchBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            totalTime = it.getLong(TOTALTIME)
            date = it.getLong(RECORDDATE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = StopwatchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.giveUp.setOnClickListener {
            sendCommandToService(ACTION_GIVEUP_SERVICE)
        }
        TimerService.totalTime.postValue(totalTime)
        sendCommandToService(ACTION_START_SERVICE)
        setObservers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun sendCommandToService(action: String) {
        activity?.startService(Intent(activity, TimerService::class.java).apply {
            this.action = action
        })
    }

    private fun setObservers() {
        viewModel.addNewItem("false", totalTime.toString(), date.toString())
        TimerService.timerInMillis.observe(viewLifecycleOwner, Observer {
            binding.tvTimer.text = TimerUtil.getFormattedTime(it, true)
        })

        TimerService.timerEvent.observe(viewLifecycleOwner, Observer {
            when (it) {
                is TimerEvent.START -> {
                }
                else -> {
                    val action =
                        StopwatchFragmentDirections.actionStopwatchFragmentToResultFragment(
                            totalTime,
                            date
                        )
                    this.findNavController().navigate(action)
                }
            }
        })
    }
}
