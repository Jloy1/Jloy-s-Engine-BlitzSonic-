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
;               17/01/2008 - Code reorganization.                                                              ;
;                                                                                                              ;
;==============================================================================================================;
;                                                                                                              ;
;   TODO:                                                                                                      ;
;                                                                                                              ;
;==============================================================================================================;

; /\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/
; 	METHODS
; /\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/

	; ---------------------------------------------------------------------------------------------------------
	; Interface_Render
	; ---------------------------------------------------------------------------------------------------------
	Function Interface_Render()
		StartDraw()
			; Setup rendering methods
			SetBlend(FI_ALPHABLEND)
			SetAlpha(1.0)
			SetScale(GAME_WINDOW_SCALE#, GAME_WINDOW_SCALE#)
			SetColor(255, 255, 255)

			; Render Score/Time/Rings image
			DrawImageEx(Interface_ScoreTimeRings, 32*GAME_WINDOW_SCALE#, 32*GAME_WINDOW_SCALE#)
			DrawImageEx(Interface_Icons, 32*GAME_WINDOW_SCALE#, GAME_WINDOW_H-85.0*GAME_WINDOW_SCALE#)
			
			; Render numbers
			Interface_Number(Game\Gameplay\Score, 				320*GAME_WINDOW_SCALE#, 30*GAME_WINDOW_SCALE#,		0, 1)
			Interface_Number((Game\Gameplay\Time/1000) Mod 60,  177*GAME_WINDOW_SCALE#, 59*GAME_WINDOW_SCALE#, 		2)
			Interface_Number((Game\Gameplay\Time/60000), 		145*GAME_WINDOW_SCALE#, 59*GAME_WINDOW_SCALE#, 		1)
			Interface_Number(Game\Gameplay\Rings, 				210*GAME_WINDOW_SCALE#, 88*GAME_WINDOW_SCALE#, 		0, 1)
			Interface_Number(Game\Gameplay\Lives, 				140*GAME_WINDOW_SCALE#, GAME_WINDOW_H-75.0*GAME_WINDOW_SCALE#, 	0, 1)

			; Render FPS
			Interface_Number(Game\Others\FPS,					500*GAME_WINDOW_SCALE#, 30*GAME_WINDOW_SCALE#,		0, 1)
			
			; DEBUG STUFF
			For d.tDeltaTime = Each tDeltaTime
				Color(0,0,0)
				Rect(0, GAME_WINDOW_H-50, GAME_WINDOW_W, 50)
				Color(255,255,255)
				Text(4, GAME_WINDOW_H-46,    "Motion DotProduct: " + Vector_DotProductNormalized#(DEBUG_AccelerationVector, DEBUG_SpeedVector))
				Text(4, GAME_WINDOW_H-46+12, "        SpeedComp: " + Vector_Length#(DEBUG_SpeedComp)*d\Delta)
				;Text(4, GAME_WINDOW_H-46+24, "     SpeedComp(z): " + DEBUG_SpeedComp\z#)
			Next	
			
			; If on pause mode, render pause
			If (Input_Lock = False) Then
				; Setup rendering methods
				SetBlend(FI_ALPHABLEND)
				SetAlpha(0.7)
				SetScale(1, 1)
				SetColor(0, 0, 0)
				DrawRect(0, 0, GAME_WINDOW_W, GAME_WINDOW_H, 1)
			
				SetBlend(FI_ALPHABLEND)
				SetAlpha(1.0)
				SetScale(GAME_WINDOW_SCALE#, GAME_WINDOW_SCALE#)
				SetColor(255, 255, 255)
				
				DrawImageEx(Interface_Pause, GAME_WINDOW_W Shr 1, GAME_WINDOW_H Shr 1)
			End If
			
		EndDraw()
	End Function

	; ---------------------------------------------------------------------------------------------------------
	; Interface_Number
	; ---------------------------------------------------------------------------------------------------------
	Function Interface_Number(Number%, x, y, ZeroPadding=0, Alignment=0)
		; Convert number to string
		Num$ = ZeroPadding$(Str$(Number%), ZeroPadding)

		If (Alignment=1) Then x = x-Len(Num$)*18*GAME_WINDOW_SCALE#
		
		; Go on and render text
		For i = 1 To Len(Num$)
			DrawImageEx(Interface_Numbers, x, y,  Asc(Mid$(Num$, i, 1))-48)
			x = x+21*GAME_WINDOW_SCALE#
		Next
	End Function
	
	
	
	; ---------------------------------------------------------------------------------------------------------
	; Debug Tools (Will be deleted)
	; ---------------------------------------------------------------------------------------------------------	
	Global DEBUG_AccelerationVector.tVector = New tVector
	Global DEBUG_SpeedVector.tVector = New tVector
	Global DEBUG_SpeedComp.tVector = New tVector
;~IDEal Editor Parameters:
;~C#Blitz3D