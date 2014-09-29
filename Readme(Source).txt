Okay, I've implemented customizable keys. Here's a basic how to on make this work. Keys are binded/defined on the Config.xml file, under the input tag. You can bind two keys for each action by the use of setting device, button, altdevice and altbutton.

There are four devices possible for the key customization

  · "none"
  · "keyboard"
  · "mouse"
  · "gamepad"

The buttons for keyboard are pretty much what they're named
  · "w"
  · "a"
  · "left arrow"
  · "up arrow"
  · etc...

The buttons for mouse are
  · "x-"
  · "x+"
  · "y-"
  · "y+"
  · "wheel-"
  · "wheel+"
  · "left"
  · "right"
  · "middle"

The buttons for gamepads are
  · "x-"
  · "x+"
  · "y-"
  · "y+"
  · "z-"
  · "z+"
  · "u-"
  · "u+"
  · "v-"
  · "v+"
  · "yaw-"
  · "yaw+"
  · "pitch-"
  · "pitch+"
  · "roll-"
  · "roll+"
  · "button1"
  · "button2"
  · "button3"
  · "button4"
  · "button5"
  · "button6"
  · "button7"
  · "button8"
  · "button9"
  · "button10"
  · "button11"
  · "button12"
  · "button13"
  · "button14"
  · "button15"
  · "button16"

Also, there are are some other key values you should look at.

<input mouse_speed = "100" mouse_sensitivy = "1" gamepad="0" gamepad_threshold="0.4"> 

 · mouse_speed, wich determines the speed of the mouse (lower or higher it depeneding on how fast your mouse to be)
 · mouse_sensitivy, pretty much nuff said
 · gamepad, wich selects the gamepad device. commonly 0 is good
 · gamepad_threshold, wich determines how much you should move the analog joystick until it's being detected.

<control mode="1"/>

  · control mode determines how controls are applied to the game. 0 is for mouse look, 1 for game pad (and enables SA-ish style control) and 2 for SRB2 default mode.

<camera smoothness="0.6" targetpov="1" rotation_x="170" rotation_y="0" rotation_speed_x="2" rotation_speed_y="2"/>

  · smoothness determines the interpolation between rotation and position values of the camera
  · targetpov enables the vomit mode
  · rotation_x and rotation_y determines the initial rotation values for the camera
  · rotation_speed_x and rotation_speed_y determine the rotation speed. set these high when using mouselook and low when using the other modes.