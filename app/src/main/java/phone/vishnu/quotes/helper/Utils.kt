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

package phone.vishnu.quotes.helper

import android.content.Context
import android.content.res.Resources
import android.util.DisplayMetrics
import phone.vishnu.quotes.model.Quote
import kotlin.math.roundToInt

class Utils {

	companion object {

		val sampleQuote: Quote = Quote(
			"Twenty years from now you will be more disappointed by the things that you didn’t do than by the ones you did do, so throw off the bowlines, sail away from safe harbor, catch the trade winds in your sails.  Explore, Dream, Discover",
			"Mark Twain",
		)

		fun DPtoPX(context: Context, DP: Int): Int {
			return (DP * (context.resources.displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
		}

		fun getScreenWidth(): Int {
			return Resources.getSystem().displayMetrics.widthPixels
		}

		fun getScreenHeight(): Int {
			return Resources.getSystem().displayMetrics.heightPixels
		}
	}
}
