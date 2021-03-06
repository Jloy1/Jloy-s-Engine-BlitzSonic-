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
; - H�ctor "Damizean" (elgigantedeyeso at gmail dot com)
; - Mark "Cor�E (mabc_bh at yahoo dot com dot br)
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
;               27/01/2008 - Major rewrite to use configuration files. Added SRB2 mode and Analog mode.        ;
;                            Also properly added Point of View camera mode.                                    ;
;               17/01/2008 - Code reorganization.                                                              ;
;                                                                                                              ;
;==============================================================================================================;
;                                                                                                              ;
;   TODO:                                                                                                      ;
;                                                                                                              ;
;==============================================================================================================;

; /\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/
; 	STRUCTURES
; /\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/

	Type tCamera
		; ---- Camera entity variables ----
		Field Entity
		
		; ---- Modern control camera pivot ----
		Field ModernPivot

		; ---- Camera attributes in world space ----
		Field Position.tVector
		Field Rotation.tVector
		Field Alignment.tVector

		; ---- Camera values -----
		Field Mode
		Field DistanceFromCamera#
		Field DistanceFromTarget#
		Field FieldOfView#

		; ---- Target values -----
		Field Target.tPlayer
		Field TargetPosition.tVector
		Field TargetRotation.tVector
		Field TargetStationaryTimer
	End Type

; /\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/
; 	CONSTANTS
; /\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/

	; ---- Camera modes -----
	Const CAMERA_MODE_NORMAL	= 0
	Const CAMERA_MODE_TARGETPOV	= 1
	
	; ---- Camera distance values -----
	Const CAMERA_DISTANCE_NEAR#	= 4
	Const CAMERA_DISTANCE_FAR#  = 30

	; ---- Camera field-of-view values -----
	Const CAMERA_FOV_SPINDASH#	= 70
	Const CAMERA_FOV_NORMAL#	= 50

; /\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/
; 	METHODS
; /\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/

	; ---------------------------------------------------------------------------------------------------------
	; Camera_Create
	; ---------------------------------------------------------------------------------------------------------
	Function Camera_Create.tCamera()
		; ----- Create camera structure -----
		c.tCamera 	= New tCamera
			; Allocate vectors
			c\Position		 = Vector(0, 0, 0)
			c\Rotation		 = Vector(Gameplay_Camera_RotationX#, Gameplay_Camera_RotationY#, 0)
			c\Alignment		 = Vector(0, 1, 0)
			c\TargetPosition = Vector(0, 0, 0)
			c\TargetRotation = Vector(0, 0, 0)
			
			; Create camera and include initial setup
			c\Entity 	= CreateCamera(Game\Stage\Root)
			c\ModernPivot = CreatePivot(Game\Stage\Root)
			PositionEntity(c\ModernPivot, 0, 3, -10)
			
				; Setup camera
				CameraZoom(c\Entity, 		CAMERA_FOV_NORMAL#)
				CameraRange(c\Entity, 		0.1, 2000)
				CameraFogMode(c\Entity, 	1)
				CameraFogColor(c\Entity,	170, 208, 255)
				CameraFogRange(c\Entity, 	940, 1600)

				; Setup camera values
				c\Mode					= Gameplay_Camera_TargetPOV
				c\DistanceFromCamera#	= (CAMERA_DISTANCE_NEAR#+CAMERA_DISTANCE_FAR#)*0.5
				c\DistanceFromTarget#	= c\DistanceFromCamera#
				c\FieldOfView#			= CAMERA_FOV_NORMAL#
				
				; Setup camera collision
				EntityType(c\Entity, COLLISION_CAMERA)
				EntityRadius(c\Entity, 1.5)

				; Initializate FX Manager
				FxManager_SetCamera(c\Entity, 0.02)
				
		; ---- Done ----
		Return c

	End Function

	; ---------------------------------------------------------------------------------------------------------
	; Camera_Bind
	; ---------------------------------------------------------------------------------------------------------
	Function Camera_Bind(c.tCamera, p.tPlayer)
		; ---- Attach player to camera -----
		c\Target = p
		p\Objects\Camera = c

		; ---- Done -----
	End Function
	
	Function Camera_Update(c.tCamera, d.tDeltaTime)

		; ---- Don't do anything to the camera if there's no target.
		If (c\Target = Null) Then Return

		; ---- Camera rotation style depending on control mode ----
		Select Gameplay_Control_Mode

			Case PLAYER_MODE_MOUSELOOK
				; Mouselook mode is completely free view of the environment. Just rotate using the
				; values provided
				c\TargetRotation\y# = c\TargetRotation\y#-Input\Camera_AnalogX#*Gameplay_Camera_RotationSpeedX#
				c\TargetRotation\x# = Clamp#(180+c\TargetRotation\x+Input\Camera_AnalogY#*Gameplay_Camera_RotationSpeedY#, 100, 260)-180

			Case PLAYER_MODE_ANALOG
				; Analog mode works like mouse look, but also provides a degree of rotation when the player
				; is rotating.
				c\TargetRotation\y# = c\TargetRotation\y#-Input\Camera_AnalogX#*Gameplay_Camera_RotationSpeedX#*d\Delta
				c\TargetRotation\x# = Clamp#(180+c\TargetRotation\x+Input\Camera_AnalogY#*Gameplay_Camera_RotationSpeedY#*d\Delta, 100, 260)-180

				; Also, when the player pressed left or right, the camera rotates with them
				RotationX# = Cos(Input\Movement_Direction)*Input\Movement_Pressure
				RotationY# = Sin(Input\Movement_Direction)*Input\Movement_Pressure
				c\TargetRotation\y# = c\TargetRotation\y#-(RotationX#*Gameplay_Camera_RotationSpeedX#*0.7*d\Delta)
				If (RotationY#>0.0) Then c\TargetRotation\y# = c\TargetRotation\y#-(RotationY#*Gameplay_Camera_RotationSpeedX#*0.7*d\Delta)*Sgn(Input\Movement_AnalogX#)
				
				; Once the player is stationary, return back to the original position
				If (c\Target\Action = ACTION_COMMON And Vector_Length#(c\Target\Motion\Speed)=0.0 And Input\Camera_Pressure=0.0) Then
					c\TargetStationaryTimer# = c\TargetStationaryTimer# + d\Delta#
				Else
					c\TargetStationaryTimer# = 0
				End If
				
				If (c\TargetStationaryTimer# > 400) Then c\TargetRotation\y# = RotateTowardsAngle#(c\TargetRotation\y#, c\Target\Animation\Direction#+180, 0.6*d\Delta)
			Case PLAYER_MODE_SRB
				; Basically a copy of SRB2's system, wich has the camera always at the back of Sonic,
				; and the player only changes his position.
				c\TargetRotation\y# = c\TargetRotation\y#-Input\Movement_AnalogX#*Gameplay_Camera_RotationSpeedX#*d\Delta#
				c\TargetRotation\x# = c\TargetRotation\x#-Input\Camera_AnalogY#*Gameplay_Camera_RotationSpeedY#*d\Delta#
			
			Case PLAYER_MODE_MODERN
				; This is the new follow camera mode. This will try and simulate the Sonic Generations camera
				; to some degree. This is best used with an analog controller or joystick.
				;[NOTE: THIS MODE DOES NOT SUPPORT TARGET NORMALS ALIGNMENT]
				
				; This code correctly moves the camera.
				c\TargetRotation\x# = Clamp#(180+c\TargetRotation\x+Input\Camera_AnalogY#*Gameplay_Camera_RotationSpeedY#*d\Delta, 100, 260)-180
				c\TargetRotation\y# = EntityYaw#(c\Entity)+360
				RotationX# = Cos(Input\Movement_Direction)*Input\Movement_Pressure
				RotationY# = Sin(Input\Movement_Direction)*Input\Movement_Pressure
				
				
		End Select

		; ---- Manage zoom in and zoom out -----
		If (Input\Hold\CameraZoomIn)  Then c\DistanceFromTarget# = Clamp(c\DistanceFromTarget#-1, CAMERA_DISTANCE_NEAR#, CAMERA_DISTANCE_FAR#)
		If (Input\Hold\CameraZoomOut) Then c\DistanceFromTarget# = Clamp(c\DistanceFromTarget#+1, CAMERA_DISTANCE_NEAR#, CAMERA_DISTANCE_FAR#)

		c\DistanceFromCamera# = Interpolate#(c\DistanceFromCamera#, c\DistanceFromTarget#, 0.1*d\Delta)
		c\FieldOfView#		  = Interpolate(c\FieldOfView#, CAMERA_FOV_NORMAL, 0.1*d\Delta)

		; ---- Finally, change camera position around the target -----
		; Change position
		Vector_Set(c\TargetPosition, EntityX#(c\Target\Objects\Entity), EntityY#(c\Target\Objects\Entity), EntityZ#(c\Target\Objects\Entity))
		Vector_LinearInterpolation(c\Position, c\TargetPosition, Gameplay_Camera_Smoothness#*d\Delta)
		Vector_LinearInterpolation(c\Rotation, c\TargetRotation, Gameplay_Camera_Smoothness#*d\Delta)
		Vector_LinearInterpolation(c\Alignment, c\Target\Animation\Align, 0.05+Vector_Length(c\Target\Motion\Speed)*0.01*d\Delta)	
		
		; Alternate Camera Mode
		If (Gameplay_Control_Mode = 3) Then
			; Apply changes
			CameraZoom(c\Entity, 1/Tan#(c\FieldOfView#))
			EntityType(c\Entity, COLLISION_NONE)
			;EntityType(c\Entity, COLLISION_CAMERA)
			AlignToVector(c\Entity, 0, 0, 0, 2)
			PointEntity(c\ModernPivot, c\Target\Objects\Entity)	
							
			dst# = EntityDistance#(c\ModernPivot, c\Target\Objects\Entity)
							
			If (c\Target\Motion\Ground = True) Then
				If (LinePick(EntityX#(c\ModernPivot), EntityY#(c\ModernPivot), EntityZ#(c\ModernPivot), 0, -28, 0)) Then	
					ds# = (EntityDistance#(c\ModernPivot, c\Target\Objects\Entity))/14.0
					If (LinePick(EntityX#(c\ModernPivot), EntityY#(c\ModernPivot), EntityZ#(c\ModernPivot), 0, -4*ds#, 0)) Then MoveEntity(c\ModernPivot, 0, 0.1*d\Delta#, 0)
					If Not (LinePick(EntityX#(c\ModernPivot), EntityY#(c\ModernPivot), EntityZ#(c\ModernPivot), 0, -4.5*ds#, 0)) Then MoveEntity(c\ModernPivot, 0, -0.1*d\Delta#, 0)
				End If
			End If				

			If EntityDistance(c\ModernPivot, c\Target\Objects\Entity) > 14 Then MoveEntity(c\ModernPivot, 0, 0, (0.5+(dst#-14)*0.25)*d\Delta#)
			
			dsm# = 20/(EntityDistance#(c\ModernPivot, c\Target\Objects\Entity))
			If EntityDistance(c\ModernPivot, c\Target\Objects\Entity) < 8 Then MoveEntity(c\ModernPivot, 0, 0, (-0.3*dsm#)*d\Delta#)
			
			PositionEntity(c\Target\Objects\CameraFollowPivot, EntityX#(c\Target\Objects\Entity), EntityY#(c\Target\Objects\Entity), EntityZ#(c\Target\Objects\Entity))
			RotateEntity(c\Target\Objects\CameraFollowPivot, EntityPitch#(c\Target\Objects\Entity), c\Target\Animation\Direction#, EntityRoll#(c\Target\Objects\Entity))
			
			; More camera helping goodness.
			playerSpeed# = Sqr#(c\Target\Motion\Speed\x#^2+c\Target\Motion\Speed\z#^2)
			camVRate# = (1.25 + (playerSpeed#*2))
			;camHRateExpected# = (c\Target\Animation\CharTilt\Tilt# / 3.5)
			If (playerSpeed# > 1.1) Then
				camHRate# = (c\Target\Animation\CharTilt\Tilt# / 3.5)
			Else
				camHRate# = 0
			End If
			MoveEntity(c\Target\Objects\CameraFollowPivot, -camHRate#, 0, -camVRate#)
			
			; Correct the camera
			PositionEntity(c\Entity, EntityX#(c\ModernPivot), EntityY#(c\ModernPivot), EntityZ#(c\ModernPivot))
			TranslateEntity(c\Entity, 0, 4, 0)
			PointEntity(c\Entity, c\Target\Objects\CameraFollowPivot)
		Else	
		; Normal Camera Stuff
			; Apply changes
			CameraZoom(c\Entity, 1/Tan#(c\FieldOfView#))
			EntityType(c\Entity, COLLISION_NONE)
			PositionEntity(c\Entity, c\Position\x#, c\Position\y#, c\Position\z#)
			EntityType(c\Entity, COLLISION_CAMERA)
			RotateEntity(c\Entity, 0, 0, 0)
		
			If (c\Mode = CAMERA_MODE_TARGETPOV) Then AlignToVector(c\Entity, c\Alignment\x#, c\Alignment\y#, c\Alignment\z#, 2)
		
			TurnEntity(c\Entity, c\Rotation\x#, c\Rotation\y#, c\Rotation\z#)
			MoveEntity(c\Entity, 0, c\DistanceFromCamera#*0.25, -c\DistanceFromCamera#)
		End If
		
		

				
	End Function
;~IDEal Editor Parameters:
;~C#Blitz3D