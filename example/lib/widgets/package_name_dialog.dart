import 'package:flutter/material.dart';

class PackageNameDialog extends StatefulWidget {
  const PackageNameDialog({super.key});

  @override
  State<PackageNameDialog> createState() => _PackageNameDialogState();
}

class _PackageNameDialogState extends State<PackageNameDialog> {
  final TextEditingController controller = TextEditingController();

  @override
  void dispose() {
    controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return AlertDialog(
      title: const Text("Find app by package name"),
      content: TextField(
        controller: controller,
        autofocus: true,
        decoration: const InputDecoration(hintText: "com.android.chrome"),
        onSubmitted: (value) => Navigator.pop(context, value),
      ),
      actions: [
        TextButton(
          onPressed: () => Navigator.pop(context),
          child: const Text("Cancel"),
        ),
        FilledButton(
          onPressed: () => Navigator.pop(context, controller.text),
          child: const Text("Find"),
        ),
      ],
    );
  }
}
