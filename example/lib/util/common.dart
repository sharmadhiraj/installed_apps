import 'package:flutter/material.dart';
import 'package:installed_apps/installed_apps.dart';

class CommonUtil {
  static void checkIfAppIsInstalled(BuildContext context, String packageName) {
    InstalledApps.isAppInstalled(packageName).then(
      (bool? value) => _showDialog(
          context,
          value ?? false
              ? "App is installed on this device."
              : "App is not installed on this device."),
    );
  }

  static void checkIfSystemApp(BuildContext context, String packageName) {
    InstalledApps.isSystemApp(packageName).then(
      (bool? value) => _showDialog(
          context,
          value ?? false
              ? "The requested app is system app."
              : "Requested app in not system app."),
    );
  }

  static void _showDialog(BuildContext context, String text) {
    showDialog(
      context: context,
      builder: (BuildContext context) {
        return AlertDialog(
          content: Text(text),
          actions: <Widget>[
            TextButton(
              child: Text("Close"),
              onPressed: () => Navigator.of(context).pop(),
            ),
          ],
        );
      },
    );
  }
}
