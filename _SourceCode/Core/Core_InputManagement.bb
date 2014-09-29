; ------------------------------------------------------------------------
; BlitzSonic Engine -- Classic Sonic the Hedgehog engine for Blitz 3D
; version 0.1, February 7th, 2008
;
; Copyright (C) 2008 - BlitzSonic Team.
; ------------------------------------------------------------------------
;
; This software is provided 'as-is', without any express or implied
; warranty.  In no event will the authors be held liable for any damages
; arising from the use of this software.
; 
; Permission is granted to anyone to use this software for any purpose
; (except for commercial applications) and to alter it and redistribute
; it freely subject to the following restrictions:
;
; 1. The origin of this software must not be misrepresented; you must not
;    claim that you wrote the original software. If you use this software
;    in a product, an acknowledgment in the product itself as a splash
;    screen is required.
; 2. Altered source versions must be plainly marked as such, and must not be
;    misrepresented as being the original software.
; 3. This notice may not be removed or altered from any source distribution.
;
; All characters and materials in relation to the Sonic the Hedgehog game series
; are copyrights/trademarks of SEGA of Japan (SEGA Co., LTD). This product
; has been developed without permission of SEGA, therefore it's prohibited
; to sell/make profit of it.
;
; The BlitzSonic Team:
; - Héctor "Damizean" (elgigantedeyeso at gmail dot com)
; - Mark "Coré" (mabc_bh at yahoo dot com dot br)
; - Streak Thunderstorm
; - Mista ED
;

;\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/;
;	Project Title : Sonic the Hedgehog                                                                         ;
; ============================================================================================================ ;
;	Author :                                                                                                   ;
;	Email :                                                                                                    ;
;	Version: 0.1                                                                                               ;
;	Date: --/--/2008                                                                                           ;
;                                                                                                              ;
;\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/;
;                                                                                                              ;
;   Changelog:  -(Damizean)------------------------------->                                                    ;
;               17/01/2008 - Code reorganization.                                                              ;
;                                                                                                              ;
;==============================================================================================================;
;                                                                                                              ;
;   TODO:                                                                                                      ;
;                                                                                                              ;
;==============================================================================================================;



; /\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/
;   STRUCTURES
; /\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/

	; ---------------------------------------------------------------------------------------------------------
	; tInput_Configuration
	; ---------------------------------------------------------------------------------------------------------
	Type tInput_Configuration
		Field Device
		Field Button
		Field AlternateDevice
		Field AlternateButton
	End Type
	
	; ---------------------------------------------------------------------------------------------------------
	; tInput_Buttons
	; ---------------------------------------------------------------------------------------------------------
	Type tInput_Buttons
		Field Up
		Field Down
		Field Left
		Field Right
		Field ActionA
		Field ActionB
		Field CameraUp
		Field CameraDown
		Field CameraLeft
		Field CameraRight
		Field CameraZoomIn
		Field CameraZoomOut
	End Type
	
	; ---------------------------------------------------------------------------------------------------------
	; tInput
	; ---------------------------------------------------------------------------------------------------------
	Type tInput
		; ---- Input mode and configuration ----
		Field Mode
		Field Configuration.tInput_Configuration[12]

		; ---- Input entries -----
		Field Hold.tInput_Buttons
		Field Pressed.tInput_Buttons

		; ---- Analogic Input ----
		Field Movement_AnalogX#
		Field Movement_AnalogY#
		Field Movement_Direction#
		Field Movement_Pressure#

		; ---- Analogic camera Input ----
		Field Camera_AnalogX#
		Field Camera_AnalogY#
		Field Camera_Direction#
		Field Camera_Pressure#
	End Type


; /\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/
;   VARIABLES
; /\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/

	; ---- Input object -----
	Global Input.tInput = New tInput
	Input\Hold		= New tInput_Buttons
	Input\Pressed	= New tInput_Buttons
	For i=0 To 11 : Input\Configuration[i] = New tInput_Configuration : Next

	; ---- Other values ----
	Global Input_Gamepad          = 0
	Global Input_GamepadThreshold# = 0.2
	Global Input_MouseSpeed#      = 0
	Global Input_MouseSensitivy#  = 1.4
	Global Input_MouseWheel		  = 0
	Global Input_Lock             = True


; /\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/
;   CONSTANTS
; /\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/

	; ---- Configuration constants ----
	Const INPUT_BUTTON_UP			= 0
	Const INPUT_BUTTON_DOWN			= 1
	Const INPUT_BUTTON_LEFT			= 2
	Const INPUT_BUTTON_RIGHT		= 3
	Const INPUT_BUTTON_ACTION_A		= 4
	Const INPUT_BUTTON_ACTION_B		= 5
	Const INPUT_BUTTON_CAMERA_UP	= 6
	Const INPUT_BUTTON_CAMERA_DOWN	= 7
	Const INPUT_BUTTON_CAMERA_LEFT	= 8
	Const INPUT_BUTTON_CAMERA_RIGHT	= 9
	Const INPUT_BUTTON_CAMERA_ZIN	= 10
	Const INPUT_BUTTON_CAMERA_ZOUT	= 11
	
	Const INPUT_DEVICE_NONE			= 0
	Const INPUT_DEVICE_KEYBOARD		= 1
	Const INPUT_DEVICE_GAMEPAD		= 2
	Const INPUT_DEVICE_MOUSE		= 3

	Const INPUT_MOUSE_XMINUS		= 0
	Const INPUT_MOUSE_XPLUS 		= 1
	Const INPUT_MOUSE_YMINUS		= 2
	Const INPUT_MOUSE_YPLUS 		= 3
	Const INPUT_MOUSE_WHEELMINUS	= 4
	Const INPUT_MOUSE_WHEELPLUS		= 5
	Const INPUT_MOUSE_LEFT  		= 6
	Const INPUT_MOUSE_RIGHT 		= 7
	Const INPUT_MOUSE_MIDDLE		= 8

	Const INPUT_GAMEPAD_XMINUS      = 0
	Const INPUT_GAMEPAD_XPLUS       = 1
	Const INPUT_GAMEPAD_YMINUS      = 2
	Const INPUT_GAMEPAD_YPLUS       = 3
	Const INPUT_GAMEPAD_ZMINUS      = 4
	Const INPUT_GAMEPAD_ZPLUS       = 5
	Const INPUT_GAMEPAD_UMINUS      = 6
	Const INPUT_GAMEPAD_UPLUS       = 7
	Const INPUT_GAMEPAD_VMINUS      = 8
	Const INPUT_GAMEPAD_VPLUS       = 9
	Const INPUT_GAMEPAD_YAWMINUS    = 10
	Const INPUT_GAMEPAD_YAWPLUS     = 11
	Const INPUT_GAMEPAD_PITCHMINUS  = 12
	Const INPUT_GAMEPAD_PITCHPLUS   = 13
	Const INPUT_GAMEPAD_ROLLMINUS   = 14
	Const INPUT_GAMEPAD_ROLLPLUS    = 15
	Const INPUT_GAMEPAD_BUTTON      = 16


; /\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/
;   METHODS
; /\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/

	; ---------------------------------------------------------------------------------------------------------
	; Input_Update
	; ---------------------------------------------------------------------------------------------------------
	Function Input_Update()
		; ---- Check for input lock -----
		Select (Input_Lock)
			Case True
				If (KeyDown(KEY_ESCAPE)) Then
					Input_Lock = False
					ShowPointer()
					Return
				End If
			Case False
				If (MouseHit(1) Or MouseHit(2) Or MouseHit(3)) Then
					Input_Lock = True
					HidePointer()
					MoveMouse(GAME_WINDOW_W Shr 1, GAME_WINDOW_H Shr 1)
					FlushMouse()
				End If
				Return		
		End Select
		
		; ---- Update mouse wheel, at it can only be checked once -----
		Input_MouseWheel = MouseZSpeed()
		
		; ---- Update digital input -----
		Input\Pressed\Up 		 	= Ceil#(Input_RetrieveStatus(INPUT_BUTTON_UP)) And (Input\Hold\Up = 0)
		Input\Pressed\Down		 	= Ceil#(Input_RetrieveStatus(INPUT_BUTTON_DOWN)) And (Input\Hold\Down = 0)
		Input\Pressed\Left 			= Ceil#(Input_RetrieveStatus(INPUT_BUTTON_LEFT)) And (Input\Hold\Left = 0)
		Input\Pressed\Right 		= Ceil#(Input_RetrieveStatus(INPUT_BUTTON_RIGHT)) And (Input\Hold\Right = 0)
		Input\Pressed\ActionA	 	= Ceil#(Input_RetrieveStatus(INPUT_BUTTON_ACTION_A)) And (Input\Hold\ActionA = 0)
		Input\Pressed\ActionB	 	= Ceil#(Input_RetrieveStatus(INPUT_BUTTON_ACTION_B)) And (Input\Hold\ActionB = 0)
		Input\Pressed\CameraUp	 	= Ceil#(Input_RetrieveStatus(INPUT_BUTTON_CAMERA_UP)) And (Input\Hold\CameraUp = 0)
		Input\Pressed\CameraDown	= Ceil#(Input_RetrieveStatus(INPUT_BUTTON_CAMERA_DOWN)) And (Input\Hold\CameraDown = 0)
		Input\Pressed\CameraLeft	= Ceil#(Input_RetrieveStatus(INPUT_BUTTON_CAMERA_LEFT)) And (Input\Hold\CameraLeft = 0)
		Input\Pressed\CameraRight	= Ceil#(Input_RetrieveStatus(INPUT_BUTTON_CAMERA_RIGHT)) And (Input\Hold\CameraRight = 0)
		Input\Pressed\CameraZoomIn	= Ceil#(Input_RetrieveStatus(INPUT_BUTTON_CAMERA_ZIN)) And (Input\Hold\CameraZoomIn = 0)
		Input\Pressed\CameraZoomOut	= Ceil#(Input_RetrieveStatus(INPUT_BUTTON_CAMERA_ZOUT)) And (Input\Hold\CameraZoomOut = 0)
		
		Input\Hold\Up 		 		= Ceil#(Input_RetrieveStatus(INPUT_BUTTON_UP))
		Input\Hold\Down		 		= Ceil#(Input_RetrieveStatus(INPUT_BUTTON_DOWN))
		Input\Hold\Left 			= Ceil#(Input_RetrieveStatus(INPUT_BUTTON_LEFT))
		Input\Hold\Right 			= Ceil#(Input_RetrieveStatus(INPUT_BUTTON_RIGHT))
		Input\Hold\ActionA	 		= Ceil#(Input_RetrieveStatus(INPUT_BUTTON_ACTION_A))
		Input\Hold\ActionB	 		= Ceil#(Input_RetrieveStatus(INPUT_BUTTON_ACTION_B))
		Input\Hold\CameraUp	 		= Ceil#(Input_RetrieveStatus(INPUT_BUTTON_CAMERA_UP))
		Input\Hold\CameraDown		= Ceil#(Input_RetrieveStatus(INPUT_BUTTON_CAMERA_DOWN))
		Input\Hold\CameraLeft		= Ceil#(Input_RetrieveStatus(INPUT_BUTTON_CAMERA_LEFT))
		Input\Hold\CameraRight		= Ceil#(Input_RetrieveStatus(INPUT_BUTTON_CAMERA_RIGHT))
		Input\Hold\CameraZoomIn		= Ceil#(Input_RetrieveStatus(INPUT_BUTTON_CAMERA_ZIN))
		Input\Hold\CameraZoomOut	= Ceil#(Input_RetrieveStatus(INPUT_BUTTON_CAMERA_ZOUT))

		; ---- Update analog movement ----
		Input\Movement_AnalogX# = Input_RetrieveStatus(INPUT_BUTTON_RIGHT)-Input_RetrieveStatus(INPUT_BUTTON_LEFT)
		Input\Movement_AnalogY# = Input_RetrieveStatus(INPUT_BUTTON_DOWN)-Input_RetrieveStatus(INPUT_BUTTON_UP)
		Input\Movement_Pressure# = Sqr#(Input\Movement_AnalogX#*Input\Movement_AnalogX#+Input\Movement_AnalogY#*Input\Movement_AnalogY#)
		If (Input\Movement_Pressure# <> 0.0) Then Input\Movement_Direction# = WrapAngle#(ATan2#(Input\Movement_AnalogY#, Input\Movement_AnalogX#))
		
		; ---- Update camera movement ----
		Input\Camera_AnalogX# = Input_RetrieveStatus(INPUT_BUTTON_CAMERA_RIGHT)-Input_RetrieveStatus(INPUT_BUTTON_CAMERA_LEFT)
		Input\Camera_AnalogY# = Input_RetrieveStatus(INPUT_BUTTON_CAMERA_DOWN)-Input_RetrieveStatus(INPUT_BUTTON_CAMERA_UP)
		Input\Camera_Pressure# = Sqr#(Input\Camera_AnalogX#*Input\Camera_AnalogX#+Input\Camera_AnalogY#*Input\Camera_AnalogY#)
		If (Input\Camera_Pressure# <> 0.0) Then Input\Camera_Direction = WrapAngle#(ATan2#(Input\Camera_AnalogY#, Input\Camera_AnalogX#))

		; ---- Reposition mouse ----
		MoveMouse(GAME_WINDOW_W Shr 1, GAME_WINDOW_H Shr 1)

	End Function 

	; ---------------------------------------------------------------------------------------------------------
	; Input_RetrieveStatus
	; ---------------------------------------------------------------------------------------------------------
	Function Input_RetrieveStatus#(Button)
		g		= Input_Gamepad
		Result# = 0.0

		; Get device status
		Select Input\Configuration[Button]\Device
			Case INPUT_DEVICE_NONE
				Result = 0.0
			Case INPUT_DEVICE_KEYBOARD
				Result = KeyDown(Input\Configuration[Button]\Button)
			Case INPUT_DEVICE_GAMEPAD
				Select Input\Configuration[Button]\Button
	 				Case INPUT_GAMEPAD_XMINUS     : If (JoyX#(g)<-Input_GamepadThreshold#) Then Result# = (Abs(JoyX#(g))-Input_GamepadThreshold#)/(1-Input_GamepadThreshold#)
	 				Case INPUT_GAMEPAD_XPLUS      : If (JoyX#(g)>Input_GamepadThreshold#)  Then Result# = (Abs(JoyX#(g))-Input_GamepadThreshold#)/(1-Input_GamepadThreshold#)
	 				Case INPUT_GAMEPAD_YMINUS     : If (JoyY#(g)<-Input_GamepadThreshold#) Then Result# = (Abs(JoyY#(g))-Input_GamepadThreshold#)/(1-Input_GamepadThreshold#)
	 				Case INPUT_GAMEPAD_YPLUS      : If (JoyY#(g)>Input_GamepadThreshold#)  Then Result# = (Abs(JoyY#(g))-Input_GamepadThreshold#)/(1-Input_GamepadThreshold#)
	 				Case INPUT_GAMEPAD_ZMINUS     : If (JoyZ#(g)<-Input_GamepadThreshold#) Then Result# = (Abs(JoyZ#(g))-Input_GamepadThreshold#)/(1-Input_GamepadThreshold#)
	 				Case INPUT_GAMEPAD_ZPLUS      : If (JoyZ#(g)>Input_GamepadThreshold#)  Then Result# = (Abs(JoyZ#(g))-Input_GamepadThreshold#)/(1-Input_GamepadThreshold#)
	 				Case INPUT_GAMEPAD_UMINUS     : If (JoyU#(g)<-Input_GamepadThreshold#) Then Result# = (Abs(JoyU#(g))-Input_GamepadThreshold#)/(1-Input_GamepadThreshold#)
					Case INPUT_GAMEPAD_UPLUS      : If (JoyU#(g)>Input_GamepadThreshold#)  Then Result# = (Abs(JoyU#(g))-Input_GamepadThreshold#)/(1-Input_GamepadThreshold#)
					Case INPUT_GAMEPAD_VMINUS     : If (JoyV#(g)<-Input_GamepadThreshold#) Then Result# = (Abs(JoyV#(g))-Input_GamepadThreshold#)/(1-Input_GamepadThreshold#)
					Case INPUT_GAMEPAD_VPLUS      : If (JoyV#(g)>Input_GamepadThreshold#)  Then Result# = (Abs(JoyV#(g))-Input_GamepadThreshold#)/(1-Input_GamepadThreshold#)
	 				Case INPUT_GAMEPAD_YAWMINUS   : If ((JoyYaw#(g)/180.0)<-Input_GamepadThreshold#)   Then Result# = (Abs(JoyYaw#(g)/180.0)-Input_GamepadThreshold#)/(1-Input_GamepadThreshold#)
	 				Case INPUT_GAMEPAD_YAWPLUS    : If ((JoyYaw#(g)/180.0)>Input_GamepadThreshold#)    Then Result# = (Abs(JoyYaw#(g)/180.0)-Input_GamepadThreshold#)/(1-Input_GamepadThreshold#)
	 				Case INPUT_GAMEPAD_PITCHMINUS : If ((JoyPitch#(g)/180.0)<-Input_GamepadThreshold#) Then Result# = (Abs(JoyPitch#(g)/180.0)-Input_GamepadThreshold#)/(1-Input_GamepadThreshold#)
	 				Case INPUT_GAMEPAD_PITCHPLUS  : If ((JoyPitch#(g)/180.0)>Input_GamepadThreshold#)  Then Result# = (Abs(JoyPitch#(g)/180.0)-Input_GamepadThreshold#)/(1-Input_GamepadThreshold#)
	 				Case INPUT_GAMEPAD_ROLLMINUS  : If ((JoyRoll#(g)/180.0)<-Input_GamepadThreshold#)  Then Result# = (Abs(JoyRoll#(g)/180.0)-Input_GamepadThreshold#)/(1-Input_GamepadThreshold#)
					Case INPUT_GAMEPAD_ROLLPLUS   : If ((JoyRoll#(g)/180.0)>Input_GamepadThreshold#)   Then Result# = (Abs(JoyRoll#(g)/180.0)-Input_GamepadThreshold#)/(1-Input_GamepadThreshold#)
	 				Default
						Result# = JoyDown(Input\Configuration[Button]\Button-INPUT_GAMEPAD_BUTTON+1, g)
				End Select
			Case INPUT_DEVICE_MOUSE
				Select Input\Configuration[Button]\Button
	 				Case INPUT_MOUSE_XMINUS
						DiffX# = ((MouseX()-GAME_WINDOW_W*0.5)*Input_MouseSensitivy#)/Input_MouseSpeed#
						If (DiffX#<0.0) Then Result# = Abs(DiffX#)
	 				Case INPUT_MOUSE_XPLUS
						DiffX# = ((MouseX()-GAME_WINDOW_W*0.5)*Input_MouseSensitivy#)/Input_MouseSpeed#
						If (DiffX#>0.0) Then Result# = Abs(DiffX#)
	 				Case INPUT_MOUSE_YMINUS
						DiffY# = ((MouseY()-GAME_WINDOW_H*0.5)*Input_MouseSensitivy#)/Input_MouseSpeed#
						If (DiffY#<0.0) Then Result# = Abs(DiffY#)
	 				Case INPUT_MOUSE_YPLUS
						DiffY# = ((MouseY()-GAME_WINDOW_H*0.5)*Input_MouseSensitivy#)/Input_MouseSpeed#
						If (DiffY#>0.0) Then Result# = Abs(DiffY#)
	 				Case INPUT_MOUSE_WHEELMINUS : If (Input_MouseWheel<0.0) Then Result# = Abs(Input_MouseWheel)
	 				Case INPUT_MOUSE_WHEELPLUS  : If (Input_MouseWheel>0.0) Then Result# = Abs(Input_MouseWheel)
	 				Default
						Result# = MouseDown(Input\Configuration[Button]\Button-INPUT_MOUSE_LEFT+1)
				End Select
		End Select


		; Get alternate device status
		Select Input\Configuration[Button]\AlternateDevice
			Case INPUT_DEVICE_NONE
			Case INPUT_DEVICE_KEYBOARD
				Result# = Result#+KeyDown(Input\Configuration[Button]\AlternateButton)
			Case INPUT_DEVICE_GAMEPAD
				Select Input\Configuration[Button]\AlternateButton
	 				Case INPUT_GAMEPAD_XMINUS     : If (JoyX#(g)<-Input_GamepadThreshold#) Then Result# = Result#+(Abs(JoyX#(g))-Input_GamepadThreshold#)/(1-Input_GamepadThreshold#)
	 				Case INPUT_GAMEPAD_XPLUS      : If (JoyX#(g)>Input_GamepadThreshold#)  Then Result# = Result#+(Abs(JoyX#(g))-Input_GamepadThreshold#)/(1-Input_GamepadThreshold#)
	 				Case INPUT_GAMEPAD_YMINUS     : If (JoyY#(g)<-Input_GamepadThreshold#) Then Result# = Result#+(Abs(JoyY#(g))-Input_GamepadThreshold#)/(1-Input_GamepadThreshold#)
	 				Case INPUT_GAMEPAD_YPLUS      : If (JoyY#(g)>Input_GamepadThreshold#)  Then Result# = Result#+(Abs(JoyY#(g))-Input_GamepadThreshold#)/(1-Input_GamepadThreshold#)
	 				Case INPUT_GAMEPAD_ZMINUS     : If (JoyZ#(g)<-Input_GamepadThreshold#) Then Result# = Result#+(Abs(JoyZ#(g))-Input_GamepadThreshold#)/(1-Input_GamepadThreshold#)
	 				Case INPUT_GAMEPAD_ZPLUS      : If (JoyZ#(g)>Input_GamepadThreshold#)  Then Result# = Result#+(Abs(JoyZ#(g))-Input_GamepadThreshold#)/(1-Input_GamepadThreshold#)
	 				Case INPUT_GAMEPAD_UMINUS     : If (JoyU#(g)<-Input_GamepadThreshold#) Then Result# = Result#+(Abs(JoyU#(g))-Input_GamepadThreshold#)/(1-Input_GamepadThreshold#)
					Case INPUT_GAMEPAD_UPLUS      : If (JoyU#(g)>Input_GamepadThreshold#)  Then Result# = Result#+(Abs(JoyU#(g))-Input_GamepadThreshold#)/(1-Input_GamepadThreshold#)
					Case INPUT_GAMEPAD_VMINUS     : If (JoyV#(g)<-Input_GamepadThreshold#) Then Result# = Result#+(Abs(JoyV#(g))-Input_GamepadThreshold#)/(1-Input_GamepadThreshold#)
					Case INPUT_GAMEPAD_VPLUS      : If (JoyV#(g)>Input_GamepadThreshold#)  Then Result# = Result#+(Abs(JoyV#(g))-Input_GamepadThreshold#)/(1-Input_GamepadThreshold#)
	 				Case INPUT_GAMEPAD_YAWMINUS   : If ((JoyYaw#(g)/180.0)<-Input_GamepadThreshold#)   Then Result# = Result#+(Abs(JoyYaw#(g)/180.0)-Input_GamepadThreshold#)/(1-Input_GamepadThreshold#)
	 				Case INPUT_GAMEPAD_YAWPLUS    : If ((JoyYaw#(g)/180.0)>Input_GamepadThreshold#)    Then Result# = Result#+(Abs(JoyYaw#(g)/180.0)-Input_GamepadThreshold#)/(1-Input_GamepadThreshold#)
	 				Case INPUT_GAMEPAD_PITCHMINUS : If ((JoyPitch#(g)/180.0)<-Input_GamepadThreshold#) Then Result# = Result#+(Abs(JoyPitch#(g)/180.0)-Input_GamepadThreshold#)/(1-Input_GamepadThreshold#)
	 				Case INPUT_GAMEPAD_PITCHPLUS  : If ((JoyPitch#(g)/180.0)>Input_GamepadThreshold#)  Then Result# = Result#+(Abs(JoyPitch#(g)/180.0)-Input_GamepadThreshold#)/(1-Input_GamepadThreshold#)
	 				Case INPUT_GAMEPAD_ROLLMINUS  : If ((JoyRoll#(g)/180.0)<-Input_GamepadThreshold#)  Then Result# = Result#+(Abs(JoyRoll#(g)/180.0)-Input_GamepadThreshold#)/(1-Input_GamepadThreshold#)
					Case INPUT_GAMEPAD_ROLLPLUS   : If ((JoyRoll#(g)/180.0)>Input_GamepadThreshold#)   Then Result# = Result#+(Abs(JoyRoll#(g)/180.0)-Input_GamepadThreshold#)/(1-Input_GamepadThreshold#)
	 				Default
						Result# = Result#+JoyDown(Input\Configuration[Button]\AlternateButton-INPUT_GAMEPAD_BUTTON+1, g)
				End Select
			Case INPUT_DEVICE_MOUSE
				Select Input\Configuration[Button]\AlternateButton
	 				Case INPUT_MOUSE_XMINUS
						DiffX# = ((MouseX()-GAME_WINDOW_W*0.5)*Input_MouseSensitivy#)/Input_MouseSpeed#
						If (DiffX#<0.0) Then Result# = Result#+Abs(DiffX#)
	 				Case INPUT_MOUSE_XPLUS
						DiffX# = ((MouseX()-GAME_WINDOW_W*0.5)*Input_MouseSensitivy#)/Input_MouseSpeed#
						If (DiffX#>0.0) Then Result# = Result#+Abs(DiffX#)
	 				Case INPUT_MOUSE_YMINUS
						DiffY# = ((MouseY()-GAME_WINDOW_H*0.5)*Input_MouseSensitivy#)/Input_MouseSpeed#
						If (DiffY#<0.0) Then Result# = Result#+Abs(DiffY#)
	 				Case INPUT_MOUSE_YPLUS
						DiffY# = ((MouseY()-GAME_WINDOW_H*0.5)*Input_MouseSensitivy#)/Input_MouseSpeed#
						If (DiffY#>0.0) Then Result# = Result#+Abs(DiffY#)
	 				Case INPUT_MOUSE_WHEELMINUS : If (Input_MouseWheel<0.0) Then Result# = Result#+Abs(Input_MouseWheel)
	 				Case INPUT_MOUSE_WHEELPLUS  : If (Input_MouseWheel>0.0) Then Result# = Result#+Abs(Input_MouseWheel)
	 				Default
						Result# = Result#+MouseDown((Input\Configuration[Button]\AlternateButton)-INPUT_MOUSE_LEFT+1)
				End Select
		End Select

		If (Result# > 1.0) Then Result# = 1.0
		Return Result#
		
	End Function
