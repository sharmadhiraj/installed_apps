import 'package:flutter/material.dart';
import 'package:installed_apps/installed_apps.dart';

void main() => runApp(App());

class App extends MaterialApp {
  @override
  Widget get home => HomeScreen();
}

class HomeScreen extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    getInstalledApps();
    return Scaffold(
      appBar: AppBar(title: const Text("Installed Apps Example")),
      body: Center(
        child: Text('Running on: A'),
      ),
    );
  }

  Future<void> getInstalledApps() async {
    List<dynamic> installedApps = await InstalledApps.installedApps;
    print(installedApps);
  }
}
