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

package phone.vishnu.quotes.model;

import android.net.Uri;
import androidx.annotation.Nullable;
import java.util.Objects;

public class UnsplashItem {

    private Uri thumbUri, regularUri, downloadTrigger;
    private Uri userProfile, userProfileLink;
    private String userName;

    public UnsplashItem(
            Uri thumbUri,
            Uri regularUri,
            Uri downloadTrigger,
            String userName,
            Uri userProfile,
            @Nullable String userProfileLink) {
        this.thumbUri = thumbUri;
        this.regularUri = regularUri;
        this.downloadTrigger = downloadTrigger;
        this.userProfile = userProfile;
        this.userProfileLink = userProfileLink == null ? Uri.EMPTY : Uri.parse(userProfileLink);
        this.userName = userName;
    }

    public Uri getThumbUri() {
        return thumbUri;
    }

    public void setThumbUri(Uri thumbUri) {
        this.thumbUri = thumbUri;
    }

    public Uri getRegularUri() {
        return regularUri;
    }

    public void setRegularUri(Uri regularUri) {
        this.regularUri = regularUri;
    }

    public Uri getDownloadTrigger() {
        return downloadTrigger;
    }

    public void setDownloadTrigger(Uri downloadTrigger) {
        this.downloadTrigger = downloadTrigger;
    }

    public Uri getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(Uri userProfile) {
        this.userProfile = userProfile;
    }

    public Uri getUserProfileLink() {
        return userProfileLink;
    }

    public void setUserProfileLink(Uri userProfileLink) {
        this.userProfileLink = userProfileLink;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UnsplashItem that = (UnsplashItem) o;
        return Objects.equals(thumbUri, that.thumbUri)
                && Objects.equals(regularUri, that.regularUri);
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
