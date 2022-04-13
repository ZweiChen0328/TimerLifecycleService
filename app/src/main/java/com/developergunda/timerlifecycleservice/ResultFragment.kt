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

import android.content.ClipData
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.developergunda.timerlifecycleservice.data.Time
import com.developergunda.timerlifecycleservice.databinding.ResultBinding
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
class ResultFragment : Fragment() {
    private val viewModel: TimeViewModel by activityViewModels {
        TimeViewModelFactory(
            (activity?.application as App).database.timeDao()
        )
    }

    companion object {
        val TOTALTIME = "totalTime"
        val RECORDDATE = "recordDate"
    }

    private var totalTime by Delegates.notNull<Long>()
    private var date by Delegates.notNull<Long>()


    lateinit var item: Time

    private var _binding: ResultBinding? = null
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
        _binding = ResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.retrieveId(date).observe(this.viewLifecycleOwner) { selectedItem ->
            item = selectedItem
            setObservers()
        }
        binding.seeResult.setOnClickListener {
            val action = ResultFragmentDirections.actionResultFragmentToRecordFragment()
            this.findNavController().navigate(action)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }

    private fun setObservers() {

        TimerService.timerEvent.observe(viewLifecycleOwner, Observer {
            when (it) {
                is TimerEvent.END -> {
                    binding.resultText.text = "成功"
                    viewModel.updateItem(item.id, "true", totalTime.toString(), date.toString())
                }
                else -> {
                    binding.resultText.text = "失敗"
                }
            }
        })
    }
}
