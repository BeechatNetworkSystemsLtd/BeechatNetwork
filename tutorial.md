# BeeChatNetwork setup tutorial

---

## Table of Contents

1. Getting Started
    1. Installing Java
    2. Setting up the hardware
    3. Configuring XCTU

2. Running BeeChat
    * Command line arguments

3. Using BeeChat

4. Building BeeChat from source

5. In The Future

---

## Getting Started

This section shows how to set up BeeChat.

### Installing Java

BeeChat requires **Java 11** or later.

To install Java 11 (default `OpenJDK` builds provided by your Linux sdistribution):

* **Fedora**

    `sudo dnf install openjdk11`

* **Ubuntu (18.04 or later)**

    `sudo apt install openjdk-11-jdk`

Then, ensure you have the right version of Java set as the default with `java -version`.

If not, run `sudo update-alternatives --config java` and follow the on-screen instructions to select the OpenJDK 11 as the default and check again.

**Note**: it is also possible to use other Java distributions.
If you wish to use a different one, or even have multiple distributions/versions installed side-by-side on your system (without interfering with the version installed by your system's package manager), it is recommended to use [SDKMAN!](https://sdkman.io/).

### Setting up the hardware

In this tutorial we will be using parts from the [official parts list](https://beechat.network/parts-list/). Other ZigBee hardware may be used, but is not recommended and will not be explained in this tutorial. The process is more or less the same for both the [long range setup](https://beechat.network/long-range-setup/) and the [short range setup](https://beechat.network/short-range-setup/).

***NOTE:*** The Sparkfun Dongle and and XBee Module must be handled with care! Only touch the side edges of the boards, as touching the electronic components with bare hands can cause damage from electrostatic discharge (ESD).

1. Connect the black XBee module to the red Sparkfun dongle by carefully pressing the connectors on the XBee module down into the ports on the Sparkfun dongle. If you are using the long-range setup, make sure the metal antenna connector on the XBee module is pointed outwards (away from the USB port on the Sparkfun dongle).

* If you are using the long-range setup, these additional steps are needed:
    1. Screw on the small metal SMA adapter onto the XBee module's connector.
    2. Connect the antenna to the SMA adapter.

Then, simply plug it into a USB port on your computer and move on to the next step.

### Configuring XCTU

1. [Download and install the correct version of the XCTU configuration tool for your system.](https://www.digi.com/products/embedded-systems/digi-xbee/digi-xbee-tools/xctu#productsupport-utilities)
2. Open the newly installed XCTU program.
3. Click on the second button from the top left (with the magnifying glass) and click it.
4. A menu should have appeared. Check the box next to your radio module (it should be labeled something like `/dev/ttyUSB`). Then press "**Next >**".
5. Then, configure the port parameters to the specifications below:

    | Parameter    | Value  |
    |--------------|--------|
    | Baud Rate    | 9600   |
    | Data Bits    | 8      |
    | Parity       | None   |
    | Stop Bits    | 1      |
    | Flow Control | None   |

6. Click "**finish**".
7. XCTU should now show your module on the left. Click it, and wait for the configuration settings to appear on the right side of the screen.
8. Set the PAN ID (Channel) to a number of your choosing. Also, set a device role for your module. If this is the first device in your area, select "**Form network**". If not, select "**Join network**".
9. On the search bar at the top, type `AP`. Find the option `AP API Enable` and set it to `API Mode Without Escapes [1]`.
10. Press the "**Write**" button (with the pencil icon) to save the changes to the XBee module.

You are now ready to move on to the next step.

## Running BeeChat

BeeChat comes as a precompiled `.jar` archive [which can be downloaded here](https://beechat.network/downloads-page/).

1. [Download the executable `.jar` file](https://beechat.network/downloads/BeeChat.jar)
2. Open a terminal in the same directory as the downloaded `.jar` file and execute it with `java -jar BeeChat.jar`
    * Depending on your system, this may require super user privileges, so you might need to run `sudo java -jar BeeChat.jar` instead

### Command line arguments

No command line arguments are currently supported.

## Using BeeChat

Coming soon!

## Building BeeChat from source

Coming soon!

## In The Future

* In "setting up the hardware" it is stated that "other zigbee hardware can be used". Can a programmer confirm this?
* Current tutorial only supports linux systems, need to extend for windows/mac
* Pictures are needed, they wont work right for me when making this so someone else needs to do em ig

Questions? Comments? Recommendations?
[Email us!](beechatnetwork@gmail.com)

Written by MysteryCombatMan
> Tox: BA21693673F2DFC79411BB4C7507789229058449A380721995B0B0743403984C659C54BB9993
