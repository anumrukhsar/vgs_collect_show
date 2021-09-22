import 'dart:math';

import 'package:flutter/foundation.dart';
import 'package:flutter/gestures.dart';
import 'package:flutter/material.dart';
import 'package:flutter/rendering.dart';
import 'package:flutter/services.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      title: 'Flutter Demo',
      theme: ThemeData(
        primarySwatch: Colors.blue,
        visualDensity: VisualDensity.adaptivePlatformDensity,
      ),
      home: MyHomePage(title: 'VGS Collect/Show SDK'),
    );
  }
}

typedef void CardCollectFormCallback(CardCollectFormController controller);

class MyHomePage extends StatefulWidget {
  MyHomePage({Key key, this.title}) : super(key: key);
  final String title;

  @override
  _MyHomePageState createState() => _MyHomePageState();
}

const COLLECT_FORM_VIEW_TYPE = 'card-collect-form-view';
const SHOW_FORM_VIEW_TYPE = 'card-show-form-view';

const CARD_TOKEN_KEY = 'cardNumber';
const DATE_TOKEN_KEY = 'expDate';
const NAME_TOKEN_KEY = 'personName';
const CVC_TOKEN_KEY = 'card_cvc';
const SSN_TOKEN_KEY = 'card_ssn';

const COLLECT_ERROR_CODE_KEY = 'collect_error_code';
const COLLECT_ERROR_MESSAGE_KEY = 'collect_error_message';

const SHOW_SUCCESS_CODE_KEY = 'show_status_code';
const SHOW_ERROR_CODE_KEY = 'show_error_code';
const SHOW_ERROR_MESSAGE_KEY = 'show_error_message';

class _MyHomePageState extends State<MyHomePage> {
  CardCollectFormController collectController;
  CardShowFormController showController;

  String cardToken;
  String dateToken;
  String nameToken;
  String cvcToken;
  String ssnToken;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.title),
      ),
      body:
      SingleChildScrollView(
          child:
          Padding(
            padding: const EdgeInsets.all(16.0),
            child: Column(
              children: [
                ConstrainedBox(
                   constraints: BoxConstraints(maxHeight: 500),
                  child: Column(

                    children: <Widget>[
                  Text(
                        'Collect Card Details',
                        style: TextStyle(
                            fontSize: 20.0,
                            fontWeight: FontWeight.w400,
                            color: Colors.grey.shade700),
                      ),
                      Expanded(
                        child: Builder(builder: (context) {
                          // any logic needed...
                          final isAndorid =
                              defaultTargetPlatform == TargetPlatform.android;

                          return isAndorid ? _cardCollect() : _cardCollectNativeiOS();
                        }),
                      ),
                  Padding(
                          padding: const EdgeInsets.only(bottom: 12.0),
                          child: _collectButton(),
                        ),
                  ]),
                ),
                ConstrainedBox(
                    constraints: BoxConstraints(maxHeight: 400),
                    child: Column(children: <Widget>[
                      Text(
                        'Show Card Details',
                        style: TextStyle(
                            fontSize: 20.0,
                            fontWeight: FontWeight.w400,
                            color: Colors.grey.shade700),
                      ),
                      Flexible(child: Builder(builder: (context) {
                        // any logic needed...
                        final isAndorid =
                            defaultTargetPlatform == TargetPlatform.android;

                        return isAndorid ? _cardShow() : _cardShowNativeiOS();
                      })),
                      Padding(
                        padding: const EdgeInsets.only(bottom: 12.0),
                        child: _showButton(),
                      ),
                    ])),
              ],
            ),
          ),


      )    //       children: <Widget>[

    );
  }

  Widget _cardCollect() {
    final Map<String, dynamic> creationParams = <String, dynamic>{};
    return PlatformViewLink(
      viewType: COLLECT_FORM_VIEW_TYPE,
      surfaceFactory:
          (BuildContext context, PlatformViewController controller) {
        return AndroidViewSurface(
          controller: controller,
          gestureRecognizers: const <Factory<OneSequenceGestureRecognizer>>{},
          hitTestBehavior: PlatformViewHitTestBehavior.opaque,
        );
      },
      onCreatePlatformView: (PlatformViewCreationParams params) {
        var platformView = PlatformViewsService.initSurfaceAndroidView(
          id: params.id,
          viewType: COLLECT_FORM_VIEW_TYPE,
          layoutDirection: TextDirection.ltr,
          creationParams: creationParams,
          creationParamsCodec: StandardMessageCodec(),
        );
        platformView
            .addOnPlatformViewCreatedListener(params.onPlatformViewCreated);
        platformView
            .addOnPlatformViewCreatedListener(_createCardCollectController);
        platformView.create();
        return platformView;
      },
    );
  }

  Widget _collectButton() {
    return MaterialButton(
      minWidth: MediaQuery.of(context).size.width,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8.0)),
      color: Colors.blue,
      child: new Text('Submit',
          style: new TextStyle(fontSize: 16.0, color: Colors.white)),
      onPressed: () {
        collectController.redactCardAsync().then((value) {
          final entries = value.entries;
          print("value.entries: $entries");

          final errorCode = value[COLLECT_ERROR_CODE_KEY];
          if (errorCode != null) {
            print("error!");
            print("VGS Collect SDK error code: $errorCode");

            final errorMessage = value[COLLECT_ERROR_MESSAGE_KEY];
            if (errorMessage != null) {
              print("VGS Collect SDK error message: $errorMessage");
            }
            return;
          }

          cardToken = value.entries
              .firstWhere((element) => element.key == CARD_TOKEN_KEY)
              .value;
          dateToken = value.entries
              .firstWhere((element) => element.key == DATE_TOKEN_KEY)
              .value;
          nameToken = value.entries
              .firstWhere((element) => element.key == NAME_TOKEN_KEY)
              .value;
          cvcToken = value.entries
              .firstWhere((element) => element.key == CVC_TOKEN_KEY)
              .value;
          ssnToken = value.entries
              .firstWhere((element) => element.key == SSN_TOKEN_KEY)
              .value;
          print("cardToken from collect: $cardToken");
          print("dateToken from collect: $dateToken");
          print("nameToken from collect: $nameToken");
          print("cvcToken from collect: $cvcToken");
          print("ssnToken from collect: $ssnToken");
        });
      },
    );
  }

  void _createCardCollectController(int id) {
    print("View id = $id");
    collectController = new CardCollectFormController._(id);
  }

  Widget _cardShow() {
    final Map<String, dynamic> creationParams = <String, dynamic>{};
    return PlatformViewLink(
      viewType: SHOW_FORM_VIEW_TYPE,
      surfaceFactory:
          (BuildContext context, PlatformViewController controller) {
        return AndroidViewSurface(
          controller: controller,
          gestureRecognizers: const <Factory<OneSequenceGestureRecognizer>>{},
          hitTestBehavior: PlatformViewHitTestBehavior.opaque,
        );
      },
      onCreatePlatformView: (PlatformViewCreationParams params) {
        var platformView = PlatformViewsService.initSurfaceAndroidView(
          id: params.id,
          viewType: SHOW_FORM_VIEW_TYPE,
          layoutDirection: TextDirection.ltr,
          creationParams: creationParams,
          creationParamsCodec: StandardMessageCodec(),
        );
        platformView
            .addOnPlatformViewCreatedListener(params.onPlatformViewCreated);
        platformView
            .addOnPlatformViewCreatedListener(_createCardShowController);
        platformView.create();
        return platformView;
      },
    );
  }

  Widget _cardCollectNativeiOS() {
    final Map<String, dynamic> creationParams = <String, dynamic>{};
    return Column(children: [
      SizedBox(
          height: 136.0,
          child: UiKitView(
              viewType: COLLECT_FORM_VIEW_TYPE,
              onPlatformViewCreated: _createCardCollectController,
              creationParams: creationParams,
              creationParamsCodec: StandardMessageCodec()))
    ]);
  }

  Widget _cardShowNativeiOS() {
    final Map<String, dynamic> creationParams = <String, dynamic>{};
    return Column(children: [
      SizedBox(
          height: 136.0,
          child: UiKitView(
              viewType: SHOW_FORM_VIEW_TYPE,
              onPlatformViewCreated: _createCardShowController,
              creationParams: creationParams,
              creationParamsCodec: StandardMessageCodec()))
    ]);
  }

  Widget _showButton() {
    return MaterialButton(
      minWidth: MediaQuery.of(context).size.width,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8.0)),
      color: Colors.blue,
      child: new Text('Reveal',
          style: new TextStyle(fontSize: 16.0, color: Colors.white)),
      onPressed: () {
        showController.revealCardAsync(cardToken,nameToken ,dateToken,cvcToken,ssnToken).then((value) {
          final entries = value.entries;
          print("show value.entries: $entries");

          final errorCode = value[SHOW_ERROR_CODE_KEY];
          if (errorCode != null) {
            print("error!");
            print("VGS Show SDK error code: $errorCode");

            final errorMessage = value[SHOW_ERROR_MESSAGE_KEY];
            if (errorMessage != null) {
              print("VGS Show SDK error message: $errorMessage");
            }
            return;
          }

          final successStatusCode = value[SHOW_SUCCESS_CODE_KEY];
          if (successStatusCode != null) {
            print("VGS Show success status code: $successStatusCode");
          }
        });
      },
    );
  }

  void _createCardShowController(int id) {
    print("Show View id = $id");
    showController = new CardShowFormController._(id);
  }
}

class CardCollectFormController {
  CardCollectFormController._(int id)
      : _channel = new MethodChannel('$COLLECT_FORM_VIEW_TYPE/$id');

  final MethodChannel _channel;

  Future<Map<dynamic, dynamic>> redactCardAsync() async {
    return await _channel.invokeMethod('redactCard', null);
  }
}

class CardShowFormController {
  CardShowFormController._(int id)
      : _channel = new MethodChannel('$SHOW_FORM_VIEW_TYPE/$id');

  final MethodChannel _channel;

  Future<Map<dynamic, dynamic>> revealCardAsync(
      String cardToken,String nameToken, String dateToken,String cvcToken,String ssnToken) async {
    return await _channel.invokeMethod('revealCard', [cardToken,nameToken, dateToken,cvcToken,ssnToken]);
  }
}
