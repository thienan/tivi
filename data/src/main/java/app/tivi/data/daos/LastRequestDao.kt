/*
 * Copyright 2018 Google, Inc.
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

package app.tivi.data.daos

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import app.tivi.data.entities.LastRequest
import app.tivi.data.entities.Request
import org.threeten.bp.Instant
import org.threeten.bp.temporal.TemporalAmount

@Dao
abstract class LastRequestDao : EntityDao<LastRequest> {
    @Query("SELECT * FROM last_requests WHERE request = :request AND entity_id = :entityId")
    protected abstract fun lastRequest(request: Request, entityId: Long): LastRequest?

    @Query("SELECT COUNT(*) FROM last_requests WHERE request = :request AND entity_id = :entityId")
    protected abstract fun requestCount(request: Request, entityId: Long): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract override fun insert(entity: LastRequest): Long

    fun hasBeenRequested(request: Request, entityId: Long) = requestCount(request, entityId) > 0

    fun hasNotBeenRequested(request: Request, entityId: Long) = requestCount(request, entityId) <= 0

    fun isRequestBefore(request: Request, entityId: Long, threshold: TemporalAmount): Boolean {
        val lastRequest = lastRequest(request, entityId)
        return when {
            lastRequest != null -> lastRequest.timestamp.isBefore(Instant.now().minus(threshold))
            else -> true
        }
    }

    fun updateLastRequest(request: Request, entityId: Long, timestamp: Instant = Instant.now()) {
        // We just use insert here since we have a unique index and onConflict = REPLACE above
        val r = LastRequest(request = request, entityId = entityId, timestamp = timestamp)
        insert(r)
    }
}