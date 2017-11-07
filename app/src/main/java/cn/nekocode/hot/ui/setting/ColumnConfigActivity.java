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
    private ActivityColumnConfigBinding mBinding;
    public Column mColumn;
    public List<Pair<String, Object>> mProperties;
    private ConfigPropertyListAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_column_config);
        mColumn = getIntent().getParcelableExtra("column");

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
    }

    /**
     * Convert to sortted property list
     */
    private static ArrayList<Pair<String, Object>> listOf(Column column) {
        final ArrayList<Pair<String, Object>> list = new ArrayList<>();
        list.add(Pair.create("NAME", column.getName()));
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
}
