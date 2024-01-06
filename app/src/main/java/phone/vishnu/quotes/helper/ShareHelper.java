/*
 * Copyright (C) 2019 - 2024 Vishnu Sanal. T
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

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.widget.Toast;
import androidx.core.content.ContextCompat;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import phone.vishnu.quotes.R;
import phone.vishnu.quotes.model.Quote;

/*
Copy -> 0
Share -> 1
Save -> 2
Ask -> 3
*/

public class ShareHelper {

    public static void copyQuote(Context context, Quote quote) {

        String q = "\"" + quote.getQuote() + "\"" + " - " + quote.getAuthor().replace("-", "");

        ClipboardManager clipboard =
                (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip =
                ClipData.newPlainText(context.getResources().getString(R.string.app_name), q);
        clipboard.setPrimaryClip(clip);

        Toast.makeText(context, context.getString(R.string.copied_to_clipboard), Toast.LENGTH_SHORT)
                .show();
    }

    public static void saveQuote(Context context, final Quote q) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            Toast.makeText(
                            context,
                            context.getString(R.string.saving_to_gallery),
                            Toast.LENGTH_SHORT)
                    .show();
            AsyncTask.execute(() -> new ExportHelper(context).saveImage(context, q));

        } else
            Dexter.withContext(context)
                    .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .withListener(
                            new PermissionListener() {
                                @Override
                                public void onPermissionGranted(
                                        PermissionGrantedResponse permissionGrantedResponse) {
                                    Toast.makeText(
                                                    context,
                                                    context.getString(R.string.saving_to_gallery),
                                                    Toast.LENGTH_SHORT)
                                            .show();
                                    AsyncTask.execute(
                                            () -> new ExportHelper(context).saveImage(context, q));
                                }

                                @Override
                                public void onPermissionDenied(
                                        final PermissionDeniedResponse permissionDeniedResponse) {
                                    showPermissionDeniedDialog(context);
                                }

                                @Override
                                public void onPermissionRationaleShouldBeShown(
                                        PermissionRequest permissionRequest,
                                        PermissionToken permissionToken) {
                                    Toast.makeText(
                                                    context,
                                                    context.getString(
                                                            R.string
                                                                    .app_requires_these_permissions_to_share_the_quote),
                                                    Toast.LENGTH_SHORT)
                                            .show();
                                    permissionToken.continuePermissionRequest();
                                }
                            })
                    .check();
    }

    public static void shareQuote(Context context, final Quote q) {
        AsyncTask.execute(() -> new ExportHelper(context).shareImage(context, q));
    }

    private static void showPermissionDeniedDialog(Context context) {
        final androidx.appcompat.app.AlertDialog.Builder builder =
                new androidx.appcompat.app.AlertDialog.Builder(context);
        builder.setTitle(R.string.permission_denied);
        builder.setMessage(R.string.please_accept_necessary_permissions);
        builder.setCancelable(true);
        builder.setPositiveButton(
                "OK",
                (imageDialog, which) -> {
                    imageDialog.cancel();
                    context.startActivity(
                            new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                    .setData(
                                            Uri.fromParts(
                                                    "package", context.getPackageName(), null)));
                });
        builder.setNegativeButton(
                context.getString(R.string.cancel),
                (imageDialog, which) -> {
                    imageDialog.cancel();
                    Toast.makeText(
                                    context,
                                    context.getString(
                                            R.string
                                                    .app_requires_these_permissions_to_run_properly),
                                    Toast.LENGTH_SHORT)
                            .show();
                });
        builder.show();
    }

    public static Drawable getShareIconDrawable(Context context, int i) {
        if (i == 0) return ContextCompat.getDrawable(context, R.drawable.ic_copy);
        else if (i == 1 || i == 3) return ContextCompat.getDrawable(context, R.drawable.ic_share);
        else if (i == 2) return ContextCompat.getDrawable(context, R.drawable.ic_save);

        return ContextCompat.getDrawable(context, R.drawable.ic_share);
    }
}
