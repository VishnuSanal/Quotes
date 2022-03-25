/*
 * Copyright (C) 2019 - 2022 Vishnu Sanal. T
 *
 * This file is part of Quotes Status Creator.
 *
 * Quotes Status Creator is free software: you can redistribute it and/or modify
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

package phone.vishnu.quotes.helper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

public class DownloadImageTask extends AsyncTask<String, Void, Void> {

    private final String targetPath;
    private final OnTaskCompleted listener;

    public DownloadImageTask(String targetPath, OnTaskCompleted listener) {
        this.targetPath = targetPath;
        this.listener = listener;
    }

    @Override
    protected Void doInBackground(String... strings) {

        String imageURL = strings[0];

        try {

            InputStream inputStream = new URL(imageURL).openStream();

            File file = new File(targetPath);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BitmapFactory.decodeStream(inputStream)
                    .compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

            FileOutputStream fos = new FileOutputStream(file);
            fos.write(outputStream.toByteArray());

            fos.flush();
            fos.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void unused) {
        super.onPostExecute(unused);
        listener.onTaskCompleted();
    }

    public interface OnTaskCompleted {
        void onTaskCompleted();
    }
}
