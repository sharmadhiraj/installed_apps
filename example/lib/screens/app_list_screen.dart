import 'package:flutter/material.dart';
import 'package:installed_apps/installed_apps.dart';
import 'package:installed_apps_example/app_options.dart';
import 'package:installed_apps_example/screens/app_detail_screen.dart';
import 'package:installed_apps_example/widgets/app_icon.dart';
import 'package:installed_apps_example/widgets/options_sheet.dart';
import 'package:installed_apps_example/widgets/package_name_dialog.dart';

class AppListScreen extends StatefulWidget {
  const AppListScreen({super.key});

  @override
  State<AppListScreen> createState() => _AppListScreenState();
}

class _AppListScreenState extends State<AppListScreen> {
  AppOptions options = const AppOptions();
  List<AppInfo> apps = [];
  bool loading = true;
  String query = "";
  int elapsedMs = 0;

  @override
  void initState() {
    super.initState();
    _load();
  }

  Future<void> _load() async {
    setState(() => loading = true);
    final Stopwatch stopwatch = Stopwatch()..start();
    final List<AppInfo> result = await options.load();
    if (!mounted) return;
    setState(() {
      apps = result;
      elapsedMs = stopwatch.elapsedMilliseconds;
      loading = false;
    });
  }

  Future<void> _openOptions() async {
    final AppOptions? result = await showModalBottomSheet<AppOptions>(
      context: context,
      isScrollControlled: true,
      showDragHandle: true,
      builder: (_) => OptionsSheet(options: options),
    );
    if (result == null) return;
    options = result;
    _load();
  }

  Future<void> _lookUpPackage() async {
    final String? packageName = await showDialog<String>(
      context: context,
      builder: (_) => const PackageNameDialog(),
    );
    if (packageName == null || packageName.trim().isEmpty) return;
    final AppInfo? app = await InstalledApps.getAppInfo(packageName.trim());
    if (!mounted) return;
    if (app == null) {
      InstalledApps.toast("App is not installed", true);
      return;
    }
    _openDetails(app);
  }

  void _openDetails(AppInfo app) {
    Navigator.push(
      context,
      MaterialPageRoute(builder: (_) => AppDetailScreen(app: app)),
    );
  }

  @override
  Widget build(BuildContext context) {
    final String lowerQuery = query.toLowerCase();
    final List<AppInfo> visibleApps = apps
        .where(
          (app) =>
              app.name.toLowerCase().contains(lowerQuery) ||
              app.packageName.toLowerCase().contains(lowerQuery),
        )
        .toList();

    return Scaffold(
      appBar: AppBar(
        title: const Text("Installed Apps"),
        actions: [
          IconButton(
            tooltip: "Find by package name",
            icon: const Icon(Icons.manage_search),
            onPressed: _lookUpPackage,
          ),
          IconButton(
            tooltip: "Options",
            icon: const Icon(Icons.tune),
            onPressed: _openOptions,
          ),
        ],
      ),
      body: Column(
        children: [
          Padding(
            padding: const EdgeInsets.fromLTRB(16, 8, 16, 0),
            child: TextField(
              decoration: const InputDecoration(
                hintText: "Search by name or package",
                prefixIcon: Icon(Icons.search),
                border: OutlineInputBorder(),
              ),
              onChanged: (value) => setState(() => query = value),
            ),
          ),
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
            child: Align(
              alignment: Alignment.centerLeft,
              child: Text(
                loading
                    ? "Loading..."
                    : "${visibleApps.length} apps loaded in $elapsedMs ms",
                style: Theme.of(context).textTheme.bodySmall,
              ),
            ),
          ),
          Expanded(child: _buildList(visibleApps)),
        ],
      ),
    );
  }

  Widget _buildList(List<AppInfo> visibleApps) {
    if (loading) return const Center(child: CircularProgressIndicator());
    if (visibleApps.isEmpty) return const Center(child: Text("No apps found"));
    return RefreshIndicator(
      onRefresh: _load,
      child: ListView.builder(
        itemCount: visibleApps.length,
        itemBuilder: (context, index) {
          final AppInfo app = visibleApps[index];
          return Semantics(
            button: true,
            label: "Open details for ${app.name}",
            child: ListTile(
              leading: AppIcon(app: app),
              title: Text(app.name),
              subtitle: Text(app.packageName),
              trailing: Text(
                app.platformType.name,
                style: Theme.of(context).textTheme.labelSmall,
              ),
              onTap: () => _openDetails(app),
            ),
          );
        },
      ),
    );
  }
}
