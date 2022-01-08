/*
 * Copyright (C) 2019 - 2022 Vishnu Sanal. T
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

@file:Suppress("HardCodedStringLiteral")

package phone.vishnu.quotes.helper

object Constants {
    const val ACRA_STACK_TRACE = "ACRA_STACK_TRACE"
    const val ACRA_ANDROID_VERSION = "ACRA_ANDROID_VERSION"
    const val ACRA_APP_VERSION_CODE = "ACRA_APP_VERSION_CODE"
    const val ACRA_APP_VERSION_NAME = "ACRA_APP_VERSION_NAME"
    const val ACRA_PACKAGE_NAME = "ACRA_PACKAGE_NAME"
    const val ACRA_USER_APP_START_DATE = "ACRA_USER_APP_START_DATE"

    const val DATA = "data"
    const val QUOTE = "quote"
    const val AUTHOR = "author"
    const val FONTS = "fonts"
    const val IMAGES = "images"

    const val SHORTCUT_FAV_ACTION = "phone.vishnu.quotes.openFavouriteFragment"
    const val SHORTCUT_RANDOM_ACTION = "phone.vishnu.quotes.shareRandomQuote"

    const val WIDGET_FAV_ACTION = "phone.vishnu.quotes.widgetFavClicked"
    const val WIDGET_SHARE_ACTION = "phone.vishnu.quotes.widgetShareClicked"

    const val CANCELLABLE_EXTRA = "isCancellable"

    const val PICK_BG_COLOR_REQ_CODE = 0
    const val PICK_CARD_COLOR_REQ_CODE = 1
    const val PICK_FONT_COLOR_REQ_CODE = 2

    const val COLOR_REQUEST_CODE = "ColorRequestCode"

    const val TOUR_IMG_EXTRA = "tourImg"
    const val TOUR_TITLE_EXTRA = "tourTitle"
    const val TOUR_DESCRIPTION_EXTRA = "tourDescription"
}
