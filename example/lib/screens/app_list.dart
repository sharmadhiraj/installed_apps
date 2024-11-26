import 'package:flutter/material.dart';
import 'package:installed_apps/app_info.dart';
import 'package:installed_apps/installed_apps.dart';
import 'package:installed_apps_example/screens/app_info.dart';

class AppListScreen extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: _buildAppBar(),
      body: _buildBody(),
    );
  }

  AppBar _buildAppBar() {
    return AppBar(title: Text("Installed Apps"));
  }

  Widget _buildBody() {
    return FutureBuilder<List<AppInfo>>(
      future: InstalledApps.getInstalledApps(false, true, true),
      builder: (
        BuildContext buildContext,
        AsyncSnapshot<List<AppInfo>> snapshot,
      ) {
        return snapshot.connectionState == ConnectionState.done
            ? snapshot.hasData
                ? _buildListView(snapshot.data ?? [])
                : _buildError()
            : _buildProgressIndicator();
      },
    );
  }

  Widget _buildListView(List<AppInfo> apps) {
    return ListView.builder(
      itemCount: apps.length,
      itemBuilder: (context, index) => _buildListItem(context, apps[index]),
    );
  }

  Widget _buildListItem(BuildContext context, AppInfo app) {
    return Card(
      child: ListTile(
        leading: CircleAvatar(
          backgroundColor: Colors.transparent,
          child: Image.memory(app.icon!),
        ),
        title: Text(app.name),
        subtitle: Text(app.getVersionInfo()),
        onTap: () => Navigator.push(
          context,
          MaterialPageRoute(builder: (context) => AppInfoScreen(app: app)),
        ),
      ),
    );
  }

  Widget _buildProgressIndicator() {
    return Center(child: Text("Getting installed apps ...."));
  }

  Widget _buildError() {
    return Center(
      child: Text("Error occurred while getting installed apps ...."),
    );
  }
}
