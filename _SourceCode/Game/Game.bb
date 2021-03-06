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
; 	STRUCTURES
; /\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/

	; ---------------------------------------------------------------------------------------------------------
	; tGame
	; ---------------------------------------------------------------------------------------------------------
	Type tGame
		Field	DeltaTime.tDeltaTime
		Field	Gameplay.tGame_Gameplay

		Field	Menu.tMenu_Machine
		Field	Stage.tGame_Stage
		
		Field	Others.tGame_Others
		Field	Mode, NewMode, State
	End Type

	; ---------------------------------------------------------------------------------------------------------
	; tGame_Gameplay
	; ---------------------------------------------------------------------------------------------------------
	Type tGame_Gameplay
		Field	Character
		Field 	Lives
		
		Field	Score
		Field 	Time
		Field 	Rings
	End Type

	; ---------------------------------------------------------------------------------------------------------
	; tGame_StagesList
	; ---------------------------------------------------------------------------------------------------------
	Type tGame_StagesList
		Field	Folder$
	End Type

	; ---------------------------------------------------------------------------------------------------------
	; tGame_Stage
	; ---------------------------------------------------------------------------------------------------------
	Type tGame_Stage
		Field	Properties.tGame_StageProperties
		Field 	List.tGame_StagesList
		Field	Root
		Field	Gravity
		Field 	GravityAlignment.tVector
	End Type

	; ---------------------------------------------------------------------------------------------------------
	; tGame_StageProperties
	; ---------------------------------------------------------------------------------------------------------
	Type tGame_StageProperties
		Field	Name$
		Field 	Music
		Field   SkyBox
		Field   Water
		Field	WaterFx.FxMeshPostProcess
		Field	WaterTexture
		Field	WaterDistortion
		Field   WaterLevel
	End Type

	; ---------------------------------------------------------------------------------------------------------
	; tGame_Others
	; ---------------------------------------------------------------------------------------------------------
	Type tGame_Others
		Field 	Fps%
		Field 	Frames%
		Field 	NextFrame%
	End Type 
	
; /\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/
; 	CONSTANTS
; /\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/
	
	; --- Game Modes ----
	Const		GAME_MODE_STARTUP	=	0
	Const 		GAME_MODE_MENU		=	1
	Const 		GAME_MODE_STAGE		=	2

	; --- Stage Modes ----
	Const 		GAME_STATE_START	=	0
	Const 		GAME_STATE_STEP		=	1
	Const 		GAME_STATE_END		=	2

	; --- World constants ---
	Const		GAME_SCALE#			=	0.1
	
; /\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/
; 	METHODS
; /\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/

	; ---------------------------------------------------------------------------------------------------------
	; Game_Startup
	; ---------------------------------------------------------------------------------------------------------
	Function Game_Startup()
		; Start up the FX Manager
		FxManager_Startup()	
		
		; Startup gameplay values
		Game\Gameplay\Character = CHARACTER_SONIC
		Game\Gameplay\Lives		= 3
		Game\Gameplay\Score		= 0
		Game\Gameplay\Time		= 0
		Game\Gameplay\Rings		= 0

		; Load stagelist
		StageListRoot = xmlLoad("Stages/Stages.xml")
		If (xmlErrorCount()>0) Then RuntimeError("Game_Startup() -> Error while parsing 'Stages.xml'")
		For i=1 To xmlNodeChildCount(StageListRoot)
			Child = xmlNodeChild(StageListRoot, i)
			If (xmlNodeNameGet(Child)="stage") Then
				stg.tGame_StagesList = New tGame_StagesList
				stg\Folder$ = xmlNodeAttributeValueGet(Child, "folder")
			End If 
		Next
		xmlNodeDelete(StageListRoot)
	End Function


	; ---------------------------------------------------------------------------------------------------------
	; Game_Update
	; ---------------------------------------------------------------------------------------------------------
	Function Game_Update()
		; Acquire keyboard and controls status and update delta time
		DeltaTime_Update(Game\DeltaTime)
		Input_Update()
		
		; Depending on current game's
		Select Game\Mode
			Case GAME_MODE_STARTUP
				Game_Startup_Update()
			Case GAME_MODE_MENU
				Game_Menu_Update()
			Case GAME_MODE_STAGE
				Game_Stage_Update()
		End Select
	End Function


	; ---------------------------------------------------------------------------------------------------------
	; Game_Startup
	; ---------------------------------------------------------------------------------------------------------
	Function Game_Startup_Update()
		; On this game Mode, we just switch Mode and set to start State.
		Game\Mode	= GAME_MODE_STAGE
		Game\State	= GAME_STATE_START
		Game\Stage\List = First tGame_StagesList
	End Function 

	
	; ---------------------------------------------------------------------------------------------------------
	; Game_End
	; ---------------------------------------------------------------------------------------------------------
	Function Game_End()
		; Set the state to the main menu.
		menuState = MENU_MAIN
		
		; Enumerate all possible entities and objects
		; World Meshes and Lights
		For st.MeshStructure = Each MeshStructure
			FreeEntity(st\Entity)
			Delete(st)
		Next
		For wl.WorldLight = Each WorldLight
			FreeEntity(wl\Entity)
			Delete(wl)
		Next
		
		FreeEntity Game\Stage\Properties\SkyBox
		FreeEntity Game\Stage\Properties\Water
		FreeTexture Game\Stage\Properties\WaterTexture
		FreeTexture Game\Stage\Properties\WaterDistortion
		
		; Objects
		For ob.tObject = Each tObject
			If ((ob\IValues[0]) And (ob\ObjType = OBJTYPE_RING)) Then FreeEntity(ob\IValues[0])
			FreeEntity ob\Entity
			Delete ob\Position
			Delete ob
		Next
		
		; Cameras
		For cm.tCamera = Each tCamera
			FreeEntity(cm\Entity)
			Delete(cm\Position)
			Delete(cm\Rotation)
			Delete(cm\Alignment)
			Delete(cm\Target)
			Delete(cm\TargetPosition)
			Delete(cm\TargetRotation)
			Delete(cm)
		Next
		
		; Players
		For pl.tPlayer = Each tPlayer
			FreeEntity(pl\Objects\Mesh_JumpBall)
			FreeEntity(pl\Objects\Entity)
			FreeEntity(pl\Objects\Mesh)
			FreeEntity(pl\Objects\Mesh_Spindash)
			FreeEntity(pl\Objects\Shadow)
			
			Delete pl\Motion\Speed
			Delete pl\Motion\Align
			Delete pl\Animation\Align
			Delete pl\Objects
			Delete pl\Motion
			Delete pl\Animation
			Delete pl\Flags
			Delete pl
		Next
		
		FreeEntity Game\Stage\Root
		FreeEntity Game\Stage\Gravity
		
		
		
		; Stop the music
		StopChannel(Channel_BackgroundMusic)
		
		; Reset Game State
		Game\State = GAME_MODE_STARTUP
	End Function

	; =========================================================================================================
	; FxManager_RenderingStepInterruption
	; =========================================================================================================
	Function FxManager_RenderingPassInterruption(Pass, Method)
		; Put this on your game. Before rendering anything, this function will be called by the
		; Render World method, so you can disable certain entities.
		If (Method = 0) Then
			Select Pass
				Case 1
					ShowEntity(Game\Stage\Properties\SkyBox)
				Case 2
					HideEntity(Game\Stage\Properties\SkyBox)
				Case 3
				Case 4
				Case 5
			End Select
		Else
			Select Pass
				Case 1
					ShowEntity(Game\Stage\Properties\SkyBox)
				Case 2
					HideEntity(Game\Stage\Properties\SkyBox)
				Case 3
				Case 4
				Case 5
			End Select
		End If
	End Function
	
; /\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/
; 	DECLARATIONS
; /\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/

	Global Game.tGame 			= New tGame
	Game\DeltaTime				= DeltaTime_Create()
	Game\Gameplay 				= New tGame_Gameplay
	Game\Stage					= New tGame_Stage
	Game\Stage\Properties		= New tGame_StageProperties
	Game\Others					= New tGame_Others
;~IDEal Editor Parameters:
;~C#Blitz3D