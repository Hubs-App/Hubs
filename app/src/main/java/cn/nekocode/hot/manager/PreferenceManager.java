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

package cn.nekocode.hot.manager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.nekocode.hot.data.model.Column;
import cn.nekocode.hot.data.model.ColumnPreference;
import cn.nekocode.hot.manager.base.BasePreferenceManager;
import io.reactivex.Observable;

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
public class PreferenceManager extends BasePreferenceManager {
    private final PreferenceDBHelper mDBHelper;


    public PreferenceManager(@NonNull Context context) {
        mDBHelper = new PreferenceDBHelper(context);
    }

    @Override
    @NonNull
    public Observable<List<Column>> getOrderedVisibleColumns(@NonNull List<Column> columns) {
        return loadColumnPreferences(columns)
                .flatMapIterable(columnPreferences -> columnPreferences)
                .filter(preference -> preference.isVisible())
                .map(preference -> preference.getColumn())
                .toList()
                .toObservable();
    }

    @Override
    @NonNull
    public Observable<List<ColumnPreference>> loadColumnPreferences(@NonNull List<Column> columns) {
        return Observable.create(emitter -> {
            final ArrayList<ColumnPreference> preferences = new ArrayList<>();
            final HashMap<String, Column> columnHashMap = new HashMap<>();
            for (Column column : columns) {
                columnHashMap.put(column.getId().toString(), column);
            }

            final SQLiteDatabase db = mDBHelper.getReadableDatabase();
            final Cursor cursor = db.query(PreferenceDBHelper.TABLE_NAME, null, null, null, null, null, "\'order\' ASC");
            final int indexOfColumnId = cursor.getColumnIndex("column_id");
            final int indexOfIsVisible = cursor.getColumnIndex("is_visible");

            int order = 0;
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                final Column column = columnHashMap.remove(cursor.getString(indexOfColumnId));

                if (column != null) {
                    preferences.add(new ColumnPreference(
                            column,
                            cursor.getInt(indexOfIsVisible) == 1,
                            order ++
                    ));
                }
            }

            cursor.close();
            db.close();

            // Add rest columns
            for (Column column : columnHashMap.values()) {
                preferences.add(new ColumnPreference(column, true, order ++));
            }

            emitter.onNext(preferences);
            emitter.onComplete();
        });
    }

    @Override
    public void saveColumnPreferences(@NonNull List<ColumnPreference> preferences) {
        final SQLiteDatabase db = mDBHelper.getWritableDatabase();
        db.execSQL("DELETE FROM " + PreferenceDBHelper.TABLE_NAME);

        for (ColumnPreference preference : preferences) {
            final ContentValues values = new ContentValues();
            values.put("column_id", preference.getColumnId());
            values.put("is_visible", preference.isVisible() ? 1 : 0);
            values.put("order", preference.getOrder());
            db.insert(PreferenceDBHelper.TABLE_NAME, "id", values);
        }

        db.close();
    }


    private class PreferenceDBHelper extends SQLiteOpenHelper {
        private static final int DB_VERSION = 1;
        private static final String DB_NAME = "preference.db";
        private static final String TABLE_NAME = "\'preference\'";


        PreferenceDBHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            final String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +
                    " (\'id\' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "\'column_id\' VARCHAR NOT NULL, " +
                    "\'is_visible\' INTEGER NOT NULL, " +
                    "\'order\' INTEGER NOT NULL)";
            sqLiteDatabase.execSQL(sql);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
            final String sql = "DROP TABLE IF EXISTS " + TABLE_NAME;
            sqLiteDatabase.execSQL(sql);
            onCreate(sqLiteDatabase);
        }
    }
}
