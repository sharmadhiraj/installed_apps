import 'package:flutter/material.dart';
import 'package:installed_apps/app_info.dart';
import 'package:installed_apps/installed_apps.dart';

class AppInfoScreen extends StatelessWidget {
  final AppInfo? app;

  const AppInfoScreen({Key? key, this.app}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: _buildAppBar(),
      body: app == null ? _buildAppInfoWithPackageName() : _buildAppInfo(app!),
    );
  }

  AppBar _buildAppBar() {
    return AppBar(title: Text(app == null ? "App Info" : app!.name));
  }

  Widget _buildAppInfoWithPackageName() {
    return FutureBuilder<AppInfo?>(
      future: InstalledApps.getAppInfo("com.google.android.gm"),
      builder: (BuildContext buildContext, AsyncSnapshot<AppInfo?> snapshot) {
        return snapshot.connectionState == ConnectionState.done
            ? snapshot.hasData && snapshot.data != null
                ? _buildAppInfo(snapshot.data!)
                : _buildError()
            : _buildProgressIndicator();
      },
    );
  }

  Widget _buildProgressIndicator() {
    return Center(child: Text("Getting app info ...."));
  }

  Widget _buildError() {
    return Center(child: Text("Error while getting app info ...."));
  }

  Widget _buildAppInfo(AppInfo app) {
    return ListView(
      padding: EdgeInsets.symmetric(vertical: 16),
      children: [
        Padding(
          padding: const EdgeInsets.symmetric(vertical: 24),
          child: Center(
            child: Image.memory(
              app.icon!,
              width: 64,
            ),
          ),
        ),
        Center(
          child: Text(
            app.name,
            style: TextStyle(
              fontWeight: FontWeight.bold,
              fontSize: 24,
            ),
          ),
        ),
        SizedBox(height: 16),
        ListTile(
          title: Text("Package Name"),
          subtitle: Text(app.packageName),
        ),
        ListTile(
          title: Text("Version Name"),
          subtitle: Text(app.versionName),
        ),
        ListTile(
          title: Text("Version Code"),
          subtitle: Text(app.versionCode.toString()),
        ),
        ListTile(
          title: Text("Platform Type"),
          subtitle: Text(app.platformType.name),
        ),
        ListTile(
          title: Text("Installed On"),
          subtitle: Text(
              DateTime.fromMillisecondsSinceEpoch(app.installedTimestamp)
                  .toLocal()
                  .toString()),
        ),
        Container(
          margin: EdgeInsets.symmetric(horizontal: 16),
          child: ElevatedButton(
            onPressed: () => InstalledApps.startApp(app.packageName),
            child: Text("Open App"),
          ),
        ),
      ],
    );
  }
}
