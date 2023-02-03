# Android Example JME Project

I created this project to help anyone trying to get a JME game running on Android. 
It can be considered a 'starter' project, written by an Android dummy.
It arose while I was trying to isolate a bug in Minie with @sgold.

The android project contains 2 models: ninja and puppet.
Ninja raises his sword and puppet tries to grab it...

# Windows
.\gradlew :desktop:DesktopLauncher runs it on windows
(Or run DesktopLauncher in an SDK)
Return steps through Ninja and Puppets animations
Space bar starts ninja and puppet colliding in Ragdoll Mode.

# Android
To run on android, open the project in AndroidStudio,
or JetBrains Intellij. When all is sync'ed, launch 'app'.
Use any device which supports Android API 31 or higher.
There are 3 buttons: 
Kinematic steps through Ninja and Puppets animations.
Ragdoll puts the models into Ragdoll Mode.
Quit button has an icon.
Lemur is used for the buttons.

# Minie
The excellent Minie project was used to put the models into ragdoll mode.
The mocels have 'standard' Minie controllers based on DynamicAnimControl.
I used Minie DACWizard to create them.
There is also a ghost controller whose collision shapes match the puppet controller
The ghost collision objects are positioned in simpleUpdate to match the puppet objects
ninja and puppet RigidBodyControl's are in different groups and do not collide
puppet PhysicsGhostObjects collide with ninja RigidBodyControls
Each collision results in a call to collision(PhysicsCollisionEvent event).
Impulses are applied to keep the colliding object apart.

# Problems
1. On Android, the game hangs for two seconds on the first tap of the screen.
2. The Splash screen appears about a second before the game starts. 
	Is it supposed to arrive so late?

# Credits
I am not an Android expert. This Example Project comes from me stumbling around 
in the dark. Most of the Android code I hacked out of the Ialon project by @vxel.
That's a great project for reference on GitHub, but was very advanced for me.
SuperiorPlugin by @Pavl_G also helped, but I could never get the project fully running.

# Future
It would be nice if any of the Android/JME experts could review the project.
Did the way do the Buttons cause the 2 second hang on first tap?
Or am I using Lemur wrong?
It does seem to work, but maybe there's a better way?
Any comments would be appreciated!
