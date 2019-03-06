import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'dart:async';
import 'dart:ui';

void main() => runApp(_widgetForRoute(window.defaultRouteName));

Widget _widgetForRoute(String route) {
  print('flutter_layer : route is $route');
  if (route == 'main') {
    print('flutter_layer : run MyApp');
    return MyApp();
  }
}

class MyApp extends StatelessWidget {
  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    print('flutter_layer : build state');
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: MyHomePage(title: 'Flutter Demo Home Page'),
    );
  }
}

class MyHomePage extends StatefulWidget {
  MyHomePage({Key key, this.title}) : super(key: key);
  final String title;

  @override
  _MyHomePageState createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {

  String _nativeResult;
  TextEditingController _controller;
  static const MethodChannel _channel =
  const MethodChannel('nscode.flutter.io/message');
  FocusNode _node = FocusNode();
  FocusScopeNode _scopeNode;
  @override
  void initState() {
    // TODO: implement initState
    super.initState();
    _nativeResult = '';
    _controller = TextEditingController();
    _channel.setMethodCallHandler((call) async {
      if (call.method == 'setText') {
        setText(content: call.arguments);
        return 'native call successfully';
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    // TODO: implement build
    return Scaffold(
      body: GestureDetector(
        onTap: () async{
          await _channel.invokeMethod('releaseFocus',);
          if(_scopeNode == null){
            _scopeNode = FocusScope.of(context);
          }
          _scopeNode.requestFocus(_node);
        },
        child: Container(
          decoration: BoxDecoration(
              border: Border.all(color: Colors.black, width: 2),
              borderRadius: BorderRadius.circular(10)),
          padding: EdgeInsets.all(20),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.start,
            children: <Widget>[
              TextField(
                focusNode: _node,
                controller: _controller,
                decoration:
                InputDecoration(hintText: 'Please input native msg'),
              ),
              Padding(
                padding: EdgeInsets.only(top: 20),
                child: Text('$_nativeResult'),
              ),
              Padding(
                padding: EdgeInsets.only(top: 20),
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: <Widget>[
                    RaisedButton(
                      onPressed: sendToNative,
                      child: Text('flutter send'),
                    )
                  ],
                ),
              )
            ],
          ),
        ),
      ),
    );
  }
  @override
  void dispose() {
    // TODO: implement dispose
    _controller.dispose();
    super.dispose();
  }

  Future<void> sendToNative() async {
    _nativeResult = await _channel.invokeMethod('setText', _controller.text);
    print(_nativeResult);
  }

  void setText({@required String content}) {
    setState(() {
      _nativeResult = content;
    });
  }

}