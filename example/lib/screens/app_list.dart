import 'package:flutter/material.dart';
import 'package:installed_apps/installed_apps.dart';
import 'package:installed_apps_example/screens/app_info.dart';

class AppListScreen extends StatefulWidget {
  const AppListScreen({super.key});

  @override
  State<AppListScreen> createState() => _AppListScreenState();
}

class _AppListScreenState extends State<AppListScreen> {
  List<AppInfo>? apps;
  bool loading = true;
  AppFilterType filter = AppFilterType.all;

  @override
  void initState() {
    super.initState();
    _loadApps();
  }

  Future<void> _loadApps() async {
    setState(() => loading = true);
    List<AppInfo> result = await InstalledApps.getInstalledApps(
      excludeSystemApps: false,
      withIcon: true,
    );
    if (filter == AppFilterType.flutter) {
      result = await InstalledApps.getInstalledApps(
        excludeSystemApps: false,
        withIcon: true,
        platformType: PlatformType.flutter,
      );
    } else if (filter == AppFilterType.withoutSystemApps) {
      result = await InstalledApps.getInstalledApps(
        withIcon: true,
        platformType: PlatformType.flutter,
      );
    }
    setState(() {
      apps = result;
      loading = false;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text("Installed Apps")),
      body: _buildBody(),
    );
  }

  Widget _buildBody() {
    if (loading) {
      return const Center(child: CircularProgressIndicator());
    } else {
      return apps == null
          ? const Center(child: Text("Error occurred"))
          : Padding(
              padding: const EdgeInsets.symmetric(horizontal: 4),
              child: Column(
                children: [
                  Wrap(
                    spacing: 4,
                    children: AppFilterType.values
                        .map(
                          (e) => ElevatedButton(
                            onPressed: () {
                              setState(() => filter = e);
                              _loadApps();
                            },
                            child: Text(e.name),
                          ),
                        )
                        .toList(),
                  ),
                  Padding(
                    padding: const EdgeInsets.symmetric(vertical: 4),
                    child: Text(
                      filter.name,
                      style: const TextStyle(fontWeight: FontWeight.bold),
                    ),
                  ),
                  Expanded(child: _buildListView()),
                ],
              ),
            );
    }
  }

  Widget _buildListView() {
    return ListView.builder(
      itemCount: apps!.length,
      itemBuilder: _buildListItem,
    );
  }

  Widget _buildListItem(BuildContext context, int index) {
    final AppInfo app = apps![index];
    return Card(
      child: ListTile(
        leading: CircleAvatar(
          backgroundColor: Colors.transparent,
          child: Image.memory(app.icon!),
        ),
        title: Text(app.name),
        subtitle: Text(app.getVersionInfo()),
        trailing: Text(
          app.platformType.name[0],
          style: const TextStyle(fontSize: 12, fontWeight: FontWeight.bold),
        ),
        onTap: () => Navigator.push(
          context,
          MaterialPageRoute(builder: (_) => AppInfoScreen(app: app)),
        ),
      ),
    );
  }
}

enum AppFilterType {
  all("All Apps"),
  flutter("Flutter Apps"),
  withoutSystemApps("Without System Apps");

  final String name;

  const AppFilterType(this.name);
}
