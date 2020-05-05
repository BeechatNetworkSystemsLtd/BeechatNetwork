package com.digi.wck.beechat;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.XBeeDevice;
import com.digi.xbee.api.XBeeNetwork;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.listeners.IDataReceiveListener;
import com.digi.xbee.api.models.XBeeMessage;

import es.vocali.util.AESCrypt;


/**
 * BEECHAT NETWORK FOR LINUX
 *
 * Based on DIGI INTERNATIONAL'S Example application.
 * Projects used on BeeChat:
 *	XBJL
 * 	RXTX
 *
 * StackOverflow link attribution:
 *	https://stackoverflow.com/questions/22935907/completely-renaming-a-project-in-eclipse
 *	https://stackoverflow.com/questions/15830952/extracting-jar-to-specified-directory
 *	https://stackoverflow.com/questions/1856565/how-do-you-determine-32-or-64-bit-architecture-of-windows-using-java
 *	https://stackoverflow.com/questions/1816673/how-do-i-check-if-a-file-exists-in-java
 *	https://stackoverflow.com/questions/858980/file-to-byte-in-java
 *	https://stackoverflow.com/questions/34054655/how-to-read-bytes-from-a-file-to-a-byte-array
 *	https://stackoverflow.com/questions/5797208/java-how-do-i-write-a-file-to-a-specified-directory
 *	https://stackoverflow.com/questions/11012819/how-can-i-get-a-resource-folder-from-inside-my-jar-file
 *	https://stackoverflow.com/questions/1298066/check-if-an-apt-get-package-is-installed-and-then-install-it-if-its-not-on-linu
 *	https://stackoverflow.com/questions/52504825/how-to-install-jdk-11-under-ubuntu
 *	https://stackoverflow.com/questions/47100605/how-to-include-resource-folder-in-executable-jar-file-in-eclipse
 *	https://stackoverflow.com/questions/11421874/downsampling-audio-from-44-1khz-to-16khz-in-java
 *	https://stackoverflow.com/questions/21757757/sound-recording-not-working-in-java
 *	https://stackoverflow.com/questions/15571496/how-to-check-if-a-folder-exists
 *	https://stackoverflow.com/questions/4105331/how-do-i-convert-from-int-to-string
 *	https://stackoverflow.com/questions/1795808/and-and-or-in-if-statements
 *	https://stackoverflow.com/questions/6340999/create-an-arraylist-of-bytes
 *	https://stackoverflow.com/questions/3705581/java-sound-api-capturing-microphone
 *	https://stackoverflow.com/questions/9125241/remove-the-last-chars-of-the-java-string-variable
 *	https://stackoverflow.com/questions/36491851/how-to-remove-nul-characters-0-from-string-in-java
 *	https://stackoverflow.com/questions/12118403/how-to-compare-binary-files-to-check-if-they-are-the-same
 *	https://stackoverflow.com/questions/28989970/java-removing-u0000-from-an-string
 *	https://stackoverflow.com/questions/9126142/output-the-result-of-a-bash-script
 *	https://stackoverflow.com/questions/17563364/xbee-two-way-communication-sender-and-receiver-at-the-same-time
 *
 * Wireless Connectivity Chat application over ZigBee.
 *
 * <p>This Java application allows users to send and receive data to/from
 * another XBee device on the same network using the XBee Java Library.</p>
 *
 *
 * TODO:
 * - https://security.stackexchange.com/questions/19969/encryption-and-compression-of-data
 *
 *
 */



public class MainApp {

	/* Constants */
	private static final int BAUD_RATE = 9600;
	private static DataReceiveListener listener = new DataReceiveListener();

	static Path generatorsLocation = Paths.get(System.getProperty("user.dir") +"/generators/");
	static Path privatekeysLocation = Paths.get(System.getProperty("user.dir") +"/privatekeys/");
	static Path publickeysLocation = Paths.get(System.getProperty("user.dir") +"/publickeys/");
	static Path configfilesLocation = Paths.get(System.getProperty("user.dir") +"/config/");
	static Path downloadfilesLocation = Paths.get(System.getProperty("user.dir") +"/downloads/");
	static Path uploadfilesLocation = Paths.get(System.getProperty("user.dir") +"/uploads/");
	static Path deepspeechLocation = Paths.get(configfilesLocation+"/DeepSpeech/");
	static Path deepspeechModelsLocation = Paths.get(configfilesLocation+"/DeepSpeech/models/");


	static String checkmark = "\u2713";
	static int maxfilesize = 255*30; //7Zip part file size

	static int secondspassed = 0;
	static int prevmsgtimestamp = 0;
	static boolean delayDetected = false;
	static boolean connected = false;
	static boolean ZIPFILESignal = false;
	static boolean firstrun = false;


	//TEMPORARY FILE BUFFER
	static ArrayList<byte[]> tempfilearray = new ArrayList<byte[]>();

	//Timer to detect delays in file transfers.
	static Timer myTimer = new Timer();
	static TimerTask task = new TimerTask() {
		public void run() {
			if ((connected == true)&&(secondspassed-prevmsgtimestamp>1)&&(ZIPFILESignal == true)) {
				if (delayDetected == false) {
					System.out.println("Transmission error.");
				}
				delayDetected = true;
				tempfilearray.removeAll(tempfilearray);
			}
			secondspassed++;
		}
	};

	public static void start() {
		myTimer.scheduleAtFixedRate(task, 1000, 1000);
	}


	/**
	 * Application main method.
	 *
	 * @param args Command line arguments.
	 * @throws Throwable
	 */
	@SuppressWarnings({"resource" })
	public static void main(String[] args) throws Throwable {


		try {
			Process mkfile;
			mkfile = Runtime.getRuntime().exec(new String[]{
					"bash", "-c", "mkdir " + configfilesLocation+ "; touch " + configfilesLocation + "/fileexists; touch "+configfilesLocation+"/activeport"});
			mkfile.waitFor();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}



		//Start Timer
		start();

		// Is this the first run? Determined with existence of directories, requirements, and contacts.json file
		Path[] allfolders = {generatorsLocation,privatekeysLocation,publickeysLocation,configfilesLocation,downloadfilesLocation,uploadfilesLocation,deepspeechLocation,deepspeechModelsLocation};

		int i = 0;

		while (i<allfolders.length) {
			if (Files.exists(allfolders[i]) == false) {
				firstrun = true;
			}
			i=i+1;
		}

		Path contactfilepath = Paths.get(configfilesLocation + "/contacts.json");

		if (Files.exists(contactfilepath) == false) {
			firstrun = true;
		}


		if (!(Files.isDirectory(Paths.get(deepspeechLocation+"/deepspeechenv")) )){
			firstrun = true;
		}

		
		//Check if software requirements are installed, if not, Install them: python3, openjdk, p7zip, openssl
		boolean py3isinstalled = false;
		boolean p7zipisinstalled = false;
		boolean opensslisinstalled = false;

		try {
	        Process python3check = Runtime.getRuntime().exec(new String[]{
		    "bash", "-c", "dpkg -s python3 | awk '/Status/ {print $3}' > " + configfilesLocation.toString() + "/py3"});
	        python3check.waitFor();
	        if ( file2string(configfilesLocation.toString()+"/py3").substring(0,2).equals("ok")) {
	        	py3isinstalled = true;

	        }

	        Process p7zipcheck = Runtime.getRuntime().exec(new String[]{
	    		    "bash", "-c", "dpkg -s p7zip | awk '/Status/ {print $3}' > " + configfilesLocation.toString() + "/p7zip"});
	        p7zipcheck.waitFor();
	        if ( file2string(configfilesLocation.toString()+"/p7zip").substring(0,2).equals("ok")) {
	    		p7zipisinstalled = true;

	    	}

	    	Process opensslcheck = Runtime.getRuntime().exec(new String[]{
	    		    "bash", "-c", "dpkg -s openssl | awk '/Status/ {print $3}' > " + configfilesLocation.toString() + "/openssl"});
	    	opensslcheck.waitFor();
	    	if ( file2string(configfilesLocation.toString()+"/openssl").substring(0,2).equals("ok")) {
	    		opensslisinstalled = true;

	    	}

		} catch (IOException e1) {
	        	System.out.println("Error checking requirement installations.");
		}


		Path[] allresources = new Path[6];

		if (System.getProperty("os.arch").contains("amd64") || System.getProperty("os.arch").contains("x86")) {
			allresources[0]= Paths.get("/x86_64-unknown-linux-gnu/librxtxSerial.so");
		} else {
			allresources[0]= Paths.get("/i686-pc-linux-gnu/librxtxSerial.so");
		}

		allresources[1]= Paths.get("lm.binary");
		allresources[2]= Paths.get("output_graph.pb");
		allresources[3]= Paths.get("output_graph.pbmm");
		allresources[4]= Paths.get("output_graph.tflite");
		allresources[5]= Paths.get("trie");


		Path[] finalpath = new Path[6];

		if (System.getProperty("os.arch").contains("amd64") || System.getProperty("os.arch").contains("x86")) {
			finalpath[0]= Paths.get("/lib/x86_64-linux-gnu/");
		} else {
			finalpath[0]= Paths.get("/lib/i386-linux-gnu/");
		}
		finalpath[1]= Paths.get(deepspeechModelsLocation +"/");
		finalpath[2]= Paths.get(deepspeechModelsLocation +"/");
		finalpath[3]= Paths.get(deepspeechModelsLocation +"/");
		finalpath[4]= Paths.get(deepspeechModelsLocation +"/");
		finalpath[5]= Paths.get(deepspeechModelsLocation +"/");


		i = 0;

		File tmpDir = new File(configfilesLocation+"/run");
		boolean exists = tmpDir.exists();

		if (exists == false && firstrun==true) {
			System.out.println("This is your first run of BeeChat. \nBeeChat has not detected DeepSpeech on your installation. \nDeepSpeech is a speech-to-text experimental feature. Do you wish to install DeepSpeech? \nType 'yes' or 'no' and then press enter to decide:\n");
			Scanner lightinstallscan = new Scanner(System.in);
			String shouldweinstall = null;
			if (lightinstallscan.hasNext()) {
				shouldweinstall = lightinstallscan.nextLine();
			}
			System.out.println("'"+shouldweinstall+"'");
			if (!shouldweinstall.equals("yes") == true) {
				i = allresources.length;
			} else {
				//Install Mozilla DeepSpeech
				if (!(Files.isDirectory(Paths.get(deepspeechLocation+"/deepspeechenv")))){
					try {
						System.out.println("Installing DeepSpeech...");

						Process virtualenv = Runtime.getRuntime().exec(new String[]{
								"bash", "-c", "virtualenv -p python3 " + deepspeechLocation.toString() + "/deepspeechenv; source "+deepspeechLocation+"deepspeechenv/bin/activate; pip3 install deepspeech"});
						virtualenv.waitFor();
						System.out.println("DeepSpeech installed.");
					} catch (IOException  e1) {
					}
				}
			}

			while (i<allresources.length) {
				final File jarFile = new File(MainApp.class.getProtectionDomain().getCodeSource().getLocation().getPath());
				if(jarFile.isFile()) {  // Run with JAR file
				    final JarFile jar = new JarFile(jarFile);

				        File f = new File(finalpath[i].toString()+"/"+allresources[i]);
		            	if(!f.exists() && !f.isDirectory()) {
					            ////////////////////////////////////////////////////////////////
					            //Must first extract from Jar and then move to required folder
					            if (i!=0) {
						            try {
						            	Process fileextractor = Runtime.getRuntime().exec(new String[]{
					            				"bash", "-c", "cd "+System.getProperty("user.dir")+"; 7z e " +jar.getName()+ " -o" +finalpath[i].toString()+" "+allresources[i]});
						            	fileextractor.waitFor();
						            } catch (IOException e) {

						            }
					            } else {
					            	try {
					            		File rxtxlibcheck = new File(configfilesLocation+"/libinstalled");
					            		if(!rxtxlibcheck.exists() && !rxtxlibcheck.isDirectory()) {
						            		Process fileextractor = Runtime.getRuntime().exec(new String[]{
					            				"bash", "-c", "cd "+System.getProperty("user.dir")+"; 7z e " +jar.getName()+ " "+allresources[i].toString().substring(1,allresources[i].toString().length())+" -y; pkexec mv "+ System.getProperty("user.dir")+"/"+allresources[i].toString().substring(allresources[i].toString().lastIndexOf("/")+1,allresources[i].toString().length())+" "+ finalpath[i].toString()+"/; touch "+configfilesLocation+"/libinstalled; echo ok > "+configfilesLocation+"/libinstalled;"});
						            		//7z e BugBee.jar x86_64-unknown-linux-gnu/librxtxSerial.so -y
					            			fileextractor.waitFor();
					            			System.out.println("Library copied to correct folder.");
					            		}
					            	} catch (IOException e) {

					            	}
					            }
				        }
				    jar.close();
				} else { // Run with IDE
				    final URL url = MainApp.class.getResource(allresources[i].toString());
				    if (url != null) {
				        try {
				            final File resource = new File(url.toURI());
				            if (i!=0) {
				            	File f = new File(finalpath[i].toString()+"/"+resource.getName());
				            	if(!f.exists() && !f.isDirectory()) {
				            	try {
				            			Process filecopier = Runtime.getRuntime().exec(new String[]{
				            				"bash", "-c", "cd "+resource.getPath()+"; cp " +resource.getAbsolutePath()+ " " +finalpath[i].toString()});
				            			System.out.println(resource.getPath() + "  " + resource.getName());
				            			filecopier.waitFor();
				            			System.out.println("Deepspeech models copied to correct folder.");
				            		} catch (IOException e1) {
				        	        	System.out.println("Failed copying deepspeech Model files.");
				        	        }
				            	}
				            } else {
				            	File f = new File(finalpath[i].toString()+"/"+resource.getName());
				            	if(!f.exists() && !f.isDirectory()) {
					            	try {
				            			Process sudofilecopier = Runtime.getRuntime().exec(new String[]{
				            				"bash", "-c", "cd "+resource.getPath()+"; pkexec cp " +resource.getAbsolutePath()+ " " + finalpath[i].toString() });
				            			sudofilecopier.waitFor();
				            			System.out.println("Library copied to correct folder.");
				            		} catch (IOException e1) {
				        	        	System.out.println("Failed copying RXTX file.");
				        	        }
				            	}

				            }
				        } catch (URISyntaxException ex) {
				            // never happens
				        }
				    }
				}

			i = i +1;
			}


		}

		//PORT SECTION
		//Search for active USB TTY port:
		try {
	        Process portsetter = Runtime.getRuntime().exec(new String[]{
		    "bash", "-c", "echo \"$(ls /dev | grep \"USB\")\" > " + configfilesLocation.toString() + "/activeport"});
	        portsetter.waitFor(); } catch (IOException e1) {
	        	System.out.println("\nNo XBee device found. Exiting program.\n");
	        	System.exit(0);
	        }

	        //Read "activeport" file and use input as working port:
	        String PORT = file2string(configfilesLocation.toString() + "/activeport");
	        String LONGPORT = "/dev/"+PORT;
		    PORT = PORT.replace("\n", "");
		    LONGPORT = LONGPORT.replace("\n", "");

			//If "activeport" contains a number, modify ownership of port with administrator privileges:
	        if (stringContainsNumber(file2string(configfilesLocation.toString() + "/activeport")) == true) {
				ProcessBuilder setportownsershipprocessBuilder = new ProcessBuilder();
				setportownsershipprocessBuilder.command("bash", "-c", "pkexec chmod 666 "+LONGPORT);
				try {
					Process process = setportownsershipprocessBuilder.start();
					process.waitFor();

					Process deletefile = Runtime.getRuntime().exec(new String[]{
							"bash", "-c", "rm -rf " + configfilesLocation.toString() + "/activeport"});
					deletefile.waitFor();
				} catch (IOException e1) {
					e1.printStackTrace();
				}

	        } else {
	        	System.out.println("\nNo XBee device found. Exiting program.\n");
	        	System.exit(0);
	        }
		//LOCK FILE CLEANING
		try {
			String loc = "/var/lock/LCK.."+PORT;
			loc = loc.replace("\n","");
			if (fileexists(loc) == true) {
				// Try to remove lock file.
				Process remlock = Runtime.getRuntime().exec(new String[]{
				"bash", "-c", "rm -rf /var/lock/LCK.."+PORT});
				remlock.waitFor();
				System.out.println("Lock file removed correctly. \n");
				}
			} catch (Exception err) {}

		//Instantiate XBee Device
		XBeeDevice myDevice = new XBeeDevice(LONGPORT, BAUD_RATE);

		try {
			i = 0;

			while (i<allfolders.length) {
				if (i==0) {

						}
				if (Files.exists(allfolders[i]) == false) {
					try {
						Process mkdir = Runtime.getRuntime().exec(new String[]{
								"bash", "-c", "mkdir " + allfolders[i]});
							    mkdir.waitFor();
							    System.out.println("Created folder "+allfolders[i].toString());
					} catch (IOException e1) {
						System.out.println("Error creating folder "+allfolders[i].toString());
					}
				}
				i=i+1;
			}

			if (fileexists(configfilesLocation.toString() + "/contacts.json") == false) {
				try {
					Process mkfile = Runtime.getRuntime().exec(new String[]{
					"bash", "-c", "touch " + configfilesLocation.toString() + "/contacts.json"});
					mkfile.waitFor();
				} catch (IOException e1) {
					System.out.println("Error creating 'contacts.json' file.");
				}
			}


						if ((fileexists(configfilesLocation.toString() + "/contacts.json") == true) && (Files.isDirectory(Paths.get(deepspeechLocation+"/deepspeechenv")))) {

						}

					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}



		if (firstrun == true) {
					Process mkfile;
					try {
						mkfile = Runtime.getRuntime().exec(new String[]{
								"bash", "-c", "touch " + configfilesLocation + "/run; "});
						mkfile.waitFor();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		}




		System.out.println("+---------------------------------------------+");
		System.out.println("|            BeeChat Network 0.1              |");
		System.out.println("+---------------------------------------------+\n");

		System.out.println("Working directory: " + System.getProperty("user.dir"));
		System.out.println("Operating System: "+ System.getProperty("os.name"));
		if (!(System.getProperty("os.name").equals("Linux"))) {
			System.out.println("Please run on a Linux OS.");
			System.exit(0);
		}

		String myPublicKey = "";
		String myGen = "";
		String uname = " ";


		try {
			myPublicKey = file2string(configfilesLocation.toString()+"/mypublickey.pem");
			myPublicKey = myPublicKey.replace("\\n","\n");

			myGen = file2string(configfilesLocation.toString()+"/mygenerator.pem");
			myGen = myGen.replace("\\n","\n");

		} catch (Exception err) {
			System.out.println("No keys found. Generating keys...");
			genkeys(0,"");
			myPublicKey = file2string(configfilesLocation.toString()+"/mypublickey.pem");
			myPublicKey = myPublicKey.replace("\\n","\n");

			myGen = file2string(configfilesLocation.toString() + "/mygenerator.pem");
			myGen = myGen.replace("\\n","\n");
		}


		int chunklength= 255; //Max payload size on XBee devices.

		try {
			myDevice.open();
			System.out.println("\nMy NodeID: "+myDevice.getNodeID().toString()+"\n");
		} catch (XBeeException e) {
			System.out.println("\nNo device found. Exiting program.");
			System.exit(0);
		} finally {
		myDevice.close();
		}

		while (true) {
			System.out.println("\nEnter command: (type in 'help' to look up the available commands)");
			Scanner commandscanner = new Scanner(System.in);
			if (commandscanner.hasNext()) {
				String command = commandscanner.next();
				docommands(command, chunklength, myDevice, uname, myPublicKey, myGen);
				//commandscanner.close();
			} else {
				return;
			}
		}
	} //end main method

	/**
	 * Application commands method.
	 *
	 * @param args Command line arguments.
	 * @throws Exception
	 * @throws Throwable
	 */
	@SuppressWarnings({ "null" })
	static void docommands(String command, int chunklength, XBeeDevice myDevice, String uname, String myPublicKey, String myGen) throws Exception, Throwable{
		//COMMANDS SECTION

			command = command.toLowerCase();

			int i;
			if(command.equals("chat")){
				try {
					myDevice.open();
					myDevice.addDataListener(listener);
					System.out.println("\nChat with (REMOTE_NODE_ID):");

					Scanner nodeid = new Scanner(System.in);
					String REMOTE_NODE_ID = nodeid.next();
					//nodeid.close();

					if (myDevice.getNodeID().equals(REMOTE_NODE_ID)) {
						System.err.println("Error: the value of the REMOTE_NODE_ID must be "
								+ "the Node Identifier (NI) of the OTHER module.");
					} else {
						System.out.println("\nEstablishing connection with " + REMOTE_NODE_ID + ".");
						XBeeNetwork network = myDevice.getNetwork();
						RemoteXBeeDevice remote = network.discoverDevice(REMOTE_NODE_ID);
						if (remote != null) {
							if (getcontact(REMOTE_NODE_ID)[0].equals(REMOTE_NODE_ID)) {
								System.out.println("Known contact, using encryption.");
								System.out.println("Connection established. Send messages below:\n");
								boolean didiexit = false;
								while (didiexit == false) {


									Scanner messagescanner = new Scanner(System.in);
									String message = messagescanner.nextLine();
									//messagescanner.close();


									if (message.substring(0, 4).contentEquals("exit") == true) {
										didiexit = true;
									}

									//write message to message file
									Process writemessagefile = Runtime.getRuntime().exec(new String[]{
											"bash", "-c", "echo '"+message+"' > "+uploadfilesLocation +"/msg"});
									writemessagefile.waitFor();

									//apend message to log file
									Process appendtologfile = Runtime.getRuntime().exec(new String[]{
											"bash", "-c", "echo '"+myDevice.getNodeID()+": "+message+"' >> "+configfilesLocation +"/"+remote.getNodeID()});
									appendtologfile.waitFor();

									//create keys from contact's generator
									genkeys(1,REMOTE_NODE_ID);

									//Getting shared secret
									Process sharedsecretfile = Runtime.getRuntime().exec(new String[]{
											"bash", "-c", "openssl pkeyutl -derive -inkey "+privatekeysLocation.toString() + "/"+REMOTE_NODE_ID+"myprivatekey.pem -peerkey "+publickeysLocation.toString() + "/"+REMOTE_NODE_ID+"publickey.pem | openssl sha3-256 | sed -e 's|(stdin)= ||' > "+System.getProperty("user.dir") + "/sharedsecret"});
									sharedsecretfile.waitFor();


									//Create AESCrypt file
									AESCrypt encrypter = new AESCrypt(file2string(System.getProperty("user.dir") + "/sharedsecret"));
									//System.out.println(file2string(System.getProperty("user.dir") + "/sharedsecret"));

									//Delete sharedsecret file
									Process deletesharedsecretfile = Runtime.getRuntime().exec(new String[]{
											"bash", "-c", "rm -rf "+System.getProperty("user.dir") + "/sharedsecret"});
									deletesharedsecretfile.waitFor();

									//Converting file to aes with shared sha3-256 secret
									encrypter.encrypt(2, uploadfilesLocation +"/msg", uploadfilesLocation +"/msg.aes");

									//send pubkey
									i = 0;
									REMOTE_NODE_ID = remote.getNodeID();
									chunklength = 255;

									//SENDING HEADER
									String pubkeysend = file2string(publickeysLocation.toString() + "/"+REMOTE_NODE_ID +"mypublickey.pem");
									pubkeysend = pubkeysend.replace("\\n","\n");
									String CHATHeader = "-----CHAT BEGIN-----\npubkey:"+pubkeysend+"\n";
									byte[] CHATHeaderbytebuffer = CHATHeader.getBytes();
									while (i<CHATHeaderbytebuffer.length) {
										myDevice.sendData(remote, Arrays.copyOfRange(CHATHeaderbytebuffer, i, i+chunklength ));
										//System.out.println("Sending file info...");
										i=i+chunklength;
									}

									//try to send bytes over zigbee
									Path encmsgloc = Paths.get(uploadfilesLocation +"/msg.aes");
									byte[] msgbytebuffer = Files.readAllBytes(encmsgloc);
									if (msgbytebuffer.length<chunklength) {
										chunklength = msgbytebuffer.length;
									}

									i = 0;
									while (i<msgbytebuffer.length) {
										myDevice.sendData(remote, Arrays.copyOfRange(msgbytebuffer, i, i+chunklength ));
										//System.out.println("Sending file info...");
										i=i+chunklength;
									}

									chunklength = 255;
									//SENDING FOOTER
									String CHATFooter = "-----CHAT END-----";
									byte[] CHATFooterbytebuffer = CHATFooter.getBytes();
									i=0;
									while (i<CHATFooterbytebuffer.length) {
										myDevice.sendData(remote, Arrays.copyOfRange(CHATFooterbytebuffer, i, i+chunklength));
										i=i+chunklength;
										}
									System.out.print(checkmark+"\n");


								}
								return;
							} else if (!(getcontact(REMOTE_NODE_ID)[0].equals(REMOTE_NODE_ID))) {
								System.out.println("Contact is unknown, please have them send their public and generator keys.");
								myDevice.close();
								return;
						}

						}
					}
		    	} catch (XBeeException e) {
		    		e.printStackTrace();
		    		myDevice.close();
		    		return;
		    	} finally {
		    		myDevice.close();
		    		//close XBEE
		    	}
			} else if (command.equals("changeid")) {
				changeid(myDevice, "");
				return;


			} else if (command.equals("listnodes")) {
				listnodes(myDevice);
				return;

			} else if (command.equals("sendmyinfo")){
				sendmyinfo(myDevice,uname,myPublicKey,myGen);
				return;

			} else if (command.equals("genkeys")) {
	            //Generate keys for myself
				genkeys(0,"");
				return;
			} else if (command.equals("exit")) {
	        	//Exit program
				System.exit(0);

			} else if (command.equals("callnode")) {
				//CALL NODE - Higher bitrate needed... maybe when we make our own OpenSource radios.

			} else if (command.equals("sendfile")) {

				chunklength = 255;
		    	myDevice.close();
		    	System.out.println("\nRemember that to send files to another device, you must have their contact information. Restart the program and enter the 'help' command to learn how to do this.");
							// sending as 7z multi-part AES file


		    	try {
		    		//To who?
		    		myDevice.open();
		    		myDevice.addDataListener(listener);

		    		System.out.println("\nLocal XBee: " + myDevice.getNodeID());

		    		System.out.println("\nSend file to (REMOTE_NODE_ID):");

		    		Scanner nodeid = new Scanner(System.in);
					String REMOTE_NODE_ID = nodeid.next();


					if (myDevice.getNodeID().equals(REMOTE_NODE_ID)) {
							System.err.println("Error: the value of the REMOTE_NODE_ID must be "
									+ "the Node Identifier (NI) of the OTHER module.");
					} else {
						System.out.println("\nEstablishing connection with " + REMOTE_NODE_ID + ".");

						XBeeNetwork network = myDevice.getNetwork();
		                RemoteXBeeDevice remote = network.discoverDevice(REMOTE_NODE_ID);

						if (remote != null) {
							if (getcontact(REMOTE_NODE_ID)[0].equals(REMOTE_NODE_ID)) {
								System.out.println("Known contact, using encryption.");
								System.out.println("Connection established with "+REMOTE_NODE_ID+". Choose your file to send, or type in 'exit' to exit:\n");
								while (true) {

									Scanner filetosendscanner = new Scanner(System.in);

									String filetosend  = filetosendscanner.nextLine();

									if (filetosend.substring(0,4).equals("exit")) {
										System.out.println("Quitting.");
										return;
									}

									String currpartString = null;
									int currpart = 0;
									try {
										currpartString = file2string(uploadfilesLocation+"/dir-"+filetosend+"/lastpartsent");
										currpartString = currpartString.replace("\\n", "");
										currpartString = currpartString.replace("\n", "");
										currpart = Integer.parseInt(currpartString);
									} catch (IOException e) {
										currpart = 1;
									}
									if (currpart == 1) {
										//create keys from contact's generator
										genkeys(1,REMOTE_NODE_ID);

										//Getting shared secret
										Process sharedsecretfile = Runtime.getRuntime().exec(new String[]{
												"bash", "-c", "openssl pkeyutl -derive -inkey "+privatekeysLocation.toString() + "/"+REMOTE_NODE_ID+"myprivatekey.pem -peerkey "+publickeysLocation.toString() + "/"+REMOTE_NODE_ID+"publickey.pem | openssl sha3-256 | sed -e 's|(stdin)= ||' > "+System.getProperty("user.dir") + "/sharedsecret"});
										sharedsecretfile.waitFor();
										//Create AESCrypt file
										AESCrypt encrypter = new AESCrypt(file2string(System.getProperty("user.dir") + "/sharedsecret"));
										System.out.println(file2string(System.getProperty("user.dir") + "/sharedsecret"));
										//Delete sharedsecret file
										Process deletesharedsecretfile = Runtime.getRuntime().exec(new String[]{
												"bash", "-c", "rm -rf "+System.getProperty("user.dir") + "/sharedsecret"});
										deletesharedsecretfile.waitFor();

										//Converting file to aes with shared sha3-256 secret
										encrypter.encrypt(2, uploadfilesLocation +"/"+filetosend, uploadfilesLocation +"/"+filetosend+".aes");
										System.out.println("Successfully encrypted file.");
										Path partfileLocation = null;

										i = 0;
										int partstosend = 0;
										// Send Message
										Path fileLocation = Paths.get(uploadfilesLocation +"/"+filetosend+".aes");
										try {
											//Zipping & sending FILE

											byte[] bytebuffer = Files.readAllBytes(fileLocation);
											int len = bytebuffer.length;
											System.out.println("File size (bytes): "+len);
											//Zipping

											Process zipfile = Runtime.getRuntime().exec(new String[]{
													"bash", "-c", "7z -v"+maxfilesize+"b a "+uploadfilesLocation+"/dir-"+filetosend+"/"+filetosend+".aes.7z "+uploadfilesLocation+"/"+filetosend+".aes"});
											zipfile.waitFor();

											Process countfiles = Runtime.getRuntime().exec(new String[]{
													"bash", "-c", "find "+uploadfilesLocation+"/dir-"+filetosend+"/ -type f | wc -l > "+configfilesLocation+"/filecount"});
											countfiles.waitFor();
											partstosend = Integer.parseInt(file2string(configfilesLocation+"/filecount").replace("\n", ""));
											System.out.println("Parts to send: "+partstosend);
											//Current part file

										} finally {
											try {
												i = 0;
												REMOTE_NODE_ID = remote.getNodeID();
												chunklength = 255;

												//SENDING HEADER
												String pubkeysend = file2string(publickeysLocation.toString() + "/"+REMOTE_NODE_ID +"mypublickey.pem");
												pubkeysend = pubkeysend.replace("\\n","\n");
												String ZIPHeader = "-----ZIPFILE BEGIN-----\npubkey:"+pubkeysend+"\nparts:"+partstosend+"*";
												byte[] ZIPHeaderbytebuffer = ZIPHeader.getBytes();
												while (i<ZIPHeaderbytebuffer.length) {
													myDevice.sendData(remote, Arrays.copyOfRange(ZIPHeaderbytebuffer, i, i+chunklength ));
													//System.out.println("Sending file info...");
													i=i+chunklength;
												}
												sendparts(myDevice, remote,currpart,partstosend,filetosend,partfileLocation);
											} catch (XBeeException e) {
													System.err.println("Error transmitting message: " + e.getMessage());

											//Delete folder containing the parts
													if (partfileLocation.toString() != null) {
														Process deleteparts = Runtime.getRuntime().exec(new String[]{
																"bash", "-c", "rm -rf "+partfileLocation.toString().substring(0,partfileLocation.toString().lastIndexOf("/"))+"; rm -rf "+uploadfilesLocation+"/"+filetosend+".aes" });
														deleteparts.waitFor();
													}
											} finally {
												myDevice.open();
												//SENDING FOOTER
												String PARTFooter = "-----ZIPFILE END-----";
												byte[] PARTFooterbytebuffer = PARTFooter.getBytes();
												i=0;
												while (i<PARTFooterbytebuffer.length) {
													myDevice.sendData(remote, Arrays.copyOfRange(PARTFooterbytebuffer, i, i+chunklength));
													i=i+chunklength;
													}

												//apend message to log file
												Process appendtologfile = Runtime.getRuntime().exec(new String[]{
														"bash", "-c", "echo '"+myDevice.getNodeID()+": File sent: "+filetosend+"' >> "+configfilesLocation +"/"+remote.getNodeID()});
												appendtologfile .waitFor();
												System.out.println("File successfully sent "+checkmark+"\n");
												System.out.println("Please input a new file to send, or type 'exit' to stop sending files:\n");

											}
									}
									} else { // if currpart is not 1
										int partstosend = Integer.parseInt(file2string(configfilesLocation+"/filecount").replace("\n", ""));
										Path partfileLocation = null;
										try {
											sendparts(myDevice, remote,currpart,partstosend,filetosend,partfileLocation);
										} catch (XBeeException e) {
												System.err.println("CURRPART IS NOT ONE. Error transmitting message: " + e.getMessage());

												//Delete folder containing the parts
												if (partfileLocation.toString() != null) {
													Process deleteparts = Runtime.getRuntime().exec(new String[]{
															"bash", "-c", "rm -rf "+partfileLocation.toString().substring(0,partfileLocation.toString().lastIndexOf("/"))+"; rm -rf "+uploadfilesLocation+"/"+filetosend+".aes" });
													deleteparts.waitFor();
												}
										} finally {
											//SENDING FOOTER
											String PARTFooter = "-----ZIPFILE END-----";
											byte[] PARTFooterbytebuffer = PARTFooter.getBytes();
											i=0;
											while (i<PARTFooterbytebuffer.length) {
												myDevice.sendData(remote, Arrays.copyOfRange(PARTFooterbytebuffer, i, i+chunklength));
												i=i+chunklength;
											}

										}
									}
								}
							} else if (!(getcontact(REMOTE_NODE_ID)[0].equals(REMOTE_NODE_ID))) {
									System.out.println("Contact is unknown, please have them send their info to you to send files.");
									return;
							}
						}
					}
		    	} catch (XBeeException e) {
		    		e.printStackTrace();
		    		myDevice.close();
		    		//if (s != null)
					//s.close();
		    		return;
		    	} finally {
		    		myDevice.close();
		    		//if (s != null)
					//s.close();
					//close scanner and XBee
		    	}

			} else if (command.equals("deepspeech")) {
				try {
					myDevice.open();
					myDevice.addDataListener(listener);
					System.out.println("\nChat with (REMOTE_NODE_ID):");

					Scanner nodeid = new Scanner(System.in);
					String REMOTE_NODE_ID = nodeid.next();

					if (myDevice.getNodeID().equals(REMOTE_NODE_ID)) {
						System.err.println("Error: the value of the REMOTE_NODE_ID must be "
								+ "the Node Identifier (NI) of the OTHER module.");
					} else {
						System.out.println("\nEstablishing connection with " + REMOTE_NODE_ID + ".");
						XBeeNetwork network = myDevice.getNetwork();
						RemoteXBeeDevice remote = network.discoverDevice(REMOTE_NODE_ID);
						if (remote != null) {
							if (getcontact(REMOTE_NODE_ID)[0].equals(REMOTE_NODE_ID)) {
								System.out.println("Known contact, using encryption.");
								System.out.println("Connection established. Speak to send a message, or type 'exit' to quit:\n");
								while (true) {

									if (nodeid.nextLine().substring(0, 4).contentEquals("exit") == true) {
										return;
									}

									//Record below to deepspeechLocation/RecordAudio.wav
										// creates a new thread that waits for a specified
										// of time before stopping
							        Thread stopper = new Thread(new Runnable() {
							            public void run() {
							                try {
							                    Thread.sleep(RECORD_TIME);
							                } catch (InterruptedException ex) {
							                    ex.printStackTrace();
							                }
							                finishrec();
							            }
							        });

							        stopper.start();

							        // start recording
							        startrec();

									//DeepSpeech Processing
									Process deepspeech = Runtime.getRuntime().exec(new String[]{
											"bash", "-c", "cd "+deepspeechLocation+" ; source deepspeechenv/bin/activate; deepspeech --model "+deepspeechModelsLocation+"/output_graph.pb --audio RecordAudio.wav > transcription"});
									deepspeech.waitFor();
									System.out.println("You said: "+file2string(deepspeechLocation+"/transcription"));


									String message = file2string(deepspeechLocation+"/transcription");


									//write message to message file
									Process writemessagefile = Runtime.getRuntime().exec(new String[]{
											"bash", "-c", "echo '"+message+"' > "+uploadfilesLocation +"/msg"});
									writemessagefile .waitFor();

									//apend message to log file
									Process appendtologfile = Runtime.getRuntime().exec(new String[]{
											"bash", "-c", "echo '"+myDevice.getNodeID()+": "+message+"' >> "+configfilesLocation +"/"+remote.getNodeID()});
									appendtologfile .waitFor();

									//create keys from contact's generator
									genkeys(1,REMOTE_NODE_ID);

									//Getting shared secret
									Process sharedsecretfile = Runtime.getRuntime().exec(new String[]{
											"bash", "-c", "openssl pkeyutl -derive -inkey "+privatekeysLocation.toString() + "/"+REMOTE_NODE_ID+"myprivatekey.pem -peerkey "+publickeysLocation.toString() + "/"+REMOTE_NODE_ID+"publickey.pem | openssl sha3-256 | sed -e 's|(stdin)= ||' > "+System.getProperty("user.dir") + "/sharedsecret"});
									sharedsecretfile.waitFor();


									//Create AESCrypt file
									AESCrypt encrypter = new AESCrypt(file2string(System.getProperty("user.dir") + "/sharedsecret"));
									//System.out.println(file2string(System.getProperty("user.dir") + "/sharedsecret"));

									//Delete sharedsecret file
									Process deletesharedsecretfile = Runtime.getRuntime().exec(new String[]{
											"bash", "-c", "rm -rf "+System.getProperty("user.dir") + "/sharedsecret"});
									deletesharedsecretfile.waitFor();

									//Converting file to aes with shared sha3-256 secret
									encrypter.encrypt(2, uploadfilesLocation +"/msg", uploadfilesLocation +"/msg.aes");

									//send pubkey
									i = 0;
									REMOTE_NODE_ID = remote.getNodeID();
									chunklength = 255;

									//SENDING HEADER
									String pubkeysend = file2string(publickeysLocation.toString() + "/"+REMOTE_NODE_ID +"mypublickey.pem");
									pubkeysend = pubkeysend.replace("\\n","\n");
									String CHATHeader = "-----CHAT BEGIN-----\npubkey:"+pubkeysend+"\n";
									byte[] CHATHeaderbytebuffer = CHATHeader.getBytes();
									while (i<CHATHeaderbytebuffer.length) {
										myDevice.sendData(remote, Arrays.copyOfRange(CHATHeaderbytebuffer, i, i+chunklength ));
										//System.out.println("Sending file info...");
										i=i+chunklength;
									}

									//try to send bytes over zigbee
									Path encmsgloc = Paths.get(uploadfilesLocation +"/msg.aes");
									byte[] msgbytebuffer = Files.readAllBytes(encmsgloc);
									if (msgbytebuffer.length<chunklength) {
										chunklength = msgbytebuffer.length;
									}

									i = 0;
									while (i<msgbytebuffer.length) {
										myDevice.sendData(remote, Arrays.copyOfRange(msgbytebuffer, i, i+chunklength ));
										//System.out.println("Sending file info...");
										i=i+chunklength;
									}

									chunklength = 255;
									//SENDING FOOTER
									String CHATFooter = "-----CHAT END-----";
									byte[] CHATFooterbytebuffer = CHATFooter.getBytes();
									i=0;
									while (i<CHATFooterbytebuffer.length) {
										myDevice.sendData(remote, Arrays.copyOfRange(CHATFooterbytebuffer, i, i+chunklength));
										i=i+chunklength;
										}
									System.out.print(checkmark+"\n");


								}
							} else if (!(getcontact(REMOTE_NODE_ID)[0].equals(REMOTE_NODE_ID))) {
								System.out.println("Contact is unknown, please have them send their public and generator keys.");
								return;
							}

						}
					}
				} catch (XBeeException e) {
					System.out.println(e.getMessage());
					myDevice.close();
		            return;
				} finally {
					myDevice.close();
				}


			} else if (command.equals("help")) {
	                	System.out.println("Commands available:\n\ngenkeys: Used to generate your keypair.\nsendmyinfo: Sends your keypair to another device, so they can contact you in an encrypted manner.\nsendfile: Allows you to send an encrypted file if they have used 'sendmyinfo' to your device previously.\nchat: Allows you to chat back and forth with encryption (if it is with a known contact) with another device.\nchangeid: Change your contact ID. You can type in \"random\" to generate a random ID.\nlistnodes: List the nodes within your PAN ID.\ndeepspeech: utilize Mozilla Deepspeech to transcribe audio and send as encrypted text to desired device.\nhelp: Display this help text.\nexit: Quit program.");
	                	return;
			} else if (command.equals("exit")) {
						System.exit(0);
			} //end commands

	}

	//Sound Methods:
    // record duration, in milliseconds
    static final long RECORD_TIME = 10000;  // 10 seconds

    // path of the wav file
    static File wavFile = new File(deepspeechLocation+"/RecordAudio.wav");

    // format of audio file
    static AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;

    // the line from which audio data is captured
    static TargetDataLine line;

    /**
     * Defines an audio format
     */
    static AudioFormat getAudioFormat() {
        float sampleRate = 44100.0F;
        int sampleSizeInBits = 8;
        int channels = 2;
        boolean signed = true;
        boolean bigEndian = true;
        AudioFormat format = new AudioFormat(sampleRate, sampleSizeInBits,
                                             channels, signed, bigEndian);
        return format;
    }

    /**
     * Captures the sound and record into a WAV file
     */
    static void startrec() {
        try {
            AudioFormat format = getAudioFormat();
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

            // checks if system supports the data line
            if (!AudioSystem.isLineSupported(info)) {
                System.out.println("Line not supported");
                System.exit(0);
            }
            line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();   // start capturing


            AudioInputStream ais = new AudioInputStream(line);

            System.out.println("Start recording...");

            // start recording
            AudioSystem.write(ais, fileType, wavFile);

        } catch (LineUnavailableException ex) {
            ex.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     * Closes the target data line to finish capturing and recording
     */
    static void finishrec() {
        line.stop();
        line.close();
        System.out.println("Finished recording.");
    }


	//Other Command Methods

	public static void changeid(XBeeDevice myDevice, String newname) throws InterruptedException {
		Scanner scan = new Scanner(System.in);
		try {
            		myDevice.open();
            		myDevice.addDataListener(listener);
			System.out.println("\nCurrent NodeID: " + myDevice.getNodeID());
			System.out.println("\nInput new NodeID:");
			String NEW_NODE_ID = scan.next();
			
            		if (NEW_NODE_ID.equals("random")) {
            		//generate random nodeID
            			try {
    		        		Process randomnodeid = Runtime.getRuntime().exec(new String[]{
    			    			"bash", "-c", "head /dev/urandom | tr -dc A-Za-z0-9 | head -c 13 > " + configfilesLocation.toString() + "/mynodeid"});
    		        		randomnodeid.waitFor();
    		        		NEW_NODE_ID = file2string( configfilesLocation.toString()  + "/mynodeid");

    		        		Process deletefile = Runtime.getRuntime().exec(new String[]{
    						"bash", "-c", "rm -rf " + configfilesLocation.toString() + "/mynodeid"});
    					deletefile.waitFor();
    				} catch (IOException e1) {
	    		        	System.out.println("Error creating nodeid file.");
    			        }
			}
			myDevice.setNodeID(NEW_NODE_ID);
            		myDevice.writeChanges();
		} catch (XBeeException e) {
            		System.err.println("Error transmitting message: " + e.getMessage());
            		myDevice.close();
            		System.exit(1);
        	} finally {
            myDevice.close();
            //scan.close();
            //close device
        }


	}

	public static void sendparts(XBeeDevice myDevice, RemoteXBeeDevice remote, int currpart, int partstosend, String filetosend, Path partfileLocation) throws Exception, IOException, Exception, ParseException, Throwable, GeneralSecurityException {
		try {
			if (myDevice.isOpen() == true) {
				myDevice.close();
			}
			myDevice.open();
			//Sending parts number
			int i = 0;
			int chunklength = 255;

			while (currpart <= partstosend) {
			//Writing to the uploads folder "currpart" was the last part sent

				Process currpartwrite = Runtime.getRuntime().exec(new String[]{
						"bash", "-c", "echo '" + currpart + "' > " + uploadfilesLocation+"/dir-"+filetosend+"/lastpartsent"});
				currpartwrite.waitFor();

				i = 0;
			//SENDING PARTHEADER
				String PARTHeader = "-----ZIPPART BEGIN-----";
				byte[] PARTHeaderbytebuffer = PARTHeader.getBytes();
				while (i<PARTHeaderbytebuffer.length) {
					myDevice.sendData(remote, Arrays.copyOfRange(PARTHeaderbytebuffer, i, i+chunklength));
			//System.out.println("Sending file info...");
					i=i+chunklength;
				}

			//convert text after 7z to int
			//lengthstate
			//is currpart under 10? 2 zeros in front + currpart
			//is currpart under 100? zero in front + currpart
			//is currpart over 100? if so, make the string after ".7z." to be currpart


				if (currpart <10) {
					partfileLocation = Paths.get(uploadfilesLocation +"/dir-"+filetosend+"/"+filetosend+".aes.7z.00"+currpart);
				} else if (!(currpart <10) && currpart <100 ) {
					partfileLocation = Paths.get(uploadfilesLocation +"/dir-"+filetosend+"/"+filetosend+".aes.7z.0"+currpart);
				} else {
					partfileLocation = Paths.get(uploadfilesLocation +"/dir-"+filetosend+"/"+filetosend+".aes.7z."+currpart);
				}

										//PARTFILE SENDING
				byte[] partbytebuffer = Files.readAllBytes(partfileLocation);
				int partlen = partbytebuffer.length;
				int filechunklength = chunklength;

				if (partbytebuffer.length<=filechunklength ) {
				filechunklength = partbytebuffer.length;
											//In case file size is smaller than chunklength
				}

				i = 0;
				DecimalFormat numberFormat = new DecimalFormat("#.00");
				while (i<partlen) {
					myDevice.sendData(remote, Arrays.copyOfRange(partbytebuffer, i, i+filechunklength ));
					Double percent = (Double.valueOf(i)/Double.valueOf(partlen))*100;
					System.out.println("Sending part file: " + numberFormat.format(percent) + "%");
					i=i+filechunklength ;
					}
				System.out.println(
				partfileLocation.toString().substring(partfileLocation.toString().lastIndexOf(filetosend))
				+ " sent. \u2713");

										//SENDING PARTFOOTER
				String PARTFooter = partfileLocation.toString().substring(partfileLocation.toString().lastIndexOf(filetosend)) +"-----ZIPPART END-----";
				byte[] PARTFooterbytebuffer = PARTFooter.getBytes();
				i=0;
				while (i<PARTFooterbytebuffer.length) {
					myDevice.sendData(remote, Arrays.copyOfRange(PARTFooterbytebuffer, i, i+chunklength));
					i=i+chunklength;
					}

				currpart = currpart+1;
			}

		} catch (XBeeException e) {
			//myDevice.close();
            //System.exit(1);
			System.err.println("NODE CONNECTION LOST. Error transmitting message: " + e.getMessage());
			List<RemoteXBeeDevice> devices = listnodes(myDevice);
			boolean wasfound = false;

			if (myDevice.isOpen() == false) {
				myDevice.open();
				}

			int i = 0;
			System.out.println("Searching for device within network...");
			while (wasfound == false) {
				System.out.print("...");
				while (i<devices.size()) {
					if (devices.get(i).getNodeID().toString().equals(remote.getNodeID())) {
						//System.out.println("Node found, restarting transfer.");
						//Send next part here
						//Which was the last part sent?
						String lastpart = file2string(uploadfilesLocation+"/dir-"+filetosend+"/lastpartsent");
						lastpart = lastpart.replace("\\n", "");
						lastpart = lastpart.replace("\n", "");
						int lastpartsent = Integer.parseInt(lastpart);

						sendparts(myDevice, remote,lastpartsent,partstosend,filetosend,partfileLocation);
						//EXIT LOOP
						wasfound = true;
					} else {
						System.out.println("Searching for Node "+devices.get(i).getNodeID());
					}
					i=i+1;
				}
			}

		} finally {
            myDevice.close();
			//Delete folder containing the parts
			Process deleteparts = Runtime.getRuntime().exec(new String[]{
					"bash", "-c", "rm -rf "+partfileLocation.toString().substring(0,partfileLocation.toString().lastIndexOf("/"))+"; rm -rf "+uploadfilesLocation+"/"+filetosend+".aes" });
			deleteparts.waitFor();
            //close device
		}
	}

	public static List<RemoteXBeeDevice> listnodes(XBeeDevice myDevice) throws InterruptedException, IOException, ParseException {
		List<RemoteXBeeDevice> devices = null;
		if (myDevice.isOpen() == true) {
			myDevice.close();
		}

		try {
			myDevice.open();
			myDevice.addDataListener(listener);
			XBeeNetwork network = myDevice.getNetwork();

		// Start the discovery process and wait for it to be over.
			network.startDiscoveryProcess();
			while (network.isDiscoveryRunning()) {
				Thread.sleep(500);
			}

		//Get a list of the devices added to the network.

			devices = network.getDevices();
			int i = 0;
			while (i<devices.size()) {
				if (getcontact(devices.get(i).getNodeID())[0].equals(devices.get(i).getNodeID())) {
					System.out.println("Known NodeID found: "+devices.get(i).getNodeID());
				} else {
					System.out.println("Unknown NodeID found: "+devices.get(i).getNodeID());
				}
				i=i+1;
			}
		} catch (XBeeException e) {
			System.err.println("Error transmitting message: " + e.getMessage());
			myDevice.close();
            System.exit(1);
		} finally {
            myDevice.close();
            //close device
		}
		return devices;
	}

	public static void genkeys(int state, String nodeid) throws InterruptedException, org.json.simple.parser.ParseException {
		//generating keys for me (state=0) or using someone else's generator (state=1) ?
		if (state==0) {
			System.out.println("Calculating keys...\n");
			//create generator "gen.pem"
			try {
		        Process generator = Runtime.getRuntime().exec(new String[]{
			    "bash", "-c", "openssl genpkey -genparam -algorithm DH -out " +
			    		configfilesLocation.toString() + "/mygenerator.pem"});
		        generator.waitFor();
		        System.out.println("Generator key created.");
			} catch (IOException e1) {
		        	System.out.println("Error creating generator file.");
		        }
			//create private key from generator
			try {
		        Process privatekey = Runtime.getRuntime().exec(new String[]{
			    "bash", "-c", "openssl genpkey -paramfile  " +
			    		configfilesLocation.toString()+ "/mygenerator.pem -out " +
			    		configfilesLocation.toString() + "/myprivatekey.pem"});
		        privatekey.waitFor();
		        System.out.println("Private key generated.");
			} catch (IOException e1) {
		        	System.out.println("Error creating private key file.");
		        }
			//extract public key from private key
			try {
		        Process publickey = Runtime.getRuntime().exec(new String[]{
			    "bash", "-c", "openssl pkey -in "+
			    		configfilesLocation.toString()+"/myprivatekey.pem -pubout -out " +
			    		configfilesLocation.toString() + "/mypublickey.pem"});
		        publickey.waitFor();
		        System.out.println("Public key generated.");
			} catch (IOException e1) {
		        	System.out.println("Error creating public key file.");
		        }
		} else if (state==1) {
			//System.out.println("Calculating keys...\n");
			//write generator "contactgenerator.pem"
			try {
				String cleangen = getcontact(nodeid)[2];
				cleangen= cleangen.replace("\\n", "?");
				cleangen= cleangen.replace("\\", "");
				cleangen= cleangen.replace("?", "\\n");
		        Process generator = Runtime.getRuntime().exec(new String[]{
			    "bash", "-c", "echo '" + cleangen + "' > " + generatorsLocation.toString()+"/"+nodeid+"generator.pem;"
			    		+ " cat " + generatorsLocation.toString()+"/"+nodeid+"generator.pem | "
			    				+ "sed -i 's/\\\\n/\\'$'\\n''/g' " + generatorsLocation.toString()+"/"+nodeid+"generator.pem"});
		        generator.waitFor();
		        //System.out.println("Contact generator key written.");
			} catch (IOException e1) {
		        	System.out.println("Error creating contact generator file.");
		        }

			//write contact public key to file "contactpublickey.pem"
			try {
				String cleanpubkey = getcontact(nodeid)[3];
				cleanpubkey = cleanpubkey.replace("\\n", "?");
				cleanpubkey = cleanpubkey.replace("\\", "");
				cleanpubkey = cleanpubkey.replace("?", "\\n");
		        Process pubkeymake = Runtime.getRuntime().exec(new String[]{
		        		"bash", "-c", "echo '" + cleanpubkey + "' > " + publickeysLocation.toString()+"/"+nodeid+"publickey.pem; "
		        				+ "cat "+ publickeysLocation.toString()+"/"+nodeid+"publickey.pem | "
		        						+ "sed -i 's/\\\\n/\\'$'\\n''/g' "+ publickeysLocation.toString()+"/"+nodeid+"publickey.pem"});
		        pubkeymake.waitFor();
		        //System.out.println("Sending file...");
			} catch (IOException e1) {
		        	System.out.println("Error creating contact public key file.");
		        }

			//create private key from generator
			try {
		        Process privatekey = Runtime.getRuntime().exec(new String[]{
			    "bash", "-c", "openssl genpkey -paramfile "+
			    		generatorsLocation.toString()+"/"+nodeid+"generator.pem -out " +
			    		privatekeysLocation.toString()+"/"+nodeid+"myprivatekey.pem"});
		        privatekey.waitFor();
		        //System.out.println("My private key generated.");
			} catch (IOException e1) {
		        	System.out.println("Error creating private key file.");
		        }
			//extract public key from private key
			try {
		        Process publickey = Runtime.getRuntime().exec(new String[]{
			    "bash", "-c", "openssl pkey -in "+
			    		privatekeysLocation.toString()+"/"+nodeid+"myprivatekey.pem -pubout -out " +
			    		publickeysLocation.toString()+"/"+nodeid+"mypublickey.pem"});
		        publickey.waitFor();
		        //System.out.println("My public key generated.");
			} catch (IOException e1) {
		        	System.out.println("Error creating public key file.");
		        }
		}

	}

	public static void sendmyinfo(XBeeDevice myDevice, String uname, String myPublicKey, String myGen) {
		if (myDevice.isOpen() == true) {
			myDevice.close();
		}
		//Scanner scan = new Scanner(System.in);

        try {
        myDevice.open();
        myDevice.addDataListener(listener);
        System.out.println("\nLocal XBee: " + myDevice.getNodeID());
        System.out.println("\nSend contact information to (REMOTE_NODE_ID):");

        Scanner nodeid = new Scanner(System.in);
		String REMOTE_NODE_ID = nodeid.next();
		//nodeid.close();




        int chunklength = 255;

        if (myDevice.getNodeID().equals(REMOTE_NODE_ID)) {
            System.err.println("Error: the value of the REMOTE_NODE_ID constant must be "
                    + "the Node Identifier (NI) of the OTHER module.");
        } else {
            System.out.println("\nEstablishing connection with " + REMOTE_NODE_ID + "...");
            XBeeNetwork network = myDevice.getNetwork();
            RemoteXBeeDevice remote = network.discoverDevice(REMOTE_NODE_ID);

            if (remote != null) {
                System.out.println("Connection established.\n");


                int i = 0;

                //SENDING NODEID
                String mynodeid = "-----BEGIN NODEID-----"+myDevice.getNodeID()+"-----END NODEID-----";
                byte[] bytebuffer = mynodeid.getBytes();
                int len = bytebuffer.length;
                i = 0;
                    while (i<len) {
                        myDevice.sendData(remote, Arrays.copyOfRange(bytebuffer, i, i+chunklength));
                        i=i+chunklength;
                    }
                 System.out.println("NODEID sent.");
                 //sending uname
                 mynodeid= "-----BEGIN UNAME-----"+uname+"-----END UNAME-----";
                 bytebuffer = mynodeid.getBytes();
                 len = bytebuffer.length;
                 i = 0;
                   while (i<len) {
                            myDevice.sendData(remote, Arrays.copyOfRange(bytebuffer, i, i+chunklength));
                            i=i+chunklength;
                     }
                 System.out.println("UNAME sent.");

                 //sending generator key
                 bytebuffer = myGen.getBytes();
                 len = bytebuffer.length;
                 i = 0;
                        while (i<len) {
                            myDevice.sendData(remote, Arrays.copyOfRange(bytebuffer, i, i+chunklength));
                            i=i+chunklength;
                        }
                 System.out.println("Generator key sent.");

               //Sending public key
                bytebuffer = myPublicKey.getBytes();
                len = bytebuffer.length;
                i = 0;
                while (i<len) {
                        myDevice.sendData(remote, Arrays.copyOfRange(bytebuffer, i, i+chunklength));
                       i=i+chunklength;
                }
                System.out.println("Public key sent.");

                }
            }
        } catch (XBeeException e) {
            System.err.println("Error transmitting message: " + e.getMessage());
            myDevice.close();
            System.exit(1);
        } finally {
            myDevice.close();
            //scan.close();
            //close device
        }

	}

	//Other Methods

	public static void addcontact(String nodeid, String uname, String generator, String pubkey) throws IOException, org.json.simple.parser.ParseException {
	//row 0 = nodeid tempnodeid
	//row 1 = username tempuname
	//row 2 = generator tempgenerator
	//row 3 = publickey temppubkey
		HashMap<String,Object> contactDetails = new HashMap<String,Object>();
		contactDetails.put("uname", uname);
		contactDetails.put("generator", generator);
		contactDetails.put("pubkey", pubkey);
		JSONObject JSONcontactDetails = new JSONObject(contactDetails);

		HashMap<String,Object> contactID = new HashMap<String,Object>();
        contactID.put(nodeid, JSONcontactDetails);
        JSONObject JSONcontactID = new JSONObject(contactID);
        try (FileWriter file = new FileWriter(configfilesLocation.toString() + "/contacts.json")) {
            String jsonstring = JSONcontactID.toJSONString();
            jsonstring = jsonstring.replace("-----END PUBLIC KEY-----\\n", "-----END PUBLIC KEY-----");
            jsonstring = jsonstring.replace("-----END DH PARAMETERS-----\\n", "-----END DH PARAMETERS-----");
            jsonstring = jsonstring.replace("\\u0000", "");
        	file.write(jsonstring);
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

	public static boolean stringContainsNumber(String s)
	{
	    return Pattern.compile( "[0-9]" ).matcher( s ).find();
	}

    public static String file2string(String filePath) throws IOException {
        File source = new File(filePath);
        final DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(source)));
        final byte[] buffer = new byte[(int) source.length()];
        dis.readFully(buffer);
        dis.close();
        return new String(buffer);
    }

	public static Boolean fileexists(String filePath) throws IOException {
		try {
		String fileexists = null;
		//Check if file exists:
		Process checkfile = Runtime.getRuntime().exec(new String[]{
		"bash", "-c", "FILE="+filePath+"; if [ -f \"$FILE\" ]; then echo \"1\" ; else echo \"0\" ; fi > " + configfilesLocation.toString() + "/fileexists"});
		checkfile.waitFor();
        //Read "fileexists" file and use input to decide whether it exists or not:
		fileexists = file2string(configfilesLocation.toString() + "/fileexists");

		Process deletefile = Runtime.getRuntime().exec(new String[]{
				"bash", "-c", "rm -rf " + configfilesLocation.toString() + "/fileexists"});
		deletefile.waitFor();


		if (fileexists.contains("0")) {
			// Return false, file doesnt exist
			return false;
			} else {
				return true;}
		} catch (Exception err) {
		      err.printStackTrace();
		      }
		return true;
		}

	@SuppressWarnings("unchecked")
	public static String[] getcontact(String nodeid) throws IOException, org.json.simple.parser.ParseException {
		String[] finalcontact = new String[4];
		try {
			JSONObject obj=(JSONObject)JSONValue.parse(new FileReader(configfilesLocation.toString()+"/contacts.json"));
			JSONArray array = new JSONArray();
			array.add(obj);
			int i = 0;
			while (i<=array.size()-1) {
				String currstring = array.get(i).toString();
				if (currstring.contains(nodeid)) {
					finalcontact[0]= nodeid;
					finalcontact[1]= currstring.substring(currstring.lastIndexOf("uname\":\"")+8,currstring.lastIndexOf("\",\"generator"));
					finalcontact[2]= currstring.substring(currstring.lastIndexOf("generator")+12,currstring.lastIndexOf("\",\"pubkey"));
					finalcontact[3]= currstring.substring(currstring.lastIndexOf("pubkey")+9,currstring.lastIndexOf("END PUBLIC KEY-----")+19);
				} else {
					finalcontact[0]= "";
					finalcontact[1]= "";
					finalcontact[2]= "";
					finalcontact[3]= "";
				}
				i = i +1;
			}
		} catch (Exception e) {
			//e.printStackTrace();
			finalcontact[0]= "";
			finalcontact[1]= "";
			finalcontact[2]= "";
			finalcontact[3]= "";
			return finalcontact;
		}
		return finalcontact;
	}


	/**
	 * Generate a local key based on the other party's public key and own private key
	 * @throws NoSuchAlgorithmException
	 * @throws GeneralSecurityException
	 * @throws GeneralSecurityException
	 */

     /**
	 * Class to manage the received data.
	 */
	private static class DataReceiveListener implements IDataReceiveListener {
		//row 0 = nodeid tempnodeid
		//row 1 = username tempuname
		//row 2 = generator tempgenerator
		//row 3 = publickey temppubkey

		String sharedsecret = null;
		String temppubkey = null;
		String tempgenerator = "";
		String tempnodeid = "";
		String tempuname = "";
		String[][] tempcontact = new String[4][1];



		//STEAMCIPHER - to be used for live feeds with higher bitrate devices.
		//boolean streamSignal = false;
		//ArrayList<byte[]> tempstreamcipherarray = new ArrayList<byte[]>();
		//data array
		//key array of same length as data array


		//AESFILE
		boolean AESSignal = false;
		String AESFilename = "receivedmessage";

		//ZIPFILE
		boolean ZIPPARTSignal = false;

		String ZIPFilename = "";
		int fileparts = 0;
		int numfiles = 0;

		int linecounter = 0;

		//CREATE ARRAYLIST FOR RECEIVED MESSAGE
		ArrayList<String> messages = new ArrayList<String>();

		public void dataReceived(XBeeMessage xbeeMessage) {

			// ADD RECEIVED LINE TO ARRAYLIST
			messages.add(new String(xbeeMessage.getData()));
			connected = true;
			delayDetected = false;
			//CATCHING DIFFERENT OPERATIONS*******************
			try {
				if (messages.size() > 0) {

					// AES FILE CATCH
						if (messages.get(linecounter).contains("CHAT BEGIN")) {
							AESSignal = true;

						} else if  (messages.get(linecounter).contains("ZIPFILE BEGIN")) {
							ZIPFILESignal  = true;
							System.out.println("Receiving file...");

						} else if  (messages.get(linecounter).contains("ZIPPART BEGIN")) {
							ZIPPARTSignal  = true;

						} else if (messages.get(linecounter).contains("CALL BEGIN")) {
							//streamSignal  = true;
							System.out.println("CALL HAS BEGUN.");

					//	CATCHING NEW CONTACT********************
					//	NODEID CATCH
						} else if (messages.get(linecounter).contains("-----BEGIN NODEID-----") && messages.get(linecounter).contains("-----END NODEID-----") && (AESSignal ==false)) {
							tempnodeid = messages.get(linecounter).toString();
							tempnodeid = tempnodeid.replace("-----BEGIN NODEID-----", "");
							tempnodeid = tempnodeid.replace("-----END NODEID-----", "");
							System.out.println(tempnodeid);
							tempcontact[0][0] = tempnodeid;

							//UNAME CATCH
						} else if (messages.get(linecounter).contains("-----BEGIN UNAME-----") && messages.get(linecounter).contains("-----END UNAME-----")&& (AESSignal ==false)) {
							tempuname = messages.get(linecounter).toString();
							tempuname = tempuname.replace("-----BEGIN UNAME-----", "");
							tempuname = tempuname.replace("-----END UNAME-----", "");
							System.out.println(tempuname);
							tempcontact[1][0] = tempuname;

					// GENERATOR KEY CATCH
						} else if (messages.get(linecounter).contains("-----BEGIN DH PARAMETERS-----") && messages.get(linecounter).contains("-----END DH PARAMETERS-----")&& (AESSignal ==false)) {
							tempgenerator = messages.get(linecounter).toString();
							System.out.println(tempgenerator);
							tempcontact[2][0] = tempgenerator;
					// 	PUBLIC KEY CATCH
						} else if (messages.get(linecounter).toString().contains("-----END PUBLIC KEY-----") && messages.get(linecounter-1).toString().contains("-----BEGIN PUBLIC KEY-----")) {
							temppubkey = (
									messages.get(linecounter-1).toString()+
									messages.get(linecounter).toString()
									);
							temppubkey = (temppubkey.substring(0, temppubkey.lastIndexOf("-")+1));
							tempcontact[3][0] = temppubkey;
							//System.out.println("RECEIVED PUBKEY: "+temppubkey);

						}

						//************CHAT***********************************
						if (AESSignal == true) {
							if (temppubkey!=null) {
								try {
									String cleanpubkey = temppubkey.substring(temppubkey.indexOf("-----BEGIN PUBLIC KEY-----"));
									cleanpubkey = cleanpubkey.replace("\\n", "?");
									cleanpubkey = cleanpubkey.replace("\\", "");
									cleanpubkey = cleanpubkey.replace("?", "\\n");
									Process pubkeymake = Runtime.getRuntime().exec(new String[]{
						        		"bash", "-c", "echo '" + cleanpubkey + "' > " +configfilesLocation.toString() + "/contactpublickey.pem;"
						        				+ "cat "+ configfilesLocation.toString() + "/contactpublickey.pem  | "
						        						+ "sed -i 's/\\\\n/\\'$'\\n''/g' "+ configfilesLocation.toString() + "/contactpublickey.pem"});
									pubkeymake.waitFor();
									//System.out.println("Contact public key written.");
									temppubkey = null;
							} catch (IOException e1) {
						        	System.out.println("Error creating contact public key file.");
							} catch (InterruptedException e) {
						        	// TODO Auto-generated catch block
						        	e.printStackTrace();

							}
								//Getting shared secret
								Process sharedsecretfile = null;
								try {
									sharedsecretfile = Runtime.getRuntime().exec(new String[]{
											"bash", "-c", "openssl pkeyutl -derive -inkey "+configfilesLocation.toString()+"/"+"myprivatekey.pem -peerkey "+configfilesLocation.toString() + "/contactpublickey.pem | openssl sha3-256 | sed -e 's|(stdin)= ||' > "+System.getProperty("user.dir") + "/sharedsecret"});
									sharedsecretfile.waitFor();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

								sharedsecret = file2string(System.getProperty("user.dir") + "/sharedsecret");
								//System.out.println("SHARED SECRET:"+sharedsecret);
								//Delete sharedsecret file
								Process deletesharedsecretfile = null;
								try {
									deletesharedsecretfile = Runtime.getRuntime().exec(new String[]{
											"bash", "-c", "rm -rf "+System.getProperty("user.dir") + "/sharedsecret"});
									deletesharedsecretfile.waitFor();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							} // end pubkey!=null


								if (
										!(messages.get(linecounter).contains("CHAT BEGIN")) &&
										!(messages.get(linecounter-1).contains("BEGIN PUBLIC KEY")) &&
										!(messages.get(linecounter).contains("CHAT END")) &&
										!(messages.get(linecounter).contains("pubkey")) &&
										!(messages.get(linecounter).contains("-----")) &&
										!(xbeeMessage.getDataString().contains("KEY"))
										) {// 2. If this is not the case, it is data worth saving. Add the bytes to the tempfilearray
									tempfilearray.add(xbeeMessage.getData());}

								if (messages.get(linecounter).contains("CHAT END")) {

									//System.out.println("Receiving file...");

	//								Convert arraylist to byte array
									int i =0;

									byte[][] tempfile = new byte[tempfilearray.size()][0];
									while (i<tempfile.length) {
										tempfile[i] = tempfilearray.get(i);
										i=i+1;
									}

										//If file exists, overwrite old file, TODO maybe ask for user input?
									if (fileexists(downloadfilesLocation + "/msg") == true) {
										try {
											Process remold = Runtime.getRuntime().exec(new String[]{
													"bash", "-c", "rm -rf "+ downloadfilesLocation + "/msg"});
											remold.waitFor();
										} catch (IOException | InterruptedException e1) {
											System.out.println("Error removing old file.");
										}
									}
									if (fileexists(downloadfilesLocation + "/msg.aes") == true) {
										try {
											Process remold = Runtime.getRuntime().exec(new String[]{
													"bash", "-c", "rm -rf "+ downloadfilesLocation + "/msg.aes"});
											remold.waitFor();
										} catch (IOException | InterruptedException e1) {
											System.out.println("Error removing old file.");
										}
									}

									//Make new, empty file for the new data received.
									try {
										Process mkfile = Runtime.getRuntime().exec(new String[]{
												"bash", "-c", "touch " + downloadfilesLocation + "/msg"});
										mkfile.waitFor();

										//write byte array to file
										try (FileOutputStream stream = new FileOutputStream(downloadfilesLocation + "/msg.aes")) {
											for (i = 0; i < tempfile.length; i++) {
												for (int j = 0; j < tempfile[i].length; j++) {
													stream.write(tempfile[i][j]);
												}
											}
										}

									} catch (IOException | InterruptedException e1) {
										//	System.out.println("Error removing old file.");
									}

									try {
										Process remzeros = Runtime.getRuntime().exec(new String[]{
												"bash", "-c", "sed '$ s/\\x00*$//' "+downloadfilesLocation + "/msg.aes > "
														+downloadfilesLocation + "/msg.aes.stripped ; rm -rf "
														+downloadfilesLocation + "/msg.aes; mv "
														+downloadfilesLocation + "/msg.aes.stripped "
														+downloadfilesLocation + "/msg.aes"});
										remzeros.waitFor();

									} catch (IOException | InterruptedException e1) {
										System.out.println("Error removing zeros.");
									}

									//	CLEAR tempfilearray for next message
									tempfilearray.removeAll(tempfilearray);
									tempfile = null;

									//System.out.println("AESFILENAME: "+AESFilename);
									//	Decrypt file with received public key and my private key
	//								DECRYPT AESCrypt file
									AESCrypt decrypter = null;
									try {
										decrypter = new AESCrypt(sharedsecret);
									} catch (GeneralSecurityException | IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									//System.out.println(sharedsecret);
									//Converting aes file to original with shared sha3-256 secret
									try {
										decrypter.decrypt(
												downloadfilesLocation + "/msg.aes",
												downloadfilesLocation + "/msg");
									} catch (IOException | GeneralSecurityException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}

									System.out.println(xbeeMessage.getDevice().getNodeID()+": "+file2string(downloadfilesLocation + "/msg"));

									//apend message to log file
									Process appendtologfile = Runtime.getRuntime().exec(new String[]{
											"bash", "-c", "echo '"+xbeeMessage.getDevice().getNodeID()+": "+file2string(downloadfilesLocation + "/msg")+"' >> "+configfilesLocation +"/"+xbeeMessage.getDevice().getNodeID()});
									appendtologfile .waitFor();



									//System.out.println("END REACHED");
									AESSignal  = false;
								}


						} //end CHAT


						//************ZIP FILES*******************************
						if (ZIPFILESignal == true) {
							//ZIPFILE SIGNAL IS TRUE
							if (temppubkey!=null) {
							try {
								String cleanpubkey = temppubkey.substring(temppubkey.indexOf("-----BEGIN PUBLIC KEY-----"));
								cleanpubkey = cleanpubkey.replace("\\n", "?");
								cleanpubkey = cleanpubkey.replace("\\", "");
								cleanpubkey = cleanpubkey.replace("?", "\\n");
						        Process pubkeymake = Runtime.getRuntime().exec(new String[]{
						        		"bash", "-c", "echo '" + cleanpubkey + "' > " +configfilesLocation.toString() + "/contactpublickey.pem;"
						        				+ "cat "+ configfilesLocation.toString() + "/contactpublickey.pem  | "
						        						+ "sed -i 's/\\\\n/\\'$'\\n''/g' "+ configfilesLocation.toString() + "/contactpublickey.pem"});
						        pubkeymake.waitFor();
						        //System.out.println("Contact public key written.");
						        temppubkey = null;
							} catch (IOException e1) {
						        	System.out.println("Error creating contact public key file.");
						        } catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();

							}


							//Getting shared secret
							Process sharedsecretfile = null;
							try {
								sharedsecretfile = Runtime.getRuntime().exec(new String[]{
										"bash", "-c", "openssl pkeyutl -derive -inkey "+configfilesLocation.toString()+"/"+"myprivatekey.pem -peerkey "+configfilesLocation.toString() + "/contactpublickey.pem | openssl sha3-256 | sed -e 's|(stdin)= ||' > "+System.getProperty("user.dir") + "/sharedsecret"});
								sharedsecretfile.waitFor();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							sharedsecret = file2string(System.getProperty("user.dir") + "/sharedsecret");

							//Delete sharedsecret file
							Process deletesharedsecretfile = null;
							try {
								deletesharedsecretfile = Runtime.getRuntime().exec(new String[]{
										"bash", "-c", "rm -rf "+System.getProperty("user.dir") + "/sharedsecret"});
								deletesharedsecretfile.waitFor();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}


							}

							if (xbeeMessage.getDataString().contains("parts:")) {
								fileparts = Integer.parseInt( xbeeMessage.getDataString().substring(
										(xbeeMessage.getDataString().indexOf("parts:")+6),
										(xbeeMessage.getDataString().indexOf("*")) ));
							}

							if (ZIPPARTSignal  == true) {
								//0. Check if there is longer than 5 seconds delay. If so, throw bytes from this part.

								if (delayDetected == true) {
									ZIPPARTSignal = false;
								}

								//1. ZIPPart detected. Exclude all incoming messages with the following pattern:
								if (
										!(messages.get(linecounter).contains("ZIPPART BEGIN")) &&
										!(messages.get(linecounter-1).contains("BEGIN PUBLIC KEY")) &&
										!(messages.get(linecounter).contains("ZIPPART END")) &&
										!(messages.get(linecounter).contains("pubkey")) &&
										!(messages.get(linecounter).contains("-----")) &&
										!(messages.get(linecounter).contains(" parts: ")) &&
										!(xbeeMessage.getDataString().contains("KEY")) &&
										(delayDetected == false)
										) {// 2. If this is not the case, it is data worth saving. Add the bytes to the tempfilearray
									tempfilearray.add(xbeeMessage.getData());}


								//3. If message contains end ZIPPart signal, write the tempfilearray bytes to a file.
								if (messages.get(linecounter).contains("ZIPPART END")) {
									ZIPPARTSignal = false;

									//If ZIPPART End signal contains a filename, use that filename for the output file.
									if ((messages.get(linecounter).lastIndexOf("-----ZIPPART END-----"))>=0) {
										ZIPFilename = messages.get(linecounter).substring(0,messages.get(linecounter).lastIndexOf("-----ZIPPART END-----"));
										ZIPFilename = ZIPFilename.replace("\0", "");
									}

									//	Convert arraylist to byte array
									int i =0;

									byte[][] tempfile = new byte[tempfilearray.size()][0];
									while (i<tempfile.length) {
										tempfile[i] = tempfilearray.get(i);
										i=i+1;
									}

										//If file exists, overwrite old file, TODO maybe ask for user input?
									if (fileexists(downloadfilesLocation + "/"+ZIPFilename.substring(0,ZIPFilename.lastIndexOf(".aes.7z.")+8)+"/"+ZIPFilename) == true) {
										try {
											Process remold = Runtime.getRuntime().exec(new String[]{
													"bash", "-c", "rm -rf "+ downloadfilesLocation + "/"+ZIPFilename.substring(0,ZIPFilename.lastIndexOf(".aes.7z.")+8)+"/"+ZIPFilename});
											remold.waitFor();
										} catch (IOException | InterruptedException e1) {
											System.out.println("Error removing old file.");
										}
									}

									//Make new, empty file for the new data received.
									try {
										Process mkfile = Runtime.getRuntime().exec(new String[]{
												"bash", "-c", "mkdir "+downloadfilesLocation + "/"+ZIPFilename.substring(0,ZIPFilename.lastIndexOf(".aes.7z.")+8)+"/"+";touch " + downloadfilesLocation + "/"+ZIPFilename.substring(0,ZIPFilename.lastIndexOf(".aes.7z.")+8)+"/"+ZIPFilename});
										mkfile.waitFor();

										//write byte array to file
										try (FileOutputStream stream = new FileOutputStream(downloadfilesLocation + "/"+ZIPFilename.substring(0,ZIPFilename.lastIndexOf(".aes.7z.")+8)+"/"+ZIPFilename)) {
											for (i = 0; i < tempfile.length; i++) {
												for (int j = 0; j < tempfile[i].length; j++) {
													stream.write(tempfile[i][j]);
												}
											}
										}

									} catch (IOException | InterruptedException e1) {
										//	System.out.println("Error removing old file.");
									}

									//	CLEAR tempfilearray for next file
									tempfilearray.removeAll(tempfilearray);
									tempfile = null;
									try {
										Process countfiles = Runtime.getRuntime().exec(new String[]{
												//ls -1a test.jpeg.aes.7z./ | wc -l
												"bash", "-c", "ls -1a "+downloadfilesLocation+"/"+ZIPFilename.substring(0,ZIPFilename.lastIndexOf(".aes.7z.")+8)+"/ | wc -l > "+ configfilesLocation+"/filecount"});
										countfiles.waitFor();

									} catch (IOException | InterruptedException e1) {
										System.out.println("Error counting files.");
									}

									String filecountstring = null;
									try {
										filecountstring = file2string(configfilesLocation.toString()+"/filecount");
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									filecountstring = filecountstring.replace("\\0", "");
									filecountstring = filecountstring.replace("\\n", "");
									filecountstring = filecountstring.replace("\n", "");

									//Remove trailing zero bytes from the last ZIPFile received, remove end zero bytes and then add 1 zero byte.


									//System.out.println("*******************REMOVING ZEROS*******************.");
									try {
										Process remzeros = Runtime.getRuntime().exec(new String[]{
												"bash", "-c", "sed '$ s/\\x00*$//' "+downloadfilesLocation + "/"+ZIPFilename.substring(0,ZIPFilename.lastIndexOf(".aes.7z.")+8)+"/"+ZIPFilename+" > "
														+downloadfilesLocation + "/"+ZIPFilename.substring(0,ZIPFilename.lastIndexOf(".aes.7z.")+8)+"/"+ZIPFilename+".stripped ; rm -rf "
														+downloadfilesLocation + "/"+ZIPFilename.substring(0,ZIPFilename.lastIndexOf(".aes.7z.")+8)+"/"+ZIPFilename+"; mv "
														+downloadfilesLocation + "/"+ZIPFilename.substring(0,ZIPFilename.lastIndexOf(".aes.7z.")+8)+"/"+ZIPFilename+".stripped "
														+downloadfilesLocation + "/"+ZIPFilename.substring(0,ZIPFilename.lastIndexOf(".aes.7z.")+8)+"/"+ZIPFilename});

										remzeros.waitFor();
										//System.out.println("End zeros removed.");

									} catch (IOException | InterruptedException e1) {
										System.out.println("Error removing zeros.");
									}
									//System.out.println("*******************END REMOVING ZEROS*******************.");

									filecountstring = filecountstring.replace("\n", "");
									numfiles = Integer.parseInt(filecountstring) -2 ;
									System.out.println("Part file received: "+ Integer.toString(numfiles)+"/"+ Integer.toString(fileparts) );

									if ((numfiles == fileparts) == true) {
											//System.out.println("*******************START ADDING ZEROS*******************.");
											//	IF FILE IS THE LAST FILE (NEEDS 0 BYTE AT THE END)
												try {
													Process addzeros = Runtime.getRuntime().exec(new String[]{ "bash", "-c", "truncate -s +2 "+downloadfilesLocation + "/"+ZIPFilename.substring(0,ZIPFilename.lastIndexOf(".aes.7z.")+8)+"/"+ZIPFilename});
													addzeros.waitFor();
													//System.out.println("End zeros added.");
												} catch (IOException | InterruptedException e1) {
													System.out.println("Error adding zeros.");
												}
											//System.out.println("*******************STOP ADDING ZEROS*******************.");

											ZIPFilename = ZIPFilename.substring(0,ZIPFilename.lastIndexOf(".aes.7z.")+8)+"001";
											//System.out.println("THE ZIP FILENAME IS:"+ZIPFilename);
											try {
												Process unzip = Runtime.getRuntime().exec(new String[]{
														"bash", "-c", "cd "+ downloadfilesLocation + "/"+ZIPFilename.substring(0,ZIPFilename.lastIndexOf(".aes.7z.")+8)+"/;"+
														"7z x "+ZIPFilename});
												unzip.waitFor();
												//System.out.println("UNZIP SUCCESS.");
											} catch (IOException | InterruptedException e1) {
												System.out.println("UNZIP ERROR.");
											}
											//

											AESFilename = ZIPFilename.substring(0,ZIPFilename.lastIndexOf(".7z."));
											//System.out.println("AESFILENAME: "+AESFilename);
											//	Decrypt file with received public key and my private key
//											DECRYPT AESCrypt file
											AESCrypt decrypter = null;
											try {
												decrypter = new AESCrypt(sharedsecret);
											} catch (GeneralSecurityException | IOException e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											}
											//System.out.println(sharedsecret);

											//Converting aes file to original with shared sha3-256 secret
											System.out.println("File received: "+AESFilename.substring(0,AESFilename.length()-4));
											try {
												decrypter.decrypt(
														downloadfilesLocation + "/"+ZIPFilename.substring(0,ZIPFilename.lastIndexOf(".aes.7z.")+8)+"/"+AESFilename,
														downloadfilesLocation + "/"+AESFilename.substring(0,AESFilename.length()-4));
											} catch (IOException | GeneralSecurityException e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											}

											//apend message to log file
											Process appendtologfile = Runtime.getRuntime().exec(new String[]{
													"bash", "-c", "echo '"+xbeeMessage.getDevice().getNodeID()+": "+("File sent: "+AESFilename.substring(0,AESFilename.length()-4))+"' >> "+configfilesLocation +"/"+xbeeMessage.getDevice().getNodeID()});
											appendtologfile .waitFor();

											ZIPFILESignal = false;
									}
								}
							//We are still on ZIPFILESignal, all the zips have been received.
							//if current files == total parts, unzip and decrypt final file
							//Count 7z received files in folder
							}
						}
						if (messages.get(linecounter).contains("-----ZIPFILE END-----")) {

							//System.out.println("END OF FILE.");
							Process deleteparts = Runtime.getRuntime().exec(new String[]{
									"bash", "-c", "rm -rf "+downloadfilesLocation + "/"+ZIPFilename.substring(0,ZIPFilename.lastIndexOf(".aes.7z.")+8)});
							deleteparts.waitFor();
							connected = false;

						}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				linecounter += 1;
				prevmsgtimestamp = secondspassed;
				//if (AESSignal == false) {
					//if ( !(xbeeMessage.getDataString().contains("PUBLIC KEY-----")) && !(xbeeMessage.getDataString().contains("-----AES END-----")) ) {
						//System.out.println(linecounter);
						//System.out.println("*START************************************************************");
						//System.out.println(xbeeMessage.getDataString());
						//System.out.println("*END************************************************************");
					//}
				//}

				if (!(tempcontact[0][0].isBlank() && (tempcontact[1][0].isBlank()) && (tempcontact[2][0].isBlank()) && (tempcontact[3][0].isBlank()))) {
					try {
						addcontact(tempcontact[0][0],tempcontact[1][0],tempcontact[2][0],tempcontact[3][0]);
					} catch (IOException e) {
					// 	TODO Auto-generated catch block
						e.printStackTrace();
					} catch (org.json.simple.parser.ParseException e) {
					// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}

		}

	}

}
