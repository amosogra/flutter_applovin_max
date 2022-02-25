package dev.iori.flutter_applovin_max;

import android.app.Activity;

import android.content.Context;
import android.content.DialogInterface;

import androidx.annotation.NonNull;

import com.applovin.sdk.AppLovinMediationProvider;
import com.applovin.sdk.AppLovinPrivacySettings;
import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkConfiguration;
import com.applovin.sdk.AppLovinUserService;
import com.shashank.sony.fancygifdialoglib.FancyGifDialog;
import com.shashank.sony.fancygifdialoglib.FancyGifDialogListener;

import io.flutter.Log;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;
import io.flutter.plugin.common.StandardMethodCodec;
import io.flutter.plugin.platform.PlatformViewRegistry;


public class FlutterApplovinMaxPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware {
    private static FlutterApplovinMaxPlugin instance;
    private RewardedVideo instanceReward;
    private InterstitialVideo instanceInter;
    private Context context;
    private MethodChannel channel;
    public Activity activity;

    public static FlutterApplovinMaxPlugin getInstance() {
        return instance;
    }

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        this.RegistrarBanner(flutterPluginBinding.getFlutterEngine().getPlatformViewsController().getRegistry());
        this.onAttachedToEngine(flutterPluginBinding.getApplicationContext(), flutterPluginBinding.getBinaryMessenger());
    }

    public static void registerWith(Registrar registrar) {
        if (instance == null) {
            instance = new FlutterApplovinMaxPlugin();
        }
        instance.onAttachedToEngine(registrar.context(), registrar.messenger());
    }


    public void onAttachedToEngine(Context applicationContext, BinaryMessenger messenger) {
        if (channel != null) {
            return;
        }
        instance = new FlutterApplovinMaxPlugin();
        Log.i("AppLovin Plugin", "onAttachedToEngine");
        this.context = applicationContext;
        channel = new MethodChannel(messenger, "flutter_applovin_max", StandardMethodCodec.INSTANCE);
        channel.setMethodCallHandler(this);
    }

    public FlutterApplovinMaxPlugin() {
    }

    public void RegistrarBanner(PlatformViewRegistry registry) {
        registry.registerViewFactory("/Banner", new BannerFactory());
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
        try {
            switch (call.method) {
                /* Reward */
                case "InitSdk":
                    AppLovinSdk.getInstance(activity).setMediationProvider(AppLovinMediationProvider.MAX);
                    AppLovinSdk.initializeSdk(activity, new AppLovinSdk.SdkInitializationListener() {
                        @Override
                        public void onSdkInitialized(final AppLovinSdkConfiguration configuration) {
                            if ( configuration.getConsentDialogState() != AppLovinSdkConfiguration.ConsentDialogState.APPLIES )
                            {
                                // Show user consent dialog
                                AppLovinUserService userService = AppLovinSdk.getInstance(activity).getUserService();
                                userService.showConsentDialog(activity, new AppLovinUserService.OnConsentDialogDismissListener() {
                                    @Override
                                    public void onDismiss()
                                    {

                                    }
                                });
                                /*new FancyGifDialog.Builder(activity)
                                        .setTitle("GDPR Compliance Notice")
                                        .setMessage("We care about your privacy and data security. We keep this app free by showing ads. We’ll partner with Google and use a unique identifier on your device to serve only non-personalized ads.\n" +
                                                "For information about how Google uses your mobile identifier please visit:\nhttps://policies.google.com/technologies/partner-sites\n\nThe privacy policies for AppLovin can be found here:\nhttps://www.applovin.com/privacy")
                                        .setNegativeBtnText("Reject")
                                        //.setTitleTextColor(R.color.titleText)
                                        //.setDescriptionTextColor(R.color.descriptionText)
                                        //.setPositiveBtnBackground(R.color.positiveButton)
                                        .setPositiveBtnText("Grant")
                                        //.setNegativeBtnBackground(R.color.negativeButton)
                                        .setGifResource(R.drawable.gif1)
                                        .isCancellable(true)
                                        .setOnCancelListener(new DialogInterface.OnCancelListener() {
                                            @Override
                                            public void onCancel(DialogInterface dialogInterface) {

                                            }
                                        })
                                        .OnPositiveClicked(new FancyGifDialogListener() {
                                            @Override
                                            public void OnClick() {
                                                AppLovinPrivacySettings.setHasUserConsent( true, context );
                                            }
                                        })
                                        .OnNegativeClicked(new FancyGifDialogListener() {
                                            @Override
                                            public void OnClick() {
                                                AppLovinPrivacySettings.setHasUserConsent( false, context );
                                            }
                                        })
                                        .build();*/
                            }
                            else if ( configuration.getConsentDialogState() == AppLovinSdkConfiguration.ConsentDialogState.DOES_NOT_APPLY )
                            {
                                // No need to show consent dialog, proceed with initialization
                            }
                            else
                            {
                                // Consent dialog state is unknown. Proceed with initialization, but check if the consent
                                // dialog should be shown on the next application initialization
                            }
                            AppLovinPrivacySettings.setIsAgeRestrictedUser( false, context );
                        }
                    });
                    break;
                case "ShowMediationDebugger":
                    AppLovinSdk.getInstance(activity).showMediationDebugger();
                    break;
                case "InitRewardAd":
                    String unitId = call.argument("UnitId").toString();
                    instanceReward.Init(unitId);
                    result.success(Boolean.TRUE);
                    break;
                case "ShowRewardVideo":
                    instanceReward.Show();
                    result.success(Boolean.TRUE);
                    break;
                case "IsRewardLoaded":
                    Boolean isLoaded = instanceReward.IsLoaded();
                    result.success(isLoaded);
                    break;
                /* Inter */
                case "InitInterAd":
                    instanceInter.Init(call.argument("UnitId").toString());
                    result.success(Boolean.TRUE);
                    break;
                case "ShowInterVideo":
                    instanceInter.Show();
                    result.success(Boolean.TRUE);
                    break;
                case "IsInterLoaded":
                    result.success(instanceInter.IsLoaded());
                    break;
                default:
                    result.notImplemented();
            }
        } catch (Exception err) {
            Log.e("Method error", err.toString());
            result.notImplemented();
        }
    }

    static public void Callback(final String method) {
        if (instance.context != null && instance.channel != null && instance.activity != null) {
            instance.activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    instance.channel.invokeMethod(method, null);
                }
            });
        } else {
            Log.e("AppLovin", "instance method channel not created");
        }
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        this.context = null;
        this.channel.setMethodCallHandler(null);
        this.channel = null;
    }

    @Override
    public void onAttachedToActivity(ActivityPluginBinding binding) {
        this.activity = binding.getActivity();
        instance.instanceReward = new RewardedVideo();
        instance.instanceInter = new InterstitialVideo();
        Log.i("AppLovin Plugin", "Instances created");
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {
    }

    @Override
    public void onReattachedToActivityForConfigChanges(ActivityPluginBinding binding) {
        this.activity = binding.getActivity();
    }

    @Override
    public void onDetachedFromActivity() {
    }
}
