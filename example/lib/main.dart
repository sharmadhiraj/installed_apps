import 'package:example/screens/home.dart';
import 'package:flutter/material.dart';

void main() => runApp(App());

class App extends MaterialApp {
  const App({super.key});

  @override
  Widget get home => HomeScreen();

  @override
  ThemeData? get theme => ThemeData(useMaterial3: false);
}
