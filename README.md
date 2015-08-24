# zbtprinter
A Cordova/Phonegap driver for Zebra bluetooth printers

This is a fork of https://github.com/mmilidoni/zbtprinter with discovery

##Usage
You can find Zebra printer using:

```
cordova.plugins.zbtprinter.find(function(mac) { 
        alert(mac); 
    }, function(fail) { 
        alert(fail); 
    }
);
```

You can send data in ZPL Zebra Programing Language:

```
cordova.plugins.zbtprinter.print("AC:3F:A4:1D:7A:5C", "! U1 setvar "device.languages" "line_print"\r\nTEXT ***Print test***\r\nPRINT\r\n",
    function(success) { 
        alert("Print ok"); 
    }, function(fail) { 
        alert(fail); 
    }
);
```

##Install
###Cordova

```
cordova plugin add https://github.com/michael79bxl/zbtprinter.git
```

###Phonegap build

```
<gap:plugin name="cordova-plugin-zbtprinter" source="npm" />
```


##ZPL - Zebra Programming Language
For more information about ZPL please see the  [PDF Official Manual](https://support.zebra.com/cpws/docs/zpl/zpl_manual.pdf)
