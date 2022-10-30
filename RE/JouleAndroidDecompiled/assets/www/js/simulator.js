this.simulator = angular.module('connectionSimulator', []);


/*
 *
 * @class connectionSimulatorService
 *
 * @description Provides high-level abstraction methods for activating and deactivating the connection simulator.
 *
 */

this.simulator.service('connectionSimulatorService', ["$window", "simulatorEvothingsService", function($window, simulatorEvothingsService) {

  /*
   *
   * @member cachedEvothingsAPI
   * @memberof connectionSimulatorService
   *
   * @description We cache a copy of Evothings in the case that we need to revert (disable the simulator).
   *
   */
  this.cachedEvothingsAPI = $window.evothings;
  this.debugService = null;

  /*
   *
   * @method enable
   * @memberof connectionSimulatorService
   *
   * @description Enables the connection simulator, substituting the simulatorEvothingsService for $window.Evothings, and soon the WebSocket provider as well.
   *
   */
  this.enable = function(debugService, connectionProvidersConfig) {
    this.debugService = debugService;
    if (!this.cachedEvothingsAPI) {
      if (this.debugService) {
        this.debugService.debug('SimulatorService.Enable: Evothings window variable not present. Adding.');
      }
      $window.evothings = {};
    }
    simulatorEvothingsService.setDebugService(debugService);
    simulatorEvothingsService.setConnectionProvidersConfig(connectionProvidersConfig);
    return $window.evothings.ble = simulatorEvothingsService;
  };

  /*
   *
   * @method disable
   * @memberof connectionSimulatorService
   *
   * @description Disables the connection simulator, replacing mock APIs with cached window values (such as cachedEvothingsAPI).
   *
   */
  this.disable = function() {
    var messageCallbacks;
    if (this.cachedEvothingsAPI) {
      $window.evothings = this.cachedEvothingsAPI;
    } else {
      if (this.debugService) {
        this.debugService.debug('SimulatorService.Disable: No cached Evothings value.');
      }
    }
    return messageCallbacks = {};
  };
  return this;
}]);


/*
 *
 * @class simulatorEvothingsService
 *
 * @description Service responsible for providing a 1:1 mapping of Evothings methods, and calling message listeners if set.
 *
 */

this.simulator.service('simulatorEvothingsService', ["$window", "$timeout", function($window, $timeout) {
  var ByteBuffer, CSSimulatorCirculator, CirculatorSDK, StreamMessageHandler, TAG, connectionProvidersConfig, debugService, deviceHandle, hexToByteAddress, messageQueue, simulatorCirculator, simulators, startScanInterval, streamHandler;
  debugService = null;
  connectionProvidersConfig = null;
  CirculatorSDK = $window.CirculatorSDK;
  ByteBuffer = $window.ByteBuffer;
  CSSimulatorCirculator = $window.CSSimulatorCirculator;
  StreamMessageHandler = $window.CirculatorSDK.StreamMessageHandler;
  hexToByteAddress = $window.CirculatorSDK.hexToByteAddress;
  simulators = [
    {
      address: '3D22022F-2729-6BA1-19A1-E8FC4E25BF49',
      advertisementData: {
        kCBAdvDataManufacturerData: 'WQEQoKOo5a3cAQI=',
        kCBAdvDataServiceUUIDs: ['700B4321-9836-4383-A2B2-31A9098D1473'],
        kCBAdvDataLocalName: 'DUMMY_01'
      },
      name: 'DUMMY_01',
      rssi: -80
    }, {
      address: '68753A44-4D6F-1226-9C60-0050E4C00067',
      advertisementData: {
        kCBAdvDataManufacturerData: 'WQEQoKGm3HXcAQI=',
        kCBAdvDataServiceUUIDs: ['700B4321-9836-4383-A2B2-31A9098D1473'],
        kCBAdvDataLocalName: 'DUMMY_02'
      },
      name: 'DUMMY_02',
      rssi: -70
    }, {
      address: '62BC88FE-37E2-0B0C-DF66-CAA52E4F87B8',
      advertisementData: {
        kCBAdvDataManufacturerData: 'WQEQoKNdt+HcAQI=',
        kCBAdvDataServiceUUIDs: ['700B4321-9836-4383-A2B2-31A9098D1473'],
        kCBAdvDataLocalName: 'DUMMY_03'
      },
      name: 'DUMMY_03',
      rssi: -60
    }
  ];
  deviceHandle = 1;
  streamHandler = null;
  simulatorCirculator = null;
  startScanInterval = null;
  messageQueue = [];
  TAG = 'SimulatorEvothings';
  this.setDebugService = function(value) {
    return debugService = value;
  };
  this.setConnectionProvidersConfig = function(value) {
    return connectionProvidersConfig = value;
  };
  this.powerStatus = function(win, fail) {
    return win({
      state: 0
    });
  };
  this.startScan = function(win, fail) {
    debugService.log('startScan', TAG);
    return startScanInterval = setInterval((function() {
      var detectedDeviceIndex;
      detectedDeviceIndex = Math.floor(Math.random() * 3);
      return win(simulators[detectedDeviceIndex]);
    }), 50);
  };
  this.stopScan = function() {
    debugService.log('stopScan', TAG);
    if (!!startScanInterval) {
      clearInterval(startScanInterval);
      return startScanInterval = null;
    }
  };
  this.connect = function(address, success, failure) {
    var matchingSimulator, matchingSimulatorByteAddress, simulatorsCopy;
    debugService.log('connect', TAG);
    simulatorsCopy = [];
    _.forEach(simulators, function(simulator) {
      var circulatorAddress, manufacturerData;
      manufacturerData = ByteBuffer.fromBase64(simulator.advertisementData.kCBAdvDataManufacturerData);
      circulatorAddress = manufacturerData.slice(3).toHex();
      simulator.circulatorAddress = circulatorAddress;
      return simulatorsCopy.push(simulator);
    });
    matchingSimulator = _.find(simulatorsCopy, {
      address: address
    });
    matchingSimulatorByteAddress = hexToByteAddress(matchingSimulator.circulatorAddress);
    this.close();
    simulatorCirculator = new CSSimulatorCirculator.SimulatorCirculator(debugService.getLogger());
    $window.simulatorCirculator = simulatorCirculator;
    streamHandler = new StreamMessageHandler({
      log: debugService.getLogger(),
      handlerType: 'simulatorEvothings',
      myAddress: matchingSimulatorByteAddress
    });
    simulatorCirculator.attachHandler(streamHandler);
    success({
      deviceHandle: deviceHandle,
      state: 1
    });
    return $timeout(function() {
      return success({
        deviceHandle: deviceHandle,
        state: 2
      });
    }, 1000);
  };
  this.close = function() {
    debugService.debug('close', TAG);
    if (!!streamHandler) {
      streamHandler.removeAllListeners('msgReady');
    }
    if (!!simulatorCirculator) {
      if (!!streamHandler) {
        simulatorCirculator.detachHandler(streamHandler);
      }
      return simulatorCirculator.cleanUp();
    }
  };
  this.services = function(deviceHandle, win, fail) {
    debugService.debug('services', TAG);
    return win([
      {
        handle: 4,
        uuid: connectionProvidersConfig.bluetooth.discovery.streamServiceUUID
      }
    ]);
  };
  this.characteristics = function(deviceHandle, serviceHandle, win, fail) {
    debugService.debug('characteristics', TAG);
    return win([
      {
        handle: 5,
        uuid: connectionProvidersConfig.bluetooth.communication.readCharacteristicUUID,
        properties: 2
      }, {
        handle: 6,
        uuid: connectionProvidersConfig.bluetooth.communication.subscribeCharacteristicUUID,
        properties: 16
      }, {
        handle: 7,
        uuid: connectionProvidersConfig.bluetooth.communication.writeCharacteristicUUID,
        properties: 8
      }, {
        handle: 8,
        uuid: connectionProvidersConfig.bluetooth.communication.fileCharacteristicUUID,
        properties: 4
      }
    ]);
  };
  this.readCharacteristic = function(deviceHandle, characteristicHandle, win, fail) {
    var message;
    debugService.debug('readCharacteristic', TAG);
    message = messageQueue.shift();
    return win(ByteBuffer.wrap(message.encode().toBuffer()));
  };
  this.writeCharacteristic = function(deviceHandle, characteristicHandle, data, win, fail, format) {
    var message;
    debugService.debug('writeCharacteristic', TAG);
    if (format === 'proto') {
      message = CirculatorSDK.messages.StreamMessage.decode(data);
      debugService.debug('Decoded message: ' + message, TAG);
      if (streamHandler === null) {
        throw new Error('StreamHandler not set but write char received');
      } else {
        streamHandler.handleMessage(message);
        return win();
      }
    } else if (format === 'raw') {
      return win();
    }
  };
  this.enableNotification = function(deviceHandle, characteristicHandle, win, fail) {
    debugService.debug('enableNotification', TAG);
    return streamHandler.on('msgReady', function(message) {
      messageQueue.push(message);
      return win(message);
    });
  };
  this.disableNotification = function(deviceHandle, characteristicHandle, win, fail) {
    debugService.debug('disableNotification', TAG);
    if (!!streamHandler) {
      streamHandler.removeAllListeners('msgReady');
    }
    return win();
  };
  this.rssi = function(deviceHandle, win, fail) {
    return win(-50);
  };
  return this;
}]);
