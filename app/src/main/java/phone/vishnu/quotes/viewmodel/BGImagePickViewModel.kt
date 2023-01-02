/*
 * Copyright (C) 2019 - 2023 Vishnu Sanal. T
 *
 * This file is part of Quotes Status Creator.
 *
 * Quotes Status Creator is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package phone.vishnu.quotes.viewmodel

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.keenencharles.unsplash.Unsplash
import com.keenencharles.unsplash.models.ContentFilter
import com.keenencharles.unsplash.models.Orientation
import phone.vishnu.quotes.model.UnsplashItem
import java.io.File

class BGImagePickViewModel(application: Application) : AndroidViewModel(application) {

    private var presentImagesArrayList: ArrayList<Uri>? = null
    private var presentImagesMutableLiveData: MutableLiveData<List<Uri>>? = null

    private var searchImagesArrayList: ArrayList<UnsplashItem>? = null
    private var searchImagesMutableLiveData: MutableLiveData<List<UnsplashItem>>? = null

    val presentImages: MutableLiveData<List<Uri>>?
        get() {
            if (presentImagesArrayList == null || presentImagesArrayList!!.isEmpty()) loadPresentImages()
            return presentImagesMutableLiveData
        }

    private fun loadPresentImages() {
        presentImagesMutableLiveData = MutableLiveData()
        presentImagesArrayList = ArrayList()
        val files = getApplication<Application>().applicationContext.fileList()

        if (files != null) {
            for (s in files) {
                val file = File(s)
                if (file.absolutePath.endsWith(".jpg")) {
                    if (file.name == "background.jpg" || file.name == "screenshot.jpg") continue
                    presentImagesArrayList!!.add(Uri.fromFile(file))
                    presentImagesMutableLiveData!!.value = presentImagesArrayList
                }
            }
        }
    }

    fun searchImages(query: String, key: String): MutableLiveData<List<UnsplashItem>>? {
        searchImagesMutableLiveData = MutableLiveData()
        searchImagesArrayList = ArrayList()

        val unsplash = Unsplash(key)

        unsplash.photos.search(
            query,
            1,
            30,
            null,
            Orientation.PORTRAIT,
            ContentFilter.HIGH,
            null,
            onComplete = {
                for (photo in it.results) {
                    val regularUri = Uri.parse(photo.urls?.regular)

                    if (!getApplication<Application>().applicationContext.fileList()
                        .contains(regularUri.lastPathSegment + ".jpg")
                    ) {
                        searchImagesArrayList!!.add(
                            UnsplashItem(
                                Uri.parse(photo.urls?.thumb),
                                regularUri,
                                Uri.parse(photo.links?.downloadLocation),
                                photo.user?.name,
                                Uri.parse(photo.user?.profileImage?.small),
                                "https://unsplash.com/@${photo.user?.username}?utm_source=Quotes%20Status%20Creator&utm_medium=referral"
                            )
                        )
                    }
                    searchImagesMutableLiveData!!.value = searchImagesArrayList
                }
            },
            onError = {
                Log.e("vishnu", it)
            }
        )

        return searchImagesMutableLiveData
    }
}
