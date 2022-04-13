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
package com.developergunda.timerlifecycleservice.data


import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Database access object to access the Inventory database
 */
@Dao
interface TimeDao {

    @Query("SELECT * from time ORDER BY Date ASC")
    fun getItems(): Flow<List<Time>>

    @Query("SELECT * from time WHERE id = :id")
    fun getItem(id: Int): Flow<Time>

    @Query("SELECT * from time WHERE Date = :timeDate")
    fun getId(timeDate: Long): Flow<Time>

    // Specify the conflict strategy as IGNORE, when the user tries to add an
    // existing Item into the database Room ignores the conflict.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: Time)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(item: Time)

    @Delete
    suspend fun delete(item: Time)
}
