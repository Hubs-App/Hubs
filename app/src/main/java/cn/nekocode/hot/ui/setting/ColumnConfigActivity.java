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

package cn.nekocode.hot.ui.setting;

import android.app.ProgressDialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.MenuItem;
import android.widget.Toast;

import com.evernote.android.state.State;
import com.evernote.android.state.StateSaver;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LoadState;
import org.luaj.vm2.LuaBoolean;
import org.luaj.vm2.LuaDouble;
import org.luaj.vm2.LuaInteger;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.compiler.LuaC;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import cn.nekocode.hot.HotApplication;
import cn.nekocode.hot.R;
import cn.nekocode.hot.base.BaseActivity;
import cn.nekocode.hot.broadcast.BroadcastRouter;
import cn.nekocode.hot.data.model.Column;
import cn.nekocode.hot.databinding.ActivityColumnConfigBinding;
import cn.nekocode.hot.manager.base.BaseColumnManager;
import cn.nekocode.hot.util.DividerItemDecoration;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
public class ColumnConfigActivity extends BaseActivity implements ConfigPropertyListAdapter.UIEventListener {
    private Globals mGlobals = new Globals();
    private BaseColumnManager mColumnManager;
    @State
    public Column mColumn;
    private ActivityColumnConfigBinding mBinding;
    private ConfigPropertyListAdapter mAdapter;
    @State
    public ArrayList<PropertyVO> mPropertyVOs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StateSaver.restoreInstanceState(this, savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_column_config);
        mColumnManager = HotApplication.getDefaultColumnManager(this);

        /*
          Data initialize
         */
        if (mColumn == null) {
            mColumn = getIntent().getParcelableExtra("column");
            // Add name config
            mColumn.getUserConfig().put("NAME", mColumn.getName());

            mPropertyVOs = listOf(mColumn);
        }

        // Setup lua globals
        LoadState.install(mGlobals);
        LuaC.install(mGlobals);
        mColumn.setAllTo(mGlobals);

        /*
          View initialize
         */
        setSupportActionBar(mBinding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Setup the recyclerview
        mAdapter = new ConfigPropertyListAdapter(mPropertyVOs);
        mAdapter.setUIEventListener(this);

        mBinding.recyclerView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mBinding.recyclerView.setAdapter(mAdapter);
        mBinding.recyclerView.setItemAnimator(null);
        mBinding.recyclerView.addItemDecoration(DividerItemDecoration.obtainDefault(this));
        mBinding.getRoot().setFocusableInTouchMode(true);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        StateSaver.saveInstanceState(this, outState);
    }

    /**
     * Convert to sortted view oject list
     */
    private static ArrayList<PropertyVO> listOf(Column column) {
        final ArrayList<PropertyVO> list = new ArrayList<>();
        for (Map.Entry<String, Object> entry : column.getUserConfig().entrySet()) {
            final String key = entry.getKey();
            final Object value = entry.getValue();

            String valueStr = null;
            if (value instanceof String) {
                valueStr = "\"" + value + "\"";

            } else if (value instanceof Integer || value instanceof Long ||
                    value instanceof Float || value instanceof Double || value instanceof Byte) {
                valueStr = String.valueOf(value);

            } else if (value instanceof Boolean) {
                valueStr = ((Boolean) value) ? "true" : "false";
            }

            if (valueStr != null) {
                list.add(new PropertyVO(key, valueStr, valueStr,
                        valueStr.length(), valueStr.length(), false));
            }
        }
        Collections.sort(list, (o1, o2) -> o1.getKey().compareTo(o2.getKey()));
        return list;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                saveAndFinish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onValueEdited(PropertyVO vo) {
        /*
          Check if the value is legal
         */
        boolean failed = false;
        try {
            mGlobals.load("rlt=" + vo.getValue()).call();
            final LuaValue rlt = mGlobals.get("rlt");
            if (!rlt.isnil()) {
                // Legal
                if (rlt instanceof LuaDouble) {
                    mColumn.getUserConfig().put(vo.getKey(), rlt.todouble());
                    vo.setValue(String.valueOf(rlt.todouble()));

                } else if (rlt instanceof LuaInteger) {
                    mColumn.getUserConfig().put(vo.getKey(), rlt.toint());
                    vo.setValue(String.valueOf(rlt.toint()));

                } else if (rlt instanceof LuaString) {
                    mColumn.getUserConfig().put(vo.getKey(), rlt.tojstring());
                    vo.setValue("\"" + rlt.tojstring() + "\"");

                } else if (rlt instanceof LuaBoolean) {
                    mColumn.getUserConfig().put(vo.getKey(), rlt.toboolean());
                    vo.setValue(rlt.toboolean() ? "true" : "false");

                } else {
                    // Not primitive type
                    failed = true;
                }

            } else {
                // Ilegal
                failed = true;
            }

        } catch (Exception e) {
            // Ilegal
            failed = true;
        }

        if (failed) {
            vo.setValue(vo.getOldValue());
        }
    }

    /**
     * Save config and finish
     */
    private void saveAndFinish() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.saving));
        progressDialog.setCancelable(false);
        progressDialog.show();

        Single.<Column>create(emitter -> {
            // Process the rest of edited values
            for (PropertyVO vo : mPropertyVOs) {
                if (vo.isFoucused()) {
                    onValueEdited(vo);
                }
            }

            emitter.onSuccess(mColumn);
        })
                .subscribeOn(Schedulers.io())
                .flatMapCompletable(column ->
                        mColumnManager.writeUserConfig(column.getId(), column.getUserConfig())
                                .subscribeOn(Schedulers.io())
                )
                .observeOn(AndroidSchedulers.mainThread())
                .to(AutoDispose.with(AndroidLifecycleScopeProvider.from(this)).forCompletable())
                .subscribe(() -> {
                    // Send local broadcast
                    BroadcastRouter.IMPL.tellColumnInstalled(this, mColumn.getId().toString());
                    progressDialog.dismiss();
                    finish();

                }, throwable -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, R.string.toast_save_config_failed, Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onBackPressed() {
        saveAndFinish();
    }
}
