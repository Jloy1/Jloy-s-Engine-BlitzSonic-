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

; /\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/
;   VARIABLES
; /\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/	

	; ---- Meshes ----
	Global Mesh_Sonic					= LoadAnimMesh("Characters/Sonic/Sonic.b3d")
	RecursiveExtractAnimSeq(Mesh_Sonic,	0,		100)	; Idle
	RecursiveExtractAnimSeq(Mesh_Sonic,	101,	140)	; Walk
	RecursiveExtractAnimSeq(Mesh_Sonic,	141,	180)	; Jog
	RecursiveExtractAnimSeq(Mesh_Sonic, 181,	220)	; Run
	RecursiveExtractAnimSeq(Mesh_Sonic,	221,	260)	; Sprint
	RecursiveExtractAnimSeq(Mesh_Sonic,	261,	276)	; Spin
	RecursiveExtractAnimSeq(Mesh_Sonic,	277,	290)	; Fall
	RecursiveExtractAnimSeq(Mesh_Sonic,	291,	304)	; Rise
	HideEntity(Mesh_Sonic)
	
	Global Mesh_Sonic_Spindash			= LoadAnimMesh("Characters/Sonic/Spindash.b3d")
	HideEntity(Mesh_Sonic_Spindash)

	Global Mesh_Sonic_JumpBall			= LoadAnimMesh("Characters/Sonic/Jump.b3d")
	HideEntity(Mesh_Sonic_JumpBall)

;~IDEal Editor Parameters:
;~C#Blitz3D