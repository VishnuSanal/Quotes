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

@file:Suppress("HardCodedStringLiteral")

package phone.vishnu.quotes.helper

object Constants {
	const val ACRA_STACK_TRACE = "ACRA_STACK_TRACE"
	const val ACRA_ANDROID_VERSION = "ACRA_ANDROID_VERSION"
	const val ACRA_APP_VERSION_CODE = "ACRA_APP_VERSION_CODE"
	const val ACRA_APP_VERSION_NAME = "ACRA_APP_VERSION_NAME"
	const val ACRA_PACKAGE_NAME = "ACRA_PACKAGE_NAME"
	const val ACRA_USER_APP_START_DATE = "ACRA_USER_APP_START_DATE"
	const val ACRA_REPORT_FILE_KEY = "REPORT_FILE"

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
	const val COLOR_PICK_REQUEST_CODE = "ColorPickRequestCode"

	const val TOUR_IMG_EXTRA = "tourImg"
	const val TOUR_TITLE_EXTRA = "tourTitle"
	const val TOUR_DESCRIPTION_EXTRA = "tourDescription"

	const val NOTIFICATION_REQUEST_CODE = 2222
	const val NOTIFICATION_CHANNEL_ID = "phone.vishnu.quotes"
	const val NOTIFICATION_CHANNEL_NAME = "QuotesNotificationChannel"
	const val NOTIFICATION_CLICK = "NotificationClick"
	const val NOTIFICATION_FAV_ACTION = "FavButton"
	const val NOTIFICATION_SHARE_ACTION = "ShareButton"

	const val WIDGET_UPDATE_ACTION = "phone.vishnu.quotes.QUOTE_WIDGET_UPDATE"
	const val WIDGET_CLICK_ACTION = "phone.vishnu.quotes.WIDGET_CLICK_LISTENER"
	const val WIDGET_REQ_CODE = "WIDGET_REQ_CODE"
	const val WIDGET_FAVOURITE_REQ_CODE = 1
	const val WIDGET_SHARE_REQ_CODE = 2
}
