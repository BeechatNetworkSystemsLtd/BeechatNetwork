/*
 * This file is part of BeeChat.
 *
 *     BeeChat is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     BeeChat is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with BeeChat.  If not, see <https://www.gnu.org/licenses/>.
 */



package src

import javafx.application.Application
import javafx.stage.Stage
import kotlin.jvm.JvmStatic

class Main : Application() {
    override fun start(primaryStage: Stage) {
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            // Display copyright notice

            launch(*args)
        }
    }
}