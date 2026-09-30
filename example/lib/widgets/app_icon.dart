import 'dart:typed_data';

import 'package:flutter/material.dart';
import 'package:installed_apps/installed_apps.dart';

class AppIcon extends StatelessWidget {
  final AppInfo app;
  final double size;

  const AppIcon({required this.app, super.key, this.size = 40});

  @override
  Widget build(BuildContext context) {
    final Widget fallback = Icon(Icons.android, size: size);
    final Uint8List? icon = app.icon;
    if (icon == null) return fallback;
    return Image.memory(
      icon,
      width: size,
      height: size,
      gaplessPlayback: true,
      errorBuilder: (_, __, ___) => fallback,
    );
  }
}
