import 'package:flutter/material.dart';
import 'package:installed_apps_example/screens/home.dart';

void main() => runApp(App());

class App extends MaterialApp {
  @override
  Widget get home => HomeScreen();

  @override
  ThemeData? get theme => ThemeData(useMaterial3: false);
}
