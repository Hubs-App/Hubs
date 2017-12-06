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

package cn.nekocode.hubs.manager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.nekocode.hubs.data.model.Hub;
import cn.nekocode.hubs.data.model.HubPreference;
import cn.nekocode.hubs.manager.base.BasePreferenceManager;
import io.reactivex.Single;

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
    public Single<List<Hub>> getOrderedVisibleHubs(@NonNull List<Hub> hubs) {
        return loadHubPreferences(hubs)
                .flattenAsObservable(list -> list)
                .filter(preference -> preference.isVisible())
                .map(preference -> preference.getHub())
                .toList();
    }

    @Override
    @NonNull
    public Single<List<HubPreference>> loadHubPreferences(@NonNull List<Hub> hubs) {
        return Single.create(emitter -> {
            final ArrayList<HubPreference> preferences = new ArrayList<>();
            final HashMap<String, Hub> hubHashMap = new HashMap<>();
            for (Hub hub : hubs) {
                hubHashMap.put(hub.getId().toLowerCase(), hub);
            }

            final SQLiteDatabase db = mDBHelper.getReadableDatabase();
            final Cursor cursor = db.query(PreferenceDBHelper.TABLE_NAME, null, null, null, null, null, "_order ASC");
            final int indexOfHubId = cursor.getColumnIndex("hub_id");
            final int indexOfIsVisible = cursor.getColumnIndex("is_visible");

            int order = 0;
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                final Hub hub = hubHashMap.remove(cursor.getString(indexOfHubId).toLowerCase());

                if (hub != null) {
                    preferences.add(new HubPreference(
                            hub,
                            cursor.getInt(indexOfIsVisible) == 1,
                            order ++
                    ));
                }
            }

            cursor.close();
            db.close();

            // Add rest hubs
            final ArrayList<HubPreference> newPreferences = new ArrayList<>();
            for (Hub hub : hubHashMap.values()) {
                newPreferences.add(new HubPreference(hub, true, order ++));
            }
            if (newPreferences.size() > 0) {
                // Save to db
                updateHubPreferences(newPreferences.toArray(new HubPreference[newPreferences.size()]));
            }

            preferences.addAll(newPreferences);
            emitter.onSuccess(preferences);
        });
    }

    @NonNull
    @Override
    public Single<HubPreference> loadHubPreference(@NonNull Hub hub) {
        return Single.create(emitter -> {
            final SQLiteDatabase db = mDBHelper.getReadableDatabase();
            final Cursor cursor = db.query(PreferenceDBHelper.TABLE_NAME, null, null, null, null, null, null);
            final int indexOfHubId = cursor.getColumnIndex("hub_id");
            final int indexOfIsVisible = cursor.getColumnIndex("is_visible");
            final int indexOfOrder = cursor.getColumnIndex("_order");

            final String id = hub.getId();
            HubPreference hubPreference = null;
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                if (cursor.getString(indexOfHubId).equalsIgnoreCase(id)) {
                    hubPreference = new HubPreference(
                            hub,
                            cursor.getInt(indexOfIsVisible) == 1,
                            cursor.getInt(indexOfOrder)
                    );
                    break;
                }
            }

            if (hubPreference == null) {
                hubPreference = new HubPreference(
                        hub,
                        true,
                        cursor.getColumnCount()
                );
            }

            cursor.close();
            db.close();

            // Save to db
            updateHubPreferences(hubPreference);

            emitter.onSuccess(hubPreference);
        });
    }

    @Override
    public void updateHubPreferences(@NonNull HubPreference... preferences) {
        final SQLiteDatabase db = mDBHelper.getWritableDatabase();

        for (HubPreference preference : preferences) {
            final ContentValues values = new ContentValues();
            values.put("hub_id", preference.getHubId());
            values.put("is_visible", preference.isVisible() ? 1 : 0);
            values.put("_order", preference.getOrder());
            db.replace(PreferenceDBHelper.TABLE_NAME, "hub_id", values);
        }

        db.close();
    }

    @Override
    public void saveHubPreferences(@NonNull List<HubPreference> preferences) {
        final SQLiteDatabase db = mDBHelper.getWritableDatabase();
        db.execSQL("DELETE FROM " + PreferenceDBHelper.TABLE_NAME);

        for (HubPreference preference : preferences) {
            final ContentValues values = new ContentValues();
            values.put("hub_id", preference.getHubId());
            values.put("is_visible", preference.isVisible() ? 1 : 0);
            values.put("_order", preference.getOrder());
            db.insert(PreferenceDBHelper.TABLE_NAME, "hub_id", values);
        }

        db.close();
    }

    @Override
    public void removeHubPreferences(@NonNull HubPreference... preferences) {
        final SQLiteDatabase db = mDBHelper.getWritableDatabase();

        for (HubPreference preference : preferences) {
            db.delete(PreferenceDBHelper.TABLE_NAME, "hub_id=?", new String[] {preference.getHubId()});
        }

        db.close();
    }

    @Override
    public void removeAllHubPreferences() {
        final SQLiteDatabase db = mDBHelper.getWritableDatabase();
        db.execSQL("DELETE FROM " + PreferenceDBHelper.TABLE_NAME);
        db.close();
    }

    private class PreferenceDBHelper extends SQLiteOpenHelper {
        private static final int DB_VERSION = 1;
        private static final String DB_NAME = "preference.db";
        private static final String TABLE_NAME = "preference";


        PreferenceDBHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            final String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +
                    " (hub_id VARCHAR PRIMARY KEY NOT NULL, " +
                    "is_visible INTEGER NOT NULL, " +
                    "_order INTEGER NOT NULL)";
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
