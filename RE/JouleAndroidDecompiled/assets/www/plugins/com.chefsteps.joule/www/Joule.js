cordova.define("com.chefsteps.joule.CSTJoulePlugin", function(require, exports, module) { var Joule = {
  initializeWebView: function (success, failure) {
    cordova.exec(success, failure, 'CSTJoulePlugin', 'initializeWebView', []);
  },
  scheduleLocalNotification: function (options, success, failure) {
    cordova.exec(success, failure, 'CSTJoulePlugin', 'scheduleLocalNotification', [options]);
  },
  cancelLocalNotification: function (id, success, failure) {
    cordova.exec(success, failure, 'CSTJoulePlugin', 'cancelLocalNotification', [id]);
  }
};

module.exports = Joule;

});
