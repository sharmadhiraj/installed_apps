import 'package:flutter/material.dart';
import 'package:installed_apps/app_info.dart';
import 'package:installed_apps/installed_apps.dart';

void main() => runApp(App());

class App extends MaterialApp {
  @override
  Widget get home => HomeScreen();
}

class HomeScreen extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text("Installed Apps Example")),
      body: ListView(
        children: [
          Card(
            child: Padding(
              padding: const EdgeInsets.all(8),
              child: ListTile(
                title: Text("Installed Apps"),
                subtitle: Text(
                    "Get installed apps on device. With options to exclude system app, get app icon & matching package name prefix."),
                onTap: () => Navigator.push(
                  context,
                  MaterialPageRoute(
                    builder: (context) => InstalledAppsScreen(),
                  ),
                ),
              ),
            ),
          ),
          Card(
            child: Padding(
              padding: const EdgeInsets.all(8),
              child: ListTile(
                title: Text("App Info"),
                subtitle: Text("Get app info with package name"),
                onTap: () => Navigator.push(
                  context,
                  MaterialPageRoute(
                    builder: (context) => AppInfoScreen(),
                  ),
                ),
              ),
            ),
          ),
          Card(
            child: Padding(
              padding: const EdgeInsets.all(8),
              child: ListTile(
                title: Text("Start App"),
                subtitle: Text(
                    "Start app with package name. Get callback of success or failure."),
                onTap: () => InstalledApps.startApp("com.google.android.gm"),
              ),
            ),
          ),
          Card(
            child: Padding(
              padding: const EdgeInsets.all(8),
              child: ListTile(
                title: Text("Go To App Settings Screen"),
                subtitle: Text(
                    "Directly navigate to app settings screen with package name"),
                onTap: () =>
                    InstalledApps.openSettings("com.google.android.gm"),
              ),
            ),
          ),
          Card(
            child: Padding(
              padding: const EdgeInsets.all(8),
              child: ListTile(
                title: Text("Check If System App"),
                subtitle: Text("Check if app is system app with package name"),
                onTap: () =>
                    InstalledApps.isSystemApp("com.google.android.gm").then(
                  (bool? value) => _showDialog(
                      context,
                      value ?? false
                          ? "The requested app is system app."
                          : "Requested app in not system app."),
                ),
              ),
            ),
          ),
          Card(
            child: Padding(
              padding: const EdgeInsets.all(8),
              child: ListTile(
                title: Text("Uninstall app"),
                subtitle: Text("Uninstall app with package name"),
                onTap: () => InstalledApps.uninstallApp(
                    "com.sharmadhiraj.installed_apps_example"),
              ),
            ),
          ),
          Card(
            child: Padding(
              padding: const EdgeInsets.all(8),
              child: ListTile(
                title: Text("Is app installed"),
                subtitle: Text("Check if app is installed using package name"),
                onTap: () => InstalledApps.isAppInstalled(
                        "com.sharmadhiraj.installed_apps_example")
                    .then(
                  (bool? value) => _showDialog(
                      context,
                      value ?? false
                          ? "App is installed on this device."
                          : "App is not installed on this device."),
                ),
              ),
            ),
          )
        ],
      ),
    );
  }

  void _showDialog(BuildContext context, String text) {
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

class InstalledAppsScreen extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text("Installed Apps")),
      body: FutureBuilder<List<AppInfo>>(
        future: InstalledApps.getInstalledApps(true, true),
        builder:
            (BuildContext buildContext, AsyncSnapshot<List<AppInfo>> snapshot) {
          return snapshot.connectionState == ConnectionState.done
              ? snapshot.hasData
                  ? ListView.builder(
                      itemCount: snapshot.data!.length,
                      itemBuilder: (context, index) {
                        AppInfo app = snapshot.data![index];
                        return Card(
                          child: ListTile(
                            leading: CircleAvatar(
                              backgroundColor: Colors.transparent,
                              child: Image.memory(app.icon!),
                            ),
                            title: Text(app.name!),
                            subtitle: Text(app.getVersionInfo()),
                            onTap: () =>
                                InstalledApps.startApp(app.packageName!),
                            onLongPress: () =>
                                InstalledApps.openSettings(app.packageName!),
                          ),
                        );
                      },
                    )
                  : Center(
                      child: Text(
                          "Error occurred while getting installed apps ...."))
              : Center(child: Text("Getting installed apps ...."));
        },
      ),
    );
  }
}

class AppInfoScreen extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text("App Info")),
      body: FutureBuilder<AppInfo>(
        future: InstalledApps.getAppInfo("com.google.android.gm"),
        builder: (BuildContext buildContext, AsyncSnapshot<AppInfo> snapshot) {
          return snapshot.connectionState == ConnectionState.done
              ? snapshot.hasData
                  ? Center(
                      child: Column(
                        children: [
                          Image.memory(snapshot.data!.icon!),
                          Text(
                            snapshot.data!.name!,
                            style: TextStyle(
                              fontWeight: FontWeight.bold,
                              fontSize: 40,
                            ),
                          ),
                          Text(snapshot.data!.getVersionInfo())
                        ],
                      ),
                    )
                  : Center(child: Text("Error while getting app info ...."))
              : Center(child: Text("Getting app info ...."));
        },
      ),
    );
  }
}
