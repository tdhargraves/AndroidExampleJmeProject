# Android Example JME Project

I created this project to help anyone trying to get a JME game running on Android. 
It can be considered a 'starter' project, written by an Android dummy.
It arose while I was trying to isolate a bug in Minie with @sgold.

The android project contains 2 animated models: ninja and puppet.
In Ragdoll mode, Ninja raises his sword, and puppet tries to grab it.

# Windows
.\gradlew :desktop:DesktopLauncher runs it on windows.
(Or run DesktopLauncher in an SDK).
Return steps through Ninja and Puppets animations.
Space bar starts ninja and puppet colliding in Ragdoll Mode.
ESC key quits.

# Android
To run on android, open the project in AndroidStudio, or JetBrains Intellij. (They both seem the same to me, but Intellij also happily runs the DesktopLauncher). When all is sync'ed, launch 'app'.
Use any emulated device (or a real device) which supports Android API 31 or higher.
When the app is running Puppet and Ninja appear performing their first animations.
There are 3 buttons: 
Kinematic steps through Ninja and Puppets animations.
Ragdoll puts the models into Ragdoll Mode.
Quit button quits, but it also has an example icon.
Lemur is used for the buttons.

# Minie
The excellent Minie project was used to put the models into ragdoll mode.
The models have 'standard' Minie controllers based on DynamicAnimControl (DAC).
I used Minie DACWizard to create them. For Nubes there's a lot to learn about Minie and bullet and DAC.
To get 'fine control' over collisions, I used a ghost controller, whose collision shapes match the puppet controller.
The ghost collision objects are positioned in simpleUpdate to match the puppet collision objects.
Ninja and Puppet RigidBodyControl's are in different groups and do not collide.
Puppet PhysicsGhostObjects collide with Ninja RigidBodyControls.
Each collision results in a call to collision(PhysicsCollisionEvent event).
Here, impulses are applied to keep the colliding objects apart. (NB. If ragdolls are allowed to collide with each other at speed, I found they tended to either speed off uncontrollably in opposite directions, or explode, or both!)

# Problems
The Splash screen 'sometimes' appears about a second before the game starts.<br/>
Is it supposed to arrive this late? I added no code to display the Splash Screen, so this is default Android behaviour.

# Credits
I am not an Android expert. This Example Project comes from me stumbling around 
in the dark. Most of the Android code I hacked out of the Ialon project by @vxel.
That's a great project for reference, and without it I'd still be stumbling around. It's freely available on GitHub, but it was very advanced for an Android nube like me.
SuperiorPlugin by @Pavl_G also helped, but I could never get the project fully running. It did have a lot of code to do with the Splash Screen, so maybe that would fix my problem, but I could not get it working. Many thanks to @sgold for his help with using his Minie Project.

# Future
I'll maintain this project periodically when the SDK's, gradle, dependencies etc. get updated. It would be nice to have a relatively simple JME Android Project that works 'out of the box'.
Also, it would be nice if any of the Android/JME experts could review this project.<br/>
Did you like the way I did the lemur buttons?
It does seem to work, but maybe there's a better way? (By better, I mean simpler :-) <br/>
Any comments would be appreciated!
@tharg2


