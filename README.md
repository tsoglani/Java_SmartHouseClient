JAVA Smart house client (For Testing)

This is a testing smart house java client for your computer device, you must have Java jdk. If you don't have it aleady you can download it from http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html.

You must download the Raspberry_SmartHouseServer project at your raspberry device "https://github.com/tsoglani/Raspberry_SmartHouseServer" and follow read the instructions.

Arduino -> "https://github.com/tsoglani/Arduino_smartHouseServer"

If you refactor-edit https://github.com/tsoglani/Raspberry_SmartHouseServer/blob/master/SmartHouseRaspberryServer/SH.java file, it will be able to play in any "Server" device, not only raspberry and arduino.

After you download it and run it on server device, to be able to test it, you MUST be on same local-WLAN network with your raspberry device.

How it works, from computer device, you are sending text file to the server by switching command buttons or by Speech (you have to say the correct command with "on" or "off" at the end of the command, for example "room light off", command ="room light", the commands are given on server side (arduino-ruspberry ... etc ) ). If you don't have response from server, that means you are not connected with the server device yet, and the application will be still waiting for a respond from the server to go to the next Frame or change the switch buttons.
