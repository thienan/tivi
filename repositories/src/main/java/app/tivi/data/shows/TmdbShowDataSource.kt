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
import app.tivi.data.entities.copyDynamic
import app.tivi.extensions.fetchBodyWithRetry
import com.uwetrottmann.tmdb2.Tmdb
import javax.inject.Inject

class TmdbShowDataSource @Inject constructor(
    private val tmdbIdMapper: ShowTmdbIdMapper,
    private val tmdb: Tmdb
) : ShowDataSource {
    override suspend fun getShow(showId: Long): TiviShow {
        val tmdbId = tmdbIdMapper.map(showId)!!

        val tmdbShow = tmdb.tvService().tv(tmdbId).fetchBodyWithRetry()

        return TiviShow().copyDynamic {
            this.tmdbId = tmdbShow.id
            if (title.isNullOrEmpty()) title = tmdbShow.name
            if (summary.isNullOrEmpty()) summary = tmdbShow.overview
            tmdbBackdropPath = tmdbShow.backdrop_path
            tmdbPosterPath = tmdbShow.poster_path
            if (homepage.isNullOrEmpty()) homepage = tmdbShow.homepage
        }
    }
}