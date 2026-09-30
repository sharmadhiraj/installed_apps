import 'package:flutter/material.dart';
import 'package:installed_apps/installed_apps.dart';
import 'package:installed_apps_example/app_options.dart';

class OptionsSheet extends StatefulWidget {
  final AppOptions options;

  const OptionsSheet({required this.options, super.key});

  @override
  State<OptionsSheet> createState() => _OptionsSheetState();
}

class _OptionsSheetState extends State<OptionsSheet> {
  late bool excludeSystemApps = widget.options.excludeSystemApps;
  late bool excludeNonLaunchableApps = widget.options.excludeNonLaunchableApps;
  late bool withIcon = widget.options.withIcon;
  late bool detectPlatformType = widget.options.detectPlatformType;
  late PlatformType? platformType = widget.options.platformType;
  late final TextEditingController prefix =
      TextEditingController(text: widget.options.packageNamePrefix);
  late final TextEditingController packageNames =
      TextEditingController(text: widget.options.packageNames);

  @override
  void dispose() {
    prefix.dispose();
    packageNames.dispose();
    super.dispose();
  }

  void _reset() {
    const AppOptions defaults = AppOptions();
    setState(() {
      excludeSystemApps = defaults.excludeSystemApps;
      excludeNonLaunchableApps = defaults.excludeNonLaunchableApps;
      withIcon = defaults.withIcon;
      detectPlatformType = defaults.detectPlatformType;
      platformType = defaults.platformType;
      prefix.clear();
      packageNames.clear();
    });
  }

  void _apply() {
    Navigator.pop(
      context,
      AppOptions(
        excludeSystemApps: excludeSystemApps,
        excludeNonLaunchableApps: excludeNonLaunchableApps,
        withIcon: withIcon,
        detectPlatformType: detectPlatformType,
        platformType: platformType,
        packageNamePrefix: prefix.text,
        packageNames: packageNames.text,
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return SafeArea(
      child: SingleChildScrollView(
        padding: EdgeInsets.fromLTRB(
          16,
          8,
          16,
          16 + MediaQuery.of(context).viewInsets.bottom,
        ),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text("Options", style: Theme.of(context).textTheme.titleLarge),
            SwitchListTile(
              title: const Text("Exclude system apps"),
              value: excludeSystemApps,
              onChanged: (v) => setState(() => excludeSystemApps = v),
            ),
            SwitchListTile(
              title: const Text("Exclude non-launchable apps"),
              value: excludeNonLaunchableApps,
              onChanged: (v) => setState(() => excludeNonLaunchableApps = v),
            ),
            SwitchListTile(
              title: const Text("Load icons"),
              value: withIcon,
              onChanged: (v) => setState(() => withIcon = v),
            ),
            SwitchListTile(
              title: const Text("Detect platform type"),
              subtitle: const Text("Ignored when a platform filter is set"),
              value: detectPlatformType || platformType != null,
              onChanged: platformType == null
                  ? (v) => setState(() => detectPlatformType = v)
                  : null,
            ),
            const SizedBox(height: 8),
            DropdownButtonFormField<PlatformType?>(
              initialValue: platformType,
              decoration: const InputDecoration(
                labelText: "Platform filter",
                border: OutlineInputBorder(),
              ),
              items: [
                const DropdownMenuItem<PlatformType?>(child: Text("Any")),
                ...PlatformType.values.map(
                  (type) =>
                      DropdownMenuItem(value: type, child: Text(type.name)),
                ),
              ],
              onChanged: (v) => setState(() => platformType = v),
            ),
            const SizedBox(height: 16),
            TextField(
              controller: prefix,
              decoration: const InputDecoration(
                labelText: "Package name prefix",
                hintText: "com.google",
                border: OutlineInputBorder(),
              ),
            ),
            const SizedBox(height: 16),
            TextField(
              controller: packageNames,
              decoration: const InputDecoration(
                labelText: "Package names (comma separated)",
                hintText: "com.android.chrome, com.whatsapp",
                border: OutlineInputBorder(),
              ),
            ),
            const SizedBox(height: 16),
            Row(
              children: [
                Expanded(
                  child: Semantics(
                    button: true,
                    label: "Reset options",
                    child: OutlinedButton(
                      onPressed: _reset,
                      child: const Text("Reset"),
                    ),
                  ),
                ),
                const SizedBox(width: 12),
                Expanded(
                  child: Semantics(
                    button: true,
                    label: "Apply options",
                    child: FilledButton(
                      onPressed: _apply,
                      child: const Text("Apply"),
                    ),
                  ),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }
}
