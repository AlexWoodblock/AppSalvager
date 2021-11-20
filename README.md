[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

# AppSalvager

### What is it?
**AppSalvager** allows you to combat the issue of repeating crashes on app startup. Failed data migration, SDKs not handling their errors correctly, corrupted data...there are many reasons why this can happen.

<img src="https://github.com/DrBreen/AppSalvager/blob/master/.github/images/XQY4BW.gif?raw=true" width="30%">

### Quick start
1. Override `attachBaseContext(Context)` in your `Application` object.
2. Call ` AppSalvager.install(AppSalvager.Config)` there.
3. Override `Application.onCreate()`, and right after calling `super.onCreate`, check boolean flag `AppSalvager.inSalvageMode`. If this flag is `true`, then you should abort normal `onCreate` flow and return as early as possible (ideally, right after the check). Not following that can lead to salvage mode itself crashing.

### Configuration
`AppSalvager.Config` contains following fields:

* `policy` is an object implementing `SalvageModePolicy` interface. This object will determine if exceptions history should trigger salvage mode. You can use one of the default policies, which will be listed shortly.

* `createSalvageView` - factory method to set up `View` for salvage mode screen. The view and factory method should be as simple as possible to minimize the risk of crashes. For example, it could be a text with illustration instructing customer to delete and reinstall the app or clear app data. Or, as in the example, it could be something that cleans `SharedPreferences`.

* `uptimeThreshold` - timeframe in milliseconds that determine whether exception will be regarded as "occurred during startup". Any exception occurring after the threshold will be ignored by AppSalvager.

* `allowedExceptionRecency` - interval in milliseconds that determine if exception is taken into consideration when evaluation the policy. If exception is older than this interval, then it's going to be discarded.

### Available salvage mode policies
* `ExceptionNumberPolicy` - if recent exceptions number exceed a certain threshold (3 by default), salvage mode will be triggered.

* `SameExceptionPolicy` - if recent exception is the same, salvage mode will be triggered. `SameExceptionPolicy.Configuration` can be used to fine tune comparison parameters.

* `CompositeSalvageModePolicy` - wrapper that checks several policies, and triggers salvage mode if all the policies agreed to trigger it.


### Reporting fixed problem in salvage mode
If you have some specific steps inside your salvaging mode that should fix your problems, you can clean exception history by calling `AppSalvager.resetExceptions`. This is an optional step - if on next startup app did not have any exceptions in `uptimeThreshold` timeframe since startup, then this method is going to be called automatically.

### Limitations
Repeated `java.lang.Error`s are not processed. JVM errors may lead to undefined behavior if code execution is
continued after an error.

### Integration with Crashlytics/other automatic exception reporting frameworks
**AppSalvage** sets its' exception handler as default exception handler, so all the crash reporting frameworks should work out of the box.
If there are cases where **AppSalvage** does not work, please open an issue.

### Pull requests
As of now, there is no defined policy on code style or pull request content, so it's basically free for all :)
I'll be very happy to see what you want to improve in the library.

### License
Apache 2.0 license is used. See `LICENSE` file for details.
