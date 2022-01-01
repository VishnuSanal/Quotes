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

package phone.vishnu.quotes.acra;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import org.acra.ReportField;
import org.acra.data.CrashReportData;
import phone.vishnu.quotes.R;
import phone.vishnu.quotes.activity.SplashActivity;

public class ACRAErrorActivity extends AppCompatActivity {

    private TextView stackTraceTV,
            packageNameTV,
            versionNameTV,
            versionCodeTV,
            androidVersionTV,
            appStartDateTV;
    private Button telegramButton, githubButton;
    private TextInputEditText userCommentTIE;

    public static void openErrorActivity(
            @NonNull Context context, @NonNull CrashReportData errorContent) {

        Intent intent =
                new Intent(context, ACRAErrorActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        intent.putExtra(
                "ACRA_STACK_TRACE", checkNullity(errorContent.getString(ReportField.STACK_TRACE)));
        intent.putExtra(
                "ACRA_ANDROID_VERSION",
                checkNullity(errorContent.getString(ReportField.ANDROID_VERSION)));
        intent.putExtra(
                "ACRA_APP_VERSION_CODE",
                checkNullity(errorContent.getString(ReportField.APP_VERSION_CODE)));
        intent.putExtra(
                "ACRA_APP_VERSION_NAME",
                checkNullity(errorContent.getString(ReportField.APP_VERSION_NAME)));
        intent.putExtra(
                "ACRA_PACKAGE_NAME",
                checkNullity(errorContent.getString(ReportField.PACKAGE_NAME)));
        intent.putExtra(
                "ACRA_USER_APP_START_DATE",
                checkNullity(errorContent.getString(ReportField.USER_APP_START_DATE)));

        context.startActivity(intent);
    }

    private static String checkNullity(String s) {
        return s == null ? "Property not found" : s;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acra_error);
        setTitle(getString(R.string.quotes_status_creator) + " crashed");
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        stackTraceTV = findViewById(R.id.errorActivityStackTraceTV);
        packageNameTV = findViewById(R.id.errorActivityPackageNameTV);
        versionNameTV = findViewById(R.id.errorActivityVersionNameTV);
        versionCodeTV = findViewById(R.id.errorActivityVersionCodeTV);
        androidVersionTV = findViewById(R.id.errorActivityAndroidVersionTV);
        appStartDateTV = findViewById(R.id.errorActivityAppStartDateTV);
        githubButton = findViewById(R.id.errorActivityGitHubReportTV);
        telegramButton = findViewById(R.id.errorActivityTelegramReportTV);
        userCommentTIE = findViewById(R.id.errorActivityUserCommentTIE);

        Intent intent = getIntent();

        if (checkIntent(intent)) {

            packageNameTV.setText(
                    String.format(
                            "Package Name: %s",
                            checkNullity(intent.getStringExtra("ACRA_PACKAGE_NAME"))));
            versionNameTV.setText(
                    String.format(
                            "App Version: %s",
                            checkNullity(intent.getStringExtra("ACRA_APP_VERSION_NAME"))));
            versionCodeTV.setText(
                    String.format(
                            "Version Code: %s",
                            checkNullity(intent.getStringExtra("ACRA_APP_VERSION_CODE"))));
            androidVersionTV.setText(
                    String.format(
                            "Android Version: %s",
                            checkNullity(intent.getStringExtra("ACRA_ANDROID_VERSION"))));
            appStartDateTV.setText(
                    String.format(
                            "App Start Date: %s",
                            checkNullity(intent.getStringExtra("ACRA_USER_APP_START_DATE"))));
            stackTraceTV.setText(
                    String.format(
                            "Stack Trace:\n\n%s",
                            checkNullity(intent.getStringExtra("ACRA_STACK_TRACE"))));
        }

        telegramButton.setOnClickListener(
                view -> {
                    generateReport();
                    openLink("https://t.me/QuotesStatusCreator");
                });

        githubButton.setOnClickListener(
                view -> {
                    generateReport();
                    openLink("https://github.com/VishnuSanal/Quotes/issues/new/choose");
                });
    }

    private void generateReport() {

        Editable commentText = userCommentTIE.getText();

        String comment =
                commentText != null && !commentText.toString().isEmpty()
                        ? "Comment: " + commentText.toString()
                        : "";

        String s =
                "Crash report for "
                        + getString(R.string.quotes_status_creator)
                        + "\n\n\n"
                        + packageNameTV.getText()
                        + "\n\n"
                        + versionNameTV.getText()
                        + "\n\n"
                        + versionCodeTV.getText()
                        + "\n\n"
                        + androidVersionTV.getText()
                        + "\n\n"
                        + appStartDateTV.getText()
                        + "\n\n"
                        + "```"
                        + "\n"
                        + stackTraceTV.getText()
                        + "\n"
                        + "```"
                        + "\n\n"
                        + comment;

        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip =
                ClipData.newPlainText(getResources().getString(R.string.app_name), s.trim());
        clipboard.setPrimaryClip(clip);

        Toast.makeText(this, "Report Copied to Clipboard", Toast.LENGTH_SHORT).show();
    }

    private void openLink(String s) {
        Uri uriUrl = Uri.parse(s);
        startActivity(new Intent(Intent.ACTION_VIEW, uriUrl));
    }

    private boolean checkIntent(Intent intent) {
        return intent != null
                && intent.getExtras() != null
                && intent.getExtras().containsKey("ACRA_STACK_TRACE");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        moveToSplash();
    }

    private void moveToSplash() {
        startActivity(new Intent(ACRAErrorActivity.this, SplashActivity.class));
        ACRAErrorActivity.this.finish();
    }
}
