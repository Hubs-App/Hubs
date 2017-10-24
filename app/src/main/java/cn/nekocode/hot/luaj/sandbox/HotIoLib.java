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

import org.luaj.vm2.LuaError;
import org.luaj.vm2.lib.jse.JseIoLib;

import java.io.IOException;

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
public class HotIoLib extends JseIoLib {
    private final PathValidator mPathValidator;


    public HotIoLib(PathValidator validator) {
        this.mPathValidator = validator;
    }

    @Override
    protected File wrapStdin() throws IOException {
        return super.wrapStdin();
    }

    @Override
    protected File wrapStdout() throws IOException {
        return super.wrapStdout();
    }

    @Override
    protected File wrapStderr() throws IOException {
        return super.wrapStderr();
    }

    @Override
    protected File openFile(String filename, boolean readMode, boolean appendMode,
                            boolean updateMode, boolean binaryMode) throws IOException {

        if (mPathValidator == null)
            return super.openFile(filename, readMode, appendMode, updateMode, binaryMode);

        final String absolutePath = mPathValidator.validate(filename);
        if (absolutePath != null) {
            return super.openFile(absolutePath, readMode, appendMode, updateMode, binaryMode);

        } else {
            throw new LuaError("Not a legal file path.");
        }
    }

    @Override
    protected File openProgram(String prog, String mode) throws IOException {
        throw new LuaError("Unsupported operation.");
    }

    @Override
    protected File tmpFile() throws IOException {
        return super.tmpFile();
    }
}
