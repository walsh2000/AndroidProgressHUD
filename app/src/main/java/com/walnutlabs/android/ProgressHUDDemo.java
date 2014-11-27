package com.walnutlabs.android;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

public class ProgressHUDDemo extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    public void showSimpleIndeterminate(View v) {
        TimeConsumingTask t = new TimeConsumingTask();
        t.execute();
    }

    public void showSimpleProgress(View v) {
        ProgressingTask t = new ProgressingTask();
        t.showSecondLine(true);
        t.execute();
    }

    public void showSingleLineProgress(View v) {
        ProgressingTask t = new ProgressingTask();
        t.showSecondLine(false);
        t.execute();
    }


    public class TimeConsumingTask extends AsyncTask<Void, String, Void> implements OnCancelListener {
        ProgressHUD mProgressHUD;

        @Override
        protected void onPreExecute() {
            mProgressHUD = ProgressHUD.show(ProgressHUDDemo.this, "Connecting", null, ProgressHUD.Style.Indeterminate, true, this);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                publishProgress("Connecting");
                Thread.sleep(2000);
                publishProgress("Downloading");
                Thread.sleep(5000);
                publishProgress("Done");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            mProgressHUD.setMessage(values[0]);
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Void result) {
            mProgressHUD.dismiss();
            super.onPostExecute(result);
        }

        @Override
        public void onCancel(DialogInterface dialog) {
            this.cancel(true);
            mProgressHUD.dismiss();
        }
    }


    public class ProgressingTask extends AsyncTask<Void, String, Void> implements OnCancelListener {
        ProgressHUD mProgressHUD;
        private Handler mHandler;
        private boolean mShowSecondLine;

        public void showSecondLine(boolean showSecond) {
            mShowSecondLine = showSecond;
        }

        @Override
        protected void onPreExecute() {
            String secondLine = "Sub-detail here";
            if (!mShowSecondLine) {
                secondLine = null;
            }
            mProgressHUD = ProgressHUD.show(ProgressHUDDemo.this, "Processing", secondLine, ProgressHUD.Style.RadialProgress, false, this);
            mHandler = new Handler(Looper.getMainLooper());
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                float percentage = 0.0f;

                while (percentage < 1.0f) {
                    Thread.sleep(250);
                    percentage += 0.05f;
                    final float finalPercentage = percentage;
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mProgressHUD.setProgress(finalPercentage);
                        }
                    });
                }
                //sleep a bit so that 100% can show
                Thread.sleep(250);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mProgressHUD.setMessage("Complete");
                    }
                });
                Thread.sleep(250);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            mProgressHUD.dismiss();
            super.onPostExecute(result);
        }

        @Override
        public void onCancel(DialogInterface dialog) {
            this.cancel(true);
            mProgressHUD.dismiss();
        }
    }

}
