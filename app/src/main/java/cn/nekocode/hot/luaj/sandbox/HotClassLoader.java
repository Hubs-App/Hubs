/*
 * Copyright (C) 2017 nekocode (nekocode.cn@gmail.com)
 *
 * This program is free software: you can redistribute it and/or modify
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

package cn.nekocode.hot.luaj.sandbox;

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
public class HotClassLoader extends ClassLoader {
    private static final String[] WHITE_LIST = new String[] {
            "cn\\.nekocode\\.hot\\.data\\.model\\..*",
            "okhttp3\\..*",
            "org\\.jsoup\\..*",
    };
    private static final String[] BLACK_LIST = new String[] {
            "java\\.lang\\.Class",
            "java\\.lang\\.ClassLoader",
            "java\\.lang\\.reflect\\..*",
    };
    private static final String[][] REPLACE_LIST = new String[][] {
            new String[] {"java\\.io\\.File", "cn.nekocode.hot.luaj.sandbox.ShadowFile"}
    };


    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        // Check black-list
        for (String pattern : BLACK_LIST) {
            if (name.matches(pattern)) {
                throw new ClassNotFoundException(name);
            }
        }

        // Check white-list
        for (String pattern : WHITE_LIST) {
            if (name.matches(pattern)) {
                return Class.forName(name);
            }
        }

        // Check replace-list
        for (String[] pair : REPLACE_LIST) {
            if (name.matches(pair[0])) {
                return Class.forName(pair[1]);
            }
        }

        // Use system default class loader
        return ClassLoader.getSystemClassLoader().loadClass(name);
    }
}
