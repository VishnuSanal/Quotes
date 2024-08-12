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
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Date;
import phone.vishnu.quotes.BuildConfig;
import phone.vishnu.quotes.R;
import phone.vishnu.quotes.activity.SplashActivity;
import phone.vishnu.quotes.helper.Constants;

public class ACRAErrorActivity extends AppCompatActivity {

    private TextView errorActivityTitleTV,
            stackTraceTV,
            packageNameTV,
            versionNameTV,
            versionCodeTV,
            androidVersionTV,
            appStartDateTV;
    private Button telegramButton, githubButton;
    private TextInputEditText userCommentTIE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!checkIntent(getIntent())) moveToSplash();

        setContentView(R.layout.activity_acra_error);

        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        errorActivityTitleTV = findViewById(R.id.errorActivityTitleTV);
        stackTraceTV = findViewById(R.id.errorActivityStackTraceTV);
        packageNameTV = findViewById(R.id.errorActivityPackageNameTV);
        versionNameTV = findViewById(R.id.errorActivityVersionNameTV);
        versionCodeTV = findViewById(R.id.errorActivityVersionCodeTV);
        androidVersionTV = findViewById(R.id.errorActivityAndroidVersionTV);
        appStartDateTV = findViewById(R.id.errorActivityAppStartDateTV);
        githubButton = findViewById(R.id.errorActivityGitHubReportTV);
        telegramButton = findViewById(R.id.errorActivityTelegramReportTV);
        userCommentTIE = findViewById(R.id.errorActivityUserCommentTIE);

        File reportFile = (File) getIntent().getSerializableExtra(Constants.ACRA_REPORT_FILE_KEY);

        String stackTrace = "Could not load stack trace";

        try {
            ACRALogFileModel acraLogFileModel =
                    new GsonBuilder()
                            .setLenient()
                            .create()
                            .fromJson(new FileReader(reportFile), ACRALogFileModel.class);

            stackTrace = acraLogFileModel.getStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        errorActivityTitleTV.setText(
                String.format(
                        "%s %s",
                        getString(R.string.quotes_status_creator), getString(R.string.crashed)));
        packageNameTV.setText(
                String.format(
                        "%s %s", getString(R.string.package_name), BuildConfig.APPLICATION_ID));
        versionNameTV.setText(
                String.format("%s %s", getString(R.string.app_version), BuildConfig.VERSION_NAME));
        versionCodeTV.setText(
                String.format("%s %s", getString(R.string.version_code), BuildConfig.VERSION_CODE));
        androidVersionTV.setText(
                String.format(
                        "%s %s",
                        getString(R.string.android_version), android.os.Build.VERSION.SDK_INT));
        appStartDateTV.setText(
                String.format("%s %s", getString(R.string.app_start_date), new Date()));
        stackTraceTV.setText(
                String.format("%s\n\n%s", getString(R.string.stack_trace), stackTrace));

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
                        ? String.format(
                                "%s %s", getString(R.string.comment), commentText.toString())
                        : "";

        String s =
                String.format(
                        "%s %s\n\n\n%s\n\n%s\n\n%s\n\n%s\n\n%s\n\n```\n%s\n```\n\n%s",
                        getString(R.string.crash_report_for),
                        getString(R.string.quotes_status_creator),
                        packageNameTV.getText(),
                        versionNameTV.getText(),
                        versionCodeTV.getText(),
                        androidVersionTV.getText(),
                        appStartDateTV.getText(),
                        stackTraceTV.getText(),
                        comment);

        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip =
                ClipData.newPlainText(getResources().getString(R.string.app_name), s.trim());
        clipboard.setPrimaryClip(clip);

        Toast.makeText(this, getString(R.string.report_copied_to_clipboard), Toast.LENGTH_SHORT)
                .show();
    }

    private void openLink(String s) {
        Uri uriUrl = Uri.parse(s);
        startActivity(new Intent(Intent.ACTION_VIEW, uriUrl));
    }

    private boolean checkIntent(Intent intent) {
        return intent != null
                && intent.getExtras() != null
                && intent.getExtras().containsKey(Constants.ACRA_REPORT_FILE_KEY);
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
