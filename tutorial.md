# BeeChatNetwork
Open source, peer-to-peer, radio communication chat based on the [ZigBee standard](https://zigbeealliance.org/).

**[Homepage]
(https://beechat.network/)**

[GitHub Page]
(https://github.com/beechatnetworkadmin/BeeChatNetwork/)

[YouTube Channel]
(https://www.youtube.com/channel/UCpkkP4lfeiD-UR6qxVQW3Dg)


## License
ZigBee uses the [MIT License](https://mit-license.org/), which should also be included in a text file within this folder.

---

## Table of Contents

1. Getting Started
    1. Installing Java
    
    2. Setting up the hardware
        
    3. Configuring XCTU
        1. Downloading XCTU
        2. Setting up XCTU
        
    2. Setting up BeeChat
    
    3. Running BeeChat
        * Command line arguments
        
2. Using BeeChat

3. In The Future

---

## Getting Started
This section shows how to set up BeeChat.

### Installing Java
BeeChat requires the **Java Runtime Environment Version 11** or later.

To ensure you have the right version of Java installed, use `java -version`.

* _If you are already using Java 11, you can skip the rest of this section._


To install Java 11 or later:

**Fedora**: 
    
>`sudo dnf install openjdk11`


Note: _other Java distributions may be used, but for the sake of this tutorial we will be using the OpenJDK._

Then, enter `sudo update-alternatives --config java` and follow the on-screen instructions to select OpenJDK 11.

To check that you have configured Java correctly, use `java -version` again.
    
### Setting up the hardware
In this tutorial we will be using parts from the [official parts list](https://beechat.network/parts-list/). Other ZigBee hardware may be used, but is not recommended and will not be explained in this tutorial. The process is more or less the same for both the [long range setup](https://beechat.network/long-range-setup/) and the [short range setup](https://beechat.network/short-range-setup/).


***NOTE:*** The Sparkfun Dongle and and XBee Module must be handled with care! Only touch the side edges of the boards, as touching the electronic components with bare hands can cause damage from electrostatic discharge (ESD).

1. Connect the black XBee module to the red Sparkfun dongle by carefully pressing the connectors on the XBee module down into the ports on the Sparkfun dongle. If you are using the long-range setup, make sure the metal antenna connector on the XBee module is pointed outwards (away from the USB port on the Sparkfun dongle).

* The following only applies if you are using the long-range setup:
    2. Screw on the small metal SMA adapter onto the XBee module's connector.
    3. Connect the antenna to the SMA adapter.

Then, simply plug it into a USB port on your computer and move on to the next step.
    
    
### Configuring XCTU
1. [Download the correct version of the XCTU configuration tool for your system.](https://www.digi.com/products/embedded-systems/digi-xbee/digi-xbee-tools/xctu#productsupport-utilities)
2. Open the newly installed XCTU program.
3. Click on the second button from the top left (with the magnifying glass) and click it.
4. A menu should have appeared. Check the box next to your radio module (it should be labeled something like `/dev/ttyUSB`). Then press `Next >`.
5. Then, configure the port parameters to the specifications below:
    > **Baud Rate**: `9600`
    
    > **Data Bits**: `8`
    
    > **Parity**: `None`
    
    > **Stop Bits**: `1`
    
    > **Flow Control**: `None`
    
Then click _finish_.

6. XCTU should now show your module on the left. Click it, and wait for the configuration settings to appear on the right side of the screen.
7. Set the PAN ID (Channel) to a number of your choosing. Also, set a device role for your module. If this is the first device in your area, select "Form network". If not, select "Join network".
8. On the search bar at the top, type "AP". Find the option "`AP API Enable`" to "`API Mode Without Escapes [1]`".
9. Press the _"Write"_ button (with the pencil icon) to save the changes to the XBee module.

You are now ready to move on to the next step.

### Setting Up BeeChat    
BeeChat comes as a precompiled .jar archive [which can be downloaded here](https://beechat.network/downloads-page/). Alternatively, [you can download the source code yourself](https://github.com/beechatnetworkadmin/BeeChatNetwork) and compile it manually; although this is not recommended and will not be covered in this guide. 

1. [Download the executable .jar file](https://beechat.network/downloads/BeeChat.jar)
2. Execute the .jar file

The .jar file must be executed within a terminal. To do this, open a terminal in the same directory as the .jar file and type `java -jar BeeChat.jar`. 

* Depending on your system, this may require super user privileges. 
* So if it doesn't work, try `sudo java -jar BeeChat.jar`


#### Command line arguments

No command line arguments are currently supported.


## Using BeeChat

Coming soon!
    
        
## In The Future
* Need a ubuntu/deb user to explain how to install java 11 on their OS please (APT), i forgot 
* Tips on compiling from source
* In "setting up the hardware" it is stated that "other zigbee hardware can be used". Can a programmer confirm this?
* Current tutorial only supports linux systems, need to extend for windows/mac
* Pictures are needed, they wont work right for me when making this so someone else needs to do em ig


Questions? Comments? Recommendations? 
[Email us!](beechatnetwork@gmail.com)

Written by MysteryCombatMan 
> Tox: BA21693673F2DFC79411BB4C7507789229058449A380721995B0B0743403984C659C54BB9993


