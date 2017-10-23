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

package cn.nekocode.hot.base;

import android.support.v4.app.Fragment;

import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;

import io.reactivex.Single;
import io.reactivex.subjects.BehaviorSubject;

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
public abstract class BaseLazyLoadFragment extends Fragment {
    private final BehaviorSubject<Boolean> mCanFirstLoad = BehaviorSubject.createDefault(false);


    public final void tryFirstLoad() {
        if (!mCanFirstLoad.getValue()) {
            mCanFirstLoad.onNext(true);
        }
    }

    protected final void doFirstLoad(Runnable load) {
        Single.<Runnable>create(emitter -> {
            emitter.onSuccess(load);
        })
                .zipWith(
                        mCanFirstLoad.filter(bool -> bool).firstOrError(),
                        (runnable, bool) -> runnable)
                .to(AutoDispose.with(AndroidLifecycleScopeProvider.from(this)).forSingle())
                .subscribe(runnable -> {
                    runnable.run();
                });
    }
}
