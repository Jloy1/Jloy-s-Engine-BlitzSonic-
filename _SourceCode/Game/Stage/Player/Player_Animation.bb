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
;               17/01/2008 - Code reorganization.                                                              ;
;                                                                                                              ;
;==============================================================================================================;
;                                                                                                              ;
;   TODO:                                                                                                      ;
;                                                                                                              ;
;==============================================================================================================;

	; Tilting consts
	Const TILT_ANGLECLAMP	= 25
	
	; Only effective between 1.05 and 2.
	Const TILT_NORMALISETIME = 1.1


; /\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/
;   METHODS
; /\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/

	; =========================================================================================================
	; Player_Animate
	; =========================================================================================================
	Function Player_Animate(p.tPlayer, d.tDeltaTime)
		; Change animation depending on action
		Select p\Action
			Case ACTION_COMMON
				; Get the current speed of the character.
				SpeedLength# = Sqr#(p\Motion\Speed\x#^2+p\Motion\Speed\z#^2)
				
				; Do the animations depending on if we are grounded or not.
				If (p\Motion\Ground = True) Then 
					p\Animation\Animation = 0 ; Idle
					If (SpeedLength# > 0.0) Then p\Animation\Animation = 1	; Walk
					If (SpeedLength# > 1.2) Then p\Animation\Animation = 2	; Jog
					If (SpeedLength# > 2.6) Then p\Animation\Animation = 3	; Run
					If (SpeedLength# > 3.5) Then p\Animation\Animation = 4	; Sprint
				Else
					; Arial animations
					If (p\Motion\Speed\y# > 0.35) Then
						p\Animation\Animation = 7 ; Rise
					Else If (p\Motion\Speed\y# < -0.35)
						p\Animation\Animation = 6 ; Fall
					Else
						p\Animation\Animation = 5 ; Spin
					End If
				End If
			Case ACTION_JUMP
				p\Animation\Animation = 5 ; Spin
			Case ACTION_CROUCH
				p\Animation\Animation = 1 ; Placeholder: Idle
			Case ACTION_SPINDASH
				p\Animation\Animation = 5 ; Spin
		End Select

		; If the animation changed, animate new
		If (p\Animation\Animation<>p\Animation\PreviousAnimation) Then
			Select p\Animation\Animation
				Case 0
					RecursiveAnimate(p\Objects\Mesh, 1, 0.4, 1, 8)
				Case 1
					RecursiveAnimate(p\Objects\Mesh, 1, 0.4, 2, 8)
					p\Animation\Time# = 0.0
				Case 2
					RecursiveAnimate(p\Objects\Mesh, 1, 0.4, 3, 9)
					p\Animation\Time# = 0.0
				Case 3
					RecursiveAnimate(p\Objects\Mesh, 1, 0.4, 4, 9)
					p\Animation\Time# = 0.0
				Case 4
					RecursiveAnimate(p\Objects\Mesh, 1, 0.4, 5, 9)
					p\Animation\Time# = 0.0
				Case 5
					RecursiveAnimate(p\Objects\Mesh, 1, 1.0, 6, 3)
				Case 6
					RecursiveAnimate(p\Objects\Mesh, 1, 0.4, 7, 3)
				Case 7
					RecursiveAnimate(p\Objects\Mesh, 1, 0.4, 8, 3)
			End Select
			
			p\Animation\PreviousAnimation = p\Animation\Animation
		End If

		; Depending on animation, change animation speed
		Select p\Animation\Animation
			Case 1
				p\Animation\Time# = p\Animation\Time#+Sqr#(p\Motion\Speed\x#^2+p\Motion\Speed\z#^2)*0.85*d\Delta
				RecursiveSetAnimTime(p\Objects\Mesh, p\Animation\Time#, 2)
			Case 2
				p\Animation\Time# = p\Animation\Time#+Sqr#(p\Motion\Speed\x#^2+p\Motion\Speed\z#^2)*0.675*d\Delta
				RecursiveSetAnimTime(p\Objects\Mesh, p\Animation\Time#, 3)
			Case 3
				p\Animation\Time# = p\Animation\Time#+Sqr#(p\Motion\Speed\x#^2+p\Motion\Speed\z#^2)*0.595*d\Delta
				RecursiveSetAnimTime(p\Objects\Mesh, p\Animation\Time#, 4)
			Case 4
				p\Animation\Time# = p\Animation\Time#+Sqr#(p\Motion\Speed\x#^2+p\Motion\Speed\z#^2)*0.53*d\Delta
				RecursiveSetAnimTime(p\Objects\Mesh, p\Animation\Time#, 5)
		End Select

		; On spinning animation, show ball
		If (p\Animation\Animation = 3) Then
			If ((MilliSecs() Mod 20) < 10) Then : ShowEntity(p\Objects\Mesh_JumpBall)
			Else : HideEntity(p\Objects\Mesh_JumpBall) : End If
		Else
			HideEntity(p\Objects\Mesh_JumpBall)
		End If
		
		; Update normals
		RecursiveUpdateNormals(p\Objects\Mesh)
	End Function
	
	Function Player_MovementTilt(p.tPlayer, d.tDeltaTime)
		; Character Tilt
		p\Animation\CharTilt\PreTilt# = p\Animation\CharTilt\CurTilt# ;Direction from the previous frame. (eg: previous was 17 deg)
		p\Animation\CharTilt\CurTilt# = p\Animation\Direction# ;Current direction. (eg: now is 15 deg)
		
		; What is the angle difference?
		differ# = (p\Animation\CharTilt\CurTilt# - p\Animation\CharTilt\PreTilt#) ; (eg: 2 difference. Yay.)
		
		; Clamping
		If (differ# > 180) Then
			differ2# = differ# - 360.0
		Else If (differ# < -180) Then ; Angle from Negative to Positive (Add 360)
			differ2# = differ# + 360.0
		Else 
			differ2# = differ#
		End If
		
		; Set it up to allow for tilting. This part is sensitive to FPS change, so be careful.
		p\Animation\CharTilt\Tilt# = p\Animation\CharTilt\Tilt# - (differ2#)
		p\Animation\CharTilt\Tilt# = p\Animation\CharTilt\Tilt# / Clamp#((TILT_NORMALISETIME*d\Delta), 1.05, 2) ; Reduce the value as the player stops turning. With clamping...
		
		; Cap the tilt to x degrees both ways. More clamping.
		If p\Animation\CharTilt\Tilt# > TILT_ANGLECLAMP Then p\Animation\CharTilt\Tilt# = TILT_ANGLECLAMP
		If p\Animation\CharTilt\Tilt# < -TILT_ANGLECLAMP Then p\Animation\CharTilt\Tilt# = -TILT_ANGLECLAMP
	End Function
;~IDEal Editor Parameters:
;~C#Blitz3D