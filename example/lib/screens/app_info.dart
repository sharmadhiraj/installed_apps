import 'package:flutter/material.dart';
import 'package:installed_apps/installed_apps.dart';

class AppInfoScreen extends StatelessWidget {
  final AppInfo? app;

  const AppInfoScreen({super.key, this.app});

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
    return const Center(child: Text("Getting app info ...."));
  }

  Widget _buildError() {
    return const Center(child: Text("Error while getting app info ...."));
  }

  Widget _buildAppInfo(AppInfo app) {
    return ListView(
      padding: const EdgeInsets.symmetric(vertical: 16),
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
            style: const TextStyle(
              fontWeight: FontWeight.bold,
              fontSize: 24,
            ),
          ),
        ),
        const SizedBox(height: 16),
        ListTile(
          title: const Text("Package Name"),
          subtitle: Text(app.packageName),
        ),
        ListTile(
          title: const Text("Version Name"),
          subtitle: Text(app.versionName),
        ),
        ListTile(
          title: const Text("Version Code"),
          subtitle: Text(app.versionCode.toString()),
        ),
        ListTile(
          title: const Text("Category"),
          subtitle: Text(app.category.toString()),
        ),
        ListTile(
          title: const Text("Platform Type"),
          subtitle: Text(app.platformType.name),
        ),
        ListTile(
          title: const Text("Installed On"),
          subtitle: Text(
            DateTime.fromMillisecondsSinceEpoch(app.installedTimestamp)
                .toLocal()
                .toString(),
          ),
        ),
        Container(
          margin: const EdgeInsets.symmetric(horizontal: 16),
          child: ElevatedButton(
            onPressed: () => InstalledApps.startApp(app.packageName),
            child: const Text("Open App"),
          ),
        ),
      ],
    );
  }
}
