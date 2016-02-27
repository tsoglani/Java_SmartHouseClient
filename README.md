Arduino_SmartHouseServer (NOT TESTED)

this is the refactored code for arduino side from Raspberry smart house project (https://github.com/tsoglani/Raspberry_SmartHouseServer)


In this project, you can send commands from your computer, android, wearable-watch device to your arduino device (if you refactor the SH.java file this would be able to work in any device not only Arduino) you can connect as many Arduino devices as you want but all of them must be conected on same local network ( the device you will use to send the command must also be on same local network ).

incide the code you might have to midify:

-NumberOfBindingCommands : the number of commands you want to bind with one or more outputs. -DeviceID : in case you want to use more than one Arduino (or any) device in the same local network, each Arduino device must have a unique DeviceID. Example: if we have 4 Arduino devices connected in local network(WLAN), each one MUST have a unique ID : the first Ruspberry device DeviceID will be 0, the second device's DeviceID will be 1 the third will be 2 the fourth will be 3 ... (it is important if you want to open each output seperate)

    initializePowerCommans() function (most important): here you call addCommandsAndPorts function, addCommandsAndPorts function, have 3 parameters, -the first parameter is an id (in code is "i" and used in switch cases, so it change auto) you don't have to change it, except if your Relay have more that 8 outputs. -the seccond parameter is an array of string-text, the firts text in this array is sending and used for switching outputs (third parameter) on and off, but you have option to put more in case you want to activate or deactivate the outputs with speech commands (you can do it with speech command also, by saying the command and then "on" or "off" word).
    the third parameter is an array of integer, each integer represents one output (in Arduino, the width of outputs is from 0 to 20) you can put one or more integers to activate or deactivate one or more output with one (or more) command (parameter 2).


