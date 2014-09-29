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
;   STARTUP
; /\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/
; --- Include critical libraries before starting ----
; Code code
Include "_SourceCode\Core\Core_GeneralConstants.bb"
Include "_SourceCode\Core\Core_InputManagement.bb"

; Libraries code
Include "_SourceCode\Libraries\Library_FastImage.bb"
Include "_SourceCode\Libraries\Library_XMLParser.bb"
Include "_SourceCode\Systems\System_FxManager.bb"

; Game code
Include "_SourceCode\Game\Game_Settings.bb"

; --- Initializate 3D mode ---
Game_LoadConfig()
Graphics3D(GAME_WINDOW_W, GAME_WINDOW_H, GAME_WINDOW_DEPTH, GAME_WINDOW_MODE)
Dither(False)
WBuffer(True)
AntiAlias(False)
SetBuffer(BackBuffer())
HidePointer()

; /\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/
;   INCLUDES
; /\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/
; Core code
Include "_SourceCode\Core\Core_DeltaTime.bb"
Include "_SourceCode\Core\Core_Maths.bb"
Include "_SourceCode\Core\Core_Tools.bb"

; Game code
Include "_SourceCode\Game\Game.bb"
Include "_SourceCode\Game\Game_Interface.bb"
Include "_SourceCode\Game\Game_Resources_Models.bb"
Include "_SourceCode\Game\Game_Resources_Textures.bb"
Include "_SourceCode\Game\Game_Resources_Sounds.bb"

; Stage code
Include "_SourceCode\Game\Stage\Stage.bb"
Include "_SourceCode\Game\Stage\Camera\Camera.bb"
Include "_SourceCode\Game\Stage\Player\Player.bb"
Include "_SourceCode\Game\Stage\Player\Player_Management.bb"
Include "_SourceCode\Game\Stage\Player\Player_Motion.bb"
Include "_SourceCode\Game\Stage\Player\Player_Animation.bb"
Include "_SourceCode\Game\Stage\Objects\Objects.bb"

; Menu code
Include "_SourceCode\Game\Menu\Menu.bb"
Include "_SourceCode\Game\Menu\Menu_Machine.bb"
Include "_SourceCode\Game\Menu\Menu_Interface.bb"
Include "_SourceCode\Game\Menu\Menu_Control_Text.bb"
Include "_SourceCode\Game\Menu\Menu_Control_Image.bb"

; General systems code
Include "_SourceCode\Systems\System_FxManager.bb"
Include "_SourceCode\Systems\System_PostFX.bb"
Include "_SourceCode\Systems\System_GameScript_VM.bb"
Include "_SourceCode\Systems\System_GameScript_Compiler.bb"
Include "_SourceCode\Systems\System_GameScript_Functions.bb"

; /\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/
;   ENTRY POINT
; /\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/

Global menuState = 0
Const MENU_TITLE = 0
Const MENU_MAIN = 1
Const MENU_CHARSEL = 2
Const MENU_GAMELOOP = 3

	Game_Startup()
	mainLoop()
	
	
	Function mainLoop()
		While(1)
			Select menuState
				Case MENU_TITLE
					; menu_MenuTitle()
				Case MENU_MAIN
					; menu_MenuMain()
				Case MENU_CHARSEL
					; menu_CharacterSelect()
				Case MENU_GAMELOOP
					; gameRunLoop()
				Default	
					RuntimeError("Unknown menu state.")
			End Select
		Wend	
		End
	End Function

	Function gameRunLoop()
		If (KeyHit(KEY_ESCAPE) And Input_Lock = False) Then Exit
		Game_Update()
	End Function

	