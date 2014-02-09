Armory Companion
===============

Armory Companion is an android application to enable signing of transactions with an offline Armory instance without using USB keys (and therefore keeping your offline wallet isolated completely from removable media and networks).  I created this when I realized that to use my offline wallet, I'd have to constantly plug USB keys into it.  This is a well-documented security risk:

http://news.cnet.com/8301-1009_3-10104496-83.html
http://community.sophos.com/t5/Sophos-EndUser-Protection/USB-Virus-Worm-in-both-Windows-7-and-Linux/td-p/16807

Even if you use Linux for your offline wallet, you should not assume that the developers of Ubuntu or whatever OS you use haven't put in some sort of autorun functionality.  If you use a Mac or especially Windows for your offline wallet, this app is a MUST-HAVE, but you're on your own with regard to my offline wallet code (it's Linux-only).

The process is fairly straightforward when combined with some python scripts I wrote which go on the offline wallet:

* Generate a transaction in Armory for your offline wallet (use the official Armory docs to get to this point)
* Create a QR code from it (it's not sensitive material, so you can use anything for this)
* Scan the QR code with your phone using a scanner app that supports showing the code again in history (eg. QR Droid)
* Go to your cold wallet machine and boot it up
* On the cold wallet machine, run the scanQr script.  zbarcam will run and show the video window.
* Hold your phone up to the cold wallet with the transaction QR displayed.  It will eventually scan and write a file to the Desktop.
* Sign the transaction using Armory (read the docs to do this).  This will overwrite the unsigned transaction file with a signed transaction file.
* Drag the signed transaction file onto the Display QR launcher.  This will dump transaction information and display a QR code in the terminal
* Scan the QR code with this app (Armory Companion)
* The details of the transaction will be shown - verify they look correct
* Tap the Broadcast button to broadcast the transaction on the bitcoin network directly from your phone.

Once this process is complete, the signed transaction will be broadcast to other peers on the bitcoin network, and you can verify it on Blockchain, for instance, within minutes.

This app is working, but needs some cleanup especially in the UI department.

USE AT YOUR OWN RISK.  SEE LICENSE FOR COPYRIGHT AND LIABILITY DETAILS.
