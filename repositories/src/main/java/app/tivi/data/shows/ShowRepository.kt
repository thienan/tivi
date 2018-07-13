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

package app.tivi.data.shows

import app.tivi.data.entities.TiviShow
import javax.inject.Inject

class ShowRepository @Inject constructor(
    private val tmdbShowDataSource: TmdbShowDataSource,
    private val traktShowDataSource: TraktShowDataSource,
    private val localShowStore: LocalShowStore
) : ShowStore, ShowDataSource {

    override fun observeShow(showId: Long) = localShowStore.observeShow(showId)

    /**
     * Updates the show with the given id from all network sources, saves the result to the database
     */
    suspend fun getShow(showId: Long): TiviShow {
        val traktResult = traktShowDataSource.getShow(showId)
        val tmdbResult = tmdbShowDataSource.getShow(showId)

        // TODO now save
    }
}