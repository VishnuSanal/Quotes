/*
package phone.vishnu.quotes.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class StartupReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if ((Intent.ACTION_BOOT_COMPLETED).equals(intent.getAction())) {
            Intent i = new Intent(context, AlarmReceiver.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }
}
*/
