this.app = angular.module('cs-mobile-cooking', ['ionic', 'ngCordova', 'ngLocalize', 'ngLocalize.Events', 'http-auth-interceptor', 'velocity.ui', 'LocalStorageModule', 'connectionSimulator', 'cs-shared', 'ngIOS9UIWebViewPatch', 'ngOpenFB']);

this.app.run(["$cordovaKeyboard", "$rootScope", "$state", "$window", "$location", "$ionicHistory", "appConfig", "collectionService", "advertisementService", "locale", "$animate", "devSimulatorService", "authenticationService", "analyticsService", "resourceService", "userService", "debugService", "metadataHeartbeatService", "pushRegistrationService", "hotDeployService", "preferences", "$timeout", "storagePurgeService", "cacheService", "connectionTroubleshootingService", "$ionicPlatform", function($cordovaKeyboard, $rootScope, $state, $window, $location, $ionicHistory, appConfig, collectionService, advertisementService, locale, $animate, devSimulatorService, authenticationService, analyticsService, resourceService, userService, debugService, metadataHeartbeatService, pushRegistrationService, hotDeployService, preferences, $timeout, storagePurgeService, cacheService, connectionTroubleshootingService, $ionicPlatform) {
  var TAG, salt;
  TAG = 'App';
  if (!cacheService.get('salt', 'security')) {
    salt = $window.bcrypt.genSaltSync(4);
    cacheService.set('salt', 'security', salt);
  }

  /* eslint-disable no-alert */
  window.onerror = function(message, source, lineno, colno, error) {
    debugService.error('unhandled error', TAG, {
      message: message,
      source: source,
      lineno: lineno,
      colno: colno,
      error: error
    });
    if (false) {
      alert('unhandled error ' + message);
    }
    return false;
  };

  /* eslint-enable no-alert */
  $rootScope.$on('$stateChangeStart', function(event, toState, toParams, fromState) {
    var properties;
    properties = {
      toState: toState.url,
      fromState: fromState.url
    };
    if (Object.keys(toParams).length) {
      properties.toParams = toParams;
    }
    return analyticsService.track("navigation from " + fromState.url + " to " + toState.url, properties);
  });
  storagePurgeService.initialize();
  cacheService.set('dfuAutocheckEnabled', 'preference', true);
  if (!false) {
    hotDeployService.initialize();
  }
  $animate.enabled(false);
  locale.ready(['authentication', 'circulatorCook', 'general', 'deviceAbout', 'pairing', 'popup', 'time', 'menu', 'settings', 'legal', 'onboarding', 'social', 'circulatorError', 'update', 'marketing', 'sidemenu', 'connectionTroubleshooting']);
  collectionService.getBySlug('sous-vide-tips', {
    populate: true
  });
  collectionService.getBySlug('home-collections', {
    populate: true
  });
  collectionService.getBySlug('home-filters', {
    populate: true
  });
  collectionService.getBySlug('home-hero', {
    populate: true
  });
  $state.go('splash');
  if (authenticationService.isAuthenticated()) {
    authenticationService.me().then(function(user) {
      var token;
      token = authenticationService.getToken();
      userService.signIn(user, token);
      return pushRegistrationService.register();
    })["catch"](function(error) {
      var token;
      debugService.error('We have an authentication token but sign in has failed.', TAG, {
        error: error,
        navigatorOnline: $window.navigator.onLine
      });
      token = authenticationService.getToken();
      return userService.signIn(null, token);
    })["finally"](function() {
      $ionicHistory.nextViewOptions({
        disableBack: true,
        historyRoot: true
      });
      return $state.go(appConfig.defaultView);
    });
  } else {
    $ionicHistory.nextViewOptions({
      disableBack: true,
      historyRoot: true
    });
    $state.go('welcome');
  }
  return ionic.Platform.ready(function() {
    var deviceModel, ref;
    debugService.log('Ionic platform is ready', TAG);
    window.open_orig = window.open;
    window.open = function(url, target, options) {
      if (!options) {
        options = 'allowinlinemediaplayback=YES';
      } else {
        options = options.concat(',allowinlinemediaplayback=YES');
      }
      return window.open_orig(url, target, options);
    };
    if (ionic.Platform.isWebView()) {
      $cordovaKeyboard.hideAccessoryBar(true);
      $window.Joule.initializeWebView(function(result) {
        return debugService.log('Joule plugin initialized with response: ' + result, TAG, {
          result: result
        });
      }, function(error) {
        return debugService.warn('Unable to initialize Joule plugin with error: ' + error, TAG, {
          error: error
        });
      });
    }
    $timeout((ref = navigator.splashscreen) != null ? ref.hide : void 0, 1000);
    $rootScope.isOverflowScrollEnabled = ionic.Platform.isAndroid();
    if (ionic.Platform.isAndroid()) {
      ionic.Platform.isFullScreen = true;
    }
    deviceModel = ionic.Platform.device().model;
    preferences.setDefault('useLessData', true);
    preferences.setDefault('guideManifestEndpoint', 'production');
    preferences.setDefault('enableVideo', !_.includes(appConfig.imageOnlyDevices, deviceModel));
    debugService.log('User preferences', TAG, {
      userPreferences: preferences.getAll()
    });
    pushRegistrationService.isNotificationEnabled().then(function(notificationEnabled) {
      return debugService.log('Push notification settings', TAG, {
        notificationEnabled: notificationEnabled
      });
    });
    resourceService.fetch().then(function() {
      resourceService.updateAll('guide');
      resourceService.updateAll('collection');
      return resourceService.updateAll('step');
    });
    devSimulatorService.initialize();
    analyticsService.initialize();
    analyticsService.track('Joule Cooking App Opened');
    metadataHeartbeatService.initialize();
    connectionTroubleshootingService.initialize();
    advertisementService.getAdContent('#/home', 'homeHero');
    return $ionicPlatform.onHardwareBackButton(function() {
      return debugService.log('Hardware back button pushed', TAG);
    });
  });
}]);

this.app.value('localeSupported', ['en-US']);

this.app.config(["$compileProvider", function($compileProvider) {
  $compileProvider.aHrefSanitizationWhitelist(/^\s*(https?|ftp|mailto|file|tel):/);
  return $compileProvider.imgSrcSanitizationWhitelist(/^\s*(https?|ftp|mailto|file|tel|data):/);
}]);

this.app.config(["$sceProvider", function($sceProvider) {
  return $sceProvider.enabled(false);
}]);

this.app.config(["$ionicConfigProvider", function($ionicConfigProvider) {
  return $ionicConfigProvider.views.swipeBackEnabled(false);
}]);

this.app.config(["$stateProvider", function($stateProvider) {
  var params, results, state, states;
  states = {
    'createAccount': {
      url: '/create-account',
      controller: 'createAccountController',
      templateUrl: 'templates/views/create-account/create-account.html'
    },
    'deviceAbout': {
      url: '/device-about',
      controller: 'deviceAboutController',
      templateUrl: 'templates/views/device-about/device-about.html'
    },
    'splash': {
      cache: false,
      url: '/splash',
      templateUrl: 'templates/views/splash/splash.html'
    },
    'home': {
      url: '/home',
      controller: 'collectionController',
      templateUrl: 'templates/views/home/home.html'
    },
    'training': {
      url: '/training',
      controller: 'trainingController',
      templateUrl: 'templates/views/training/training.html'
    },
    'legal': {
      url: '/legal',
      controller: 'legalController',
      templateUrl: 'templates/views/legal/legal.html'
    },
    'support': {
      url: '/support',
      controller: 'supportController',
      templateUrl: 'templates/views/support/support.html'
    },
    'pairingSequencePrompt': {
      cache: false,
      url: '/pairingSequencePrompt/:isOnboarding',
      controller: 'pairingPromptController',
      templateUrl: 'templates/views/pairing/pairing-prompt.html',
      data: {
        nextView: 'pairingSequencePairing'
      }
    },
    'pairingSequencePairing': {
      url: '/pairingSequencePairing',
      controller: 'pairingController',
      templateUrl: 'templates/views/pairing/pairing.html',
      data: {
        nextView: 'pairingSequenceCirculatorRename'
      }
    },
    'circulatorRename': {
      url: '/circulator/rename',
      controller: 'circulatorNameController',
      templateUrl: 'templates/views/circulator-rename/circulator-rename.html'
    },
    'pairingSequenceCirculatorRename': {
      url: 'circulator/pairingSequenceRename',
      controller: 'circulatorNameController',
      templateUrl: 'templates/views/pairing-circulator-name/pairing-circulator-name.html',
      data: {
        nextView: 'pairingSequenceWifi'
      }
    },
    'pairingSequenceWifi': {
      url: '/circulator/wifi',
      controller: 'circulatorWifiController',
      templateUrl: 'templates/views/circulator-wifi/circulator-wifi.html',
      data: {
        nextView: 'pairingSequenceSuccess'
      }
    },
    'pairingSequenceSuccess': {
      cache: false,
      url: 'circulator/pairingSequenceSuccess',
      controller: 'pairingSequenceSuccessController',
      templateUrl: 'templates/views/pairing-sequence-success/pairing-sequence-success.html'
    },
    'trainingSuccess': {
      url: 'trainingSuccess',
      controller: 'trainingSuccessController',
      templateUrl: 'templates/views/pairing/training-success.html'
    },
    'circulatorWifi': {
      url: '/circulator/wifi',
      controller: 'circulatorWifiController',
      templateUrl: 'templates/views/circulator-wifi/circulator-wifi.html'
    },
    'collection': {
      url: '/collection/:slug',
      controller: 'collectionController',
      templateUrl: 'templates/views/collection/collection.html'
    },
    'cook': {
      url: '/cook?showStepsHint&shouldShowTempHint',
      controller: 'cookController',
      templateUrl: 'templates/views/cook/cook.html'
    },
    'firmwareUpdate': {
      url: '/circulator/update',
      controller: 'firmwareUpdateController',
      templateUrl: 'templates/views/firmware-update/firmware-update.html'
    },
    'developerSettings': {
      url: '/developer-settings',
      controller: 'developerSettingsController',
      templateUrl: 'templates/views/developer-settings/developer-settings.html'
    },
    'deviceSettings': {
      url: '/device-settings',
      controller: 'deviceSettingsController',
      templateUrl: 'templates/views/device-settings/device-settings.html'
    },
    'guideDoneness': {
      url: '/guide/:id/doneness',
      controller: 'guideDonenessController',
      templateUrl: 'templates/views/guide-doneness/guide-doneness.html'
    },
    'guideOverview': {
      cache: false,
      url: '/guide/:id/overview',
      controller: 'guideOverviewController',
      templateUrl: 'templates/views/guide-overview/guide-overview.html'
    },
    'guideSteps': {
      cache: false,
      url: '/guide/:id/steps',
      controller: 'guideStepsController',
      templateUrl: 'templates/views/guide-steps/guide-steps.html'
    },
    'guideTimer': {
      url: '/guide/:id/timer/:programId?update',
      controller: 'guideTimerController',
      templateUrl: 'templates/views/guide-timer/guide-timer.html'
    },
    'profile': {
      url: '/profile',
      controller: 'profileController',
      templateUrl: 'templates/views/profile/profile.html'
    },
    'settings': {
      url: '/settings',
      controller: 'settingsController',
      templateUrl: 'templates/views/settings/settings.html'
    },
    'signIn': {
      url: '/sign-in',
      controller: 'signInController as signIn',
      templateUrl: 'templates/views/sign-in/sign-in.html'
    },
    'signInConfirmation': {
      url: '/sign-in-confirmation',
      controller: 'signInConfirmationController',
      templateUrl: 'templates/views/sign-in-confirmation/sign-in-confirmation.html'
    },
    'onboarding': {
      cache: false,
      url: '/onboarding',
      controller: 'onboardingController',
      templateUrl: 'templates/views/onboarding/onboarding.html'
    },
    'notifications': {
      cache: false,
      url: '/notifications',
      controller: 'notificationsController',
      templateUrl: 'templates/views/onboarding/notifications.html'
    },
    'sousVideTips': {
      cache: false,
      url: '/sous-vide-tips',
      controller: 'sousVideTipsController',
      templateUrl: 'templates/views/sous-vide-tips/sous-vide-tips.html'
    },
    'temperatureEntry': {
      url: '/temperature-entry',
      controller: 'temperatureEntryController',
      templateUrl: 'templates/views/temperature-entry/temperature-entry.html'
    },
    'temperatureUpdate': {
      url: '/temperature-update',
      controller: 'temperatureEntryController',
      templateUrl: 'templates/views/temperature-entry/temperature-entry.html'
    },
    'timer': {
      url: '/timer',
      controller: 'timerController',
      templateUrl: 'templates/views/timer/timer.html'
    },
    'welcome': {
      url: '/welcome',
      controller: 'welcomeController',
      templateUrl: 'templates/views/welcome/welcome.html'
    },
    'feedback': {
      url: '/feedback',
      controller: 'feedbackController',
      templateUrl: 'templates/views/feedback/feedback.html'
    },
    'about': {
      url: '/about',
      controller: 'aboutController',
      templateUrl: 'templates/views/about/about.html'
    },
    'buyJoule': {
      cache: false,
      url: '/guide/:id/buyJoule',
      controller: 'buyJouleController',
      templateUrl: 'templates/views/marketing/buy-joule.html'
    },
    'connectionTroubleshooting': {
      url: '/connection-troubleshooting',
      controller: 'connectionTroubleshootingController',
      templateUrl: 'templates/views/connection-troubleshooting/connection-troubleshooting.html'
    }
  };
  results = [];
  for (state in states) {
    params = states[state];
    results.push($stateProvider.state(state, params));
  }
  return results;
}]);

this.app.config(["$ionicConfigProvider", function($ionicConfigProvider) {
  $ionicConfigProvider.transitions.views.chefsteps = function(enteringEle, leavingEle, direction) {
    var transition;
    transition = function(step) {
      var incomingView;
      if (direction === 'none') {
        return;
      }
      incomingView = enteringEle != null ? enteringEle[0] : void 0;
      if (step === 0) {
        return incomingView != null ? incomingView.style.opacity = '0' : void 0;
      } else {
        if (incomingView != null) {
          incomingView.style.opacity = '1';
        }
        return incomingView != null ? incomingView.style.webkitTransition = 'opacity 250ms' : void 0;
      }
    };
    return {
      run: transition,
      shouldAnimate: true
    };
  };
  $ionicConfigProvider.transitions.navBar.chefsteps = function() {
    return {
      run: angular.noop
    };
  };
  return $ionicConfigProvider.views.transition('chefsteps');
}]);

this.app.constant('appConfig', {
  contentTypes: ['collection', 'guide', 'step'],
  imageOnlyDevices: ['iPod5,1', 'iPhone4,1', 'SAMSUNG-SM-G900A', 'BLU Advance 5.0'],
  defaultView: 'home',
  scanIntervalMilliseconds: 15000,
  metadataHeartbeatIntervalMilliseconds: 10000,
  minimumTemperature: 20,
  maximumTemperature: 90,
  assetBasePath: 'http://d92f495ogyf88.cloudfront.net/circulator/',
  developmentGuideManifestEndpoint: 'http://api.jouleapp.com/manifests/resources.json',
  stagingGuideManifestEndpoint: 'https://d1azuiz827qxpe.cloudfront.net/resources/staging/resources.json',
  productionGuideManifestEndpoint: 'https://d1azuiz827qxpe.cloudfront.net/resources/latest/resources.json',
  applicationResourcesPath: 'json/resources.json',
  jouleSalesPage: 'https://www.chefsteps.com/joule'
});

this.app.constant('backButtonPriorities', {
  navigation: 1000,
  noop: 10000
});

this.app.constant('bluetoothStates', {
  poweredOn: 0,
  poweredOff: 1,
  resetting: 2,
  unauthorized: 3,
  unsupported: 4,
  unknown: 5
});

this.app.constant('circulatorButtonStyles', {
  powerNormal: 1,
  powerInactive: 2,
  powerInactiveLight: 3,
  powerActivate: 4,
  powerError: 5,
  textNormal: 6,
  textActivate: 7,
  textInactive: 8,
  temperatureNormal: 9,
  temperatureError: 10,
  connectingNormal: 11,
  connectingActivate: 12,
  hidden: 13,
  powerDarkInactive: 14
});

this.app.constant('circulatorConnectingMaybeStates', {
  maybeCooking: 'MAYBE_COOKING',
  maybeIdle: 'MAYBE_IDLE',
  maybeDisconnected: 'MAYBE_DISCONNECTED'
});

this.app.constant('circulatorConnectionStates', {
  unpaired: 'unpaired',
  connected: 'connected',
  disconnected: 'disconnected',
  connecting: 'connecting',
  jouleFound: 'jouleFound'
});

this.app.constant('circulatorErrorStatePriorities', {
  NO_ERROR: 0,
  SOFT_ERROR: 1,
  HARD_ERROR: 2
});

this.app.constant('circulatorErrorStates', {
  NO_ERROR: 'NO_ERROR',
  SOFT_ERROR: 'SOFT_ERROR',
  HARD_ERROR: 'HARD_ERROR'
});

this.app.constant('circulatorEventReasons', {
  UNKNOWN_REASON: 'UNKNOWN_REASON',
  HARDWARE_FAILURE: 'HARDWARE_FAILURE',
  BUTTON_PRESSED: 'BUTTON_PRESSED',
  LOW_WATER_LEVEL: 'LOW_WATER_LEVEL',
  TIPPED_OVER: 'TIPPED_OVER',
  OVERHEATING: 'OVERHEATING',
  POWER_LOSS: 'POWER_LOSS'
});

this.app.constant('circulatorEventTypes', {
  INITIALIZATION_FAILURE: 'INITIALIZATION_FAILURE',
  STOP_PROGRAM: 'STOP_PROGRAM',
  UNKNOWN_TYPE: 'UNKNOWN_TYPE'
});

this.app.constant('colors', {
  white: '#ffffff',
  primaryText: '#43413f',
  textPrimary: '#43413f',
  textSecondary: '#b8b6b4',
  backdrop: '#000000',
  brandPrimary: '#ff674c',
  brandSecondary: '#ffb3a5',
  buttonBackgroundDisabledOnSecondary: '#fadbd5',
  buttonBackgroundDisabledOnPrimary: '#f6f6f5',
  buttonTextDisabledOnPrimary: '#f5a99a'
});

this.app.service('connectionProvidersConfig', ["csConfig", function(csConfig) {
  return {
    bluetooth: {
      type: 'bluetooth',
      discovery: {
        streamServiceUUID: '700B4321-9836-4383-A2B2-31A9098D1473',
        manufacturerId: '0159'
      },
      communication: {
        fileCharacteristicUUID: '700B4326-9836-4383-A2B2-31A9098D1473',
        subscribeCharacteristicUUID: '700B4325-9836-4383-A2B2-31A9098D1473',
        readCharacteristicUUID: '700B4323-9836-4383-A2B2-31A9098D1473',
        writeCharacteristicUUID: '700B4322-9836-4383-A2B2-31A9098D1473'
      },
      reconnectBaseTime: 20000,
      connectionTimeout: 10000,
      scanTimeout: 4000
    },
    webSocket: {
      type: 'webSocket',
      discovery: csConfig.webSocketEndpoint,
      reconnectBaseTime: 20000,
      connectionTimeout: 10000,
      pingTimeoutSeconds: 10
    }
  };
}]);

this.app.constant('connectionState', {
  disconnected: 'DISCONNECTED',
  connected: 'CONNECTED',
  connectedAuthorized: 'CONNECTED_AUTHORIZED',
  connecting: 'CONNECTING'
});

this.app.constant('cookStates', {
  idle: 'IDLE',
  cooking: 'COOKING'
});

this.app.service('csConfig', function() {
  return {
    debug: {
      chefstepsEndpoint: 'https://www.chocolateyshatner.com',
      webSocketEndpoint: 'wss://staging-cc-app-1.chefsteps.com/direct',
      forgotPasswordUrl: 'https://www.chocolateyshatner.com/password-reset',
      segmentWriteKey: 'd1iKTmkGittQwXQbBSY7egXumHgmTai4',
      facebookAppId: '642634055780525'
    },
    production: {
      chefstepsEndpoint: 'https://www.chefsteps.com',
      webSocketEndpoint: 'wss://production-cc-app-1.chefsteps.com/direct',
      forgotPasswordUrl: 'https://www.chefsteps.com/password-reset',
      segmentWriteKey: 'CohfhzCATDidS52kLILe3ZZ3mVYYgzsP',
      facebookAppId: '380147598730003'
    }
  }['production'];
});

this.app.constant('disconnectReasons', {
  initialState: 'INITIAL_STATE',
  terminatedByOther: 'TERMINATED_BY_OTHER',
  unreachableAddress: 'UNREACHABLE_ADDRESS',
  manualReset: 'MANUAL_RESET',
  inactive: 'INACTIVE',
  cleanUp: 'CLEAN_UP',
  writeError: 'WRITE_ERROR',
  readError: 'READ_ERROR',
  openTimeout: 'OPEN_TIMEOUT',
  noInternet: 'NO_INTERNET',
  subscribeError: 'SUBSCRIBE_ERROR',
  connectError: 'CONNECT_ERROR',
  authorizeError: 'AUTHORIZE_ERROR',
  deviceAddressNotFound: 'DEVICE_ADDRESS_NOT_FOUND',
  deviceNotFound: 'DEVICE_NOT_FOUND',
  deviceAlreadyConnected: 'DEVICE_ALREADY_CONNECTED',
  gattFailure: 'GATT_FAILURE',
  powerError: 'POWER_ERROR',
  missingAddress: 'MISSING_ADDRESS',
  disconnectWifi: 'DISCONNECT_WIFI',
  pairingFail: 'PAIRING_FAIL'
});

this.app.service('faqLinkConfig', ["externalLinkPreprocessor", function(externalLinkPreprocessor) {
  return {
    cantConnectToWifi: {
      uri: 'https://support.chefsteps.com/hc/en-us/articles/223021308--Why-can-t-I-connect-Joule-to-WiFi-',
      preprocessor: externalLinkPreprocessor.sso
    },
    cantSignIn: {
      uri: 'https://support.chefsteps.com/hc/en-us/articles/223020748-Why-can-t-I-sign-in-'
    },
    cantPair: {
      uri: 'https://support.chefsteps.com/hc/en-us/articles/223021408-I-can-t-pair-with-Joule-',
      preprocessor: externalLinkPreprocessor.sso
    },
    pairedButCantConnect: {
      uri: 'https://support.chefsteps.com/hc/en-us/articles/223021468-I-ve-paired-with-Joule-but-I-can-t-connect-',
      preprocessor: externalLinkPreprocessor.sso
    },
    noOwnerNoWifi: {
      uri: 'https://support.chefsteps.com/hc/en-us/articles/223021348--I-can-t-connect-Joule-to-WiFi-because-I-m-not-the-owner-',
      preprocessor: externalLinkPreprocessor.sso
    },
    dfuProblems: {
      uri: 'https://support.chefsteps.com/hc/en-us/articles/225107367-My-firmware-update-keeps-failing-A-little-help-',
      preprocessor: externalLinkPreprocessor.sso
    },
    maximumTemperature: {
      uri: 'https://support.chefsteps.com/hc/en-us/articles/214790827-What-is-the-maximum-temperature-Joule-will-reach-',
      preprocessor: externalLinkPreprocessor.sso
    }
  };
}]);

this.app.constant('firmwareFileTypes', {
  APPLICATION_FIRMWARE: 'APPLICATION_FIRMWARE',
  SOFTDEVICE_FIRMWARE: 'SOFTDEVICE_FIRMWARE',
  WIFI_FIRMWARE: 'WIFI_FIRMWARE',
  CERTIFICATE_FIRMWARE: 'CERTIFICATE_FIRMWARE',
  BOOTLOADER_FIRMWARE: 'BOOTLOADER_FIRMWARE'
});

this.app.constant('firmwareTransferTypes', {
  tftp: 'tftp',
  download: 'download',
  http: 'http'
});

this.app.service('firmwareUpdateConfig', ["csConfig", function(csConfig) {
  return {
    firmwareUpdatesBasePath: csConfig.chefstepsEndpoint,
    packetsPerConnectionInterval: 2,
    connectionIntervalDelayIos: 60,
    perPacketDelayIos: 0,
    connectionIntervalDelayAndroid: 0,
    transferFileBlockTimeoutSecs: 4,
    transferFileBlockRetryAttempts: 5,
    numGetWiFiDFUStatusRetries: 4,
    getWifiDFUStatusRetryDelaySecs: 8,
    reconnectTimeoutSecs: 60,
    postRebootDelaySecs: 5
  };
}]);

this.app.service('floatingActionButtonStates', ["locale", function(locale) {
  return {
    unpaired: {
      classes: null,
      icon: 'connect-joule',
      text: null,
      banner: null
    },
    connected: {
      classes: 'theme-active',
      icon: 'power',
      text: null,
      banner: null
    },
    connecting: {
      classes: 'busy',
      icon: 'pairing-fab-indicator',
      text: null,
      banner: null
    },
    connectingMaybeCooking: {
      classes: 'theme-active temperature',
      icon: 'maybe-cooking',
      banner: null
    },
    connectingMaybeIdle: {
      classes: 'theme-active',
      icon: 'pairing-fab-indicator',
      text: null,
      banner: null
    },
    connectingMaybeDisconnected: {
      classes: 'faded',
      icon: 'pairing-fab-indicator',
      text: null
    },
    connectingFaded: {
      classes: 'faded-transparent',
      icon: 'pairing-fab-indicator',
      text: null
    },
    jouleFound: {
      classes: 'joule-found expanded theme-active',
      icon: 'joule-found',
      text: null,
      banner: locale.getString('pairing.jouleFoundPrompt')
    },
    firmwareUpdateAvailable: {
      classes: 'theme-active update-text',
      icon: null,
      text: locale.getString('general.update'),
      banner: locale.getString('pairing.firmwareUpdateAvailablePrompt')
    },
    next: {
      classes: 'theme-active next',
      text: locale.getString('general.next'),
      icon: null,
      banner: null
    },
    waitForFood: {
      classes: 'hidden',
      text: null,
      icon: null,
      banner: null
    },
    cooking: {
      classes: 'theme-active temperature',
      icon: null,
      banner: null
    },
    error: {
      classes: 'faded temperature',
      icon: null,
      banner: null
    },
    powerActivate: {
      classes: 'expanded theme-active',
      icon: 'power',
      text: null,
      banner: null
    },
    powerActive: {
      classes: 'dark',
      icon: 'power',
      text: null,
      banner: null
    },
    powerDarkInactive: {
      text: null,
      classes: 'dark-faded-transparent',
      icon: 'power',
      banner: null
    },
    powerInactive: {
      text: null,
      classes: 'faded-transparent',
      icon: 'power',
      banner: null
    },
    disconnected: {
      text: null,
      classes: 'faded',
      icon: 'power',
      banner: null
    },
    disconnectedFadedTransparent: {
      text: null,
      classes: 'faded-transparent',
      icon: 'power',
      banner: null
    },
    startInactive: {
      classes: 'faded timer-text',
      icon: null,
      text: locale.getString('general.start'),
      banner: null
    },
    startActivate: {
      classes: 'expanded theme-active timer-text',
      icon: null,
      text: locale.getString('general.start'),
      banner: null
    }
  };
}]);

this.app.constant('guideManifestEndpoints', {
  DEVELOPMENT: 'development',
  STAGING: 'staging',
  PRODUCTION: 'production'
});

this.app.constant('loggingConfig', {
  remoteFileSignerPath: '/api/v0/users/log_upload_url',
  fileRotationInterval: 1000 * 60 * 5,
  maxFileSize: 500 * 1024
});

this.app.constant('loggingTags', {
  circulatorButton: 'CirculatorButton',
  cook: 'Cook',
  bluetooth: 'Bluetooth',
  connectionTroubleshooting: 'ConnectionTroubleshooting'
});

this.app.constant('navigationButtonStates', {
  back: 'back',
  hidden: 'hidden',
  home: 'home'
});

this.app.constant('notificationTypes', {
  WATER_HEATED: 'water_heated',
  CIRCULATOR_ERROR_HARDWARE_FAILURE: 'circulator_error_hardware_failure',
  CIRCULATOR_ERROR_BUTTON_PRESSED: 'circulator_error_button_pressed',
  CIRCULATOR_ERROR_LOW_WATER_LEVEL: 'circulator_error_low_water_level',
  CIRCULATOR_ERROR_TIPPED_OVER: 'circulator_error_tipped_over',
  CIRCULATOR_ERROR_OVERHEATING: 'circulator_error_overheating',
  CIRCULATOR_ERROR_POWER_LOSS: 'circulator_error_power_loss',
  CIRCULATOR_ERROR_UNKNOWN_REASON: 'circulator_error_unknown_reason'
});

this.app.constant('programSteps', {
  unknown: 'UNKNOWN',
  preheat: 'PRE_HEAT',
  waitForFood: 'WAIT_FOR_FOOD',
  cook: 'COOK',
  waitForRemoveFood: 'WAIT_FOR_REMOVE_FOOD',
  error: 'ERROR'
});

this.app.constant('programTypes', {
  manual: 'MANUAL',
  automatic: 'AUTOMATIC'
});

this.app.service('pushRegistrationConfig', ["csConfig", function(csConfig) {
  return {
    pushRegistrationBasePath: csConfig.chefstepsEndpoint,
    options: {
      android: {
        senderID: 954060247489,
        sound: true,
        vibrate: true,
        clearNotifications: false,
        forceShow: false
      },
      ios: {
        sound: true,
        alert: true,
        badge: false,
        clearBadge: false
      }
    }
  };
}]);

this.app.constant('routingConfig', {
  "default": ['bluetooth', 'webSocket'],
  preferredRoute: 'webSocket',
  fileTransferPreferredRoute: 'bluetooth',
  fileTransferInitiationMessage: 'startFileTransferRequest',
  fileTransferMessages: ['transferFileBlockRequest'],
  messageTypeRules: {
    startKeyExchangeRequest: ['bluetooth'],
    submitKeyRequest: ['bluetooth'],
    listWifiRequest: ['bluetooth'],
    connectWifiRequest: ['bluetooth'],
    disconnectWifiRequest: ['bluetooth']
  },
  messagesNotRequiringAuth: ['submitKeyRequest']
});

this.app.constant('statusBarStyles', {
  light: 0,
  dark: 1,
  hidden: 2
});

this.app.constant('storagePurgeConfig', {
  mapOfVersionAndPurgeNamespaces: {
    '0.25.3': ['circulator', 'user', 'devSimulatorService', 'training']
  }
});

this.app.constant('wifiConnectionStates', {
  WIFI_IDLE: 'WIFI_IDLE',
  WIFI_CONNECTING: 'WIFI_CONNECTING',
  WIFI_WRONG_PASSWORD: 'WIFI_WRONG_PASSWORD',
  WIFI_NO_AP_FOUND: 'WIFI_NO_AP_FOUND',
  WIFI_CONNECT_FAIL: 'WIFI_CONNECT_FAIL',
  WIFI_GOT_IP: 'WIFI_GOT_IP'
});

this.app.constant('wifiSecurityTypes', {
  OPEN: 'OPEN',
  WPA: 'WPA',
  WPA2: 'WPA2',
  UNKNOWN_WIFI: 'UNKNOWN_WIFI'
});

var extend = function(child, parent) { for (var key in parent) { if (hasProp.call(parent, key)) child[key] = parent[key]; } function ctor() { this.constructor = child; } ctor.prototype = parent.prototype; child.prototype = new ctor(); child.__super__ = parent.prototype; return child; },
  hasProp = {}.hasOwnProperty;

this.app.factory('BluetoothConnectionProvider', ["$window", "$timeout", "bluetooth", "debugService", "BluetoothConnection", "connectionProvidersConfig", "loggingTags", "disconnectReasons", function($window, $timeout, bluetooth, debugService, BluetoothConnection, connectionProvidersConfig, loggingTags, disconnectReasons) {
  var TAG;
  TAG = 'BluetoothConnectionProvider';

  /**
   *
   * @interface BluetoothConnectionProvider
   *
   * @description This class implements the ConnectionProvider interface.
   * It is responsible for providing methods to discover {Endpoint}s and create {Connection}s over bluetooth.
   * It manages the lifetime of BluetoothConnection objects.
   *
   */
  return (function(superClass) {
    extend(_Class, superClass);


    /**
     *
     * @method constructor
     * @memberof BluetoothConnectionProvider
     *
     */

    function _Class() {
      this.type = connectionProvidersConfig.bluetooth.type;
      this.scanDeferred = null;
      this.scanTimeout = null;
      this.connections = {};
    }


    /**
     *
     * @method discover
     * @memberof BluetoothConnectionProvider
     *
     * @description To discover compatible objects over this channel
     * The promise has a timeout, which rejects if no endpoint is discovered if timeout is reached, or resolves if at least one endpoint is found
     * Upon calling this function again the previous promise would be cancelled
     *
     * @returns {Promise} Notifies with an {Endpoint} every time one has been discovered
     *
     */

    _Class.prototype.discover = function() {
      var bluetoothScanTimeout, knownEndpoints;
      debugService.log('BluetoothConnectionProvider discover', [TAG, loggingTags.bluetooth]);
      this.stopScan();
      knownEndpoints = {};
      this.scanDeferred = $window.Q.defer();
      bluetoothScanTimeout = connectionProvidersConfig.bluetooth.scanTimeout;
      if (ionic.Platform.isAndroid()) {
        bluetoothScanTimeout = bluetoothScanTimeout * 3;
      }
      this.scanTimeout = $timeout((function(_this) {
        return function() {
          if (_this.scanDeferred.promise.isPending()) {
            bluetooth.stopScan();
            return _this.scanDeferred.resolve(knownEndpoints);
          }
        };
      })(this), bluetoothScanTimeout);
      this.scanDeferred.promise.then(angular.noop);
      bluetooth.startScan(connectionProvidersConfig.bluetooth.discovery.streamServiceUUID, (function(_this) {
        return function(device) {
          var bluetoothAddress, circulatorAddress, connectedFlag, decimalContainingVersionAndIsConnected, endpoint, id, isConnected, manufacturerData, manufacturerId, versionNumber;
          if (device) {
            endpoint = knownEndpoints[device.address];
            if (!endpoint) {
              manufacturerData = $window.ByteBuffer.fromBase64(device.advertisementData.kCBAdvDataManufacturerData);
              manufacturerId = $window.ByteBuffer.concat([manufacturerData.slice(1, 2), manufacturerData.slice(0, 1)]).toHex();
              decimalContainingVersionAndIsConnected = parseInt(manufacturerData.slice(2, 3).toHex(), 16);
              connectedFlag = Math.pow(2, 7);
              isConnected = Boolean(decimalContainingVersionAndIsConnected & connectedFlag);
              versionNumber = decimalContainingVersionAndIsConnected & parseInt('01111111', 2);
              circulatorAddress = manufacturerData.slice(3).toHex();
              id = circulatorAddress;
              if (manufacturerId === connectionProvidersConfig.bluetooth.discovery.manufacturerId) {
                bluetoothAddress = device.address;
                debugService.log('BluetoothConnectionProvider found circulator', [TAG, loggingTags.bluetooth], {
                  address: circulatorAddress,
                  advertisementData: device.advertisementData,
                  isConnected: isConnected,
                  versionNumber: versionNumber
                });
                endpoint = new $window.CirculatorSDK.Endpoint(_this.type, circulatorAddress, id, device.advertisementData.kCBAdvDataLocalName, bluetoothAddress, isConnected, versionNumber);
                knownEndpoints[device.address] = endpoint;
                _this.scanDeferred.notify(endpoint);
              }
            }
          }
        };
      })(this), (function(_this) {
        return function(error) {
          debugService.warn('BluetoothConnectionProvider scan error', [TAG, loggingTags.bluetooth], {
            error: error
          });
          _this.scanDeferred.reject(error);
          return _this.stopScan();
        };
      })(this));
      return this.scanDeferred.promise;
    };


    /**
     *
     * @method createConnection
     * @memberof BluetoothConnectionProvider
     *
     * @description Translates an {Endpoint} into {BluetoothConnection}
     *
     * @returns {BluetoothConnection}
     *
     */

    _Class.prototype.createConnectionFromEndpoint = function(endpoint, authCallback) {
      if (authCallback == null) {
        authCallback = null;
      }
      debugService.log('BluetoothConnectionProvider creating BluetoothConnection from endpoint', [TAG, loggingTags.bluetooth]);
      return this.createConnection(endpoint.bluetoothAddress, endpoint.address, authCallback);
    };


    /*
     *
     * @method createConnectionByAddress
     * @memberof BluetoothConnectionProvider
     *
     * @description Translates an {ConnectionData} into {BluetoothConnection}
     *
     * @param {ConnectionData} connectionData
     * @returns {BluetoothConnection}
     *
     */

    _Class.prototype.createConnectionFromData = function(connectionData, authCallback) {
      if (authCallback == null) {
        authCallback = null;
      }
      debugService.log('BluetoothConnectionProvider creating BluetoothConnection from connectionData', [TAG, loggingTags.bluetooth]);
      return this.createConnection(connectionData.bluetoothAddress, connectionData.address, authCallback);
    };


    /*
     *
     * @method createConnectionFromCandidate
     * @memberof BluetoothConnectionProvider
     *
     * @description Translates an {CirculatorCandidate} into {BluetoothConnection}
     *
     * @param {CirculatorCandidate} candidate
     * @returns {BluetoothConnection}
     *
     */

    _Class.prototype.createConnectionFromCandidate = function(candidate, authCallback) {
      if (authCallback == null) {
        authCallback = null;
      }
      debugService.log('BluetoothConnectionProvider creating BluetoothConnection from circulatorCandidate', [TAG, loggingTags.bluetooth]);
      return this.createConnection(candidate.bluetoothAddress, candidate.address, authCallback);
    };


    /*
     *
     * @method createConnection
     * @memberof BluetoothConnectionProvider
     *
     * @description Creates a {BluetoothConnection}
     *
     * @param {string} bluetoothAddress - connection bluetooth address
     * @param {string} circulatorAddress - The circulator address
     * @param {function} authCallback - auth callback
     * @returns {BluetoothConnection}
     *
     */

    _Class.prototype.createConnection = function(bluetoothAddress, circulatorAddress, authCallback) {
      var id;
      id = this.type + '-' + circulatorAddress;
      if (this.connections[id] == null) {
        debugService.log('BluetoothConnectionProvider creating new BluetoothConnection', TAG, {
          id: id,
          circulatorAddress: circulatorAddress
        });
        this.connections[id] = new BluetoothConnection(id, bluetoothAddress, circulatorAddress);
        this.connections[id].on('missingAddress', (function(_this) {
          return function(connection) {
            return _this.onMissingBluetoothAddress(connection);
          };
        })(this));
      }
      this.connections[id].authCallback = authCallback;
      return this.connections[id];
    };


    /*
     *
     * @method onMissingBluetoothAddress
     * @memberof BluetoothConnectionProvider
     * @private
     *
     * @description Handler for when a connection is missing its bluetooth address and requires a discover to find the bluetooth address with matching circulator address
     * Note: This can happen when we have obtained a circulator based on user's cloud storage earlier, but the circulator is missing bluetooth address as we couldn't find it nearby.
     *
     * @param {BluetoothConnection} connection - The bluetooth connection
     *
     */

    _Class.prototype.onMissingBluetoothAddress = function(connection) {
      var circulatorAddress;
      circulatorAddress = connection.circulatorAddress;
      debugService.warn("Missing bluetooth address for connection: " + connection.type + " and circulator address: " + circulatorAddress + ". Scanning for endpoint...", [TAG, loggingTags.bluetooth]);
      return this.discover().progress((function(_this) {
        return function(endpoint) {
          var matchingEndpoint;
          matchingEndpoint = null;
          if (endpoint.address === circulatorAddress) {
            matchingEndpoint = endpoint;
          }
          if (matchingEndpoint != null) {
            debugService.log("Missing bluetooth address.  Scan has found matching endpoint with circulator address: " + circulatorAddress, [TAG, loggingTags.bluetooth]);
            _this.stopScan();
            connection.bluetoothAddress = matchingEndpoint.bluetoothAddress;
            return connection.openAndAuthorize();
          }
        };
      })(this));
    };


    /**
     *
     * @method stopScan
     * @memberOf BluetoothConnectionProvider
     * @private
     *
     * @description Stop the scan and clean up everything
     *
     */

    _Class.prototype.stopScan = function() {
      var ref;
      debugService.log('BluetoothConnectionProvider stopScan', [TAG, loggingTags.bluetooth]);
      bluetooth.stopScan();
      if (this.scanTimeout != null) {
        $timeout.cancel(this.scanTimeout);
      }
      this.scanTimeout = null;
      return (ref = this.scanDeferred) != null ? ref.resolve({}) : void 0;
    };


    /*
     *
     * @method cleanUp
     * @memberof BluetoothConnectionProvider
     *
     * @description Clean up states
     *
     */

    _Class.prototype.cleanUp = function() {
      debugService.log('BluetoothConnectionProvider cleanUp', [TAG, loggingTags.bluetooth]);
      this.stopScan();
      _.forEach(this.connections, function(connection) {
        connection.close(disconnectReasons.cleanUp);
        connection.removeAllListeners('missingAddress');
        return connection = null;
      });
      return this.connections = {};
    };

    return _Class;

  })($window.CirculatorSDK.ConnectionProvider);
}]);

var extend = function(child, parent) { for (var key in parent) { if (hasProp.call(parent, key)) child[key] = parent[key]; } function ctor() { this.constructor = child; } ctor.prototype = parent.prototype; child.prototype = new ctor(); child.__super__ = parent.prototype; return child; },
  hasProp = {}.hasOwnProperty;

this.app.factory('BluetoothConnection', ["$window", "$timeout", "bluetooth", "debugService", "connectionState", "connectionProvidersConfig", "DeviceAddressNotFoundError", "DeviceNotFoundError", "DeviceAlreadyConnectedError", "BluetoothPowerError", "BluetoothConnectError", "DisconnectedWriteError", "GattFailure", "BluetoothOpenTimeoutError", "firmwareUpdateConfig", "loggingTags", "disconnectReasons", "$interval", "utilities", "BluetoothWriteError", function($window, $timeout, bluetooth, debugService, connectionState, connectionProvidersConfig, DeviceAddressNotFoundError, DeviceNotFoundError, DeviceAlreadyConnectedError, BluetoothPowerError, BluetoothConnectError, DisconnectedWriteError, GattFailure, BluetoothOpenTimeoutError, firmwareUpdateConfig, loggingTags, disconnectReasons, $interval, utilities, BluetoothWriteError) {
  var TAG;
  TAG = 'BluetoothConnection';

  /**
   *
   * @class BluetoothConnection
   *
   * @description This class implements the Connection interface.
   * It is responsible for managing the communication to and from a single bluetooth channel for a given circulator address.
   *
   * @fires 'close'
   * @fires 'open'
   * @fires 'authorize'
   * @fires 'connecting'
   * @fires 'message' with {Message} object when there's a message to read
   *
   */
  return (function(superClass) {
    extend(_Class, superClass);


    /**
     *
     * @method constructor
     * @memberof BluetoothConnection
     *
     * @param {string} id - The unique id of the connection
     * @param {string} bluetoothAddress - The device's bluetooth address
     * @param {string} circulatorAddress - The ChefSteps hex address used to identify a circulator
     *
     */

    function _Class(id, bluetoothAddress, circulatorAddress) {
      this.id = id;
      this.bluetoothAddress = bluetoothAddress;
      this.circulatorAddress = circulatorAddress;
      this.type = connectionProvidersConfig.bluetooth.type;
      this.reconnectBaseTime = connectionProvidersConfig.bluetooth.reconnectBaseTime;
      this.supportsSendingFile = true;
      this.preferredFileTransferBlockSize = 16;
      this.deviceHandle = null;
      this.services = {};
      this.characteristics = {};
      this.connectionState = connectionState.disconnected;
      this.disconnectReason = disconnectReasons.initialState;
      this.openDeferred = null;
      this.authorizeDeferred = null;
      this.openTimeout = null;
      this.authCallback = null;
      this.rssiInterval = null;
      this.rssiHistoryWhileConnected = [];
      this.rssiHistoryBeforeDisconnected = [];
      this.rssiIntervalMilliseconds = 2000;
      this.numberOfRssiValuesToCollect = utilities.convertMinutesToMilliseconds(1) / this.rssiIntervalMilliseconds;
    }


    /**
     *
     * @method open
     * @memberof BluetoothConnection
     *
     * @description Establishes a connection
     *
     * @param {number} timeoutInMilliseconds - The timeout to set in milliseconds, null / no timeout by default.
     *
     * @returns {Promise} Resolves when the connection has been established successfully
     *
     * @fires 'open'
     * @fires 'close'
     *
     */

    _Class.prototype.open = function(timeoutInMilliseconds) {
      var beginOpenTime;
      if (timeoutInMilliseconds == null) {
        timeoutInMilliseconds = connectionProvidersConfig.bluetooth.connectionTimeout;
      }
      if (this.connectionState === connectionState.connected || this.connectionState === connectionState.connectedAuthorized) {
        return $window.Q({});
      } else {
        if (this.openDeferred == null) {
          this.openDeferred = $window.Q.defer();
          if (this.bluetoothAddress == null) {
            $window.Q.nextTick((function(_this) {
              return function() {
                var ref;
                debugService.warn('BluetoothConnection missing bluetooth address', [TAG, loggingTags.bluetooth]);
                if ((ref = _this.openDeferred) != null) {
                  ref.reject(new BluetoothConnectError('Missing bluetooth address.  Unable to connect.'));
                }
                _this.openDeferred = null;
                _this.close(disconnectReasons.missingAddress);
                return _this.emit('missingAddress', _this);
              };
            })(this));
          } else {
            this.clearOpenTimeout();
            this.setOpenTimeout(timeoutInMilliseconds);
            beginOpenTime = new Date();
            debugService.log('BluetoothConnection connect started', [TAG, loggingTags.bluetooth], {
              bluetoothAddress: this.bluetoothAddress
            });
            this.connectionState = connectionState.connecting;
            this.emit('connecting');
            bluetooth.connect(this.bluetoothAddress, (function(_this) {
              return function(connectionInfo) {
                debugService.log('BluetoothConnection connect success', [TAG, loggingTags.bluetooth], {
                  connectionInfo: connectionInfo
                });
                _this.deviceHandle = connectionInfo.deviceHandle;
                _this.setRssiInterval();
                return _this.subscribeToNotification().then(function() {
                  var elapsedTime, endOpenTime, ref;
                  endOpenTime = new Date();
                  elapsedTime = endOpenTime - beginOpenTime;
                  debugService.log('BluetoothConnection subscribeToNotification success', [TAG, loggingTags.bluetooth], {
                    openTime: elapsedTime
                  });
                  _this.connectionState = connectionState.connected;
                  if ((ref = _this.openDeferred) != null) {
                    ref.resolve();
                  }
                  _this.openDeferred = null;
                  _this.clearOpenTimeout();
                  return _this.emit('open');
                }, function(error) {
                  var ref;
                  debugService.error('BluetoothConnection subscribeToNotification error', [TAG, loggingTags.bluetooth], {
                    error: error
                  });
                  if ((ref = _this.openDeferred) != null) {
                    ref.reject(error);
                  }
                  _this.openDeferred = null;
                  return _this.close(disconnectReasons.subscribeError);
                });
              };
            })(this), (function(_this) {
              return function() {
                var ref;
                debugService.warn('Device has terminated connection!', [TAG, loggingTags.bluetooth], {
                  connectionState: _this.connectionState
                });
                if (_this.connectionState !== connectionState.disconnected) {
                  if ((ref = _this.openDeferred) != null) {
                    ref.reject(new BluetoothConnectError('Device has terminated connection!'));
                  }
                  _this.openDeferred = null;
                  return _this.close(disconnectReasons.terminatedByOther);
                }
              };
            })(this), (function(_this) {
              return function(connectionInfo) {
                _this.connectionState = connectionState.connecting;
                debugService.log('BluetoothConnection connecting', [TAG, loggingTags.bluetooth], {
                  connectionInfo: connectionInfo
                });
                _this.deviceHandle = connectionInfo.deviceHandle;
                return _this.emit('connecting');
              };
            })(this), (function(_this) {
              return function(error) {
                var ref;
                debugService.warn('BluetoothConnection connect error', [TAG, loggingTags.bluetooth], {
                  error: error
                });
                if (_this.connectionState !== connectionState.disconnected) {
                  if ((ref = _this.openDeferred) != null) {
                    ref.reject(error);
                  }
                  _this.openDeferred = null;
                  if (error instanceof BluetoothPowerError) {
                    return _this.close(disconnectReasons.powerError);
                  } else if (error instanceof DeviceAddressNotFoundError) {
                    return _this.close(disconnectReasons.deviceAddressNotFound);
                  } else if (error instanceof DeviceNotFoundError) {
                    return _this.close(disconnectReasons.deviceNotFound);
                  } else if (error instanceof DeviceAlreadyConnectedError) {
                    return _this.close(disconnectReasons.deviceAlreadyConnected);
                  } else if (error instanceof GattFailure) {
                    return _this.close(disconnectReasons.gattFailure);
                  } else {
                    return _this.close(disconnectReasons.connectError);
                  }
                }
              };
            })(this));
          }
        }
        return this.openDeferred.promise;
      }
    };


    /*
     *
     * @method openAndAuthorize
     * @memberof BluetoothConnection
     *
     * @description Establishes a connection with authorization
     *
     * @param {number} timeoutInMilliseconds - The timeout to set in milliseconds, null / no timeout by default.
     *
     * @returns {Promise} Resolves when the connection has been authorized successfully
     *
     * @fires 'authorize'
     *
     */

    _Class.prototype.openAndAuthorize = function(timeoutInMilliseconds) {
      if (timeoutInMilliseconds == null) {
        timeoutInMilliseconds = connectionProvidersConfig.bluetooth.connectionTimeout;
      }
      if (this.connectionState === connectionState.connectedAuthorized) {
        return $window.Q({});
      } else {
        if (this.authorizeDeferred == null) {
          this.authorizeDeferred = $window.Q.defer();
          this.open(timeoutInMilliseconds).then((function(_this) {
            return function() {
              debugService.log('BluetoothConnection authorize started', [TAG, loggingTags.bluetooth]);
              return _this.authCallback().then(function() {
                var ref;
                debugService.log('BluetoothConnection authorize success', [TAG, loggingTags.bluetooth]);
                _this.connectionState = connectionState.connectedAuthorized;
                if ((ref = _this.authorizeDeferred) != null) {
                  ref.resolve();
                }
                _this.authorizeDeferred = null;
                return _this.emit('authorize');
              })["catch"](function(error) {
                var ref;
                debugService.warn('BluetoothConnection openAndAuthorize error', [TAG, loggingTags.bluetooth], {
                  error: error
                });
                if ((ref = _this.authorizeDeferred) != null) {
                  ref.reject(error);
                }
                _this.authorizeDeferred = null;
                if (_this.connectionState !== connectionState.disconnected) {
                  return _this.close(disconnectReasons.authorizeError);
                }
              }).done(_.noop, function(e) {
                return debugService.onPromiseUnhandledRejection(e, TAG);
              });
            };
          })(this))["catch"]((function(_this) {
            return function(error) {
              var ref;
              if ((ref = _this.authorizeDeferred) != null) {
                ref.reject(error);
              }
              return _this.authorizeDeferred = null;
            };
          })(this)).done(_.noop, function(e) {
            return debugService.onPromiseUnhandledRejection(e, TAG);
          });
        }
        return this.authorizeDeferred.promise;
      }
    };


    /**
     *
     * @method close
     * @memberof BluetoothConnection
     *
     * @description Perform clean up and close the connection
     *
     * @param {disconnectReasons enum} disconnectReason - The reason for closing
     *
     * @fires 'close'
     *
     */

    _Class.prototype.close = function(disconnectReason) {
      debugService.log('BluetoothConnection close', [TAG, loggingTags.bluetooth], {
        disconnectReason: disconnectReason
      });
      this.connectionState = connectionState.disconnected;
      this.disconnectReason = disconnectReason;
      this.clearRssiInterval(disconnectReason);
      this.clearOpenTimeout();
      if (this.openDeferred != null) {
        this.openDeferred.reject(new BluetoothConnectError('Closed while trying to open connection'));
        this.openDeferred = null;
      }
      if (this.authorizeDeferred != null) {
        this.authorizeDeferred.reject(new BluetoothConnectError('Closed while trying to authorize connection'));
        this.authorizeDeferred = null;
      }
      if (this.deviceHandle !== null) {
        bluetooth.disconnect(this.deviceHandle);
      }
      return this.emit('close', this.disconnectReason);
    };


    /**
     *
     * @method writeFile
     * @memberof BluetoothConnection
     *
     * @description Sends a file, only called if this connection @supportsSendingFile
     *
     * @param {hex string} address - recipient address
     * @param {ByteBuffer} file - The file to send
     * @param {number} blockSize - amount of data (number of bytes) to send in each block
     *
     * @returns {Promise} Resolves when the send is successful,
     *   notifies of progress,
     *   rejects with error otherwise
     *
     */

    _Class.prototype.writeFile = function(address, file, blockSize) {
      var blocks, messageQueue, writeTo;
      writeTo = this.characteristics[connectionProvidersConfig.bluetooth.communication.fileCharacteristicUUID];
      messageQueue = $window.CirculatorSDK.FileTransferUtils.blocksFromData(file, blockSize, true);
      blocks = _.chunk(messageQueue, firmwareUpdateConfig.packetsPerConnectionInterval);
      return $window.CirculatorSDK.FileTransferUtils.processMessages(this.sendBlock.bind(this), blocks, writeTo);
    };


    /**
     *
     * @method write
     * @memberof BluetoothConnection
     *
     * @description Sends a message
     *
     * @param {Message} message - The message to send
     *
     * @returns {Promise} Resolves when the write is successful, rejects with error otherwise
     *
     */

    _Class.prototype.write = function(message) {
      var deferred, error, failureHandler, writeTo;
      deferred = $window.Q.defer();
      if (this.connectionState === connectionState.connected || this.connectionState === connectionState.connectedAuthorized) {
        writeTo = this.characteristics[connectionProvidersConfig.bluetooth.communication.writeCharacteristicUUID];
        failureHandler = (function(_this) {
          return function(error, numberOfAttempts) {
            if (numberOfAttempts == null) {
              numberOfAttempts = 0;
            }
            numberOfAttempts++;
            debugService.warn('BluetoothConnection write failure', [TAG, loggingTags.bluetooth], {
              error: error,
              numberOfAttempts: numberOfAttempts
            });
            if (error instanceof BluetoothWriteError && error.message === 134) {
              debugService.log('Treating error 134 as a write success', [TAG, loggingTags.bluetooth]);
              deferred.resolve();
              return;
            }
            if (numberOfAttempts > 2) {
              deferred.reject(error);
              debugService.warn('Bluetooth closing connection after write failure', [TAG, loggingTags.bluetooth]);
              return _this.close(disconnectReasons.writeError);
            } else {
              debugService.log('Retry write operation', [TAG, loggingTags.bluetooth]);
              return bluetooth.writeCharacteristic(_this.deviceHandle, writeTo, message, deferred.resolve, function(error) {
                return failureHandler(error, numberOfAttempts);
              });
            }
          };
        })(this);
        bluetooth.writeCharacteristic(this.deviceHandle, writeTo, message, deferred.resolve, failureHandler);
      } else {
        error = "Attempt to write without being connected! connectionState " + this.connectionState;
        debugService.warn(error, [TAG, loggingTags.bluetooth]);
        deferred.reject(new DisconnectedWriteError(error));
      }
      return deferred.promise;
    };


    /**
     *
     * @method subscribeToNotification
     * @memberof BluetoothConnection
     * @private
     *
     * @description Helper function to subscribe to reading characterstics
     *
     * @returns {Promise} Resolves when subscribe has been successful, rejects with error otherwise
     *
     */

    _Class.prototype.subscribeToNotification = function() {
      var deferred;
      deferred = $window.Q.defer();
      bluetooth.readServices(this.deviceHandle, (function(_this) {
        return function(services) {
          var i, len, service, streamService;
          for (i = 0, len = services.length; i < len; i++) {
            service = services[i];
            _this.services[service.uuid.toLowerCase()] = service;
            _this.services[service.uuid.toUpperCase()] = service;
          }
          streamService = _this.services[connectionProvidersConfig.bluetooth.discovery.streamServiceUUID];
          return bluetooth.readCharacteristics(_this.deviceHandle, streamService, function(characteristics) {
            var characteristic, j, len1, subscribeTo;
            for (j = 0, len1 = characteristics.length; j < len1; j++) {
              characteristic = characteristics[j];
              _this.characteristics[characteristic.uuid.toLowerCase()] = characteristic;
              _this.characteristics[characteristic.uuid.toLowerCase()].permissions = _this.convertFlagsToEnglish(characteristic.permissions, bluetooth.permissions);
              _this.characteristics[characteristic.uuid.toLowerCase()].properties = _this.convertFlagsToEnglish(characteristic.properties, bluetooth.properties);
              _this.characteristics[characteristic.uuid.toLowerCase()].writeType = _this.convertFlagsToEnglish(characteristic.writeType, bluetooth.writeTypes);
              _this.characteristics[characteristic.uuid.toUpperCase()] = characteristic;
              _this.characteristics[characteristic.uuid.toUpperCase()].permissions = _this.convertFlagsToEnglish(characteristic.permissions, bluetooth.permissions);
              _this.characteristics[characteristic.uuid.toUpperCase()].properties = _this.convertFlagsToEnglish(characteristic.properties, bluetooth.properties);
              _this.characteristics[characteristic.uuid.toUpperCase()].writeType = _this.convertFlagsToEnglish(characteristic.writeType, bluetooth.writeTypes);
            }
            subscribeTo = _this.characteristics[connectionProvidersConfig.bluetooth.communication.subscribeCharacteristicUUID];
            _this.subscribe(subscribeTo);
            return deferred.resolve();
          }, function(error) {
            return deferred.reject(error);
          });
        };
      })(this), function(error) {
        return deferred.reject(error);
      });
      return deferred.promise;
    };


    /**
     *
     * @method convertFlagsToEnglish
     * @memberof BluetoothConnection
     * @private
     *
     * @description Converts a bitmask into a group of flags that describes a characteristics
     *
     * @param {number} bitmask - An integer containing the masked flags
     * @param {array} maskOptions - All the possible options for the bitmask to do the comparision
     *
     * @returns {array} An array of the flags from the bitmask
     *
     */

    _Class.prototype.convertFlagsToEnglish = function(bitmask, maskOptions) {
      var flag, maskName, setFlags;
      setFlags = [];
      for (flag in maskOptions) {
        maskName = maskOptions[flag];
        if ((bitmask & flag) > 0) {
          setFlags.push(maskName);
        }
      }
      return setFlags;
    };


    /**
     *
     * @method subscribe
     * @memberof BluetoothConnection
     * @private
     *
     * @description Subscribe to the characteristic to be notified of events
     *
     * @param {object} subscribeTo - An object that contains characterstic handle to subscribe to
     *
     */

    _Class.prototype.subscribe = function(subscribeTo) {
      return bluetooth.subscribe(this.deviceHandle, subscribeTo, (function(_this) {
        return function() {
          var readChar;
          _this.characteristics[subscribeTo.uuid].subscribed = true;
          readChar = _this.characteristics[connectionProvidersConfig.bluetooth.communication.readCharacteristicUUID];
          return _this.read(readChar);
        };
      })(this));
    };


    /**
     *
     * @method unsubscribe
     * @memberof BluetoothConnection
     * @private
     *
     * @description Unsubscribe from the characteristic
     *
     */

    _Class.prototype.unsubscribe = function(unsubscribeTo) {
      var deferred;
      deferred = $window.Q.defer();
      this.characteristics[unsubscribeTo.uuid].subscribed = false;
      bluetooth.unsubscribe(this.deviceHandle, unsubscribeTo, deferred.resolve, deferred.reject);
      return deferred.promise;
    };


    /**
     *
     * @method read
     * @memberof BluetoothConnection
     * @private
     *
     * @description Read from the characteristic
     *
     * @param {Object} readFrom - An object that contains the characteristic handle to read from
     *
     */

    _Class.prototype.read = function(readFrom) {
      return bluetooth.readCharacteristic(this.deviceHandle, readFrom, (function(_this) {
        return function(message) {
          return _this.emit('message', message);
        };
      })(this), (function(_this) {
        return function(error) {
          debugService.error('BluetoothConnection read failure', [TAG, loggingTags.bluetooth], {
            error: error
          });
          return _this.close(disconnectReasons.readError);
        };
      })(this));
    };


    /**
     *
     * @method setOpenTimeout
     * @memberof BluetoothConnection
     * @private
     *
     * @description Private helper function to set an a timeout on the open operation
     * @param {number} timeoutInMilliseconds - The timeout to set in milliseconds
     *
     */

    _Class.prototype.setOpenTimeout = function(timeoutInMilliseconds) {
      return this.openTimeout = $timeout((function(_this) {
        return function() {
          var ref, ref1, ref2;
          if (((ref = _this.openDeferred) != null ? (ref1 = ref.promise) != null ? ref1.isPending() : void 0 : void 0) && _this.connectionState === connectionState.connecting) {
            debugService.warn('BluetoothConnection Open Timeout', [TAG, loggingTags.bluetooth]);
            if ((ref2 = _this.openDeferred) != null) {
              ref2.reject(new BluetoothOpenTimeoutError('Open Timeout'));
            }
            _this.openDeferred = null;
            return _this.close(disconnectReasons.openTimeout);
          }
        };
      })(this), timeoutInMilliseconds);
    };


    /**
     *
     * @method clearOpenTimeout
     * @memberof BluetoothConnection
     * @private
     *
     * @description Private helper function to clear a timeout on the open operation
     *
     */

    _Class.prototype.clearOpenTimeout = function() {
      if (this.openTimeout != null) {
        $timeout.cancel(this.openTimeout);
      }
      return this.openTimeout = null;
    };


    /**
     *
     * @method sendBlock
     * @memberof BluetoothConnection
     * @private
     *
     * @param {int} index - index of block being sent
     * @param [ByteBuffer] block - Array of ByteBuffers to send over the wire
     * @param {string} characteristic - characteristic UUID to send the data on
     *
     * @description Sends a block (defined as an array of packets) to writeCharacteristic
     *
     */

    _Class.prototype.sendBlock = function(index, block, characteristic) {
      var connectionIntervalDelay, perPacketDelay;
      connectionIntervalDelay = firmwareUpdateConfig.connectionIntervalDelayAndroid;
      if (ionic.Platform.isIOS()) {
        connectionIntervalDelay = firmwareUpdateConfig.connectionIntervalDelayIos;
      }
      perPacketDelay = 0;
      if (ionic.Platform.isIOS()) {
        perPacketDelay = firmwareUpdateConfig.perPacketDelayIos;
      }
      return block.reduce((function(_this) {
        return function(promise, packet) {
          return promise.then(function() {
            var deferred, successFn;
            deferred = $window.Q.defer();
            successFn = function() {
              return $timeout((function() {
                return deferred.resolve();
              }), perPacketDelay);
            };
            bluetooth.writeCharacteristic(_this.deviceHandle, characteristic, packet.buffer, successFn, deferred.reject, 'raw');
            return deferred.promise;
          });
        };
      })(this), $window.Q.delay(connectionIntervalDelay));
    };


    /**
     *
     * @method rssi
     * @memberof BluetoothConnection
     * @private
     *
     * @description Fetch the remove device's RSSI
     *
     */

    _Class.prototype.rssi = function() {
      return bluetooth.rssi(this.deviceHandle);
    };


    /**
     *
     * @method setRssiInterval
     * @memberof BluetoothConnection
     * @private
     *
     * @description Start a interval to fetch the device's RSSI
     *
     */

    _Class.prototype.setRssiInterval = function() {
      this.rssi(this.deviceHandle).then((function(_this) {
        return function(rssi) {
          debugService.log('Initial RSSI', [TAG, loggingTags.bluetooth], {
            initialRssi: rssi
          });
          _this.rssiHistoryWhileConnected.push(rssi);
          return _this.rssiHistoryBeforeDisconnected.push(rssi);
        };
      })(this));
      return this.rssiInterval = $interval((function(_this) {
        return function() {
          return _this.rssi(_this.deviceHandle).then(function(rssi) {
            if (_this.rssiHistoryWhileConnected.length > _this.numberOfRssiValuesToCollect) {
              debugService.log('RSSI history while connected', [TAG, loggingTags.bluetooth], {
                rssiHistory: _this.rssiHistoryWhileConnected
              });
              _this.rssiHistoryWhileConnected = [];
            }
            _this.rssiHistoryWhileConnected.push(rssi);
            if (_this.rssiHistoryBeforeDisconnected.length > _this.numberOfRssiValuesToCollect) {
              _this.rssiHistoryBeforeDisconnected.shift();
            }
            return _this.rssiHistoryBeforeDisconnected.push(rssi);
          })["catch"](function(error) {
            return debugService.log('RSSI failure', [TAG, loggingTags.bluetooth], {
              error: error
            });
          });
        };
      })(this), this.rssiIntervalMilliseconds);
    };


    /**
     *
     * @method clearRssiInterval
     * @memberof BluetoothConnection
     * @private
     *
     * @description Clear the interval to fetch the device's RSSI
     *
     * @param {disconnectReasons enum} disconnectReason - The reason for disconnection
     *
     */

    _Class.prototype.clearRssiInterval = function(disconnectReason) {
      if (this.rssiInterval != null) {
        $interval.cancel(this.rssiInterval);
        this.rssiInterval = null;
        debugService.log('RSSI history leading to disconnect', [TAG, loggingTags.bluetooth], {
          disconnectReason: disconnectReason,
          rssiHistory: this.rssiHistoryBeforeDisconnected
        });
        this.rssiHistoryBeforeDisconnected = [];
        return this.rssiHistoryWhileConnected = [];
      }
    };


    /*
     *
     * @method powerStatePromise
     * @memberof BluetoothConnection
     * @private
     *
     * @description Retreive bluetooth power status in the form of a promise
     *
     */

    _Class.prototype.powerState = function() {
      return bluetooth.powerStatePromise();
    };

    return _Class;

  })($window.CirculatorSDK.Connection);
}]);

this.app.factory('CirculatorProgram', function() {

  /**
   *
   * @class CirculatorProgram
   *
   * @classdesc This class describes the program that is running
   *
   */
  return (function() {

    /**
     *
     * @method constructor
     * @memberof CirculatorProgram
     *
     * @description
     * - Stores information about a circulator program that will control the server
     * - This uses named parameters so that it can be filled in
     * new CirculatorProgram(setPoint: 1.23, holdingTemperature: 65, turbo: true)
     *
     * @param {float} setPoint - The temperature where the cooking will be done
     * @param {int} cookTime - Time (in seconds) to perform the cook
     * @param {int} delayedStart - Time (in seconds) to wait before heating the water bath.
     * @param {float} holdingTemperature - The temperature to drop the bath down to after the cookTime
     * @param {boolean} waitForPreheat - Whether the food is dropped in with the cold water or need to wait for the water to be preheated
     * @param {boolean} turbo - Whether we can overshoot the setPoint to heat the water and food up faster
     * @param {boolean} predictive - Whether it should use predictive cooking to figure out the cookTime
     * @param {programTypes} programType - What kind of program you are going to run Automatic or Manual
     * @param {string} guide - The corresponding guide identifier for the program
     *
     */
    function _Class(arg) {
      var cookTime, delayedStart, guide, holdingTemperature, id, predictive, programMetadata, programType, ref, setPoint, turbo, waitForPreheat;
      ref = arg != null ? arg : {}, setPoint = ref.setPoint, cookTime = ref.cookTime, delayedStart = ref.delayedStart, holdingTemperature = ref.holdingTemperature, waitForPreheat = ref.waitForPreheat, turbo = ref.turbo, predictive = ref.predictive, programType = ref.programType, guide = ref.guide, id = ref.id, programMetadata = ref.programMetadata;

      /**
       * @member {float} setPoint
       * @memberof CirculatorProgram
       */
      this.setPoint = setPoint;

      /**
       * @member {int} cookTime
       * @memberof CirculatorProgram
       */
      this.cookTime = cookTime;

      /**
       * @member {int} delayedStart
       * @memberof CirculatorProgram
       */
      this.delayedStart = delayedStart;

      /**
       * @member {float} holdingTemperature
       * @memberof CirculatorProgram
       */
      this.holdingTemperature = holdingTemperature;

      /**
       * @member {boolean} waitForPreheat
       * @memberof CirculatorProgram
       */
      this.waitForPreheat = waitForPreheat;

      /**
       * @member {boolean} turbo
       * @memberof CirculatorProgram
       */
      this.turbo = turbo;

      /**
       * @member {boolean} predictive
       * @memberof CirculatorProgram
       */
      this.predictive = predictive;

      /**
       * @member {programTypes} programType
       * @memberof CirculatorProgram
       */
      this.programType = programType;

      /**
       * @member {string} guide
       * @memberof CirculatorProgram
       */
      this.guide = guide;

      /**
       * @member {string} id
       * @memberof CirculatorProgram
       */
      this.id = id;

      /**
       * @member {ProgramMetadata} programMetadata
       * @memberof CirculatorProgram
       */
      this.programMetadata = programMetadata;
    }

    return _Class;

  })();
});

var extend = function(child, parent) { for (var key in parent) { if (hasProp.call(parent, key)) child[key] = parent[key]; } function ctor() { this.constructor = child; } ctor.prototype = parent.prototype; child.prototype = new ctor(); child.__super__ = parent.prototype; return child; },
  hasProp = {}.hasOwnProperty;

this.app.factory('BluetoothPowerError', ["$window", function($window) {
  return (function(superClass) {
    extend(_Class, superClass);

    function _Class(message, bluetoothPowerState) {
      this.message = message;
      this.bluetoothPowerState = bluetoothPowerState;
    }

    return _Class;

  })($window.CirculatorSDK.BaseError);
}]);

this.app.factory('BluetoothScanError', ["$window", function($window) {
  return (function(superClass) {
    extend(_Class, superClass);

    function _Class() {
      return _Class.__super__.constructor.apply(this, arguments);
    }

    return _Class;

  })($window.CirculatorSDK.BaseError);
}]);

this.app.factory('BluetoothWriteError', ["$window", function($window) {
  return (function(superClass) {
    extend(_Class, superClass);

    function _Class() {
      return _Class.__super__.constructor.apply(this, arguments);
    }

    return _Class;

  })($window.CirculatorSDK.BaseError);
}]);

this.app.factory('BluetoothReadServiceError', ["$window", function($window) {
  return (function(superClass) {
    extend(_Class, superClass);

    function _Class() {
      return _Class.__super__.constructor.apply(this, arguments);
    }

    return _Class;

  })($window.CirculatorSDK.BaseError);
}]);

this.app.factory('BluetoothReadCharacteristicsError', ["$window", function($window) {
  return (function(superClass) {
    extend(_Class, superClass);

    function _Class() {
      return _Class.__super__.constructor.apply(this, arguments);
    }

    return _Class;

  })($window.CirculatorSDK.BaseError);
}]);

this.app.factory('BluetoothReadError', ["$window", function($window) {
  return (function(superClass) {
    extend(_Class, superClass);

    function _Class() {
      return _Class.__super__.constructor.apply(this, arguments);
    }

    return _Class;

  })($window.CirculatorSDK.BaseError);
}]);

this.app.factory('BluetoothSubscribeError', ["$window", function($window) {
  return (function(superClass) {
    extend(_Class, superClass);

    function _Class() {
      return _Class.__super__.constructor.apply(this, arguments);
    }

    return _Class;

  })($window.CirculatorSDK.BaseError);
}]);

this.app.factory('BluetoothUnsubscribeError', ["$window", function($window) {
  return (function(superClass) {
    extend(_Class, superClass);

    function _Class() {
      return _Class.__super__.constructor.apply(this, arguments);
    }

    return _Class;

  })($window.CirculatorSDK.BaseError);
}]);

this.app.factory('BluetoothConnectError', ["$window", function($window) {
  return (function(superClass) {
    extend(_Class, superClass);

    function _Class() {
      return _Class.__super__.constructor.apply(this, arguments);
    }

    return _Class;

  })($window.CirculatorSDK.BaseError);
}]);

this.app.factory('BluetoothOpenTimeoutError', ["$window", function($window) {
  return (function(superClass) {
    extend(_Class, superClass);

    function _Class() {
      return _Class.__super__.constructor.apply(this, arguments);
    }

    return _Class;

  })($window.CirculatorSDK.TimeoutError);
}]);

this.app.factory('DeviceAddressNotFoundError', ["$window", function($window) {
  return (function(superClass) {
    extend(_Class, superClass);

    function _Class() {
      return _Class.__super__.constructor.apply(this, arguments);
    }

    return _Class;

  })($window.CirculatorSDK.BaseError);
}]);

this.app.factory('DeviceNotFoundError', ["$window", function($window) {
  return (function(superClass) {
    extend(_Class, superClass);

    function _Class() {
      return _Class.__super__.constructor.apply(this, arguments);
    }

    return _Class;

  })($window.CirculatorSDK.BaseError);
}]);

this.app.factory('DeviceAlreadyConnectedError', ["$window", function($window) {
  return (function(superClass) {
    extend(_Class, superClass);

    function _Class() {
      return _Class.__super__.constructor.apply(this, arguments);
    }

    return _Class;

  })($window.CirculatorSDK.BaseError);
}]);

this.app.factory('GattFailure', ["$window", function($window) {
  return (function(superClass) {
    extend(_Class, superClass);

    function _Class() {
      return _Class.__super__.constructor.apply(this, arguments);
    }

    return _Class;

  })($window.CirculatorSDK.BaseError);
}]);

this.app.factory('WebSocketConnectError', ["$window", function($window) {
  return (function(superClass) {
    extend(_Class, superClass);

    function _Class() {
      return _Class.__super__.constructor.apply(this, arguments);
    }

    return _Class;

  })($window.CirculatorSDK.BaseError);
}]);

this.app.factory('WebSocketWriteError', ["$window", function($window) {
  return (function(superClass) {
    extend(_Class, superClass);

    function _Class() {
      return _Class.__super__.constructor.apply(this, arguments);
    }

    return _Class;

  })($window.CirculatorSDK.BaseError);
}]);

this.app.factory('WebSocketOpenTimeoutError', ["$window", function($window) {
  return (function(superClass) {
    extend(_Class, superClass);

    function _Class() {
      return _Class.__super__.constructor.apply(this, arguments);
    }

    return _Class;

  })($window.CirculatorSDK.TimeoutError);
}]);

this.app.factory('DisconnectedWriteError', ["$window", function($window) {
  return (function(superClass) {
    extend(_Class, superClass);

    function _Class() {
      return _Class.__super__.constructor.apply(this, arguments);
    }

    return _Class;

  })($window.CirculatorSDK.BaseError);
}]);

this.app.factory('NoCirculatorError', ["$window", function($window) {
  return (function(superClass) {
    extend(_Class, superClass);

    function _Class() {
      return _Class.__super__.constructor.apply(this, arguments);
    }

    return _Class;

  })($window.CirculatorSDK.BaseError);
}]);

this.app.factory('NoInternetError', ["$window", function($window) {
  return (function(superClass) {
    extend(_Class, superClass);

    function _Class() {
      return _Class.__super__.constructor.apply(this, arguments);
    }

    return _Class;

  })($window.CirculatorSDK.BaseError);
}]);

this.app.factory('NonOwnerError', ["$window", function($window) {
  return (function(superClass) {
    extend(_Class, superClass);

    function _Class() {
      return _Class.__super__.constructor.apply(this, arguments);
    }

    return _Class;

  })($window.CirculatorSDK.BaseError);
}]);

this.app.factory('NullOwnerError', ["$window", function($window) {
  return (function(superClass) {
    extend(_Class, superClass);

    function _Class() {
      return _Class.__super__.constructor.apply(this, arguments);
    }

    return _Class;

  })($window.CirculatorSDK.BaseError);
}]);

this.app.factory('FileTransferBlockTimeoutError', ["$window", function($window) {
  return (function(superClass) {
    extend(_Class, superClass);

    function _Class() {
      return _Class.__super__.constructor.apply(this, arguments);
    }

    return _Class;

  })($window.CirculatorSDK.TimeoutError);
}]);

this.app.factory('SafeAbortError', ["$window", function($window) {
  return (function(superClass) {
    extend(_Class, superClass);

    function _Class() {
      return _Class.__super__.constructor.apply(this, arguments);
    }

    return _Class;

  })($window.CirculatorSDK.BaseError);
}]);

var bind = function(fn, me){ return function(){ return fn.apply(me, arguments); }; };

this.app.factory('guideFactory', ["$q", "$rootScope", "$ionicPlatform", "$window", "assetService", "cacheService", "locale", "debugService", function($q, $rootScope, $ionicPlatform, $window, assetService, cacheService, locale, debugService) {
  var Guide, TAG, guideCache;
  TAG = 'GuideFactory';
  guideCache = {};
  Guide = (function() {

    /*
     *
     * @constructor
     *
     */
    var requiredAssetPromiseChain;

    requiredAssetPromiseChain = $q.resolve();

    function Guide(guide) {
      this.refresh = bind(this.refresh, this);
      var deferred;
      if (!guide) {
        throw new Error('Guide: Must pass guide object to initialize!');
      }
      deferred = $q.defer();
      this.ready = deferred.promise;
      this.guide = guide;
      this.status = {
        isLatest: true,
        isDownloaded: false,
        isSyncing: false,
        percentageSynced: 0
      };
      _.extend(this, this.guide);
      $rootScope.$on('guides.updated', (function(_this) {
        return function() {
          return _this.refresh();
        };
      })(this));
      $rootScope.$on("guide." + this.guide.id + ".updated", (function(_this) {
        return function() {
          return _this.refresh();
        };
      })(this));
      $rootScope.$on("guide." + this.guide.id + ".syncing", (function(_this) {
        return function() {
          return _this.refresh();
        };
      })(this));
      assetService.get(this.guide.thumbnail);
      this.imageAssets = _.filter(this.guide.assets, function(x) {
        return /\.jpg$/.test(x);
      });
      assetService.existsAll(this.imageAssets).then((function(_this) {
        return function() {
          _this.status.isDownloaded = true;
          _this.status.percentageSynced = 1;
          return debugService.debug('Guide images were all already available locally', TAG, {
            title: _this.guide.title
          });
        };
      })(this))["catch"]((function(_this) {
        return function() {
          return requiredAssetPromiseChain = requiredAssetPromiseChain.then(_.bind(_this.getRequiredAssets, _this));
        };
      })(this));
      guideCache[this.guide.slug] = this;
      deferred.resolve();
    }

    Guide.prototype.getRequiredAssets = function() {
      debugService.debug('Started getting guide images', TAG, {
        title: this.guide.title
      });
      return assetService.getAll(this.imageAssets).then(((function(_this) {
        return function() {
          _this.status.isDownloaded = true;
          return debugService.log('Guide images download succeeded', TAG, {
            title: _this.guide.title
          });
        };
      })(this)), ((function(_this) {
        return function(error) {
          return debugService.error('Guide images download failed for ' + _this.guide.title, TAG, {
            error: error,
            title: _this.guide.title
          });
        };
      })(this)), ((function(_this) {
        return function(completed) {
          return _this.status.percentageSynced = completed / _this.imageAssets.length;
        };
      })(this)));
    };

    Guide.prototype.getDownloadProgressPercent = function() {
      return Math.floor(this.status.percentageSynced * 100.0);
    };


    /*
     *
     * @method refresh
     *
     * @description
     *
     */

    Guide.prototype.refresh = function() {
      var updatedGuide;
      updatedGuide = cacheService.get(this.guide.id + ".update", 'guide');
      if (updatedGuide) {
        return this.status.isLatest = updatedGuide.version <= this.guide.version;
      }
    };

    return Guide;

  })();
  return function(guide) {
    return guideCache[guide.slug] || new Guide(guide);
  };
}]);

this.app.factory('timerFactory', ["$state", "$interval", "$window", "alertService", "locale", "cacheService", "debugService", "localNotificationService", function($state, $interval, $window, alertService, locale, cacheService, debugService, localNotificationService) {
  var TAG, Timer;
  TAG = 'TimerFactory';

  /**
   *
   * @class Timer
   *
   * @description An instance of the timer class does a couple things a $timeout just can't do - fire notifications upon timer expiry, survive background-ing by Cordova.
   *
   */
  Timer = (function() {

    /**
     *
     * @constructor
     *
     * @param options {object}
     * @param options.duration {number} - Time in milliseconds.
     *
     */
    function Timer(duration, options) {
      if (options == null) {
        options = {};
      }
      if (!duration) {
        throw new Error('Timer: Must pass duration to constuctor!');
      }
      this._duration = Math.min(duration, 356400000 + 3540000);
      if (options.startTime) {
        this._startTime = options.startTime;
      }
      this._id = options.id || Math.round(this.getStartTime() * Math.random());
      if (options.push) {
        this._push = options.push;
      }
      if (options.autostart) {
        this.start();
      }
      if (options.paused) {
        this.pause();
      }
      this._cache();
      debugService.log('Constructing new timer', TAG, {
        timerOptions: options,
        timerId: this._id
      });
    }


    /**
     *
     * @property duration
     * @private
     *
     * @description Duration of the timer, in milliseconds.
     *
     */

    Timer.prototype._id = null;


    /**
     *
     * @property duration
     * @private
     *
     * @description Duration of the timer, in milliseconds.
     *
     */

    Timer.prototype._duration = 0;


    /**
     *
     * @property startTime
     * @private
     *
     * @description The time, as a Unix timestamp, when the timer was started.
     *
     */

    Timer.prototype._startTime = null;


    /**
     *
     * @property interval
     *
     * @description Set to a interval ID, which we use within the `cancel` method.
     *
     */

    Timer.prototype._interval = null;


    /**
     *
     * @property paused
     * @private
     *
     * @description Whether the timer has been paused. Once a timer has been
     * started in cannot be "unstarted" - only paused.
     *
     */

    Timer.prototype._paused = false;


    /**
     *
     * @property started
     * @private
     *
     * @description Whether the timer has begun counting down.
     *
     */

    Timer.prototype._started = false;


    /**
     *
     * @property push
     * @private
     *
     * @description Cached push notification object.
     *
     */

    Timer.prototype._push = null;


    /**
     *
     * @method getId
     *
     * @description Returns the ID of the current timer.
     *
     */

    Timer.prototype.getId = function() {
      return this._id;
    };


    /**
     *
     * @method getStartTime
     *
     * @description Time, as a Unix timestamp, when the timer was started. We use
     * Underscore's `once` method to capture the time upon first call, which is
     * within the constructor.
     *
     */

    Timer.prototype.getStartTime = function() {
      if (this._startTime) {
        return this._startTime;
      } else {
        return Date.now();
      }
    };


    /**
     *
     * @method getDisplayHours
     *
     * @description Returns the number of hours as would be displayed to the
     * the user. Given that hours are the largest time units we display, this
     * method is identical to the getRemainingHours method.
     *
     */

    Timer.prototype.getDisplayHours = function() {
      var hoursString;
      hoursString = this.getRemainingHours().toString();
      if (hoursString.length === 1) {
        hoursString = '0' + hoursString;
      }
      return hoursString;
    };


    /**
     *
     * @method getRemainingHours
     *
     * @description Time at which the timer has progressed to, in milliseconds.
     *
     */

    Timer.prototype.getRemainingHours = function() {
      return Math.floor(this.getRemainingMinutes() / 60);
    };


    /**
     *
     * @method getDisplayMinutes
     *
     * @description Returns the time in minutes remaining, modulus hours. Whereas
     * getRemainingMinutes() returns the total number of minutes remaining, and
     * can thus return any positive integer, getDisplayMinutes() will return values
     * from zero to fifty-nine inclusive.
     *
     */

    Timer.prototype.getDisplayMinutes = function() {
      var minutesString;
      minutesString = (this.getRemainingMinutes() % 60).toString();
      if (minutesString.length === 1) {
        minutesString = '0' + minutesString;
      }
      return minutesString;
    };


    /**
     *
     * @method getRemainingMinutes
     *
     * @description Time remaining before timer expiry, in minutes. Note that
     * one minute may display for two minutes - this is to avoid displaying
     * zero minutes. A better solution needs to be found.
     *
     */

    Timer.prototype.getRemainingMinutes = function() {
      return Math.floor(this.getRemainingTime() / 1000 / 60);
    };


    /**
     *
     * @method getDisplaySeconds
     *
     * @description Time remaining before timer expiry, in seconds.
     *
     */

    Timer.prototype.getDisplaySeconds = function() {
      var secondsString;
      secondsString = (this.getRemainingSeconds() % 60).toString();
      if (secondsString.length === 1) {
        secondsString = '0' + secondsString;
      }
      return secondsString;
    };


    /**
     *
     * @method getRemainingSeconds
     *
     * @description Time remaining before timer expiry, in seconds.
     *
     */

    Timer.prototype.getRemainingSeconds = function() {
      return Math.floor(this.getRemainingTime() / 1000);
    };


    /**
     *
     * @method getRemainingTime
     *
     * @description Time remaining before timer expiry, in milliseconds.
     *
     */

    Timer.prototype.getRemainingTime = function() {
      return Math.max(0, this.getEndTime() - Date.now());
    };


    /**
     *
     * @method getCurrentTime
     *
     * @description Time at which the timer has progressed to, in milliseconds.
     *
     */

    Timer.prototype.getCurrentTime = function() {
      return Date.now() - this.getStartTime();
    };


    /**
     *
     * @method getEndTime
     *
     * @description Time, as a Unix timestamp, when the timer will expire.
     *
     */

    Timer.prototype.getEndTime = function() {
      return this.getStartTime() + this._duration;
    };


    /**
     *
     * @method isActive
     *
     * @description Returns a Boolean that indicates whether the timer is
     * actively counting down.
     *
     */

    Timer.prototype.isActive = function() {
      return this.isStarted() && !this.isPaused();
    };


    /**
     *
     * @method isPaused
     *
     * @description Returns whether the timer is paused.
     *
     */

    Timer.prototype.isPaused = function() {
      return this._paused;
    };


    /**
     *
     * @method isStarted
     *
     * @description Returns whether the timer has been started.
     *
     */

    Timer.prototype.isStarted = function() {
      return this._started;
    };


    /**
     *
     * @method cache
     * @private
     *
     * @description Cache the current state of the timer in local storage.
     *
     */

    Timer.prototype._cache = function() {
      var timerState;
      timerState = {
        id: this._id,
        duration: this._duration,
        startTime: this._startTime,
        started: this._started,
        paused: this._paused
      };
      debugService.log('Caching the current state of the timer in local storage', TAG, {
        timerState: timerState,
        timerId: this._id
      });
      return cacheService.set(this._id, 'timer', timerState);
    };


    /**
     *
     * @method start
     * @private
     *
     * @description Private start method callable by both the public `start`
     * and `resume` methods, which handle setting high-level timer state.
     *
     */

    Timer.prototype._start = function() {
      var localNotificationOptions;
      if (!this._startTime) {
        this._startTime = Date.now();
      }
      this._interval = $interval(((function(_this) {
        return function() {
          if (Date.now() >= _this.getEndTime()) {
            $interval.cancel(_this._interval);
            return _this.onExpiration();
          }
        };
      })(this)), 500);
      debugService.log('Starting timer', TAG, {
        startTime: this._startTime,
        endTime: this.getEndTime(),
        timerId: this._id
      });
      if (this._push) {
        localNotificationOptions = {
          id: this._id,
          at: this.getEndTime(),
          title: this._push.title,
          onOpen: function() {
            debugService.log('User clicked local notification that was scheduled by a timer', TAG, {
              localNotificationOptions: localNotificationOptions,
              timerId: this._id
            });
            return $state.go('cook');
          }
        };
        localNotificationService.schedule(localNotificationOptions);
        debugService.log('Scheduling a local notification for when timer finishes', TAG, {
          localNotificationOptions: localNotificationOptions,
          timerId: this._id
        });
      }
      document.addEventListener('resume', (function(_this) {
        return function() {
          return _this.onTimerRetrievedFromBackground();
        };
      })(this));
      return this._cache();
    };


    /**
     *
     * @method cancel
     *
     * @description Calls any callbacks registered for a given event.
     *
     */

    Timer.prototype.cancel = function() {
      localNotificationService.cancel(this._id);
      cacheService.remove(this._id, 'timer');
      document.removeEventListener('resume', this.onTimerRetrievedFromBackground);
      $interval.cancel(this._interval);
      return debugService.log('Cancelling timer', TAG, {
        timerId: this._id
      });
    };


    /**
     *
     * @method pause
     *
     * @description Pauses the timer by setting @_started to false and
     * updating the duration by the elapsed time running.
     *
     */

    Timer.prototype.pause = function() {
      if (this._paused || !this._started) {
        return;
      }
      this._paused = true;
      this._duration = this._duration - (Date.now() - this.getStartTime());
      this._startTime = null;
      localNotificationService.cancel(this._id);
      this._cache();
      $interval.cancel(this._interval);
      return debugService.log('Pausing timer', TAG, {
        duration: this._duration,
        timerId: this._id
      });
    };

    Timer.prototype.resume = function() {
      debugService.log('Resuming timer', TAG, {
        timerId: this._id
      });
      if (!this._paused) {
        return;
      }
      this._paused = false;
      return this._start();
    };


    /**
     *
     * @method start
     *
     * @description Kicks off the timer. Timers are not started by default,
     * unless the option autostart is passed as true.
     *
     */

    Timer.prototype.start = function() {
      if (this._started) {
        debugService.log('Start timer called, but timer was already started', TAG, {
          timerId: this._id
        });
        return;
      }
      this._started = true;
      return this._start();
    };


    /**
     *
     * @method on
     *
     * @description Calls any callbacks registered for a given event.
     *
     */

    Timer.prototype.onExpiration = function() {
      debugService.log('Timer expired', TAG, {
        timerId: this._id
      });
      if (this._push) {
        debugService.log('Showing alert because timer expired', TAG, {
          timerAlertBody: this._push.title,
          timerId: this._id
        });
        return alertService.alert({
          headerColor: 'alert-green',
          iconUrl: 'svg/timer-alert.svg#timer-alert',
          titleString: locale.getString('time.timerExpired'),
          bodyString: this._push.title,
          sound: true,
          vibrate: 'long'
        });
      }
    };


    /**
     *
     * @method onTimerRetrievedFromBackground
     *
     * @description Handles timer state upon app retrieval from the background.
     *
     */

    Timer.prototype.onTimerRetrievedFromBackground = function() {
      debugService.log('Retrieved timer from background', TAG, {
        timerId: this._id
      });
      if (this.getRemainingTime() === 0) {
        debugService.log('Will cancel timer because remaining time is 0', TAG, {
          timerId: this._id
        });
        return this.cancel();
      }
    };

    return Timer;

  })();
  return function(duration, options) {
    return new Timer(duration, options);
  };
}]);

var extend = function(child, parent) { for (var key in parent) { if (hasProp.call(parent, key)) child[key] = parent[key]; } function ctor() { this.constructor = child; } ctor.prototype = parent.prototype; child.prototype = new ctor(); child.__super__ = parent.prototype; return child; },
  hasProp = {}.hasOwnProperty;

this.app.factory('WebSocketAddressConnection', ["$window", "connectionState", "connectionProvidersConfig", "WebSocketOpenTimeoutError", "WebSocketConnectError", "debugService", "disconnectReasons", function($window, connectionState, connectionProvidersConfig, WebSocketOpenTimeoutError, WebSocketConnectError, debugService, disconnectReasons) {
  var TAG;
  TAG = 'WebSocketAddressConnection';

  /**
   *
   * @class WebSocketAddressConnection
   *
   * @description This class implements the Connection interface.
   * It is responsible for managing the communication to and from a single webSocket channel for a single circulator address.
   * It listens on events from WebSocketConnection and act on them only if they match the circulator address.
   * It determines if the circulator address is reachable by listening on recipientUnavailableReply and pong.
   *
   * @fires 'close'
   * @fires 'open'
   * @fires 'authorize'
   * @fires 'connecting'
   * @fires 'message' with {Message} object when there's a message to read
   *
   */
  return (function(superClass) {
    extend(_Class, superClass);


    /**
     *
     * @method constructor
     * @memberof WebSocketAddressConnection
     *
     * @param {string} id - The unique id of the connection
     * @param {string} circulatorAddress - The hex address used to identify a circulator
     * @param {WebSocketConnection} webSocketConnection - The underlying webSocket connection
     *
     */

    function _Class(id, circulatorAddress, webSocketConnection) {
      this.id = id;
      this.circulatorAddress = circulatorAddress;
      this.webSocketConnection = webSocketConnection;
      this.type = connectionProvidersConfig.webSocket.type;
      this.reconnectBaseTime = connectionProvidersConfig.webSocket.reconnectBaseTime;
      this.supportsSendingFile = true;
      this.preferredFileTransferBlockSize = 128;
      this.connectionState = connectionState.disconnected;
      this.disconnectReason = disconnectReasons.initialState;
      this.onOpenHandler = this.onOpen.bind(this);
      this.onConnectingHandler = this.onConnecting.bind(this);
      this.onMessageHandler = this.onMessage.bind(this);
      this.onCloseHandler = this.onClose.bind(this);
      this.onRecipientUnavailableReplyHandler = this.onRecipientUnavailableReply.bind(this);
      this.onPongHandler = this.onPong.bind(this);
    }


    /**
     *
     * @method open
     * @memberof WebSocketAddressConnection
     *
     * @description Establishes a connection
     *
     * @param {number} timeoutInMilliseconds - The timeout to set in milliseconds, null / no timeout by default.
     *
     * @returns {Promise} Resolves when the connection has been established successfully
     *
     * @fires 'open'
     * @fires 'close'
     *
     */

    _Class.prototype.open = function(timeoutInMilliseconds) {
      if (timeoutInMilliseconds == null) {
        timeoutInMilliseconds = connectionProvidersConfig.webSocket.connectionTimeout;
      }
      if (this.connectionState !== connectionState.disconnected) {
        return $window.Q({});
      } else {
        this.webSocketConnection.removeListener('open', this.onOpenHandler);
        this.webSocketConnection.removeListener('connecting', this.onConnectingHandler);
        this.webSocketConnection.removeListener('message', this.onMessageHandler);
        this.webSocketConnection.removeListener('close', this.onCloseHandler);
        this.webSocketConnection.removeListener('recipientUnavailableReply', this.onRecipientUnavailableReplyHandler);
        this.webSocketConnection.removeListener('pong', this.onPongHandler);
        this.webSocketConnection.on('open', this.onOpenHandler);
        this.webSocketConnection.on('connecting', this.onConnectingHandler);
        this.webSocketConnection.on('message', this.onMessageHandler);
        this.webSocketConnection.on('close', this.onCloseHandler);
        this.webSocketConnection.on('recipientUnavailableReply', this.onRecipientUnavailableReplyHandler);
        this.webSocketConnection.on('pong', this.onPongHandler);
        return this.webSocketConnection.open(timeoutInMilliseconds);
      }
    };


    /*
     *
     * @method openAndAuthorize
     * @memberof WebSocketAddressConnection
     *
     * @description Establishes a connection with authorization.
     * An authorized state means the connection is reachable by receiving 'pong'.
     *
     * @param {number} timeoutInMilliseconds - The timeout to set in milliseconds, null / no timeout by default.
     *
     * @returns {Promise} Resolves when the connection has been authorized successfully
     *
     */

    _Class.prototype.openAndAuthorize = function(timeoutInMilliseconds) {
      var address;
      if (timeoutInMilliseconds == null) {
        timeoutInMilliseconds = connectionProvidersConfig.webSocket.connectionTimeout;
      }
      if (this.connectionState === connectionState.connectedAuthorized) {
        return $window.Q();
      } else {
        address = this.circulatorAddress;
        return this.open(timeoutInMilliseconds).then((function(_this) {
          return function() {
            var message, waitOptions;
            if (_this.connectionState === connectionState.connectedAuthorized) {
              return $window.Q();
            } else if (_this.connectionState !== connectionState.connected) {
              throw new WebSocketConnectError('Invalid connection state during authorize');
            } else {
              if (_this.webSocketConnection.handler == null) {
                throw new WebSocketConnectError('Invalid handler');
              } else {
                debugService.log("WebSocketAddressConnection attempting to reach address " + address, TAG);
                message = new $window.CirculatorSDK.messages.StreamMessage().setRecipientAddress($window.CirculatorSDK.hexToByteAddress(address)).set('ping', new $window.CirculatorSDK.messages.Ping());
                waitOptions = {
                  maxResponses: 1,
                  timeoutSecs: connectionProvidersConfig.webSocket.pingTimeoutSeconds
                };
                return _this.webSocketConnection.handler.initiateStreamAndWait(message, waitOptions).then(function(streamResponse) {
                  var msg, msgType;
                  if (streamResponse.timedOut) {
                    debugService.warn('WebSocketAddressConnection openAndAuthorize got timeout', TAG);
                    throw new WebSocketOpenTimeoutError('Stream response has timed out');
                  } else {
                    msg = streamResponse.messages[0];
                    if (msg != null) {
                      msgType = msg.getMessageType();
                      if (msgType === 'pong') {
                        debugService.log('WebSocketAddressConnection openAndAuthorize got pong response back', TAG);
                        _this.connectionState = connectionState.connectedAuthorized;
                        _this.emit('authorize');
                        return msg;
                      } else {
                        debugService.warn("WebSocketAddressConnection openAndAuthorize has failed with msgType " + msgType, TAG);
                        throw new Error("Wrong message type: " + msgType);
                      }
                    } else {
                      throw new Error('Invalid message.  The stream might have ended.');
                    }
                  }
                });
              }
            }
          };
        })(this));
      }
    };


    /**
     *
     * @method close
     * @memberof WebSocketAddressConnection
     *
     * @description Close the connection
     *
     * @param {disconnectReasons enum} disconnectReason - The reason for closing
     *
     * @fires 'close'
     *
     */

    _Class.prototype.close = function(disconnectReason) {
      this.disconnectReason = disconnectReason;
      return this.webSocketConnection.close(disconnectReason);
    };


    /**
     *
     * @method write
     * @memberof WebSocketAddressConnection
     *
     * @description Sends a message
     *
     * @param {Message} message - The message to send
     *
     * @returns {Promise} Resolves when the write is successful, rejects with error otherwise
     *
     */

    _Class.prototype.write = function(message) {
      return this.webSocketConnection.write(message);
    };


    /**
     *
     * @method writeFile
     * @memberof WebSocketAddressConnection
     *
     * @description Sends a file, only called if this connection @supportsSendingFile
     *
     * @param {hex string} address - recipient address
     * @param {ByteBuffer} file - The file to send
     * @param {number} blockSize - amount of data (number of bytes) to send in each block
     * @param {message stream} stream - open stream to send file blocks on
     *
     * @returns {Promise} Resolves when the send is successful,
     *   notifies of progress,
     *   rejects with error otherwise
     *
     */

    _Class.prototype.writeFile = function(address, file, blockSize, stream) {
      return this.webSocketConnection.writeFile(address, file, blockSize, stream);
    };


    /**
     *
     * @method sendBlock
     * @memberof WebSocketAddressConnection
     * @private
     *
     * @param {int} index - block index
     * @param {ByteBuffer} block - ByteBuffer to send over the wire
     * @param {hex string} address - recipient address
     * @param {message stream} stream - open stream to send file blocks on
     *
     * @description writes the block and returns promise
     *
     */

    _Class.prototype.sendBlock = function(index, block, address, stream) {
      return this.webSocketConnection.sendBlock(index, block, address, stream);
    };


    /**
     *
     * @method onOpen
     * @memberof WebSocketAddressConnection
     * @private
     *
     * @description Handler for websocket open
     *
     */

    _Class.prototype.onOpen = function() {
      this.connectionState = connectionState.connected;
      return this.emit('open');
    };


    /**
     *
     * @method onConnecting
     * @memberof WebSocketAddressConnection
     * @private
     *
     * @description Handler for websocket connecting
     *
     */

    _Class.prototype.onConnecting = function() {
      this.connectionState = connectionState.connecting;
      return this.emit('connecting');
    };


    /**
     *
     * @method onMessage
     * @memberof WebSocketAddressConnection
     * @private
     *
     * @description Handler for websocket message
     *
     */

    _Class.prototype.onMessage = function(message) {
      var decodedMessage, senderAddress;
      decodedMessage = $window.CirculatorSDK.messages.StreamMessage.decode(message);
      senderAddress = decodedMessage.getSenderAddress().toHex();
      if (senderAddress === this.circulatorAddress) {
        return this.emit('message', message);
      }
    };


    /**
     *
     * @method onClose
     * @memberof WebSocketAddressConnection
     * @private
     *
     * @description Handler for websocket close
     *
     */

    _Class.prototype.onClose = function(disconnectReason) {
      this.webSocketConnection.removeListener('open', this.onOpenHandler);
      this.webSocketConnection.removeListener('connecting', this.onConnectingHandler);
      this.webSocketConnection.removeListener('message', this.onMessageHandler);
      this.webSocketConnection.removeListener('close', this.onCloseHandler);
      this.webSocketConnection.removeListener('recipientUnavailableReply', this.onRecipientUnavailableReplyHandler);
      this.webSocketConnection.removeListener('pong', this.onPongHandler);
      this.connectionState = connectionState.disconnected;
      this.disconnectReason = disconnectReason;
      return this.emit('close', this.disconnectReason);
    };


    /**
     *
     * @method onRecipientUnavailableReply
     * @memberof WebSocketAddressConnection
     * @private
     *
     * @description Handler for websocket recipientUnavailableReply message
     *
     */

    _Class.prototype.onRecipientUnavailableReply = function(decodedMessage) {
      var recipientAddress;
      recipientAddress = decodedMessage.getRecipientUnavailableReply().getRecipientAddress().toHex();
      if (recipientAddress === this.circulatorAddress) {
        debugService.warn('Address is not reachable', TAG, {
          circulatorAddress: this.circulatorAddress
        });
        return this.close(disconnectReasons.unreachableAddress);
      }
    };


    /**
     *
     * @method onPong
     * @memberof WebSocketAddressConnection
     * @private
     *
     * @description Handler for websocket pong message
     *
     */

    _Class.prototype.onPong = function(decodedMessage) {
      var senderAddress;
      senderAddress = decodedMessage.getSenderAddress().toHex();
      if (senderAddress === this.circulatorAddress) {
        return this.webSocketConnection.handler.handleMessage(decodedMessage);
      }
    };

    return _Class;

  })($window.CirculatorSDK.Connection);
}]);

var extend = function(child, parent) { for (var key in parent) { if (hasProp.call(parent, key)) child[key] = parent[key]; } function ctor() { this.constructor = child; } ctor.prototype = parent.prototype; child.prototype = new ctor(); child.__super__ = parent.prototype; return child; },
  hasProp = {}.hasOwnProperty;

this.app.factory('WebSocketConnectionProvider', ["$window", "debugService", "WebSocketConnection", "WebSocketAddressConnection", "connectionProvidersConfig", "disconnectReasons", function($window, debugService, WebSocketConnection, WebSocketAddressConnection, connectionProvidersConfig, disconnectReasons) {
  var TAG;
  TAG = 'WebSocketConnectionProvider';

  /**
   *
   * @interface WebSocketConnectionProvider
   *
   * @description This class implements the ConnectionProvider interface.
   * It is responsible for providing methods to discover {Endpoint}s and create {Connection}s over webSocket.
   * It manages the lifetime of WebSocketConnection objects.
   *
   */
  return (function(superClass) {
    extend(_Class, superClass);


    /**
     *
     * @method constructor
     * @memberof WebSocketConnectionProvider
     *
     */

    function _Class(applicationAddress) {
      this.applicationAddress = applicationAddress;
      this.type = connectionProvidersConfig.webSocket.type;
      this.webSocketConnection = null;
      this.webSocketAddressConnections = {};
    }


    /**
     *
     * @method discover
     * @memberof WebSocketConnectionProvider
     *
     * @description To discover compatible objects over this channel
     *
     * @returns {Promise} Notifies with an {Endpoint} every time one has been discovered
     *
     */

    _Class.prototype.discover = function() {
      return $window.Q([]);
    };


    /**
     *
     * @method createConnection
     * @memberof WebSocketConnectionProvider
     *
     * @description Translates an {Endpoint} into {WebSocketConnection}
     *
     * @returns {WebSocketConnection}
     *
     */

    _Class.prototype.createConnectionFromEndpoint = function(endpoint) {
      debugService.log('WebSocketConnectionProvider creating WebSocketConnection from endpoint', TAG);
      return this.createConnection(endpoint.address);
    };


    /*
     *
     * @method createConnectionByAddress
     * @memberof WebSocketConnectionProvider
     *
     * @description Translates an {ConnectionData} into {WebSocketConnection}
     *
     * @param {ConnectionData} connectionData
     * @returns {WebSocketConnection}
     *
     */

    _Class.prototype.createConnectionFromData = function(connectionData) {
      debugService.log('WebSocketConnectionProvider creating WebSocketConnection from connectionData', TAG);
      return this.createConnection(connectionData.address);
    };


    /*
     *
     * @method createConnectionFromCandidate
     * @memberof WebSocketConnectionProvider
     *
     * @description Translates an {CirculatorCandidate} into {WebSocketConnection}
     *
     * @param {CirculatorCandidate} candidate
     * @returns {WebSocketConnection}
     *
     */

    _Class.prototype.createConnectionFromCandidate = function(candidate) {
      debugService.log('WebSocketConnectionProvider creating WebSocketConnection from connectionData', TAG);
      return this.createConnection(candidate.address);
    };


    /*
     *
     * @method createConnection
     * @memberof WebSocketConnectionProvider
     *
     * @description Creates a {WebSocketConnection}
     *
     * @param {string} circulatorAddress - The circulator address
     * @returns {WebSocketConnection}
     *
     */

    _Class.prototype.createConnection = function(circulatorAddress) {
      var id;
      id = this.type + '-' + circulatorAddress;
      if (this.webSocketConnection == null) {
        debugService.log('WebSocketConnectionProvider creating new WebSocketConnection', TAG);
        this.webSocketConnection = new WebSocketConnection(this.applicationAddress);
      }
      if (this.webSocketAddressConnections[id] == null) {
        debugService.log('WebSocketConnectionProvider creating new WebSocketAddressConnection', TAG, {
          id: id,
          circulatorAddress: circulatorAddress
        });
        this.webSocketAddressConnections[id] = new WebSocketAddressConnection(id, circulatorAddress, this.webSocketConnection);
      }
      return this.webSocketAddressConnections[id];
    };


    /*
     *
     * @method cleanUp
     * @memberof WebSocketConnectionProvider
     *
     * @description Clean up states
     *
     */

    _Class.prototype.cleanUp = function() {
      debugService.log('WebSocketConnectionProvider cleanUp', TAG);
      _.forEach(this.webSocketAddressConnections, function(connection) {
        connection.close(disconnectReasons.cleanUp);
        return connection = null;
      });
      this.webSocketAddressConnections = {};
      return this.webSocketConnection = null;
    };

    return _Class;

  })($window.CirculatorSDK.ConnectionProvider);
}]);

var extend = function(child, parent) { for (var key in parent) { if (hasProp.call(parent, key)) child[key] = parent[key]; } function ctor() { this.constructor = child; } ctor.prototype = parent.prototype; child.prototype = new ctor(); child.__super__ = parent.prototype; return child; },
  hasProp = {}.hasOwnProperty;

this.app.factory('WebSocketConnection', ["$window", "$timeout", "CSWebSocket", "authenticationService", "debugService", "connectionState", "connectionProvidersConfig", "WebSocketConnectError", "WebSocketOpenTimeoutError", "DisconnectedWriteError", "WebSocketWriteError", "FileTransferBlockTimeoutError", "firmwareUpdateConfig", "disconnectReasons", "networkStateService", function($window, $timeout, CSWebSocket, authenticationService, debugService, connectionState, connectionProvidersConfig, WebSocketConnectError, WebSocketOpenTimeoutError, DisconnectedWriteError, WebSocketWriteError, FileTransferBlockTimeoutError, firmwareUpdateConfig, disconnectReasons, networkStateService) {
  var TAG;
  TAG = 'WebSocketConnection';

  /**
   *
   * @class WebSocketConnection
   *
   * @description This class consumes CSWebSocket class and manages opening and closing the web socket channel.
   * It is responsible for managing the communication to and from a single webSocket channel for multiple circulators.
   * It is the first point of contact for sending and receiving circulator messages over webSocket.
   * It bubbles up messages/events to multiple address connections and let them handle accordingly.
   *
   * @fires 'close'
   * @fires 'open'
   * @fires 'connecting'
   * @fires 'message' with {Message} object when there's a message to read
   * @fires 'recipientUnavailableReply' with {Message} object
   * @fires 'pong' with {Message} object
   *
   *
   */
  return (function(superClass) {
    extend(_Class, superClass);


    /**
     *
     * @method constructor
     * @memberof WebSocketConnection
     *
     */

    function _Class(applicationAddress) {
      this.applicationAddress = applicationAddress;
      this.webSocket = null;
      this.handler = null;
      this.url = connectionProvidersConfig.webSocket.discovery;
      this.connectionState = connectionState.disconnected;
      this.disconnectReason = disconnectReasons.initialState;
      this.openDeferred = null;
      this.openTimeout = null;
    }


    /**
     *
     * @method open
     * @memberof WebSocketConnection
     *
     * @description Establishes a connection
     *
     * @param {number} timeoutInMilliseconds - The timeout to set in milliseconds, null / no timeout by default.
     *
     * @returns {Promise} Resolves when the connection has been established successfully
     *
     * @fires 'open'
     * @fires 'close'
     *
     */

    _Class.prototype.open = function(timeoutInMilliseconds) {
      var beginOpenTime;
      if (timeoutInMilliseconds == null) {
        timeoutInMilliseconds = connectionProvidersConfig.webSocket.connectionTimeout;
      }
      if (this.connectionState !== connectionState.disconnected) {
        return $window.Q({});
      } else if (networkStateService.noInternet()) {
        this.close(disconnectReasons.noInternet);
        return $window.Q.reject(new WebSocketConnectError('No internet'));
      } else {
        debugService.log('WebSocketConnection open', TAG);
        if (this.openDeferred == null) {
          this.clearOpenTimeout();
          this.setOpenTimeout(timeoutInMilliseconds);
          beginOpenTime = new Date();
          this.openDeferred = $window.Q.defer();
          this.connectionState = connectionState.connecting;
          this.webSocket = this.getWebSocket();
          this.emit('connecting');

          /*
           *
           * @description Invoked when we have received a message from the webSocket, emit a message event
           *
           * @param {buffer} message
           *
           */
          this.webSocket.onMessage((function(_this) {
            return function(message) {
              var decodedMsg;
              decodedMsg = $window.CirculatorSDK.messages.StreamMessage.decode(message);
              if (_.indexOf(['connectionReadyReply', 'ping'], decodedMsg.getMessageType()) > -1) {
                debugService.log("WebSocketConnection is handling " + (decodedMsg.getMessageType()), TAG);
                if (!_this.handler) {
                  return debugService.error('WebSocketConnection got a null handler while handling a message', TAG);
                } else {
                  return _this.handler.handleMessage(decodedMsg);
                }
              } else if (decodedMsg.getMessageType() === 'recipientUnavailableReply') {
                debugService.log('WebSocketConnection is firing recipientUnavailableReply', TAG);
                return _this.emit('recipientUnavailableReply', decodedMsg);
              } else if (decodedMsg.getMessageType() === 'pong') {
                debugService.log('WebSocketConnection is firing pong', TAG);
                return _this.emit('pong', decodedMsg);
              } else {
                debugService.debug("WebSocketConnection has received message " + (decodedMsg.getMessageType()), TAG);
                return _this.emit('message', message);
              }
            };
          })(this));

          /*
           *
           * @description Invoked when the webSocket connection is established.
           *
           */
          this.webSocket.onOpen((function(_this) {
            return function() {
              _this.handler = _this.getHandler();
              debugService.log('WebSocketConnection on connection open', TAG);
              _this.handler.registerOutput('connectionReadyReply', function(stream) {
                var elapsedTime, endOpenTime, ref;
                endOpenTime = new Date();
                elapsedTime = endOpenTime - beginOpenTime;
                debugService.log('WebSocketConnection is ready for connection', TAG, {
                  openTime: elapsedTime
                });
                _this.connectionState = connectionState.connected;
                stream.end();
                if ((ref = _this.openDeferred) != null) {
                  ref.resolve();
                }
                _this.openDeferred = null;
                _this.clearOpenTimeout();
                return _this.emit('open');
              });
              _this.handler.registerOutput('ping', function(stream) {
                var newMessage;
                debugService.log('WebSocketConnection got ping, sending pong', TAG);
                newMessage = new $window.CirculatorSDK.messages.StreamMessage().set('pong', new $window.CirculatorSDK.messages.Pong()).setEnd(true);
                return stream.send(newMessage);
              });
              return _this.handler.on('msgReady', function(msg) {
                var encodedMsg;
                debugService.debug("WebSocketConnection handler sending " + (msg.getMessageType()) + " message", TAG);
                encodedMsg = msg.encode().toBuffer();
                return _this.write(encodedMsg);
              });
            };
          })(this));

          /*
           *
           * @description Invoked if a connection error has occurred.
           *
           *
           */
          this.webSocket.onError(function(error) {
            return debugService.warn('WebSocketConnection on connection error', TAG, {
              error: error,
              connectionState: this.connectionState
            });
          });

          /*
           *
           * @description Invoked if connection is closed.
           *
           *
           */
          this.webSocket.onClose((function(_this) {
            return function() {
              var ref;
              debugService.warn('WebSocketConnection on connection close', TAG, {
                connectionState: _this.connectionState
              });
              if (_this.connectionState !== connectionState.disconnected) {
                if ((ref = _this.openDeferred) != null) {
                  ref.reject(new WebSocketConnectError('WebSocketConnection on connection close'));
                }
                _this.openDeferred = null;
                return _this.close(disconnectReasons.terminatedByOther);
              }
            };
          })(this));
        }
        return this.openDeferred.promise;
      }
    };


    /**
     *
     * @method close
     * @memberof WebSocketConnection
     *
     * @description Close the connection
     *
     * @param {disconnectReasons enum} disconnectReason - The reason for closing
     *
     * @fires 'close'
     *
     */

    _Class.prototype.close = function(disconnectReason) {
      var ref, ref1;
      debugService.log('WebSocketConnection close', TAG, {
        disconnectReason: disconnectReason
      });
      this.connectionState = connectionState.disconnected;
      this.disconnectReason = disconnectReason;
      if ((ref = this.webSocket) != null) {
        ref.close();
      }
      this.clearOpenTimeout();
      this.webSocket = null;
      if ((ref1 = this.handler) != null) {
        ref1.cleanUp();
      }
      this.handler = null;
      if (this.openDeferred != null) {
        this.openDeferred.reject(new WebSocketConnectError('Closed while trying to open connection'));
        this.openDeferred = null;
      }
      return this.emit('close', this.disconnectReason);
    };


    /**
     *
     * @method writeFile
     * @memberof WebSocketConnection
     *
     * @description Sends a file, only called if this connection @supportsSendingFile
     *
     * @param {hex string} address - recipient address
     * @param {ByteBuffer} file - The file to send
     * @param {number} blockSize - amount of data (number of bytes) to send in each block
     * @param {message stream} stream - open stream to send file blocks on
     *
     * @returns {Promise} Resolves when the send is successful,
     *   notifies of progress,
     *   rejects with error otherwise
     *
     */

    _Class.prototype.writeFile = function(address, file, blockSize, stream) {
      var messageQueue;
      debugService.log("WebSocketConnection attempting to write file to " + address, TAG);
      messageQueue = $window.CirculatorSDK.FileTransferUtils.blocksFromData(file, blockSize);
      return $window.CirculatorSDK.FileTransferUtils.processMessages(_.partial(this.sendBlock.bind(this), _, _, _, stream), messageQueue, address);
    };


    /**
     *
     * @method sendBlock
     * @memberof WebSocketConnection
     * @private
     *
     * @param {int} index - block index
     * @param {ByteBuffer} block - ByteBuffer to send over the wire
     * @param {hex string} address - recipient address
     * @param {message stream} stream - open stream to send file blocks on
     *
     * @description writes the block and returns promise
     *
     */

    _Class.prototype.sendBlock = function(index, block, address, stream) {
      var deferred, handleMessage, handleTimeout, message, numTries, sendMessage, timer;
      debugService.log("WebSocketConnection attempting to write file block to " + address, TAG);
      message = new $window.CirculatorSDK.messages.StreamMessage().setRecipientAddress($window.CirculatorSDK.hexToByteAddress(address)).set('transferFileBlockRequest', new $window.CirculatorSDK.messages.TransferFileBlockRequest().set('blockIdx', index).set('block', block.buffer));
      deferred = $window.Q.defer();
      numTries = 0;
      timer = null;
      handleMessage = function(msg) {
        var msgType, reply;
        msgType = msg.getMessageType();
        if (msgType === 'transferFileBlockReply') {
          $timeout.cancel(timer);
          reply = msg.get('transferFileBlockReply');
          debugService.log('Got transferFileBlockReply', TAG, reply);
          if (reply.blockIdx < index) {
            return debugService.log("Got duplicate transferFileBlockReply for block " + reply.blockIdx + ", ignoring", TAG);
          } else if (reply.blockIdx === index && reply.result === $window.CirculatorSDK.messages.Result.CS_SUCCESS) {
            deferred.resolve();
            return stream.removeListener('msgReceived', handleMessage);
          } else {
            deferred.reject('Invalid transferFileBlockReply');
            return stream.removeListener('msgReceived', handleMessage);
          }
        }
      };
      stream.on('msgReceived', handleMessage);
      sendMessage = function() {
        debugService.log("transferFileBlockRequest attemp " + numTries + "/" + firmwareUpdateConfig.transferFileBlockRetryAttempts + " for block " + index, TAG);
        stream.send(message);
        numTries++;
        return timer = $timeout(handleTimeout, firmwareUpdateConfig.transferFileBlockTimeoutSecs * 1000);
      };
      handleTimeout = function() {
        if (numTries >= firmwareUpdateConfig.transferFileBlockRetryAttempts) {
          debugService.error("transferFileBlockRequest failed " + firmwareUpdateConfig.transferFileBlockRetryAttempts + ", bailing on block " + index, TAG);
          return deferred.reject(new FileTransferBlockTimeoutError("Timed out sending block " + index));
        } else {
          return sendMessage();
        }
      };
      sendMessage();
      return deferred.promise;
    };


    /**
     *
     * @method write
     * @memberof WebSocketConnection
     *
     * @description Sends a message
     *
     * @param {Message} message - The message to send
     *
     * @returns {Promise} Resolves when the write is successful, rejects with error otherwise
     *
     */


    /* eslint-disable no-unused-vars */

    _Class.prototype.write = function(message) {
      var decodedMsg, deferred, error, error1, errorMessage, ref;
      deferred = $window.Q.defer();
      if (this.connectionState === connectionState.connected || this.connectionState === connectionState.connectedAuthorized) {
        decodedMsg = $window.CirculatorSDK.messages.StreamMessage.decode(message);
        debugService.log("WebSocketConnection is writing message " + (decodedMsg.getMessageType()), TAG);
        try {
          if ((ref = this.webSocket) != null) {
            ref.send(message);
          }
          deferred.resolve();
        } catch (error1) {
          error = error1;
          debugService.warn('WebSocketConnection closing connection after write failure', TAG, {
            error: error
          });
          deferred.reject(new WebSocketWriteError(error));
          this.close(disconnectReasons.writeError);
        }
      } else {
        errorMessage = 'WebSocketConnection attempt to write without being connected! connectionState: ' + this.connectionState;
        debugService.warn(errorMessage, TAG);
        deferred.reject(new DisconnectedWriteError(errorMessage));
      }
      return deferred.promise;
    };


    /* eslint-enable no-unused-vars */


    /**
     *
     * @method getWebSocket
     * @memberof WebSocketConnection
     * @private
     *
     * @description Private helper function to create a CSWebSocket
     *
     */

    _Class.prototype.getWebSocket = function() {
      return new CSWebSocket(this.url + '?token=' + authenticationService.getToken(), false);
    };


    /**
     *
     * @method getHandler
     * @memberof WebSocketConnection
     * @private
     *
     * @description Private helper function to create a StreamMessageHandler
     *
     */

    _Class.prototype.getHandler = function() {
      return new $window.CirculatorSDK.StreamMessageHandler({
        myAddress: $window.CirculatorSDK.hexToByteAddress(this.applicationAddress),
        log: debugService.getLogger(),
        handlerType: 'webSocketConnection'
      });
    };


    /**
     *
     * @method setOpenTimeout
     * @memberof WebSocketConnection
     * @private
     *
     * @description Private helper function to set an a timeout on the open operation
     * @param {number} timeoutInMilliseconds - The timeout to set in milliseconds
     *
     */

    _Class.prototype.setOpenTimeout = function(timeoutInMilliseconds) {
      return this.openTimeout = $timeout((function(_this) {
        return function() {
          var ref, ref1, ref2;
          if (((ref = _this.openDeferred) != null ? (ref1 = ref.promise) != null ? ref1.isPending() : void 0 : void 0) && _this.connectionState === connectionState.connecting) {
            debugService.error('WebSocketConnection Open Timeout', TAG);
            if ((ref2 = _this.openDeferred) != null) {
              ref2.reject(new WebSocketOpenTimeoutError('Open Timeout'));
            }
            _this.openDeferred = null;
            return _this.close(disconnectReasons.openTimeout);
          }
        };
      })(this), timeoutInMilliseconds);
    };


    /**
     *
     * @method clearOpenTimeout
     * @memberof WebSocketConnection
     * @private
     *
     * @description Private helper function to clear a timeout on the open operation
     *
     */

    _Class.prototype.clearOpenTimeout = function() {
      if (this.openTimeout != null) {
        $timeout.cancel(this.openTimeout);
      }
      return this.openTimeout = null;
    };

    return _Class;

  })($window.CirculatorSDK.EventEmitter);
}]);

this.app.constant('webSocketReadyStates', {
  connecting: 0,
  open: 1,
  closing: 2,
  closed: 3
});

this.app.factory('CSWebSocket', ["$window", "$interval", "debugService", "webSocketReadyStates", function($window, $interval, debugService, webSocketReadyStates) {
  var TAG;
  TAG = 'CSWebSocket';

  /*
   *
   * @name WebSocket
   *
   * @description This class wraps around window.WebSocket with our own interface.
   * It provides generic methods for the caller to interact with a WebSocket given an URL.
   *
   * @example myWebSocket = new CSWebSocket('ws://localhost:8080/')
   *
   */
  return (function() {

    /*
     *
     * Constructor
     * @class
     *
     * @description Establishes a websocket connection with a given url
     *
     * @param {string} url - websocket url to connect to
     * @param {boolean} [shouldReconnect = false] - flag to indicate if it should reconnect upon closing, default is false
     *
     * @throws {Error} Will throw if missing parameter url
     *
     */
    function _Class(url, shouldReconnect) {
      if (shouldReconnect == null) {
        shouldReconnect = false;
      }
      if (!url) {
        throw new Error('WebSocket:constructor: Missing parameter [url].');
      }
      this.url = url;
      this.shouldReconnect = shouldReconnect;
      this.reconnectInterval = null;
      this.ws = this.initialize();
    }


    /*
     *
     * @method initialize
     * @public
     *
     * @description Initializes the websocket channel
     *
     * @returns {$window.WebSocket} - a new WebSocket object
     *
     */

    _Class.prototype.initialize = function() {

      /*
       * @type {WebSocket} - Webview WebSocket object
       */
      var ws;
      ws = new $window.WebSocket(this.url);
      ws.binaryType = 'arraybuffer';

      /*
       *
       * @description Invoked if a connection error has occurred
       *
       * @param {object} error - the error object
       *
       */
      ws.onerror = (function(_this) {
        return function(error) {
          debugService.log('Websocket onerror: ', TAG, {
            onerrorObject: error
          });
          if (_this.onErrorCallback != null) {
            return _this.onErrorCallback(error);
          }
        };
      })(this);

      /*
       *
       * @description Invoked when a connection is terminated
       *
       * @param {object} ev - the close event object
       *
       */
      ws.onclose = (function(_this) {
        return function(ev) {
          debugService.log('Websocket onclose: ', TAG, {
            oncloseEvent: ev
          });
          if (_this.shouldReconnect && _this.reconnectInterval === null) {
            _this.reconnectInterval = $interval(function() {
              debugService.log('Websocket reconnecting', TAG);
              return _this.initialize(_this.url, _this.shouldReconnect);
            }, 2000);
          }
          if (_this.onCloseCallback != null) {
            return _this.onCloseCallback();
          }
        };
      })(this);

      /*
       *
       * @description Invoked when a WebSocket connection is established
       *
       */
      ws.onopen = (function(_this) {
        return function() {
          debugService.debug('Websocket onopen', TAG);
          if (_this.reconnectInterval) {
            debugService.log('Websocket reconnected', TAG);
            $interval.cancel(_this.reconnectInterval);
            _this.reconnectInterval = null;
          }
          if (_this.onOpenCallback != null) {
            return _this.onOpenCallback();
          }
        };
      })(this);

      /*
       *
       * @description Invoked when a message is received from the server
       *
       * @param {WebSocket.MessageEvent} message - see http://www.w3.org/TR/webmessaging/
       * Its data field contains the actual data being sent over the web socket
       *
       */
      ws.onmessage = (function(_this) {
        return function(message) {
          if (_this.onMessageCallback != null) {
            return _this.onMessageCallback(message.data);
          } else {
            return debugService.warn('Websocket onMessage without a message callback', TAG);
          }
        };
      })(this);
      return ws;
    };


    /*
     *
     * @method send
     * @public
     *
     * @description Sends a message to the websocket server
     *
     * @param {object} data - data to be sent
     *
     * @throws {Error} Will throw if data is missing, or if web socket is not opened
     *
     */

    _Class.prototype.send = function(data) {
      var ref;
      if (!data) {
        throw new Error('WebSocket:send: Missing parameter [data].');
      }
      if (((ref = this.ws) != null ? ref.readyState : void 0) !== webSocketReadyStates.open) {
        throw new Error('WebSocket:send: readyState is not open.');
      }
      return this.ws.send(data);
    };


    /*
     *
     * @method close
     * @public
     *
     * @description Closes the connection without allowing reconnect
     *
     */

    _Class.prototype.close = function() {
      var ref;
      debugService.debug('Websocket closing', TAG);
      this.shouldReconnect = false;
      if (this.reconnectInterval != null) {
        $interval.cancel(this.reconnectInterval);
        this.reconnectInterval = null;
      }
      if (((ref = this.ws) != null ? ref.readyState : void 0) !== webSocketReadyStates.closed) {
        return this.ws.close();
      }
    };


    /*
     *
     * @method onOpen
     * @public
     *
     * @description Attach a callback handler for the onopen event
     *
     * @param {function} callback - The callback function to invoke during onopen
     *
     */

    _Class.prototype.onOpen = function(callback) {
      return this.onOpenCallback = callback;
    };


    /*
     *
     * @method onMessage
     * @public
     *
     * @description Attach a callback handler for the onmessage event
     *
     *
     * @param {function} callback - The callback function to invoke during onmessage
     *
     */

    _Class.prototype.onMessage = function(callback) {
      return this.onMessageCallback = callback;
    };


    /*
     *
     * @method onError
     * @public
     *
     * @description Attach a callback handler for the onerror event
     *
     *
     * @param {function} callback - The callback function to invoke during onerror
     *
     */

    _Class.prototype.onError = function(callback) {
      return this.onErrorCallback = callback;
    };


    /*
     *
     * @method onClose
     * @public
     *
     * @description Attach a callback handler for the onclose event
     *
     *
     * @param {function} callback - The callback function to invoke during onclose
     *
     */

    _Class.prototype.onClose = function(callback) {
      return this.onCloseCallback = callback;
    };

    return _Class;

  })();
}]);

this.app.service('advertisementService', ["$window", "$location", "debugService", "analyticsService", "csConfig", "circulatorConnectionStates", "circulatorManager", "$http", "cacheService", "utilities", "appConfig", function($window, $location, debugService, analyticsService, csConfig, circulatorConnectionStates, circulatorManager, $http, cacheService, utilities, appConfig) {
  var CACHE_PREFIX, TAG, _token, anyJouleSeen;
  TAG = 'advertisementService';
  CACHE_PREFIX = 'advertisement';
  _token = null;
  this.onUserSignIn = function(token) {
    return _token = token;
  };
  this.onUserSignOut = function() {
    return _token = null;
  };
  anyJouleSeen = function() {
    return circulatorManager.getCirculatorConnectionState() !== circulatorConnectionStates.unpaired;
  };
  this.openAdTarget = function(advertisement, medium) {
    var baseUrl, url, utm_source;
    utm_source = 'jouleApp';
    baseUrl = advertisement.url || appConfig.jouleSalesPage;
    if (baseUrl.match(/^\/[^\/]/)) {
      $location.url(baseUrl);
      debugService.log('Internal ad clicked', TAG, advertisement);
      analyticsService.track('Internal Ad Clicked', advertisement);
    } else {
      url = baseUrl + "?utm_source=" + utm_source + "&utm_medium=" + medium + "&utm_campaign=" + advertisement.campaign;
      $window.open(url, '_system');
      debugService.log('Ad clicked', TAG, advertisement);
      analyticsService.track('Ad Clicked', advertisement);
    }
    return true;
  };
  this.getAdContent = function(page, slot, aspect) {
    if (slot == null) {
      slot = null;
    }
    if (aspect == null) {
      aspect = null;
    }
    return $window.Q.Promise(function(resolve, reject) {
      var cacheKey, cachedContent, config;
      cacheKey = page + "+" + slot + "+" + _token + "+" + (anyJouleSeen());
      cachedContent = cacheService.get(cacheKey, CACHE_PREFIX);
      if (cachedContent && ((Date.now() - cachedContent[0].cacheTime) < utilities.convertHoursToMilliseconds(1))) {
        return resolve(cachedContent);
      } else {
        config = {
          url: csConfig.chefstepsEndpoint + "/api/v0/recommendations",
          method: 'GET',
          params: {
            page: page,
            slot: slot,
            aspect: aspect,
            platform: 'jouleApp',
            limit: 1,
            connected: anyJouleSeen()
          }
        };
        if (_token) {
          config['headers'] = {
            Authorization: "Bearer " + _token
          };
        }
        return $http(config).then(function(response) {
          var count, results;
          count = response.data.results.length;
          if (count > 0) {
            debugService.log("Succesfully got " + count + " ad(s) for", TAG, {
              advertisementParams: config.params
            });
            results = response.data.results;
            results[0].cacheTime = Date.now();
            cacheService.set(cacheKey, CACHE_PREFIX, results);
            return resolve(results);
          } else {
            debugService.log('Got 0 ads for', TAG, {
              advertisementParams: config.params
            });
            return reject(new Error('Found 0 usable advertisements'));
          }
        })["catch"](function(error) {
          debugService.error('Failed to get ad', TAG, {
            error: error,
            advertisementParams: config.params
          });
          if (cachedContent) {
            debugService.error('Using expired ad as fallback', TAG);
            return resolve(cachedContent);
          } else {
            return reject(error);
          }
        });
      }
    });
  };
  return this;
}]);

this.app.service('alertService', ["$ionicPopup", "$rootScope", "locale", "vibrateService", "$window", "debugService", "$ionicHistory", function($ionicPopup, $rootScope, locale, vibrateService, $window, debugService, $ionicHistory) {
  var TAG, checkForVibrate, getPopupOptions, logAlert, playSound, popupScope, sound;
  popupScope = null;
  TAG = 'alertService';
  logAlert = function(alertType, popupOptions) {
    return debugService.log('alert shown of type: ' + alertType, TAG, {
      popupOptions: popupOptions,
      appCurrentViewName: $ionicHistory.currentStateName()
    });
  };
  getPopupOptions = function(options) {
    var popupOptions, ref;
    popupScope = $rootScope.$new();
    popupScope.headerColor = options.headerColor;
    popupScope.iconUrl = options.iconUrl;
    if (options.icon != null) {
      switch (options.icon) {
        case 'fail':
          popupScope.iconUrl = 'svg/fail-alert.svg#fail-alert';
          break;
        case 'success':
          popupScope.iconUrl = 'svg/success-alert.svg#success-alert';
          break;
        case 'heart-joule':
          popupScope.iconUrl = 'svg/heart-joule.svg#heart-joule';
          break;
        case 'connecting-joule':
          popupScope.iconUrl = 'svg/connecting-joule-alert.svg#connecting-joule-alert';
          break;
        case 'question':
          popupScope.iconUrl = 'svg/question.svg#question';
      }
    }
    popupScope.titleString = options.titleString;
    popupScope.bodyString = options.bodyString;
    popupScope.actions = options.actions;
    popupScope.tertiaryActionText = options.tertiaryActionText;
    popupScope.tertiaryActionClicked = function() {
      var popupOptionsToLog;
      popupOptionsToLog = _.pick(options, ['titleString', 'bodyString', 'tertiaryActionText']);
      debugService.log('User clicked alert tertiary action', {
        popupOptions: popupOptionsToLog
      });
      return options.tertiaryAction();
    };
    popupScope.link = options.link || {};
    popupScope.link.string = ((ref = options.link) != null ? ref.string : void 0) || locale.getString('popup.learnMore');
    popupScope.linkClicked = function() {
      var linkObj;
      debugService.log('user clicked popup link', TAG, popupScope.link);
      linkObj = popupScope.link;
      if (linkObj.preprocessor != null) {
        popupScope.linkLoading = true;
        linkObj.preprocessor(linkObj.uri).then(function(redirectUrl) {
          popupScope.linkLoading = false;
          $window.open(redirectUrl, '_blank', 'clearcache=yes');
          return true;
        })["catch"](function(err) {
          debugService.error('error while preprocessing alert link', TAG, {
            error: err
          });
          popupScope.linkLoading = false;
          $window.open(linkObj.uri, '_blank', 'clearcache=yes');
          return true;
        }).done(_.noop, function(e) {
          return debugService.onPromiseUnhandledRejection(e, TAG);
        });
      } else {
        $window.open(linkObj.uri, '_blank', 'clearcache=yes');
      }
    };
    popupScope.linkLoading = false;
    popupScope.hasInput = options.hasInput;
    popupScope.sound = options.sound;
    popupScope.inputPlaceholder = options.inputPlaceholder;
    popupScope.passwordInputPlaceholder = locale.getString('pairing.password');
    popupScope.inputType = options.inputType;
    popupScope.inputValues = {};
    popupScope.inputChanged = function(inputText, inputName) {
      return popupScope.inputValues[inputName] = inputText;
    };
    popupOptions = {
      scope: popupScope,
      templateUrl: options.templateUrl ? options.templateUrl : 'templates/alerts/generic-alert.html',
      template: options.template,
      okText: options.okText || locale.getString('general.okay'),
      cancelText: options.cancelText || locale.getString('general.cancel')
    };
    popupScope.data = {
      singleSelect: ''
    };
    return popupOptions;
  };
  checkForVibrate = function(options) {
    if (options.vibrate === 'short') {
      vibrateService.vibrate(100);
    }
    if (options.vibrate === 'long') {
      return vibrateService.vibrate(500);
    }
  };
  sound = new Audio('./sounds/notification.mp3');
  playSound = function() {
    return sound.play();
  };
  this.show = function(options) {
    var popup, popupOptions, popupPromise, ref, ref1;
    options.hasInput = true;
    popupOptions = getPopupOptions(options);
    checkForVibrate(options);
    if (options.sound) {
      playSound();
    }
    popupOptions.buttons = options.buttons;
    if ((ref = $window.cordova) != null) {
      ref.plugins.Keyboard.hideKeyboardAccessoryBar(false);
    }
    if ((ref1 = $window.cordova) != null) {
      ref1.plugins.Keyboard.show(true);
    }
    logAlert('show', options);
    popup = $ionicPopup.show(popupOptions);
    popupPromise = popup.then(function(response) {
      if (response !== 'cancel') {
        return popupScope.inputValues;
      }
    });
    popupPromise.close = popup.close;
    return popupPromise;
  };
  this.confirm = function(options) {
    var popupOptions;
    popupOptions = getPopupOptions(options);
    checkForVibrate(options);
    if (options.sound) {
      playSound();
    }
    logAlert('confirm', options);
    return $ionicPopup.confirm(popupOptions);
  };
  this.alert = function(options) {
    var popupOptions;
    popupOptions = getPopupOptions(options);
    checkForVibrate(options);
    if (options.sound) {
      playSound();
    }
    logAlert('alert', options);
    return $ionicPopup.alert(popupOptions);
  };
  this.prompt = (function(_this) {
    return function(options) {
      return _this.show(angular.extend(options, {
        buttons: [
          {
            text: locale.getString('general.cancel'),
            onTap: function() {
              return 'cancel';
            }
          }, {
            text: locale.getString('general.okay'),
            type: 'button-positive'
          }
        ],
        hasInput: true
      }));
    };
  })(this);
  return this;
}]);

this.app.service('analyticsService', ["$window", "$state", "$rootScope", "debugService", "csConfig", "authenticationService", "cacheService", "devSimulatorService", "temperatureUnitService", function($window, $state, $rootScope, debugService, csConfig, authenticationService, cacheService, devSimulatorService, temperatureUnitService) {
  var TAG, getUsageHistory, sessionCount;
  TAG = 'AnalyticsService';
  this.analytics = null;
  this.user = null;
  this.isInitialized = false;
  this.initializeDeferred = null;
  sessionCount = null;

  /*
   *
   * @method initialize
   *
   * @description Initialize the analystics components if they haven't been initalized yet.
   *
   * @returns {Promise} - A promise which resolves if the request succeeds, or rejects with an error
   *
   */
  this.initialize = function() {
    var options;
    if (this.isInitialized) {
      return $window.Q(true);
    } else {
      if (this.initializeDeferred == null) {
        this.initializeDeferred = $window.Q.defer();
        sessionCount = cacheService.get('usageHistory', 'sessionCount') || 0;
        sessionCount += 1;
        cacheService.set('usageHistory', 'sessionCount', sessionCount);
        ionic.Platform.device();
        if (false) {
          options = {
            flushAt: 10
          };
        } else {
          options = {};
        }
        this.analytics = new $window.Analytics(csConfig.segmentWriteKey, options);
        if (authenticationService.isAuthenticated()) {
          authenticationService.me().then((function(_this) {
            return function(user) {
              debugService.log('AnalyticsService user is authenticated, calling identify', TAG);
              _this.identify(user);
              _this.isInitialized = true;
              return _this.initializeDeferred.resolve();
            };
          })(this), (function(_this) {
            return function(error) {
              debugService.error('AnalyticsService unable to call me to get user!', TAG, {
                error: error
              });
              _this.isInitialized = true;
              return _this.initializeDeferred.resolve();
            };
          })(this));
        } else {
          debugService.log('AnalyticsService user is not authenticated', TAG);
          this.isInitialized = true;
          this.initializeDeferred.resolve();
        }
      }
      return this.initializeDeferred.promise;
    }
  };

  /*
   *
   * @method getUserId
   *
   * @description Returns the user ID if there is a user, null otherwise
   *
   */
  this.getUserId = function() {
    var ref;
    return (ref = this.user) != null ? ref.id : void 0;
  };

  /*
   *
   * @method getAnonymousId
   *
   * @description Returns the anonymous ID to use.  Use one from the cache, or generate a new one
   *
   * Notes: The lifetime of anonymous ID and user ID look like this:
   *        - User is not signed in, new anonymous ID is populated and cached
   *        - User signs in, calls analytics.identify(user), anonymous ID is associated with user ID
   *        - User logs out, calls analytics.identify(null), both anonymous ID and user ID are erased
   *
   * Notes: We always populate the anonymous ID in analytics regardless of user being signed in or not.
   *        So that we can match a user's activity to both an anonymous ID and the user ID when there is one.
   *        However, once a user is logged out, we will clear the anonymous ID in the cache,
   *        As we may be dealing with a completely different user in the next session.
   *
   */
  this.getAnonymousId = function() {
    var result;
    result = cacheService.get('anonymousId', 'analytics');
    if (result == null) {
      result = $window.Uuid.v4();
      cacheService.set('anonymousId', 'analytics', result);
    }
    return result;
  };

  /*
   *
   * @method getSegmentCommonContext
   *
   * @description Please see https://segment.com/docs/spec/common/
   *
   */
  this.getSegmentCommonContext = function() {
    var device, result;
    result = {
      app: {
        name: 'Joule Cooking App',
        build: '2.37.1'
      },
      locale: 'en-us',
      page: {
        path: $state.href($state.current.name, $state.params)
      },
      os: {
        name: ionic.Platform.platform(),
        version: ionic.Platform.version()
      },
      userAgent: $window.navigator.userAgent,
      ip: 0
    };
    device = ionic.Platform.device();
    if (device != null) {
      result.device = {
        model: device.model,
        manufacturer: device.manufacturer,
        id: device.uuid,
        name: device.name,
        type: device.platform,
        version: device.version,
        cordova: device.cordova
      };
    }
    return result;
  };

  /*
   *
   * @method getMixpanelUserProperties
   *
   * @description It seems that the integration of analytics-node and mixpanel does not pass user properties when an identify is called
   * This function will retun an object that list all the user properties per mixpanel's format
   *
   */
  this.getMixpanelUserProperties = function() {
    var result;
    result = {};
    if (this.user != null) {
      result = this.user;
      result.$email = this.user.email;
      result.$name = this.user.name;
    }
    return result;
  };

  /*
   *
   * @method identify
   *
   * @description Please see https://segment.com/docs/libraries/node/#identify
   * This method is called whenever a user has signed in or signed out
   *
   * @param {object} user - ChefSteps user, null if the user is no longer available (e.g. signed out)
   *
   */
  this.identify = function(user) {
    if (user != null) {
      this.user = user;
      return this.analytics.identify({
        userId: user.id,
        traits: _.omit(user, ['id']),
        context: this.getSegmentCommonContext(),
        anonymousId: this.getAnonymousId(),
        integrations: {
          Intercom: {
            user_hash: user.intercom_user_hash
          }
        }
      });
    } else {
      this.user = null;
      return cacheService.remove('anonymousId', 'analytics');
    }
  };

  /*
   *
   * @method alias
   *
   * @description Please see https://segment.com/docs/libraries/node/#alias
   *
   */
  this.alias = function(user) {
    this.analytics.alias({
      previousId: this.getAnonymousId(),
      userId: user.id,
      context: this.getSegmentCommonContext(),
      anonymousId: this.getAnonymousId()
    });
    return this.analytics.flush;
  };

  /*
   *
   * @method join
   *
   * @description This method is invoked when a user is registered for the first time
   *
   */
  this.registerUser = function() {
    return authenticationService.me().then((function(_this) {
      return function(user) {
        _this.alias(user);
        _this.identify(user);
        return _this.track('Signed Up JS', {
          source: 'Joule Cooking App'
        });
      };
    })(this));
  };

  /*
   *
   * @method track
   *
   * @description Please see https://segment.com/docs/libraries/node/#track
   *
   */
  this.track = function(event, properties) {
    if (properties == null) {
      properties = {};
    }
    return this.initialize().then((function(_this) {
      return function() {
        var allProperties, globalProperties, structure;
        globalProperties = {
          loggedIn: authenticationService.isAuthenticated(),
          nonInteraction: 1,
          tempUnit: temperatureUnitService.get()
        };
        allProperties = _.extend(properties, globalProperties, _this.getMixpanelUserProperties(), getUsageHistory());
        structure = {
          event: event,
          properties: allProperties,
          context: _this.getSegmentCommonContext()
        };
        if (_this.getUserId() != null) {
          structure.userId = _this.getUserId();
        }
        structure.anonymousId = _this.getAnonymousId();
        _this.analytics.track(structure);
        return debugService.log('Analytics track event', 'Analytics', structure);
      };
    })(this));
  };

  /*
   *
   * @method page
   *
   * @description Please see https://segment.com/docs/libraries/node/#page
   *
   */
  this.page = function(pageName, properties) {
    if (properties == null) {
      properties = {};
    }
    return this.initialize().then((function(_this) {
      return function() {
        var allProperties, globalProperties, structure;
        globalProperties = {
          loggedIn: authenticationService.isAuthenticated(),
          tempUnit: temperatureUnitService.get()
        };
        allProperties = _.extend(properties, globalProperties, _this.getMixpanelUserProperties(), getUsageHistory());
        structure = {
          name: pageName,
          properties: allProperties,
          context: _this.getSegmentCommonContext()
        };
        if (_this.getUserId() != null) {
          structure.userId = _this.getUserId();
        }
        structure.anonymousId = _this.getAnonymousId();
        _this.analytics.page(structure);
        return debugService.log('Analytics page event', 'Analytics', structure);
      };
    })(this));
  };
  getUsageHistory = function() {
    var hasEverPaired;
    hasEverPaired = cacheService.get('usageHistory', 'hasEverPaired') || false;
    return {
      usageHistory: {
        sessionCount: sessionCount,
        hasEverPaired: hasEverPaired
      }
    };
  };
  return this;
}]);


/*
 *
 * @name Asset Service
 *
 * @description Responsible for obtaining and caching remote assets.
 *
 */
this.app.service('assetService', ["$q", "$window", "networkStateService", "localStorageService", "debugService", "appConfig", "metadataEventTimer", function($q, $window, networkStateService, localStorageService, debugService, appConfig, metadataEventTimer) {
  var MIN_PURGE, TAG, VIDEO_CACHE_LIMIT, allAssetKeys, cachedPaths, isVideo;
  TAG = 'AssetService';
  VIDEO_CACHE_LIMIT = 1000 * 1024 * 1024;
  MIN_PURGE = 15 * 1024 * 1024;
  allAssetKeys = function() {
    return _.filter(localStorageService.keys(), function(k) {
      return k.match(/^asset/);
    });
  };
  isVideo = function(slug) {
    return /\.mp4$/i.test(slug);
  };
  cachedPaths = {};

  /*
   *
   * @method applicationDirectory
   * @public
   *
   * @description Returns asset path appropriate for accessing
   * files included with the application.
   *
   * @returns {String}
   *
   */
  this.applicationDirectory = function() {
    return cordova.file.applicationDirectory + 'www/';
  };

  /*
   *
   * @method dataDirectory
   * @public
   *
   * @description Returns asset path appropriate
   * for accessing files stored by the application.
   *
   * @returns {String}
   *
   */
  this.dataDirectory = function() {
    return cordova.file.dataDirectory;
  };

  /*
   *
   * @method remoteDirectory
   * @public
   *
   * @description Returns asset path appropriate for
   * accessing files stored within CloudFront.
   *
   * @returns {Number}
   *
   */
  this.remoteDirectory = function() {
    return appConfig.assetBasePath;
  };

  /*
   *
   * @method tempDirectory
   * @public
   *
   * @description Returns asset path appropriate for storing files temporarily.
   * We use this directory to store partial downloads, then move them to the
   * data directory upon completion. We use dataDirectory as a fallback (in the case of (Android).
   *
   * @returns {String}
   *
   */
  this.tempDirectory = function() {
    return cordova.file.tempDirectory || cordova.file.cacheDirectory;
  };

  /*
   *
   * @method delete
   * @public
   *
   * @description Removes a file from the file system.
   *
   * @param {string} path - Absolute asset path.
   *
   * @returns {Number}
   *
   */
  this["delete"] = (function(_this) {
    return function(path) {
      var deferred;
      if (!path) {
        throw new Error('AssetService.Delete(): Missing parameter [path].');
      }
      deferred = $q.defer();
      _this.resolve(path).then(function(file) {
        return file.remove(function(error) {
          if (error) {
            debugService.warn("delete(): Error deleting asset at path " + path + ".", TAG, {
              error: error
            });
            deferred.reject();
          }
          return deferred.resolve();
        });
      }, deferred.reject);
      return deferred.promise;
    };
  })(this);

  /*
   *
   * @method download
   * @public
   *
   * @description Abstraction method for retrieving an asset
   * from the remote asset cache. Returns a promise.
   *
   * @param {string} slug - Relative asset path.
   *
   * @returns {object} - Promise, resolves with path
   * to cached asset.
   *
   */
  this.download = (function(_this) {
    return function(slug) {
      var deferred, downloadFailure, downloadSuccess, fileTransfer, fromURL, toURL;
      if (!slug) {
        throw new Error('AssetService.Download(): Missing parameter [slug].');
      }
      deferred = $q.defer();
      fileTransfer = new $window.FileTransfer();
      downloadSuccess = function(file) {
        debugService.log('Download success for: ' + slug, TAG, {
          slug: slug
        });
        _this.move(file, _this.dataDirectory(), slug).then(deferred.resolve, deferred.reject);
        if (isVideo(slug)) {
          _this.purgeVideos();
        }
        if (isVideo(slug)) {
          return metadataEventTimer.endEventTimer('assetService-video-download_' + slug);
        }
      };
      downloadFailure = function() {
        if (isVideo(slug)) {
          metadataEventTimer.endEventTimer('assetService-video-download_' + slug);
        }
        return deferred.reject();
      };
      fromURL = _this.paths.remote(slug);
      toURL = _this.paths.temp(slug);
      if (isVideo(slug)) {
        metadataEventTimer.startEventTimer('assetService-video-download_' + slug);
      }
      fileTransfer.download(fromURL, toURL, downloadSuccess, downloadFailure);
      return deferred.promise;
    };
  })(this);

  /*
   *
   * @method exists
   * @public
   *
   * @description Determines if a given asset (as specified via a slug)
   * exists on the user's device, either in the application
   * directory itself or within the cache.
   *
   * @param {string} slug - Relative asset path.
   *
   * @returns {object} - Promise, resolves File object.
   *
   */
  this.exists = (function(_this) {
    return function(slug) {
      var deferred, updateMetadata;
      if (!slug) {
        throw new Error('AssetService.Exists(): Missing parameter [slug].');
      }
      deferred = $q.defer();
      if (!ionic.Platform.isWebView()) {
        deferred.resolve();
      } else {
        updateMetadata = function(rawFile) {
          var metadata;
          metadata = _this.metadata(slug) || {};
          metadata.path = rawFile.nativeURL;
          _this.metadata(slug, metadata);
          return deferred.resolve(rawFile);
        };
        _this.resolve(_this.paths.app(slug)).then(function(rawFile) {
          return updateMetadata(rawFile);
        })["catch"](function() {
          return _this.resolve(_this.paths.data(slug)).then(function(rawFile) {
            return updateMetadata(rawFile);
          })["catch"](deferred.reject);
        });
      }
      return deferred.promise;
    };
  })(this);

  /*
   *
   * @method existsAll
   * @public
   *
   * @description Convenience method to determining if multiple files
   * exist at an array of slugs.
   *
   * @param {array} slugs - Array of relative asset path.
   *
   * @returns {object} - Promise, resolves to an array of File objects.
   *
   */
  this.existsAll = (function(_this) {
    return function(slugs) {
      var existences, i, len, slug;
      if (!slugs) {
        throw new Error('AssetService.ExistsAll(): Missing parameter [slugs].');
      }
      existences = [];
      for (i = 0, len = slugs.length; i < len; i++) {
        slug = slugs[i];
        existences.push(_this.exists(slug));
      }
      return $q.all(existences);
    };
  })(this);

  /*
   *
   * @method fetch
   * @public
   *
   * @description Retrieve an asset by its slug from any of our
   * three main asset sources (app folder, cache, Cloudfront).
   * Upon successfully obtaining the asset, writes a metadata
   * entry for said asset.
   *
   * @param {string} slug - Relative asset path.
   * @param {object} [options] - Hash of options.
   * @property {boolean} [options.locked] - Whether to overwrite asset on purge.
   *
   * @returns {object} - Promise, resolves with path {string}.
   *
   * @todo Abstract metadata authoring logic.
   *
   */
  this.fetch = (function(_this) {
    return function(slug, options) {
      var deferred, path;
      if (options == null) {
        options = {};
      }
      if (!slug) {
        throw new Error('AssetService.Fetch(): Missing parameter [slug].');
      }
      if (_this.fetching[slug]) {
        return _this.fetching[slug].promise;
      }
      deferred = _this.fetching[slug] = $q.defer();
      if (!ionic.Platform.isWebView()) {
        path = _.startsWith(slug, './') ? slug : _this.paths.remote(slug);
        _this.metadata(slug, {
          path: path
        });
        deferred.resolve(path);
      } else {
        _this.exists(slug).then(function(rawFile) {
          return rawFile.file(function(file) {
            _this.metadata(slug, {
              cached: !!rawFile.nativeURL.match(new RegExp(_this.dataDirectory())),
              locked: !!options.locked,
              path: rawFile.nativeURL,
              size: file.size
            });
            deferred.resolve(rawFile.nativeURL);
            return delete _this.fetching[slug];
          });
        }, function() {
          return _this.download(slug).then(function(rawFile) {
            return rawFile.file(function(file) {
              _this.metadata(slug, {
                cached: !!rawFile.nativeURL.match(new RegExp(_this.dataDirectory())),
                locked: !!options.locked,
                path: rawFile.nativeURL,
                size: file.size
              });
              deferred.resolve(rawFile.nativeURL);
              return delete _this.fetching[slug];
            });
          }, function() {
            delete _this.fetching[slug];
            return deferred.reject();
          });
        });
      }
      return deferred.promise;
    };
  })(this);

  /*
   *
   * @method fetchAll
   * @public
   *
   * @description Abstraction method for fetching an array of slugs.
   *
   * @param {array} slugs - Array of slugs {string} (relative asset paths).
   * @param {object} [options] - Hash of options.
   * @property {boolean} [options.locked] - Whether to overwrite asset on purge.
   *
   * @returns {object} - Promise, resolves with array of paths {string}.
   *
   */
  this.fetchAll = (function(_this) {
    return function(slugs, options) {
      var fetches, i, len, slug;
      if (!slugs) {
        throw new Error('AssetService.FetchAll(): Missing parameter [slugs].');
      }
      fetches = [];
      for (i = 0, len = slugs.length; i < len; i++) {
        slug = slugs[i];
        fetches.push(_this.fetch(slug, options));
      }
      return $q.all(fetches);
    };
  })(this);

  /*
   *
   * @member fetching
   * @public
   *
   * @description The fetched object is keyed to all slugs the app has attempted to
   * to fetch - each key returns a promise. The existence of this object
   * allows us to consolidate resource requests, in such cases where we
   * begin a large prefetch and the user requests a video shortly thereafter.
   *
   */
  this.fetching = {};

  /*
   *
   * @member isFetching
   * @public
   *
   * @description Return whether or not there is currently a fetch in process for a given slug
   *
   */
  this.isFetching = (function(_this) {
    return function(slug) {
      return _this.fetching[slug] != null;
    };
  })(this);

  /*
   *
   * @method get
   * @public
   *
   * @description Returns a promise which resolves to the URL of the locally cached
   * asset.
   *
   * @param {string} slug - Relative asset path.
   *
   * @returns {object} - Promise, resolves with path to cached asset.
   *
   */
  this.get = (function(_this) {
    return function(slug, allowFetch) {
      var assetMetadata, deferred, path;
      if (allowFetch == null) {
        allowFetch = true;
      }
      if (!slug) {
        throw new Error('AssetService.Get(): Missing parameter [slug].');
      }
      deferred = $q.defer();
      assetMetadata = _this.metadata(slug);
      if (!ionic.Platform.isWebView()) {
        path = _.startsWith(slug, './') ? slug : _this.paths.remote(slug);
        _this.metadata(slug, {
          path: path
        });
        deferred.resolve(path);
      } else if (_.startsWith(slug, './')) {
        deferred.resolve(slug);
      } else if (assetMetadata) {
        _this.resolve(assetMetadata.path).then(function() {
          _this.updateAccessTime(slug);
          debugService.debug('Get(): Resolving with ' + assetMetadata.path, TAG);
          return deferred.resolve(assetMetadata.path);
        }, function() {
          return _this.fetch(slug).then(function(path) {
            debugService.debug('Get(): Resolving from fetch with ' + path, TAG);
            return deferred.resolve(path);
          }, deferred.reject);
        });
      } else if (allowFetch) {
        _this.fetch(slug).then(function(path) {
          debugService.debug('Get(): Resolving from fetch with ' + path, TAG);
          return deferred.resolve(path);
        }, deferred.reject);
      } else {
        debugService.info("Get(): Unable to get() asset due to lack of metadata and fetching being disallowed. Slug: " + slug, TAG);
        deferred.reject();
      }
      return deferred.promise;
    };
  })(this);

  /*
   *
   * @method getAll
   * @public
   *
   * @description Abstraction method for getting an array of slugs.
   *
   * @param {array} slugs - Array of slugs {string} (relative asset paths).
   *
   * @returns {object} - Promise, resolves with array of paths {string}.
   *
   */
  this.getAll = (function(_this) {
    return function(slugs) {
      var deferred, gets, recursor;
      deferred = $q.defer();
      if (!slugs) {
        throw new Error('AssetService.GetAll(): Missing parameter [slugs].');
      }
      gets = [];
      recursor = function(index) {
        debugService.debug('GetAll(): About to get ' + slugs[index], TAG);
        return _this.get(slugs[index]).then((function(path) {
          debugService.debug('GetAll(): Got ' + path, TAG);
          gets.push(path);
          deferred.notify(gets.length);
          if (gets.length === slugs.length) {
            return deferred.resolve(gets);
          } else {
            return recursor(++index);
          }
        }), deferred.reject);
      };
      recursor(0);
      return deferred.promise;
    };
  })(this);

  /*
   *
   * @method lock
   * @public
   *
   * @description Ensure that a file won't be deleted if we
   * need to purge the cache to achieve the
   * maximum cache size.
   *
   * @param {string} slug - Relative asset path.
   * @param {boolean} [lock = true] - Whether the asset should be locked
   * or unlocked.
   *
   * @returns {object} - Asset metadata.
   *
   */
  this.lock = function(slug, lock) {
    var assetMetadata;
    if (lock == null) {
      lock = true;
    }
    if (!slug) {
      throw new Error('AssetService.Lock(): Missing parameter [slug].');
    }
    assetMetadata = this.metadata(slug);
    if (assetMetadata) {
      assetMetadata.locked = lock;
      this.metadata(slug, assetMetadata);
    } else {
      debugService.log("Lock(): Unable to locate metadata for asset with slug " + slug + ".", TAG);
    }
    return assetMetadata;
  };

  /*
   *
   * @method lockAll
   * @public
   *
   * @description Abstraction method for locking an array of slugs
   *
   * @param {array} [slugs] - Array of asset slugs to lock.
   *
   */
  this.lockAll = (function(_this) {
    return function(slugs) {
      var i, len, results, slug;
      if (!slugs) {
        throw new Error('AssetService.LockAll(): Missing parameter [slugs].');
      }
      results = [];
      for (i = 0, len = slugs.length; i < len; i++) {
        slug = slugs[i];
        results.push(_this.lock(slug));
      }
      return results;
    };
  })(this);
  this.purgeableVideos = function() {
    var i, key, len, metadata, purgeable, ref;
    purgeable = [];
    ref = allAssetKeys();
    for (i = 0, len = ref.length; i < len; i++) {
      key = ref[i];
      metadata = localStorageService.get(key);
      if (metadata.cached && !metadata.locked && isVideo(metadata.slug)) {
        purgeable.push(metadata);
      }
    }
    return purgeable;
  };

  /*
   *
   * @method purge
   * @public
   *
   * @description Remove least recently used video assets until we are under cache limit.
   * The internal version is exposed so it can be mocked. Which I guess
   * is an indicator of low self esteem.
   *
   */
  this.purgeVideosInternal = function() {
    var amountToPurge, item, overLimit, purgeable, start, totalUsage;
    start = Date.now();
    purgeable = this.purgeableVideos();
    totalUsage = _.reduce(purgeable, (function(total, meta) {
      return total + meta.size;
    }), 0);
    if (totalUsage >= VIDEO_CACHE_LIMIT) {
      overLimit = totalUsage - VIDEO_CACHE_LIMIT;
      amountToPurge = Math.max(overLimit, MIN_PURGE);
      debugService.log('purge: Over limit', TAG, {
        overLimit: overLimit,
        amountToPurge: amountToPurge
      });
      purgeable = _.sortBy(purgeable, 'lastAccess');
      while (amountToPurge > 0) {
        if (purgeable.length === 0) {
          debugService.warn("purge: Couldn't purge enough - allowing overflow", TAG);
          break;
        }
        item = purgeable.shift();
        debugService.debug('purge: Removing item', TAG, {
          slug: item.slug,
          size: item.size,
          lastAccess: item.lastAccess
        });
        amountToPurge -= item.size;
        this.remove(item.slug)["catch"](function() {
          return debugService.warn('purge: Failed to remove item', TAG, {
            slug: item.slug,
            size: item.size
          });
        });
      }
    } else {
      debugService.debug('purge: Under limit, not doing anything.', TAG, {
        totalUsage: totalUsage
      });
    }
    return debugService.debug('purge: Complete.', TAG, {
      time: Date.now() - start
    });
  };
  this.purgeVideos = _.throttle(this.purgeVideosInternal, 5000);

  /*
   *
   * @method metadata
   * @public
   *
   * @description Gets and sets metadata for a cached asset.
   *
   * @param {string} slug - Relative asset path.
   * @param {object} [metadata] - Metadata to set for given slug.
   * @property {boolean} [metadata.cached] - Whether the asset lives in the cache.
   * @property {number} [metadata.lastAccess] - Timestamp of the asset's last usage.
   * @property {boolean} [metadata.locked] - Whether the file may be deleted.
   * @property {string} [metadata.path] - The absolute path to the asset on the device.
   * @property {number} [metadata.size] - The size in octets of the file.
   * @property {string} [metadata.slug] - The relative path of the asset.
   *
   * @returns {object} - Asset metadata.
   *
   * @todo Abstract key access into data service.
   *
   */
  this.metadata = function(slug, metadata) {
    var defaultMetadata, metadataKey;
    if (!slug) {
      throw new Error('AssetService.Metadata(): Missing parameter [slug].');
    }
    metadataKey = "asset." + slug;
    if (metadata) {
      defaultMetadata = {
        slug: slug,
        lastAccess: Date.now()
      };
      metadata = _.assign(defaultMetadata, metadata);
      localStorageService.set(metadataKey, metadata);
      return metadata;
    } else {
      return localStorageService.get(metadataKey);
    }
  };

  /*
   *
   * @method updateAccessTime
   * @public
   *
   * @description Update last time an asset was used.
   *
   */
  this.updateAccessTime = function(slug) {
    var metadata;
    metadata = this.metadata(slug);
    if (metadata) {
      metadata.lastAccess = Date.now();
      return this.metadata(slug, metadata);
    }
  };

  /*
   *
   * @method pathFor
   * @public
   *
   * @description Non-promise returning version of get() that produces a value suitable
   * for use in an angular binding. It always returns the cached value, but will initiate an async
   * get if that doesn't exist. Slugs that start with './' are presumed to refer to built-in assets.
   *
   */
  this.pathFor = function(slug) {
    if (_.startsWith(slug, './')) {
      return slug;
    }
    if (cachedPaths[slug]) {
      this.updateAccessTime(slug);
      return cachedPaths[slug];
    }
    this.get(slug, !networkStateService.requireManualVideoDownloads()).then(function(path) {
      return cachedPaths[slug] = path;
    });
    return void 0;
  };

  /*
   *
   * @method move
   * @public
   *
   * @description Accepts a file, and a new path, and moves the file to that path.
   * Note that we don't use the FileEntry.moveTo() method, which has proved itself
   * to be a poorly implemented monster in that i throws mysterious FileError #9 errors.
   * Instead, we simply use a FileTransfer object to shuttle the file locally.
   *
   */
  this.move = function(file, directory, slug) {
    return this.copy(file, directory, slug, true);
  };
  this.copy = function(file, directory, slug, deleteOriginal) {
    var deferred, fileTransfer;
    deferred = $q.defer();
    fileTransfer = new $window.FileTransfer();
    fileTransfer.download(file.nativeURL, directory + slug, (function(copiedFile) {
      if (deleteOriginal) {
        file.remove();
      }
      return deferred.resolve(copiedFile);
    }), deferred.reject);
    return deferred.promise;
  };

  /*
   *
   * @member paths
   * @public
   *
   * @description Keyed to simple utility methods for generating
   * resource paths given a slug.
   *
   */
  this.paths = {
    app: (function(_this) {
      return function(slug) {
        return "" + (_this.applicationDirectory()) + slug;
      };
    })(this),
    data: (function(_this) {
      return function(slug) {
        return "" + (_this.dataDirectory()) + slug;
      };
    })(this),
    remote: (function(_this) {
      return function(slug) {
        return "" + (_this.remoteDirectory()) + slug;
      };
    })(this),
    temp: (function(_this) {
      return function(slug) {
        return "" + (_this.tempDirectory()) + slug;
      };
    })(this)
  };

  /*
   *
   * @method remove
   *
   * @description Remove an asset with a given slug.
   *
   */
  this.remove = (function(_this) {
    return function(slug) {
      var deferred, metadata;
      if (!slug) {
        throw new Error('AssetService.Remove(): Missing parameter [slug].');
      }
      deferred = $q.defer();
      metadata = _this.metadata(slug);
      if (metadata && !metadata.locked) {
        _this.exists(slug).then(function(rawFile) {
          return _this["delete"](rawFile.nativeURL).then(function() {
            localStorageService.remove("asset." + slug);
            delete cachedPaths[slug];
            return deferred.resolve;
          })["catch"](function() {
            return deferred.reject;
          });
        }, function() {
          debugService.warn('remove(): Unable to resolve file for asset with metadata.', TAG);
          return deferred.resolve();
        });
      } else {
        deferred.resolve();
      }
      return deferred.promise;
    };
  })(this);

  /*
   *
   * @method removeAll
   *
   */
  this.removeAll = (function(_this) {
    return function(slugs) {
      var deferred, i, j, key, len, len1, ref, removals, slug;
      if (!slugs) {
        slugs = [];
        ref = allAssetKeys();
        for (i = 0, len = ref.length; i < len; i++) {
          key = ref[i];
          slugs.push(localStorageService.get(key).slug);
        }
      }
      deferred = $q.defer();
      removals = [];
      for (j = 0, len1 = slugs.length; j < len1; j++) {
        slug = slugs[j];
        removals.push(_this.remove(slug));
      }
      $q.all(removals);
      return deferred.promise;
    };
  })(this);

  /*
   *
   * @method resolve
   * @public
   *
   * @description Ensures that a path to the local file system can be
   * sucessfully resolved.
   *
   * @param {string} path - Absolute asset path.
   *
   * @returns {object} - Promise which resolve to a File {object},
   *
   */
  this.resolve = function(path) {
    var deferred;
    if (!path) {
      throw new Error('AssetService.Resolve(): Missing parameter [path].');
    }
    deferred = $q.defer();
    $window.resolveLocalFileSystemURL(path, (function(_this) {
      return function(file) {
        var fullCachePath, fullPath;
        if (file.nativeURL.indexOf('android_asset') === -1) {
          deferred.resolve(file);
        } else {
          fullPath = file.fullPath.substr(0, file.fullPath.lastIndexOf('/') + 1);
          if (fullPath.includes('/www')) {
            fullPath = fullPath.substr(5);
          }
          fullCachePath = _this.dataDirectory() + fullPath;
          return $window.resolveLocalFileSystemURL("" + fullCachePath + file.name, function(cachedFile) {
            debugService.debug("resolve(): File was already in the cache directory: " + cachedFile, TAG);
            return deferred.resolve(cachedFile);
          }, function() {
            debugService.debug("resolve(): File was not cached, copying android_asset " + (JSON.stringify(file)) + " to cache directory " + (_this.dataDirectory()), TAG);
            return _this.copy(file, fullCachePath, file.name).then(function(copiedFile) {
              debugService.debug("resolve(): Copied file to " + (JSON.stringify(copiedFile)), TAG);
              return deferred.resolve(copiedFile);
            }, function(error) {
              debugService.error("resolve(): Failed to copy file with error " + (JSON.stringify(error)), TAG, {
                error: error
              });
              return deferred.reject();
            });
          });
        }
      };
    })(this), deferred.reject);
    return deferred.promise;
  };

  /*
   *
   * @method unlock
   * @public
   *
   * @description Partially applied abstraction
   * method for calling lock([slug], false).
   *
   * @param {string} slug - Relative asset path.
   *
   * @returns {object} - Asset metadata.
   *
   */
  this.unlock = _.partial(this.lock, _, false);

  /*
   *
   * @method unlockAll
   * @public
   *
   * @description Abstraction method for unlocking an array of slugs
   *
   * @param {array} [slugs] - Array of asset slugs to unlock.
   *
   */
  this.unlockAll = (function(_this) {
    return function(slugs) {
      var i, len, results, slug;
      if (!slugs) {
        throw new Error('AssetService.UnlockAll(): Missing parameter [slugs].');
      }
      results = [];
      for (i = 0, len = slugs.length; i < len; i++) {
        slug = slugs[i];
        results.push(_this.unlock(slug));
      }
      return results;
    };
  })(this);
  return this;
}]);

this.app.constant('bluetoothConnectionStates', {
  disconnected: 0,
  connecting: 1,
  connected: 2,
  disconnecting: 3
});

this.app.service('bluetooth', ["$window", "$timeout", "bluetoothStates", "bluetoothConnectionStates", "connectionProvidersConfig", "debugService", "BluetoothPowerError", "BluetoothScanError", "BluetoothWriteError", "BluetoothConnectError", "BluetoothReadServiceError", "BluetoothReadCharacteristicsError", "BluetoothReadError", "BluetoothSubscribeError", "BluetoothUnsubscribeError", "DeviceAddressNotFoundError", "DeviceNotFoundError", "DeviceAlreadyConnectedError", "GattFailure", function($window, $timeout, bluetoothStates, bluetoothConnectionStates, connectionProvidersConfig, debugService, BluetoothPowerError, BluetoothScanError, BluetoothWriteError, BluetoothConnectError, BluetoothReadServiceError, BluetoothReadCharacteristicsError, BluetoothReadError, BluetoothSubscribeError, BluetoothUnsubscribeError, DeviceAddressNotFoundError, DeviceNotFoundError, DeviceAlreadyConnectedError, GattFailure) {
  var TAG, powerStateRetry;
  TAG = 'Bluetooth';

  /*
   *
   * @name bluetooth
   *
   * @description This service provides the underlying bluetooth connection and basic API
   *
   */

  /** Internal variable used to track reading of service data. */
  powerStateRetry = 0;

  /*
   *
   * @description Start scanning bluetooth low energy channels searching for devices
   *
   * @param {string} streamServiceUUID - The stream service UUID for a matching device
   * @param {function} success - success callback
   * @param {function} failure - failure callback
   *
   */
  this.startScan = function(streamServiceUUID, success, failure) {
    debugService.log('Bluetooth scan initializing...', TAG);
    this.stopScan();
    return this.powerState(function(powerStatus) {
      if (powerStatus === bluetoothStates.poweredOn) {
        return typeof evothings !== "undefined" && evothings !== null ? evothings.ble.startScan(function(device) {
          var ref, ref1, ref2;
          if (((ref = device.advertisementData) != null ? (ref1 = ref.kCBAdvDataServiceUUIDs) != null ? (ref2 = ref1[0]) != null ? ref2.toLowerCase() : void 0 : void 0 : void 0) !== streamServiceUUID.toLowerCase()) {
            return;
          }
          return success(device);
        }, function(errorCode) {
          debugService.error("Bluetooth scan error " + errorCode, TAG);
          return failure(new BluetoothScanError(errorCode));
        }) : void 0;
      } else {
        debugService.warn("Bluetooth power error " + powerStatus, TAG);
        return failure(new BluetoothPowerError('Bluetooth power error', powerStatus));
      }
    }, function(error) {
      return failure(error);
    });
  };

  /*
   *
   * @description Stop scanning bluetooth low energy
   *
   */
  this.stopScan = function() {
    return typeof evothings !== "undefined" && evothings !== null ? evothings.ble.stopScan() : void 0;
  };

  /*
   *
   * @description Connect to a bluetooth low energy device
   *
   * @param {string} bluetoothAddress - Address of the bluetooth device
   * @param {function} connect - callback in case of successful connection
   * @param {function} disconnect - callback in case of disconnection
   * @param {function} connecting - callback in case of connecting state
   * @param {function} failure - failure callback
   *
   */
  this.connect = function(bluetoothAddress, connect, disconnect, connecting, failure) {
    debugService.log('Bluetooth connect initializing...', TAG);
    this.stopScan();
    return this.powerState(function(powerStatus) {
      if (powerStatus === bluetoothStates.poweredOn) {
        return typeof evothings !== "undefined" && evothings !== null ? evothings.ble.connect(bluetoothAddress, function(connectionInfo) {
          debugService.log('Bluetooth has returned connection status', TAG, {
            connectionInfo: connectionInfo
          });
          switch (connectionInfo.state) {
            case bluetoothConnectionStates.connected:
              return connect(connectionInfo);
            case bluetoothConnectionStates.disconnected:
              return disconnect(connectionInfo);
            case bluetoothConnectionStates.connecting:
              return connecting(connectionInfo);
          }
        }, function(errorCode) {
          debugService.warn('Bluetooth connection error: ' + errorCode, TAG);
          if (errorCode) {
            if (errorCode === 'device with given address not found') {
              return failure(new DeviceAddressNotFoundError());
            } else if (errorCode === 'device not found') {
              return failure(new DeviceNotFoundError());
            } else if (errorCode === 'device already connected') {
              return failure(new DeviceAlreadyConnectedError());
            } else if (errorCode === 257) {
              return failure(new GattFailure());
            } else {
              return failure(new BluetoothConnectError('Unexpected connection error ' + errorCode));
            }
          }
        }) : void 0;
      } else {
        debugService.warn("Bluetooth power error " + powerStatus, TAG);
        return failure(new BluetoothPowerError('Bluetooth power error', powerStatus));
      }
    }, function(error) {
      return failure(error);
    });
  };

  /*
   *
   * @description Disconnect from a device
   *
   * @param {object} deviceHandle - Bluetooth device handle
   *
   */
  this.disconnect = function(deviceHandle) {
    return typeof evothings !== "undefined" && evothings !== null ? evothings.ble.close(deviceHandle) : void 0;
  };

  /*
   *
   * @description Read the bluetooth devices services
   *
   * @param {object} deviceHandle - Bluetooth device handle
   * @param {function} success - success callback
   * @param {function} failure - failure callback
   *
   */
  this.readServices = function(deviceHandle, success, failure) {
    return typeof evothings !== "undefined" && evothings !== null ? evothings.ble.services(deviceHandle, function(services) {
      debugService.log('Bluetooth reading services success', TAG, {
        numberOfServices: services.length
      });
      return success(services);
    }, function(errorCode) {
      debugService.error('Bluetooth reading services error: ' + errorCode, TAG);
      return failure(new BluetoothReadServiceError(errorCode));
    }) : void 0;
  };

  /*
   *
   * @description Read the bluetooth characteristics for a service (this doesn't get the characteristic value)
   *
   * @param {object} deviceHandle - Bluetooth device handle
   * @param {object} service - The service to read characteristics from
   * @param {function} success - success callback
   * @param {function} failure - failure callback
   *
   */
  this.readCharacteristics = function(deviceHandle, service, success, failure) {
    return typeof evothings !== "undefined" && evothings !== null ? evothings.ble.characteristics(deviceHandle, service.handle, function(characteristics) {
      debugService.log('Bluetooth reading charaterisitics success', TAG, {
        numberOfCharacteristics: characteristics.length
      });
      return success(characteristics);
    }, function(errorCode) {
      debugService.error('Bluetooth reading characteristics error: ' + errorCode, TAG);
      return failure(new BluetoothReadCharacteristicsError(errorCode));
    }) : void 0;
  };

  /*
   *
   * @description Read a bluetooth characteristic value
   *
   * @param {object} deviceHandle - Bluetooth device handle
   * @param {object} characteristic - The characteristic to read from
   * @param {function} success - success callback
   * @param {function} failure - failure callback
   *
   */
  this.readCharacteristic = function(deviceHandle, characteristic, success, failure) {
    return typeof evothings !== "undefined" && evothings !== null ? evothings.ble.readCharacteristic(deviceHandle, characteristic.handle, function(characteristicValue) {
      if (characteristicValue.readByte(0) !== 0) {
        return success(characteristicValue);
      } else {
        debugService.warn('Bluetooth read characteristic first byte is null');
        return null;
      }
    }, function(errorCode) {
      debugService.error('Bluetooth read characteristic error: ' + errorCode, TAG);
      return failure(new BluetoothReadError(errorCode));
    }) : void 0;
  };

  /*
   *
   * @description Writes a bluetooth characteristic value
   *
   * @param {object} deviceHandle - Bluetooth device handle
   * @param {object} characteristic - The characteristic to write to
   * @param {string} value - The value to write
   * @param {function} success - success callback
   * @param {function} failure - failure callback
   * @param {string} format - either 'raw' or 'proto' (default).  The underlying
   *   evothings.ble library doesn't care about the last parameter, but it enables
   *   better handling of the messages when in simulator mode.
   *
   */
  this.writeCharacteristic = function(deviceHandle, characteristic, value, success, failure, format) {
    if (format == null) {
      format = 'proto';
    }
    return this.powerState(function(powerStatus) {
      if (powerStatus === bluetoothStates.poweredOn) {
        return typeof evothings !== "undefined" && evothings !== null ? evothings.ble.writeCharacteristic(deviceHandle, characteristic.handle, value, function() {
          return success();
        }, function(errorCode) {
          debugService.error('Bluetooth writing characteristic error:' + errorCode, TAG);
          return failure(new BluetoothWriteError(errorCode));
        }, format) : void 0;
      } else {
        debugService.warn("Bluetooth power error " + powerStatus, TAG);
        return failure(new BluetoothPowerError('Bluetooth power error', powerStatus));
      }
    }, function(error) {
      return failure(error);
    });
  };

  /*
   *
   * @description Subscribe to be notified of characteristic updates,  The characteristic should allow notify or indicate
   * Every time a notification is received it will read the read characteristic value
   * This is under the assumption that the subscribe notification won't contain any data
   * The success function is called every time a notification is received.
   *
   * @param {object} deviceHandle - Bluetooth device handle
   * @param {object} characteristic - The characteristic to enable notification on
   * @param {function} success - success callback that is called every time the value changes
   * @param {function} failure - failure callback
   *
   */
  this.subscribe = function(deviceHandle, characteristic, success, failure) {
    debugService.log('Bluetooth subscribe', TAG);
    return typeof evothings !== "undefined" && evothings !== null ? evothings.ble.enableNotification(deviceHandle, characteristic.handle, function(characteristicValue) {
      return success(characteristicValue);
    }, function(errorCode) {
      debugService.error('Bluetooth subscribe error:' + errorCode, TAG);
      if (failure != null) {
        return failure(new BluetoothSubscribeError(errorCode));
      }
    }) : void 0;
  };

  /*
   *
   * @description Unsubscribe to a characteristic.  This stops listening for updates.
   * It can be called on incorrect characterstiics as well as idempotent so it can be called multiple times
   *
   * @param {object} deviceHandle - Bluetooth device handle
   * @param {object} characteristic - The characteristic to disable notification on
   * @param {function} success - success callback
   * @param {function} failure - failure callback
   *
   */
  this.unsubscribe = function(deviceHandle, characteristic, success, failure) {
    debugService.log('Bluetooth unsubscribe', TAG);
    return typeof evothings !== "undefined" && evothings !== null ? evothings.ble.disableNotification(deviceHandle, characteristic.handle, function() {
      debugService.log('Bluetooth unsubscribe success', TAG);
      if (success != null) {
        return success();
      }
    }, function(errorCode) {
      debugService.error('Bluetooth unsubscribe error: ' + errorCode, TAG);
      if (failure != null) {
        return failure(new BluetoothUnsubscribeError(errorCode));
      }
    }) : void 0;
  };

  /*
   *
   * @description Retreive bluetooth power status
   *
   * @param {function} success - success callback
   * @param {function} failure - failure callback
   *
   */
  this.powerState = function(success, failure) {
    return typeof evothings !== "undefined" && evothings !== null ? evothings.ble.powerStatus((function(_this) {
      return function(powerStatus) {
        var state;
        state = powerStatus.state;
        if (state === bluetoothStates.unknown && powerStateRetry < 5) {
          powerStateRetry++;
          debugService.debug('Bluetooth retrying power state #: ' + powerStateRetry, TAG);
          return $timeout(function() {
            return _this.powerState(success, failure);
          }, 500);
        } else {
          powerStateRetry = 0;
          return success(state);
        }
      };
    })(this), function(errorCode) {
      debugService.warn("Power Status error " + errorCode, TAG);
      powerStateRetry = 0;
      return failure(new BluetoothPowerError("Power Status error " + errorCode, null));
    }) : void 0;
  };

  /*
   *
   * @description Retreive bluetooth power status in the form of a promise
   *
   */
  this.powerStatePromise = function() {
    return $window.Q.Promise((function(_this) {
      return function(resolve, reject) {
        return _this.powerState(function(powerStatus) {
          if (powerStatus === bluetoothStates.poweredOn) {
            return resolve();
          } else {
            debugService.warn("Bluetooth power error " + powerStatus, TAG);
            return reject(new BluetoothPowerError('Bluetooth power error', powerStatus));
          }
        }, function(error) {
          return reject(error);
        });
      };
    })(this));
  };

  /*
   *
   * @description Fetch the remove device's RSSI
   *
   * @param {object} deviceHandle - Bluetooth device handle
   *
   */
  this.rssi = function(deviceHandle) {
    return $window.Q.promise(function(resolve, reject) {
      return typeof evothings !== "undefined" && evothings !== null ? evothings.ble.rssi(deviceHandle, function(rssi) {
        return resolve(rssi);
      }, function(errorCode) {
        return reject(errorCode);
      }) : void 0;
    });
  };
  this.permissions = function() {
    return typeof evothings !== "undefined" && evothings !== null ? evothings.ble.permission : void 0;
  };
  this.properties = function() {
    return typeof evothings !== "undefined" && evothings !== null ? evothings.ble.property : void 0;
  };
  this.writeTypes = function() {
    return typeof evothings !== "undefined" && evothings !== null ? evothings.ble.writeType : void 0;
  };
  return this;
}]);

this.app.service('circulatorConnectionError', ["$ionicHistory", "$state", "$window", "$ionicLoading", "bluetoothStates", "BluetoothPowerError", "BluetoothScanError", "NoInternetError", "NoCirculatorError", "NonOwnerError", "NullOwnerError", "debugService", "locale", "alertService", "circulatorManager", "circulatorConnectionStates", "faqLinkConfig", "connectionProvidersConfig", "disconnectReasons", "appConfig", function($ionicHistory, $state, $window, $ionicLoading, bluetoothStates, BluetoothPowerError, BluetoothScanError, NoInternetError, NoCirculatorError, NonOwnerError, NullOwnerError, debugService, locale, alertService, circulatorManager, circulatorConnectionStates, faqLinkConfig, connectionProvidersConfig, disconnectReasons, appConfig) {
  var TAG, handleBluetoothError, handleConnectingWithPopup, handleErrorWithPopup, handleOtherError, handleRepair;
  TAG = 'CirculatorConnectionErrorService';
  handleBluetoothError = function(error) {
    var handleable, templateOptions;
    templateOptions = null;
    handleable = false;
    if (error instanceof BluetoothPowerError) {
      handleable = true;
      templateOptions = (function() {
        switch (error.bluetoothPowerState) {
          case bluetoothStates.poweredOff:
            return {
              title: locale.getString('popup.bluetoothOffTitle'),
              description: locale.getString('popup.bluetoothOffDescription')
            };
          case bluetoothStates.resetting:
            return {
              title: locale.getString('popup.bluetoothResettingTitle'),
              description: locale.getString('popup.bluetoothResettingDescription')
            };
          case bluetoothStates.unauthorized:
            return {
              title: locale.getString('popup.bluetoothUnauthorizedTitle'),
              desciption: locale.getString('popup.bluetoothUnauthorizedDescription')
            };
          case bluetoothStates.unsupported:
            return {
              title: locale.getString('popup.bluetoothUnsupportedTitle'),
              description: locale.getString('popup.bluetoothUnsupportedDescription')
            };
          default:
            return {
              title: locale.getString('popup.bluetoothUnknownErrorTitle'),
              description: locale.getString('popup.bluetoothUnknownErrorDescription')
            };
        }
      })();
    } else if (error instanceof BluetoothScanError) {
      handleable = true;
      templateOptions = {
        title: locale.getString('popup.bluetoothScanErrorTitle'),
        description: locale.getString('popup.bluetoothScanErrorDescription')
      };
    }
    return {
      templateOptions: templateOptions,
      handleable: handleable
    };
  };
  handleOtherError = function(error) {
    var handleable, templateOptions;
    templateOptions = null;
    handleable = false;
    if (error instanceof NoInternetError) {
      handleable = true;
      templateOptions = {
        title: locale.getString('popup.noInternetTitle'),
        description: locale.getString('popup.noInternetDescription')
      };
    } else if (error instanceof NoCirculatorError) {
      handleable = true;
      templateOptions = {
        title: locale.getString('popup.noCirculatorTitle'),
        description: locale.getString('popup.noCirculatorDescription')
      };
    } else if (error instanceof NonOwnerError) {
      handleable = true;
      templateOptions = {
        title: locale.getString('popup.wifiUnavailableNotOwnerTitle'),
        description: locale.getString('popup.wifiUnavailableNotOwnerDescription'),
        headerColor: 'alert-yellow',
        icon: 'fail',
        link: faqLinkConfig.noOwnerNoWifi
      };
    } else if (error instanceof NullOwnerError) {
      handleable = true;
      templateOptions = {
        title: locale.getString('popup.wifiUnavailableNullOwnerTitle'),
        description: locale.getString('popup.wifiUnavailableNullOwnerDescription')
      };
    }
    return {
      templateOptions: templateOptions,
      handleable: handleable
    };
  };
  handleErrorWithPopup = function(error) {
    return $window.Q.promise(function(resolve, reject) {
      var handleable, ref, ref1, templateOptions;
      ref = handleBluetoothError(error), templateOptions = ref.templateOptions, handleable = ref.handleable;
      if (!handleable) {
        ref1 = handleOtherError(error), templateOptions = ref1.templateOptions, handleable = ref1.handleable;
      }
      if (handleable) {
        debugService.log('Handling circulator connection error with popup', TAG, {
          error: error,
          templateOptions: templateOptions
        });
        alertService.alert({
          headerColor: templateOptions.headerColor || 'alert-red',
          icon: templateOptions.icon || 'fail',
          titleString: templateOptions.title,
          bodyString: templateOptions.description,
          link: templateOptions.link || null
        });
        return resolve(error);
      } else {
        debugService.log('Not handling circulator connection error with popup', TAG, {
          error: error
        });
        return reject(error);
      }
    });
  };
  handleConnectingWithPopup = function() {
    return alertService.confirm({
      headerColor: 'alert-yellow',
      icon: 'connecting-joule',
      titleString: locale.getString('popup.connectingToJouleTitle'),
      bodyString: locale.getString('popup.connectingToJouleMessage', {
        name: circulatorManager.getCurrentCirculatorName()
      }),
      cancelText: locale.getString('popup.connectingToJouleTroubleshootButton'),
      okText: locale.getString('popup.connectingToJouleKeepLookingButton')
    }).then(function(keepLooking) {
      if (keepLooking) {
        return debugService.log('User selected keep looking', TAG);
      } else {
        debugService.log('User selected troubleshoot', TAG);
        return $state.go('connectionTroubleshooting');
      }
    });
  };
  handleRepair = function() {
    debugService.log('Handling repair', TAG);
    $ionicLoading.show({
      template: "<div class='loading-indicator' />",
      noBackdrop: true
    });
    return $state.go(appConfig.defaultView).then(function() {
      return circulatorManager.unpair()["catch"](function(error) {
        return debugService.error('Unpair has failed', TAG, {
          error: error
        });
      })["finally"](function() {
        debugService.log('Unpair has finished', TAG);
        $ionicLoading.hide();
        return $state.go('pairingSequencePrompt');
      }).done(_.noop, function(e) {
        return debugService.onPromiseUnhandledRejection(e, TAG);
      });
    });
  };
  this.handleErrorWithPopup = handleErrorWithPopup;
  this.handleConnectingWithPopup = handleConnectingWithPopup;
  this.handleRepair = handleRepair;
  this.handleBluetoothError = handleBluetoothError;
  return this;
}]);

this.app.service('circulatorEventDialogConfigProvider', ["$window", "locale", "circulatorEventTypes", "circulatorEventReasons", "$ionicHistory", "$state", "appConfig", function($window, locale, circulatorEventTypes, circulatorEventReasons, $ionicHistory, $state, appConfig) {
  var configMap, isUnexpectedProgramStop, obj, obj1, obj2, obj3;
  configMap = (
    obj = {},
    obj["" + circulatorEventTypes.STOP_PROGRAM] = (
      obj1 = {},
      obj1["" + circulatorEventReasons.BUTTON_PRESSED] = {
        strings: 'BUTTON_PRESSED_strings',
        headerColor: 'alert-green',
        icon: 'fail',
        recoverable: true,
        waitForClearToResume: true,
        onDismiss: function() {
          $ionicHistory.nextViewOptions({
            disableBack: true,
            historyRoot: true
          });
          return $state.go(appConfig.defaultView);
        }
      },
      obj1["" + circulatorEventReasons.TIPPED_OVER] = {
        strings: 'TIPPED_OVER_strings',
        headerColor: 'alert-red',
        icon: 'fail',
        recoverable: true,
        waitForClearToResume: true
      },
      obj1["" + circulatorEventReasons.OVERHEATING] = {
        strings: 'OVERHEATING_strings',
        headerColor: 'alert-red',
        icon: 'fail',
        recoverable: true,
        waitForClearToResume: false
      },
      obj1["" + circulatorEventReasons.HARDWARE_FAILURE] = {
        strings: 'HARDWARE_FAILURE_strings',
        headerColor: 'alert-red',
        icon: 'fail',
        recoverable: false
      },
      obj1["" + circulatorEventReasons.LOW_WATER_LEVEL] = {
        strings: 'LOW_WATER_LEVEL_strings',
        headerColor: 'alert-red',
        icon: 'fail',
        recoverable: true,
        waitForClearToResume: false
      },
      obj1["" + circulatorEventReasons.POWER_LOSS] = {
        strings: 'POWER_LOSS_strings',
        headerColor: 'alert-red',
        icon: 'fail',
        recoverable: false
      },
      obj1
    ),
    obj["" + circulatorEventTypes.INITIALIZATION_FAILURE] = (
      obj2 = {},
      obj2["" + circulatorEventReasons.OVERHEATING] = {
        strings: 'OVERHEATING_strings',
        headerColor: 'alert-red',
        icon: 'fail'
      },
      obj2["" + circulatorEventReasons.HARDWARE_FAILURE] = {
        strings: 'HARDWARE_FAILURE_strings',
        headerColor: 'alert-red',
        icon: 'fail'
      },
      obj2
    ),
    obj["" + circulatorEventTypes.UNKNOWN_TYPE] = (
      obj3 = {},
      obj3["" + circulatorEventReasons.UNKNOWN_REASON] = {
        strings: 'UNKNOWN_REASON_strings',
        headerColor: 'alert-red',
        icon: 'fail',
        recoverable: true
      },
      obj3
    ),
    obj
  );
  isUnexpectedProgramStop = function(eventType, eventReason) {
    if (eventType !== circulatorEventTypes.STOP_PROGRAM) {
      return false;
    } else if (eventReason === circulatorEventReasons.BUTTON_PRESSED) {
      return false;
    } else {
      return true;
    }
  };
  this.getPopupConfig = function(eventType, eventReason) {
    var alertOptions, eventConfig, ref, stringsPath;
    eventConfig = (ref = configMap[eventType]) != null ? ref[eventReason] : void 0;
    if (eventConfig == null) {
      return;
    }
    stringsPath = "circulatorError." + eventConfig.strings;
    alertOptions = {
      type: 'alert',
      headerColor: eventConfig.headerColor,
      icon: eventConfig.icon,
      sound: true,
      titleString: locale.getString(stringsPath + ".titleString"),
      bodyString: locale.getString(stringsPath + ".bodyString"),
      cancelText: locale.getString('circulatorError.cancelText'),
      onDismiss: eventConfig.onDismiss ? eventConfig.onDismiss : function() {}
    };
    if (isUnexpectedProgramStop(eventType, eventReason)) {
      if (eventType === circulatorEventTypes.STOP_PROGRAM) {
        _.extend(alertOptions, {
          okText: locale.getString('circulatorError.okText'),
          type: 'confirm'
        });
      }
    }
    return alertOptions;
  };
  this.getErrorOverlayConfig = function(eventType, eventReason) {
    var config, eventConfig, ref, stringsPath;
    eventConfig = (ref = configMap[eventType]) != null ? ref[eventReason] : void 0;
    if (eventConfig == null) {
      return;
    }
    stringsPath = "circulatorError." + eventConfig.strings;
    config = {
      shortTitleString: locale.getString(stringsPath + ".shortTitleString"),
      shortBodyString: locale.getString(stringsPath + ".shortBodyString"),
      recoverable: eventConfig.recoverable,
      waitForClearToResume: eventConfig.waitForClearToResume
    };
    return config;
  };
  return this;
}]);

this.app.service('circulatorManager', ["BluetoothConnectionProvider", "WebSocketConnectionProvider", "routingConfig", "debugService", "circulatorStorage", "csConfig", "CirculatorProgram", "$window", "$ionicPlatform", "circulatorConnectionStates", "cookStates", "appConfig", "$timeout", "bluetooth", "networkStateService", "NoCirculatorError", "NoInternetError", "circulatorConnectingMaybeStates", "connectionProvidersConfig", "cacheService", function(BluetoothConnectionProvider, WebSocketConnectionProvider, routingConfig, debugService, circulatorStorage, csConfig, CirculatorProgram, $window, $ionicPlatform, circulatorConnectionStates, cookStates, appConfig, $timeout, bluetooth, networkStateService, NoCirculatorError, NoInternetError, circulatorConnectingMaybeStates, connectionProvidersConfig, cacheService) {
  var TAG, applicationAddress, bathTemperatureState, bathTemperatureUpdateHandlers, bindCurrentCirculatorListeners, bindHandler, bindMaybeDisconnectedListener, bindMaybeStateListeners, callHandlers, checkOwnership, circulatorConnectingMaybeState, circulatorConnectionState, circulatorConnectionUpdateHandlers, circulatorCookState, circulatorCookUpdateHandlers, circulatorErrorState, circulatorErrorUpdateHandlers, circulatorTimestamp, circulatorTimestampUpdateHandlers, cleanUpConnectionProviders, connectionProviders, currentCirculator, currentCirculatorWifiStatus, existingCirculatorScanPromise, existingCirculatorScanSessionPromise, getAndSetIsOwner, initializeSdk, isOwnerOfCirculator, latestCirculatorScanSessionResult, onApplicationPause, onApplicationResume, programState, programStepState, programStepUpdateHandlers, programUpdateHandlers, scanForCirculators, scanTimeout, sdkCirculatorManager, setBathTemperatureState, setCirculatorConnectingMaybeDisconnected, setCirculatorConnectingMaybeState, setCirculatorConnectionState, setCirculatorConnectionStateDebounced, setCirculatorCookState, setCirculatorErrorState, setCirculatorTimestamp, setCurrentCirculator, setIsOwner, setProgramState, setProgramStepState, setTimeRemainingState, timeRemainingState, timeRemainingUpdateHandlers, tokenToApplicationAddress, unbindCurrentCirculatorListeners, unbindHandler, unbindMaybeDisconnectedListener;
  TAG = 'AppCirculatorManager';
  sdkCirculatorManager = null;
  connectionProviders = [];
  applicationAddress = null;
  cleanUpConnectionProviders = function() {
    return _.each(connectionProviders, function(provider) {
      return provider.cleanUp();
    });
  };
  tokenToApplicationAddress = function(token) {
    var results;
    results = token.split('.');
    return JSON.parse(atob(results[1])).a;
  };
  initializeSdk = function(token) {
    debugService.log('Initializing sdkCirculatorManager', TAG);
    applicationAddress = token != null ? tokenToApplicationAddress(token) : 'aabbaabbaabbaabb';
    connectionProviders = [new WebSocketConnectionProvider(applicationAddress), new BluetoothConnectionProvider()];
    return sdkCirculatorManager = new $window.CirculatorSDK.CirculatorManager({
      routingConfig: routingConfig,
      connectionProviders: connectionProviders,
      logger: debugService.getLogger(),
      callerAddress: applicationAddress,
      localCirculatorStorage: circulatorStorage,
      apiConfig: csConfig,
      userAuthenticationToken: token
    });
  };
  this.getApplicationAddress = function() {
    return applicationAddress;
  };

  /*
   *
   * Circulator Scanning and Pairing Methods
   *
   */
  currentCirculator = null;
  setCurrentCirculator = function(circulator) {
    if (currentCirculator) {
      unbindCurrentCirculatorListeners();
    }
    if (circulator == null) {
      debugService.log('Setting current circulator to null', TAG);
      setCirculatorConnectingMaybeState(circulatorConnectingMaybeStates.maybeDisconnected);
      setCirculatorConnectionState(circulatorConnectionStates.unpaired);
      setCirculatorCookState(null);
      setBathTemperatureState(null);
      setTimeRemainingState(null);
      setCirculatorErrorState(null);
      setCirculatorTimestamp(null);
      setProgramStepState(null);
      setProgramState(null);
      return currentCirculator = null;
    } else {
      debugService.log('Setting current circulator', TAG, {
        circulatorAddress: circulator.address,
        circulatorName: circulator.name
      });
      currentCirculator = circulator;
      currentCirculator.setActive(true);
      setCirculatorCookState(currentCirculator.circulatorState);
      setBathTemperatureState(currentCirculator.data.bathTemp);
      setTimeRemainingState(currentCirculator.data.timeRemaining);
      setCirculatorErrorState(currentCirculator.data.errorState);
      setCirculatorTimestamp(currentCirculator.data.timestamp);
      setProgramStepState(currentCirculator.data.programStep);
      setProgramState(currentCirculator.program);
      bindCurrentCirculatorListeners();
      return bindMaybeStateListeners();
    }
  };

  /*
   *
   * Circulator Connecting Maybe State
   *
   */
  setCirculatorConnectingMaybeDisconnected = function() {
    return setCirculatorConnectingMaybeState(circulatorConnectingMaybeStates.maybeDisconnected);
  };
  unbindMaybeDisconnectedListener = function() {
    return currentCirculator.removeListener('disconnected', setCirculatorConnectingMaybeDisconnected);
  };
  bindMaybeDisconnectedListener = function() {
    return currentCirculator.on('disconnected', setCirculatorConnectingMaybeDisconnected);
  };
  bindMaybeStateListeners = function() {
    currentCirculator.on('state:cooking', function() {
      return setCirculatorConnectingMaybeState(circulatorConnectingMaybeStates.maybeCooking);
    });
    currentCirculator.on('state:idle', function() {
      return setCirculatorConnectingMaybeState(circulatorConnectingMaybeStates.maybeIdle);
    });
    return bindMaybeDisconnectedListener();
  };

  /*
   *
   * Private Low Level Scan Method
   *
   * Gaurds against multiple concurrent scans by returning a reference to an existing scan promise if scan is in progress
   *
   * Note: Do not call this method if the app is already paired with a circulator
   * Please see https://chefsteps.atlassian.net/browse/MOBCOOK-1933
   *
   */
  scanTimeout = null;
  existingCirculatorScanPromise = null;
  scanForCirculators = function() {
    var clearScanTimeout;
    clearScanTimeout = function() {
      debugService.debug('Clearing scan timeout', TAG);
      if (scanTimeout != null) {
        $timeout.cancel(scanTimeout);
      }
      return scanTimeout = null;
    };
    debugService.log('Scanning for circulators', TAG);
    if (existingCirculatorScanPromise == null) {
      debugService.debug('New scan promise', TAG);
      clearScanTimeout();
      existingCirculatorScanPromise = sdkCirculatorManager.discoverCirculators().then(function(circulatorCandidates) {
        var timeout;
        debugService.log('CirculatorManager scan succeeded', TAG);
        if (_.isEmpty(circulatorCandidates)) {
          timeout = appConfig.scanIntervalMilliseconds;
          debugService.warn("Scan has timed out without finding any circulator, scheduling scan again in " + timeout + " milliseconds", TAG);
          scanTimeout = $timeout(function() {
            if (currentCirculator == null) {
              return scanForCirculators();
            }
          }, timeout);
        } else {
          if (currentCirculator == null) {
            setCirculatorConnectionState(circulatorConnectionStates.jouleFound);
          }
        }
        return circulatorCandidates;
      }).progress(function(circulatorCandidate) {
        debugService.log('CirculatorManager circulator detected', TAG);
        return circulatorCandidate.once('paired', function(circulatorClient) {
          return setCurrentCirculator(circulatorClient);
        });
      })["catch"](function(error) {
        debugService.warn('Circulator scan failed', TAG, {
          error: error
        });
        throw error;
      })["finally"](function() {
        return existingCirculatorScanPromise = null;
      });
    }
    return existingCirculatorScanPromise;
  };

  /*
   *
   * Public Scan Method
   *
   * Guards against multiple concurrent scans by returning a reference to an existing scan promise if scan is in progress
   *
   * Creates a new scan session, with a reference keeping track of the latest result
   *
   * If one caller calls this method, another caller can later call the Get Results method
   *
   * The results reset every time a new session is started
   *
   * Note: Do not call this method if the app is already paired with a circulator
   * Please see https://chefsteps.atlassian.net/browse/MOBCOOK-1933
   *
   */
  latestCirculatorScanSessionResult = {};
  this.getLatestCirculatorScanSessionResult = function() {
    return latestCirculatorScanSessionResult;
  };
  existingCirculatorScanSessionPromise = null;
  this.createCirculatorScanSession = function() {
    if (existingCirculatorScanSessionPromise == null) {
      debugService.log('Create a new circulator scan session', TAG);
      latestCirculatorScanSessionResult = {};
      existingCirculatorScanSessionPromise = scanForCirculators().then(function(circulatorCandidates) {
        latestCirculatorScanSessionResult = circulatorCandidates;
        return latestCirculatorScanSessionResult;
      })["finally"](function() {
        return existingCirculatorScanSessionPromise = null;
      });
    }
    return existingCirculatorScanSessionPromise;
  };
  this.scanForEndpoints = function() {
    var provider;
    debugService.log('Scan for endpoints', TAG);
    provider = _.find(connectionProviders, {
      type: connectionProvidersConfig.bluetooth.type
    });
    return provider.discover();
  };
  this.findLastAccessedCirculator = (function(_this) {
    return function() {
      debugService.log('Finding last accessed circulator', TAG);
      setCirculatorConnectionState(circulatorConnectionStates.connecting);
      return sdkCirculatorManager.findLastAccessedCirculator().then(function(lastAccessedCirculator) {
        var isJouleFound, newCirculatorConnectionState;
        if (lastAccessedCirculator) {
          debugService.log('Found a last accessed circulator', TAG);
          setCurrentCirculator(lastAccessedCirculator);
          return cacheService.set('usageHistory', 'hasEverPaired', true);
        } else {
          isJouleFound = _this.getCirculatorConnectionState() === circulatorConnectionStates.jouleFound;
          newCirculatorConnectionState = isJouleFound ? circulatorConnectionStates.jouleFound : circulatorConnectionStates.unpaired;
          setCirculatorConnectionState(newCirculatorConnectionState);
          return debugService.log('Did not find a last accessed circulator', TAG);
        }
      })["catch"](function(error) {
        setCirculatorConnectionState(circulatorConnectionStates.unpaired);
        return debugService.error('Find last accessed circulator failed', TAG, {
          error: error
        });
      });
    };
  })(this);
  this.onUserSignIn = function(token) {
    debugService.log('User has signed in', TAG);
    initializeSdk(token);
    return this.findLastAccessedCirculator();
  };
  this.onUserSkipSignIn = function() {
    debugService.log('User has skipped sign in', TAG);
    initializeSdk(null);
    return setCirculatorConnectionState(circulatorConnectionStates.unpaired);
  };
  this.onUserSignOut = function() {
    debugService.log('User has signed out', TAG);
    setCirculatorConnectionState(circulatorConnectionStates.unpaired);
    if (currentCirculator) {
      debugService.log('Removing circulators from local storage', TAG);
      sdkCirculatorManager.removeCirculator(currentCirculator, false);
      setCurrentCirculator(null);
    }
    cleanUpConnectionProviders();
    initializeSdk(null);
    circulatorStorage.removeAll();
    return setIsOwner(null);
  };
  this.pair = function(candidate) {
    var pairPromise;
    debugService.log('Pair', TAG);
    setCirculatorConnectionState(circulatorConnectionStates.connecting);
    pairPromise = candidate.pair();
    pairPromise.then(function() {
      setCirculatorConnectionState(circulatorConnectionStates.connected);
      return cacheService.set('usageHistory', 'hasEverPaired', true);
    })["catch"](function(error) {
      setCirculatorConnectionState(circulatorConnectionStates.unpaired);
      debugService.error('pair candidate failed', TAG, {
        error: error
      });
      throw error;
    });
    return {
      pairPromise: pairPromise,
      openPromise: pairPromise.open
    };
  };
  this.unpair = function() {
    return $window.Q.promise((function(_this) {
      return function(resolve) {
        debugService.log('Unpair', TAG);
        cacheService.removeAll('neverShowNagAlert');
        if (currentCirculator != null) {
          currentCirculator.setActive(false);
          return _this.getIsOwner().then(function(isOwner) {
            if (isOwner) {
              return currentCirculator.disconnectAccessPoint()["catch"](function(error) {
                return debugService.warn('Disconnect access point has failed during unpair', TAG, {
                  error: error
                });
              });
            } else {
              return null;
            }
          })["finally"](function() {
            sdkCirculatorManager.removeCirculator(currentCirculator);
            cleanUpConnectionProviders();
            setCurrentCirculator(null);
            setIsOwner(null);
            return resolve();
          }).done(_.noop, function(e) {
            return debugService.onPromiseUnhandledRejection(e, TAG);
          });
        } else {
          setIsOwner(null);
          return resolve();
        }
      };
    })(this));
  };

  /*
   *
   * Checks if the current user is the owner of the current circulator
   * Returns true if is owner, false otherwise
   *
   */
  isOwnerOfCirculator = null;
  this.getIsOwner = function() {
    if (isOwnerOfCirculator != null) {
      return $window.Q.resolve(isOwnerOfCirculator);
    } else {
      return getAndSetIsOwner();
    }
  };
  getAndSetIsOwner = function() {
    return checkOwnership().then(function(isOwner) {
      if (isOwner != null) {
        setIsOwner(isOwner);
      }
      return isOwner;
    });
  };
  checkOwnership = function() {
    if (networkStateService.noInternet()) {
      return $window.Q.reject(new NoInternetError('No internet'));
    } else {
      if (currentCirculator != null) {
        debugService.log('Check if user is the owner of the circulator by calling getFromRemoteStorage', TAG);
        return sdkCirculatorManager.circulatorStorage.getFromRemoteStorage().then(function(connectionData) {
          var matchingCirculator;
          matchingCirculator = connectionData[currentCirculator.address];
          if (matchingCirculator != null) {
            debugService.log('User is the owner of circulator', TAG);
            return true;
          } else {
            debugService.log('Saving to storage to determine ownership', TAG);
            return sdkCirculatorManager.circulatorStorage.saveToRemoteStorage(currentCirculator).then(function() {
              debugService.log('User is the owner of circulator', TAG);
              return true;
            })["catch"](function(error) {
              if (error instanceof $window.CirculatorSDK.DuplicateCirculatorIdError) {
                debugService.log('User is not the owner of circulator', TAG, {
                  error: error
                });
                return false;
              } else {
                debugService.error('Unable to determine if user is the owner of circulator', TAG, {
                  error: error
                });
                return null;
              }
            });
          }
        });
      } else {
        return $window.Q.reject(new NoCirculatorError('No connected circulator'));
      }
    }
  };
  setIsOwner = function(value) {
    return isOwnerOfCirculator = value;
  };
  this.resetAllConnections = function() {
    debugService.log('Reset all connections', TAG);
    currentCirculator.resetAllConnections();
    return $window.Q.delay(3000);
  };

  /*
   *
   * Application Pause and Resume
   *
   * Methods to handle disconnecting and connecting to circulator when app
   * is opened, backgrounded, dismissed, or closed
   *
   */
  onApplicationResume = function() {
    debugService.log('Application resume', TAG);
    if (!sdkCirculatorManager) {
      return;
    }
    if (!currentCirculator) {
      return scanForCirculators();
    } else {
      bindMaybeDisconnectedListener();
      setCirculatorConnectionState(circulatorConnectionStates.connecting);
      return currentCirculator.setActive(true);
    }
  };
  onApplicationPause = function() {
    debugService.log('Application pause', TAG);
    if (currentCirculator) {
      unbindMaybeDisconnectedListener();
      return currentCirculator.setActive(false);
    }
  };
  $ionicPlatform.on('pause', onApplicationPause);
  $ionicPlatform.on('resume', onApplicationResume);

  /*
   *
   * Circulator Wi-Fi Access Point Connection Methods
   *
   */
  this.listAccessPoints = function() {
    if (currentCirculator != null) {
      return currentCirculator.listAccessPoints();
    } else {
      return $window.Q.reject(new NoCirculatorError('No connected circulator'));
    }
  };
  this.stopListAccessPoints = function() {
    if (currentCirculator != null) {
      return currentCirculator.stopListAccessPoints();
    } else {
      return $window.Q.reject(new NoCirculatorError('No connected circulator'));
    }
  };
  this.connectAccessPoint = function(options) {
    var apiClient, bearerToken, password, securityType, ssid;
    if (currentCirculator != null) {
      ssid = options.ssid, password = options.password, securityType = options.securityType;
      apiClient = sdkCirculatorManager.circulatorStorage.circulatorApi;
      bearerToken = null;
      return bluetooth.powerStatePromise().then(function() {
        debugService.log('Getting bearer token from api', TAG);
        return apiClient.getCirculatorToken(currentCirculator.address);
      }).then(function(token) {
        return bearerToken = token;
      }).then(function() {
        debugService.log('Calling connectAccessPoint', TAG);
        return currentCirculator.connectAccessPoint(ssid, password, securityType)["catch"](function(error) {
          if (error instanceof $window.CirculatorSDK.FirmwareBusyError) {
            debugService.warn('Firmware is busy, will retry connectAccessPoint', TAG);
            return currentCirculator.connectAccessPoint(ssid, password, securityType);
          } else {
            throw error;
          }
        });
      }).then(function() {
        debugService.log('Setting bearer token on circulator', TAG, {
          circulatorTokenMiddle: bearerToken.split('.')[1]
        });
        return currentCirculator.setCirculatorToken(bearerToken);
      })["catch"](function(error) {
        debugService.warn('Got error calling connectAccessPoint.  Disconnecting.', TAG, {
          error: error
        });
        return currentCirculator.disconnectAccessPoint()["finally"](function() {
          throw error;
        });
      });
    } else {
      return $window.Q.reject(new NoCirculatorError('No connected circulator'));
    }
  };
  currentCirculatorWifiStatus = null;
  this.getCurrentCirculatorWifiStatus = function() {
    return currentCirculatorWifiStatus;
  };
  this.getWifiStatus = function() {
    if (currentCirculator != null) {
      return currentCirculator.getWifiStatus().then(function(result) {
        currentCirculatorWifiStatus = result;
        return result;
      })["catch"](function(error) {
        if (_.some([error instanceof $window.CirculatorSDK.FirmwareBusyError, error instanceof $window.CirculatorSDK.TimeoutError])) {
          debugService.warn('Firmware is busy, will retry getWifiStatus', TAG);
          return currentCirculator.getWifiStatus();
        } else {
          throw error;
        }
      });
    } else {
      return $window.Q.reject(new NoCirculatorError('No connected circulator'));
    }
  };
  this.disconnectAccessPoint = function() {
    if (currentCirculator != null) {
      return bluetooth.powerStatePromise().then(function() {
        return currentCirculator.disconnectAccessPoint()["catch"](function(error) {
          if (_.some([error instanceof $window.CirculatorSDK.FirmwareBusyError, error instanceof $window.CirculatorSDK.TimeoutError])) {
            debugService.warn('Firmware is busy, will retry disconnectAccessPoint', TAG);
            return currentCirculator.disconnectAccessPoint();
          } else {
            throw error;
          }
        });
      }).then((function(_this) {
        return function() {
          return _this.getWifiStatus();
        };
      })(this));
    } else {
      return $window.Q.reject(new NoCirculatorError('No connected circulator'));
    }
  };

  /*
   *
   * Circulator Identify
   *
   * Get info about the current circulator like name, firmware version
   *
   */
  this.identify = function() {
    debugService.log("Calling identify(), currentCirculator set? " + (currentCirculator != null), TAG);
    if (currentCirculator != null) {
      return currentCirculator.identify();
    } else {
      return $window.Q.reject(new NoCirculatorError('No connected circulator'));
    }
  };

  /*
   *
   * Circulator Cook Control Methods
   *
   */
  this.renameCirculator = function(newName) {
    return currentCirculator.rename(newName)["catch"](function(error) {
      debugService.error('Rename failed on first attempt, will retry', TAG, {
        error: error
      });
      return currentCirculator.rename(newName);
    });
  };
  this.dropFood = function() {
    return currentCirculator.dropFood()["catch"](function(error) {
      debugService.error('Drop food failed on first attempt, will retry', TAG, {
        error: error
      });
      return currentCirculator.dropFood();
    });
  };
  this.stopProgram = function() {
    return currentCirculator.stopProgram()["catch"](function(error) {
      debugService.error('Stop program failed on first attempt, will retry', TAG, {
        error: error
      });
      return $window.Q.delay(5000).then(function() {
        return currentCirculator.stopProgram();
      });
    });
  };
  this.startProgram = function(programOptions) {
    var program;
    program = new CirculatorProgram(programOptions);
    if (this.getProgramState() != null) {
      return currentCirculator.updateProgram(program)["catch"](function(error) {
        debugService.error('Update program failed on first attempt, will retry', TAG, {
          error: error
        });
        return $window.Q.delay(5000).then(function() {
          return currentCirculator.updateProgram(program);
        });
      });
    } else {
      return currentCirculator.startProgram(program)["catch"](function(error) {
        debugService.error('Start program failed on first attempt, will retry', TAG, {
          error: error
        });
        return $window.Q.delay(5000).then(function() {
          return currentCirculator.startProgram(program);
        });
      });
    }
  };

  /*
   *
   * Circulator Error Handling Methods
   *
   */
  this.listRecentEvents = function() {
    return currentCirculator.listRecentEvents()["catch"](function(error) {
      debugService.error('List recent events failed on first attempt, will retry', TAG, {
        error: error
      });
      return currentCirculator.listRecentEvents();
    });
  };

  /*
   *
   * Circulator Connection State
   *
   */
  circulatorConnectionUpdateHandlers = {};
  circulatorConnectionState = null;
  setCirculatorConnectionState = function(newState) {
    debugService.log('New Circulator Connection State: ' + newState, TAG, {
      circulatorConnectionState: newState
    });
    circulatorConnectionState = newState;
    return callHandlers(circulatorConnectionUpdateHandlers, circulatorConnectionState);
  };
  setCirculatorConnectionStateDebounced = _.debounce(function(newState) {
    if (currentCirculator != null) {
      return setCirculatorConnectionState(newState);
    }
  }, 1000, {
    leading: false
  });
  this.getCirculatorConnectionState = function() {
    return circulatorConnectionState;
  };
  this.bindCirculatorConnectionUpdateHandler = function(handler) {
    var unbind;
    handler(circulatorConnectionState);
    unbind = bindHandler(circulatorConnectionUpdateHandlers, handler);
    return function() {
      return circulatorConnectionUpdateHandlers = unbind(circulatorConnectionUpdateHandlers);
    };
  };

  /*
   *
   * Circulator Cook State
   *
   */
  circulatorCookState = null;
  circulatorCookUpdateHandlers = {};
  setCirculatorCookState = function(newState) {
    debugService.log('New Circulator Cook State ' + newState, TAG, {
      circulatorCookState: newState
    });
    circulatorCookState = newState;
    return callHandlers(circulatorCookUpdateHandlers, circulatorCookState);
  };
  this.getCirculatorCookState = function() {
    return circulatorCookState;
  };
  this.bindCirculatorCookUpdateHandler = function(handler) {
    var unbind;
    handler(circulatorCookState);
    unbind = bindHandler(circulatorCookUpdateHandlers, handler);
    return function() {
      return circulatorCookUpdateHandlers = unbind(circulatorCookUpdateHandlers);
    };
  };

  /*
   *
   * Bath Temperature State
   *
   */
  bathTemperatureState = null;
  bathTemperatureUpdateHandlers = {};
  setBathTemperatureState = function(newState) {
    debugService.debug('New Bath Temperature State', TAG, {
      bathTemperatureState: newState
    });
    bathTemperatureState = newState;
    return callHandlers(bathTemperatureUpdateHandlers, bathTemperatureState);
  };
  this.getBathTemperatureState = function() {
    return bathTemperatureState;
  };
  this.bindBathTemperatureUpdateHandler = function(handler) {
    var unbind;
    handler(bathTemperatureState);
    unbind = bindHandler(bathTemperatureUpdateHandlers, handler);
    return function() {
      return bathTemperatureUpdateHandlers = unbind(bathTemperatureUpdateHandlers);
    };
  };

  /*
   *
   * Time Remaining State
   *
   */
  timeRemainingState = null;
  timeRemainingUpdateHandlers = {};
  setTimeRemainingState = function(newState) {
    debugService.debug('New Time Remaining State', TAG, {
      timeRemainingState: newState
    });
    timeRemainingState = newState;
    return callHandlers(timeRemainingUpdateHandlers, timeRemainingState);
  };
  this.getTimeRemainingState = function() {
    return timeRemainingState;
  };
  this.bindTimeRemainingUpdateHandler = function(handler) {
    var unbind;
    handler(timeRemainingState);
    unbind = bindHandler(timeRemainingUpdateHandlers, handler);
    return function() {
      return timeRemainingUpdateHandlers = unbind(timeRemainingUpdateHandlers);
    };
  };

  /*
   *
   * Circulator Error State
   *
   */
  circulatorErrorState = null;
  circulatorErrorUpdateHandlers = {};
  setCirculatorErrorState = function(newState) {
    debugService.log('New Circulator Error State', TAG, {
      circulatorErrorState: newState
    });
    circulatorErrorState = newState;
    return callHandlers(circulatorErrorUpdateHandlers, circulatorErrorState);
  };
  this.getCirculatorErrorState = function() {
    return circulatorErrorState;
  };
  this.bindCirculatorErrorUpdateHandler = function(handler) {
    var unbind;
    handler(circulatorErrorState);
    unbind = bindHandler(circulatorErrorUpdateHandlers, handler);
    return function() {
      return circulatorErrorUpdateHandlers = unbind(circulatorErrorUpdateHandlers);
    };
  };

  /*
   *
   * Circulator Timestamp - publisher for subscribers interested in the timestamp of the circulator
   *
   */
  circulatorTimestamp = null;
  circulatorTimestampUpdateHandlers = {};
  setCirculatorTimestamp = function(timestamp) {
    debugService.debug('New Circulator Timestamp', TAG, {
      circulatorTimestamp: timestamp
    });
    circulatorTimestamp = timestamp;
    return callHandlers(circulatorTimestampUpdateHandlers, circulatorTimestamp);
  };
  this.getCirculatorTimestamp = function() {
    return circulatorTimestamp;
  };
  this.bindCirculatorTimestampUpdateHandler = function(handler) {
    var unbind;
    handler(circulatorTimestamp);
    unbind = bindHandler(circulatorTimestampUpdateHandlers, handler);
    return function() {
      return circulatorTimestampUpdateHandlers = unbind(circulatorTimestampUpdateHandlers);
    };
  };

  /*
   *
   * Program Step State
   *
   */
  programStepState = null;
  programStepUpdateHandlers = {};
  setProgramStepState = function(newState) {
    debugService.log('New Program Step State: ' + newState, TAG, {
      programStepState: newState
    });
    programStepState = newState;
    return callHandlers(programStepUpdateHandlers, programStepState);
  };
  this.getProgramStepState = function() {
    return programStepState;
  };
  this.bindProgramStepUpdateHandlers = function(handler) {
    var unbind;
    handler(programStepState);
    unbind = bindHandler(programStepUpdateHandlers, handler);
    return function() {
      return programStepUpdateHandlers = unbind(programStepUpdateHandlers);
    };
  };

  /*
   *
   * Program State
   *
   */
  programState = null;
  programUpdateHandlers = {};
  setProgramState = function(newState) {
    debugService.log('New Program State', TAG, {
      programState: newState
    });
    programState = newState;
    return callHandlers(programUpdateHandlers, programState);
  };
  this.getProgramState = function() {
    return programState;
  };
  this.bindProgramUpdateHandler = function(handler) {
    var unbind;
    handler(programState);
    unbind = bindHandler(programUpdateHandlers, handler);
    return function() {
      return programUpdateHandlers = unbind(programUpdateHandlers);
    };
  };

  /*
   *
   * Live Feed Error
   * Note: This is a special state until we figure out the architecture and eventing system for errors in general
   *
   */
  this.bindLiveFeedErrorHander = function(handler) {
    currentCirculator.on('noLiveFeed', handler);
    return function() {
      return currentCirculator.removeListener('noLiveFeed', handler);
    };
  };

  /*
   *
   * Bind and Unbind Circulator Events
   *
   * map the current circulator's events to circulator manager states
   *
   */
  bindCurrentCirculatorListeners = function() {
    debugService.log('Binding listeners to circulator', TAG);
    currentCirculator.on('state:cooking', function() {
      return setCirculatorCookState(cookStates.cooking);
    });
    currentCirculator.on('state:idle', function() {
      return setCirculatorCookState(cookStates.idle);
    });
    currentCirculator.on('bathTemp:updated', function(value) {
      return setBathTemperatureState(value);
    });
    currentCirculator.on('programStep:updated', function(value) {
      return setProgramStepState(value);
    });
    currentCirculator.on('program:updated', function(value) {
      return setProgramState(value);
    });
    currentCirculator.on('timeRemaining:updated', function(value) {
      return setTimeRemainingState(value);
    });
    currentCirculator.on('errorState:updated', function(value) {
      return setCirculatorErrorState(value);
    });
    currentCirculator.on('circulatorTimestamp:updated', function(value) {
      return setCirculatorTimestamp(value);
    });
    currentCirculator.on('disconnected', function() {
      return setCirculatorConnectionStateDebounced(circulatorConnectionStates.disconnected);
    });
    currentCirculator.on('connected', function() {
      return setCirculatorConnectionStateDebounced(circulatorConnectionStates.connected);
    });
    return currentCirculator.on('connecting', function() {
      return setCirculatorConnectionStateDebounced(circulatorConnectionStates.connecting);
    });
  };
  unbindCurrentCirculatorListeners = function() {
    debugService.log('unbindCurrentCirculatorListeners', TAG);
    currentCirculator.removeAllListeners('state:cooking');
    currentCirculator.removeAllListeners('state:idle');
    currentCirculator.removeAllListeners('bathTemp:updated');
    currentCirculator.removeAllListeners('programStep:updated');
    currentCirculator.removeAllListeners('program:updated');
    currentCirculator.removeAllListeners('timeRemaining:updated');
    currentCirculator.removeAllListeners('errorState:updated');
    currentCirculator.removeAllListeners('circulatorTimestamp:updated');
    currentCirculator.removeAllListeners('disconnected');
    currentCirculator.removeAllListeners('connected');
    return currentCirculator.removeAllListeners('connecting');
  };

  /*
   *
   * State Update Event Handler Utilities
   *
   */
  unbindHandler = function(handlers, handlerId) {
    var newHandlers;
    newHandlers = _.omit(handlers, handlerId);
    return newHandlers;
  };
  bindHandler = function(handlers, handler) {
    var handlerId, unbind;
    handlerId = $window.Uuid.v4();
    handlers[handlerId] = handler;
    unbind = function(oldHandlers) {
      var newHandlers;
      newHandlers = unbindHandler(oldHandlers, handlerId);
      return newHandlers;
    };
    return unbind;
  };
  callHandlers = function(handlers, state) {
    return _.each(handlers, function(handler) {
      return handler(state);
    });
  };
  setCirculatorConnectionState(circulatorConnectionStates.connecting);

  /*
   *
   * Circulator Connecting Maybe State
   *
   * used to optimistically guess which state we'll be in once connecting succeeds
   *
   */
  circulatorConnectingMaybeState = null;
  setCirculatorConnectingMaybeState = function(newState) {
    return circulatorConnectingMaybeState = newState;
  };
  setCirculatorConnectingMaybeState(circulatorConnectingMaybeStates.maybeDisconnected);
  this.getCirculatorConnectingMaybeState = function() {
    return circulatorConnectingMaybeState;
  };

  /*
   *
   * Circulator Getters
   *
   */
  this.getCurrentCirculatorName = function() {
    return currentCirculator != null ? currentCirculator.name : void 0;
  };
  this.getCurrentCirculatorAddress = function() {
    return currentCirculator != null ? currentCirculator.address : void 0;
  };
  this.getCurrentCirculatorConnectionsStatus = function() {
    return currentCirculator != null ? currentCirculator.getConnectionsStatus() : void 0;
  };
  this.getCurrentCirculatorConnections = function() {
    return currentCirculator != null ? currentCirculator.getConnections() : void 0;
  };

  /*
   *
   * Debug Getters
   *
   * Use these for console debugging and testing, but they shouldn't be called by any code
   *
   */

  /* eslint-disable no-console */
  this.getSdkCirculatorManager = function() {
    console.warn('WARNING! getSdkCirculatorManager is only for testing and debugging, and should not be called by code');
    return sdkCirculatorManager;
  };
  this.getCurrentCirculator = function() {
    console.warn('WARNING! getCurrentCirculator is only for testing and debugging, and should not be called by code');
    return currentCirculator;
  };
  this.setCirculatorConnectionState = function(newState) {
    console.warn('WARNING! setCirculatorConnectionState is only for testing and debugging, and should not be called by code');
    return setCirculatorConnectionState(newState);
  };

  /* eslinst-enable no-console */

  /*
   *
   *
   * TODO: refactor so that we don't have to have special methods to accomodate unit tests
   * or use a test framework and module loading system that lets us better mock dependencies
   * this would mean using require.js or (better yet) ES2015 import / exports, which in turn can be tested
   * using tools like Jest or Mockery
   *
   */
  this.setSdkCirculatorManager = function(newSdkCirculatorManager) {
    console.warn('WARNING! setSdkCirculatorManager is only for testing and debugging, and should not be called by code');
    return $window.CirculatorSDK.CirculatorManager = newSdkCirculatorManager;
  };
  this.getScanTimeout = function() {
    console.warn('WARNING! getScanTimeout is only for testing and debugging, and should not be called by code');
    return scanTimeout;
  };

  /*
   *
   * Circulator Simulator Methods
   *
   * Used for working with the circulator simulator
   *
   */
  this.isSimulatorEnabled = function() {
    return currentCirculator.isSimulatorEnabled();
  };
  this.enableSimulator = function(enabled) {
    return currentCirculator.enableSimulator(enabled);
  };

  /*
   *
   * Circulator sendFile
   *
   * Sends a file of specified type to the circulator
   *
   */
  this.sendFile = function(type, file) {
    debugService.log("Calling sendFile(), currentCirculator set? " + (currentCirculator != null), TAG);
    if (currentCirculator != null) {
      return currentCirculator.sendFile(type, file);
    } else {
      return $window.Q.reject(new NoCirculatorError('No connected circulator'));
    }
  };

  /*
   *
   * Circulator stopActivities
   *
   * Stops monitoring messages (particularly CirculatorDataPoint)
   *
   */
  this.stopActivities = function() {
    debugService.log("Calling stopActivities(), currentCirculator set? " + (currentCirculator != null), TAG);
    if (currentCirculator != null) {
      currentCirculator.stopActivities();
      return $window.Q.resolve();
    } else {
      return $window.Q.reject(new NoCirculatorError('No connected circulator'));
    }
  };

  /*
   *
   * Circulator startActivities
   *
   * Starts monitoring messages (particularly CirculatorDataPoint).
   * Example use case is during device firmware update when it's beneficial
   * to disable stream messages for the duration of the update.  A call to
   * `startActivities` will typically follow a corresponding call to
   * `stopActivities` (see above).
   *
   * @returns a promise
   *
   */
  this.startActivities = function() {
    debugService.log("Calling startActivities(), currentCirculator set? " + (currentCirculator != null), TAG);
    if (currentCirculator != null) {
      currentCirculator.startActivities();
      return $window.Q.resolve();
    } else {
      return $window.Q.reject(new NoCirculatorError('No connected circulator'));
    }
  };

  /*
   *
   * Circulator enterBootMode
   *
   * Restarts the firmware into boot mode with the type specified
   *
   * @param {string} bootModeType -
   *   see doc in CirculatorSDK.CirculatorClient for valid bootModeTypes
   *
   */
  this.enterBootMode = function(bootModeType) {
    debugService.log("Calling enterBootMode(), currentCirculator set? " + (currentCirculator != null), TAG);
    if (currentCirculator != null) {
      return currentCirculator.enterBootMode(bootModeType);
    } else {
      return $window.Q.reject(new NoCirculatorError('No connected circulator'));
    }
  };

  /*
   *
   * Circulator restartDevice
   *
   * Restarts the device
   *
   */
  this.restartDevice = function() {
    debugService.log("Calling restartDevice(), currentCirculator set? " + (currentCirculator != null), TAG);
    if (currentCirculator != null) {
      return currentCirculator.restartDevice();
    } else {
      return $window.Q.reject(new NoCirculatorError('No connected circulator'));
    }
  };

  /*
   *
   * Circulator wifiDFUDownloadTFTP
   *
   * Asks the ESP chip in the circulator to download the mentioned
   *   file from a tftp server
   *
   * @param {string} host - tftp host where the file is being hosted
   * @param {string] filename - name of the filename on the host
   * @param {sha256} sha256 - SHA-256 hash of the file
   * @param {int} totalBytes - size of the file
   *
   * @returns {Promise} -
   *   A promise which resolves if the request succeeds,
   *   rejects with an error
   *
   */
  this.wifiDFUDownloadTFTP = function(host, filename, sha256, totalBytes) {
    debugService.log("Calling wifiDFUDownloadTFTP(), currentCirculator set? " + (currentCirculator != null), TAG);
    if (currentCirculator != null) {
      return currentCirculator.wifiDFUDownloadTFTP(host, filename, sha256, totalBytes);
    } else {
      return $window.Q.reject(new NoCirculatorError('No connected circulator'));
    }
  };

  /*
   *
   * Circulator setWifiDFUFirmware
   *
   * Sets the active ESP FW to the one with the specified SHA
   *
   * @param {sha256} sha256 - SHA-256 hash of the file to apply
   *
   * @returns {Promise} -
   *   A promise which resolves if the request succeeds,
   *   rejects with an error
   *
   */
  this.setWifiDFUFirmware = function(sha256) {
    debugService.log("Calling setWifiDFUFirmware(), currentCirculator set? " + (currentCirculator != null), TAG);
    if (currentCirculator != null) {
      return currentCirculator.setWifiDFUFirmware(sha256);
    } else {
      return $window.Q.reject(new NoCirculatorError('No connected circulator'));
    }
  };

  /*
   *
   * Circulator getWifiDFUStatus
   *
   * Gets the status of each of the ESP FW slots and which is running
   *
   * @returns {Promise} -
   *   A promise which resolves if the request succeeds,
   *   rejects with an error
   *
   */
  this.getWifiDFUStatus = function() {
    debugService.log("Calling getWifiDFUStatus(), currentCirculator set? " + (currentCirculator != null), TAG);
    if (currentCirculator != null) {
      return currentCirculator.getWifiDFUStatus();
    } else {
      return $window.Q.reject(new NoCirculatorError('No connected circulator'));
    }
  };
  return this;
}]);

this.app.service('circulatorStorage', ["cacheService", function(cacheService) {

  /**
   *
   * @method list
   * @memberof CirculatorStorage
   *
   * @description List all the circulators that the app has previously connected to
   *
   * @returns An array of {ConnectionData} objects
   *
   *
   */
  this.list = function() {
    return cacheService.getAll('circulator');
  };

  /**
   *
   * @method get
   * @memberof CirculatorStorage
   *
   * @description Retrieve a corresponding ConnectionData object based on the given circulator address
   *
   * @param {string} The address of the circulator
   *
   * @returns {ConnectionData}
   *
   */
  this.get = function(address) {
    return cacheService.get(address, 'circulator');
  };

  /**
   *
   * @method set
   * @memberof CirculatorStorage
   *
   * @description Retrieve a corresponding ConnectionData object based on the given circulator address
   *
   * @param {string} The address of the circulator
   * @param {ConnectionData} The connection data to save
   *
   */
  this.set = function(address, data) {
    return cacheService.set(address, 'circulator', data);
  };

  /**
   *
   * @method remove
   * @memberof CirculatorStorage
   *
   * @description Removes the corresponding ConnectionData object for the given circulator address
   *
   * @param {string} The address of the circulator
   *
   */
  this.remove = function(address) {
    return cacheService.remove(address, 'circulator');
  };

  /**
   *
   * @method removeAll
   * @memberof CirculatorStorage
   *
   * @description Remove all data stored in this service
   *
   */
  this.removeAll = function() {
    return cacheService.removeAll('circulator');
  };
  return this;
}]);

this.app.service('circulatorWifiStatusService', ["$window", "$state", "debugService", "locale", "alertService", "faqLinkConfig", "wifiConnectionStates", function($window, $state, debugService, locale, alertService, faqLinkConfig, wifiConnectionStates) {
  var TAG, handleUnhealthyWifiStatusWithPopup, isHealthyWifiStatus;
  TAG = 'CirculatorWifiStatusService';
  isHealthyWifiStatus = function(wifiStatus) {
    if (wifiStatus == null) {
      throw new $window.CirculatorSDK.InvalidParameterError('Null wifi status');
    }
    if (((wifiStatus.bearerTokenSet != null) && wifiStatus.bearerTokenSet !== true) || ((wifiStatus.connectionStatus != null) && wifiStatus.connectionStatus !== wifiConnectionStates.WIFI_GOT_IP) || ((wifiStatus.cloudStatus != null) && wifiStatus.cloudStatus !== 101)) {
      return false;
    } else {
      return true;
    }
  };
  handleUnhealthyWifiStatusWithPopup = function(wifiStatus) {
    var alertOptions;
    if (wifiStatus == null) {
      throw new $window.CirculatorSDK.InvalidParameterError('Null wifi status');
    }
    debugService.log('Handle unhealthy wifi status with popup', TAG, {
      wifiStatus: wifiStatus
    });
    alertOptions = {
      headerColor: 'alert-yellow',
      icon: 'fail',
      titleString: locale.getString('popup.wifiTroubleshootTitle'),
      bodyString: locale.getString('popup.wifiTroubleShootDescription'),
      cancelText: locale.getString('general.cancel'),
      okText: locale.getString('popup.weShall'),
      link: faqLinkConfig.cantConnectToWifi,
      okAction: function() {
        return $state.go('circulatorWifi');
      }
    };
    if (wifiStatus.connectionStatus === wifiConnectionStates.WIFI_GOT_IP && wifiStatus.cloudStatus !== 101) {
      alertOptions.titleString = locale.getString('popup.wifiCloudConnectionErrorTitle');
      alertOptions.bodyString = locale.getString('popup.wifiCloudConnectionErrorDescription');
      alertOptions.okText = locale.getString('popup.yesPlease');
    } else if (wifiStatus.connectionStatus === wifiConnectionStates.WIFI_WRONG_PASSWORD) {
      alertOptions.titleString = locale.getString('popup.wifiIncorrectPasswordTitle');
      alertOptions.bodyString = locale.getString('popup.wifiIncorrectPasswordDescription');
      alertOptions.okText = locale.getString('popup.letsDoIt');
    } else if (wifiStatus.connectionStatus === wifiConnectionStates.WIFI_NO_AP_FOUND) {
      alertOptions.titleString = locale.getString('popup.wifiNotFoundTitle');
      alertOptions.bodyString = locale.getString('popup.wifiNotFoundDescription', {
        wifiNetworkName: wifiStatus.SSID
      });
      alertOptions.okText = locale.getString('popup.letsDoIt');
    }
    return alertService.confirm(alertOptions).then(function(okAction) {
      if (okAction) {
        return alertOptions.okAction();
      }
    });
  };
  this.isHealthyWifiStatus = isHealthyWifiStatus;
  this.handleUnhealthyWifiStatusWithPopup = handleUnhealthyWifiStatusWithPopup;
  return this;
}]);

this.app.service('collectionService', ["$http", "$q", "$ionicPlatform", "appConfig", "cacheService", "preferences", "resourceService", "stepService", "guideService", "assetService", function($http, $q, $ionicPlatform, appConfig, cacheService, preferences, resourceService, stepService, guideService, assetService) {

  /**
   *
   * @method get
   * @public
   *
   */
  this.get = function(id) {
    return resourceService.get('collection', id);
  };

  /**
   *
   * @method getAll
   * @public
   *
   * @description Returns all collections available on the device.
   *
   */
  this.getAll = function() {
    return resourceService.getAll('collection').then(function(collections) {
      var c, i, len;
      for (i = 0, len = collections.length; i < len; i++) {
        c = collections[i];
        if (c.thumbnail) {
          assetService.get(c.thumbnail);
        }
      }
      return $q.when(collections);
    });
  };

  /**
   *
   * @method getBySlug
   * @public
   *
   * @description Returns a collection with a specific slug.
   *
   */
  this.getBySlug = (function(_this) {
    return function(slug, options) {
      if (options == null) {
        options = {};
      }
      return _this.updateAll().then(_this.getAll).then(function(collections) {
        var collection;
        collection = _.findWhere(collections, {
          slug: slug
        });
        if (collection) {
          if (options.populate) {
            return _this.populateCollection(collection);
          } else {
            return $q.when(collection);
          }
        } else {
          return $q.reject();
        }
      });
    };
  })(this);
  this.populateCollection = (function(_this) {
    return function(arg) {
      var itemPromises, items;
      items = (arg != null ? arg : {}).items;
      if (!items) {
        throw new Error('Must pass collection to populateCollection!');
      }
      itemPromises = [];
      _.map(items, function(item) {
        switch (item.type) {
          case 'step':
            return itemPromises.push(stepService.get(item.id).then(function(step) {
              if (step.image) {
                assetService.get(step.image);
              }
              if (step.noVideoThumbnail) {
                assetService.get(step.noVideoThumbnail);
              }
              return $q.resolve(step);
            }));
          case 'guide':
            return itemPromises.push(guideService.get(item.id));
          case 'collection':
            return itemPromises.push(_this.get(item.id).then(function(nestedCollection) {
              return _this.populateCollection(nestedCollection);
            }));
          default:
            throw new Error('Unknown type present in collection passed to populateCollection.');
        }
      });
      return $q.all(itemPromises);
    };
  })(this);
  this.updateAll = function() {
    return resourceService.updateAll('collection');
  };
  return this;
}]);

this.app.service('connectionTroubleshootingService', ["$ionicPlatform", "debugService", "circulatorManager", "circulatorConnectionStates", "loggingTags", function($ionicPlatform, debugService, circulatorManager, circulatorConnectionStates, loggingTags) {
  var TAG, initialAppResumeTime, initialAppStartTime, initialDisconnectTime, onApplicationPause, onApplicationResume, onConnectionUpdated, unbindConnectionUpdateHandler;
  TAG = 'ConnectionTroubleshootingService';
  initialDisconnectTime = null;
  initialAppStartTime = null;
  initialAppResumeTime = null;
  unbindConnectionUpdateHandler = null;
  onConnectionUpdated = function(connectionState) {
    var accumulativeTime, now;
    if (connectionState === circulatorConnectionStates.disconnected) {
      if (initialDisconnectTime == null) {
        initialDisconnectTime = new Date();
        return debugService.log('Initial disconnect detected', [TAG, loggingTags.connectionTroubleshooting]);
      }
    } else if (connectionState === circulatorConnectionStates.connected) {
      now = new Date();
      if (initialDisconnectTime != null) {
        accumulativeTime = now - initialDisconnectTime;
        debugService.log('Time from disconnected to connected', [TAG, loggingTags.connectionTroubleshooting], {
          accumulativeTime: accumulativeTime
        });
        initialDisconnectTime = null;
      }
      if (initialAppStartTime != null) {
        accumulativeTime = now - initialAppStartTime;
        debugService.log('Time from app start up to connected', [TAG, loggingTags.connectionTroubleshooting], {
          accumulativeTime: accumulativeTime
        });
        initialAppStartTime = null;
      }
      if (initialAppResumeTime != null) {
        accumulativeTime = now - initialAppResumeTime;
        debugService.log('Time from app resume to connected', [TAG, loggingTags.connectionTroubleshooting], {
          accumulativeTime: accumulativeTime
        });
        return initialAppResumeTime = null;
      }
    } else if (connectionState === circulatorConnectionStates.unpaired || connectionState === circulatorConnectionStates.jouleFound) {
      initialDisconnectTime = null;
      initialAppStartTime = null;
      return initialAppResumeTime = null;
    }
  };
  onApplicationPause = function() {
    var accumulativeTime, now;
    if (initialDisconnectTime != null) {
      now = new Date();
      accumulativeTime = now - initialDisconnectTime;
      debugService.log('Time from disconnected to application pause', [TAG, loggingTags.connectionTroubleshooting], {
        accumulativeTime: accumulativeTime
      });
      initialDisconnectTime = null;
    }
    return unbindConnectionUpdateHandler();
  };
  onApplicationResume = function() {
    unbindConnectionUpdateHandler = circulatorManager.bindCirculatorConnectionUpdateHandler(onConnectionUpdated);
    initialDisconnectTime = null;
    return initialAppResumeTime = new Date();
  };
  this.getInitialDisconnectTime = function() {
    return initialDisconnectTime;
  };
  this.initialize = function() {
    unbindConnectionUpdateHandler = circulatorManager.bindCirculatorConnectionUpdateHandler(onConnectionUpdated);
    $ionicPlatform.on('pause', onApplicationPause);
    $ionicPlatform.on('resume', onApplicationResume);
    return initialAppStartTime = new Date();
  };
  return this;
}]);

this.app.service('debugService', ["$window", "$rootScope", "fileWriteStream", function($window, $rootScope, fileWriteStream) {
  var consoleWriteStream, logHelper, logger, sessionStaticProperties;
  logger = null;
  sessionStaticProperties = {
    appSessionId: $window.Uuid.v4(),
    appBuildFlavor: 'production',
    appVersionNumber: '2.37.1'
  };
  this.debug = function(message, tags, properties) {
    return logHelper('debug', message, tags, properties);
  };
  this.warn = function(message, tags, properties) {
    return logHelper('warn', message, tags, properties);
  };
  this.error = function(message, tags, properties) {
    return logHelper('error', message, tags, properties);
  };
  this.log = function(message, tags, properties) {
    return logHelper('info', message, tags, properties);
  };
  this.info = function(message, tags, properties) {
    return logHelper('info', message, tags, properties);
  };
  logHelper = function(loggingFunction, message, tags, properties) {
    var fields, i, len, tag;
    fields = {};
    if (properties != null) {
      fields.csProperties = properties;
    }
    if (tags != null) {
      if (_.isString(tags)) {
        fields[tags] = 1;
      } else {
        for (i = 0, len = tags.length; i < len; i++) {
          tag = tags[i];
          fields[tag] = 1;
        }
      }
    }
    return logger[loggingFunction](fields, message);
  };
  consoleWriteStream = new $window.CSLogging.ConsoleStream();
  this.getLogger = function() {
    if (logger == null) {
      logger = $window.CSLogging.getRootLogger().child(sessionStaticProperties);
      logger.streams = [];
      logger.addStream({
        level: 'debug',
        type: 'raw',
        stream: consoleWriteStream,
        name: 'console'
      });
      if ($window.cordova) {
        logger.addStream({
          level: 'debug',
          type: 'stream',
          stream: fileWriteStream.createStream(),
          name: 'file'
        });
      }
    }
    return logger;
  };
  this.setLogLevel = function() {
    if (false) {
      return consoleWriteStream.setLevel($window.Bunyan.DEBUG);
    } else {
      return consoleWriteStream.setLevel($window.Bunyan.WARN);
    }
  };

  /*
   *
   * @method setConsoleLogFilters
   * @memberof debugService
   *
   * @description Sets a filter so that the stream only outputs records with matching filters
   *
   * @param {string[]} An array of filter strings, or null if no filter.
   *
   */
  this.setConsoleLogFilters = function(filters) {
    return consoleWriteStream.setFilters(filters);
  };
  $window.setConsoleLogFilters = this.setConsoleLogFilters;

  /*
   *
   * @method onPromiseUnhandledRejection
   * @memberof debugService
   *
   * @description The handler for an unhandled rejection in a promise
   * See https://github.com/kriskowal/q/wiki/API-Reference
   * Terminating with catch is not sufficient because the catch handler may itself throw an error.
   *
   */

  /* eslint-disable no-alert */
  this.onPromiseUnhandledRejection = function(error, tags) {
    this.error('onPromiseUnhandledRejection', tags, {
      error: error
    });
    if (false) {
      return alert("Sorry you've just got an onPromiseUnhandledRejection and the app could be in a bad state.  The error is: " + error);
    }
  };

  /* eslint-enable no-alert */
  logger = this.getLogger();
  this.setLogLevel();
  this.setConsoleLogFilters(null);
  return this;
}]);

this.app.service('deepLinkService', ["$window", "debugService", function($window, debugService) {
  var TAG, goToDiscoveryHelper;
  TAG = 'DeepLinkService';
  goToDiscoveryHelper = function(path, fallback) {
    var iosIdentifier, ref;
    if (!$window.appAvailability) {
      if (ionic.Platform.isWebView()) {
        throw new Error('Unable to determine app availability!');
      } else {
        debugService.warn('Deep link is disabled', TAG);
        return;
      }
    }
    iosIdentifier = 'comchefstepsmobile://';
    return (ref = $window.appAvailability) != null ? ref.checkBool(iosIdentifier, function(isAvailable) {
      debugService.log('AppAvailability', TAG, {
        iosIdentifier: iosIdentifier,
        isAvailable: isAvailable
      });
      if (isAvailable) {
        return $window.open(iosIdentifier + path, '_system');
      } else if (fallback != null) {
        return $window.open('http://chefsteps.com' + fallback, '_system');
      }
    }) : void 0;
  };
  this.goToDiscoveryHome = function() {
    return goToDiscoveryHelper('index.html', '/');
  };
  this.goToRecipe = function(recipeId, guideId) {
    return goToDiscoveryHelper('index.html#/recipe/' + recipeId + '?guideId=' + guideId, '/activities/' + recipeId);
  };
  return this;
}]);

this.app.service('devSimulatorService', ["$window", "$rootScope", "connectionSimulatorService", "cacheService", "debugService", "connectionProvidersConfig", function($window, $rootScope, connectionSimulatorService, cacheService, debugService, connectionProvidersConfig) {
  this.simulating = null;
  this.initialize = function() {
    this.simulating = cacheService.get('simulating', 'devSimulatorService');
    if (this.simulating == null) {
      this.simulating = false;
    }
    return this.setSimulation(this.simulating);
  };
  this.setSimulation = function(isSimulating) {
    this.simulating = isSimulating;
    cacheService.set('simulating', 'devSimulatorService', this.simulating);
    if (this.simulating) {
      connectionSimulatorService.enable(debugService, connectionProvidersConfig);
    } else {
      connectionSimulatorService.disable();
    }
    return this.simulating;
  };
  return this;
}]);

this.app.service('externalLinkPreprocessor', ["zendeskRedirectService", function(zendeskRedirectService) {
  return {
    sso: function(uri) {
      return zendeskRedirectService.getRedirectUrl(uri);
    }
  };
}]);

this.app.service('fileService', ["$window", "$cordovaFile", "$cordovaFileTransfer", function($window, $cordovaFile, $cordovaFileTransfer) {
  this.getDir = function(type) {
    return $window.Q.Promise(function(resolve, reject) {
      return ionic.Platform.ready(function() {
        var cordovaDir;
        if (type == null) {
          reject(new Error('FileService.getDir(): directory type not specified'));
        }
        if (($window.cordova != null) && ($window.cordova.file != null)) {
          cordovaDir = {
            'data': $window.cordova.file.dataDirectory,
            'cache': $window.cordova.file.cacheDirectory,
            'temp': $window.cordova.file.tempDirectory
          }[type];
          if (!cordovaDir) {
            reject(new Error('FileService.getDir(): incorrect type specified'));
          }
          if ($window.resolveLocalFileSystemURL) {
            return $window.resolveLocalFileSystemURL(cordovaDir, resolve, reject);
          } else if ($window.webkitResolveFileSystemURL) {
            return $window.webkitResolveLocalFileSystemURL(cordovaDir, resolve, reject);
          } else {
            return reject(new Error('FileService.getDir(): cannot resolve file system URL'));
          }
        } else {
          return $window.webkitRequestFileSystem($window.TEMPORARY, 5 * 1024 * 1024, (function(fs) {
            return resolve(fs.root);
          }), reject);
        }
      });
    });
  };
  this.getFile = function(dirRef, filename) {
    return $window.Q.Promise(function(resolve, reject) {
      if (!dirRef) {
        return reject('tried to getFile when dir was not set');
      }
      return dirRef.getFile(filename, {
        create: true
      }, resolve, reject);
    });
  };
  this.renameFile = function(dir, fileEntry, newFilename) {
    return $window.Q.Promise(function(resolve, reject) {
      if (!dir) {
        return reject('tried to renameFile when dir was not set');
      }
      return fileEntry.moveTo(dir, newFilename, resolve, reject);
    });
  };
  this.download = function(src, dest, options) {
    if (options == null) {
      options = {};
    }
    return $cordovaFileTransfer.download(src, dest, options);
  };
  this.fileFromFileEntry = function(fileEntry) {
    return $window.Q.Promise(function(resolve, reject) {
      return fileEntry.file(resolve, reject);
    });
  };
  this.readFile = function(file, encoding) {
    if (encoding == null) {
      encoding = 'arrayBuffer';
    }
    return $window.Q.Promise(function(resolve, reject) {
      var method, reader;
      method = {
        arrayBuffer: 'readAsArrayBuffer',
        text: 'readAsText'
      }[encoding];
      if (!method) {
        reject(new Error("Unknown encoding: " + encoding));
        return;
      }
      reader = new FileReader();
      reader.onload = function() {
        return resolve(reader.result);
      };
      reader.onerror = function(e) {
        return reject(e);
      };
      return reader[method](file);
    });
  };
  this.readFileEntry = function(fileEntry, encoding) {
    if (encoding == null) {
      encoding = 'arrayBuffer';
    }
    return this.fileFromFileEntry(fileEntry).then((function(_this) {
      return function(file) {
        return _this.readFile(file, encoding);
      };
    })(this));
  };
  return this;
}]);


/* eslint-disable no-alert, no-console */

/* eslint-disable no-console */
this.app.service('fileWriteStream', ["$q", "$window", "logTransportService", "$interval", "loggingConfig", "fileService", function($q, $window, logTransportService, $interval, loggingConfig, fileService) {
  var buffer, bufferWrite, dirRef, fileReady, fileWrite, filenames, flushBuffer, headRef, initHeadFile, initialize, maxBufferSize, onError, onFileReady, readFile, rotate, rotationInterval, setDirRef, setHeadRef, startRotationTimer, uploadOldFile;
  rotationInterval = loggingConfig.fileRotationInterval;
  dirRef = null;
  headRef = null;
  filenames = {
    head: 'log_head.txt',
    old: 'log_old.txt'
  };
  fileReady = false;
  buffer = [];
  maxBufferSize = 5000;
  onError = function(e) {
    return console.error(e);
  };
  onFileReady = function() {
    fileReady = true;
    flushBuffer();
    return $q.resolve();
  };
  flushBuffer = function() {
    if (fileReady && buffer.length > 0) {
      return fileWrite(buffer.shift());
    }
  };
  readFile = function(fileEntry) {
    return $window.Q.Promise(function(resolve, reject) {
      return fileEntry.file(function(file) {
        return fileService.readFile(file, 'text').then(resolve)["catch"](reject);
      });
    });
  };
  uploadOldFile = function() {
    return fileService.getFile(dirRef, filenames.old).then(readFile).then(logTransportService.postFile);
  };
  bufferWrite = function(record) {
    if (buffer.length > maxBufferSize) {
      return onError('log file memory buffer full');
    }
    buffer.push(record);
    return flushBuffer();
  };
  fileWrite = function(record) {
    fileReady = false;
    return headRef.createWriter(function(fileWriter) {
      if (fileWriter.length > loggingConfig.maxFileSize) {
        buffer.unshift(record);
        return rotate();
      }
      fileWriter.onwriteend = onFileReady;
      fileWriter.seek(fileWriter.length);
      return fileWriter.write(record);
    }, onError);
  };
  rotate = function() {
    fileReady = false;
    return fileService.renameFile(dirRef, headRef, filenames.old).then(initHeadFile).then(onFileReady).then(uploadOldFile)["catch"](onError);
  };
  startRotationTimer = function() {
    $interval(rotate, rotationInterval);
    return $q.resolve();
  };
  setDirRef = function() {
    return fileService.getDir('data').then(function(dir) {
      dirRef = dir;
      return $q.resolve();
    });
  };
  setHeadRef = function(fileEntry) {
    headRef = fileEntry;
    return $q.resolve();
  };
  initHeadFile = function() {
    return fileService.getFile(dirRef, filenames.head).then(setHeadRef);
  };
  initialize = function() {
    return startRotationTimer().then(setDirRef).then(initHeadFile);
  };
  this.createStream = function() {
    initialize().then(onFileReady)["catch"](onError);
    return {
      write: bufferWrite
    };
  };
  this.forceUpload = function() {
    if (!headRef) {
      return alert('could not force upload, no log file found');
    }
    return rotate();
  };
  return this;
}]);

this.app.service('firmwareUpdateService', ["$window", "$http", "firmwareUpdateConfig", "debugService", "fileService", "authenticationService", "cacheService", "circulatorManager", "circulatorConnectionStates", "NoCirculatorError", function($window, $http, firmwareUpdateConfig, debugService, fileService, authenticationService, cacheService, circulatorManager, circulatorConnectionStates, NoCirculatorError) {
  var TAG, addAppVersion, cachedFirmwareUpdateAvailable, firmwareUpdateCached, getClient, getDate, recordLastDateChecked, scrubFields, shouldCheck;
  TAG = 'FirmwareUpdateService';
  firmwareUpdateCached = false;
  cachedFirmwareUpdateAvailable = false;
  getClient = function() {
    var client;
    client = new $window.CSApiClient.FirmwareApi(firmwareUpdateConfig.firmwareUpdatesBasePath, authenticationService.getToken(), debugService.getLogger());
    return client;
  };
  scrubFields = function(info) {
    return {
      name: info.name,
      firmwareVersion: info.firmwareVersion,
      hardwareVersion: info.hardwareVersion,
      serialNumber: info.serialNumber,
      bleMacAddress: info.bleMacAddress,
      softdeviceVersion: info.softdeviceVersion,
      bootloaderVersion: info.bootloaderVersion,
      appFirmwareVersion: info.appFirmwareVersion,
      espFirmwareVersion: info.espFirmwareVersion,
      certificateVersion: info.certificateVersion,
      modelNumber: info.modelNumber
    };
  };
  addAppVersion = function(info) {
    info.appVersion = '2.37.1';
    return info;
  };
  getDate = function() {
    return $window.moment().format('YYYY-MM-DD');
  };
  recordLastDateChecked = function() {
    var date;
    date = getDate();
    debugService.log("Recording last date firmware versions checked: " + date, TAG);
    return cacheService.set('lastDateChecked', 'firmwareUpdateService', date);
  };
  shouldCheck = function() {
    var dfuAutocheckEnabled;
    if (!authenticationService.isAuthenticated()) {
      return false;
    }
    dfuAutocheckEnabled = !!cacheService.get('dfuAutocheckEnabled', 'preference');
    return dfuAutocheckEnabled;
  };
  this.fetchUpdateManifest = function() {
    debugService.log('Fetching firmware update manifest', TAG);
    recordLastDateChecked();
    return circulatorManager.identify().then(scrubFields).then(addAppVersion).then(function(versions) {
      debugService.log('Sending firmware update manifest request to server', TAG, {
        firmwareVersions: versions
      });
      return getClient().getFirmwareUpdates(versions).then(function(manifest) {
        return {
          old: versions,
          "new": manifest
        };
      });
    });
  };
  this.newManifestAvailable = function() {
    var connectionState;
    connectionState = circulatorManager.getCirculatorConnectionState();
    if (connectionState !== circulatorConnectionStates.connected) {
      $window.Q.reject(new NoCirculatorError('No connected circulator'));
    }
    if (shouldCheck()) {
      debugService.log('Checking for whether there is a new firmware manifest', TAG);
      if (firmwareUpdateCached) {
        debugService.log("Firmware update availability cached as: " + cachedFirmwareUpdateAvailable, TAG);
        return $window.Q.resolve(cachedFirmwareUpdateAvailable);
      } else {
        debugService.log('Fetching firmware manifest', TAG);
        return this.fetchUpdateManifest().then(function(res) {
          var ref;
          debugService.log('Firmware manifest result', TAG, {
            manifestResult: res
          });
          firmwareUpdateCached = true;
          cachedFirmwareUpdateAvailable = ((ref = res["new"].updates) != null ? ref.length : void 0) > 0;
          return cachedFirmwareUpdateAvailable;
        });
      }
    } else {
      return $window.Q.resolve(false);
    }
  };
  this.invalidateCachedUpdateAvailability = function() {
    debugService.log('Invalidating firmware update availability cache', TAG);
    firmwareUpdateCached = false;
    return cachedFirmwareUpdateAvailable = false;
  };
  this.fetchFirmwareImage = function(uri) {
    var constructFullLocalPath, convertArrayBufferToByteArray, convertFileEntryToArrayBuffer, downloadToPath, localName, run;
    debugService.log("Fetching image from " + uri, TAG);
    localName = 'firmware-tmp';
    run = function() {
      return fileService.getDir('cache').then(constructFullLocalPath).then(downloadToPath).then(convertFileEntryToArrayBuffer).then(convertArrayBufferToByteArray);
    };
    constructFullLocalPath = function(dirEntry) {
      return "" + dirEntry.nativeURL + localName;
    };
    downloadToPath = function(localPath) {
      var goodUri;
      debugService.log("Downloading firmware file to: " + localPath, TAG);
      goodUri = decodeURIComponent(uri).replace(/\+/g, '%2b');
      return fileService.download(goodUri, localPath, {
        encodeURI: false
      });
    };
    convertFileEntryToArrayBuffer = function(fileEntry) {
      debugService.log('Firmware file downloaded', TAG);
      return fileService.readFileEntry(fileEntry, 'arrayBuffer');
    };
    convertArrayBufferToByteArray = function(arrayBuffer) {
      return $window.ByteBuffer.wrap(arrayBuffer, 'binary');
    };
    return run();
  };
  return this;
}]);


/*
 *
 * @name Guide Service
 *
 * @description Responsible for obtaining and caching guides.
 *
 */
this.app.service('guideService', ["$q", "appConfig", "preferences", "guideFactory", "resourceService", "assetService", "networkStateService", "debugService", function($q, appConfig, preferences, guideFactory, resourceService, assetService, networkStateService, debugService) {
  var TAG;
  TAG = 'guideService';

  /**
   *
   * @method getAll
   * @public
   *
   * @description Returns all guides available from the resource service.
   *
   * @returns {object} - Promise, resolves with an array of guides.
   *
   */
  this.getAll = function() {
    return resourceService.getAll('guide').then(function(guides) {
      return $q.when(_.map(guides, guideFactory));
    });
  };

  /**
   *
   * @method get
   * @public
   *
   * @description Returns all guides when no parameter is
   * passed and returns specific guides when an ID is passed.
   * Not passing an ID is a deprecated way of using this method,
   * use getAll() instead.
   *
   * @returns {object} - Promise, resolves with an array of guides.
   *
   */
  this.get = (function(_this) {
    return function(id) {
      if (id) {
        return resourceService.get('guide', id).then(function(guide) {
          return $q.when(guideFactory(guide));
        });
      } else {
        return _this.getAll();
      }
    };
  })(this);

  /**
   *
   * @method prefetchVideos
   * @public
   *
   * @description Try to get all of the videos we might need for a guide, prioritizing the overview video
   * and the default doneness and first step first.
   *
   * @returns nothing
   *
   */
  this.prefetchVideos = function(guide) {
    var defaultProgramIndex;
    if (networkStateService.requireManualVideoDownloads()) {
      return;
    }
    defaultProgramIndex = Math.max(_.findIndex(guide.programs, function(p) {
      var ref;
      return p.id === ((ref = guide.defaultProgram) != null ? ref.id : void 0);
    }), 0);
    debugService.debug('prefetchVideos called', [TAG], {
      guide: guide.title
    });
    assetService.get(guide.video).then(function() {
      return assetService.get(guide.programs[defaultProgramIndex].video);
    }).then(function() {
      if (guide.steps[0].video != null) {
        return assetService.get(guide.steps[0].video);
      }
    }).then(function() {
      return assetService.getAll(guide.assets);
    });
    return void 0;
  };
  return this;
}]);


/*
This service allows us to deploy a new version of the app w/o going through the
official app store version update process
http://microsoft.github.io/code-push/docs/cordova.html

Calling codePush.sync() will look at the remote codepush server to see if there is a new version available
We can then configure what the update experience is for the user, whether it is mandatory (will force update),
whether the user see's a dialog or not, whether the app installs immediately, on next resume, on next restart, etc.
 */
this.app.service('hotDeployService', ["$window", function($window) {
  var ref, sync, syncOptions;
  syncOptions = {
    updateDialog: {
      appendReleaseDescription: true,
      descriptionPrefix: '\n\nChange log:\n'
    },
    installMode: (ref = $window.InstallMode) != null ? ref.IMMEDIATE : void 0
  };
  sync = function() {
    return $window.codePush.sync(null, syncOptions);
  };
  this.initialize = function() {
    document.addEventListener('deviceready', sync);
    return document.addEventListener('resume', sync);
  };
  return this;
}]);

this.app.service('localNotificationService', ["$q", "$window", function($q, $window) {
  this.schedule = function(options) {
    var deferred, notificationOptions, ref, ref1;
    if (options == null) {
      options = {};
    }
    if (!ionic.Platform.isWebView()) {
      return $q.resolve();
    }
    deferred = $q.defer();
    notificationOptions = {
      id: (ref = options.id) != null ? ref.toString() : void 0,
      at: options.at,
      title: options.title
    };
    if (ionic.Platform.isIOS()) {
      $window.Joule.scheduleLocalNotification(notificationOptions, deferred.resolve, deferred.reject);
      if ((ref1 = ionic.Platform.device().version) != null ? ref1.match(/^10\./g) : void 0) {
        $window.onLocalNotificationOpen = function() {
          options.onOpen();
          return $window.onLocalNotificationOpen = angular.noop;
        };
      } else {
        if (options.onOpen && $window.cordova) {
          cordova.plugins.notification.local.on('click', options.onOpen);
        }
      }
    } else {
      if ($window.cordova) {
        cordova.plugins.notification.local.schedule(notificationOptions, deferred.resolve);
      }
      if (options.onOpen && $window.cordova) {
        cordova.plugins.notification.local.on('click', options.onOpen);
      }
    }
    return deferred.promise;
  };
  this.cancel = function(id) {
    var deferred, ref;
    if (!ionic.Platform.isWebView()) {
      return $q.resolve();
    }
    deferred = $q.defer();
    if (ionic.Platform.isIOS()) {
      $window.Joule.cancelLocalNotification(id != null ? id.toString() : void 0, deferred.resolve, deferred.reject);
    } else {
      if (((ref = $window.cordova) != null ? ref.plugins : void 0) != null) {
        cordova.plugins.notification.local.cancel(id, deferred.resolve);
      }
    }
    return deferred.promise;
  };
  return this;
}]);

this.app.service('logTransportService', ["$q", "$http", "csConfig", "loggingConfig", "authenticationService", "$log", function($q, $http, csConfig, loggingConfig, authenticationService, $log) {
  var getSignedUrl, onError, onSuccess, put;
  getSignedUrl = function() {
    var config, token;
    config = {};
    token = authenticationService.getToken();
    if (token) {
      config.headers = {
        'Authorization': token
      };
    }
    return $http.get(csConfig.chefstepsEndpoint + loggingConfig.remoteFileSignerPath, config);
  };
  onSuccess = function() {
    $log.log('successfully posted logs at ' + Date.now());
    return $q.resolve();
  };
  onError = function(e) {
    return $log.error(e);
  };
  put = function(contents, url_response) {
    var upload_url;
    upload_url = url_response.data.upload_url;
    return $http.put(upload_url, contents, {
      headers: {
        'Content-Type': 'text/plain'
      }
    });
  };
  this.postFile = function(contents) {
    var putContents;
    putContents = _.partial(put, contents);
    return getSignedUrl().then(putContents).then(onSuccess)["catch"](onError);
  };
  return this;
}]);

this.app.service('metadataEventTimer', ["debugService", function(debugService) {
  var log;
  log = function(message, eventId) {
    var eventTimestamp;
    if (!eventId) {
      return;
    }
    eventTimestamp = Date.now();
    return debugService.log(message, 'eventTimer', {
      eventTimestamp: eventTimestamp,
      eventId: eventId
    });
  };
  this.startEventTimer = function(eventId) {
    return log('start eventTimer', eventId);
  };
  this.endEventTimer = function(eventId) {
    return log('end eventTimer', eventId);
  };
  return this;
}]);

this.app.service('metadataHeartbeatService', ["$interval", "metadataSnapshotService", "$window", "appConfig", "debugService", "$ionicPlatform", function($interval, metadataSnapshotService, $window, appConfig, debugService, $ionicPlatform) {
  var TAG, clearInterval, heartbeatInterval, heartbeatIntervalMilliseconds, logMetadata, sessionStartTime, setInterval;
  TAG = 'MetadataHeartbeatService';
  sessionStartTime = null;
  heartbeatIntervalMilliseconds = appConfig.metadataHeartbeatIntervalMilliseconds;
  heartbeatInterval = null;
  logMetadata = function() {
    var snapshot;
    snapshot = metadataSnapshotService.getSnapshot();
    snapshot.metadataSessionAliveTime = $window.Date.now() - sessionStartTime;
    return debugService.log('metadata heartbeat!', TAG, snapshot);
  };
  setInterval = function() {
    return heartbeatInterval = $interval(logMetadata, heartbeatIntervalMilliseconds);
  };
  clearInterval = function() {
    if (heartbeatInterval != null) {
      return $interval.cancel(heartbeatInterval);
    }
  };
  this.initialize = function() {
    sessionStartTime = $window.Date.now();
    return setInterval();
  };
  $ionicPlatform.on('pause', clearInterval);
  $ionicPlatform.on('resume', setInterval);
  return this;
}]);

this.app.service('metadataSnapshotService', ["circulatorManager", "circulatorConnectionStates", "debugService", "$window", "$ionicHistory", "cacheService", function(circulatorManager, circulatorConnectionStates, debugService, $window, $ionicHistory, cacheService) {
  var TAG, appInfo, createSnapshot, getWifiStatus, identifyResponse, inProgress, onConnectionUpdated;
  TAG = 'MetadataSnapshotService';
  appInfo = {
    headRef: 'refs/heads/master',
    commitHash: 'ab4f7ff'
  };
  identifyResponse = null;
  inProgress = false;
  createSnapshot = function(appInfo) {
    var bathTemp, ref, ref1, ref2, ref3, ref4, ref5;
    debugService.debug('creating snapshot', TAG);
    bathTemp = circulatorManager.getBathTemperatureState();
    return {
      circulatorHardwareVersion: identifyResponse != null ? identifyResponse.hardwareVersion : void 0,
      circulatorSerialNumber: identifyResponse != null ? identifyResponse.serialNumber : void 0,
      circulatorName: circulatorManager.getCurrentCirculatorName(),
      circulatorAddress: circulatorManager.getCurrentCirculatorAddress(),
      circulatorConnectionState: circulatorManager.getCirculatorConnectionState(),
      circulatorCookState: circulatorManager.getCirculatorCookState(),
      circulatorBathTemperatureState: bathTemp != null ? bathTemp.toPrecision(5) : void 0,
      circulatorTimeRemainingState: circulatorManager.getTimeRemainingState(),
      circulatorProgramStepState: circulatorManager.getProgramStepState(),
      circulatorProgramState: circulatorManager.getProgramState(),
      firmwareVersion: identifyResponse != null ? identifyResponse.firmwareVersion : void 0,
      softdeviceVersion: identifyResponse != null ? identifyResponse.softdeviceVersion : void 0,
      bootloaderVersion: identifyResponse != null ? identifyResponse.bootloaderVersion : void 0,
      appFirmwareVersion: identifyResponse != null ? identifyResponse.appFirmwareVersion : void 0,
      espFirmwareVersion: identifyResponse != null ? identifyResponse.espFirmwareVersion : void 0,
      certificateVersion: identifyResponse != null ? identifyResponse.certificateVersion : void 0,
      wifiSSIDSet: !_.isEmpty((ref = circulatorManager.getCurrentCirculatorWifiStatus()) != null ? ref.SSID : void 0),
      wifiBearerTokenSet: (ref1 = circulatorManager.getCurrentCirculatorWifiStatus()) != null ? ref1.bearerTokenSet : void 0,
      wifiConnectionStatus: (ref2 = circulatorManager.getCurrentCirculatorWifiStatus()) != null ? ref2.connectionStatus : void 0,
      wifiTimeSinceLastPacketMS: (ref3 = circulatorManager.getCurrentCirculatorWifiStatus()) != null ? ref3.timeSinceLastPacketMS : void 0,
      wifiCloudStatus: (ref4 = circulatorManager.getCurrentCirculatorWifiStatus()) != null ? ref4.cloudStatus : void 0,
      applicationAddress: circulatorManager.getApplicationAddress(),
      appHeadRef: appInfo.headRef,
      appCommitHash: appInfo.commitHash,
      appPlatform: ionic.Platform.platform(),
      appPlatformVersion: ionic.Platform.version(),
      appCurrentViewName: $ionicHistory.currentStateName(),
      userName: (ref5 = cacheService.get('profile', 'user')) != null ? ref5.name : void 0,
      device: $window.device,
      hasEverPaired: cacheService.get('usageHistory', 'hasEverPaired'),
      sessionCount: cacheService.get('sessionCount', 'sessionCount')
    };
  };
  onConnectionUpdated = function(connectionState) {
    if (connectionState === circulatorConnectionStates.connected) {
      if (!inProgress) {
        inProgress = true;
        return circulatorManager.identify().then(function(response) {
          identifyResponse = response;
          return getWifiStatus();
        })["catch"](function(error) {
          return debugService.error('Fail to call identify', TAG, {
            error: error
          });
        })["finally"](function() {
          return inProgress = false;
        }).done(_.noop, function(e) {
          return debugService.onPromiseUnhandledRejection(e, TAG);
        });
      }
    }
  };
  getWifiStatus = function() {
    if (circulatorManager.getCirculatorConnectionState() === circulatorConnectionStates.connected) {
      return circulatorManager.getWifiStatus()["catch"](function(error) {
        return debugService.info('Fail to call getWifiStatus', TAG, {
          error: error
        });
      });
    }
  };
  circulatorManager.bindCirculatorConnectionUpdateHandler(onConnectionUpdated);
  this.getSnapshot = function() {
    return createSnapshot(appInfo);
  };
  return this;
}]);

this.app.service('networkStateService', ["preferences", function(preferences) {
  this.requireManualVideoDownloads = function() {
    var conn, ref, ref1;
    if (!preferences.get('useLessData')) {
      return false;
    }
    conn = (ref = navigator.network) != null ? (ref1 = ref.connection) != null ? ref1.type : void 0 : void 0;
    if (!conn || _.includes(['wifi', 'ethernet'], conn)) {
      return false;
    }
    return true;
  };
  this.noInternet = function() {
    var conn, ref, ref1;
    conn = (ref = navigator.network) != null ? (ref1 = ref.connection) != null ? ref1.type : void 0 : void 0;
    if (conn === 'none') {
      return true;
    }
    return false;
  };
  return this;
}]);

this.app.service('preferences', ["cacheService", function(cacheService) {
  var prefNamespace;
  prefNamespace = 'preference';
  this.get = function(key) {
    if (!key) {
      throw new Error('When retrieving a preference, you must supply a key!');
    }
    return cacheService.get(key, prefNamespace);
  };
  this.getAll = function() {
    return cacheService.getAllKeyValuePairs(prefNamespace);
  };
  this.set = function(key, value) {
    if (!key) {
      throw new Error('When setting a preference, you must supply a key!');
    }
    if (value == null) {
      throw new Error('When setting a preference, you must supply a value!');
    }
    return cacheService.set(key, prefNamespace, value);
  };
  this.setDefault = (function(_this) {
    return function(key, value) {
      if (_this.get(key) == null) {
        return _this.set(key, value);
      }
    };
  })(this);
  this.remove = function(key) {
    if (!key) {
      throw new Error('When removing a preference, you must supply a key!');
    }
    return cacheService.remove(key, prefNamespace);
  };
  return this;
}]);

this.app.service('pushRegistrationService', ["$state", "$window", "debugService", "appConfig", "pushRegistrationConfig", "authenticationService", "cacheService", "notificationTypes", function($state, $window, debugService, appConfig, pushRegistrationConfig, authenticationService, cacheService, notificationTypes) {
  var TAG, cacheKey, cacheNamespace, deregisterOldId, getClient, getRegisteredId, pushPlugin, recordLatestRegistrationId, registerNewId;
  TAG = 'PushRegistrationService';
  cacheKey = 'registrationId';
  cacheNamespace = 'pushRegistrationService';
  pushPlugin = null;
  getClient = function() {
    var client;
    client = new $window.CSApiClient.PushRegistrationApi(pushRegistrationConfig.pushRegistrationBasePath, authenticationService.getToken(), debugService.getLogger());
    return client;
  };
  recordLatestRegistrationId = function(registrationId) {
    debugService.log("Recording registrationId: " + registrationId, TAG);
    return cacheService.set(cacheKey, cacheNamespace, registrationId);
  };
  getRegisteredId = function() {
    return cacheService.get(cacheKey, cacheNamespace);
  };
  deregisterOldId = function(newRegistrationId) {
    var previouslyRegisteredId;
    if (newRegistrationId == null) {
      newRegistrationId = '';
    }
    previouslyRegisteredId = getRegisteredId();
    debugService.log('Deregistering old ID', TAG, {
      oldRegistrationId: previouslyRegisteredId,
      newRegistrationId: newRegistrationId
    });
    if (newRegistrationId !== previouslyRegisteredId && (previouslyRegisteredId != null)) {
      return getClient().deregister(previouslyRegisteredId);
    } else {
      debugService.log('No need to deregister the old ID', TAG);
      return $window.Q.resolve();
    }
  };
  registerNewId = function(newRegistrationId) {
    var platform;
    debugService.log('Registering new ID with cloud', TAG, {
      newRegistrationId: newRegistrationId
    });
    platform = ionic.Platform.platform();
    return getClient().register(newRegistrationId, platform);
  };
  this.register = function() {
    debugService.log('Registering device for push notifications', TAG);
    return document.addEventListener('deviceready', function() {
      var options;
      options = pushRegistrationConfig.options;
      pushPlugin = $window.PushNotification.init(options);
      pushPlugin.on('notification', function(data) {
        var ref, ref1;
        debugService.log('Received push notification.', TAG, {
          pushNotificationData: data
        });
        if ((ref = data.additionalData) != null ? ref.foreground : void 0) {
          debugService.log('Notification received while app was in foreground - ignoring.', TAG, {
            pushNotificationData: data
          });
          return;
        }
        if (((ref1 = data.additionalData) != null ? ref1.notification_type : void 0) === notificationTypes.CIRCULATOR_ERROR_BUTTON_PRESSED) {
          debugService.log("Received CIRCULATOR_ERROR_BUTTON_PRESSED notification - navigating to default view: " + appConfig.defaultView + ".", TAG, {
            pushNotificationData: data
          });
          return $state.go(appConfig.defaultView);
        }
      });
      return pushPlugin.on('registration', function(data) {
        var registrationId;
        debugService.log('Received registrationId from notification service', TAG, {
          data: data
        });
        registrationId = data.registrationId;
        return deregisterOldId(registrationId).then(function() {
          return registerNewId(registrationId);
        }).then(function() {
          return recordLatestRegistrationId(registrationId);
        }).then(function() {
          return debugService.log('Successfully registered device for push notifications', TAG);
        })["catch"](function(error) {
          return debugService.error('Failed to register device for push notifications', TAG, {
            error: error
          });
        }).done(_.noop, function(e) {
          return debugService.onPromiseUnhandledRejection(e, TAG);
        });
      });
    });
  };
  this.deregister = function() {
    return $window.Q.promise(function(resolve) {
      var onPushPluginUnregisterError, onPushPluginUnregisterSuccess, removeFromCloud;
      debugService.log('Deregistering device from recieving push notifications', TAG);
      removeFromCloud = function() {
        return deregisterOldId().then(function() {
          return debugService.log('Successfully deregistered device for push notifications', TAG);
        })["catch"](function(error) {
          return debugService.error('Failed to deregister device for push notifications', TAG, {
            error: error
          });
        })["finally"](function() {
          return cacheService.remove(cacheKey, cacheNamespace);
        });
      };
      onPushPluginUnregisterSuccess = function() {
        debugService.log('Successfully unregistered device from notification service', TAG);
        return removeFromCloud().then(resolve);
      };
      onPushPluginUnregisterError = function() {
        debugService.warn('Failed to unregister device from notification service', TAG);
        return removeFromCloud().then(resolve);
      };
      return pushPlugin != null ? pushPlugin.unregister(onPushPluginUnregisterSuccess, onPushPluginUnregisterError) : void 0;
    });
  };
  this.isNotificationEnabled = function() {
    return $window.Q.promise(function(resolve) {
      return document.addEventListener('deviceready', function() {
        return $window.PushNotification.hasPermission(function(data) {
          return resolve(data.isEnabled);
        });
      });
    });
  };
  return this;
}]);


/*
 *
 * @name Recipe Service
 *
 * @description Makes API calls to get recipes
 *
 */
this.app.service('recipeService', ["$q", "$http", "cacheService", "debugService", function($q, $http, cacheService, debugService) {
  var TAG, generateRecipe, getRecipeFromApi, root;
  TAG = 'RecipeService';
  root = 'https://www.chefsteps.com/api/v0';

  /*
   *
   * @typedef Recipe
   * @type {object}
   * @property {string} id - ID of the recipe
   * @property {string} slug - Slug of the recipe
   * @property {string} title - title text of the recipe
   * @property {string} image - image url of the recipe's
   *
   */

  /*
   *
   * @method getRecipe
   * @public
   *
   * @description Retrieces a recipe based on an ID or recipe slug
   *              It would retrieve the recipe from the cache first, and if not found, it then gets it form the service
   *
   * @param {string} id - An ID such as '9527' or a slug such as 'green-pea-mash'
   *
   * @returns {Promise} Resolves with a recipe object
   *
   */
  this.getRecipe = function(id) {
    var cachedRecipe, deferred;
    deferred = $q.defer();
    cachedRecipe = cacheService.get(id, 'recipe');
    if (cachedRecipe != null) {
      deferred.resolve(cachedRecipe);
      getRecipeFromApi(id);
    } else {
      getRecipeFromApi(id).then(function(recipe) {
        return deferred.resolve(recipe);
      });
    }
    return deferred.promise;
  };

  /*
   *
   * @method generateRecipe
   * @private
   *
   * @description Generates a recipe based on json data
   *
   * @param {JSON} data - A JSON structure representing the recipe
   *
   * @returns {Recipe} - The recipe object
   *
   */
  generateRecipe = function(data) {
    var recipe, tokens;
    recipe = {};
    recipe.id = data.id;
    recipe.title = data.title;
    recipe.image = data.image;
    tokens = data.url.split('/activities/');
    recipe.slug = tokens[tokens.length - 1];
    return recipe;
  };

  /*
   *
   * @method getRecipeFromApi
   * @private
   *
   * @description Gets recipe by making an API call, subsequently storing the recipe in the cache
   *
   * @param {string} id - An ID such as '9527' or a slug such as 'green-pea-mash'
   *
   * @returns {Promise} Resolves with a recipe object
   *
   */
  getRecipeFromApi = function(id) {
    var deferred, recipeUrl;
    deferred = $q.defer();
    recipeUrl = root + "/activities/" + id;
    debugService.log('Retriving recipe', TAG, {
      recipeUrl: recipeUrl
    });
    $http({
      method: 'GET',
      url: recipeUrl,
      params: {
        client: 'iOS'
      }
    }).then(function(response) {
      var recipe;
      recipe = generateRecipe(response.data);
      cacheService.set(id, 'recipe', recipe);
      return deferred.resolve(recipe);
    });
    return deferred.promise;
  };
  return this;
}]);


/**
 *
 * @name Resource Service
 *
 * @description The resource service is responsible for maintaining records
 * of app resources (guides, steps, etc...), as well as updating, deleting, and
 * retrieving them.
 *
 */
this.app.service('resourceService', ["$http", "$ionicHistory", "$q", "appConfig", "cacheService", "preferences", "guideManifestEndpoints", function($http, $ionicHistory, $q, appConfig, cacheService, preferences, guideManifestEndpoints) {

  /*
   *
   * @member version
   * @private
   *
   * @description Used to version resources in the case that we need to iterate
   * on the guide content model.
   *
   */
  var _version, deleteUpdate, getUpdate, loadApplicationResources, loadRemoteResources, saveResource, saveUpdate;
  _version = 'v2';

  /*
   *
   * @member version
   * @private
   *
   * @description Used to version resources in the case that we need to iterate
   * on the guide content model.
   *
   */
  this.getVersion = function() {
    return _version;
  };

  /*
   *
   * @method loadApplicationResources
   * @private
   *
   * @description Fetches preloaded resources via the path provided by
   * calling applicationResourcesPath().
   *
   */
  loadApplicationResources = function() {
    var deferred;
    deferred = $q.defer();
    $http.get(appConfig.applicationResourcesPath).success(deferred.resolve).error(deferred.reject);
    return deferred.promise;
  };

  /*
   *
   * @method loadRemoteResources
   * @private
   *
   */
  loadRemoteResources = function() {
    var deferred, remoteResourcesPath;
    deferred = $q.defer();
    remoteResourcesPath = (function() {
      switch (preferences.get('guideManifestEndpoint')) {
        case guideManifestEndpoints.DEVELOPMENT:
          return appConfig.developmentGuideManifestEndpoint;
        case guideManifestEndpoints.STAGING:
          return appConfig.stagingGuideManifestEndpoint;
        case guideManifestEndpoints.PRODUCTION:
          return appConfig.productionGuideManifestEndpoint;
        default:
          return appConfig.productionGuideManifestEndpoint;
      }
    })();
    $http.get(remoteResourcesPath).success(deferred.resolve).error(deferred.reject);
    return deferred.promise;
  };

  /*
   *
   * @method fetch
   * @public
   *
   * @description Goes to the remote resources endpoint and pulls down
   * updated resources. We term the document that contains all resources
   * the "manifest", and once we have it, the resource service parses it up
   * and stores the obtained resources either as new records, potential updates,
   * or neither (if the resource is a version older than the one we have).
   *
   */
  this.fetch = (function(_this) {
    return function() {
      return _this.load().then(loadRemoteResources).then(function(m) {
        return $q.when(_this.saveManifest(m));
      });
    };
  })(this);

  /**
   *
   * @method get
   * @public
   *
   * @description Returns a specific resource type for a given ID.
   *
   */
  this.get = function(type, id) {
    var deferred;
    deferred = $q.defer();
    ionic.Platform.ready((function(_this) {
      return function() {
        return _this.load().then(function() {
          var resource;
          resource = cacheService.get(id, type + "." + _version);
          if (resource) {
            return deferred.resolve(resource);
          } else {
            return deferred.reject();
          }
        });
      };
    })(this));
    return deferred.promise;
  };

  /**
   *
   * @method getAll
   * @public
   *
   * @description Returns all types of a given resource.
   *
   */
  this.getAll = function(type) {
    var deferred;
    deferred = $q.defer();
    ionic.Platform.ready((function(_this) {
      return function() {
        return _this.load().then(function() {
          var resources;
          resources = cacheService.getAll(type + "." + _version);
          if (resources.length) {
            return deferred.resolve(resources);
          } else {
            return deferred.reject();
          }
        });
      };
    })(this));
    return deferred.promise;
  };

  /**
   *
   * @method getMany
   *
   * @description Given a resource type and array of IDs, this method returns
   * an array of resources in the same order as the passed IDs.
   *
   */
  this.getMany = (function(_this) {
    return function(type, ids) {
      return _.map(ids, _.partial(_this.get, type));
    };
  })(this);

  /**
   *
   * @method load
   *
   * @description Loads resources from the local filesystem. Resolved application
   * resources are cached via a call to _.once. If the call to the local filesystem
   * fails, we cache that response as well, but given that the only real way this
   * method can fail is if the JSON doesn't exist, that's acceptable.
   *
   */
  this.load = _.once((function(_this) {
    return function() {
      return loadApplicationResources().then(function(manifest) {
        return $q.when(_this.saveManifest(manifest));
      });
    };
  })(this));

  /**
   *
   * @method saveResource
   * @private
   *
   */
  saveResource = function(id, type, resource) {
    return cacheService.set(id, type + "." + _version, resource);
  };

  /**
   *
   * @method saveManifest
   * @public
   *
   * @description Saves resources to local storage, creating updates for resources
   * whose version number is greater than the current resource. Resources which
   * have not been seen before are immediately committed.
   *
   */
  this.saveManifest = function(manifest) {
    return _.each(manifest, function(resources, type) {
      return _.each(resources, function(resource) {
        var savedResource, updatedResource;
        savedResource = cacheService.get(resource.id, type + "." + _version);
        updatedResource = getUpdate(type, resource.id);
        if (savedResource == null) {
          return saveResource(resource.id, type, resource);
        } else if (savedResource.version < resource.version) {
          if (!updatedResource || (updatedResource != null ? updatedResource.version : void 0) < resource.version) {
            return saveUpdate(resource.id, type, resource);
          }
        }
      });
    });
  };

  /**
   *
   * @method deleteUpdate
   * @private
   *
   * @description Removes an update from LocalStorage. Used directly after
   * applying an update.
   *
   */
  deleteUpdate = function(type, id) {
    return cacheService.remove(id + ".update", type + "." + _version);
  };

  /**
   *
   * @method getUpdate
   * @private
   *
   * @description Retrieves an update for a specific resource.
   *
   */
  getUpdate = function(type, id) {
    return cacheService.get(id + ".update", type + "." + _version);
  };

  /**
   *
   * @method saveUpdate
   * @private
   *
   * @description Places an update in LocalStorage for a given resource.
   *
   */
  saveUpdate = function(id, type, update) {
    return cacheService.set(id + ".update", type + "." + _version, update);
  };

  /**
   *
   * @method hasUpdate
   * @public
   *
   * @description Returns a boolean value indicating whether an update for a
   * specific resource exists for a given resource.
   *
   */
  this.hasUpdate = function(type, id) {
    return getUpdate(type, id) != null;
  };

  /**
   *
   * @method refresh
   * @public
   *
   * @description Completely clears existing content (as well as any views utilizing said content),
   * reloads preloaded resources, then fetches content from the app's currently preferred remote.
   *
   */
  this.refresh = (function(_this) {
    return function() {
      var deferred, i, len, ref, type;
      deferred = $q.defer();
      ref = appConfig.contentTypes;
      for (i = 0, len = ref.length; i < len; i++) {
        type = ref[i];
        cacheService.removeAll(type + "." + _version);
      }
      $ionicHistory.clearCache();
      loadApplicationResources().then(function(m) {
        return $q.when(_this.saveManifest(m));
      }).then(loadRemoteResources).then(function(m) {
        return $q.when(_this.saveManifest(m));
      }).then(function() {
        return $q.all((function() {
          var j, len1, ref1, results;
          ref1 = appConfig.contentTypes;
          results = [];
          for (j = 0, len1 = ref1.length; j < len1; j++) {
            type = ref1[j];
            results.push(this.updateAll(type));
          }
          return results;
        }).call(_this));
      }).then(deferred.resolve)["catch"](deferred.reject);
      return deferred.promise;
    };
  })(this);

  /**
   *
   * @method update
   * @public
   *
   * @description Applies an update to a resource, if it exists, then returns
   * the updated resource.
   *
   */
  this.update = (function(_this) {
    return function(type, id) {
      var record, update;
      if (_this.hasUpdate(type, id)) {
        record = cacheService.get(id, type + "." + _version);
        update = getUpdate(type, id);
        if (record.version < update.version) {
          cacheService.set(id, type + "." + _version, update);
          deleteUpdate(type, id);
        }
      }
      return _this.get(type, id);
    };
  })(this);

  /**
   *
   * @method updateAll
   *
   */
  this.updateAll = (function(_this) {
    return function(type) {
      return _this.getAll(type).then(function(resources) {
        return $q.all(_.map(resources, function(r) {
          return _this.update(type, r.id);
        }));
      });
    };
  })(this);

  /**
   *
   * @method updateMany
   *
   */
  this.updateMany = (function(_this) {
    return function(type, ids) {
      return $q.all(_.map(ids, _.partial(_this.update, type)));
    };
  })(this);
  return this;
}]);

this.app.service('sequenceBackService', ["$state", function($state) {
  var viewName, viewOptions;
  viewName = null;
  viewOptions = null;
  this.setSequenceBackView = function(newViewName, newViewOptions) {
    viewName = newViewName;
    return viewOptions = newViewOptions;
  };
  this.goToSequenceBackView = function() {
    if (viewName) {
      if (viewOptions) {
        $state.go(viewName, viewOptions);
        viewName = null;
      } else {
        $state.go(viewName);
      }
      viewName = null;
      return viewOptions = null;
    } else {
      return $state.go('home');
    }
  };
  return this;
}]);


/**
 *
 * @class statusBarService
 *
 * @classdesc This services provides methods for updating the style of status bar.
 *
 */
this.app.service('statusBarService', ["$ionicPlatform", "$window", "$q", "statusBarStyles", "debugService", function($ionicPlatform, $window, $q, statusBarStyles, debugService) {
  var TAG;
  TAG = 'StatusBarService';
  this.defaultTransitionDuration = 0.3;
  this.statusBarHeightPromise = null;
  this.getHeightInPixels = (function(_this) {
    return function() {
      var deferred, onError, onSuccess;
      if (_this.statusBarHeightPromise != null) {
        return _this.statusBarHeightPromise;
      }
      deferred = $q.defer();
      _this.statusBarHeightPromise = deferred.promise;
      if (ionic.Platform.isIOS() || ($window.StatusBar == null)) {
        deferred.resolve(20);
      } else {
        onSuccess = function(height) {
          debugService.log('Got height from native plugin', TAG, {
            height: height
          });
          return deferred.resolve(height);
        };
        onError = function(error) {
          debugService.error('Got error while retrieving height', TAG, {
            error: error
          });
          return deferred.reject(error);
        };
        $window.StatusBar.getHeightInPixels(onSuccess, onError);
      }
      return _this.statusBarHeightPromise;
    };
  })(this);
  this.setStyle = (function(_this) {
    return function(style, options) {
      if (options == null) {
        options = {};
      }
      return ionic.Platform.ready(function() {
        var duration, statusBar;
        statusBar = $window.StatusBar;
        if (_.isUndefined(statusBar)) {
          return;
        }
        duration = options.duration || _this.defaultTransitionDuration;
        switch (style) {
          case statusBarStyles.light:
            statusBar.styleLightContent();
            if (!statusBar.isVisible) {
              return statusBar.show(duration);
            }
            break;
          case statusBarStyles.dark:
            statusBar.styleDefault();
            if (!statusBar.isVisible) {
              return statusBar.show(duration);
            }
            break;
          case statusBarStyles.hidden:
            if (statusBar.isVisible) {
              return statusBar.hide(duration);
            }
            break;
          default:
            return debugService.warn('Called statusBarService.setStyle() with unknown style.', TAG);
        }
      });
    };
  })(this);
  return this;
}]);


/*
 *
 * @name Step Service
 *
 * @description Responsible for obtaining steps from the resource service.
 *
 */
this.app.service('stepService', ["$q", "appConfig", "preferences", "resourceService", function($q, appConfig, preferences, resourceService) {

  /**
   *
   * @method getAll
   * @public
   *
   * @description Returns all steps when no parameter is
   * passed and returns specific steps when an ID is passed.
   *
   * @returns {object} - Promise, resolves with an array of steps.
   *
   */
  this.getAll = function() {
    return resourceService.getAll('step');
  };

  /**
   *
   * @method get
   * @public
   *
   * @description Returns all steps when no parameter is
   * passed and returns specific steps when an ID is passed.
   * Not passing an ID is a deprecated way of using this method,
   * use getAll() instead.
   *
   * @returns {object} - Promise, resolves with an array of steps.
   *
   */
  this.get = (function(_this) {
    return function(id) {
      if (id) {
        return resourceService.get('step', id);
      } else {
        return _this.getAll();
      }
    };
  })(this);
  return this;
}]);

this.app.service('storagePurgeService', ["cacheService", "debugService", "utilities", "storagePurgeConfig", "alertService", function(cacheService, debugService, utilities, storagePurgeConfig, alertService) {
  var TAG;
  TAG = 'StoragePurgeService';
  this.initialize = function() {
    var currentAppVersion, currentBuildFlavor, lastBuildFlavorChecked, lastVersionChecked;
    lastVersionChecked = cacheService.get('lastVersionChecked', 'storagePurgeService');
    if (lastVersionChecked == null) {
      lastVersionChecked = '0.0.0';
    }
    debugService.debug("Last app version checked: " + lastVersionChecked, TAG);
    currentAppVersion = '2.37.1';
    debugService.debug("Current installed app version: " + currentAppVersion, TAG);
    this.checkVersionAndPurge(lastVersionChecked, currentAppVersion, storagePurgeConfig.mapOfVersionAndPurgeNamespaces);
    lastBuildFlavorChecked = cacheService.get('lastBuildFlavorChecked', 'storagePurgeService');
    currentBuildFlavor = 'production';
    if (lastBuildFlavorChecked == null) {
      lastBuildFlavorChecked = currentBuildFlavor;
      cacheService.set('lastBuildFlavorChecked', 'storagePurgeService', currentBuildFlavor);
    }
    return this.checkBuildFlavorAndPurge(lastBuildFlavorChecked, currentBuildFlavor);
  };
  this.checkVersionAndPurge = function(lastVersionChecked, currentAppVersion, mapOfVersionAndPurgeNamespaces) {
    var mapOfNamespacesToPurge;
    if (lastVersionChecked !== currentAppVersion) {
      debugService.log('App versions are not equal', TAG, {
        lastVersionChecked: lastVersionChecked,
        currentAppVersion: currentAppVersion
      });
      mapOfNamespacesToPurge = {};
      _.forEach(mapOfVersionAndPurgeNamespaces, function(namespaces, version) {
        if (utilities.compareVersions(version, lastVersionChecked) === 1) {
          return _.forEach(namespaces, function(namespace) {
            return mapOfNamespacesToPurge[namespace] = 1;
          });
        }
      });
      _.forEach(mapOfNamespacesToPurge, function(value, namespaceToPurge) {
        debugService.log("Purging namespace: " + namespaceToPurge, TAG);
        return cacheService.removeAll(namespaceToPurge);
      });
      return cacheService.set('lastVersionChecked', 'storagePurgeService', currentAppVersion);
    }
  };
  this.checkBuildFlavorAndPurge = function(lastBuildFlavorChecked, currentBuildFlavor) {
    if (lastBuildFlavorChecked !== currentBuildFlavor) {
      debugService.log('Build flavors are not equal', TAG, {
        lastBuildFlavorChecked: lastBuildFlavorChecked,
        currentBuildFlavor: currentBuildFlavor
      });
      cacheService.removeAll('user');
      cacheService.set('lastBuildFlavorChecked', 'storagePurgeService', currentBuildFlavor);
      return alertService.alert({
        headerColor: 'alert-red',
        icon: 'fail',
        titleString: 'Build Flavor Changed!',
        bodyString: "Your build flavor has changed from " + lastBuildFlavorChecked + " to " + currentBuildFlavor + ".  As a result we are resetting your user profile."
      });
    }
  };
  return this;
}]);

this.app.service('temperatureUnitService', ["debugService", "cacheService", function(debugService, cacheService) {
  var storedUnit, unit;
  storedUnit = cacheService.get('temperatureUnit', 'preferences');
  if (storedUnit) {
    unit = storedUnit;
  } else {
    unit = 'f';
  }
  this.set = function(newUnit) {
    var warning;
    if (!_.includes(['c', 'f'], newUnit)) {
      warning = 'tried to set an invalid temp unit: ' + newUnit;
      return debugService.warn(warning);
    }
    unit = newUnit;
    return cacheService.set('temperatureUnit', 'preferences', unit);
  };
  this.get = function() {
    return _.clone(unit);
  };
  return this;
}]);


/**
 *
 * @name timerService
 *
 * @description This service maintains a record of the app timer, and allows
 * other services to interact with it.
 *
 */
this.app.service('timerService', ["cacheService", "timerFactory", "$window", "circulatorManager", "utilities", "programSteps", "programTypes", "guideService", "debugService", "analyticsService", "locale", function(cacheService, timerFactory, $window, circulatorManager, utilities, programSteps, programTypes, guideService, debugService, analyticsService, locale) {
  var TAG, _timer, bindHandler, callHandlers, getTimer, internalTimer, onProgramUpdated, onTimeRemainingUpdated, setTimer, setTimerState, timer, timerUpdateHandlers, unbindHandler;
  TAG = 'timerService';

  /*
   *
   * Private simple getter and setter for just the timer object in local storage
   *
   */
  _timer = null;
  timer = _.first(cacheService.getAll('timer'));
  if (timer != null) {
    _timer = timerFactory(timer.duration, angular.extend(timer, {
      autostart: timer.started,
      paused: timer.paused
    }));
  }
  getTimer = function() {
    return _timer;
  };
  setTimer = function(newTimer) {
    if (_timer != null) {
      _timer.cancel();
    }
    return _timer = newTimer;
  };

  /*
   *
   * This function is called whenever the app detects that the circulator's timer is ticking
   * As a result a timer will be set in order to schedule local notifications
   *
   */
  onTimeRemainingUpdated = (function(_this) {
    return function(timeRemainingInSeconds) {
      var guideId, manualTimer, program, timeInMilliseconds, timerId, timerOptions;
      if (getTimer() === null && timeRemainingInSeconds > 0) {
        program = circulatorManager.getProgramState();
        timeInMilliseconds = utilities.convertSecondsToMilliseconds(timeRemainingInSeconds);
        debugService.log('Got time remaining tick.  Attempting to set timer based on time remaining.', TAG, {
          timeRemainingInSeconds: timeRemainingInSeconds
        });
        if ((program != null ? program.programType : void 0) === programTypes.automatic && circulatorManager.getProgramStepState() === programSteps.cook) {
          guideId = program.programMetadata.guideId;
          timerId = program.programMetadata.timerId;
          debugService.log('Attempting to set guided cooking timer', TAG, {
            guideId: guideId,
            timeInMilliseconds: timeInMilliseconds
          });
          return guideService.get(guideId).then(function(guide) {
            var guidedCookingTimer, time, timerOptions;
            time = _this.getTimeForGuide(guide, timerId);
            timerOptions = {
              push: {
                title: (time != null ? time.notification : void 0) || guide.cookingTimerNotification
              }
            };
            guidedCookingTimer = timerFactory(timeInMilliseconds, timerOptions);
            setTimer(guidedCookingTimer);
            analyticsService.track('Set guided cooking timer', {
              timeInMilliseconds: timeInMilliseconds
            });
            debugService.log('Set guided cooking timer', TAG, {
              timeInMilliseconds: timeInMilliseconds
            });
            guidedCookingTimer.start();
            return setTimerState(guidedCookingTimer);
          })["catch"](function(error) {
            return debugService.error('Guide service get failed.  Unable to set timer.', TAG, {
              error: error
            });
          });
        } else if ((program != null ? program.programType : void 0) === programTypes.manual) {
          timerOptions = {
            push: {
              title: locale.getString('time.timerComplete')
            }
          };
          manualTimer = timerFactory(timeInMilliseconds, timerOptions);
          setTimer(manualTimer);
          analyticsService.track('Set manual cooking timer', {
            timeInMilliseconds: timeInMilliseconds
          });
          debugService.log('Set manual cooking timer', TAG, {
            timeInMilliseconds: timeInMilliseconds
          });
          manualTimer.start();
          return setTimerState(manualTimer);
        }
      }
    };
  })(this);
  onProgramUpdated = function() {
    debugService.log('Clearing timer upon program update', TAG);
    setTimer(null);
    return setTimerState(null);
  };

  /*
   *
   * State Update Event Handler Utilities
   *
   */
  unbindHandler = function(handlers, handlerId) {
    var newHandlers;
    newHandlers = _.omit(handlers, handlerId);
    return newHandlers;
  };
  bindHandler = function(handlers, handler) {
    var handlerId, unbind;
    handlerId = $window.Uuid.v4();
    handlers[handlerId] = handler;
    unbind = function(oldHandlers) {
      var newHandlers;
      newHandlers = unbindHandler(oldHandlers, handlerId);
      return newHandlers;
    };
    return unbind;
  };
  callHandlers = function(handlers, state) {
    return _.each(handlers, function(handler) {
      return handler(state);
    });
  };

  /*
   *
   * Public methods that allow callers to set timer and only get timer via bind handlers
   * In this way, we consolidate the logic of both guided cooking timers and manual timers altogether in this service
   *
   */
  timerUpdateHandlers = {};
  internalTimer = null;
  setTimerState = function(newState) {
    debugService.debug('New timer State', TAG, {
      newTimer: newState
    });
    internalTimer = newState;
    return callHandlers(timerUpdateHandlers, internalTimer);
  };
  this.getTimerState = function() {
    return internalTimer;
  };
  this.bindTimerUpdateHandlers = function(handler) {
    var unbind;
    handler(internalTimer);
    unbind = bindHandler(timerUpdateHandlers, handler);
    return function() {
      return timerUpdateHandlers = unbind(timerUpdateHandlers);
    };
  };

  /*
   *
   * Utility Timer Methods
   *
   */
  this.getTimeForGuide = function(guide, timeId) {
    var times;
    times = _.flatten(_.map(guide.programs, function(p) {
      return p.freshTimes.concat(p.frozenTimes);
    }));
    return _.findWhere(times, {
      id: timeId
    });
  };

  /*
   *
   * Initialization
   *
   */
  circulatorManager.bindTimeRemainingUpdateHandler(onTimeRemainingUpdated);
  circulatorManager.bindProgramUpdateHandler(onProgramUpdated);
  return this;
}]);

this.app.service('userService', ["authenticationService", "cacheService", "circulatorManager", "advertisementService", "debugService", function(authenticationService, cacheService, circulatorManager, advertisementService, debugService) {
  var TAG, _token;
  TAG = 'UserService';
  _token = null;
  this.get = function() {
    return cacheService.get('profile', 'user');
  };
  this.signIn = function(user, token) {
    var userTokenMiddle;
    _token = token;
    userTokenMiddle = null;
    if (token != null) {
      userTokenMiddle = token.split('.')[1];
    }
    debugService.log('Sign in', TAG, {
      userTokenMiddle: userTokenMiddle
    });
    if (user != null) {
      cacheService.set('profile', 'user', user);
    }
    advertisementService.onUserSignIn(token);
    return circulatorManager.onUserSignIn(token).done(_.noop, function(e) {
      return debugService.onPromiseUnhandledRejection(e, TAG);
    });
  };
  this.signOut = function() {
    debugService.log('Sign out', TAG);
    cacheService.remove('profile', 'user');
    authenticationService.logoutWithToken();
    advertisementService.onUserSignOut();
    circulatorManager.onUserSignOut();
    return _token = null;
  };
  this.getToken = function() {
    return _token;
  };
  return this;
}]);

this.app.service('utilities', ["$window", "locale", "temperatureUnitService", function($window, locale, temperatureUnitService) {
  this.generateCookId = function() {
    return $window.Uuid.v4().split('-').join('');
  };
  this.roundTo = function(number, decimalDigits) {
    var mult;
    if (number == null) {
      return null;
    }
    mult = Math.pow(10, decimalDigits);
    return Math.round(parseFloat(number) * mult) / mult;
  };
  this.clamp = function(value, min, max) {
    return Math.min(Math.max(value, min), max);
  };
  this.alpha = function(value, min, max) {
    var alpha;
    alpha = (value - min) / (max - min);
    return this.clamp(alpha, 0, 1);
  };
  this.smoothstep = function(x, xmin, xmax, ymin, ymax) {
    var alpha;
    alpha = this.alpha(x, xmin, xmax);
    alpha = alpha * alpha * (3 - (2 * alpha));
    return ymin + (alpha * (ymax - ymin));
  };
  this.convertCtoF = function(c) {
    return parseFloat(c * 1.8) + 32;
  };
  this.convertFtoC = function(f) {
    return parseFloat(f - 32) / 1.8;
  };
  this.convertSecondsToMilliseconds = function(s) {
    return s * 1000;
  };
  this.convertMinutesToMilliseconds = function(m) {
    return m * 1000 * 60;
  };
  this.convertHoursToMilliseconds = function(h) {
    return h * 1000 * 60 * 60;
  };
  this.convertHoursToMinutes = function(h) {
    return h * 60;
  };
  this.convertMinutesToSeconds = function(m) {
    return m * 60;
  };
  this.secondsToHMS = function(s) {
    var h, m;
    if (s == null) {
      return null;
    }
    s = Math.floor(parseFloat(s));
    m = Math.floor(s / 60);
    s -= m * 60;
    h = Math.floor(m / 60);
    m -= h * 60;
    return {
      hours: h,
      minutes: m,
      seconds: s
    };
  };
  this.formatTimeDisplay = function(value) {
    var timeValue;
    timeValue = (value != null ? value.toString() : void 0) || '--';
    if (timeValue.length < 2) {
      return '0' + timeValue;
    } else {
      return timeValue;
    }
  };
  this.formatTime = function(t, showSeconds) {
    var h, m, result, s;
    if (showSeconds == null) {
      showSeconds = true;
    }
    h = Math.floor(t / 3600);
    t = t - (h * 3600);
    m = Math.floor(t / 60);
    t = t - (m * 60);
    s = Math.floor(t);
    if ((s >= 30) && (!showSeconds)) {
      m += 1;
    }
    if (h > 0) {
      result = h + "h&nbsp;" + m + "m";
    } else if (showSeconds) {
      if (s === 0) {
        s = 1;
      }
      result = m + "m&nbsp;" + s + "s";
    } else {
      result = m + "&nbsp;min";
    }
    return result;
  };

  /*
   *
   * @method humanizeTime
   * @public
   *
   * @description Take a given number of milliseconds and returns a humanized
   * string. We tried Moment.js, but it really didn't give us the duration
   * formatting we needed.
   *
   */
  this.humanizeDuration = function(time) {
    var hour, hourString, hoursString, hoursTime, humanizedString, minute, minuteString, minutesString, minutesTime, pluralizedHours, pluralizedMinutes, pluralizedSeconds, second, secondString, secondsString, secondsTime;
    humanizedString = '';
    second = 1000;
    minute = second * 60;
    hour = minute * 60;
    secondString = locale.getString('time.second');
    secondsString = locale.getString('time.seconds');
    minuteString = locale.getString('time.minute');
    minutesString = locale.getString('time.minutes');
    hourString = locale.getString('time.hour');
    hoursString = locale.getString('time.hours');
    if ((hoursTime = Math.floor(time / hour)) >= 1) {
      pluralizedHours = hoursTime > 1 ? hoursString : hourString;
      humanizedString += hoursTime + " " + pluralizedHours;
    }
    if ((minutesTime = Math.floor((time % hour) / minute)) >= 1) {
      if (humanizedString.length) {
        humanizedString += ' ';
      }
      pluralizedMinutes = minutesTime > 1 ? minutesString : minuteString;
      humanizedString += minutesTime + " " + pluralizedMinutes;
    }
    if ((secondsTime = Math.floor(((time % hour) % minute) / second)) >= 1) {
      if (humanizedString.length) {
        humanizedString += ' ';
      }
      pluralizedSeconds = secondsTime > 1 ? secondsString : secondString;
      humanizedString += secondsTime + " " + pluralizedSeconds;
    }
    return humanizedString;
  };
  this.htmlDecode = function(value) {
    return $('<div/>').html(value).text();
  };
  this.getPreheatAlpha = function(intakeTemperature, setPointTemperature) {
    var alpha, min;
    if ((intakeTemperature == null) || (setPointTemperature == null)) {
      return 0;
    }
    min = 19.9;
    alpha = (intakeTemperature - min) / (setPointTemperature - min);
    return Math.min(Math.max(0, alpha), 1);
  };
  this.nearSetpoint = function(intakeTemperature, setPointTemperature) {
    if ((intakeTemperature == null) || (setPointTemperature == null)) {
      return false;
    }
    return Math.abs(intakeTemperature - setPointTemperature) <= 0.21;
  };

  /*
   *
   * @method isWithinTargetDelta
   * @public
   *
   * @description Indicates if a given value is within +/- delta range of the given target value
   *
   * @param {number} value - The value to compare to
   * @param {number} targetValue - The target value to compare to
   * @param {number} delta - The delta range to +/- onto the target value
   *
   * @returns {boolean} True if the value is within delta range of the target value, false otherwise
   *
   */
  this.isWithinTargetDelta = function(value, targetValue, delta) {
    var max, min;
    if (isNaN(value) || isNaN(targetValue) || isNaN(delta)) {
      return false;
    } else {
      min = targetValue - delta;
      max = targetValue + delta;
      if (value < min || value > max) {
        return false;
      } else {
        return true;
      }
    }
  };

  /*
   *
   * @method roundToFirstDecimal
   * @public
   *
   * @description Round a given value to one decimal place
   *
   * @param {string|number} value - A value to round to one decimal place
   *
   * @returns {string} A string representation of the value that's always rounded to one decimal place (including 0),
   * null if the input is not a parseable number
   *
   * @example roundToFirstDecimal(4.556) returns 4.6, roundToFirstDecimal(4.99) returns 5.0
   *
   */
  this.roundToFirstDecimal = function(value) {
    value = parseFloat(value);
    if (!isNaN(value)) {
      value = value.toFixed(1);
      return value;
    } else {
      return null;
    }
  };

  /*
   *
   * @method getIntDisplay
   * @public
   *
   * @description Returns the integer part of a given value in string format
   * Returns '--' if value is not a number
   *
   * @param {string|number} value - A value to get the integer part
   *
   * @returns {string|number} An integer, or '--' if the input is not a parseable number
   *
   * @example getIntDisplay(4.5) returns 4
   *
   */
  this.getIntDisplay = function(value) {
    value = this.roundToFirstDecimal(value);
    if (value != null) {
      return parseInt(value);
    } else {
      return '--';
    }
  };

  /*
   *
   * @method getDecimalDisplay
   * @public
   *
   * @description Returns the first decimal part of a given value in string format, including '.'
   * Returns '' if value is not a number
   *
   * @param {string|number} value - A value to get the first decimal part
   *
   * @returns {string} '.' + first decimal, or '.' if it's the last part of the string, or '' if the input is not a parseable number
   *
   * @example getDecimalDisplay(4.5) returns .5
   *
   */
  this.getDecimalDisplay = function(value) {
    var firstDecimal, strArray;
    if ((value != null) && value[value.length - 1] === '.') {
      return '.';
    }
    value = this.roundToFirstDecimal(value);
    if (value != null) {
      strArray = value.toString().split('.');
      if (strArray.length > 1) {
        firstDecimal = strArray[1].charAt(0);
        if (firstDecimal !== '0') {
          return '.' + firstDecimal;
        }
      }
    }
    return '';
  };

  /*
   *
   * @method isValueMoreThanOneDecimal
   * @public
   *
   * @description Returns true if the value has more than one decimal digit
   *
   * @param {string|number} value - A value can be either or string or a number
   *
   * @returns {boolean} True if the value has more than one decimal digit
   *
   * @example isValueMoreThanOneDecimal(4.55) returns true
   *
   */
  this.isMoreThanOneDecimal = function(value) {
    var floatValue, strArray;
    floatValue = parseFloat(value);
    if (!isNaN(floatValue)) {
      strArray = null;
      if (typeof value === 'string') {
        strArray = value.split('.');
      } else {
        strArray = floatValue.toString().split('.');
      }
      if (strArray.length > 1 && strArray[1].length > 1) {
        return true;
      }
    }
    return false;
  };

  /*
   *
   * @method isThreeDigits
   * @public
   *
   * @description Returns true if the value contains three digits
   *
   * @param {string|number} value - A value can be either or string or a number
   *
   * @returns {boolean} True if the value contains three digits, false otherwise
   *
   */
  this.isThreeDigits = function(value) {
    value = parseInt(value);
    if (!isNaN(value)) {
      return value.toString().length === 3;
    }
    return false;
  };

  /*
   *
   * @method computerCircleScaleToViewPort
   * @public
   *
   * @description Given a diameter of a circle element, which is positioned in the corner of the app,
   * Calculate the scale / multiplier it would require to expand the circle to cover the entire view port
   * e.g. A scale of 5 indicates that the circle element would have to be five times its original size in order to cover the entire view port
   *
   * @param {number} diameter - The diameter of the circle element in px unit
   *
   * @returns {number} scale - the scale / multipler it would require to expand the circle to cover the entire view port
   *
   */
  this.computerCircleScaleToViewPort = function(diameter) {
    var longestRadiusFromDiameter, longestWindowDiagonal, scale;
    longestWindowDiagonal = Math.sqrt(Math.pow($window.innerHeight, 2) + Math.pow($window.innerWidth, 2));
    longestRadiusFromDiameter = diameter / 2;
    scale = longestWindowDiagonal / longestRadiusFromDiameter;
    scale = Math.round(scale * 10) / 10;
    return scale;
  };

  /*
   *
   * @method validateCirculatorName
   * @public
   *
   * @description Determine whether a circulator name is valid. This can be used when choosing a name for a circulator.
   * e.g. during initial pairing sequence or during renaming
   *
   * @param {string} name - The name to check for validity
   *
   * @returns {boolean} valid - Whether or not the name is valid
   *
   */
  this.validateCirculatorName = function(name) {
    if (name) {
      return true;
    } else {
      return false;
    }
  };
  this.displayToModelTemperature = function(value) {
    if (value != null) {
      if (temperatureUnitService.get() === 'c') {
        return value;
      }
      return this.convertFtoC(value);
    } else {
      return null;
    }
  };
  this.modelToDisplayTemperature = function(value) {
    if (value != null) {
      if (temperatureUnitService.get() === 'c') {
        return value;
      }
      return this.convertCtoF(value);
    } else {
      return null;
    }
  };

  /*
   *
   * @method compareVersions
   * @public
   *
   * @description Compares two version numbers
   *
   * @param {string} versionNumberA - A version number in the format of major.minor.revision
   * @param {string} versionNumberB - A version number in the format of major.minor.revision
   *
   * @returns {number} 0 if versionNumberA equals to versionNumberB, 1 if versionNumberA is greater than versionNumberB, -1 if versionNumberA is less than versionNumberB
   *
   */
  this.compareVersions = function(versionNumberA, versionNumberB) {
    var i, maximumArrayLength, numberA, numberB, versionNumberArrayA, versionNumberArrayB;
    versionNumberArrayA = versionNumberA.split('.');
    versionNumberArrayB = versionNumberB.split('.');
    maximumArrayLength = Math.max(versionNumberArrayA.length, versionNumberArrayB.length);
    while (versionNumberArrayA.length < maximumArrayLength) {
      versionNumberArrayA.push('0');
    }
    while (versionNumberArrayB.length < maximumArrayLength) {
      versionNumberArrayB.push('0');
    }
    i = 0;
    while (i < maximumArrayLength) {
      numberA = parseInt(versionNumberArrayA[i]);
      numberB = parseInt(versionNumberArrayB[i]);
      if (isNaN(numberA) || isNaN(numberB)) {
        throw new Error('Invalid version number!');
      }
      if (numberA > numberB) {
        return 1;
      }
      if (numberB > numberA) {
        return -1;
      }
      i++;
    }
    return 0;
  };
  return this;
}]);

this.app.service('vibrateService', ["$window", function($window) {
  this.vibrate = function(time) {
    if ($window.cordova != null) {
      return navigator.vibrate(time);
    }
  };
  return this;
}]);

this.app.service('zendeskRedirectService', ["csConfig", "userService", "$http", "$window", "debugService", function(csConfig, userService, $http, $window, debugService) {
  var TAG;
  TAG = 'ZendeskRedirectService';
  this.getRedirectUrl = function(path) {
    return $window.Q.Promise(function(resolve) {
      var config, token;
      debugService.log("Attempting to get zendesk redirect url for path: " + path, TAG);
      token = userService.getToken();
      if (!token) {
        debugService.warn('No user token found, user may have to manually enter zendesk credentials', TAG);
        return resolve(path);
      } else {
        config = {
          url: csConfig.chefstepsEndpoint + "/api/v0/auth/external_redirect",
          method: 'GET',
          params: {
            path: path
          },
          headers: {
            Authorization: "Bearer " + token
          }
        };
        return $http(config).then(function(response) {
          debugService.log("Succesfully got zendesk redirect url for path: " + path, TAG);
          return resolve(response.data.redirect);
        })["catch"](function(error) {
          debugService.error("Failed to get zendesk redirect url for path: " + path, TAG, {
            error: error,
            path: path
          });
          return resolve(path);
        });
      }
    });
  };
  return this;
}]);

this.app.directive('advertisement', ["appConfig", "$window", "advertisementService", "debugService", "userService", function(appConfig, $window, advertisementService, debugService, userService) {
  return {
    restrict: 'E',
    scope: {
      slot: '@',
      aspect: '@',
      medium: '@',
      anyJouleSeen: '@'
    },
    link: function($scope) {
      var TAG, loadAd, unbindTokenWatcher;
      TAG = 'advertisement';
      loadAd = function() {
        return advertisementService.getAdContent($window.location.hash, $scope.slot, $scope.aspect).then(function(advertisements) {
          return $scope.advertisement = advertisements[0];
        })["catch"](function(error) {
          return debugService.error('Failed to get ad', TAG, {
            error: error,
            advertisementParams: {
              slot: $scope.slot,
              aspect: $scope.aspect,
              medium: $scope.medium
            }
          });
        });
      };
      $scope.$watch('anyJouleSeen', loadAd);
      unbindTokenWatcher = $scope.$watch(userService.getToken, loadAd);
      $scope.$on('$destroy', unbindTokenWatcher);
      return $scope.openAdvertisementLink = function() {
        return advertisementService.openAdTarget($scope.advertisement, $scope.medium);
      };
    },
    templateUrl: 'templates/directives/advertisement/advertisement.html'
  };
}]);

this.app.directive('backgroundVideo', ["$timeout", "debugService", "networkStateService", "assetService", "alertService", "locale", "metadataEventTimer", function($timeout, debugService, networkStateService, assetService, alertService, locale, metadataEventTimer) {
  return {
    restrict: 'E',
    scope: {
      backgroundColor: '@',
      disableLoop: '@',
      disablePlaceholder: '@',
      disablePoster: '=',
      disableVideo: '=',
      posterSlug: '@',
      videoSlug: '@',
      instance: '=',
      hideControls: '@',
      onPlayButtonClicked: '&',
      loopLimit: '@',
      onLoopLimitReached: '&'
    },
    transclude: true,
    link: function($scope, $element, $attributes) {
      var TAG, clearVideoSource, disableVideoWatcher, findVideoElement, handleOnPause, handleOnResume, initializeVideo, onDestroy, playVideo, ref, showGenericError, showNoNetworkError, unbindVideoSlugWatcher, videoSlugWatcher;
      TAG = 'BackgroundVideoDirective';
      if ((ref = $scope.instance) != null) {
        ref.actions = {
          pause: function() {
            return $($element[0]).find('video').each(function() {
              return this.pause();
            });
          },
          play: function() {
            return $($element[0]).find('video').each(function() {
              return this.play();
            });
          },
          seekTo: function(time) {
            return $($element[0]).find('video').each(function() {
              return this.currentTime = time;
            });
          }
        };
      }
      $scope.loopLimit = $scope.loopLimit || 20;
      $scope.loopLimitReached = false;
      $scope.onVideoPlaying = function() {
        metadataEventTimer.endEventTimer('backgroundVideo-play-video_' + $scope.videoSlug);
        $scope.loading = false;
        $scope.paused = false;
        return $scope.playing = true;
      };
      $scope.onLoopLimitReachedInternal = function() {
        $scope.loopLimitReached = true;
        $timeout(function() {
          return $scope.loading = $scope.playing = $scope.autoplay = $scope.paused = false;
        });
        return typeof $scope.onLoopLimitReached === "function" ? $scope.onLoopLimitReached() : void 0;
      };
      $scope.playButtonClicked = function() {
        if (typeof $scope.onPlayButtonClicked === "function") {
          $scope.onPlayButtonClicked();
        }
        $scope.loopLimitReached = false;
        $timeout(function() {
          return playVideo();
        });
        return true;
      };
      initializeVideo = function() {
        $scope.autoplay = $attributes.autoplay != null;
        $scope.loading = false;
        $scope.paused = false;
        $scope.playing = false;
        $timeout(function() {
          var ref1;
          if ($scope.disableLoop === 'true') {
            return (ref1 = findVideoElement()) != null ? ref1.removeAttribute('loop') : void 0;
          }
        });
        if ($scope.videoSlug) {
          return assetService.exists($scope.videoSlug).then(function() {
            if ($scope.autoplay && !$scope.disableVideo) {
              return playVideo();
            }
          })["catch"](function() {
            if (networkStateService.requireManualVideoDownloads()) {
              return $scope.autoplay = false;
            } else {
              if ($scope.autoplay && !$scope.disableVideo) {
                return playVideo();
              }
            }
          });
        } else {
          return $scope.hideControls = true;
        }
      };
      clearVideoSource = function() {
        var ref1;
        return (ref1 = findVideoElement()) != null ? ref1.src = '' : void 0;
      };
      findVideoElement = function() {
        return $($element[0]).find('video')[0];
      };
      playVideo = function() {
        if ($scope.playing) {
          return;
        }
        if ($scope.paused) {
          $timeout(function() {
            var ref1;
            return (ref1 = findVideoElement()) != null ? ref1.play() : void 0;
          });
          return;
        }
        $scope.loading = true;
        metadataEventTimer.startEventTimer('backgroundVideo-play-video_' + $scope.videoSlug);
        if ($scope.videoURL) {
          $scope.loading = true;
          return $timeout(function() {
            var ref1;
            return (ref1 = findVideoElement()) != null ? ref1.play() : void 0;
          });
        } else {
          return assetService.get($scope.videoSlug).then(function(url) {
            $scope.videoURL = url;
            $scope.autoplay = $attributes.autoplay != null;
            return $timeout(function() {
              var ref1;
              return (ref1 = findVideoElement()) != null ? ref1.play() : void 0;
            });
          })["catch"](function(error) {
            $scope.loading = $scope.playing = $scope.autoplay = false;
            if ($scope.$$destroyed) {
              return;
            }
            if (networkStateService.noInternet()) {
              debugService.warn("Attempted to play video with slug " + $scope.videoSlug + " when background video was offline", TAG);
              if (!$scope.disableVideo) {
                return showNoNetworkError();
              }
            } else {
              debugService.error("Unable to load video with slug: " + $scope.videoSlug, TAG, {
                error: error
              });
              if (!$scope.disableVideo) {
                return showGenericError();
              }
            }
          });
        }
      };
      showGenericError = function() {
        return alertService.alert({
          headerColor: 'alert-yellow',
          icon: 'fail',
          titleString: locale.getString('popup.videoErrorTitle'),
          bodyString: locale.getString('popup.videoErrorDescription')
        });
      };
      showNoNetworkError = function() {
        return alertService.alert({
          headerColor: 'alert-yellow',
          icon: 'fail',
          titleString: locale.getString('popup.noInternetTitle'),
          bodyString: locale.getString('popup.noInternetNoVideoMessage')
        });
      };
      disableVideoWatcher = function(newValue, oldValue) {
        if (newValue == null) {
          return;
        }
        $scope.loading = false;
        $scope.playing = false;
        if (newValue && !oldValue) {
          clearVideoSource();
        } else if (!newValue) {
          initializeVideo();
        }
        return $scope.hideVideo = newValue;
      };
      $scope.$watch('disableVideo', disableVideoWatcher);
      handleOnPause = function() {
        var ref1;
        if ((ref1 = findVideoElement()) != null) {
          ref1.pause();
        }
        if ($scope.playing) {
          $scope.paused = true;
          return $scope.playing = false;
        }
      };
      handleOnResume = function() {
        if ($scope.autoplay && $scope.videoSlug && !$scope.disableVideo) {
          return playVideo();
        }
      };
      document.addEventListener('pause', handleOnPause, false);
      document.addEventListener('resume', handleOnResume, false);
      onDestroy = function() {
        document.removeEventListener('pause', handleOnPause, false);
        document.removeEventListener('resume', handleOnResume, false);
        return clearVideoSource();
      };
      $scope.$on('$destroy', onDestroy);
      assetService.get($scope.posterSlug).then(function(url) {
        return $scope.posterURL = url;
      })["catch"](function() {
        return debugService.error("Cannot load poster for video: " + $scope.posterSlug, TAG);
      });
      $scope.hideVideo = $scope.disableVideo;
      videoSlugWatcher = function(slug) {
        if (slug) {
          initializeVideo();
          return unbindVideoSlugWatcher();
        }
      };
      unbindVideoSlugWatcher = $scope.$watch('videoSlug', videoSlugWatcher);
      return initializeVideo();
    },
    templateUrl: 'templates/directives/background-video/background-video.html'
  };
}]);

this.app.directive('bottomGradient', function() {
  return {
    restrict: 'E',
    scope: {
      'gradientStyle': '@'
    },
    link: function(scope) {
      scope.isIos8 = false;
      ionic.Platform.ready(function() {
        var platform, version;
        platform = ionic.Platform.platform();
        version = ionic.Platform.version();
        if (platform === 'ios' && Math.floor(version) === 8) {
          return scope.isIos8 = true;
        }
      });
      return this;
    },
    templateUrl: 'templates/directives/bottom-gradient/bottom-gradient.html'
  };
});

this.app.directive('circulatorEventDialog', ["alertService", "$window", "circulatorConnectionStates", "circulatorEventDialogConfigProvider", "circulatorManager", "debugService", "$state", "locale", "programSteps", "programTypes", "circulatorErrorStates", "circulatorErrorStatePriorities", "circulatorEventTypes", "circulatorEventReasons", function(alertService, $window, circulatorConnectionStates, circulatorEventDialogConfigProvider, circulatorManager, debugService, $state, locale, programSteps, programTypes, circulatorErrorStates, circulatorErrorStatePriorities, circulatorEventTypes, circulatorEventReasons) {
  return {
    restrict: 'E',
    template: '',
    link: function($scope) {
      var TAG, closePopups, displayErrorPopup, heatedCooks, lastProgram, onCirculatorErrorUpdated, onConnectionUpdated, onProgramStepUpdated, onProgramUpdated, popupPromises, previousProgramStep, showPopup, unbindCirculatorErrorUpdatedHandler, unbindConnectionUpdateHandler, unbindProgramStepUpdatedHandler, unbindProgramUpdateHandler;
      TAG = 'CirculatorEventDialog';
      unbindCirculatorErrorUpdatedHandler = _.noop;
      unbindConnectionUpdateHandler = _.noop;
      unbindProgramStepUpdatedHandler = _.noop;
      unbindProgramUpdateHandler = _.noop;
      popupPromises = {};
      heatedCooks = {};
      closePopups = function() {
        _.forIn(popupPromises, function(value, key) {
          var ref;
          return (ref = popupPromises[key]) != null ? typeof ref.close === "function" ? ref.close() : void 0 : void 0;
        });
        return popupPromises = {};
      };
      showPopup = function(recentEvents) {
        var popupConfig, popupKey;
        debugService.log('Got reply of recent events', TAG, {
          recentEvents: recentEvents
        });
        popupKey = recentEvents.eventReason;
        popupConfig = null;
        if (popupPromises[popupKey] == null) {
          debugService.log('Popup is not displayed already', TAG);
          popupPromises[popupKey] = true;
          popupConfig = circulatorEventDialogConfigProvider.getPopupConfig(recentEvents.eventType, recentEvents.eventReason);
        } else {
          return null;
        }
        if (!popupConfig) {
          return;
        }
        if (popupConfig.type === 'alert') {
          return popupPromises[popupKey] = alertService.alert(popupConfig).then(popupConfig.onDismiss).then(function() {
            return popupPromises[popupKey] = null;
          });
        } else if (popupConfig.type === 'confirm') {
          return popupPromises[popupKey] = alertService.confirm(popupConfig).then(function(confirmation) {
            popupPromises[popupKey] = null;
            if (confirmation) {
              return $state.go('cook');
            }
          });
        }
      };
      displayErrorPopup = function() {
        debugService.log('Fetching error details', TAG);
        return circulatorManager.listRecentEvents().then(function(recentEvents) {
          if (recentEvents.eventReason === circulatorEventReasons.BUTTON_PRESSED) {
            return null;
          } else {
            return showPopup(recentEvents);
          }
        })["catch"](function(error) {
          debugService.error('unhandled error while calling listRecentEvents', TAG, {
            error: error
          });
          return circulatorEventDialogConfigProvider.getPopupConfig(circulatorEventTypes.UNKNOWN_TYPE, circulatorEventReasons.UNKNOWN_REASON);
        });
      };
      onCirculatorErrorUpdated = function(changeDescription) {
        var errorState, oldErrorState;
        if (!changeDescription) {
          return;
        }
        oldErrorState = changeDescription.oldState;
        errorState = changeDescription.newState;
        debugService.log("Circulator error state updated from " + oldErrorState + " to " + errorState, TAG);
        if (circulatorErrorStatePriorities[errorState] > circulatorErrorStatePriorities[oldErrorState]) {
          return displayErrorPopup();
        }
      };
      previousProgramStep = null;
      onProgramStepUpdated = function(newProgramStep) {
        var alertBathTempReachedOptions, baseBathTempReachedOptions, confirmBathTempReachedOptions, currentProgram, currentState, dialogType, guidedCookTempReached, manualCookTempReached, options, ref, ref1;
        currentProgram = circulatorManager.getProgramState();
        currentState = $state.current.name;
        baseBathTempReachedOptions = {
          type: 'alert',
          headerColor: 'alert-green',
          icon: 'success',
          sound: true,
          titleString: locale.getString('circulatorCook.bathReady'),
          bodyString: locale.getString('circulatorCook.addFood'),
          vibrate: 'short'
        };
        confirmBathTempReachedOptions = _.extend({}, baseBathTempReachedOptions, {
          cancelText: locale.getString('general.okay'),
          okText: locale.getString('general.more')
        });
        alertBathTempReachedOptions = _.extend({}, baseBathTempReachedOptions, {
          okText: locale.getString('general.okay')
        });
        manualCookTempReached = (currentProgram != null ? currentProgram.programType : void 0) === programTypes.manual && newProgramStep === programSteps.cook && previousProgramStep === programSteps.preheat;
        guidedCookTempReached = (currentProgram != null ? currentProgram.programType : void 0) === programTypes.automatic && newProgramStep === programSteps.waitForFood && previousProgramStep === programSteps.preheat;
        if (manualCookTempReached || guidedCookTempReached) {
          if (heatedCooks[(ref = currentProgram.programMetadata) != null ? ref.cookId : void 0]) {
            return;
          }
          heatedCooks[(ref1 = currentProgram.programMetadata) != null ? ref1.cookId : void 0] = true;
          if (guidedCookTempReached && currentState === 'cook') {
            return;
          }
          options = currentState === 'cook' ? alertBathTempReachedOptions : confirmBathTempReachedOptions;
          dialogType = currentState === 'cook' ? 'alert' : 'confirm';
          if (!popupPromises.tempReached) {
            popupPromises.tempReached = alertService[dialogType](options).then(function(res) {
              if (res && currentState !== 'cook') {
                $state.go('cook');
              }
              return popupPromises.tempReached = null;
            });
          }
        } else if (newProgramStep === programSteps.error) {
          debugService.log("Program step updated to: " + newProgramStep, TAG);
          displayErrorPopup();
        }
        return previousProgramStep = newProgramStep;
      };
      lastProgram = null;
      onProgramUpdated = function(program) {
        if (lastProgram && !program) {
          lastProgram = null;
          return circulatorManager.listRecentEvents().then(function(recentEvents) {
            if (recentEvents.eventReason === circulatorEventReasons.BUTTON_PRESSED) {
              return showPopup(recentEvents);
            } else {
              return null;
            }
          })["catch"](function(error) {
            debugService.error('unhandled error while calling listRecentEvents', TAG, {
              error: error
            });
            return circulatorEventDialogConfigProvider.getPopupConfig(circulatorEventTypes.UNKNOWN_TYPE, circulatorEventReasons.UNKNOWN_REASON);
          });
        } else {
          return lastProgram = program;
        }
      };
      onConnectionUpdated = function(connectionState) {
        if (connectionState === circulatorConnectionStates.connected) {
          unbindCirculatorErrorUpdatedHandler = circulatorManager.bindCirculatorErrorUpdateHandler(onCirculatorErrorUpdated);
          unbindProgramStepUpdatedHandler = circulatorManager.bindProgramStepUpdateHandlers(onProgramStepUpdated);
          return unbindProgramUpdateHandler = circulatorManager.bindProgramUpdateHandler(onProgramUpdated);
        } else {
          unbindCirculatorErrorUpdatedHandler();
          unbindProgramStepUpdatedHandler();
          unbindProgramUpdateHandler();
          unbindProgramUpdateHandler = _.noop;
          unbindCirculatorErrorUpdatedHandler = _.noop;
          unbindProgramStepUpdatedHandler = _.noop;
          return closePopups();
        }
      };
      unbindConnectionUpdateHandler = circulatorManager.bindCirculatorConnectionUpdateHandler(onConnectionUpdated);
      return $scope.$on('destroy', function() {
        unbindConnectionUpdateHandler();
        unbindCirculatorErrorUpdatedHandler();
        return unbindProgramStepUpdatedHandler();
      });
    }
  };
}]);

this.app.constant('closeButtonStates', {
  close: 'close',
  cancel: 'cancel',
  x: 'x',
  xWhite: 'x-white',
  xBlack: 'x-black',
  skipWifi: 'skip-wifi',
  skip: 'skip'
});

this.app.directive('closeButton', ["$ionicHistory", "colors", "closeButtonStates", function($ionicHistory, colors, closeButtonStates) {
  return {
    restrict: 'E',
    scope: {
      action: '&',
      state: '@',
      color: '@',
      ionicModal: '='
    },
    link: function($scope, $element, $attributes) {
      var color;
      $scope.style = {
        color: (color = colors[$scope.color]) ? color : $scope.color
      };
      return $element.on('click', function() {
        if ($attributes.action) {
          $scope.action();
          return;
        }
        switch ($scope.state) {
          case closeButtonStates.close:
          case closeButtonStates.cancel:
          case closeButtonStates.x:
            return $ionicHistory.goBack();
          case closeButtonStates.xWhite:
          case closeButtonStates.xBlack:
            return $scope.ionicModal.hide();
        }
      });
    },
    replace: true,
    templateUrl: 'templates/directives/close-button/close-button.html'
  };
}]);

this.app.directive('collectionCard', ["appConfig", "collectionService", "assetService", "$state", function(appConfig, collectionService, assetService, $state) {
  return {
    restrict: 'E',
    scope: {
      id: '@'
    },
    link: function($scope, _, attr) {
      var ref;
      $scope.placeholder = (ref = attr.placeholder === 'true') != null ? ref : {
        "true": false
      };
      $scope.getCollectionThumbnail = function(collection) {
        if (collection != null ? collection.thumbnail : void 0) {
          return assetService.pathFor(collection.thumbnail);
        } else {
          return '';
        }
      };
      $scope.collectionClicked = function() {
        if (!$scope.placeholder) {
          return $state.go('collection', {
            slug: $scope.collection.slug
          });
        }
      };
      if ($scope.placeholder) {
        return $scope.collection = {};
      } else {
        return collectionService.get($scope.id).then(function(collection) {
          return $scope.collection = collection;
        });
      }
    },
    templateUrl: 'templates/directives/collection-card/collection-card.html'
  };
}]);

this.app.directive('collectionList', ["appConfig", "collectionService", function(appConfig, collectionService) {
  return {
    restrict: 'E',
    scope: {
      buffer: '@',
      slug: '@',
      itemStyle: '@'
    },
    link: function($scope, _, attr) {
      var ref;
      $scope.placeholder = (ref = attr.placeholder === 'true') != null ? ref : {
        "true": false
      };
      if ($scope.placeholder) {
        return $scope.collectionList = {
          items: [
            {
              type: 'collection'
            }, {
              type: 'collection'
            }, {
              type: 'collection'
            }
          ]
        };
      } else {
        return collectionService.getBySlug($scope.slug).then(function(collectionList) {
          return $scope.collectionList = collectionList;
        });
      }
    },
    templateUrl: 'templates/directives/collection-list/collection-list.html'
  };
}]);

this.app.directive('collectionListing', ["appConfig", "collectionService", "assetService", function(appConfig, collectionService, assetService) {
  return {
    restrict: 'E',
    scope: {
      id: '@'
    },
    link: function($scope) {
      $scope.getCollectionThumbnail = function(collection) {
        if (collection != null ? collection.thumbnail : void 0) {
          return assetService.pathFor(collection.thumbnail);
        } else {
          return '';
        }
      };
      return collectionService.get($scope.id).then(function(collection) {
        return $scope.collection = collection;
      });
    },
    templateUrl: 'templates/directives/collection-listing/collection-listing.html'
  };
}]);

this.app.directive('cookErrorOverlay', ["timerFactory", "utilities", "circulatorManager", "circulatorEventDialogConfigProvider", "$timeout", "circulatorErrorStates", "debugService", "alertService", "locale", "circulatorEventTypes", "circulatorEventReasons", function(timerFactory, utilities, circulatorManager, circulatorEventDialogConfigProvider, $timeout, circulatorErrorStates, debugService, alertService, locale, circulatorEventTypes, circulatorEventReasons) {
  return {
    restrict: 'E',
    replace: true,
    templateUrl: 'templates/directives/cook-error-overlay/cook-error-overlay.html',
    scope: {
      show: '=',
      resumeHandler: '='
    },
    link: function($scope) {
      var TAG, fetchErrorDetails, parseDowntime, resetErrorDetails, shouldShowResume, unbindCirculatorErrorUpdatedHandler, unbindTimestampUpdateHandler;
      TAG = 'cookErrorOverlay';
      $scope.overlayConfig = {};
      $scope.loaded = false;
      $scope.stillInErrorState = true;
      unbindTimestampUpdateHandler = _.noop;
      unbindCirculatorErrorUpdatedHandler = _.noop;
      fetchErrorDetails = function() {
        var getErrorOverlayConfig;
        debugService.log('Fetching error details', TAG);
        getErrorOverlayConfig = function() {
          return circulatorManager.listRecentEvents().then(function(recentEvents) {
            debugService.log('Got reply of recent events', TAG, {
              recentEvents: recentEvents
            });
            $scope.errorTimestamp = recentEvents.eventTimestamp;
            return circulatorEventDialogConfigProvider.getErrorOverlayConfig(recentEvents.eventType, recentEvents.eventReason);
          })["catch"](function(error) {
            debugService.error('unhandled error while calling listRecentEvents', TAG, {
              error: error
            });
            return circulatorEventDialogConfigProvider.getErrorOverlayConfig(circulatorEventTypes.UNKNOWN_TYPE, circulatorEventReasons.UNKNOWN_REASON);
          });
        };
        getErrorOverlayConfig().then(function(overlayConfig) {
          debugService.log('Got configuration for error overlay', TAG, {
            overlayConfig: overlayConfig
          });
          if (!overlayConfig) {
            return;
          }
          $scope.overlayConfig = overlayConfig;
          return $scope.loaded = true;
        });
        unbindTimestampUpdateHandler = circulatorManager.bindCirculatorTimestampUpdateHandler(function(currentTimestamp) {
          var duration;
          duration = Math.floor((currentTimestamp - $scope.errorTimestamp) / 1000);
          parseDowntime(duration);
          return $timeout();
        });
        return unbindCirculatorErrorUpdatedHandler = circulatorManager.bindCirculatorErrorUpdateHandler(function(changeDescription) {
          var newState;
          if (!changeDescription) {
            return;
          }
          newState = changeDescription.newState;
          return $scope.stillInErrorState = (newState != null) && newState !== circulatorErrorStates.NO_ERROR;
        });
      };
      resetErrorDetails = function() {
        unbindTimestampUpdateHandler();
        unbindTimestampUpdateHandler = _.noop;
        unbindCirculatorErrorUpdatedHandler();
        unbindCirculatorErrorUpdatedHandler = _.noop;
        $scope.stillInErrorState = false;
        $scope.loaded = false;
        return $scope.overlayConfig = {};
      };
      parseDowntime = function(downtime) {
        var hours, minutes, ref, seconds;
        ref = utilities.secondsToHMS(downtime), hours = ref.hours, minutes = ref.minutes, seconds = ref.seconds;
        $scope.h = utilities.formatTimeDisplay(hours);
        $scope.m = utilities.formatTimeDisplay(minutes);
        return $scope.s = utilities.formatTimeDisplay(seconds);
      };
      shouldShowResume = function() {
        return $scope.shouldShowResume = (function() {
          if (!$scope.overlayConfig.recoverable) {
            return false;
          }
          if ($scope.overlayConfig.waitForClearToResume && $scope.stillInErrorState) {
            return false;
          }
          return true;
        })();
      };
      $scope.handleClick = function() {
        return $scope.resumeHandler();
      };
      $scope.$watch('show', function(newVal) {
        if (newVal) {
          $scope.loaded = false;
          return fetchErrorDetails();
        } else {
          return resetErrorDetails();
        }
      });
      $scope.$watch('stillInErrorState', shouldShowResume);
      $scope.$watch('overlayConfig', shouldShowResume);
      return $scope.$on('$destroy', function() {
        unbindTimestampUpdateHandler();
        return unbindCirculatorErrorUpdatedHandler();
      });
    }
  };
}]);

this.app.directive('floatingActionButton', function() {
  return {
    restrict: 'E',
    scope: {
      action: '&',
      dismiss: '&',
      state: '='
    },
    link: function($scope) {
      var stateWatcher, updateButton;
      updateButton = function(state) {
        var banner, classes, icon, text;
        classes = state.classes, text = state.text, banner = state.banner, icon = state.icon;
        $scope.classes = classes;
        $scope.text = text;
        $scope.banner = banner;
        $scope.icon = icon;
        if (icon != null) {
          $scope.iconUrl = "svg/" + icon + ".svg#" + icon;
        }
        return $scope.hideMe = false;
      };
      stateWatcher = function(newState) {
        if (newState != null) {
          return updateButton(newState);
        }
      };
      $scope.dismissClicked = function() {
        $scope.hideMe = true;
        return typeof $scope.dismiss === "function" ? $scope.dismiss() : void 0;
      };
      return $scope.$watch('state', stateWatcher, true);
    },
    replace: true,
    templateUrl: 'templates/directives/floating-action-button/floating-action-button.html'
  };
});

this.app.directive('guideCard', ["appConfig", "guideService", "assetService", function(appConfig, guideService, assetService) {
  return {
    restrict: 'E',
    scope: {
      id: '@'
    },
    link: function($scope) {
      $scope.getGuideThumbnail = function(guide) {
        return assetService.pathFor(guide.thumbnail);
      };
      return guideService.get($scope.id).then(function(guide) {
        return $scope.guide = guide;
      });
    },
    templateUrl: 'templates/directives/guide-card/guide-card.html'
  };
}]);

this.app.directive('hero', ["appConfig", "collectionService", "assetService", "$state", function(appConfig, collectionService, assetService, $state) {
  return {
    restrict: 'E',
    scope: {
      slug: '@'
    },
    link: function($scope, _, attr) {
      var ref;
      $scope.placeholder = (ref = attr.placeholder === 'true') != null ? ref : {
        "true": false
      };
      $scope.getHeroThumbnail = function(hero) {
        if (hero != null ? hero.thumbnail : void 0) {
          return assetService.pathFor(hero.thumbnail);
        } else {
          return '';
        }
      };
      collectionService.getBySlug($scope.slug).then(function(hero) {
        return $scope.hero = hero;
      });
      return $scope.heroLink = function() {
        if ($scope.hero.slug === 'home-hero') {
          return $state.go('training');
        } else {
          return $state.go('collection', {
            slug: $scope.hero.items[0].slug
          });
        }
      };
    },
    templateUrl: 'templates/directives/hero/hero.html'
  };
}]);

this.app.directive('imageWithPlaceholder', function() {
  return {
    restrict: 'A',
    scope: {
      imageWithPlaceholder: '@'
    },
    link: function(scope, element) {
      element.addClass('image-with-placeholder');
      return scope.$watch('imageWithPlaceholder', function(newValue) {
        if (newValue) {
          element.removeClass('random-placeholder');
          return element.css('background-image', "url(" + newValue + ")");
        } else {
          element.addClass('random-placeholder');
          return newValue = 'svg/imagePlaceholder.svg#imagePlaceholder';
        }
      });
    }
  };
});

this.app.directive('inputPasswordVisibility', ["debugService", "$timeout", function(debugService, $timeout) {
  return {
    restrict: 'E',
    link: function($scope, $attributes, $element) {
      return $timeout(function() {
        var TAG, container, input, node, onDestroy, onInputKeyUp;
        TAG = 'InputPasswordVisibilityDirective';
        $scope.obfuscated = true;
        node = $element.$$element[0];
        container = node.parentNode;
        input = container.querySelector('input[type="password"]');
        if (!input) {
          debugService.warn('The input-password-visibility directive was used without an adjacent input[type="password"].', TAG);
          return;
        }
        onInputKeyUp = function() {
          return $timeout(function() {
            var ref;
            return $scope.showToggle = ((ref = input.value) != null ? ref.length : void 0) > 0;
          });
        };
        $timeout(function() {
          return input.addEventListener('keyup', onInputKeyUp);
        });
        $scope.toggleObfuscation = function() {
          $scope.obfuscated = !$scope.obfuscated;
          return input.type = $scope.obfuscated ? 'password' : 'text';
        };
        onDestroy = function() {
          return input.removeEventListener('keyup', onInputKeyUp);
        };
        return $scope.$on('$destroy', onDestroy);
      });
    },
    replace: true,
    templateUrl: 'templates/directives/input-password-visibility/input-password-visibility.html'
  };
}]);

this.app.directive('keypad', function() {
  return {
    restrict: 'E',
    scope: {
      value: '=',
      hideDecimal: '@',
      onValueUpdated: '&'
    },
    link: function($scope) {
      var decimal, zero;
      zero = 0;
      decimal = '.';
      $scope.addValue = function(value) {
        if (!$scope.value && (value === decimal || value === zero)) {
          return;
        }
        if ($scope.value && ($scope.value.indexOf(decimal) >= zero) && value === decimal) {
          return;
        }
        $scope.value = ($scope.value || '') + value;
        return typeof $scope.onValueUpdated === "function" ? $scope.onValueUpdated({
          value: $scope.value
        }) : void 0;
      };
      return $scope.clear = function() {
        $scope.value = null;
        return typeof $scope.onValueUpdated === "function" ? $scope.onValueUpdated({
          value: $scope.value
        }) : void 0;
      };
    },
    templateUrl: 'templates/directives/keypad/keypad.html'
  };
});

this.app.directive('loadingIndicator', function() {
  return {
    restrict: 'C',
    scope: {
      hideBePatientMessage: '='
    },
    templateUrl: 'templates/directives/loading-indicator/loading-indicator.html'
  };
});

this.app.directive('menuButton', ["$ionicSideMenuDelegate", "$ionicScrollDelegate", "statusBarService", function($ionicSideMenuDelegate, $ionicScrollDelegate, statusBarService) {
  return {
    restrict: 'E',
    scope: {
      scrollDelegateHandle: '@'
    },
    link: function($scope, $element) {
      var contentNode, isShown, lastScrollY, onScroll;
      statusBarService.getHeightInPixels().then(function(height) {
        return $element.css('top', (height + 10) + "px");
      });
      if ($scope.scrollDelegateHandle) {
        contentNode = $ionicScrollDelegate.$getByHandle($scope.scrollDelegateHandle).getScrollView();
      } else {
        contentNode = $ionicScrollDelegate.getScrollView();
      }
      isShown = true;
      lastScrollY = null;
      onScroll = function(event) {
        var currentScrollY;
        currentScrollY = event.target.scrollTop;
        if (Math.abs(currentScrollY - lastScrollY) < 20) {
          return;
        }
        if (currentScrollY < 50) {
          isShown = true;
          $element.removeClass('menu-button-hidden');
        } else if (lastScrollY && lastScrollY < currentScrollY && isShown) {
          isShown = false;
          $element.addClass('menu-button-hidden');
        } else if (lastScrollY && lastScrollY > currentScrollY && !isShown) {
          isShown = true;
          $element.removeClass('menu-button-hidden');
        }
        return lastScrollY = event.target.scrollTop;
      };
      if (contentNode != null ? contentNode.el : void 0) {
        contentNode.el.addEventListener('scroll', onScroll);
        $scope.$on('$destroy', (function() {
          return contentNode.el.removeEventListener('scroll', onScroll);
        }));
      }
      return $element.on('click', function() {
        return $ionicSideMenuDelegate.toggleLeft();
      });
    },
    templateUrl: 'templates/directives/menu-button/menu-button.html'
  };
}]);

this.app.directive('navigationButton', ["$ionicHistory", "$rootScope", "$state", "appConfig", "colors", "navigationButtonStates", function($ionicHistory, $rootScope, $state, appConfig, colors, navigationButtonStates) {
  return {
    restrict: 'E',
    scope: {
      action: '&',
      state: '@',
      color: '@'
    },
    link: function($scope, $element, $attributes) {
      var color;
      if ($scope.state === navigationButtonStates.back && ionic.Platform.isAndroid()) {
        $element.css('display', 'none');
      }
      $scope.style = {
        color: (color = colors[$scope.color]) ? color : $scope.color
      };
      return $element.on('click', function() {
        if ($attributes.action) {
          $scope.action();
          return;
        }
        switch ($scope.state) {
          case navigationButtonStates.back:
            return $ionicHistory.goBack();
          case navigationButtonStates.home:
            $ionicHistory.nextViewOptions({
              disableBack: true,
              historyRoot: true
            });
            return $state.go(appConfig.defaultView);
        }
      });
    },
    replace: true,
    templateUrl: 'templates/directives/navigation-button/navigation-button.html'
  };
}]);

var TAG;

TAG = 'onPlayDirective';

this.app.directive('onPlay', ["$timeout", "debugService", function($timeout, debugService) {
  return {
    restrict: 'A',
    scope: {
      'onPlay': '&',
      'onLoopLimitReached': '&',
      'loopLimit': '='
    },
    link: function($scope, $element) {
      var bindNotify, hasPlayed, loopCount, notify, onPlaying, onTimeUpdate, prevTime, timeUpdateThreshold, video;
      video = $element[0];
      video.muted = true;
      prevTime = 0;
      loopCount = 0;
      hasPlayed = false;
      timeUpdateThreshold = ionic.Platform.isAndroid() ? 6 : 1;
      notify = angular.noop;
      bindNotify = function() {
        if (!hasPlayed) {
          video.muted = true;
        }
        return notify = _.after(timeUpdateThreshold, _.once(function() {
          var handleAndroidRewind;
          if (ionic.Platform.isAndroid()) {
            handleAndroidRewind = function() {
              onPlaying();
              return $element.off('timeupdate', handleAndroidRewind);
            };
            if (!hasPlayed) {
              $element.on('timeupdate', handleAndroidRewind);
              return video.currentTime = prevTime = 0;
            } else {
              return onPlaying();
            }
          } else {
            return onPlaying();
          }
        }));
      };
      onPlaying = function() {
        video.muted = false;
        hasPlayed = true;
        return $scope.onPlay();
      };
      onTimeUpdate = function() {
        var time;
        if (!video.paused) {
          notify();
        }
        if ($scope.loopLimit) {
          time = video.currentTime;
          if (time < 0.25 && time < prevTime) {
            loopCount += 1;
            if (loopCount >= $scope.loopLimit) {
              debugService.info('Video loop limit of ' + $scope.loopLimit + ' reached for ' + video.currentSrc, [TAG], {
                url: video.currentSrc,
                loopCount: loopCount
              });
              loopCount = 0;
              $timeout(function() {
                video.pause();
                hasPlayed = false;
                video.currentTime = 0;
                return typeof $scope.onLoopLimitReached === "function" ? $scope.onLoopLimitReached() : void 0;
              }, 50);
            }
          }
          prevTime = time;
        }
        return $timeout();
      };
      bindNotify();
      $element.on('timeupdate', onTimeUpdate);
      $element.on('pause', bindNotify);
      return $scope.$on('destroy', function() {
        return $element.off('timeupdate', onTimeUpdate);
      });
    }
  };
}]);

this.app.directive('recipeCard', ["deepLinkService", "recipeService", function(deepLinkService, recipeService) {
  return {
    restrict: 'E',
    scope: {
      slug: '='
    },
    link: function($scope, $element) {
      recipeService.getRecipe($scope.slug).then(function(recipe) {
        return $scope.recipe = recipe;
      });
      return $element.on('click', (function() {
        return deepLinkService.goToRecipe($scope.recipe.id);
      }));
    },
    templateUrl: 'templates/directives/recipe-card/recipe-card.html'
  };
}]);

this.app.directive('sidemenu', ["$ionicSideMenuDelegate", "advertisementService", "$window", "cacheService", "circulatorManager", "circulatorConnectionStates", function($ionicSideMenuDelegate, advertisementService, $window, cacheService, circulatorManager, circulatorConnectionStates) {
  return {
    restrict: 'E',
    link: function($scope, $element) {
      $scope.developerMenuEnabled = function() {
        return cacheService.get('developerMenuEnabled', 'preferences');
      };
      $scope.onRecipesClick = function() {
        $window.open('https://www.chefsteps.com/gallery?tag=Sous%20Vide&generator=chefsteps&published_status=published&difficulty=any&sort=newest&premium=everything', '_system');
        return true;
      };
      $scope.paired = function() {
        return circulatorManager.getCirculatorConnectionState() !== circulatorConnectionStates.unpaired;
      };
      $scope.jouleAdClick = function() {
        advertisementService.openAdTarget({
          campaign: 'hardcoded'
        }, 'sideMenu');
        return true;
      };
      return $element.on('click', (function() {
        return $ionicSideMenuDelegate.toggleLeft();
      }));
    },
    templateUrl: 'templates/directives/sidemenu/sidemenu.html'
  };
}]);

this.app.directive('snackbar', ["$rootScope", function($rootScope) {
  return {
    restrict: 'E',
    link: function($scope) {
      var defaultOptions, hideSnackbar, isShown, showSnackbar;
      isShown = false;
      defaultOptions = {
        duration: 4000,
        modal: false,
        persistent: false
      };
      showSnackbar = function(options) {
        var timeoutID;
        if (options == null) {
          options = defaultOptions;
        }
        angular.extend($scope, options);
        document.body.classList.add('snackbar-shown');
        if (!options.persistent) {
          timeoutID = setTimeout(hideSnackbar, $scope.duration || defaultOptions.duration);
        }
        return $scope.hideSnackBar = function() {
          document.body.classList.remove('snackbar-shown');
          return clearTimeout(timeoutID);
        };
      };
      hideSnackbar = function() {
        document.body.classList.remove('snackbar-shown');
        return setTimeout((function() {
          return isShown = false;
        }), 500);
      };
      return $rootScope.$on('snackbar:show', function(e, options) {
        if (!isShown) {
          return showSnackbar(options);
        }
      });
    },
    templateUrl: 'templates/directives/snackbar/snackbar.html'
  };
}]);

this.app.directive('statusBarAutohide', ["$ionicSideMenuDelegate", "$ionicScrollDelegate", "statusBarService", "statusBarStyles", function($ionicSideMenuDelegate, $ionicScrollDelegate, statusBarService, statusBarStyles) {
  return {
    restrict: 'E',
    scope: {
      scrollDelegateHandle: '@'
    },
    link: function($scope, $element) {
      var contentNode, isShown, lastScrollY, onScroll, statusBarState;
      if ($scope.scrollDelegateHandle) {
        contentNode = $ionicScrollDelegate.$getByHandle($scope.scrollDelegateHandle).getScrollView();
      } else {
        contentNode = $ionicScrollDelegate.getScrollView();
      }
      isShown = true;
      lastScrollY = null;
      statusBarState = function(scrollTop) {
        var currentScrollY;
        currentScrollY = scrollTop;
        if (currentScrollY < 50) {
          $element.addClass('status-bar-transparent');
          $element.addClass('status-bar-hidden');
          statusBarService.setStyle(statusBarStyles.light);
        }
        if (Math.abs(currentScrollY - lastScrollY) < 20) {
          return;
        }
        if (currentScrollY < 50) {
          isShown = true;
          $element.removeClass('status-bar-hidden');
          statusBarService.setStyle(statusBarStyles.light);
        } else if (lastScrollY && lastScrollY < currentScrollY && isShown) {
          isShown = false;
          $element.addClass('status-bar-hidden');
          $element.removeClass('status-bar-transparent');
          statusBarService.setStyle(statusBarStyles.hidden);
        } else if (lastScrollY && lastScrollY > currentScrollY && !isShown) {
          isShown = true;
          $element.removeClass('status-bar-hidden');
          $element.removeClass('status-bar-transparent');
          statusBarService.setStyle(statusBarStyles.light);
        }
        return lastScrollY = scrollTop;
      };
      onScroll = function(event) {
        var ref, ref1;
        return statusBarState(((ref = event.detail) != null ? ref.scrollTop : void 0) || ((ref1 = event.target) != null ? ref1.scrollTop : void 0));
      };
      if (contentNode != null ? contentNode.__container : void 0) {
        contentNode.__container.addEventListener('scroll', onScroll);
      } else if (contentNode != null ? contentNode.el : void 0) {
        contentNode.el.addEventListener('scroll', onScroll);
      }
      $scope.$on('$destroy', function() {
        if (contentNode != null ? contentNode.__container : void 0) {
          return contentNode.__container.removeEventListener('scroll', onScroll);
        } else if (contentNode != null ? contentNode.el : void 0) {
          return contentNode.el.removeEventListener('scroll', onScroll);
        }
      });
      return statusBarState(window.scrollY);
    },
    templateUrl: 'templates/directives/status-bar-autohide/status-bar-autohide.html'
  };
}]);

this.app.directive('statusBarBackground', ["statusBarService", function(statusBarService) {
  return {
    restrict: 'E',
    link: function($scope) {
      return statusBarService.getHeightInPixels().then(function(height) {
        return $scope.backgroundStyle = {
          height: height + "px"
        };
      });
    },
    templateUrl: 'templates/directives/status-bar-background/status-bar-background.html'
  };
}]);

this.app.directive('statusBarSpacer', ["statusBarService", function(statusBarService) {
  return {
    restrict: 'E',
    link: function($scope) {
      return statusBarService.getHeightInPixels().then(function(height) {
        return $scope.spacingStyle = {
          paddingTop: height + "px"
        };
      });
    },
    templateUrl: 'templates/directives/status-bar-spacer/status-bar-spacer.html'
  };
}]);

this.app.directive('stepsCarousel', ["assetService", "preferences", "$timeout", "$window", function(assetService, preferences, $timeout, $window) {
  return {
    restrict: 'E',
    scope: {
      steps: '=',
      startingIndex: '@'
    },
    controller: ["$scope", function($scope) {
      var loadedPosters;
      $scope.videoEnabled = preferences.get('enableVideo');
      loadedPosters = [];
      $scope.shouldDisablePoster = function(index) {
        if (_.includes(loadedPosters, index)) {
          return false;
        } else if (index > ($scope.activeIndex + 1) || index < ($scope.activeIndex - 1)) {
          return true;
        } else {
          loadedPosters.push(index);
          return false;
        }
      };
      $scope.shouldDisableVideo = function($index) {
        return !$scope.videoEnabled || ($index !== $scope.activeIndex);
      };
      $scope.activeIndex = parseInt($scope.startingIndex) || 0;
      $scope.sliderOptions = {
        initialSlide: $scope.activeIndex,
        onTransitionEnd: function(swiper) {
          $scope.activeIndex = swiper.activeIndex;
          return $timeout();
        }
      };
      $scope.getStepImage = function(step) {
        if (!$scope.videoEnabled) {
          return step.noVideoThumbnail || step.image;
        } else {
          return step.image;
        }
      };
      $scope.onLearnMoreClick = function(link) {
        $window.open(link, '_blank');
        return true;
      };
      $scope.nextStep = function() {
        var ref;
        return (ref = $scope.slider) != null ? ref.slideNext() : void 0;
      };
      return $scope.previousStep = function() {
        var ref;
        return (ref = $scope.slider) != null ? ref.slidePrev() : void 0;
      };
    }],
    templateUrl: 'templates/directives/steps-carousel/steps-carousel.html'
  };
}]);

this.app.directive('stepsList', ["guideService", "assetService", "$ionicModal", "$ionicHistory", "$state", "statusBarService", "statusBarStyles", function(guideService, assetService, $ionicModal, $ionicHistory, $state, statusBarService, statusBarStyles) {
  return {
    restrict: 'E',
    scope: {
      steps: '=',
      id: '@'
    },
    link: function($scope) {
      $scope.$watch('id', function() {
        return guideService.get($scope.id).then(function(guide) {
          return $scope.guide = guide;
        });
      });
      $scope.getGuideThumbnail = function(guide) {
        return assetService.pathFor(guide.thumbnail);
      };
      $scope.nextAction = function() {
        return $state.go('guideDoneness', {
          id: $scope.guide.id
        }, {
          reload: true
        });
      };
      $scope.showStartJouleCard = function() {
        var ref, ref1, ref2;
        if (((ref = $ionicHistory.currentView()) != null ? ref.stateName : void 0) === 'guideOverview' || ((ref1 = $ionicHistory.currentView()) != null ? ref1.stateName : void 0) === 'guideDoneness') {
          return $scope.isGuideOverview = true;
        } else if (((ref2 = $ionicHistory.currentView()) != null ? ref2.stateName : void 0) === 'cook') {
          return $scope.isGuideOverview = false;
        }
      };
      return $scope.onStepsClick = function(index) {
        var modalOptions;
        statusBarService.setStyle(statusBarStyles.hidden);
        modalOptions = {
          scope: $scope,
          animation: 'slide-in-up'
        };
        return $ionicModal.fromTemplateUrl('templates/modals/steps/guide-steps-modal.html', modalOptions).then(function(modal) {
          var unbindlistener;
          $scope.modal = modal;
          $scope.startingIndex = index;
          $scope.modal.show();
          $scope.closeModal = function() {
            return $scope.modal.hide();
          };
          return unbindlistener = $scope.$on('modal.hidden', function() {
            var ref;
            unbindlistener();
            if (((ref = $ionicHistory.currentView()) != null ? ref.stateName : void 0) === 'cook') {
              statusBarService.setStyle(statusBarStyles.dark);
            }
            return $scope.modal.remove();
          });
        });
      };
    },
    templateUrl: 'templates/directives/steps-list/steps-list.html'
  };
}]);

this.app.directive('timer', ["$interval", "utilities", function($interval, utilities) {
  return {
    restrict: 'E',
    scope: {
      timer: '=',
      canHideHours: '='
    },
    link: function($scope) {
      var timerUpdateInterval;
      timerUpdateInterval = $interval(angular.noop, 500);
      $scope.$on('$destroy', (function() {
        return $interval.cancel(timerUpdateInterval);
      }));
      $scope.showHour = function() {
        return $scope.timer.getRemainingHours() > 0;
      };
      return $scope.humanizeTime = utilities.humanizeDuration($scope.timer.getRemainingTime());
    },
    templateUrl: 'templates/directives/timer/timer.html'
  };
}]);

this.app.directive('wifiSelector', ["$ionicLoading", "alertService", "debugService", "circulatorManager", "wifiSecurityTypes", "locale", function($ionicLoading, alertService, debugService, circulatorManager, wifiSecurityTypes, locale) {
  return {
    restrict: 'E',
    scope: {
      accessPoints: '=',
      connectAccessPoint: '&'
    },
    link: function($scope) {
      var TAG, selectAccessPoint;
      TAG = 'WifiSelectorDirective';
      selectAccessPoint = function(accessPoint) {
        var securityType, ssid;
        debugService.log('user selected access point', TAG, accessPoint);
        circulatorManager.stopListAccessPoints();
        securityType = accessPoint.securityType, ssid = accessPoint.ssid;
        if (securityType === wifiSecurityTypes.OPEN) {
          return $scope.connectAccessPoint()({
            ssid: ssid,
            password: '',
            securityType: securityType
          });
        } else {
          return alertService.prompt({
            headerColor: 'alert-yellow short',
            titleString: ssid,
            bodyString: locale.getString('pairing.enterAPPassword'),
            inputType: 'password',
            inputPlaceholder: locale.getString('pairing.wifiPasswordPlaceholder')
          }).then(function(response) {
            var password;
            if (!(response != null ? response.primary : void 0)) {
              return;
            }
            password = response.primary;
            return $scope.connectAccessPoint()({
              ssid: ssid,
              password: password,
              securityType: securityType
            });
          });
        }
      };
      $scope.handleClickAccessPoint = selectAccessPoint;
      return this;
    },
    templateUrl: 'templates/directives/wifi-selector/wifi-selector.html'
  };
}]);

this.app.controller('circulatorNameController', ["$scope", "circulatorManager", "$ionicLoading", "$ionicHistory", "$state", "appConfig", "alertService", "circulatorConnectionStates", "locale", "statusBarService", "statusBarStyles", "debugService", "circulatorConnectionError", function($scope, circulatorManager, $ionicLoading, $ionicHistory, $state, appConfig, alertService, circulatorConnectionStates, locale, statusBarService, statusBarStyles, debugService, circulatorConnectionError) {
  var TAG, navigateToNextView, oldName, onBeforeEnter, onConnectionUpdated, onLeave, unbindConnectionUpdateHandler, validate;
  oldName = null;
  $scope.buttonText = '';
  $scope.newName = '';
  TAG = 'PairingCirculatorNameView';
  navigateToNextView = null;
  unbindConnectionUpdateHandler = _.noop;
  validate = function(circulatorName) {
    return !!circulatorName;
  };
  $scope.onInputChange = function(newName) {
    if (!validate(newName)) {
      $scope.buttonClass = 'sign-in-button-disabled style-rounded';
      $scope.buttonText = locale.getString('pairing.namingInvalid');
      return $scope.buttonAction = _.noop;
    } else {
      if (newName === oldName) {
        $scope.buttonClass = 'sign-in-button-active style-rounded';
        $scope.buttonText = locale.getString('pairing.namingDefault');
        return $scope.buttonAction = navigateToNextView;
      } else {
        $scope.buttonClass = 'sign-in-button-active style-rounded';
        $scope.buttonText = locale.getString('pairing.namingValid');
        return $scope.buttonAction = function() {
          if (circulatorManager.getCirculatorConnectionState() !== circulatorConnectionStates.connected) {
            debugService.error('Circulator is not connected while renaming', TAG);
            return $state.go('connectionTroubleshooting');
          } else {
            $ionicLoading.show({
              template: "<div class='loading-indicator' />",
              noBackdrop: true
            });
            return circulatorManager.renameCirculator(newName).then(function() {
              debugService.log('successfully named circulator', TAG);
              $ionicLoading.hide();
              return navigateToNextView();
            })["catch"](function(error) {
              $ionicLoading.hide();
              return circulatorConnectionError.handleErrorWithPopup(error)["catch"](function(error) {
                debugService.error('unhandled error while renaming circulator', TAG, {
                  error: error
                });
                return alertService.confirm({
                  headerColor: 'alert-red',
                  icon: 'fail',
                  titleString: locale.getString('pairing.sorryTitle'),
                  bodyString: locale.getString('pairing.namingErrorHeaderSecondary'),
                  cancelText: locale.getString('general.okay'),
                  okText: locale.getString('general.skip')
                }).then(function(skip) {
                  if (!skip) {
                    return;
                  }
                  return navigateToNextView();
                });
              });
            }).done(_.noop, function(e) {
              return debugService.onPromiseUnhandledRejection(e, TAG);
            });
          }
        };
      }
    }
  };
  onConnectionUpdated = function() {
    var connectionState;
    connectionState = circulatorManager.getCirculatorConnectionState();
    if (connectionState === circulatorConnectionStates.connecting) {
      return $ionicLoading.show({
        template: "<div class='loading-indicator' />",
        noBackdrop: true
      });
    } else {
      return $ionicLoading.hide();
    }
  };
  onBeforeEnter = function() {
    var nextView, ref;
    statusBarService.setStyle(statusBarStyles.dark);
    nextView = (ref = $state.current.data) != null ? ref.nextView : void 0;
    if (nextView) {
      navigateToNextView = function() {
        return $state.go(nextView);
      };
    } else {
      navigateToNextView = function() {
        return $ionicHistory.goBack();
      };
    }
    oldName = circulatorManager.getCurrentCirculatorName();
    $scope.newName = oldName;
    $scope.onInputChange($scope.newName);
    return unbindConnectionUpdateHandler = circulatorManager.bindCirculatorConnectionUpdateHandler(onConnectionUpdated);
  };
  onLeave = function() {
    return unbindConnectionUpdateHandler();
  };
  $scope.$on('$ionicView.beforeEnter', onBeforeEnter);
  return $scope.$on('$ionicView.leave', onLeave);
}]);

this.app.controller('aboutController', ["statusBarService", "statusBarStyles", "$scope", function(statusBarService, statusBarStyles, $scope) {
  var onBeforeEnter;
  $scope.versionNumber = '2.37.1';
  onBeforeEnter = function() {
    return statusBarService.setStyle(statusBarStyles.dark);
  };
  return $scope.$on('$ionicView.beforeEnter', onBeforeEnter);
}]);

this.app.controller('circulatorWifiController', ["debugService", "locale", "$ionicLoading", "$ionicHistory", "$window", "wifiSecurityTypes", "circulatorManager", "statusBarService", "alertService", "statusBarStyles", "$scope", "$state", "$timeout", "circulatorConnectionStates", "NonOwnerError", "NullOwnerError", "circulatorConnectionError", "faqLinkConfig", "circulatorWifiStatusService", "cacheService", "$ionicPlatform", function(debugService, locale, $ionicLoading, $ionicHistory, $window, wifiSecurityTypes, circulatorManager, statusBarService, alertService, statusBarStyles, $scope, $state, $timeout, circulatorConnectionStates, NonOwnerError, NullOwnerError, circulatorConnectionError, faqLinkConfig, circulatorWifiStatusService, cacheService, $ionicPlatform) {
  var TAG, accessPointsMap, cachedStrengthsMap, checkConnectionState, checkOwnership, initialize, navigateToNextView, onBeforeEnter, onCirculatorConnectionUpdated, onConnectAccessPointFail, onConnectAccessPointSuccess, onLeave, onPause, onResume, onWifiScanUpdate, onWifiScanUpdateUnthrottled, pauseHandler, resumeHandler, sadCloudTimer, smoothRSSI, startWifiScan, stopWifiScan, unbindCirculatorConnectionUpdateHandler, unbindOwnershipCheckConnectionUpdateHandler, wifiScanEventEmitter;
  TAG = 'CirculatorWifiView';
  wifiScanEventEmitter = null;
  $scope.accessPoints = [];
  accessPointsMap = {};
  cachedStrengthsMap = {};
  navigateToNextView = null;
  unbindCirculatorConnectionUpdateHandler = _.noop;
  unbindOwnershipCheckConnectionUpdateHandler = _.noop;
  resumeHandler = _.noop;
  pauseHandler = _.noop;
  sadCloudTimer = null;
  checkConnectionState = function(connectionState) {
    var connected, disconnected;
    connected = circulatorConnectionStates.connected, disconnected = circulatorConnectionStates.disconnected;
    $scope.isPaired = _.includes([connected, disconnected], connectionState);
    $scope.connected = _.includes([connected], connectionState);
    return $timeout();
  };
  $scope.connectAccessPoint = function(options) {
    var loggedOptions, passwordHash, salt;
    salt = cacheService.get('salt', 'security');
    passwordHash = $window.bcrypt.hashSync(options.password || '', salt);
    loggedOptions = _.extend(_.omit(options, 'password'), {
      passwordHash: passwordHash
    });
    debugService.log('attempting to connect to access point', TAG, loggedOptions);
    $ionicLoading.show({
      template: "<div class='loading-indicator' />",
      noBackdrop: true
    });
    return circulatorManager.connectAccessPoint(options).then(onConnectAccessPointSuccess)["catch"](onConnectAccessPointFail).done(_.noop, function(e) {
      return debugService.onPromiseUnhandledRejection(e, TAG);
    });
  };
  $scope.successAction = function(isSkipped) {
    var alertOptions;
    if (isSkipped) {
      alertOptions = {
        headerColor: 'alert-green',
        icon: 'connecting-joule',
        titleString: locale.getString('pairing.wifiUpsellHeaderPrimary'),
        bodyString: locale.getString('pairing.wifiUpsellBody'),
        cancelText: locale.getString('pairing.wifiUpsellButtonSecondary'),
        okText: locale.getString('pairing.wifiUpsellButtonPrimary')
      };
      return alertService.confirm(alertOptions).then(function(ok) {
        if (!ok) {
          debugService.log('Skipped Wifi', TAG);
          return navigateToNextView();
        }
      });
    } else {
      return navigateToNextView();
    }
  };
  onConnectAccessPointSuccess = function() {
    debugService.log('connect to access point attempt succeeded', TAG);
    return circulatorManager.getWifiStatus().then(function(wifiStatus) {
      $ionicLoading.hide();
      debugService.log('Wifi status after a successful connect', TAG, {
        wifiStatus: wifiStatus
      });
      if (circulatorWifiStatusService.isHealthyWifiStatus(wifiStatus)) {
        return alertService.alert({
          headerColor: 'alert-green',
          icon: 'success',
          titleString: locale.getString('popup.wifiConnectSuccessTitle'),
          bodyString: locale.getString('popup.wifiConnectSuccessDescription')
        }).then(function() {
          return $scope.successAction();
        });
      } else {
        return alertService.alert({
          headerColor: 'alert-green',
          icon: 'success',
          titleString: locale.getString('popup.wifiStillConnectingTitle'),
          bodyString: locale.getString('popup.wifiStillConnectingDescription')
        }).then(function() {
          return $scope.successAction();
        });
      }
    })["catch"](function(error) {
      $ionicLoading.hide();
      throw error;
    });
  };
  onConnectAccessPointFail = function(error) {
    $ionicLoading.hide();
    return circulatorConnectionError.handleErrorWithPopup(error)["catch"](function(error) {
      var alertOptions;
      debugService.error('connect to access point attempt failed', TAG, {
        error: error
      });
      alertOptions = {
        headerColor: 'alert-yellow',
        icon: 'fail',
        titleString: locale.getString('popup.wifiConnectFailTitle'),
        bodyString: locale.getString('popup.wifiConnectFailDescription'),
        cancelText: locale.getString('general.cancel'),
        okText: locale.getString('popup.letsDoIt'),
        link: faqLinkConfig.cantConnectToWifi,
        okAction: function() {
          debugService.log('User selected reconnecting', TAG);
          $scope.accessPoints = [];
          stopWifiScan();
          startWifiScan();
          return $timeout();
        }
      };
      return alertService.confirm(alertOptions).then(function(okAction) {
        if (okAction) {
          return alertOptions.okAction();
        }
      });
    }).done(_.noop, function(e) {
      return debugService.onPromiseUnhandledRejection(e, TAG);
    });
  };
  $scope.handleClickJoinOtherNetwork = function() {
    return alertService.show({
      templateUrl: 'templates/alerts/join-other-network.html',
      headerColor: 'alert-yellow',
      titleString: locale.getString('popup.joinAnotherNetworkTitle'),
      inputType: 'text',
      inputPlaceholder: locale.getString('pairing.otherNetworkPlaceholder'),
      buttons: [
        {
          text: locale.getString('general.cancel'),
          type: 'button',
          onTap: function() {
            return 'cancel';
          }
        }, {
          text: locale.getString('general.okay'),
          type: 'button-positive'
        }
      ]
    }).then(function(response) {
      var options;
      if (!response) {
        return;
      }
      $ionicLoading.show({
        template: "<div class='loading-indicator' />",
        noBackdrop: true
      });
      options = {
        ssid: response.ssid,
        password: response.password,
        securityType: (function() {
          switch (response.securityType) {
            case 'WPA':
              return wifiSecurityTypes.WPA;
            case 'WPA2':
              return wifiSecurityTypes.WPA2;
            default:
              return wifiSecurityTypes.OPEN;
          }
        })()
      };
      return $scope.connectAccessPoint(options);
    });
  };
  smoothRSSI = function(ssid, rssi) {
    var cachedStrengths, i, len, smoothedStrengthTotal, strength;
    if (!cachedStrengthsMap[ssid]) {
      cachedStrengthsMap[ssid] = [];
    }
    cachedStrengths = cachedStrengthsMap[ssid];
    cachedStrengths.push(rssi);
    if (cachedStrengths.length > 20) {
      cachedStrengths.pop();
    }
    smoothedStrengthTotal = 0;
    for (i = 0, len = cachedStrengths.length; i < len; i++) {
      strength = cachedStrengths[i];
      smoothedStrengthTotal += strength;
    }
    return smoothedStrengthTotal / cachedStrengths.length;
  };
  onWifiScanUpdateUnthrottled = function(rawAP) {
    var ap;
    debugService.log('access point found: ' + rawAP.ssid, TAG, rawAP);
    ap = accessPointsMap[rawAP.ssid] || (accessPointsMap[rawAP.ssid] = angular.copy(rawAP));
    ap.rssi = smoothRSSI(ap.ssid, rawAP.rssi);
    ap.signalStrength = (function() {
      switch (false) {
        case !(ap.rssi <= -80):
          return 'weak';
        case !(ap.rssi <= -60 && ap.rssi > -80):
          return 'medium';
        case !(ap.rssi <= 0 && ap.rssi > -60):
          return 'strong';
      }
    })();
    $scope.accessPoints = _.sortBy(_.values(accessPointsMap), 'rssi').reverse();
    $scope.networksFound = $scope.accessPoints.length;
    $timeout.cancel(sadCloudTimer);
    return $timeout();
  };
  onWifiScanUpdate = _.throttle(onWifiScanUpdateUnthrottled, 500);
  stopWifiScan = function() {
    $scope.scanning = false;
    return circulatorManager.stopListAccessPoints();
  };
  startWifiScan = function() {
    debugService.log('starting wifi scan', TAG);
    $scope.scanning = true;
    wifiScanEventEmitter = circulatorManager.listAccessPoints();
    return wifiScanEventEmitter.on('update', onWifiScanUpdate);
  };
  onCirculatorConnectionUpdated = function(circulatorConnectionState) {
    debugService.log("On wi-fi page, circulator connection state updated to " + circulatorConnectionState, TAG, {
      circulatorConnectionState: circulatorConnectionState,
      ownershipChecked: $scope.ownershipChecked
    });
    checkConnectionState(circulatorConnectionState);
    if (_.every([circulatorConnectionState === circulatorConnectionStates.connected, $scope.ownershipChecked, $state.current.controller === 'circulatorWifiController'])) {
      stopWifiScan();
      startWifiScan();
      $scope.showSadCloud = false;
      sadCloudTimer = $timeout(function() {
        if ($scope.accessPoints.length === 0) {
          return $scope.showSadCloud = true;
        }
      }, 10000);
    }
    return $timeout();
  };
  checkOwnership = function() {
    return circulatorManager.getIsOwner().then(function(isOwner) {
      if (isOwner) {
        debugService.log('User is the owner, rendering circulator wifi view', TAG);
        return circulatorManager.disconnectAccessPoint();
      } else if (isOwner === null) {
        return window.Q.reject(new NullOwnerError('Unable to determine ownership'));
      } else {
        return $window.Q.reject(new NonOwnerError('User is not the owner of Joule'));
      }
    })["catch"](function(error) {
      debugService.log('checkOwnership has failed.  Leaving circulator wifi view and continue to the next view', TAG, {
        error: error
      });
      return circulatorConnectionError.handleErrorWithPopup(error).then(function() {
        return navigateToNextView();
      })["catch"](function(error) {
        debugService.error('Unexpected error', TAG, {
          error: error
        });
        return alertService.alert({
          headerColor: 'alert-red',
          icon: 'fail',
          titleString: locale.getString('popup.wifiErrorTitle'),
          bodyString: locale.getString('popup.wifiErrorDescription')
        }).then(function() {
          return navigateToNextView();
        });
      });
    });
  };
  onBeforeEnter = function() {
    var ref;
    statusBarService.setStyle(statusBarStyles.dark);
    $scope.nextView = (ref = $state.current.data) != null ? ref.nextView : void 0;
    debugService.log('Setting up navigation to next view', TAG, {
      nextView: $scope.nextView
    });
    if ($scope.nextView) {
      navigateToNextView = function() {
        return $state.go($scope.nextView);
      };
    } else {
      navigateToNextView = function() {
        return $ionicHistory.goBack();
      };
    }
    return initialize();
  };
  initialize = function() {
    unbindOwnershipCheckConnectionUpdateHandler = circulatorManager.bindCirculatorConnectionUpdateHandler(function(circulatorConnectionState) {
      debugService.log("On wi-fi page while waiting for ownership check, circulator connection state updated to " + circulatorConnectionState, TAG, {
        circulatorConnectionState: circulatorConnectionState
      });
      checkConnectionState(circulatorConnectionState);
      if (circulatorConnectionState === circulatorConnectionStates.connected) {
        unbindOwnershipCheckConnectionUpdateHandler();
        return checkOwnership().then(function() {
          debugService.log('Ownership check complete', TAG);
          $scope.ownershipChecked = true;
          return unbindCirculatorConnectionUpdateHandler = circulatorManager.bindCirculatorConnectionUpdateHandler(onCirculatorConnectionUpdated);
        }).done(_.noop, function(e) {
          return debugService.onPromiseUnhandledRejection(e, TAG);
        });
      }
    });
    $scope.isPaired = false;
    $scope.connected = false;
    $scope.showSadCloud = false;
    $scope.accessPoints = [];
    $scope.networksFound = false;
    stopWifiScan();
    $timeout.cancel(sadCloudTimer);
    $timeout();
    resumeHandler();
    pauseHandler();
    return pauseHandler = $ionicPlatform.on('pause', onPause);
  };
  onLeave = function() {
    stopWifiScan();
    unbindCirculatorConnectionUpdateHandler();
    unbindOwnershipCheckConnectionUpdateHandler();
    if (wifiScanEventEmitter != null) {
      wifiScanEventEmitter.removeAllListeners();
    }
    return pauseHandler();
  };
  onResume = function() {
    debugService.log('Application resumed while on wi-fi page', TAG);
    return initialize();
  };
  onPause = function() {
    debugService.log('Application paused while on wi-fi page', TAG);
    onLeave();
    resumeHandler();
    return resumeHandler = $ionicPlatform.on('resume', onResume);
  };
  $scope.$on('$ionicView.beforeEnter', onBeforeEnter);
  return $scope.$on('$ionicView.leave', onLeave);
}]);

this.app.controller('collectionController', ["circulatorConnectionStates", "$timeout", "$rootScope", "$scope", "$stateParams", "$state", "collectionService", "$ionicLoading", "circulatorManager", "cookStates", "alertService", "$ionicHistory", "utilities", "statusBarService", "statusBarStyles", "floatingActionButtonStates", "locale", "assetService", "debugService", "circulatorConnectionError", "firmwareUpdateService", "programSteps", "cacheService", "circulatorConnectingMaybeStates", "circulatorWifiStatusService", function(circulatorConnectionStates, $timeout, $rootScope, $scope, $stateParams, $state, collectionService, $ionicLoading, circulatorManager, cookStates, alertService, $ionicHistory, utilities, statusBarService, statusBarStyles, floatingActionButtonStates, locale, assetService, debugService, circulatorConnectionError, firmwareUpdateService, programSteps, cacheService, circulatorConnectingMaybeStates, circulatorWifiStatusService) {
  var TAG, firmwareUpdateAvailable, firmwareUpdateDismissed, onBeforeEnter, onConnectionUpdated, onLeave, onLoaded, shouldPromptForFirmwareUpdate, unbindBathTemperatureUpdateHandler, unbindConnectionUpdateHandler, unbindCookStateUpdateHandler, unbindProgramStepUpdatedHandler, updateButton, wifiStatusChecked;
  TAG = 'CollectionView';
  $scope.buttonAction = null;
  $scope.buttonState = floatingActionButtonStates.jouleFound;
  $scope.assetService = assetService;
  firmwareUpdateAvailable = false;
  firmwareUpdateDismissed = false;
  wifiStatusChecked = false;
  $scope.skipTutorial = function() {
    cacheService.set('isTraining', 'training', false);
    return $scope.isTraining = false;
  };
  $scope.getCollectionThumbnail = function(collection) {
    if (collection != null ? collection.thumbnail : void 0) {
      return assetService.pathFor(collection.thumbnail);
    } else {
      return '';
    }
  };
  onConnectionUpdated = function() {
    var connectionState;
    connectionState = circulatorManager.getCirculatorConnectionState();
    if (connectionState !== circulatorConnectionStates.connected) {
      firmwareUpdateAvailable = false;
      firmwareUpdateDismissed = false;
    } else {
      firmwareUpdateService.newManifestAvailable().then(function(updateAvailable) {
        firmwareUpdateAvailable = updateAvailable;
        return updateButton();
      })["catch"](function(error) {
        debugService.error('Error while retrieving FW update manifest', TAG, {
          error: error
        });
        firmwareUpdateAvailable = false;
        return updateButton();
      }).done(_.noop, function(e) {
        return debugService.onPromiseUnhandledRejection(e, TAG);
      });
    }
    if (connectionState === circulatorConnectionStates.connected) {
      if (!wifiStatusChecked) {
        wifiStatusChecked = true;
        circulatorManager.getIsOwner().then(function(isOwner) {
          if (isOwner) {
            return circulatorManager.getWifiStatus().then(function(wifiStatus) {
              if ((wifiStatus != null) && !circulatorWifiStatusService.isHealthyWifiStatus(wifiStatus)) {
                return circulatorWifiStatusService.handleUnhealthyWifiStatusWithPopup(wifiStatus);
              }
            });
          }
        })["catch"](function(error) {
          return debugService.warn('Got error while checking for wifi status', TAG, {
            error: error
          });
        }).done(_.noop, function(e) {
          return debugService.onPromiseUnhandledRejection(e, TAG);
        });
      }
    }
    return updateButton();
  };
  shouldPromptForFirmwareUpdate = function() {
    return _.every([firmwareUpdateAvailable, !$scope.isTraining, !firmwareUpdateDismissed, $ionicHistory.currentView().stateName === 'home']);
  };

  /* eslint-disable no-unreachable */
  updateButton = function() {
    var bathTemp, connectionState, cookState, maybeState, programStepState;
    connectionState = circulatorManager.getCirculatorConnectionState();
    cookState = circulatorManager.getCirculatorCookState();
    programStepState = circulatorManager.getProgramStepState();
    bathTemp = utilities.roundTo(utilities.modelToDisplayTemperature(circulatorManager.getBathTemperatureState()), 0);
    debugService.debug('updating floating action button', TAG, {
      connectionState: connectionState,
      cookState: cookState,
      bathTemp: bathTemp,
      firmwareUpdateAvailable: firmwareUpdateAvailable,
      firmwareUpdateDismissed: firmwareUpdateDismissed
    });
    $scope.anyJouleSeen = true;
    switch (connectionState) {
      case circulatorConnectionStates.unpaired:
        $scope.anyJouleSeen = false;
        circulatorManager.createCirculatorScanSession();
        $scope.buttonState = floatingActionButtonStates.unpaired;
        $scope.buttonAction = function() {
          return $state.go('buyJoule');
        };
        return $timeout();
      case circulatorConnectionStates.jouleFound:
        $scope.buttonState = floatingActionButtonStates.jouleFound;
        $scope.buttonAction = function() {
          return $state.go('pairingSequencePrompt');
        };
        return $timeout();
      case circulatorConnectionStates.connected:
        if (shouldPromptForFirmwareUpdate()) {
          $scope.buttonState = floatingActionButtonStates.firmwareUpdateAvailable;
          $scope.buttonAction = function() {
            firmwareUpdateAvailable = false;
            firmwareUpdateDismissed = false;
            return $state.go('firmwareUpdate');
          };
          $scope.dismissAction = function() {
            firmwareUpdateDismissed = true;
            $scope.dismissAction = _.noop;
            return updateButton();
          };
          return $timeout();
        } else {
          switch (cookState) {
            case cookStates.idle:
              $scope.buttonState = floatingActionButtonStates.connected;
              $scope.buttonAction = function() {
                return $state.go('temperatureEntry');
              };
              return $timeout();
            case cookStates.cooking:
              $scope.buttonState = programStepState === programSteps.error ? floatingActionButtonStates.error : floatingActionButtonStates.cooking;
              $scope.buttonState.text = bathTemp;
              $scope.buttonAction = function() {
                $ionicHistory.nextViewOptions({
                  disableBack: true,
                  historyRoot: true
                });
                return $state.go('cook');
              };
              return $timeout();
            default:
              $scope.buttonState = floatingActionButtonStates.connecting;
              $scope.buttonAction = circulatorConnectionError.handleConnectingWithPopup;
              return $timeout();
          }
        }
        break;
      case circulatorConnectionStates.connecting:
        maybeState = circulatorManager.getCirculatorConnectingMaybeState();
        switch (maybeState) {
          case circulatorConnectingMaybeStates.maybeCooking:
            $scope.buttonState = floatingActionButtonStates.connectingMaybeCooking;
            $scope.buttonState.text = bathTemp;
            $scope.buttonAction = function() {
              $ionicHistory.nextViewOptions({
                disableBack: true,
                historyRoot: true
              });
              return $state.go('cook');
            };
            return $timeout();
          case circulatorConnectingMaybeStates.maybeIdle:
            $scope.buttonState = floatingActionButtonStates.connectingMaybeIdle;
            $scope.buttonAction = function() {
              return $state.go('temperatureEntry');
            };
            return $timeout();
          case circulatorConnectingMaybeStates.maybeDisconnected:
            $scope.buttonState = floatingActionButtonStates.connectingMaybeDisconnected;
            $scope.buttonAction = circulatorConnectionError.handleConnectingWithPopup;
            return $timeout();
        }
        break;
      case circulatorConnectionStates.disconnected:
        $scope.buttonState = floatingActionButtonStates.disconnected;
        $scope.buttonAction = function() {
          return $state.go('connectionTroubleshooting');
        };
        return $timeout();
    }
  };

  /* eslint-enable no-unreachable */
  onLoaded = function() {
    return collectionService.getBySlug($stateParams.slug).then(function(collection) {
      $scope.collection = collection;
      $scope.stepsOnly = _.every(collection.items, function(item) {
        return item.type === 'step';
      });
      if ($scope.stepsOnly) {
        collectionService.getBySlug($stateParams.slug, {
          populate: true
        }).then(function(steps) {
          return $scope.steps = steps;
        });
        return statusBarService.setStyle(statusBarStyles.hidden);
      }
    });
  };
  unbindConnectionUpdateHandler = _.noop;
  unbindBathTemperatureUpdateHandler = _.noop;
  unbindCookStateUpdateHandler = _.noop;
  unbindProgramStepUpdatedHandler = _.noop;
  onBeforeEnter = function() {
    var isTraining, splashView;
    splashView = document.querySelector('ion-view.pane.splash[nav-view="entering"]');
    if (splashView) {
      debugService.log('Removing splash view from DOM, as Ionic failed to purge it after transition.', TAG);
      splashView.remove();
    }
    $scope.collectionSlug = $stateParams.slug;
    unbindConnectionUpdateHandler = circulatorManager.bindCirculatorConnectionUpdateHandler(onConnectionUpdated);
    unbindBathTemperatureUpdateHandler = circulatorManager.bindBathTemperatureUpdateHandler(updateButton);
    unbindCookStateUpdateHandler = circulatorManager.bindCirculatorCookUpdateHandler(updateButton);
    unbindProgramStepUpdatedHandler = circulatorManager.bindProgramStepUpdateHandlers(updateButton);
    if (!isTraining && $ionicHistory.currentStateName() === 'home') {
      statusBarService.setStyle(statusBarStyles.light);
    } else {
      statusBarService.setStyle(statusBarStyles.dark);
    }
    $scope.isTraining = false;
    isTraining = cacheService.get('isTraining', 'training');
    if (isTraining) {
      $timeout((function() {
        return $scope.isTraining = true;
      }), 1000);
      return statusBarService.setStyle(statusBarStyles.hidden);
    }
  };
  onLeave = function() {
    firmwareUpdateAvailable = false;
    unbindConnectionUpdateHandler();
    unbindBathTemperatureUpdateHandler();
    unbindCookStateUpdateHandler();
    return unbindProgramStepUpdatedHandler();
  };
  $scope.$on('$ionicView.loaded', onLoaded);
  $scope.$on('$ionicView.beforeEnter', onBeforeEnter);
  return $scope.$on('$ionicView.leave', onLeave);
}]);

this.app.controller('connectionTroubleshootingController', ["$scope", "locale", "$timeout", "$state", "$window", "$ionicLoading", "$ionicHistory", "$ionicPlatform", "appConfig", "alertService", "BluetoothPowerError", "bluetoothStates", "statusBarService", "statusBarStyles", "debugService", "disconnectReasons", "connectionProvidersConfig", "circulatorConnectionError", "circulatorManager", "circulatorConnectionStates", "circulatorWifiStatusService", "connectionTroubleshootingService", "loggingTags", "utilities", function($scope, locale, $timeout, $state, $window, $ionicLoading, $ionicHistory, $ionicPlatform, appConfig, alertService, BluetoothPowerError, bluetoothStates, statusBarService, statusBarStyles, debugService, disconnectReasons, connectionProvidersConfig, circulatorConnectionError, circulatorManager, circulatorConnectionStates, circulatorWifiStatusService, connectionTroubleshootingService, loggingTags, utilities) {
  var TAG, existingPromise, getBluetoothTroubleshootRecommendations, getWebSocketTroubleshootRecommendations, onAppPause, onBeforeEnter, onConnectionUpdated, onLeave, retryConnection, showConnectedUI, showDisconnectedUI, unbindAppPauseHandler, unbindAppResumeHandler, unbindConnectionUpdateHandler;
  TAG = 'ConnectionTroubleshootingView';
  unbindConnectionUpdateHandler = _.noop;
  unbindAppPauseHandler = _.noop;
  unbindAppResumeHandler = _.noop;
  existingPromise = null;
  $scope.cancelButtonAction = function() {
    debugService.log('User selected cancel', TAG);
    return $ionicHistory.goBack();
  };
  showDisconnectedUI = function() {
    var bluetoothConnection, ref, webSocketConnection;
    if (existingPromise == null) {
      debugService.log('Attempt to populate a list of recommendations', TAG);
      bluetoothConnection = null;
      webSocketConnection = null;
      _.each(circulatorManager.getCurrentCirculatorConnections(), function(connection) {
        if (connection.type === connectionProvidersConfig.bluetooth.type) {
          return bluetoothConnection = connection;
        } else if (connection.type === connectionProvidersConfig.webSocket.type) {
          return webSocketConnection = connection;
        }
      });
      if (!(((ref = $scope.messages) != null ? ref.length : void 0) > 0)) {
        $ionicLoading.show({
          template: "<div class='loading-indicator' />",
          noBackdrop: true
        });
      }
      return existingPromise = $window.Q.all([getBluetoothTroubleshootRecommendations(bluetoothConnection), getWebSocketTroubleshootRecommendations(webSocketConnection)]).done(function(recommendations) {
        var initialDisconnectTime, now, retry, totalDisconnectTime;
        $ionicLoading.hide();
        retry = false;
        if (circulatorManager.getCirculatorConnectionState() !== circulatorConnectionStates.connected && $state.current.controller === 'connectionTroubleshootingController') {
          debugService.log('Showing recommendations', [TAG, loggingTags.connectionTroubleshooting], {
            recommendations: recommendations
          });
          $scope.messages = [];
          $scope.showSuggestion = false;
          $scope.showPrimaryButton = true;
          $scope.showCancelButton = true;
          $scope.primaryButtonText = locale.getString('connectionTroubleshooting.retryButtonText');
          $scope.primaryButtonAction = retryConnection;
          _.each(recommendations, function(recommendation) {
            if (recommendation != null) {
              $scope.messages.push(recommendation.message);
              if (recommendation.retry) {
                retry = recommendation.retry;
              }
              if (recommendation.action != null) {
                $scope.primaryButtonText = recommendation.actionText;
                return $scope.primaryButtonAction = recommendation.action;
              }
            }
          });
          initialDisconnectTime = connectionTroubleshootingService.getInitialDisconnectTime();
          if (initialDisconnectTime != null) {
            now = new Date();
            totalDisconnectTime = now - initialDisconnectTime;
            debugService.log('Total disconnect time', TAG, {
              accumulativeTime: totalDisconnectTime
            });
            if (totalDisconnectTime > utilities.convertMinutesToMilliseconds(1)) {
              $scope.showSuggestion = true;
              $scope.suggestionText = locale.getString('connectionTroubleshooting.moreHelpText');
              $scope.suggestionAction = function() {
                debugService.log('User selected more help', TAG);
                return $state.go('support');
              };
            }
          }
          $timeout();
          if (retry && (initialDisconnectTime != null)) {
            debugService.log('Retry to refresh recommendations', TAG);
            return circulatorManager.resetAllConnections().then(function() {
              return existingPromise = null;
            });
          } else {
            return existingPromise = null;
          }
        } else {
          debugService.log('Ignoring troubleshooting recommendations', TAG, {
            controller: $state.current.controller,
            connectionState: circulatorManager.getCirculatorConnectionState()
          });
          return existingPromise = null;
        }
      });
    }
  };
  getBluetoothTroubleshootRecommendations = function(bluetoothConnection) {
    return $window.Q.promise(function(resolve) {
      var bluetoothDisconnectReason;
      bluetoothDisconnectReason = bluetoothConnection.disconnectReason;
      debugService.log('Handling bluetooth disconnection', [TAG, loggingTags.connectionTroubleshooting], {
        disconnectReason: bluetoothDisconnectReason
      });
      if (bluetoothDisconnectReason === disconnectReasons.powerError) {
        return bluetoothConnection.powerState().then(function() {
          debugService.error('Disconnect reason is power error but Bluetooth is healthy and turned on', [TAG, loggingTags.connectionTroubleshooting]);
          return resolve({
            message: locale.getString('connectionTroubleshooting.bluetoothGeneralDisconnectReason'),
            retry: true
          });
        })["catch"](function(error) {
          var handleable, ref, templateOptions;
          if (error instanceof BluetoothPowerError && error.bluetoothPowerState === bluetoothStates.poweredOff) {
            return resolve({
              message: locale.getString('connectionTroubleshooting.bluetoothOffError')
            });
          } else {
            ref = circulatorConnectionError.handleBluetoothError(error), templateOptions = ref.templateOptions, handleable = ref.handleable;
            if (handleable) {
              return resolve({
                message: templateOptions.description
              });
            } else {
              debugService.error('Unexpected bluetooth power state error while troubleshooting', [TAG, loggingTags.connectionTroubleshooting], {
                error: error
              });
              return resolve({
                message: locale.getString('connectionTroubleshooting.bluetoothRestartError')
              });
            }
          }
        }).done(_.noop, function(e) {
          return debugService.onPromiseUnhandledRejection(e, TAG);
        });
      } else if (bluetoothDisconnectReason === disconnectReasons.gattFailure) {
        return resolve({
          message: locale.getString('connectionTroubleshooting.bluetoothGattFailure')
        });
      } else {
        $scope.isScanning = true;
        return circulatorManager.scanForEndpoints().then(function(scanResults) {
          var matchingCirculator;
          if (_.isEmpty(scanResults)) {
            debugService.log('Did not find any circulator nearby', [TAG, loggingTags.connectionTroubleshooting]);
            return resolve({
              message: locale.getString('connectionTroubleshooting.bluetoothCouldNotFindCirculator'),
              retry: true
            });
          } else {
            matchingCirculator = _.find(scanResults, {
              'address': bluetoothConnection.circulatorAddress
            });
            if (matchingCirculator != null) {
              debugService.log('Found matching circulator nearby', [TAG, loggingTags.connectionTroubleshooting], {
                matchingCirculator: matchingCirculator
              });
              return resolve({
                message: locale.getString('connectionTroubleshooting.bluetoothGeneralDisconnectReason'),
                retry: true
              });
            } else {
              debugService.log('Found different circulators nearby', [TAG, loggingTags.connectionTroubleshooting]);
              return resolve({
                message: locale.getString('connectionTroubleshooting.bluetoothFoundOtherCirculators'),
                action: function() {
                  debugService.log('User selected connect to new Joule', TAG);
                  return circulatorConnectionError.handleRepair();
                },
                actionText: locale.getString('connectionTroubleshooting.connectToNewJouleButtonText'),
                retry: true
              });
            }
          }
        })["catch"](function(error) {
          debugService.error('Unexpected scan error while troubleshooting', [TAG, loggingTags.connectionTroubleshooting], {
            error: error
          });
          return resolve({
            message: locale.getString('connectionTroubleshooting.bluetoothRestartError')
          });
        })["finally"](function() {
          return $scope.isScanning = false;
        }).done(_.noop, function(e) {
          return debugService.onPromiseUnhandledRejection(e, TAG);
        });
      }
    });
  };
  getWebSocketTroubleshootRecommendations = function(webSocketConnection) {
    return $window.Q.promise(function(resolve) {
      return circulatorManager.getIsOwner().then(function(isOwner) {
        var lastKnownWifiStatus, wifiDisconnectReason;
        debugService.log('Got user ownership status', [TAG, loggingTags.connectionTroubleshooting], {
          isOwner: isOwner
        });
        if (isOwner) {
          wifiDisconnectReason = webSocketConnection.disconnectReason;
          debugService.log('Handling wifi disconnection', [TAG, loggingTags.connectionTroubleshooting], {
            disconnectReason: wifiDisconnectReason
          });
          if (wifiDisconnectReason === disconnectReasons.noInternet) {
            return resolve({
              message: locale.getString('connectionTroubleshooting.appOfflineDescription')
            });
          } else {
            lastKnownWifiStatus = circulatorManager.getCurrentCirculatorWifiStatus();
            debugService.log('Last known wifi status', [TAG, loggingTags.connectionTroubleshooting], {
              wifiStatus: lastKnownWifiStatus
            });
            return resolve(null);
          }
        } else {
          debugService.log('No wifi recommendation because user is not the owner', [TAG, loggingTags.connectionTroubleshooting]);
          return resolve(null);
        }
      })["catch"](function(error) {
        debugService.log('No wifi recommendation because unable to get owner status', [TAG, loggingTags.connectionTroubleshooting], {
          error: error
        });
        return resolve(null);
      }).done(_.noop, function(e) {
        return debugService.onPromiseUnhandledRejection(e, TAG);
      });
    });
  };
  retryConnection = function() {
    var finallyHandler, resetConnections;
    debugService.log('User claimed to have followed instructions.  Reset all connections to try again.', TAG);
    resetConnections = function() {
      $ionicLoading.show({
        template: "<div class='loading-indicator' />",
        noBackdrop: true
      });
      return circulatorManager.resetAllConnections();
    };
    finallyHandler = function() {
      return $ionicLoading.hide();
    };
    return resetConnections()["finally"](finallyHandler).done(_.noop, function(e) {
      return debugService.onPromiseUnhandledRejection(e, TAG);
    });
  };
  showConnectedUI = function() {
    $ionicLoading.hide();
    $scope.isScanning = false;
    $scope.messages = null;
    $scope.showPrimaryButton = true;
    $scope.showCancelButton = false;
    $scope.showSuggestion = false;
    $scope.primaryButtonText = locale.getString('general.okay');
    $scope.primaryButtonAction = function() {
      debugService.log('User selected okay', TAG);
      return $ionicHistory.goBack();
    };
    $timeout();
    return circulatorManager.getIsOwner().then(function(isOwner) {
      debugService.log('Got user ownership status', [TAG, loggingTags.connectionTroubleshooting], {
        isOwner: isOwner
      });
      if (isOwner) {
        return circulatorManager.getWifiStatus().then(function(wifiStatus) {
          debugService.log('Wifi status while connected', [TAG, loggingTags.connectionTroubleshooting], {
            wifiStatus: wifiStatus
          });
          if (wifiStatus === null) {
            debugService.log('Not connected to wifi', TAG);
            $scope.showSuggestion = true;
            $scope.suggestionText = locale.getString('connectionTroubleshooting.connectToWifiUpsell');
            $scope.suggestionAction = function() {
              debugService.log('User selected connect to wifi', TAG);
              return $state.go('circulatorWifi');
            };
          } else {
            if (circulatorWifiStatusService.isHealthyWifiStatus(wifiStatus)) {
              $scope.showSuggestion = false;
            } else {
              debugService.warn('Wifi status is not healthy', TAG, {
                wifiStatus: wifiStatus
              });
              $scope.showSuggestion = true;
              $scope.suggestionText = locale.getString('connectionTroubleshooting.reconnectToWifiText');
              $scope.suggestionAction = function() {
                debugService.log('User selected troubleshoot wifi', TAG);
                return circulatorWifiStatusService.handleUnhealthyWifiStatusWithPopup(wifiStatus);
              };
            }
          }
          return $timeout();
        })["catch"](function(error) {
          debugService.error('Fail to get wifi status', [TAG, loggingTags.connectionTroubleshooting], {
            error: error
          });
          return $scope.showSuggestion = false;
        }).done(_.noop, function(e) {
          return debugService.onPromiseUnhandledRejection(e, TAG);
        });
      }
    })["catch"](function(error) {
      debugService.log('Fail to get wifi status because unable to get owner status', [TAG, loggingTags.connectionTroubleshooting], {
        error: error
      });
      return $scope.showSuggestion = false;
    }).done(_.noop, function(e) {
      return debugService.onPromiseUnhandledRejection(e, TAG);
    });
  };
  onConnectionUpdated = function(connectionState) {
    debugService.log("Troubleshooting with connection state " + connectionState, TAG);
    $scope.isDisconnected = connectionState === circulatorConnectionStates.disconnected;
    $scope.isConnecting = connectionState === circulatorConnectionStates.connecting;
    $scope.isConnected = connectionState === circulatorConnectionStates.connected;
    $timeout();
    if ($scope.isDisconnected || $scope.isConnecting) {
      return showDisconnectedUI();
    } else if ($scope.isConnected) {
      return showConnectedUI();
    } else {
      debugService.error("Connection troubleshooting while in an unsupported state " + connectionState, TAG);
      return $state.go(appConfig.defaultView);
    }
  };
  onBeforeEnter = function() {
    var connectionState;
    $scope.messages = null;
    $scope.showPrimaryButton = false;
    $scope.showCancelButton = false;
    $scope.showSuggestion = false;
    statusBarService.setStyle(statusBarStyles.light);
    connectionState = circulatorManager.getCirculatorConnectionState();
    if (connectionState === circulatorConnectionStates.disconnected || connectionState === circulatorConnectionStates.connecting || connectionState === circulatorConnectionStates.connected) {
      debugService.log("Connection troubleshooting with initial connection state " + connectionState, TAG);
      unbindConnectionUpdateHandler = circulatorManager.bindCirculatorConnectionUpdateHandler(onConnectionUpdated);
      return unbindAppPauseHandler = $ionicPlatform.on('pause', onAppPause);
    } else {
      debugService.error("Connection troubleshooting while in an unsupported state " + connectionState, TAG);
      return $state.go(appConfig.defaultView);
    }
  };
  onLeave = function() {
    unbindConnectionUpdateHandler();
    unbindAppPauseHandler();
    return unbindAppResumeHandler();
  };
  onAppPause = function() {
    onLeave();
    return unbindAppResumeHandler = $ionicPlatform.on('resume', function() {
      onBeforeEnter();
      return unbindAppResumeHandler();
    });
  };
  $scope.$on('$ionicView.beforeEnter', onBeforeEnter);
  return $scope.$on('$ionicView.leave', onLeave);
}]);

this.app.controller('cookController', ["$scope", "$state", "$ionicHistory", "$ionicLoading", "$timeout", "appConfig", "alertService", "$ionicPlatform", "timerService", "debugService", "circulatorEventReasons", "circulatorManager", "programSteps", "programTypes", "guideService", "$stateParams", "assetService", "utilities", "analyticsService", "locale", "loggingTags", "circulatorConnectionStates", "statusBarService", "statusBarStyles", "floatingActionButtonStates", "circulatorConnectionError", "cacheService", "$window", "circulatorWifiStatusService", "pushRegistrationService", "temperatureUnitService", function($scope, $state, $ionicHistory, $ionicLoading, $timeout, appConfig, alertService, $ionicPlatform, timerService, debugService, circulatorEventReasons, circulatorManager, programSteps, programTypes, guideService, $stateParams, assetService, utilities, analyticsService, locale, loggingTags, circulatorConnectionStates, statusBarService, statusBarStyles, floatingActionButtonStates, circulatorConnectionError, cacheService, $window, circulatorWifiStatusService, pushRegistrationService, temperatureUnitService) {
  var TAG, checkForEnableNotificationsNagAlert, checkForWifiNagAlert, checkValidProgramMaximumTimeoutMilliseconds, checkValidProgramTimeout, confirmAndStopCook, deregisterHardwareBackButtonAction, dropFood, hardwareBackButtonAction, isTraining, onAppPause, onBathTemperatureUpdated, onBeforeEnter, onConnectionUpdated, onEnter, onLeave, onLiveFeedError, onPageReady, onProgramStepUpdated, onProgramStopped, onProgramUpdated, onStopCookFailure, onStopCookSuccess, onTimerUpdated, resetSlider, showChangeTempHint, stopCook, unbindAppPauseHandler, unbindAppResumeHandler, unbindBathTemperatureUpdateHandler, unbindConnectionUpdateHandler, unbindLiveFeedErrorHandler, unbindProgramStepUpdatedHandler, unbindProgramUpdateHandler, unbindTimerUpdateHandlers, updateButton;
  TAG = 'CookView';
  isTraining = null;
  $scope.sliderOptions = {
    onTransitionStart: function(swiper) {
      if (swiper.activeIndex === 1) {
        $scope.stepsSelected = true;
        $scope.hideButtons = true;
      } else {
        $scope.stepsSelected = false;
        $scope.hideButtons = false;
      }
      return $timeout();
    }
  };
  unbindBathTemperatureUpdateHandler = _.noop;
  unbindProgramUpdateHandler = _.noop;
  unbindProgramStepUpdatedHandler = _.noop;
  unbindConnectionUpdateHandler = _.noop;
  unbindAppPauseHandler = _.noop;
  unbindAppResumeHandler = _.noop;
  unbindLiveFeedErrorHandler = _.noop;
  unbindTimerUpdateHandlers = _.noop;
  checkValidProgramTimeout = null;
  checkValidProgramMaximumTimeoutMilliseconds = 5000;
  resetSlider = function() {
    $scope.sliderOptions = {
      onTransitionEnd: function(swiper) {
        $scope.activeIndex = swiper.activeIndex;
        return $timeout();
      },
      initialSlide: 0
    };
    if ($scope.slider) {
      return $scope.slider.slideTo(0, 0);
    }
  };
  $scope.skipTutorial = function() {
    cacheService.set('isTraining', 'training', false);
    return $scope.isTraining = false;
  };
  $scope.nextTutorial = function() {
    return $scope.trainingPowerOff = true;
  };
  $scope.buttonState = floatingActionButtonStates.powerActive;
  $scope.buttonAction = _.noop;
  $scope.showErrorOverlay = false;
  $scope.resumeCook = function() {
    var newProgram, timeRemaining;
    newProgram = $scope.program;
    timeRemaining = circulatorManager.getTimeRemainingState();
    if (timeRemaining != null) {
      newProgram = _.extend(newProgram, {
        cookTime: timeRemaining
      });
    }
    debugService.log('Going to start new program', [TAG, loggingTags.cook], {
      newProgram: newProgram,
      oldProgram: $scope.program
    });
    $ionicLoading.show({
      template: "<div class='loading-indicator' />",
      noBackdrop: true
    });
    unbindProgramUpdateHandler();
    return circulatorManager.startProgram(newProgram).then(function() {
      debugService.log('New program started', [TAG, loggingTags.cook], {
        newProgram: newProgram
      });
      return unbindProgramUpdateHandler = circulatorManager.bindProgramUpdateHandler(onProgramUpdated);
    })["catch"](function(error) {
      return debugService.error('Fail to resume cook', [TAG, loggingTags.cook], {
        error: error,
        newProgram: newProgram
      });
    })["finally"](function() {
      return $ionicLoading.hide();
    });
  };
  $scope.cookCircleSelected = function() {
    var ref;
    if (((ref = $scope.program) != null ? ref.programType : void 0) === programTypes.manual) {
      return $state.go('temperatureUpdate');
    } else {
      return $state.go('guideDoneness', {
        id: $scope.guide.id
      });
    }
  };
  $scope.timerAction = function() {
    var guideId, programId, ref, ref1, ref2;
    if ((((ref = $scope.program.programMetadata) != null ? ref.guideId : void 0) != null) && (((ref1 = $scope.program.programMetadata) != null ? ref1.programId : void 0) != null)) {
      ref2 = $scope.program.programMetadata, guideId = ref2.guideId, programId = ref2.programId;
      if ($scope.waitForFoodState) {
        return dropFood();
      } else {
        return $state.go('guideTimer', {
          id: guideId,
          programId: programId,
          update: true
        });
      }
    } else {
      return $state.go('timer');
    }
  };
  $scope.getTemperatureProgress = function(bathTemp, setPointTemp) {
    return 'translateY(-' + Math.min(utilities.getPreheatAlpha(bathTemp, setPointTemp) * 150, 150) + 'px)';
  };
  $scope.getDisplaySetPointTemperature = function(setPointTemp) {
    return utilities.roundTo(utilities.modelToDisplayTemperature(setPointTemp), 1);
  };
  $scope.getDisplayBathTemperatureInteger = function(bathTemp) {
    return utilities.getIntDisplay(utilities.modelToDisplayTemperature(bathTemp));
  };
  $scope.getDisplayBathTemperatureDecimal = function(bathTemp) {
    return utilities.getDecimalDisplay(utilities.modelToDisplayTemperature(bathTemp));
  };
  onStopCookSuccess = function() {
    debugService.log('Stop cook succeeded', [TAG, loggingTags.cook]);
    $ionicLoading.hide();
    $ionicHistory.nextViewOptions({
      disableBack: true,
      historyRoot: true
    });
    if ($scope.isTraining === true) {
      return $state.go('trainingSuccess');
    } else {
      return $state.go(appConfig.defaultView);
    }
  };
  onStopCookFailure = function(error) {
    $ionicLoading.hide();
    circulatorConnectionError.handleErrorWithPopup(error)["catch"](function(error) {
      debugService.error('unhandled error while stopping cook', [TAG, loggingTags.cook], {
        error: error
      });
      return alertService.confirm({
        headerColor: 'alert-yellow',
        icon: 'fail',
        titleString: locale.getString('popup.stopProgramFailureTitle'),
        bodyString: locale.getString('popup.stopProgramFailureDescription'),
        okText: locale.getString('general.tryAgain')
      }).then(function(confirmation) {
        if (!confirmation) {
          return;
        }
        debugService.log('Retrying stop program after failed attempt', TAG);
        return stopCook();
      });
    });
    return unbindProgramUpdateHandler = circulatorManager.bindProgramUpdateHandler(onProgramUpdated);
  };
  confirmAndStopCook = function() {
    return alertService.confirm({
      headerColor: 'alert-yellow',
      iconUrl: 'svg/power-alert.svg#power-alert',
      titleString: locale.getString('popup.stopProgramTitle'),
      bodyString: locale.getString('popup.stopProgramConfirmation')
    }).then(function(confirmation) {
      if (!confirmation) {
        return;
      }
      return stopCook();
    });
  };
  stopCook = function() {
    unbindProgramUpdateHandler();
    $ionicLoading.show({
      template: "<div class='loading-indicator' />",
      noBackdrop: true
    });
    return circulatorManager.stopProgram().then(onStopCookSuccess)["catch"](onStopCookFailure).done(_.noop, function(e) {
      return debugService.onPromiseUnhandledRejection(e, TAG);
    });
  };
  dropFood = function() {
    $ionicLoading.show({
      template: "<div class='loading-indicator' />",
      noBackdrop: true
    });
    return circulatorManager.dropFood().then(function() {
      $ionicLoading.hide();
      analyticsService.track('Dropped Food');
      return debugService.log('Drop food success', [TAG, loggingTags.cook]);
    })["catch"](function(error) {
      $ionicLoading.hide();
      return circulatorConnectionError.handleErrorWithPopup(error)["catch"](function(error) {
        debugService.error('unhandled error while dropping food', [TAG, loggingTags.cook], {
          error: error
        });
        return alertService.alert({
          headerColor: 'alert-red',
          icon: 'fail',
          titleString: locale.getString('popup.dropFoodFailureMessage')
        });
      });
    }).done(_.noop, function(e) {
      return debugService.onPromiseUnhandledRejection(e, TAG);
    });
  };
  onBathTemperatureUpdated = function(bathTemp) {
    $scope.bathTemperature = bathTemp;
    return $timeout();
  };
  onProgramStopped = function() {
    $ionicHistory.nextViewOptions({
      disableBack: true,
      historyRoot: true
    });
    return $state.go(appConfig.defaultView);
  };
  onProgramUpdated = function(program) {
    var timerId;
    $scope.program = program;
    if (program != null) {
      debugService.debug('Got a valid program', [TAG, loggingTags.cook], {
        program: program
      });
      $scope.setPointTemperature = program.setPoint;
      $scope.isGuidedCook = $scope.program.programType === programTypes.automatic;
      $scope.isManualCook = $scope.program.programType === programTypes.manual;
      if ($scope.isGuidedCook) {
        $scope.humanizeCookTime = utilities.humanizeDuration(utilities.convertSecondsToMilliseconds(program.cookTime));
        timerId = $scope.program.programMetadata.timerId;
        guideService.get($scope.program.programMetadata.guideId).then(function(guide) {
          var ref, ref1;
          $scope.guide = guide;
          $scope.time = timerService.getTimeForGuide(guide, timerId);
          return $scope.timerCompletedMessage = ((ref = $scope.time) != null ? ref.notification : void 0) || ((ref1 = $scope.guide) != null ? ref1.cookingTimerNotification : void 0);
        });
        $scope.shouldShowSteps = false;
        $timeout(function() {
          $scope.shouldShowSteps = true;
          return $timeout();
        });
      }
      return $timeout();
    } else {
      debugService.warn('Got a null or undefined program', [TAG, loggingTags.cook]);
      return circulatorManager.listRecentEvents().then(function(recentEvents) {
        debugService.log('Got reply of recent events', [TAG, loggingTags.cook], {
          recentEvents: recentEvents
        });
        if (recentEvents.eventReason === circulatorEventReasons.BUTTON_PRESSED) {
          return null;
        } else {
          return alertService.alert({
            headerColor: 'alert-red',
            icon: 'fail',
            sound: true,
            titleString: locale.getString('popup.cookingStoppedUnknownReasonTitle'),
            bodyString: locale.getString('popup.cookingStoppedUnknownReasonDescription')
          }).then(function() {
            return onProgramStopped();
          });
        }
      })["catch"](function(error) {
        debugService.error('unhandled error while calling listRecentEvents', [TAG, loggingTags.cook], {
          error: error
        });
        return alertService.alert({
          headerColor: 'alert-red',
          icon: 'fail',
          sound: true,
          titleString: locale.getString('popup.cookingStoppedUnknownReasonTitle'),
          bodyString: locale.getString('popup.cookingStoppedUnknownReasonDescription')
        }).then(function() {
          return onProgramStopped();
        });
      });
    }
  };
  updateButton = function() {
    var connectionState, programStep;
    connectionState = circulatorManager.getCirculatorConnectionState();
    if (connectionState === circulatorConnectionStates.connected) {
      programStep = circulatorManager.getProgramStepState();
      if (_.includes([programSteps.preheat, programSteps.cook, programSteps.waitForRemoveFood, programSteps.error], programStep)) {
        $scope.buttonState = floatingActionButtonStates.powerActive;
        $scope.buttonAction = confirmAndStopCook;
      } else if (programStep === programSteps.waitForFood) {
        $scope.buttonState = floatingActionButtonStates.waitForFood;
        $scope.buttonAction = dropFood;
      }
    } else if (connectionState === circulatorConnectionStates.connecting) {
      $scope.buttonState = floatingActionButtonStates.powerActive;
      $scope.buttonAction = circulatorConnectionError.handleConnectingWithPopup;
      $timeout();
    } else {
      $scope.buttonState = floatingActionButtonStates.powerDarkInactive;
      $scope.buttonAction = function() {
        return $state.go('connectionTroubleshooting');
      };
    }
    return $timeout();
  };
  onProgramStepUpdated = function(programStep) {
    updateButton();
    if (programStep === programSteps.waitForFood) {
      $scope.waitForFoodState = true;
    } else {
      $scope.waitForFoodState = false;
    }
    if (programStep === programSteps.preheat) {
      $scope.preheatState = true;
    } else {
      $scope.preheatState = false;
    }
    if (programStep === programSteps.cook) {
      $scope.cookState = true;
    } else {
      $scope.cookState = false;
    }
    if (programStep === programSteps.waitForRemoveFood) {
      $scope.waitForRemoveFoodState = true;
    } else {
      $scope.waitForRemoveFoodState = false;
    }
    if (programStep === programSteps.error) {
      $scope.showErrorOverlay = true;
    } else {
      $scope.showErrorOverlay = false;
    }
    return $timeout();
  };
  onConnectionUpdated = function(connectionState) {
    if (connectionState === circulatorConnectionStates.connecting) {
      debugService.log('Showing connecting spinner', [TAG, loggingTags.cook]);
      $scope.showConnectingSpinner = true;
    } else if (connectionState === circulatorConnectionStates.disconnected) {
      debugService.log('Showing disconnected message', [TAG, loggingTags.cook]);
      $scope.showDisconnectedMessage = true;
    } else {
      $scope.showConnectingSpinner = false;
      $scope.showDisconnectedMessage = false;
    }
    return updateButton();
  };
  $scope.goToTroubleshooting = function() {
    return $state.go('connectionTroubleshooting');
  };
  onLiveFeedError = function() {
    debugService.warn('onLiveFeedError', [TAG, loggingTags.cook]);
    return alertService.alert({
      headerColor: 'alert-yellow',
      icon: 'connecting-joule',
      titleString: locale.getString('popup.workingOnCookDataTitle'),
      bodyString: locale.getString('popup.workingOnCookDataMessage')
    });
  };
  onTimerUpdated = function(timer) {
    debugService.log('onTimerUpdated', [TAG, loggingTags.cook], {
      timer: timer
    });
    $scope.timer = timer;
    return $timeout();
  };
  checkForWifiNagAlert = function() {
    return $window.Q.Promise(function(resolve) {
      if (circulatorManager.getCurrentCirculatorWifiStatus()) {
        return resolve(false);
      }
      return circulatorManager.getIsOwner().then(function(isOwner) {
        var alert;
        if (!isOwner) {
          return resolve(false);
        }
        debugService.log('Showing nag alert because user does not have healthy wifi', TAG);
        if (!cacheService.get('enableWifi', 'neverShowNagAlert')) {
          alert = alertService.confirm({
            headerColor: 'alert-yellow',
            icon: 'heart-joule',
            titleString: locale.getString('popup.wifiUpsellTitle'),
            bodyString: locale.getString('popup.wifiUpsellBodyShort'),
            cancelText: locale.getString('general.cancel'),
            tertiaryActionText: locale.getString('popup.dontShowMeThisAgain'),
            okText: locale.getString('pairing.wifiUpsellButtonPrimary'),
            tertiaryAction: function() {
              cacheService.set('enableWifi', 'neverShowNagAlert', true);
              alert.close(false);
              return resolve(true);
            }
          });
          return alert.then(function(confirmation) {
            if (!confirmation) {
              debugService.log('User declined to connect to wifi after nag alert', TAG);
              return resolve(true);
            }
            debugService.log('User clicked okay on connect to wifi nag alert', TAG);
            $state.go('circulatorWifi');
            return resolve(true);
          })["catch"](function(error) {
            debugService.error('Error while checking whether to show wifi nag alert', TAG, {
              error: error
            });
            return resolve(false);
          });
        }
      });
    });
  };
  checkForEnableNotificationsNagAlert = function() {
    return $window.Q.Promise(function(resolve) {
      var onNotificationSettingsOpenFail, onNotificationSettingsOpenSuccess, showNotificationsNagAlert;
      onNotificationSettingsOpenSuccess = function() {
        debugService.log('User went to settings app after nag alert to enable notifications', TAG);
        return pushRegistrationService.isNotificationEnabled().then(function(isEnabled) {
          if (isEnabled) {
            debugService.log('User went to settings app and enabled notifications after nag alert', TAG);
          } else {
            debugService.log('User went to settings app did not enable notifications after nag alert', TAG);
          }
          return resolve(true);
        });
      };
      onNotificationSettingsOpenFail = function(error) {
        debugService.error('Failed to open settings app', TAG, {
          error: error
        });
        return resolve(true);
      };
      showNotificationsNagAlert = function() {
        var alert;
        if (!cacheService.get('enableNotifications', 'neverShowNagAlert')) {
          alert = alertService.confirm({
            headerColor: 'alert-yellow',
            icon: 'heart-joule',
            titleString: locale.getString('onboarding.notificationsHeaderPrimary'),
            bodyString: locale.getString('onboarding.notificationsHeaderSecondary'),
            tertiaryActionText: locale.getString('popup.dontShowMeThisAgain'),
            tertiaryAction: function() {
              cacheService.set('enableNotifications', 'neverShowNagAlert', true);
              alert.close(false);
              return resolve(true);
            }
          });
          return alert.then(function(confirmation) {
            if (!confirmation) {
              debugService.log('User declined to enable notifications after nag alert');
              return resolve(true);
            }
            if (ionic.Platform.isIOS()) {
              return cordova.plugins.settings.open(onNotificationSettingsOpenSuccess, onNotificationSettingsOpenFail);
            } else if (ionic.Platform.isAndroid()) {
              return cordova.plugins.settings.openSetting('application', onNotificationSettingsOpenSuccess, onNotificationSettingsOpenFail);
            }
          });
        }
      };
      return pushRegistrationService.isNotificationEnabled().then(function(isEnabled) {
        if (isEnabled) {
          return resolve(false);
        }
        return showNotificationsNagAlert();
      });
    });
  };
  showChangeTempHint = function() {
    var connectionState;
    connectionState = circulatorManager.getCirculatorConnectionState();
    if ($stateParams.shouldShowTempHint === 'true' && connectionState === circulatorConnectionStates.connected) {
      $scope.showTempHint = true;
    } else {
      $scope.showTempHint = false;
    }
    return $timeout((function() {
      return $scope.showTempHint = false;
    }), 5000);
  };
  onPageReady = function() {
    unbindBathTemperatureUpdateHandler = circulatorManager.bindBathTemperatureUpdateHandler(onBathTemperatureUpdated);
    unbindProgramUpdateHandler = circulatorManager.bindProgramUpdateHandler(onProgramUpdated);
    unbindProgramStepUpdatedHandler = circulatorManager.bindProgramStepUpdateHandlers(onProgramStepUpdated);
    unbindConnectionUpdateHandler = circulatorManager.bindCirculatorConnectionUpdateHandler(onConnectionUpdated);
    unbindLiveFeedErrorHandler = circulatorManager.bindLiveFeedErrorHander(onLiveFeedError);
    unbindTimerUpdateHandlers = timerService.bindTimerUpdateHandlers(onTimerUpdated);
    unbindAppPauseHandler = $ionicPlatform.on('pause', onAppPause);
    if (!isTraining) {
      return checkForEnableNotificationsNagAlert().then(function(notificationsNagAlertShown) {
        if (!notificationsNagAlertShown) {
          return checkForWifiNagAlert();
        }
      });
    }
  };
  onAppPause = function() {
    onLeave();
    return unbindAppResumeHandler = $ionicPlatform.on('resume', function() {
      onBeforeEnter();
      return unbindAppResumeHandler();
    });
  };
  hardwareBackButtonAction = function() {
    return $state.go('home');
  };
  deregisterHardwareBackButtonAction = null;
  onLeave = function() {
    if (typeof deregisterHardwareBackButtonAction === "function") {
      deregisterHardwareBackButtonAction();
    }
    if (checkValidProgramTimeout != null) {
      $timeout.cancel(checkValidProgramTimeout);
    }
    unbindBathTemperatureUpdateHandler();
    unbindProgramUpdateHandler();
    unbindProgramStepUpdatedHandler();
    unbindConnectionUpdateHandler();
    unbindLiveFeedErrorHandler();
    unbindTimerUpdateHandlers();
    unbindAppPauseHandler();
    return unbindAppResumeHandler();
  };
  onEnter = function() {
    $scope.swipeTease = $stateParams.showStepsHint === 'true';
    $timeout((function() {
      return $scope.swipeTease = false;
    }), 1500);
    showChangeTempHint();
    if ($scope.isGuidedCook && $scope.slider) {
      $scope.slider.unlockSwipes();
    }
    if ($scope.isManualCook && $scope.slider) {
      return $scope.slider.lockSwipes();
    }
  };
  onBeforeEnter = function() {
    var currentTemperatureUnit;
    statusBarService.setStyle(statusBarStyles.dark);
    resetSlider();
    currentTemperatureUnit = temperatureUnitService.get();
    if (currentTemperatureUnit === 'f') {
      $scope.showUnit = locale.getString('general.degreeF');
    } else {
      $scope.showUnit = locale.getString('general.degreeC');
    }
    deregisterHardwareBackButtonAction = $ionicPlatform.registerBackButtonAction(hardwareBackButtonAction, 100);
    $scope.showErrorOverlay = false;
    $scope.trainingPowerOff = false;
    $scope.isTraining = false;
    isTraining = cacheService.get('isTraining', 'training');
    if (isTraining) {
      $timeout((function() {
        return $scope.isTraining = true;
      }), 1500);
      statusBarService.setStyle(statusBarStyles.hidden);
    }
    if (circulatorManager.getProgramState() === null) {
      debugService.debug('Received a null program upon cook page enter', [TAG, loggingTags.cook]);
      checkValidProgramTimeout = $timeout(function() {
        if (circulatorManager.getProgramState() === null) {
          debugService.error('Still on the cook page and program is null.  Exiting.', [TAG, loggingTags.cook]);
          return alertService.alert({
            headerColor: 'alert-red',
            icon: 'fail',
            sound: true,
            titleString: locale.getString('circulatorError.UNKNOWN_REASON_strings.titleString'),
            bodyString: locale.getString('circulatorError.UNKNOWN_REASON_strings.bodyString')
          }).then(function() {
            $ionicHistory.nextViewOptions({
              disableBack: true,
              historyRoot: true
            });
            return $state.go(appConfig.defaultView);
          });
        }
      }, checkValidProgramMaximumTimeoutMilliseconds);
      return unbindProgramUpdateHandler = circulatorManager.bindProgramUpdateHandler(function(program) {
        if (program != null) {
          debugService.debug('Received a valid program.  Calling onPageReady.', [TAG, loggingTags.cook], program);
          $timeout.cancel(checkValidProgramTimeout);
          unbindProgramUpdateHandler();
          return onPageReady();
        }
      });
    } else {
      debugService.debug('Received a valid program upon cook page enter', [TAG, loggingTags.cook]);
      return onPageReady();
    }
  };
  $scope.$on('$ionicView.beforeEnter', onBeforeEnter);
  $scope.$on('$ionicView.leave', onLeave);
  return $scope.$on('$ionicView.enter', onEnter);
}]);

this.app.controller('createAccountController', ["$scope", "statusBarService", "statusBarStyles", "$ionicLoading", "authenticationService", "analyticsService", "userService", "$ionicHistory", "$state", "alertService", "locale", "$window", "debugService", "$timeout", "ngFB", "$q", "faqLinkConfig", "csConfig", "cacheService", function($scope, statusBarService, statusBarStyles, $ionicLoading, authenticationService, analyticsService, userService, $ionicHistory, $state, alertService, locale, $window, debugService, $timeout, ngFB, $q, faqLinkConfig, csConfig, cacheService) {
  var TAG, createAccount, facebookLogin, onBeforeEnter, onCreateAccountError, onCreateAccountSuccess, onEmailAlreadyRegistered, onFacebookClick, onFormInvalid, onFormValid, onUserOffline, register, setButton, setIsLoading, userLookup;
  TAG = 'createAccountController';
  ngFB.init({
    appId: csConfig.facebookAppId
  });
  $scope.setForm = function(form) {
    return $scope.form = form;
  };
  $scope.isPasswordShowing = false;
  $scope.signInAction = function() {
    return $state.go('signIn');
  };
  $scope.toggleIsPasswordShowing = function() {
    return $scope.isPasswordShowing = !$scope.isPasswordShowing;
  };
  userLookup = function() {
    return authenticationService.me().then(function(user) {
      var token;
      analyticsService.identify(user);
      token = authenticationService.getToken();
      return userService.signIn(user, token);
    });
  };
  onCreateAccountSuccess = function() {
    setIsLoading(false);
    analyticsService.registerUser();
    analyticsService.track('User Created Account');
    $ionicHistory.nextViewOptions({
      disableBack: true
    });
    return $state.go('onboarding');
  };
  onCreateAccountError = function(error) {
    setIsLoading(false);
    debugService.error('Unknown create account error', TAG, {
      error: error
    });
    return alertService.alert({
      headerColor: 'alert-red',
      icon: 'fail',
      titleString: locale.getString('authentication.errorGenericHeaderPrimary'),
      bodyString: locale.getString('authentication.errorGenericHeaderSecondary'),
      link: faqLinkConfig.cantSignIn
    });
  };
  onUserOffline = function() {
    setIsLoading(false);
    return alertService.alert({
      headerColor: 'alert-red',
      icon: 'fail',
      titleString: locale.getString('authentication.errorOfflineHeaderPrimary'),
      bodyString: locale.getString('authentication.errorOfflineHeaderSecondary')
    });
  };
  onEmailAlreadyRegistered = function() {
    setIsLoading(false);
    debugService.log('tried to create an account for already registered email', TAG);
    return alertService.alert({
      headerColor: 'alert-red',
      icon: 'fail',
      titleString: locale.getString('authentication.emailAlreadyRegisteredTitle'),
      bodyString: locale.getString('authentication.emailAlreadyRegistered')
    });
  };
  register = function() {
    return $window.Q.Promise(function(resolve, reject) {
      var email, name, password, passwordHash, ref, salt;
      ref = $scope.user, name = ref.name, email = ref.email, password = ref.password;
      salt = cacheService.get('salt', 'security');
      passwordHash = $window.bcrypt.hashSync(password || '', salt);
      debugService.log('User submitted create account form', TAG, {
        passwordHash: passwordHash
      });
      return authenticationService.registerWithEmail(name, email, password).then(resolve)["catch"](function(e) {
        if (e === 400) {
          return onEmailAlreadyRegistered();
        } else {
          return reject(e);
        }
      });
    });
  };
  createAccount = function() {
    if (!$window.navigator.onLine) {
      return onUserOffline();
    }
    setIsLoading(true);
    return register().then(userLookup).then(onCreateAccountSuccess)["catch"](onCreateAccountError);
  };
  setButton = function(state) {
    switch (state) {
      case 'active':
        $scope.buttonClass = 'sign-in-button-active';
        $scope.buttonAction = function() {
          return createAccount();
        };
        return $timeout();
      case 'disabled':
        $scope.buttonClass = 'sign-in-button-disabled';
        $scope.buttonAction = function() {
          return _.noop();
        };
        return $timeout();
    }
  };
  onFormValid = function() {
    return setButton('active');
  };
  onFormInvalid = function() {
    return setButton('disabled');
  };
  setIsLoading = function(bool) {
    if (bool) {
      return $ionicLoading.show({
        template: "<div class='loading-indicator' />",
        noBackdrop: true
      });
    } else {
      return $ionicLoading.hide();
    }
  };
  $scope.handleFormChange = function() {
    if (!$scope.form.$valid) {
      return onFormInvalid();
    }
    if ($scope.user.password.length < 6) {
      return onFormInvalid();
    }
    return onFormValid();
  };
  onFacebookClick = function() {
    if (!$window.navigator.onLine) {
      return onUserOffline();
    }
    setIsLoading(true);
    return facebookLogin().then(userLookup).then(onCreateAccountSuccess)["catch"](onCreateAccountError);
  };
  facebookLogin = function() {
    return ngFB.login({
      scope: 'public_profile, email'
    }).then(function(loginResp) {
      var access_token;
      access_token = loginResp.authResponse.accessToken;
      return ngFB.api({
        path: '/me',
        params: {
          access_token: access_token,
          '?fields': 'user_id'
        }
      }).then(function(apiResp) {
        var deferred, user_id;
        user_id = apiResp.id;
        deferred = $q.defer();
        return authenticationService.csAuthenticateFacebook({
          access_token: access_token,
          user_id: user_id
        }, deferred);
      });
    });
  };
  $scope.handleClickFacebook = onFacebookClick;
  onBeforeEnter = function() {
    var privacyLink, termsLink;
    statusBarService.setStyle(statusBarStyles.hidden);
    setIsLoading(false);
    setButton('disabled');
    $scope.user = {
      name: '',
      email: '',
      password: ''
    };
    termsLink = "<a onclick=\"window.open('https://www.chefsteps.com/terms', '_blank')\">" + (locale.getString('legal.terms')) + "</a>";
    privacyLink = "<a onclick=\"window.open('https://www.chefsteps.com/privacy', '_blank')\">" + (locale.getString('legal.privacyPolicy')) + "</a>";
    return $scope.legal = locale.getString('authentication.createAccountLegal', {
      termsOfUse: termsLink,
      privacyPolicy: privacyLink
    });
  };
  return $scope.$on('$ionicView.beforeEnter', onBeforeEnter);
}]);


/* eslint-disable no-alert */
this.app.controller('developerSettingsController', ["$scope", "$timeout", "$ionicLoading", "$ionicPopup", "cacheService", "preferences", "debugService", "alertService", "statusBarService", "statusBarStyles", "fileWriteStream", "assetService", "locale", "devSimulatorService", "circulatorManager", "circulatorConnectionStates", "metadataSnapshotService", "resourceService", function($scope, $timeout, $ionicLoading, $ionicPopup, cacheService, preferences, debugService, alertService, statusBarService, statusBarStyles, fileWriteStream, assetService, locale, devSimulatorService, circulatorManager, circulatorConnectionStates, metadataSnapshotService, resourceService) {
  var TAG, onBeforeEnter, onEnter;
  TAG = 'DeveloperSettingsView';
  $scope.versionNumber = null;
  $scope.headRef = null;
  $scope.commitHash = null;
  $scope.firmwareVersion = null;
  $scope.circulatorHardwareVersion = null;
  $scope.serialNumber = null;
  $scope.guideManifestEndpoint = preferences.get('guideManifestEndpoint');
  $scope.dfuAutocheckEnabled = !!cacheService.get('dfuAutocheckEnabled', 'preference');
  $scope.platform = null;
  $scope.platformVersion = null;
  $scope.isSimulating = devSimulatorService.simulating;
  $scope.isFirmwareSimulating = false;
  $scope.toggleAppSimulation = function() {
    if (circulatorManager.getCirculatorConnectionState() === circulatorConnectionStates.unpaired) {
      alertService.alert({
        headerColor: 'alert-yellow',
        icon: 'heart-joule',
        titleString: locale.getString('pairing.heyThere'),
        bodyString: locale.getString('popup.restartForChanges')
      });
      return devSimulatorService.setSimulation(!devSimulatorService.simulating);
    } else {
      return circulatorManager.unpair()["catch"](function(error) {
        return debugService.error('Unpair has failed', TAG, {
          error: error
        });
      })["finally"](function() {
        debugService.log('Unpair has finished', TAG);
        devSimulatorService.setSimulation(!devSimulatorService.simulating);
        return alertService.alert({
          headerColor: 'alert-yellow',
          icon: 'heart-joule',
          titleString: locale.getString('pairing.heyThere'),
          bodyString: locale.getString('popup.restartForChanges')
        });
      }).done(_.noop, function(e) {
        return debugService.onPromiseUnhandledRejection(e, TAG);
      });
    }
  };
  $scope.updateGuideManifestEndpoint = function() {
    var guideManifestEndpoint;
    guideManifestEndpoint = preferences.get('guideManifestEndpoint');
    $scope.value = {
      guideManifestEndpoint: guideManifestEndpoint
    };
    return $ionicPopup.show({
      templateUrl: 'templates/views/developer-settings/guide-manifest-endpoint.html',
      scope: $scope,
      buttons: [
        {
          text: locale.getString('general.cancel'),
          type: 'button',
          onTap: function() {
            return 'cancel';
          }
        }, {
          text: locale.getString('general.okay'),
          type: 'button-positive',
          onTap: function() {
            return $scope.value.guideManifestEndpoint;
          }
        }
      ]
    }).then(function(selection) {
      if (selection === 'cancel' || selection === guideManifestEndpoint) {
        return;
      }
      $scope.guideManifestEndpoint = selection;
      preferences.set('guideManifestEndpoint', selection);
      debugService.log('Setting guide manifest endpoint to ' + selection, TAG);
      $ionicLoading.show({
        template: "<div class='loading-indicator' />",
        noBackdrop: true
      });
      return resourceService.refresh().then(function() {
        return $ionicLoading.hide();
      })["catch"](function() {
        $ionicLoading.hide();
        return alertService.alert({
          headerColor: 'alert-red',
          icon: 'fail',
          titleString: 'Refresh Failed',
          bodyString: 'Sorry, we we\'ren\'t able to fetch down the updated guides. Please try again.'
        });
      });
    });
  };
  $scope.clearAssetCache = function() {
    return assetService.removeAll();
  };
  $scope.postLogFile = function() {
    return fileWriteStream.forceUpload();
  };
  $scope.toggleFirmwareSimulation = function() {
    if (circulatorManager.getCirculatorConnectionState() === circulatorConnectionStates.connected) {
      return circulatorManager.enableSimulator(!$scope.isFirmwareSimulating).then(function() {
        $scope.isFirmwareSimulating = !$scope.isFirmwareSimulating;
        $timeout();
        return alertService.alert({
          headerColor: 'alert-green',
          icon: 'success',
          titleString: locale.getString('pairing.heyThere'),
          bodyString: 'Firmware simulator enabled!'
        });
      })["catch"](function(error) {
        return alert(error);
      }).done(_.noop, function(e) {
        return debugService.onPromiseUnhandledRejection(e, TAG);
      });
    } else {
      return alertService.alert({
        headerColor: 'alert-red',
        icon: 'fail',
        titleString: locale.getString('popup.embarrassing'),
        bodyString: 'You are not connected, so you cannot enable firmware simulator.'
      });
    }
  };
  $scope.toggleDfuAutocheck = function() {
    debugService.log('Toggling DFU autocheck.', TAG);
    cacheService.set('dfuAutocheckEnabled', 'preference', !$scope.dfuAutocheckEnabled);
    $scope.dfuAutocheckEnabled = cacheService.get('dfuAutocheckEnabled', 'preference');
    return alert('Please restart the app to complete the switch!');
  };
  onBeforeEnter = function() {
    return statusBarService.setStyle(statusBarStyles.dark);
  };
  onEnter = function() {
    var snapshot;
    if (circulatorManager.getCirculatorConnectionState() === circulatorConnectionStates.connected) {
      circulatorManager.isSimulatorEnabled().then(function(reply) {
        $scope.isFirmwareSimulating = reply.simulatorOn;
        return $timeout();
      })["catch"](function(error) {
        return alert(error);
      }).done(_.noop, function(e) {
        return debugService.onPromiseUnhandledRejection(e, TAG);
      });
    }
    snapshot = metadataSnapshotService.getSnapshot();
    $scope.circulatorHardwareVersion = snapshot.circulatorHardwareVersion;
    $scope.circulatorSerialNumber = snapshot.circulatorSerialNumber;
    $scope.circulatorName = snapshot.circulatorName;
    $scope.firmwareVersion = snapshot.firmwareVersion;
    $scope.softdeviceVersion = snapshot.softdeviceVersion;
    $scope.bootloaderVersion = snapshot.bootloaderVersion;
    $scope.appFirmwareVersion = snapshot.appFirmwareVersion;
    $scope.espFirmwareVersion = snapshot.espFirmwareVersion;
    $scope.certificateVersion = snapshot.certificateVersion;
    $scope.versionNumber = '2.37.1';
    $scope.headRef = snapshot.appHeadRef;
    $scope.buildFlavor = 'production';
    $scope.commitHash = snapshot.appCommitHash;
    $scope.platform = snapshot.appPlatform;
    $scope.platformVersion = snapshot.appPlatformVersion;
    return $timeout();
  };
  $scope.$on('$ionicView.beforeEnter', onBeforeEnter);
  return $scope.$on('$ionicView.enter', onEnter);
}]);

this.app.controller('deviceAboutController', ["metadataSnapshotService", "statusBarService", "statusBarStyles", "$scope", "$timeout", function(metadataSnapshotService, statusBarService, statusBarStyles, $scope, $timeout) {
  var onBeforeEnter;
  onBeforeEnter = function() {
    var snapshot;
    statusBarService.setStyle(statusBarStyles.dark);
    snapshot = metadataSnapshotService.getSnapshot();
    $scope.circulatorSerialNumber = snapshot.circulatorSerialNumber;
    $scope.circulatorName = snapshot.circulatorName;
    $scope.appFirmwareVersion = snapshot.appFirmwareVersion;
    $scope.espFirmwareVersion = snapshot.espFirmwareVersion;
    return $timeout();
  };
  return $scope.$on('$ionicView.beforeEnter', onBeforeEnter);
}]);

this.app.controller('deviceSettingsController', ["$scope", "$ionicLoading", "$timeout", "$window", "appConfig", "circulatorManager", "$ionicHistory", "$state", "alertService", "statusBarService", "statusBarStyles", "debugService", "locale", "NonOwnerError", "NullOwnerError", "circulatorConnectionError", "faqLinkConfig", "circulatorWifiStatusService", "circulatorConnectionStates", function($scope, $ionicLoading, $timeout, $window, appConfig, circulatorManager, $ionicHistory, $state, alertService, statusBarService, statusBarStyles, debugService, locale, NonOwnerError, NullOwnerError, circulatorConnectionError, faqLinkConfig, circulatorWifiStatusService, circulatorConnectionStates) {
  var TAG, accessPointErrorDisplay, connectToAccessPointAction, disconnectAccessPoint, getAccessPointInformation, getOwnerAccessPoint, onBeforeEnter, onLeave, unbindConnectionUpdateHandler, unpairCirculator;
  TAG = 'DeviceSettingsView';
  unbindConnectionUpdateHandler = _.noop;
  unpairCirculator = function() {
    return alertService.confirm({
      headerColor: 'alert-yellow',
      icon: 'heart-joule',
      titleString: locale.getString('pairing.heyThere'),
      bodyString: locale.getString('pairing.areYouSureYouWantTo')
    }).then(function(confirmation) {
      if (!confirmation) {
        return;
      }
      debugService.log('Attempt to unpair', TAG);
      $ionicLoading.show({
        template: "<div class='loading-indicator' />",
        noBackdrop: true
      });
      return circulatorManager.unpair()["catch"](function(error) {
        return debugService.error('Unpair has failed', TAG, {
          error: error
        });
      })["finally"](function() {
        debugService.log('Unpair has finished', TAG);
        $ionicLoading.hide();
        $ionicHistory.nextViewOptions({
          disableBack: true,
          historyRoot: true
        });
        return $state.go(appConfig.defaultView);
      }).done(_.noop, function(e) {
        return debugService.onPromiseUnhandledRejection(e, TAG);
      });
    });
  };
  $scope.unpairCirculator = unpairCirculator;
  $scope.firmwareUpdateAction = function() {
    if (!$scope.connected) {
      return alertService.alert({
        headerColor: 'alert-yellow',
        icon: 'heart-joule',
        titleString: locale.getString('update.firmwareUpdateNotConnectedTitle'),
        bodyString: locale.getString('update.firmwareUpdateNotConnectedBody')
      });
    }
    return $state.go('firmwareUpdate');
  };
  disconnectAccessPoint = function() {
    return alertService.confirm({
      headerColor: 'alert-yellow',
      icon: 'heart-joule',
      titleString: locale.getString('pairing.disconnectWifiTitle'),
      bodyString: locale.getString('pairing.disconnectWifiDescription')
    }).then(function(confirmation) {
      if (!confirmation) {
        return;
      }
      debugService.log('Attempt to disconnect from wifi', TAG);
      $ionicLoading.show({
        template: "<div class='loading-indicator' />",
        noBackdrop: true
      });
      return circulatorManager.disconnectAccessPoint().then(function() {
        debugService.log('Disconnect from wifi has succeeded', TAG);
        $ionicLoading.hide();
        $scope.isAccessPointConnecting = false;
        $scope.isAccessPointUnavailable = false;
        $scope.accessPointText = locale.getString('settings.connectToAccessPoint');
        $scope.accessPointAction = connectToAccessPointAction;
        return $timeout();
      })["catch"](function(error) {
        $ionicLoading.hide();
        return circulatorConnectionError.handleErrorWithPopup(error)["catch"](function(error) {
          debugService.error('Disconnect from wifi has failed', TAG, {
            error: error
          });
          return alertService.alert({
            headerColor: 'alert-red',
            icon: 'fail',
            titleString: locale.getString('pairing.sorryTitle'),
            bodyString: locale.getString('pairing.disconnectWifiFailedDescription')
          });
        });
      }).done(_.noop, function(e) {
        return debugService.onPromiseUnhandledRejection(e, TAG);
      });
    });
  };
  accessPointErrorDisplay = function() {
    return alertService.confirm({
      headerColor: 'alert-yellow',
      icon: 'fail',
      titleString: locale.getString('popup.wifiErrorTitle'),
      bodyString: locale.getString('popup.wifiErrorDescription'),
      cancelText: locale.getString('general.cancel'),
      okText: locale.getString('general.tryAgain')
    }).then(function(tryAgain) {
      if (tryAgain) {
        debugService.log('User selected retry', TAG);
        return getAccessPointInformation();
      }
    });
  };
  connectToAccessPointAction = function() {
    return $state.go('circulatorWifi');
  };
  getOwnerAccessPoint = function() {
    return circulatorManager.getWifiStatus().then(function(result) {
      var accessPointSSID;
      if (result === null) {
        debugService.log('Device is not connected to wifi.  Showing connectToAccessPoint', TAG);
        $scope.isAccessPointConnecting = false;
        $scope.isAccessPointUnavailable = false;
        $scope.accessPointText = locale.getString('settings.connectToAccessPoint');
        $scope.accessPointAction = connectToAccessPointAction;
      } else {
        if (circulatorWifiStatusService.isHealthyWifiStatus(result)) {
          accessPointSSID = result.SSID;
          debugService.log('Device is connected to wifi.  Showing disconnectFromAccessPoint', TAG);
          $scope.isAccessPointConnecting = false;
          $scope.isAccessPointUnavailable = false;
          $scope.accessPointText = locale.getString('settings.disconnectFromAccessPoint', {
            name: accessPointSSID
          });
          $scope.accessPointAction = disconnectAccessPoint;
        } else {
          debugService.warn('Wifi status is not healthy', TAG, {
            wifiStatus: result
          });
          $scope.isAccessPointConnecting = false;
          $scope.isAccessPointUnavailable = false;
          $scope.accessPointText = locale.getString('settings.wifiTroubleshoot');
          $scope.accessPointAction = function() {
            return circulatorWifiStatusService.handleUnhealthyWifiStatusWithPopup(result);
          };
        }
      }
      return $timeout();
    })["catch"](function(error) {
      throw error;
    });
  };
  getAccessPointInformation = function() {
    $scope.isAccessPointConnecting = true;
    $scope.isAccessPointUnavailable = false;
    $scope.accessPointText = locale.getString('settings.gettingAccessPoint');
    $scope.accessPointAction = _.noop;
    $timeout();
    return circulatorManager.getIsOwner().then(function(isOwner) {
      if (isOwner) {
        return getOwnerAccessPoint();
      } else if (isOwner === null) {
        return window.Q.reject(new NullOwnerError('Unable to determine ownership'));
      } else {
        return $window.Q.reject(new NonOwnerError('User is not the owner of Joule'));
      }
    })["catch"](function(error) {
      debugService.log('getAccessPointInformation has failed', TAG, {
        error: error
      });
      $scope.isAccessPointConnecting = false;
      $scope.isAccessPointUnavailable = true;
      $scope.accessPointText = locale.getString('settings.wifiUnavailable');
      $scope.accessPointAction = function() {
        return circulatorConnectionError.handleErrorWithPopup(error)["catch"](function(error) {
          debugService.log('Unexpected error', TAG, {
            error: error
          });
          return accessPointErrorDisplay();
        });
      };
      return $timeout();
    }).done(_.noop, function(e) {
      return debugService.onPromiseUnhandledRejection(e, TAG);
    });
  };
  onBeforeEnter = function() {
    statusBarService.setStyle(statusBarStyles.dark);
    $scope.circulatorName = circulatorManager.getCurrentCirculatorName();
    getAccessPointInformation();
    return unbindConnectionUpdateHandler = circulatorManager.bindCirculatorConnectionUpdateHandler(function(connectionState) {
      if (connectionState === circulatorConnectionStates.connected) {
        $scope.connected = true;
      } else if (connectionState === circulatorConnectionStates.disconnected || connectionState === circulatorConnectionStates.connecting) {
        $scope.connected = false;
      } else {
        debugService.error("On device settings page while in an unsupported state " + connectionState, TAG);
        $state.go('settings');
      }
      return $timeout();
    });
  };
  onLeave = function() {
    return unbindConnectionUpdateHandler();
  };
  $scope.$on('$ionicView.beforeEnter', onBeforeEnter);
  return $scope.$on('$ionicView.leave', onLeave);
}]);

this.app.controller('feedbackController', ["$scope", "$window", "statusBarService", "statusBarStyles", function($scope, $window, statusBarService, statusBarStyles) {
  var bugAddress, bugBodyEncoded, bugSubjectEncoded, email, featureAddress, featureSubjectEncoded, generalAddress, generalSubjectEncoded, onBeforeEnter;
  bugSubjectEncoded = 'Joule%20App%20Beta%20Bug%20Report';
  bugAddress = 'beta-bugs@chefsteps.com';
  bugBodyEncoded = 'Please%20list%20your%20phone%20model%20and%20OS%20version%2C%20if%20possible.%20That%20will%20help%20us%20fix%20the%20bug%20faster!';
  generalSubjectEncoded = 'Joule%20App%20General%20Feedback';
  generalAddress = 'beta-feedback@chefsteps.com';
  featureSubjectEncoded = 'I%20have%20an%20awesome%20idea';
  featureAddress = 'beta-features@chefsteps.com';
  email = function(address, subject, body) {
    var mailtoString;
    mailtoString = 'mailto:' + address + '?subject=' + subject;
    if (body) {
      mailtoString += '&body=' + body;
    }
    return $window.location.href = mailtoString;
  };
  $scope.emailBug = function() {
    return email(bugAddress, bugSubjectEncoded, bugBodyEncoded);
  };
  $scope.emailGeneral = function() {
    return email(generalAddress, generalSubjectEncoded);
  };
  $scope.emailFeature = function() {
    return email(featureAddress, featureSubjectEncoded);
  };
  onBeforeEnter = function() {
    return statusBarService.setStyle(statusBarStyles.dark);
  };
  return $scope.$on('$ionicView.beforeEnter', onBeforeEnter);
}]);

this.app.controller('firmwareUpdateController', ["$scope", "circulatorManager", "circulatorConnectionError", "circulatorConnectionStates", "$ionicHistory", "$ionicLoading", "appConfig", "firmwareUpdateService", "debugService", "$q", "locale", "$state", "$timeout", "$window", "cookStates", "alertService", "firmwareFileTypes", "firmwareTransferTypes", "firmwareUpdateConfig", "cacheService", "SafeAbortError", "networkStateService", "wifiConnectionStates", "faqLinkConfig", "$ionicPlatform", "backButtonPriorities", function($scope, circulatorManager, circulatorConnectionError, circulatorConnectionStates, $ionicHistory, $ionicLoading, appConfig, firmwareUpdateService, debugService, $q, locale, $state, $timeout, $window, cookStates, alertService, firmwareFileTypes, firmwareTransferTypes, firmwareUpdateConfig, cacheService, SafeAbortError, networkStateService, wifiConnectionStates, faqLinkConfig, $ionicPlatform, backButtonPriorities) {
  var TAG, checkForUpdates, deregisterHardwareBackButtonAction, getUpdates, goHome, init, showFailurePopup, unbindConnectionUpdateHandler;
  TAG = 'FirmwareUpdate';
  unbindConnectionUpdateHandler = _.noop;
  deregisterHardwareBackButtonAction = _.noop;
  init = function() {
    $scope.checking = false;
    $scope.updatesAvailable = false;
    $scope.manifest = {};
    $scope.updating = false;
    $scope.showProgress = false;
    $scope.percentUpdated = 0;
    $scope.currentlyUploadingIndex = 0;
    $scope.currentVersion = '';
    $scope.newVersion = '';
    $scope.steps = {
      none: 'none',
      uploading: 'uploading',
      applying: 'applying',
      rebooting: 'rebooting',
      connecting: 'connecting',
      checking: 'checking'
    };
    return $scope.step = $scope.steps.none;
  };
  showFailurePopup = function() {
    return alertService.alert({
      headerColor: 'alert-red',
      icon: 'fail',
      titleString: locale.getString('update.firmwareUpdateFailedTitle'),
      bodyString: locale.getString('update.firmwareUpdateFailedDescription')
    })["finally"](goHome);
  };
  goHome = function() {
    $ionicHistory.nextViewOptions({
      disableBack: true,
      historyRoot: true
    });
    return $state.go(appConfig.defaultView);
  };
  getUpdates = function() {
    var computeNewFirmwareVersion, recordManifest, sortUpdates;
    computeNewFirmwareVersion = function(manifest, old) {
      var appUpdate, espUpdate, newUpdateFor, newVersion;
      newVersion = '';
      newUpdateFor = function(updates, type) {
        return _.head(_.filter(updates, function(update) {
          return update.type === type;
        }));
      };
      appUpdate = newUpdateFor(manifest.updates, firmwareFileTypes.APPLICATION_FIRMWARE);
      newVersion = appUpdate != null ? appUpdate.version : old.appFirmwareVersion;
      espUpdate = newUpdateFor(manifest.updates, firmwareFileTypes.WIFI_FIRMWARE);
      newVersion = newVersion + '.' + (espUpdate != null ? espUpdate.version : old.espFirmwareVersion);
      return newVersion;
    };
    recordManifest = function(res) {
      var manifest, ref, ref1;
      manifest = res["new"];
      if (((ref = manifest.updates) != null ? ref.length : void 0) > 0) {
        $scope.updatesAvailable = true;
        $scope.manifest = manifest;
        $scope.releaseNotesAvailable = (ref1 = manifest.releaseNotesUrl != null) != null ? ref1 : {
          "true": false
        };
        $scope.newVersion = computeNewFirmwareVersion(manifest, res.old);
        return $scope.currentVersion = res.old.appFirmwareVersion + '.' + res.old.espFirmwareVersion;
      }
    };
    sortUpdates = function() {
      var updates;
      updates = $scope.manifest.updates;
      $scope.manifest.updates = _.sortBy(updates, function(update) {
        return {
          'tftp': 0,
          'http': 1,
          'download': 2
        }[update.transfer.type];
      });
    };
    debugService.log('Getting firmware updates from cloud', TAG);
    return firmwareUpdateService.fetchUpdateManifest().then(recordManifest).then(sortUpdates);
  };
  checkForUpdates = function() {
    var failureHandler, finallyHandler, run;
    run = function() {
      debugService.log('Checking for firmware updates', TAG);
      if (networkStateService.noInternet()) {
        return alertService.alert({
          headerColor: 'alert-yellow',
          icon: 'fail',
          titleString: locale.getString('popup.noInternetTitle'),
          bodyString: locale.getString('popup.noInternetDescription')
        })["finally"](function() {
          return $state.go('deviceSettings');
        });
      } else {
        $ionicLoading.show({
          template: "<div class='loading-indicator' />",
          noBackdrop: true
        });
        return getUpdates()["catch"](failureHandler)["finally"](finallyHandler).done(_.noop, function(e) {
          return debugService.onPromiseUnhandledRejection(e, TAG);
        });
      }
    };
    finallyHandler = function() {
      debugService.log('Done checking for firwmare updates', TAG);
      $scope.checking = false;
      return $ionicLoading.hide();
    };
    failureHandler = function(error) {
      debugService.error('Error while checking for firmware updates', TAG, {
        error: error
      });
      $ionicLoading.hide();
      return alertService.alert({
        headerColor: 'alert-red',
        icon: 'fail',
        titleString: locale.getString('update.firmwareUpdateCannotDownloadManifestTitle'),
        bodyString: locale.getString('update.firmwareUpdateCannotDownloadManifestDescription')
      })["finally"](function() {
        return $state.go('deviceSettings');
      });
    };
    return run();
  };
  $scope.viewReleaseNotes = function() {
    $window.open($scope.manifest.releaseNotesUrl, '_blank');
    return true;
  };
  $scope.applyUpdates = function() {
    var applyTftpUpdate, disableBackButton, enterBootMode, failureHandler, fetchAndApplyUpdate, fetchAndApplyUpdates, finallyHandler, reboot, resetConnections, restart, run, sendFile, setStayAwake, showPreUpdateConfirmation, showProgressModal, stopActivities, successHandler, waitForConnectionState, waitForReconnect;
    if ($scope.updating) {
      return;
    }
    if (!$scope.connected) {
      return alertService.alert({
        headerColor: 'alert-yellow',
        icon: 'heart-joule',
        titleString: locale.getString('update.firmwareUpdateNotConnectedTitle'),
        bodyString: locale.getString('update.firmwareUpdateNotConnectedBody')
      });
    }
    run = function() {
      debugService.log("Applying firmware updates, from current version: " + $scope.currentVersion + " to new version: " + $scope.newVersion, TAG, {
        manifest: $scope.manifest
      });
      $scope.updating = true;
      $scope.percentUpdated = 0;
      return showPreUpdateConfirmation().then(stopActivities).then(setStayAwake).then(disableBackButton).then(showProgressModal).then(fetchAndApplyUpdates).then(reboot).then(waitForReconnect).then(successHandler).then(goHome)["catch"](failureHandler)["finally"](finallyHandler).done(_.noop, function(e) {
        return debugService.onPromiseUnhandledRejection(e, TAG);
      });
    };
    showPreUpdateConfirmation = function() {
      return $window.Q.Promise(function(resolve, reject) {
        return alertService.confirm({
          headerColor: 'alert-green',
          icon: 'heart-joule',
          titleString: locale.getString('update.firmwareUpdatePreUpdateTitle'),
          bodyString: locale.getString('update.firmwareUpdatePreUpdateDescription'),
          okText: locale.getString('update.firmwareUpdatePreUpdateOkText'),
          cancelText: locale.getString('update.firmwareUpdatePreUpdateCancelText')
        }).then(function(confirmation) {
          if (!confirmation) {
            return $ionicHistory.goBack();
          } else {
            return resolve();
          }
        })["catch"](reject);
      });
    };
    setStayAwake = function() {
      var ref, ref1;
      return (ref = $window.plugins) != null ? (ref1 = ref.insomnia) != null ? ref1.keepAwake() : void 0 : void 0;
    };
    disableBackButton = function() {
      return deregisterHardwareBackButtonAction = $ionicPlatform.registerBackButtonAction(_.noop, backButtonPriorities.noop);
    };
    showProgressModal = function() {
      $scope.step = $scope.steps.none;
      $ionicLoading.show({
        scope: $scope,
        templateUrl: 'templates/views/firmware-update/progress.html'
      });
    };
    stopActivities = function() {
      return circulatorManager.stopActivities();
    };
    fetchAndApplyUpdates = function(allowRetry) {
      var retry, updates, updatesReduced;
      if (allowRetry == null) {
        allowRetry = true;
      }
      retry = function() {
        debugService.log('Retrying update', TAG);
        return resetConnections().then(waitForReconnect).then(restart).then(waitForReconnect).then(stopActivities).then(getUpdates).then(function() {
          return fetchAndApplyUpdates(false);
        });
      };
      updates = $scope.manifest.updates;
      updatesReduced = updates.reduce(function(arg, update) {
        var index, promise;
        promise = arg.promise, index = arg.index;
        return {
          promise: promise.then(function() {
            $scope.currentlyUploadingIndex = index;
            $scope.percentUpdated = 0;
            return fetchAndApplyUpdate(update)["catch"](function(err) {
              debugService.warn("Error during DFU, allowRetry? " + allowRetry, TAG, {
                error: err
              });
              if (!allowRetry || err instanceof SafeAbortError) {
                throw err;
              } else {
                return retry();
              }
            });
          }),
          index: index + 1
        };
      }, {
        promise: $window.Q(),
        index: 1
      });
      return updatesReduced.promise;
    };
    sendFile = function(updateType, file) {
      return circulatorManager.sendFile(updateType, file).progress(function(val) {
        var percent;
        $scope.showProgress = true;
        percent = (val.value.blocksSent / val.value.totalBlocks) * 100;
        $scope.percentUpdated = percent.toFixed(1);
        return $timeout();
      });
    };
    applyTftpUpdate = function(arg) {
      var checkWiFiConnectionStatus, filename, host, ref, sha256, totalBytes, useHTTP, verifyWifiDFUStatus;
      host = arg.host, filename = arg.filename, sha256 = arg.sha256, totalBytes = arg.totalBytes, useHTTP = (ref = arg.useHTTP) != null ? ref : false;
      debugService.log('Applying TFTP update', TAG, {
        host: host,
        filename: filename,
        sha256: sha256,
        totalBytes: totalBytes,
        useHTTP: useHTTP
      });
      $scope.showProgress = false;
      verifyWifiDFUStatus = function(attemptsLeft, sha256) {
        return $window.Q.Promise(function(resolve, reject) {
          var errorHandler, successHandler;
          errorHandler = function(error) {
            debugService.log("Error while getting status, with " + attemptsLeft + " attempts left", TAG, {
              error: error
            });
            if (attemptsLeft === 0) {
              return reject(error);
            } else {
              attemptsLeft--;
              return $window.Q.delay(firmwareUpdateConfig.getWifiDFUStatusRetryDelaySecs * 1000).then(run);
            }
          };
          successHandler = function(dfuStatus) {
            var running_slot;
            running_slot = dfuStatus.running_slot;
            if (dfuStatus.slot[running_slot].sha256 === sha256) {
              return resolve();
            } else {
              throw new Error('Incorrect running_slot sha256');
            }
          };
          run = function() {
            debugService.log('Getting Wifi DFU status to confirm running sha256', TAG);
            return circulatorManager.getWifiDFUStatus().then(successHandler)["catch"](errorHandler);
          };
          return run();
        });
      };
      checkWiFiConnectionStatus = function() {
        var showTftpAccessTroubleAlert, showWifiConnectionRequiredAlert, showWifiConnectionTroubleAlert;
        showWifiConnectionRequiredAlert = function() {
          return alertService.confirm({
            headerColor: 'alert-yellow',
            icon: 'fail',
            titleString: locale.getString('update.firmwareUpdateWifiRequiredTitle'),
            bodyString: locale.getString('update.firmwareUpdateWifiRequiredDescription'),
            okText: locale.getString('update.firmwareUpdateWifiRequiredOkText'),
            cancelText: locale.getString('update.firmwareUpdateWifiRequiredCancelText')
          }).then(function(confirmation) {
            if (!confirmation) {
              return $ionicHistory.goBack();
            } else {
              return $state.go('circulatorWifi');
            }
          });
        };
        showWifiConnectionTroubleAlert = function() {
          return alertService.confirm({
            headerColor: 'alert-yellow',
            icon: 'fail',
            titleString: locale.getString('update.firmwareUpdateWifiProblemsTitle'),
            bodyString: locale.getString('update.firmwareUpdateWifiProblemsDescription'),
            okText: locale.getString('update.firmwareUpdateWifiRequiredOkText'),
            cancelText: locale.getString('update.firmwareUpdateWifiRequiredCancelText'),
            link: faqLinkConfig.dfuProblems
          }).then(function(confirmation) {
            if (!confirmation) {
              return $ionicHistory.goBack();
            } else {
              return $state.go('circulatorWifi');
            }
          });
        };
        showTftpAccessTroubleAlert = function() {
          return alertService.alert({
            headerColor: 'alert-red',
            icon: 'fail',
            titleString: locale.getString('update.cannotAccessTftpServerTitle'),
            bodyString: locale.getString('update.cannotAccessTftpServerDescription'),
            link: faqLinkConfig.dfuProblems
          })["finally"](function() {
            return $ionicHistory.goBack();
          });
        };
        debugService.log('Checking Joule wifi connection status', TAG);
        return circulatorManager.getWifiStatus().then(function(result) {
          return $timeout((function() {
            return result;
          }), 200);
        }).then(function(result) {
          if (result === null || result.connectionStatus === wifiConnectionStates.WIFI_IDLE) {
            debugService.log('Joule is not connected to wifi.  Redirecting to wifi setup', TAG, {
              result: result
            });
            showWifiConnectionRequiredAlert();
          } else if (result.connectionStatus === wifiConnectionStates.WIFI_GOT_IP) {
            debugService.error('Joule is connected to wifi, but cannot access TFTP server.  Aborting!', TAG, {
              result: result
            });
            showTftpAccessTroubleAlert();
          } else {
            debugService.log('Joule is having trouble connecting to wifi, alert then set up wifi', TAG, {
              result: result
            });
            showWifiConnectionTroubleAlert();
          }
          throw new SafeAbortError('safely aborting firmware update to connect to wi-fi');
        });
      };
      return circulatorManager.wifiDFUDownloadTFTP(host, filename, sha256, totalBytes, useHTTP).progress(function(val) {
        var percent;
        percent = (val.blocksSent / val.totalBlocks) * 100;
        debugService.log("Received TFTP download progress update - " + (percent.toFixed(2)) + "% completed", TAG, val);
        $scope.percentUpdated = percent.toFixed(1);
        $scope.showProgress = true;
        return $timeout();
      }).then(function() {
        debugService.log('TFTP download complete, waiting 10 seconds to calculate sha256', TAG);
        $scope.step = $scope.steps.applying;
        $timeout();
        return $window.Q.delay(10000);
      }).then(function() {
        debugService.log('Setting wifi firmware', TAG, {
          sha256: sha256
        });
        return circulatorManager.setWifiDFUFirmware(sha256);
      }).then(function() {
        return verifyWifiDFUStatus(firmwareUpdateConfig.numGetWiFiDFUStatusRetries, sha256);
      })["catch"](function(error) {
        if (error instanceof $window.CirculatorSDK.TftpRoutingError) {
          debugService.warn('Could not reach TFTP server', TAG, {
            error: error
          });
          $ionicLoading.hide();
          return checkWiFiConnectionStatus();
        } else {
          debugService.warn('Error while applying TFTP update', TAG, {
            error: error
          });
          throw error;
        }
      });
    };
    fetchAndApplyUpdate = function(update) {
      var ref, ref1, ref2;
      $scope.step = $scope.steps.uploading;
      if (((ref = update.transfer) != null ? ref.type : void 0) === firmwareTransferTypes.tftp) {
        return applyTftpUpdate(update.transfer);
      } else if (((ref1 = update.transfer) != null ? ref1.type : void 0) === firmwareTransferTypes.http) {
        update.transfer.useHTTP = true;
        return applyTftpUpdate(update.transfer);
      } else if (((ref2 = update.transfer) != null ? ref2.type : void 0) === firmwareTransferTypes.download) {
        return firmwareUpdateService.fetchFirmwareImage(update.transfer.url).then(_.partial(sendFile, update.type));
      } else {
        return firmwareUpdateService.fetchFirmwareImage(update.location).then(_.partial(sendFile, update.type));
      }
    };
    enterBootMode = function() {
      $scope.step = $scope.steps.rebooting;
      $timeout();
      debugService.log("Entering boot mode " + $scope.manifest.bootModeType, TAG);
      return circulatorManager.enterBootMode($scope.manifest.bootModeType).then(resetConnections).then(function() {
        return $window.Q.delay(firmwareUpdateConfig.postRebootDelaySecs * 1000);
      });
    };
    restart = function() {
      $scope.step = $scope.steps.rebooting;
      $timeout();
      debugService.log('Rebooting Joule', TAG);
      return circulatorManager.restartDevice().then(resetConnections).then(function() {
        return $window.Q.delay(firmwareUpdateConfig.postRebootDelaySecs * 1000);
      });
    };
    reboot = function() {
      if ($scope.manifest.bootModeType != null) {
        return enterBootMode();
      } else {
        return restart();
      }
    };
    waitForReconnect = function() {
      $scope.step = $scope.steps.connecting;
      $timeout();
      return waitForConnectionState(circulatorConnectionStates.connected, firmwareUpdateConfig.reconnectTimeoutSecs);
    };
    waitForConnectionState = function(desiredConnectionState, timeoutSecs) {
      var deferred, timeoutPromise;
      deferred = $window.Q.defer();
      timeoutPromise = null;
      unbindConnectionUpdateHandler = _.noop;
      unbindConnectionUpdateHandler = circulatorManager.bindCirculatorConnectionUpdateHandler(function() {
        var connectionState;
        connectionState = circulatorManager.getCirculatorConnectionState();
        debugService.log("While waiting for " + desiredConnectionState + ", connection state changed to " + connectionState, TAG);
        if (connectionState === desiredConnectionState) {
          unbindConnectionUpdateHandler();
          if (timeoutPromise != null) {
            $timeout.cancel(timeoutPromise);
          }
          return deferred.resolve();
        }
      });
      timeoutPromise = $timeout(function() {
        debugService.log("Timed out waiting for Joule to be " + desiredConnectionState, TAG);
        unbindConnectionUpdateHandler();
        return deferred.reject("Timed out waiting for Joule to be " + desiredConnectionState + " after " + timeoutSecs + " seconds");
      }, timeoutSecs * 1000);
      return deferred.promise;
    };
    resetConnections = function() {
      return circulatorManager.resetAllConnections();
    };
    successHandler = function() {
      debugService.log("Successfully completed firmware update from " + $scope.currentVersion + " to new version " + $scope.newVersion, TAG);
      $ionicLoading.hide();
      firmwareUpdateService.invalidateCachedUpdateAvailability();
      return alertService.alert({
        headerColor: 'alert-green',
        icon: 'success',
        titleString: locale.getString('update.firmwareUpdateSuccessTitle'),
        bodyString: locale.getString('update.firmwareUpdateSuccessDescription'),
        okText: locale.getString('popup.great')
      });
    };
    failureHandler = function(error) {
      $ionicLoading.hide();
      if (!(error instanceof SafeAbortError)) {
        return circulatorConnectionError.handleErrorWithPopup(error)["catch"](function(error) {
          debugService.error("Got error while applying updates from " + $scope.currentVersion + " to new version " + $scope.newVersion, TAG, {
            error: error
          });
          return showFailurePopup().then(function() {
            return cacheService.remove('lastDateChecked', 'firmwareUpdateService');
          }).then(restart);
        });
      }
    };
    finallyHandler = function() {
      var ref, ref1;
      debugService.log('Firmware update complete', TAG);
      $scope.updating = false;
      if ((ref = $window.plugins) != null) {
        if ((ref1 = ref.insomnia) != null) {
          ref1.allowSleepAgain();
        }
      }
      return deregisterHardwareBackButtonAction();
    };
    if (circulatorManager.getCirculatorCookState() === cookStates.cooking) {
      return alertService.alert({
        headerColor: 'alert-red',
        icon: 'fail',
        titleString: locale.getString('update.firmwareUpdateFailedTitle'),
        bodyString: locale.getString('update.firmwareUpdateWhileCooking'),
        okText: locale.getString('general.okay')
      }).then(goHome);
    } else {
      return run();
    }
  };
  $scope.$on('$ionicView.beforeEnter', function() {
    init();
    checkForUpdates();
    return unbindConnectionUpdateHandler = circulatorManager.bindCirculatorConnectionUpdateHandler(function(connectionState) {
      $scope.connected = connectionState === circulatorConnectionStates.connected;
      return $timeout();
    });
  });
  return $scope.$on('$ionicView.leave', function() {
    deregisterHardwareBackButtonAction();
    return unbindConnectionUpdateHandler();
  });
}]);

this.app.controller('guideDonenessController', ["$scope", "$state", "$stateParams", "$timeout", "guideService", "preferences", "temperatureUnitService", "statusBarService", "statusBarStyles", "utilities", "floatingActionButtonStates", function($scope, $state, $stateParams, $timeout, guideService, preferences, temperatureUnitService, statusBarService, statusBarStyles, utilities, floatingActionButtonStates) {
  var loadedPosters, onBeforeEnter, onLeave, onLoaded, swiperOptions;
  $scope.videoEnabled = preferences.get('enableVideo');
  $scope.programButtonWidth = 70;
  swiperOptions = {
    effect: 'fade',
    onInit: function(swiper) {
      return $scope.swiper = swiper;
    },
    onSlideChangeEnd: function(swiper) {
      $scope.programIndex = swiper.activeIndex;
      return $timeout();
    }
  };
  loadedPosters = [];
  $scope.shouldDisablePoster = function(index) {
    if (_.includes(loadedPosters, index)) {
      return false;
    } else if (index > ($scope.programIndex + 1) || index < ($scope.programIndex - 1)) {
      return true;
    } else {
      loadedPosters.push(index);
      return false;
    }
  };
  $scope.shouldDisableVideo = function(index) {
    return !$scope.videoEnabled || (index !== $scope.programIndex);
  };
  $scope.getTemperatureString = function(value) {
    var currentTemperatureUnit;
    currentTemperatureUnit = temperatureUnitService.get();
    if (currentTemperatureUnit === 'f') {
      return (Math.round(utilities.convertCtoF(value))) + "°F";
    } else {
      return value + "°C";
    }
  };
  $scope.selectProgram = function(program) {
    return $scope.swiper.slideTo($scope.guide.programs.indexOf(program));
  };
  $scope.buttonAction = function() {
    return $state.go('guideTimer', {
      id: $scope.guide.id,
      programId: $scope.guide.programs[$scope.programIndex].id
    });
  };
  onLoaded = function() {
    return guideService.get($stateParams.id).then(function(guide) {
      var defaultProgramIndex;
      $scope.guide = guide;
      defaultProgramIndex = _.findIndex(guide.programs, function(p) {
        var ref;
        return p.id === ((ref = guide.defaultProgram) != null ? ref.id : void 0);
      });
      $scope.programIndex = swiperOptions.initialSlide = Math.max(defaultProgramIndex, 0);
      return $scope.swiperOptions = swiperOptions;
    });
  };
  onBeforeEnter = function() {
    $scope.showVideo = true;
    $scope.videoEnabled = preferences.get('enableVideo');
    statusBarService.setStyle(statusBarStyles.hidden);
    return $scope.buttonState = floatingActionButtonStates.next;
  };
  onLeave = function() {
    $scope.showVideo = false;
    return $scope.$digest();
  };
  $scope.$on('$ionicView.loaded', onLoaded);
  $scope.$on('$ionicView.beforeEnter', onBeforeEnter);
  return $scope.$on('$ionicView.leave', onLeave);
}]);

this.app.controller('guideOverviewController', ["$scope", "$state", "$stateParams", "$ionicModal", "$timeout", "appConfig", "guideService", "preferences", "statusBarService", "statusBarStyles", "floatingActionButtonStates", function($scope, $state, $stateParams, $ionicModal, $timeout, appConfig, guideService, preferences, statusBarService, statusBarStyles, floatingActionButtonStates) {
  var onBeforeEnter, onLeave, onLoaded;
  $scope.videoEnabled = preferences.get('enableVideo');
  $scope.onReadMoreClick = function() {
    var modalOptions;
    modalOptions = {
      scope: $scope,
      animation: 'slide-in-up'
    };
    return $ionicModal.fromTemplateUrl('templates/modals/overview-description/overview-description.html', modalOptions).then(function(modal) {
      var unbindlistener;
      $scope.modal = modal;
      $scope.modal.show();
      $scope.closeModal = function() {
        return $scope.modal.hide();
      };
      return unbindlistener = $scope.$on('modal.hidden', function() {
        unbindlistener();
        return $scope.modal.remove();
      });
    });
  };
  $scope.getGuideOverviewImage = function() {
    if (!$scope.guide) {
      return;
    }
    if (!$scope.videoEnabled) {
      return $scope.guide.noVideoThumbnail || $scope.guide.image;
    } else {
      return $scope.guide.image;
    }
  };
  $scope.sliderOptions = {
    onTransitionStart: function(swiper) {
      if (swiper.activeIndex === 1) {
        $scope.stepsSelected = true;
      } else {
        $scope.stepsSelected = false;
      }
      return $timeout();
    },
    onTransitionEnd: function(swiper) {
      if (swiper.activeIndex === 1) {
        $scope.disableOverviewVideo = true;
      } else {
        $scope.disableOverviewVideo = false;
      }
      return $timeout();
    }
  };
  onLoaded = function() {
    guideService.get($stateParams.id).then(function(guide) {
      return $scope.guide = guide;
    }).then(function(guide) {
      if ($scope.videoEnabled) {
        return guideService.prefetchVideos(guide);
      }
    });
    return $scope.nextAction = function() {
      return $state.go('guideDoneness', {
        id: $scope.guide.id
      }, {
        reload: true
      });
    };
  };
  onBeforeEnter = function() {
    statusBarService.setStyle(statusBarStyles.hidden);
    return $scope.buttonState = floatingActionButtonStates.next;
  };
  onLeave = function() {
    var ref;
    return (ref = $scope.modal) != null ? ref.remove() : void 0;
  };
  $scope.$on('$ionicView.loaded', onLoaded);
  $scope.$on('$ionicView.beforeEnter', onBeforeEnter);
  return $scope.$on('$ionicView.leave', onLeave);
}]);

this.app.controller('guideStepsController', ["$scope", "$stateParams", "guideService", "statusBarService", "statusBarStyles", function($scope, $stateParams, guideService, statusBarService, statusBarStyles) {
  var onBeforeEnter, onLoaded;
  onLoaded = function() {
    return guideService.get($stateParams.id).then(function(guide) {
      return $scope.guide = guide;
    });
  };
  onBeforeEnter = function() {
    return statusBarService.setStyle(statusBarStyles.hidden);
  };
  $scope.$on('$ionicView.loaded', onLoaded);
  return $scope.$on('$ionicView.beforeEnter', onBeforeEnter);
}]);

this.app.controller('guideTimerController', ["$ionicLoading", "$rootScope", "$scope", "$state", "$stateParams", "$window", "analyticsService", "circulatorConnectionStates", "circulatorManager", "CirculatorProgram", "debugService", "guideService", "loggingTags", "programTypes", "statusBarService", "statusBarStyles", "utilities", "locale", "$ionicHistory", "$timeout", "alertService", "floatingActionButtonStates", "circulatorConnectionError", "cookStates", "programSteps", function($ionicLoading, $rootScope, $scope, $state, $stateParams, $window, analyticsService, circulatorConnectionStates, circulatorManager, CirculatorProgram, debugService, guideService, loggingTags, programTypes, statusBarService, statusBarStyles, utilities, locale, $ionicHistory, $timeout, alertService, floatingActionButtonStates, circulatorConnectionError, cookStates, programSteps) {
  var TAG, getProgramFromPage, onBeforeEnter, onBeforeLeave, onLoaded, onStartProgramError, onStartProgramSuccess, shakeAnimationDuration, shakeFunc, startProgram, startProgramAndDropFood, unbindCirculatorConnectionUpdateHandler, updateButtonState, updateTimerForExistingProgram, waitForDropFoodProgramStep;
  TAG = 'GuideTimerView';
  shakeAnimationDuration = 820;
  shakeFunc = function() {
    $scope.shake = true;
    return $timeout((function() {
      return $scope.shake = false;
    }), shakeAnimationDuration);
  };
  $scope.shakeMe = _.throttle(shakeFunc, shakeAnimationDuration + 100, true);

  /* eslint-disable no-unreachable */
  updateButtonState = function() {
    var circulatorConnectionState, cookState;
    circulatorConnectionState = circulatorManager.getCirculatorConnectionState();
    cookState = circulatorManager.getCirculatorCookState();
    switch (circulatorConnectionState) {
      case circulatorConnectionStates.unpaired:
        $scope.buttonState = floatingActionButtonStates.unpaired;
        $scope.buttonAction = function() {
          return $state.go('buyJoule', {
            id: $scope.guide.id
          });
        };
        return $timeout();
      case circulatorConnectionStates.jouleFound:
        $scope.buttonState = floatingActionButtonStates.jouleFound;
        $scope.buttonAction = function() {
          return $state.go('pairingSequencePrompt');
        };
        return $timeout();
      case circulatorConnectionStates.connected:
        if ($scope.time) {
          switch (cookState) {
            case cookStates.idle:
              $scope.buttonState = floatingActionButtonStates.powerActivate;
              $scope.buttonAction = function() {
                return startProgram();
              };
              return $timeout();
            case cookStates.cooking:
              if ($stateParams.update) {
                $scope.buttonState = floatingActionButtonStates.startActivate;
                return $scope.buttonAction = function() {
                  debugService.log('only updating timer', [TAG, loggingTags.cook]);
                  return updateTimerForExistingProgram();
                };
              } else {
                $scope.buttonState = floatingActionButtonStates.powerActivate;
                return $scope.buttonAction = function() {
                  return alertService.confirm({
                    headerColor: 'alert-yellow',
                    iconUrl: 'svg/question.svg#question',
                    titleString: locale.getString('popup.programInterruptTitle'),
                    bodyString: locale.getString('popup.programInterruptDescription'),
                    okText: locale.getString('general.okay'),
                    cancelText: locale.getString('general.cancel')
                  }).then(function(confirmation) {
                    if (!confirmation) {
                      analyticsService.track('Canceled Program Interrupt');
                      return debugService.log('User clicked cancel on program interrupt', TAG);
                    } else {
                      analyticsService.track('Proceeded with Program Interrupt');
                      debugService.log('User proceeded with program interrupt', TAG);
                      return startProgram();
                    }
                  });
                };
              }
              break;
            default:
              $scope.buttonState = floatingActionButtonStates.connecting;
              $scope.buttonAction = circulatorConnectionError.handleConnectingWithPopup;
              return $timeout();
          }
        } else {
          if ($stateParams.update) {
            $scope.buttonState = floatingActionButtonStates.startInactive;
          } else {
            $scope.buttonState = floatingActionButtonStates.powerInactive;
          }
          $scope.buttonAction = _.noop;
          return $timeout();
        }
        break;
      case circulatorConnectionStates.connecting:
        $scope.buttonState = floatingActionButtonStates.connecting;
        $scope.buttonAction = circulatorConnectionError.handleConnectingWithPopup;
        return $timeout();
      case circulatorConnectionStates.disconnected:
        $scope.buttonState = floatingActionButtonStates.disconnected;
        $scope.buttonAction = function() {
          return $state.go('connectionTroubleshooting');
        };
        return $timeout();
    }
  };

  /* eslint-enable no-unreachable */
  onStartProgramSuccess = function(programParams) {
    $ionicLoading.hide();
    analyticsService.track('Started a Guided Program', programParams);
    debugService.log('Started a Guided Program', [TAG, loggingTags.cook], programParams);
    $ionicHistory.nextViewOptions({
      disableBack: true,
      historyRoot: true
    });
    return $state.go('cook', {
      showStepsHint: $stateParams.update !== 'true',
      shouldShowTempHint: 'true'
    });
  };
  onStartProgramError = function(error, retryFunction) {
    $ionicLoading.hide();
    return circulatorConnectionError.handleErrorWithPopup(error)["catch"](function(error) {
      debugService.error('unhandled error while starting program', [TAG, loggingTags.cook], {
        error: error
      });
      return alertService.confirm({
        headerColor: 'alert-yellow',
        icon: 'fail',
        titleString: locale.getString('popup.startProgramFailureTitle'),
        bodyString: locale.getString('popup.startProgramFailureDescription'),
        okText: locale.getString('general.tryAgain')
      }).then(function(confirmation) {
        if (!confirmation) {
          return;
        }
        debugService.log('Retrying start program after failed attempt', TAG);
        return retryFunction();
      });
    });
  };
  getProgramFromPage = function() {
    var cookId, newProgram, ref;
    cookId = null;
    if ($stateParams.update) {
      cookId = (ref = circulatorManager.getProgramState().programMetadata) != null ? ref.cookId : void 0;
    } else {
      cookId = utilities.generateCookId();
    }
    newProgram = {
      id: $scope.program.id,
      setPoint: $scope.program.cookingTemperature,
      holdingTemperature: parseFloat($scope.program.holdingTemperature),
      programType: programTypes.automatic,
      guide: $scope.guide.id,
      cookTime: utilities.convertMinutesToSeconds($scope.time.duration),
      programMetadata: {
        guideId: $scope.guide.id,
        programId: $scope.program.id,
        timerId: $scope.time.id,
        cookId: cookId
      }
    };
    return newProgram;
  };
  startProgram = function() {
    var newProgram;
    debugService.log('Attempting to start a program', [TAG, loggingTags.cook]);
    $ionicLoading.show({
      template: "<div class='loading-indicator'/>"
    });
    newProgram = getProgramFromPage();
    return circulatorManager.startProgram(newProgram).then(function() {
      var guideId, programId, ref;
      ref = newProgram.programMetadata, guideId = ref.guideId, programId = ref.programId;
      return onStartProgramSuccess({
        guideId: guideId,
        programId: programId
      });
    })["catch"](function(error) {
      return onStartProgramError(error, startProgram);
    }).done(_.noop, function(e) {
      return debugService.onPromiseUnhandledRejection(e, TAG);
    });
  };
  startProgramAndDropFood = function() {
    var newProgram;
    debugService.log('Attempting to update a program, and calling drop food.', [TAG, loggingTags.cook]);
    $ionicLoading.show({
      template: "<div class='loading-indicator'/>"
    });
    newProgram = getProgramFromPage();
    return circulatorManager.startProgram(newProgram).then(function() {
      return waitForDropFoodProgramStep();
    }).then(function() {
      return circulatorManager.dropFood();
    }).then(onStartProgramSuccess)["catch"](function(error) {
      return onStartProgramError(error, startProgramAndDropFood);
    }).done(_.noop, function(e) {
      return debugService.onPromiseUnhandledRejection(e, TAG);
    });
  };
  waitForDropFoodProgramStep = function() {
    var deferred, unbindProgramStepUpdatedHandler;
    deferred = $window.Q.defer();
    unbindProgramStepUpdatedHandler = circulatorManager.bindProgramStepUpdateHandlers(function(programStep) {
      if (programStep === programSteps.waitForFood) {
        unbindProgramStepUpdatedHandler();
        return deferred.resolve();
      }
    });
    return deferred.promise;
  };
  updateTimerForExistingProgram = function() {
    debugService.log('Updating timer for existing guided program', [TAG, loggingTags.cook], {
      programStep: circulatorManager.getProgramStepState()
    });
    if (circulatorManager.getProgramStepState() === programSteps.cook) {
      return startProgramAndDropFood();
    } else {
      return startProgram();
    }
  };
  $scope.getEstimatedTimeString = function(time) {
    return utilities.humanizeDuration(utilities.convertMinutesToMilliseconds(time.duration));
  };
  $scope.selectStartingTemperatureOption = function(temperature) {
    $scope.startingTemperature = temperature;
    switch (temperature.id) {
      case 'fresh':
        return $scope.time = $scope.program.freshTimes[$scope.timeIndex];
      case 'frozen':
        return $scope.time = $scope.program.frozenTimes[$scope.timeIndex];
    }
  };
  $scope.selectTime = function(time, index) {
    $scope.time = time;
    $scope.timeIndex = index;
    updateButtonState();
    return debugService.log('Selected Time Option', [TAG, loggingTags.cook], time);
  };
  $scope.startingTemperatures = [
    {
      id: 'fresh',
      title: locale.getString('time.startingTemperatureFresh')
    }, {
      id: 'frozen',
      title: locale.getString('time.startingTemperatureFrozen')
    }
  ];
  $scope.backAction = function() {
    if ($stateParams.update) {
      return $state.go('cook');
    } else {
      return $ionicHistory.goBack();
    }
  };
  onLoaded = function() {
    return guideService.get($stateParams.id).then(function(guide) {
      $scope.guide = guide;
      $scope.program = _.findWhere($scope.guide.programs, {
        id: $stateParams.programId
      });
      $scope.cookingTemperature = $scope.program.cookingTemperature;
      $scope.startingTemperature = _.first($scope.startingTemperatures);
      if ($scope.program.freshTimes.length === 1) {
        $scope.selectTime(_.first($scope.program.freshTimes), 0);
      }
      return updateButtonState();
    })["catch"](function(error) {
      return debugService.error('guide service get failed', [TAG, loggingTags.cook], {
        error: error
      });
    });
  };
  unbindCirculatorConnectionUpdateHandler = _.noop;
  onBeforeEnter = function() {
    updateButtonState();
    unbindCirculatorConnectionUpdateHandler = circulatorManager.bindCirculatorConnectionUpdateHandler(updateButtonState);
    return statusBarService.setStyle(statusBarStyles.light);
  };
  onBeforeLeave = function() {
    return unbindCirculatorConnectionUpdateHandler();
  };
  $scope.$on('$ionicView.loaded', onLoaded);
  $scope.$on('$ionicView.beforeEnter', onBeforeEnter);
  return $scope.$on('$ionicView.beforeLeave', onBeforeLeave);
}]);

this.app.controller('legalController', ["$scope", "$window", function($scope, $window) {
  $scope.handleClickEula = function() {
    if (ionic.Platform.isIOS()) {
      $window.open('https://www.chefsteps.com/eula-ios', '_system');
    } else if (ionic.Platform.isAndroid()) {
      $window.open('https://www.chefsteps.com/eula-android', '_system');
    }
    return true;
  };
  $scope.handleClickPrivacy = function() {
    $window.open('https://www.chefsteps.com/privacy', '_system');
    return true;
  };
  $scope.handleClickTerms = function() {
    $window.open('https://www.chefsteps.com/terms', '_system');
    return true;
  };
  return $scope.handleWarrantyClick = function() {
    $window.open('https://www.chefsteps.com/joule/warranty', '_system');
    return true;
  };
}]);

this.app.controller('buyJouleController', ["$scope", "$state", "$stateParams", "appConfig", "guideService", "preferences", "statusBarService", "statusBarStyles", "sequenceBackService", "$ionicHistory", "advertisementService", function($scope, $state, $stateParams, appConfig, guideService, preferences, statusBarService, statusBarStyles, sequenceBackService, $ionicHistory, advertisementService) {
  var onBeforeEnter, onLoaded;
  $scope.jouleAdClick = function() {
    advertisementService.openAdTarget({
      campaign: 'hardcoded'
    }, 'guide');
    return true;
  };
  $scope.onCancelButtonClick = sequenceBackService.goToSequenceBackView;
  $scope.videoEnabled = preferences.get('enableVideo');
  $scope.getGuideOverviewImage = function() {
    if (!$scope.guide) {
      return './images/buy-joule-default.jpg';
    }
    if (!$scope.videoEnabled) {
      return $scope.guide.noVideoThumbnail || $scope.guide.image;
    } else {
      return $scope.guide.image;
    }
  };
  $scope.getGuideVideo = function() {
    var ref;
    return ((ref = $scope.guide) != null ? ref.video : void 0) || './videos/buy-joule-default.mp4';
  };
  onLoaded = function() {
    if ($stateParams.id) {
      return guideService.get($stateParams.id).then(function(guide) {
        return $scope.guide = guide;
      }).then(function(guide) {
        if ($scope.videoEnabled) {
          return guideService.prefetchVideos(guide);
        }
      });
    }
  };
  onBeforeEnter = function() {
    var previousView;
    statusBarService.setStyle(statusBarStyles.hidden);
    $scope.closeButtonState = 'cancel';
    previousView = $ionicHistory.backView();
    return sequenceBackService.setSequenceBackView(previousView.stateName, previousView.stateParams);
  };
  $scope.$on('$ionicView.loaded', onLoaded);
  return $scope.$on('$ionicView.beforeEnter', onBeforeEnter);
}]);

this.app.controller('notificationsController', ["statusBarService", "statusBarStyles", "$scope", "$state", "pushRegistrationService", function(statusBarService, statusBarStyles, $scope, $state, pushRegistrationService) {
  var onBeforeEnter, onBeforeLeave;
  $scope.enableNotifications = function() {
    pushRegistrationService.register();
    return $scope.notificationsSet = true;
  };
  $scope.onNextButtonClick = function() {
    return $state.go('pairingSequencePrompt', {
      isOnboarding: true
    });
  };
  onBeforeEnter = function() {
    return statusBarService.setStyle(statusBarStyles.hidden);
  };
  onBeforeLeave = function() {
    return pushRegistrationService.register();
  };
  $scope.$on('$ionicView.beforeEnter', onBeforeEnter);
  return $scope.$on('$ionicView.beforeLeave', onBeforeLeave);
}]);

this.app.controller('onboardingController', ["userService", "$scope", "statusBarService", "statusBarStyles", "$state", "locale", "pushRegistrationService", function(userService, $scope, statusBarService, statusBarStyles, $state, locale, pushRegistrationService) {
  var onBeforeEnter;
  $scope.nextButtonClicked = function() {
    if (ionic.Platform.isIOS()) {
      return $state.go('notifications');
    } else {
      pushRegistrationService.register();
      return $state.go('pairingSequencePrompt', {
        isOnboarding: true
      });
    }
  };
  $scope.onVideoPlaying = function() {
    return $scope.videoHasStarted = true;
  };
  $scope.onLoopLimitReached = function() {
    return $scope.videoHasEnded = true;
  };
  $scope.getButtonText = function() {
    if ($scope.videoHasFinished) {
      return locale.getString('onboarding.onboardingButtonPrimary');
    }
    return locale.getString('general.skip');
  };
  onBeforeEnter = function() {
    statusBarService.setStyle(statusBarStyles.hidden);
    return $scope.onboardingHeader = locale.getString('onboarding.onboardingHeaderPrimary', {
      userName: userService.get().name
    });
  };
  $scope.$on('$ionicView.beforeEnter', onBeforeEnter);
  return this;
}]);

this.app.controller('pairingPromptController', ["$scope", "$state", "$ionicLoading", "$ionicHistory", "$stateParams", "circulatorManager", "alertService", "statusBarService", "statusBarStyles", "locale", "debugService", "circulatorConnectionError", "circulatorConnectionStates", "sequenceBackService", "appConfig", "userService", "faqLinkConfig", function($scope, $state, $ionicLoading, $ionicHistory, $stateParams, circulatorManager, alertService, statusBarService, statusBarStyles, locale, debugService, circulatorConnectionError, circulatorConnectionStates, sequenceBackService, appConfig, userService, faqLinkConfig) {
  var TAG, onBeforeEnter, scanButtonAction;
  TAG = 'PairingPrompt';
  $scope.onCancelButtonClick = sequenceBackService.goToSequenceBackView;
  scanButtonAction = function() {
    var onCirculatorScanEmpty, onCirculatorScanError, onCirculatorScanSucess, scan;
    scan = function() {
      debugService.log('Starting a scan for circulators', TAG);
      $ionicLoading.show({
        template: "<div class='loading-indicator' />",
        noBackdrop: true
      });
      return circulatorManager.createCirculatorScanSession().then(onCirculatorScanSucess)["catch"](onCirculatorScanError).done(_.noop, function(error) {
        return debugService.onPromiseUnhandledRejection(error, TAG);
      });
    };
    onCirculatorScanEmpty = function() {
      debugService.log('Scan for circulators had no results', TAG);
      return alertService.confirm({
        headerColor: 'alert-red',
        icon: 'fail',
        titleString: locale.getString('pairing.weDidNotFindAnyCirculatorsTitle'),
        bodyString: locale.getString('pairing.weDidNotFindAnyCirculators'),
        cancelText: locale.getString('general.okay'),
        okText: locale.getString('pairing.scanAgain'),
        link: faqLinkConfig.cantPair
      }).then(function(confirmation) {
        if (!confirmation) {
          return;
        }
        return scan();
      });
    };
    onCirculatorScanSucess = function(candidates) {
      debugService.log('Scan for circulators succeeded', TAG);
      $ionicLoading.hide();
      if (_.isEmpty(candidates)) {
        return onCirculatorScanEmpty();
      } else {
        return $state.go('pairingSequencePairing');
      }
    };
    onCirculatorScanError = function(error) {
      debugService.warn('Circulator scan error', TAG, {
        error: error
      });
      $ionicLoading.hide();
      return circulatorConnectionError.handleErrorWithPopup(error)["catch"](function(error) {
        debugService.error('Unhandled circulator scan error', TAG, {
          error: error
        });
        return alertService.alert({
          headerColor: 'alert-red',
          icon: 'fail',
          titleString: locale.getString('popup.bluetoothScanErrorTitle'),
          bodyString: locale.getString('popup.bluetoothScanErrorDescription')
        });
      }).done(_.noop, function(error) {
        return debugService.onPromiseUnhandledRejection(error, TAG);
      });
    };
    return scan();
  };
  $scope.connectToJouleAction = function() {
    var connectionState, isUnpaired;
    connectionState = circulatorManager.getCirculatorConnectionState();
    isUnpaired = _.includes([circulatorConnectionStates.unpaired, circulatorConnectionStates.jouleFound], connectionState);
    if (isUnpaired) {
      return scanButtonAction();
    } else {
      return alertService.alert({
        headerColor: 'alert-green',
        icon: 'success',
        titleString: locale.getString('popup.alreadyPairedTitle'),
        bodyString: locale.getString('popup.alreadyPairedDescription', {
          name: circulatorManager.getCurrentCirculatorName()
        })
      }).then(function() {
        $ionicHistory.nextViewOptions({
          disableBack: true,
          historyRoot: true
        });
        return $state.go(appConfig.defaultView);
      });
    }
  };
  onBeforeEnter = function() {
    var previousView;
    if (!userService.get()) {
      return alertService.alert({
        headerColor: 'alert-yellow',
        icon: 'heart-joule',
        titleString: locale.getString('pairing.signInToUseJouleTitle'),
        bodyString: locale.getString('pairing.signInToUseJouleDescription')
      }).then(function() {
        $ionicHistory.nextViewOptions({
          disableBack: true,
          historyRoot: true
        });
        return $state.go('welcome');
      });
    } else {
      statusBarService.setStyle(statusBarStyles.hidden);
      if ($stateParams.isOnboarding) {
        $scope.closeButtonState = 'skip';
        return sequenceBackService.setSequenceBackView('home');
      } else {
        $scope.closeButtonState = 'cancel';
        previousView = $ionicHistory.backView();
        return sequenceBackService.setSequenceBackView(previousView.stateName, previousView.stateParams);
      }
    }
  };
  return $scope.$on('$ionicView.beforeEnter', onBeforeEnter);
}]);

this.app.controller('pairingController', ["$rootScope", "$scope", "$ionicLoading", "alertService", "$ionicHistory", "circulatorManager", "$timeout", "$state", "locale", "statusBarService", "statusBarStyles", "debugService", "circulatorConnectionError", "sequenceBackService", "faqLinkConfig", "$ionicPlatform", "circulatorConnectionStates", "networkStateService", "appConfig", function($rootScope, $scope, $ionicLoading, alertService, $ionicHistory, circulatorManager, $timeout, $state, locale, statusBarService, statusBarStyles, debugService, circulatorConnectionError, sequenceBackService, faqLinkConfig, $ionicPlatform, circulatorConnectionStates, networkStateService, appConfig) {
  var TAG, deregisterHardwareBackButtonAction, navigateToNextView, onBeforeEnter, onEnter, onPairCandidateError, onPairCandidateNotConnectable, onPairCandidateSuccess, pairCandidate, pairingVideoPauseTime, pausePairingVideoTimeout, scanAgain;
  TAG = 'PairingView';
  navigateToNextView = null;
  $scope.candidates = {};
  $scope.circulatorWidth = 195;
  $scope.backgroundVideoInstance = {};
  deregisterHardwareBackButtonAction = _.noop;
  pairingVideoPauseTime = 27.75;
  $scope.onCloseButtonClick = sequenceBackService.goToSequenceBackView;
  $scope.scanAgainAction = function() {
    return scanAgain();
  };
  scanAgain = function() {
    var onCirculatorScanEmpty, onCirculatorScanError, onCirculatorScanSucess, scan;
    scan = function() {
      debugService.log('starting a scan for circulators', TAG);
      $ionicLoading.show({
        template: "<div class='loading-indicator' />",
        noBackdrop: true
      });
      return circulatorManager.createCirculatorScanSession().then(onCirculatorScanSucess)["catch"](onCirculatorScanError).done(_.noop, function(e) {
        return debugService.onPromiseUnhandledRejection(e, TAG);
      });
    };
    onCirculatorScanEmpty = function() {
      debugService.log('scan for circulators had no results', TAG);
      return alertService.confirm({
        headerColor: 'alert-red',
        icon: 'fail',
        titleString: locale.getString('pairing.weDidNotFindAnyCirculatorsTitle'),
        bodyString: locale.getString('pairing.weDidNotFindAnyCirculators'),
        cancelText: locale.getString('general.okay'),
        okText: locale.getString('pairing.scanAgain'),
        link: faqLinkConfig.cantPair
      }).then(function(confirmation) {
        if (!confirmation) {
          return $ionicHistory.goBack();
        } else {
          return scan();
        }
      });
    };
    onCirculatorScanSucess = function(candidates) {
      debugService.log('scan for circulators succeeded', TAG);
      $ionicLoading.hide();
      if (_.isEmpty(candidates)) {
        return onCirculatorScanEmpty();
      } else {
        $scope.candidates = _.values(circulatorManager.getLatestCirculatorScanSessionResult());
        return $scope.selectCandidate($scope.candidates[0]);
      }
    };
    onCirculatorScanError = function(error) {
      $ionicLoading.hide();
      return circulatorConnectionError.handleErrorWithPopup(error)["catch"](function(error) {
        debugService.error('unhandled circulator scan error', TAG, {
          error: error
        });
        return alertService.alert({
          headerColor: 'alert-red',
          icon: 'fail',
          titleString: locale.getString('popup.bluetoothScanErrorTitle'),
          bodyString: locale.getString('popup.bluetoothScanErrorDescription')
        });
      }).done(_.noop, function(e) {
        return debugService.onPromiseUnhandledRejection(e, TAG);
      });
    };
    return scan();
  };
  $scope.selectCandidate = function(candidate) {
    $scope.selectedCandidate = candidate;
    return $scope.selectedCandidateIndex = $scope.candidates.indexOf(candidate);
  };
  $scope.selectPreviousCandidate = _.throttle(function() {
    var currentCandidateIndex;
    currentCandidateIndex = $scope.candidates.indexOf($scope.selectedCandidate);
    if (currentCandidateIndex === 0) {
      return;
    }
    return $scope.selectCandidate($scope.candidates[currentCandidateIndex - 1]);
  });
  $scope.selectNextCandidate = function() {
    var currentCandidateIndex;
    currentCandidateIndex = $scope.candidates.indexOf($scope.selectedCandidate);
    if (currentCandidateIndex === ($scope.candidates.length - 1)) {
      return;
    }
    return $scope.selectCandidate($scope.candidates[currentCandidateIndex + 1]);
  };
  pausePairingVideoTimeout = null;
  onPairCandidateSuccess = function() {
    debugService.log('circulator candidate successfully paired', TAG);
    deregisterHardwareBackButtonAction();
    $timeout.cancel(pausePairingVideoTimeout);
    $scope.backgroundVideoInstance.actions.seekTo(pairingVideoPauseTime);
    $scope.backgroundVideoInstance.actions.play();
    $scope.pairingHeaderMessage = locale.getString('pairing.successButton');
    $timeout(navigateToNextView, 3000);
    return $timeout();
  };
  onPairCandidateError = function(error) {
    debugService.error('circulator candidate pair failed', TAG, {
      error: error
    });
    deregisterHardwareBackButtonAction();
    $scope.pairingStarted = false;
    return alertService.confirm({
      headerColor: 'alert-red',
      icon: 'fail',
      titleString: locale.getString('pairing.pairingFailedTitle'),
      bodyString: locale.getString('pairing.pairingFailedDescription'),
      cancelText: locale.getString('general.okay'),
      okText: locale.getString('pairing.tryAgain'),
      link: faqLinkConfig.cantPair
    }).then(function(confirmation) {
      if (!confirmation) {
        return $ionicHistory.goBack();
      } else {
        return scanAgain();
      }
    });
  };
  onPairCandidateNotConnectable = function() {
    debugService.warn('circulator candidate is not connectable', TAG);
    return alertService.confirm({
      headerColor: 'alert-red',
      icon: 'fail',
      titleString: locale.getString('pairing.circulatorIsNotConnectableTitle'),
      bodyString: locale.getString('pairing.circulatorIsNotConnectableDescription'),
      cancelText: locale.getString('general.okay'),
      okText: locale.getString('pairing.scanAgain')
    }).then(function(confirmation) {
      if (confirmation) {
        return scanAgain();
      }
    });
  };
  pairCandidate = function(candidate) {
    var openPromise, pairPromise, ref;
    debugService.log('attempting to pair with candidate', TAG, {
      address: candidate.address,
      name: candidate.name,
      isConnected: candidate.isConnected
    });
    if (candidate.isConnected) {
      return onPairCandidateNotConnectable();
    } else {
      $ionicLoading.show({
        template: "<div class='loading-indicator' />",
        noBackdrop: true
      });
      ref = circulatorManager.pair(candidate), pairPromise = ref.pairPromise, openPromise = ref.openPromise;
      deregisterHardwareBackButtonAction = $ionicPlatform.registerBackButtonAction(_.noop, 9000);
      openPromise.then(function() {
        $ionicLoading.hide();
        $scope.pairingStarted = true;
        $scope.pairingHeaderMessage = locale.getString('pairing.pressButton');
        pausePairingVideoTimeout = $timeout(function() {
          return $scope.backgroundVideoInstance.actions.pause();
        }, pairingVideoPauseTime * 1000);
        return $timeout();
      })["catch"](function(error) {
        $ionicLoading.hide();
        debugService.error('unhandled open promise error', TAG, {
          error: error
        });
        throw error;
      });
      return pairPromise.then(onPairCandidateSuccess)["catch"](onPairCandidateError).done(_.noop, function(e) {
        return debugService.onPromiseUnhandledRejection(e, TAG);
      });
    }
  };
  onBeforeEnter = function() {
    var connected, connecting, connectionState, disconnected, isPaired, nextView, ref;
    statusBarService.setStyle(statusBarStyles.hidden);
    $scope.pairingStarted = false;
    nextView = (ref = $state.current.data) != null ? ref.nextView : void 0;
    if (nextView) {
      navigateToNextView = function() {
        return $state.go(nextView);
      };
    } else {
      navigateToNextView = function() {
        return $ionicHistory.goBack();
      };
    }
    connectionState = circulatorManager.getCirculatorConnectionState();
    connecting = circulatorConnectionStates.connecting, connected = circulatorConnectionStates.connected, disconnected = circulatorConnectionStates.disconnected;
    isPaired = _.includes([connecting, connected, disconnected], connectionState);
    if (isPaired) {
      return alertService.alert({
        headerColor: 'alert-green',
        icon: 'success',
        titleString: locale.getString('popup.alreadyPairedTitle'),
        bodyString: locale.getString('popup.alreadyPairedDescription', {
          name: circulatorManager.getCurrentCirculatorName()
        })
      }).then(function() {
        return navigateToNextView();
      });
    } else {
      $scope.candidates = _.values(circulatorManager.getLatestCirculatorScanSessionResult());
      if (!_.isEmpty($scope.candidates)) {
        return $scope.selectCandidate($scope.candidates[0]);
      }
    }
  };
  onEnter = function() {
    if (_.isEmpty($scope.candidates)) {
      $ionicLoading.hide();
      return alertService.confirm({
        headerColor: 'alert-red',
        icon: 'fail',
        titleString: locale.getString('pairing.weDidNotFindAnyCirculatorsTitle'),
        bodyString: locale.getString('pairing.weDidNotFindAnyCirculators'),
        cancelText: locale.getString('general.okay'),
        okText: locale.getString('pairing.scanAgain'),
        link: faqLinkConfig.cantPair
      }).then(function(confirmation) {
        if (!confirmation) {
          return $ionicHistory.goBack();
        } else {
          return scanAgain();
        }
      });
    }
  };
  $scope.$on('$ionicView.beforeEnter', onBeforeEnter);
  $scope.$on('$ionicView.enter', onEnter);
  return $scope.pairCandidate = pairCandidate;
}]);

this.app.controller('trainingSuccessController', ["$scope", "$ionicHistory", "$state", "statusBarService", "statusBarStyles", "floatingActionButtonStates", "stepService", "cacheService", function($scope, $ionicHistory, $state, statusBarService, statusBarStyles, floatingActionButtonStates, stepService, cacheService) {
  var onBeforeEnter, onLoaded, trainingSuccessStepId;
  trainingSuccessStepId = '6I7EKYHr7ak6OKCiWy4eAO';
  onLoaded = function() {
    return stepService.get(trainingSuccessStepId).then(function(step) {
      return $scope.step = step;
    });
  };
  onBeforeEnter = function() {
    $scope.buttonAction = function() {
      return $state.go('home');
    };
    statusBarService.setStyle(statusBarStyles.hidden);
    return cacheService.set('isTraining', 'training', false);
  };
  $scope.$on('$ionicView.loaded', onLoaded);
  return $scope.$on('$ionicView.beforeEnter', onBeforeEnter);
}]);

this.app.controller('pairingSequenceSuccessController', ["$scope", "$state", "statusBarService", "statusBarStyles", "floatingActionButtonStates", "stepService", "cacheService", function($scope, $state, statusBarService, statusBarStyles, floatingActionButtonStates, stepService, cacheService) {
  var onBeforeEnter, onLoaded, pairingSequenceSuccessStepId;
  pairingSequenceSuccessStepId = '32BGWOwA9aaua66OeoEooK';
  $scope.buttonAction = function() {
    return $state.go('home');
  };
  $scope.skipAction = function() {
    cacheService.set('isTraining', 'training', false);
    return $state.go('home');
  };
  onLoaded = function() {
    return stepService.get(pairingSequenceSuccessStepId).then(function(step) {
      return $scope.step = step;
    });
  };
  onBeforeEnter = function() {
    cacheService.set('isTraining', 'training', true);
    return statusBarService.setStyle(statusBarStyles.hidden);
  };
  $scope.$on('$ionicView.loaded', onLoaded);
  return $scope.$on('$ionicView.beforeEnter', onBeforeEnter);
}]);

this.app.controller('profileController', ["$scope", "$timeout", "$state", "$ionicHistory", "appConfig", "userService", "debugService", "alertService", "analyticsService", "statusBarService", "statusBarStyles", "locale", "circulatorManager", "cookStates", "pushRegistrationService", function($scope, $timeout, $state, $ionicHistory, appConfig, userService, debugService, alertService, analyticsService, statusBarService, statusBarStyles, locale, circulatorManager, cookStates, pushRegistrationService) {
  var TAG, onBeforeEnter, signOut;
  TAG = 'ProfileView';
  $scope.userName = '';
  $scope.imageUrl = '';
  $scope.email = '';
  signOut = function() {
    debugService.info('User signed out', TAG);
    analyticsService.identify(null);
    analyticsService.track('User Signed Out');
    userService.signOut();
    pushRegistrationService.deregister().done(_.noop, function(e) {
      return debugService.onPromiseUnhandledRejection(e, TAG);
    });
    $ionicHistory.nextViewOptions({
      disableBack: true,
      historyRoot: true
    });
    return $state.go('welcome');
  };
  $scope.handleClickSignOut = function() {
    if (circulatorManager.getCirculatorCookState() === cookStates.cooking) {
      return alertService.confirm({
        headerColor: 'alert-yellow',
        iconUrl: 'svg/question.svg#question',
        bodyString: locale.getString('popup.signOutWhileCookingConfirmation'),
        cancelText: locale.getString('popup.no'),
        okText: locale.getString('popup.yes')
      }).then(function(confirmation) {
        if (!confirmation) {
          return;
        }
        return signOut();
      });
    } else {
      return signOut();
    }
  };
  onBeforeEnter = function() {
    var user;
    user = userService.get();
    if (user != null) {
      debugService.log('Valid user', TAG);
      $scope.userName = user.name;
      $scope.imageUrl = user.avatar_url;
      $scope.email = user.email;
      $timeout();
    } else {
      debugService.warn('Null user', TAG);
      alertService.alert({
        headerColor: 'alert-red',
        icon: 'fail',
        titleString: locale.getString('popup.embarrassing'),
        bodyString: locale.getString('popup.invalidProfileMessage')
      }).then(function() {
        $ionicHistory.nextViewOptions({
          disableBack: true,
          historyRoot: true
        });
        return $state.go(appConfig.defaultView);
      });
    }
    return statusBarService.setStyle(statusBarStyles.dark);
  };
  return $scope.$on('$ionicView.beforeEnter', onBeforeEnter);
}]);

this.app.controller('settingsController', ["$window", "$scope", "$state", "locale", "$ionicPopup", "$ionicLoading", "$timeout", "alertService", "temperatureUnitService", "preferences", "statusBarService", "statusBarStyles", "debugService", "circulatorManager", "circulatorConnectionStates", "circulatorConnectionError", "cacheService", "userService", "faqLinkConfig", function($window, $scope, $state, locale, $ionicPopup, $ionicLoading, $timeout, alertService, temperatureUnitService, preferences, statusBarService, statusBarStyles, debugService, circulatorManager, circulatorConnectionStates, circulatorConnectionError, cacheService, userService, faqLinkConfig) {
  var TAG, accountHeaderClickCount, onBeforeEnter, onLeave, unbindConnectionUpdateHandler, updateConnectionState;
  TAG = 'SettingsView';
  unbindConnectionUpdateHandler = _.noop;
  accountHeaderClickCount = 0;
  $scope.handleClickAccountHeader = function() {
    var updatedSetting;
    accountHeaderClickCount += 1;
    if (accountHeaderClickCount === 7) {
      updatedSetting = !cacheService.get('developerMenuEnabled', 'preferences');
      cacheService.set('developerMenuEnabled', 'preferences', updatedSetting);
      debugService.log('User toggled developer mode', TAG, {
        enabled: updatedSetting
      });
      accountHeaderClickCount = 0;
      return $state.go('home');
    }
  };
  $scope.handleClickPair = function() {
    var onCirculatorScanEmpty, onCirculatorScanError, onCirculatorScanSucess, scan;
    scan = function() {
      debugService.log('starting a scan for circulators', TAG);
      $ionicLoading.show({
        template: "<div class='loading-indicator' />",
        noBackdrop: true
      });
      return circulatorManager.createCirculatorScanSession().then(onCirculatorScanSucess)["catch"](onCirculatorScanError).done(_.noop, function(e) {
        return debugService.onPromiseUnhandledRejection(e, TAG);
      });
    };
    onCirculatorScanEmpty = function() {
      debugService.log('scan for circulators had no results', TAG);
      return alertService.confirm({
        headerColor: 'alert-red',
        icon: 'fail',
        titleString: locale.getString('pairing.weDidNotFindAnyCirculatorsTitle'),
        bodyString: locale.getString('pairing.weDidNotFindAnyCirculators'),
        cancelText: locale.getString('general.okay'),
        okText: locale.getString('pairing.scanAgain'),
        link: faqLinkConfig.cantPair
      }).then(function(confirmation) {
        if (!confirmation) {
          return;
        }
        return scan();
      });
    };
    onCirculatorScanSucess = function(candidates) {
      debugService.log('scan for circulators succeeded', TAG);
      $ionicLoading.hide();
      if (_.isEmpty(candidates)) {
        return onCirculatorScanEmpty();
      } else {
        return $state.go('pairingSequencePairing');
      }
    };
    onCirculatorScanError = function(error) {
      $ionicLoading.hide();
      return circulatorConnectionError.handleErrorWithPopup(error)["catch"](function(error) {
        debugService.error('unhandled circulator scan error', TAG, {
          error: error
        });
        return alertService.alert({
          headerColor: 'alert-red',
          icon: 'fail',
          titleString: locale.getString('popup.bluetoothScanErrorTitle'),
          bodyString: locale.getString('popup.bluetoothScanErrorDescription')
        });
      }).done(_.noop, function(e) {
        return debugService.onPromiseUnhandledRejection(e, TAG);
      });
    };
    return scan();
  };
  $scope.handleClickDefaultTempUnit = function() {
    $scope.choices = {};
    $scope.choices.temp = temperatureUnitService.get();
    return $ionicPopup.show({
      templateUrl: 'templates/views/settings/default-temperature-unit.html',
      scope: $scope,
      buttons: [
        {
          text: locale.getString('general.okay'),
          type: 'button-positive',
          onTap: function() {
            return $scope.choices.temp;
          }
        }
      ]
    }).then(function(selection) {
      debugService.log('new default temperature unit selected', TAG, selection);
      return temperatureUnitService.set(selection);
    });
  };
  $scope.handleClickVideoSettings = function() {
    if (!$scope.value) {
      $scope.value = {};
    }
    $scope.value.enableVideo = preferences.get('enableVideo');
    return $ionicPopup.show({
      templateUrl: 'templates/views/settings/video-settings.html',
      scope: $scope,
      buttons: [
        {
          text: locale.getString('general.okay'),
          type: 'button-positive',
          onTap: function() {
            return $scope.value.enableVideo;
          }
        }
      ]
    }).then(function(selection) {
      debugService.log("New video setting selection - enableVideo: " + selection, TAG);
      preferences.set('enableVideo', selection);
      return $scope.enableVideo = selection;
    });
  };
  $scope.handleClickDataUsage = function() {
    if (!$scope.value) {
      $scope.value = {};
    }
    $scope.value.useLessData = preferences.get('useLessData');
    return $ionicPopup.show({
      templateUrl: 'templates/views/settings/data-usage.html',
      scope: $scope,
      buttons: [
        {
          text: locale.getString('general.okay'),
          type: 'button-positive',
          onTap: function() {
            return $scope.value.useLessData;
          }
        }
      ]
    }).then(function(selection) {
      debugService.log('new data usage selection', TAG, selection);
      return preferences.set('useLessData', selection);
    });
  };
  updateConnectionState = function(connectionState) {
    var connected, connecting, disconnected, jouleFound, unpaired;
    unpaired = circulatorConnectionStates.unpaired, jouleFound = circulatorConnectionStates.jouleFound, connecting = circulatorConnectionStates.connecting, connected = circulatorConnectionStates.connected, disconnected = circulatorConnectionStates.disconnected;
    $scope.isUnpaired = _.includes([unpaired, jouleFound], connectionState);
    $scope.isConnecting = connecting === connectionState;
    $scope.isPaired = _.includes([connected, disconnected], connectionState);
    $scope.enableVideo = preferences.get('enableVideo');
    return $timeout();
  };
  onBeforeEnter = function() {
    $scope.user = userService.get();
    statusBarService.setStyle(statusBarStyles.dark);
    return unbindConnectionUpdateHandler = circulatorManager.bindCirculatorConnectionUpdateHandler(updateConnectionState);
  };
  onLeave = function() {
    return unbindConnectionUpdateHandler();
  };
  $scope.$on('$ionicView.beforeEnter', onBeforeEnter);
  return $scope.$on('$ionicView.leave', onLeave);
}]);

this.app.controller('signInController', ["$ionicLoading", "analyticsService", "$q", "authenticationService", "locale", "$state", "$ionicHistory", "$scope", "debugService", "csConfig", "$window", "ngFB", "alertService", "userService", "statusBarService", "statusBarStyles", "faqLinkConfig", "cacheService", function($ionicLoading, analyticsService, $q, authenticationService, locale, $state, $ionicHistory, $scope, debugService, csConfig, $window, ngFB, alertService, userService, statusBarService, statusBarStyles, faqLinkConfig, cacheService) {
  var TAG, facebookLogin, formLogin, onBeforeEnter, onFacebookClick, onFacebookLoginError, onFormInvalid, onFormLoginError, onFormLoginErrorTimedOut, onFormLoginErrorUnauthorized, onFormLoginErrorUnknown, onFormSignInClick, onFormValid, onLoginError, onLoginSuccess, onProgressStart, onUserLookupError, onUserOffline, setButton, setIsLoading, userLookup;
  TAG = 'SignInView';
  $scope.buttonText = locale.getString('authentication.login');
  $scope.buttonAction = function() {
    return analyticsService.track('User clicked disabled sign in button');
  };
  ngFB.init({
    appId: csConfig.facebookAppId
  });
  $scope.setForm = function(form) {
    return $scope.form = form;
  };
  $scope.createAccountAction = function() {
    return $state.go('createAccount');
  };
  setIsLoading = function(bool) {
    if (bool) {
      return $ionicLoading.show({
        template: "<div class='loading-indicator' />",
        noBackdrop: true
      });
    } else {
      return $ionicLoading.hide();
    }
  };
  setButton = function(state) {
    switch (state) {
      case 'active':
        $scope.buttonText = locale.getString('authentication.login');
        $scope.buttonClass = 'sign-in-button-active';
        return $scope.buttonAction = function() {
          return onFormSignInClick();
        };
      case 'disabled':
        $scope.buttonText = locale.getString('authentication.login');
        $scope.buttonClass = 'sign-in-button-disabled';
        return $scope.buttonAction = function() {
          return analyticsService.track('User clicked disabled sign in button');
        };
      case 'unauthorized':
        $scope.buttonText = locale.getString('authentication.emailPasswordDontMatch');
        $scope.buttonClass = 'sign-in-button-unauthorized';
        return $scope.buttonAction = function() {
          return _.noop;
        };
    }
  };
  $scope.isPasswordShowing = false;
  facebookLogin = function() {
    return ngFB.login({
      scope: 'public_profile, email'
    }).then(function(loginResp) {
      var access_token;
      access_token = loginResp.authResponse.accessToken;
      return ngFB.api({
        path: '/me',
        params: {
          access_token: access_token,
          '?fields': 'user_id'
        }
      }).then(function(apiResp) {
        var deferred, user_id;
        user_id = apiResp.id;
        deferred = $q.defer();
        return authenticationService.csAuthenticateFacebook({
          access_token: access_token,
          user_id: user_id
        }, deferred);
      });
    });
  };
  formLogin = function() {
    var passwordHash, salt;
    salt = cacheService.get('salt', 'security');
    passwordHash = $window.bcrypt.hashSync($scope.user.password || '', salt);
    debugService.log('User submitted login form', TAG, {
      passwordHash: passwordHash
    });
    return authenticationService.loginWithEmail($scope.user.email, $scope.user.password);
  };
  userLookup = function() {
    return authenticationService.me().then(function(user) {
      var token;
      analyticsService.identify(user);
      token = authenticationService.getToken();
      return userService.signIn(user, token);
    })["catch"](onUserLookupError);
  };
  onFormValid = function() {
    return setButton('active');
  };
  onFormInvalid = function() {
    return setButton('disabled');
  };
  onProgressStart = function() {
    return setIsLoading(true);
  };
  onFormLoginErrorUnauthorized = function(error) {
    debugService.info('Form login request got unauthorized response', TAG, {
      error: error
    });
    setButton('disabled');
    return alertService.alert({
      headerColor: 'alert-red',
      icon: 'fail',
      titleString: locale.getString('authentication.emailPasswordDontMatchTitle'),
      bodyString: locale.getString('authentication.emailPasswordDontMatch'),
      link: faqLinkConfig.cantSignIn
    });
  };
  onFormLoginErrorTimedOut = function(error) {
    debugService.error('Form Login Timed Out', TAG, {
      error: error
    });
    return alertService.alert({
      headerColor: 'alert-red',
      icon: 'fail',
      titleString: locale.getString('authentication.errorTimedOutHeaderPrimary'),
      bodyString: locale.getString('authentication.errorTimedOutHeaderSecondary'),
      link: faqLinkConfig.cantSignIn
    });
  };
  onFormLoginErrorUnknown = function(error) {
    debugService.error('Unknown form login error', TAG, {
      error: error
    });
    return alertService.alert({
      headerColor: 'alert-red',
      icon: 'fail',
      titleString: locale.getString('authentication.errorGenericHeaderPrimary'),
      bodyString: locale.getString('authentication.errorGenericHeaderSecondary'),
      link: faqLinkConfig.cantSignIn
    });
  };
  onLoginSuccess = function() {
    setIsLoading(false);
    analyticsService.track('User Signed In');
    $ionicHistory.nextViewOptions({
      disableBack: true
    });
    return $state.go('onboarding');
  };
  onUserLookupError = function(error) {
    onLoginError();
    debugService.error('Unknown user lookup error', TAG, {
      error: error
    });
    return alertService.alert({
      headerColor: 'alert-red',
      icon: 'fail',
      titleString: locale.getString('authentication.errorGenericHeaderPrimary'),
      bodyString: locale.getString('authentication.errorGenericHeaderSecondary'),
      link: faqLinkConfig.cantSignIn
    });
  };
  onUserOffline = function() {
    onLoginError();
    return alertService.alert({
      headerColor: 'alert-red',
      icon: 'fail',
      titleString: locale.getString('authentication.errorOfflineHeaderPrimary'),
      bodyString: locale.getString('authentication.errorOfflineHeaderSecondary')
    });
  };
  onFacebookClick = function() {
    if (!$window.navigator.onLine) {
      return onUserOffline();
    }
    onProgressStart();
    return facebookLogin().then(userLookup).then(onLoginSuccess)["catch"](onFacebookLoginError);
  };
  onFormSignInClick = function() {
    if (!$window.navigator.onLine) {
      return onUserOffline();
    }
    onProgressStart();
    return formLogin().then(userLookup).then(onLoginSuccess)["catch"](onFormLoginError);
  };
  onLoginError = function() {
    setIsLoading(false);
    return $scope.handleFormChange();
  };
  onFormLoginError = function(error) {
    onLoginError();
    if (!error) {
      return onFormLoginErrorUnknown();
    }
    switch (error.status) {
      case 403:
        return onFormLoginErrorUnauthorized(error);
      case 408:
        return onFormLoginErrorTimedOut(error);
      default:
        return onFormLoginErrorUnknown(error);
    }
  };
  onFacebookLoginError = function(error) {
    onLoginError();
    if (error.status === 'user_cancelled') {
      return;
    }
    debugService.error('Unknown facebook login error', TAG, {
      error: error
    });
    return alertService.alert({
      headerColor: 'alert-red',
      icon: 'fail',
      titleString: locale.getString('authentication.errorFacebookHeaderPrimary'),
      bodyString: locale.getString('authentication.errorFacebookHeaderSecondary'),
      link: faqLinkConfig.cantSignIn
    });
  };
  $scope.handleForgotPasswordClick = function() {
    analyticsService.track('Forgot Password Clicked');
    return $window.open(csConfig.forgotPasswordUrl, '_system');
  };
  $scope.toggleIsPasswordShowing = function() {
    return $scope.isPasswordShowing = !$scope.isPasswordShowing;
  };
  $scope.handleClickFacebook = onFacebookClick;
  $scope.handleFormChange = function() {
    if ($scope.form.$valid) {
      return onFormValid();
    } else {
      return onFormInvalid();
    }
  };
  onBeforeEnter = function() {
    statusBarService.setStyle(statusBarStyles.hidden);
    setIsLoading(false);
    setButton('disabled');
    return $scope.user = {
      email: '',
      password: ''
    };
  };
  $scope.$on('$ionicView.beforeEnter', onBeforeEnter);
  return this;
}]);

this.app.controller('signInConfirmationController', ["$timeout", "$state", "$ionicHistory", "appConfig", "userService", "$scope", "statusBarService", "statusBarStyles", "pushRegistrationService", function($timeout, $state, $ionicHistory, appConfig, userService, $scope, statusBarService, statusBarStyles, pushRegistrationService) {
  var onBeforeEnter;
  onBeforeEnter = function() {
    var avatar_url, name, ref;
    statusBarService.setStyle(statusBarStyles.hidden);
    $timeout(function() {
      pushRegistrationService.register();
      return $state.go(appConfig.defaultView);
    }, 3000);
    $ionicHistory.nextViewOptions({
      disableBack: true,
      historyRoot: true
    });
    ref = userService.get(), avatar_url = ref.avatar_url, name = ref.name;
    return $scope.signInConfirmation = {
      imageUrl: avatar_url,
      name: name
    };
  };
  $scope.$on('$ionicView.beforeEnter', onBeforeEnter);
  return this;
}]);

this.app.controller('supportController', ["$scope", "$window", "statusBarService", "statusBarStyles", "networkStateService", "alertService", "locale", "zendeskRedirectService", "debugService", function($scope, $window, statusBarService, statusBarStyles, networkStateService, alertService, locale, zendeskRedirectService, debugService) {
  var TAG, noInternetAlert, onBeforeEnter, openSupportUrl;
  TAG = 'SupportController';
  noInternetAlert = {
    headerColor: 'alert-yellow',
    icon: 'fail',
    titleString: locale.getString('popup.noInternetTitle'),
    bodyString: locale.getString('popup.noInternetDescription')
  };
  openSupportUrl = function(url) {
    if (networkStateService.noInternet()) {
      return alertService.alert(noInternetAlert);
    }
    return zendeskRedirectService.getRedirectUrl(url).then(function(redirectUrl) {
      $window.open(redirectUrl, '_blank', 'clearcache=yes');
      return true;
    });
  };
  $scope.onKnownIssuesClick = function() {
    debugService.log('User clicked known issues link', TAG);
    $window.open('https://www.chefsteps.com/known-issues', '_blank');
    return true;
  };
  $scope.onJouleAppTroubleClick = function() {
    debugService.log('User clicked app troubleshooting link', TAG);
    return openSupportUrl('https://support.chefsteps.com/hc/en-us/categories/203258268');
  };
  $scope.onSubmitSupportRequestClick = function() {
    debugService.log('User clicked hardware troubleshooting link', TAG);
    return openSupportUrl('https://support.chefsteps.com/hc/en-us/requests/new');
  };
  $scope.onMySupportRequestsClick = function() {
    debugService.log('User clicked hardware troubleshooting link', TAG);
    return openSupportUrl('https://support.chefsteps.com/hc/en-us/requests');
  };
  onBeforeEnter = function() {
    return statusBarService.setStyle(statusBarStyles.light);
  };
  return $scope.$on('$ionicView.beforeEnter', onBeforeEnter);
}]);

this.app.controller('temperatureEntryController', ["$scope", "$state", "$ionicLoading", "$window", "alertService", "$timeout", "$rootScope", "vibrateService", "circulatorManager", "analyticsService", "debugService", "programTypes", "loggingTags", "temperatureUnitService", "statusBarService", "statusBarStyles", "utilities", "locale", "floatingActionButtonStates", "circulatorConnectionError", "cacheService", "circulatorConnectionStates", "appConfig", "faqLinkConfig", function($scope, $state, $ionicLoading, $window, alertService, $timeout, $rootScope, vibrateService, circulatorManager, analyticsService, debugService, programTypes, loggingTags, temperatureUnitService, statusBarService, statusBarStyles, utilities, locale, floatingActionButtonStates, circulatorConnectionError, cacheService, circulatorConnectionStates, appConfig, faqLinkConfig) {
  var TAG, confirmCelsiusAlert, confirmFahrenheitAlert, isTempAmbiguous, maximumDisplayTemperatures, minimumDisplayTemperatures, onBathTemperatureUpdated, onBeforeEnter, onConnectionUpdated, onInvalidKeypadEntry, onLeave, onProgramStartFailure, onProgramStartSuccess, onValidKeypadEntry, openAboutMaxTempUrl, shake, shakeAnimationDuration, shakeUnthrottled, showConvertedTooHighSnackbar, showConvertedTooLowSnackbar, showTooHighSnackbarCelsius, showTooHighSnackbarFahrenheit, showTooLowSnackbarCelsius, showTooLowSnackbarFahrenheit, unbindBathTemperatureUpdateHandler, unbindConnectionUpdateHandler, updateButton;
  TAG = 'TemperatureEntryView';
  $scope.buttonState = floatingActionButtonStates.powerInactive;
  $scope.buttonAction = _.noop;
  $scope.isReadyToStart = false;
  showTooHighSnackbarFahrenheit = function() {
    var textParams;
    textParams = {
      maximumFahrenheit: Math.floor(utilities.convertCtoF(appConfig.maximumTemperature))
    };
    return $rootScope.$emit('snackbar:show', {
      text: locale.getString('general.tempRangeMsgFahrenheit', textParams),
      showLink: true,
      linkText: locale.getString('general.aboutMaxTempLinkText'),
      linkAction: openAboutMaxTempUrl
    });
  };
  showTooHighSnackbarCelsius = function() {
    var textParams;
    textParams = {
      maximumCelsius: appConfig.maximumTemperature
    };
    return $rootScope.$emit('snackbar:show', {
      text: locale.getString('general.tempRangeMsgCelsius', textParams),
      showLink: true,
      linkText: locale.getString('general.aboutMaxTempLinkText'),
      linkAction: openAboutMaxTempUrl
    });
  };
  openAboutMaxTempUrl = function() {
    debugService.log('User selected about max temperature', TAG);
    return faqLinkConfig.maximumTemperature.preprocessor(faqLinkConfig.maximumTemperature.uri).then(function(redirectUrl) {
      $window.open(redirectUrl, '_blank', 'clearcache=yes');
      return true;
    })["catch"](function(err) {
      debugService.error('error while preprocessing link', TAG, {
        error: err
      });
      $window.open(faqLinkConfig.maximumTemperature.uri, '_blank', 'clearcache=yes');
      return true;
    }).done(_.noop, function(e) {
      return debugService.onPromiseUnhandledRejection(e, TAG);
    });
  };
  showConvertedTooHighSnackbar = function() {
    var textParams;
    textParams = {
      convertedFahrenheit: Math.floor(utilities.convertCtoF($scope.keypadValue))
    };
    return $rootScope.$emit('snackbar:show', {
      text: locale.getString('general.convertedTooHigh', textParams)
    });
  };
  showConvertedTooLowSnackbar = function() {
    var textParams;
    textParams = {
      convertedCelsius: Math.floor(utilities.convertFtoC($scope.keypadValue))
    };
    return $rootScope.$emit('snackbar:show', {
      text: locale.getString('general.convertedTooLow', textParams)
    });
  };
  showTooLowSnackbarFahrenheit = function() {
    var textParams;
    textParams = {
      minimumFahrenheit: Math.floor(utilities.convertCtoF(appConfig.minimumTemperature))
    };
    return $rootScope.$emit('snackbar:show', {
      text: locale.getString('general.rangeTooLowFahrenheit', textParams)
    });
  };
  showTooLowSnackbarCelsius = function() {
    var textParams;
    textParams = {
      minimumCelsius: appConfig.minimumTemperature
    };
    return $rootScope.$emit('snackbar:show', {
      text: locale.getString('general.rangeTooLowCelsius', textParams)
    });
  };
  shakeAnimationDuration = 820;
  shakeUnthrottled = function() {
    $scope.shake = true;
    return $timeout((function() {
      return $scope.shake = false;
    }), shakeAnimationDuration);
  };
  shake = _.throttle(shakeUnthrottled, shakeAnimationDuration + 100, true);
  maximumDisplayTemperatures = {
    c: appConfig.maximumTemperature,
    f: Math.floor(utilities.convertCtoF(appConfig.maximumTemperature))
  };
  minimumDisplayTemperatures = {
    c: appConfig.minimumTemperature,
    f: appConfig.maximumTemperature
  };
  $scope.onKeypadValueUpdated = function(value) {
    var numericKeypadValue;
    if (utilities.isMoreThanOneDecimal(value)) {
      $scope.keypadValue = null;
      shake();
      vibrateService.vibrate(100);
      updateButton();
      return;
    }
    numericKeypadValue = utilities.getIntDisplay(value) + utilities.getDecimalDisplay(value);
    if ($scope.unit === 'f' && numericKeypadValue > maximumDisplayTemperatures.f) {
      $scope.keypadValue = null;
      shake();
      vibrateService.vibrate(100);
      showTooHighSnackbarFahrenheit();
      updateButton();
      return;
    }
    if ($scope.unit === 'c' && numericKeypadValue > maximumDisplayTemperatures.f) {
      $scope.keypadValue = null;
      shake();
      vibrateService.vibrate(100);
      showTooHighSnackbarCelsius();
      updateButton();
      return;
    }
    return $timeout(updateButton);
  };
  $scope.isKeypadValueValid = function() {
    return $scope.keypadValue !== null && $scope.keypadValue >= minimumDisplayTemperatures.c && $scope.keypadValue <= maximumDisplayTemperatures.f;
  };
  isTempAmbiguous = function() {
    if ($scope.unit === 'f' && $scope.keypadValue < minimumDisplayTemperatures.f) {
      return true;
    }
    if ($scope.unit === 'c' && $scope.keypadValue > maximumDisplayTemperatures.c) {
      return true;
    }
    return false;
  };
  $scope.updateUnit = function() {
    var unit, updatedValue;
    unit = 'c';
    if ($scope.unit === 'c') {
      unit = 'f';
    }
    if ($scope.keypadValue === null) {
      $scope.unit = unit;
      onBathTemperatureUpdated(circulatorManager.getBathTemperatureState());
      return;
    }
    if ($scope.unit === 'c' && $scope.keypadValue > maximumDisplayTemperatures.c) {
      showConvertedTooHighSnackbar();
      $scope.keypadValue = null;
      shake();
      vibrateService.vibrate(100);
      updateButton();
      return;
    }
    if ($scope.unit === 'f' && $scope.keypadValue < Math.floor(utilities.convertCtoF(minimumDisplayTemperatures.c))) {
      showConvertedTooLowSnackbar();
      $scope.keypadValue = null;
      shake();
      vibrateService.vibrate(100);
      updateButton();
      return;
    }
    updatedValue = null;
    if (unit === 'c') {
      updatedValue = utilities.convertFtoC($scope.keypadValue);
    } else {
      updatedValue = utilities.convertCtoF($scope.keypadValue);
    }
    $scope.unit = unit;
    $scope.keypadValue = utilities.getIntDisplay(updatedValue) + utilities.getDecimalDisplay(updatedValue);
    return onBathTemperatureUpdated(circulatorManager.getBathTemperatureState());
  };
  onProgramStartSuccess = function() {
    $ionicLoading.hide();
    analyticsService.track('Started a Manual Program');
    debugService.log('startProgram has succeeded', [TAG, loggingTags.cook]);
    temperatureUnitService.set($scope.unit);
    return $state.go('cook', {
      shouldShowTempHint: 'true'
    });
  };
  onProgramStartFailure = function(error) {
    $ionicLoading.hide();
    return circulatorConnectionError.handleErrorWithPopup(error)["catch"](function(error) {
      debugService.error('unhandled error while starting program', [TAG, loggingTags.cook], {
        error: error
      });
      return alertService.confirm({
        headerColor: 'alert-yellow',
        icon: 'fail',
        titleString: locale.getString('popup.startProgramFailureTitle'),
        bodyString: locale.getString('popup.startProgramFailureDescription'),
        okText: locale.getString('general.tryAgain')
      }).then(function(confirmation) {
        if (!confirmation) {
          return;
        }
        debugService.log('Retrying start program after failed attempt', TAG);
        return onValidKeypadEntry();
      });
    });
  };
  $scope.getDisplayInteger = function(value) {
    return utilities.getIntDisplay(value);
  };
  $scope.getDisplayDecimal = function(value) {
    return utilities.getDecimalDisplay(value);
  };
  onInvalidKeypadEntry = function() {
    shake();
    vibrateService.vibrate(100);
    if ($scope.keypadValue === null) {
      return false;
    } else if ($scope.unit === 'c' && $scope.keypadValue < minimumDisplayTemperatures.c && $scope.keypadValue > maximumDisplayTemperatures.f) {
      return showTooHighSnackbarCelsius();
    } else if ($scope.unit === 'f' && $scope.keypadValue > maximumDisplayTemperatures.f) {
      return showTooHighSnackbarFahrenheit();
    } else if ($scope.unit === 'f' && $scope.keypadValue < minimumDisplayTemperatures.f) {
      return showTooLowSnackbarFahrenheit();
    } else if ($scope.unit === 'c' && $scope.keypadValue < minimumDisplayTemperatures.c) {
      return showTooLowSnackbarCelsius();
    }
  };
  confirmFahrenheitAlert = function() {
    return alertService.confirm({
      headerColor: 'alert-green',
      icon: 'question',
      titleString: locale.getString('popup.confirmFahrenheitTitle'),
      bodyString: locale.getString('popup.confirmUnitDescription'),
      okText: locale.getString('popup.fahrenheit'),
      cancelText: locale.getString('popup.celsius')
    });
  };
  confirmCelsiusAlert = function() {
    return alertService.confirm({
      headerColor: 'alert-green',
      icon: 'question',
      titleString: locale.getString('popup.confirmCelsiusTitle'),
      bodyString: locale.getString('popup.confirmUnitDescription'),
      okText: locale.getString('popup.fahrenheit'),
      cancelText: locale.getString('popup.celsius')
    });
  };
  onValidKeypadEntry = function() {
    var confirmUnitSwitch, startProgram, switchUnit;
    startProgram = function() {
      var programOptions, temperature;
      $ionicLoading.show({
        template: "<div class='loading-indicator' />",
        noBackdrop: true
      });
      if ($scope.unit === 'f') {
        temperature = utilities.convertFtoC($scope.keypadValue);
      } else {
        temperature = parseFloat($scope.keypadValue);
      }
      programOptions = {
        setPoint: temperature,
        programType: programTypes.manual,
        programMetadata: {
          cookId: utilities.generateCookId()
        }
      };
      if ((circulatorManager.getProgramState() != null) && circulatorManager.getTimeRemainingState() > 0) {
        programOptions.cookTime = circulatorManager.getTimeRemainingState();
      }
      return circulatorManager.startProgram(programOptions).then(onProgramStartSuccess)["catch"](onProgramStartFailure).done(_.noop, function(e) {
        return debugService.onPromiseUnhandledRejection(e, TAG);
      });
    };
    confirmUnitSwitch = function() {
      if ($scope.unit === 'c') {
        return confirmCelsiusAlert();
      } else {
        return confirmFahrenheitAlert().then(function(clickedF) {
          if (clickedF) {
            return false;
          } else {
            return true;
          }
        });
      }
    };
    switchUnit = function() {
      return $scope.unit = {
        f: 'c',
        c: 'f'
      }[$scope.unit];
    };
    if (isTempAmbiguous()) {
      return confirmUnitSwitch().then(function(isUnitSwitchDesired) {
        if (isUnitSwitchDesired) {
          switchUnit();
          $timeout();
          return startProgram();
        } else {
          if ($scope.unit === 'c') {
            $scope.keypadValue = null;
            shake();
            vibrateService.vibrate(100);
            return showTooHighSnackbarCelsius();
          } else if ($scope.unit === 'f' && $scope.keypadValue < Math.floor(utilities.convertCtoF(minimumDisplayTemperatures.c))) {
            showTooLowSnackbarFahrenheit();
            $scope.keypadValue = null;
            shake();
            updateButton();
            return vibrateService.vibrate(100);
          } else {
            return startProgram();
          }
        }
      });
    } else {
      return startProgram();
    }
  };
  updateButton = function() {
    var connectionState;
    connectionState = circulatorManager.getCirculatorConnectionState();
    if (connectionState === circulatorConnectionStates.connected) {
      if ($scope.isKeypadValueValid()) {
        $scope.buttonState = floatingActionButtonStates.powerActivate;
        $scope.buttonAction = onValidKeypadEntry;
        $scope.isReadyToStart = true;
      } else {
        $scope.buttonState = floatingActionButtonStates.powerInactive;
        $scope.buttonAction = onInvalidKeypadEntry;
        $scope.isReadyToStart = false;
      }
    } else if (connectionState === circulatorConnectionStates.connecting) {
      $scope.buttonState = floatingActionButtonStates.connectingFaded;
      $scope.buttonAction = circulatorConnectionError.handleConnectingWithPopup;
      $scope.isReadyToStart = false;
    } else {
      $scope.buttonState = floatingActionButtonStates.disconnectedFadedTransparent;
      $scope.buttonAction = function() {
        return $state.go('connectionTroubleshooting');
      };
      $scope.isReadyToStart = false;
    }
    return $timeout();
  };
  onBathTemperatureUpdated = function(bathTemp) {
    var bathTempStringParams, unitString;
    if ($scope.unit === 'f') {
      $scope.bathTemperature = utilities.getIntDisplay(utilities.convertCtoF(bathTemp));
      unitString = locale.getString('general.degreeF');
    } else {
      $scope.bathTemperature = utilities.getIntDisplay(bathTemp);
      unitString = locale.getString('general.degreeC');
    }
    bathTempStringParams = {
      bathTemperature: $scope.bathTemperature,
      temperatureUnit: unitString
    };
    $scope.bathTemperatureWithUnit = locale.getString('general.currentBathTemperatureWithUnit', bathTempStringParams);
    return $timeout();
  };
  onConnectionUpdated = function() {
    return updateButton();
  };
  unbindBathTemperatureUpdateHandler = _.noop;
  unbindConnectionUpdateHandler = _.noop;
  onBeforeEnter = function() {
    unbindBathTemperatureUpdateHandler = circulatorManager.bindBathTemperatureUpdateHandler(onBathTemperatureUpdated);
    unbindConnectionUpdateHandler = circulatorManager.bindCirculatorConnectionUpdateHandler(onConnectionUpdated);
    $scope.unit = temperatureUnitService.get();
    statusBarService.setStyle(statusBarStyles.hidden);
    $scope.keypadValue = null;
    $scope.isTraining = cacheService.get('isTraining', 'training');
    return onBathTemperatureUpdated(circulatorManager.getBathTemperatureState());
  };
  onLeave = function() {
    unbindBathTemperatureUpdateHandler();
    return unbindConnectionUpdateHandler();
  };
  $scope.$on('$ionicView.beforeEnter', onBeforeEnter);
  return $scope.$on('$ionicView.leave', onLeave);
}]);

this.app.controller('timerController', ["$interval", "$scope", "$state", "$timeout", "$ionicLoading", "locale", "timerService", "analyticsService", "debugService", "loggingTags", "statusBarService", "statusBarStyles", "utilities", "floatingActionButtonStates", "circulatorManager", "alertService", "programTypes", "circulatorConnectionStates", "circulatorConnectionError", function($interval, $scope, $state, $timeout, $ionicLoading, locale, timerService, analyticsService, debugService, loggingTags, statusBarService, statusBarStyles, utilities, floatingActionButtonStates, circulatorManager, alertService, programTypes, circulatorConnectionStates, circulatorConnectionError) {
  var TAG, onBeforeEnter, onLeave, onTimeRemainingUpdated, onTimerUpdated, onUpdateButtonStates, setButtonState, shakeAnimationDuration, shakeFunc, showKeypad, showTimer, unbindConnectionUpdateHandler, unbindTimeRemainingUpdateHandler, unbindTimerUpdateHandlers, updateResetButton, updateTimerForExistingProgram;
  TAG = 'TimerView';
  unbindConnectionUpdateHandler = _.noop;
  shakeAnimationDuration = 820;
  shakeFunc = function() {
    $scope.shake = true;
    return $timeout((function() {
      return $scope.shake = false;
    }), shakeAnimationDuration);
  };
  $scope.shakeMe = _.throttle(shakeFunc, shakeAnimationDuration + 100, true);
  showTimer = function() {
    $scope.shouldShowKeypad = false;
    $scope.shouldShowTimer = true;
    $scope.timer = timerService.getTimerState();
    return $timeout();
  };
  showKeypad = function() {
    $scope.shouldShowTimer = false;
    $scope.shouldShowKeypad = true;
    $scope.timer = null;
    return setButtonState();
  };
  $scope.clearKeypad = function() {
    return $scope.setKeypadValue(null);
  };
  updateResetButton = function() {
    var connectionState;
    connectionState = circulatorManager.getCirculatorConnectionState();
    if (connectionState === circulatorConnectionStates.connecting) {
      return $scope.resetButtonDisabled = true;
    } else if (connectionState === circulatorConnectionStates.disconnected) {
      return $scope.resetButtonDisabled = true;
    } else {
      return $scope.resetButtonDisabled = false;
    }
  };
  $scope.resetTimer = function(connectionState) {
    connectionState = circulatorManager.getCirculatorConnectionState();
    if (connectionState === circulatorConnectionStates.connecting) {
      circulatorConnectionError.handleConnectingWithPopup();
    }
    if (connectionState === circulatorConnectionStates.connected) {
      return updateTimerForExistingProgram(null).then(function() {
        $scope.setKeypadValue(null);
        return showKeypad();
      }).done(_.noop, function(e) {
        return debugService.onPromiseUnhandledRejection(e, TAG);
      });
    } else if (connectionState === circulatorConnectionStates.disconnected) {
      return $state.go('connectionTroubleshooting');
    }
  };
  $scope.setKeypadValue = function(value) {
    if (value == null) {
      value = '';
    }
    $scope.keypadValue = value;
    value = '0000'.concat(value.toString());
    $scope.minutes = value.slice(value.length - 2, value.length - 0);
    $scope.hours = value.slice(value.length - 4, value.length - 2);
    if (value.toString().length > 8) {
      $scope.minutes = $scope.hours = $scope.keypadValue = null;
    }
    return setButtonState();
  };
  updateTimerForExistingProgram = function(timeInMinutes) {
    var onProgramStartFailure, onProgramStartSuccess, startProgram;
    startProgram = function() {
      var currentProgram, programOptions, ref;
      currentProgram = circulatorManager.getProgramState();
      debugService.log('Updating timer for existing manual program', [TAG, loggingTags.cook]);
      $ionicLoading.show({
        template: "<div class='loading-indicator' />",
        noBackdrop: true
      });
      programOptions = {
        setPoint: currentProgram.setPoint,
        programType: programTypes.manual,
        programMetadata: {
          cookId: (ref = currentProgram.programMetadata) != null ? ref.cookId : void 0
        }
      };
      if (timeInMinutes > 0) {
        programOptions.cookTime = utilities.convertMinutesToSeconds(timeInMinutes);
      }
      return circulatorManager.startProgram(programOptions).then(onProgramStartSuccess)["catch"](onProgramStartFailure);
    };
    onProgramStartSuccess = function() {
      $ionicLoading.hide();
      analyticsService.track('Started a Manual Program');
      return debugService.log('startProgram has succeeded', [TAG, loggingTags.cook]);
    };
    onProgramStartFailure = function(error) {
      $ionicLoading.hide();
      return circulatorConnectionError.handleErrorWithPopup(error)["catch"](function(error) {
        debugService.error('unhandled error while starting program', [TAG, loggingTags.cook], {
          error: error
        });
        return alertService.confirm({
          headerColor: 'alert-yellow',
          icon: 'fail',
          titleString: locale.getString('popup.startProgramFailureTitle'),
          bodyString: locale.getString('popup.startProgramFailureDescription'),
          okText: locale.getString('general.tryAgain')
        }).then(function(confirmation) {
          if (!confirmation) {
            return;
          }
          debugService.log('Retrying start program after failed attempt', TAG);
          return startProgram();
        });
      });
    };
    return startProgram();
  };
  setButtonState = function() {
    var connectionState;
    connectionState = circulatorManager.getCirculatorConnectionState();
    if (connectionState === circulatorConnectionStates.connected) {
      if ($scope.keypadValue) {
        $scope.buttonState = floatingActionButtonStates.startActivate;
        $scope.buttonAction = function() {
          var hours, minutes, timeInMinutes;
          minutes = parseInt($scope.minutes || 0);
          hours = utilities.convertHoursToMinutes(parseInt($scope.hours || 0));
          timeInMinutes = minutes + hours;
          return updateTimerForExistingProgram(timeInMinutes).then(function() {
            return $state.go('cook');
          }).done(_.noop, function(e) {
            return debugService.onPromiseUnhandledRejection(e, TAG);
          });
        };
      } else {
        $scope.buttonState = floatingActionButtonStates.startInactive;
        $scope.buttonAction = _.noop;
      }
    }
    if (connectionState === circulatorConnectionStates.connecting) {
      $scope.buttonState = floatingActionButtonStates.startInactive;
      $scope.buttonAction = circulatorConnectionError.handleConnectingWithPopup;
    } else if (connectionState === circulatorConnectionStates.disconnected) {
      $scope.buttonState = floatingActionButtonStates.startInactive;
      $scope.buttonAction = function() {
        return $state.go('connectionTroubleshooting');
      };
    }
    return $timeout();
  };
  onTimerUpdated = function(timer) {
    debugService.log('onTimerUpdated', TAG, {
      timer: timer
    });
    if ((timer != null) && timer.getRemainingTime() > 0) {
      return showTimer();
    } else {
      return showKeypad();
    }
  };
  onTimeRemainingUpdated = function(timeRemainingInSeconds) {
    if ($scope.shouldShowTimer && timeRemainingInSeconds === 0) {
      return showKeypad();
    }
  };
  unbindTimerUpdateHandlers = _.noop;
  unbindTimeRemainingUpdateHandler = _.noop;
  unbindConnectionUpdateHandler = _.noop;
  onUpdateButtonStates = function() {
    setButtonState();
    return updateResetButton();
  };
  onBeforeEnter = function() {
    statusBarService.setStyle(statusBarStyles.light);
    $scope.minutes = '';
    $scope.hours = '';
    $scope.keypadValue = '';
    $scope.shouldShowTimer = false;
    $scope.shouldShowKeypad = false;
    unbindTimerUpdateHandlers = timerService.bindTimerUpdateHandlers(onTimerUpdated);
    unbindTimeRemainingUpdateHandler = circulatorManager.bindTimeRemainingUpdateHandler(onTimeRemainingUpdated);
    return unbindConnectionUpdateHandler = circulatorManager.bindCirculatorConnectionUpdateHandler(onUpdateButtonStates);
  };
  onLeave = function() {
    unbindTimerUpdateHandlers();
    unbindTimeRemainingUpdateHandler();
    return unbindConnectionUpdateHandler();
  };
  $scope.$on('$ionicView.beforeEnter', onBeforeEnter);
  return $scope.$on('$ionicView.leave', onLeave);
}]);

this.app.controller('trainingController', ["$scope", "collectionService", "statusBarService", "statusBarStyles", "assetService", function($scope, collectionService, statusBarService, statusBarStyles, assetService) {
  var onBeforeEnter, onLoaded;
  $scope.getCollectionThumbnail = function(collection) {
    if (collection != null ? collection.thumbnail : void 0) {
      return assetService.pathFor(collection.thumbnail);
    } else {
      return '';
    }
  };
  onLoaded = function() {
    return collectionService.getBySlug('training').then(function(collection) {
      return $scope.collection = collection;
    });
  };
  onBeforeEnter = function() {
    return statusBarService.setStyle(statusBarStyles.light);
  };
  $scope.$on('$ionicView.loaded', onLoaded);
  return $scope.$on('$ionicView.beforeEnter', onBeforeEnter);
}]);

this.app.controller('welcomeController', ["$scope", "statusBarService", "statusBarStyles", "analyticsService", "$ionicHistory", "$state", "circulatorManager", function($scope, statusBarService, statusBarStyles, analyticsService, $ionicHistory, $state, circulatorManager) {
  var onBeforeEnter;
  onBeforeEnter = function() {
    return statusBarService.setStyle(statusBarStyles.hidden);
  };
  $scope.skipSignIn = function() {
    analyticsService.track('User Skipped Signed In');
    $ionicHistory.nextViewOptions({
      disableBack: true
    });
    circulatorManager.onUserSkipSignIn();
    return $state.go('home');
  };
  return $scope.$on('$ionicView.beforeEnter', onBeforeEnter);
}]);
