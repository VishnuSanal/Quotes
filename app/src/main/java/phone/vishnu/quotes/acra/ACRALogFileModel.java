/*
 * Copyright (C) 2019 - 2024 Vishnu Sanal. T
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

package phone.vishnu.quotes.acra;

public class ACRALogFileModel {

    private String STACK_TRACE, LOGCAT;

    public ACRALogFileModel(String STACK_TRACE, String LOGCAT) {
        this.STACK_TRACE = STACK_TRACE;
        this.LOGCAT = LOGCAT;
    }

    public String getStackTrace() {
        return STACK_TRACE;
    }

    public void setStackTrace(String STACK_TRACE) {
        this.STACK_TRACE = STACK_TRACE;
    }

    public String getLogcat() {
        return LOGCAT;
    }

    public void setLogcat(String LOGCAT) {
        this.LOGCAT = LOGCAT;
    }

    @Override
    public String toString() {
        return "\nACRALogFileModel:\n\n"
                + "STACK_TRACE: "
                + STACK_TRACE
                + '\n'
                + "LOGCAT: "
                + LOGCAT
                + '\n';
    }
}
