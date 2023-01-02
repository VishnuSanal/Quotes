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

package phone.vishnu.quotes.fragment

import android.content.DialogInterface
import android.os.Bundle
import android.view.KeyEvent
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import phone.vishnu.quotes.R

open class BaseBottomSheetDialogFragment : BottomSheetDialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onResume() {
        super.onResume()

        if (dialog != null) {
            dialog?.setOnKeyListener(
                fun(_: DialogInterface?, keyCode: Int, event: KeyEvent?): Boolean {
                    if (keyCode == KeyEvent.KEYCODE_BACK && event?.action == KeyEvent.ACTION_DOWN && tag != null) {
                        when (tag) {
                            "BGOptionPickFragment" ->
                                BGOptionPickFragment.newInstance(true)
                                    .show(requireActivity().supportFragmentManager, null)
                            "FavoriteFragment" ->
                                FavoriteFragment.newInstance()
                                    .show(requireActivity().supportFragmentManager, null)
                            "FontOptionPickFragment" ->
                                FontOptionPickFragment.newInstance()
                                    .show(requireActivity().supportFragmentManager, null)
                            "SettingsFragment" ->
                                SettingsFragment.newInstance()
                                    .show(requireActivity().supportFragmentManager, null)
                        }
                    }

                    return false
                }
            )
        }
    }
}
