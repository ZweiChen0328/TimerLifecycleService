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

package com.developergunda.timerlifecycleservice.model

import androidx.lifecycle.*
import com.developergunda.timerlifecycleservice.data.Time
import com.developergunda.timerlifecycleservice.data.TimeDao

import kotlinx.coroutines.launch

/**
 * View Model to keep a reference to the Inventory repository and an up-to-date list of all items.
 *
 */
class TimeViewModel(private val timeDao: TimeDao) : ViewModel() {

    // Cache all items form the database using LiveData.
    val allItems: LiveData<List<Time>> = timeDao.getItems().asLiveData()


    /**
     * Updates an existing Item in the database.
     */
    fun updateItem(
        itemId: Int,
        itemName: String,
        itemPrice: String,
        itemCount: String
    ) {
        val updatedItem = getUpdatedItemEntry(itemId, itemName, itemPrice, itemCount)
        updateItem(updatedItem)
    }


    /**
     * Launching a new coroutine to update an item in a non-blocking way
     */
    private fun updateItem(item: Time) {
        viewModelScope.launch {
            timeDao.update(item)
        }
    }

    /**
     * Inserts the new Item into database.
     */
    fun addNewItem(itemSuccess: String, itemTime: String, itemDate: String){
        val newItem = getNewItemEntry(itemSuccess, itemTime, itemDate)
        insertItem(newItem)
    }

    /**
     * Launching a new coroutine to insert an item in a non-blocking way
     */
    private fun insertItem(item: Time) {
        viewModelScope.launch {
            timeDao.insert(item)
        }
    }

    /**
     * Launching a new coroutine to delete an item in a non-blocking way
     */
    fun deleteItem(item: Time) {
        viewModelScope.launch {
            timeDao.delete(item)
        }
    }

    /**
     * Retrieve an item from the repository.
     */
    fun retrieveItem(id: Int): LiveData<Time> {
        return timeDao.getItem(id).asLiveData()
    }

    fun retrieveId(timeDate: Long): LiveData<Time> {
        return timeDao.getId(timeDate).asLiveData()
    }

    /**
     * Returns true if the EditTexts are not empty
     */
    fun isEntryValid(itemName: String, itemPrice: String, itemCount: String): Boolean {
        if (itemName.isBlank() || itemPrice.isBlank() || itemCount.isBlank()) {
            return false
        }
        return true
    }

    /**
     * Returns an instance of the [Item] entity class with the item info entered by the user.
     * This will be used to add a new entry to the Inventory database.
     */
    private fun getNewItemEntry(itemSuccess: String, itemTime: String, itemDate: String): Time {
        return Time(
            timeSuccess = itemSuccess.toBoolean(),
            time = itemTime.toLong(),
            timeDate = itemDate.toLong()
        )
    }

    /**
     * Called to update an existing entry in the Inventory database.
     * Returns an instance of the [Item] entity class with the item info updated by the user.
     */
    private fun getUpdatedItemEntry(
        itemId: Int,
        itemSuccess: String,
        itemTime: String,
        itemDate: String
    ): Time {
        return Time(
            id = itemId,
            timeSuccess = itemSuccess.toBoolean(),
            time = itemTime.toLong(),
            timeDate = itemDate.toLong()
        )
    }
}

/**
 * Factory class to instantiate the [ViewModel] instance.
 */
class TimeViewModelFactory(private val itemDao: TimeDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TimeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TimeViewModel(itemDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
