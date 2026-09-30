import 'package:flutter/material.dart';
import 'package:installed_apps_example/screens/home.dart';

void main() => runApp(const ExampleInstalledApps());

class ExampleInstalledApps extends MaterialApp {
  const ExampleInstalledApps({super.key});

  @override
  Widget get home => const HomeScreen();

  @override
  ThemeData? get theme => ThemeData(useMaterial3: false);
}
