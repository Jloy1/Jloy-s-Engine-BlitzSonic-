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
; - Mark "CorE (mabc_bh at yahoo dot com dot br)
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
;               17/01/2008 - Finally managed to fully convert management code to Delta Time. Works great :)    ;
;                                                                                                              ;
;               -(Damizean)------------------------------->                                                    ;
;               16/01/2008 - Code reorganization.                                                              ;
;                          - Rewrote some chunks of code to adapt to changes on the Maths library. The code    ;
;                            is now overall better readable.                                                   ;
;                          - Rewrote some chunks of code to work in a Delta Time environment. Not very good    ;
;                            results, but it's a start.                                                        ;
;                                                                                                              ;
;==============================================================================================================;
;                                                                                                              ;
;   TODO: - Implement basic movements, interaction with Rings, Monitors and Springs.                           ;
;                                                                                                              ;
;==============================================================================================================;

; /\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/
; 	CONSTANTS
; /\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/

	Const PLAYER_MODE_MOUSELOOK		= 0
	Const PLAYER_MODE_ANALOG		= 1
	Const PLAYER_MODE_SRB			= 2
	Const PLAYER_MODE_MODERN		= 3
	
; /\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/
; 	METHODS
; /\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/

	; ---- Player management ----
	; =========================================================================================================
	; Player_Handle
	; =========================================================================================================
	Function Player_Handle(p.tPlayer, d.tDeltaTime)
		; Depending on current mode, the pressed direction uses a different method. Mouselook and analog are
		; quite similar in this aspect.
		Select Gameplay_Control_Mode
			Case PLAYER_MODE_MOUSELOOK
				MotionDirection# = p\Objects\Camera\Rotation\y#-Input\Movement_Direction#
				MotionPressure#  = Input\Movement_Pressure#
			Case PLAYER_MODE_ANALOG
				MotionDirection# = p\Objects\Camera\Rotation\y#-Input\Movement_Direction#
				MotionPressure#  = Input\Movement_Pressure#
			Case PLAYER_MODE_SRB
				MotionDirection# = p\Objects\Camera\Rotation\y#-90*Input\Movement_AnalogY#
				MotionPressure#  = Input\Movement_Pressure#*Abs(Input\Movement_AnalogY#)
;			Case PLAYER_MODE_MODERN
;				MotionDirection# = EntityYaw#(p\Objects\Camera\Entity)-Input\Movement_Direction#
;				MotionPressure#  = Input\Movement_Pressure#
		End Select
		
		; Declarate acceleration and speed vectors and setup.
		Acceleration.tVector 		= Vector(Cos#(MotionDirection#)*MotionPressure#, 0, Sin#(MotionDirection#)*MotionPressure#)
		Speed.tVector 		 		= Vector(p\Motion\Speed\x#, 0, p\Motion\Speed\z#)
		SpeedCompensation.tVector	= Vector(0, 0, 0)
		Speed_Length#				= Vector_Length#(Speed)
		
		; Debugging Tools
		DEBUG_AccelerationVector =  Vector_Copy(Acceleration)
		DEBUG_SpeedVector = Vector_Copy(Speed)
		
		; Disable skidding flag
		p\Flags\Skidding = False

		; If there exists acceleration, handle the acceleration and change to new
		; direction, preserving the momentum in the needed cases.
		If (Vector_Length#(Acceleration)) Then

			; Calculate delta cos and sin
			DeltaCos# = Cos#(p\Animation\Direction#-90)
			DeltaSin# = Sin#(p\Animation\Direction#-90)
			
			; Change Player's direction. Depending on current motion orientation and speed, this
			; direction change would be done instantly or smoothly. This rotation isn't done entirely
			; based on delta, because it would appear as if the character automatically rotates when
			; at low FPS xD
			If (Speed_Length# < 0.1) Then
				p\Animation\Direction# = ATan2(((Acceleration\x#+DeltaCos#*3)/4)*1.0001,-(Acceleration\z#+DeltaSin#*3)/4)
			Else
				p\Animation\Direction# = ATan2(((Acceleration\x#+DeltaCos#*5)/6)*1.0001,-(Acceleration\z#+DeltaSin#*5)/6)
			End If
			
			; Depending on the dot product between current direction and new motion direction
			DotProduct# = Vector_DotProductNormalized#(Acceleration, Speed)
			If (DotProduct# < 0.0) Then
					
				; If there's an opposite change of motion direction, completely 
				If (p\Motion\Ground = True) Then
					Vector_MultiplyByScalar(Acceleration, 1.2)

					If (Speed_Length#>0.4) Then
						p\Flags\Skidding = True
						If (ChannelPlaying(Channel_Skidding)=False) Then Channel_Skidding = PlaySound(Sound_Skidding)
					End If
				End If

				Player_SubstractTowardsZero(Speed, 0.06*d\Delta#)
				
			Else If (DotProduct# < 0.4) Then
			
				; If there's a harsh change in motion direction, decrease
				; greatly the motion in current direction and increase acceleration
				; on the new.
				If (p\Motion\Ground = True) Then
					SpeedCompensation\x# = (Speed\x#*33+DeltaCos#*Speed_Length#)/34*0.96
					SpeedCompensation\z# = (Speed\z#*33+DeltaSin#*Speed_Length#)/34*0.96
					
					Vector_LinearInterpolation(Speed, SpeedCompensation, d\Delta#)
				Else
					Player_SubstractTowardsZero(Speed, 0.02*d\Delta#)
				EndIf
				
				Vector_MultiplyByScalar(Acceleration, 1.2)
				
			Else If (DotProduct# < 0.95) Then

				; If there's a mild change in direction, slighty decresae
				; the motion in current direction.
				If (p\Motion\Ground = True) Then
					SpeedCompensation\x# = (Speed\x#*19+DeltaCos#*Speed_Length#)/20
					SpeedCompensation\z# = (Speed\z#*19+DeltaSin#*Speed_Length#)/20
				Else
					SpeedCompensation\x# = (Speed\x#*21+DeltaCos#*Speed_Length#)/22*0.98
					SpeedCompensation\z# = (Speed\z#*21+DeltaSin#*Speed_Length#)/22*0.98					
				EndIf
				
				; Debugging tools
				DEBUG_SpeedComp = Vector_Copy(SpeedCompensation)
				
				
				Vector_LinearInterpolation(Speed, SpeedCompensation, d\Delta#)
				
			End If

			If (Speed_Length# <= COMMON_XZTOPSPEED#) Then
				Vector_MultiplyByScalar(Acceleration, COMMON_XZACCELERATION#*d\Delta#)
				Vector_Add(Speed, Acceleration)
			End If
		End If

		; Set back the ground speed
		p\Motion\Speed\x# = Speed\x# : p\Motion\Speed\z# = Speed\z#
		Delete Acceleration : Delete Speed : Delete SpeedCompensation


		; If the character's on the ground, apply deceleration based on the current slope, and
		; check if he has not enough speed to go further.
		If (p\Motion\Ground=True) Then Player_HandleAngleAcceleration(p, d)

		
		; However, decelerate if no acceleration exists.
		If (Speed_Length#>0.0) Then
			If (p\Motion\Speed\x#>0.0) Then
				p\Motion\Speed\x# = Max#(p\Motion\Speed\x#-(p\Motion\Speed\x#/Speed_Length#)*COMMON_XZDECELERATION#*d\Delta#, 0.0)
			Else
				p\Motion\Speed\x# = Min#(p\Motion\Speed\x#-(p\Motion\Speed\x#/Speed_Length#)*COMMON_XZDECELERATION#*d\Delta#, 0.0)
			End If
			If (p\Motion\Speed\z#>0.0) Then
				p\Motion\Speed\z# = Max#(p\Motion\Speed\z#-(p\Motion\Speed\z#/Speed_Length#)*COMMON_XZDECELERATION#*d\Delta#, 0.0)
			Else
				p\Motion\Speed\z# = Min#(p\Motion\Speed\z#-(p\Motion\Speed\z#/Speed_Length#)*COMMON_XZDECELERATION#*d\Delta#, 0.0)
			End If 
		End If
		

		; Manage Y speeds
		If (p\Motion\Ground = False) Then
			p\Motion\Speed\y# = Max(p\Motion\Speed\y#-(COMMON_YACCELERATION#*d\Delta), COMMON_YTOPSPEED#)
		Else
			p\Motion\Speed\y# = 0
		End If 
	End Function


	; =========================================================================================================
	; Player_HandleAngleAcceleration
	; =========================================================================================================
	Function Player_HandleAngleAcceleration(p.tPlayer, d.tDeltaTime)
		; Decelerate and check for falling
		If (Abs(p\Motion\Align\y#) <= 0.7) Then 
			p\Motion\Speed\x# = p\Motion\Speed\x#+p\Motion\Align\x#^2*0.04*Sgn(p\Motion\Align\x#)*d\Delta
			p\Motion\Speed\z# = p\Motion\Speed\z#+p\Motion\Align\z#^2*0.04*Sgn(p\Motion\Align\z#)*d\Delta
		End If

		; Check if player falls
		If (p\Motion\Align\y# <= 0.1 And Vector_Length#(p\Motion\Speed)*10<((-Dot#)+2.0))
			Player_ConvertGroundToAir(p)
			p\Motion\Align\x# 	= Game\Stage\GravityAlignment\x#
			p\Motion\Align\y# 	= Game\Stage\GravityAlignment\y#
			p\Motion\Align\z# 	= Game\Stage\GravityAlignment\z#
			p\Motion\Ground 	= False 
		End If
	End Function


	; =========================================================================================================
	; Player_Action_Common
	; =========================================================================================================
	Function Player_Action_Common(p.tPlayer, d.tDeltaTime)
		; Jump when pressed and if on ground
		If (Input\Pressed\ActionA And p\Motion\Ground=True) Then			
			p\Action = ACTION_JUMP
			p\Motion\Speed\x# = p\Motion\Speed\x#*0.7
			p\Motion\Speed\y# = JUMP_STRENGHT#
			p\Motion\Speed\z# = p\Motion\Speed\z#*0.7
			Player_ConvertGroundToAir(p)
			p\Motion\Ground = False
			
			; When jumping, we need to set the align vector instantly, or else the 
			; player may reattach to the wall
			p\Motion\Align\x# = 0.0
			p\Motion\Align\y# = 1.0
			p\Motion\Align\z# = 0.0


			PlaySound(Sound_Jump)
		End If

		If (Input\Pressed\ActionB And p\Motion\Ground=True) Then
			p\Action = ACTION_CROUCH
		End If
	End Function


	; =========================================================================================================
	; Player_Action_Jump
	; =========================================================================================================
	Function Player_Action_Jump(p.tPlayer, d.tDeltaTime)
		; Variable jump
		If (Input\Hold\ActionA = False And p\Motion\Speed\y# > JUMP_STRENGHT_VARIABLE#) Then
			p\Motion\Speed\y# = JUMP_STRENGHT_VARIABLE#
		End If

		; Land
		If (p\Motion\Ground = True) Then p\Action = ACTION_COMMON
	End Function


	; =========================================================================================================
	; Player_Action_Crouch
	; =========================================================================================================
	Function Player_Action_Crouch(p.tPlayer, d.tDeltaTime)
		p\Motion\Speed\x# = 0
		p\Motion\Speed\z# = 0		
		If (Input\Pressed\ActionA) Then
			p\Action = ACTION_SPINDASH
			p\SpindashCharge = 1.7
			PlaySound(Sound_SpinDash)
			Animate p\Objects\Mesh_Spindash, 1,  0.8
;			Animate FindChild(p\Objects\Mesh_Spindash, "Cone01"), 1, 0.8
		EndIf
		
		If (Not Input\Hold\ActionB) Then p\Action = ACTION_COMMON
	End Function 


	; =========================================================================================================
	; Player_Action_Spindash
	; =========================================================================================================
	Function Player_Action_Spindash(p.tPlayer, d.tDeltaTime)
		p\Motion\Speed\x# = 0
		p\Motion\Speed\z# = 0		

		If (Input\Pressed\ActionA) Then
			p\SpindashCharge = p\SpindashCharge + 0.4
			If (p\SpindashCharge > 4.0) Then p\SpindashCharge = 4.0
			
			PlaySound(Sound_SpinDash)
		Else
			p\SpindashCharge = p\SpindashCharge - 0.1
			If (p\SpindashCharge < 1.7) Then p\SpindashCharge = 2.6
		EndIf
		
		HideEntity p\Objects\Mesh
		ShowEntity p\Objects\Mesh_Spindash
		RotateEntity(p\Objects\Mesh_Spindash, 0, 0, 0)
		PositionEntity p\Objects\Mesh_Spindash, EntityX(p\Objects\Mesh), EntityY(p\Objects\Mesh), EntityZ(p\Objects\Mesh)
		RotateEntity(p\Objects\Mesh_Spindash, 0, p\Animation\Direction#-90, 0)
		AlignToVector(p\Objects\Mesh_Spindash, p\Motion\Align\x#, p\Motion\Align\y#, p\Motion\Align\z#, 2)
;		UpdateNormals FindChild(p\Objects\Mesh_Spindash, "Cone01")

		If (Not Input\Hold\ActionB) Then
			p\Action = ACTION_COMMON
			p\Motion\Speed\x# = Sin(p\Animation\Direction#)*p\SpindashCharge
			p\Motion\Speed\z# = -Cos(p\Animation\Direction#)*p\SpindashCharge	
			PlaySound(Sound_SpindashRelease)
			ShowEntity p\Objects\Mesh
			HideEntity p\Objects\Mesh_Spindash
		EndIf		
	End Function


	; =========================================================================================================
	; Player_SubstractTowardsZero
	; =========================================================================================================
	Function Player_SubstractTowardsZero(v.tVector, Delta#)
		; Clamp delta
		Delta# = Min#(Max#(Delta#, 0.0), 1.0)

		; Calculate substract value
		SubX# = v\x#*Delta#
		SubY# = v\y#*Delta#
		SubZ# = v\z#*Delta#

		; Substract to each axys
		If (v\x# > 0) Then : v\x# = Max#(v\x#-SubX#, 0) : Else  : v\x# = Min#(v\x#-SubX#, 0) : End If
		If (v\y# > 0) Then : v\y# = Max#(v\y#-SubY#, 0) : Else  : v\y# = Min#(v\y#-SubY#, 0) : End If
		If (v\z# > 0) Then : v\z# = Max#(v\z#-SubZ#, 0) : Else  : v\z# = Min#(v\z#-SubZ#, 0) : End If
	End Function 
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	; =========================================================================================================
	; Player_Handle
	; =========================================================================================================
	Function Player_Handle_Modern(p.tPlayer, d.tDeltaTime)
		; Depending on current mode, the pressed direction uses a different method. Mouselook and analog are
		; quite similar in this aspect.
		Select Gameplay_Control_Mode
			Case PLAYER_MODE_MOUSELOOK
				MotionDirection# = p\Objects\Camera\Rotation\y#-Input\Movement_Direction#
				MotionPressure#  = Input\Movement_Pressure#
			Case PLAYER_MODE_ANALOG
				MotionDirection# = p\Objects\Camera\Rotation\y#-Input\Movement_Direction#
				MotionPressure#  = Input\Movement_Pressure#
			Case PLAYER_MODE_SRB
				MotionDirection# = p\Objects\Camera\Rotation\y#-90*Input\Movement_AnalogY#
				MotionPressure#  = Input\Movement_Pressure#*Abs(Input\Movement_AnalogY#)
;			Case PLAYER_MODE_MODERN
;				MotionDirection# = EntityYaw#(p\Objects\Camera\Entity)-Input\Movement_Direction#
;				MotionPressure#  = Input\Movement_Pressure#
		End Select
		
		; Declarate acceleration and speed vectors and setup.
		Acceleration.tVector 		= Vector(Cos#(MotionDirection#)*MotionPressure#, 0, Sin#(MotionDirection#)*MotionPressure#)
		Speed.tVector 		 		= Vector(p\Motion\Speed\x#, 0, p\Motion\Speed\z#)
		SpeedNormX# 				= ((Acceleration\x#+Speed\x#)/2.0)*d\Delta
		SpeedNormZ# 				= ((Acceleration\z#+Speed\z#)/2.0)*d\Delta
		SpeedNormal.tVector 		= Vector(SpeedNormX#, 0, SpeedNormZ#)
		SpeedCompensation.tVector	= Vector(0, 0, 0)
		Speed_Length#				= Vector_Length#(Speed)
		
		; Debugging Tools
		DEBUG_AccelerationVector =  Vector_Copy(Acceleration)
		DEBUG_SpeedVector = Vector_Copy(Speed)
		
		; Disable skidding flag
		p\Flags\Skidding = False

		; If there exists acceleration, handle the acceleration and change to new
		; direction, preserving the momentum in the needed cases.
		If (Vector_Length#(Acceleration)) Then

			; Calculate delta cos and sin
			DeltaCos# = Cos#(p\Animation\Direction#-90)
			DeltaSin# = Sin#(p\Animation\Direction#-90)
			
			; Change Player's direction. Depending on current motion orientation and speed, this
			; direction change would be done instantly or smoothly. This rotation isn't done entirely
			; based on delta, because it would appear as if the character automatically rotates when
			; at low FPS xD
			If (Speed_Length# < 0.1) Then
				p\Animation\Direction# = ATan2(((SpeedNormal\x#+DeltaCos#*3)/4)*1.0001,-(SpeedNormal\z#+DeltaSin#*3)/4)
			Else
				p\Animation\Direction# = ATan2(((SpeedNormal\x#+DeltaCos#*5)/6)*1.0001,-(SpeedNormal\z#+DeltaSin#*5)/6)
			End If
			
			; Depending on the dot product between current direction and new motion direction
			DotProduct# = Vector_DotProductNormalized#(Acceleration, Speed)
			If (DotProduct# < 0.0) Then
					
				; If there's an opposite change of motion direction, completely 
				If (p\Motion\Ground = True) Then
					Vector_MultiplyByScalar(Acceleration, 1.2)

					If (Speed_Length#>0.4) Then
						p\Flags\Skidding = True
						If (ChannelPlaying(Channel_Skidding)=False) Then Channel_Skidding = PlaySound(Sound_Skidding)
					End If
				Else	
					Player_SubstractTowardsZero(Speed, 0.02*d\Delta#)
				End If

				
				
			Else If (DotProduct# < 0.4) Then
			
				; If there's a harsh change in motion direction, decrease
				; greatly the motion in current direction and increase acceleration
				; on the new.
				If (p\Motion\Ground = True) Then
					SpeedCompensation\x# = (Speed\x#*33+DeltaCos#*Speed_Length#)/34*0.96
					SpeedCompensation\z# = (Speed\z#*33+DeltaSin#*Speed_Length#)/34*0.96
					
					Vector_LinearInterpolation(Speed, SpeedCompensation, d\Delta#)
				Else
					Player_SubstractTowardsZero(Speed, 0.02*d\Delta#)
				EndIf
				
				Vector_MultiplyByScalar(Acceleration, 1.2)
				
			Else If (DotProduct# < 0.95) Then

				; If there's a mild change in direction, slighty decresae
				; the motion in current direction.
				If (p\Motion\Ground = True) Then
					SpeedCompensation\x# = (Speed\x#*19+DeltaCos#*Speed_Length#)/20
					SpeedCompensation\z# = (Speed\z#*19+DeltaSin#*Speed_Length#)/20
				Else
					SpeedCompensation\x# = (Speed\x#*21+DeltaCos#*Speed_Length#)/22*0.98
					SpeedCompensation\z# = (Speed\z#*21+DeltaSin#*Speed_Length#)/22*0.98					
				EndIf
				
				; Debugging tools
				DEBUG_SpeedComp = Vector_Copy(SpeedCompensation)
				
				
				Vector_LinearInterpolation(Speed, SpeedCompensation, d\Delta#)
				
			End If
			
			
			
			If (Speed_Length# <= COMMON_XZTOPSPEED#) Then
				Vector_MultiplyByScalar(Acceleration, COMMON_XZACCELERATION#*d\Delta#)
				Vector_Add(Speed, Acceleration)
			End If
		End If

		; Set back the ground speed
		p\Motion\Speed\x# = Speed\x# : p\Motion\Speed\z# = Speed\z#
		Delete Acceleration : Delete Speed : Delete SpeedCompensation


		; If the character's on the ground, apply deceleration based on the current slope, and
		; check if he has not enough speed to go further.
		If (p\Motion\Ground=True) Then Player_HandleAngleAcceleration(p, d)

		
		; However, decelerate if no acceleration exists.
		If (Speed_Length#>0.0) Then
			If (p\Motion\Speed\x#>0.0) Then
				p\Motion\Speed\x# = Max#(p\Motion\Speed\x#-(p\Motion\Speed\x#/Speed_Length#)*COMMON_XZDECELERATION#*d\Delta#, 0.0)
			Else
				p\Motion\Speed\x# = Min#(p\Motion\Speed\x#-(p\Motion\Speed\x#/Speed_Length#)*COMMON_XZDECELERATION#*d\Delta#, 0.0)
			End If
			If (p\Motion\Speed\z#>0.0) Then
				p\Motion\Speed\z# = Max#(p\Motion\Speed\z#-(p\Motion\Speed\z#/Speed_Length#)*COMMON_XZDECELERATION#*d\Delta#, 0.0)
			Else
				p\Motion\Speed\z# = Min#(p\Motion\Speed\z#-(p\Motion\Speed\z#/Speed_Length#)*COMMON_XZDECELERATION#*d\Delta#, 0.0)
			End If 
		End If
		

		; Manage Y speeds
		If (p\Motion\Ground = False) Then
			p\Motion\Speed\y# = Max(p\Motion\Speed\y#-(COMMON_YACCELERATION#*d\Delta), COMMON_YTOPSPEED#)
		Else
			p\Motion\Speed\y# = 0
		End If 
	End Function	
	
	
	
;~IDEal Editor Parameters:
;~C#Blitz3D