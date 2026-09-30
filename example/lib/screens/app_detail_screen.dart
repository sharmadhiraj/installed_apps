import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:installed_apps/installed_apps.dart';
import 'package:installed_apps_example/widgets/app_icon.dart';

class AppDetailScreen extends StatefulWidget {
  final AppInfo app;

  const AppDetailScreen({required this.app, super.key});

  @override
  State<AppDetailScreen> createState() => _AppDetailScreenState();
}

class _AppDetailScreenState extends State<AppDetailScreen>
    with WidgetsBindingObserver {
  bool? isInstalled;
  bool? isSystemApp;

  AppInfo get app => widget.app;

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addObserver(this);
    _refreshStatus();
  }

  @override
  void dispose() {
    WidgetsBinding.instance.removeObserver(this);
    super.dispose();
  }

  @override
  void didChangeAppLifecycleState(AppLifecycleState state) {
    if (state == AppLifecycleState.resumed) _refreshStatus();
  }

  Future<void> _refreshStatus() async {
    final bool? installed = await InstalledApps.isAppInstalled(app.packageName);
    final bool? system = await InstalledApps.isSystemApp(app.packageName);
    if (!mounted) return;
    setState(() {
      isInstalled = installed;
      isSystemApp = system;
    });
  }

  Future<void> _confirmUninstall() async {
    final bool? confirmed = await showDialog<bool>(
      context: context,
      builder: (dialogContext) => AlertDialog(
        title: Text("Uninstall ${app.name}?"),
        content: const Text("Android will ask you to confirm."),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(dialogContext, false),
            child: const Text("Cancel"),
          ),
          FilledButton(
            onPressed: () => Navigator.pop(dialogContext, true),
            child: const Text("Uninstall"),
          ),
        ],
      ),
    );
    if (confirmed ?? false) InstalledApps.uninstallApp(app.packageName);
  }

  Future<void> _launch() async {
    final bool? started = await InstalledApps.startApp(app.packageName);
    if (started != true) InstalledApps.toast("Could not open the app", true);
  }

  void _copyPackageName() {
    Clipboard.setData(ClipboardData(text: app.packageName));
    InstalledApps.toast("Package name copied", true);
  }

  String _yesNo(bool? value) => value == null ? "..." : (value ? "Yes" : "No");

  @override
  Widget build(BuildContext context) {
    final DateTime updated =
        DateTime.fromMillisecondsSinceEpoch(app.installedTimestamp).toLocal();
    return Scaffold(
      appBar: AppBar(title: Text(app.name)),
      body: ListView(
        padding: const EdgeInsets.symmetric(vertical: 16),
        children: [
          Center(child: AppIcon(app: app, size: 72)),
          const SizedBox(height: 12),
          Center(
            child:
                Text(app.name, style: Theme.of(context).textTheme.titleLarge),
          ),
          const SizedBox(height: 16),
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: 16),
            child: Wrap(
              spacing: 8,
              runSpacing: 8,
              alignment: WrapAlignment.center,
              children: [
                _action("Open", Icons.open_in_new, _launch),
                _action(
                  "Settings",
                  Icons.settings,
                  () => InstalledApps.openSettings(app.packageName),
                ),
                _action("Uninstall", Icons.delete_outline, _confirmUninstall),
              ],
            ),
          ),
          const Divider(height: 32),
          ListTile(
            title: const Text("Package name"),
            subtitle: Text(app.packageName),
            trailing: const Icon(Icons.copy, size: 18),
            onTap: _copyPackageName,
          ),
          _info("Version", app.getVersionInfo()),
          _info("Category", app.category.name),
          _info("Platform", app.platformType.name),
          _info("Last updated", updated.toString().split(".").first),
          _info("Launchable", _yesNo(app.isLaunchableApp)),
          _info("System app", _yesNo(isSystemApp ?? app.isSystemApp)),
          _info("Installed", _yesNo(isInstalled)),
        ],
      ),
    );
  }

  Widget _info(String label, String value) {
    return ListTile(title: Text(label), subtitle: Text(value));
  }

  Widget _action(String label, IconData icon, VoidCallback onPressed) {
    return Semantics(
      button: true,
      label: label,
      child: FilledButton.tonalIcon(
        onPressed: onPressed,
        icon: Icon(icon),
        label: Text(label),
      ),
    );
  }
}
