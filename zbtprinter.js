/*
 * cordova.plugins.zbtprinter.print = function(str, successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, 'ZebraBluetoothPrinter', 'print', [str]);
};
*/

//var ZebraBluetoothPrinterLoader = function (require, exports, module) {

	var exec = require("cordova/exec");
 
 	function ZebraBluetoothPrinter() {
    }

    ZebraBluetoothPrinter.prototype.print = function (mac, msg, successCallback, errorCallback) {
        exec(successCallback, errorCallback, 'ZebraBluetoothPrinter', 'print', [mac, msg]);
    };
    ZebraBluetoothPrinter.prototype.find = function (successCallback, errorCallback) {
        exec(successCallback, errorCallback, 'ZebraBluetoothPrinter', 'find', []);
    };
    var bluetoothPrinter = new ZebraBluetoothPrinter();
    module.exports = bluetoothPrinter;
//};

//ZebraBluetoothPrinterLoader(require, exports, module);
//cordova.define("cordova/plugins/zbtprinter", ZebraBluetoothPrinter);
