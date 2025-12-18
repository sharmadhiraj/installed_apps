import 'package:flutter/material.dart';
import 'package:installed_apps/app_info.dart';
import 'package:installed_apps/installed_apps.dart';
import 'package:installed_apps_example/screens/app_info.dart';

class AppListScreen extends StatefulWidget {
  const AppListScreen({Key? key}) : super(key: key);

  @override
  State<AppListScreen> createState() => _AppListScreenState();
}

class _AppListScreenState extends State<AppListScreen> {
  List<AppInfo>? apps;
  bool loading = true;
  final Stopwatch _stopwatch = Stopwatch();

  @override
  void initState() {
    super.initState();
    _loadApps();
  }

  Future<void> _loadApps() async {
    _stopwatch.start();
    final List<AppInfo> result = await InstalledApps.getInstalledApps(
      excludeSystemApps: false,
      withIcon: true,
    );
    _stopwatch.stop();

    final String message =
        "Took ${_stopwatch.elapsedMilliseconds} ms for ${result.length} apps";
    debugPrint(message);
    InstalledApps.toast(message, true);

    setState(() {
      apps = result;
      loading = false;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text("Installed Apps")),
      body: loading
          ? const Center(child: CircularProgressIndicator())
          : apps == null
              ? const Center(child: Text("Error occurred"))
              : _buildListView(),
    );
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
          style: TextStyle(fontSize: 12, fontWeight: FontWeight.bold),
        ),
        onTap: () => Navigator.push(
          context,
          MaterialPageRoute(builder: (_) => AppInfoScreen(app: app)),
        ),
      ),
    );
  }
}
