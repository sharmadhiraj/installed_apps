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
        body: FutureBuilder<List<AppInfo>>(
            future: InstalledApps.getInstalledApps(),
            builder: (BuildContext buildContext,
                AsyncSnapshot<List<AppInfo>> snapshot) {
              return snapshot.hasData
                  ? ListView.builder(
                      itemCount: snapshot.data.length,
                      itemBuilder: (context, index) {
                        AppInfo app = snapshot.data[index];
                        return ListTile(
                          title: Text(app.name),
                          onTap: () => InstalledApps.startApp(app.packageName),
                          onLongPress: () =>
                              InstalledApps.openSettings(app.packageName),
                        );
                      })
                  : Center(child: Text("Getting installed apps ...."));
            }));
  }
}
