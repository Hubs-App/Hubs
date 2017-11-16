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

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Pair;
import android.view.MenuItem;
import android.widget.EditText;

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
import java.util.List;
import java.util.Map;

import cn.nekocode.hot.R;
import cn.nekocode.hot.base.BaseActivity;
import cn.nekocode.hot.data.model.Column;
import cn.nekocode.hot.databinding.ActivityColumnConfigBinding;
import cn.nekocode.hot.util.DividerItemDecoration;

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
public class ColumnConfigActivity extends BaseActivity implements ConfigPropertyListAdapter.UIEventListener {
    private Globals mGlobals = new Globals();
    private ActivityColumnConfigBinding mBinding;
    public Column mColumn;
    public List<Pair<String, Object>> mProperties;
    private ConfigPropertyListAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_column_config);
        mColumn = getIntent().getParcelableExtra("column");
        mColumn.getUserConfig().put("NAME", mColumn.getName());

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
        mAdapter = new ConfigPropertyListAdapter((mProperties = listOf(mColumn)));
        mAdapter.setUIEventListener(this);

        mBinding.recyclerView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mBinding.recyclerView.setAdapter(mAdapter);
        mBinding.recyclerView.setItemAnimator(null);
        mBinding.recyclerView.addItemDecoration(DividerItemDecoration.obtainDefault(this));
        mBinding.getRoot().setFocusableInTouchMode(true);
    }

    /**
     * Convert to sortted property list
     */
    private static ArrayList<Pair<String, Object>> listOf(Column column) {
        final ArrayList<Pair<String, Object>> list = new ArrayList<>();
        for (Map.Entry<String, Object> entry : column.getUserConfig().entrySet()) {
            list.add(Pair.create(entry.getKey(), entry.getValue()));
        }
        Collections.sort(list, (o1, o2) -> o1.first.compareTo(o2.first));
        return list;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onValueEdited(String key, ConfigPropertyListAdapter.RevertibleEditText valueEt) {
        /*
          Check if the value is legal
         */
        boolean failed = false;
        try {
            mGlobals.load("rlt=" + valueEt.getText()).call();
            final LuaValue rlt = mGlobals.get("rlt");
            if (!rlt.isnil()) {
                // Legal
                if (rlt instanceof LuaDouble) {
                    valueEt.setText(String.valueOf(rlt.todouble()));
                } else if (rlt instanceof LuaInteger) {
                    valueEt.setText(String.valueOf(rlt.toint()));
                } else if (rlt instanceof LuaString) {
                    valueEt.setText("\"" + rlt.tojstring() + "\"");
                } else if (rlt instanceof LuaBoolean) {
                    valueEt.setText(rlt.toboolean() ? "true" : "false");
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
            valueEt.revert();
        }
    }
}
