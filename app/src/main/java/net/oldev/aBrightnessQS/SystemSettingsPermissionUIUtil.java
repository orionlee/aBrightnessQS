package net.oldev.aBrightnessQS;

import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.service.quicksettings.TileService;

/**
 * Helper to manage System Settings Permission UI, in the context of TileService.
 *
 * Android's guide provides a pattern (and helpers) for doing so in an Activity.
 * This helper mimics a simplified version in the context of a TileService.
 *
 * @see https://developer.android.com/training/permissions/requesting.html the pattern followed
 *
 */
public class SystemSettingsPermissionUIUtil {

    public static void requestPermission(TileService tileService) {
        // The BrightnessTileService is *not functional* without the permission to modify system settings.
        // Instead of launching an extra dialog as the general pattern suggested,
        // here we launch the actual permission activity, along with an explanation
        // of the reason doing so.

        // Note: PowerToggle has an implementation that mimics the general pattern.
        // https://github.com/sunnygoyal/PowerToggles/blob/master/src/com/painless/pc/PermissionDialog.java

        String permissionNeededMsg = tileService.getString(R.string.sys_permission_required_msg);
        // TODO: Consider to use a snack bar as it is more prominent?!
        android.widget.Toast.makeText(tileService.getApplicationContext(), permissionNeededMsg,
                android.widget.Toast.LENGTH_LONG).show();

        doRequestPermission(tileService);
    }

    // Analogous to ActivityCompat.requestPermissions(Activity ...)
    private static void doRequestPermission(TileService tileService) {

        // Use TileService.startActivityAndCollapse to close out the Quick Settings Panel
        // Otherwise, the activity will only be launched in background, pending closing the quick settings panel
        tileService.startActivityAndCollapse(new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .setData(Uri.parse("package:" + tileService.getPackageName())));
    }

}
