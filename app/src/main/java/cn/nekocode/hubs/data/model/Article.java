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

package cn.nekocode.hubs.data.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
public class Article implements Parcelable {
    private String url;
    private String coverUrl;
    private Spanned title;
    private Spanned description;


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public Spanned getTitle() {
        return title;
    }

    public void setTitle(Spanned title) {
        this.title = title;
    }

    /**
     * Set tile with html text
     */
    public void setTitleHtml(String titleHtml) {
        this.title = Html.fromHtml(titleHtml);
    }

    public Spanned getDescription() {
        return description;
    }

    public void setDescription(Spanned description) {
        this.description = description;
    }

    /**
     * Set description with html text
     */
    public void setDescriptionHtml(String descriptionHtml) {
        this.description = Html.fromHtml(descriptionHtml);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.url);
        dest.writeString(this.coverUrl);
        TextUtils.writeToParcel(this.title, dest, flags);
        TextUtils.writeToParcel(this.description, dest, flags);
    }

    public Article() {
    }

    protected Article(Parcel in) {
        this.url = in.readString();
        this.coverUrl = in.readString();
        this.title = (Spanned) TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(in);
        this.description = (Spanned) TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(in);
    }

    public static final Creator<Article> CREATOR = new Creator<Article>() {
        @Override
        public Article createFromParcel(Parcel source) {
            return new Article(source);
        }

        @Override
        public Article[] newArray(int size) {
            return new Article[size];
        }
    };
}
